package dev.isxander.kanzicontrol.mixins;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.isxander.kanzicontrol.config.KanziConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin extends Projectile {

    public FireworkRocketEntityMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    private static final EntityDataAccessor<Float> DATA_SPEED_MULTIPLIER = SynchedEntityData.defineId(FireworkRocketEntity.class, EntityDataSerializers.FLOAT);

    @Inject(method = "defineSynchedData", at = @At("RETURN"))
    private void defineSpeedMultiplier(CallbackInfo ci) {
        this.entityData.define(DATA_SPEED_MULTIPLIER, 1.0f);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void readSpeedMultiplier(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("KanziSpeedMultiplier")) {
            this.entityData.set(DATA_SPEED_MULTIPLIER, tag.getFloat("KanziSpeedMultiplier"));
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void writeSpeedMultiplier(CompoundTag tag, CallbackInfo ci) {
        tag.putFloat("KanziSpeedMultiplier", this.entityData.get(DATA_SPEED_MULTIPLIER));
    }

    @Definition(id = "setDeltaMovement", method = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V")
    @Definition(id = "add", method = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;")
    @Expression("?.setDeltaMovement(@(?.add(?, ?, ?)))")
    @WrapOperation(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private Vec3 modifyAttachedRocketSpeed(Vec3 instance, double x, double y, double z, Operation<Vec3> original) {
        float m = this.entityData.get(DATA_SPEED_MULTIPLIER);
        return original.call(instance, x*m, y*m, z*m);
    }
}
