package com.westerhoud.osrs.taskman;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("taskman")
public interface TaskmanConfig extends Config {
  @ConfigItem(
      position = 1,
      section = "credentials",
      keyName = "username",
      name = "Username",
      description = "Your username on the Taskman website")
  default String username() {
    return "username";
  }

  @ConfigItem(
      position = 2,
      section = "credentials",
      keyName = "password",
      name = "Password",
      secret = true,
      description = "Your password on the Taskman website")
  default String password() {
    return "password";
  }

  @ConfigItem(
      position = 3,
      keyName = "url",
      name = "Taskman website",
      description = "Url of the Taskman website",
      hidden = true)
  default String url() {
    return "https://osrs-taskman.herokuapp.com";
  }
}
