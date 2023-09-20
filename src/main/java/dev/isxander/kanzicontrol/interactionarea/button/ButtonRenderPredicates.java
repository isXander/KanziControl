package dev.isxander.kanzicontrol.interactionarea.button;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.HashMap;
import java.util.Map;

public final class ButtonRenderPredicates {
    public static final Map<String, ButtonRenderPredicate> ALL = new HashMap<>();

    public static final ButtonRenderPredicate
            ALWAYS = predicate("always", ctx -> true),
            HOVER = predicate("hover", ButtonRenderPredicate.RenderCtx::isHovered),
            BREAK_BLOCK = predicate("break_block", ctx -> {
                HitResult hitResult = ctx.hitResult();
                boolean isLookingAtBlock = hitResult.getType() == HitResult.Type.BLOCK;
                if (isLookingAtBlock) {
                    Minecraft minecraft = Minecraft.getInstance();
                    BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
                    boolean canBreak = !minecraft.player.blockActionRestricted(minecraft.level, blockPos, minecraft.gameMode.getPlayerMode());
                    boolean canPlace = minecraft.player.mayUseItemAt(blockPos, ((BlockHitResult) hitResult).getDirection(), minecraft.player.getMainHandItem());
                    return canBreak;
                }
                return false;
            }),
            USE_BLOCK = predicate("use_block", ctx -> {
                HitResult hitResult = ctx.hitResult();
                boolean isLookingAtBlock = hitResult.getType() == HitResult.Type.BLOCK;
                if (isLookingAtBlock) {
                    Minecraft minecraft = Minecraft.getInstance();
                    return minecraft.gameMode.getPlayerMode() != GameType.ADVENTURE || minecraft.player.getMainHandItem().hasAdventureModePlaceTagForBlock(minecraft.level.registryAccess().registryOrThrow(Registries.BLOCK), new BlockInWorld(minecraft.level, ((BlockHitResult) hitResult).getBlockPos(), false)) || minecraft.player.getMainHandItem().isEmpty();
                }
                return false;
            }),
            LOOK_AT_ENTITY = predicate("look_at_entity", ctx -> ctx.hitResult().getType() == HitResult.Type.ENTITY),
            HUNGRY = predicate("hungry", ctx -> Minecraft.getInstance().player.getFoodData().needsFood());

    private static ButtonRenderPredicate predicate(String id, ButtonRenderPredicate predicate) {
        ALL.put(id, predicate);
        return predicate;
    }
}
