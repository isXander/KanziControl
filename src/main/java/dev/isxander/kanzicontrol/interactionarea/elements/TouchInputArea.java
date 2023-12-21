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

public class TouchInputArea extends AbstractInteractionAreaContainer<InteractionArea> {
    private static final int RESET_TICKS = 20*4;

    private Animator.AnimationInstance movementAnimation, resetAnimation;
    private int ticksTillReset = RESET_TICKS;

    private int upTicks = 0, downTicks = 0, leftTicks = 0, rightTicks = 0;

    public TouchInputArea() {
        int width = (int) (windowSize().x() / 3f);
        int height = (int) (windowSize().y() / 3f);

        boolean diagonalZones = true;

        if (diagonalZones) {
            insertTop(new DirectionArea(0, 0, width, height, -1, -1, false)); // up and left
            insertTop(new DirectionArea(width, 0, width, height, 0, -1, true)); // up
            insertTop(new DirectionArea(width * 2, 0, width, height, 1, -1, false)); // up and right
            insertTop(new DirectionArea(0, height, width, height, -1, 0, false)); // left
            insertTop(new DirectionArea(width * 2, height, width, height, 1, 0, false)); // right
            insertTop(new DirectionArea(0, height * 2, width, height, -1, 1, false)); // down and left
            insertTop(new DirectionArea(width, height * 2, width, height, 0, 1, true)); // down
            insertTop(new DirectionArea(width * 2, height * 2, width, height, 1, 1, false)); // down right
        } else {
            // insert left, right, up, down. make sure to leave no gaps, left and right should take the diagonal zone area
            insertTop(new DirectionArea(0, 0, width, height * 3, -1, 0, false)); // left
            insertTop(new DirectionArea(width * 2, 0, width, height * 3, 1, 0, false)); // right
            insertTop(new DirectionArea(width, 0, width, height, 0, -1, true)); // up
            insertTop(new DirectionArea(width, height * 2, width, height, 0, 1, true)); // down
        }

        insertTop(new WalkArea(width, height, width, height)); // middle walk area
    }

    @Override
    public boolean isInBounds(Vector2fc position) {
        return true;
    }


    @Override
    public boolean fingerDown(Vector2fc position) {
        if ((movementAnimation != null && movementAnimation.isCurrentlyPlaying()) || (resetAnimation != null && resetAnimation.isCurrentlyPlaying())) {
            return false;
        }

        TouchInput.INSTANCE.cancelMining();

        return super.fingerDown(position);
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

        if (ticksTillReset == 0) {
            if (movementAnimation == null || movementAnimation.isThisDone()) {
                movementAnimation = null;

                LocalPlayer player = minecraft().player;
                float resetDegrees = -player.getXRot() + 12f;
                resetAnimation = Animator.INSTANCE.play(new Animator.AnimationInstance(calculateTickDuration(resetDegrees), Animator::linear)
                        .addDeltaConsumer(f -> player.turn(0, f / 0.15), 0, resetDegrees));
            }
            ticksTillReset--;
        } else if (ticksTillReset > 0) {
            ticksTillReset--;
        }
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
        ticksTillReset = RESET_TICKS;
    }

    public void stopNow() {
        if (movementAnimation != null)
            movementAnimation.cancelThis();
        if (resetAnimation != null)
            resetAnimation.cancelThis();
    }

    private Vector2f distFromCenter(Vector2fc position) {
        Vector2fc windowSize = windowSize();
        return new Vector2f(position).sub(windowSize.x() / 2f, windowSize.y() / 2f);
    }

    private int calculateTickDuration(float degrees) {
        return (int) (Math.abs(degrees) / KanziConfig.INSTANCE.instance().touchLookDegreesPerSecond * 20);
    }

    private abstract static class AbstractTouchArea implements InteractionArea {
        private final int x, y, width, height;
        private final int debugColor = 0x60000000 | (int) (Math.random() * 0xFFFFFF);

        public AbstractTouchArea(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public boolean isInBounds(Vector2fc position) {
            return position.x() >= x && position.x() <= x + width && position.y() >= y && position.y() <= y + height;
        }

        @Override
        public void render(GuiGraphics graphics, float deltaTime, Vector2fc position, boolean interacting) {
            //graphics.fill(x, y, x + width, y + height, interacting ? 0x60FFFFFF : debugColor);
        }
    }

    private class DirectionArea extends AbstractTouchArea {
        private final boolean reset;
        private final float xDir, yDir;

        public DirectionArea(int x, int y, int width, int height, float xDir, float yDir, boolean reset) {
            super(x, y, width, height);
            this.xDir = xDir;
            this.yDir = yDir;
            this.reset = reset;
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

                if (reset) {
                    restartResetDelay();
                } else if (ticksTillReset == -1) {
                    restartResetDelay();
                }

                movementAnimation = Animator.INSTANCE.play(new Animator.AnimationInstance(animationTickDuration, Animator::linear)
                        .addDeltaConsumer(f -> player.turn(f / 0.15, 0), 0, horizontalDegrees)
                        .addDeltaConsumer(f -> player.turn(0, f / 0.15), 0, verticalDegrees));
            }

            return true;
        }
    }

    private static class WalkArea extends AbstractTouchArea {
        public WalkArea(int x, int y, int width, int height) {
            super(x, y, width, height);
        }

        @Override
        public boolean fingerDown(Vector2fc position) {
            if (!TouchInput.INSTANCE.isMovingForward()) {
                TouchInput.INSTANCE.setForward((int) (KanziConfig.INSTANCE.instance().walkForwardDuration / 0.05f));
                return true;
            }
            return false;
        }

        @Override
        public void render(GuiGraphics graphics, float deltaTime, Vector2fc position, boolean interacting) {

        }
    }
}
