// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.msv.grammar.NameClass;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.visitor.XSWildcardFunction;

public final class WildcardNameClassBuilder implements XSWildcardFunction
{
    private static final XSWildcardFunction theInstance;
    
    private WildcardNameClassBuilder() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aastore        
        //     1: pop            
        //     2: lmul           
        //     3: idiv           
        //     4: isub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/xmlschema/WildcardNameClassBuilder;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public static NameClass build(final XSWildcard wc) {
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
        //     9: aastore        
        //    10: pop            
        //    11: lmul           
        //    12: idiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------
        //  0      13      0     wc    Lcom/sun/xml/xsom/XSWildcard;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object any(final XSWildcard.Any wc) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ldiv           
        //     1: laload         
        //     2: drem           
        //     3: lneg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------------------
        //  0      4       0     this  Lcom/sun/tools/xjc/reader/xmlschema/WildcardNameClassBuilder;
        //  0      4       1     wc    Lcom/sun/xml/xsom/XSWildcard$Any;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object other(final XSWildcard.Other wc) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dload_2        
        //     1: astore_1        /* wc */
        //     2: fmul           
        //     3: ladd           
        //     4: fneg           
        //     5: ladd           
        //     6: laload         
        //     7: idiv           
        //     8: ladd           
        //     9: fdiv           
        //    10: dsub           
        //    11: laload         
        //    12: aastore        
        //    13: ineg           
        //    14: frem           
        //    15: lmul           
        //    16: fdiv           
        //    17: dsub           
        //    18: istore_0        /* this */
        //    19: dload_3        
        //    20: sastore        
        //    21: aconst_null    
        //    22: nop            
        //    23: sipush          26469
        //    26: ineg           
        //    27: iastore        
        //    28: ineg           
        //    29: imul           
        //    30: lsub           
        //    31: frem           
        //    32: astore_3       
        //    33: ladd           
        //    34: ldiv           
        //    35: lsub           
        //    36: drem           
        //    37: irem           
        //    38: ladd           
        //    39: dadd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------------------
        //  0      40      0     this  Lcom/sun/tools/xjc/reader/xmlschema/WildcardNameClassBuilder;
        //  0      40      1     wc    Lcom/sun/xml/xsom/XSWildcard$Other;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object union(final XSWildcard.Union wc) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ddiv           
        //     1: imul           
        //     2: drem           
        //     3: lneg           
        //     4: dmul           
        //     5: lsub           
        //     6: laload         
        //     7: frem           
        //     8: fdiv           
        //     9: dsub           
        //    10: ddiv           
        //    11: ldiv           
        //    12: laload         
        //    13: fdiv           
        //    14: dadd           
        //    15: laload         
        //    16: astore_3        /* itr */
        //    17: ladd           
        //    18: ldiv           
        //    19: lsub           
        //    20: fstore_0        /* this */
        //    21: idiv           
        //    22: ladd           
        //    23: drem           
        //    24: drem           
        //    25: istore_0        /* this */
        //    26: dload_3         /* itr */
        //    27: sastore        
        //    28: aconst_null    
        //    29: nop            
        //    30: fload_1         /* wc */
        //    31: dload_2         /* nc */
        //    32: astore_1        /* wc */
        //    33: ddiv           
        //    34: frem           
        //    35: dsub           
        //    36: laload         
        //    37: dmul           
        //    38: ddiv           
        //    39: imul           
        //    40: drem           
        //    41: lneg           
        //    42: dmul           
        //    43: lsub           
        //    44: laload         
        //    45: frem           
        //    46: fdiv           
        //    47: dsub           
        //    48: ddiv           
        //    49: ldiv           
        //    50: laload         
        //    51: fdiv           
        //    52: dadd           
        //    53: laload         
        //    54: astore_3        /* itr */
        //    55: ladd           
        //    56: ldiv           
        //    57: lsub           
        //    58: fstore_0        /* this */
        //    59: idiv           
        //    60: ladd           
        //    61: drem           
        //    62: drem           
        //    63: istore_0        /* this */
        //    64: dload_3         /* itr */
        //    65: sastore        
        //    66: aconst_null    
        //    67: nop            
        //    68: sipush          26996
        //    71: lsub           
        //    72: frem           
        //    73: ladd           
        //    74: ineg           
        //    75: lsub           
        //    76: astore_3       
        //    77: ladd           
        //    78: ldiv           
        //    79: lsub           
        //    80: drem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------------------
        //  29     35      4     ns    Ljava/lang/String;
        //  9      58      3     itr   Ljava/util/Iterator;
        //  0      81      0     this  Lcom/sun/tools/xjc/reader/xmlschema/WildcardNameClassBuilder;
        //  0      81      1     wc    Lcom/sun/xml/xsom/XSWildcard$Union;
        //  2      79      2     nc    Lcom/sun/msv/grammar/NameClass;
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
        //     0: isub           
        //     1: lstore_2       
        //     2: drem           
        //     3: drem           
        //     4: lsub           
        //     5: frem           
        //     6: ineg           
        //     7: lmul           
        //     8: ddiv           
        //     9: fdiv           
        //    10: aastore        
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
