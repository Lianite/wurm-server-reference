// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

import java.util.Set;
import java.util.List;

public class JMethod implements JDeclaration
{
    private JMods mods;
    private JType type;
    private String name;
    private final List params;
    private final Set _throws;
    private JBlock body;
    private JDefinedClass outer;
    private JDocComment jdoc;
    
    private boolean isConstructor() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ldiv           
        //     1: ldiv           
        //     2: lsub           
        //     3: fdiv           
        //     4: ineg           
        //     5: fconst_1       
        //     6: nop            
        //     7: lneg           
        //     8: aconst_null    
        //     9: istore_0        /* this */
        //    10: iconst_4       
        //    11: aconst_null    
        //    12: istore_1       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------
        //  0      13      0     this  Lcom/sun/codemodel/JMethod;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    JMethod(final JDefinedClass outer, final int mods, final JType type, final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: ddiv           
        //     2: fconst_1       
        //     3: aconst_null    
        //     4: astore_0        /* this */
        //     5: aconst_null    
        //     6: astore_1        /* outer */
        //     7: fconst_1       
        //     8: aconst_null    
        //     9: astore_2        /* mods */
        //    10: nop            
        //    11: ifeq            267
        //    14: iconst_1       
        //    15: iaload         
        //    16: iaload         
        //    17: iaload         
        //    18: lload_2         /* mods */
        //    19: fconst_1       
        //    20: nop            
        //    21: ret             outer
        //    23: dstore_1        /* outer */
        //    24: fconst_1       
        //    25: aconst_null    
        //    26: astore_3        /* type */
        //    27: aconst_null    
        //    28: fstore_3        /* type */
        //    29: fconst_1       
        //    30: aconst_null    
        //    31: iastore        
        //    32: nop            
        //    33: ddiv           
        //    34: aconst_null    
        //    35: nop            
        //    36: iconst_3       
        //    37: ineg           
        //    38: imul           
        //    39: frem           
        //    40: ddiv           
        //    41: dneg           
        //    42: drem           
        //    43: fconst_1       
        //    44: aconst_null    
        //    45: istore_2        /* mods */
        //    46: aconst_null    
        //    47: lastore        
        //    48: aconst_null    
        //    49: nop            
        //    50: iconst_5       
        //    51: isub           
        //    52: lsub           
        //    53: fsub           
        //    54: ladd           
        //    55: lneg           
        //    56: idiv           
        //    57: ineg           
        //    58: lload_2         /* mods */
        //    59: fconst_1       
        //    60: aconst_null    
        //    61: fastore        
        //    62: aconst_null    
        //    63: dastore        
        //    64: fconst_1       
        //    65: aconst_null    
        //    66: aastore        
        //    67: nop            
        //    68: ddiv           
        //    69: fconst_1       
        //    70: aconst_null    
        //    71: bastore        
        //    72: nop            
        //    73: ddiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name   Signature
        //  -----  ------  ----  -----  ---------------------------------
        //  0      74      0     this   Lcom/sun/codemodel/JMethod;
        //  0      74      1     outer  Lcom/sun/codemodel/JDefinedClass;
        //  0      74      2     mods   I
        //  0      74      3     type   Lcom/sun/codemodel/JType;
        //  0      74      4     name   Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException: -72
        //     at com.strobel.assembler.flowanalysis.ControlFlowGraphBuilder.calculateIncomingJumps(ControlFlowGraphBuilder.java:122)
        //     at com.strobel.assembler.flowanalysis.ControlFlowGraphBuilder.build(ControlFlowGraphBuilder.java:96)
        //     at com.strobel.assembler.flowanalysis.ControlFlowGraphBuilder.build(ControlFlowGraphBuilder.java:60)
        //     at com.strobel.decompiler.ast.AstBuilder$FinallyInlining.preProcess(AstBuilder.java:4670)
        //     at com.strobel.decompiler.ast.AstBuilder$FinallyInlining.<init>(AstBuilder.java:4160)
        //     at com.strobel.decompiler.ast.AstBuilder$FinallyInlining.run(AstBuilder.java:4275)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:100)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:210)
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
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    JMethod(final int mods, final JDefinedClass _class) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: lsub           
        //     2: aconst_null    
        //     3: nop            
        //     4: iconst_0       
        //     5: dload_2         /* _class */
        //     6: dload_3        
        //     7: sastore        
        //     8: aconst_null    
        //     9: nop            
        //    10: fload           99
        //    12: ddiv           
        //    13: ldiv           
        //    14: laload         
        //    15: drem           
        //    16: lneg           
        //    17: fdiv           
        //    18: laload         
        //    19: dadd           
        //    20: ddiv           
        //    21: isub           
        //    22: lsub           
        //    23: ldiv           
        //    24: ddiv           
        //    25: isub           
        //    26: lsub           
        //    27: idiv           
        //    28: laload         
        //    29: dstore_3       
        //    30: astore_2        /* _class */
        //    31: ddiv           
        //    32: isub           
        //    33: drem           
        //    34: aconst_null    
        //    35: nop            
        //    36: lconst_0       
        //    37: fsub           
        //    38: ddiv           
        //    39: frem           
        //    40: astore_2        /* _class */
        //    41: lsub           
        //    42: ineg           
        //    43: imul           
        //    44: ddiv           
        //    45: isub           
        //    46: aconst_null    
        //    47: nop            
        //    48: iload_2         /* _class */
        //    49: dload_2         /* _class */
        //    50: dstore_2        /* _class */
        //    51: dload_3        
        //    52: astore_1        /* mods */
        //    53: dadd           
        //    54: ddiv           
        //    55: ldiv           
        //    56: laload         
        //    57: drem           
        //    58: lneg           
        //    59: fdiv           
        //    60: laload         
        //    61: dadd           
        //    62: ddiv           
        //    63: isub           
        //    64: lsub           
        //    65: ldiv           
        //    66: ddiv           
        //    67: isub           
        //    68: lsub           
        //    69: idiv           
        //    70: laload         
        //    71: dstore_3       
        //    72: astore_2        /* _class */
        //    73: ddiv           
        //    74: isub           
        //    75: drem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  ---------------------------------
        //  0      76      0     this    Lcom/sun/codemodel/JMethod;
        //  0      76      1     mods    I
        //  0      76      2     _class  Lcom/sun/codemodel/JDefinedClass;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JMethod _throws(final JClass exception) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: aload           40
        //     3: astore_1        /* exception */
        //     4: fmul           
        //     5: ladd           
        //     6: fneg           
        //     7: ladd           
        //     8: laload         
        //     9: lneg           
        //    10: ineg           
        //    11: lmul           
        //    12: idiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name       Signature
        //  -----  ------  ----  ---------  ---------------------------
        //  0      13      0     this       Lcom/sun/codemodel/JMethod;
        //  0      13      1     exception  Lcom/sun/codemodel/JClass;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JMethod _throws(final Class exception) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: dadd           
        //     2: ddiv           
        //     3: isub           
        //     4: lsub           
        //     5: ldiv           
        //     6: ddiv           
        //     7: isub           
        //     8: lsub           
        //     9: idiv           
        //    10: laload         
        //    11: dstore_3       
        //    12: fstore_0        /* this */
        //    13: ddiv           
        //    14: isub           
        //    15: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name       Signature
        //  -----  ------  ----  ---------  ---------------------------
        //  0      16      0     this       Lcom/sun/codemodel/JMethod;
        //  0      16      1     exception  Ljava/lang/Class;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JVar param(final int mods, final JType type, final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ladd           
        //     1: frem           
        //     2: aconst_null    
        //     3: nop            
        //     4: fsub           
        //     5: dload_2         /* type */
        //     6: astore_1        /* mods */
        //     7: dadd           
        //     8: ddiv           
        //     9: ldiv           
        //    10: laload         
        //    11: drem           
        //    12: lneg           
        //    13: fdiv           
        //    14: laload         
        //    15: dadd           
        //    16: ddiv           
        //    17: isub           
        //    18: lsub           
        //    19: ldiv           
        //    20: ddiv           
        //    21: isub           
        //    22: lsub           
        //    23: idiv           
        //    24: laload         
        //    25: dstore_3        /* name */
        //    26: astore_2        /* type */
        //    27: ddiv           
        //    28: isub           
        //    29: drem           
        //    30: istore_0        /* this */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------
        //  0      31      0     this  Lcom/sun/codemodel/JMethod;
        //  0      31      1     mods  I
        //  0      31      2     type  Lcom/sun/codemodel/JType;
        //  0      31      3     name  Ljava/lang/String;
        //  16     15      4     v     Lcom/sun/codemodel/JVar;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JVar param(final JType type, final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: astore_1        /* type */
        //     1: fmul           
        //     2: ladd           
        //     3: fneg           
        //     4: ladd           
        //     5: laload         
        //     6: idiv           
        //     7: ladd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------
        //  0      8       0     this  Lcom/sun/codemodel/JMethod;
        //  0      8       1     type  Lcom/sun/codemodel/JType;
        //  0      8       2     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JVar param(final int mods, final Class type, final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: iload_3         /* name */
        //     1: dload_2         /* type */
        //     2: astore_1        /* mods */
        //     3: dadd           
        //     4: ddiv           
        //     5: ldiv           
        //     6: laload         
        //     7: drem           
        //     8: lneg           
        //     9: fdiv           
        //    10: laload         
        //    11: dadd           
        //    12: ddiv           
        //    13: isub           
        //    14: lsub           
        //    15: ldiv           
        //    16: ddiv           
        //    17: isub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------
        //  0      18      0     this  Lcom/sun/codemodel/JMethod;
        //  0      18      1     mods  I
        //  0      18      2     type  Ljava/lang/Class;
        //  0      18      3     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JVar param(final Class type, final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ddiv           
        //     1: ldiv           
        //     2: laload         
        //     3: drem           
        //     4: lneg           
        //     5: fdiv           
        //     6: laload         
        //     7: dadd           
        //     8: ddiv           
        //     9: isub           
        //    10: lsub           
        //    11: ldiv           
        //    12: ddiv           
        //    13: isub           
        //    14: lsub           
        //    15: idiv           
        //    16: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------
        //  0      17      0     this  Lcom/sun/codemodel/JMethod;
        //  0      17      1     type  Ljava/lang/Class;
        //  0      17      2     name  Ljava/lang/String;
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
        //     0: astore_1       
        //     1: fmul           
        //     2: ladd           
        //     3: fneg           
        //     4: ladd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------
        //  0      5       0     this  Lcom/sun/codemodel/JMethod;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JType type() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fdiv           
        //     1: dsub           
        //     2: laload         
        //     3: iastore        
        //     4: fadd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------
        //  0      5       0     this  Lcom/sun/codemodel/JMethod;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JType[] listParamTypes() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dstore_3       
        //     1: fstore_0        /* this */
        //     2: ddiv           
        //     3: isub           
        //     4: lsub           
        //     5: astore_2       
        //     6: ddiv           
        //     7: isub           
        //     8: lsub           
        //     9: idiv           
        //    10: istore_0        /* this */
        //    11: dload_3        
        //    12: sastore        
        //    13: aconst_null    
        //    14: nop            
        //    15: iload_2         /* i */
        //    16: dadd           
        //    17: ddiv           
        //    18: ldiv           
        //    19: laload         
        //    20: drem           
        //    21: lneg           
        //    22: fdiv           
        //    23: laload         
        //    24: dadd           
        //    25: ddiv           
        //    26: isub           
        //    27: lsub           
        //    28: ldiv           
        //    29: ddiv           
        //    30: isub           
        //    31: lsub           
        //    32: idiv           
        //    33: laload         
        //    34: dstore_3       
        //    35: fstore_3       
        //    36: ddiv           
        //    37: frem           
        //    38: ldiv           
        //    39: ladd           
        //    40: ineg           
        //    41: ineg           
        //    42: lsub           
        //    43: frem           
        //    44: aconst_null    
        //    45: nop            
        //    46: aconst_null    
        //    47: dsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------
        //  15     31      2     i     I
        //  0      48      0     this  Lcom/sun/codemodel/JMethod;
        //  13     35      1     r     [Lcom/sun/codemodel/JType;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JVar[] listParams() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dstore_2       
        //     1: ineg           
        //     2: lsub           
        //     3: frem           
        //     4: ladd           
        //     5: ineg           
        //     6: ddiv           
        //     7: frem           
        //     8: istore_0        /* this */
        //     9: aconst_null    
        //    10: nop            
        //    11: ldc             "i"
        //    13: ladd           
        //    14: fneg           
        //    15: ladd           
        //    16: laload         
        //    17: lneg           
        //    18: ineg           
        //    19: lmul           
        //    20: idiv           
        //    21: laload         
        //    22: dstore_2       
        //    23: ineg           
        //    24: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------
        //  0      25      0     this  Lcom/sun/codemodel/JMethod;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean hasSignature(final JType[] argTypes) {
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
        //     8: dadd           
        //     9: ddiv           
        //    10: isub           
        //    11: lsub           
        //    12: ldiv           
        //    13: ddiv           
        //    14: isub           
        //    15: lsub           
        //    16: idiv           
        //    17: laload         
        //    18: dstore_3        /* i */
        //    19: fstore_3        /* i */
        //    20: ddiv           
        //    21: frem           
        //    22: ldiv           
        //    23: ladd           
        //    24: ineg           
        //    25: ineg           
        //    26: lsub           
        //    27: frem           
        //    28: istore_0        /* this */
        //    29: aconst_null    
        //    30: nop            
        //    31: iconst_m1      
        //    32: lmul           
        //    33: isub           
        //    34: aconst_null    
        //    35: nop            
        //    36: aaload         
        //    37: dload_2         /* p */
        //    38: astore_1        /* argTypes */
        //    39: fmul           
        //    40: ladd           
        //    41: fneg           
        //    42: ladd           
        //    43: laload         
        //    44: idiv           
        //    45: ladd           
        //    46: fdiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ---------------------------
        //  16     29      3     i         I
        //  0      47      0     this      Lcom/sun/codemodel/JMethod;
        //  0      47      1     argTypes  [Lcom/sun/codemodel/JType;
        //  5      42      2     p         [Lcom/sun/codemodel/JVar;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JBlock body() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: idiv           
        //     2: laload         
        //     3: dstore_3       
        //     4: sastore        
        //     5: ladd           
        //     6: frem           
        //     7: istore_0        /* this */
        //     8: dload_3        
        //     9: astore_1       
        //    10: dadd           
        //    11: ddiv           
        //    12: ldiv           
        //    13: laload         
        //    14: drem           
        //    15: lneg           
        //    16: fdiv           
        //    17: laload         
        //    18: dadd           
        //    19: ddiv           
        //    20: isub           
        //    21: lsub           
        //    22: ldiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------
        //  0      23      0     this  Lcom/sun/codemodel/JMethod;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JDocComment javadoc() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dadd           
        //     1: ineg           
        //     2: lmul           
        //     3: ddiv           
        //     4: fdiv           
        //     5: istore_0        /* this */
        //     6: dload_3        
        //     7: astore_1       
        //     8: dadd           
        //     9: ddiv           
        //    10: ldiv           
        //    11: laload         
        //    12: drem           
        //    13: lneg           
        //    14: fdiv           
        //    15: laload         
        //    16: dadd           
        //    17: ddiv           
        //    18: isub           
        //    19: lsub           
        //    20: ldiv           
        //    21: ddiv           
        //    22: isub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------
        //  0      23      0     this  Lcom/sun/codemodel/JMethod;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void declare(final JFormatter f) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JMethod.declare:(Lcom/sun/codemodel/JFormatter;)V'.
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
        // Caused by: java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JMods getMods() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: nop            
        //     2: nop            
        //     3: nop            
        //     4: astore_0        /* this */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------
        //  0      5       0     this  Lcom/sun/codemodel/JMethod;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2162)
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
}
