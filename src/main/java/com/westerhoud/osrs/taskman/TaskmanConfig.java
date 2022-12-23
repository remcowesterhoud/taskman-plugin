package com.westerhoud.osrs.taskman;

import com.westerhoud.osrs.taskman.domain.TaskSource;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("taskman")
public interface TaskmanConfig extends Config {

  @ConfigSection(
      name = "Spreadsheet",
      description = "Spreadsheet configuration",
      position = 1,
      closedByDefault = true)
  String spreadsheet = "spreadsheet";

  @ConfigSection(
      name = "Website",
      description = "Website configuration",
      position = 2,
      closedByDefault = true)
  String website = "website";

  @ConfigItem(
      position = 0,
      keyName = "taskSource",
      name = "Task source",
      description = "Spreadsheet or website")
  default TaskSource taskSource() {
    return TaskSource.SPREADSHEET;
  }

  @ConfigItem(
      position = 3,
      keyName = "key",
      name = "Spreadsheet key",
      description = "The key of your spreadsheet",
      section = spreadsheet)
  default String spreadsheetKey() {
    return "";
  }

  @ConfigItem(
      position = 4,
      keyName = "passphrase",
      name = "Passphrase",
      secret = true,
      description = "The passphrase you have added in your sheet",
      section = spreadsheet)
  default String passphrase() {
    return "";
  }

  @ConfigItem(
      position = 5,
      keyName = "websiteUsername",
      name = "Username",
      description = "Website username",
      section = website)
  default String websiteUsername() {
    return "username";
  }

  @ConfigItem(
      position = 6,
      keyName = "websitePassword",
      name = "Password",
      secret = true,
      description = "Website password",
      section = website)
  default String websitePassword() {
    return "password";
  }

  @ConfigItem(
      position = 7,
      keyName = "showOverlay",
      name = "Show current task overlay",
      description = "Adds an overlay displaying the current task to the game client")
  default boolean showOverlay() {
    return false;
  }

  @ConfigItem(
      position = 8,
      keyName = "taskmanCommand",
      name = "Enable !taskman chat command",
      description = "Send your current progress and task into the chat")
  default boolean taskmanCommand() {
    return true;
  }
}
