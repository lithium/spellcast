package com.hlidskialf.spellcast.swing.components;

import com.hlidskialf.spellcast.swing.Hand;
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

    private ArrayList<String> leftGestures;
    private ArrayList<String> rightGestures;


    /* initialize */
    public GestureHistoryPanel()  {
        super();
        setLayout(new GridLayout(gestureHistorySize,2));

        leftGestureLabels = new JLabel[gestureHistorySize];
        rightGestureLabels = new JLabel[gestureHistorySize];

        leftGestures = new ArrayList<String>();
        rightGestures = new ArrayList<String>();

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
            leftGestureLabels[i].setIcon(Icons.Left.nothing);
            rightGestureLabels[i].setIcon(Icons.Right.nothing);
        }

    }

    @Override
    public Dimension getPreferredSize() {
        return preferredSize;
    }

    public void addGestures(String left, String right) {
        leftGestures.add(left.toUpperCase());
        rightGestures.add(right.toUpperCase());
        sync();
    }

    public void resetHistory() {
        leftGestures.clear();
        rightGestures.clear();
        sync();
    }

    public void sync() {
        int l = leftGestures.size()-1;
        for (int i=0; i < gestureHistorySize; i++) {
            if (l >= 0) {
                leftGestureLabels[i].setIcon(Icons.iconForGesture(Hand.Left, leftGestures.get(l).charAt(0)));
                rightGestureLabels[i].setIcon(Icons.iconForGesture(Hand.Right, rightGestures.get(l).charAt(0)));
                l -= 1;
            } else {
                leftGestureLabels[i].setIcon(Icons.Left.nothing);
                rightGestureLabels[i].setIcon(Icons.Right.nothing);
            }
        }
    }
}
