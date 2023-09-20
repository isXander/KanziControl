package dev.isxander.kanzicontrol.entityhandler;

import net.minecraft.world.entity.Entity;

public interface EntityClickTaskFactory<T extends Entity> {
    AutomatedPlayerTask create(T entity);
}
