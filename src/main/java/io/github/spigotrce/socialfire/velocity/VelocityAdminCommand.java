package io.github.spigotrce.socialfire.velocity;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.*;
import com.velocitypowered.api.command.*;
import net.kyori.adventure.text.Component;

import java.io.IOException;

public class VelocityAdminCommand {
  public final int SINGLE_SUCCESS = Command.SINGLE_SUCCESS;

  public VelocityAdminCommand() {
    VelocityFire.PROXY_SERVER.getCommandManager()
      .register(VelocityFire.PROXY_SERVER.getCommandManager()
        .metaBuilder("socialfire")
        .aliases("socialfire")
        .plugin(VelocityFire.INSTANCE)
        .build(), this.build());
  }

  public BrigadierCommand build() {
    return new BrigadierCommand(LiteralArgumentBuilder.<CommandSource>literal("socialfire")
      .requires(source -> source.hasPermission("socialfire.admin"))
      .then(LiteralArgumentBuilder.<CommandSource>literal("reload").executes(context -> {
        context.getSource().sendMessage(Component.text("Reloading SocialFire..."));
        try {
          VelocityFire.CONFIG.reload();
        } catch (IOException e) {
          context.getSource().sendMessage(Component.text("Error reloading config: " + e.getMessage()));
          VelocityFire.LOGGER.error("Error reloading config", e);
        }
        return SINGLE_SUCCESS;
      }))
      .then(LiteralArgumentBuilder.<CommandSource>literal("announce")
        .executes(context -> {
          context.getSource().sendMessage(Component.text("Incomplete command!"));
          return SINGLE_SUCCESS;
        })
        .then(RequiredArgumentBuilder.<CommandSource, String>argument("name", StringArgumentType.word())
          .suggests((ctx, builder) -> {
            String partial;

            try {
              partial = ctx.getArgument("name", String.class).toLowerCase();
            } catch (IllegalArgumentException ignored) {
              partial = "";
            }

            String finalPartial = partial;
            VelocityFire.CONFIG.getLinks()
              .keySet()
              .stream()
              .filter(name -> name.toLowerCase().startsWith(finalPartial))
              .forEach(builder::suggest);

            return builder.buildFuture();
          })
          .executes(context -> {
            String name = context.getArgument("name", String.class);
            VelocityFire.PROXY_SERVER.getAllPlayers()
              .forEach(player -> VelocityFire.ANNOUNCEMENT_MANAGER.sendAnnouncement(player,
                VelocityFire.CONFIG.getLinks().get(name)));
            return SINGLE_SUCCESS;
          }))));
  }
}
