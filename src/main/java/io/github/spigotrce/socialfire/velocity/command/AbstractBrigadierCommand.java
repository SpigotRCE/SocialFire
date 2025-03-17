package io.github.spigotrce.socialfire.velocity.command;

import com.mojang.brigadier.Command;
import com.velocitypowered.api.command.BrigadierCommand;
import io.github.spigotrce.socialfire.velocity.VelocityFire;

public abstract class AbstractBrigadierCommand {
    public final String commandName;
    public final String[] commandAliases;
    public final int SINGLE_SUCCESS = Command.SINGLE_SUCCESS;

    public AbstractBrigadierCommand(String commandName, String... commandAliases) {
        this(true, commandName, commandAliases);
    }

    public AbstractBrigadierCommand(boolean autoRegister, String commandName, String... commandAliases) {
        this.commandName = commandName;
        this.commandAliases = commandAliases;
        if (!autoRegister) return;
        register();
    }

    public void register() {
        VelocityFire.PROXY_SERVER.getCommandManager().register(
                VelocityFire.PROXY_SERVER.getCommandManager().metaBuilder(commandName)
                        .aliases(commandAliases).plugin(VelocityFire.INSTANCE).build(),
                this.build()
        );
        VelocityFire.LOGGER.info("Registered command {}", commandName);
    }

    public abstract BrigadierCommand build();
}
