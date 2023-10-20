package dev.isxander.kanzicontrol.mixins;

import dev.isxander.kanzicontrol.config.KanziConfig;
import dev.isxander.kanzicontrol.interactionarea.RootInteractionArea;
import net.minecraft.client.gui.components.BossHealthOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BossHealthOverlay.class)
public class BossHealthOverlayMixin {
    /**
     * Shift the boss bar itself up a bit to align with the text.
     */
    @ModifyArg(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;drawBar(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/world/BossEvent;)V"),
            index = 2
    )
    private int modifyBarY(int y) {
        if (!KanziConfig.INSTANCE.instance().enabled)
            return y;

        return y - 6;
    }

    /**
     * Push the text down a bit to align with the boss bar.
     */
    @ModifyArg(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I"),
            index = 3
    )
    private int modifyTextY(int y) {
        if (!KanziConfig.INSTANCE.instance().enabled)
            return y;

        return y + 2;
    }
}
