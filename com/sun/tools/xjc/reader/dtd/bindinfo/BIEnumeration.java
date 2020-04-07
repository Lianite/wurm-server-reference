// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.dtd.bindinfo;

import com.sun.msv.grammar.Expression;
import java.util.HashMap;
import com.sun.tools.xjc.grammar.xducer.Transducer;
import org.dom4j.Element;

public final class BIEnumeration implements BIConversion
{
    private final Element e;
    private final Transducer xducer;
    private static final HashMap emptyHashMap;
    
    private BIEnumeration(final Element _e, final Transducer _xducer) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: aload_1         /* _e */
        //     2: dload_2         /* _xducer */
        //     3: dload_3        
        //     4: astore_1        /* _e */
        //     5: dadd           
        //     6: ddiv           
        //     7: ldiv           
        //     8: laload         
        //     9: drem           
        //    10: lneg           
        //    11: fdiv           
        //    12: laload         
        //    13: ishl           
        //    14: ldiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  -----------------------------------------------------
        //  0      15      0     this     Lcom/sun/tools/xjc/reader/dtd/bindinfo/BIEnumeration;
        //  0      15      1     _e       Lorg/dom4j/Element;
        //  0      15      2     _xducer  Lcom/sun/tools/xjc/grammar/xducer/Transducer;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String name() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: aconst_null    
        //     2: nop            
        //     3: dload_0         /* this */
        //     4: dload_2        
        //     5: astore_1       
        //     6: fmul           
        //     7: ladd           
        //     8: fneg           
        //     9: ladd           
        //    10: laload         
        //    11: idiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------------------------
        //  0      12      0     this  Lcom/sun/tools/xjc/reader/dtd/bindinfo/BIEnumeration;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Transducer getTransducer() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ladd           
        //     1: laload         
        //     2: idiv           
        //     3: ladd           
        //     4: fdiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/dtd/bindinfo/BIEnumeration;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    static BIEnumeration create(final Element dom, final BindInfo parent) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ladd           
        //     1: laload         
        //     2: lneg           
        //     3: ineg           
        //     4: lmul           
        //     5: idiv           
        //     6: laload         
        //     7: astore_1        /* parent */
        //     8: lmul           
        //     9: drem           
        //    10: ineg           
        //    11: aconst_null    
        //    12: nop            
        //    13: iconst_0       
        //    14: ladd           
        //    15: isub           
        //    16: isub           
        //    17: aconst_null    
        //    18: nop            
        //    19: iload           40
        //    21: astore_1        /* parent */
        //    22: fmul           
        //    23: ladd           
        //    24: fneg           
        //    25: ladd           
        //    26: laload         
        //    27: idiv           
        //    28: ladd           
        //    29: fdiv           
        //    30: dsub           
        //    31: laload         
        //    32: iastore        
        //    33: fadd           
        //    34: fmul           
        //    35: lsub           
        //    36: dadd           
        //    37: ineg           
        //    38: istore_0        /* dom */
        //    39: dload_3        
        //    40: dup_x1         
        //    41: nop            
        //    42: daload         
        //    43: nop            
        //    44: iconst_3       
        //    45: nop            
        //    46: lload_2        
        //    47: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  ------------------------------------------------
        //  0      48      0     dom     Lorg/dom4j/Element;
        //  0      48      1     parent  Lcom/sun/tools/xjc/reader/dtd/bindinfo/BindInfo;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    static BIEnumeration create(final Element dom, final BIElement parent) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/dtd/bindinfo/BIEnumeration.create:(Lorg/dom4j/Element;Lcom/sun/tools/xjc/reader/dtd/bindinfo/BIElement;)Lcom/sun/tools/xjc/reader/dtd/bindinfo/BIEnumeration;'.
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
        // Caused by: java.lang.ArrayIndexOutOfBoundsException: -1
        //     at java.util.ArrayList.elementData(Unknown Source)
        //     at java.util.ArrayList.get(Unknown Source)
        //     at com.strobel.assembler.ir.ConstantPool.get(ConstantPool.java:80)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1313)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:286)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private static Expression buildMemberExp(final Element dom) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: aload_1        
        //     2: nop            
        //     3: aload_2        
        //     4: nop            
        //     5: nop            
        //     6: nop            
        //     7: aconst_null    
        //     8: nop            
        //     9: daload         
        //    10: nop            
        //    11: aaload         
        //    12: nop            
        //    13: aconst_null    
        //    14: nop            
        //    15: dload_2        
        //    16: nop            
        //    17: nop            
        //    18: nop            
        //    19: laload         
        //    20: nop            
        //    21: aconst_null    
        //    22: nop            
        //    23: aconst_null    
        //    24: nop            
        //    25: nop            
        //    26: nop            
        //    27: iconst_2       
        //    28: aload_0         /* dom */
        //    29: getfield        com/sun/tools/xjc/reader/dtd/bindinfo/BIEnumeration.xducer:Lcom/sun/tools/xjc/grammar/xducer/Transducer;
        //    32: areturn        
        //    33: nop            
        //    34: nop            
        //    35: nop            
        //    36: iconst_m1      
        //    37: nop            
        //    38: dload_3         /* exp */
        //    39: nop            
        //    40: nop            
        //    41: nop            
        //    42: iconst_3       
        //    43: nop            
        //    44: aconst_null    
        //    45: nop            
        //    46: nop            
        //    47: nop            
        //    48: fstore_3        /* exp */
        //    49: nop            
        //    50: aload_0         /* dom */
        //    51: nop            
        //    52: nop            
        //    53: nop            
        //    54: fconst_1       
        //    55: nop            
        //    56: aconst_null    
        //    57: nop            
        //    58: nop            
        //    59: nop            
        //    60: iconst_2       
        //    61: nop            
        //    62: aload_1         /* members */
        //    63: nop            
        //    64: aload_2         /* pool */
        //    65: nop            
        //    66: nop            
        //    67: nop            
        //    68: iconst_5       
        //    69: nop            
        //    70: baload         
        //    71: nop            
        //    72: caload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ------------------------------------
        //  53     15      5     token    Ljava/lang/String;
        //  0      73      0     dom      Lorg/dom4j/Element;
        //  9      64      1     members  Ljava/lang/String;
        //  24     49      2     pool     Lcom/sun/msv/grammar/ExpressionPool;
        //  28     45      3     exp      Lcom/sun/msv/grammar/Expression;
        //  38     35      4     tokens   Ljava/util/StringTokenizer;
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        //     at java.util.ArrayList.rangeCheck(Unknown Source)
        //     at java.util.ArrayList.get(Unknown Source)
        //     at com.strobel.decompiler.ast.AstBuilder.convertLocalVariables(AstBuilder.java:3035)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2445)
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
    
    static {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/dtd/bindinfo/BIEnumeration.<clinit>:()V'.
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
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:214)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
