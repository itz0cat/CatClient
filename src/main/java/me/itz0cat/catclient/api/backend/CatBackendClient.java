package me.itz0cat.catclient.api.backend;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.itz0cat.catclient.CatClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class CatBackendClient {

    private static final Gson GSON = new Gson();

    // Primary Render Web Service URL and fallbacks
    public static String PRIMARY_BACKEND_URL = "https://catclient-backend.onrender.com";
    public static String FALLBACK_BACKEND_URL = "https://itz0cat.onrender.com";
    public static String SECONDARY_FALLBACK_URL = "https://catgame-backend-btit.onrender.com";

    // Fast timeout (8s) vs wake-up timeout (45s)
    private static final int INITIAL_TIMEOUT_MS = 8000;
    private static final int WAKEUP_TIMEOUT_MS = 45000;
    private static final int MAX_RETRIES = 4;
    private static final int[] RETRY_DELAYS_SEC = { 5, 8, 12, 16 };

    private static final AtomicBoolean isWakingUp = new AtomicBoolean(false);
    private static long lastNotificationTime = 0;

    public static final Set<UUID> onlineClientUsers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    public static final Map<UUID, String> playerCapes = new ConcurrentHashMap<>();
    public static final List<String> availableCapes = new ArrayList<>();
    public static String activeMotd = "CatClient 1.21.11 | Blue Flame Edition";

    static {
        availableCapes.add("cat-blueflame");
        availableCapes.add("blaze-red");
        availableCapes.add("pastel-aesthetic");
        availableCapes.add("animated-astelic");
        availableCapes.add("animated-purple-sky");
        availableCapes.add("axolotl");
        availableCapes.add("glow-squid");
    }

    public static void init() {
        CatClient.LOGGER.info("[CatClient] Initializing backend client: {}", PRIMARY_BACKEND_URL);
        fetchMotd();
    }

    public static void onPlayerJoin() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        CompletableFuture.runAsync(() -> {
            try {
                JsonObject payload = new JsonObject();
                payload.addProperty("uuid", mc.player.getUuid().toString());
                payload.addProperty("username", mc.player.getName().getString());
                payload.addProperty("version", "1.0.0");
                payload.addProperty("edition", "Fabric-1.21.11");

                postRequest("/api/v1/catclient/session/connect", payload.toString());
                refreshPlayerCosmetics(mc.player.getUuid());
                syncOnlinePlayers();
            } catch (Exception e) {
                CatClient.LOGGER.warn("[CatClient] Failed to register session: {}", e.getMessage());
            }
        });
    }

    public static void onPlayerDisconnect() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        CompletableFuture.runAsync(() -> {
            try {
                JsonObject payload = new JsonObject();
                payload.addProperty("uuid", mc.player.getUuid().toString());
                postRequest("/api/v1/catclient/session/disconnect", payload.toString());
            } catch (Exception ignored) {}
        });
    }

    public static void syncOnlinePlayers() {
        CompletableFuture.runAsync(() -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.getNetworkHandler() == null) return;

            try {
                JsonArray uuidArray = new JsonArray();
                for (PlayerListEntry entry : mc.getNetworkHandler().getPlayerList()) {
                    uuidArray.add(entry.getProfile().getId().toString());
                }

                JsonObject payload = new JsonObject();
                payload.add("players", uuidArray);

                String response = postRequest("/api/v1/catclient/players", payload.toString());
                if (response != null && !response.isEmpty()) {
                    JsonObject resObj = GSON.fromJson(response, JsonObject.class);
                    if (resObj.has("activeUsers")) {
                        JsonArray active = resObj.getAsJsonArray("activeUsers");
                        onlineClientUsers.clear();
                        for (JsonElement el : active) {
                            try {
                                onlineClientUsers.add(UUID.fromString(el.getAsString()));
                            } catch (Exception ignored) {}
                        }
                    }
                }
            } catch (Exception ignored) {}
        });
    }

    public static void refreshPlayerCosmetics(UUID uuid) {
        CompletableFuture.runAsync(() -> {
            try {
                String response = getRequest("/api/v1/catclient/user/" + uuid);
                if (response != null && !response.isEmpty()) {
                    JsonObject resObj = GSON.fromJson(response, JsonObject.class);
                    if (resObj.has("equippedCape")) {
                        playerCapes.put(uuid, resObj.get("equippedCape").getAsString());
                    }
                }
            } catch (Exception ignored) {}
        });
    }

    public static void equipCosmetic(String cosmeticName) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        CompletableFuture.runAsync(() -> {
            try {
                JsonObject payload = new JsonObject();
                payload.addProperty("uuid", mc.player.getUuid().toString());
                payload.addProperty("cosmetic", cosmeticName);
                postRequest("/api/v1/catclient/cosmetics/equip", payload.toString());
                playerCapes.put(mc.player.getUuid(), cosmeticName);
            } catch (Exception ignored) {}
        });
    }

    public static void fetchMotd() {
        CompletableFuture.runAsync(() -> {
            try {
                String response = getRequest("/api/v1/catclient/motd");
                if (response != null && !response.isEmpty()) {
                    JsonObject resObj = GSON.fromJson(response, JsonObject.class);
                    if (resObj.has("motd")) {
                        activeMotd = resObj.get("motd").getAsString();
                    }
                }
            } catch (Exception ignored) {}
        });
    }

    public static boolean isClientUser(UUID uuid) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null && mc.player.getUuid().equals(uuid)) return true;
        return onlineClientUsers.contains(uuid);
    }

    private static String postRequest(String endpoint, String jsonBody) {
        return executeWithIdleDetection(endpoint, "POST", jsonBody);
    }

    private static String getRequest(String endpoint) {
        return executeWithIdleDetection(endpoint, "GET", null);
    }

    /**
     * Executes HTTP requests with built-in idle wake-up detection,
     * countdown notification, and retry loop.
     */
    private static String executeWithIdleDetection(String endpoint, String method, String body) {
        String[] targets = { PRIMARY_BACKEND_URL, FALLBACK_BACKEND_URL, SECONDARY_FALLBACK_URL };

        for (String base : targets) {
            String fullUrl = base + endpoint;

            for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
                int timeout = (attempt == 0) ? INITIAL_TIMEOUT_MS : WAKEUP_TIMEOUT_MS;
                HttpResult result = attemptRequest(fullUrl, method, body, timeout);

                if (result.isSuccess()) {
                    if (isWakingUp.getAndSet(false)) {
                        sendClientMessage("§b[CatClient] §aConnected to server.");
                    }
                    return result.data;
                }

                // Check if server is waking up
                boolean looksLikeColdStart = result.isColdStartCandidate();

                if (looksLikeColdStart && attempt < MAX_RETRIES - 1) {
                    int waitSec = RETRY_DELAYS_SEC[attempt];

                    if (!isWakingUp.getAndSet(true)) {
                        sendClientMessage("§b[CatClient] §7Waking up server...");
                    }

                    // Throttle notification so chat is not spammed
                    long now = System.currentTimeMillis();
                    if (now - lastNotificationTime > 3000) {
                        sendClientMessage(String.format("§b[CatClient] §7Waking up server... Retrying in %ds (%d/%d)", waitSec, attempt + 1, MAX_RETRIES));
                        lastNotificationTime = now;
                    }

                    try {
                        Thread.sleep(waitSec * 1000L);
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                        return null;
                    }
                } else if (!looksLikeColdStart) {
                    break;
                }
            }
        }

        if (isWakingUp.getAndSet(false)) {
            sendClientMessage("§b[CatClient] §cCould not reach server. Offline mode active.");
        }
        return null;
    }

    private static HttpResult attemptRequest(String fullUrl, String method, String body, int timeoutMs) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(fullUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setConnectTimeout(timeoutMs);
            conn.setReadTimeout(timeoutMs);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("User-Agent", "CatClient/1.21.11 (Fabric)");

            if ("POST".equalsIgnoreCase(method) && body != null) {
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(body.getBytes(StandardCharsets.UTF_8));
                }
            }

            int code = conn.getResponseCode();
            if (code >= 200 && code < 300) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    return new HttpResult(true, code, sb.toString(), null);
                }
            } else {
                return new HttpResult(false, code, null, null);
            }
        } catch (SocketTimeoutException | ConnectException e) {
            return new HttpResult(false, -1, null, e);
        } catch (Exception e) {
            return new HttpResult(false, -2, null, e);
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    /**
     * Sends an asynchronous, thread-safe message to the player's in-game chat HUD.
     */
    public static void sendClientMessage(String message) {
        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc != null && mc.inGameHud != null) {
                mc.execute(() -> {
                    if (mc.inGameHud != null && mc.inGameHud.getChatHud() != null) {
                        mc.inGameHud.getChatHud().addMessage(Text.literal(message));
                    }
                });
            }
        } catch (Exception ignored) {}
    }

    private static class HttpResult {
        final boolean success;
        final int statusCode;
        final String data;
        final Exception exception;

        HttpResult(boolean success, int statusCode, String data, Exception exception) {
            this.success = success;
            this.statusCode = statusCode;
            this.data = data;
            this.exception = exception;
        }

        boolean isSuccess() {
            return success;
        }

        boolean isColdStartCandidate() {
            // Render returns 502/503/504 while booting or times out on TCP connection
            if (statusCode == 502 || statusCode == 503 || statusCode == 504) return true;
            if (exception instanceof SocketTimeoutException || exception instanceof ConnectException) return true;
            return false;
        }
    }
}
