package dev.isxander.kanzicontrol.mixins;

import dev.isxander.kanzicontrol.server.KanziControlMain;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpikeFeature.class)
public class SpikeFeatureMixin {
    /**
     * Replace the end crystal type with the regular hitbox if guarded so the
     * hitbox doesn't expand past the iron bars.
     */
    @Redirect(
            method = "placeSpike",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/EntityType;END_CRYSTAL:Lnet/minecraft/world/entity/EntityType;",
                    opcode = Opcodes.GETSTATIC
            )
    )
    private EntityType<EndCrystal> replaceEndCrystalType(ServerLevelAccessor world, RandomSource random, SpikeConfiguration config, SpikeFeature.EndSpike spike) {
        if (spike.isGuarded()) {
            return KanziControlMain.END_CRYSTAL_SML_HITBOX;
        } else {
            return EntityType.END_CRYSTAL;
        }
    }
}
