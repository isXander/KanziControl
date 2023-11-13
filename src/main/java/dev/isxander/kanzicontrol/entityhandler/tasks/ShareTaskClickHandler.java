package dev.isxander.kanzicontrol.entityhandler.tasks;

import dev.isxander.kanzicontrol.TouchInput;
import dev.isxander.kanzicontrol.entityhandler.AbstractAutoPlayerTask;
import dev.isxander.kanzicontrol.entityhandler.subtasks.SmoothlyLookAtTask;
import dev.isxander.kanzicontrol.entityhandler.subtasks.SubTask;
import dev.isxander.kanzicontrol.utils.InventoryUtils;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Predicate;

public class ShareTaskClickHandler extends AbstractAutoPlayerTask {
    private final Entity entityToShareWith;

    public ShareTaskClickHandler(Entity entityToShareWith) {
        this.entityToShareWith = entityToShareWith;
    }

    @Override
    public void start() {
        float prevYaw = player.getYRot();
        float prevPitch = player.getXRot();

        queueTask(new SmoothlyLookAtTask(entityToShareWith, 10));
        if (entityToShareWith.getTags().contains("needsArrows")) {
            queueTask(new DropRandomItemsTask(stack -> stack.is(ItemTags.ARROWS), 20));
        }
        if (entityToShareWith.getTags().contains("needsFood")) {
            queueTask(new DropRandomItemsTask(ItemStack::isEdible, 5));
        }
        if (InventoryUtils.hasSlotMatching(stack -> stack.is(Items.SPLASH_POTION))) {
            queueTask(new ThrowPotionTask());
        }
        queueTask(new SmoothlyLookAtTask(prevYaw, prevPitch, 10));
    }

    private static class DropRandomItemsTask implements SubTask {
        private final Predicate<ItemStack> predicate;
        private final int amount;
        private boolean dropped;

        public DropRandomItemsTask(Predicate<ItemStack> predicate, int amount) {
            this.predicate = predicate;
            this.amount = amount;
        }

        @Override
        public void start() {
            dropped = true;

            RandomSource random = RandomSource.create();
            for (int i = 0; i < amount; i++) {
                int slot = InventoryUtils.findRandomSlotMatching(predicate, random);
                if (slot == -1) {
                    break;
                }

                InventoryUtils.dropSlot(slot);
            }
        }

        @Override
        public boolean shouldFinish() {
            return dropped;
        }
    }

    private class ThrowPotionTask implements SubTask {
        private boolean thrown;
        private boolean finished;

        private int prevHotbarSlot = -1, bestHotbarSlot, potionSlot;

        @Override
        public void start() {
            potionSlot = InventoryUtils.findSlotMatching(stack -> stack.is(Items.SPLASH_POTION), false);
            if (potionSlot == -1) {
                finished = true;
                return;
            }

            prevHotbarSlot = player.getInventory().selected;
            if (potionSlot < 0 || potionSlot >= 9) {
                bestHotbarSlot = player.getInventory().getSuitableHotbarSlot();
                InventoryUtils.swapSlots(potionSlot, bestHotbarSlot);
            } else {
                bestHotbarSlot = potionSlot;
                InventoryUtils.selectHotbarSlotNow(bestHotbarSlot);
            }

            TouchInput.INSTANCE.startUseItem();

            thrown = true;
        }

        @Override
        public void tick() {
            if (thrown) {
                TouchInput.INSTANCE.stopUsingItem();

                if (bestHotbarSlot != potionSlot) {
                    InventoryUtils.swapSlots(potionSlot, bestHotbarSlot);
                }
                InventoryUtils.selectHotbarSlotNow(prevHotbarSlot);

                finished = true;
            }
        }

        @Override
        public boolean shouldFinish() {
            return finished;
        }
    }
}
