// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import com.sun.relaxng.javadt.DatatypeLibraryImpl;
import com.sun.msv.grammar.relaxng.datatype.BuiltinDatatypeLibrary;
import com.sun.msv.datatype.xsd.ngimpl.DataTypeLibraryImpl;
import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;
import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierFactory;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.iso_relax.verifier.impl.ForkContentHandler;
import com.sun.msv.verifier.jarv.RELAXNGFactoryImpl;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.parser.NGCCHandler;
import com.sun.xml.bind.JAXBAssertionError;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import com.sun.xml.xsom.parser.AnnotationContext;
import com.sun.tools.xjc.Options;
import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.parser.AnnotationState;
import com.sun.xml.xsom.parser.AnnotationParser;

public class AnnotationParserImpl extends AnnotationParser
{
    private AnnotationState parser;
    private final JCodeModel codeModel;
    private final Options options;
    
    public AnnotationParserImpl(final JCodeModel cm, final Options opts) {
        this.parser = null;
        this.codeModel = cm;
        this.options = opts;
    }
    
    public ContentHandler getContentHandler(final AnnotationContext context, final String parentElementName, final ErrorHandler errorHandler, final EntityResolver entityResolver) {
        try {
            if (this.parser != null) {
                throw new JAXBAssertionError();
            }
            final NGCCRuntimeEx runtime = new NGCCRuntimeEx(this.codeModel, this.options, errorHandler);
            runtime.setRootHandler((NGCCHandler)(this.parser = new AnnotationState(runtime)));
            final VerifierFactory factory = (VerifierFactory)new RELAXNGFactoryImpl();
            factory.setProperty("datatypeLibraryFactory", (Object)new DatatypeLibraryFactoryImpl((AnnotationParserImpl$1)null));
            final Verifier v = factory.newVerifier(this.getClass().getClassLoader().getResourceAsStream("com/sun/tools/xjc/reader/xmlschema/bindinfo/binding.purified.rng"));
            v.setErrorHandler(errorHandler);
            return (ContentHandler)new ForkContentHandler((ContentHandler)v.getVerifierHandler(), (ContentHandler)runtime);
        }
        catch (VerifierConfigurationException e) {
            e.printStackTrace();
            throw new InternalError();
        }
        catch (SAXException e2) {
            e2.printStackTrace();
            throw new InternalError();
        }
        catch (IOException e3) {
            e3.printStackTrace();
            throw new InternalError();
        }
    }
    
    public Object getResult(final Object existing) {
        if (this.parser == null) {
            throw new JAXBAssertionError();
        }
        if (existing != null) {
            final BindInfo bie = (BindInfo)existing;
            bie.absorb(this.parser.bi);
            return bie;
        }
        return this.parser.bi;
    }
    
    private static class DatatypeLibraryFactoryImpl implements DatatypeLibraryFactory
    {
        public DatatypeLibrary createDatatypeLibrary(final String namespaceURI) {
            if (namespaceURI.equals("http://www.w3.org/2001/XMLSchema-datatypes")) {
                return (DatatypeLibrary)new DataTypeLibraryImpl();
            }
            if (namespaceURI.equals("")) {
                return (DatatypeLibrary)BuiltinDatatypeLibrary.theInstance;
            }
            if (namespaceURI.equals("http://java.sun.com/xml/ns/relaxng/java-datatypes")) {
                return new DatatypeLibraryImpl();
            }
            return null;
        }
    }
}
