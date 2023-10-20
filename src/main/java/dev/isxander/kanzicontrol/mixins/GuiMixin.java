package dev.isxander.kanzicontrol.mixins;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import dev.isxander.kanzicontrol.config.KanziConfig;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public class GuiMixin {
    /**
     * Hide the crosshair if configured that way.
     */
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;)V"))
    private boolean shouldRenderCrosshair(Gui gui, GuiGraphics graphics) {
        return !KanziConfig.INSTANCE.instance().hideCrosshair || !KanziConfig.INSTANCE.instance().enabled;
    }
}
