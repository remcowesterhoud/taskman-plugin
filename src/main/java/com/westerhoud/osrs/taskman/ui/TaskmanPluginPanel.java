package com.westerhoud.osrs.taskman.ui;

import com.westerhoud.osrs.taskman.TaskmanPlugin;
import com.westerhoud.osrs.taskman.domain.Task;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.ColorJButton;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.ui.components.shadowlabel.JShadowedLabel;
import net.runelite.client.util.ImageUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

@Slf4j
public class TaskmanPluginPanel extends PluginPanel {

  private final TaskmanPlugin taskmanPlugin;
  private final JPanel taskPanel;
  private final PluginErrorPanel errorPanel;
  private final JShadowedLabel currentTaskLabel = new JShadowedLabel("Current task:");
  private final JShadowedLabel imageLabel = new JShadowedLabel();
  private final JShadowedLabel nameLabel = new JShadowedLabel();
  private final ColorJButton generateButton =
      new ColorJButton("Generate task", ColorScheme.DARK_GRAY_COLOR);
  private final ColorJButton completeButton =
      new ColorJButton("Complete task", ColorScheme.DARK_GRAY_COLOR);

  public TaskmanPluginPanel(final TaskmanPlugin taskmanPlugin) {
    super();
    this.taskmanPlugin = taskmanPlugin;

    setOpaque(false);
    setBorder(new EmptyBorder(50, 10, 0, 10));
    setLayout(new BorderLayout());

    taskPanel = new JPanel(new BorderLayout(10, 10));
    taskPanel.setVisible(false);

    currentTaskLabel.setFont(FontManager.getRunescapeFont());
    currentTaskLabel.setForeground(Color.WHITE);
    nameLabel.setFont(FontManager.getRunescapeSmallFont());
    nameLabel.setHorizontalAlignment(SwingConstants.LEFT);

    final JPanel buttonPanel = new JPanel(new BorderLayout(5, 5));
    generateButton.setFont(FontManager.getRunescapeSmallFont());
    generateButton.setFocusPainted(false);
    generateButton.addActionListener(e -> generateTaskAndUpdateContent());
    completeButton.setFont(FontManager.getRunescapeSmallFont());
    completeButton.setFocusPainted(false);
    buttonPanel.add(generateButton, BorderLayout.WEST);
    buttonPanel.add(completeButton, BorderLayout.CENTER);

    taskPanel.add(currentTaskLabel, BorderLayout.NORTH);
    taskPanel.add(imageLabel, BorderLayout.WEST);
    taskPanel.add(nameLabel, BorderLayout.CENTER);
    taskPanel.add(buttonPanel, BorderLayout.SOUTH);

    errorPanel = new PluginErrorPanel();
    errorPanel.setVisible(false);

    getCurrentTaskAndUpdateContent();

    add(taskPanel, BorderLayout.NORTH);
    add(errorPanel, BorderLayout.SOUTH);
  }

  private void updateTaskPanelContent(final Task task) {
    imageLabel.setIcon(getTaskImage(task));
    nameLabel.setText(task.getName());
    taskPanel.setVisible(true);
    errorPanel.setVisible(false);
  }

  private void showErrorMessage(Exception e) {
    errorPanel.setContent("Oops... Something went wrong", e.getMessage());
    errorPanel.setVisible(true);
    errorPanel.revalidate();
    errorPanel.repaint();
  }

  private void getCurrentTaskAndUpdateContent() {
    try {
      final Task currentTask = taskmanPlugin.getCurrentTask();
      updateTaskPanelContent(currentTask);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      showErrorMessage(e);
    }
  }

  private void generateTaskAndUpdateContent() {
    try {
      final Task newTask = taskmanPlugin.generateTask();
      updateTaskPanelContent(newTask);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      showErrorMessage(e);
    }
  }

  private Icon getTaskImage(final Task currentTask) {
    BufferedImage image = null;
    try {
      String parsedImageUrl = currentTask.getImageUrl().split("\\?")[0];
      final URL imageUrl = new URL(parsedImageUrl);
      image = ImageIO.read(imageUrl);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }

    if (image == null) {
      log.info(currentTask.getImageUrl());
      image = ImageUtil.loadImageResource(getClass(), "error.png");
    }

    Image resizedImage = image.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
    return new ImageIcon(resizedImage);
  }

  public void reset() {
    taskPanel.setVisible(false);
    getCurrentTaskAndUpdateContent();
  }
}
