package dev.isxander.kanzicontrol.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class PlayerUtils {
    public static int findSlotMatching(Predicate<ItemStack> predicate) {
        Inventory inv = Minecraft.getInstance().player.getInventory();
        for (int i = 0; i < inv.items.size(); i++) {
            if (predicate.test(inv.items.get(i))) {
                return i;
            }
        }

        return -1;
    }

    public static boolean hasSlotMatching(Predicate<ItemStack> predicate) {
        return findSlotMatching(predicate) != -1;
    }

    public static void swapSlots(int slot1, int slot2) {
        Minecraft minecraft = Minecraft.getInstance();

        int serverSlot1 = convertClientToServerSlot(slot1);
        int serverSlot2 = convertClientToServerSlot(slot2);
        Player player = Minecraft.getInstance().player;

        minecraft.gameMode.handleInventoryMouseClick(0, serverSlot2, serverSlot1, ClickType.SWAP, player);
        minecraft.getConnection().send(new ServerboundContainerClosePacket(0));
    }

    public static ItemStack findAndSelectItemInHotbar(Predicate<ItemStack> stack) {
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = Minecraft.getInstance().player.getInventory().getItem(i);
            if (stack.test(itemStack)) {
                Minecraft.getInstance().player.getInventory().selected = i;
                return itemStack;
            }
        }

        return null;
    }

    public static int convertClientToServerSlot(int slot) {
        if (slot >= 0 && slot < 9)
            return 36 + slot;
        if (slot >= 9 && slot < 36)
            return slot;
        if (slot >= 36 && slot < 40)
            return 39 - slot + 5;
        if (slot == 40)
            return 45;
        throw new IllegalArgumentException();
    }
}
