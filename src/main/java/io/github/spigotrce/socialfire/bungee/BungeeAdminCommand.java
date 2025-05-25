package io.github.spigotrce.socialfire.bungee;

import io.github.spigotrce.socialfire.common.model.LinkModel;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.IOException;

public class BungeeAdminCommand extends Command {
    public BungeeAdminCommand() {
        super("socialfire", "socialfire.admin");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /socialfire <subcommand>");
            return;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "reload": {
                sender.sendMessage("Reloading SocialFire...");
                try {
                    BungeeFire.CONFIG.reload();
                } catch (IOException e) {
                    sender.sendMessage("Error reloading config: " + e.getMessage());
                    BungeeFire.LOGGER.severe("Error reloading config: " + e.getMessage());
                    e.printStackTrace();
                }
                break;
            }
            case "announce":
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /socialfire announce <name>");
                    return;
                }
                String name = args[1];
                LinkModel link = BungeeFire.CONFIG.getLinks().get(name);
                for (ProxiedPlayer player : BungeeFire.PROXY_SERVER.getPlayers()) {
                    BungeeFire.ANNOUNCEMENT_MANAGER.sendAnnouncement(player, link);
                }
                break;
            default:
                sender.sendMessage("§cUnknown subcommand. Use /socialfire reload or /socialfire annonce <name>");
                break;
        }
    }
}
