// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.relaxng;

import org.kohsuke.rngom.nc.NameClass;
import org.kohsuke.rngom.digested.DRefPattern;
import org.kohsuke.rngom.digested.DPatternWalker;
import org.kohsuke.rngom.digested.DElementPattern;
import com.sun.tools.xjc.model.TypeUse;
import java.util.List;
import com.sun.tools.xjc.model.CCustomizations;
import com.sun.xml.xsom.XSComponent;
import javax.xml.namespace.QName;
import com.sun.tools.xjc.model.CEnumLeafInfo;
import java.util.Collection;
import com.sun.tools.xjc.model.CClassInfoParent;
import com.sun.tools.xjc.model.CNonElement;
import com.sun.tools.xjc.model.CBuiltinLeafInfo;
import org.kohsuke.rngom.digested.DValuePattern;
import org.kohsuke.rngom.digested.DChoicePattern;
import com.sun.tools.xjc.model.CEnumConstant;
import java.util.ArrayList;
import java.util.Iterator;
import org.kohsuke.rngom.digested.DPatternVisitor;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.bind.api.impl.NameConverter;
import java.util.HashMap;
import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import java.util.Map;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.Options;
import org.kohsuke.rngom.digested.DDefine;
import java.util.Set;
import org.kohsuke.rngom.digested.DPattern;

public final class RELAXNGCompiler
{
    final DPattern grammar;
    final Set<DDefine> defs;
    final Options opts;
    final Model model;
    final JPackage pkg;
    final Map<String, DatatypeLib> datatypes;
    final Map<DPattern, CTypeInfo[]> classes;
    final Map<CClassInfo, DPattern> bindQueue;
    final TypeUseBinder typeUseBinder;
    
    public static Model build(final DPattern grammar, final JCodeModel codeModel, final Options opts) {
        final RELAXNGCompiler compiler = new RELAXNGCompiler(grammar, codeModel, opts);
        compiler.compile();
        return compiler.model;
    }
    
    public RELAXNGCompiler(final DPattern grammar, final JCodeModel codeModel, final Options opts) {
        this.datatypes = new HashMap<String, DatatypeLib>();
        this.classes = new HashMap<DPattern, CTypeInfo[]>();
        this.bindQueue = new HashMap<CClassInfo, DPattern>();
        this.typeUseBinder = new TypeUseBinder(this);
        this.grammar = grammar;
        this.opts = opts;
        this.model = new Model(opts, codeModel, NameConverter.smart, opts.classNameAllocator, null);
        this.datatypes.put("", DatatypeLib.BUILTIN);
        this.datatypes.put("http://www.w3.org/2001/XMLSchema-datatypes", DatatypeLib.XMLSCHEMA);
        final DefineFinder deff = new DefineFinder();
        grammar.accept((DPatternVisitor<Object>)deff);
        this.defs = deff.defs;
        if (opts.defaultPackage2 != null) {
            this.pkg = codeModel._package(opts.defaultPackage2);
        }
        else if (opts.defaultPackage != null) {
            this.pkg = codeModel._package(opts.defaultPackage);
        }
        else {
            this.pkg = codeModel.rootPackage();
        }
    }
    
    private void compile() {
        this.promoteElementDefsToClasses();
        this.promoteTypeSafeEnums();
        this.promoteTypePatternsToClasses();
        for (final Map.Entry<CClassInfo, DPattern> e : this.bindQueue.entrySet()) {
            this.bindContentModel(e.getKey(), e.getValue());
        }
    }
    
    private void bindContentModel(final CClassInfo clazz, final DPattern pattern) {
        pattern.accept((DPatternVisitor<Object>)new ContentModelBinder(this, clazz));
    }
    
    private void promoteTypeSafeEnums() {
        final List<CEnumConstant> members = new ArrayList<CEnumConstant>();
    Label_0018:
        for (final DDefine def : this.defs) {
            final DPattern p = def.getPattern();
            if (p instanceof DChoicePattern) {
                final DChoicePattern cp = (DChoicePattern)p;
                members.clear();
                DValuePattern vp = null;
                for (final DPattern child : cp) {
                    if (!(child instanceof DValuePattern)) {
                        continue Label_0018;
                    }
                    final DValuePattern c = (DValuePattern)child;
                    if (vp == null) {
                        vp = c;
                    }
                    else {
                        if (!vp.getDatatypeLibrary().equals(c.getDatatypeLibrary())) {
                            continue Label_0018;
                        }
                        if (!vp.getType().equals(c.getType())) {
                            continue Label_0018;
                        }
                    }
                    members.add(new CEnumConstant(this.model.getNameConverter().toConstantName(c.getValue()), null, c.getValue(), c.getLocation()));
                }
                if (members.isEmpty()) {
                    continue;
                }
                CNonElement base = CBuiltinLeafInfo.STRING;
                final DatatypeLib lib = this.datatypes.get(vp.getNs());
                if (lib != null) {
                    final TypeUse use = lib.get(vp.getType());
                    if (use instanceof CNonElement) {
                        base = (CNonElement)use;
                    }
                }
                final CEnumLeafInfo xducer = new CEnumLeafInfo(this.model, null, new CClassInfoParent.Package(this.pkg), def.getName(), base, new ArrayList<CEnumConstant>(members), null, null, cp.getLocation());
                this.classes.put(cp, new CTypeInfo[] { xducer });
            }
        }
    }
    
    private void promoteElementDefsToClasses() {
        for (final DDefine def : this.defs) {
            final DPattern p = def.getPattern();
            if (p instanceof DElementPattern) {
                final DElementPattern ep = (DElementPattern)p;
                this.mapToClass(ep);
            }
        }
        this.grammar.accept((DPatternVisitor<Object>)new DPatternWalker() {
            public Void onRef(final DRefPattern p) {
                return null;
            }
            
            public Void onElement(final DElementPattern p) {
                RELAXNGCompiler.this.mapToClass(p);
                return null;
            }
        });
    }
    
    private void mapToClass(final DElementPattern p) {
        final NameClass nc = p.getName();
        if (nc.isOpen()) {
            return;
        }
        final Set<QName> names = nc.listNames();
        final CClassInfo[] types = new CClassInfo[names.size()];
        int i = 0;
        for (final QName n : names) {
            final String name = this.model.getNameConverter().toClassName(n.getLocalPart());
            this.bindQueue.put(types[i++] = new CClassInfo(this.model, this.pkg, name, p.getLocation(), null, n, null, null), p.getChild());
        }
        this.classes.put(p, types);
    }
    
    private void promoteTypePatternsToClasses() {
    }
}
