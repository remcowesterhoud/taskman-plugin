package com.westerhoud.osrs.taskman.ui;

import javax.swing.*;
import java.awt.*;

public class ButtonsPanel extends JPanel {

  public ButtonsPanel() {
    setLayout(new GridLayout(1, 2, 5, 5));

    final JButton generateButton = new JButton("Generate task");
    generateButton.addActionListener(e -> System.out.println("Generating new task!"));
    add(generateButton);

    final JButton completeButton = new JButton("Complete task");
    completeButton.addActionListener(e -> System.out.println("Completing task!"));
    add(completeButton);
  }
}
