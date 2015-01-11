package com.hlidskialf.spellcast.swing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by wiggins on 1/11/15.
 */
public class SpellcastForm {
    private JPanel contentPanel;
    private JSplitPane splitPane;
    private JPanel leftPane;
    private JPanel rightPane;
    private JTextField chatInput;
    private JTextArea gameLog;

    public static void main(String[] args) {

        String appName = System.getProperty("appName");
        if (appName == null) {
            appName = "Spellcast";
        }

        // set up for mac menus
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", appName);

        SpellcastForm form = new SpellcastForm();

        JFrame frame = new JFrame(appName);
        frame.setContentPane(form.contentPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(form.buildMenuBar());
        frame.pack();
        frame.setVisible(true);
    }


    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("File");
        JMenuItem joinAction = new JMenuItem("Join Game");
        joinAction.addActionListener(joinGameListener);
        file.add(joinAction);

        JMenuItem disconnectAction = new JMenuItem("Disconnect");
        disconnectAction.addActionListener(disconnectListener);
        file.add(disconnectAction);

        JMenuItem saveAction = new JMenuItem("Save log...");
        saveAction.addActionListener(saveLogListener);
        file.add(saveAction);
        menuBar.add(file);

        return menuBar;
    }


    /*
     * Menu Action Listeners
     */
    ActionListener joinGameListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            System.out.println("join");

        }
    };
    ActionListener disconnectListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            System.out.println("disco");

        }
    };
    ActionListener saveLogListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            System.out.println("save");

        }
    };

}
