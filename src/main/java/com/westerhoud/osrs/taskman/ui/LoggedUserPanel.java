package com.westerhoud.osrs.taskman.ui;

import com.westerhoud.osrs.taskman.domain.Account;

import javax.swing.*;
import java.awt.*;

public class LoggedUserPanel extends JPanel {

    public LoggedUserPanel(final Account account) {
        setLayout(new GridLayout(2, 2));
        setPreferredSize(new Dimension(300, 40));
        setMinimumSize(new Dimension(170, 20));

        final JLabel usernameLabel = new JLabel("Logged in as:");
        final JLabel usernameText = new JLabel(account.getUsername());
        add(usernameLabel);
        add(usernameText);

        final JLabel tierLabel = new JLabel("Current tier:");
        final JLabel tierText = new JLabel(account.getTier());
        add(tierLabel);
        add(tierText);
    }
}
