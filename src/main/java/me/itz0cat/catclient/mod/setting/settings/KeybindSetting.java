package me.itz0cat.catclient.mod.setting.settings;

import me.itz0cat.catclient.CatClient;
import me.itz0cat.catclient.api.event.events.KeyPressEvent;
import me.itz0cat.catclient.api.event.orbit.EventHandler;
import me.itz0cat.catclient.api.event.orbit.EventPriority;
import me.itz0cat.catclient.mod.Mod;
import me.itz0cat.catclient.mod.setting.Setting;
import org.lwjgl.glfw.GLFW;

public class KeybindSetting extends Setting {
    public int code;
    private boolean isListening = false;

    public KeybindSetting(String name, int code, Mod parent) {
        this.name = name;
        this.code = code;
        this.parent = parent;
        if (parent != null) parent.addSettings(this);
    }

    public int getKeyCode() {
        return this.code;
    }

    public void setKeyCode(int code) {
        this.code = code;
    }

    public void setKey(int code) {
        setKeyCode(code);
    }

    public boolean isListening() {
        return isListening;
    }

    public void setListening(boolean listening) {
        this.isListening = listening;
        if (listening) {
            CatClient.EVENTBUS.subscribe(this);
        } else {
            CatClient.EVENTBUS.unsubscribe(this);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onKeyPress(KeyPressEvent event) {
        if (!isListening) return;
        if (event.action != GLFW.GLFW_RELEASE) {
            setListening(false);
            if (event.key == GLFW.GLFW_KEY_ESCAPE)
                return;
            setKeyCode(event.key == GLFW.GLFW_KEY_DELETE ? 0 : event.key);
        }
    }
}