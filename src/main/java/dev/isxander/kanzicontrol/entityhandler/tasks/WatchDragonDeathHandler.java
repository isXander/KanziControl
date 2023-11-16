package dev.isxander.kanzicontrol.entityhandler.tasks;

import dev.isxander.kanzicontrol.entityhandler.AbstractAutoPlayerTask;
import dev.isxander.kanzicontrol.entityhandler.subtasks.SmoothlyLookAtTask;
import dev.isxander.kanzicontrol.entityhandler.subtasks.SubTask;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

import java.util.Optional;

public class WatchDragonDeathHandler extends AbstractAutoPlayerTask {
    @Override
    public void start() {
        Optional<EnderDragon> dragonOpt = player.level().getEntitiesOfClass(EnderDragon.class, player.getBoundingBox().inflate(200))
                .stream()
                .findAny();

        if (dragonOpt.isEmpty())
            return;

        EnderDragon dragon = dragonOpt.get();

        queueTask(new SmoothlyLookAtTask(dragon, 30));
        queueTask(new WatchEntity(dragon, 20*14));
        queueTask(new SmoothlyLookAtTask(player.getYRot(), 0, 20));
    }

    public class WatchEntity implements SubTask {
        private final Entity entity;
        private final int duration;

        private int ticks;

        public WatchEntity(Entity entity, int duration) {
            this.entity = entity;
            this.duration = duration;
        }

        @Override
        public void tick() {
            player.lookAt(EntityAnchorArgument.Anchor.EYES, entity.position());
            ticks++;
        }

        @Override
        public boolean shouldFinish() {
            return ticks >= duration;
        }
    }
}
