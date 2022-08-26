package com.westerhoud.osrs.taskman.ui;

import com.westerhoud.osrs.taskman.TaskmanPlugin;
import com.westerhoud.osrs.taskman.domain.Task;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.ColorJButton;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.ui.components.shadowlabel.JShadowedLabel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

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

    currentTaskLabel.setFont(FontManager.getRunescapeFont());
    currentTaskLabel.setForeground(Color.WHITE);
    nameLabel.setFont(FontManager.getRunescapeSmallFont());
    nameLabel.setHorizontalAlignment(SwingConstants.LEFT);

    final JPanel buttonPanel = new JPanel(new BorderLayout(5, 5));
    generateButton.setFont(FontManager.getRunescapeSmallFont());
    completeButton.setFont(FontManager.getRunescapeSmallFont());
    buttonPanel.add(generateButton, BorderLayout.WEST);
    buttonPanel.add(completeButton, BorderLayout.CENTER);

    taskPanel.add(currentTaskLabel, BorderLayout.NORTH);
    taskPanel.add(imageLabel, BorderLayout.WEST);
    taskPanel.add(nameLabel, BorderLayout.CENTER);
    taskPanel.add(buttonPanel, BorderLayout.SOUTH);


    errorPanel = new PluginErrorPanel();
    errorPanel.setVisible(false);

    updateTaskPanelContent();
    add(errorPanel, BorderLayout.CENTER);
    add(taskPanel, BorderLayout.CENTER);
  }

  private void updateTaskPanelContent() {
    try {
      errorPanel.setVisible(false);
      final Task currentTask = taskmanPlugin.getCurrentTask();
      imageLabel.setIcon(getTaskImage(currentTask));
      nameLabel.setText(currentTask.getName());
      taskPanel.setVisible(true);
    } catch (Exception e) {
      taskPanel.setVisible(false);
      errorPanel.setContent("Oops... Something went wrong", e.getMessage());
      errorPanel.setVisible(true);
    }
    repaint();
  }

  private Icon getTaskImage(final Task currentTask) throws IOException {
    BufferedImage myPicture = ImageIO.read(new URL(currentTask.getImageUrl()));
    return new ImageIcon(myPicture);
  }

  public void reset() {
    updateTaskPanelContent();
  }
}
