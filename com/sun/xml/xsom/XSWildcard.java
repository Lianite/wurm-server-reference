// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom;

import java.util.Collection;
import java.util.Iterator;
import com.sun.xml.xsom.visitor.XSWildcardFunction;
import com.sun.xml.xsom.visitor.XSWildcardVisitor;

public interface XSWildcard extends XSComponent, XSTerm
{
    public static final int LAX = 1;
    public static final int STRTICT = 2;
    public static final int SKIP = 3;
    
    int getMode();
    
    boolean acceptsNamespace(final String p0);
    
    void visit(final XSWildcardVisitor p0);
    
    Object apply(final XSWildcardFunction p0);
    
    public interface Union extends XSWildcard
    {
        Iterator<String> iterateNamespaces();
        
        Collection<String> getNamespaces();
    }
    
    public interface Other extends XSWildcard
    {
        String getOtherNamespace();
    }
    
    public interface Any extends XSWildcard
    {
    }
}
