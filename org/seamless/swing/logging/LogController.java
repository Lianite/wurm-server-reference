// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.swing.logging;

import java.awt.Frame;
import javax.swing.Icon;
import java.util.ArrayList;
import javax.swing.Box;
import java.util.Iterator;
import java.awt.Window;
import org.seamless.swing.Application;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import java.awt.Component;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.table.TableModel;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.util.List;
import org.seamless.swing.Controller;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.JTable;
import javax.swing.JPanel;
import org.seamless.swing.AbstractController;

public abstract class LogController extends AbstractController<JPanel>
{
    private final LogCategorySelector logCategorySelector;
    private final JTable logTable;
    private final LogTableModel logTableModel;
    private final JToolBar toolBar;
    private final JButton configureButton;
    private final JButton clearButton;
    private final JButton copyButton;
    private final JButton expandButton;
    private final JButton pauseButton;
    private final JLabel pauseLabel;
    private final JComboBox expirationComboBox;
    
    public LogController(final Controller parentController, final List<LogCategory> logCategories) {
        this(parentController, Expiration.SIXTY_SECONDS, logCategories);
    }
    
    public LogController(final Controller parentController, final Expiration expiration, final List<LogCategory> logCategories) {
        super(new JPanel(new BorderLayout()), parentController);
        this.toolBar = new JToolBar();
        this.configureButton = this.createConfigureButton();
        this.clearButton = this.createClearButton();
        this.copyButton = this.createCopyButton();
        this.expandButton = this.createExpandButton();
        this.pauseButton = this.createPauseButton();
        this.pauseLabel = new JLabel(" (Active)");
        this.expirationComboBox = new JComboBox((E[])Expiration.values());
        this.logCategorySelector = new LogCategorySelector(logCategories);
        this.logTableModel = new LogTableModel(expiration.getSeconds());
        (this.logTable = new JTable(this.logTableModel)).setDefaultRenderer(LogMessage.class, new LogTableCellRenderer() {
            protected ImageIcon getWarnErrorIcon() {
                return LogController.this.getWarnErrorIcon();
            }
            
            protected ImageIcon getDebugIcon() {
                return LogController.this.getDebugIcon();
            }
            
            protected ImageIcon getTraceIcon() {
                return LogController.this.getTraceIcon();
            }
            
            protected ImageIcon getInfoIcon() {
                return LogController.this.getInfoIcon();
            }
        });
        this.logTable.setCellSelectionEnabled(false);
        this.logTable.setRowSelectionAllowed(true);
        this.logTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(final ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                if (e.getSource() == LogController.this.logTable.getSelectionModel()) {
                    final int[] rows = LogController.this.logTable.getSelectedRows();
                    if (rows == null || rows.length == 0) {
                        LogController.this.copyButton.setEnabled(false);
                        LogController.this.expandButton.setEnabled(false);
                    }
                    else if (rows.length == 1) {
                        LogController.this.copyButton.setEnabled(true);
                        final LogMessage msg = (LogMessage)LogController.this.logTableModel.getValueAt(rows[0], 0);
                        if (msg.getMessage().length() > LogController.this.getExpandMessageCharacterLimit()) {
                            LogController.this.expandButton.setEnabled(true);
                        }
                        else {
                            LogController.this.expandButton.setEnabled(false);
                        }
                    }
                    else {
                        LogController.this.copyButton.setEnabled(true);
                        LogController.this.expandButton.setEnabled(false);
                    }
                }
            }
        });
        this.adjustTableUI();
        this.initializeToolBar(expiration);
        this.getView().setPreferredSize(new Dimension(250, 100));
        this.getView().setMinimumSize(new Dimension(250, 50));
        this.getView().add(new JScrollPane(this.logTable), "Center");
        this.getView().add(this.toolBar, "South");
    }
    
    public void pushMessage(final LogMessage message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LogController.this.logTableModel.pushMessage(message);
                if (!LogController.this.logTableModel.isPaused()) {
                    LogController.this.logTable.scrollRectToVisible(LogController.this.logTable.getCellRect(LogController.this.logTableModel.getRowCount() - 1, 0, true));
                }
            }
        });
    }
    
    protected void adjustTableUI() {
        this.logTable.setFocusable(false);
        this.logTable.setRowHeight(18);
        this.logTable.getTableHeader().setReorderingAllowed(false);
        this.logTable.setBorder(BorderFactory.createEmptyBorder());
        this.logTable.getColumnModel().getColumn(0).setMinWidth(30);
        this.logTable.getColumnModel().getColumn(0).setMaxWidth(30);
        this.logTable.getColumnModel().getColumn(0).setResizable(false);
        this.logTable.getColumnModel().getColumn(1).setMinWidth(90);
        this.logTable.getColumnModel().getColumn(1).setMaxWidth(90);
        this.logTable.getColumnModel().getColumn(1).setResizable(false);
        this.logTable.getColumnModel().getColumn(2).setMinWidth(100);
        this.logTable.getColumnModel().getColumn(2).setMaxWidth(250);
        this.logTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        this.logTable.getColumnModel().getColumn(3).setMaxWidth(400);
        this.logTable.getColumnModel().getColumn(4).setPreferredWidth(600);
    }
    
    protected void initializeToolBar(final Expiration expiration) {
        this.configureButton.setFocusable(false);
        this.configureButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                Application.center(LogController.this.logCategorySelector, LogController.this.getParentWindow());
                LogController.this.logCategorySelector.setVisible(!LogController.this.logCategorySelector.isVisible());
            }
        });
        this.clearButton.setFocusable(false);
        this.clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                LogController.this.logTableModel.clearMessages();
            }
        });
        this.copyButton.setFocusable(false);
        this.copyButton.setEnabled(false);
        this.copyButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final StringBuilder sb = new StringBuilder();
                final List<LogMessage> messages = LogController.this.getSelectedMessages();
                for (final LogMessage message : messages) {
                    sb.append(message.toString()).append("\n");
                }
                Application.copyToClipboard(sb.toString());
            }
        });
        this.expandButton.setFocusable(false);
        this.expandButton.setEnabled(false);
        this.expandButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final List<LogMessage> messages = LogController.this.getSelectedMessages();
                if (messages.size() != 1) {
                    return;
                }
                LogController.this.expand(messages.get(0));
            }
        });
        this.pauseButton.setFocusable(false);
        this.pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                LogController.this.logTableModel.setPaused(!LogController.this.logTableModel.isPaused());
                if (LogController.this.logTableModel.isPaused()) {
                    LogController.this.pauseLabel.setText(" (Paused)");
                }
                else {
                    LogController.this.pauseLabel.setText(" (Active)");
                }
            }
        });
        this.expirationComboBox.setSelectedItem(expiration);
        this.expirationComboBox.setMaximumSize(new Dimension(100, 32));
        this.expirationComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final JComboBox cb = (JComboBox)e.getSource();
                final Expiration expiration = (Expiration)cb.getSelectedItem();
                LogController.this.logTableModel.setMaxAgeSeconds(expiration.getSeconds());
            }
        });
        this.toolBar.setFloatable(false);
        this.toolBar.add(this.copyButton);
        this.toolBar.add(this.expandButton);
        this.toolBar.add(Box.createHorizontalGlue());
        this.toolBar.add(this.configureButton);
        this.toolBar.add(this.clearButton);
        this.toolBar.add(this.pauseButton);
        this.toolBar.add(this.pauseLabel);
        this.toolBar.add(Box.createHorizontalGlue());
        this.toolBar.add(new JLabel("Clear after:"));
        this.toolBar.add(this.expirationComboBox);
    }
    
    protected List<LogMessage> getSelectedMessages() {
        final List<LogMessage> messages = new ArrayList<LogMessage>();
        for (final int row : this.logTable.getSelectedRows()) {
            messages.add((LogMessage)this.logTableModel.getValueAt(row, 0));
        }
        return messages;
    }
    
    protected int getExpandMessageCharacterLimit() {
        return 100;
    }
    
    public LogTableModel getLogTableModel() {
        return this.logTableModel;
    }
    
    protected JButton createConfigureButton() {
        return new JButton("Options...", Application.createImageIcon(LogController.class, "img/configure.png"));
    }
    
    protected JButton createClearButton() {
        return new JButton("Clear Log", Application.createImageIcon(LogController.class, "img/removetext.png"));
    }
    
    protected JButton createCopyButton() {
        return new JButton("Copy", Application.createImageIcon(LogController.class, "img/copyclipboard.png"));
    }
    
    protected JButton createExpandButton() {
        return new JButton("Expand", Application.createImageIcon(LogController.class, "img/viewtext.png"));
    }
    
    protected JButton createPauseButton() {
        return new JButton("Pause/Continue Log", Application.createImageIcon(LogController.class, "img/pause.png"));
    }
    
    protected ImageIcon getWarnErrorIcon() {
        return Application.createImageIcon(LogController.class, "img/warn.png");
    }
    
    protected ImageIcon getDebugIcon() {
        return Application.createImageIcon(LogController.class, "img/debug.png");
    }
    
    protected ImageIcon getTraceIcon() {
        return Application.createImageIcon(LogController.class, "img/trace.png");
    }
    
    protected ImageIcon getInfoIcon() {
        return Application.createImageIcon(LogController.class, "img/info.png");
    }
    
    protected abstract void expand(final LogMessage p0);
    
    protected abstract Frame getParentWindow();
    
    public enum Expiration
    {
        TEN_SECONDS(10, "10 Seconds"), 
        SIXTY_SECONDS(60, "60 Seconds"), 
        FIVE_MINUTES(300, "5 Minutes"), 
        NEVER(Integer.MAX_VALUE, "Never");
        
        private int seconds;
        private String label;
        
        private Expiration(final int seconds, final String label) {
            this.seconds = seconds;
            this.label = label;
        }
        
        public int getSeconds() {
            return this.seconds;
        }
        
        public String getLabel() {
            return this.label;
        }
        
        public String toString() {
            return this.getLabel();
        }
    }
}
