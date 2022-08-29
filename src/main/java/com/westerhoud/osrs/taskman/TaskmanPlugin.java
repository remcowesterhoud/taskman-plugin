package com.westerhoud.osrs.taskman;

import com.google.inject.Provides;
import com.westerhoud.osrs.taskman.domain.Progress;
import com.westerhoud.osrs.taskman.domain.Task;
import com.westerhoud.osrs.taskman.service.SheetService;
import com.westerhoud.osrs.taskman.ui.TaskmanPluginPanel;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
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
  private SheetService sheetService;

  @Override
  protected void startUp() throws Exception {
    // Sidebar
    final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");

    sheetService = new SheetService();

    sidePanel = new TaskmanPluginPanel(this);
    clientToolbar.addNavigation(
        NavigationButton.builder()
            .tooltip("Taskman")
            .priority(69)
            .icon(icon)
            .panel(sidePanel)
            .build());
  }

  public Task getCurrentTask() throws Exception {
    return sheetService.getCurrentTask(config.spreadsheetKey(), config.passphrase());
  }

  public Task generateTask() throws Exception {
    return sheetService.generateTask(config.spreadsheetKey(), config.passphrase());
  }

  public Task completeTask() throws Exception {
    return sheetService.completeTask(config.spreadsheetKey(), config.passphrase());
  }

  public Progress progress() throws Exception {
    return sheetService.progress(config.spreadsheetKey(), config.passphrase());
  }

  @Subscribe
  public void onConfigChanged(final ConfigChanged configChanged) {
    sidePanel.reset();
  }

  @Provides
  TaskmanConfig provideConfig(ConfigManager configManager) {
    return configManager.getConfig(TaskmanConfig.class);
  }
}
