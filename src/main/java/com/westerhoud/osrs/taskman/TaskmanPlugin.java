package com.westerhoud.osrs.taskman;

import com.google.inject.Provides;
import com.westerhoud.osrs.taskman.ui.TaskmanPluginPanel;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(name = "Taskman")
public class TaskmanPlugin extends Plugin {
  @Inject private Client client;
  @Inject private ClientToolbar clientToolbar;
  @Inject private TaskmanConfig config;

  private TaskmanPluginPanel sidePanel;
  @Override
  protected void startUp() {
    // Sidebar
    final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");
    sidePanel = new TaskmanPluginPanel();
    clientToolbar.addNavigation(
        NavigationButton.builder()
            .tooltip("Taskman")
            .priority(1)
            .icon(icon)
            .panel(sidePanel)
            .build());
  }

  @Provides
  TaskmanConfig provideConfig(ConfigManager configManager) {
    return configManager.getConfig(TaskmanConfig.class);
  }
}
