package dev.isxander.kanzicontrol.entityhandler.subtasks;

public class SleepTask implements SubTask {
    private int ticks;

    public SleepTask(int sleepTicks) {
        this.ticks = sleepTicks;
    }

    @Override
    public void tick() {
        ticks--;
    }

    @Override
    public boolean shouldFinish() {
        return ticks <= 0;
    }
}
