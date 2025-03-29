package io.github.spigotrce.socialfire.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.command.PlayerAvailableCommandsEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.TabCompleteEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import io.github.spigotrce.socialfire.common.Constants;
import io.github.spigotrce.socialfire.velocity.command.singlecommand.SingleCommandManager;
import io.github.spigotrce.socialfire.common.config.Config;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

@Plugin(
        id = "socialfire",
        name = "SocialFire",
        version = "1.3",
        authors = "SpigotRCE"
)
public class VelocityFire {
    public static VelocityFire INSTANCE;

    @Inject
    public static Logger LOGGER;
    @Inject
    @DataDirectory
    public static Path DATA_DIRECTORY;
    @Inject
    public static ProxyServer PROXY_SERVER;

    public static Config CONFIG;

    public static VelocityAnnouncementsManager ANNOUNCEMENT_MANAGER;


    public static SingleCommandManager SINGLE_COMMAND_MANAGER;

    public static ChannelIdentifier CHANNEL_NAME;

    public static String VERSION;

    @Inject
    public VelocityFire(Logger logger, @DataDirectory Path dataDirectory, ProxyServer proxyServer) {
        INSTANCE = this;
        LOGGER = logger;
        DATA_DIRECTORY = dataDirectory;
        PROXY_SERVER = proxyServer;
        CONFIG = new Config(DATA_DIRECTORY);
        ANNOUNCEMENT_MANAGER = new VelocityAnnouncementsManager();
        CHANNEL_NAME = MinecraftChannelIdentifier.from(Constants.CHANNEL);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        LOGGER.info("Starting SocialFire...");
        VERSION = PROXY_SERVER.getPluginManager().getPlugin("socialfire").get().getDescription().getVersion().get();
        LOGGER.info("Initializing config...");
        try {
            CONFIG.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CONFIG.updateLinks();

        LOGGER.info("Initializing channel...");
        PROXY_SERVER.getChannelRegistrar().register(CHANNEL_NAME);

        LOGGER.info("Initializing commands...");
        SINGLE_COMMAND_MANAGER = new SingleCommandManager();

        LOGGER.info("SocialFire successfully initialized!");
        LOGGER.info("Thanks for using SocialFire!");
        LOGGER.info("Developed by https://github.com/SpigotRCE/");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        LOGGER.info("Stopping SocialFire...");
        LOGGER.info("SocialFire stopped successfully!");
        LOGGER.info("Bye!");
    }

    public void reloadCommands() {
        PROXY_SERVER.getCommandManager().unregister("socialfire:callback");
        PROXY_SERVER.getCommandManager().register(
                PROXY_SERVER.getCommandManager()
                        .metaBuilder("socialfire:callback")
                        .aliases(
                                new ArrayList<>(
                                        CONFIG.getLinks().keySet()
                                ).toArray(
                                        new String[0]
                                )
                        )
                        .build(),
                new DummyCommand()
        );
    }

    @Subscribe
    public void onTabComplete(TabCompleteEvent event) {
        if (!event.getPartialMessage().startsWith("/"))
            return;
        String partialMessage = event.getPartialMessage().substring(1);
        CONFIG.getLinks().keySet().forEach(label -> {
            if (label.startsWith(partialMessage) || partialMessage.split(" ").length == 0)
                event.getSuggestions().add("/" + label);
        });
    }

    @Subscribe
    public void onCommand(CommandExecuteEvent event) {
        CONFIG.getLinks().keySet().forEach(label -> {
            if (label.equals(event.getCommand())) {
                event.setResult(CommandExecuteEvent.CommandResult.denied());
                if (event.getCommandSource() instanceof Player player)
                    ANNOUNCEMENT_MANAGER.sendAnnouncement(player, CONFIG.getLinks().get(label));
                else
                    event.getCommandSource().sendMessage(Component.text("Only players can execute this command!"));
            }
        });
    }

    @Subscribe
    public void onAvailableCommands(PlayerAvailableCommandsEvent event) {
        event.getRootNode().removeChildByName("socialfire:callback");
    }

    private static class DummyCommand implements SimpleCommand {
        @Override
        public void execute(Invocation invocation) {
        }
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!Objects.equals(event.getIdentifier(), CHANNEL_NAME)) return;
        event.setResult(PluginMessageEvent.ForwardResult.handled());
    }
}
