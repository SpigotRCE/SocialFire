package io.github.spigotrce.socialfire.common;

import io.github.spigotrce.socialfire.common.model.LinkModel;
import net.kyori.adventure.text.format.TextColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractAnnouncementsManager<T, P> {
    public Map<String, LinkModel> ANNOUNCEMENTS = new HashMap<>();
    public ArrayList<T> tasks = new ArrayList<>();

    public abstract void reload();
    public abstract void sendAnnouncement(P player, LinkModel model);

    public TextColor parseHexColors(String message) {
        Matcher matcher = Pattern.compile("#([0-9A-Fa-f]{6})").matcher(message);
        if (matcher.find())
            return TextColor.color(Integer.parseInt(matcher.group(0)));
        return TextColor.color(0xFFFFFF);
    }
}
