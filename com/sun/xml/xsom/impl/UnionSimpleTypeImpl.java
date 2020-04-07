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
import com.sun.xml.xsom.XSUnionSimpleType;

public class UnionSimpleTypeImpl extends SimpleTypeImpl implements XSUnionSimpleType
{
    private final Ref.SimpleType[] memberTypes;
    
    public UnionSimpleTypeImpl(final SchemaImpl _parent, final AnnotationImpl _annon, final Locator _loc, final String _name, final boolean _anonymous, final Ref.SimpleType[] _members) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: irem           
        //     1: lsub           
        //     2: iconst_4       
        //     3: nop            
        //     4: lushr          
        //     5: aconst_null    
        //     6: nop            
        //     7: fload_2         /* _annon */
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
        //  -----  ------  ----  ----------  -------------------------------------------
        //  0      25      0     this        Lcom/sun/xml/xsom/impl/UnionSimpleTypeImpl;
        //  0      25      1     _parent     Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      25      2     _annon      Lcom/sun/xml/xsom/impl/AnnotationImpl;
        //  0      25      3     _loc        Lorg/xml/sax/Locator;
        //  0      25      4     _name       Ljava/lang/String;
        //  0      25      5     _anonymous  Z
        //  0      25      6     _members    [Lcom/sun/xml/xsom/impl/Ref$SimpleType;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSSimpleType getMember(final int idx) {
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
        //     7: ishl           
        //     8: ldiv           
        //     9: idiv           
        //    10: laload         
        //    11: ishl           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------
        //  0      12      0     this  Lcom/sun/xml/xsom/impl/UnionSimpleTypeImpl;
        //  0      12      1     idx   I
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public int getMemberSize() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: idiv           
        //     1: istore_0        /* this */
        //     2: aconst_null    
        //     3: nop            
        //     4: fload_1        
        //     5: dadd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------
        //  0      6       0     this  Lcom/sun/xml/xsom/impl/UnionSimpleTypeImpl;
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
        //     0: lstore_2       
        //     1: fdiv           
        //     2: lshl           
        //     3: aastore        
        //     4: lmul           
        //     5: ldiv           
        //     6: irem           
        //     7: idiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ----------------------------------------------
        //  0      8       0     this     Lcom/sun/xml/xsom/impl/UnionSimpleTypeImpl;
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
        //     0: fdiv           
        //     1: laload         
        //     2: ishl           
        //     3: ldiv           
        //     4: idiv           
        //     5: laload         
        //     6: ishl           
        //     7: drem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  -----------------------------------------------
        //  0      8       0     this      Lcom/sun/xml/xsom/impl/UnionSimpleTypeImpl;
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
        //     0: fdiv           
        //     1: dstore_2       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/UnionSimpleTypeImpl;
        //  0      2       1     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
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
        //     1: irem           
        //     2: idiv           
        //     3: istore_0        /* this */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------
        //  0      4       0     this  Lcom/sun/xml/xsom/impl/UnionSimpleTypeImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isUnion() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: dastore        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/UnionSimpleTypeImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSUnionSimpleType asUnion() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ddiv           
        //     1: fdiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/UnionSimpleTypeImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
