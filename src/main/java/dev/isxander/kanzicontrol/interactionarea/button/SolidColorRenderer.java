package dev.isxander.kanzicontrol.interactionarea.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;

public class SolidColorRenderer implements ButtonRenderer {
    private final int color;

    public SolidColorRenderer(int color) {
        this.color = color | 0xFF000000;
    }

    @Override
    public void render(GuiGraphics graphics, float deltaTime, ButtonInteractionArea button, boolean fingerDown) {
        var size = button.getSize();
        var pos = button.getPosition();

        RenderSystem.enableBlend();

        graphics.fill(
                (int) pos.x(), (int) pos.y(),
                (int) pos.x() + (int) size.x(), (int) pos.y() + (int) size.y(),
                fingerDown ? -1 : color
        );

        RenderSystem.disableBlend();
    }
}
