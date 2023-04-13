package dev.isxander.kanzicontrol.mixins;

import dev.isxander.kanzicontrol.TouchInput;
import dev.isxander.kanzicontrol.config.KanziConfig;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Inject(method = "destroyBlock", at = @At("RETURN"))
    private void tellDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (KanziConfig.INSTANCE.getConfig().enabled)
            TouchInput.INSTANCE.cancelMining();
    }
}
