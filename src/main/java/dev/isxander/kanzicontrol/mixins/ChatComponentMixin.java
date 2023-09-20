package dev.isxander.kanzicontrol.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.isxander.kanzicontrol.config.KanziConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
    @Shadow @Final private Minecraft minecraft;

    @ModifyReturnValue(method = "isChatHidden", at = @At("RETURN"))
    private boolean modifyChatHidden(boolean original) {
        // don't distract Kanzi with chat commands
        return original || (!(minecraft.screen instanceof ChatScreen) && KanziConfig.INSTANCE.instance().enabled);
    }
}
