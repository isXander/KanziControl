package dev.isxander.kanzicontrol.entityhandler;

import dev.isxander.kanzicontrol.TouchInput;
import dev.isxander.kanzicontrol.entityhandler.subtasks.SelectSlotTask;
import dev.isxander.kanzicontrol.entityhandler.subtasks.SubTask;
import dev.isxander.kanzicontrol.utils.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

public class AutoEatTask extends AbstractAutoPlayerTask {
    @Override
    public void start() {
        int slot = PlayerUtils.findSlotMatching(ItemStack::isEdible);
        if (slot == -1)
            return;

        Player player = Minecraft.getInstance().player;
        ItemStack stack = player.getInventory().items.get(slot);
        FoodProperties food = stack.getItem().getFoodProperties();
        if (food == null || !player.canEat(food.canAlwaysEat()))
            return;

        queueTask(new SelectSlotTask(this, slot, true));
        queueTask(new EatTask());
    }

    public static class EatTask implements SubTask {
        @Override
        public void start() {
            TouchInput.INSTANCE.startUseItem();
        }

        @Override
        public boolean shouldFinish() {
            return !TouchInput.INSTANCE.isUsingItem();
        }
    }

}
