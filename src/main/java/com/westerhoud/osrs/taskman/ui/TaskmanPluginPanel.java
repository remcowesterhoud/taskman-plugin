package com.westerhoud.osrs.taskman.ui;

import com.westerhoud.osrs.taskman.domain.Account;
import net.runelite.client.ui.PluginPanel;

import javax.swing.text.html.Option;
import java.awt.*;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class TaskmanPluginPanel extends PluginPanel {

  public TaskmanPluginPanel(final Optional<Account> account) {
    super();
    setLayout(new BorderLayout());
    account.ifPresentOrElse(this::addLoggedInPanels, this::addErrorPanels);
  }

  private void addLoggedInPanels(Account account) {
    add(new LoggedUserPanel(account.getUsername()), BorderLayout.NORTH);
//    add(new CurrentTaskPanel(currentTask.getTask()), BorderLayout.CENTER);
    add(new ButtonsPanel(), BorderLayout.SOUTH);
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
