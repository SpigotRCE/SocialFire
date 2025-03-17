package io.github.spigotrce.socialfire.common.config;

import dev.dejvokep.boostedyaml.route.Route;
import io.github.spigotrce.socialfire.velocity.VelocityFire;
import io.github.spigotrce.socialfire.common.model.LinkModel;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Config extends ConfigProvider {
    public Config(Path dataDirectory) {
        super("config.yml", "file-version", dataDirectory.toFile());
    }

    public void updateLinks() {
        VelocityFire.ANNOUNCEMENT_MANAGER.reload();
    }

    public Map<String, LinkModel> getLinks() {
        Map<String, LinkModel> models = new HashMap<>();
        getFileConfig().getSection(
                Route.fromString("announcements"))
                .getKeys().forEach(
                        type -> {
                            Map<String, String> fields = new HashMap<>();
                            getFileConfig().getSection(
                                    Route.fromString("announcements." + type)
                            ).getKeys().forEach(
                                    field -> fields.put(
                                            (String) field,
                                            getFileConfig().getString(
                                                    Route.fromString(
                                                            "announcements." + type + "." + field)
                                            )
                                    )
                            );
                            models.put(
                                    (String) type,
                                    new LinkModel().deserialize(fields)
                            );
                        });

        return models;
    }

    public Object getExact(String route) {
        return getFileConfig().get(Route.fromString(route));
    }

    @Override
    public void onReload() {
        updateLinks();
    }
}
