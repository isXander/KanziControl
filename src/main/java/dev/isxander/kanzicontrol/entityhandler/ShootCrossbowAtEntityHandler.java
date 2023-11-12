package dev.isxander.kanzicontrol.entityhandler;

import dev.isxander.kanzicontrol.TouchInput;
import dev.isxander.kanzicontrol.entityhandler.subtasks.RecenterYawTask;
import dev.isxander.kanzicontrol.entityhandler.subtasks.SubTask;
import dev.isxander.kanzicontrol.utils.BowAimUtils;
import dev.isxander.kanzicontrol.utils.InventoryUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.Validate;

public class ShootCrossbowAtEntityHandler extends AbstractEntityClickHandler<Entity> {
    private final float heightAim;

    public ShootCrossbowAtEntityHandler(Entity entity, float heightAim) {
        super(entity);
        this.heightAim = heightAim;
    }

    @Override
    public void start() {
        ItemStack crossbowStack = InventoryUtils.findAndSelectItemInHotbar(stack -> stack.getItem() instanceof CrossbowItem);
        if (crossbowStack == null) {
            return;
        }

        if (!CrossbowItem.isCharged(crossbowStack)) {
            queueTask(new LoadCrossbowTask());
        }
        queueTask(new AimAndShootTask(crossbowStack));
        queueTask(new RecenterYawTask());
        queueTask(new LoadCrossbowTask());
    }

    private static class LoadCrossbowTask implements SubTask {
        private int ticks;

        public LoadCrossbowTask() {
        }

        @Override
        public void start() {
            TouchInput.INSTANCE.startUseItem();
        }

        @Override
        public void tick() {
            if (ticks == 30) {
                TouchInput.INSTANCE.stopUsingItem();
            }

            ticks++;
        }

        @Override
        public boolean shouldFinish() {
            return ticks > 30;
        }
    }

    private class AimAndShootTask implements SubTask {
        private int aimTicks = 0;

        private float targetYaw, currentYaw;
        private float targetPitch, currentPitch;

        private final ItemStack crossbowStack;

        public AimAndShootTask(ItemStack crossbowStack) {
            this.crossbowStack = crossbowStack;
        }

        @Override
        public void start() {
            Validate.isTrue(CrossbowItem.isCharged(crossbowStack), "Crossbow must be loaded to aim and shoot");
        }

        @Override
        public void tick() {
            if (aimTicks < 10) {
                BowAimUtils.Rotation rotation = BowAimUtils.aimCorrectlyAtTarget(entity.position().add(0, entity.getBbHeight() * heightAim, 0), entity);
                if (rotation != null) {
                    targetYaw = rotation.yaw();
                    targetPitch = rotation.pitch();
                }
            } else if (aimTicks == 10) {
                TouchInput.INSTANCE.toggleUseItem();
            }

            aimTicks++;
        }

        @Override
        public void onFrame(float tickDelta) {
            currentYaw = Mth.lerp(tickDelta, currentYaw, targetYaw);
            currentPitch = Mth.lerp(tickDelta, currentPitch, targetPitch);

            Minecraft.getInstance().player.setXRot(currentPitch);
            Minecraft.getInstance().player.setYRot(currentYaw);
        }

        @Override
        public boolean shouldFinish() {
            return aimTicks > 11;
        }
    }

}
