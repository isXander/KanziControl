package dev.isxander.kanzicontrol.controls;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.kanzicontrol.TouchInput;
import dev.isxander.kanzicontrol.config.KanziConfig;
import dev.isxander.kanzicontrol.interactionarea.InteractionArea;
import dev.isxander.kanzicontrol.utils.TickTaskScheduler;
import net.minecraft.client.player.LocalPlayer;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class TouchLook implements InteractionArea {
    private final Vector2f lookDirection = new Vector2f();
    private float degreesRemaining;
    private TickTaskScheduler.Task resetTask = null;

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
        if (resetTask != null)
            resetTask.cancel();

        degreesRemaining = KanziConfig.INSTANCE.getConfig().touchLookDegreesPerTap;
        TouchInput.INSTANCE.cancelMining();

        Vector2f distFromCenter = distFromCenter(position);
        double touchAngle = Math.atan2(distFromCenter.y(), distFromCenter.x());
        Vector2f freeLookDirection = new Vector2f((float) Math.cos(touchAngle), (float) Math.sin(touchAngle));
        int maxComponent = freeLookDirection.maxComponent();
        if (maxComponent == 0) {
            // x is max component
            lookDirection.set(Math.signum(freeLookDirection.x()), 0);
        } else {
            // y is max component
            lookDirection.set(0, Math.signum(freeLookDirection.y()));

            LocalPlayer player = minecraft().player;
            resetTask = TickTaskScheduler.INSTANCE.schedule((int)(KanziConfig.INSTANCE.getConfig().verticalResetDelay * 20), client -> {
                if (player != null) {
                    lookDirection.set(0, -Math.signum(player.getXRot()));
                    degreesRemaining = Math.abs(player.getXRot());
                }
            });

            var pitch = player.getXRot();
            var rotation = degreesRemaining * Math.signum(lookDirection.y());
            var maxPitch = KanziConfig.INSTANCE.getConfig().maxMinVerticalDegrees;
            if (pitch + rotation > maxPitch)
                degreesRemaining = maxPitch - pitch;
            else if (pitch + rotation < -maxPitch)
                degreesRemaining = -maxPitch - pitch;
        }
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
