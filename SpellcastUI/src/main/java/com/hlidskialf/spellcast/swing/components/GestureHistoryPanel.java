package com.hlidskialf.spellcast.swing.components;

import com.hlidskialf.spellcast.swing.Icons;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by wiggins on 1/14/15.
 */
public class GestureHistoryPanel extends JPanel {
    private JLabel leftGestureLabels[];
    private JLabel rightGestureLabels[];

    private final static int gestureHistorySize=8;
    private final static Dimension iconDimension = new Dimension(48,48);
    private static final Dimension preferredSize = new Dimension(100,400);


    /* initialize */
    public GestureHistoryPanel()  {
        super();
        setLayout(new GridLayout(gestureHistorySize,2));

        leftGestureLabels = new JLabel[gestureHistorySize];
        rightGestureLabels = new JLabel[gestureHistorySize];

        for (int i=gestureHistorySize-1; i>=0; i--) {
            JLabel l = new JLabel();
            l.setPreferredSize(iconDimension); l.setMinimumSize(iconDimension); l.setMaximumSize(iconDimension);
            leftGestureLabels[i] = l;
            add(l);

            JLabel r = new JLabel();
            r.setPreferredSize(iconDimension); r.setMinimumSize(iconDimension); r.setMaximumSize(iconDimension);
            rightGestureLabels[i] = r;
            add(r);
        }

        for (int i=0; i <gestureHistorySize; i++) {
            leftGestureLabels[i].setIcon(Icons.Left.clap);
            rightGestureLabels[i].setIcon(Icons.Right.clap);
        }

    }

    @Override
    public Dimension getPreferredSize() {
        return preferredSize;
    }
}
