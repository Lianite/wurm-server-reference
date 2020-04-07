// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse.xml;

import org.xml.sax.EntityResolver;
import org.kohsuke.rngom.parse.IllegalSchemaException;
import org.xml.sax.XMLReader;
import java.io.IOException;
import org.kohsuke.rngom.ast.builder.BuildException;
import org.xml.sax.SAXException;
import org.kohsuke.rngom.ast.builder.Scope;
import org.kohsuke.rngom.ast.builder.IncludedGrammar;
import org.kohsuke.rngom.ast.om.ParsedPattern;
import org.kohsuke.rngom.ast.builder.SchemaBuilder;
import org.kohsuke.rngom.xml.sax.JAXPXMLReaderCreator;
import org.xml.sax.ErrorHandler;
import org.kohsuke.rngom.xml.sax.XMLReaderCreator;
import org.xml.sax.InputSource;
import org.kohsuke.rngom.parse.Parseable;

public class SAXParseable implements Parseable
{
    private final InputSource in;
    final XMLReaderCreator xrc;
    final ErrorHandler eh;
    
    public SAXParseable(final InputSource in, final ErrorHandler eh, final XMLReaderCreator xrc) {
        this.xrc = xrc;
        this.eh = eh;
        this.in = in;
    }
    
    public SAXParseable(final InputSource in, final ErrorHandler eh) {
        this(in, eh, new JAXPXMLReaderCreator());
    }
    
    public ParsedPattern parse(final SchemaBuilder schemaBuilder) throws BuildException, IllegalSchemaException {
        try {
            final XMLReader xr = this.xrc.createXMLReader();
            final SchemaParser sp = new SchemaParser(this, xr, this.eh, schemaBuilder, null, null, "");
            xr.parse(this.in);
            final ParsedPattern p = sp.getParsedPattern();
            return schemaBuilder.expandPattern(p);
        }
        catch (SAXException e) {
            throw toBuildException(e);
        }
        catch (IOException e2) {
            throw new BuildException(e2);
        }
    }
    
    public ParsedPattern parseInclude(final String uri, final SchemaBuilder schemaBuilder, final IncludedGrammar g, final String inheritedNs) throws BuildException, IllegalSchemaException {
        try {
            final XMLReader xr = this.xrc.createXMLReader();
            final SchemaParser sp = new SchemaParser(this, xr, this.eh, schemaBuilder, g, g, inheritedNs);
            xr.parse(makeInputSource(xr, uri));
            return sp.getParsedPattern();
        }
        catch (SAXException e) {
            throw toBuildException(e);
        }
        catch (IOException e2) {
            throw new BuildException(e2);
        }
    }
    
    public ParsedPattern parseExternal(final String uri, final SchemaBuilder schemaBuilder, final Scope s, final String inheritedNs) throws BuildException, IllegalSchemaException {
        try {
            final XMLReader xr = this.xrc.createXMLReader();
            final SchemaParser sp = new SchemaParser(this, xr, this.eh, schemaBuilder, null, s, inheritedNs);
            xr.parse(makeInputSource(xr, uri));
            return sp.getParsedPattern();
        }
        catch (SAXException e) {
            throw toBuildException(e);
        }
        catch (IOException e2) {
            throw new BuildException(e2);
        }
    }
    
    private static InputSource makeInputSource(final XMLReader xr, final String systemId) throws IOException, SAXException {
        final EntityResolver er = xr.getEntityResolver();
        if (er != null) {
            final InputSource inputSource = er.resolveEntity(null, systemId);
            if (inputSource != null) {
                return inputSource;
            }
        }
        return new InputSource(systemId);
    }
    
    static BuildException toBuildException(final SAXException e) {
        final Exception inner = e.getException();
        if (inner instanceof BuildException) {
            throw (BuildException)inner;
        }
        throw new BuildException(e);
    }
}
