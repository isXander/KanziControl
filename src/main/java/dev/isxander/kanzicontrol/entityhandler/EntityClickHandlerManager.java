package dev.isxander.kanzicontrol.entityhandler;

import dev.isxander.kanzicontrol.entityhandler.tasks.DebugEntityTouchTask;
import dev.isxander.kanzicontrol.entityhandler.tasks.ShootCrossbowAtDragonHandler;
import dev.isxander.kanzicontrol.entityhandler.tasks.ShootCrossbowAtEntityHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EntityClickHandlerManager {
    private static final Map<Class<? extends Entity>, EntityClickTaskFactory<?>> HANDLERS = new HashMap<>();

    static {
        register(EndCrystal.class, crystal -> new ShootCrossbowAtEntityHandler(crystal, 0.7f));
        register(EnderDragon.class, dragon -> new ShootCrossbowAtDragonHandler(dragon, 0.5f));
        //register(Pig.class, DebugEntityTouchTask::new);
    }

    public static Optional<AutomatedPlayerTask> create(Entity entity) {
        EntityClickTaskFactory<?> factory = HANDLERS.get(entity.getClass());
        if (factory == null)
            return Optional.empty();
        return Optional.of(((EntityClickTaskFactory<Entity>) factory).create(entity));
    }

    public static <T extends Entity> void register(Class<T> entityClass, EntityClickTaskFactory<T> factory) {
        HANDLERS.put(entityClass, factory);
    }
}
