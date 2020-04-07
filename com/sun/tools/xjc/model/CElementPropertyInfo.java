// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.xml.bind.v2.model.core.Adapter;
import java.util.Collection;
import java.util.Map;
import com.sun.xml.bind.v2.model.core.PropertyKind;
import java.util.Iterator;
import javax.xml.namespace.QName;
import java.util.AbstractList;
import java.util.ArrayList;
import org.xml.sax.Locator;
import com.sun.xml.xsom.XSComponent;
import java.util.List;
import com.sun.xml.bind.v2.model.core.ID;
import javax.activation.MimeType;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.xml.bind.v2.model.core.ElementPropertyInfo;

public final class CElementPropertyInfo extends CPropertyInfo implements ElementPropertyInfo<NType, NClass>
{
    private final boolean required;
    private final MimeType expectedMimeType;
    private CAdapter adapter;
    private final boolean isValueList;
    private ID id;
    private final List<CTypeRef> types;
    private final List<CNonElement> ref;
    
    public CElementPropertyInfo(final String name, final CollectionMode collection, final ID id, final MimeType expectedMimeType, final XSComponent source, final CCustomizations customizations, final Locator locator, final boolean required) {
        super(name, collection.col, source, customizations, locator);
        this.types = new ArrayList<CTypeRef>();
        this.ref = new AbstractList<CNonElement>() {
            public CNonElement get(final int index) {
                return CElementPropertyInfo.this.getTypes().get(index).getTarget();
            }
            
            public int size() {
                return CElementPropertyInfo.this.getTypes().size();
            }
        };
        this.required = required;
        this.id = id;
        this.expectedMimeType = expectedMimeType;
        this.isValueList = collection.val;
    }
    
    public ID id() {
        return this.id;
    }
    
    public List<CTypeRef> getTypes() {
        return this.types;
    }
    
    public List<CNonElement> ref() {
        return this.ref;
    }
    
    public QName getSchemaType() {
        if (this.types.size() != 1) {
            return null;
        }
        final CTypeRef t = this.types.get(0);
        if (CPropertyInfo.needsExplicitTypeName(t.getTarget(), t.typeName)) {
            return t.typeName;
        }
        return null;
    }
    
    @Deprecated
    public QName getXmlName() {
        return null;
    }
    
    public boolean isCollectionRequired() {
        return false;
    }
    
    public boolean isCollectionNillable() {
        return false;
    }
    
    public boolean isRequired() {
        return this.required;
    }
    
    public boolean isValueList() {
        return this.isValueList;
    }
    
    public boolean isUnboxable() {
        if (!this.isCollection() && !this.required) {
            return false;
        }
        for (final CTypeRef t : this.getTypes()) {
            if (t.isNillable()) {
                return false;
            }
        }
        return super.isUnboxable();
    }
    
    public boolean isOptionalPrimitive() {
        for (final CTypeRef t : this.getTypes()) {
            if (t.isNillable()) {
                return false;
            }
        }
        return !this.isCollection() && !this.required && super.isUnboxable();
    }
    
    public <V> V accept(final CPropertyVisitor<V> visitor) {
        return visitor.onElement(this);
    }
    
    public CAdapter getAdapter() {
        return this.adapter;
    }
    
    public void setAdapter(final CAdapter a) {
        assert this.adapter == null;
        this.adapter = a;
    }
    
    public final PropertyKind kind() {
        return PropertyKind.ELEMENT;
    }
    
    public MimeType getExpectedMimeType() {
        return this.expectedMimeType;
    }
    
    public QName collectElementNames(final Map<QName, CPropertyInfo> table) {
        for (final CTypeRef t : this.types) {
            final QName n = t.getTagName();
            if (table.containsKey(n)) {
                return n;
            }
            table.put(n, this);
        }
        return null;
    }
    
    public enum CollectionMode
    {
        NOT_REPEATED(false, false), 
        REPEATED_ELEMENT(true, false), 
        REPEATED_VALUE(true, true);
        
        private final boolean col;
        private final boolean val;
        
        private CollectionMode(final boolean col, final boolean val) {
            this.col = col;
            this.val = val;
        }
        
        public boolean isRepeated() {
            return this.col;
        }
    }
}
