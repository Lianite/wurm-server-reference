// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse.compact;

import org.kohsuke.rngom.xml.util.EncodingMap;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.net.URL;
import java.io.Reader;
import org.kohsuke.rngom.ast.builder.IncludedGrammar;
import org.kohsuke.rngom.parse.IllegalSchemaException;
import org.kohsuke.rngom.ast.builder.BuildException;
import org.kohsuke.rngom.ast.builder.Scope;
import org.kohsuke.rngom.ast.om.ParsedPattern;
import org.kohsuke.rngom.ast.builder.SchemaBuilder;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.kohsuke.rngom.parse.Parseable;

public class CompactParseable implements Parseable
{
    private final InputSource in;
    private final ErrorHandler eh;
    private static final String UTF8;
    private static final String UTF16;
    
    public CompactParseable(final InputSource in, final ErrorHandler eh) {
        this.in = in;
        this.eh = eh;
    }
    
    public ParsedPattern parse(final SchemaBuilder sb) throws BuildException, IllegalSchemaException {
        final ParsedPattern p = new CompactSyntax(this, makeReader(this.in), this.in.getSystemId(), sb, this.eh, "").parse(null);
        return sb.expandPattern(p);
    }
    
    public ParsedPattern parseInclude(final String uri, final SchemaBuilder sb, final IncludedGrammar g, final String inheritedNs) throws BuildException, IllegalSchemaException {
        final InputSource tem = new InputSource(uri);
        tem.setEncoding(this.in.getEncoding());
        return new CompactSyntax(this, makeReader(tem), uri, sb, this.eh, inheritedNs).parseInclude(g);
    }
    
    public ParsedPattern parseExternal(final String uri, final SchemaBuilder sb, final Scope scope, final String inheritedNs) throws BuildException, IllegalSchemaException {
        final InputSource tem = new InputSource(uri);
        tem.setEncoding(this.in.getEncoding());
        return new CompactSyntax(this, makeReader(tem), uri, sb, this.eh, inheritedNs).parse(scope);
    }
    
    private static Reader makeReader(final InputSource is) throws BuildException {
        try {
            Reader r = is.getCharacterStream();
            if (r == null) {
                InputStream in = is.getByteStream();
                if (in == null) {
                    final String systemId = is.getSystemId();
                    in = new URL(systemId).openStream();
                }
                String encoding = is.getEncoding();
                if (encoding == null) {
                    final PushbackInputStream pb = new PushbackInputStream(in, 2);
                    encoding = detectEncoding(pb);
                    in = pb;
                }
                r = new InputStreamReader(in, encoding);
            }
            return r;
        }
        catch (IOException e) {
            throw new BuildException(e);
        }
    }
    
    private static String detectEncoding(final PushbackInputStream in) throws IOException {
        String encoding = CompactParseable.UTF8;
        final int b1 = in.read();
        if (b1 != -1) {
            final int b2 = in.read();
            if (b2 != -1) {
                in.unread(b2);
                if ((b1 == 255 && b2 == 254) || (b1 == 254 && b2 == 255)) {
                    encoding = CompactParseable.UTF16;
                }
            }
            in.unread(b1);
        }
        return encoding;
    }
    
    static {
        UTF8 = EncodingMap.getJavaName("UTF-8");
        UTF16 = EncodingMap.getJavaName("UTF-16");
    }
}
