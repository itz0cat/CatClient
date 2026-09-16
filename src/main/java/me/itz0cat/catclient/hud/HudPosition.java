package me.itz0cat.catclient.hud;

public class HudPosition {
    public float x;
    public float y;
    public float scale = 1.0f;

    public HudPosition() {
        this(0, 0, 1.0f);
    }

    public HudPosition(float x, float y) {
        this(x, y, 1.0f);
    }

    public HudPosition(float x, float y, float scale) {
        this.x = x;
        this.y = y;
        this.scale = scale;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
