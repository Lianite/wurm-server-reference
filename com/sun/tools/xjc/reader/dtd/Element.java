// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.dtd;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Set;
import com.sun.xml.xsom.XmlString;
import com.sun.tools.xjc.model.CNonElement;
import com.sun.tools.xjc.model.CTypeRef;
import javax.activation.MimeType;
import com.sun.xml.bind.v2.model.core.ID;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import java.util.Collection;
import java.util.HashSet;
import com.sun.tools.xjc.model.CValuePropertyInfo;
import com.sun.xml.bind.v2.model.core.WildcardMode;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.model.CCustomizations;
import com.sun.xml.xsom.XSComponent;
import javax.xml.namespace.QName;
import com.sun.tools.xjc.reader.dtd.bindinfo.BIConversion;
import com.sun.tools.xjc.reader.dtd.bindinfo.BIElement;
import com.sun.tools.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.xjc.model.TypeUse;
import java.util.Iterator;
import java.util.ArrayList;
import org.xml.sax.Locator;
import com.sun.tools.xjc.model.CPropertyInfo;
import java.util.List;
import com.sun.tools.xjc.model.CClassInfo;

final class Element extends Term implements Comparable<Element>
{
    final String name;
    private final TDTDReader owner;
    private short contentModelType;
    private Term contentModel;
    boolean isReferenced;
    private CClassInfo classInfo;
    private boolean classInfoComputed;
    final List<CPropertyInfo> attributes;
    private final List<Block> normalizedBlocks;
    private boolean mustBeClass;
    private Locator locator;
    
    public Element(final TDTDReader owner, final String name) {
        this.attributes = new ArrayList<CPropertyInfo>();
        this.normalizedBlocks = new ArrayList<Block>();
        this.owner = owner;
        this.name = name;
    }
    
    void normalize(final List<Block> r, final boolean optional) {
        final Block o = new Block(optional, false);
        o.elements.add(this);
        r.add(o);
    }
    
    void addAllElements(final Block b) {
        b.elements.add(this);
    }
    
    boolean isOptional() {
        return false;
    }
    
    boolean isRepeated() {
        return false;
    }
    
    void define(final short contentModelType, final Term contentModel, final Locator locator) {
        assert this.contentModel == null;
        this.contentModelType = contentModelType;
        this.contentModel = contentModel;
        this.locator = locator;
        contentModel.normalize(this.normalizedBlocks, false);
        for (final Block b : this.normalizedBlocks) {
            if (b.isRepeated || b.elements.size() > 1) {
                for (final Element e : b.elements) {
                    this.owner.getOrCreateElement(e.name).mustBeClass = true;
                }
            }
        }
    }
    
    private TypeUse getConversion() {
        assert this.contentModel == Term.EMPTY;
        final BIElement e = this.owner.bindInfo.element(this.name);
        if (e != null) {
            final BIConversion conv = e.getConversion();
            if (conv != null) {
                return conv.getTransducer();
            }
        }
        return CBuiltinLeafInfo.STRING;
    }
    
    CClassInfo getClassInfo() {
        if (!this.classInfoComputed) {
            this.classInfoComputed = true;
            this.classInfo = this.calcClass();
        }
        return this.classInfo;
    }
    
    private CClassInfo calcClass() {
        final BIElement e = this.owner.bindInfo.element(this.name);
        if (e != null) {
            return e.clazz;
        }
        if (this.contentModelType != 2 || !this.attributes.isEmpty() || this.mustBeClass) {
            return this.createDefaultClass();
        }
        if (this.contentModel != Term.EMPTY) {
            throw new UnsupportedOperationException("mixed content model not supported");
        }
        if (this.isReferenced) {
            return null;
        }
        return this.createDefaultClass();
    }
    
    private CClassInfo createDefaultClass() {
        final String className = this.owner.model.getNameConverter().toClassName(this.name);
        final QName tagName = new QName("", this.name);
        return new CClassInfo(this.owner.model, this.owner.getTargetPackage(), className, this.locator, null, tagName, null, null);
    }
    
    void bind() {
        final CClassInfo ci = this.getClassInfo();
        assert !(!this.attributes.isEmpty());
        for (final CPropertyInfo p : this.attributes) {
            ci.addProperty(p);
        }
        switch (this.contentModelType) {
            case 1: {
                final CReferencePropertyInfo rp = new CReferencePropertyInfo("Content", true, true, null, null, this.locator);
                rp.setWildcard(WildcardMode.SKIP);
                ci.addProperty(rp);
                return;
            }
            case 2: {
                if (this.contentModel != Term.EMPTY) {
                    throw new UnsupportedOperationException("mixed content model unsupported yet");
                }
                if (ci != null) {
                    final CValuePropertyInfo p2 = new CValuePropertyInfo("value", null, null, this.locator, this.getConversion(), null);
                    ci.addProperty(p2);
                }
                return;
            }
            case 0: {
                assert ci != null;
                return;
            }
        }
        final List<Block> n = new ArrayList<Block>();
        this.contentModel.normalize(n, false);
        final Set<String> names = new HashSet<String>();
        boolean collision = false;
    Label_0330:
        for (final Block b : n) {
            for (final Element e : b.elements) {
                if (!names.add(e.name)) {
                    collision = true;
                    break Label_0330;
                }
            }
        }
        if (collision) {
            final Block all = new Block(true, true);
            for (final Block b2 : n) {
                all.elements.addAll(b2.elements);
            }
            n.clear();
            n.add(all);
        }
        for (final Block b3 : n) {
            CElementPropertyInfo p3;
            if (b3.isRepeated || b3.elements.size() > 1) {
                final StringBuilder name = new StringBuilder();
                for (final Element e : b3.elements) {
                    if (name.length() > 0) {
                        name.append("Or");
                    }
                    name.append(this.owner.model.getNameConverter().toPropertyName(e.name));
                }
                p3 = new CElementPropertyInfo(name.toString(), CElementPropertyInfo.CollectionMode.REPEATED_ELEMENT, ID.NONE, null, null, null, this.locator, !b3.isOptional);
                for (final Element e : b3.elements) {
                    final CClassInfo child = this.owner.getOrCreateElement(e.name).getClassInfo();
                    assert child != null;
                    p3.getTypes().add(new CTypeRef(child, new QName("", e.name), null, false, null));
                }
            }
            else {
                final String name2 = b3.elements.iterator().next().name;
                final String propName = this.owner.model.getNameConverter().toPropertyName(name2);
                final Element ref = this.owner.getOrCreateElement(name2);
                TypeUse refType;
                if (ref.getClassInfo() != null) {
                    refType = ref.getClassInfo();
                }
                else {
                    refType = ref.getConversion().getInfo();
                }
                p3 = new CElementPropertyInfo(propName, refType.isCollection() ? CElementPropertyInfo.CollectionMode.REPEATED_VALUE : CElementPropertyInfo.CollectionMode.NOT_REPEATED, ID.NONE, null, null, null, this.locator, !b3.isOptional);
                p3.getTypes().add(new CTypeRef(refType.getInfo(), new QName("", name2), null, false, null));
            }
            ci.addProperty(p3);
        }
    }
    
    public int compareTo(final Element that) {
        return this.name.compareTo(that.name);
    }
}
