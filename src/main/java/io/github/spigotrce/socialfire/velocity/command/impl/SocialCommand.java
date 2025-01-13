package io.github.spigotrce.socialfire.velocity.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import io.github.spigotrce.socialfire.velocity.command.AbstractBrigadierCommand;
import io.github.spigotrce.socialfire.velocity.model.LinkModel;
import io.github.spigotrce.socialfire.velocity.SocialFire;
import net.kyori.adventure.text.Component;

public class SocialCommand extends AbstractBrigadierCommand {
    private final LinkModel model;

    public SocialCommand(String command, LinkModel model) {
        super(command);
        this.model = model;
    }

    @Override
    public BrigadierCommand build() {
        return new BrigadierCommand(LiteralArgumentBuilder.<CommandSource>literal(super.commandName)
                .executes(commandContext -> {
                            if (!(commandContext.getSource() instanceof Player player)) {
                                commandContext.getSource().sendMessage(Component.text("Only players can use this command."));
                                return SINGLE_SUCCESS;
                            }

                            SocialFire.ANNOUNCEMENT_MANAGER.sendAnnouncement(player, model);

                            return SINGLE_SUCCESS;
                        }
                ).build()
        );
    }
}
