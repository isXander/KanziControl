package dev.isxander.kanzicontrol.interactionarea.button;

import net.minecraft.client.gui.GuiGraphics;

public interface ButtonRenderer {
    void render(GuiGraphics graphics, float deltaTime, ButtonInteractionArea button, boolean fingerDown);
}
