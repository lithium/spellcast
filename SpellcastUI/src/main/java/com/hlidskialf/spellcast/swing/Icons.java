package com.hlidskialf.spellcast.swing;

import com.hlidskialf.spellcast.swing.components.GestureImageIcon;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Created by wiggins on 1/15/15.
 */
public class Icons {

    public static class Left {
        public static final ImageIcon clap = GestureImageIcon.fromResource(Hand.Left, "icon/clap.png", "Clap");
        public static final ImageIcon wave = GestureImageIcon.fromResource(Hand.Left, "icon/wave.png", "Wave");
        public static final ImageIcon snap = GestureImageIcon.fromResource(Hand.Left, "icon/snap.png", "Snap");
        public static final ImageIcon fingers = GestureImageIcon.fromResource(Hand.Left, "icon/fingers.png", "Fingers");
        public static final ImageIcon palm = GestureImageIcon.fromResource(Hand.Left, "icon/palm.png", "Palm");
        public static final ImageIcon digit = GestureImageIcon.fromResource(Hand.Left, "icon/digit.png", "Digit");
        public static final ImageIcon stab = GestureImageIcon.fromResource(Hand.Left, "icon/stab.png", "Stab");
        public static final ImageIcon nothing = GestureImageIcon.fromResource(Hand.Left, "icon/nothing.png", "Nothing");
        public static final Object[] all = { clap, wave, snap, fingers, palm, digit, stab, nothing };
    }
    public static class Right {
        public static final ImageIcon clap = GestureImageIcon.fromResource(Hand.Right, "icon/clap.png", "Clap");
        public static final ImageIcon wave = GestureImageIcon.fromResource(Hand.Right, "icon/wave.png", "Wave");
        public static final ImageIcon snap = GestureImageIcon.fromResource(Hand.Right, "icon/snap.png", "Snap");
        public static final ImageIcon fingers = GestureImageIcon.fromResource(Hand.Right, "icon/fingers.png", "Fingers");
        public static final ImageIcon palm = GestureImageIcon.fromResource(Hand.Right, "icon/palm.png", "Palm");
        public static final ImageIcon digit = GestureImageIcon.fromResource(Hand.Right, "icon/digit.png", "Digit");
        public static final ImageIcon stab = GestureImageIcon.fromResource(Hand.Right, "icon/stab.png", "Stab");
        public static final ImageIcon nothing = GestureImageIcon.fromResource(Hand.Right, "icon/nothing.png", "Nothing");
        public static final Object[] all = { clap, wave, snap, fingers, palm, digit, stab, nothing };
    }


    public static final Dimension dimension = new Dimension(48,48);

    public static class IconComboBoxModel extends AbstractListModel implements ComboBoxModel {

        private ImageIcon selected;
        private Hand hand;

        public IconComboBoxModel(Hand hand) {
            this.hand = hand;
        }

        @Override
        public void setSelectedItem(Object o) {
            selected = (ImageIcon)o;
        }

        @Override
        public Object getSelectedItem() {
            return selected;
        }

        @Override
        public int getSize() {
            return icons().length;
        }

        @Override
        public Object getElementAt(int i) {
            return icons()[i];
        }

        private Object[] icons() {
            return hand == Hand.Left ? Icons.Left.all : Icons.Right.all;
        }
    }

//    private static ImageIcon gesture(char gesture) {
//        switch (gesture) {
//            case 'C': return clap;
//            case 'W': return wave;
//            case 'S': return snap;
//            case 'F': return fingers;
//            case 'P': return palm;
//            case 'D': return digit;
//            case 'K': return stab;
//            case '_': return nothing;
//        }
//        return null;
//    }

//    private static ImageIcon imageIcon(String path, String description) {
//        URL url = Main.class.getClassLoader().getResource(path);
//        return url == null ? null : new ImageIcon(url, description);
//    }
}
