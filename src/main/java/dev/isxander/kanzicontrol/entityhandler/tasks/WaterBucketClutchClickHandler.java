package dev.isxander.kanzicontrol.entityhandler.tasks;

import dev.isxander.kanzicontrol.TouchInput;
import dev.isxander.kanzicontrol.entityhandler.AbstractAutoPlayerTask;
import dev.isxander.kanzicontrol.entityhandler.subtasks.SimpleTask;
import dev.isxander.kanzicontrol.entityhandler.subtasks.SleepTask;
import dev.isxander.kanzicontrol.entityhandler.subtasks.SmoothlyLookAtTask;
import dev.isxander.kanzicontrol.entityhandler.subtasks.SubTask;
import dev.isxander.kanzicontrol.utils.InventoryUtils;
import net.minecraft.world.item.Items;

public class WaterBucketClutchClickHandler extends AbstractAutoPlayerTask {
    @Override
    public void start() {
        int bucketSlot = InventoryUtils.findSlotMatching(stack -> stack.is(Items.WATER_BUCKET), false);
        if (bucketSlot == -1) {
            return;
        }

        queueTask(new SmoothlyLookAtTask(player.getYRot(), 90f, 3));
        queueTask(new SimpleTask(() -> InventoryUtils.swapSlots2(bucketSlot, player.getInventory().selected)));
        queueTask(new PlaceWaterTask());

        queueTask(new SleepTask(2));
        if (player.isInWater() && player.getMainHandItem().is(Items.WATER_BUCKET)) {
            queueTask(new PickupWaterTask());
        }

        queueTask(new SimpleTask(() -> InventoryUtils.swapSlots2(bucketSlot, player.getInventory().selected)));
        queueTask(new SmoothlyLookAtTask(player.getYRot(), 0f, 10));
    }

    private class PlaceWaterTask implements SubTask {
        private int ticks;

        @Override
        public void tick() {
            TouchInput.INSTANCE.startUseItem();
            ticks++;
        }

        @Override
        public boolean shouldFinish() {
            return player.onGround() || ticks > 20*15;
        }
    }

    private class PickupWaterTask implements SubTask {
        private int ticks;

        @Override
        public void tick() {
            TouchInput.INSTANCE.startUseItem();
            ticks++;
        }

        @Override
        public boolean shouldFinish() {
            return !player.isInWater() || ticks > 20;
        }
    }
}
