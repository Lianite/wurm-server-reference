// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

import java.util.List;

public class JForLoop implements JStatement
{
    private List inits;
    private JExpression test;
    private List updates;
    private JBlock body;
    
    public JForLoop() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ladd           
        //     1: ineg           
        //     2: ineg           
        //     3: lsub           
        //     4: frem           
        //     5: istore_0        /* this */
        //     6: aconst_null    
        //     7: nop            
        //     8: istore          40
        //    10: astore_1       
        //    11: fmul           
        //    12: ladd           
        //    13: fneg           
        //    14: ladd           
        //    15: laload         
        //    16: lneg           
        //    17: ineg           
        //    18: lmul           
        //    19: idiv           
        //    20: laload         
        //    21: fstore_0        /* this */
        //    22: ddiv           
        //    23: idiv           
        //    24: idiv           
        //    25: lsub           
        //    26: dadd           
        //    27: ineg           
        //    28: lmul           
        //    29: ddiv           
        //    30: fdiv           
        //    31: istore_0        /* this */
        //    32: dload_3        
        //    33: astore_1       
        //    34: dadd           
        //    35: ddiv           
        //    36: ldiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------
        //  0      37      0     this  Lcom/sun/codemodel/JForLoop;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JVar init(final int mods, final JType type, final String var, final JExpression e) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: aconst_null    
        //     2: nop            
        //     3: iload_3         /* var */
        //     4: nop            
        //     5: iconst_1       
        //     6: nop            
        //     7: iconst_m1      
        //     8: nop            
        //     9: lload_0         /* this */
        //    10: nop            
        //    11: lload_1         /* mods */
        //    12: nop            
        //    13: aconst_null    
        //    14: nop            
        //    15: lload_2         /* type */
        //    16: nop            
        //    17: nop            
        //    18: nop            
        //    19: iconst_m1      
        //    20: nop            
        //    21: lload_3         /* var */
        //    22: nop            
        //    23: iconst_m1      
        //    24: nop            
        //    25: fload_0         /* this */
        //    26: nop            
        //    27: fload_1         /* mods */
        //    28: nop            
        //    29: nop            
        //    30: nop            
        //    31: iconst_m1      
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------
        //  0      32      0     this  Lcom/sun/codemodel/JForLoop;
        //  0      32      1     mods  I
        //  0      32      2     type  Lcom/sun/codemodel/JType;
        //  0      32      3     var   Ljava/lang/String;
        //  0      32      4     e     Lcom/sun/codemodel/JExpression;
        //  17     15      5     v     Lcom/sun/codemodel/JVar;
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
    
    public JVar init(final JType type, final String var, final JExpression e) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fload_2         /* var */
        //     1: nop            
        //     2: aload_2         /* var */
        //     3: nop            
        //     4: nop            
        //     5: nop            
        //     6: fconst_1       
        //     7: nop            
        //     8: aconst_null    
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------
        //  0      9       0     this  Lcom/sun/codemodel/JForLoop;
        //  0      9       1     type  Lcom/sun/codemodel/JType;
        //  0      9       2     var   Ljava/lang/String;
        //  0      9       3     e     Lcom/sun/codemodel/JExpression;
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
    
    public void init(final JVar v, final JExpression e) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: dload_3        
        //     2: nop            
        //     3: aload_2         /* e */
        //     4: nop            
        //     5: nop            
        //     6: nop            
        //     7: istore_3       
        //     8: nop            
        //     9: iconst_3       
        //    10: nop            
        //    11: nop            
        //    12: nop            
        //    13: lload_2         /* e */
        //    14: nop            
        //    15: aload_3        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------
        //  0      16      0     this  Lcom/sun/codemodel/JForLoop;
        //  0      16      1     v     Lcom/sun/codemodel/JVar;
        //  0      16      2     e     Lcom/sun/codemodel/JExpression;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void test(final JExpression e) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aload_3        
        //     1: invokevirtual   com/sun/codemodel/JForLoop.init:(ILcom/sun/codemodel/JType;Ljava/lang/String;Lcom/sun/codemodel/JExpression;)Lcom/sun/codemodel/JVar;
        //     4: areturn        
        //     5: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------
        //  0      6       0     this  Lcom/sun/codemodel/JForLoop;
        //  0      6       1     e     Lcom/sun/codemodel/JExpression;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void update(final JExpression e) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: nop            
        //     2: aload_0         /* this */
        //     3: nop            
        //     4: nop            
        //     5: nop            
        //     6: dastore        
        //     7: nop            
        //     8: iconst_0       
        //     9: nop            
        //    10: iconst_0       
        //    11: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------
        //  0      12      0     this  Lcom/sun/codemodel/JForLoop;
        //  0      12      1     e     Lcom/sun/codemodel/JExpression;
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
        //     0: bipush          0
        //     2: lstore          this
        //     4: fload_1        
        //     5: nop            
        //     6: iconst_m1      
        //     7: nop            
        //     8: aconst_null    
        //     9: nop            
        //    10: fload_0         /* this */
        //    11: nop            
        //    12: istore_1       
        //    13: nop            
        //    14: aconst_null    
        //    15: nop            
        //    16: aload_0         /* this */
        //    17: nop            
        //    18: nop            
        //    19: nop            
        //    20: istore_3       
        //    21: nop            
        //    22: iconst_m1      
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------
        //  0      23      0     this  Lcom/sun/codemodel/JForLoop;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void state(final JFormatter f) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JForLoop.state:(Lcom/sun/codemodel/JFormatter;)V'.
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
}
