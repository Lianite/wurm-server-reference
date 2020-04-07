// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.JAXBException;
import org.xml.sax.ContentHandler;
import com.sun.msv.verifier.IVerifier;
import com.sun.msv.verifier.VerifierFilter;
import org.xml.sax.ErrorHandler;
import com.sun.msv.verifier.DocumentDeclaration;
import com.sun.msv.verifier.Verifier;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.xml.bind.validator.Locator;
import com.sun.msv.grammar.Grammar;
import org.xml.sax.helpers.AttributesImpl;
import org.iso_relax.verifier.impl.ForkContentHandler;

public class ValidatingUnmarshaller extends ForkContentHandler implements SAXUnmarshallerHandler
{
    private final SAXUnmarshallerHandler core;
    private final AttributesImpl xsiLessAtts;
    
    public static ValidatingUnmarshaller create(final Grammar grammar, final SAXUnmarshallerHandler _core, final Locator locator) {
        final Verifier v = new Verifier((DocumentDeclaration)new REDocumentDeclaration(grammar), (ErrorHandler)new ErrorHandlerAdaptor(_core, locator));
        v.setPanicMode(true);
        return new ValidatingUnmarshaller(new VerifierFilter((IVerifier)v), _core);
    }
    
    private ValidatingUnmarshaller(final VerifierFilter filter, final SAXUnmarshallerHandler _core) {
        super((ContentHandler)filter, (ContentHandler)_core);
        this.xsiLessAtts = new AttributesImpl();
        this.core = _core;
    }
    
    public Object getResult() throws JAXBException, IllegalStateException {
        return this.core.getResult();
    }
    
    public void handleEvent(final ValidationEvent event, final boolean canRecover) throws SAXException {
        this.core.handleEvent(event, canRecover);
    }
    
    public void startElement(final String nsUri, final String local, final String qname, final Attributes atts) throws SAXException {
        this.xsiLessAtts.clear();
        for (int len = atts.getLength(), i = 0; i < len; ++i) {
            final String aUri = atts.getURI(i);
            final String aLocal = atts.getLocalName(i);
            if (aUri.equals("http://www.w3.org/2001/XMLSchema-instance")) {
                if (aLocal.equals("schemaLocation")) {
                    continue;
                }
                if (aLocal.equals("noNamespaceSchemaLocation")) {
                    continue;
                }
            }
            this.xsiLessAtts.addAttribute(aUri, aLocal, atts.getQName(i), atts.getType(i), atts.getValue(i));
        }
        super.startElement(nsUri, local, qname, (Attributes)this.xsiLessAtts);
    }
}
