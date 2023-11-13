package dev.isxander.kanzicontrol.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonHoldingPatternPhase;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DragonHoldingPatternPhase.class)
public class DragonHoldingPatternPhaseMixin {
    @Shadow
    private @Nullable Vec3 targetLocation;

    /**
     * Make the dragon change direction more infrequently
     */
    @ModifyExpressionValue(method = "findNewTarget", at = @At(value = "CONSTANT", args = "intValue=8"))
    private int modifyChangeDirectionOdds(int original) {
        return original * 4; // third the odds of changing direction
    }
}
