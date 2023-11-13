package dev.isxander.kanzicontrol.interactionarea.button;

import dev.isxander.kanzicontrol.TouchInput;
import dev.isxander.kanzicontrol.entityhandler.tasks.AutoEatTask;
import dev.isxander.kanzicontrol.entityhandler.tasks.ShareTaskClickHandler;
import dev.isxander.kanzicontrol.entityhandler.tasks.WaterBucketClutchClickHandler;
import dev.isxander.kanzicontrol.interactionarea.RootInteractionArea;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.HashMap;
import java.util.Map;

public class ButtonActions {
    public static final Map<String, ButtonAction> ACTIONS = new HashMap<>();

    private static final Minecraft minecraft = Minecraft.getInstance();
    public static final ButtonAction
            NONE = action("none", ButtonAction.none()),
            JUMP = action("jump", ButtonAction.down(TouchInput.INSTANCE::jump).withNarration("jump").cooldown(1000)),
            FIGHT = action("fight", ButtonAction.down(TouchInput.INSTANCE::attack).withNarration("fight").cooldown(1000)),
            BREAK = action("break", ButtonAction.down(TouchInput.INSTANCE::tryStartMining).withNarration("break").cooldown(1000)),
            USE = action("use", ButtonAction.down(TouchInput.INSTANCE::toggleUseItem).withNarration("use").cooldown(1000)),
            TOGGLE_SWIM_DOWN = action("swim_down", ButtonAction.down(TouchInput.INSTANCE::toggleSwimDown).withNarration(() -> TouchInput.INSTANCE.swimDown ? "sink" : "swim up").cooldown(1000)),
            EAT = action("eat", ButtonAction.down(() -> RootInteractionArea.getInstance().TOUCH_ENTITY.queueHandler(new AutoEatTask())).withNarration("eat").cooldown(1000)),
            SHARE = action("share", ButtonAction.down(() -> {
                LocalPlayer player = Minecraft.getInstance().player;
                RootInteractionArea.getInstance().TOUCH_ENTITY.queueHandler(new ShareTaskClickHandler(player.level().getNearestPlayer(player.getX(), player.getY(), player.getZ(), 3, entity -> !entity.isSpectator() && !entity.equals(player))));
            }).withNarration("share")),
            WATER_BUCKET_CLUTCH = action("clutch", ButtonAction.down(() -> RootInteractionArea.getInstance().TOUCH_ENTITY.queueHandler(new WaterBucketClutchClickHandler())).withNarration("clutch")),
            HOTBAR_SLOT_1 = action("hotbar_slot_1", ButtonAction.down(() -> minecraft.player.getInventory().selected = 0)),
            HOTBAR_SLOT_2 = action("hotbar_slot_2", ButtonAction.down(() -> minecraft.player.getInventory().selected = 1)),
            HOTBAR_SLOT_3 = action("hotbar_slot_3", ButtonAction.down(() -> minecraft.player.getInventory().selected = 2)),
            HOTBAR_SLOT_4 = action("hotbar_slot_4", ButtonAction.down(() -> minecraft.player.getInventory().selected = 3)),
            HOTBAR_SLOT_5 = action("hotbar_slot_5", ButtonAction.down(() -> minecraft.player.getInventory().selected = 4)),
            HOTBAR_SLOT_6 = action("hotbar_slot_6", ButtonAction.down(() -> minecraft.player.getInventory().selected = 5)),
            HOTBAR_SLOT_7 = action("hotbar_slot_7", ButtonAction.down(() -> minecraft.player.getInventory().selected = 6)),
            HOTBAR_SLOT_8 = action("hotbar_slot_8", ButtonAction.down(() -> minecraft.player.getInventory().selected = 7)),
            HOTBAR_SLOT_9 = action("hotbar_slot_9", ButtonAction.down(() -> minecraft.player.getInventory().selected = 8));

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
