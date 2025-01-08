package io.github.spigotrce.socialfire.model;

import com.velocitypowered.api.scheduler.ScheduledTask;
import io.github.spigotrce.socialfire.SocialFire;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
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
            ScheduledTask task = SocialFire.PROXY_SERVER.getScheduler().buildTask(SocialFire.INSTANCE, () -> {

                        String formattedMessage = model.message.replace("&", "§");


                        Component messageComponent = Component.text(formattedMessage)
                                .style(Style.style(parseHexColors(formattedMessage)));

                        SocialFire.PROXY_SERVER.sendMessage(messageComponent.clickEvent(
                                ClickEvent.openUrl(model.link)
                        ));

                        SocialFire.PROXY_SERVER.sendActionBar(Component.text(model.actionBar));

                        SocialFire.PROXY_SERVER.playSound(Sound.sound().type(Key.key("minecraft")).build());
                    })
                    .repeat(model.interval, TimeUnit.SECONDS)
                    .schedule();

            tasks.add(task);
        });
    }

    private TextColor parseHexColors(String message) {
        Matcher matcher = Pattern.compile("#([0-9A-Fa-f]{6})").matcher(message);
        if (matcher.find())
            return TextColor.color(Integer.parseInt(matcher.group(0)));
        return TextColor.color(0xFFFFFF);
    }
}
