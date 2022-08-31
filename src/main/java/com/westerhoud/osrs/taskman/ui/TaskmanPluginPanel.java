package com.westerhoud.osrs.taskman.ui;

import com.westerhoud.osrs.taskman.TaskmanPlugin;
import com.westerhoud.osrs.taskman.domain.Progress;
import com.westerhoud.osrs.taskman.domain.Task;
import com.westerhoud.osrs.taskman.domain.TierProgress;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.ColorJButton;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.ui.components.ProgressBar;
import net.runelite.client.ui.components.shadowlabel.JShadowedLabel;
import net.runelite.client.util.ImageUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

@Slf4j
public class TaskmanPluginPanel extends PluginPanel {

  private final TaskmanPlugin taskmanPlugin;
  private final JPanel taskPanel;
  private final JPanel taskDataPanel;
  private final JPanel progressPanel;
  private final PluginErrorPanel errorPanel;
  private final JShadowedLabel currentTaskLabel = new JShadowedLabel("Current task:");
  private final JShadowedLabel progressLabel = new JShadowedLabel("Progress:");
  private final JShadowedLabel imageLabel = new JShadowedLabel();
  private final JShadowedLabel nameLabel = new JShadowedLabel();
  private final JButton refreshButton = new JButton();
  private final ColorJButton generateButton =
      new ColorJButton("Generate task", ColorScheme.DARK_GRAY_COLOR);
  private final ColorJButton completeButton =
      new ColorJButton("Complete task", ColorScheme.DARK_GRAY_COLOR);

  public TaskmanPluginPanel(final TaskmanPlugin taskmanPlugin) {
    super();
    this.taskmanPlugin = taskmanPlugin;

    setOpaque(false);
    setBorder(new EmptyBorder(50, 0, 0, 0));
    setLayout(new BorderLayout());

    taskPanel = new JPanel(new BorderLayout(10, 10));
    taskPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
    taskPanel.setVisible(false);

    taskDataPanel = new JPanel(new BorderLayout(10, 5));
    final JPanel taskDataTitlePanel = new JPanel(new BorderLayout());
    currentTaskLabel.setFont(FontManager.getRunescapeFont());
    currentTaskLabel.setForeground(Color.WHITE);
    refreshButton.setIcon(getRefreshButton());
    refreshButton.setPreferredSize(new Dimension(25, 25));
    refreshButton.setMaximumSize(new Dimension(25, 25));
    refreshButton.setFocusPainted(false);
    refreshButton.addActionListener(e -> this.reset());
    taskDataTitlePanel.add(currentTaskLabel, BorderLayout.WEST);
    taskDataTitlePanel.add(refreshButton, BorderLayout.EAST);
    nameLabel.setFont(FontManager.getRunescapeSmallFont());
    nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
    taskDataPanel.add(taskDataTitlePanel, BorderLayout.NORTH);
    taskDataPanel.add(imageLabel, BorderLayout.WEST);
    taskDataPanel.add(nameLabel, BorderLayout.CENTER);

    final JPanel buttonPanel = new JPanel(new BorderLayout(10, 10));
    generateButton.setFont(FontManager.getRunescapeSmallFont());
    generateButton.setFocusPainted(false);
    generateButton.addActionListener(e -> generateTaskAndUpdateContent());
    completeButton.setFont(FontManager.getRunescapeSmallFont());
    completeButton.setFocusPainted(false);
    completeButton.addActionListener(e -> completeTaskAndUpdateContent());
    buttonPanel.add(generateButton, BorderLayout.WEST);
    buttonPanel.add(completeButton, BorderLayout.CENTER);

    progressPanel = new JPanel(new GridLayout(5, 1, 10, 10));
    progressPanel.setBorder(new EmptyBorder(30, 10, 0, 10));
    progressPanel.setVisible(false);

    progressLabel.setFont(FontManager.getRunescapeFont());
    progressLabel.setForeground(Color.WHITE);

    taskPanel.add(taskDataPanel, BorderLayout.NORTH);
    taskPanel.add(buttonPanel, BorderLayout.CENTER);
    taskPanel.add(progressPanel, BorderLayout.SOUTH);

    errorPanel = new PluginErrorPanel();
    errorPanel.setBorder(new EmptyBorder(50, 0, 0, 0));
    errorPanel.setVisible(false);

    getCurrentTaskAndUpdateContent();
    getProgressAndUpdateContent();

    add(taskPanel, BorderLayout.NORTH);
    add(progressPanel, BorderLayout.CENTER);
    add(errorPanel, BorderLayout.SOUTH);
  }

  private void updateTaskPanelContent(final Task task) {
    imageLabel.setIcon(getTaskImage(task));
    nameLabel.setText(task.getName());
    taskPanel.setVisible(true);
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
      errorPanel.setVisible(false);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      showErrorMessage(e);
    }
  }

  private void generateTaskAndUpdateContent() {
    try {
      final Task newTask = taskmanPlugin.generateTask();
      updateTaskPanelContent(newTask);
      errorPanel.setVisible(false);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      showErrorMessage(e);
    }
  }

  private void completeTaskAndUpdateContent() {
    try {
      final Task newTask = taskmanPlugin.completeTask();
      updateTaskPanelContent(newTask);
      getProgressAndUpdateContent();
      errorPanel.setVisible(false);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      showErrorMessage(e);
    }
  }

  private void getProgressAndUpdateContent() {
    try {
      final Progress progress = taskmanPlugin.progress();
      progressPanel.removeAll();
      progressPanel.add(progressLabel);
      for (Map.Entry<String, TierProgress> entry : progress.getProgressByTier().entrySet()) {
        final String key = entry.getKey();
        final TierProgress value = entry.getValue();
        final ProgressBar progressBar = new ProgressBar();
        progressBar.setMaximumValue(value.getMaxValue());
        progressBar.setValue(value.getValue());
        progressBar.setRightLabel(String.valueOf(value.getMaxValue()));
        progressBar.setLeftLabel(String.valueOf(value.getValue()));
        int percentage = progressBar.getPercentage();
        progressBar.setCenterLabel(String.format("%s %d%%", key, percentage));
        progressBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        if (percentage == 0) {
          progressBar.setForeground(Color.RED);
        } else if (percentage < 25) {
          progressBar.setForeground(Color.decode("#ea6600"));
        } else if (percentage < 50) {
          progressBar.setForeground(Color.decode("#ffb600"));
        } else if (percentage < 75) {
          progressBar.setForeground(Color.decode("#ffe500"));
        } else if (percentage < 100) {
          progressBar.setForeground(Color.decode("#aeff00"));
        } else {
          progressBar.setForeground(Color.GREEN);
        }
        progressPanel.add(progressBar);
        progressPanel.setVisible(true);
      }
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

  private Icon getRefreshButton() {
    final BufferedImage image = ImageUtil.loadImageResource(getClass(), "refresh.png");
    final Image resizedImage = image.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
    return new ImageIcon(resizedImage);
  }

  public void reset() {
    taskPanel.setVisible(false);
    progressPanel.setVisible(false);
    getCurrentTaskAndUpdateContent();
    getProgressAndUpdateContent();
  }
}
