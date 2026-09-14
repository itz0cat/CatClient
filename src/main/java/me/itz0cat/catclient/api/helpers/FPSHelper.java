package me.itz0cat.catclient.api.helpers;

public class FPSHelper {

    private static int fps = 0;

    public static int getFPS() {
        return fps;
    }

    public static void setFPS(int fps) {
        FPSHelper.fps = fps;
    }
}