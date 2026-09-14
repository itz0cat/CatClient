package me.itz0cat.catclient.api.event.events;

import me.itz0cat.catclient.api.event.Cancellable;
import net.minecraft.entity.Entity;

@SuppressWarnings("all")
public class AttackEntityEvent extends Cancellable {
    private static final AttackEntityEvent INSTANCE = new AttackEntityEvent();

    public Entity target;

    public static AttackEntityEvent get(Entity target) {
        INSTANCE.setCancelled(false);
        INSTANCE.target = target;
        return INSTANCE;
    }
}
