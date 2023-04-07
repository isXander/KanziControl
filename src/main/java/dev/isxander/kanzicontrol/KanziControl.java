package dev.isxander.kanzicontrol;

import dev.isxander.kanzicontrol.config.KanziConfig;
import dev.isxander.kanzicontrol.interactionarea.InteractionAreaStorage;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class KanziControl implements ClientModInitializer {
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
