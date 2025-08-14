package dev.isxander.kanzicontrol.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Shadow
    @Final
    protected SynchedEntityData entityData;
    @Unique
    private static final EntityDataAccessor<Boolean> DATA_INF_RENDER_DISTANCE = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);

    @Unique
    private boolean isInfiniteRenderDistance() {
        return this.entityData.get(DATA_INF_RENDER_DISTANCE);
    }

    @Unique
    private void setInfiniteRenderDistance(boolean value) {
        this.entityData.set(DATA_INF_RENDER_DISTANCE, value);
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;defineSynchedData()V"))
    private void defineRenderDistanceScaling(CallbackInfo ci) {
        this.entityData.define(DATA_INF_RENDER_DISTANCE, false);
    }

    @Inject(method = "saveWithoutId", at = @At("RETURN"))
    private void saveScalingTag(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        tag.putBoolean("KanziInfiniteRD", this.isInfiniteRenderDistance());
    }

    @Inject(method = "load", at = @At("RETURN"))
    private void loadScalingTag(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("KanziInfiniteRD")) {
            this.setInfiniteRenderDistance(tag.getBoolean("KanziInfiniteRD"));
        }
    }

    @ModifyReturnValue(method = "shouldRenderAtSqrDistance", at = @At("RETURN"))
    private boolean shouldRender(boolean original) {
        return this.isInfiniteRenderDistance() || original;
    }
}
