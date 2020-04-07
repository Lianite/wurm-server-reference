// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc;

import com.sun.tools.xjc.reader.xmlschema.parser.CustomizationContextChecker;
import com.sun.tools.xjc.reader.xmlschema.parser.IncorrectNamespaceURIChecker;
import com.sun.tools.xjc.reader.ExtensionBindingChecker;
import org.xml.sax.helpers.XMLFilterImpl;
import com.sun.tools.xjc.reader.xmlschema.parser.ProhibitedFeaturesFilter;
import org.xml.sax.EntityResolver;
import org.xml.sax.ContentHandler;
import javax.xml.parsers.SAXParserFactory;
import com.sun.tools.xjc.reader.relaxng.TRELAXNGReader;
import com.sun.xml.xsom.impl.parser.SAXParserFactoryAdaptor;
import com.sun.tools.xjc.reader.relaxng.CustomizationConverter;
import com.sun.tools.xjc.reader.relaxng.RELAXNGInternalizationLogic;
import com.sun.xml.xsom.parser.AnnotationParserFactory;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.AnnotationParserFactoryImpl;
import com.sun.xml.xsom.parser.XMLParser;
import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import com.sun.tools.xjc.reader.internalizer.DOMForestScanner;
import com.sun.xml.xsom.parser.XSOMParser;
import com.sun.tools.xjc.reader.xmlschema.parser.XMLSchemaInternalizationLogic;
import com.sun.tools.xjc.reader.xmlschema.parser.SchemaConstraintChecker;
import com.sun.xml.bind.util.Which;
import com.sun.xml.xsom.XSSchemaSet;
import org.w3c.dom.Element;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.tools.xjc.reader.internalizer.DOMForest;
import com.sun.tools.xjc.reader.internalizer.InternalizationLogic;
import com.sun.tools.xjc.reader.dtd.TDTDReader;
import com.sun.msv.grammar.ExpressionPool;
import org.xml.sax.Locator;
import org.dom4j.DocumentFactory;
import com.sun.xml.bind.JAXBAssertionError;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import com.sun.codemodel.JCodeModel;
import java.io.IOException;
import org.xml.sax.SAXException;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import com.sun.tools.xjc.util.ErrorReceiverFilter;

public final class GrammarLoader
{
    private final Options opt;
    private final ErrorReceiverFilter errorReceiver;
    
    public static AnnotatedGrammar load(final Options opt, final ErrorReceiver er) throws SAXException, IOException {
        return new GrammarLoader(opt, er).load();
    }
    
    public GrammarLoader(final Options _opt, final ErrorReceiver er) {
        this.opt = _opt;
        this.errorReceiver = new ErrorReceiverFilter(er);
    }
    
    private AnnotatedGrammar load() throws IOException {
        if (!this.sanityCheck()) {
            return null;
        }
        try {
            final JCodeModel codeModel = new JCodeModel();
            AnnotatedGrammar grammar = null;
            switch (this.opt.getSchemaLanguage()) {
                case 0: {
                    InputSource bindFile = null;
                    if (this.opt.getBindFiles().length > 0) {
                        bindFile = this.opt.getBindFiles()[0];
                    }
                    if (bindFile == null) {
                        bindFile = new InputSource(new StringReader("<?xml version='1.0'?><xml-java-binding-schema><options package='" + ((this.opt.defaultPackage == null) ? "generated" : this.opt.defaultPackage) + "'/></xml-java-binding-schema>"));
                    }
                    this.checkTooManySchemaErrors();
                    grammar = this.loadDTD(this.opt.getGrammars()[0], bindFile);
                    break;
                }
                case 2: {
                    this.checkTooManySchemaErrors();
                    grammar = this.loadRELAXNG();
                    break;
                }
                case 3: {
                    this.checkTooManySchemaErrors();
                    grammar = this.annotateXMLSchema(this.loadWSDL(codeModel), codeModel);
                    break;
                }
                case 1: {
                    grammar = this.annotateXMLSchema(this.loadXMLSchema(codeModel), codeModel);
                    break;
                }
                default: {
                    throw new JAXBAssertionError();
                }
            }
            if (this.errorReceiver.hadError()) {
                grammar = null;
            }
            return grammar;
        }
        catch (SAXException e) {
            if (this.opt.debugMode) {
                if (e.getException() != null) {
                    e.getException().printStackTrace();
                }
                else {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
    
    private boolean sanityCheck() {
        if (this.opt.getSchemaLanguage() == 0) {
            try {
                new DocumentFactory();
            }
            catch (NoClassDefFoundError e) {
                this.errorReceiver.error(null, Messages.format("Driver.MissingDOM4J"));
                return false;
            }
        }
        if (this.opt.getSchemaLanguage() == 1) {
            final int guess = this.opt.guessSchemaLanguage();
            String[] msg = null;
            switch (guess) {
                case 0: {
                    msg = new String[] { "DTD", "-dtd" };
                    break;
                }
                case 2: {
                    msg = new String[] { "RELAX NG", "-relaxng" };
                    break;
                }
            }
            if (msg != null) {
                this.errorReceiver.warning(null, Messages.format("Driver.ExperimentalLanguageWarning", (Object)msg[0], (Object)msg[1]));
            }
        }
        return true;
    }
    
    private void checkTooManySchemaErrors() {
        if (this.opt.getGrammars().length != 1) {
            this.errorReceiver.error(null, Messages.format("GrammarLoader.TooManySchema"));
        }
    }
    
    private AnnotatedGrammar loadDTD(final InputSource source, final InputSource bindFile) {
        return TDTDReader.parse(source, bindFile, (ErrorReceiver)this.errorReceiver, this.opt, new ExpressionPool());
    }
    
    public DOMForest buildDOMForest(final InternalizationLogic logic) throws SAXException, IOException {
        DOMForest forest;
        try {
            forest = new DOMForest(logic);
        }
        catch (ParserConfigurationException e) {
            throw new SAXException(e);
        }
        forest.setErrorHandler((ErrorHandler)this.errorReceiver);
        if (this.opt.entityResolver != null) {
            forest.setEntityResolver(this.opt.entityResolver);
        }
        final InputSource[] sources = this.opt.getGrammars();
        for (int i = 0; i < sources.length; ++i) {
            forest.parse(sources[i]);
        }
        final InputSource[] externalBindingFiles = this.opt.getBindFiles();
        for (int j = 0; j < externalBindingFiles.length; ++j) {
            final Element root = forest.parse(externalBindingFiles[j]).getDocumentElement();
            if (!root.getNamespaceURI().equals("http://java.sun.com/xml/ns/jaxb") || !root.getLocalName().equals("bindings")) {
                this.errorReceiver.error(new SAXParseException(Messages.format("Driver.NotABindingFile", (Object)root.getNamespaceURI(), (Object)root.getLocalName()), null, externalBindingFiles[j].getSystemId(), -1, -1));
            }
        }
        forest.transform();
        return forest;
    }
    
    private XSSchemaSet loadXMLSchema(final JCodeModel codeModel) throws SAXException, IOException {
        try {
            this.errorReceiver.info(new SAXParseException("Using Xerces from " + Which.which((GrammarLoader.class$com$sun$org$apache$xerces$internal$impl$Version == null) ? (GrammarLoader.class$com$sun$org$apache$xerces$internal$impl$Version = class$("com.sun.org.apache.xerces.internal.impl.Version")) : GrammarLoader.class$com$sun$org$apache$xerces$internal$impl$Version), (Locator)null));
        }
        catch (Throwable t) {}
        try {
            if (this.opt.strictCheck && !SchemaConstraintChecker.check(this.opt.getGrammars(), this.errorReceiver, this.opt.entityResolver)) {
                return null;
            }
        }
        catch (LinkageError e) {
            this.errorReceiver.warning(new SAXParseException(Messages.format("GrammarLoader.IncompatibleXerces", (Object)e.toString()), (Locator)null));
            try {
                this.errorReceiver.warning(new SAXParseException("Using Xerces from " + Which.which((GrammarLoader.class$com$sun$org$apache$xerces$internal$impl$Version == null) ? (GrammarLoader.class$com$sun$org$apache$xerces$internal$impl$Version = class$("com.sun.org.apache.xerces.internal.impl.Version")) : GrammarLoader.class$com$sun$org$apache$xerces$internal$impl$Version), (Locator)null));
            }
            catch (Throwable t2) {}
            if (this.opt.debugMode) {
                throw e;
            }
        }
        final DOMForest forest = this.buildDOMForest(new XMLSchemaInternalizationLogic());
        final XSOMParser xsomParser = this.createXSOMParser(forest, codeModel);
        final InputSource[] grammars = this.opt.getGrammars();
        for (int i = 0; i < grammars.length; ++i) {
            xsomParser.parse(grammars[i]);
        }
        return xsomParser.getResult();
    }
    
    private XSSchemaSet loadWSDL(final JCodeModel codeModel) throws SAXException, IOException {
        final DOMForest forest = this.buildDOMForest(new XMLSchemaInternalizationLogic());
        final DOMForestScanner scanner = new DOMForestScanner(forest);
        final XSOMParser xsomParser = this.createXSOMParser(forest, codeModel);
        final InputSource[] grammars = this.opt.getGrammars();
        final Document wsdlDom = forest.get(grammars[0].getSystemId());
        final NodeList schemas = wsdlDom.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "schema");
        for (int i = 0; i < schemas.getLength(); ++i) {
            scanner.scan((Element)schemas.item(i), xsomParser.getParserHandler());
        }
        return xsomParser.getResult();
    }
    
    public AnnotatedGrammar annotateXMLSchema(final XSSchemaSet xs, final JCodeModel codeModel) throws SAXException {
        if (xs == null) {
            return null;
        }
        return BGMBuilder.build(xs, codeModel, (ErrorReceiver)this.errorReceiver, this.opt.defaultPackage, this.opt.compatibilityMode == 2);
    }
    
    public XSOMParser createXSOMParser(final DOMForest forest, final JCodeModel codeModel) {
        final XSOMParser reader = new XSOMParser((XMLParser)new XMLSchemaForestParser(this, forest, (GrammarLoader$1)null));
        reader.setAnnotationParser(new AnnotationParserFactoryImpl(codeModel, this.opt));
        reader.setErrorHandler(this.errorReceiver);
        return reader;
    }
    
    private AnnotatedGrammar loadRELAXNG() throws IOException, SAXException {
        final DOMForest forest = this.buildDOMForest(new RELAXNGInternalizationLogic());
        new CustomizationConverter(this.opt).fixup(forest);
        final XMLParser parser = (XMLParser)new GrammarLoader$1(this, forest);
        final SAXParserFactory parserFactory = new SAXParserFactoryAdaptor(parser);
        parserFactory.setNamespaceAware(true);
        final TRELAXNGReader reader = new TRELAXNGReader((ErrorReceiver)this.errorReceiver, this.opt.entityResolver, parserFactory, this.opt.defaultPackage);
        reader.parse(this.opt.getGrammars()[0]);
        return reader.getAnnotatedResult();
    }
    
    private class XMLSchemaForestParser implements XMLParser
    {
        private final XMLParser forestParser;
        
        private XMLSchemaForestParser(final GrammarLoader this$0, final DOMForest forest) {
            this.this$0 = this$0;
            this.forestParser = forest.createParser();
        }
        
        public void parse(final InputSource source, ContentHandler handler, final ErrorHandler errorHandler, final EntityResolver entityResolver) throws SAXException, IOException {
            handler = this.wrapBy((XMLFilterImpl)new ProhibitedFeaturesFilter((ErrorHandler)this.this$0.errorReceiver, this.this$0.opt.compatibilityMode == 1), handler);
            handler = this.wrapBy(new ExtensionBindingChecker("http://www.w3.org/2001/XMLSchema", (ErrorHandler)this.this$0.errorReceiver), handler);
            handler = this.wrapBy(new IncorrectNamespaceURIChecker(this.this$0.errorReceiver), handler);
            handler = this.wrapBy(new CustomizationContextChecker(this.this$0.errorReceiver), handler);
            this.forestParser.parse(source, handler, errorHandler, entityResolver);
        }
        
        private ContentHandler wrapBy(final XMLFilterImpl filter, final ContentHandler handler) {
            filter.setContentHandler(handler);
            return filter;
        }
    }
}
