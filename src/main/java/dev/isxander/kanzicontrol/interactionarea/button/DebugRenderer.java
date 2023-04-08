package dev.isxander.kanzicontrol.interactionarea.button;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;

public class DebugRenderer implements ButtonRenderer {
    public static final DebugRenderer INSTANCE = new DebugRenderer();

    @Override
    public void render(PoseStack poseStack, float deltaTime, ButtonInteractionArea button, boolean fingerDown) {
        GuiComponent.fill(
                poseStack,
                (int) button.getPosition().x(),
                (int) button.getPosition().y(),
                (int) button.getPosition().x() + (int) button.getSize().x(),
                (int) button.getPosition().y() + (int) button.getSize().y(),
                fingerDown ? 0 : -1
        );
    }
}
