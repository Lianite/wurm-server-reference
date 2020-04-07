// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

final class MetaVariable
{
    static final boolean M;
    static final boolean U;
    static final boolean V;
    static final boolean W;
    
    static {
        MetaVariable.M = (System.getProperty("jaxb.runtime.M") == null);
        MetaVariable.U = (System.getProperty("jaxb.runtime.U") == null);
        MetaVariable.V = (System.getProperty("jaxb.runtime.V") == null);
        MetaVariable.W = (System.getProperty("jaxb.runtime.W") == null);
    }
}
