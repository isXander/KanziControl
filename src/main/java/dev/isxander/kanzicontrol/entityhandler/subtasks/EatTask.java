package dev.isxander.kanzicontrol.entityhandler.subtasks;

import dev.isxander.kanzicontrol.TouchInput;

public class EatTask implements SubTask {
    @Override
    public void start() {
        TouchInput.INSTANCE.startUseItem();
    }

    @Override
    public boolean shouldFinish() {
        return !TouchInput.INSTANCE.isUsingItem();
    }
}
