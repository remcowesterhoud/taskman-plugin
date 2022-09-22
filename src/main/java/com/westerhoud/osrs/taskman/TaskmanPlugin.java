package com.westerhoud.osrs.taskman;

import com.google.inject.Provides;
import com.westerhoud.osrs.taskman.domain.AccountCredentials;
import com.westerhoud.osrs.taskman.domain.AccountProgress;
import com.westerhoud.osrs.taskman.domain.Task;
import com.westerhoud.osrs.taskman.service.SheetService;
import com.westerhoud.osrs.taskman.ui.CurrentTaskOverlay;
import com.westerhoud.osrs.taskman.ui.TaskmanPluginPanel;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import okhttp3.OkHttpClient;

@Slf4j
@PluginDescriptor(name = "Taskman")
public class TaskmanPlugin extends Plugin {

  public static final String TASKMAN_CONFIG_GROUP = "taskman";
  @Inject private Client client;
  @Inject private ClientToolbar clientToolbar;
  @Inject private TaskmanConfig config;
  @Inject private OkHttpClient okHttpClient;
  @Inject private OverlayManager overlayManager;
  @Inject private CurrentTaskOverlay currentTaskOverlay;

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
    overlayManager.add(currentTaskOverlay);
  }

  @Override
  protected void shutDown() throws Exception {
    // Sidebar
    clientToolbar.removeNavigation(navigationButton);
    overlayManager.remove(currentTaskOverlay);
  }

  public Task getCurrentTask() throws Exception {
    final Task task = sheetService.getCurrentTask(getCredentials().getIdentifier());
    currentTaskOverlay.setTask(task);
    return task;
  }

  public Task generateTask() throws Exception {
    final Task task = sheetService.generateTask(getCredentials());
    currentTaskOverlay.setTask(task);
    return task;
  }

  public Task completeTask() throws Exception {
    final Task task = sheetService.completeTask(getCredentials());
    currentTaskOverlay.setTask(task);
    return task;
  }

  public AccountProgress progress() throws Exception {
    return sheetService.getAccountProgress(getCredentials().getIdentifier());
  }

  private AccountCredentials getCredentials() {
    return new AccountCredentials(config.spreadsheetKey(), config.passphrase());
  }

  @Subscribe
  public void onConfigChanged(final ConfigChanged configChanged) {
    if (configChanged.getGroup().equals(TASKMAN_CONFIG_GROUP)) {
      SwingUtilities.invokeLater(() -> sidePanel.reset());
    }
  }

  @Provides
  TaskmanConfig provideConfig(final ConfigManager configManager) {
    return configManager.getConfig(TaskmanConfig.class);
  }
}
