package dev.isxander.kanzicontrol.entityhandler.subtasks;

import dev.isxander.kanzicontrol.utils.InventoryUtils;

public class SwapSlotsTask implements SubTask {
    private final int slot1, slot2;

    public SwapSlotsTask(int slot1, int slot2) {
        this.slot1 = slot1;
        this.slot2 = slot2;
    }

    @Override
    public void start() {
        InventoryUtils.swapSlots(slot2, slot1);
    }

    @Override
    public boolean shouldFinish() {
        return true;
    }
}
