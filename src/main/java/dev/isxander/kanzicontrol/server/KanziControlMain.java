package dev.isxander.kanzicontrol.server;

import dev.isxander.kanzicontrol.server.commands.ClientTagCommand;
import dev.isxander.kanzicontrol.server.commands.DragonFightCommand;
import dev.isxander.kanzicontrol.server.commands.IndicateCommand;
import dev.isxander.kanzicontrol.server.commands.SortInventoryCommand;
import dev.isxander.kanzicontrol.utils.PublicMobEffect;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KanziControlMain implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("KanziControl");

    public static final EntityType<EndCrystal> END_CRYSTAL_SML_HITBOX = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation("kanzicontrol", "end_crystal_small_hitbox"),
            FabricEntityTypeBuilder.<EndCrystal>create(MobCategory.MISC, EndCrystal::new)
                    .dimensions(new EntityDimensions(2.0f, 2.0f, true))
                    .trackRangeChunks(16)
                    .trackedUpdateRate(Integer.MAX_VALUE)
                    .build()
    );
    public static final MobEffect DRAGON_BREATH_EFFECT = Registry.register(
            BuiltInRegistries.MOB_EFFECT,
            new ResourceLocation("kanzicontrol", "dragons_breath"),
            new PublicMobEffect(MobEffectCategory.NEUTRAL, 0xFF00FF)
    );

    @Override
    public void onInitialize() {
        KanziHandshake.setupOnServer();

        CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) -> {
            IndicateCommand.registerCommand(dispatcher);
            SortInventoryCommand.registerCommand(dispatcher);
            ClientTagCommand.registerCommand(dispatcher);
            DragonFightCommand.registerCommand(dispatcher);
        });
    }
}
