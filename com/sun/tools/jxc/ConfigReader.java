// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.jxc;

import java.util.HashMap;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Result;
import java.util.Map;
import javax.xml.validation.ValidatorHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import com.sun.tools.jxc.gen.config.NGCCHandler;
import org.xml.sax.ContentHandler;
import com.sun.tools.xjc.util.ForkContentHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.util.regex.Matcher;
import com.sun.tools.jxc.gen.config.Schema;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.io.IOException;
import org.xml.sax.SAXException;
import com.sun.tools.jxc.gen.config.Config;
import java.util.HashSet;
import org.xml.sax.ErrorHandler;
import java.io.File;
import com.sun.mirror.declaration.TypeDeclaration;
import java.util.Collection;
import com.sun.tools.xjc.SchemaCache;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import javax.xml.bind.SchemaOutputResolver;
import com.sun.tools.xjc.api.Reference;
import java.util.Set;

public final class ConfigReader
{
    private final Set<Reference> classesToBeIncluded;
    private final SchemaOutputResolver schemaOutputResolver;
    private final AnnotationProcessorEnvironment env;
    private static SchemaCache configSchema;
    
    public ConfigReader(final AnnotationProcessorEnvironment env, final Collection<? extends TypeDeclaration> classes, final File xmlFile, final ErrorHandler errorHandler) throws SAXException, IOException {
        this.classesToBeIncluded = new HashSet<Reference>();
        this.env = env;
        final Config config = this.parseAndGetConfig(xmlFile, errorHandler);
        this.checkAllClasses(config, classes);
        final String path = xmlFile.getAbsolutePath();
        final String xmlPath = path.substring(0, path.lastIndexOf(File.separatorChar));
        this.schemaOutputResolver = this.createSchemaOutputResolver(config, xmlPath);
    }
    
    public Collection<Reference> getClassesToBeIncluded() {
        return this.classesToBeIncluded;
    }
    
    private void checkAllClasses(final Config config, final Collection<? extends TypeDeclaration> rootClasses) {
        final List<Pattern> includeRegexList = (List<Pattern>)config.getClasses().getIncludes();
        final List<Pattern> excludeRegexList = (List<Pattern>)config.getClasses().getExcludes();
    Label_0025:
        for (final TypeDeclaration typeDecl : rootClasses) {
            final String qualifiedName = typeDecl.getQualifiedName();
            for (final Pattern pattern : excludeRegexList) {
                final boolean match = this.checkPatternMatch(qualifiedName, pattern);
                if (match) {
                    continue Label_0025;
                }
            }
            for (final Pattern pattern : includeRegexList) {
                final boolean match = this.checkPatternMatch(qualifiedName, pattern);
                if (match) {
                    this.classesToBeIncluded.add(new Reference(typeDecl, this.env));
                    break;
                }
            }
        }
    }
    
    public SchemaOutputResolver getSchemaOutputResolver() {
        return this.schemaOutputResolver;
    }
    
    private SchemaOutputResolver createSchemaOutputResolver(final Config config, final String xmlpath) {
        final File baseDir = new File(xmlpath, config.getBaseDir().getPath());
        final SchemaOutputResolverImpl schemaOutputResolver = new SchemaOutputResolverImpl(baseDir);
        for (final Schema schema : config.getSchema()) {
            final String namespace = schema.getNamespace();
            final File location = schema.getLocation();
            schemaOutputResolver.addSchemaInfo(namespace, location);
        }
        return schemaOutputResolver;
    }
    
    private boolean checkPatternMatch(final String qualifiedName, final Pattern pattern) {
        final Matcher matcher = pattern.matcher(qualifiedName);
        return matcher.matches();
    }
    
    private Config parseAndGetConfig(final File xmlFile, final ErrorHandler errorHandler) throws SAXException, IOException {
        XMLReader reader;
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            reader = factory.newSAXParser().getXMLReader();
        }
        catch (ParserConfigurationException e) {
            throw new Error(e);
        }
        final NGCCRuntimeEx runtime = new NGCCRuntimeEx(errorHandler);
        final ValidatorHandler validator = ConfigReader.configSchema.newValidator();
        validator.setErrorHandler(errorHandler);
        reader.setContentHandler(new ForkContentHandler(validator, runtime));
        reader.setErrorHandler(errorHandler);
        final Config config = new Config(runtime);
        runtime.setRootHandler(config);
        reader.parse(new InputSource(xmlFile.toURL().toExternalForm()));
        runtime.reset();
        return config;
    }
    
    static {
        ConfigReader.configSchema = new SchemaCache(Config.class.getResource("config.xsd"));
    }
    
    private static final class SchemaOutputResolverImpl extends SchemaOutputResolver
    {
        private final File baseDir;
        private final Map<String, File> schemas;
        
        public Result createOutput(final String namespaceUri, final String suggestedFileName) {
            if (!this.schemas.containsKey(namespaceUri)) {
                final File schemaFile = new File(this.baseDir, suggestedFileName);
                return new StreamResult(schemaFile);
            }
            final File loc = this.schemas.get(namespaceUri);
            if (loc == null) {
                return null;
            }
            loc.getParentFile().mkdirs();
            return new StreamResult(loc);
        }
        
        public SchemaOutputResolverImpl(final File baseDir) {
            this.schemas = new HashMap<String, File>();
            assert baseDir != null;
            this.baseDir = baseDir;
        }
        
        public void addSchemaInfo(String namespaceUri, final File location) {
            if (namespaceUri == null) {
                namespaceUri = "";
            }
            this.schemas.put(namespaceUri, location);
        }
    }
}
