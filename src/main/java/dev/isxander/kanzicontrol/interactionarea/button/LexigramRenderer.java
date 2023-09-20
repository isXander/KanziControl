package dev.isxander.kanzicontrol.interactionarea.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class LexigramRenderer implements ButtonRenderer {
    private static final int TEXTURE_SIZE = 32;

    private final ResourceLocation textureLocation;

    public LexigramRenderer(String lexigramId) {
        this.textureLocation = new ResourceLocation("kanzicontrol", "textures/lexigrams/" + lexigramId + ".png");
    }

    @Override
    public void render(GuiGraphics graphics, float deltaTime, ButtonInteractionArea button, boolean fingerDown) {
        RenderSystem.enableBlend();

        graphics.pose().pushPose();
        graphics.pose().translate(button.getPosition().x(), button.getPosition().y(), 0);
        graphics.pose().scale(button.getSize().x() / TEXTURE_SIZE, button.getSize().y() / TEXTURE_SIZE, 1);

        graphics.blit(textureLocation, 0, 0, 0, 0, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE);

        if (fingerDown) {
            graphics.fill(0, 0, TEXTURE_SIZE, TEXTURE_SIZE, 0x7f000000);
        }

        graphics.pose().popPose();

        RenderSystem.disableBlend();
    }
}
