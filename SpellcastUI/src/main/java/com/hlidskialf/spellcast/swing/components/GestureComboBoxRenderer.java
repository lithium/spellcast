package com.hlidskialf.spellcast.swing.components;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;

/**
 * Created by wiggins on 1/15/15.
 */
public class GestureComboBoxRenderer extends BasicComboBoxRenderer {
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(48, 48);
    }
}

