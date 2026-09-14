package me.itz0cat.catclient.gui;

public interface Renderable {
    String getName();

    void render();

    default Theme getTheme() {
        return new Theme() {
        };
    }
}
