package dev.isxander.kanzicontrol.interactionarea;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public interface InteractionArea {
    InteractionArea EMPTY = new InteractionArea() {
        @Override
        public boolean isInBounds(Vector2fc position) {
            return true;
        }

        @Override
        public InteractionAreaContainer parent() {
            return null;
        }
    };

    boolean isInBounds(Vector2fc position);

    default void fingerDown(Vector2fc position) {}
    default void fingerUp(Vector2fc position) {}
    default void fingerMove(Vector2fc position) {}

    default void tick(Vector2fc position, boolean interacting) {}

    default void render(PoseStack stack, float deltaTime, Vector2fc position) {}

    default Minecraft minecraft() {
        return Minecraft.getInstance();
    }

    InteractionAreaContainer parent();
}
