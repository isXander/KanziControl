package dev.isxander.kanzicontrol.entityhandler.subtasks;

public class SimpleTask implements SubTask {
    private final Runnable runnable;

    public SimpleTask(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void start() {
        runnable.run();
    }

    @Override
    public boolean shouldFinish() {
        return true;
    }
}
