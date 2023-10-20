package dev.isxander.kanzicontrol.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(targets = "net.minecraft.world.level.levelgen.feature.SpikeFeature$SpikeCacheLoader")
public class SpikeCacheLoaderMixin {
    @ModifyExpressionValue(method = "load(Ljava/lang/Long;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;create(J)Lnet/minecraft/util/RandomSource;"))
    private RandomSource captureRandomSource(RandomSource random, @Share("random") LocalRef<RandomSource> share) {
        share.set(random);
        return random;
    }

    /**
     * Adjust the height and the iron-bar properties of each end tower here.
     * Was going to use this to disabling iron bars altogether or lower end towers just a tad, but may not be necessary.
     */
    @ModifyArgs(method = "load(Ljava/lang/Long;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/SpikeFeature$EndSpike;<init>(IIIIZ)V"))
    private void modifySpikeFeature(Args args, @Share("random") LocalRef<RandomSource> random) {
        int height = args.get(3);
        boolean guarded = args.get(4);
    }
}
