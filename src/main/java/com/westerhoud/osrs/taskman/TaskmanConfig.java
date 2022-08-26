package com.westerhoud.osrs.taskman;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("taskman")
public interface TaskmanConfig extends Config {
  @ConfigItem(
      position = 1,
      keyName = "key",
      name = "Spreadsheet key",
      description = "The key of your spreadsheet")
  default String spreadsheetKey() {
    return "";
  }

  @ConfigItem(
      position = 2,
      keyName = "passphrase",
      name = "Passphrase",
      secret = true,
      description = "The passphrase you have added in your sheet")
  default String passphrase() {
    return "";
  }

  @ConfigItem(
      position = 3,
      keyName = "url",
      name = "Backend",
      description = "Backend which handles communicating with the spreadsheet using the Google Sheet API",
      hidden = true)
  default String url() {
    return "https://osrs-taskman.herokuapp.com";
  }
}
