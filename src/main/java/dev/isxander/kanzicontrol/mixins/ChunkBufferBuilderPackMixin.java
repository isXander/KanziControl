package dev.isxander.kanzicontrol.mixins;

import com.mojang.blaze3d.vertex.BufferBuilder;
import dev.isxander.kanzicontrol.blockhighlight.BlockOutlineRenderType;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ChunkBufferBuilderPack.class)
public class ChunkBufferBuilderPackMixin {
    @Shadow @Final private Map<RenderType, BufferBuilder> builders;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void addOutlineRenderType(CallbackInfo ci) {
        builders.put(BlockOutlineRenderType.OUTLINE, new BufferBuilder(BlockOutlineRenderType.OUTLINE.bufferSize()));
    }

}
