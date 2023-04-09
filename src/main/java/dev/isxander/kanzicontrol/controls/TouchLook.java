package dev.isxander.kanzicontrol.controls;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.kanzicontrol.TouchInput;
import dev.isxander.kanzicontrol.config.KanziConfig;
import dev.isxander.kanzicontrol.interactionarea.InteractionArea;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class TouchLook implements InteractionArea {
    private final Vector2f lookDirection = new Vector2f();
    private float degreesRemaining;

    @Override
    public boolean isInBounds(Vector2fc position) {
        Vector2fc windowSize = windowSize();

        float deadRadius = Math.min(windowSize.x(), windowSize.y())
                * KanziConfig.INSTANCE.getConfig().touchForwardRadius
                / 2f;

        return position.distanceSquared(windowSize.x() / 2f, windowSize.y() / 2f) > deadRadius*deadRadius;
    }

    @Override
    public void fingerDown(Vector2fc position) {
        Vector2f distFromCenter = distFromCenter(position);

//        // clamp dist from center to smallest window dimension to make comparison fair
//        var maxDist = Math.min(windowSize().x(), windowSize().y()) * 0.5f;
//        distFromCenter.set(
//                Math.min(Math.abs(distFromCenter.x()), maxDist) * Math.copySign(1f, distFromCenter.x()),
//                Math.min(Math.abs(distFromCenter.y()), maxDist) * Math.copySign(1f, distFromCenter.y())
//        );
//        System.out.println(distFromCenter.toString(NumberFormat.getInstance()));

        // calculate angle of touch from center
        double touchAngle = Math.atan2(distFromCenter.y(), distFromCenter.x());
        Vector2f freeLookDirection = new Vector2f((float) Math.cos(touchAngle), (float) Math.sin(touchAngle));
        int maxComponent = freeLookDirection.maxComponent();
        if (maxComponent == 0) {
            // x is max component
            lookDirection.set(Math.signum(freeLookDirection.x()), 0);
        } else {
            // y is max component
            lookDirection.set(0, Math.signum(freeLookDirection.y()));
        }

        degreesRemaining = KanziConfig.INSTANCE.getConfig().touchLookDegreesPerTap;
        TouchInput.INSTANCE.cancelMining();
    }

    @Override
    public void render(PoseStack stack, float deltaTime, Vector2fc position, boolean interacting) {
        // no actual rendering, but we need to update every frame...
        if (minecraft().player != null && degreesRemaining > 0) {
            float degreesPerFrame = Math.min(KanziConfig.INSTANCE.getConfig().touchLookDegreesPerSecond / 20f * deltaTime, degreesRemaining);

            // divide by 0.15f because player.turn multiplies cursor deltas by 0.15
            // before applying rotation degrees
            float cursorXDelta = lookDirection.x() * degreesPerFrame / 0.15f;
            float cursorYDelta = lookDirection.y() * degreesPerFrame / 0.15f;

            minecraft().player.turn(cursorXDelta, cursorYDelta);

            degreesRemaining -= degreesPerFrame;
            degreesRemaining = Math.max(degreesRemaining, 0);
        }
    }

    private Vector2f distFromCenter(Vector2fc position) {
        Vector2fc windowSize = windowSize();
        return new Vector2f(position).sub(windowSize.x() / 2f, windowSize.y() / 2f);
    }
}
