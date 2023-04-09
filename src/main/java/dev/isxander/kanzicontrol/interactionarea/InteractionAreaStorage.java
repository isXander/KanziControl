package dev.isxander.kanzicontrol.interactionarea;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.kanzicontrol.debug.KanziControlDebug;
import dev.isxander.kanzicontrol.interactionarea.button.ButtonInteractionArea;
import dev.isxander.kanzicontrol.interactionarea.button.EmptyRenderer;
import dev.isxander.kanzicontrol.controls.TouchLook;
import dev.isxander.kanzicontrol.TouchInput;
import dev.isxander.kanzicontrol.controls.TouchWalk;
import dev.isxander.kanzicontrol.interactionarea.button.Lexigrams;
import dev.isxander.kanzicontrol.interactionarea.button.SolidColorRenderer;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import org.joml.Vector2fc;

import java.util.ArrayList;
import java.util.List;

import static dev.isxander.kanzicontrol.utils.RenderUtils.scaledFingerPosition;

public class InteractionAreaStorage extends AbstractInteractionAreaContainer<InteractionArea> {
    private static InteractionAreaStorage instance = new InteractionAreaStorage();

    public final TouchLook TOUCH_LOOK = insertBottom(new TouchLook());
    public final TouchWalk TOUCH_WALK = insertAbove(new TouchWalk(), TOUCH_LOOK);
    public final ButtonInteractionArea TOUCH_JUMP_BUTTON = new ButtonInteractionArea(Lexigrams.JUMP, 30f, 30f, TouchInput.INSTANCE::jump);
    public final ButtonInteractionArea TOUCH_MINE_BUTTON = new ButtonInteractionArea(Lexigrams.BREAK, 30f, 30f, TouchInput.INSTANCE::toggleMining);
    public final ButtonInteractionArea TOUCH_ATTACK_BUTTON = new ButtonInteractionArea(Lexigrams.FIGHT, 30f, 30f, TouchInput.INSTANCE::attack);
    public final ButtonInteractionArea TOUCH_USE_BUTTON = new ButtonInteractionArea(new SolidColorRenderer(0xFF00FF), 30f, 30f, TouchInput.INSTANCE::toggleUseItem);
    public final ButtonInteractionArea TOUCH_SWIM_DOWN = new ButtonInteractionArea(new SolidColorRenderer(0x0000FF), 30f, 30f, TouchInput.INSTANCE::toggleSwimDown);
    public final RowInteractionArea BUTTON_ROW = insertAbove(
            RowInteractionArea.builder()
                    .element(TOUCH_JUMP_BUTTON)
                    .element(TOUCH_MINE_BUTTON)
                    .element(TOUCH_ATTACK_BUTTON)
                    .element(TOUCH_SWIM_DOWN)
                    .element(TOUCH_USE_BUTTON)
                    .elementPadding(3f)
                    .elementPosition(RowInteractionArea.ElementPosition.MIDDLE)
                    .position(AnchorPoint.TOP_RIGHT, 0f, 0f, AnchorPoint.TOP_RIGHT)
                    .build(),
            TOUCH_WALK
    );

    public final RowInteractionArea HOTBAR_ROW = insertAbove(
            RowInteractionArea.builder()
                    .elements(Util.make(() -> {
                        List<ButtonInteractionArea> list = new ArrayList<>();
                        for (int i = 0; i < 9; i++) {
                            int finalI = i;
                            list.add(new ButtonInteractionArea(KanziControlDebug.DEBUG_HOTBAR_BUTTONS ? new SolidColorRenderer(0x20FFFFFF) : EmptyRenderer.INSTANCE, 16f, 16f, () -> {
                                var minecraft = Minecraft.getInstance();
                                minecraft.player.getInventory().selected = finalI;
                            }));
                        }
                        return list;
                    }))
                    .elementPadding(4f)
                    .elementPosition(RowInteractionArea.ElementPosition.BOTTOM)
                    .rowPadding(0, 0, 0, 2f)
                    .position(AnchorPoint.BOTTOM_CENTER, 0f, 0f, AnchorPoint.BOTTOM_CENTER)
                    .build(),
            BUTTON_ROW
    );

    @Override
    public boolean isInBounds(Vector2fc position) {
        return true;
    }

    public static void onMouseDown(float fingerScreenX, float fingerScreenY) {
        if (Minecraft.getInstance().screen != null) return;

        getInstance().fingerDown(scaledFingerPosition(fingerScreenX, fingerScreenY));
    }

    public static void onMouseMove(float fingerScreenX, float fingerScreenY) {
        if (Minecraft.getInstance().screen != null) return;

        getInstance().fingerMove(scaledFingerPosition(fingerScreenX, fingerScreenY));
    }

    public static void onMouseUp(float fingerScreenX, float fingerScreenY) {
        if (Minecraft.getInstance().screen != null) return;

        getInstance().fingerUp(scaledFingerPosition(fingerScreenX, fingerScreenY));
    }

    public static void render(PoseStack poseStack, float deltaTime) {
        if (Minecraft.getInstance().screen != null) return;

        getInstance().render(poseStack, deltaTime, scaledFingerPosition(Minecraft.getInstance().mouseHandler.xpos(), Minecraft.getInstance().mouseHandler.ypos()), true);
    }

    public static void tick() {
        if (Minecraft.getInstance().screen != null) return;

        getInstance().tick(scaledFingerPosition(Minecraft.getInstance().mouseHandler.xpos(), Minecraft.getInstance().mouseHandler.ypos()), true);
    }

    public static InteractionAreaStorage getInstance() {
        return instance;
    }

    public static void regen() {
        instance = new InteractionAreaStorage();
    }
}
