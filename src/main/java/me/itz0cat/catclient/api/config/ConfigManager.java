package me.itz0cat.catclient.api.config;

import com.google.gson.*;
import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.api.font.JColor;
import me.itz0cat.catclient.mod.Mod;
import me.itz0cat.catclient.mod.mods.CrosshairMod;
import me.itz0cat.catclient.mod.setting.Setting;
import me.itz0cat.catclient.mod.setting.settings.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ConfigManager {
    private final Gson GSON = new Gson();
    private final Path pathConfigFolder;
    private final Path pathConfig;
    private JsonObject jsonConfig;

    public boolean privacyConsent = false;
    public boolean privacyPrompted = false;

    public ConfigManager() {
        pathConfigFolder = FabricLoader.getInstance().getConfigDir();
        pathConfig = pathConfigFolder.resolve("catclient.json");
    }

    public void loadConfig() {
        loadConfigFromPath(pathConfig);
    }

    public void loadConfigFromPath(Path pathConfig) {
        try {
            if (!Files.isRegularFile(pathConfig))
                return;

            jsonConfig = GSON.fromJson(Files.readString(pathConfig), JsonObject.class);
            if (jsonConfig == null) return;

            if (jsonConfig.has("privacyConsent")) {
                privacyConsent = jsonConfig.get("privacyConsent").getAsBoolean();
            }
            if (jsonConfig.has("privacyPrompted")) {
                privacyPrompted = jsonConfig.get("privacyPrompted").getAsBoolean();
            }

            for (Mod mod : CatClient.modManager().mods) {
                JsonElement moduleJson = jsonConfig.get(mod.getName());
                if (moduleJson == null || !moduleJson.isJsonObject())
                    continue;
                JsonObject moduleConfig = moduleJson.getAsJsonObject();

                JsonElement enabledJson = moduleConfig.get("enabled");
                if (enabledJson != null && enabledJson.isJsonPrimitive()) {
                    if (enabledJson.getAsBoolean()) {
                        mod.enable();
                    } else {
                        mod.disable();
                    }
                }

                for (Setting setting : mod.settings) {
                    JsonElement settingJson = moduleConfig.get(setting.name);
                    if (settingJson == null)
                        continue;

                    if (setting instanceof BooleanSetting booleanSetting) {
                        booleanSetting.enabled = settingJson.getAsBoolean();
                    } else if (setting instanceof ModeSetting modeSetting) {
                        modeSetting.setMode(settingJson.getAsString());
                    } else if (setting instanceof NumberSetting numberSetting) {
                        numberSetting.setValue(settingJson.getAsDouble());
                    } else if (setting instanceof ColorSetting colorSetting) {
                        if (!settingJson.isJsonObject())
                            continue;

                        JsonObject colorJson = settingJson.getAsJsonObject();
                        JColor jc = new JColor(colorJson.get("color").getAsInt());
                        float[] jf = jc.getFloatColor();
                        colorSetting.setColor(new JColor(jf[0], jf[1], jf[2], colorJson.get("alpha").getAsInt() * 0.392156862745098f / 100f), colorJson.get("rainbow").getAsBoolean());
                    } else if (setting instanceof KeybindSetting keybindSetting) {
                        keybindSetting.setKeyCode(settingJson.getAsInt());
                    }
                }

                JsonElement positionXJson = moduleConfig.get("posX");
                JsonElement positionYJson = moduleConfig.get("posY");
                if (positionXJson != null && positionXJson.isJsonPrimitive()) {
                    mod.position.x = positionXJson.getAsFloat();
                }
                if (positionYJson != null && positionYJson.isJsonPrimitive()) {
                    mod.position.y = positionYJson.getAsFloat();
                }

                if (mod.getName().equals("Crosshair") && mod instanceof CrosshairMod crosshairMod) {
                    JsonElement rowsElement = moduleConfig.get("crosshair");
                    if (rowsElement != null && rowsElement.isJsonObject()) {
                        JsonObject rows = rowsElement.getAsJsonObject();
                        for (int row = 0; row < 11; row++) {
                            if (rows.has(String.valueOf(row))) {
                                String str = rows.get(String.valueOf(row)).getAsString();
                                str = str.replace("[", "").replace("]", "");
                                int col = 0;
                                for (String s : str.split(", ")) {
                                    if (col < 11) {
                                        crosshairMod.crosshair[row][col] = s.trim().equals("true");
                                        col++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            CatClient.LOGGER.error("[CatClient] Error loading config: {}", e.getMessage());
        }
    }

    public void saveConfig() {
        saveConfigFromPath(pathConfig, "Default");
    }

    public void saveConfigFromPath(Path pathConfig, String name) {
        try {
            Files.createDirectories(pathConfigFolder);
            jsonConfig = new JsonObject();
            jsonConfig.addProperty("privacyConsent", privacyConsent);
            jsonConfig.addProperty("privacyPrompted", privacyPrompted);
            jsonConfig.addProperty("name", name);

            for (Mod mod : CatClient.modManager().mods) {
                JsonObject moduleConfig = new JsonObject();
                moduleConfig.addProperty("enabled", mod.isEnabled());

                for (Setting setting : mod.settings) {
                    if (setting instanceof BooleanSetting booleanSetting) {
                        moduleConfig.addProperty(setting.getName(), booleanSetting.isEnabled());
                    } else if (setting instanceof ModeSetting modeSetting) {
                        moduleConfig.addProperty(setting.getName(), modeSetting.getMode());
                    } else if (setting instanceof NumberSetting numberSetting) {
                        moduleConfig.addProperty(setting.getName(), numberSetting.getValue());
                    } else if (setting instanceof ColorSetting colorSetting) {
                        JsonObject colorJson = new JsonObject();
                        colorJson.addProperty("color", colorSetting.getValue().getRGB());
                        colorJson.addProperty("alpha", colorSetting.getValue().getAlpha());
                        colorJson.addProperty("rainbow", colorSetting.isRainbow());
                        moduleConfig.add(setting.getName(), colorJson);
                    } else if (setting instanceof KeybindSetting keybindSetting) {
                        moduleConfig.addProperty(setting.getName(), keybindSetting.getKeyCode());
                    }
                }

                moduleConfig.addProperty("posX", mod.position.x);
                moduleConfig.addProperty("posY", mod.position.y);

                if (mod.getName().equals("Crosshair") && mod instanceof CrosshairMod crosshairMod) {
                    JsonObject rows = new JsonObject();
                    for (int row = 0; row < 11; row++) {
                        rows.addProperty(String.valueOf(row), Arrays.toString(crosshairMod.crosshair[row]));
                    }
                    moduleConfig.add("crosshair", rows);
                }

                jsonConfig.add(mod.getName(), moduleConfig);
            }
            Files.writeString(pathConfig, GSON.toJson(jsonConfig));
        } catch (Exception e) {
            CatClient.LOGGER.error("[CatClient] Error saving config: {}", e.getMessage());
        }
    }

    public Map<Path, String> getConfigs() {
        try {
            Map<Path, String> paths = new HashMap<>();
            File folder = pathConfigFolder.toFile();
            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles();
                if (files != null) {
                    for (final File fileEntry : files) {
                        if (fileEntry.getName().contains("catclient-")) {
                            JsonObject jc = GSON.fromJson(Files.readString(fileEntry.toPath()), JsonObject.class);
                            if (jc != null && jc.has("name")) {
                                paths.put(fileEntry.toPath(), jc.get("name").getAsString());
                            }
                        }
                    }
                }
            }
            return paths;
        } catch (IOException ignored) {}
        return Collections.emptyMap();
    }
}
