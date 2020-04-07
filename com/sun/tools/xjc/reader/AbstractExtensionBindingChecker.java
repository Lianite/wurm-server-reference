// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader;

import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import com.sun.xml.bind.v2.util.EditDistance;
import java.util.Iterator;
import java.util.Collection;
import com.sun.tools.xjc.Plugin;
import java.util.HashSet;
import org.xml.sax.ErrorHandler;
import com.sun.tools.xjc.Options;
import org.xml.sax.Locator;
import java.util.Set;
import org.xml.sax.helpers.NamespaceSupport;
import com.sun.tools.xjc.util.SubtreeCutter;

public abstract class AbstractExtensionBindingChecker extends SubtreeCutter
{
    protected final NamespaceSupport nsSupport;
    protected final Set<String> enabledExtensions;
    private final Set<String> recognizableExtensions;
    private Locator locator;
    protected final String schemaLanguage;
    protected final boolean allowExtensions;
    private final Options options;
    
    public AbstractExtensionBindingChecker(final String schemaLanguage, final Options options, final ErrorHandler handler) {
        this.nsSupport = new NamespaceSupport();
        this.enabledExtensions = new HashSet<String>();
        this.recognizableExtensions = new HashSet<String>();
        this.schemaLanguage = schemaLanguage;
        this.allowExtensions = (options.compatibilityMode != 1);
        this.options = options;
        this.setErrorHandler(handler);
        for (final Plugin plugin : options.getAllPlugins()) {
            this.recognizableExtensions.addAll(plugin.getCustomizationURIs());
        }
        this.recognizableExtensions.add("http://java.sun.com/xml/ns/jaxb/xjc");
    }
    
    protected final void checkAndEnable(final String uri) throws SAXException {
        if (!this.isRecognizableExtension(uri)) {
            final String nearest = EditDistance.findNearest(uri, this.recognizableExtensions);
            this.error(Messages.ERR_UNSUPPORTED_EXTENSION.format(uri, nearest));
        }
        else if (!this.isSupportedExtension(uri)) {
            Plugin owner = null;
            for (final Plugin p : this.options.getAllPlugins()) {
                if (p.getCustomizationURIs().contains(uri)) {
                    owner = p;
                    break;
                }
            }
            if (owner != null) {
                this.error(Messages.ERR_PLUGIN_NOT_ENABLED.format(owner.getOptionName(), uri));
            }
            else {
                this.error(Messages.ERR_UNSUPPORTED_EXTENSION.format(uri));
            }
        }
        this.enabledExtensions.add(uri);
    }
    
    protected final void verifyTagName(final String namespaceURI, final String localName, final String qName) throws SAXException {
        if (this.options.pluginURIs.contains(namespaceURI)) {
            boolean correct = false;
            for (final Plugin p : this.options.activePlugins) {
                if (p.isCustomizationTagName(namespaceURI, localName)) {
                    correct = true;
                    break;
                }
            }
            if (!correct) {
                this.error(Messages.ERR_ILLEGAL_CUSTOMIZATION_TAGNAME.format(qName));
                this.startCutting();
            }
        }
    }
    
    protected final boolean isSupportedExtension(final String namespaceUri) {
        return namespaceUri.equals("http://java.sun.com/xml/ns/jaxb/xjc") || this.options.pluginURIs.contains(namespaceUri);
    }
    
    protected final boolean isRecognizableExtension(final String namespaceUri) {
        return this.recognizableExtensions.contains(namespaceUri);
    }
    
    public void setDocumentLocator(final Locator locator) {
        super.setDocumentLocator(locator);
        this.locator = locator;
    }
    
    public void startDocument() throws SAXException {
        super.startDocument();
        this.nsSupport.reset();
        this.enabledExtensions.clear();
    }
    
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        super.startPrefixMapping(prefix, uri);
        this.nsSupport.pushContext();
        this.nsSupport.declarePrefix(prefix, uri);
    }
    
    public void endPrefixMapping(final String prefix) throws SAXException {
        super.endPrefixMapping(prefix);
        this.nsSupport.popContext();
    }
    
    protected final SAXParseException error(final String msg) throws SAXException {
        final SAXParseException spe = new SAXParseException(msg, this.locator);
        this.getErrorHandler().error(spe);
        return spe;
    }
    
    protected final void warning(final String msg) throws SAXException {
        final SAXParseException spe = new SAXParseException(msg, this.locator);
        this.getErrorHandler().warning(spe);
    }
}
