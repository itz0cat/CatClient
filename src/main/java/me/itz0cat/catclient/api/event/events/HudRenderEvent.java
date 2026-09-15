package me.itz0cat.catclient.api.event.events;

import org.joml.Matrix3x2fStack;

@SuppressWarnings("all")
public class HudRenderEvent {

	private static final HudRenderEvent INSTANCE = new HudRenderEvent();

	public Matrix3x2fStack matrices;
	public float tickDelta;

	public static HudRenderEvent get(Matrix3x2fStack matrices, float tickDelta) {
		INSTANCE.matrices = matrices;
		INSTANCE.tickDelta = tickDelta;
		return INSTANCE;
	}
}
