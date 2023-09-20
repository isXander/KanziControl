package dev.isxander.kanzicontrol.utils;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Consumer;

public class TickTaskScheduler {
    public static final TickTaskScheduler INSTANCE = new TickTaskScheduler();

    private final Queue<Task> taskQueue;

    private TickTaskScheduler() {
        this.taskQueue = new PriorityQueue<>(Comparator.comparingInt(Task::ticksRemaining));

        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    public Task schedule(int ticks, Consumer<Minecraft> runnable) {
        Task task = new Task(ticks, runnable);
        taskQueue.add(task);
        return task;
    }

    private void onTick(Minecraft client) {
        taskQueue.removeIf(task -> {
            if (task.isCanceled()) return true;

            if (task.tick()) {
                task.runnable().accept(client);
                return true;
            }
            return false;
        });
    }

    public static final class Task {
        private final int ticks;
        private int ticksRemaining;
        private final Consumer<Minecraft> runnable;
        private boolean canceled;

        private Task(int ticks, Consumer<Minecraft> runnable) {
            this.ticks = this.ticksRemaining = ticks;
            this.runnable = runnable;
        }

        public void cancel() {
            canceled = true;
        }

        public void resetTimer() {
            ticksRemaining = ticks;
        }

        private boolean isCanceled() {
            return canceled;
        }

        private int ticksRemaining() {
            return ticksRemaining;
        }

        private boolean tick() {
            return --ticksRemaining <= 0;
        }

        private Consumer<Minecraft> runnable() {
            return runnable;
        }
    }
}
