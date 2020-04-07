// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

public class JFieldRef extends JExpressionImpl implements JAssignmentTarget
{
    private JGenerable object;
    private String name;
    private boolean explicitThis;
    
    JFieldRef(final JExpression object, final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: aconst_null    
        //     2: nop            
        //     3: aconst_null    
        //     4: irem           
        //     5: aconst_null    
        //     6: nop            
        //     7: lload_3        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  -------------------------------
        //  0      8       0     this    Lcom/sun/codemodel/JFieldRef;
        //  0      8       1     object  Lcom/sun/codemodel/JExpression;
        //  0      8       2     name    Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    JFieldRef(final JType type, final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: ineg           
        //     2: ineg           
        //     3: lsub           
        //     4: frem           
        //     5: istore_0        /* this */
        //     6: aconst_null    
        //     7: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------
        //  0      8       0     this  Lcom/sun/codemodel/JFieldRef;
        //  0      8       1     type  Lcom/sun/codemodel/JType;
        //  0      8       2     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    JFieldRef(final JGenerable object, final String name, final boolean explicitThis) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: isub           
        //     2: lsub           
        //     3: ldiv           
        //     4: ddiv           
        //     5: isub           
        //     6: lsub           
        //     7: idiv           
        //     8: laload         
        //     9: dstore_3        /* explicitThis */
        //    10: fstore_2        /* name */
        //    11: ishl           
        //    12: irem           
        //    13: frem           
        //    14: lsub           
        //    15: drem           
        //    16: drem           
        //    17: lmul           
        //    18: ddiv           
        //    19: fdiv           
        //    20: istore_0        /* this */
        //    21: dload_3         /* explicitThis */
        //    22: astore_1        /* object */
        //    23: dadd           
        //    24: ddiv           
        //    25: ldiv           
        //    26: laload         
        //    27: drem           
        //    28: lneg           
        //    29: fdiv           
        //    30: laload         
        //    31: dadd           
        //    32: ddiv           
        //    33: isub           
        //    34: lsub           
        //    35: ldiv           
        //    36: ddiv           
        //    37: isub           
        //    38: lsub           
        //    39: idiv           
        //    40: laload         
        //    41: dstore_3        /* explicitThis */
        //    42: fstore_2        /* name */
        //    43: ishl           
        //    44: irem           
        //    45: frem           
        //    46: lsub           
        //    47: drem           
        //    48: drem           
        //    49: lmul           
        //    50: ddiv           
        //    51: fdiv           
        //    52: istore_0        /* this */
        //    53: nop            
        //    54: lload_3         /* explicitThis */
        //    55: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name          Signature
        //  -----  ------  ----  ------------  ------------------------------
        //  0      56      0     this          Lcom/sun/codemodel/JFieldRef;
        //  0      56      1     object        Lcom/sun/codemodel/JGenerable;
        //  0      56      2     name          Ljava/lang/String;
        //  0      56      3     explicitThis  Z
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void generate(final JFormatter f) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: nop            
        //     3: nop            
        //     4: iconst_5       
        //     5: nop            
        //     6: iload_1         /* f */
        //     7: nop            
        //     8: aload_0         /* this */
        //     9: nop            
        //    10: aconst_null    
        //    11: nop            
        //    12: nop            
        //    13: nop            
        //    14: iconst_5       
        //    15: nop            
        //    16: iload_3        
        //    17: nop            
        //    18: lload_0         /* this */
        //    19: nop            
        //    20: iconst_m1      
        //    21: nop            
        //    22: nop            
        //    23: nop            
        //    24: fload_1         /* f */
        //    25: nop            
        //    26: aload_1         /* f */
        //    27: nop            
        //    28: aconst_null    
        //    29: nop            
        //    30: fload_3        
        //    31: nop            
        //    32: nop            
        //    33: nop            
        //    34: dstore_3       
        //    35: nop            
        //    36: iconst_1       
        //    37: nop            
        //    38: iconst_0       
        //    39: nop            
        //    40: nop            
        //    41: nop            
        //    42: iconst_5       
        //    43: aload_0         /* this */
        //    44: aload_1         /* f */
        //    45: aload_2        
        //    46: iconst_0       
        //    47: invokespecial   com/sun/codemodel/JExpressionImpl.<init>:()V
        //    50: return         
        //    51: nop            
        //    52: nop            
        //    53: nop            
        //    54: iconst_m1      
        //    55: nop            
        //    56: dload_0         /* this */
        //    57: nop            
        //    58: nop            
        //    59: nop            
        //    60: lconst_1       
        //    61: nop            
        //    62: iconst_m1      
        //    63: nop            
        //    64: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------
        //  0      65      0     this  Lcom/sun/codemodel/JFieldRef;
        //  0      65      1     f     Lcom/sun/codemodel/JFormatter;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException: 3
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2064)
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
    
    public JExpression assign(final JExpression rhs) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: nop            
        //     3: dstore_1        /* rhs */
        //     4: nop            
        //     5: iconst_4       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------
        //  0      6       0     this  Lcom/sun/codemodel/JFieldRef;
        //  0      6       1     rhs   Lcom/sun/codemodel/JExpression;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JExpression assignPlus(final JExpression rhs) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: invokespecial   com/sun/codemodel/JExpressionImpl.<init>:()V
        //     3: return         
        //     4: nop            
        //     5: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------
        //  0      6       0     this  Lcom/sun/codemodel/JFieldRef;
        //  0      6       1     rhs   Lcom/sun/codemodel/JExpression;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
