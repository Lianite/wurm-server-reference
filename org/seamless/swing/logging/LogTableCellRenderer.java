// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.swing.logging;

import javax.swing.ImageIcon;
import java.util.Date;
import javax.swing.Icon;
import javax.swing.JLabel;
import java.util.logging.Level;
import java.awt.Component;
import javax.swing.JTable;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableCellRenderer;

public abstract class LogTableCellRenderer extends DefaultTableCellRenderer
{
    protected SimpleDateFormat dateFormat;
    
    public LogTableCellRenderer() {
        this.dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
    }
    
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        final LogMessage message = (LogMessage)value;
        switch (column) {
            case 0: {
                if (message.getLevel().equals(Level.SEVERE) || message.getLevel().equals(Level.WARNING)) {
                    return new JLabel(this.getWarnErrorIcon());
                }
                if (message.getLevel().equals(Level.FINE)) {
                    return new JLabel(this.getDebugIcon());
                }
                if (message.getLevel().equals(Level.FINER) || message.getLevel().equals(Level.FINEST)) {
                    return new JLabel(this.getTraceIcon());
                }
                return new JLabel(this.getInfoIcon());
            }
            case 1: {
                final Date date = new Date(message.getCreatedOn());
                return super.getTableCellRendererComponent(table, this.dateFormat.format(date), isSelected, hasFocus, row, column);
            }
            case 2: {
                return super.getTableCellRendererComponent(table, message.getThread(), isSelected, hasFocus, row, column);
            }
            case 3: {
                return super.getTableCellRendererComponent(table, message.getSource(), isSelected, hasFocus, row, column);
            }
            default: {
                return super.getTableCellRendererComponent(table, message.getMessage().replaceAll("\n", "<NL>").replaceAll("\r", "<CR>"), isSelected, hasFocus, row, column);
            }
        }
    }
    
    protected abstract ImageIcon getWarnErrorIcon();
    
    protected abstract ImageIcon getInfoIcon();
    
    protected abstract ImageIcon getDebugIcon();
    
    protected abstract ImageIcon getTraceIcon();
}
