package dev.isxander.kanzicontrol.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.kanzicontrol.config.KanziConfig;
import dev.isxander.kanzicontrol.server.KanziControlMain;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndCrystalRenderer.class)
public class EndCrystalRendererMixin {
    /**
     * Scale just the crystal without the bedrock base.
     * Only applies to renderers of the larger entity type.
     */
    @Inject(
            method = "render(Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V",
                    ordinal = 0
            )
    )
    private void scaleCrystalOnly(EndCrystal endCrystal, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if (KanziConfig.INSTANCE.instance().largeEndCrystalRendering && endCrystal.getType() != KanziControlMain.END_CRYSTAL_SML_HITBOX) {
            poseStack.scale(2f, 2f, 2f);
        }
    }
}
