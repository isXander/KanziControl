package dev.isxander.kanzicontrol.mixins;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import dev.isxander.kanzicontrol.config.KanziConfig;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.multiplayer.ClientAdvancements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientAdvancements.class)
public class ClientAdvancementsMixin {
    /**
     * Hide advancements in Kanzi-mode to stop distractions.
     */
    @WrapWithCondition(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/toasts/ToastComponent;addToast(Lnet/minecraft/client/gui/components/toasts/Toast;)V"))
    private boolean shouldAddAdvancementToast(ToastComponent toastComponent, Toast toastInstance) {
        return !KanziConfig.INSTANCE.instance().enabled;
    }
}
