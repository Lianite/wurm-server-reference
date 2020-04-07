// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.swing;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.beans.PropertyDescriptor;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class BeanTableModel<T> extends AbstractTableModel
{
    private Class<T> beanClass;
    private List<PropertyDescriptor> properties;
    private List<T> rows;
    
    public BeanTableModel(final Class<T> beanClass, final Collection<T> rows) {
        this.properties = new ArrayList<PropertyDescriptor>();
        this.beanClass = beanClass;
        this.rows = new ArrayList<T>((Collection<? extends T>)rows);
    }
    
    public String getColumnName(final int column) {
        return this.properties.get(column).getDisplayName();
    }
    
    public int getColumnCount() {
        return this.properties.size();
    }
    
    public int getRowCount() {
        return this.rows.size();
    }
    
    public Object getValueAt(final int row, final int column) {
        Object value = null;
        final T entityInstance = this.rows.get(row);
        if (entityInstance != null) {
            final PropertyDescriptor property = this.properties.get(column);
            final Method readMethod = property.getReadMethod();
            try {
                value = readMethod.invoke(entityInstance, new Object[0]);
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return value;
    }
    
    public void addColumn(final String displayName, final String propertyName) {
        try {
            final PropertyDescriptor property = new PropertyDescriptor(propertyName, this.beanClass, propertyName, null);
            property.setDisplayName(displayName);
            this.properties.add(property);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void resetColumns() {
        this.properties = new ArrayList<PropertyDescriptor>();
    }
    
    public List<T> getRows() {
        return this.rows;
    }
    
    public void setRows(final Collection<T> rows) {
        this.rows = new ArrayList<T>((Collection<? extends T>)rows);
        this.fireTableDataChanged();
    }
    
    public void addRow(final T value) {
        this.rows.add(value);
        this.fireTableRowsInserted(this.getRowCount() - 1, this.getRowCount() - 1);
    }
    
    public void removeRow(final int row) {
        if (this.rows.size() > row && row != -1) {
            this.rows.remove(row);
            this.fireTableRowsDeleted(row, row);
        }
    }
    
    public void setRow(final int row, final T entityInstance) {
        this.rows.remove(row);
        this.rows.add(row, entityInstance);
        this.fireTableDataChanged();
    }
}
