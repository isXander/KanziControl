package dev.isxander.kanzicontrol.mixins;

import dev.isxander.kanzicontrol.server.KanziControlMain;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonSittingPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.Node;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EnderDragon.class)
public abstract class EnderDragonMixin extends Mob {
    @Shadow
    @Final
    private Node[] nodes;

    @Shadow
    @Final
    private int[] nodeAdjacency;

    @Shadow
    public abstract int findClosestNode(double x, double y, double z);

    protected EnderDragonMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * Tweak the pathfinding of the dragon
     * @reason Calculating all the pathfinding nodes is very difficult to modify to my liking, so overwrite works best
     *      additionally,
     * @author xander
     */
    @Overwrite
    public int findClosestNode() {
        // calculate the pathfinding nodes
        // why isn't this its own method??
        if (this.nodes[0] == null) {
            // must have 24 nodes to fill the array
            for (int i = 0; i < 24; i++) {
                int x, z;
                int extraY = 15;

                if (i < 12) { // OUTER CIRCLE
                    float radius = 35f;
                    // get 12 nodes in a circle with a radius of 45
                    x = Mth.floor(radius * Mth.cos(2 * (-Mth.PI + Mth.PI / 12 * i)));
                    z = Mth.floor(radius * Mth.sin(2 * (-Mth.PI + Mth.PI / 12 * i)));

                    extraY += 25;
                } else { // INNER CIRCLE
                    float radius = 28f;
                    // get 12 nodes in a circle with a radius of 25
                    x = Mth.floor(radius * Mth.cos(2 * (-Mth.PI + Mth.PI / 12 * (i - 12))));
                    z = Mth.floor(radius * Mth.sin(2 * (-Mth.PI + Mth.PI / 12 * (i - 12))));

                    extraY += 15;
                }

                // prevent dragon pathfinding into the void when not over land
                int y = Math.max(
                        this.level().getSeaLevel() + 10,
                        this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z)).getY() + extraY
                );
                this.nodes[i] = new Node(x, y, z);
            }

            // assuming the first 12 nodes form a large circle
            for (int i = 0; i < 12; i++) {
                int nextIndex = (i + 1) % 12;
                int prevIndex = (i - 1 + 12) % 12;

                nodeAdjacency[i] |= (1 << nextIndex);
                nodeAdjacency[i] |= (1 << prevIndex);
            }

            // assuming the last 12 nodes form a smaller circle
            for (int i = 12; i < 24; i++) {
                int nextIndex = (i + 1) % 24;
                int prevIndex = (i - 1 + 24) % 24;

                nodeAdjacency[i] |= (1 << nextIndex);
                nodeAdjacency[i] |= (1 << prevIndex);
            }
        }

        return this.findClosestNode(this.getX(), this.getY(), this.getZ());
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/enderdragon/phases/DragonPhaseInstance;doServerTick()V"))
    private void onServerTickPhase(DragonPhaseInstance instance) {
        instance.doServerTick();

        if (instance instanceof AbstractDragonSittingPhase && tickCount % 20 == 0) {
            List<Player> players = level().getNearbyPlayers(
                    TargetingConditions.forNonCombat(),
                    this,
                    this.getBoundingBox().inflate(64.0)
            );
            for (Player p : players) {
                p.addEffect(new MobEffectInstance(KanziControlMain.DRAGON_BREATH_EFFECT, 25, 0, false, false));
            }
        }
    }
}
