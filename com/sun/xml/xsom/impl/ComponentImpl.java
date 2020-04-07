// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl;

import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSSchema;
import org.xml.sax.Locator;
import com.sun.xml.xsom.XSComponent;

public abstract class ComponentImpl implements XSComponent
{
    protected final SchemaImpl ownerSchema;
    private final AnnotationImpl annotation;
    private final Locator locator;
    
    protected ComponentImpl(final SchemaImpl _owner, final AnnotationImpl _annon, final Locator _loc) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: idiv           
        //     1: laload         
        //     2: aastore        
        //     3: dadd           
        //     4: imul           
        //     5: lsub           
        //     6: ldiv           
        //     7: ladd           
        //     8: dstore_2        /* _annon */
        //     9: ldiv           
        //    10: irem           
        //    11: idiv           
        //    12: istore_0        /* this */
        //    13: aconst_null    
        //    14: nop            
        //    15: iconst_4       
        //    16: dsub           
        //    17: lsub           
        //    18: ineg           
        //    19: dastore        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  --------------------------------------
        //  0      20      0     this    Lcom/sun/xml/xsom/impl/ComponentImpl;
        //  0      20      1     _owner  Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      20      2     _annon  Lcom/sun/xml/xsom/impl/AnnotationImpl;
        //  0      20      3     _loc    Lorg/xml/sax/Locator;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public final XSSchema getOwnerSchema() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: istore_0        /* this */
        //     1: aconst_null    
        //     2: nop            
        //     3: fconst_2       
        //     4: dsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/ComponentImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public final XSAnnotation getAnnotation() {
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
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/ComponentImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public final Locator getLocator() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ladd           
        //     1: ineg           
        //     2: ddiv           
        //     3: frem           
        //     4: aconst_null    
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/ComponentImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
