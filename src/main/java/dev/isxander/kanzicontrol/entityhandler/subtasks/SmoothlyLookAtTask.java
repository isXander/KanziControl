package dev.isxander.kanzicontrol.entityhandler.subtasks;

import dev.isxander.kanzicontrol.utils.Animator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class SmoothlyLookAtTask implements SubTask {
    private final float yaw, pitch;
    private final int durationTicks;

    private final LocalPlayer player = Minecraft.getInstance().player;

    private Animator.AnimationInstance animationInstance;

    public SmoothlyLookAtTask(float yaw, float pitch, int durationTicks) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.durationTicks = durationTicks;
    }

    public SmoothlyLookAtTask(Vec3 pos, int durationTicks) {
        double deltaX = pos.x - player.getX();
        double deltaY = pos.y - player.getY();
        double deltaZ = pos.z - player.getZ();
        double hDist = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) (Mth.atan2(deltaZ, deltaX) * 180f / Mth.PI) - 90f;
        float pitch = (float) (-(Mth.atan2(deltaY, hDist) * 180f / Mth.PI));

        if (Math.abs(yaw - player.getYRot()) > 180f) {
            yaw += 360f;
        }
        this.yaw = yaw;
        this.pitch = pitch;

        this.durationTicks = durationTicks;
    }

    public SmoothlyLookAtTask(Entity entity, int durationTicks) {
        this(entity.position(), durationTicks);
    }

    @Override
    public void start() {
        animationInstance = Animator.INSTANCE.play(new Animator.AnimationInstance(durationTicks, Animator::easeOutExpo)
                .addConsumer(player::setYRot, player.getYRot(), yaw))
                .addConsumer(player::setXRot, player.getXRot(), pitch);
    }

    @Override
    public boolean shouldFinish() {
        return animationInstance != null && animationInstance.isThisDone();
    }
}
