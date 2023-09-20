package dev.isxander.kanzicontrol.entityhandler;

import dev.isxander.kanzicontrol.entityhandler.subtasks.SubTask;

import java.util.ArrayDeque;
import java.util.Queue;

public abstract class AbstractAutoPlayerTask implements AutomatedPlayerTask {
    private SubTask currentTask;
    private final Queue<SubTask> queuedTasks;

    public AbstractAutoPlayerTask() {
        this.queuedTasks = new ArrayDeque<>();
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
