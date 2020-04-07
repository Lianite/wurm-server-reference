// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

public class JVar extends JExpressionImpl implements JDeclaration, JAssignmentTarget
{
    private JMods mods;
    JType type;
    String name;
    JExpression init;
    
    JVar(final JMods mods, final JType type, final String name, final JExpression init) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: frem           
        //     2: ldiv           
        //     3: ladd           
        //     4: ineg           
        //     5: ineg           
        //     6: lsub           
        //     7: frem           
        //     8: istore_0        /* this */
        //     9: dload_3         /* name */
        //    10: sastore        
        //    11: aconst_null    
        //    12: nop            
        //    13: aconst_null    
        //    14: lmul           
        //    15: aconst_null    
        //    16: nop            
        //    17: aconst_null    
        //    18: dstore_2        /* type */
        //    19: aconst_null    
        //    20: nop            
        //    21: aconst_null    
        //    22: fsub           
        //    23: aconst_null    
        //    24: nop            
        //    25: lload_0         /* this */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------
        //  0      26      0     this  Lcom/sun/codemodel/JVar;
        //  0      26      1     mods  Lcom/sun/codemodel/JMods;
        //  0      26      2     type  Lcom/sun/codemodel/JType;
        //  0      26      3     name  Ljava/lang/String;
        //  0      26      4     init  Lcom/sun/codemodel/JExpression;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JVar init(final JExpression init) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fstore_2       
        //     1: ishl           
        //     2: irem           
        //     3: frem           
        //     4: lsub           
        //     5: drem           
        //     6: drem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------
        //  0      7       0     this  Lcom/sun/codemodel/JVar;
        //  0      7       1     init  Lcom/sun/codemodel/JExpression;
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
        //     0: dload_0         /* this */
        //     1: nop            
        //     2: dload_1        
        //     3: fconst_1       
        //     4: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------
        //  0      5       0     this  Lcom/sun/codemodel/JVar;
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
        //     0: lneg           
        //     1: ineg           
        //     2: lmul           
        //     3: idiv           
        //     4: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------
        //  0      5       0     this  Lcom/sun/codemodel/JVar;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void bind(final JFormatter f) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: l2f            
        //     1: iconst_4       
        //     2: nop            
        //     3: l2d            
        //     4: fconst_1       
        //     5: nop            
        //     6: f2i            
        //     7: nop            
        //     8: f2l            
        //     9: iconst_4       
        //    10: nop            
        //    11: f2d            
        //    12: fconst_1       
        //    13: nop            
        //    14: d2i            
        //    15: nop            
        //    16: d2l            
        //    17: fconst_1       
        //    18: nop            
        //    19: istore_2       
        //    20: nop            
        //    21: istore_3       
        //    22: iconst_4       
        //    23: nop            
        //    24: d2f            
        //    25: fconst_1       
        //    26: nop            
        //    27: i2b            
        //    28: nop            
        //    29: i2c            
        //    30: fconst_1       
        //    31: nop            
        //    32: i2s            
        //    33: nop            
        //    34: lcmp           
        //    35: fconst_1       
        //    36: nop            
        //    37: fcmpl          
        //    38: nop            
        //    39: fcmpg          
        //    40: aconst_null    
        //    41: nop            
        //    42: iload_2        
        //    43: dadd           
        //    44: ddiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------
        //  0      45      0     this  Lcom/sun/codemodel/JVar;
        //  0      45      1     f     Lcom/sun/codemodel/JFormatter;
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
        // Original Bytecode:
        // 
        //     0: dadd           
        //     1: ddiv           
        //     2: isub           
        //     3: lsub           
        //     4: ldiv           
        //     5: ddiv           
        //     6: isub           
        //     7: lsub           
        //     8: idiv           
        //     9: laload         
        //    10: dstore_3       
        //    11: sastore        
        //    12: ladd           
        //    13: frem           
        //    14: aconst_null    
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------
        //  0      15      0     this  Lcom/sun/codemodel/JVar;
        //  0      15      1     f     Lcom/sun/codemodel/JFormatter;
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
        //     0: fload_1         /* f */
        //     1: dadd           
        //     2: ddiv           
        //     3: ldiv           
        //     4: laload         
        //     5: drem           
        //     6: lneg           
        //     7: fdiv           
        //     8: laload         
        //     9: dadd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------
        //  0      10      0     this  Lcom/sun/codemodel/JVar;
        //  0      10      1     f     Lcom/sun/codemodel/JFormatter;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JExpression assign(final JExpression rhs) {
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
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------
        //  0      6       0     this  Lcom/sun/codemodel/JVar;
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
        //     0: astore_1        /* rhs */
        //     1: dadd           
        //     2: ddiv           
        //     3: ldiv           
        //     4: laload         
        //     5: drem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------
        //  0      6       0     this  Lcom/sun/codemodel/JVar;
        //  0      6       1     rhs   Lcom/sun/codemodel/JExpression;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
