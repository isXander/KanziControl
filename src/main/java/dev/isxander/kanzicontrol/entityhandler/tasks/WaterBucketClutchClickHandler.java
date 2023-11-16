package dev.isxander.kanzicontrol.entityhandler.tasks;

import dev.isxander.kanzicontrol.TouchInput;
import dev.isxander.kanzicontrol.entityhandler.AbstractAutoPlayerTask;
import dev.isxander.kanzicontrol.entityhandler.subtasks.SimpleTask;
import dev.isxander.kanzicontrol.entityhandler.subtasks.SleepTask;
import dev.isxander.kanzicontrol.entityhandler.subtasks.SmoothlyLookAtTask;
import dev.isxander.kanzicontrol.entityhandler.subtasks.SubTask;
import dev.isxander.kanzicontrol.utils.InventoryUtils;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;

import java.util.concurrent.atomic.AtomicInteger;

public class WaterBucketClutchClickHandler extends AbstractAutoPlayerTask {
    @Override
    public void start() {
        int bucketSlot = InventoryUtils.findSlotMatching(stack -> stack.is(Items.WATER_BUCKET), false);
        AtomicInteger prevSlot = new AtomicInteger(-1);
        if (bucketSlot == -1) {
            return;
        }

        queueTask(new SmoothlyLookAtTask(player.getYRot(), 90f, 3));
        queueTask(new SimpleTask(() -> {
            prevSlot.set(player.getInventory().selected);
            if (Inventory.isHotbarSlot(bucketSlot)) {
                InventoryUtils.selectHotbarSlotNow(bucketSlot);
            } else {
                InventoryUtils.swapSlots2(bucketSlot, 8);
                InventoryUtils.selectHotbarSlotNow(8);
            }
        }));
        queueTask(new PlaceWaterTask());

        queueTask(new SleepTask(5));
        if (player.isInWater() && player.getMainHandItem().is(Items.WATER_BUCKET)) {
            queueTask(new PickupWaterTask());
        }

        queueTask(new SimpleTask(() -> {
            if (prevSlot.get() != -1) {
                player.getInventory().selected = prevSlot.get();
            }

            InventoryUtils.swapSlots2(bucketSlot, 8);
        }));

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
