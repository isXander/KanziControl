package dev.isxander.kanzicontrol.mixins;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import dev.isxander.kanzicontrol.config.KanziConfig;
import dev.isxander.kanzicontrol.interactionarea.RootInteractionArea;
import dev.isxander.kanzicontrol.utils.Animator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    public abstract float getDeltaFrameTime();

    /**
     * Update interaction area size when window resizes.
     */
    @Inject(method = "resizeDisplay", at = @At("RETURN"))
    private void resizeInteractionArea(CallbackInfo ci) {
        RootInteractionArea.regen();
    }

    /**
     * Don't release use item when not holding right click.
     * TouchInput toggles this.
     */
    @WrapWithCondition(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;releaseUsingItem(Lnet/minecraft/world/entity/player/Player;)V"))
    private boolean shouldReleaseUseItem(MultiPlayerGameMode gameMode, Player player) {
        return !KanziConfig.INSTANCE.instance().enabled;
    }

    /**
     * Tick the animator.
     */
    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;render(FJZ)V"))
    private void tickAnimator(boolean tick, CallbackInfo ci) {
        Animator.INSTANCE.progress(this.getDeltaFrameTime());
    }
}
