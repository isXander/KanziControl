package dev.isxander.kanzicontrol.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class InventoryUtils {
    public static int findSlotMatching(Predicate<ItemStack> predicate, boolean last) {
        Inventory inv = Minecraft.getInstance().player.getInventory();

        if (!last) {
            for (int i = 0; i < inv.items.size(); i++) {
                if (predicate.test(inv.items.get(i))) {
                    return i;
                }
            }
        } else {
            for (int i = inv.items.size() - 1; i >= 0; i--) {
                if (predicate.test(inv.items.get(i))) {
                    return i;
                }
            }
        }

        return -1;
    }

    public static int findRandomSlotMatching(Predicate<ItemStack> predicate, RandomSource random) {
        Inventory inv = Minecraft.getInstance().player.getInventory();
        int[] slots = new int[inv.items.size()];
        int slotCount = 0;
        for (int i = 0; i < inv.items.size(); i++) {
            if (predicate.test(inv.items.get(i))) {
                slots[slotCount++] = i;
            }
        }

        if (slotCount == 0) {
            return -1;
        }

        return slots[random.nextInt(slotCount)];
    }

    public static Stream<Integer> findAllSlotsMatching(Predicate<ItemStack> predicate) {
        Inventory inv = Minecraft.getInstance().player.getInventory();
        return Stream.iterate(0, i -> i < inv.items.size(), i -> i + 1)
                .filter(i -> predicate.test(inv.items.get(i)));
    }

    public static boolean hasSlotMatching(Predicate<ItemStack> predicate) {
        return findSlotMatching(predicate, false) != -1;
    }

    public static void swapSlots(int slot1, int slot2) {
        if (slot1 == slot2) return;

        Minecraft minecraft = Minecraft.getInstance();

        int serverSlot1 = convertClientToServerSlot(slot1);
        int serverSlot2 = convertClientToServerSlot(slot2);
        Player player = Minecraft.getInstance().player;

        minecraft.gameMode.handleInventoryMouseClick(0, serverSlot2, serverSlot1, ClickType.SWAP, player);
        minecraft.getConnection().send(new ServerboundContainerClosePacket(0));
    }

    public static void dropSlot(int slot) {
        Minecraft minecraft = Minecraft.getInstance();

        int serverSlot = convertClientToServerSlot(slot);
        Player player = Minecraft.getInstance().player;

        minecraft.gameMode.handleInventoryMouseClick(0, serverSlot, 0, ClickType.THROW, player);

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

    public static void selectHotbarSlotNow(int slot) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player.getInventory().selected == slot) return;

        player.getInventory().selected = slot;
        notifySelectedHotbarSlotNow();
    }

    public static void notifySelectedHotbarSlotNow() {
        LocalPlayer player = Minecraft.getInstance().player;
        player.connection.send(new ServerboundSetCarriedItemPacket(player.getInventory().selected));
    }

    public static void mergeSlots(int slotTo, int slotFrom) {
        Minecraft minecraft = Minecraft.getInstance();

        int serverSlotTo = convertClientToServerSlot(slotTo);
        int serverSlotFrom = convertClientToServerSlot(slotFrom);
        Player player = Minecraft.getInstance().player;

        minecraft.gameMode.handleInventoryMouseClick(0, serverSlotFrom, 0, ClickType.PICKUP, player); // click first slot. now carrying
        minecraft.gameMode.handleInventoryMouseClick(0, serverSlotTo, 0, ClickType.PICKUP, player); // click the second slot. put down
        minecraft.getConnection().send(new ServerboundContainerClosePacket(0));
    }

    /**
     * Swap two slots by imitating the following actions in an inventory:
     * <ol>
     *     <li>Click to pick up the first slot</li>
     *     <li>Click the second slot, swap the two slots from carrying <-> second slot</li>
     *     <li>Click the first slot again, put down the second item</li>
     * </ol>
     * This works regardless if either slot is empty or not.
     */
    public static void swapSlots2(int slot1, int slot2) {
        if (slot1 == slot2) return;

        Minecraft minecraft = Minecraft.getInstance();

        int serverSlot1 = convertClientToServerSlot(slot1);
        int serverSlot2 = convertClientToServerSlot(slot2);
        Player player = Minecraft.getInstance().player;

        minecraft.gameMode.handleInventoryMouseClick(0, serverSlot1, 0, ClickType.PICKUP, player); // click first slot. now carrying
        minecraft.gameMode.handleInventoryMouseClick(0, serverSlot2, 0, ClickType.PICKUP, player); // click the second slot. swap with carrying
        minecraft.gameMode.handleInventoryMouseClick(0, serverSlot1, 0, ClickType.PICKUP, player); // click first slot. put down
        minecraft.getConnection().send(new ServerboundContainerClosePacket(0));
    }

    public static void sortInventory() {
        int swordSlot = 0;
        int crossbowSlot = 1;
        int arrowSlot = 2;
        int foodSlotStart = 3;
        int foodSlotEnd = 7;
        int waterBucketSlot = 8;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = inventory().getItem(i);

            if (stack.is(ItemTags.SWORDS)) {
                if (i != swordSlot) {
                    swapOrMerge(stack, i, inventory().getItem(swordSlot), swordSlot);
                }
            } else if (stack.is(Items.CROSSBOW)) {
                if (i != crossbowSlot) {
                    swapOrMerge(stack, i, inventory().getItem(crossbowSlot), crossbowSlot);
                }
            } else if (stack.is(ItemTags.ARROWS)) {
                if (i != arrowSlot) {
                    swapOrMerge(stack, i, inventory().getItem(arrowSlot), arrowSlot);
                }
            } else if (stack.is(Items.WATER_BUCKET) || stack.is(Items.BUCKET)) {
                ItemStack desiredSlotStack = inventory().getItem(waterBucketSlot);
                if (i != waterBucketSlot && !desiredSlotStack.is(Items.WATER_BUCKET)) {
                    swapSlots2(i, waterBucketSlot);
                }
            } else if (stack.isEdible()) {
                if (i < foodSlotStart || i > foodSlotEnd) {
                    int foodSlot = -1;
                    for (int j = foodSlotStart; j <= foodSlotEnd; j++) {
                        ItemStack foodStack = inventory().getItem(j);
                        if (inventory().getItem(j).isEmpty() || (ItemStack.isSameItemSameTags(stack, foodStack) && foodStack.getCount() + stack.getCount() <= foodStack.getMaxStackSize())) {
                            foodSlot = j;
                            break;
                        }
                    }

                    if (foodSlot == -1) {
                        foodSlot = findSlotMatching(s -> isMergeable(stack, s), false);
                    }

                    if (foodSlot != -1) {
                        swapOrMerge(stack, i, inventory().getItem(foodSlot), foodSlot);
                    }
                }
            } else if (stack.getItem() instanceof ArmorItem armorItem) {
                int slotToSwitch = switch (armorItem.getEquipmentSlot()) {
                    case HEAD -> 39;
                    case CHEST -> 38;
                    case LEGS -> 37;
                    case FEET -> 36;
                    default -> -1;
                };

                if (slotToSwitch != -1) {
                    ItemStack armorStack = inventory().getItem(slotToSwitch);
                    if (armorStack.isEmpty() || armorStack.getItem() instanceof ArmorItem currentArmor && currentArmor.getDefense() < armorItem.getDefense()) {
                        swapSlots2(i, slotToSwitch);
                    }
                }
            } else if (!stack.isEmpty()) {
                int emptySlot = findSlotMatching(s -> isMergeable(stack, s), true);
                if (emptySlot != -1) {
                    swapOrMerge(inventory().getItem(i), i, inventory().getItem(emptySlot), emptySlot);
                }
            }
        }
    }

    public static void swapOrMerge(ItemStack stackFrom, int slotFrom, ItemStack stackInto, int slotInto) {
        if (!stackInto.isEmpty() && !ItemStack.isSameItemSameTags(stackFrom, stackInto)) {
            swapSlots2(slotFrom, slotInto);
        } else if (isMergeable(stackFrom, stackInto)) {
            mergeSlots(slotInto, slotFrom);
        } else {
            System.out.printf("swapOrMerge: failed to %s -> %s%n", slotFrom, slotInto);
        }
    }

    public static boolean isMergeable(ItemStack stackFrom, ItemStack stackInto) {
        return stackInto.isEmpty() || (ItemStack.isSameItemSameTags(stackFrom, stackInto) && stackInto.getCount() + stackFrom.getCount() <= stackInto.getMaxStackSize());
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

    private static LocalPlayer player() {
        return Minecraft.getInstance().player;
    }

    private static Inventory inventory() {
        return player().getInventory();
    }
}
