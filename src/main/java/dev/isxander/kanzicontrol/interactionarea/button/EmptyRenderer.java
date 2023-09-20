package dev.isxander.kanzicontrol.interactionarea.button;

import net.minecraft.client.gui.GuiGraphics;

public class EmptyRenderer implements ButtonRenderer {
    public static final EmptyRenderer INSTANCE = new EmptyRenderer();

    private EmptyRenderer() {
    }

    @Override
    public void render(GuiGraphics graphics, float deltaTime, ButtonInteractionArea button, boolean fingerDown) {

    }
}
