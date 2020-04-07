// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.util;

import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.visitor.XSFunction;

public class ComponentNameFunction implements XSFunction
{
    private NameGetter nameGetter;
    
    public ComponentNameFunction() {
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
        //     9: daload         
        //    10: dload_2        
        //    11: astore_1       
        //    12: dadd           
        //    13: ddiv           
        //    14: ldiv           
        //    15: laload         
        //    16: drem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------
        //  0      17      0     this  Lcom/sun/xml/xsom/util/ComponentNameFunction;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object annotation(final XSAnnotation ann) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: drem           
        //     1: ddiv           
        //     2: ldiv           
        //     3: laload         
        //     4: pop2           
        //     5: aastore        
        //     6: aastore        
        //     7: lmul           
        //     8: ldiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------
        //  0      9       0     this  Lcom/sun/xml/xsom/util/ComponentNameFunction;
        //  0      9       1     ann   Lcom/sun/xml/xsom/XSAnnotation;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object attGroupDecl(final XSAttGroupDecl decl) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ladd           
        //     1: fneg           
        //     2: ladd           
        //     3: laload         
        //     4: idiv           
        //     5: ladd           
        //     6: fdiv           
        //     7: dsub           
        //     8: laload         
        //     9: iastore        
        //    10: fadd           
        //    11: fmul           
        //    12: lsub           
        //    13: dadd           
        //    14: ineg           
        //    15: istore_0        /* this */
        //    16: aconst_null    
        //    17: nop            
        //    18: baload         
        //    19: dload_2         /* name */
        //    20: astore_1        /* decl */
        //    21: dadd           
        //    22: ddiv           
        //    23: ldiv           
        //    24: laload         
        //    25: drem           
        //    26: lneg           
        //    27: fdiv           
        //    28: laload         
        //    29: ishl           
        //    30: ldiv           
        //    31: idiv           
        //    32: laload         
        //    33: ishl           
        //    34: drem           
        //    35: ddiv           
        //    36: ldiv           
        //    37: laload         
        //    38: pop2           
        //    39: aastore        
        //    40: astore_2        /* name */
        //    41: ddiv           
        //    42: isub           
        //    43: lsub           
        //    44: idiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------
        //  0      45      0     this  Lcom/sun/xml/xsom/util/ComponentNameFunction;
        //  0      45      1     decl  Lcom/sun/xml/xsom/XSAttGroupDecl;
        //  7      38      2     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object attributeDecl(final XSAttributeDecl decl) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: daload         
        //     2: dload_2        
        //     3: astore_1        /* decl */
        //     4: dadd           
        //     5: ddiv           
        //     6: ldiv           
        //     7: laload         
        //     8: drem           
        //     9: lneg           
        //    10: fdiv           
        //    11: laload         
        //    12: ishl           
        //    13: ldiv           
        //    14: idiv           
        //    15: laload         
        //    16: ishl           
        //    17: drem           
        //    18: ddiv           
        //    19: ldiv           
        //    20: laload         
        //    21: pop2           
        //    22: aastore        
        //    23: pop            
        //    24: lmul           
        //    25: idiv           
        //    26: isub           
        //    27: dadd           
        //    28: ladd           
        //    29: frem           
        //    30: isub           
        //    31: istore_0        /* this */
        //    32: dload_3        
        //    33: astore_1        /* decl */
        //    34: fmul           
        //    35: ladd           
        //    36: fneg           
        //    37: ladd           
        //    38: laload         
        //    39: idiv           
        //    40: ladd           
        //    41: fdiv           
        //    42: dsub           
        //    43: laload         
        //    44: iastore        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------
        //  0      45      0     this  Lcom/sun/xml/xsom/util/ComponentNameFunction;
        //  0      45      1     decl  Lcom/sun/xml/xsom/XSAttributeDecl;
        //  7      38      2     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object attributeUse(final XSAttributeUse use) {
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
        //  -----  ------  ----  ----  ---------------------------------------------
        //  0      9       0     this  Lcom/sun/xml/xsom/util/ComponentNameFunction;
        //  0      9       1     use   Lcom/sun/xml/xsom/XSAttributeUse;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object complexType(final XSComplexType type) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/util/ComponentNameFunction.complexType:(Lcom/sun/xml/xsom/XSComplexType;)Ljava/lang/Object;'.
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
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:170)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object schema(final XSSchema schema) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/util/ComponentNameFunction.schema:(Lcom/sun/xml/xsom/XSSchema;)Ljava/lang/Object;'.
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
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:170)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object facet(final XSFacet facet) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ldiv           
        //     1: laload         
        //     2: lneg           
        //     3: ineg           
        //     4: lmul           
        //     5: idiv           
        //     6: laload         
        //     7: fstore_0        /* this */
        //     8: ddiv           
        //     9: ldiv           
        //    10: irem           
        //    11: ddiv           
        //    12: fdiv           
        //    13: lsub           
        //    14: fdiv           
        //    15: ineg           
        //    16: astore_3       
        //    17: ladd           
        //    18: ldiv           
        //    19: lsub           
        //    20: fstore_3       
        //    21: lneg           
        //    22: fdiv           
        //    23: dadd           
        //    24: ineg           
        //    25: lmul           
        //    26: ddiv           
        //    27: fdiv           
        //    28: aconst_null    
        //    29: nop            
        //    30: bipush          106
        //    32: ladd           
        //    33: fneg           
        //    34: ladd           
        //    35: laload         
        //    36: idiv           
        //    37: ladd           
        //    38: fdiv           
        //    39: dsub           
        //    40: laload         
        //    41: iastore        
        //    42: fadd           
        //    43: fmul           
        //    44: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name   Signature
        //  -----  ------  ----  -----  ---------------------------------------------
        //  0      45      0     this   Lcom/sun/xml/xsom/util/ComponentNameFunction;
        //  0      45      1     facet  Lcom/sun/xml/xsom/XSFacet;
        //  7      38      2     name   Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object notation(final XSNotation notation) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/util/ComponentNameFunction.notation:(Lcom/sun/xml/xsom/XSNotation;)Ljava/lang/Object;'.
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
        // Caused by: java.lang.IllegalArgumentException: Argument 'index' must be in the range [0, 165], but value was: 10281.
        //     at com.strobel.core.VerifyArgument.inRange(VerifyArgument.java:346)
        //     at com.strobel.assembler.ir.ConstantPool.get(ConstantPool.java:78)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1313)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:293)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object simpleType(final XSSimpleType simpleType) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: ishl           
        //     2: drem           
        //     3: ddiv           
        //     4: ldiv           
        //     5: laload         
        //     6: pop2           
        //     7: aastore        
        //     8: lstore_2        /* name */
        //     9: ineg           
        //    10: ineg           
        //    11: frem           
        //    12: lmul           
        //    13: fadd           
        //    14: lneg           
        //    15: ineg           
        //    16: lsub           
        //    17: fstore_1        /* simpleType */
        //    18: lsub           
        //    19: dadd           
        //    20: idiv           
        //    21: aconst_null    
        //    22: nop            
        //    23: lload_0         /* this */
        //    24: dadd           
        //    25: ddiv           
        //    26: ldiv           
        //    27: laload         
        //    28: drem           
        //    29: lneg           
        //    30: fdiv           
        //    31: laload         
        //    32: ishl           
        //    33: ldiv           
        //    34: idiv           
        //    35: laload         
        //    36: ishl           
        //    37: drem           
        //    38: ddiv           
        //    39: ldiv           
        //    40: laload         
        //    41: pop2           
        //    42: aastore        
        //    43: fstore_0        /* this */
        //    44: ddiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name        Signature
        //  -----  ------  ----  ----------  ---------------------------------------------
        //  0      45      0     this        Lcom/sun/xml/xsom/util/ComponentNameFunction;
        //  0      45      1     simpleType  Lcom/sun/xml/xsom/XSSimpleType;
        //  7      38      2     name        Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object particle(final XSParticle particle) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ineg           
        //     1: aconst_null    
        //     2: nop            
        //     3: iload_1         /* particle */
        //     4: dadd           
        //     5: ddiv           
        //     6: ldiv           
        //     7: laload         
        //     8: drem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ---------------------------------------------
        //  0      9       0     this      Lcom/sun/xml/xsom/util/ComponentNameFunction;
        //  0      9       1     particle  Lcom/sun/xml/xsom/XSParticle;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object empty(final XSContentType empty) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ldiv           
        //     1: idiv           
        //     2: laload         
        //     3: ishl           
        //     4: drem           
        //     5: ddiv           
        //     6: ldiv           
        //     7: laload         
        //     8: pop2           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name   Signature
        //  -----  ------  ----  -----  ---------------------------------------------
        //  0      9       0     this   Lcom/sun/xml/xsom/util/ComponentNameFunction;
        //  0      9       1     empty  Lcom/sun/xml/xsom/XSContentType;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object wildcard(final XSWildcard wc) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ldiv           
        //     1: laload         
        //     2: pop2           
        //     3: aastore        
        //     4: dstore_2       
        //     5: isub           
        //     6: lsub           
        //     7: fdiv           
        //     8: ineg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------
        //  0      9       0     this  Lcom/sun/xml/xsom/util/ComponentNameFunction;
        //  0      9       1     wc    Lcom/sun/xml/xsom/XSWildcard;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object modelGroupDecl(final XSModelGroupDecl decl) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: invokespecial   com/sun/xml/xsom/util/NameGetter.<init>:(Ljava/util/Locale;)V
        //     4: putfield        com/sun/xml/xsom/util/ComponentNameFunction.nameGetter:Lcom/sun/xml/xsom/util/NameGetter;
        //     7: return         
        //     8: nop            
        //     9: nop            
        //    10: nop            
        //    11: iconst_m1      
        //    12: nop            
        //    13: lstore_3       
        //    14: nop            
        //    15: nop            
        //    16: nop            
        //    17: lconst_1       
        //    18: nop            
        //    19: iconst_m1      
        //    20: nop            
        //    21: nop            
        //    22: nop            
        //    23: iload_2         /* name */
        //    24: nop            
        //    25: iconst_1       
        //    26: nop            
        //    27: lload_1         /* decl */
        //    28: nop            
        //    29: fstore_0        /* this */
        //    30: nop            
        //    31: nop            
        //    32: nop            
        //    33: fconst_1       
        //    34: nop            
        //    35: aconst_null    
        //    36: nop            
        //    37: nop            
        //    38: nop            
        //    39: sipush          68
        //    42: nop            
        //    43: fstore_2        /* name */
        //    44: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------
        //  0      45      0     this  Lcom/sun/xml/xsom/util/ComponentNameFunction;
        //  0      45      1     decl  Lcom/sun/xml/xsom/XSModelGroupDecl;
        //  7      38      2     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object modelGroup(final XSModelGroup group) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: lstore_2       
        //     2: nop            
        //     3: nop            
        //     4: nop            
        //     5: drem           
        //     6: nop            
        //     7: iconst_0       
        //     8: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name   Signature
        //  -----  ------  ----  -----  ---------------------------------------------
        //  0      9       0     this   Lcom/sun/xml/xsom/util/ComponentNameFunction;
        //  0      9       1     group  Lcom/sun/xml/xsom/XSModelGroup;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object elementDecl(final XSElementDecl decl) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: iconst_4       
        //     2: nop            
        //     3: iaload         
        //     4: nop            
        //     5: dconst_0       
        //     6: nop            
        //     7: laload         
        //     8: nop            
        //     9: fstore_0        /* this */
        //    10: nop            
        //    11: nop            
        //    12: nop            
        //    13: lload_2         /* name */
        //    14: nop            
        //    15: iconst_0       
        //    16: nop            
        //    17: nop            
        //    18: nop            
        //    19: aload_3        
        //    20: nop            
        //    21: fstore_1        /* decl */
        //    22: nop            
        //    23: fstore_2        /* name */
        //    24: nop            
        //    25: nop            
        //    26: nop            
        //    27: nop            
        //    28: nop            
        //    29: aload_3        
        //    30: nop            
        //    31: astore_1        /* decl */
        //    32: nop            
        //    33: astore_2        /* name */
        //    34: nop            
        //    35: aconst_null    
        //    36: nop            
        //    37: iconst_4       
        //    38: nop            
        //    39: dload_0         /* this */
        //    40: nop            
        //    41: astore_3       
        //    42: nop            
        //    43: iastore        
        //    44: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------
        //  0      45      0     this  Lcom/sun/xml/xsom/util/ComponentNameFunction;
        //  0      45      1     decl  Lcom/sun/xml/xsom/XSElementDecl;
        //  7      38      2     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
