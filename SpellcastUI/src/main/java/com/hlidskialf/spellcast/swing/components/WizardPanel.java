package com.hlidskialf.spellcast.swing.components;

import com.hlidskialf.spellcast.swing.NameChangeListener;
import com.hlidskialf.spellcast.swing.Player;
import com.hlidskialf.spellcast.swing.components.GestureHistoryPanel;
import com.hlidskialf.spellcast.swing.components.PlayerStatusPanel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wiggins on 1/15/15.
 */
public class WizardPanel extends JPanel implements NameChangeListener {
    private JPanel monsterPanel;
    private PlayerStatusPanel statusPanel;
    private Player wizard;
    private GestureHistoryPanel gestureHistoryPanel;
    private Map<Player, PlayerStatusPanel> monsterStatuses;

    public WizardPanel(Player wizard) {
        super(new GridBagLayout());
        setupUI();

        monsterStatuses = new HashMap<Player, PlayerStatusPanel>();

        setWizard(wizard);
    }

    public void setWizard(Player wizard) {
        this.wizard = wizard;
        sync();
    }
    private void sync() {
        statusPanel.setPlayer(wizard);
        updateMonsters();
    }

    public void updateMonsters() {
        for (Player monster : wizard.getMonsters().values()) {
            PlayerStatusPanel monsterStatusPanel;
            if (monsterStatuses.containsKey(monster)) {
                monsterStatusPanel = monsterStatuses.get(monster);
            } else {
                monsterStatusPanel = new PlayerStatusPanel(monster);
                monsterStatusPanel.setFontSize(10);
                monsterPanel.add(monsterStatusPanel);
                monsterStatuses.put(monster, monsterStatusPanel);
            }
            monsterStatusPanel.sync();
        }
    }

    private void setupUI() {
        GridBagConstraints gbc;

        gestureHistoryPanel = new GestureHistoryPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.7;
        gbc.fill = GridBagConstraints.BOTH;
        add(gestureHistoryPanel, gbc);

        statusPanel = new PlayerStatusPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.7;
        gbc.anchor = GridBagConstraints.WEST;
        add(statusPanel, gbc);


        monsterPanel = new JPanel();
        monsterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        monsterPanel.setPreferredSize(new Dimension(96, -1));
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        add(monsterPanel, gbc);

    }

    @Override
    public void onNameChanged(String name, String gender) {
        wizard.setName(name);
        wizard.setGender(gender);
        sync();
    }
}
