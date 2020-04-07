// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.swing;

import javax.swing.text.View;
import javax.swing.SwingUtilities;
import java.awt.FontMetrics;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.AWTEvent;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import java.awt.Component;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.metal.MetalTabbedPaneUI;
import javax.swing.Icon;
import javax.swing.JViewport;
import javax.swing.event.EventListenerList;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import javax.swing.JTabbedPane;

public class ClosableTabbedPane extends JTabbedPane implements MouseListener, MouseMotionListener
{
    private EventListenerList listenerList;
    private JViewport headerViewport;
    private Icon normalCloseIcon;
    private Icon hooverCloseIcon;
    private Icon pressedCloseIcon;
    
    public ClosableTabbedPane() {
        this.listenerList = null;
        this.headerViewport = null;
        this.normalCloseIcon = null;
        this.hooverCloseIcon = null;
        this.pressedCloseIcon = null;
        this.init(2);
    }
    
    public ClosableTabbedPane(final int horizontalTextPosition) {
        this.listenerList = null;
        this.headerViewport = null;
        this.normalCloseIcon = null;
        this.hooverCloseIcon = null;
        this.pressedCloseIcon = null;
        this.init(horizontalTextPosition);
    }
    
    private void init(final int horizontalTextPosition) {
        this.listenerList = new EventListenerList();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        if (this.getUI() instanceof MetalTabbedPaneUI) {
            this.setUI(new CloseableMetalTabbedPaneUI(horizontalTextPosition));
        }
        else {
            this.setUI(new CloseableTabbedPaneUI(horizontalTextPosition));
        }
    }
    
    public void setCloseIcons(final Icon normal, final Icon hoover, final Icon pressed) {
        this.normalCloseIcon = normal;
        this.hooverCloseIcon = hoover;
        this.pressedCloseIcon = pressed;
    }
    
    public void addTab(final String title, final Component component) {
        this.addTab(title, component, null);
    }
    
    public void addTab(final String title, final Component component, final Icon extraIcon) {
        boolean doPaintCloseIcon = true;
        try {
            Object prop = null;
            if ((prop = ((JComponent)component).getClientProperty("isClosable")) != null) {
                doPaintCloseIcon = (boolean)prop;
            }
        }
        catch (Exception ex) {}
        super.addTab(title, doPaintCloseIcon ? new CloseTabIcon(extraIcon) : null, component);
        if (this.headerViewport == null) {
            for (final Component c : this.getComponents()) {
                if ("TabbedPane.scrollableViewport".equals(c.getName())) {
                    this.headerViewport = (JViewport)c;
                }
            }
        }
    }
    
    public void mouseClicked(final MouseEvent e) {
        this.processMouseEvents(e);
    }
    
    public void mouseEntered(final MouseEvent e) {
    }
    
    public void mouseExited(final MouseEvent e) {
        for (int i = 0; i < this.getTabCount(); ++i) {
            final CloseTabIcon icon = (CloseTabIcon)this.getIconAt(i);
            if (icon != null) {
                icon.mouseover = false;
            }
        }
        this.repaint();
    }
    
    public void mousePressed(final MouseEvent e) {
        this.processMouseEvents(e);
    }
    
    public void mouseReleased(final MouseEvent e) {
    }
    
    public void mouseDragged(final MouseEvent e) {
        this.processMouseEvents(e);
    }
    
    public void mouseMoved(final MouseEvent e) {
        this.processMouseEvents(e);
    }
    
    private void processMouseEvents(final MouseEvent e) {
        final int tabNumber = this.getUI().tabForCoordinate(this, e.getX(), e.getY());
        if (tabNumber < 0) {
            return;
        }
        final CloseTabIcon icon = (CloseTabIcon)this.getIconAt(tabNumber);
        if (icon != null) {
            final Rectangle rect = icon.getBounds();
            final Point pos = (this.headerViewport == null) ? new Point() : this.headerViewport.getViewPosition();
            final Rectangle drawRect = new Rectangle(rect.x - pos.x, rect.y - pos.y, rect.width, rect.height);
            if (e.getID() == 501) {
                icon.mousepressed = (e.getModifiers() == 16);
                this.repaint(drawRect);
            }
            else if (e.getID() == 503 || e.getID() == 506 || e.getID() == 500) {
                final Point point = pos;
                point.x += e.getX();
                final Point point2 = pos;
                point2.y += e.getY();
                if (rect.contains(pos)) {
                    if (e.getID() == 500) {
                        final int selIndex = this.getSelectedIndex();
                        if (this.fireCloseTab(selIndex)) {
                            if (selIndex > 0) {
                                final Rectangle rec = this.getUI().getTabBounds(this, selIndex - 1);
                                final MouseEvent event = new MouseEvent((Component)e.getSource(), e.getID() + 1, System.currentTimeMillis(), e.getModifiers(), rec.x, rec.y, e.getClickCount(), e.isPopupTrigger(), e.getButton());
                                this.dispatchEvent(event);
                            }
                            this.remove(selIndex);
                        }
                        else {
                            icon.mouseover = false;
                            icon.mousepressed = false;
                            this.repaint(drawRect);
                        }
                    }
                    else {
                        icon.mouseover = true;
                        icon.mousepressed = (e.getModifiers() == 16);
                    }
                }
                else {
                    icon.mouseover = false;
                }
                this.repaint(drawRect);
            }
        }
    }
    
    public void addCloseableTabbedPaneListener(final ClosableTabbedPaneListener l) {
        this.listenerList.add(ClosableTabbedPaneListener.class, l);
    }
    
    public void removeCloseableTabbedPaneListener(final ClosableTabbedPaneListener l) {
        this.listenerList.remove(ClosableTabbedPaneListener.class, l);
    }
    
    public ClosableTabbedPaneListener[] getCloseableTabbedPaneListener() {
        return this.listenerList.getListeners(ClosableTabbedPaneListener.class);
    }
    
    protected boolean fireCloseTab(final int tabIndexToClose) {
        boolean closeit = true;
        final Object[] arr$;
        final Object[] listeners = arr$ = this.listenerList.getListenerList();
        for (final Object i : arr$) {
            if (i instanceof ClosableTabbedPaneListener && !((ClosableTabbedPaneListener)i).closeTab(tabIndexToClose)) {
                closeit = false;
                break;
            }
        }
        return closeit;
    }
    
    class CloseTabIcon implements Icon
    {
        private int x_pos;
        private int y_pos;
        private int width;
        private int height;
        private Icon fileIcon;
        private boolean mouseover;
        private boolean mousepressed;
        
        public CloseTabIcon(final Icon fileIcon) {
            this.mouseover = false;
            this.mousepressed = false;
            this.fileIcon = fileIcon;
            this.width = 16;
            this.height = 16;
        }
        
        public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
            boolean doPaintCloseIcon = true;
            try {
                final JTabbedPane tabbedpane = (JTabbedPane)c;
                final int tabNumber = tabbedpane.getUI().tabForCoordinate(tabbedpane, x, y);
                final JComponent curPanel = (JComponent)tabbedpane.getComponentAt(tabNumber);
                Object prop = null;
                if ((prop = curPanel.getClientProperty("isClosable")) != null) {
                    doPaintCloseIcon = (boolean)prop;
                }
            }
            catch (Exception ex) {}
            if (doPaintCloseIcon) {
                this.x_pos = x;
                this.y_pos = y;
                int y_p = y + 1;
                if (ClosableTabbedPane.this.normalCloseIcon != null && !this.mouseover) {
                    ClosableTabbedPane.this.normalCloseIcon.paintIcon(c, g, x, y_p);
                }
                else if (ClosableTabbedPane.this.hooverCloseIcon != null && this.mouseover && !this.mousepressed) {
                    ClosableTabbedPane.this.hooverCloseIcon.paintIcon(c, g, x, y_p);
                }
                else if (ClosableTabbedPane.this.pressedCloseIcon != null && this.mousepressed) {
                    ClosableTabbedPane.this.pressedCloseIcon.paintIcon(c, g, x, y_p);
                }
                else {
                    ++y_p;
                    final Color col = g.getColor();
                    if (this.mousepressed && this.mouseover) {
                        g.setColor(Color.WHITE);
                        g.fillRect(x + 1, y_p, 12, 13);
                    }
                    g.setColor(Color.black);
                    g.drawLine(x + 1, y_p, x + 12, y_p);
                    g.drawLine(x + 1, y_p + 13, x + 12, y_p + 13);
                    g.drawLine(x, y_p + 1, x, y_p + 12);
                    g.drawLine(x + 13, y_p + 1, x + 13, y_p + 12);
                    g.drawLine(x + 3, y_p + 3, x + 10, y_p + 10);
                    if (this.mouseover) {
                        g.setColor(Color.GRAY);
                    }
                    g.drawLine(x + 3, y_p + 4, x + 9, y_p + 10);
                    g.drawLine(x + 4, y_p + 3, x + 10, y_p + 9);
                    g.drawLine(x + 10, y_p + 3, x + 3, y_p + 10);
                    g.drawLine(x + 10, y_p + 4, x + 4, y_p + 10);
                    g.drawLine(x + 9, y_p + 3, x + 3, y_p + 9);
                    g.setColor(col);
                    if (this.fileIcon != null) {
                        this.fileIcon.paintIcon(c, g, x + this.width, y_p);
                    }
                }
            }
        }
        
        public int getIconWidth() {
            return this.width + ((this.fileIcon != null) ? this.fileIcon.getIconWidth() : 0);
        }
        
        public int getIconHeight() {
            return this.height;
        }
        
        public Rectangle getBounds() {
            return new Rectangle(this.x_pos, this.y_pos, this.width, this.height);
        }
    }
    
    class CloseableTabbedPaneUI extends BasicTabbedPaneUI
    {
        private int horizontalTextPosition;
        
        public CloseableTabbedPaneUI() {
            this.horizontalTextPosition = 2;
        }
        
        public CloseableTabbedPaneUI(final int horizontalTextPosition) {
            this.horizontalTextPosition = 2;
            this.horizontalTextPosition = horizontalTextPosition;
        }
        
        protected void layoutLabel(final int tabPlacement, final FontMetrics metrics, final int tabIndex, final String title, final Icon icon, final Rectangle tabRect, final Rectangle iconRect, final Rectangle textRect, final boolean isSelected) {
            final boolean b = false;
            iconRect.y = (b ? 1 : 0);
            iconRect.x = (b ? 1 : 0);
            textRect.y = (b ? 1 : 0);
            textRect.x = (b ? 1 : 0);
            final View v = this.getTextViewForTab(tabIndex);
            if (v != null) {
                this.tabPane.putClientProperty("html", v);
            }
            SwingUtilities.layoutCompoundLabel(this.tabPane, metrics, title, icon, 0, 0, 0, this.horizontalTextPosition, tabRect, iconRect, textRect, this.textIconGap + 2);
            this.tabPane.putClientProperty("html", null);
            final int xNudge = this.getTabLabelShiftX(tabPlacement, tabIndex, isSelected);
            final int yNudge = this.getTabLabelShiftY(tabPlacement, tabIndex, isSelected);
            iconRect.x += xNudge;
            iconRect.y += yNudge;
            textRect.x += xNudge;
            textRect.y += yNudge;
        }
    }
    
    class CloseableMetalTabbedPaneUI extends MetalTabbedPaneUI
    {
        private int horizontalTextPosition;
        
        public CloseableMetalTabbedPaneUI() {
            this.horizontalTextPosition = 2;
        }
        
        public CloseableMetalTabbedPaneUI(final int horizontalTextPosition) {
            this.horizontalTextPosition = 2;
            this.horizontalTextPosition = horizontalTextPosition;
        }
        
        protected void layoutLabel(final int tabPlacement, final FontMetrics metrics, final int tabIndex, final String title, final Icon icon, final Rectangle tabRect, final Rectangle iconRect, final Rectangle textRect, final boolean isSelected) {
            final boolean b = false;
            iconRect.y = (b ? 1 : 0);
            iconRect.x = (b ? 1 : 0);
            textRect.y = (b ? 1 : 0);
            textRect.x = (b ? 1 : 0);
            final View v = this.getTextViewForTab(tabIndex);
            if (v != null) {
                this.tabPane.putClientProperty("html", v);
            }
            SwingUtilities.layoutCompoundLabel(this.tabPane, metrics, title, icon, 0, 0, 0, this.horizontalTextPosition, tabRect, iconRect, textRect, this.textIconGap + 2);
            this.tabPane.putClientProperty("html", null);
            final int xNudge = this.getTabLabelShiftX(tabPlacement, tabIndex, isSelected);
            final int yNudge = this.getTabLabelShiftY(tabPlacement, tabIndex, isSelected);
            iconRect.x += xNudge;
            iconRect.y += yNudge;
            textRect.x += xNudge;
            textRect.y += yNudge;
        }
    }
}
