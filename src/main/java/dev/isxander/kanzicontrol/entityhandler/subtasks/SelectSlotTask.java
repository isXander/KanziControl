package dev.isxander.kanzicontrol.entityhandler.subtasks;

import dev.isxander.kanzicontrol.entityhandler.AbstractAutoPlayerTask;
import dev.isxander.kanzicontrol.utils.InventoryUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;

public class SelectSlotTask implements SubTask {
    private final AbstractAutoPlayerTask parentTask;
    private final int slot;
    private final boolean swapBack;

    public SelectSlotTask(AbstractAutoPlayerTask parentTask, int slot, boolean swapBack) {
        this.parentTask = parentTask;
        this.slot = slot;
        this.swapBack = swapBack;
    }

    @Override
    public void start() {
        Minecraft minecraft = Minecraft.getInstance();
        Inventory inventory = minecraft.player.getInventory();

        if (slot == -1)
            return;

        if (Inventory.isHotbarSlot(slot)) {
            inventory.selected = slot;
        } else {
            int swapSlot = inventory.getSuitableHotbarSlot();
            InventoryUtils.swapSlots(slot, swapSlot);

            if (swapBack)
                parentTask.queueTask(new SwapSlotsTask(swapSlot, slot));
        }
    }

    @Override
    public boolean shouldFinish() {
        return true;
    }
}
