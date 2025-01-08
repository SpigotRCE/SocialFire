package io.github.spigotrce.socialfire;


import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import io.github.spigotrce.socialfire.command.impl.SocialCommand;
import io.github.spigotrce.socialfire.command.singlecommand.SingleCommandManager;
import io.github.spigotrce.socialfire.config.Config;
import io.github.spigotrce.socialfire.model.AnnouncementsManager;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Plugin(
        id = "socialfire",
        name = "SocialFire",
        version = "1.0",
        authors = "SpigotRCE"
)
public class SocialFire {
    public static SocialFire INSTANCE;

    @Inject
    public static Logger LOGGER;
    @Inject
    @DataDirectory
    public static Path DATA_DIRECTORY;
    @Inject
    public static ProxyServer PROXY_SERVER;

    public static Config CONFIG;

    public static AnnouncementsManager ANNOUNCEMENT_MANAGER;

    public static List<ScheduledTask> TASKS;

    public static SingleCommandManager SINGLE_COMMAND_MANAGER;

    @Inject
    public SocialFire(Logger logger, @DataDirectory Path dataDirectory, ProxyServer proxyServer) {
        INSTANCE = this;
        LOGGER = logger;
        DATA_DIRECTORY = dataDirectory;
        PROXY_SERVER = proxyServer;
        CONFIG = new Config(DATA_DIRECTORY);
        ANNOUNCEMENT_MANAGER = new AnnouncementsManager();
        TASKS = new ArrayList<>();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        LOGGER.info("Starting SocialFire...");
        LOGGER.info("Initializing config...");
        try {
            CONFIG.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CONFIG.updateLinks();

        LOGGER.info("Initializing commands...");
        CONFIG.getLinks().forEach(SocialCommand::new);
        SINGLE_COMMAND_MANAGER = new SingleCommandManager();

        LOGGER.info("SocialFire successfully initialized!");
        LOGGER.info("Thanks for using SocialFire!");
        LOGGER.info("Developed by https://github.com/SpigotRCE/");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        LOGGER.info("Stopping SocialFire...");
        TASKS.forEach(ScheduledTask::cancel);
        LOGGER.info("SocialFire stopped successfully!");
        LOGGER.info("Bye!");
    }
}
