// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader;

import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import com.sun.tools.xjc.ErrorReceiver;
import org.xml.sax.EntityResolver;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.tools.xjc.util.ErrorReceiverFilter;

public class GrammarReaderControllerAdaptor extends ErrorReceiverFilter implements GrammarReaderController
{
    private final EntityResolver entityResolver;
    
    public GrammarReaderControllerAdaptor(final ErrorReceiver core, final EntityResolver _entityResolver) {
        super(core);
        this.entityResolver = _entityResolver;
    }
    
    public void warning(final Locator[] locs, final String msg) {
        boolean firstTime = true;
        if (locs != null) {
            for (int i = 0; i < locs.length; ++i) {
                if (locs[i] != null) {
                    if (firstTime) {
                        this.warning(locs[i], msg);
                    }
                    else {
                        this.warning(locs[i], Messages.format("GrammarReaderControllerAdaptor.RelevantLocation"));
                    }
                    firstTime = false;
                }
            }
        }
        if (firstTime) {
            this.warning(null, msg);
        }
    }
    
    public void error(final Locator[] locs, final String msg, final Exception e) {
        boolean firstTime = true;
        if (locs != null) {
            for (int i = 0; i < locs.length; ++i) {
                if (locs[i] != null) {
                    if (firstTime) {
                        this.error(locs[i], msg);
                    }
                    else {
                        this.error(locs[i], Messages.format("GrammarReaderControllerAdaptor.RelevantLocation"));
                    }
                    firstTime = false;
                }
            }
        }
        if (firstTime) {
            this.error(null, msg);
        }
    }
    
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
        if (this.entityResolver == null) {
            return null;
        }
        return this.entityResolver.resolveEntity(publicId, systemId);
    }
}
