package com.hlidskialf.spellcast.swing;

import com.hlidskialf.spellcast.swing.components.GestureComboBoxRenderer;
import com.hlidskialf.spellcast.swing.components.WizardPanel;
import com.hlidskialf.spellcast.swing.dialogs.ChangeNameDialog;
import com.hlidskialf.spellcast.swing.dialogs.JoinGameDialog;
import com.hlidskialf.spellcast.swing.dialogs.ReferenceDialog;
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
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wiggins on 1/11/15.
 */
public class SpellcastForm implements NameChangeListener, SpellcastMessage.MessageListener {
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
    private JMenuItem joinMenuItem;
    private JMenuItem disconnectMenuItem;
    private Map<Player, WizardPanel> opponentPanels;
    private Map<String, Player> opponents;

    public SpellcastForm() {

        opponentPanels = new HashMap<Player, WizardPanel>();
        opponents = new HashMap<String, Player>();

        wizard = new Player("player1");
        wizardPanel = new WizardPanel(wizard);

        playPanel.add(wizardPanel);


        leftComboBox.setModel(new Icons.IconComboBoxModel(Hand.Left));
        leftComboBox.setRenderer(new GestureComboBoxRenderer());
        leftComboBox.setUI(new MetalComboBoxUI());

        rightComboBox.setModel(new Icons.IconComboBoxModel(Hand.Right));
        rightComboBox.setRenderer(new GestureComboBoxRenderer());
        rightComboBox.setUI(new MetalComboBoxUI());

        DefaultCaret caret = (DefaultCaret) gameLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);


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
        container.setPreferredSize(new Dimension(900, 700));
        frame.pack();
        frame.setVisible(true);
    }


    public JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("Game");

        JMenuItem changeNameItem = new JMenuItem("Change Name");
        changeNameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onChangeName();
            }
        });
        file.add(changeNameItem);

        joinMenuItem = new JMenuItem("Join Game");
        joinMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onJoinGame();
            }
        });
        file.add(joinMenuItem);

        disconnectMenuItem = new JMenuItem("Disconnect");
        disconnectMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onDisconnect();
            }
        });
        disconnectMenuItem.setEnabled(false);
        file.add(disconnectMenuItem);

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
        JMenuItem referenceAction = new JMenuItem("Spell list");
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

    private void onChangeName() {
        ChangeNameDialog changeNameDialog = new ChangeNameDialog(this);
        changeNameDialog.pack();
        changeNameDialog.setPlayer(wizard);
        changeNameDialog.setVisible(true);

    }

    private void onJoinGame() {
        JoinGameDialog joinGameDialog = new JoinGameDialog(this);
        joinGameDialog.pack();
        joinGameDialog.setPlayer(wizard);
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

                joinMenuItem.setEnabled(false);
                disconnectMenuItem.setEnabled(true);
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

    private Player playerByNickname(String nickname) {
        if (nickname == null) {
            return null;
        }
        if (nickname.equals(wizard.getNickname())) {
            return wizard;
        }
        if (opponents.containsKey(nickname)) {
            return opponents.get(nickname);
        }
        Player newPlayer = new Player(nickname);
        opponents.put(nickname, newPlayer);
        return newPlayer;
    }

    private WizardPanel wizardPanelForPlayer(Player player) {
        if (player == null) {
            return null;
        }
        if (player.equals(wizard)) {
            return wizardPanel;
        }
        if (opponentPanels.containsKey(player)) {
            return opponentPanels.get(player);
        }

        //no panel yet
        WizardPanel panel = new WizardPanel(player);
        opponentPanels.put(player, panel);
        playPanel.add(panel);
        return panel;
    }

    @Override
    public void onNameChanged(String name, String gender) {
        wizard.setName(name);
        wizard.setGender(gender);
        wizardPanel.onNameChanged(wizard.getName(), wizard.getGender());
        if (channel != null) { // connected so tell server our new visible name
            channel.writeAndFlush("NAME " + wizard.getGender() + " :" + wizard.getName() + "\r\n");
        } else {
            wizard.setNickname(name.toLowerCase().replace("\\W", "")); // disconnected so update nickname
        }
    }



    /*
     * Spellcast Messages
     */

    @Override
    public void onWelcome(SpellcastChannel channel, String[] message) {
        channel.writeMessage("NAME " + wizard.getNickname() + " " + wizard.getGender() + " :" + wizard.getName());
    }

    @Override
    public void onHello(SpellcastChannel channel, String[] message) {
        wizard.setNickname(message[2]);

    }

    @Override
    public void onWizardStatus(SpellcastChannel channel, String[] message) {
        // 301 <nick> <hp/maxhp | - | +> <gender> <real name>

        Player player = playerByNickname(message[1]);
        player.setGender(message[3]);
        player.setName(message[4]);

        if ("-".equals(message[2]) || "+".equals(message[2])) {
            //observing

        } else {
            String[] hp = message[2].split("/");
            player.setMaxHP(Integer.valueOf(hp[1]));
            player.setCurrentHP(Integer.valueOf(hp[0]));
        }
        WizardPanel panel = wizardPanelForPlayer(player);
        panel.sync();
    }

    @Override
    public void onMonsterStatus(SpellcastChannel channel, String[] message) {
        // 311 <nickname>+<monsterid> <hp/maxhp> :monster name

    }

    @Override
    public void errorNicknameInUse(SpellcastChannel channel, String[] message) {
        String nick = wizard.getNickname();
        int idx = new Random().nextInt(nick.length());
        String newnick = nick.substring(idx) + nick.substring(0, idx) + idx;
        wizard.setNickname(newnick);
        wizard.setName(newnick);
        wizardPanel.onNameChanged(wizard.getName(), wizard.getGender());

        //retry with new nickname
        onWelcome(channel, message);
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
        playPanel = new JPanel();
        playPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        GridBagConstraints gbc;
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
        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPanel.add(scrollPane1, gbc);
        gameLog = new JTextArea();
        gameLog.setColumns(80);
        gameLog.setEditable(false);
        gameLog.setLineWrap(true);
        gameLog.setRows(0);
        scrollPane1.setViewportView(gameLog);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPanel;
    }






/*====================================================================================================================*/


}
