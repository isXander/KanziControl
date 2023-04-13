package dev.isxander.kanzicontrol.mixins;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import dev.isxander.kanzicontrol.config.KanziConfig;
import dev.isxander.kanzicontrol.interactionarea.InteractionAreaStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "resizeDisplay", at = @At("RETURN"))
    private void resizeInteractionArea(CallbackInfo ci) {
        InteractionAreaStorage.regen();
    }

    @WrapWithCondition(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;releaseUsingItem(Lnet/minecraft/world/entity/player/Player;)V"))
    private boolean shouldReleaseUseItem(MultiPlayerGameMode gameMode, Player player) {
        return !KanziConfig.INSTANCE.getConfig().enabled;
    }
}
