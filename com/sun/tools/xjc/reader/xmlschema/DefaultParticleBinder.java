// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.xml.xsom.XSComponent;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIProperty;
import java.util.Iterator;
import java.util.ArrayList;
import com.sun.tools.xjc.model.CPropertyInfo;
import java.util.List;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSTerm;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import com.sun.xml.xsom.visitor.XSTermVisitor;
import com.sun.msv.grammar.Expression;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.xml.xsom.XSParticle;

class DefaultParticleBinder extends ParticleBinder
{
    DefaultParticleBinder(final BGMBuilder builder) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: ddiv           
        //     2: istore_0        /* this */
        //     3: dload_3        
        //     4: sastore        
        //     5: aconst_null    
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ----------------------------------------------------------
        //  0      6       0     this     Lcom/sun/tools/xjc/reader/xmlschema/DefaultParticleBinder;
        //  0      6       1     builder  Lcom/sun/tools/xjc/reader/xmlschema/BGMBuilder;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Expression build(final XSParticle p, final ClassItem superClass) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/xmlschema/DefaultParticleBinder.build:(Lcom/sun/xml/xsom/XSParticle;Lcom/sun/tools/xjc/grammar/ClassItem;)Lcom/sun/msv/grammar/Expression;'.
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:65)
        //     at com.strobel.assembler.metadata.MethodDefinition.tryLoadBody(MethodDefinition.java:722)
        //     at com.strobel.assembler.metadata.MethodDefinition.getBody(MethodDefinition.java:83)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:202)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:757)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:655)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:532)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:499)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:141)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:130)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:105)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:317)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:238)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:123)
        // Caused by: java.lang.IllegalArgumentException: Argument 'index' must be in the range [0, 128], but value was: 10281.
        //     at com.strobel.core.VerifyArgument.inRange(VerifyArgument.java:346)
        //     at com.strobel.assembler.ir.ConstantPool.get(ConstantPool.java:78)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1313)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:293)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean checkFallback(final XSParticle p, final ClassItem superClass) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ldiv           
        //     1: laload         
        //     2: drem           
        //     3: lneg           
        //     4: fdiv           
        //     5: laload         
        //     6: ineg           
        //     7: ddiv           
        //     8: ddiv           
        //     9: idiv           
        //    10: drem           
        //    11: laload         
        //    12: ishl           
        //    13: fmul           
        //    14: dadd           
        //    15: laload         
        //    16: ldiv           
        //    17: ddiv           
        //    18: isub           
        //    19: lsub           
        //    20: idiv           
        //    21: laload         
        //    22: fstore_0        /* this */
        //    23: lastore        
        //    24: frem           
        //    25: ddiv           
        //    26: irem           
        //    27: lsub           
        //    28: frem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name        Signature
        //  -----  ------  ----  ----------  ------------------------------------------------------------------
        //  0      29      0     this        Lcom/sun/tools/xjc/reader/xmlschema/DefaultParticleBinder;
        //  0      29      1     p           Lcom/sun/xml/xsom/XSParticle;
        //  0      29      2     superClass  Lcom/sun/tools/xjc/grammar/ClassItem;
        //  10     19      3     checker     Lcom/sun/tools/xjc/reader/xmlschema/DefaultParticleBinder$Checker;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private final class Checker implements XSTermVisitor
    {
        private CollisionInfo collisionInfo;
        private final NameCollisionChecker cchecker;
        private final Collection<XSParticle> forcedProps;
        private XSParticle outerParticle;
        public final Map<XSParticle, String> markedParticles;
        private final Map<XSParticle, String> labelCache;
        
        Checker(final Collection<XSParticle> forcedProps) {
            this.collisionInfo = null;
            this.cchecker = new NameCollisionChecker();
            this.markedParticles = new HashMap<XSParticle, String>();
            this.labelCache = new Hashtable<XSParticle, String>();
            this.forcedProps = forcedProps;
        }
        
        boolean hasNameCollision() {
            return this.collisionInfo != null;
        }
        
        CollisionInfo getCollisionInfo() {
            return this.collisionInfo;
        }
        
        public void particle(final XSParticle p) {
            if (DefaultParticleBinder.this.getLocalPropCustomization(p) != null || DefaultParticleBinder.this.builder.getLocalDomCustomization(p) != null) {
                this.check(p);
                this.mark(p);
                return;
            }
            final XSTerm t = p.getTerm();
            if (p.isRepeated() && (t.isModelGroup() || t.isModelGroupDecl())) {
                this.mark(p);
                return;
            }
            if (this.forcedProps.contains(p)) {
                this.mark(p);
                return;
            }
            this.outerParticle = p;
            t.visit(this);
        }
        
        public void elementDecl(final XSElementDecl decl) {
            this.check(this.outerParticle);
            this.mark(this.outerParticle);
        }
        
        public void modelGroup(final XSModelGroup mg) {
            if (mg.getCompositor() == XSModelGroup.Compositor.CHOICE && DefaultParticleBinder.this.builder.getGlobalBinding().isChoiceContentPropertyEnabled()) {
                this.mark(this.outerParticle);
                return;
            }
            for (final XSParticle child : mg.getChildren()) {
                this.particle(child);
            }
        }
        
        public void modelGroupDecl(final XSModelGroupDecl decl) {
            this.modelGroup(decl.getModelGroup());
        }
        
        public void wildcard(final XSWildcard wc) {
            this.mark(this.outerParticle);
        }
        
        void readSuperClass(final CClassInfo ci) {
            this.cchecker.readSuperClass(ci);
        }
        
        private void check(final XSParticle p) {
            if (this.collisionInfo == null) {
                this.collisionInfo = this.cchecker.check(p);
            }
        }
        
        private void mark(final XSParticle p) {
            this.markedParticles.put(p, this.computeLabel(p));
        }
        
        private String computeLabel(final XSParticle p) {
            String label = this.labelCache.get(p);
            if (label == null) {
                this.labelCache.put(p, label = DefaultParticleBinder.this.computeLabel(p));
            }
            return label;
        }
        
        final class Range
        {
            int start;
            int end;
            
            Range(final int s, final int e) {
                this.start = s;
                this.end = e;
            }
        }
        
        private final class NameCollisionChecker
        {
            private final List<XSParticle> particles;
            private final Map<String, CPropertyInfo> occupiedLabels;
            
            private NameCollisionChecker() {
                this.particles = new ArrayList<XSParticle>();
                this.occupiedLabels = new HashMap<String, CPropertyInfo>();
            }
            
            CollisionInfo check(final XSParticle p) {
                final String label = Checker.this.computeLabel(p);
                if (this.occupiedLabels.containsKey(label)) {
                    return new CollisionInfo(label, p.getLocator(), this.occupiedLabels.get(label).locator);
                }
                for (final XSParticle jp : this.particles) {
                    if (!this.check(p, jp)) {
                        return new CollisionInfo(label, p.getLocator(), jp.getLocator());
                    }
                }
                this.particles.add(p);
                return null;
            }
            
            private boolean check(final XSParticle p1, final XSParticle p2) {
                return !Checker.this.computeLabel(p1).equals(Checker.this.computeLabel(p2));
            }
            
            void readSuperClass(CClassInfo base) {
                while (base != null) {
                    for (final CPropertyInfo p : base.getProperties()) {
                        this.occupiedLabels.put(p.getName(true), p);
                    }
                    base = base.getBaseClass();
                }
            }
        }
    }
    
    private final class Builder implements XSTermVisitor
    {
        private final Map<XSParticle, String> markedParticles;
        private boolean insideOptionalParticle;
        
        Builder(final Map<XSParticle, String> markedParticles) {
            this.markedParticles = markedParticles;
        }
        
        private boolean marked(final XSParticle p) {
            return this.markedParticles.containsKey(p);
        }
        
        private String getLabel(final XSParticle p) {
            return this.markedParticles.get(p);
        }
        
        public void particle(final XSParticle p) {
            final XSTerm t = p.getTerm();
            if (this.marked(p)) {
                final BIProperty cust = BIProperty.getCustomization(p);
                final CPropertyInfo prop = cust.createElementOrReferenceProperty(this.getLabel(p), false, p, RawTypeSetBuilder.build(p, this.insideOptionalParticle));
                DefaultParticleBinder.this.getCurrentBean().addProperty(prop);
            }
            else {
                assert !p.isRepeated();
                final boolean oldIOP = this.insideOptionalParticle;
                this.insideOptionalParticle |= (p.getMinOccurs() == 0);
                t.visit(this);
                this.insideOptionalParticle = oldIOP;
            }
        }
        
        public void elementDecl(final XSElementDecl e) {
            assert false;
        }
        
        public void wildcard(final XSWildcard wc) {
            assert false;
        }
        
        public void modelGroupDecl(final XSModelGroupDecl decl) {
            this.modelGroup(decl.getModelGroup());
        }
        
        public void modelGroup(final XSModelGroup mg) {
            final boolean oldIOP = this.insideOptionalParticle;
            this.insideOptionalParticle |= (mg.getCompositor() == XSModelGroup.CHOICE);
            for (final XSParticle p : mg.getChildren()) {
                this.particle(p);
            }
            this.insideOptionalParticle = oldIOP;
        }
    }
}
