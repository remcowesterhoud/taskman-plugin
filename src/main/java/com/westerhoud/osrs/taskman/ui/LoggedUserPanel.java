package com.westerhoud.osrs.taskman.ui;

import javax.swing.*;
import java.awt.*;

public class LoggedUserPanel extends JPanel {

    public LoggedUserPanel(final String username) {
        setLayout(new GridLayout(1, 2));
        setPreferredSize(new Dimension(300, 40));
        setMinimumSize(new Dimension(170, 20));

        final JLabel label = new JLabel("Logged in as:");
        add(label);
        final JLabel usernameLabel = new JLabel(username);
        add(usernameLabel);
    }
}
