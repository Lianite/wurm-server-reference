// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.util;

public final class Util
{
    private Util() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: nop            
        //     2: bipush          106
        //     4: ladd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/util/Util;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public static final String getSystemProperty(final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: nop            
        //     2: iconst_4       
        //     3: dsub           
        //     4: lsub           
        //     5: ineg           
        //     6: astore_3       
        //     7: ladd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------
        //  6      2       1     e     Ljava/lang/SecurityException;
        //  0      8       0     name  Ljava/lang/String;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                         
        //  -----  -----  -----  -----  -----------------------------
        //  0      4      5      8      Ljava/lang/SecurityException;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public static final String getSystemProperty(final Class clazz, final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lstore_3       
        //     1: lneg           
        //     2: lmul           
        //     3: idiv           
        //     4: isub           
        //     5: lsub           
        //     6: frem           
        //     7: istore_0        /* clazz */
        //     8: aconst_null    
        //     9: nop            
        //    10: iconst_5       
        //    11: ineg           
        //    12: ddiv           
        //    13: aastore        
        //    14: ineg           
        //    15: frem           
        //    16: lmul           
        //    17: fdiv           
        //    18: dsub           
        //    19: nop            
        //    20: daload         
        //    21: nop            
        //    22: sipush          18
        //    25: nop            
        //    26: nop            
        //    27: nop            
        //    28: nop            
        //    29: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name   Signature
        //  -----  ------  ----  -----  ------------------
        //  0      30      0     clazz  Ljava/lang/Class;
        //  0      30      1     name   Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public static int calculateInitialHashMapCapacity(final int count, float loadFactor) {
        null;
        loadFactor = count;
        null;
        -1;
        5;
        return (int)System.getProperty((String)count);
    }
}
