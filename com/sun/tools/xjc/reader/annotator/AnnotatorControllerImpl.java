// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.annotator;

import org.xml.sax.Locator;
import java.util.Vector;
import com.sun.msv.grammar.Expression;
import com.sun.tools.xjc.reader.NameConverter;
import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.xjc.reader.PackageTracker;
import com.sun.msv.reader.GrammarReader;

public class AnnotatorControllerImpl implements AnnotatorController
{
    private final GrammarReader reader;
    private final PackageTracker tracker;
    private final ErrorReceiver errorReceiver;
    
    public AnnotatorControllerImpl(final GrammarReader _reader, final ErrorReceiver _errorReceiver, final PackageTracker _tracker) {
        this.reader = _reader;
        this.tracker = _tracker;
        this.errorReceiver = _errorReceiver;
    }
    
    public NameConverter getNameConverter() {
        return NameConverter.smart;
    }
    
    public PackageTracker getPackageTracker() {
        return this.tracker;
    }
    
    public void reportError(final Expression[] srcs, final String msg) {
        final Vector locs = new Vector();
        for (int i = 0; i < srcs.length; ++i) {
            final Locator loc = this.reader.getDeclaredLocationOf((Object)srcs[i]);
            if (loc != null) {
                locs.add(loc);
            }
        }
        this.reader.controller.error((Locator[])locs.toArray(new Locator[0]), msg, (Exception)null);
    }
    
    public void reportError(final Locator[] srcs, final String msg) {
        this.reader.controller.error(srcs, msg, (Exception)null);
    }
    
    public ErrorReceiver getErrorReceiver() {
        return this.errorReceiver;
    }
}
