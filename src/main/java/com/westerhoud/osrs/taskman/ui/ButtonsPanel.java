package com.westerhoud.osrs.taskman.ui;

import com.westerhoud.osrs.taskman.domain.Account;
import com.westerhoud.osrs.taskman.domain.AccountTask;
import com.westerhoud.osrs.taskman.service.TaskService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Optional;

public class ButtonsPanel extends JPanel {

  private Account account;
  private TaskService taskService;
  private final LoggedUserPanel loggedUserPanel;
  private final CurrentTaskPanel currentTaskPanel;
  private final ErrorPanel errorPanel;

  public ButtonsPanel(
      final Account account,
      final TaskService taskService,
      LoggedUserPanel loggedUserPanel,
      CurrentTaskPanel currentTaskPanel) {
    this.account = account;
    this.taskService = taskService;
    this.loggedUserPanel = loggedUserPanel;
    this.currentTaskPanel = currentTaskPanel;

    setLayout(new GridLayout(2, 1, 5, 5));
    setPreferredSize(new Dimension(200, 70));
    setMinimumSize(new Dimension(125, 50));

    final JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 5, 5));
    final JButton generateButton = new JButton("Generate task");
    generateButton.addActionListener(this::generateTask);
    buttonsPanel.add(generateButton);
    final JButton completeButton = new JButton("Complete task");
    completeButton.addActionListener(this::completeTask);
    buttonsPanel.add(completeButton);
    add(buttonsPanel);

    errorPanel = new ErrorPanel("");
    errorPanel.setVisible(false);
    add(errorPanel);
  }

  private void generateTask(ActionEvent actionEvent) {
    final Optional<AccountTask> task = taskService.generateTask(account);
    task.ifPresentOrElse(
        this::updatePanelsAfterGenerate, () -> addError("Could not generate task."));
  }

  private void updatePanelsAfterGenerate(AccountTask t) {
    currentTaskPanel.setCurrentTask(t.getTask().getName());
    errorPanel.setVisible(false);
  }

  private void completeTask(ActionEvent actionEvent) {
    final Optional<AccountTask> task = taskService.completeTask(account);
    task.ifPresentOrElse(
        this::updatePanelsAfterComplete, () -> addError("Could not complete task."));
  }

  private void updatePanelsAfterComplete(AccountTask t) {
    currentTaskPanel.setCurrentTask("Task complete. Generate a new one!");
    loggedUserPanel.setTier(t.getAccount().getTier());
    errorPanel.setVisible(false);
  }

  private void addError(final String error) {
    errorPanel.setError(error);
    errorPanel.setVisible(true);
  }
}
