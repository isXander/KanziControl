package dev.isxander.kanzicontrol.entityhandler.subtasks;

public interface SubTask {
    default void start() {}

    default void tick() {}

    default void onFrame(float tickDelta) {}

    boolean shouldFinish();
}
