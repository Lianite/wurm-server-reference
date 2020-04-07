// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl;

import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.xml.xsom.visitor.XSTermFunction;
import com.sun.xml.xsom.visitor.XSTermVisitor;
import com.sun.xml.xsom.visitor.XSVisitor;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSWildcard;
import org.xml.sax.Locator;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSModelGroup;

public class ModelGroupImpl extends ComponentImpl implements XSModelGroup, Ref.Term
{
    private final XSParticle[] children;
    private final Compositor compositor;
    
    public ModelGroupImpl(final SchemaImpl owner, final AnnotationImpl _annon, final Locator _loc, final Compositor _compositor, final XSParticle[] _children) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: iload_1         /* owner */
        //     1: fconst_1       
        //     2: nop            
        //     3: iload           this
        //     5: lload           owner
        //     7: nop            
        //     8: fload_0         /* this */
        //     9: fmul           
        //    10: ladd           
        //    11: fneg           
        //    12: ladd           
        //    13: laload         
        //    14: idiv           
        //    15: ladd           
        //    16: fdiv           
        //    17: dsub           
        //    18: laload         
        //    19: dstore_2        /* _annon */
        //    20: idiv           
        //    21: idiv           
        //    22: lsub           
        //    23: dsub           
        //    24: ladd           
        //    25: idiv           
        //    26: lstore_2        /* _annon */
        //    27: frem           
        //    28: dsub           
        //    29: lneg           
        //    30: ldiv           
        //    31: lsub           
        //    32: fdiv           
        //    33: ineg           
        //    34: fstore_2        /* _annon */
        //    35: ishl           
        //    36: dadd           
        //    37: lsub           
        //    38: irem           
        //    39: ineg           
        //    40: lmul           
        //    41: ddiv           
        //    42: fdiv           
        //    43: fconst_1       
        //    44: nop            
        //    45: iload_2         /* _annon */
        //    46: nop            
        //    47: f2i            
        //    48: iconst_4       
        //    49: nop            
        //    50: f2l            
        //    51: fconst_1       
        //    52: nop            
        //    53: fstore          this
        //    55: dstore          owner
        //    57: nop            
        //    58: lload_0         /* this */
        //    59: dup_x2         
        //    60: astore_1        /* owner */
        //    61: dadd           
        //    62: ddiv           
        //    63: ldiv           
        //    64: laload         
        //    65: drem           
        //    66: lneg           
        //    67: fdiv           
        //    68: laload         
        //    69: ishl           
        //    70: ldiv           
        //    71: idiv           
        //    72: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name         Signature
        //  -----  ------  ----  -----------  ------------------------------------------
        //  43     29      6     i            I
        //  0      73      0     this         Lcom/sun/xml/xsom/impl/ModelGroupImpl;
        //  0      73      1     owner        Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      73      2     _annon       Lcom/sun/xml/xsom/impl/AnnotationImpl;
        //  0      73      3     _loc         Lorg/xml/sax/Locator;
        //  0      73      4     _compositor  Lcom/sun/xml/xsom/XSModelGroup$Compositor;
        //  0      73      5     _children    [Lcom/sun/xml/xsom/XSParticle;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSParticle getChild(final int idx) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dadd           
        //     1: ddiv           
        //     2: ldiv           
        //     3: laload         
        //     4: drem           
        //     5: lneg           
        //     6: fdiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------
        //  0      7       0     this  Lcom/sun/xml/xsom/impl/ModelGroupImpl;
        //  0      7       1     idx   I
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public int getSize() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: dload_2        
        //     2: dadd           
        //     3: ddiv           
        //     4: ldiv           
        //     5: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------
        //  0      6       0     this  Lcom/sun/xml/xsom/impl/ModelGroupImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSParticle[] getChildren() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ldiv           
        //     1: laload         
        //     2: lmul           
        //     3: ldiv           
        //     4: irem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/ModelGroupImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Compositor getCompositor() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ladd           
        //     1: ineg           
        //     2: lmul           
        //     3: ddiv           
        //     4: fdiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/ModelGroupImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void redefine(final ModelGroupDeclImpl oldMG) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lstore_2       
        //     1: ineg           
        //     2: ineg           
        //     3: frem           
        //     4: lmul           
        //     5: fadd           
        //     6: lneg           
        //     7: ineg           
        //     8: lsub           
        //     9: drem           
        //    10: dstore_2        /* i */
        //    11: ldiv           
        //    12: irem           
        //    13: idiv           
        //    14: istore_0        /* this */
        //    15: dload_3        
        //    16: sastore        
        //    17: aconst_null    
        //    18: nop            
        //    19: iconst_0       
        //    20: dload_2         /* i */
        //    21: dload_3         /* p */
        //    22: sastore        
        //    23: aconst_null    
        //    24: nop            
        //    25: fload_0         /* this */
        //    26: dadd           
        //    27: ddiv           
        //    28: ldiv           
        //    29: laload         
        //    30: drem           
        //    31: lneg           
        //    32: fdiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name   Signature
        //  -----  ------  ----  -----  ------------------------------------------
        //  21     5       3     p      Lcom/sun/xml/xsom/impl/ParticleImpl;
        //  2      30      2     i      I
        //  0      33      0     this   Lcom/sun/xml/xsom/impl/ModelGroupImpl;
        //  0      33      1     oldMG  Lcom/sun/xml/xsom/impl/ModelGroupDeclImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isWildcard() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: lneg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ModelGroupImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isModelGroupDecl() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lneg           
        //     1: irem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ModelGroupImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isModelGroup() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: fneg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ModelGroupImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isElementDecl() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fdiv           
        //     1: dadd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ModelGroupImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSWildcard asWildcard() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ineg           
        //     1: istore_0        /* this */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ModelGroupImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSModelGroupDecl asModelGroupDecl() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dadd           
        //     1: ddiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ModelGroupImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSModelGroup asModelGroup() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dsub           
        //     1: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ModelGroupImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSElementDecl asElementDecl() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ishl           
        //     1: ldiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ModelGroupImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void visit(final XSVisitor visitor) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/ModelGroupImpl.visit:(Lcom/sun/xml/xsom/visitor/XSVisitor;)V'.
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
        // Caused by: java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void visit(final XSTermVisitor visitor) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/ModelGroupImpl.visit:(Lcom/sun/xml/xsom/visitor/XSTermVisitor;)V'.
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
        // Caused by: java.nio.BufferUnderflowException
        //     at com.strobel.assembler.metadata.Buffer.verifyReadableBytes(Buffer.java:387)
        //     at com.strobel.assembler.metadata.Buffer.readShort(Buffer.java:219)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:231)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object apply(final XSTermFunction function) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: iconst_4       
        //     2: nop            
        //     3: nop            
        //     4: nop            
        //     5: astore_0        /* this */
        //     6: nop            
        //     7: fload_1         /* function */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  -----------------------------------------
        //  0      8       0     this      Lcom/sun/xml/xsom/impl/ModelGroupImpl;
        //  0      8       1     function  Lcom/sun/xml/xsom/visitor/XSTermFunction;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object apply(final XSFunction function) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: laload         
        //     2: nop            
        //     3: faload         
        //     4: nop            
        //     5: aconst_null    
        //     6: nop            
        //     7: lload_0         /* this */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  --------------------------------------
        //  0      8       0     this      Lcom/sun/xml/xsom/impl/ModelGroupImpl;
        //  0      8       1     function  Lcom/sun/xml/xsom/visitor/XSFunction;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSTerm getTerm() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aaload         
        //     1: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ModelGroupImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
