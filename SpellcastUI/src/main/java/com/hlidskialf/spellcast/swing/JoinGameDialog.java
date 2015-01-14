package com.hlidskialf.spellcast.swing;

import javax.swing.*;
import java.awt.event.*;

public class JoinGameDialog extends JDialog {
	private final SpellcastForm parentForm;
	private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField wizardName;
    private JTextField serverHostname;
    private JRadioButton unspecifiedRadioButton;
    private JRadioButton femaleRadioButton;
    private JRadioButton maleRadioButton;
    private JTextField serverPort;


    private final static String GenderMale = "male";
    private final static String GenderFemale = "female";
    private final static String GenderUnspecified = "none";

    public JoinGameDialog(SpellcastForm parentForm) {
	    this.parentForm = parentForm;
	    setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        String hostname = serverHostname.getText();
        int port = Integer.valueOf(serverPort.getText());

        String nickname = wizardName.getText();
        if (nickname.trim().equals("")) {
            nickname = "Player1";
        }
        String gender = selectedGender();

		parentForm.connectToServer(hostname, port);

        dispose();
    }

    private void onCancel() {
        dispose();
    }

    private String selectedGender() {
        if (femaleRadioButton.isSelected()) {
            return GenderFemale;
        }
        if (maleRadioButton.isSelected()) {
            return GenderMale;
        }
        return GenderUnspecified;

    }
}
