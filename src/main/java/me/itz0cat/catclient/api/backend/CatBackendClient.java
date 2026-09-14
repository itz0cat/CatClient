package me.itz0cat.catclient.api.backend;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.itz0cat.catclient.CatClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class CatBackendClient {

    private static final Gson GSON = new Gson();
    
    // Primary Itz0Cat Render service with fallback
    public static String PRIMARY_BACKEND_URL = "https://itz0cat.onrender.com";
    public static String FALLBACK_BACKEND_URL = "https://catgame-backend-btit.onrender.com";
    
    private static final int TIMEOUT_MS = 4000;

    public static final Set<UUID> onlineClientUsers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    public static final Map<UUID, String> playerCapes = new ConcurrentHashMap<>();
    public static final List<String> availableCapes = new ArrayList<>();
    public static String activeMotd = "Welcome to CatClient 1.21.11 • Powered by Itz0Cat";

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
        CatClient.LOGGER.info("[CatClient] Connecting to Itz0Cat Cloud Backend: {}", PRIMARY_BACKEND_URL);
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
                CatClient.LOGGER.warn("[CatClient] Failed to register session with Itz0Cat backend: {}", e.getMessage());
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
        String res = executeHttp(PRIMARY_BACKEND_URL + endpoint, "POST", jsonBody);
        if (res == null) {
            res = executeHttp(FALLBACK_BACKEND_URL + endpoint, "POST", jsonBody);
        }
        return res;
    }

    private static String getRequest(String endpoint) {
        String res = executeHttp(PRIMARY_BACKEND_URL + endpoint, "GET", null);
        if (res == null) {
            res = executeHttp(FALLBACK_BACKEND_URL + endpoint, "GET", null);
        }
        return res;
    }

    private static String executeHttp(String fullUrl, String method, String body) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(fullUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("User-Agent", "CatClient/1.21.11 (Itz0Cat)");

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
                    return sb.toString();
                }
            }
        } catch (Exception ignored) {
        } finally {
            if (conn != null) conn.disconnect();
        }
        return null;
    }
}
