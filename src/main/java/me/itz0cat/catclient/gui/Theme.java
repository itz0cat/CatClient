package me.itz0cat.catclient.gui;

public interface Theme {
    default void preRender() {
        // do nothing
    }

    default void postRender() {
        // do nothing
    }
}
