package com.westerhoud.osrs.taskman;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("taskman")
public interface TaskmanConfig extends Config {
  @ConfigItem(
      keyName = "username",
      name = "Username",
      description = "Your username on the Taskman website")
  String username();

  @ConfigItem(
      keyName = "password",
      name = "Password",
      description = "Your password on the Taskman website")
  String password();
}
