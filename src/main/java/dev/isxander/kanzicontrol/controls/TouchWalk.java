package dev.isxander.kanzicontrol.controls;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.kanzicontrol.TouchInput;
import dev.isxander.kanzicontrol.config.KanziConfig;
import dev.isxander.kanzicontrol.debug.DebugRendering;
import dev.isxander.kanzicontrol.interactionarea.InteractionArea;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class TouchWalk implements InteractionArea {
    @Override
    public boolean isInBounds(Vector2fc position) {
        Vector2fc windowSize = windowSize();

        float deadRadius = Math.min(windowSize.x(), windowSize.y())
                * KanziConfig.INSTANCE.getConfig().touchForwardRadius
                / 2f;

        return position.distanceSquared(windowSize.x() / 2f, windowSize.y() / 2f) <= deadRadius*deadRadius;
    }

    @Override
    public void fingerDown(Vector2fc position) {
        if (TouchInput.INSTANCE.isMovingForward())
            TouchInput.INSTANCE.setForward(0);
        else
            TouchInput.INSTANCE.setForward((int) (KanziConfig.INSTANCE.getConfig().walkForwardDuration / 0.05f));
    }

    @Override
    public void render(PoseStack stack, float deltaTime, Vector2fc position, boolean interacting) {
        Vector2fc windowSize = windowSize();

        DebugRendering.fillCircle(
                windowSize.x() / 2f,
                windowSize.y() / 2f,
                Math.min(windowSize.x(), windowSize.y()) * KanziConfig.INSTANCE.getConfig().touchForwardRadius / 2f,
                180,
                KanziConfig.INSTANCE.getConfig().forwardOverlayColor
        );
    }
}
