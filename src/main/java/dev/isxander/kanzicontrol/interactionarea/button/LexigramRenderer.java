package dev.isxander.kanzicontrol.interactionarea.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;

public class LexigramRenderer implements ButtonRenderer {
    private static final int TEXTURE_SIZE = 128;

    private final ResourceLocation textureLocation;

    public LexigramRenderer(String lexigramId) {
        this.textureLocation = new ResourceLocation("kanzicontrol", "textures/lexigrams/" + lexigramId + ".png");
    }

    @Override
    public void render(PoseStack poseStack, float deltaTime, ButtonInteractionArea button, boolean fingerDown) {
        RenderSystem.setShaderTexture(0, textureLocation);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableBlend();

        poseStack.pushPose();
        poseStack.translate(button.getPosition().x(), button.getPosition().y(), 0);
        poseStack.scale(button.getSize().x() / TEXTURE_SIZE, button.getSize().y() / TEXTURE_SIZE, 1);

        GuiComponent.blit(poseStack, 0, 0, 0, 0, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE);

        if (fingerDown) {
            GuiComponent.fill(poseStack, 0, 0, TEXTURE_SIZE, TEXTURE_SIZE, 0x7f000000);
        }

        poseStack.popPose();

        RenderSystem.disableBlend();
    }
}
