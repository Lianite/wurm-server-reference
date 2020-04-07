// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl;

import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSElementDecl;

public abstract class Ref
{
    public Ref() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: bastore        
        //     1: lshl           
        //     2: irem           
        //     3: lsub           
        //     4: aconst_null    
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/Ref;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public interface Element extends Term
    {
        XSElementDecl get();
    }
    
    public interface AttGroup
    {
        XSAttGroupDecl get();
    }
    
    public interface Attribute
    {
        XSAttributeDecl getAttribute();
    }
    
    public interface ComplexType extends Type
    {
        XSComplexType getType();
    }
    
    public interface SimpleType extends Type
    {
        XSSimpleType getType();
    }
    
    public interface ContentType
    {
        XSContentType getContentType();
    }
    
    public interface Type
    {
        XSType getType();
    }
    
    public interface Term
    {
        XSTerm getTerm();
    }
}
