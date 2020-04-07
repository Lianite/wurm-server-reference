// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.swing.logging;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JToolBar;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.util.Iterator;
import java.awt.Dimension;
import java.awt.Component;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import java.awt.LayoutManager;
import java.awt.Container;
import javax.swing.BoxLayout;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JDialog;

public class LogCategorySelector extends JDialog
{
    protected final JPanel mainPanel;
    
    public LogCategorySelector(final List<LogCategory> logCategories) {
        this.mainPanel = new JPanel();
        this.setTitle("Select logging categories...");
        this.mainPanel.setLayout(new BoxLayout(this.mainPanel, 1));
        this.mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.addLogCategories(logCategories);
        final JScrollPane scrollPane = new JScrollPane(this.mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        this.add(scrollPane);
        this.setMaximumSize(new Dimension(750, 550));
        this.setResizable(false);
        this.pack();
    }
    
    protected void addLogCategories(final List<LogCategory> logCategories) {
        for (final LogCategory logCategory : logCategories) {
            this.addLogCategory(logCategory);
        }
    }
    
    protected void addLogCategory(final LogCategory logCategory) {
        final JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.setBorder(BorderFactory.createTitledBorder(logCategory.getName()));
        this.addLoggerGroups(logCategory, categoryPanel);
        this.mainPanel.add(categoryPanel);
    }
    
    protected void addLoggerGroups(final LogCategory logCategory, final JPanel categoryPanel) {
        final JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, 1));
        for (final LogCategory.Group group : logCategory.getGroups()) {
            final JCheckBox checkBox = new JCheckBox(group.getName());
            checkBox.setSelected(group.isEnabled());
            checkBox.setFocusable(false);
            checkBox.addItemListener(new ItemListener() {
                public void itemStateChanged(final ItemEvent e) {
                    if (e.getStateChange() == 2) {
                        LogCategorySelector.this.disableLoggerGroup(group);
                    }
                    else if (e.getStateChange() == 1) {
                        LogCategorySelector.this.enableLoggerGroup(group);
                    }
                }
            });
            checkboxPanel.add(checkBox);
        }
        final JToolBar buttonBar = new JToolBar();
        buttonBar.setFloatable(false);
        final JButton enableAllButton = new JButton("All");
        enableAllButton.setFocusable(false);
        enableAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                for (final LogCategory.Group group : logCategory.getGroups()) {
                    LogCategorySelector.this.enableLoggerGroup(group);
                }
                categoryPanel.removeAll();
                LogCategorySelector.this.addLoggerGroups(logCategory, categoryPanel);
                categoryPanel.revalidate();
            }
        });
        buttonBar.add(enableAllButton);
        final JButton disableAllButton = new JButton("None");
        disableAllButton.setFocusable(false);
        disableAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                for (final LogCategory.Group group : logCategory.getGroups()) {
                    LogCategorySelector.this.disableLoggerGroup(group);
                }
                categoryPanel.removeAll();
                LogCategorySelector.this.addLoggerGroups(logCategory, categoryPanel);
                categoryPanel.revalidate();
            }
        });
        buttonBar.add(disableAllButton);
        categoryPanel.add(checkboxPanel, "Center");
        categoryPanel.add(buttonBar, "North");
    }
    
    protected void enableLoggerGroup(final LogCategory.Group group) {
        group.setEnabled(true);
        group.getPreviousLevels().clear();
        for (final LogCategory.LoggerLevel loggerLevel : group.getLoggerLevels()) {
            final Logger logger = Logger.getLogger(loggerLevel.getLogger());
            group.getPreviousLevels().add(new LogCategory.LoggerLevel(logger.getName(), this.getLevel(logger)));
            logger.setLevel(loggerLevel.getLevel());
        }
    }
    
    protected void disableLoggerGroup(final LogCategory.Group group) {
        group.setEnabled(false);
        for (final LogCategory.LoggerLevel loggerLevel : group.getPreviousLevels()) {
            final Logger logger = Logger.getLogger(loggerLevel.getLogger());
            logger.setLevel(loggerLevel.getLevel());
        }
        if (group.getPreviousLevels().size() == 0) {
            for (final LogCategory.LoggerLevel loggerLevel : group.getLoggerLevels()) {
                final Logger logger = Logger.getLogger(loggerLevel.getLogger());
                logger.setLevel(Level.INFO);
            }
        }
        group.getPreviousLevels().clear();
    }
    
    public Level getLevel(final Logger logger) {
        Level level = logger.getLevel();
        if (level == null && logger.getParent() != null) {
            level = logger.getParent().getLevel();
        }
        return level;
    }
}
