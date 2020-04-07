// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types.csv;

import java.util.Iterator;
import org.seamless.util.Reflections;
import org.fourthline.cling.model.ModelUtil;
import java.util.List;
import org.fourthline.cling.model.types.InvalidValueException;
import java.util.Collection;
import org.fourthline.cling.model.types.Datatype;
import java.util.ArrayList;

public abstract class CSV<T> extends ArrayList<T>
{
    protected final Datatype.Builtin datatype;
    
    public CSV() {
        this.datatype = this.getBuiltinDatatype();
    }
    
    public CSV(final String s) throws InvalidValueException {
        this.datatype = this.getBuiltinDatatype();
        this.addAll(this.parseString(s));
    }
    
    protected List parseString(final String s) throws InvalidValueException {
        final String[] strings = ModelUtil.fromCommaSeparatedList(s);
        final List values = new ArrayList();
        for (final String string : strings) {
            values.add(this.datatype.getDatatype().valueOf(string));
        }
        return values;
    }
    
    protected Datatype.Builtin getBuiltinDatatype() throws InvalidValueException {
        final Class csvType = Reflections.getTypeArguments(ArrayList.class, this.getClass()).get(0);
        final Datatype.Default defaultType = Datatype.Default.getByJavaType(csvType);
        if (defaultType == null) {
            throw new InvalidValueException("No built-in UPnP datatype for Java type of CSV: " + csvType);
        }
        return defaultType.getBuiltinType();
    }
    
    @Override
    public String toString() {
        final List<String> stringValues = new ArrayList<String>();
        for (final T t : this) {
            stringValues.add(this.datatype.getDatatype().getString(t));
        }
        return ModelUtil.toCommaSeparatedList(stringValues.toArray(new Object[stringValues.size()]));
    }
}
