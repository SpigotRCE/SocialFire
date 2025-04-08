package io.github.spigotrce.socialfire.velocity.command.singlecommand.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import io.github.spigotrce.socialfire.velocity.VelocityFire;
import io.github.spigotrce.socialfire.velocity.command.AbstractBrigadierCommand;
import net.kyori.adventure.text.Component;

public class AnnounceCommand extends AbstractBrigadierCommand {
    public AnnounceCommand() {
        super(false, "announce");
    }

    @Override
    public BrigadierCommand build() {
        return new BrigadierCommand(LiteralArgumentBuilder.<CommandSource>literal(super.commandName)
                .executes(commandContext -> {
                    commandContext.getSource().sendMessage(Component.text("Incomplete command!"));
                    return SINGLE_SUCCESS;
                }).then(RequiredArgumentBuilder.<CommandSource, String>argument("name", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            String partialType;

                            try {
                                partialType = ctx.getArgument("name", String.class).toLowerCase();
                            } catch (IllegalArgumentException ignored) {
                                partialType = "";
                            }

                            if (partialType.isEmpty()) {
                                VelocityFire.CONFIG.getLinks().keySet().forEach(builder::suggest);
                                return builder.buildFuture();
                            }

                            String finalPartialType = partialType;

                            VelocityFire.CONFIG.getLinks().keySet().stream().filter(name -> name.toLowerCase().startsWith(finalPartialType)).forEach(builder::suggest);

                            return builder.buildFuture();
                        })

                        .executes(commandContext -> {
                            String name = commandContext.getArgument("name", String.class);

                            for (Player player : VelocityFire.PROXY_SERVER.getAllPlayers()) {
                                VelocityFire.ANNOUNCEMENT_MANAGER.sendAnnouncement(player, VelocityFire.CONFIG.getLinks().get(name));
                            }

                            return SINGLE_SUCCESS;
                        })
                )
        );
    }
}
