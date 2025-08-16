package io.github.spigotrce.socialfire.bungee;

import io.github.spigotrce.socialfire.common.AbstractAnnouncementsManager;
import io.github.spigotrce.socialfire.common.model.LinkModel;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class BungeeAnnouncementsManager extends AbstractAnnouncementsManager<ScheduledTask, ProxiedPlayer> {
  @Override public void reload() {
    tasks.forEach(ScheduledTask::cancel);
    ANNOUNCEMENTS = BungeeFire.CONFIG.getLinks();
    BungeeFire.INSTANCE.reloadCommands();


    ANNOUNCEMENTS.forEach((name, model) -> {

      ScheduledTask task = BungeeFire.PROXY_SERVER.getScheduler()
        .schedule(BungeeFire.INSTANCE,
          () -> BungeeFire.PROXY_SERVER.getPlayers().forEach(player -> sendAnnouncement(player, model)),
          0,
          model.interval,
          TimeUnit.SECONDS);
      tasks.add(task);
    });
  }

  @Override public void sendAnnouncement(ProxiedPlayer player, LinkModel model) {
    String formattedMessage = model.message.replace("&", "§");

    TextComponent messageComponent = new TextComponent(formattedMessage);
    messageComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, model.link));
    player.sendMessage(messageComponent);

    String formattedActionBar = model.actionBar.replace("&", "§");

    player.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(formattedActionBar));

    if (player.getServer() != null) {
      try {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteOut);
        out.writeUTF(BungeeFire.VERSION);
        out.writeUTF("sound");
        out.writeUTF(model.sound);

        player.getServer().sendData(BungeeFire.CHANNEL_NAME, byteOut.toByteArray());
      } catch (Exception e) {
        BungeeFire.LOGGER.severe("Failed to send plugin message: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }
}
