package io.github.spigotrce.socialfire.velocity;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.ScheduledTask;
import io.github.spigotrce.socialfire.common.AbstractAnnouncementsManager;
import io.github.spigotrce.socialfire.common.model.LinkModel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.Style;

import java.util.concurrent.TimeUnit;

public class VelocityAnnouncementsManager extends AbstractAnnouncementsManager {
    @Override
    public void reload() {
        tasks.forEach(ScheduledTask::cancel);
        ANNOUNCEMENTS = VelocityFire.CONFIG.getLinks();
        VelocityFire.INSTANCE.reloadCommands();


        ANNOUNCEMENTS.forEach((name, model) -> {
            ScheduledTask task = VelocityFire.PROXY_SERVER.getScheduler().buildTask(
                    VelocityFire.INSTANCE,
                            () -> VelocityFire.PROXY_SERVER.getAllPlayers()
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
    @Override
    public void sendAnnouncement(Object playerObject, LinkModel model) {
        Player player = (Player) playerObject;

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
                                VelocityFire.CHANNEL_NAME,
                                out -> {
                                    out.writeUTF(VelocityFire.VERSION);
                                    out.writeUTF("sound");
                                    out.writeUTF(model.sound);
                                }
                        )
        );
    }
}
