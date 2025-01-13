package io.github.spigotrce.socialfire.velocity.command.singlecommand.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import io.github.spigotrce.socialfire.velocity.SocialFire;
import io.github.spigotrce.socialfire.velocity.command.AbstractBrigadierCommand;
import net.kyori.adventure.text.Component;

import java.io.IOException;

public class ReloadCommand extends AbstractBrigadierCommand {
    public ReloadCommand() {
        super(false, "reload");
    }

    @Override
    public BrigadierCommand build() {
        return new BrigadierCommand(LiteralArgumentBuilder.<CommandSource>literal(super.commandName)
                .executes(commandContext -> {
                    commandContext.getSource().sendMessage(Component.text("Reloading SocialFire..."));
                    try {
                        SocialFire.CONFIG.reload();
                    } catch (IOException e) {
                        commandContext.getSource().sendMessage(Component.text("Error reloading config: "
                                + e.getMessage()));
                        SocialFire.LOGGER.error("Error reloading config", e);
                    }
                    return SINGLE_SUCCESS;
                })
        );
    }
}
