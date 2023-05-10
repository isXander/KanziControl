package dev.isxander.kanzicontrol.interactionarea.button;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.HashMap;
import java.util.Map;

public final class ButtonRenderPredicates {
    public static final Map<String, ButtonRenderPredicate> ALL = new HashMap<>();

    public static final ButtonRenderPredicate
            ALWAYS = predicate("always", ctx -> true),
            HOVER = predicate("hover", ButtonRenderPredicate.RenderCtx::isHovered),
            LOOK_AT_BLOCK = predicate("look_at_block", ctx -> {
                HitResult hitResult = ctx.hitResult();
                boolean isLookingAtBlock = hitResult.getType() == HitResult.Type.BLOCK;
                if (isLookingAtBlock) {
                    Minecraft minecraft = Minecraft.getInstance();
                    BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
                    return minecraft.player.blockActionRestricted(minecraft.level, blockPos, minecraft.gameMode.getPlayerMode());
                }
                return false;
            }),
            LOOK_AT_ENTITY = predicate("look_at_entity", ctx -> ctx.hitResult().getType() == HitResult.Type.ENTITY);

    private static ButtonRenderPredicate predicate(String id, ButtonRenderPredicate predicate) {
        ALL.put(id, predicate);
        return predicate;
    }
}
