// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl;

import com.sun.xml.xsom.XSVariety;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.visitor.XSSimpleTypeFunction;
import com.sun.xml.xsom.visitor.XSSimpleTypeVisitor;
import com.sun.xml.xsom.XSSimpleType;
import org.xml.sax.Locator;
import com.sun.xml.xsom.XSListSimpleType;

public class ListSimpleTypeImpl extends SimpleTypeImpl implements XSListSimpleType
{
    private final Ref.SimpleType itemType;
    
    public ListSimpleTypeImpl(final SchemaImpl _parent, final AnnotationImpl _annon, final Locator _loc, final String _name, final boolean _anonymous, final Ref.SimpleType _itemType) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ldiv           
        //     1: ladd           
        //     2: aconst_null    
        //     3: nop            
        //     4: fload_2         /* _annon */
        //     5: dload_2         /* _annon */
        //     6: dload_3         /* _loc */
        //     7: astore_1        /* _parent */
        //     8: dadd           
        //     9: ddiv           
        //    10: ldiv           
        //    11: laload         
        //    12: drem           
        //    13: lneg           
        //    14: fdiv           
        //    15: laload         
        //    16: ishl           
        //    17: ldiv           
        //    18: idiv           
        //    19: laload         
        //    20: ishl           
        //    21: drem           
        //    22: ddiv           
        //    23: ldiv           
        //    24: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name        Signature
        //  -----  ------  ----  ----------  ------------------------------------------
        //  0      25      0     this        Lcom/sun/xml/xsom/impl/ListSimpleTypeImpl;
        //  0      25      1     _parent     Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      25      2     _annon      Lcom/sun/xml/xsom/impl/AnnotationImpl;
        //  0      25      3     _loc        Lorg/xml/sax/Locator;
        //  0      25      4     _name       Ljava/lang/String;
        //  0      25      5     _anonymous  Z
        //  0      25      6     _itemType   Lcom/sun/xml/xsom/impl/Ref$SimpleType;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSSimpleType getItemType() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: aastore        
        //     2: dadd           
        //     3: imul           
        //     4: lsub           
        //     5: ldiv           
        //     6: ladd           
        //     7: aastore        
        //     8: lsub           
        //     9: ineg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------------
        //  0      10      0     this  Lcom/sun/xml/xsom/impl/ListSimpleTypeImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void visit(final XSSimpleTypeVisitor visitor) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: drem           
        //     1: ddiv           
        //     2: ldiv           
        //     3: laload         
        //     4: lmul           
        //     5: ldiv           
        //     6: irem           
        //     7: idiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ----------------------------------------------
        //  0      8       0     this     Lcom/sun/xml/xsom/impl/ListSimpleTypeImpl;
        //  0      8       1     visitor  Lcom/sun/xml/xsom/visitor/XSSimpleTypeVisitor;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object apply(final XSSimpleTypeFunction function) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lmul           
        //     1: ldiv           
        //     2: irem           
        //     3: idiv           
        //     4: lsub           
        //     5: bastore        
        //     6: lshl           
        //     7: irem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  -----------------------------------------------
        //  0      8       0     this      Lcom/sun/xml/xsom/impl/ListSimpleTypeImpl;
        //  0      8       1     function  Lcom/sun/xml/xsom/visitor/XSSimpleTypeFunction;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSFacet getFacet(final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ListSimpleTypeImpl;
        //  0      2       1     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSVariety getVariety() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ldiv           
        //     1: laload         
        //     2: pop2           
        //     3: aastore        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------------
        //  0      4       0     this  Lcom/sun/xml/xsom/impl/ListSimpleTypeImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isList() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: sastore        
        //     1: ladd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ListSimpleTypeImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSListSimpleType asList() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: idiv           
        //     1: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ListSimpleTypeImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
