package com.westerhoud.osrs.taskman;

import com.google.inject.Provides;
import com.westerhoud.osrs.taskman.domain.AccountCredentials;
import com.westerhoud.osrs.taskman.domain.AccountProgress;
import com.westerhoud.osrs.taskman.domain.Task;
import com.westerhoud.osrs.taskman.domain.TaskmanCommandData;
import com.westerhoud.osrs.taskman.service.TaskService;
import com.westerhoud.osrs.taskman.ui.CurrentTaskOverlay;
import com.westerhoud.osrs.taskman.ui.TaskmanPluginPanel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MessageNode;
import net.runelite.api.Player;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatCommandManager;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;
import okhttp3.OkHttpClient;

@Slf4j
@PluginDescriptor(name = "Taskman")
public class TaskmanPlugin extends Plugin {

  private static final String TASKMAN_CONFIG_GROUP = "taskman";
  private static final String TASKMAN_CHAT_COMMAND = "!taskman";
  @Inject private Client client;
  @Inject private ClientThread clientThread;
  @Inject private ClientToolbar clientToolbar;
  @Inject private TaskmanConfig config;
  @Inject private OkHttpClient okHttpClient;
  @Inject private OverlayManager overlayManager;
  @Inject private CurrentTaskOverlay currentTaskOverlay;
  @Inject private ChatCommandManager chatCommandManager;

  private TaskmanPluginPanel sidePanel;
  private TaskService taskService;
  private NavigationButton navigationButton;
  private boolean loggedIn = false;
  private boolean sidePanelInitialized = false;

  @Override
  protected void startUp() throws Exception {
    // Sidebar
    final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");
    taskService = new TaskService(okHttpClient);
    chatCommandManager.registerCommandAsync(TASKMAN_CHAT_COMMAND, this::getTaskmanCommandData);

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
    chatCommandManager.unregisterCommand(TASKMAN_CHAT_COMMAND);
  }

  public Task getCurrentTask() throws Exception {
    final Task task = taskService.getCurrentTask(getCredentials(), getRsn());
    currentTaskOverlay.setTask(task);
    return task;
  }

  public Task generateTask() throws Exception {
    final Task task = taskService.generateTask(getCredentials(), getRsn());
    currentTaskOverlay.setTask(task);
    return task;
  }

  public Task completeTask() throws Exception {
    final Task task = taskService.completeTask(getCredentials(), getRsn());
    currentTaskOverlay.setTask(task);
    return task;
  }

  public AccountProgress progress() throws Exception {
    return taskService.getAccountProgress(getCredentials(), getRsn());
  }

  private void getTaskmanCommandData(final ChatMessage chatMessage, final String message) {
    if (!config.taskmanCommand()) {
      return;
    }

    final ChatMessageType type = chatMessage.getType();
    final String rsn;
    if (type == ChatMessageType.PRIVATECHATOUT) {
      rsn = getRsn();
    } else {
      rsn = Text.removeTags(chatMessage.getName()).replace('\u00A0', ' ');
    }

    final TaskmanCommandData data;
    try {
      data = taskService.getChatCommandData(rsn);
    } catch (final IOException ex) {
      log.debug("Unable to get chat command data", ex);
      return;
    }

    final String response =
        new ChatMessageBuilder()
            .append(ChatColorType.NORMAL)
            .append("Progress: ")
            .append(ChatColorType.HIGHLIGHT)
            .append(data.getProgressPercentage() + "% " + data.getTier())
            .append(ChatColorType.NORMAL)
            .append(" Current task: ")
            .append(ChatColorType.HIGHLIGHT)
            .append(data.getTask().getName())
            .build();

    final MessageNode messageNode = chatMessage.getMessageNode();
    messageNode.setRuneLiteFormatMessage(response);
    client.refreshChat();
  }

  private AccountCredentials getCredentials() {
    switch (config.taskSource()) {
      case SPREADSHEET:
        return new AccountCredentials(
            config.spreadsheetKey(), config.passphrase(), config.taskSource());
      case WEBSITE:
        return new AccountCredentials(
            config.websiteUsername(), config.websitePassword(), config.taskSource());
      default:
        throw new IllegalArgumentException("No task source selected in config.");
    }
  }

  private String getRsn() {
    final Player player = client.getLocalPlayer();
    if (player == null) {
      throw new IllegalArgumentException("Please login first!");
    }
    return player.getName();
  }

  @Subscribe
  public void onGameStateChanged(final GameStateChanged gameStateChanged) {
    if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
      loggedIn = true;
    } else if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN) {
      loggedIn = false;
      sidePanelInitialized = false;
      sidePanel.onLogout();
    }
  }

  @Subscribe
  public void onGameTick(final GameTick gameTick) {
    if (!sidePanelInitialized && loggedIn) {
      sidePanel.init();
      sidePanelInitialized = true;
    }
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
