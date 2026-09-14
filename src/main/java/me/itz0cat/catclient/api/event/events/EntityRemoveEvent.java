package me.itz0cat.catclient.api.event.events;

import me.itz0cat.catclient.api.event.Cancellable;
import net.minecraft.entity.Entity;

@SuppressWarnings("all")
public class EntityRemoveEvent extends Cancellable {
    private static final EntityRemoveEvent INSTANCE = new EntityRemoveEvent();

    public Entity entity;

    public static EntityRemoveEvent get(Entity entity) {
        INSTANCE.setCancelled(false);
        INSTANCE.entity = entity;
        return INSTANCE;
    }
}
