package com.westerhoud.osrs.taskman;

import com.google.inject.Provides;
import com.westerhoud.osrs.taskman.domain.Account;
import com.westerhoud.osrs.taskman.domain.AccountTask;
import com.westerhoud.osrs.taskman.service.AuthenticationService;
import com.westerhoud.osrs.taskman.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(name = "Taskman")
public class TaskmanPlugin extends Plugin {
  @Inject private Client client;

  @Inject private TaskmanConfig config;

  private AuthenticationService authenticationService;
  private TaskService taskService;
  private Account loggedInAccount;

  @Override
  protected void startUp() throws Exception {
    log.info("Taskman started!");
    authenticationService = new AuthenticationService(config);
    taskService = new TaskService(config);

    loggedInAccount = authenticationService.login();
    AccountTask currentTask = taskService.getCurrentTask(loggedInAccount);
  }

  @Override
  protected void shutDown() throws Exception {
    log.info("Taskman stopped!");
  }

  @Subscribe
  public void onGameStateChanged(GameStateChanged gameStateChanged) {
    if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
      client.addChatMessage(
          ChatMessageType.GAMEMESSAGE, "", "Example says " + config.username(), null);
    }
  }

  @Provides
  TaskmanConfig provideConfig(ConfigManager configManager) {
    return configManager.getConfig(TaskmanConfig.class);
  }
}
