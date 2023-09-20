package dev.isxander.kanzicontrol.interactionarea.elements;

import dev.isxander.kanzicontrol.TouchInput;
import dev.isxander.kanzicontrol.config.KanziConfig;
import dev.isxander.kanzicontrol.debug.DebugRendering;
import dev.isxander.kanzicontrol.interactionarea.InteractionArea;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2fc;

public class TouchWalk implements InteractionArea {
    private int indicateTicks;

    @Override
    public boolean isInBounds(Vector2fc position) {
        Vector2fc windowSize = windowSize();

        float deadRadius = Math.min(windowSize.x(), windowSize.y())
                * KanziConfig.INSTANCE.instance().touchForwardRadius
                / 2f;

        return position.distanceSquared(windowSize.x() / 2f, windowSize.y() / 2f) <= deadRadius*deadRadius;
    }

    @Override
    public boolean fingerDown(Vector2fc position) {
        if (!TouchInput.INSTANCE.isMovingForward()) {
            TouchInput.INSTANCE.setForward((int) (KanziConfig.INSTANCE.instance().walkForwardDuration / 0.05f));
//            indicate(10);
        }
        return true;
    }

    @Override
    public void render(GuiGraphics graphics, float deltaTime, Vector2fc position, boolean interacting) {
        Vector2fc windowSize = windowSize();

        if (indicateTicks > 0 && indicateTicks % 16 >= 7) {
            DebugRendering.fillCircle(
                    windowSize.x() / 2f,
                    windowSize.y() / 2f,
                    Math.min(windowSize.x(), windowSize.y()) * KanziConfig.INSTANCE.instance().touchForwardRadius / 2f,
                    180,
                    0x50FFFFFF
            );
        }
    }

    @Override
    public void tick(Vector2fc position, boolean interacting) {
        if (indicateTicks > 0) indicateTicks--;
    }

    public void indicate(int ticks) {
        this.indicateTicks = ticks;
    }
}
