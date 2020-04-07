// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.property;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import javafx.collections.FXCollections;
import java.beans.PropertyDescriptor;
import java.util.function.Predicate;
import org.controlsfx.control.PropertySheet;
import javafx.collections.ObservableList;

public final class BeanPropertyUtils
{
    public static ObservableList<PropertySheet.Item> getProperties(final Object bean) {
        return getProperties(bean, p -> true);
    }
    
    public static ObservableList<PropertySheet.Item> getProperties(final Object bean, final Predicate<PropertyDescriptor> test) {
        final ObservableList<PropertySheet.Item> list = (ObservableList<PropertySheet.Item>)FXCollections.observableArrayList();
        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
            for (final PropertyDescriptor p : beanInfo.getPropertyDescriptors()) {
                if (test.test(p)) {
                    list.add((Object)new BeanProperty(bean, p));
                }
            }
        }
        catch (IntrospectionException e) {
            e.printStackTrace();
        }
        return list;
    }
}
