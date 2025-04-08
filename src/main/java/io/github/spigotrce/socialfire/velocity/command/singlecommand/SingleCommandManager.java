package io.github.spigotrce.socialfire.velocity.command.singlecommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import io.github.spigotrce.socialfire.velocity.VelocityFire;
import io.github.spigotrce.socialfire.velocity.command.AbstractBrigadierCommand;
import io.github.spigotrce.socialfire.velocity.command.singlecommand.impl.AnnounceCommand;
import io.github.spigotrce.socialfire.velocity.command.singlecommand.impl.ReloadCommand;

public class SingleCommandManager extends AbstractBrigadierCommand {
    LiteralArgumentBuilder<CommandSource> command;

    public SingleCommandManager() {
        super(false, "socialfire");
        command = LiteralArgumentBuilder.literal(super.commandName);
        command.requires(invoker -> invoker.hasPermission("socialfire.admin"));
        registerCommand(new ReloadCommand());
        registerCommand(new AnnounceCommand());
        super.register();
    }

    public void registerCommand(AbstractBrigadierCommand singleCommand) {
        VelocityFire.LOGGER.info("Registered command {} {}", super.commandName, singleCommand.commandName);
        command.then(singleCommand.build().getNode());
    }

    @Override
    public BrigadierCommand build() {
        return new BrigadierCommand(command.build());
    }
}
