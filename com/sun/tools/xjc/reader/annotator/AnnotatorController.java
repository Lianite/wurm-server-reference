// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.annotator;

import com.sun.tools.xjc.ErrorReceiver;
import org.xml.sax.Locator;
import com.sun.msv.grammar.Expression;
import com.sun.tools.xjc.reader.PackageTracker;
import com.sun.tools.xjc.reader.NameConverter;

public interface AnnotatorController
{
    NameConverter getNameConverter();
    
    PackageTracker getPackageTracker();
    
    void reportError(final Expression[] p0, final String p1);
    
    void reportError(final Locator[] p0, final String p1);
    
    ErrorReceiver getErrorReceiver();
}
