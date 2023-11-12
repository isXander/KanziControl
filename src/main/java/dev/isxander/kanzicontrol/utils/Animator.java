package dev.isxander.kanzicontrol.utils;

import net.minecraft.util.Mth;
import org.apache.commons.lang3.Validate;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public final class Animator {
    public static final Animator INSTANCE = new Animator();

    private final List<AnimationInstance> animations;
    private final Queue<AnimationInstance> queuedAnimations = new LinkedList<>();

    private Animator() {
        this.animations = new ArrayList<>();
    }

    public void progress(float deltaTime) {
        while (!queuedAnimations.isEmpty()) {
            animations.add(queuedAnimations.poll());
        }

        animations.removeIf(animation -> {
            if (animation.isThisDone()) {
                return true;
            }
            animation.tick(deltaTime);
            return false;
        });
    }

    public AnimationInstance play(AnimationInstance animation) {
        animations.add(animation);
        return animation;
    }

    public boolean remove(AnimationInstance animation) {
        return animations.remove(animation);
    }

    public void clearQueue() {
        queuedAnimations.clear();
    }

    public static class AnimationInstance {
        private final List<AnimationConsumer> animations;
        private final List<ContinualConsumer> deltaAnimations;
        private final UnaryOperator<Float> easingFunction;
        private final int durationTicks;
        private float time;
        private boolean done, started, queuedNext;

        private final List<Runnable> callbacks = new ArrayList<>();
        private AnimationInstance next = null;

        public AnimationInstance(int durationTicks, UnaryOperator<Float> easingFunction) {
            Validate.isTrue(durationTicks >= 0, "durationTicks must be greater than or equal 0");

            this.animations = new ArrayList<>();
            this.deltaAnimations = new ArrayList<>();
            this.easingFunction = easingFunction;
            this.durationTicks = durationTicks;

            if (durationTicks == 0) {
                finishThis();
            }
        }

        public AnimationInstance addConsumer(Consumer<Float> consumer, float start, float end) {
            animations.add(new AnimationConsumer(consumer, start, end));
            return this;
        }

        public AnimationInstance addConsumer(Consumer<Integer> consumer, int start, int end) {
            animations.add(new AnimationConsumer(aFloat -> consumer.accept(aFloat.intValue()), start, end));
            return this;
        }

        public AnimationInstance addDeltaConsumer(Consumer<Float> consumer, float start, float end) {
            deltaAnimations.add(new ContinualConsumer(consumer, start, end));
            return this;
        }

        public AnimationInstance onComplete(Runnable callback) {
            callbacks.add(callback);
            return this;
        }

        public AnimationInstance andThen(AnimationInstance next) {
            if (this.next != null)
                this.next.andThen(next);
            else
                this.playOnComplete(next);
            return this;
        }

        public AnimationInstance playOnComplete(AnimationInstance next) {
            this.next = next;
            return this;
        }

        private void tick(float deltaTime) {
            started = true;

            if (durationTicks == 0) {
                done = true;
                return;
            }

            time += deltaTime;
            if (time > durationTicks) {
                if (!done) {
                    callbacks.removeIf(callback -> {
                        callback.run();
                        return true;
                    });
                }
                done = true;
                time = durationTicks;

                if (next != null && !queuedNext) {
                    Animator.INSTANCE.queuedAnimations.add(next);
                    queuedNext = true;
                }
            }

            updateConsumers();
        }

        private void updateConsumers() {
            float progress = easingFunction.apply(time / durationTicks);
            animations.forEach(consumer -> {
                float value = Mth.lerp(progress, consumer.start, consumer.end);
                consumer.consumer.accept(value);
            });

            deltaAnimations.forEach(consumer -> {
                float value = Mth.lerp(progress, consumer.start, consumer.end);
                consumer.consumer.accept(value - consumer.prevValue);
                consumer.prevValue = value;
            });
        }

        public void finishThis() {
            if (done)
                return;

            time = durationTicks;

            callbacks.removeIf(callback -> {
                callback.run();
                return true;
            });

            started = true;
            done = true;
            updateConsumers();
        }

        public void finishFamily() {
            if (next != null)
                next.finishFamily(); // always propagate to the end so even if this animation part is done the rest w
            finishThis();
        }

        public void cancelThis() {
            if (done)
                return;

            callbacks.removeIf(callback -> {
                callback.run();
                return true;
            });

            done = true;
        }

        public void cancelFamily() {
            if (next != null)
                next.cancelFamily(); // always propagate to the end so even if this animation part is done the rest w
            cancelThis();
        }

        public void startAgain() {
            done = false;
            time = 0;
            started = false;
        }

        public boolean isFamilyDone() {
            return done && (next == null || next.isFamilyDone());
        }

        public boolean isThisDone() {
            return done;
        }

        public boolean isCurrentlyPlaying() {
            return started && !done;
        }

        private record AnimationConsumer(Consumer<Float> consumer, float start, float end) {
        }

        private static class ContinualConsumer {
            private final Consumer<Float> consumer;
            private final float start;
            private final float end;

            private float prevValue;

            public ContinualConsumer(Consumer<Float> consumer, float start, float end) {
                this.consumer = consumer;
                this.start = start;
                this.prevValue = start;
                this.end = end;
            }
        }
    }

    public static float easeOutExpo(float t) {
        return t == 1 ? 1 : 1 - (float) Math.pow(2, -10 * t);
    }

    public static float easeOutSin(float t) {
        return Mth.sin(t * (Mth.PI / 2));
    }

    public static float linear(float t) {
        return t;
    }
}
