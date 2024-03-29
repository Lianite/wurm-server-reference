// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.tools;

import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.TextArea;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JComboBox;
import java.awt.BorderLayout;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.WindowListener;
import java.awt.event.KeyListener;
import javax.swing.JFrame;

public class ItemModeller extends JFrame implements KeyListener, WindowListener
{
    private static final long serialVersionUID = 1008389608509176516L;
    private JPanel southHackPanel;
    private JLabel ipAdressLabel;
    private JProgressBar hackProgressBar;
    private FlowLayout flowLayout1;
    private JPanel textAreaPanel;
    private JTextField ipAdressTextField;
    private BorderLayout borderLayout1;
    private JComboBox<?> portComboBox;
    private JToggleButton hackButton;
    private JComboBox<?> hackComboBox;
    private JTextField inputTextField;
    private JPanel ipAdressPanel;
    private JButton pingButton;
    private JButton scanButton;
    private TextArea messageTextArea;
    private TextArea codeTextArea;
    
    public ItemModeller() {
        super("Wurm Item Modeller");
        this.southHackPanel = new JPanel();
        this.ipAdressLabel = new JLabel();
        this.hackProgressBar = new JProgressBar();
        this.flowLayout1 = new FlowLayout();
        this.textAreaPanel = new JPanel();
        this.ipAdressTextField = new JTextField();
        this.borderLayout1 = new BorderLayout();
        this.portComboBox = new JComboBox<Object>();
        this.hackButton = new JToggleButton();
        this.hackComboBox = new JComboBox<Object>();
        this.inputTextField = new JTextField();
        this.ipAdressPanel = new JPanel();
        this.pingButton = new JButton();
        this.scanButton = new JButton();
        this.messageTextArea = new TextArea();
        this.codeTextArea = new TextArea();
        this.addMessage("Welcome to wurm item modeller.");
        try {
            this.jbInit();
            this.setBounds(0, 0, 1000, 700);
            this.setVisible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.addWindowListener(this);
    }
    
    private void jbInit() throws Exception {
        this.ipAdressLabel.setText("Create new:");
        this.ipAdressLabel.setVerticalAlignment(1);
        this.ipAdressLabel.setVerticalTextPosition(1);
        this.southHackPanel.setLayout(this.flowLayout1);
        this.ipAdressTextField.setMinimumSize(new Dimension(70, 21));
        this.ipAdressTextField.setPreferredSize(new Dimension(170, 21));
        this.ipAdressTextField.setText("");
        this.ipAdressTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                ItemModeller.this.ipAdressTextField_actionPerformed(e);
            }
        });
        this.textAreaPanel.setLayout(this.borderLayout1);
        this.hackButton.setText("Load");
        this.hackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                ItemModeller.this.hackButton_actionPerformed(e);
            }
        });
        this.hackProgressBar.setMaximum(100);
        this.hackProgressBar.setMinimum(0);
        this.textAreaPanel.setMinimumSize(new Dimension(200, 500));
        this.textAreaPanel.setPreferredSize(new Dimension(250, 500));
        this.textAreaPanel.setToolTipText("");
        this.inputTextField.setText("");
        this.inputTextField.setHorizontalAlignment(0);
        this.inputTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                ItemModeller.this.inputTextField_actionPerformed(e);
            }
        });
        this.pingButton.setText("Save");
        this.pingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                ItemModeller.this.pingButton_actionPerformed(e);
            }
        });
        this.scanButton.setToolTipText("");
        this.scanButton.setText("Load");
        this.scanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                ItemModeller.this.scanButton_actionPerformed(e);
            }
        });
        this.hackComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                ItemModeller.this.hackComboBox_actionPerformed(e);
            }
        });
        this.portComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                ItemModeller.this.portComboBox_actionPerformed(e);
            }
        });
        this.textAreaPanel.add(this.inputTextField, "South");
        this.textAreaPanel.add(this.messageTextArea, "Center");
        this.getContentPane().add(this.ipAdressPanel, "North");
        this.ipAdressPanel.add(this.ipAdressLabel, null);
        this.ipAdressPanel.add(this.ipAdressTextField, null);
        this.ipAdressPanel.add(this.pingButton, null);
        this.ipAdressPanel.add(this.scanButton, null);
        this.getContentPane().add(this.southHackPanel, "South");
        final String[] data = { "" };
        this.portComboBox = new JComboBox<Object>((Object[])data);
        this.southHackPanel.add(this.hackComboBox, null);
        this.southHackPanel.add(this.portComboBox, null);
        this.southHackPanel.add(this.hackButton, null);
        this.southHackPanel.add(this.hackProgressBar, null);
        this.getContentPane().add(this.codeTextArea, "Center");
        this.getContentPane().add(this.textAreaPanel, "East");
        this.addMessage("Read all about it here.");
    }
    
    void hackButton_actionPerformed(final ActionEvent e) {
    }
    
    void inputTextField_actionPerformed(final ActionEvent e) {
        this.inputTextField.setText("");
    }
    
    void ipAdressTextField_actionPerformed(final ActionEvent e) {
    }
    
    void pingButton_actionPerformed(final ActionEvent e) {
        this.ipAdressTextField.setBackground(Color.white);
    }
    
    void scanButton_actionPerformed(final ActionEvent e) {
    }
    
    void remoteFileSystem_actionPerformed(final ActionEvent e) {
    }
    
    void localFileSystem_actionPerformed(final ActionEvent e) {
        this.addMessage("Doing something with the local window.");
    }
    
    void hackComboBox_actionPerformed(final ActionEvent e) {
    }
    
    void portComboBox_actionPerformed(final ActionEvent e) {
    }
    
    @Override
    public void windowDeactivated(final WindowEvent e) {
    }
    
    @Override
    public void windowActivated(final WindowEvent e) {
    }
    
    @Override
    public void windowDeiconified(final WindowEvent e) {
    }
    
    @Override
    public void windowIconified(final WindowEvent e) {
    }
    
    @Override
    public void windowClosed(final WindowEvent e) {
    }
    
    @Override
    public void windowClosing(final WindowEvent e) {
        this.shutDown();
    }
    
    @Override
    public void windowOpened(final WindowEvent e) {
    }
    
    public void addMessage(final String message) {
        if (message.endsWith("\n")) {
            this.messageTextArea.append(message);
        }
        else {
            this.messageTextArea.append(message + "\n");
        }
    }
    
    private void shutDown() {
        System.exit(0);
    }
    
    @Override
    public void keyReleased(final KeyEvent e) {
    }
    
    @Override
    public void keyTyped(final KeyEvent e) {
    }
    
    @Override
    public synchronized void keyPressed(final KeyEvent e) {
        if (e.getKeyCode() == 27) {
            this.shutDown();
        }
    }
    
    public static void main(final String[] args) {
    }
}
