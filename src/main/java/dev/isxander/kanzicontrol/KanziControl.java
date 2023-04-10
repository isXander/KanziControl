package dev.isxander.kanzicontrol;

import dev.isxander.kanzicontrol.config.KanziConfig;
import dev.isxander.kanzicontrol.interactionarea.InteractionAreaStorage;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KanziControl implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("KanziControl");

    private static KanziControl instance;

    @Override
    public void onInitializeClient() {
        instance = this;

        KanziConfig.INSTANCE.load();

        ClientTickEvents.START_CLIENT_TICK.register(client -> InteractionAreaStorage.tick());
    }

    public static KanziControl get() {
        return instance;
    }
}
