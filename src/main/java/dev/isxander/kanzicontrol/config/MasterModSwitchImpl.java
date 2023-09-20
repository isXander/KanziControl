package dev.isxander.kanzicontrol.config;

import dev.isxander.kanzicontrol.TouchInput;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionFlag;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.OptionAccess;
import dev.isxander.yacl3.config.v2.api.autogen.SimpleOptionFactory;

import java.util.Set;

public class MasterModSwitchImpl extends SimpleOptionFactory<MasterModSwitch, Boolean> {
    @Override
    protected ControllerBuilder<Boolean> createController(MasterModSwitch annotation, ConfigField<Boolean> field, OptionAccess storage, Option<Boolean> option) {
        return TickBoxControllerBuilder.create(option);
    }

    @Override
    protected Set<OptionFlag> flags(MasterModSwitch annotation, ConfigField<Boolean> field, OptionAccess storage) {
        return Set.of(client -> {
            if (client.player != null) {
                TouchInput.INSTANCE.setEnabled(KanziConfig.INSTANCE.instance().enabled, client.player);
            }
        });
    }
}
