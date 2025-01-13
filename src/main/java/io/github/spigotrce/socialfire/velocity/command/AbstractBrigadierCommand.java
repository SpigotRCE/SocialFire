package io.github.spigotrce.socialfire.velocity.command;

import com.mojang.brigadier.Command;
import com.velocitypowered.api.command.BrigadierCommand;
import io.github.spigotrce.socialfire.velocity.SocialFire;

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
        SocialFire.PROXY_SERVER.getCommandManager().register(
                SocialFire.PROXY_SERVER.getCommandManager().metaBuilder(commandName)
                        .aliases(commandAliases).plugin(SocialFire.INSTANCE).build(),
                this.build()
        );
        SocialFire.LOGGER.info("Registered command {}", commandName);
    }

    public abstract BrigadierCommand build();
}
