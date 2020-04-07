// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import org.xml.sax.SAXParseException;
import javax.xml.namespace.QName;
import java.util.StringTokenizer;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import com.sun.tools.xjc.reader.TypeUtil;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.Options;
import org.xml.sax.ErrorHandler;
import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.parser.NGCCRuntime;

public final class NGCCRuntimeEx extends NGCCRuntime
{
    public final JCodeModel codeModel;
    public final ErrorHandler errorHandler;
    public final Options options;
    public BindInfo currentBindInfo;
    static final String ERR_UNIMPLEMENTED = "NGCCRuntimeEx.Unimplemented";
    static final String ERR_UNSUPPORTED = "NGCCRuntimeEx.Unsupported";
    static final String ERR_UNDEFINED_PREFIX = "NGCCRuntimeEx.UndefinedPrefix";
    
    public NGCCRuntimeEx(final JCodeModel _codeModel, final Options opts, final ErrorHandler _errorHandler) {
        this.codeModel = _codeModel;
        this.options = opts;
        this.errorHandler = _errorHandler;
    }
    
    public final JType getType(final String typeName) throws SAXException {
        return TypeUtil.getType(this.codeModel, typeName, this.errorHandler, this.getLocator());
    }
    
    public final Locator copyLocator() {
        return new LocatorImpl(super.getLocator());
    }
    
    public final String truncateDocComment(final String s) {
        final StringBuffer buf = new StringBuffer(s.length());
        final StringTokenizer tokens = new StringTokenizer(s, "\n");
        while (tokens.hasMoreTokens()) {
            buf.append(tokens.nextToken().trim());
            if (tokens.hasMoreTokens()) {
                buf.append('\n');
            }
        }
        return buf.toString();
    }
    
    public final String escapeMarkup(final String s) {
        final StringBuffer buf = new StringBuffer(s.length());
        for (int i = 0; i < s.length(); ++i) {
            final char ch = s.charAt(i);
            switch (ch) {
                case '<': {
                    buf.append("&lt;");
                    break;
                }
                case '&': {
                    buf.append("&amp;");
                    break;
                }
                default: {
                    buf.append(ch);
                    break;
                }
            }
        }
        return buf.toString();
    }
    
    public final boolean parseBoolean(String str) {
        str = str.trim();
        return str.equals("true") || str.equals("1");
    }
    
    public final QName parseQName(final String str) throws SAXException {
        final int idx = str.indexOf(58);
        if (idx < 0) {
            final String uri = this.resolveNamespacePrefix("");
            return new QName(uri, str);
        }
        final String prefix = str.substring(0, idx);
        String uri2 = this.resolveNamespacePrefix(prefix);
        if (uri2 == null) {
            this.errorHandler.error(new SAXParseException(Messages.format("NGCCRuntimeEx.UndefinedPrefix", (Object)prefix), this.getLocator()));
            uri2 = "undefined";
        }
        return new QName(uri2, str.substring(idx + 1));
    }
    
    public void reportUnimplementedFeature(final String name) throws SAXException {
        this.errorHandler.warning(new SAXParseException(Messages.format("NGCCRuntimeEx.Unimplemented", (Object)name), this.getLocator()));
    }
    
    public void reportUnsupportedFeature(final String name) throws SAXException {
        this.errorHandler.warning(new SAXParseException(Messages.format("NGCCRuntimeEx.Unsupported", (Object)name), this.getLocator()));
    }
}
