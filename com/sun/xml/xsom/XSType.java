// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom;

public interface XSType extends XSDeclaration
{
    public static final int EXTENSION = 1;
    public static final int RESTRICTION = 2;
    public static final int SUBSTITUTION = 4;
    
    XSType getBaseType();
    
    int getDerivationMethod();
    
    boolean isSimpleType();
    
    boolean isComplexType();
    
    XSType[] listSubstitutables();
    
    XSSimpleType asSimpleType();
    
    XSComplexType asComplexType();
}
