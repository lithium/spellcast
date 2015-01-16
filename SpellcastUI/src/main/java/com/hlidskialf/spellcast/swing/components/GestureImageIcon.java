package com.hlidskialf.spellcast.swing.components;

import com.hlidskialf.spellcast.swing.Hand;
import com.hlidskialf.spellcast.swing.Main;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Created by wiggins on 1/15/15.
 */
public class GestureImageIcon extends ImageIcon {

    private Hand hand;



    public GestureImageIcon(Hand hand, URL url, String description) {
        super(url, description);
        this.hand = hand;
    }

    public static GestureImageIcon fromResource(Hand hand, String path, String description) {
        URL url = GestureImageIcon.class.getClassLoader().getResource(path);
        return url == null ? null : new GestureImageIcon(hand, url, description);
    }

    @Override
    public synchronized void paintIcon(Component component, Graphics graphics, int x, int y) {
        if (hand == Hand.Left) {
            Graphics2D g = (Graphics2D)graphics.create();
            g.translate(getIconWidth(), 0);
            g.scale(-1, 1);
            super.paintIcon(component, g, x, y);
        } else {
            super.paintIcon(component, graphics, x, y);

        }
    }
}
