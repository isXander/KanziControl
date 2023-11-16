package dev.isxander.kanzicontrol.mixins;

import dev.isxander.kanzicontrol.utils.ClientTagHolder;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashSet;
import java.util.Set;

@Mixin(Player.class)
public class PlayerMixin implements ClientTagHolder {
    @Unique
    private final Set<String> clientTags = new HashSet<>();

    @Override
    public Set<String> bonobo$getClientTags() {
        return clientTags;
    }
}
