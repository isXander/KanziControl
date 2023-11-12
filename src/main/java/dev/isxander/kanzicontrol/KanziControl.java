package dev.isxander.kanzicontrol;

import com.mojang.blaze3d.platform.InputConstants;
import dev.isxander.kanzicontrol.config.KanziConfig;
import dev.isxander.kanzicontrol.config.MasterModSwitch;
import dev.isxander.kanzicontrol.config.MasterModSwitchImpl;
import dev.isxander.kanzicontrol.indicator.IndicatorHandlerManager;
import dev.isxander.kanzicontrol.interactionarea.RootInteractionArea;
import dev.isxander.kanzicontrol.mixins.MouseHandlerAccessor;
import dev.isxander.kanzicontrol.server.KanziControlMain;
import dev.isxander.kanzicontrol.server.KanziHandshake;
import dev.isxander.kanzicontrol.server.ClientboundKanziIndicatorPacket;
import dev.isxander.kanzicontrol.server.ClientboundSortInventoryPacket;
import dev.isxander.kanzicontrol.utils.InventoryUtils;
import dev.isxander.yacl3.config.v2.impl.autogen.OptionFactoryRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KanziControl implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("KanziControl");

    private static KanziControl instance;

    private KeyMapping toggleKey;
    private KeyMapping indicateTouchUp, indicateTouchDown, indicateTouchLeft, indicateTouchRight;
    private KeyMapping moveCursorUp, moveCursorDown, moveCursorLeft, moveCursorRight;

    @Override
    public void onInitializeClient() {
        instance = this;

        OptionFactoryRegistry.registerOptionFactory(MasterModSwitch.class, new MasterModSwitchImpl());
        KanziConfig.INSTANCE.serializer().load();

        KanziHandshake.setupOnClient();

        ClientPlayNetworking.registerGlobalReceiver(ClientboundSortInventoryPacket.TYPE, (packet, player, sender) -> {
            Minecraft.getInstance().execute(InventoryUtils::sortInventory);
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (KanziConfig.INSTANCE.instance().enabled)
                RootInteractionArea.tick();

            while (toggleKey.consumeClick()) {
                boolean nowEnabled = KanziConfig.INSTANCE.instance().enabled = !KanziConfig.INSTANCE.instance().enabled;

                ((MouseHandlerAccessor) client.mouseHandler).setMouseGrabbed(false);
                client.mouseHandler.grabMouse(); // the mixin will handle proper mouse grabbing

                TouchInput.INSTANCE.setEnabled(nowEnabled, client.player);
            }

            while (indicateTouchUp.consumeClick()) {
                RootInteractionArea.getInstance().TOUCH_LOOK.indicateUp(100);
            }
            while (indicateTouchDown.consumeClick()) {
                RootInteractionArea.getInstance().TOUCH_LOOK.indicateDown(100);
            }
            while (indicateTouchLeft.consumeClick()) {
                RootInteractionArea.getInstance().TOUCH_LOOK.indicateLeft(100);
            }
            while (indicateTouchRight.consumeClick()) {
                RootInteractionArea.getInstance().TOUCH_LOOK.indicateRight(100);
            }

            int x = 0, y = 0;
            if (moveCursorUp.isDown()) y--;
            if (moveCursorDown.isDown()) y++;
            if (moveCursorLeft.isDown()) x--;
            if (moveCursorRight.isDown()) x++;
            if (x != 0 || y != 0) {
                RootInteractionArea.getInstance().CURSOR_DISPLAY.moveCursor(x * 10, y * 10);
            }
        });

        KeyBindingHelper.registerKeyBinding(toggleKey = new KeyMapping("Toggle Mod Completely", InputConstants.KEY_P, "Bonobocraft"));
        KeyBindingHelper.registerKeyBinding(indicateTouchUp = new KeyMapping("Indicate Up", -1, "Bonobocraft"));
        KeyBindingHelper.registerKeyBinding(indicateTouchDown = new KeyMapping("Indicate Down", -1, "Bonobocraft"));
        KeyBindingHelper.registerKeyBinding(indicateTouchLeft = new KeyMapping("Indicate Left", -1, "Bonobocraft"));
        KeyBindingHelper.registerKeyBinding(indicateTouchRight = new KeyMapping("Indicate Right", -1, "Bonobocraft"));
        KeyBindingHelper.registerKeyBinding(moveCursorUp = new KeyMapping("Cursor Up", InputConstants.KEY_UP, "Bonobocraft"));
        KeyBindingHelper.registerKeyBinding(moveCursorDown = new KeyMapping("Cursor Down", InputConstants.KEY_DOWN, "Bonobocraft"));
        KeyBindingHelper.registerKeyBinding(moveCursorLeft = new KeyMapping("Cursor Left", InputConstants.KEY_LEFT, "Bonobocraft"));
        KeyBindingHelper.registerKeyBinding(moveCursorRight = new KeyMapping("Cursor Right", InputConstants.KEY_RIGHT, "Bonobocraft"));

        ClientPlayNetworking.registerGlobalReceiver(ClientboundKanziIndicatorPacket.TYPE, (packet, player, sender) -> {
            IndicatorHandlerManager.handleIndicator(packet.indicatorType(), packet.durationTicks());
        });
        EntityRendererRegistry.register(KanziControlMain.END_CRYSTAL_SML_HITBOX, EndCrystalRenderer::new);
    }

    public static KanziControl get() {
        return instance;
    }
}
