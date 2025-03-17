package io.github.spigotrce.socialfire.common.model;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.ScheduledTask;
import io.github.spigotrce.socialfire.velocity.SocialFire;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnnouncementsManager {
    public Map<String, LinkModel> ANNOUNCEMENTS;
    public ArrayList<ScheduledTask> tasks;

    public AnnouncementsManager() {
        ANNOUNCEMENTS = new HashMap<>();
        tasks = new ArrayList<>();
    }

    public void reload() {
        tasks.forEach(ScheduledTask::cancel);
        ANNOUNCEMENTS = SocialFire.CONFIG.getLinks();


        ANNOUNCEMENTS.forEach((name, model) -> {
            ScheduledTask task = SocialFire.PROXY_SERVER.getScheduler().buildTask(
                    SocialFire.INSTANCE,
                            () -> SocialFire.PROXY_SERVER.getAllPlayers()
                                    .forEach(
                                            player -> sendAnnouncement(
                                                    player,
                                                    model
                                            )
                                    )
                    )
                    .repeat(model.interval, TimeUnit.SECONDS)
                    .schedule();

            tasks.add(task);
        });
    }

    // TODO: Implement component formatting in LinkModel
    public void sendAnnouncement(Player player, LinkModel model) {
        String formattedMessage = model.message.replace("&", "§");

        Component messageComponent = Component.text(formattedMessage)
                .style(Style.style(parseHexColors(formattedMessage)));

        player.sendMessage(messageComponent.clickEvent(
                ClickEvent.openUrl(model.link)
        ));

        String formattedActionBar = model.actionBar.replace("&", "§");

        Component actionBarComponent = Component.text(formattedActionBar)
                .style(Style.style(parseHexColors(formattedActionBar)));

        player.sendActionBar(
                actionBarComponent
        );

        player.getCurrentServer().ifPresent(
                serverConnection -> serverConnection
                        .sendPluginMessage(
                                SocialFire.CHANNEL_NAME,
                                out -> {
                                    out.writeUTF(SocialFire.VERSION);
                                    out.writeUTF(model.sound);
                                }
                        )
        );
    }

    private TextColor parseHexColors(String message) {
        Matcher matcher = Pattern.compile("#([0-9A-Fa-f]{6})").matcher(message);
        if (matcher.find())
            return TextColor.color(Integer.parseInt(matcher.group(0)));
        return TextColor.color(0xFFFFFF);
    }
}
