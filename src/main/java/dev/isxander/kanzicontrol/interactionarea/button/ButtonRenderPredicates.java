package dev.isxander.kanzicontrol.interactionarea.button;

import dev.isxander.kanzicontrol.utils.ClientTagHolder;
import dev.isxander.kanzicontrol.utils.InventoryUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
            HUNGRY = predicate("hungry", ctx -> {
                Minecraft minecraft = Minecraft.getInstance();
                LocalPlayer player = minecraft.player;
                if (player == null) return false;
                return player.canEat(false) && InventoryUtils.hasSlotMatching(ItemStack::isEdible);
            }),
            PLAYER_NEARBY = predicate("player_nearby", ctx -> {
                LocalPlayer player = Minecraft.getInstance().player;
                return player.level().getNearestPlayer(player.getX(), player.getY(), player.getZ(), 3, entity -> !entity.isSpectator() && !entity.equals(player)) != null;
            }),
            PLAYER_NEARBY_SHARING = predicate("player_nearby_sharing", ctx -> {
                LocalPlayer localPlayer = Minecraft.getInstance().player;
                return localPlayer.level().getNearestPlayer(
                        localPlayer.getX(), localPlayer.getY(), localPlayer.getZ(),
                        3,
                        entity -> {
                            if (entity.equals(localPlayer)) return false;
                            if (entity instanceof Player player) {
                                Set<String> tags = ClientTagHolder.getClientTags(player);
                                return (tags.contains("needsFood") && InventoryUtils.hasSlotMatching(ItemStack::isEdible))
                                        || (tags.contains("needsArrows") && InventoryUtils.hasSlotMatching(stack -> stack.is(ItemTags.ARROWS)));
                            }
                            return false;
                        }
                ) != null;
            }),
            FALLING = predicate("falling", ctx -> {
                LocalPlayer player = Minecraft.getInstance().player;
                return player.fallDistance > 2.5;
            });

    private static ButtonRenderPredicate predicate(String id, ButtonRenderPredicate predicate) {
        ALL.put(id, predicate);
        return predicate;
    }
}
