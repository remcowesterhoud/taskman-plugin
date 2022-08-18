package com.westerhoud.osrs.taskman.ui;

import javax.swing.*;
import java.awt.*;

public class ErrorPanel extends JPanel {

    public ErrorPanel(final String error) {
        setLayout(new GridLayout(1,1));
        setPreferredSize(new Dimension(300, 100));
        setMinimumSize(new Dimension(170, 20));

        final JTextArea errorField = new JTextArea();
        errorField.setEditable(false);
        errorField.setText(error);
        errorField.setForeground(Color.RED);
        errorField.setLineWrap(true);
        errorField.setWrapStyleWord(true);
        add(errorField);
    }
}
