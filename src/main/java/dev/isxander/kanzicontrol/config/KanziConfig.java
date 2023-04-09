package dev.isxander.kanzicontrol.config;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl.config.ConfigEntry;
import dev.isxander.yacl.config.ConfigInstance;
import dev.isxander.yacl.config.GsonConfigInstance;
import net.fabricmc.loader.api.FabricLoader;

import java.awt.*;

public class KanziConfig {
    public static final ConfigInstance<KanziConfig> INSTANCE =
            GsonConfigInstance.createBuilder(KanziConfig.class)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("kanzicontrol.json"))
                    .overrideGsonBuilder(new GsonBuilder().setPrettyPrinting())
                    .build();

    @ConfigEntry public int touchLookDegreesPerTap = 30;
    @ConfigEntry public int touchLookDegreesPerSecond = 90;

    @ConfigEntry public float touchForwardRadius = 0.33f;
    @ConfigEntry public float walkForwardDuration = 1f;
    @ConfigEntry public int forwardOverlayColor = 0x50FFFFFF;

    @ConfigEntry public float blockOutlineWidth = 5f;
}
