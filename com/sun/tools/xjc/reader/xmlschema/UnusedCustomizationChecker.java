// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.xml.xsom.XSUnionSimpleType;
import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.xml.xsom.XSListSimpleType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSAttContainer;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSComponent;
import java.util.Iterator;
import java.util.Set;
import com.sun.xml.xsom.visitor.XSSimpleTypeVisitor;
import com.sun.xml.xsom.visitor.XSVisitor;

class UnusedCustomizationChecker implements XSVisitor, XSSimpleTypeVisitor
{
    private final BGMBuilder builder;
    private Set visitedComponents;
    static final String ERR_UNACKNOWLEDGED_CUSTOMIZATION = "UnusedCustomizationChecker.UnacknolwedgedCustomization";
    
    UnusedCustomizationChecker(final BGMBuilder _builder) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: lsub           
        //     2: ladd           
        //     3: isub           
        //     4: lsub           
        //     5: frem           
        //     6: laload         
        //     7: ishl           
        //     8: ldiv           
        //     9: idiv           
        //    10: drem           
        //    11: dadd           
        //    12: imul           
        //    13: lsub           
        //    14: ldiv           
        //    15: ladd           
        //    16: laload         
        //    17: fstore_2       
        //    18: frem           
        //    19: frem           
        //    20: ddiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ---------------------------------------------------------------
        //  0      21      0     this      Lcom/sun/tools/xjc/reader/xmlschema/UnusedCustomizationChecker;
        //  0      21      1     _builder  Lcom/sun/tools/xjc/reader/xmlschema/BGMBuilder;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    void run() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: ineg           
        //     2: istore_0        /* this */
        //     3: dload_3        
        //     4: sastore        
        //     5: aconst_null    
        //     6: nop            
        //     7: dconst_0       
        //     8: lmul           
        //     9: drem           
        //    10: lstore_2       
        //    11: dadd           
        //    12: dmul           
        //    13: fdiv           
        //    14: ddiv           
        //    15: dneg           
        //    16: idiv           
        //    17: lsub           
        //    18: isub           
        //    19: dsub           
        //    20: lsub           
        //    21: isub           
        //    22: aconst_null    
        //    23: nop            
        //    24: fconst_0       
        //    25: dsub           
        //    26: lsub           
        //    27: ineg           
        //    28: astore_1        /* itr */
        //    29: ddiv           
        //    30: dadd           
        //    31: ladd           
        //    32: ineg           
        //    33: lmul           
        //    34: ddiv           
        //    35: fdiv           
        //    36: aconst_null    
        //    37: nop            
        //    38: iconst_4       
        //    39: dsub           
        //    40: lsub           
        //    41: ineg           
        //    42: astore_3       
        //    43: ladd           
        //    44: ldiv           
        //    45: lsub           
        //    46: aconst_null    
        //    47: nop            
        //    48: iload_3        
        //    49: dload_2         /* s */
        //    50: dload_3        
        //    51: astore_1        /* itr */
        //    52: fmul           
        //    53: ladd           
        //    54: fneg           
        //    55: ladd           
        //    56: ishl           
        //    57: laload         
        //    58: ishl           
        //    59: ldiv           
        //    60: idiv           
        //    61: laload         
        //    62: fdiv           
        //    63: ladd           
        //    64: ldiv           
        //    65: lsub           
        //    66: drem           
        //    67: irem           
        //    68: ladd           
        //    69: dadd           
        //    70: lsub           
        //    71: laload         
        //    72: fastore        
        //    73: astore_3       
        //    74: ladd           
        //    75: ldiv           
        //    76: lsub           
        //    77: istore_0        /* this */
        //    78: aconst_null    
        //    79: nop            
        //    80: aload           106
        //    82: ladd           
        //    83: fneg           
        //    84: ladd           
        //    85: ishl           
        //    86: laload         
        //    87: ishl           
        //    88: ldiv           
        //    89: idiv           
        //    90: laload         
        //    91: fdiv           
        //    92: ladd           
        //    93: ldiv           
        //    94: lsub           
        //    95: drem           
        //    96: irem           
        //    97: ladd           
        //    98: dadd           
        //    99: lsub           
        //   100: laload         
        //   101: fastore        
        //   102: astore_3       
        //   103: ladd           
        //   104: ldiv           
        //   105: lsub           
        //   106: aconst_null    
        //   107: nop            
        //   108: fconst_1       
        //   109: dsub           
        //   110: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------------------------
        //  32     75      2     s     Lcom/sun/xml/xsom/XSSchema;
        //  13     97      1     itr   Ljava/util/Iterator;
        //  0      111     0     this  Lcom/sun/tools/xjc/reader/xmlschema/UnusedCustomizationChecker;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void run(final Iterator itr) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ineg           
        //     1: bastore        
        //     2: lshl           
        //     3: irem           
        //     4: lsub           
        //     5: aconst_null    
        //     6: nop            
        //     7: lload_3        
        //     8: dload_2        
        //     9: dload_3        
        //    10: astore_1        /* itr */
        //    11: dadd           
        //    12: ddiv           
        //    13: ldiv           
        //    14: laload         
        //    15: drem           
        //    16: lneg           
        //    17: fdiv           
        //    18: laload         
        //    19: ishl           
        //    20: ldiv           
        //    21: idiv           
        //    22: laload         
        //    23: ishl           
        //    24: drem           
        //    25: ddiv           
        //    26: ldiv           
        //    27: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------------------------
        //  0      28      0     this  Lcom/sun/tools/xjc/reader/xmlschema/UnusedCustomizationChecker;
        //  0      28      1     itr   Ljava/util/Iterator;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private boolean check(final XSComponent c) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: pop2           
        //     2: aastore        
        //     3: aastore        
        //     4: lmul           
        //     5: ldiv           
        //     6: irem           
        //     7: idiv           
        //     8: lsub           
        //     9: bastore        
        //    10: lshl           
        //    11: irem           
        //    12: lsub           
        //    13: sastore        
        //    14: lmul           
        //    15: drem           
        //    16: lmul           
        //    17: ineg           
        //    18: ddiv           
        //    19: frem           
        //    20: istore_0        /* this */
        //    21: dload_3        
        //    22: sastore        
        //    23: aconst_null    
        //    24: nop            
        //    25: iconst_4       
        //    26: dsub           
        //    27: lsub           
        //    28: ineg           
        //    29: fstore_1        /* c */
        //    30: lsub           
        //    31: dadd           
        //    32: idiv           
        //    33: aconst_null    
        //    34: nop            
        //    35: fload_2         /* decls */
        //    36: dload_2         /* decls */
        //    37: dload_3         /* i */
        //    38: astore_1        /* c */
        //    39: dadd           
        //    40: ddiv           
        //    41: ldiv           
        //    42: laload         
        //    43: drem           
        //    44: lneg           
        //    45: fdiv           
        //    46: laload         
        //    47: ishl           
        //    48: ldiv           
        //    49: idiv           
        //    50: laload         
        //    51: ishl           
        //    52: drem           
        //    53: ddiv           
        //    54: ldiv           
        //    55: laload         
        //    56: pop2           
        //    57: aastore        
        //    58: lstore_2        /* decls */
        //    59: ineg           
        //    60: ineg           
        //    61: frem           
        //    62: lmul           
        //    63: fadd           
        //    64: lneg           
        //    65: ineg           
        //    66: lsub           
        //    67: fstore_1        /* c */
        //    68: lsub           
        //    69: dadd           
        //    70: idiv           
        //    71: istore_0        /* this */
        //    72: aconst_null    
        //    73: nop            
        //    74: lload_0         /* this */
        //    75: dadd           
        //    76: ddiv           
        //    77: ldiv           
        //    78: laload         
        //    79: drem           
        //    80: lneg           
        //    81: fdiv           
        //    82: laload         
        //    83: ishl           
        //    84: ldiv           
        //    85: idiv           
        //    86: laload         
        //    87: ishl           
        //    88: drem           
        //    89: ddiv           
        //    90: ldiv           
        //    91: laload         
        //    92: pop2           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name   Signature
        //  -----  ------  ----  -----  ---------------------------------------------------------------
        //  29     62      3     i      I
        //  0      93      0     this   Lcom/sun/tools/xjc/reader/xmlschema/UnusedCustomizationChecker;
        //  0      93      1     c      Lcom/sun/xml/xsom/XSComponent;
        //  27     66      2     decls  [Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIDeclaration;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void annotation(final XSAnnotation ann) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------------------------
        //  0      1       0     this  Lcom/sun/tools/xjc/reader/xmlschema/UnusedCustomizationChecker;
        //  0      1       1     ann   Lcom/sun/xml/xsom/XSAnnotation;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void attGroupDecl(final XSAttGroupDecl decl) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/xmlschema/UnusedCustomizationChecker.attGroupDecl:(Lcom/sun/xml/xsom/XSAttGroupDecl;)V'.
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
        // Caused by: java.lang.IllegalArgumentException: Argument 'index' must be in the range [0, 322], but value was: 26469.
        //     at com.strobel.core.VerifyArgument.inRange(VerifyArgument.java:346)
        //     at com.strobel.assembler.ir.ConstantPool.get(ConstantPool.java:78)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1313)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:293)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void attributeDecl(final XSAttributeDecl decl) {
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
        //     7: laload         
        //     8: ishl           
        //     9: ldiv           
        //    10: idiv           
        //    11: laload         
        //    12: ishl           
        //    13: drem           
        //    14: ddiv           
        //    15: ldiv           
        //    16: laload         
        //    17: pop2           
        //    18: aastore        
        //    19: astore_2       
        //    20: ddiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------------------------
        //  0      21      0     this  Lcom/sun/tools/xjc/reader/xmlschema/UnusedCustomizationChecker;
        //  0      21      1     decl  Lcom/sun/xml/xsom/XSAttributeDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void attributeUse(final XSAttributeUse use) {
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
        //    10: pop2           
        //    11: aastore        
        //    12: astore_2       
        //    13: ddiv           
        //    14: isub           
        //    15: lsub           
        //    16: idiv           
        //    17: dstore_0        /* this */
        //    18: frem           
        //    19: ddiv           
        //    20: lneg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------------------------
        //  0      21      0     this  Lcom/sun/tools/xjc/reader/xmlschema/UnusedCustomizationChecker;
        //  0      21      1     use   Lcom/sun/xml/xsom/XSAttributeUse;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void complexType(final XSComplexType type) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ishl           
        //     1: ldiv           
        //     2: idiv           
        //     3: laload         
        //     4: ishl           
        //     5: drem           
        //     6: ddiv           
        //     7: ldiv           
        //     8: laload         
        //     9: pop2           
        //    10: aastore        
        //    11: fstore_2       
        //    12: idiv           
        //    13: lsub           
        //    14: ldiv           
        //    15: lsub           
        //    16: fdiv           
        //    17: ineg           
        //    18: fstore_1        /* type */
        //    19: lsub           
        //    20: dadd           
        //    21: idiv           
        //    22: aconst_null    
        //    23: nop            
        //    24: iload_1         /* type */
        //    25: dload_2        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------------------------
        //  0      26      0     this  Lcom/sun/tools/xjc/reader/xmlschema/UnusedCustomizationChecker;
        //  0      26      1     type  Lcom/sun/xml/xsom/XSComplexType;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void attContainer(final XSAttContainer cont) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/xmlschema/UnusedCustomizationChecker.attContainer:(Lcom/sun/xml/xsom/XSAttContainer;)V'.
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
        // Caused by: java.lang.ClassCastException: com.strobel.assembler.ir.ConstantPool$InterfaceMethodReferenceEntry cannot be cast to com.strobel.assembler.ir.ConstantPool$ConstantEntry
        //     at com.strobel.assembler.ir.ConstantPool.lookupConstant(ConstantPool.java:120)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1319)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:286)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void schema(final XSSchema schema) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lshl           
        //     1: irem           
        //     2: lsub           
        //     3: aconst_null    
        //     4: nop            
        //     5: fload_0         /* this */
        //     6: dadd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  ---------------------------------------------------------------
        //  0      7       0     this    Lcom/sun/tools/xjc/reader/xmlschema/UnusedCustomizationChecker;
        //  0      7       1     schema  Lcom/sun/xml/xsom/XSSchema;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void facet(final XSFacet facet) {
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
        //     6: ishl           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name   Signature
        //  -----  ------  ----  -----  ---------------------------------------------------------------
        //  0      7       0     this   Lcom/sun/tools/xjc/reader/xmlschema/UnusedCustomizationChecker;
        //  0      7       1     facet  Lcom/sun/xml/xsom/XSFacet;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void notation(final XSNotation notation) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: ineg           
        //     2: fstore_3       
        //     3: lmul           
        //     4: lsub           
        //     5: idiv           
        //     6: isub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ---------------------------------------------------------------
        //  0      7       0     this      Lcom/sun/tools/xjc/reader/xmlschema/UnusedCustomizationChecker;
        //  0      7       1     notation  Lcom/sun/xml/xsom/XSNotation;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void wildcard(final XSWildcard wc) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/xmlschema/UnusedCustomizationChecker.wildcard:(Lcom/sun/xml/xsom/XSWildcard;)V'.
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
        //     at com.strobel.assembler.metadata.Buffer.readUnsignedShort(Buffer.java:225)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:291)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void modelGroupDecl(final XSModelGroupDecl decl) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: aconst_null    
        //     2: nop            
        //     3: nop            
        //     4: nop            
        //     5: aload_0         /* this */
        //     6: nop            
        //     7: dmul           
        //     8: nop            
        //     9: idiv           
        //    10: nop            
        //    11: nop            
        //    12: nop            
        //    13: nop            
        //    14: nop            
        //    15: ldiv           
        //    16: nop            
        //    17: dsub           
        //    18: nop            
        //    19: aconst_null    
        //    20: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------------------------
        //  0      21      0     this  Lcom/sun/tools/xjc/reader/xmlschema/UnusedCustomizationChecker;
        //  0      21      1     decl  Lcom/sun/xml/xsom/XSModelGroupDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void modelGroup(final XSModelGroup group) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/xmlschema/UnusedCustomizationChecker.modelGroup:(Lcom/sun/xml/xsom/XSModelGroup;)V'.
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
        // Caused by: java.lang.ClassCastException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void elementDecl(final XSElementDecl decl) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ldc2_w          "(Lcom/sun/xml/xsom/XSAnnotation;)V"
        //     3: nop            
        //     4: irem           
        //     5: nop            
        //     6: lrem           
        //     7: nop            
        //     8: aconst_null    
        //     9: nop            
        //    10: nop            
        //    11: nop            
        //    12: fneg           
        //    13: nop            
        //    14: dmul           
        //    15: nop            
        //    16: idiv           
        //    17: nop            
        //    18: nop            
        //    19: nop            
        //    20: iconst_m1      
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------------------------
        //  0      21      0     this  Lcom/sun/tools/xjc/reader/xmlschema/UnusedCustomizationChecker;
        //  0      21      1     decl  Lcom/sun/xml/xsom/XSElementDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ClassCastException: java.lang.String cannot be cast to java.lang.Number
        //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.execute(StackMappingVisitor.java:935)
        //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.visit(StackMappingVisitor.java:398)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2030)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:210)
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
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void simpleType(final XSSimpleType simpleType) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: nop            
        //     3: dmul           
        //     4: nop            
        //     5: lload_1         /* simpleType */
        //     6: nop            
        //     7: idiv           
        //     8: nop            
        //     9: dload_3        
        //    10: nop            
        //    11: ldiv           
        //    12: nop            
        //    13: fmul           
        //    14: nop            
        //    15: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name        Signature
        //  -----  ------  ----  ----------  ---------------------------------------------------------------
        //  0      16      0     this        Lcom/sun/tools/xjc/reader/xmlschema/UnusedCustomizationChecker;
        //  0      16      1     simpleType  Lcom/sun/xml/xsom/XSSimpleType;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void particle(final XSParticle particle) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/xmlschema/UnusedCustomizationChecker.particle:(Lcom/sun/xml/xsom/XSParticle;)V'.
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
        // Caused by: java.lang.ClassCastException: com.strobel.assembler.ir.ConstantPool$MethodReferenceEntry cannot be cast to com.strobel.assembler.ir.ConstantPool$FieldReferenceEntry
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupField(ClassFileReader.java:1233)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:214)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void empty(final XSContentType empty) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: nop            
        //     3: fneg           
        //     4: nop            
        //     5: fconst_2       
        //     6: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name   Signature
        //  -----  ------  ----  -----  ---------------------------------------------------------------
        //  0      7       0     this   Lcom/sun/tools/xjc/reader/xmlschema/UnusedCustomizationChecker;
        //  0      7       1     empty  Lcom/sun/xml/xsom/XSContentType;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void listSimpleType(final XSListSimpleType type) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: lstore_2       
        //     3: nop            
        //     4: dmul           
        //     5: nop            
        //     6: idiv           
        //     7: nop            
        //     8: nop            
        //     9: nop            
        //    10: nop            
        //    11: nop            
        //    12: lstore_2       
        //    13: nop            
        //    14: drem           
        //    15: nop            
        //    16: ineg           
        //    17: nop            
        //    18: aconst_null    
        //    19: nop            
        //    20: iconst_m1      
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------------------------
        //  0      21      0     this  Lcom/sun/tools/xjc/reader/xmlschema/UnusedCustomizationChecker;
        //  0      21      1     type  Lcom/sun/xml/xsom/XSListSimpleType;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void restrictionSimpleType(final XSRestrictionSimpleType type) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: iconst_m1      
        //     3: nop            
        //     4: lmul           
        //     5: nop            
        //     6: nop            
        //     7: nop            
        //     8: lload_0         /* this */
        //     9: nop            
        //    10: iconst_4       
        //    11: nop            
        //    12: nop            
        //    13: nop            
        //    14: ixor           
        //    15: nop            
        //    16: fconst_2       
        //    17: nop            
        //    18: lxor           
        //    19: nop            
        //    20: dconst_0       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------------------------
        //  0      21      0     this  Lcom/sun/tools/xjc/reader/xmlschema/UnusedCustomizationChecker;
        //  0      21      1     type  Lcom/sun/xml/xsom/XSRestrictionSimpleType;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void unionSimpleType(final XSUnionSimpleType type) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/xmlschema/UnusedCustomizationChecker.unionSimpleType:(Lcom/sun/xml/xsom/XSUnionSimpleType;)V'.
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
        // Caused by: java.lang.ClassCastException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
