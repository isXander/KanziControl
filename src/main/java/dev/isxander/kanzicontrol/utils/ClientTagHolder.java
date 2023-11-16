package dev.isxander.kanzicontrol.utils;

import net.minecraft.world.entity.player.Player;

import java.util.Set;

public interface ClientTagHolder {
    Set<String> bonobo$getClientTags();

    static Set<String> getClientTags(Player player) {
        return ((ClientTagHolder) player).bonobo$getClientTags();
    }
}
