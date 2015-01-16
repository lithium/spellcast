package com.hlidskialf.spellcast.swing.components;

import com.hlidskialf.spellcast.swing.Player;

import javax.swing.*;
import java.awt.*;

/**
 * Created by wiggins on 1/15/15.
 */
public class PlayerStatusPanel extends JPanel {
    private Player player;
    private JLabel nameLabel;
    private JProgressBar hpProgress;

    public PlayerStatusPanel(Player player) {
        super();
        this.player = player;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0; gbc.weighty = 1.0;

        Dimension d = new Dimension(80, 16);

        nameLabel = new JLabel(player.getName());
        nameLabel.setPreferredSize(d);
        gbc.gridx = 0; gbc.gridy = 0;
        add(nameLabel, gbc);

        hpProgress = new JProgressBar(0, player.getMaxHP());
        hpProgress.setPreferredSize(d);
        gbc.gridx = 0; gbc.gridy = 1;
        add(hpProgress, gbc);
    }

    public void sync() {
        nameLabel.setText(player.getName());
        hpProgress.setMaximum(player.getMaxHP());
        hpProgress.setValue(player.getCurrentHP());
        String tooltip = String.format("%s: %s/%s", player.getName(), player.getCurrentHP(), player.getMaxHP());
        setToolTipText(tooltip);
    }

    public void setFontSize(int size) {
        Font f = nameLabel.getFont();
        nameLabel.setFont(new Font(f.getName(), f.getStyle(), size));
    }


}
