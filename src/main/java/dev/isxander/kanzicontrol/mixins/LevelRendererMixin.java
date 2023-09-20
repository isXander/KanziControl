package dev.isxander.kanzicontrol.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.isxander.kanzicontrol.blockhighlight.BlockHighlightRenderTypes;
import dev.isxander.kanzicontrol.config.KanziConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.awt.*;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow private @Nullable ClientLevel level;

    @Shadow @Final private Minecraft minecraft;

    @ModifyVariable(method = "renderLevel", at = @At("HEAD"), argsOnly = true)
    private boolean shouldRenderHitOutline(boolean renderHitOutline) {
        if (!KanziConfig.INSTANCE.instance().enabled)
            return renderHitOutline;

        HitResult hitResult = minecraft.hitResult;
        boolean isLookingAtBlock = hitResult.getType() == HitResult.Type.BLOCK;
        if (isLookingAtBlock) {
            Minecraft minecraft = Minecraft.getInstance();
            BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
            boolean canBreak = !minecraft.player.blockActionRestricted(minecraft.level, blockPos, minecraft.gameMode.getPlayerMode());
            boolean canPlace = minecraft.gameMode.getPlayerMode() != GameType.ADVENTURE || minecraft.player.getMainHandItem().hasAdventureModePlaceTagForBlock(minecraft.level.registryAccess().registryOrThrow(Registries.BLOCK), new BlockInWorld(minecraft.level, ((BlockHitResult) hitResult).getBlockPos(), false)) || minecraft.player.getMainHandItem().isEmpty();
            return (canBreak || canPlace) && renderHitOutline;
        }
        return false;
    }

    @WrapOperation(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;renderHitOutline(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"
            )
    )
    private void modifyRenderType(LevelRenderer instance, PoseStack matrices, VertexConsumer consumer, Entity entity, double offsetX, double offsetY, double offsetZ, BlockPos blockPos, BlockState blockState, Operation<Void> original, @Local MultiBufferSource.BufferSource bufferSource) {
        if (!KanziConfig.INSTANCE.instance().enabled || !KanziConfig.INSTANCE.instance().useEnhancedBlockHighlight) {
            original.call(instance, matrices, consumer, entity, offsetX, offsetY, offsetZ, blockPos, blockState);
            return;
        }

        VoxelShape voxelShape = blockState.getShape(level, blockPos, CollisionContext.of(entity));

        RenderType renderType = KanziConfig.INSTANCE.instance().ignoreBlockHighlightDepth
                ? BlockHighlightRenderTypes.NO_DEPTH_BLOCK_FILL
                : RenderType.debugFilledBox();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);

        Color color = KanziConfig.INSTANCE.instance().blockHighlightColor;
        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = color.getAlpha() / 255f;

        for (AABB part : voxelShape.toAabbs()) {
            AABB positioned = part.move(blockPos).move(-offsetX, -offsetY, -offsetZ);

            LevelRenderer.addChainedFilledBoxVertices(
                    matrices,
                    vertexConsumer,
                    positioned.minX, positioned.minY, positioned.minZ,
                    positioned.maxX, positioned.maxY, positioned.maxZ,
                    r, g, b, a
            );
        }
    }
}
