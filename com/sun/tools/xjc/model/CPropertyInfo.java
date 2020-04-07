// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.xml.bind.v2.model.core.TypeInfo;
import com.sun.xml.bind.v2.model.core.Adapter;
import java.lang.annotation.Annotation;
import java.util.Map;
import javax.xml.namespace.QName;
import com.sun.codemodel.JClass;
import java.util.Collection;
import com.sun.codemodel.JJavaName;
import com.sun.xml.bind.api.impl.NameConverter;
import com.sun.xml.bind.v2.runtime.RuntimeUtil;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.sun.tools.xjc.generator.bean.field.FieldRenderer;
import com.sun.codemodel.JType;
import com.sun.xml.xsom.XSComponent;
import org.xml.sax.Locator;
import javax.xml.bind.annotation.XmlTransient;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.xml.bind.v2.model.core.PropertyInfo;

public abstract class CPropertyInfo implements PropertyInfo<NType, NClass>, CCustomizable
{
    @XmlTransient
    private CClassInfo parent;
    private String privateName;
    private String publicName;
    private final boolean isCollection;
    @XmlTransient
    public final Locator locator;
    private final XSComponent source;
    public JType baseType;
    public String javadoc;
    public boolean inlineBinaryData;
    @XmlJavaTypeAdapter(RuntimeUtil.ToStringAdapter.class)
    public FieldRenderer realization;
    public CDefaultValue defaultValue;
    private final CCustomizations customizations;
    
    protected CPropertyInfo(final String name, final boolean collection, final XSComponent source, final CCustomizations customizations, final Locator locator) {
        this.javadoc = "";
        this.publicName = name;
        String n = NameConverter.standard.toVariableName(name);
        if (!JJavaName.isJavaIdentifier(n)) {
            n = '_' + n;
        }
        this.privateName = n;
        this.isCollection = collection;
        this.locator = locator;
        if (customizations == null) {
            this.customizations = CCustomizations.EMPTY;
        }
        else {
            this.customizations = customizations;
        }
        this.source = source;
    }
    
    final void setParent(final CClassInfo parent) {
        assert this.parent == null;
        assert parent != null;
        this.parent = parent;
        this.customizations.setParent(parent.model, this);
    }
    
    public CTypeInfo parent() {
        return this.parent;
    }
    
    public Locator getLocator() {
        return this.locator;
    }
    
    public final XSComponent getSchemaComponent() {
        return this.source;
    }
    
    public abstract CAdapter getAdapter();
    
    public String getName() {
        return this.getName(false);
    }
    
    public String getName(final boolean isPublic) {
        return isPublic ? this.publicName : this.privateName;
    }
    
    public void setName(final boolean isPublic, final String newName) {
        if (isPublic) {
            this.publicName = newName;
        }
        else {
            this.privateName = newName;
        }
    }
    
    public String displayName() {
        return this.parent.toString() + '#' + this.getName(false);
    }
    
    public boolean isCollection() {
        return this.isCollection;
    }
    
    public abstract Collection<? extends CTypeInfo> ref();
    
    public boolean isUnboxable() {
        final Collection<? extends CTypeInfo> ts = this.ref();
        if (ts.size() != 1) {
            return false;
        }
        if (this.baseType != null && this.baseType instanceof JClass) {
            return false;
        }
        final CTypeInfo t = (CTypeInfo)ts.iterator().next();
        return ((TypeInfo<NType, C>)t).getType().isBoxedType();
    }
    
    public boolean isOptionalPrimitive() {
        return false;
    }
    
    public CCustomizations getCustomizations() {
        return this.customizations;
    }
    
    public boolean inlineBinaryData() {
        return this.inlineBinaryData;
    }
    
    public abstract <V> V accept(final CPropertyVisitor<V> p0);
    
    protected static boolean needsExplicitTypeName(final TypeUse type, final QName typeName) {
        if (typeName == null) {
            return false;
        }
        if (!typeName.getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema")) {
            return false;
        }
        if (type.isCollection()) {
            return true;
        }
        final QName itemType = type.getInfo().getTypeName();
        return itemType == null || !itemType.equals(typeName);
    }
    
    public QName collectElementNames(final Map<QName, CPropertyInfo> table) {
        return null;
    }
    
    public final <A extends Annotation> A readAnnotation(final Class<A> annotationType) {
        throw new UnsupportedOperationException();
    }
    
    public final boolean hasAnnotation(final Class<? extends Annotation> annotationType) {
        throw new UnsupportedOperationException();
    }
}
