package me.itz0cat.catclient.api.event.codex.events;

import me.itz0cat.catclient.api.event.codex.Event;

public class PacketEvent<T> extends Event {
    private final T packet;
    private final Type type;

    public PacketEvent(T packet, Type type) {
        this.packet = packet;
        this.type = type;
    }

    public T getPacket() {
        return packet;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        SEND, RECEIVE
    }
}
