package dev.isxander.kanzicontrol.config;

import dev.isxander.yacl.config.ConfigInstance;
import dev.isxander.yacl.config.GsonConfigInstance;
import net.fabricmc.loader.api.FabricLoader;

public class KanziConfig {
    public static final ConfigInstance<KanziConfig> INSTANCE =
            GsonConfigInstance.createBuilder(KanziConfig.class)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("kanzicontrol.json"))
                    .build();

    public float touchLookDeadRadius = 0.125f;
    public float touchLookDegreesPerSecond = 90f;
}
