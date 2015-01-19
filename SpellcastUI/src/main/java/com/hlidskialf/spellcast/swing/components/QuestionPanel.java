package com.hlidskialf.spellcast.swing.components;

import com.hlidskialf.spellcast.swing.Hand;
import com.hlidskialf.spellcast.swing.Icons;

import javax.swing.*;
import javax.swing.plaf.metal.MetalComboBoxUI;

/**
 * Created by wiggins on 1/18/15.
 */
public class QuestionPanel extends JPanel {

    private JLabel label;
    private JComboBox comboBox;
    private Hand hand;
    private final QuestionType questionType;
    private String spellName;
    private DefaultComboBoxModel comboBoxModel;

    public enum QuestionType {
        Spell,
        Target
    };

    public QuestionPanel(Hand hand, QuestionType questionType) {
        this(hand,questionType,null);
    }
    public QuestionPanel(Hand hand, QuestionType questionType, String spellName) {
        super();
        setupUI();
        this.hand = hand;
        this.questionType = questionType;
        this.spellName = spellName;
        sync();
    }

    public void addOption(String option) {
        comboBoxModel.addElement(option);
    }

    public String getAnswer() {
        return (String)comboBox.getSelectedItem();
    }

    public Hand getHand() {
        return hand;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public String getSpellName() {
        return spellName;
    }

    public void setSpellName(String spellName) {
        this.spellName = spellName;
    }

    private void sync() {
        switch (this.questionType) {
            case Spell:
                label.setText("Which spell would you like to cast with your " + hand + "hand?");
                break;
            case Target:
                label.setText("Which target would you like to cast '"+spellName+"' at with your "+hand+" hand?");
                break;
        }
    }

    private void setupUI() {
        label = new JLabel();

        comboBoxModel = new DefaultComboBoxModel();
        comboBox = new JComboBox();
        comboBox.setModel(comboBoxModel);
        comboBox.setUI(new MetalComboBoxUI());

        add(label);
        add(comboBox);
    }




}

