package io.github.spigotrce.socialfire.command.singlecommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import io.github.spigotrce.socialfire.command.AbstractBrigadierCommand;
import io.github.spigotrce.socialfire.command.singlecommand.impl.ReloadCommand;

public class SingleCommandManager extends AbstractBrigadierCommand {
    LiteralArgumentBuilder<CommandSource> command;

    public SingleCommandManager() {
        super("SocialFire");
        command = LiteralArgumentBuilder.literal(super.commandName);
        command.requires(invoker -> invoker.hasPermission("SocialFire.admin"));
        registerCommand(new ReloadCommand());
    }

    public void registerCommand(AbstractBrigadierCommand singleCommand) {
        command.then(singleCommand.build().getNode());
    }

    @Override
    public BrigadierCommand build() {
        return new BrigadierCommand(command.build());
    }
}
