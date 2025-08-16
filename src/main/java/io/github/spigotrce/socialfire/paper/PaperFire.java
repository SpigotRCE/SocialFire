package io.github.spigotrce.socialfire.paper;

import com.google.common.io.*;
import io.github.spigotrce.socialfire.common.Constants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class PaperFire extends JavaPlugin implements PluginMessageListener {
  public static Logger LOGGER;

  @Override public void onDisable() {
    LOGGER.info("Stopping SocialFire...");
    getServer().getMessenger().unregisterIncomingPluginChannel(this, Constants.CHANNEL);
    LOGGER.info("SocialFire stopped successfully!");
    LOGGER.info("Bye!");
  }

  @Override public void onEnable() {
    LOGGER = getLogger();
    LOGGER.info("Starting SocialFire...");
    getServer().getMessenger().registerIncomingPluginChannel(this, Constants.CHANNEL, this);
    LOGGER.warning("SocialFire is running on paper(fork) server, all configuration is done only on the proxy side!");
    LOGGER.warning(
      "If you don't have all proxies with this plugin, remove this plugin from the paper side as it is exploitable!");
    LOGGER.info("SocialFire successfully initialized!");
    LOGGER.info("Thanks for using SocialFire!");
    LOGGER.info("Developed by https://github.com/SpigotRCE/");
  }

  @Override public void onPluginMessageReceived(@NotNull String s, @NotNull Player player, byte @NotNull [] bytes) {
    if (!s.equalsIgnoreCase(Constants.CHANNEL)) {
      return;
    }
    ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
    String version = in.readUTF();
    if (!version.equals(getPluginMeta().getVersion())) {
      player.kick(Component.text("[SocialFire] Proxy-Paper version de-sync! Check console for more information.")
        .color(TextColor.color(255, 0, 0)));
      getLogger().warning("[SocialFire] Proxy-Paper version de-sync for player: " + player.getName());
      getLogger().warning("[SocialFire] Proxy version: " + version + " Paper version: " + getPluginMeta().getVersion());
      getLogger().warning("[SocialFire] Make sure both proxy and paper are on the same plugin version");
      return;
    }
    switch (in.readUTF()) {
      case "sound": {
        String soundName = in.readUTF();
        if (soundName.equalsIgnoreCase("")) {
          return;
        }
        player.playSound(player.getLocation(), Sound.valueOf(soundName), 1f, 1f);
      }
      ;
      case "reload": {
        player.updateCommands();
      }
    }

  }
}
