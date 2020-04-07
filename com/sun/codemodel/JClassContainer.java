// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

import java.util.Iterator;

public interface JClassContainer
{
    JDefinedClass _class(final int p0, final String p1) throws JClassAlreadyExistsException;
    
    JDefinedClass _class(final String p0) throws JClassAlreadyExistsException;
    
    JDefinedClass _interface(final int p0, final String p1) throws JClassAlreadyExistsException;
    
    JDefinedClass _interface(final String p0) throws JClassAlreadyExistsException;
    
    JDefinedClass _class(final int p0, final String p1, final boolean p2) throws JClassAlreadyExistsException;
    
    Iterator classes();
    
    JClassContainer parentContainer();
    
    JCodeModel owner();
}
