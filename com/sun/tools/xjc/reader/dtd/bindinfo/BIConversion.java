// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.dtd.bindinfo;

import com.sun.tools.xjc.grammar.xducer.Transducer;

public interface BIConversion
{
    String name();
    
    Transducer getTransducer();
}
