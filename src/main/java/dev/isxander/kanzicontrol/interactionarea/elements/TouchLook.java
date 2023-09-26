package dev.isxander.kanzicontrol.interactionarea.elements;

import dev.isxander.kanzicontrol.TouchInput;
import dev.isxander.kanzicontrol.config.KanziConfig;
import dev.isxander.kanzicontrol.interactionarea.InteractionArea;
import dev.isxander.kanzicontrol.utils.Animator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class TouchLook implements InteractionArea {
    private Animator.AnimationInstance movementAnimation, resetDelay, resetAnimation;

    private int upTicks = 0, downTicks = 0, leftTicks = 0, rightTicks = 0;

    @Override
    public boolean isInBounds(Vector2fc position) {
        Vector2fc windowSize = windowSize();

        float deadRadius = Math.min(windowSize.x(), windowSize.y())
                * KanziConfig.INSTANCE.instance().touchForwardRadius
                / 2f;

        return position.distanceSquared(windowSize.x() / 2f, windowSize.y() / 2f) > deadRadius*deadRadius;
    }

    @Override
    public boolean fingerDown(Vector2fc position) {
        KanziConfig config = KanziConfig.INSTANCE.instance();

        LocalPlayer player = minecraft().player;
        Vector2f distFromCenter = distFromCenter(position);
        double touchAngle = Math.atan2(distFromCenter.y(), distFromCenter.x());
        Vector2f freeLookDirection = new Vector2f((float) Math.cos(touchAngle), (float) Math.sin(touchAngle));

        if (movementAnimation != null) {
            if (!movementAnimation.isThisDone()) {
                return false;
            }
            if (resetAnimation != null && resetAnimation.isCurrentlyPlaying()) {
                return false;
            }
            resetAnimation = null;
            movementAnimation = null;
        }

        TouchInput.INSTANCE.cancelMining();

        float x = freeLookDirection.x();
        float y = freeLookDirection.y();

        if (x == 0 && y == 0)
            return false;

        if (Math.abs(x) > Math.abs(y)) {
            if (resetDelay != null && resetDelay.isCurrentlyPlaying())
                resetDelay.startAgain();

            int animationTickDuration = calculateTickDuration(config.touchLookDegreesPerTap);
            if (animationTickDuration > 0) {
                movementAnimation = Animator.INSTANCE.play(new Animator.AnimationInstance(animationTickDuration, Animator::linear)
                        .addContinualConsumer(f -> player.turn(f / 0.15, 0.0), 0, config.touchLookDegreesPerTap * Math.signum(x)));
            }
        } else if (Math.abs(y) > Math.abs(x)) {
            if (resetDelay != null && resetDelay.isCurrentlyPlaying())
                resetDelay.cancelFamily();

            int degrees = (int) (Mth.clamp(player.getXRot() + config.touchLookDegreesPerTap * Math.signum(y), -config.maxMinVerticalDegrees, config.maxMinVerticalDegrees) - player.getXRot());
            int animationTicks = calculateTickDuration(degrees);

            if (animationTicks > 0) {
                movementAnimation = Animator.INSTANCE.play(new Animator.AnimationInstance(animationTicks, Animator::linear)
                        .addContinualConsumer(f -> player.turn(0.0, f / 0.15), 0, config.touchLookDegreesPerTap * Math.signum(y))
                );

                int resetDegrees = (int) -(player.getXRot() + degrees);
                int resetAnimationTicks = calculateTickDuration(resetDegrees);
                if (resetAnimationTicks > 0) {
                    resetAnimation = new Animator.AnimationInstance(resetAnimationTicks, Animator::linear)
                            .addContinualConsumer(f -> player.turn(0.0, f / 0.15), 0, resetDegrees);
                    if (resetDelay != null && resetDelay.isCurrentlyPlaying()) {
                        resetDelay.startAgain();
                        resetDelay.playOnComplete(resetAnimation);
                        movementAnimation.andThen(resetDelay);
                    } else {
                        movementAnimation.andThen(resetDelay = new Animator.AnimationInstance((int) (config.verticalResetDelay * 20), Animator::linear)
                                .andThen(resetAnimation));
                    }
                }
            }
        }

        return true;
    }

    @Override
    public void render(GuiGraphics graphics, float deltaTime, Vector2fc position, boolean interacting) {
        if (rightTicks > 0) {
            if (rightTicks % 16 >= 7) {
                graphics.fill((int) (windowSize().x() / 2f), 0, (int) windowSize().x(), (int) windowSize().y(), 0x60FFFFFF);
            }
        }
        if (leftTicks > 0) {
            if (leftTicks % 16 >= 7) {
                graphics.fill(0, 0, (int) (windowSize().x() / 2), (int) windowSize().y(), 0x60FFFFFF);
            }
        }
        if (upTicks > 0) {
            if (upTicks % 16 >= 7) {
                graphics.fill(0, 0, (int) windowSize().x(), (int) (windowSize().y() / 2), 0x60FFFFFF);
            }
        }
        if (downTicks > 0) {
            if (downTicks % 16 >= 7) {
                graphics.fill(0, (int) (windowSize().y() / 2), (int) windowSize().x(), (int) windowSize().y(), 0x60FFFFFF);
            }
        }
    }

    @Override
    public void tick(Vector2fc position, boolean interacting) {
        if (upTicks > 0) upTicks--;
        if (downTicks > 0) downTicks--;
        if (leftTicks > 0) leftTicks--;
        if (rightTicks > 0) rightTicks--;
    }

    public void indicateUp(int ticks) {
        upTicks = ticks;
    }

    public void indicateDown(int ticks) {
        downTicks = ticks;
    }

    public void indicateLeft(int ticks) {
        leftTicks = ticks;
    }

    public void indicateRight(int ticks) {
        rightTicks = ticks;
    }

    public void restartResetDelay() {
        if (resetDelay != null && resetDelay.isCurrentlyPlaying())
            resetDelay.startAgain();
    }

    public void stopNow() {
        movementAnimation.cancelFamily();
    }

    private Vector2f distFromCenter(Vector2fc position) {
        Vector2fc windowSize = windowSize();
        return new Vector2f(position).sub(windowSize.x() / 2f, windowSize.y() / 2f);
    }

    private int calculateTickDuration(float degrees) {
        return (int) (Math.abs(degrees) / KanziConfig.INSTANCE.instance().touchLookDegreesPerSecond * 20);
    }
}
