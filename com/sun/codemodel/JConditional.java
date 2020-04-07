// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

public class JConditional implements JStatement
{
    private JExpression test;
    private JBlock _then;
    private JBlock _else;
    
    JConditional(final JExpression test) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: dsub           
        //     2: aconst_null    
        //     3: nop            
        //     4: istore_3       
        //     5: dload_2        
        //     6: astore_1        /* test */
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
        //    25: dstore_3       
        //    26: dstore_0        /* this */
        //    27: lsub           
        //    28: fdiv           
        //    29: lsub           
        //    30: frem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------
        //  0      31      0     this  Lcom/sun/codemodel/JConditional;
        //  0      31      1     test  Lcom/sun/codemodel/JExpression;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JBlock _then() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: drem           
        //     2: lneg           
        //     3: fdiv           
        //     4: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------
        //  0      5       0     this  Lcom/sun/codemodel/JConditional;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JBlock _else() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: iconst_2       
        //     3: nop            
        //     4: nop            
        //     5: nop            
        //     6: iload_2        
        //     7: nop            
        //     8: iload_3        
        //     9: nop            
        //    10: aconst_null    
        //    11: nop            
        //    12: lload_0         /* this */
        //    13: nop            
        //    14: nop            
        //    15: nop            
        //    16: dsub           
        //    17: nop            
        //    18: iconst_0       
        //    19: nop            
        //    20: iconst_m1      
        //    21: nop            
        //    22: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------
        //  0      23      0     this  Lcom/sun/codemodel/JConditional;
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
    
    public void state(final JFormatter f) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aload_0         /* this */
        //     1: nop            
        //     2: aload           this
        //     4: baload         
        //     5: nop            
        //     6: lload_0         /* this */
        //     7: nop            
        //     8: caload         
        //     9: nop            
        //    10: lload_2        
        //    11: nop            
        //    12: nop            
        //    13: nop            
        //    14: lload           this
        //    16: iconst_m1      
        //    17: nop            
        //    18: nop            
        //    19: nop            
        //    20: lload_1         /* f */
        //    21: nop            
        //    22: lload_3        
        //    23: nop            
        //    24: fload_0         /* this */
        //    25: nop            
        //    26: nop            
        //    27: nop            
        //    28: nop            
        //    29: nop            
        //    30: lload_1         /* f */
        //    31: nop            
        //    32: fload           this
        //    34: dload           this
        //    36: aconst_null    
        //    37: nop            
        //    38: aconst_null    
        //    39: nop            
        //    40: aload           this
        //    42: fload_1         /* f */
        //    43: nop            
        //    44: aconst_null    
        //    45: nop            
        //    46: lload_0         /* this */
        //    47: nop            
        //    48: nop            
        //    49: nop            
        //    50: laload         
        //    51: nop            
        //    52: aconst_null    
        //    53: nop            
        //    54: aconst_null    
        //    55: nop            
        //    56: nop            
        //    57: nop            
        //    58: iconst_2       
        //    59: aload_0         /* this */
        //    60: getfield        com/sun/codemodel/JConditional._then:Lcom/sun/codemodel/JBlock;
        //    63: areturn        
        //    64: nop            
        //    65: nop            
        //    66: nop            
        //    67: iconst_m1      
        //    68: nop            
        //    69: lload_1         /* f */
        //    70: nop            
        //    71: nop            
        //    72: nop            
        //    73: iconst_3       
        //    74: nop            
        //    75: aconst_null    
        //    76: nop            
        //    77: nop            
        //    78: nop            
        //    79: istore_1        /* f */
        //    80: nop            
        //    81: lload_2        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------
        //  0      82      0     this  Lcom/sun/codemodel/JConditional;
        //  0      82      1     f     Lcom/sun/codemodel/JFormatter;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException: 2
        //     at com.strobel.decompiler.ast.AstBuilder.convertLocalVariables(AstBuilder.java:2708)
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
}
