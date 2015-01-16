package com.hlidskialf.spellcast.swing;

import com.hlidskialf.spellcast.swing.components.GestureComboBoxRenderer;
import com.hlidskialf.spellcast.swing.components.WizardPanel;
import com.hlidskialf.spellcast.swing.dialogs.JoinGameDialog;
import com.hlidskialf.spellcast.swing.dialogs.ReferenceDialog;
import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import javax.swing.*;
import javax.swing.plaf.metal.MetalComboBoxUI;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.synth.SynthLookAndFeel;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by wiggins on 1/11/15.
 */
public class SpellcastForm {
    private static JFrame frame;

    private JPanel contentPanel;
    private JTextField chatInput;
    private JTextArea gameLog;
    private JButton sendButton;
    private JPanel playPanel;
    private JComboBox rightComboBox;
    private JComboBox leftComboBox;
    private Channel channel;
    private Player wizard;
    private WizardPanel wizardPanel;

    public SpellcastForm() {

        wizard = new Player("Player1", 30);
        wizard.setCurrentHP(8);
        wizard.addMonster(new Player("muglalook the goblin", 25));
        wizardPanel = new WizardPanel(wizard);

        playPanel.add(wizardPanel);
        playPanel.add(new WizardPanel(new Player("Player2")));


        leftComboBox.setModel(new Icons.IconComboBoxModel(Hand.Left));
        leftComboBox.setRenderer(new GestureComboBoxRenderer());
        leftComboBox.setUI(new MetalComboBoxUI());

        rightComboBox.setModel(new Icons.IconComboBoxModel(Hand.Right));
        rightComboBox.setRenderer(new GestureComboBoxRenderer());
        rightComboBox.setUI(new MetalComboBoxUI());


        playPanel.setBorder(BorderFactory.createLineBorder(Color.RED));


        chatInput.addKeyListener(
                new KeyAdapter() {
                    @Override
                    public void keyTyped(final KeyEvent keyEvent) {
                        if (keyEvent.getKeyChar() == KeyEvent.VK_ENTER) {
                            String txt = chatInput.getText().trim();
                            if (!txt.isEmpty()) {
                                say(txt);
                                chatInput.setText("");
                            }
                        }
                    }
                }
        );
    }

    public static void main(String[] args) {

        String appName = System.getProperty("appName");
        if (appName == null) {
            appName = "Spellcast";
        }

        // set up for mac menus
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", appName);

        try {
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (Exception E) {

        }

        SpellcastForm form = new SpellcastForm();

        frame = new JFrame(appName);
        frame.setContentPane(form.contentPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(form.buildMenuBar());


        Container container = frame.getContentPane();
        container.setPreferredSize(new Dimension(800, 700));
        frame.pack();
        frame.setVisible(true);
    }


    public JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("File");
        JMenuItem joinAction = new JMenuItem("Join Game");
        joinAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onJoinGame();
            }
        });
        file.add(joinAction);

        JMenuItem disconnectAction = new JMenuItem("Disconnect");
        disconnectAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onDisconnect();
            }
        });
        disconnectAction.setEnabled(false);
        file.add(disconnectAction);

        JMenuItem saveAction = new JMenuItem("Save log...");
        saveAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onSaveLog();
            }
        });
        file.add(saveAction);

        file.addSeparator();
        JMenuItem quitAction = new JMenuItem("Quit");
        quitAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onQuit();
            }
        });
        file.add(quitAction);
        menuBar.add(file);



        JMenu spells = new JMenu("Spells");
        JMenuItem referenceAction = new JMenuItem("Show Spells");
        referenceAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onShowSpells();
            }
        });
        spells.add(referenceAction);
        menuBar.add(spells);


        return menuBar;
    }

    private void onQuit() {
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    private void onJoinGame() {
        JoinGameDialog joinGameDialog = new JoinGameDialog(this);
        joinGameDialog.pack();
        joinGameDialog.setVisible(true);
    }

    private void onShowSpells() {
        ReferenceDialog.buildAndShow();
    }

    private void onDisconnect() {

    }

    private void onSaveLog() {

    }


    public void connectToServer(String hostname, int port) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(final SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new LineBasedFrameDecoder(512))
                        .addLast(new StringDecoder(CharsetUtil.UTF_8))
                        .addLast(new StringEncoder(CharsetUtil.UTF_8))
                        .addLast(new SpellcastClientHandler(SpellcastForm.this));
            }
        });
        b.connect(hostname, port).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture channelFuture) throws Exception {
                channel = channelFuture.channel();
                channel.closeFuture().addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(final ChannelFuture channelFuture) throws Exception {
                        channel = null;
                    }
                });
            }
        });
    }


    public void appendToLog(String line) {
        gameLog.append(line + "\n");
    }

    public void say(String message) {
        if (channel != null) {
            channel.writeAndFlush(message + "\r\n");
        }
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        gameLog = new JTextArea();
        gameLog.setEditable(false);
        gameLog.setLineWrap(true);
        gameLog.setRows(0);
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 10;
        contentPanel.add(gameLog, gbc);
        playPanel = new JPanel();
        playPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 5;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPanel.add(playPanel, gbc);
        rightComboBox = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(rightComboBox, gbc);
        sendButton = new JButton();
        sendButton.setText("Send");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(sendButton, gbc);
        chatInput = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(chatInput, gbc);
        leftComboBox = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(leftComboBox, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPanel;
    }
}
