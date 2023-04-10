package dev.isxander.kanzicontrol;

import dev.isxander.kanzicontrol.mixins.MinecraftAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class TouchInput extends Input {
    public static final TouchInput INSTANCE = new TouchInput();

    private int forwardRemainingTicks = 0;

    private boolean isMining = false;

    private int jumpTimer = 0;
    private boolean swimDown = false;

    @Override
    public void tick(boolean slowDown, float movementMultiplier) {
        if (forwardRemainingTicks > 0) {
            this.up = true;
            this.forwardImpulse = 1;
            forwardRemainingTicks--;
        } else {
            this.up = false;
            this.forwardImpulse = 0;
        }

        if (jumpTimer-- <= 0) {
            this.jumping = false;
        }
        jumpTimer = Math.max(jumpTimer, 0);

        if (getPlayer().isInWater() && !swimDown) {
            this.jumping = true;
        }

        var minecraft = Minecraft.getInstance();
        if (isMining && !minecraft.player.isUsingItem()) {
            if (minecraft.gameMode != null && minecraft.hitResult instanceof BlockHitResult blockHit) {
                if (!minecraft.level.getBlockState(blockHit.getBlockPos()).isAir() && minecraft.gameMode.continueDestroyBlock(blockHit.getBlockPos(), blockHit.getDirection())) {
                    minecraft.particleEngine.crack(blockHit.getBlockPos(), blockHit.getDirection());
                    minecraft.player.swing(InteractionHand.MAIN_HAND);
                }
            } else {
                cancelMining();
            }
        }
    }

    public void setForward(int time) {
        this.forwardRemainingTicks = time;
        cancelMining();
    }

    public boolean isMovingForward() {
        return forwardRemainingTicks > 0;
    }

    public void setBack(boolean back) {
        this.down = back;
        this.forwardImpulse = back ? -1 : 0;
    }

    public void setLeft(boolean left) {
        this.left = left;
        this.leftImpulse = left ? 1 : 0;
    }

    public void setRight(boolean right) {
        this.right = right;
        this.leftImpulse = right ? -1 : 0;
    }

    public void jump() {
        if (jumpTimer <= 0) {
            this.jumping = true;
            jumpTimer = 5;
        }
    }

    public void toggleSwimDown() {
        if (getPlayer().isInWater())
            this.swimDown = !this.swimDown;
    }

    public void attack() {
        var minecraft = Minecraft.getInstance();

        if (minecraft.gameMode != null && minecraft.hitResult != null) {
            switch (minecraft.hitResult.getType()) {
                case ENTITY -> minecraft.gameMode.attack(minecraft.player, ((EntityHitResult) minecraft.hitResult).getEntity());
                case MISS, BLOCK -> minecraft.player.swing(InteractionHand.MAIN_HAND);
            }
        }
    }

    public void toggleMining() {
        var minecraft = Minecraft.getInstance();

        if (isMining) {
            cancelMining();
            return;
        }

        if (minecraft.gameMode != null
                && minecraft.hitResult instanceof BlockHitResult blockHit
                && !minecraft.level.getBlockState(blockHit.getBlockPos()).isAir()
        ) {
            isMining = true; // must set before calling startDestroyBlock, which may set it to false if instabreak
            if (!minecraft.gameMode.startDestroyBlock(blockHit.getBlockPos(), blockHit.getDirection())) {
                // if fails, cancel mining
                isMining = false;
            } else if (!isMining) { // if startDestroyBlock set isMining to false, we need to show it has broken, which usually happens on tick
                minecraft.particleEngine.crack(blockHit.getBlockPos(), blockHit.getDirection());
                minecraft.player.swing(InteractionHand.MAIN_HAND);
            }
        }
    }

    public void cancelMining() {
        var minecraft = Minecraft.getInstance();

        if (isMining && minecraft.gameMode != null) {
            minecraft.gameMode.stopDestroyBlock();
            isMining = false;
        }
    }

    public void toggleUseItem() {
        var minecraft = Minecraft.getInstance();
        if (minecraft.player.isUsingItem()) {
            minecraft.gameMode.releaseUsingItem(minecraft.player);
        } else {
            ((MinecraftAccessor) minecraft).invokeStartUseItem();
        }
    }

    private LocalPlayer getPlayer() {
        return Minecraft.getInstance().player;
    }
}