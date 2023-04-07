package dev.isxander.kanzicontrol.interactionarea;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.kanzicontrol.movement.TouchLook;
import dev.isxander.kanzicontrol.movement.TouchWalk;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.util.ArrayList;
import java.util.List;

import static dev.isxander.kanzicontrol.utils.RenderUtils.scaledFingerPosition;

public class InteractionAreaStorage extends AbstractInteractionAreaContainer {
    public static final InteractionAreaStorage INSTANCE = new InteractionAreaStorage();

    public final TouchLook TOUCH_LOOK = insertBottom(new TouchLook());
    public final TouchWalk TOUCH_WALK = insertAbove(new TouchWalk(), TOUCH_LOOK);
    public final ButtonInteractionArea TOUCH_TEST_BUTTON = insertAbove(new ButtonInteractionArea(ButtonInteractionArea.AnchorPoint.TOP_CENTER, 0f, 0f, 30f, 30f, ButtonInteractionArea.AnchorPoint.TOP_RIGHT), TOUCH_WALK);

    @Override
    public boolean isInBounds(Vector2fc position) {
        return true;
    }

    public static void onMouseDown(float fingerScreenX, float fingerScreenY) {
        INSTANCE.fingerDown(scaledFingerPosition(fingerScreenX, fingerScreenY));
    }

    public static void onMouseMove(float fingerScreenX, float fingerScreenY) {
        INSTANCE.fingerMove(scaledFingerPosition(fingerScreenX, fingerScreenY));
    }

    public static void onMouseUp(float fingerScreenX, float fingerScreenY) {
        INSTANCE.fingerUp(scaledFingerPosition(fingerScreenX, fingerScreenY));
    }

    public static void render(PoseStack poseStack, float deltaTime) {
        INSTANCE.render(poseStack, deltaTime, scaledFingerPosition(Minecraft.getInstance().mouseHandler.xpos(), Minecraft.getInstance().mouseHandler.ypos()));
    }

    public static void tick() {
        INSTANCE.tick(scaledFingerPosition(Minecraft.getInstance().mouseHandler.xpos(), Minecraft.getInstance().mouseHandler.ypos()), true);
    }
}
