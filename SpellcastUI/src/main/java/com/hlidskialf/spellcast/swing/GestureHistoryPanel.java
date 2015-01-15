package com.hlidskialf.spellcast.swing;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by wiggins on 1/14/15.
 */
public class GestureHistoryPanel extends JPanel {
    private ArrayList<JLabel> leftGestureLabels;
    private ArrayList<JLabel> rightGestureLabels;

    private final static int gestureHistorySize=8;
    private final static Dimension iconDimension = new Dimension(48,48);


    /* initialize */
    public GestureHistoryPanel()  {
        setLayout(new GridLayout(gestureHistorySize,2));

        leftGestureLabels = new ArrayList<JLabel>(gestureHistorySize);
        rightGestureLabels = new ArrayList<JLabel>(gestureHistorySize);

        for (int i=gestureHistorySize-1; i>=0; i--) {
            JLabel l = new JLabel();
            l.setPreferredSize(iconDimension); l.setMinimumSize(iconDimension); l.setMaximumSize(iconDimension);
            l.setText("l" + String.valueOf(i));
            leftGestureLabels.set(i, l);
            add(l);

            JLabel r = new JLabel();
            r.setPreferredSize(iconDimension); r.setMinimumSize(iconDimension); r.setMaximumSize(iconDimension);
            r.setText("r" + String.valueOf(i));
            rightGestureLabels.set(i, r);
            add(r);
        }
    }

}
