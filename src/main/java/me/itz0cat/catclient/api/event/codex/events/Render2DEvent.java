package me.itz0cat.catclient.api.event.codex.events;

import me.itz0cat.catclient.api.event.codex.Event;

public class Render2DEvent extends Event {
    private final float tickDelta;

    public Render2DEvent(float tickDelta) {
        this.tickDelta = tickDelta;
    }

    public float getTickDelta() {
        return tickDelta;
    }
}
