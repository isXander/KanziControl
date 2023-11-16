package dev.isxander.kanzicontrol.entityhandler.tasks;

import dev.isxander.kanzicontrol.server.KanziControlMain;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

public class ShootCrossbowAtDragonHandler extends ShootCrossbowAtEntityHandler {
    public ShootCrossbowAtDragonHandler(EnderDragon entity, float heightAim) {
        super(entity, heightAim);
    }

    @Override
    public boolean shouldStart() {
        return !player.hasEffect(KanziControlMain.DRAGON_BREATH_EFFECT) && super.shouldStart();
    }
}
