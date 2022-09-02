package com.westerhoud.osrs.taskman;

import com.google.inject.Provides;
import com.westerhoud.osrs.taskman.domain.AccountCredentials;
import com.westerhoud.osrs.taskman.domain.AccountProgress;
import com.westerhoud.osrs.taskman.domain.Task;
import com.westerhoud.osrs.taskman.service.SheetService;
import com.westerhoud.osrs.taskman.ui.TaskmanPluginPanel;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
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
import okhttp3.OkHttpClient;

@Slf4j
@PluginDescriptor(name = "Taskman")
public class TaskmanPlugin extends Plugin {
  @Inject private Client client;
  @Inject private ClientToolbar clientToolbar;
  @Inject private TaskmanConfig config;
  @Inject private OkHttpClient okHttpClient;

  private TaskmanPluginPanel sidePanel;
  private SheetService sheetService;
  private NavigationButton navigationButton;

  @Override
  protected void startUp() throws Exception {
    // Sidebar
    final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");

    sheetService = new SheetService(okHttpClient);

    sidePanel = new TaskmanPluginPanel(this);
    navigationButton =
        NavigationButton.builder()
            .tooltip("Taskman")
            .priority(8)
            .icon(icon)
            .panel(sidePanel)
            .build();
    clientToolbar.addNavigation(navigationButton);
  }

  @Override
  protected void shutDown() throws Exception {
    // Sidebar
    clientToolbar.removeNavigation(navigationButton);
  }

  public Task getCurrentTask() throws Exception {
    return sheetService.getCurrentTask(getCredentials().getIdentifier());
  }

  public Task generateTask() throws Exception {
    return sheetService.generateTask(getCredentials());
  }

  public Task completeTask() throws Exception {
    return sheetService.completeTask(getCredentials());
  }

  public AccountProgress progress() throws Exception {
    return sheetService.getAccountProgress(getCredentials().getIdentifier());
  }

  private AccountCredentials getCredentials() {
    return new AccountCredentials(config.spreadsheetKey(), config.passphrase());
  }

  @Subscribe
  public void onConfigChanged(final ConfigChanged configChanged) {
    sidePanel.reset();
  }

  @Provides
  TaskmanConfig provideConfig(final ConfigManager configManager) {
    return configManager.getConfig(TaskmanConfig.class);
  }
}
