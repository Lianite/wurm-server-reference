// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.xml.bind.v2.model.core.Adapter;
import java.util.Map;
import javax.activation.MimeType;
import com.sun.tools.xjc.model.nav.NavigatorImpl;
import javax.xml.bind.annotation.W3CDomHandler;
import com.sun.xml.bind.v2.model.core.ID;
import com.sun.xml.bind.v2.model.core.PropertyKind;
import javax.xml.namespace.QName;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import org.xml.sax.Locator;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.bind.v2.model.core.WildcardMode;
import java.util.Set;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.xml.bind.v2.model.core.ReferencePropertyInfo;

public final class CReferencePropertyInfo extends CPropertyInfo implements ReferencePropertyInfo<NType, NClass>
{
    private final Set<CElement> elements;
    private final boolean isMixed;
    private WildcardMode wildcard;
    
    public CReferencePropertyInfo(final String name, final boolean collection, final boolean isMixed, final XSComponent source, final CCustomizations customizations, final Locator locator) {
        super(name, collection || isMixed, source, customizations, locator);
        this.elements = new HashSet<CElement>();
        this.isMixed = isMixed;
    }
    
    public Set<? extends CTypeInfo> ref() {
        final class RefList extends HashSet<CTypeInfo>
        {
            RefList() {
                super(CReferencePropertyInfo.this.elements.size());
                this.addAll(CReferencePropertyInfo.this.elements);
            }
            
            public boolean addAll(final Collection<? extends CTypeInfo> col) {
                boolean r = false;
                for (final CTypeInfo e : col) {
                    if (e instanceof CElementInfo) {
                        r |= this.addAll(((CElementInfo)e).getSubstitutionMembers());
                    }
                    r |= this.add(e);
                }
                return r;
            }
        }
        final RefList r = new RefList();
        if (this.wildcard != null) {
            if (this.wildcard.allowDom) {
                ((HashSet<CWildcardTypeInfo>)r).add(CWildcardTypeInfo.INSTANCE);
            }
            if (this.wildcard.allowTypedObject) {
                ((HashSet<CBuiltinLeafInfo>)r).add(CBuiltinLeafInfo.ANYTYPE);
            }
        }
        if (this.isMixed()) {
            ((HashSet<CBuiltinLeafInfo>)r).add(CBuiltinLeafInfo.STRING);
        }
        return r;
    }
    
    public Set<CElement> getElements() {
        return this.elements;
    }
    
    public boolean isMixed() {
        return this.isMixed;
    }
    
    @Deprecated
    public QName getXmlName() {
        return null;
    }
    
    public boolean isUnboxable() {
        return false;
    }
    
    public boolean isOptionalPrimitive() {
        return false;
    }
    
    public <V> V accept(final CPropertyVisitor<V> visitor) {
        return visitor.onReference(this);
    }
    
    public CAdapter getAdapter() {
        return null;
    }
    
    public final PropertyKind kind() {
        return PropertyKind.REFERENCE;
    }
    
    public ID id() {
        return ID.NONE;
    }
    
    public WildcardMode getWildcard() {
        return this.wildcard;
    }
    
    public void setWildcard(final WildcardMode mode) {
        this.wildcard = mode;
    }
    
    public NClass getDOMHandler() {
        if (this.getWildcard() != null) {
            return NavigatorImpl.create(W3CDomHandler.class);
        }
        return null;
    }
    
    public MimeType getExpectedMimeType() {
        return null;
    }
    
    public boolean isCollectionNillable() {
        return false;
    }
    
    public boolean isCollectionRequired() {
        return false;
    }
    
    public QName getSchemaType() {
        return null;
    }
    
    public QName collectElementNames(final Map<QName, CPropertyInfo> table) {
        for (final CElement e : this.elements) {
            final QName n = e.getElementName();
            if (table.containsKey(n)) {
                return n;
            }
            table.put(n, this);
        }
        return null;
    }
}
