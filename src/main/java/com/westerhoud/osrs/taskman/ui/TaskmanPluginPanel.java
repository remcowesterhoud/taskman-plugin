package com.westerhoud.osrs.taskman.ui;

import com.westerhoud.osrs.taskman.domain.Account;
import com.westerhoud.osrs.taskman.service.TaskService;
import net.runelite.client.ui.PluginPanel;

import java.awt.*;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class TaskmanPluginPanel extends PluginPanel {

  private final TaskService taskService;

  public TaskmanPluginPanel(final Optional<Account> account, TaskService taskService) {
    super();
    this.taskService = taskService;
    setLayout(new BorderLayout());
    account.ifPresentOrElse(this::addLoggedInPanels, this::addErrorPanels);
  }

  private void addLoggedInPanels(Account account) {
    LoggedUserPanel loggedUserPanel = new LoggedUserPanel(account);
    CurrentTaskPanel currentTaskPanel = new CurrentTaskPanel(account, taskService);
    ButtonsPanel buttonsPanel = new ButtonsPanel(account, taskService, loggedUserPanel, currentTaskPanel);

    add(loggedUserPanel, BorderLayout.NORTH);
    add(currentTaskPanel, BorderLayout.CENTER);
    add(buttonsPanel, BorderLayout.SOUTH);
  }

  private void addErrorPanels() {
    add(new ErrorPanel("Could not login. Please check verify that you have entered the correct credentials." +
            " Contact us if the problem persists."), BorderLayout.CENTER);
  }

  public void reset(final Optional<Account> account) {
    removeAll();
    account.ifPresentOrElse(this::addLoggedInPanels, this::addErrorPanels);
    revalidate();
    repaint();
  }
}
