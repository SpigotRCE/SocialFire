package io.github.spigotrce.socialfire.model;

import java.util.Map;

public class LinkModel {
    public String link;
    public int interval;
    public String sound;
    public String message;
    public String actionBar;

    public LinkModel() {
    }

    public LinkModel(String link, int interval, String sound, String message, String actionBar) {
        this.link = link;
        this.interval = interval;
        this.sound = sound;
        this.message = message;
        this.actionBar = actionBar;
    }

    public LinkModel deserialize(Map<String, String> map) {
        this.link = map.get("link");
        this.interval = Integer.parseInt(map.get("interval"));
        this.sound = map.get("sound");
        this.message = map.get("message");
        this.actionBar = map.get("action-bar");
        return this;
    }

    public String toString() {
        return "AnnouncementModel{link=" + link + ", interval=" + interval + ", sound=" + sound + ", message=" + message + ", actionBar=" + actionBar + "}";
    }
}
