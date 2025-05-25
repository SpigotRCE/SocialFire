package io.github.spigotrce.socialfire.bungee;

import io.github.spigotrce.socialfire.common.Constants;
import io.github.spigotrce.socialfire.common.config.Config;
import io.github.spigotrce.socialfire.common.model.LinkModel;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.*;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;
import org.bstats.bungeecord.Metrics;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BungeeFire extends Plugin implements Listener {
    public static BungeeFire INSTANCE;

    public static Logger LOGGER;
    public static File DATA_DIRECTORY;
    public static ProxyServer PROXY_SERVER;
    public static String VERSION;

    public static BungeeAnnouncementsManager ANNOUNCEMENT_MANAGER;

    public static Config<ScheduledTask, ProxiedPlayer> CONFIG;

    public static final String CHANNEL_NAME = Constants.CHANNEL;

    @Override
    public void onEnable() {
        INSTANCE = this;
        LOGGER = getLogger();
        DATA_DIRECTORY = getDataFolder();
        PROXY_SERVER = ProxyServer.getInstance();
        VERSION = getDescription().getVersion();
        LOGGER.info("SocialFire version " + VERSION + " is starting...");
        ANNOUNCEMENT_MANAGER = new BungeeAnnouncementsManager();
        LOGGER.info("Initializing config...");
        CONFIG = new Config<>(DATA_DIRECTORY.toPath(), ANNOUNCEMENT_MANAGER);
        try {
            CONFIG.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CONFIG.updateLinks();

        LOGGER.info("Initializing channel...");
        PROXY_SERVER.registerChannel(CHANNEL_NAME);

        LOGGER.info("Registering listeners...");
        PROXY_SERVER.getPluginManager().registerListener(INSTANCE, this);

        LOGGER.info("Initializing commands...");
        PROXY_SERVER.getPluginManager().registerCommand(INSTANCE, new BungeeAdminCommand());

        LOGGER.info("Initializing bstats...");
        new Metrics(this, 25407);

        LOGGER.info("SocialFire successfully initialized!");
        LOGGER.info("Thanks for using SocialFire!");
        LOGGER.info("Developed by https://github.com/SpigotRCE/");
    }

    @Override
    public void onDisable() {
        LOGGER.info("Stopping SocialFire...");
        LOGGER.info("SocialFire stopped successfully!");
        LOGGER.info("Bye!");
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getTag().equals(CHANNEL_NAME)) return;
        event.setCancelled(true);
    }

    public void reloadCommands() {
        PluginManager pm = INSTANCE.getProxy().getPluginManager();

        for (Map.Entry<String, LinkModel> entry : BungeeFire.CONFIG.getLinks().entrySet()) {
            String label = entry.getKey();

            if (pm.getCommands().stream().anyMatch(cmd -> cmd.getValue().getName().equalsIgnoreCase(label)))
                continue;

            pm.registerCommand(INSTANCE, new CallbackCommand(label));
        }

        for (ProxiedPlayer player : INSTANCE.getProxy().getPlayers()) {
            if (player.getServer() == null) continue;

            player.getServer().sendData(CHANNEL_NAME, buildPluginMessage("reload"));
        }
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        String partial = event.getCursor().replaceFirst("/", "");

        for (String label : BungeeFire.CONFIG.getLinks().keySet()) {
            if (label.startsWith(partial)) {
                event.getSuggestions().add("/" + label);
            }
        }
    }

    private byte[] buildPluginMessage(String action) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream out = new DataOutputStream(baos)) {

            out.writeUTF(BungeeFire.VERSION);
            out.writeUTF(action);
            return baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static class CallbackCommand extends Command implements TabExecutor {
        public CallbackCommand(String name, String... aliases) {
            super(name, null, aliases);
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if (!(sender instanceof ProxiedPlayer player)) {
                sender.sendMessage(new TextComponent("Only players can execute this command!"));
                return;
            }

            LinkModel model = BungeeFire.CONFIG.getLinks().get(getName());
            BungeeFire.ANNOUNCEMENT_MANAGER.sendAnnouncement(player, model);
        }

        @Override
        public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
            if (args.length == 1) {
                return BungeeFire.CONFIG.getLinks().keySet().stream()
                        .filter(k -> k.startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        }
    }
}
