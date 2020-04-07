// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.impl;

import java.util.Iterator;
import com.sun.xml.bind.v2.model.core.ClassInfo;
import com.sun.xml.bind.v2.runtime.Location;
import javax.xml.bind.annotation.XmlEnumValue;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlEnum;
import com.sun.xml.bind.v2.model.annotation.Locatable;
import javax.xml.namespace.QName;
import com.sun.xml.bind.v2.model.core.NonElement;
import com.sun.xml.bind.v2.model.core.Element;
import com.sun.xml.bind.v2.model.core.EnumLeafInfo;

class EnumLeafInfoImpl<T, C, F, M> extends TypeInfoImpl<T, C, F, M> implements EnumLeafInfo<T, C>, Element<T, C>, Iterable<EnumConstantImpl<T, C, F, M>>
{
    final C clazz;
    NonElement<T, C> baseType;
    private final T type;
    private final QName typeName;
    private EnumConstantImpl<T, C, F, M> firstConstant;
    private QName elementName;
    
    public EnumLeafInfoImpl(final ModelBuilder<T, C, F, M> builder, final Locatable upstream, final C clazz, final T type) {
        super(builder, upstream);
        this.clazz = clazz;
        this.type = type;
        this.elementName = this.parseElementName(clazz);
        this.typeName = this.parseTypeName(clazz);
        final XmlEnum xe = builder.reader.getClassAnnotation(XmlEnum.class, clazz, this);
        if (xe != null) {
            final T base = builder.reader.getClassValue(xe, "value");
            this.baseType = builder.getTypeInfo(base, this);
        }
        else {
            this.baseType = builder.getTypeInfo(builder.nav.ref(String.class), this);
        }
    }
    
    protected void calcConstants() {
        EnumConstantImpl<T, C, F, M> last = null;
        final F[] constants = this.nav().getEnumConstants(this.clazz);
        for (int i = constants.length - 1; i >= 0; --i) {
            final F constant = constants[i];
            final String name = this.nav().getFieldName(constant);
            final XmlEnumValue xev = this.builder.reader.getFieldAnnotation(XmlEnumValue.class, constant, this);
            String literal;
            if (xev == null) {
                literal = name;
            }
            else {
                literal = xev.value();
            }
            last = this.createEnumConstant(name, literal, constant, last);
        }
        this.firstConstant = last;
    }
    
    protected EnumConstantImpl<T, C, F, M> createEnumConstant(final String name, final String literal, final F constant, final EnumConstantImpl<T, C, F, M> last) {
        return new EnumConstantImpl<T, C, F, M>(this, name, literal, last);
    }
    
    public T getType() {
        return this.type;
    }
    
    public final boolean canBeReferencedByIDREF() {
        return false;
    }
    
    public QName getTypeName() {
        return this.typeName;
    }
    
    public C getClazz() {
        return this.clazz;
    }
    
    public NonElement<T, C> getBaseType() {
        return this.baseType;
    }
    
    public boolean isSimpleType() {
        return true;
    }
    
    public Location getLocation() {
        return this.nav().getClassLocation(this.clazz);
    }
    
    public Iterable<? extends EnumConstantImpl<T, C, F, M>> getConstants() {
        if (this.firstConstant == null) {
            this.calcConstants();
        }
        return this;
    }
    
    public void link() {
        this.getConstants();
        super.link();
    }
    
    public Element<T, C> getSubstitutionHead() {
        return null;
    }
    
    public QName getElementName() {
        return this.elementName;
    }
    
    public boolean isElement() {
        return this.elementName != null;
    }
    
    public Element<T, C> asElement() {
        if (this.isElement()) {
            return this;
        }
        return null;
    }
    
    public ClassInfo<T, C> getScope() {
        return null;
    }
    
    public Iterator<EnumConstantImpl<T, C, F, M>> iterator() {
        return new Iterator<EnumConstantImpl<T, C, F, M>>() {
            private EnumConstantImpl<T, C, F, M> next = EnumLeafInfoImpl.this.firstConstant;
            
            public boolean hasNext() {
                return this.next != null;
            }
            
            public EnumConstantImpl<T, C, F, M> next() {
                final EnumConstantImpl<T, C, F, M> r = this.next;
                this.next = this.next.next;
                return r;
            }
            
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
