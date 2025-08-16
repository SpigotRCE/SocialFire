package io.github.spigotrce.socialfire.common.config;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;

import java.io.*;
import java.util.Objects;

public abstract class ConfigProvider {
  private final String fileName;
  private final String versioningTag;
  private final File dataDirectory;
  private YamlDocument fileConfig;

  public ConfigProvider(String fileName, String versioningTag, File dataDirectory) {
    this.fileName = fileName;
    this.versioningTag = versioningTag;
    this.dataDirectory = dataDirectory;
  }

  public void load() throws IOException {
    this.fileConfig = YamlDocument.create(new File(getDataDirectory(), getFileName()),
      Objects.requireNonNull(getClass().getResourceAsStream("/" + getFileName())),
      GeneralSettings.DEFAULT,
      LoaderSettings.builder().setAutoUpdate(true).build(),
      DumperSettings.DEFAULT,
      UpdaterSettings.builder()
        .setVersioning(new BasicVersioning(getVersioningTag()))
        .setOptionSorting(UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS)
        .build());

    update();
    reload();
    save();
  }

  public File getDataDirectory() {
    return dataDirectory;
  }

  public String getFileName() {
    return fileName;
  }

  public String getVersioningTag() {
    return versioningTag;
  }

  public void update() throws IOException {
    getFileConfig().update();
  }

  public void reload() throws IOException {
    getFileConfig().reload();
    onReload();
  }

  public void save() throws IOException {
    getFileConfig().save();
  }

  public YamlDocument getFileConfig() {
    return fileConfig;
  }

  public abstract void onReload();
}
