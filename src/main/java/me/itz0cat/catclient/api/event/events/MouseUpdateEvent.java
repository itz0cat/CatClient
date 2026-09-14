package me.itz0cat.catclient.api.event.events;

@SuppressWarnings("all")
public class MouseUpdateEvent {

    private static final MouseUpdateEvent INSTANCE = new MouseUpdateEvent();

    public static MouseUpdateEvent get() {
        return INSTANCE;
    }

}
