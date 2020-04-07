// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.shared.log.impl;

import java.util.ArrayList;
import javax.swing.Box;
import java.util.Iterator;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.annotation.PostConstruct;
import java.awt.Component;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.ImageIcon;
import org.seamless.swing.logging.LogTableCellRenderer;
import org.seamless.swing.logging.LogMessage;
import javax.swing.table.TableModel;
import org.seamless.swing.logging.LogCategory;
import java.util.List;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.Icon;
import org.seamless.swing.Application;
import org.seamless.swing.logging.LogController;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JToolBar;
import org.seamless.swing.logging.LogTableModel;
import javax.swing.JTable;
import org.seamless.swing.logging.LogCategorySelector;
import org.fourthline.cling.support.shared.CenterWindow;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.fourthline.cling.support.shared.log.LogView;
import javax.swing.JPanel;

@Singleton
public class LogViewImpl extends JPanel implements LogView
{
    @Inject
    protected LogCategories logCategories;
    @Inject
    protected Event<CenterWindow> centerWindowEvent;
    protected LogCategorySelector logCategorySelector;
    protected JTable logTable;
    protected LogTableModel logTableModel;
    protected final JToolBar toolBar;
    protected final JButton configureButton;
    protected final JButton clearButton;
    protected final JButton copyButton;
    protected final JButton expandButton;
    protected final JButton pauseButton;
    protected final JLabel pauseLabel;
    protected final JComboBox expirationComboBox;
    protected Presenter presenter;
    
    public LogViewImpl() {
        this.toolBar = new JToolBar();
        this.configureButton = new JButton("Options...", Application.createImageIcon(LogController.class, "img/configure.png"));
        this.clearButton = new JButton("Clear Log", Application.createImageIcon(LogController.class, "img/removetext.png"));
        this.copyButton = new JButton("Copy", Application.createImageIcon(LogController.class, "img/copyclipboard.png"));
        this.expandButton = new JButton("Expand", Application.createImageIcon(LogController.class, "img/viewtext.png"));
        this.pauseButton = new JButton("Pause/Continue Log", Application.createImageIcon(LogController.class, "img/pause.png"));
        this.pauseLabel = new JLabel(" (Active)");
        this.expirationComboBox = new JComboBox((E[])LogController.Expiration.values());
    }
    
    @PostConstruct
    public void init() {
        this.setLayout(new BorderLayout());
        final LogController.Expiration defaultExpiration = this.getDefaultExpiration();
        this.logCategorySelector = new LogCategorySelector(this.logCategories);
        this.logTableModel = new LogTableModel(defaultExpiration.getSeconds());
        (this.logTable = new JTable(this.logTableModel)).setDefaultRenderer(LogMessage.class, new LogTableCellRenderer() {
            @Override
            protected ImageIcon getWarnErrorIcon() {
                return LogViewImpl.this.getWarnErrorIcon();
            }
            
            @Override
            protected ImageIcon getDebugIcon() {
                return LogViewImpl.this.getDebugIcon();
            }
            
            @Override
            protected ImageIcon getTraceIcon() {
                return LogViewImpl.this.getTraceIcon();
            }
            
            @Override
            protected ImageIcon getInfoIcon() {
                return LogViewImpl.this.getInfoIcon();
            }
        });
        this.logTable.setCellSelectionEnabled(false);
        this.logTable.setRowSelectionAllowed(true);
        this.logTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                if (e.getSource() == LogViewImpl.this.logTable.getSelectionModel()) {
                    final int[] rows = LogViewImpl.this.logTable.getSelectedRows();
                    if (rows == null || rows.length == 0) {
                        LogViewImpl.this.copyButton.setEnabled(false);
                        LogViewImpl.this.expandButton.setEnabled(false);
                    }
                    else if (rows.length == 1) {
                        LogViewImpl.this.copyButton.setEnabled(true);
                        final LogMessage msg = (LogMessage)LogViewImpl.this.logTableModel.getValueAt(rows[0], 0);
                        if (msg.getMessage().length() > LogViewImpl.this.getExpandMessageCharacterLimit()) {
                            LogViewImpl.this.expandButton.setEnabled(true);
                        }
                        else {
                            LogViewImpl.this.expandButton.setEnabled(false);
                        }
                    }
                    else {
                        LogViewImpl.this.copyButton.setEnabled(true);
                        LogViewImpl.this.expandButton.setEnabled(false);
                    }
                }
            }
        });
        this.adjustTableUI();
        this.initializeToolBar(defaultExpiration);
        this.setPreferredSize(new Dimension(250, 100));
        this.setMinimumSize(new Dimension(250, 50));
        this.add(new JScrollPane(this.logTable), "Center");
        this.add(this.toolBar, "South");
    }
    
    @Override
    public Component asUIComponent() {
        return this;
    }
    
    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;
    }
    
    @Override
    public void pushMessage(final LogMessage logMessage) {
        this.logTableModel.pushMessage(logMessage);
        if (!this.logTableModel.isPaused()) {
            this.logTable.scrollRectToVisible(this.logTable.getCellRect(this.logTableModel.getRowCount() - 1, 0, true));
        }
    }
    
    @Override
    public void dispose() {
        this.logCategorySelector.dispose();
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
        this.logTable.getColumnModel().getColumn(2).setMinWidth(110);
        this.logTable.getColumnModel().getColumn(2).setMaxWidth(250);
        this.logTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        this.logTable.getColumnModel().getColumn(3).setMaxWidth(400);
        this.logTable.getColumnModel().getColumn(4).setPreferredWidth(600);
    }
    
    protected void initializeToolBar(final LogController.Expiration expiration) {
        this.configureButton.setFocusable(false);
        this.configureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                LogViewImpl.this.centerWindowEvent.fire((Object)new CenterWindow(LogViewImpl.this.logCategorySelector));
                LogViewImpl.this.logCategorySelector.setVisible(!LogViewImpl.this.logCategorySelector.isVisible());
            }
        });
        this.clearButton.setFocusable(false);
        this.clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                LogViewImpl.this.logTableModel.clearMessages();
            }
        });
        this.copyButton.setFocusable(false);
        this.copyButton.setEnabled(false);
        this.copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final StringBuilder sb = new StringBuilder();
                final List<LogMessage> messages = LogViewImpl.this.getSelectedMessages();
                for (final LogMessage message : messages) {
                    sb.append(message.toString()).append("\n");
                }
                Application.copyToClipboard(sb.toString());
            }
        });
        this.expandButton.setFocusable(false);
        this.expandButton.setEnabled(false);
        this.expandButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final List<LogMessage> messages = LogViewImpl.this.getSelectedMessages();
                if (messages.size() != 1) {
                    return;
                }
                LogViewImpl.this.presenter.onExpand(messages.get(0));
            }
        });
        this.pauseButton.setFocusable(false);
        this.pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                LogViewImpl.this.logTableModel.setPaused(!LogViewImpl.this.logTableModel.isPaused());
                if (LogViewImpl.this.logTableModel.isPaused()) {
                    LogViewImpl.this.pauseLabel.setText(" (Paused)");
                }
                else {
                    LogViewImpl.this.pauseLabel.setText(" (Active)");
                }
            }
        });
        this.expirationComboBox.setSelectedItem(expiration);
        this.expirationComboBox.setMaximumSize(new Dimension(100, 32));
        this.expirationComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final JComboBox cb = (JComboBox)e.getSource();
                final LogController.Expiration expiration = (LogController.Expiration)cb.getSelectedItem();
                LogViewImpl.this.logTableModel.setMaxAgeSeconds(expiration.getSeconds());
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
    
    protected LogController.Expiration getDefaultExpiration() {
        return LogController.Expiration.SIXTY_SECONDS;
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
    
    protected int getExpandMessageCharacterLimit() {
        return 100;
    }
    
    protected List<LogMessage> getSelectedMessages() {
        final List<LogMessage> messages = new ArrayList<LogMessage>();
        for (final int row : this.logTable.getSelectedRows()) {
            messages.add((LogMessage)this.logTableModel.getValueAt(row, 0));
        }
        return messages;
    }
}
