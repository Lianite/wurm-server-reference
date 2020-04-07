// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.xml.bind.v2.model.core.ElementInfo;
import com.sun.xml.bind.v2.model.core.NonElement;
import javax.xml.transform.Result;
import javax.xml.bind.annotation.XmlNsForm;
import org.xml.sax.helpers.LocatorImpl;
import com.sun.xml.xsom.XSComponent;
import java.util.Collections;
import com.sun.xml.bind.v2.model.core.Ref;
import com.sun.tools.xjc.model.nav.NavigatorImpl;
import com.sun.xml.bind.v2.model.nav.Navigator;
import java.util.Set;
import com.sun.tools.xjc.reader.xmlschema.Messages;
import java.util.HashSet;
import org.xml.sax.SAXException;
import com.sun.tools.xjc.generator.bean.BeanGenerator;
import org.xml.sax.ErrorHandler;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.api.ErrorListener;
import com.sun.tools.xjc.util.ErrorReceiverFilter;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.ErrorReceiver;
import com.sun.codemodel.JType;
import com.sun.xml.bind.v2.util.FlattenIterator;
import java.util.Iterator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.sun.tools.xjc.api.ClassNameAllocator;
import org.xml.sax.Locator;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.generator.bean.ImplStructureStrategy;
import com.sun.codemodel.JClass;
import javax.xml.bind.annotation.XmlAttribute;
import com.sun.tools.xjc.Options;
import javax.xml.bind.annotation.XmlTransient;
import com.sun.codemodel.JCodeModel;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.bind.api.impl.NameConverter;
import javax.xml.namespace.QName;
import java.util.Map;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.xml.bind.v2.model.core.TypeInfoSet;

public final class Model implements TypeInfoSet<NType, NClass, Void, Void>, CCustomizable
{
    private final Map<NClass, CClassInfo> beans;
    private final Map<NClass, CEnumLeafInfo> enums;
    private final Map<NClass, Map<QName, CElementInfo>> elementMappings;
    private final Iterable<? extends CElementInfo> allElements;
    private final Map<QName, TypeUse> typeUses;
    private NameConverter nameConverter;
    CCustomizations customizations;
    private boolean packageLevelAnnotations;
    public final XSSchemaSet schemaComponent;
    private CCustomizations gloablCustomizations;
    @XmlTransient
    public final JCodeModel codeModel;
    public final Options options;
    @XmlAttribute
    public boolean serializable;
    @XmlAttribute
    public Long serialVersionUID;
    @XmlTransient
    public JClass rootClass;
    @XmlTransient
    public JClass rootInterface;
    public ImplStructureStrategy strategy;
    final ClassNameAllocatorWrapper allocator;
    @XmlTransient
    public final SymbolSpace defaultSymbolSpace;
    private final Map<String, SymbolSpace> symbolSpaces;
    private final Map<JPackage, CClassInfoParent.Package> cache;
    static final Locator EMPTY_LOCATOR;
    
    public Model(final Options opts, final JCodeModel cm, final NameConverter nc, ClassNameAllocator allocator, final XSSchemaSet schemaComponent) {
        this.beans = new LinkedHashMap<NClass, CClassInfo>();
        this.enums = new LinkedHashMap<NClass, CEnumLeafInfo>();
        this.elementMappings = new HashMap<NClass, Map<QName, CElementInfo>>();
        this.allElements = new Iterable<CElementInfo>() {
            public Iterator<CElementInfo> iterator() {
                return new FlattenIterator<CElementInfo>(Model.this.elementMappings.values());
            }
        };
        this.typeUses = new LinkedHashMap<QName, TypeUse>();
        this.packageLevelAnnotations = true;
        this.gloablCustomizations = new CCustomizations();
        this.strategy = ImplStructureStrategy.BEAN_ONLY;
        this.symbolSpaces = new HashMap<String, SymbolSpace>();
        this.cache = new HashMap<JPackage, CClassInfoParent.Package>();
        this.options = opts;
        this.codeModel = cm;
        this.nameConverter = nc;
        (this.defaultSymbolSpace = new SymbolSpace(this.codeModel)).setType(this.codeModel.ref(Object.class));
        this.elementMappings.put(null, new HashMap<QName, CElementInfo>());
        if (opts.automaticNameConflictResolution) {
            allocator = new AutoClassNameAllocator(allocator);
        }
        this.allocator = new ClassNameAllocatorWrapper(allocator);
        this.schemaComponent = schemaComponent;
        this.gloablCustomizations.setParent(this, this);
    }
    
    public void setNameConverter(final NameConverter nameConverter) {
        assert this.nameConverter == null;
        assert nameConverter != null;
        this.nameConverter = nameConverter;
    }
    
    public final NameConverter getNameConverter() {
        return this.nameConverter;
    }
    
    public boolean isPackageLevelAnnotations() {
        return this.packageLevelAnnotations;
    }
    
    public void setPackageLevelAnnotations(final boolean packageLevelAnnotations) {
        this.packageLevelAnnotations = packageLevelAnnotations;
    }
    
    public SymbolSpace getSymbolSpace(final String name) {
        SymbolSpace ss = this.symbolSpaces.get(name);
        if (ss == null) {
            this.symbolSpaces.put(name, ss = new SymbolSpace(this.codeModel));
        }
        return ss;
    }
    
    public Outline generateCode(final Options opt, final ErrorReceiver receiver) {
        final ErrorReceiverFilter ehf = new ErrorReceiverFilter(receiver);
        for (final Plugin ma : opt.activePlugins) {
            ma.postProcessModel(this, ehf);
        }
        Outline o = BeanGenerator.generate(this, ehf);
        try {
            for (final Plugin ma2 : opt.activePlugins) {
                ma2.run(o, opt, ehf);
            }
        }
        catch (SAXException e) {
            return null;
        }
        final Set<CCustomizations> check = new HashSet<CCustomizations>();
        for (CCustomizations c = this.customizations; c != null; c = c.next) {
            if (!check.add(c)) {
                throw new AssertionError();
            }
            for (final CPluginCustomization p : c) {
                if (!p.isAcknowledged()) {
                    ehf.error(p.locator, Messages.format("UnusedCustomizationChecker.UnacknolwedgedCustomization", p.element.getNodeName()));
                    ehf.error(c.getOwner().getLocator(), Messages.format("UnusedCustomizationChecker.UnacknolwedgedCustomization.Relevant", new Object[0]));
                }
            }
        }
        if (ehf.hadError()) {
            o = null;
        }
        return o;
    }
    
    public final Map<QName, CClassInfo> createTopLevelBindings() {
        final Map<QName, CClassInfo> r = new HashMap<QName, CClassInfo>();
        for (final CClassInfo b : this.beans().values()) {
            if (b.isElement()) {
                r.put(b.getElementName(), b);
            }
        }
        return r;
    }
    
    public Navigator<NType, NClass, Void, Void> getNavigator() {
        return NavigatorImpl.theInstance;
    }
    
    public CNonElement getTypeInfo(final NType type) {
        final CBuiltinLeafInfo leaf = CBuiltinLeafInfo.LEAVES.get(type);
        if (leaf != null) {
            return leaf;
        }
        return this.getClassInfo((NClass)this.getNavigator().asDecl(type));
    }
    
    public CBuiltinLeafInfo getAnyTypeInfo() {
        return CBuiltinLeafInfo.ANYTYPE;
    }
    
    public CNonElement getTypeInfo(final Ref<NType, NClass> ref) {
        assert !ref.valueList;
        return this.getTypeInfo((NType)ref.type);
    }
    
    public Map<NClass, CClassInfo> beans() {
        return this.beans;
    }
    
    public Map<NClass, CEnumLeafInfo> enums() {
        return this.enums;
    }
    
    public Map<QName, TypeUse> typeUses() {
        return this.typeUses;
    }
    
    public Map<NType, ? extends CArrayInfo> arrays() {
        return Collections.emptyMap();
    }
    
    public Map<NType, ? extends CBuiltinLeafInfo> builtins() {
        return CBuiltinLeafInfo.LEAVES;
    }
    
    public CClassInfo getClassInfo(final NClass t) {
        return this.beans.get(t);
    }
    
    public CElementInfo getElementInfo(final NClass scope, final QName name) {
        final Map<QName, CElementInfo> m = this.elementMappings.get(scope);
        if (m != null) {
            final CElementInfo r = m.get(name);
            if (r != null) {
                return r;
            }
        }
        return this.elementMappings.get(null).get(name);
    }
    
    public Map<QName, CElementInfo> getElementMappings(final NClass scope) {
        return this.elementMappings.get(scope);
    }
    
    public Iterable<? extends CElementInfo> getAllElements() {
        return this.allElements;
    }
    
    public XSComponent getSchemaComponent() {
        return null;
    }
    
    public Locator getLocator() {
        final LocatorImpl r = new LocatorImpl();
        r.setLineNumber(-1);
        r.setColumnNumber(-1);
        return r;
    }
    
    public CCustomizations getCustomizations() {
        return this.gloablCustomizations;
    }
    
    public Map<String, String> getXmlNs(final String namespaceUri) {
        return Collections.emptyMap();
    }
    
    public Map<String, String> getSchemaLocations() {
        return Collections.emptyMap();
    }
    
    public XmlNsForm getElementFormDefault(final String nsUri) {
        throw new UnsupportedOperationException();
    }
    
    public XmlNsForm getAttributeFormDefault(final String nsUri) {
        throw new UnsupportedOperationException();
    }
    
    public void dump(final Result out) {
        throw new UnsupportedOperationException();
    }
    
    void add(final CEnumLeafInfo e) {
        this.enums.put(e.getClazz(), e);
    }
    
    void add(final CClassInfo ci) {
        this.beans.put(ci.getClazz(), ci);
    }
    
    void add(final CElementInfo ei) {
        NClass clazz = null;
        if (ei.getScope() != null) {
            clazz = ei.getScope().getClazz();
        }
        Map<QName, CElementInfo> m = this.elementMappings.get(clazz);
        if (m == null) {
            this.elementMappings.put(clazz, m = new HashMap<QName, CElementInfo>());
        }
        m.put(ei.getElementName(), ei);
    }
    
    public CClassInfoParent.Package getPackage(final JPackage pkg) {
        CClassInfoParent.Package r = this.cache.get(pkg);
        if (r == null) {
            this.cache.put(pkg, r = new CClassInfoParent.Package(pkg));
        }
        return r;
    }
    
    static {
        final LocatorImpl l = new LocatorImpl();
        l.setColumnNumber(-1);
        l.setLineNumber(-1);
        EMPTY_LOCATOR = l;
    }
}
