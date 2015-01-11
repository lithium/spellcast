package com.hlidskialf.spellcast.swing;

import javax.swing.*;

/**
 * Created by wiggins on 1/11/15.
 */
public class SpellcastForm {
    private JPanel contentPanel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("SpellcastForm");
        frame.setContentPane(new SpellcastForm().contentPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
