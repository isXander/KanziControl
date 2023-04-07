package dev.isxander.kanzicontrol.movement;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.kanzicontrol.config.KanziConfig;
import dev.isxander.kanzicontrol.interactionarea.InteractionArea;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class TouchLook implements InteractionArea {
    private final Vector2f touchVector = new Vector2f();

    @Override
    public boolean isInBounds(Vector2fc position) {
        Vector2fc windowSize = windowSize();
        Vector2fc distFromCenter = distFromCenter(position).absolute();

        float deadRadius = Math.min(windowSize.x(), windowSize.y())
                * KanziConfig.INSTANCE.getConfig().touchLookDeadRadius;

        return Math.max(distFromCenter.x(), distFromCenter.y()) > deadRadius;
    }

    @Override
    public void tick(Vector2fc position, boolean interacting) {
        if (!interacting)
            return;

        if (!isInBounds(position)) {
            this.fingerUp(position);
            return;
        }

        Vector2fc distFromCenter = distFromCenter(position);

        // calculate angle of touch from center
        double touchAngle = Math.atan2(distFromCenter.y(), distFromCenter.x());
        touchVector.set((float) Math.cos(touchAngle), (float) Math.sin(touchAngle));
    }

    @Override
    public void fingerUp(Vector2fc position) {
        touchVector.set(0);
    }

    @Override
    public void render(PoseStack stack, float deltaTime, Vector2fc position) {
        // no actual rendering, but we need to update every frame...
        if (minecraft().player != null) {
            float degreesPerFrame = KanziConfig.INSTANCE.getConfig().touchLookDegreesPerSecond / 20f * deltaTime;

            // divide by 0.15f because player.turn multiplies cursor deltas by 0.15
            // before applying rotation degrees
            float cursorXDelta = touchVector.x() * degreesPerFrame / 0.15f;
            float cursorYDelta = touchVector.y() * degreesPerFrame / 0.15f;

            minecraft().player.turn(cursorXDelta, cursorYDelta);
        }
    }

    private Vector2f distFromCenter(Vector2fc position) {
        Vector2fc windowSize = windowSize();
        return new Vector2f(position).sub(windowSize.x() / 2f, windowSize.y() / 2f);
    }
}
