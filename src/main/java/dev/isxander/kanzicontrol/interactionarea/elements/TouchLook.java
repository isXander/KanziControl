package dev.isxander.kanzicontrol.interactionarea.elements;

import dev.isxander.kanzicontrol.TouchInput;
import dev.isxander.kanzicontrol.config.KanziConfig;
import dev.isxander.kanzicontrol.interactionarea.AbstractInteractionAreaContainer;
import dev.isxander.kanzicontrol.interactionarea.InteractionArea;
import dev.isxander.kanzicontrol.utils.Animator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class TouchLook extends AbstractInteractionAreaContainer<InteractionArea> {
    private Animator.AnimationInstance movementAnimation, resetDelay, resetAnimation;

    private int upTicks = 0, downTicks = 0, leftTicks = 0, rightTicks = 0;

    public TouchLook() {
        int width = (int) (windowSize().x() / 3f);
        int height = (int) (windowSize().y() / 3f);

//        insertTop(new DirectionArea(0, 0, width, height, -1, -1, true)); // top left
//        insertTop(new DirectionArea(width, 0, width, height, 0, -1, true)); // top middle
//        insertTop(new DirectionArea(width * 2, 0, width, height, 1, -1, true)); // top right
//        insertTop(new DirectionArea(0, height, width, height, -1, 0, false)); // middle left
//        insertTop(new DirectionArea(width * 2, height, width, height, 1, 0, false)); // middle right
//        insertTop(new DirectionArea(0, height * 2, width, height, -1, 1, true)); // bottom left
//        insertTop(new DirectionArea(width, height * 2, width, height, 0, 1, true)); // bottom middle
//        insertTop(new DirectionArea(width * 2, height * 2, width, height, 1, 1, true)); // bottom right
    }

    @Override
    public boolean isInBounds(Vector2fc position) {
        Vector2fc windowSize = windowSize();

        float deadRadius = Math.min(windowSize.x(), windowSize.y())
                * KanziConfig.INSTANCE.instance().touchForwardRadius
                / 2f;

        return position.distanceSquared(windowSize.x() / 2f, windowSize.y() / 2f) > deadRadius*deadRadius;
    }

//    @Override
//    public boolean fingerDown(Vector2fc position) {
//        if ((movementAnimation != null && movementAnimation.isCurrentlyPlaying()) || (resetAnimation != null && resetAnimation.isCurrentlyPlaying())) {
//            return false;
//        }
//
//        TouchInput.INSTANCE.cancelMining();
//
//        return super.fingerDown(position);
//    }

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
                        .addDeltaConsumer(f -> player.turn(f / 0.15, 0.0), 0, config.touchLookDegreesPerTap * Math.signum(x)));
            }
        } else if (Math.abs(y) > Math.abs(x)) {
            if (resetDelay != null && resetDelay.isCurrentlyPlaying())
                resetDelay.cancelFamily();

            int degrees = (int) (Mth.clamp(player.getXRot() + config.touchLookDegreesPerTap * Math.signum(y), -config.maxMinVerticalDegrees, config.maxMinVerticalDegrees) - player.getXRot());
            int animationTicks = calculateTickDuration(degrees);

            if (animationTicks > 0) {
                movementAnimation = Animator.INSTANCE.play(new Animator.AnimationInstance(animationTicks, Animator::linear)
                        .addDeltaConsumer(f -> player.turn(0.0, f / 0.15), 0, config.touchLookDegreesPerTap * Math.signum(y))
                );

                int resetDegrees = (int) -(player.getXRot() + degrees);
                int resetAnimationTicks = calculateTickDuration(resetDegrees);
                if (resetAnimationTicks > 0) {
                    resetAnimation = new Animator.AnimationInstance(resetAnimationTicks, Animator::linear)
                            .addDeltaConsumer(f -> player.turn(0.0, f / 0.15), 0, resetDegrees);
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
        super.render(graphics, deltaTime, position, interacting);

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
        if (movementAnimation != null)
            movementAnimation.cancelFamily();
    }

    private Vector2f distFromCenter(Vector2fc position) {
        Vector2fc windowSize = windowSize();
        return new Vector2f(position).sub(windowSize.x() / 2f, windowSize.y() / 2f);
    }

    private int calculateTickDuration(float degrees) {
        return (int) (Math.abs(degrees) / KanziConfig.INSTANCE.instance().touchLookDegreesPerSecond * 20);
    }

    private class DirectionArea implements InteractionArea {
        private final int x, y, width, height;
        private final boolean causeReset;
        private final float xDir, yDir;

        public DirectionArea(int x, int y, int width, int height, float xDir, float yDir, boolean causeReset) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.xDir = xDir;
            this.yDir = yDir;
            this.causeReset = causeReset;
        }

        @Override
        public boolean fingerDown(Vector2fc position) {
            KanziConfig config = KanziConfig.INSTANCE.instance();
            LocalPlayer player = minecraft().player;

            int animationTickDuration = calculateTickDuration(config.touchLookDegreesPerTap);
            if (animationTickDuration > 0) {
                int horizontalDegrees = (int) (config.touchLookDegreesPerTap * xDir);
                int verticalDegrees = (int) (Mth.clamp(player.getXRot() + config.touchLookDegreesPerTap * yDir, -config.maxMinVerticalDegrees, config.maxMinVerticalDegrees) - player.getXRot());

                if (horizontalDegrees == 0 && verticalDegrees == 0) {
                    return true;
                }

                if (resetDelay == null) {
                    resetDelay = new Animator.AnimationInstance((int) (config.verticalResetDelay * 20), Animator::linear);
                } else if (movementAnimation != null) {
                    movementAnimation.playOnComplete(null);
                }

                movementAnimation = Animator.INSTANCE.play(new Animator.AnimationInstance(animationTickDuration, Animator::linear)
                        .addDeltaConsumer(f -> player.turn(f / 0.15, 0), 0, horizontalDegrees)
                        .addDeltaConsumer(f -> player.turn(0, f / 0.15), 0, verticalDegrees))
                        .playOnComplete(resetDelay);

                resetDelay.startAgain();

                if (verticalDegrees != 0) {
                    int resetDegrees = (int) -(player.getXRot() + verticalDegrees);
                    resetAnimation = new Animator.AnimationInstance(calculateTickDuration(resetDegrees), Animator::linear)
                            .addDeltaConsumer(f -> player.turn(0, f / 0.15), 0, resetDegrees);
                    resetDelay.playOnComplete(resetAnimation);
                }
            }

            return true;
        }

        @Override
        public boolean isInBounds(Vector2fc position) {
            return position.x() >= x && position.x() <= x + width && position.y() >= y && position.y() <= y + height;
        }

        @Override
        public void render(GuiGraphics graphics, float deltaTime, Vector2fc position, boolean interacting) {
            graphics.fill(x + width / 2 - 5, y + height / 2 - 5, x + width / 2 + 5, y + height / 2 + 5, 0x60FFFFFF);
        }
    }
}
