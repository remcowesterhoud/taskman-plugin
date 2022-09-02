package com.westerhoud.osrs.taskman;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class TaskmanPluginTest {
  public static void main(final String[] args) throws Exception {
    ExternalPluginManager.loadBuiltin(TaskmanPlugin.class);
    RuneLite.main(args);
  }
}
