package com.hlidskialf.spellcast.swing;

import com.hlidskialf.spellcast.swing.components.GestureImageIcon;

import javax.swing.*;
import java.awt.*;

/**
 * Created by wiggins on 1/15/15.
 */
public class Icons {

    public static class Left {
        public static final ImageIcon clap = GestureImageIcon.fromResource(Hand.left, "icon/clap.png", "Clap");
        public static final ImageIcon wave = GestureImageIcon.fromResource(Hand.left, "icon/wave.png", "Wave");
        public static final ImageIcon snap = GestureImageIcon.fromResource(Hand.left, "icon/snap.png", "Snap");
        public static final ImageIcon fingers = GestureImageIcon.fromResource(Hand.left, "icon/fingers.png", "Fingers");
        public static final ImageIcon palm = GestureImageIcon.fromResource(Hand.left, "icon/palm.png", "Palm");
        public static final ImageIcon digit = GestureImageIcon.fromResource(Hand.left, "icon/digit.png", "Digit");
        public static final ImageIcon stab = GestureImageIcon.fromResource(Hand.left, "icon/stab.png", "Knife");
        public static final ImageIcon nothing = GestureImageIcon.fromResource(Hand.left, "icon/nothing.png", "_nothing");
        public static final Object[] all = { clap, wave, snap, fingers, palm, digit, stab, nothing };
    }
    public static class Right {
        public static final ImageIcon clap = GestureImageIcon.fromResource(Hand.right, "icon/clap.png", "Clap");
        public static final ImageIcon wave = GestureImageIcon.fromResource(Hand.right, "icon/wave.png", "Wave");
        public static final ImageIcon snap = GestureImageIcon.fromResource(Hand.right, "icon/snap.png", "Snap");
        public static final ImageIcon fingers = GestureImageIcon.fromResource(Hand.right, "icon/fingers.png", "Fingers");
        public static final ImageIcon palm = GestureImageIcon.fromResource(Hand.right, "icon/palm.png", "Palm");
        public static final ImageIcon digit = GestureImageIcon.fromResource(Hand.right, "icon/digit.png", "Digit");
        public static final ImageIcon stab = GestureImageIcon.fromResource(Hand.right, "icon/stab.png", "Knife");
        public static final ImageIcon nothing = GestureImageIcon.fromResource(Hand.right, "icon/nothing.png", "_nothing");
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
            return hand == Hand.left ? Icons.Left.all : Icons.Right.all;
        }
    }

    public static ImageIcon iconForGesture(Hand hand, char gesture) {
        if (hand == Hand.left) {
            switch (gesture) {
                case 'C': return Left.clap;
                case 'W': return Left.wave;
                case 'S': return Left.snap;
                case 'F': return Left.fingers;
                case 'P': return Left.palm;
                case 'D': return Left.digit;
                case 'K': return Left.stab;
                case '_': return Left.nothing;
            }
        }
        else if (hand == Hand.right) {
            switch (gesture) {
                case 'C': return Right.clap;
                case 'W': return Right.wave;
                case 'S': return Right.snap;
                case 'F': return Right.fingers;
                case 'P': return Right.palm;
                case 'D': return Right.digit;
                case 'K': return Right.stab;
                case '_': return Right.nothing;
            }
        }
        return null;
    }

//    private static ImageIcon imageIcon(String path, String description) {
//        URL url = Main.class.getClassLoader().getResource(path);
//        return url == null ? null : new ImageIcon(url, description);
//    }
}
