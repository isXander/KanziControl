package dev.isxander.kanzicontrol.mixins;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.multiplayer.ClientAdvancements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientAdvancements.class)
public class ClientAdvancementsMixin {
    @WrapWithCondition(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/toasts/ToastComponent;addToast(Lnet/minecraft/client/gui/components/toasts/Toast;)V"))
    private boolean shouldAddAdvancementToast(ToastComponent toastComponent, Toast toastInstance) {
        return false;
    }
}
