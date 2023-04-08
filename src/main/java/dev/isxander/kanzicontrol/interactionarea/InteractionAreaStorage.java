package dev.isxander.kanzicontrol.interactionarea;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.kanzicontrol.interactionarea.button.ButtonInteractionArea;
import dev.isxander.kanzicontrol.interactionarea.button.DebugRenderer;
import dev.isxander.kanzicontrol.interactionarea.button.Lexigrams;
import dev.isxander.kanzicontrol.movement.TouchLook;
import dev.isxander.kanzicontrol.movement.TouchMovementInput;
import dev.isxander.kanzicontrol.movement.TouchWalk;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import org.joml.Vector2fc;

import static dev.isxander.kanzicontrol.utils.RenderUtils.scaledFingerPosition;

public class InteractionAreaStorage extends AbstractInteractionAreaContainer<InteractionArea> {
    public static final InteractionAreaStorage INSTANCE = new InteractionAreaStorage();

    public final TouchLook TOUCH_LOOK = insertBottom(new TouchLook());
    public final TouchWalk TOUCH_WALK = insertAbove(new TouchWalk(), TOUCH_LOOK);
    public final ButtonInteractionArea TOUCH_JUMP_BUTTON = new ButtonInteractionArea(Lexigrams.JUMP, 20f, 20f, TouchMovementInput.INSTANCE::jump);
    public final ButtonInteractionArea TOUCH_SWING_BUTTON = new ButtonInteractionArea(Lexigrams.PUNCH, 20f, 20f, fingerDown -> {
        Minecraft.getInstance().player.swing(InteractionHand.MAIN_HAND);
    });
    public final RowInteractionArea BUTTON_ROW = insertAbove(
            RowInteractionArea.builder()
                    .element(TOUCH_JUMP_BUTTON)
                    .element(TOUCH_SWING_BUTTON)
                    .elementPadding(3f)
                    .elementPosition(RowInteractionArea.ElementPosition.MIDDLE)
                    .position(AnchorPoint.TOP_CENTER, 0f, 0f, AnchorPoint.TOP_CENTER)
                    .build(),
            TOUCH_WALK
    );

    @Override
    public boolean isInBounds(Vector2fc position) {
        return true;
    }

    public static void onMouseDown(float fingerScreenX, float fingerScreenY) {
        if (Minecraft.getInstance().screen != null) return;

        INSTANCE.fingerDown(scaledFingerPosition(fingerScreenX, fingerScreenY));
    }

    public static void onMouseMove(float fingerScreenX, float fingerScreenY) {
        if (Minecraft.getInstance().screen != null) return;

        INSTANCE.fingerMove(scaledFingerPosition(fingerScreenX, fingerScreenY));
    }

    public static void onMouseUp(float fingerScreenX, float fingerScreenY) {
        if (Minecraft.getInstance().screen != null) return;

        INSTANCE.fingerUp(scaledFingerPosition(fingerScreenX, fingerScreenY));
    }

    public static void render(PoseStack poseStack, float deltaTime) {
        if (Minecraft.getInstance().screen != null) return;

        INSTANCE.render(poseStack, deltaTime, scaledFingerPosition(Minecraft.getInstance().mouseHandler.xpos(), Minecraft.getInstance().mouseHandler.ypos()), true);
    }

    public static void tick() {
        if (Minecraft.getInstance().screen != null) return;

        INSTANCE.tick(scaledFingerPosition(Minecraft.getInstance().mouseHandler.xpos(), Minecraft.getInstance().mouseHandler.ypos()), true);
    }
}
