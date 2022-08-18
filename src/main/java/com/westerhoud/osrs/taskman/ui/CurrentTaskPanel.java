package com.westerhoud.osrs.taskman.ui;

import com.westerhoud.osrs.taskman.domain.Account;
import com.westerhoud.osrs.taskman.domain.AccountTask;
import com.westerhoud.osrs.taskman.service.TaskService;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class CurrentTaskPanel extends JPanel {

  public CurrentTaskPanel(final Account account, final TaskService taskService) {
    setLayout(new GridLayout(1, 2));
    setPreferredSize(new Dimension(300, 40));
    setMinimumSize(new Dimension(170, 20));

    Optional<AccountTask> currentTask = taskService.getCurrentTask(account);
    add(new JLabel("Current task:"));
    add(
        new JLabel(
            currentTask
                .map(r -> r.getTask().getName())
                .orElse("Could not find current task. Please make sure you have generated task.")));
  }
}
