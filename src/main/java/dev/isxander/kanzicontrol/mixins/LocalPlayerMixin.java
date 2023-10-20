package dev.isxander.kanzicontrol.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.GameProfile;
import dev.isxander.kanzicontrol.config.KanziConfig;
import dev.isxander.kanzicontrol.interactionarea.RootInteractionArea;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
    @Unique private float lastHealth = this.getHealth();
    @Unique private boolean skipTick = true;

    @Unique private boolean isAboutToFall = false;

    public LocalPlayerMixin(ClientLevel world, GameProfile profile) {
        super(world, profile);
    }

    /**
     * Detect if the health has changed since last tick and notify damage flash area.
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (!KanziConfig.INSTANCE.instance().enabled)
            return;

        float health = this.getHealth();
        float damageTaken = Math.max(0, lastHealth - health);
        lastHealth = health;

        if (damageTaken > 0 && !skipTick) {
            RootInteractionArea.getInstance().SCREEN_DAMAGE_FLASH.indicateDamage();
        }
        // skip first tick from spawn
        skipTick = false;
    }

    /**
     * Allow auto-jump whilst sneaking.
     */
    @ModifyExpressionValue(method = "canAutoJump", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isStayingOnGroundSurface()Z"))
    private boolean shouldNotBypassSneakingCheck(boolean sneaking) {
        return !KanziConfig.INSTANCE.instance().enabled;
    }

    /**
     * Sneak when on an edge.
     */
    @ModifyReturnValue(method = "isShiftKeyDown", at = @At("RETURN"))
    private boolean shouldBeSneaking(boolean sneaking) {
        return sneaking || isAboutToFall;
    }

    /**
     * Update the edge detection.
     */
    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V", shift = At.Shift.BEFORE))
    private void onMovePre(MoverType movementType, Vec3 movement, CallbackInfo ci) {
        if (KanziConfig.INSTANCE.instance().enabled) {
            isAboutToFall = checkWillFall(movement, movementType);
        } else {
            isAboutToFall = false;
        }
    }

    /**
     * Detects whether the movement vector will result in the player being on an edge of a >=5 block drop.
     * @param movement
     * @param type
     * @return
     */
    @Unique
    protected boolean checkWillFall(Vec3 movement, MoverType type) {
        if (!this.getAbilities().flying
                && movement.y <= 0.0
                && (type == MoverType.SELF || type == MoverType.PLAYER)
                && ((PlayerAccessor) this).invokeIsAboveGround()) {
            double xd = movement.x;
            double zd = movement.z;
            double minLedge = 5.0;

            Vec3 top = this.position().add(xd, 0.0, zd).add(getBbWidth() / 2f, -maxUpStep(), getBbWidth() / 2f);
            Vec3 bottom = top.add(0, -minLedge, 0);

            if (this.level().clip(new ClipContext(top, bottom, ClipContext.Block.VISUAL, ClipContext.Fluid.ANY, this)).getType() == HitResult.Type.MISS)
                return true;
        }

        return false;
    }
}
