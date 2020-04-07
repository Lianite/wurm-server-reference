// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc;

import org.xml.sax.Attributes;
import com.sun.tools.xjc.reader.xmlschema.parser.CustomizationContextChecker;
import com.sun.tools.xjc.reader.xmlschema.parser.IncorrectNamespaceURIChecker;
import org.kohsuke.rngom.parse.IllegalSchemaException;
import com.sun.tools.xjc.reader.relaxng.RELAXNGCompiler;
import org.kohsuke.rngom.digested.DPattern;
import org.kohsuke.rngom.ast.builder.SchemaBuilder;
import org.kohsuke.rngom.ast.util.CheckingSchemaBuilder;
import org.kohsuke.rngom.digested.DSchemaBuilderImpl;
import org.kohsuke.rngom.parse.compact.CompactParseable;
import org.kohsuke.rngom.parse.Parseable;
import org.kohsuke.rngom.parse.xml.SAXParseable;
import org.xml.sax.XMLFilter;
import com.sun.tools.xjc.reader.ExtensionBindingChecker;
import org.xml.sax.XMLReader;
import org.kohsuke.rngom.xml.sax.XMLReaderCreator;
import com.sun.tools.xjc.reader.relaxng.RELAXNGInternalizationLogic;
import java.util.Iterator;
import com.sun.tools.xjc.reader.internalizer.VersionChecker;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.ContentHandler;
import com.sun.xml.xsom.parser.JAXPParser;
import java.io.IOException;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import com.sun.xml.xsom.parser.AnnotationParserFactory;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.AnnotationParserFactoryImpl;
import com.sun.xml.xsom.parser.XMLParser;
import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;
import org.w3c.dom.NodeList;
import com.sun.xml.xsom.parser.XSOMParser;
import com.sun.tools.xjc.reader.internalizer.DOMForestScanner;
import com.sun.tools.xjc.reader.xmlschema.parser.XMLSchemaInternalizationLogic;
import com.sun.tools.xjc.reader.xmlschema.parser.SchemaConstraintChecker;
import com.sun.xml.xsom.XSSchemaSet;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.xml.sax.SAXParseException;
import com.sun.tools.xjc.reader.internalizer.DOMForest;
import com.sun.tools.xjc.reader.internalizer.InternalizationLogic;
import com.sun.tools.xjc.reader.dtd.TDTDReader;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import com.sun.tools.xjc.api.ErrorListener;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.reader.internalizer.SCDBasedBindingSet;
import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.util.ErrorReceiverFilter;

public final class ModelLoader
{
    private final Options opt;
    private final ErrorReceiverFilter errorReceiver;
    private final JCodeModel codeModel;
    private SCDBasedBindingSet scdBasedBindingSet;
    
    public static Model load(final Options opt, final JCodeModel codeModel, final ErrorReceiver er) {
        return new ModelLoader(opt, codeModel, er).load();
    }
    
    public ModelLoader(final Options _opt, final JCodeModel _codeModel, final ErrorReceiver er) {
        this.opt = _opt;
        this.codeModel = _codeModel;
        this.errorReceiver = new ErrorReceiverFilter(er);
    }
    
    private Model load() {
        if (!this.sanityCheck()) {
            return null;
        }
        try {
            Model grammar = null;
            switch (this.opt.getSchemaLanguage()) {
                case DTD: {
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
                case RELAXNG: {
                    this.checkTooManySchemaErrors();
                    grammar = this.loadRELAXNG();
                    break;
                }
                case RELAXNG_COMPACT: {
                    this.checkTooManySchemaErrors();
                    grammar = this.loadRELAXNGCompact();
                    break;
                }
                case WSDL: {
                    grammar = this.annotateXMLSchema(this.loadWSDL());
                    break;
                }
                case XMLSCHEMA: {
                    grammar = this.annotateXMLSchema(this.loadXMLSchema());
                    break;
                }
                default: {
                    throw new AssertionError();
                }
            }
            if (this.errorReceiver.hadError()) {
                grammar = null;
            }
            else {
                grammar.setPackageLevelAnnotations(this.opt.packageLevelAnnotations);
            }
            return grammar;
        }
        catch (SAXException e) {
            if (this.opt.verbose) {
                if (e.getException() != null) {
                    e.getException().printStackTrace();
                }
                else {
                    e.printStackTrace();
                }
            }
            return null;
        }
        catch (AbortException e2) {
            return null;
        }
    }
    
    private boolean sanityCheck() {
        if (this.opt.getSchemaLanguage() == Language.XMLSCHEMA) {
            final Language guess = this.opt.guessSchemaLanguage();
            String[] msg = null;
            switch (guess) {
                case DTD: {
                    msg = new String[] { "DTD", "-dtd" };
                    break;
                }
                case RELAXNG: {
                    msg = new String[] { "RELAX NG", "-relaxng" };
                    break;
                }
                case RELAXNG_COMPACT: {
                    msg = new String[] { "RELAX NG compact syntax", "-relaxng-compact" };
                    break;
                }
                case WSDL: {
                    msg = new String[] { "WSDL", "-wsdl" };
                    break;
                }
            }
            if (msg != null) {
                this.errorReceiver.warning(null, Messages.format("Driver.ExperimentalLanguageWarning", msg[0], msg[1]));
            }
        }
        return true;
    }
    
    private void checkTooManySchemaErrors() {
        if (this.opt.getGrammars().length != 1) {
            this.errorReceiver.error(null, Messages.format("ModelLoader.TooManySchema", new Object[0]));
        }
    }
    
    private Model loadDTD(final InputSource source, final InputSource bindFile) {
        return TDTDReader.parse(source, bindFile, this.errorReceiver, this.opt);
    }
    
    public DOMForest buildDOMForest(final InternalizationLogic logic) throws SAXException {
        final DOMForest forest = new DOMForest(logic);
        forest.setErrorHandler(this.errorReceiver);
        if (this.opt.entityResolver != null) {
            forest.setEntityResolver(this.opt.entityResolver);
        }
        for (final InputSource value : this.opt.getGrammars()) {
            this.errorReceiver.pollAbort();
            forest.parse(value, true);
        }
        for (final InputSource value : this.opt.getBindFiles()) {
            this.errorReceiver.pollAbort();
            final Document dom = forest.parse(value, true);
            if (dom != null) {
                final Element root = dom.getDocumentElement();
                if (!this.fixNull(root.getNamespaceURI()).equals("http://java.sun.com/xml/ns/jaxb") || !root.getLocalName().equals("bindings")) {
                    this.errorReceiver.error(new SAXParseException(Messages.format("Driver.NotABindingFile", root.getNamespaceURI(), root.getLocalName()), null, value.getSystemId(), -1, -1));
                }
            }
        }
        this.scdBasedBindingSet = forest.transform(this.opt.isExtensionMode());
        return forest;
    }
    
    private String fixNull(final String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
    
    public XSSchemaSet loadXMLSchema() throws SAXException {
        if (this.opt.strictCheck && !SchemaConstraintChecker.check(this.opt.getGrammars(), this.errorReceiver, this.opt.entityResolver)) {
            return null;
        }
        if (this.opt.getBindFiles().length == 0) {
            try {
                return this.createXSOMSpeculative();
            }
            catch (SpeculationFailure speculationFailure) {}
        }
        final DOMForest forest = this.buildDOMForest(new XMLSchemaInternalizationLogic());
        return this.createXSOM(forest, this.scdBasedBindingSet);
    }
    
    private XSSchemaSet loadWSDL() throws SAXException {
        final DOMForest forest = this.buildDOMForest(new XMLSchemaInternalizationLogic());
        final DOMForestScanner scanner = new DOMForestScanner(forest);
        final XSOMParser xsomParser = this.createXSOMParser(forest);
        for (final InputSource grammar : this.opt.getGrammars()) {
            final Document wsdlDom = forest.get(grammar.getSystemId());
            final NodeList schemas = wsdlDom.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "schema");
            for (int i = 0; i < schemas.getLength(); ++i) {
                scanner.scan((Element)schemas.item(i), xsomParser.getParserHandler());
            }
        }
        return xsomParser.getResult();
    }
    
    public Model annotateXMLSchema(final XSSchemaSet xs) {
        if (xs == null) {
            return null;
        }
        return BGMBuilder.build(xs, this.codeModel, this.errorReceiver, this.opt);
    }
    
    public XSOMParser createXSOMParser(final XMLParser parser) {
        final XSOMParser reader = new XSOMParser(new XMLSchemaParser(parser));
        reader.setAnnotationParser(new AnnotationParserFactoryImpl(this.opt));
        reader.setErrorHandler(this.errorReceiver);
        reader.setEntityResolver(this.opt.entityResolver);
        return reader;
    }
    
    public XSOMParser createXSOMParser(final DOMForest forest) {
        final XSOMParser p = this.createXSOMParser(forest.createParser());
        p.setEntityResolver(new EntityResolver() {
            public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
                if (systemId != null && forest.get(systemId) != null) {
                    return new InputSource(systemId);
                }
                if (ModelLoader.this.opt.entityResolver != null) {
                    return ModelLoader.this.opt.entityResolver.resolveEntity(publicId, systemId);
                }
                return null;
            }
        });
        return p;
    }
    
    private XSSchemaSet createXSOMSpeculative() throws SAXException, SpeculationFailure {
        final XMLParser parser = new XMLParser() {
            private final JAXPParser base = new JAXPParser();
            
            public void parse(final InputSource source, ContentHandler handler, final ErrorHandler errorHandler, final EntityResolver entityResolver) throws SAXException, IOException {
                handler = this.wrapBy(new SpeculationChecker(), handler);
                handler = this.wrapBy(new VersionChecker(null, ModelLoader.this.errorReceiver, entityResolver), handler);
                this.base.parse(source, handler, errorHandler, entityResolver);
            }
            
            private ContentHandler wrapBy(final XMLFilterImpl filter, final ContentHandler handler) {
                filter.setContentHandler(handler);
                return filter;
            }
        };
        final XSOMParser reader = this.createXSOMParser(parser);
        for (final InputSource value : this.opt.getGrammars()) {
            reader.parse(value);
        }
        return reader.getResult();
    }
    
    public XSSchemaSet createXSOM(final DOMForest forest, final SCDBasedBindingSet scdBasedBindingSet) throws SAXException {
        final XSOMParser reader = this.createXSOMParser(forest);
        for (final String systemId : forest.getRootDocuments()) {
            this.errorReceiver.pollAbort();
            final Document dom = forest.get(systemId);
            if (!dom.getDocumentElement().getNamespaceURI().equals("http://java.sun.com/xml/ns/jaxb")) {
                reader.parse(systemId);
            }
        }
        final XSSchemaSet result = reader.getResult();
        if (result != null) {
            scdBasedBindingSet.apply(result, this.errorReceiver);
        }
        return result;
    }
    
    private Model loadRELAXNG() throws SAXException {
        final DOMForest forest = this.buildDOMForest(new RELAXNGInternalizationLogic());
        final XMLReaderCreator xrc = new XMLReaderCreator() {
            public XMLReader createXMLReader() {
                final XMLFilter buffer = new XMLFilterImpl() {
                    public void parse(final InputSource source) throws IOException, SAXException {
                        forest.createParser().parse(source, this, this, this);
                    }
                };
                final XMLFilter f = new ExtensionBindingChecker("http://relaxng.org/ns/structure/1.0", ModelLoader.this.opt, ModelLoader.this.errorReceiver);
                f.setParent(buffer);
                f.setEntityResolver(ModelLoader.this.opt.entityResolver);
                return f;
            }
        };
        final Parseable p = new SAXParseable(this.opt.getGrammars()[0], this.errorReceiver, xrc);
        return this.loadRELAXNG(p);
    }
    
    private Model loadRELAXNGCompact() {
        if (this.opt.getBindFiles().length > 0) {
            this.errorReceiver.error(new SAXParseException(Messages.format("ModelLoader.BindingFileNotSupportedForRNC", new Object[0]), (Locator)null));
        }
        final Parseable p = new CompactParseable(this.opt.getGrammars()[0], this.errorReceiver);
        return this.loadRELAXNG(p);
    }
    
    private Model loadRELAXNG(final Parseable p) {
        final SchemaBuilder sb = new CheckingSchemaBuilder(new DSchemaBuilderImpl(), this.errorReceiver);
        try {
            final DPattern out = p.parse((SchemaBuilder<?, DPattern, ?, ?, ?, ?>)sb);
            return RELAXNGCompiler.build(out, this.codeModel, this.opt);
        }
        catch (IllegalSchemaException e) {
            this.errorReceiver.error(e.getMessage(), e);
            return null;
        }
    }
    
    private class XMLSchemaParser implements XMLParser
    {
        private final XMLParser baseParser;
        
        private XMLSchemaParser(final XMLParser baseParser) {
            this.baseParser = baseParser;
        }
        
        public void parse(final InputSource source, ContentHandler handler, final ErrorHandler errorHandler, final EntityResolver entityResolver) throws SAXException, IOException {
            handler = this.wrapBy(new ExtensionBindingChecker("http://www.w3.org/2001/XMLSchema", ModelLoader.this.opt, ModelLoader.this.errorReceiver), handler);
            handler = this.wrapBy(new IncorrectNamespaceURIChecker(ModelLoader.this.errorReceiver), handler);
            handler = this.wrapBy(new CustomizationContextChecker(ModelLoader.this.errorReceiver), handler);
            this.baseParser.parse(source, handler, errorHandler, entityResolver);
        }
        
        private ContentHandler wrapBy(final XMLFilterImpl filter, final ContentHandler handler) {
            filter.setContentHandler(handler);
            return filter;
        }
    }
    
    private static final class SpeculationFailure extends Error
    {
    }
    
    private static final class SpeculationChecker extends XMLFilterImpl
    {
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
            if (localName.equals("bindings") && uri.equals("http://java.sun.com/xml/ns/jaxb")) {
                throw new SpeculationFailure();
            }
            super.startElement(uri, localName, qName, attributes);
        }
    }
}
