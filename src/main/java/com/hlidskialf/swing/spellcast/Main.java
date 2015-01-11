package com.hlidskialf.swing.spellcast;

import javax.swing.*;

/**
 * Created by wiggins on 1/11/15.
 */
public class Main {

    private static void createAndShow() {
       JFrame frame = new JFrame("HelloSwingMain");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Hello Swing!");
        frame.getContentPane().add(label);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShow();
            }

        });
    }

}
