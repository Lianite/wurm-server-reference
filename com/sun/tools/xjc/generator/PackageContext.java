// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator;

import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;

public final class PackageContext
{
    public final JPackage _package;
    public final JDefinedClass objectFactory;
    public final JVar rootTagMap;
    protected final VersionGenerator versionGenerator;
    protected final ObjectFactoryGenerator objectFactoryGenerator;
    
    protected PackageContext(final GeneratorContext _context, final AnnotatedGrammar _grammar, final Options _opt, final JPackage _pkg) {
        this._package = _pkg;
        this.versionGenerator = new VersionGenerator(_context, _grammar, _pkg.subPackage("impl"));
        this.objectFactoryGenerator = new ObjectFactoryGenerator(_context, _grammar, _opt, _pkg);
        this.objectFactory = this.objectFactoryGenerator.getObjectFactory();
        this.rootTagMap = this.objectFactoryGenerator.getRootTagMap();
        this.versionGenerator.generateVersionReference(this.objectFactory);
    }
}
