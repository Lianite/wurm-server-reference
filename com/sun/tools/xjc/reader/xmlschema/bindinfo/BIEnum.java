// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import org.xml.sax.Locator;
import javax.xml.namespace.QName;
import java.util.HashMap;

public final class BIEnum extends AbstractDeclarationImpl
{
    private final String className;
    private final String javadoc;
    private final HashMap members;
    public static final QName NAME;
    
    public BIEnum(final Locator loc, final String _className, final String _javadoc, final HashMap _members) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: aaload         
        //     2: dadd           
        //     3: ddiv           
        //     4: ldiv           
        //     5: laload         
        //     6: drem           
        //     7: lneg           
        //     8: fdiv           
        //     9: laload         
        //    10: ineg           
        //    11: ddiv           
        //    12: ddiv           
        //    13: idiv           
        //    14: drem           
        //    15: laload         
        //    16: ishl           
        //    17: fmul           
        //    18: dadd           
        //    19: laload         
        //    20: frem           
        //    21: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name        Signature
        //  -----  ------  ----  ----------  ----------------------------------------------------
        //  0      22      0     this        Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIEnum;
        //  0      22      1     loc         Lorg/xml/sax/Locator;
        //  0      22      2     _className  Ljava/lang/String;
        //  0      22      3     _javadoc    Ljava/lang/String;
        //  0      22      4     _members    Ljava/util/HashMap;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String getClassName() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ddiv           
        //     1: ddiv           
        //     2: idiv           
        //     3: drem           
        //     4: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIEnum;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String getJavadoc() {
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
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIEnum;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public HashMap getMembers() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: idiv           
        //     1: idiv           
        //     2: lsub           
        //     3: dadd           
        //     4: ineg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIEnum;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public QName getName() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ladd           
        //     1: ineg           
        //     2: ddiv           
        //     3: frem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------------------
        //  0      4       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIEnum;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void setParent(final BindInfo p) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: isub           
        //     2: dsub           
        //     3: lsub           
        //     4: isub           
        //     5: aconst_null    
        //     6: nop            
        //     7: iconst_2       
        //     8: fneg           
        //     9: ladd           
        //    10: idiv           
        //    11: lneg           
        //    12: lsub           
        //    13: aconst_null    
        //    14: nop            
        //    15: iconst_0       
        //    16: irem           
        //    17: lneg           
        //    18: ineg           
        //    19: aconst_null    
        //    20: nop            
        //    21: fstore          40
        //    23: astore_1        /* p */
        //    24: fmul           
        //    25: ladd           
        //    26: fneg           
        //    27: ladd           
        //    28: laload         
        //    29: idiv           
        //    30: ladd           
        //    31: fdiv           
        //    32: dsub           
        //    33: laload         
        //    34: iastore        
        //    35: fadd           
        //    36: fmul           
        //    37: lsub           
        //    38: dadd           
        //    39: ineg           
        //    40: istore_0        /* this */
        //    41: astore_1        /* p */
        //    42: fmul           
        //    43: ladd           
        //    44: fneg           
        //    45: ladd           
        //    46: laload         
        //    47: idiv           
        //    48: ladd           
        //    49: fdiv           
        //    50: dsub           
        //    51: laload         
        //    52: iastore        
        //    53: fadd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------------------------
        //  45     5       3     mem   Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIEnumMember;
        //  0      54      0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIEnum;
        //  0      54      1     p     Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BindInfo;
        //  18     36      2     itr   Ljava/util/Iterator;
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
        //     0: fload_1        
        //     1: nop            
        //     2: aconst_null    
        //     3: nop            
        //     4: lload_1        
        //     5: nop            
        //     6: nop            
        //     7: nop            
        //     8: fconst_0       
        //     9: nop            
        //    10: aconst_null    
        //    11: nop            
        //    12: lload_2        
        //    13: nop            
        //    14: aconst_null    
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
