// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api.impl.s2j;

import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.model.Model;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.tools.xjc.reader.internalizer.SCDBasedBindingSet;
import com.sun.tools.xjc.ModelLoader;
import com.sun.codemodel.JCodeModel;
import org.xml.sax.ErrorHandler;
import javax.xml.validation.SchemaFactory;
import com.sun.tools.xjc.reader.internalizer.InternalizationLogic;
import com.sun.tools.xjc.reader.xmlschema.parser.XMLSchemaInternalizationLogic;
import com.sun.tools.xjc.api.ClassNameAllocator;
import org.xml.sax.EntityResolver;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URI;
import java.net.URL;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import com.sun.tools.xjc.api.SpecVersion;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import com.sun.istack.SAXParseException2;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;
import com.sun.xml.bind.unmarshaller.DOMScanner;
import org.w3c.dom.Element;
import org.xml.sax.ContentHandler;
import com.sun.istack.NotNull;
import com.sun.tools.xjc.reader.internalizer.DOMForest;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.api.ErrorListener;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.ErrorReceiver;

public final class SchemaCompilerImpl extends ErrorReceiver implements SchemaCompiler
{
    private ErrorListener errorListener;
    protected final Options opts;
    @NotNull
    protected DOMForest forest;
    private boolean hadError;
    private static boolean NO_CORRECTNESS_CHECK;
    
    public SchemaCompilerImpl() {
        this.opts = new Options();
        this.opts.compatibilityMode = 2;
        this.resetSchema();
        if (System.getProperty("xjc-api.test") != null) {
            this.opts.debugMode = true;
            this.opts.verbose = true;
        }
    }
    
    @NotNull
    public Options getOptions() {
        return this.opts;
    }
    
    public ContentHandler getParserHandler(final String systemId) {
        return this.forest.getParserHandler(systemId, true);
    }
    
    public void parseSchema(final String systemId, final Element element) {
        this.checkAbsoluteness(systemId);
        try {
            final DOMScanner scanner = new DOMScanner();
            final LocatorImpl loc = new LocatorImpl();
            loc.setSystemId(systemId);
            scanner.setLocator(loc);
            scanner.setContentHandler(this.getParserHandler(systemId));
            scanner.scan(element);
        }
        catch (SAXException e) {
            this.fatalError(new SAXParseException2(e.getMessage(), null, systemId, -1, -1, e));
        }
    }
    
    public void parseSchema(final InputSource source) {
        this.checkAbsoluteness(source.getSystemId());
        try {
            this.forest.parse(source, true);
        }
        catch (SAXException e) {
            e.printStackTrace();
        }
    }
    
    public void setTargetVersion(SpecVersion version) {
        if (version == null) {
            version = SpecVersion.LATEST;
        }
        this.opts.target = version;
    }
    
    public void parseSchema(final String systemId, final XMLStreamReader reader) throws XMLStreamException {
        this.checkAbsoluteness(systemId);
        this.forest.parse(systemId, reader, true);
    }
    
    private void checkAbsoluteness(final String systemId) {
        try {
            new URL(systemId);
        }
        catch (MalformedURLException _) {
            try {
                new URI(systemId);
            }
            catch (URISyntaxException e) {
                throw new IllegalArgumentException("system ID '" + systemId + "' isn't absolute", e);
            }
        }
    }
    
    public void setEntityResolver(final EntityResolver entityResolver) {
        this.forest.setEntityResolver(entityResolver);
        this.opts.entityResolver = entityResolver;
    }
    
    public void setDefaultPackageName(final String packageName) {
        this.opts.defaultPackage2 = packageName;
    }
    
    public void forcePackageName(final String packageName) {
        this.opts.defaultPackage = packageName;
    }
    
    public void setClassNameAllocator(final ClassNameAllocator allocator) {
        this.opts.classNameAllocator = allocator;
    }
    
    public void resetSchema() {
        (this.forest = new DOMForest(new XMLSchemaInternalizationLogic())).setErrorHandler(this);
        this.forest.setEntityResolver(this.opts.entityResolver);
    }
    
    public JAXBModelImpl bind() {
        final SCDBasedBindingSet scdBasedBindingSet = this.forest.transform(this.opts.isExtensionMode());
        if (!SchemaCompilerImpl.NO_CORRECTNESS_CHECK) {
            final SchemaFactory sf = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            sf.setErrorHandler(new DowngradingErrorHandler(this));
            this.forest.weakSchemaCorrectnessCheck(sf);
            if (this.hadError) {
                return null;
            }
        }
        final JCodeModel codeModel = new JCodeModel();
        final ModelLoader gl = new ModelLoader(this.opts, codeModel, this);
        try {
            final XSSchemaSet result = gl.createXSOM(this.forest, scdBasedBindingSet);
            if (result == null) {
                return null;
            }
            final Model model = gl.annotateXMLSchema(result);
            if (model == null) {
                return null;
            }
            if (this.hadError) {
                return null;
            }
            final Outline context = model.generateCode(this.opts, this);
            if (context == null) {
                return null;
            }
            if (this.hadError) {
                return null;
            }
            return new JAXBModelImpl(context);
        }
        catch (SAXException e) {
            return null;
        }
    }
    
    public void setErrorListener(final ErrorListener errorListener) {
        this.errorListener = errorListener;
    }
    
    public void info(final SAXParseException exception) {
        if (this.errorListener != null) {
            this.errorListener.info(exception);
        }
    }
    
    public void warning(final SAXParseException exception) {
        if (this.errorListener != null) {
            this.errorListener.warning(exception);
        }
    }
    
    public void error(final SAXParseException exception) {
        this.hadError = true;
        if (this.errorListener != null) {
            this.errorListener.error(exception);
        }
    }
    
    public void fatalError(final SAXParseException exception) {
        this.hadError = true;
        if (this.errorListener != null) {
            this.errorListener.fatalError(exception);
        }
    }
    
    static {
        SchemaCompilerImpl.NO_CORRECTNESS_CHECK = false;
        try {
            SchemaCompilerImpl.NO_CORRECTNESS_CHECK = Boolean.getBoolean(SchemaCompilerImpl.class + ".noCorrectnessCheck");
        }
        catch (Throwable t) {}
    }
}
