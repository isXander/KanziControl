package dev.isxander.kanzicontrol.interactionarea.button;

import com.mojang.blaze3d.vertex.PoseStack;

public class EmptyRenderer implements ButtonRenderer {
    public static final EmptyRenderer INSTANCE = new EmptyRenderer();

    private EmptyRenderer() {
    }

    @Override
    public void render(PoseStack poseStack, float deltaTime, ButtonInteractionArea button, boolean fingerDown) {

    }
}
