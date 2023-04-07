package dev.isxander.kanzicontrol.movement;

import net.minecraft.client.player.Input;

public class TouchMovementInput extends Input {
    public static final TouchMovementInput INSTANCE = new TouchMovementInput();

    private int jumpTimer = 0;

    @Override
    public void tick(boolean slowDown, float movementMultiplier) {
        if (jumpTimer-- <= 0) {
            this.jumping = false;
        }
    }

    public void setForward(boolean forward) {
        this.up = forward;
        this.forwardImpulse = forward ? 1 : 0;
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
}
