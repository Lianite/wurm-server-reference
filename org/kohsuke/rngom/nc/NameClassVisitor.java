// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.nc;

import javax.xml.namespace.QName;

public interface NameClassVisitor<V>
{
    V visitChoice(final NameClass p0, final NameClass p1);
    
    V visitNsName(final String p0);
    
    V visitNsNameExcept(final String p0, final NameClass p1);
    
    V visitAnyName();
    
    V visitAnyNameExcept(final NameClass p0);
    
    V visitName(final QName p0);
    
    V visitNull();
}
