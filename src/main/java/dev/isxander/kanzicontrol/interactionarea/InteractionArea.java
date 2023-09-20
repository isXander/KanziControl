package dev.isxander.kanzicontrol.interactionarea;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public interface InteractionArea {
    InteractionArea SCREEN = position -> true;

    boolean isInBounds(Vector2fc position);

    default boolean fingerDown(Vector2fc position) {
        return false;
    }
    default void fingerUp(Vector2fc position) {

    }
    default void fingerMove(Vector2fc position) {

    }

    default void tick(Vector2fc position, boolean interacting) {}

    default void render(GuiGraphics graphics, float deltaTime, Vector2fc position, boolean interacting) {}

    default Minecraft minecraft() {
        return Minecraft.getInstance();
    }

    default Window window() {
        return minecraft().getWindow();
    }

    default Vector2fc windowSize() {
        return new Vector2f(window().getGuiScaledWidth(), window().getGuiScaledHeight());
    }
}
