package dev.isxander.kanzicontrol.server.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import dev.isxander.kanzicontrol.utils.EnderDragonFightExt;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonSittingPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class DragonFightCommand {
    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dragon")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("charge")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> {
                                    EndDragonFight dragonFight = getDragonFight(ctx.getSource());
                                    EnderDragon dragon = getDragon(dragonFight, ctx.getSource(), true);

                                    Player player = EntityArgument.getPlayer(ctx, "player");

                                    dragon.getPhaseManager().setPhase(EnderDragonPhase.CHARGING_PLAYER);
                                    dragon.getPhaseManager().getPhase(EnderDragonPhase.CHARGING_PLAYER)
                                            .setTarget(new Vec3(player.getX(), player.getY() - 5, player.getZ()));

                                    ctx.getSource().sendSuccess(() -> Component.literal("Charging dragon at ").append(player.getName()), true);
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("perch")
                        .executes(ctx -> {
                            EndDragonFight dragonFight = getDragonFight(ctx.getSource());
                            EnderDragon dragon = getDragon(dragonFight, ctx.getSource(), true);

                            dragon.getPhaseManager().setPhase(EnderDragonPhase.LANDING_APPROACH);

                            ctx.getSource().sendSuccess(() -> Component.literal("Dragon is now approaching landing"), true);
                            return 1;
                        })
                )
                .then(Commands.literal("fireballphase")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> {
                                    EndDragonFight dragonFight = getDragonFight(ctx.getSource());
                                    EnderDragon dragon = getDragon(dragonFight, ctx.getSource(), true);
                                    Player player = EntityArgument.getPlayer(ctx, "player");

                                    dragon.getPhaseManager().setPhase(EnderDragonPhase.STRAFE_PLAYER);
                                    dragon.getPhaseManager().getPhase(EnderDragonPhase.STRAFE_PLAYER)
                                            .setTarget(player);

                                    ctx.getSource().sendSuccess(() -> Component.literal("Dragon is now approaching landing"), true);
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("shootfireball")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx ->  {
                                    EndDragonFight dragonFight = getDragonFight(ctx.getSource());
                                    EnderDragon dragon = getDragon(dragonFight, ctx.getSource(), true);
                                    Player player = EntityArgument.getPlayer(ctx, "player");

                                    Vec3 viewVec = dragon.getViewVector(1.0F);
                                    double headX = dragon.head.getX() - viewVec.x;
                                    double headY = dragon.head.getY(0.5) + 0.5;
                                    double headZ = dragon.head.getZ() - viewVec.z;
                                    double diffX = player.getX() - headX;
                                    double diffY = player.getY(0.5) - headY;
                                    double diffZ = player.getZ() - headZ;

                                    if (!dragon.isSilent()) {
                                        dragon.level().levelEvent(null, 1017, dragon.blockPosition(), 0);
                                    }

                                    DragonFireball fireball = new DragonFireball(dragon.level(), dragon, diffX, diffY, diffZ);
                                    fireball.moveTo(headX, headY, headZ, 0.0F, 0.0F);
                                    dragon.level().addFreshEntity(fireball);

                                    ctx.getSource().sendSuccess(() -> Component.literal("Dragon shot fireball at ").append(player.getName()), true);
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("respawn")
                        .executes(ctx -> {
                            ServerLevel world = ctx.getSource().getLevel();

                            EndDragonFight dragonFight = getDragonFight(ctx.getSource());
                            EnderDragon dragon = getDragon(dragonFight, ctx.getSource(), false);
                            if (dragon != null) dragon.kill();

                            List<SpikeFeature.EndSpike> spikeList = SpikeFeature.getSpikesForLevel(world);
                            for (SpikeFeature.EndSpike spike : spikeList) {
                                List<EndCrystal> existingCrystals = world.getEntitiesOfClass(EndCrystal.class, spike.getTopBoundingBox());
                                existingCrystals.forEach(EndCrystal::kill);

                                SpikeConfiguration config = new SpikeConfiguration(true, ImmutableList.of(spike), null);
                                Feature.END_SPIKE.place(
                                        config,
                                        world,
                                        world.getChunkSource().getGenerator(),
                                        RandomSource.create(),
                                        new BlockPos(spike.getCenterX(), 45, spike.getCenterZ())
                                );
                            }

                            ((EnderDragonFightExt) dragonFight).bonobo$startFightAgain();
                            dragonFight.resetSpikeCrystals();

                            ctx.getSource().sendSuccess(() -> Component.literal("Restarted dragon fight."), true);

                            return 1;
                        })
                )
                .then(Commands.literal("stop_perching")
                        .executes(ctx -> {
                            EndDragonFight dragonFight = getDragonFight(ctx.getSource());
                            EnderDragon dragon = getDragon(dragonFight, ctx.getSource(), true);

                            if (!(dragon.getPhaseManager().getCurrentPhase() instanceof AbstractDragonSittingPhase)) {
                                throw new CommandRuntimeException(Component.literal("Dragon is not perching"));
                            }

                            dragon.getPhaseManager().setPhase(EnderDragonPhase.TAKEOFF);

                            ctx.getSource().sendSuccess(() -> Component.literal("Dragon is now flying"), true);
                            return 1;
                        })
                )
                .then(Commands.literal("die")
                        .executes(ctx -> {
                            EndDragonFight dragonFight = getDragonFight(ctx.getSource());
                            EnderDragon dragon = getDragon(dragonFight, ctx.getSource(), true);

                            dragon.hurt(dragon.damageSources().badRespawnPointExplosion(new Vec3(0, 0, 0)), Float.MAX_VALUE);

                            ctx.getSource().sendSuccess(() -> Component.literal("Starting dragon death animation."), true);
                            return 1;
                        })
                )
        );
    }

    private static EndDragonFight getDragonFight(CommandSourceStack source) throws CommandRuntimeException {
        EndDragonFight dragonFight = source.getLevel().getDragonFight();

        if (dragonFight == null) {
            throw new CommandRuntimeException(Component.literal("No dragon fight found!"));
        }

        return dragonFight;
    }

    private static EnderDragon getDragon(EndDragonFight fight, CommandSourceStack source, boolean checkNull) {
        EnderDragon dragon = (EnderDragon) source.getLevel().getEntity(fight.getDragonUUID());

        if (dragon == null && checkNull) {
            throw new CommandRuntimeException(Component.literal("No dragon found!"));
        }

        return dragon;
    }
}
