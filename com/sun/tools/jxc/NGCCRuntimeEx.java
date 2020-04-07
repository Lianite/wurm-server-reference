// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.jxc;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.List;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import java.io.File;
import org.xml.sax.ErrorHandler;
import com.sun.tools.jxc.gen.config.NGCCRuntime;

public final class NGCCRuntimeEx extends NGCCRuntime
{
    private final ErrorHandler errorHandler;
    
    public NGCCRuntimeEx(final ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
    
    public File getBaseDir(final String baseDir) throws SAXException {
        final File dir = new File(baseDir);
        if (dir.exists()) {
            return dir;
        }
        final SAXParseException e = new SAXParseException(Messages.BASEDIR_DOESNT_EXIST.format(dir.getAbsolutePath()), this.getLocator());
        this.errorHandler.error(e);
        throw e;
    }
    
    public List<Pattern> getIncludePatterns(final List includeContent) {
        final List<Pattern> includeRegexList = new ArrayList<Pattern>();
        for (int i = 0; i < includeContent.size(); ++i) {
            final String includes = includeContent.get(i);
            final String regex = this.convertToRegex(includes);
            final Pattern pattern = Pattern.compile(regex);
            includeRegexList.add(pattern);
        }
        return includeRegexList;
    }
    
    public List getExcludePatterns(final List excludeContent) {
        final List excludeRegexList = new ArrayList();
        for (int i = 0; i < excludeContent.size(); ++i) {
            final String excludes = excludeContent.get(i);
            final String regex = this.convertToRegex(excludes);
            final Pattern pattern = Pattern.compile(regex);
            excludeRegexList.add(pattern);
        }
        return excludeRegexList;
    }
    
    private String convertToRegex(final String pattern) {
        final StringBuilder regex = new StringBuilder();
        char nc = ' ';
        if (pattern.length() > 0) {
            for (int i = 0; i < pattern.length(); ++i) {
                final char c = pattern.charAt(i);
                final int j = i;
                nc = ' ';
                if (j + 1 != pattern.length()) {
                    nc = pattern.charAt(j + 1);
                }
                if (c == '.' && nc != '.') {
                    regex.append('\\');
                    regex.append('.');
                }
                else if (c != '.' || nc != '.') {
                    if (c == '*' && nc == '*') {
                        regex.append(".*");
                        break;
                    }
                    if (c == '*') {
                        regex.append("[^\\.]+");
                    }
                    else if (c == '?') {
                        regex.append("[^\\.]");
                    }
                    else {
                        regex.append(c);
                    }
                }
            }
        }
        return regex.toString();
    }
    
    protected void unexpectedX(final String token) throws SAXException {
        this.errorHandler.error(new SAXParseException(Messages.UNEXPECTED_NGCC_TOKEN.format(token, this.getLocator().getLineNumber(), this.getLocator().getColumnNumber()), this.getLocator()));
    }
}
