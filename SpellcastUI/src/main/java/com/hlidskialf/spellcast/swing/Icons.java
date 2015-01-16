package com.hlidskialf.spellcast.swing;

import javax.swing.*;
import java.net.URL;

/**
 * Created by wiggins on 1/15/15.
 */
public class Icons {

    public static final ImageIcon clap = imageIcon("icon/clap.png", "Clap");
    public static final ImageIcon wave = imageIcon("icon/wave.png", "Wave");
    public static final ImageIcon snap = imageIcon("icon/snap.png", "Snap");
    public static final ImageIcon fingers = imageIcon("icon/fingers.png", "Fingers");
    public static final ImageIcon palm = imageIcon("icon/palm.png", "Palm");
    public static final ImageIcon digit = imageIcon("icon/digit.png", "Digit");
    public static final ImageIcon stab = imageIcon("icon/stab.png", "Stab");
    public static final ImageIcon nothing = imageIcon("icon/nothing.png", "Nothing");

    private static ImageIcon gesture(char gesture) {
        switch (gesture) {
            case 'C': return clap;
            case 'W': return wave;
            case 'S': return snap;
            case 'F': return fingers;
            case 'P': return palm;
            case 'D': return digit;
            case 'K': return stab;
            case '_': return nothing;
        }
        return null;
    }

    private static ImageIcon imageIcon(String path, String description) {
        URL url = Main.class.getClassLoader().getResource(path);
        return url == null ? null : new ImageIcon(url, description);
    }
}
