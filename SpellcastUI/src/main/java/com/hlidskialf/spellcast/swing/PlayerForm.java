package com.hlidskialf.spellcast.swing;

import com.hlidskialf.spellcast.swing.components.GestureHistoryPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by wiggins on 1/15/15.
 */
public class PlayerForm {
    private JPanel contentPanel;
    private JLabel playerName;
    private JProgressBar hitPoints;
    private JPanel gesturePanel;
    private Player player;
    private GestureHistoryPanel gestureHistoryPanel;

    public PlayerForm(Player player) {
        setPlayer(player);

        gestureHistoryPanel = new GestureHistoryPanel();
        gesturePanel.add(gestureHistoryPanel);
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public void setPlayer(Player player) {
        this.player = player;
        playerName.setText(player.getName());
        updateHitpoints();
    }

    public void updateHitpoints() {
        hitPoints.setMaximum(player.getMaxHP());
        hitPoints.setValue(player.getCurrentHP());

    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        playerName = new JLabel();
        playerName.setText("Player the Wizard");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(playerName, gbc);
        hitPoints = new JProgressBar();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(hitPoints, gbc);
        gesturePanel = new JPanel();
        gesturePanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPanel.add(gesturePanel, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPanel;
    }
}
