package me.itz0cat.catclient.api.helpers;

import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.api.backend.CatBackendClient;
import me.itz0cat.catclient.mod.GeneralSettings;

import java.util.*;

import static me.itz0cat.catclient.CatClient.mc;
import static me.itz0cat.catclient.CatClient.uuid;

public class CapeHelper {

    public static HashMap<String, String> capes = new HashMap<>();
    public static ArrayList<String> ownedCapes = new ArrayList<>();
    public static String equippedCape = "cat-blueflame";

    public static void init() {
        capes.put("cat-blueflame", "Cat Blue Flame");
        capes.put("blaze-red", "Blue Flame Classic");
        capes.put("pastel-aesthetic", "Pastel Aesthetic");
        capes.put("animated-astelic", "Astelic");
        capes.put("animated-purple-sky", "Purple Sky");
        capes.put("axolotl", "Axolotl");
        capes.put("glow-squid", "Glow Squid");

        // By default unlock all for CatClient users!
        ownedCapes.addAll(capes.keySet());
    }

    public static void equipCosmetic(String cosmetic) {
        if (mc.player == null) return;
        equippedCape = cosmetic;
        CatBackendClient.equipCosmetic(cosmetic);
    }

    public static void getPlayerCosmetics() {
        if (!CatClient.modManager().getMod(GeneralSettings.class).showCosmetics.isEnabled()) return;
        if (uuid != null) {
            CatBackendClient.refreshPlayerCosmetics(uuid);
            String backendEquipped = CatBackendClient.playerCapes.get(uuid);
            if (backendEquipped != null && !backendEquipped.isEmpty()) {
                equippedCape = backendEquipped;
            }
        }
    }

    public static ArrayList<String> getOwnedCosmetics(UUID userUuid) {
        return new ArrayList<>(capes.keySet());
    }

    public static String getEquippedCosmetic(UUID userUuid) {
        if (!CatClient.modManager().getMod(GeneralSettings.class).showCosmetics.isEnabled()) return "";
        if (mc.player != null && mc.player.getUuid().equals(userUuid)) {
            return equippedCape;
        }
        String cape = CatBackendClient.playerCapes.get(userUuid);
        return cape != null ? cape : "";
    }
}
