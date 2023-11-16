package dev.isxander.kanzicontrol.mixins;

import dev.isxander.kanzicontrol.utils.EnderDragonFightExt;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.dimension.end.DragonRespawnAnimation;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EndDragonFight.class)
public abstract class EndDragonFightMixin implements EnderDragonFightExt {
    @Shadow
    private @Nullable DragonRespawnAnimation respawnStage;

    @Shadow
    private boolean dragonKilled;

    @Shadow
    @Nullable
    protected abstract EnderDragon createNewDragon();

    @Shadow
    @Final
    private ServerBossEvent dragonEvent;

    @Shadow
    protected abstract void spawnExitPortal(boolean previouslyKilled);

    @Override
    public void bonobo$startFightAgain() {
        this.respawnStage = null;
        this.dragonKilled = false;
        EnderDragon dragon = this.createNewDragon();
        if (dragon != null) {
            for (ServerPlayer player : this.dragonEvent.getPlayers()) {
                CriteriaTriggers.SUMMONED_ENTITY.trigger(player, dragon);
            }
        }

        spawnExitPortal(false);
    }
}
