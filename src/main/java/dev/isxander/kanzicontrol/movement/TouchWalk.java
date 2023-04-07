package dev.isxander.kanzicontrol.movement;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.kanzicontrol.config.KanziConfig;
import dev.isxander.kanzicontrol.debug.DebugRendering;
import dev.isxander.kanzicontrol.interactionarea.InteractionArea;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class TouchWalk implements InteractionArea {
    @Override
    public boolean isInBounds(Vector2fc position) {
        Vector2fc windowSize = windowSize();
        Vector2fc distFromCenter = distFromCenter(position).absolute();

        float deadRadius = Math.min(windowSize.x(), windowSize.y())
                * KanziConfig.INSTANCE.getConfig().touchLookDeadRadius;

        return Math.max(distFromCenter.x(), distFromCenter.y()) <= deadRadius;
    }

    @Override
    public void fingerDown(Vector2fc position) {
        TouchMovementInput.INSTANCE.setForward(true);
    }

    @Override
    public void fingerUp(Vector2fc position) {
        TouchMovementInput.INSTANCE.setForward(false);
    }

    private Vector2f distFromCenter(Vector2fc position) {
        Vector2fc windowSize = windowSize();
        return new Vector2f(position).sub(windowSize.x() / 2f, windowSize.y() / 2f);
    }
}
