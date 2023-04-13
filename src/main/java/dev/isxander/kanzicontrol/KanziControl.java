package dev.isxander.kanzicontrol;

import com.mojang.blaze3d.platform.InputConstants;
import dev.isxander.kanzicontrol.config.KanziConfig;
import dev.isxander.kanzicontrol.interactionarea.InteractionAreaStorage;
import dev.isxander.kanzicontrol.mixins.MouseHandlerAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KanziControl implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("KanziControl");

    private static KanziControl instance;

    private KeyMapping toggleKey;

    @Override
    public void onInitializeClient() {
        instance = this;

        KanziConfig.INSTANCE.load();

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (KanziConfig.INSTANCE.getConfig().enabled)
                InteractionAreaStorage.tick();

            while (toggleKey.consumeClick()) {
                boolean nowEnabled = KanziConfig.INSTANCE.getConfig().enabled = !KanziConfig.INSTANCE.getConfig().enabled;

                ((MouseHandlerAccessor) client.mouseHandler).setMouseGrabbed(false);
                client.mouseHandler.grabMouse(); // the mixin will handle proper mouse grabbing

                TouchInput.INSTANCE.setEnabled(nowEnabled, client.player);
            }
        });

        KeyBindingHelper.registerKeyBinding(toggleKey = new KeyMapping("Toggle Mod", InputConstants.KEY_Y, "Bonobocraft"));
    }

    public static KanziControl get() {
        return instance;
    }
}
