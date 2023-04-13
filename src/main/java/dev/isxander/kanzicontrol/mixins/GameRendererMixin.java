package dev.isxander.kanzicontrol.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.kanzicontrol.config.KanziConfig;
import dev.isxander.kanzicontrol.interactionarea.InteractionAreaStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final Minecraft minecraft;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V", ordinal = 0))
    private void onPostRenderHud(float tickDelta, long startTime, boolean tick, CallbackInfo ci, @Local(ordinal = 1) PoseStack poseStack) {
        if (KanziConfig.INSTANCE.getConfig().enabled)
            InteractionAreaStorage.render(poseStack, minecraft.getDeltaFrameTime());
    }

    @ModifyReturnValue(method = "shouldRenderBlockOutline", at = @At("RETURN"))
    private boolean modifyShouldRenderBlockOutline(boolean original) {
        return original || KanziConfig.INSTANCE.getConfig().enabled;
    }
}
