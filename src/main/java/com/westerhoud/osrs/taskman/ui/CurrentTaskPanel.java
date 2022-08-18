package com.westerhoud.osrs.taskman.ui;

import com.westerhoud.osrs.taskman.domain.Task;

import javax.swing.*;
import java.awt.*;

public class CurrentTaskPanel extends JPanel {

    public CurrentTaskPanel(final Task task) {
        setLayout(new GridLayout(1, 2));
        setPreferredSize(new Dimension(300, 40));
        setMinimumSize(new Dimension(170, 20));

        add(new JLabel("Current task:"));
        add(new JLabel(task.getName()));
    }
}
