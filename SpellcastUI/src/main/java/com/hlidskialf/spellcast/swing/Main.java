package com.hlidskialf.spellcast.swing;

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
