package dev.isxander.kanzicontrol.entityhandler;

import net.minecraft.world.entity.Entity;

public interface AutomatedPlayerTask {
    void start();

    void tick();

    void onFrame(float tickDelta);

    boolean shouldFinish();

    void finish();

}
