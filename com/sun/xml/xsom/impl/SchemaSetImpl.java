// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl;

import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSModelGroup;
import java.util.List;
import com.sun.xml.xsom.XSUnionSimpleType;
import com.sun.xml.xsom.XSListSimpleType;
import com.sun.xml.xsom.XSVariety;
import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.xml.xsom.visitor.XSContentTypeFunction;
import com.sun.xml.xsom.visitor.XSSimpleTypeFunction;
import com.sun.xml.xsom.visitor.XSVisitor;
import com.sun.xml.xsom.visitor.XSContentTypeVisitor;
import com.sun.xml.xsom.visitor.XSSimpleTypeVisitor;
import java.util.Collections;
import java.util.Collection;
import com.sun.xml.xsom.impl.scd.Iterators;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSSchema;
import org.xml.sax.Locator;
import java.util.Iterator;
import java.util.Vector;
import java.util.Map;
import com.sun.xml.xsom.XSSchemaSet;

public class SchemaSetImpl implements XSSchemaSet
{
    private final Map schemas;
    private final Vector schemas2;
    public final EmptyImpl empty;
    public final AnySimpleType anySimpleType;
    public final AnyType anyType;
    private static final Iterator emptyIterator;
    
    public SchemaSetImpl() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/SchemaSetImpl.<init>:()V'.
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:65)
        //     at com.strobel.assembler.metadata.MethodDefinition.tryLoadBody(MethodDefinition.java:722)
        //     at com.strobel.assembler.metadata.MethodDefinition.getBody(MethodDefinition.java:83)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:202)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:757)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:692)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:529)
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
    
    public SchemaImpl createSchema(final String targetNamespace, final Locator location) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/SchemaSetImpl.createSchema:(Ljava/lang/String;Lorg/xml/sax/Locator;)Lcom/sun/xml/xsom/impl/SchemaImpl;'.
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
    
    public int getSchemaSize() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: aastore        
        //     2: dadd           
        //     3: imul           
        //     4: lsub           
        //     5: ldiv           
        //     6: ladd           
        //     7: aastore        
        //     8: lsub           
        //     9: ineg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------
        //  0      10      0     this  Lcom/sun/xml/xsom/impl/SchemaSetImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSSchema getSchema(final String targetNamespace) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: ishl           
        //     2: ldiv           
        //     3: idiv           
        //     4: laload         
        //     5: ishl           
        //     6: drem           
        //     7: ddiv           
        //     8: ldiv           
        //     9: laload         
        //    10: lmul           
        //    11: ldiv           
        //    12: irem           
        //    13: idiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name             Signature
        //  -----  ------  ----  ---------------  -------------------------------------
        //  0      14      0     this             Lcom/sun/xml/xsom/impl/SchemaSetImpl;
        //  0      14      1     targetNamespace  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSSchema getSchema(final int idx) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: ishl           
        //     2: ldiv           
        //     3: idiv           
        //     4: laload         
        //     5: ishl           
        //     6: drem           
        //     7: ddiv           
        //     8: ldiv           
        //     9: laload         
        //    10: lmul           
        //    11: ldiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------
        //  0      12      0     this  Lcom/sun/xml/xsom/impl/SchemaSetImpl;
        //  0      12      1     idx   I
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator iterateSchema() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lneg           
        //     1: fdiv           
        //     2: laload         
        //     3: ishl           
        //     4: ldiv           
        //     5: idiv           
        //     6: laload         
        //     7: ishl           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------
        //  0      8       0     this  Lcom/sun/xml/xsom/impl/SchemaSetImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSSimpleType getSimpleType(final String ns, final String localName) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/SchemaSetImpl.getSimpleType:(Ljava/lang/String;Ljava/lang/String;)Lcom/sun/xml/xsom/XSSimpleType;'.
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
    
    public XSElementDecl getElementDecl(final String ns, final String localName) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: lmul           
        //     2: ldiv           
        //     3: irem           
        //     4: idiv           
        //     5: laload         
        //     6: aastore        
        //     7: dadd           
        //     8: imul           
        //     9: lsub           
        //    10: ldiv           
        //    11: ladd           
        //    12: aastore        
        //    13: lsub           
        //    14: ineg           
        //    15: dstore_2        /* localName */
        //    16: ldiv           
        //    17: irem           
        //    18: idiv           
        //    19: aconst_null    
        //    LocalVariableTable:
        //  Start  Length  Slot  Name       Signature
        //  -----  ------  ----  ---------  -------------------------------------
        //  0      20      0     this       Lcom/sun/xml/xsom/impl/SchemaSetImpl;
        //  0      20      1     ns         Ljava/lang/String;
        //  0      20      2     localName  Ljava/lang/String;
        //  6      14      3     schema     Lcom/sun/xml/xsom/XSSchema;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSAttributeDecl getAttributeDecl(final String ns, final String localName) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fload_0         /* this */
        //     1: dload_2         /* localName */
        //     2: astore_1        /* ns */
        //     3: fmul           
        //     4: ladd           
        //     5: fneg           
        //     6: ladd           
        //     7: laload         
        //     8: lneg           
        //     9: ineg           
        //    10: lmul           
        //    11: idiv           
        //    12: laload         
        //    13: astore_1        /* ns */
        //    14: lmul           
        //    15: drem           
        //    16: ineg           
        //    17: istore_0        /* this */
        //    18: dload_3         /* schema */
        //    19: astore_1        /* ns */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name       Signature
        //  -----  ------  ----  ---------  -------------------------------------
        //  0      20      0     this       Lcom/sun/xml/xsom/impl/SchemaSetImpl;
        //  0      20      1     ns         Ljava/lang/String;
        //  0      20      2     localName  Ljava/lang/String;
        //  6      14      3     schema     Lcom/sun/xml/xsom/XSSchema;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSModelGroupDecl getModelGroupDecl(final String ns, final String localName) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: iastore        
        //     2: fadd           
        //     3: fmul           
        //     4: lsub           
        //     5: dadd           
        //     6: ineg           
        //     7: istore_0        /* this */
        //     8: dload_3         /* schema */
        //     9: astore_1        /* ns */
        //    10: fmul           
        //    11: ladd           
        //    12: fneg           
        //    13: ladd           
        //    14: laload         
        //    15: idiv           
        //    16: ladd           
        //    17: fdiv           
        //    18: dsub           
        //    19: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name       Signature
        //  -----  ------  ----  ---------  -------------------------------------
        //  0      20      0     this       Lcom/sun/xml/xsom/impl/SchemaSetImpl;
        //  0      20      1     ns         Ljava/lang/String;
        //  0      20      2     localName  Ljava/lang/String;
        //  6      14      3     schema     Lcom/sun/xml/xsom/XSSchema;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSAttGroupDecl getAttGroupDecl(final String ns, final String localName) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ineg           
        //     1: aconst_null    
        //     2: nop            
        //     3: fstore          40
        //     5: astore_1        /* ns */
        //     6: fmul           
        //     7: ladd           
        //     8: fneg           
        //     9: ladd           
        //    10: laload         
        //    11: idiv           
        //    12: ladd           
        //    13: fdiv           
        //    14: dsub           
        //    15: laload         
        //    16: iastore        
        //    17: fadd           
        //    18: fmul           
        //    19: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name       Signature
        //  -----  ------  ----  ---------  -------------------------------------
        //  0      20      0     this       Lcom/sun/xml/xsom/impl/SchemaSetImpl;
        //  0      20      1     ns         Ljava/lang/String;
        //  0      20      2     localName  Ljava/lang/String;
        //  6      14      3     schema     Lcom/sun/xml/xsom/XSSchema;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSComplexType getComplexType(final String ns, final String localName) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: iastore        
        //     1: fadd           
        //     2: fmul           
        //     3: lsub           
        //     4: dadd           
        //     5: ineg           
        //     6: istore_0        /* this */
        //     7: aconst_null    
        //     8: nop            
        //     9: iconst_5       
        //    10: lmul           
        //    11: ineg           
        //    12: lsub           
        //    13: frem           
        //    14: ladd           
        //    15: ineg           
        //    16: ddiv           
        //    17: frem           
        //    18: aconst_null    
        //    19: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name       Signature
        //  -----  ------  ----  ---------  -------------------------------------
        //  0      20      0     this       Lcom/sun/xml/xsom/impl/SchemaSetImpl;
        //  0      20      1     ns         Ljava/lang/String;
        //  0      20      2     localName  Ljava/lang/String;
        //  6      14      3     schema     Lcom/sun/xml/xsom/XSSchema;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator iterateElementDecls() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: bastore        
        //     2: lshl           
        //     3: irem           
        //     4: lsub           
        //     5: istore_0        /* this */
        //     6: aconst_null    
        //     7: nop            
        //     8: caload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------
        //  0      9       0     this  Lcom/sun/xml/xsom/impl/SchemaSetImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator iterateTypes() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: astore_1       
        //     1: fmul           
        //     2: ladd           
        //     3: fneg           
        //     4: ladd           
        //     5: laload         
        //     6: idiv           
        //     7: ladd           
        //     8: fdiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------
        //  0      9       0     this  Lcom/sun/xml/xsom/impl/SchemaSetImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator iterateAttributeDecls() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fdiv           
        //     1: dsub           
        //     2: laload         
        //     3: aastore        
        //     4: ineg           
        //     5: frem           
        //     6: lmul           
        //     7: fdiv           
        //     8: dsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------
        //  0      9       0     this  Lcom/sun/xml/xsom/impl/SchemaSetImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator iterateAttGroupDecls() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fdiv           
        //     1: dsub           
        //     2: istore_0        /* this */
        //     3: dload_3        
        //     4: astore_1       
        //     5: dadd           
        //     6: ddiv           
        //     7: ldiv           
        //     8: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------
        //  0      9       0     this  Lcom/sun/xml/xsom/impl/SchemaSetImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator iterateModelGroupDecls() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: drem           
        //     1: lneg           
        //     2: fdiv           
        //     3: laload         
        //     4: ishl           
        //     5: ldiv           
        //     6: idiv           
        //     7: laload         
        //     8: ishl           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------
        //  0      9       0     this  Lcom/sun/xml/xsom/impl/SchemaSetImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator iterateSimpleTypes() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ddiv           
        //     1: ldiv           
        //     2: laload         
        //     3: pop2           
        //     4: aastore        
        //     5: dstore_2       
        //     6: isub           
        //     7: lsub           
        //     8: fdiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------
        //  0      9       0     this  Lcom/sun/xml/xsom/impl/SchemaSetImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator iterateComplexTypes() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: astore_1       
        //     1: fmul           
        //     2: ladd           
        //     3: fneg           
        //     4: ladd           
        //     5: laload         
        //     6: lneg           
        //     7: ineg           
        //     8: lmul           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------
        //  0      9       0     this  Lcom/sun/xml/xsom/impl/SchemaSetImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator iterateNotations() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ladd           
        //     1: fdiv           
        //     2: dsub           
        //     3: laload         
        //     4: aastore        
        //     5: ineg           
        //     6: frem           
        //     7: lmul           
        //     8: fdiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------
        //  0      9       0     this  Lcom/sun/xml/xsom/impl/SchemaSetImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSContentType getEmpty() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ddiv           
        //     1: ldiv           
        //     2: laload         
        //     3: aastore        
        //     4: fstore_0        /* this */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/SchemaSetImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSSimpleType getAnySimpleType() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ddiv           
        //     1: fdiv           
        //     2: istore_0        /* this */
        //     3: aconst_null    
        //     4: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/SchemaSetImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSComplexType getAnyType() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: ineg           
        //     2: istore_0        /* this */
        //     3: dload_3        
        //     4: astore_1       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/SchemaSetImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    static {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: sipush          82
        //     5: nop            
        //     6: aastore        
        //     7: nop            
        //     8: nop            
        //     9: nop            
        //    10: iload_3        
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private class AnySimpleType extends DeclarationImpl implements XSRestrictionSimpleType, Ref.SimpleType
    {
        AnySimpleType() {
            super(null, null, null, null, "http://www.w3.org/2001/XMLSchema", "anySimpleType", false);
        }
        
        public SchemaImpl getOwnerSchema() {
            return SchemaSetImpl.this.createSchema("http://www.w3.org/2001/XMLSchema", null);
        }
        
        public XSSimpleType asSimpleType() {
            return this;
        }
        
        public XSComplexType asComplexType() {
            return null;
        }
        
        public boolean isDerivedFrom(final XSType t) {
            return t == this || t == SchemaSetImpl.this.anyType;
        }
        
        public boolean isSimpleType() {
            return true;
        }
        
        public boolean isComplexType() {
            return false;
        }
        
        public XSContentType asEmpty() {
            return null;
        }
        
        public XSParticle asParticle() {
            return null;
        }
        
        public XSType getBaseType() {
            return SchemaSetImpl.this.anyType;
        }
        
        public XSSimpleType getSimpleBaseType() {
            return null;
        }
        
        public int getDerivationMethod() {
            return 2;
        }
        
        public Iterator<XSFacet> iterateDeclaredFacets() {
            return Iterators.empty();
        }
        
        public Collection<? extends XSFacet> getDeclaredFacets() {
            return (Collection<? extends XSFacet>)Collections.EMPTY_LIST;
        }
        
        public void visit(final XSSimpleTypeVisitor visitor) {
            visitor.restrictionSimpleType(this);
        }
        
        public void visit(final XSContentTypeVisitor visitor) {
            visitor.simpleType(this);
        }
        
        public void visit(final XSVisitor visitor) {
            visitor.simpleType(this);
        }
        
        public <T> T apply(final XSSimpleTypeFunction<T> f) {
            return f.restrictionSimpleType(this);
        }
        
        public <T> T apply(final XSContentTypeFunction<T> f) {
            return f.simpleType(this);
        }
        
        public <T> T apply(final XSFunction<T> f) {
            return f.simpleType(this);
        }
        
        public XSVariety getVariety() {
            return XSVariety.ATOMIC;
        }
        
        public XSSimpleType getPrimitiveType() {
            return this;
        }
        
        public boolean isPrimitive() {
            return true;
        }
        
        public XSListSimpleType getBaseListType() {
            return null;
        }
        
        public XSUnionSimpleType getBaseUnionType() {
            return null;
        }
        
        public XSFacet getFacet(final String name) {
            return null;
        }
        
        public XSFacet getDeclaredFacet(final String name) {
            return null;
        }
        
        public List<XSFacet> getDeclaredFacets(final String name) {
            return (List<XSFacet>)Collections.EMPTY_LIST;
        }
        
        public boolean isRestriction() {
            return true;
        }
        
        public boolean isList() {
            return false;
        }
        
        public boolean isUnion() {
            return false;
        }
        
        public boolean isFinal(final XSVariety v) {
            return false;
        }
        
        public XSRestrictionSimpleType asRestriction() {
            return this;
        }
        
        public XSListSimpleType asList() {
            return null;
        }
        
        public XSUnionSimpleType asUnion() {
            return null;
        }
        
        public XSSimpleType getType() {
            return this;
        }
        
        public XSSimpleType getRedefinedBy() {
            return null;
        }
        
        public int getRedefinedCount() {
            return 0;
        }
        
        public XSType[] listSubstitutables() {
            return Util.listSubstitutables(this);
        }
    }
    
    private class AnyType extends DeclarationImpl implements XSComplexType, Ref.Type
    {
        private final WildcardImpl anyWildcard;
        private final XSContentType contentType;
        
        AnyType() {
            super(null, null, null, null, "http://www.w3.org/2001/XMLSchema", "anyType", false);
            this.anyWildcard = new WildcardImpl.Any(null, null, null, null, 3);
            this.contentType = new ParticleImpl(null, null, new ModelGroupImpl(null, null, null, null, XSModelGroup.SEQUENCE, new ParticleImpl[] { new ParticleImpl(null, null, this.anyWildcard, null, -1, 0) }), null, 1, 1);
        }
        
        public SchemaImpl getOwnerSchema() {
            return SchemaSetImpl.this.createSchema("http://www.w3.org/2001/XMLSchema", null);
        }
        
        public boolean isAbstract() {
            return false;
        }
        
        public XSWildcard getAttributeWildcard() {
            return this.anyWildcard;
        }
        
        public XSAttributeUse getAttributeUse(final String nsURI, final String localName) {
            return null;
        }
        
        public Iterator<XSAttributeUse> iterateAttributeUses() {
            return Iterators.empty();
        }
        
        public XSAttributeUse getDeclaredAttributeUse(final String nsURI, final String localName) {
            return null;
        }
        
        public Iterator<XSAttributeUse> iterateDeclaredAttributeUses() {
            return Iterators.empty();
        }
        
        public Iterator<XSAttGroupDecl> iterateAttGroups() {
            return Iterators.empty();
        }
        
        public Collection<XSAttributeUse> getAttributeUses() {
            return (Collection<XSAttributeUse>)Collections.EMPTY_LIST;
        }
        
        public Collection<? extends XSAttributeUse> getDeclaredAttributeUses() {
            return (Collection<? extends XSAttributeUse>)Collections.EMPTY_LIST;
        }
        
        public Collection<? extends XSAttGroupDecl> getAttGroups() {
            return (Collection<? extends XSAttGroupDecl>)Collections.EMPTY_LIST;
        }
        
        public boolean isFinal(final int i) {
            return false;
        }
        
        public boolean isSubstitutionProhibited(final int i) {
            return false;
        }
        
        public boolean isMixed() {
            return true;
        }
        
        public XSContentType getContentType() {
            return this.contentType;
        }
        
        public XSContentType getExplicitContent() {
            return null;
        }
        
        public XSType getBaseType() {
            return this;
        }
        
        public XSSimpleType asSimpleType() {
            return null;
        }
        
        public XSComplexType asComplexType() {
            return this;
        }
        
        public boolean isDerivedFrom(final XSType t) {
            return t == this;
        }
        
        public boolean isSimpleType() {
            return false;
        }
        
        public boolean isComplexType() {
            return true;
        }
        
        public XSContentType asEmpty() {
            return null;
        }
        
        public int getDerivationMethod() {
            return 2;
        }
        
        public XSElementDecl getScope() {
            return null;
        }
        
        public void visit(final XSVisitor visitor) {
            visitor.complexType(this);
        }
        
        public <T> T apply(final XSFunction<T> f) {
            return f.complexType(this);
        }
        
        public XSType getType() {
            return this;
        }
        
        public XSComplexType getRedefinedBy() {
            return null;
        }
        
        public int getRedefinedCount() {
            return 0;
        }
        
        public XSType[] listSubstitutables() {
            return Util.listSubstitutables(this);
        }
    }
}
