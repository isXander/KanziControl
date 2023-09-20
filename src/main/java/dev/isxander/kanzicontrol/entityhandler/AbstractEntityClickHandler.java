package dev.isxander.kanzicontrol.entityhandler;

import net.minecraft.world.entity.Entity;

import java.util.ArrayDeque;
import java.util.Queue;

public abstract class AbstractEntityClickHandler<T extends Entity> extends AbstractAutoPlayerTask {
    protected final T entity;

    public AbstractEntityClickHandler(T entity) {
        this.entity = entity;
    }
}
