// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

import org.kohsuke.rngom.parse.Parseable;
import org.xml.sax.ErrorHandler;
import org.kohsuke.rngom.ast.builder.BuildException;
import org.kohsuke.rngom.ast.om.ParsedPattern;
import org.kohsuke.rngom.ast.builder.SchemaBuilder;
import org.kohsuke.rngom.ast.util.CheckingSchemaBuilder;
import org.kohsuke.rngom.parse.compact.CompactParseable;
import org.kohsuke.rngom.parse.xml.SAXParseable;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class Main
{
    public static void main(final String[] args) throws Exception {
        final ErrorHandler eh = new DefaultHandler() {
            public void error(final SAXParseException e) throws SAXException {
                throw e;
            }
        };
        Parseable p;
        if (args[0].endsWith(".rng")) {
            p = new SAXParseable(new InputSource(args[0]), eh);
        }
        else {
            p = new CompactParseable(new InputSource(args[0]), eh);
        }
        final SchemaBuilder sb = new CheckingSchemaBuilder(new DSchemaBuilderImpl(), eh);
        try {
            p.parse((SchemaBuilder<?, ParsedPattern, ?, ?, ?, ?>)sb);
        }
        catch (BuildException e) {
            if (e.getCause() instanceof SAXException) {
                final SAXException se = (SAXException)e.getCause();
                if (se.getException() != null) {
                    se.getException().printStackTrace();
                }
            }
            throw e;
        }
    }
}
