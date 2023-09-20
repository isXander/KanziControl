package dev.isxander.kanzicontrol.entityhandler;

import dev.isxander.kanzicontrol.entityhandler.subtasks.RecenterYawTask;
import dev.isxander.kanzicontrol.entityhandler.subtasks.SleepTask;
import dev.isxander.kanzicontrol.entityhandler.subtasks.SubTask;
import dev.isxander.kanzicontrol.utils.Animator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class RewardOnClickHandler extends AbstractEntityClickHandler<Entity> {
    public RewardOnClickHandler(Entity entity) {
        super(entity);
    }

    @Override
    public void start() {
        queueTask(new LookAtEntityTask(entity));
        queueTask(new SleepTask(40));
        queueTask(new RecenterYawTask());
    }

    private static class LookAtEntityTask implements SubTask {
        private final Entity entity;
        private Animator.AnimationInstance animationInstance;

        public LookAtEntityTask(Entity entity) {
            this.entity = entity;
        }

        @Override
        public void start() {
            LocalPlayer player = Minecraft.getInstance().player;

            Vec3 eyes = EntityAnchorArgument.Anchor.EYES.apply(player);
            Vec3 target = entity.position().add(0, entity.getBbHeight() / 2, 0);
            double diffX = target.x - eyes.x;
            double diffY = target.y - eyes.y;
            double diffZ = target.z - eyes.z;
            double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
            float targetYaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
            float targetPitch = (float) (-(Math.atan2(diffY, diffXZ) * 180.0D / Math.PI));

            animationInstance = Animator.INSTANCE.play(new Animator.AnimationInstance(10, Animator::easeOutExpo)
                    .addConsumer(player::setXRot, player.getXRot(), targetPitch)
                    .addConsumer(player::setYRot, player.getYRot(), targetYaw));
        }

        @Override
        public boolean shouldFinish() {
            return animationInstance.isThisDone();
        }
    }
}
