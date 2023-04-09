package dev.isxander.kanzicontrol.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.isxander.kanzicontrol.blockhighlight.BlockOutlineRenderType;
import dev.isxander.kanzicontrol.config.KanziConfig;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @ModifyExpressionValue(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;lines()Lnet/minecraft/client/renderer/RenderType;", ordinal = 0))
    private RenderType modifyRenderType(RenderType original) {
        return BlockOutlineRenderType.OUTLINE;
    }
}
