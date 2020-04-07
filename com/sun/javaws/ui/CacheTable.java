// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.ui;

import javax.swing.ListSelectionModel;
import java.util.Iterator;
import com.sun.javaws.cache.DiskCacheEntry;
import com.sun.javaws.cache.Cache;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.ArrayList;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.event.MouseListener;
import java.awt.Point;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.BorderFactory;
import java.awt.Dimension;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTable;

class CacheTable extends JTable
{
    private static final TableCellRenderer _defaultRenderer;
    private static final int MIN_ROW_HEIGHT = 36;
    private boolean _system;
    private int _filter;
    static /* synthetic */ Class class$javax$swing$JLabel;
    
    public CacheTable(final CacheViewer cacheViewer, final boolean system) {
        this._filter = 0;
        this._system = system;
        this.setShowGrid(false);
        this.setIntercellSpacing(new Dimension(0, 0));
        this.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        if (this.getRowHeight() < 36) {
            this.setRowHeight(36);
        }
        this.setPreferredScrollableViewportSize(new Dimension(640, 280));
        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(final MouseEvent mouseEvent) {
                if (mouseEvent.isPopupTrigger()) {
                    final int y = mouseEvent.getY();
                    final int n = y / CacheTable.this.getRowHeight();
                    CacheTable.this.getSelectionModel().clearSelection();
                    CacheTable.this.getSelectionModel().addSelectionInterval(n, n);
                    cacheViewer.popupApplicationMenu(CacheTable.this, mouseEvent.getX(), y);
                }
            }
            
            public void mouseReleased(final MouseEvent mouseEvent) {
                if (mouseEvent.isPopupTrigger()) {
                    final int y = mouseEvent.getY();
                    final int n = y / CacheTable.this.getRowHeight();
                    CacheTable.this.getSelectionModel().clearSelection();
                    CacheTable.this.getSelectionModel().addSelectionInterval(n, n);
                    cacheViewer.popupApplicationMenu(CacheTable.this, mouseEvent.getX(), y);
                }
            }
            
            public void mouseClicked(final MouseEvent mouseEvent) {
                final Point point = mouseEvent.getPoint();
                if (mouseEvent.getClickCount() == 2 && mouseEvent.getButton() == 1 && CacheTable.this.getColumnModel().getColumnIndexAtX(point.x) < 3) {
                    cacheViewer.launchApplication();
                }
            }
        });
        this.reset();
    }
    
    public void setFilter(final int filter) {
        if (filter != this._filter) {
            this._filter = filter;
            this.reset();
        }
    }
    
    public void reset() {
        final TableModel model = this.getModel();
        if (model instanceof CacheTableModel) {
            ((CacheTableModel)model).removeMouseListenerFromHeaderInTable(this);
        }
        final CacheTableModel model2 = new CacheTableModel(this._system, this._filter);
        this.setModel(model2);
        for (int i = 0; i < this.getModel().getColumnCount(); ++i) {
            final TableColumn column = this.getColumnModel().getColumn(i);
            column.setHeaderRenderer(new CacheTableHeaderRenderer());
            final int preferredWidth = model2.getPreferredWidth(i);
            column.setPreferredWidth(preferredWidth);
            column.setMinWidth(preferredWidth);
        }
        this.setDefaultRenderer((CacheTable.class$javax$swing$JLabel == null) ? (CacheTable.class$javax$swing$JLabel = class$("javax.swing.JLabel")) : CacheTable.class$javax$swing$JLabel, model2);
        model2.addMouseListenerToHeaderInTable(this);
    }
    
    public CacheObject getCacheObject(final int n) {
        return ((CacheTableModel)this.getModel()).getCacheObject(n);
    }
    
    public String[] getAllHrefs() {
        final ArrayList<String> list = new ArrayList<String>();
        final TableModel model = this.getModel();
        if (model instanceof CacheTableModel) {
            for (int i = 0; i < model.getRowCount(); ++i) {
                final String rowHref = ((CacheTableModel)model).getRowHref(i);
                if (rowHref != null) {
                    list.add(rowHref);
                }
            }
        }
        return list.toArray(new String[0]);
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    static {
        _defaultRenderer = new DefaultTableCellRenderer();
    }
    
    private class CacheTableHeaderRenderer extends DefaultTableCellRenderer
    {
        public Component getTableCellRendererComponent(final JTable table, final Object o, final boolean b, final boolean b2, final int n, final int n2) {
            if (table != null) {
                final JTableHeader tableHeader = table.getTableHeader();
                if (tableHeader != null) {
                    this.setForeground(tableHeader.getForeground());
                    this.setBackground(tableHeader.getBackground());
                    this.setFont(tableHeader.getFont());
                }
            }
            this.setText((o == null) ? "" : o.toString());
            this.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            this.setHorizontalAlignment(0);
            final String headerToolTipText = CacheObject.getHeaderToolTipText(n2);
            if (headerToolTipText != null && headerToolTipText.length() > 0) {
                this.setToolTipText(headerToolTipText);
            }
            return this;
        }
    }
    
    private class CacheTableModel extends AbstractTableModel implements TableCellRenderer
    {
        private boolean _system;
        private CacheObject[] _rows;
        private int _filter;
        private int _sortColumn;
        private boolean _sortAscending;
        private MouseListener _mouseListener;
        
        public CacheTableModel(final boolean system, final int filter) {
            this._mouseListener = null;
            this._system = system;
            this._filter = filter;
            this._rows = new CacheObject[0];
            this._sortColumn = -1;
            this._sortAscending = true;
            this.refresh();
            this.fireTableDataChanged();
        }
        
        public Component getTableCellRendererComponent(final JTable table, final Object o, final boolean b, final boolean b2, final int n, final int n2) {
            if (o instanceof Component) {
                final Component component = (Component)o;
                if (b) {
                    component.setForeground(table.getSelectionForeground());
                    component.setBackground(table.getSelectionBackground());
                }
                else {
                    component.setForeground(table.getForeground());
                    component.setBackground(table.getBackground());
                }
                CacheObject.hasFocus(component, b2);
                return component;
            }
            return CacheTable._defaultRenderer.getTableCellRendererComponent(table, o, b, b2, n, n2);
        }
        
        public void refresh() {
            final ArrayList<CacheObject> list = new ArrayList<CacheObject>();
            final Iterator jnlpCacheEntries = Cache.getJnlpCacheEntries(this._system);
            while (jnlpCacheEntries.hasNext()) {
                final CacheObject cacheObject = new CacheObject(jnlpCacheEntries.next(), this);
                if (cacheObject.inFilter(this._filter) && cacheObject.getLaunchDesc() != null) {
                    list.add(cacheObject);
                }
            }
            this._rows = list.toArray(new CacheObject[0]);
            if (this._sortColumn != -1) {
                this.sort();
            }
        }
        
        CacheObject getCacheObject(final int n) {
            return this._rows[n];
        }
        
        public Object getValueAt(final int n, final int n2) {
            return this._rows[n].getObject(n2);
        }
        
        public int getRowCount() {
            return this._rows.length;
        }
        
        public String getRowHref(final int n) {
            return this._rows[n].getHref();
        }
        
        public int getColumnCount() {
            return CacheObject.getColumnCount();
        }
        
        public boolean isCellEditable(final int n, final int n2) {
            return this._rows[n].isEditable(n2);
        }
        
        public Class getColumnClass(final int n) {
            return CacheObject.getClass(n);
        }
        
        public String getColumnName(final int n) {
            return CacheObject.getColumnName(n);
        }
        
        public void setValueAt(final Object o, final int n, final int n2) {
            this._rows[n].setValue(n2, o);
        }
        
        public int getPreferredWidth(final int n) {
            return CacheObject.getPreferredWidth(n);
        }
        
        public void removeMouseListenerFromHeaderInTable(final JTable table) {
            if (this._mouseListener != null) {
                table.getTableHeader().removeMouseListener(this._mouseListener);
            }
        }
        
        public void addMouseListenerToHeaderInTable(final JTable table) {
            table.setColumnSelectionAllowed(false);
            this._mouseListener = new MouseAdapter() {
                private final /* synthetic */ ListSelectionModel val$lsm = table.getSelectionModel();
                
                public void mouseClicked(final MouseEvent mouseEvent) {
                    final int columnIndexAtX = table.getColumnModel().getColumnIndexAtX(mouseEvent.getX());
                    final int minSelectionIndex = this.val$lsm.getMinSelectionIndex();
                    this.val$lsm.clearSelection();
                    final int convertColumnIndexToModel = table.convertColumnIndexToModel(columnIndexAtX);
                    if (mouseEvent.getClickCount() == 1 && convertColumnIndexToModel >= 0) {
                        CacheTableModel.this._sortAscending = ((mouseEvent.getModifiers() & 0x1) == 0x0);
                        CacheTableModel.this._sortColumn = convertColumnIndexToModel;
                        CacheTableModel.this.runSort(this.val$lsm, minSelectionIndex);
                    }
                }
            };
            table.getTableHeader().addMouseListener(this._mouseListener);
        }
        
        public void sort() {
            boolean b = false;
            if (this._sortAscending) {
                for (int i = 0; i < this.getRowCount(); ++i) {
                    for (int j = i + 1; j < this.getRowCount(); ++j) {
                        if (this._rows[i].compareColumns(this._rows[j], this._sortColumn) > 0) {
                            b = true;
                            final CacheObject cacheObject = this._rows[i];
                            this._rows[i] = this._rows[j];
                            this._rows[j] = cacheObject;
                        }
                    }
                }
            }
            else {
                for (int k = 0; k < this.getRowCount(); ++k) {
                    for (int l = k + 1; l < this.getRowCount(); ++l) {
                        if (this._rows[l].compareColumns(this._rows[k], this._sortColumn) > 0) {
                            b = true;
                            final CacheObject cacheObject2 = this._rows[k];
                            this._rows[k] = this._rows[l];
                            this._rows[l] = cacheObject2;
                        }
                    }
                }
            }
            if (b) {
                this.fireTableDataChanged();
            }
        }
        
        private void runSort(final ListSelectionModel listSelectionModel, final int n) {
            if (CacheViewer.getStatus() != 4) {
                new Thread(new Runnable() {
                    public void run() {
                        CacheViewer.setStatus(4);
                        try {
                            CacheObject cacheObject = null;
                            if (n >= 0) {
                                cacheObject = CacheTableModel.this._rows[n];
                            }
                            CacheTableModel.this.sort();
                            if (cacheObject != null) {
                                for (int i = 0; i < CacheTableModel.this._rows.length; ++i) {
                                    if (CacheTableModel.this._rows[i] == cacheObject) {
                                        listSelectionModel.addSelectionInterval(i, i);
                                        break;
                                    }
                                }
                            }
                        }
                        finally {
                            CacheViewer.setStatus(0);
                        }
                    }
                }).start();
            }
        }
    }
}
