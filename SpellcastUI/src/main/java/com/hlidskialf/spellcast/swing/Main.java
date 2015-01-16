package com.hlidskialf.spellcast.swing;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Created by wiggins on 1/11/15.
 */
public class Main {

    public static void main(final String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SpellcastForm.main(args);
            }

        });
    }

}
