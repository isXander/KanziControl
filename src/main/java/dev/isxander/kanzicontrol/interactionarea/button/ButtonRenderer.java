package dev.isxander.kanzicontrol.interactionarea.button;

import com.mojang.blaze3d.vertex.PoseStack;

public interface ButtonRenderer {
    void render(PoseStack poseStack, float deltaTime, ButtonInteractionArea button, boolean fingerDown);
}
