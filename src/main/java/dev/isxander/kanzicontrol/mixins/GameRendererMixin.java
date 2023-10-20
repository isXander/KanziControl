package dev.isxander.kanzicontrol.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.kanzicontrol.config.KanziConfig;
import dev.isxander.kanzicontrol.interactionarea.RootInteractionArea;
import dev.isxander.kanzicontrol.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final Minecraft minecraft;

    /**
     * Render our own interaction area stuff.
     */
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V", ordinal = 0))
    private void onPostRenderHud(float tickDelta, long startTime, boolean tick, CallbackInfo ci, @Local GuiGraphics graphics) {
        if (KanziConfig.INSTANCE.instance().enabled)
            RootInteractionArea.render(graphics, minecraft.getDeltaFrameTime());
    }

    /**
     * Prevent drawing block outline if enabled.
     */
    @ModifyReturnValue(method = "shouldRenderBlockOutline", at = @At("RETURN"))
    private boolean modifyShouldRenderBlockOutline(boolean original) {
        return original || KanziConfig.INSTANCE.instance().enabled;
    }

    /**
     * Save some matrices for rendering utilities.
     */
    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/GameRenderer;renderHand:Z",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 0
            )
    )
    private void onPostRender(float tickDelta, long limitTime, PoseStack matrix, CallbackInfo ci) {
        RenderUtils.lastProjectionMatrix = RenderSystem.getProjectionMatrix();
        RenderUtils.lastModelViewMatrix = RenderSystem.getModelViewMatrix();
        RenderUtils.lastWorldSpaceMatrix = matrix.last().pose();
    }
}
