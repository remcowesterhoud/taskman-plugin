package com.westerhoud.osrs.taskman;

import com.google.inject.Provides;
import com.westerhoud.osrs.taskman.domain.Account;
import com.westerhoud.osrs.taskman.domain.AccountTask;
import com.westerhoud.osrs.taskman.service.AuthenticationService;
import com.westerhoud.osrs.taskman.service.TaskService;
import com.westerhoud.osrs.taskman.ui.TaskmanPluginPanel;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
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
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

@Slf4j
@PluginDescriptor(name = "Taskman")
public class TaskmanPlugin extends Plugin {
  @Inject private Client client;
  @Inject private ClientToolbar clientToolbar;
  @Inject private TaskmanConfig config;

  private TaskmanPluginPanel sidePanel;
  private AuthenticationService authenticationService;
  private TaskService taskService;
  private URI baseUri;
  private Optional<Account> loggedInUser;

  @Override
  protected void startUp() throws Exception {
    log.info("Taskman started!");
    baseUri = new URI(config.url());

    authenticationService = new AuthenticationService(baseUri);
    taskService = new TaskService(baseUri);

    loggedInUser = authenticationService.login(config.username(), config.password());

    // Sidebar
    final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");
    sidePanel = new TaskmanPluginPanel(loggedInUser);

    clientToolbar.addNavigation(
        NavigationButton.builder()
            .tooltip("Taskman")
            .priority(1)
            .icon(icon)
            .panel(sidePanel)
            .build());
  }

  @Override
  protected void shutDown() throws Exception {
    log.info("Taskman stopped!");
  }

  @Subscribe
  public void onConfigChanged(final ConfigChanged configChanged) {
    System.out.println("Config was changed!");
    if (configChanged.getKey().equals("username") || configChanged.getKey().equals("password")) {
      loggedInUser = authenticationService.login(config.username(), config.password());
    }
    sidePanel.reset(loggedInUser);
  }

  @Provides
  TaskmanConfig provideConfig(ConfigManager configManager) {
    return configManager.getConfig(TaskmanConfig.class);
  }
}
