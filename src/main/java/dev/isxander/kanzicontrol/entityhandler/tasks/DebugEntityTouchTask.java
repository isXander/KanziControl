package dev.isxander.kanzicontrol.entityhandler.tasks;

import dev.isxander.kanzicontrol.entityhandler.AbstractEntityClickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;

public class DebugEntityTouchTask<T extends Entity> extends AbstractEntityClickHandler<T> {
    public DebugEntityTouchTask(T entity) {
        super(entity);
    }

    @Override
    public void start() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1f));
        System.out.println("Just touched " + entity.getName().getString() + " at " + entity.blockPosition());
    }
}
