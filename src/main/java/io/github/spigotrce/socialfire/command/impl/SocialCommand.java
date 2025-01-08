package io.github.spigotrce.socialfire.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import io.github.spigotrce.socialfire.command.AbstractBrigadierCommand;
import io.github.spigotrce.socialfire.model.LinkModel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

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
                            commandContext.getSource().sendMessage(
                                    Component.text(model.message)
                                            .clickEvent(
                                                    ClickEvent.clickEvent(
                                                            ClickEvent.Action.OPEN_URL,
                                                            model.link
                                                    )
                                            )
                            );
                            commandContext.getSource().sendActionBar(
                                    Component.text(model.actionBar)
                            );

                            // TODO: sounds

                            return SINGLE_SUCCESS;
                        }
                ).build()
        );
    }
}
