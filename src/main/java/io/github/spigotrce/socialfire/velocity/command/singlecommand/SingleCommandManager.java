package io.github.spigotrce.socialfire.velocity.command.singlecommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import io.github.spigotrce.socialfire.velocity.command.AbstractBrigadierCommand;
import io.github.spigotrce.socialfire.velocity.command.singlecommand.impl.AnnouncementCommand;
import io.github.spigotrce.socialfire.velocity.command.singlecommand.impl.ReloadCommand;

public class SingleCommandManager extends AbstractBrigadierCommand {
    LiteralArgumentBuilder<CommandSource> command;

    public SingleCommandManager() {
        super(false, "socialfire");
        command = LiteralArgumentBuilder.literal(super.commandName);
        command.requires(invoker -> invoker.hasPermission("socialfire.admin"));
        registerCommand(new ReloadCommand());
        registerCommand(new AnnouncementCommand());
        super.register();
    }

    public void registerCommand(AbstractBrigadierCommand singleCommand) {
        command.then(singleCommand.build().getNode());
    }

    @Override
    public BrigadierCommand build() {
        return new BrigadierCommand(command.build());
    }
}
