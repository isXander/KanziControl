package dev.isxander.kanzicontrol.entityhandler.tasks;

import dev.isxander.kanzicontrol.entityhandler.AbstractAutoPlayerTask;
import dev.isxander.kanzicontrol.entityhandler.subtasks.EatTask;
import dev.isxander.kanzicontrol.entityhandler.subtasks.SelectSlotTask;
import dev.isxander.kanzicontrol.utils.InventoryUtils;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Comparator;

public class AutoEatTask extends AbstractAutoPlayerTask {
    @Override
    public void start() {
        int foodSlot = -1;

        if (player.getHealth() < player.getMaxHealth()) {
            foodSlot = InventoryUtils.findSlotMatching(stack -> stack.is(Items.GOLDEN_APPLE) || stack.is(Items.ENCHANTED_GOLDEN_APPLE), false);
        }

        if (foodSlot == -1) {
            foodSlot = InventoryUtils.findAllSlotsMatching(stack -> stack.isEdible() && player.canEat(stack.getItem().getFoodProperties().canAlwaysEat()))
                    .max(Comparator.comparingInt(slot -> {
                        ItemStack stack = player.getInventory().items.get(slot);
                        FoodProperties food = stack.getItem().getFoodProperties();

                        return food.getNutrition();
                    }))
                    .orElse(-1);
        }
        if (foodSlot == -1)
            return;

        queueTask(new SelectSlotTask(this, foodSlot, true));
        queueTask(new EatTask());
    }
}
