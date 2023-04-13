package dev.isxander.kanzicontrol.interactionarea.button;

import dev.isxander.kanzicontrol.TouchInput;
import net.minecraft.client.Minecraft;

import java.util.HashMap;
import java.util.Map;

public class ButtonActions {
    public static final Map<String, ButtonAction> ACTIONS = new HashMap<>();

    public static final ButtonAction
            NONE = action("none", ButtonAction.none()),
            JUMP = action("jump", ButtonAction.down(TouchInput.INSTANCE::jump).withNarration("jump")),
            FIGHT = action("fight", ButtonAction.down(TouchInput.INSTANCE::attack).withNarration("fight")),
            BREAK = action("break", ButtonAction.down(TouchInput.INSTANCE::toggleMining).withNarration("break")),
            USE = action("use", ButtonAction.down(TouchInput.INSTANCE::toggleUseItem).withNarration("use")),
            TOGGLE_SWIM_DOWN = action("swim_down", ButtonAction.down(TouchInput.INSTANCE::toggleSwimDown).withNarration(() -> TouchInput.INSTANCE.swimDown ? "sink" : "swim up")),
            HOTBAR_SLOT_1 = action("hotbar_slot_1", ButtonAction.down(() -> Minecraft.getInstance().player.getInventory().selected = 0)),
            HOTBAR_SLOT_2 = action("hotbar_slot_2", ButtonAction.down(() -> Minecraft.getInstance().player.getInventory().selected = 1)),
            HOTBAR_SLOT_3 = action("hotbar_slot_3", ButtonAction.down(() -> Minecraft.getInstance().player.getInventory().selected = 2)),
            HOTBAR_SLOT_4 = action("hotbar_slot_4", ButtonAction.down(() -> Minecraft.getInstance().player.getInventory().selected = 3)),
            HOTBAR_SLOT_5 = action("hotbar_slot_5", ButtonAction.down(() -> Minecraft.getInstance().player.getInventory().selected = 4)),
            HOTBAR_SLOT_6 = action("hotbar_slot_6", ButtonAction.down(() -> Minecraft.getInstance().player.getInventory().selected = 5)),
            HOTBAR_SLOT_7 = action("hotbar_slot_7", ButtonAction.down(() -> Minecraft.getInstance().player.getInventory().selected = 6)),
            HOTBAR_SLOT_8 = action("hotbar_slot_8", ButtonAction.down(() -> Minecraft.getInstance().player.getInventory().selected = 7)),
            HOTBAR_SLOT_9 = action("hotbar_slot_9", ButtonAction.down(() -> Minecraft.getInstance().player.getInventory().selected = 8));

    public static ButtonAction command(String command) {
        if (command.startsWith("/"))
            command = command.substring(1);
        String finalCommand = command;
        return ButtonAction.down(() -> Minecraft.getInstance().player.connection.sendCommand(finalCommand));
    }

    private static ButtonAction action(String actionId, ButtonAction action) {
        ACTIONS.put(actionId, action);
        return action;
    }

    private static void say(String message) {
        Minecraft.getInstance().getNarrator().sayNow(message);
    }
}
