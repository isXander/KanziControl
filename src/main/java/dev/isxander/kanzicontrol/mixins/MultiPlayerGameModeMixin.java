package dev.isxander.kanzicontrol.mixins;

import dev.isxander.kanzicontrol.TouchInput;
import dev.isxander.kanzicontrol.config.KanziConfig;
import dev.isxander.kanzicontrol.interactionarea.RootInteractionArea;
import dev.isxander.kanzicontrol.interactionarea.elements.TouchLook;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    /**
     * Notify TouchInput that the mining has been completed and it can let go of mine key.
     */
    @Inject(method = "destroyBlock", at = @At("RETURN"))
    private void tellDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (KanziConfig.INSTANCE.instance().enabled)
            TouchInput.INSTANCE.cancelMining();
    }

    /**
     * Prevent touchlook from recentering Y whilst mining.
     * @param pos
     * @param direction
     * @param cir
     */
    @Inject(method = "continueDestroyBlock", at = @At("HEAD"))
    private void preventRecentering(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        TouchLook touchLook = RootInteractionArea.getInstance().TOUCH_LOOK;
        if (touchLook != null) touchLook.restartResetDelay();
    }
}
