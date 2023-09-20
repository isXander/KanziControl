package dev.isxander.kanzicontrol.entityhandler.subtasks;

import dev.isxander.kanzicontrol.config.KanziConfig;
import dev.isxander.kanzicontrol.utils.Animator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class RecenterYawTask implements SubTask {
    private Animator.AnimationInstance animationInstance;

    @Override
    public void start() {
        LocalPlayer player = Minecraft.getInstance().player;
        float interval = KanziConfig.INSTANCE.instance().touchLookDegreesPerTap;

        animationInstance = Animator.INSTANCE.play(new Animator.AnimationInstance(5, Animator::easeOutExpo)
                .addConsumer(player::setYRot, player.getYRot(), Math.round(player.getYRot() / interval) * interval));
    }

    @Override
    public boolean shouldFinish() {
        return animationInstance != null && animationInstance.isThisDone();
    }
}
