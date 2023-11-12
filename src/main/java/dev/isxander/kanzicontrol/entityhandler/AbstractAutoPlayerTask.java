package dev.isxander.kanzicontrol.entityhandler;

import dev.isxander.kanzicontrol.entityhandler.subtasks.SubTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.ArrayDeque;
import java.util.Queue;

public abstract class AbstractAutoPlayerTask implements AutomatedPlayerTask {
    protected final LocalPlayer player;
    private SubTask currentTask;
    private final Queue<SubTask> queuedTasks;

    public AbstractAutoPlayerTask() {
        this.queuedTasks = new ArrayDeque<>();
        this.player = Minecraft.getInstance().player;
    }

    @Override
    public void tick() {
        if (currentTask == null) {
            currentTask = queuedTasks.poll();
            if (currentTask != null)
                currentTask.start();
        }

        if (currentTask != null) {
            if (currentTask.shouldFinish()) {
                currentTask = null;
            } else {
                currentTask.tick();
            }
        }
    }

    @Override
    public void onFrame(float tickDelta) {
        if (currentTask != null) {
            currentTask.onFrame(tickDelta);
        }
    }

    @Override
    public boolean shouldFinish() {
        return currentTask == null && queuedTasks.isEmpty();
    }

    @Override
    public void finish() {

    }

    public void queueTask(SubTask task) {
        queuedTasks.add(task);
    }
}
