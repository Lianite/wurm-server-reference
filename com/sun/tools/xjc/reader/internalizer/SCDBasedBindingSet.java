// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.internalizer;

import com.sun.xml.xsom.XSAnnotation;
import java.util.Iterator;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIDeclaration;
import com.sun.tools.xjc.util.DOMUtils;
import com.sun.xml.xsom.XSComponent;
import java.util.ArrayList;
import java.util.List;
import com.sun.istack.NotNull;
import org.xml.sax.SAXParseException;
import com.sun.istack.SAXParseException2;
import javax.xml.validation.ValidatorHandler;
import com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import java.util.Collection;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BindInfo;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.SCD;
import org.w3c.dom.Element;
import com.sun.tools.xjc.util.ForkContentHandler;
import javax.xml.bind.UnmarshallerHandler;
import com.sun.tools.xjc.ErrorReceiver;

public final class SCDBasedBindingSet
{
    private Target topLevel;
    private final DOMForest forest;
    private ErrorReceiver errorReceiver;
    private UnmarshallerHandler unmarshaller;
    private ForkContentHandler loader;
    
    SCDBasedBindingSet(final DOMForest forest) {
        this.forest = forest;
    }
    
    Target createNewTarget(final Target parent, final Element src, final SCD scd) {
        return new Target(parent, src, scd);
    }
    
    public void apply(final XSSchemaSet schema, final ErrorReceiver errorReceiver) {
        if (this.topLevel != null) {
            this.errorReceiver = errorReceiver;
            final UnmarshallerImpl u = BindInfo.getJAXBContext().createUnmarshaller();
            this.unmarshaller = u.getUnmarshallerHandler();
            final ValidatorHandler v = BindInfo.bindingFileSchema.newValidator();
            v.setErrorHandler(errorReceiver);
            this.loader = new ForkContentHandler(v, this.unmarshaller);
            this.topLevel.applyAll(schema.getSchemas());
            this.loader = null;
            this.unmarshaller = null;
            this.errorReceiver = null;
        }
    }
    
    private void reportError(final Element errorSource, final String formattedMsg) {
        this.reportError(errorSource, formattedMsg, null);
    }
    
    private void reportError(final Element errorSource, final String formattedMsg, final Exception nestedException) {
        final SAXParseException e = new SAXParseException2(formattedMsg, this.forest.locatorTable.getStartLocation(errorSource), nestedException);
        this.errorReceiver.error(e);
    }
    
    final class Target
    {
        private Target firstChild;
        private final Target nextSibling;
        @NotNull
        private final SCD scd;
        @NotNull
        private final Element src;
        private final List<Element> bindings;
        
        private Target(final Target parent, final Element src, final SCD scd) {
            this.bindings = new ArrayList<Element>();
            if (parent == null) {
                this.nextSibling = SCDBasedBindingSet.this.topLevel;
                SCDBasedBindingSet.this.topLevel = this;
            }
            else {
                this.nextSibling = parent.firstChild;
                parent.firstChild = this;
            }
            this.src = src;
            this.scd = scd;
        }
        
        void addBinidng(final Element binding) {
            this.bindings.add(binding);
        }
        
        private void applyAll(final Collection<? extends XSComponent> contextNode) {
            for (Target self = this; self != null; self = self.nextSibling) {
                self.apply(contextNode);
            }
        }
        
        private void apply(final Collection<? extends XSComponent> contextNode) {
            final Collection<XSComponent> childNodes = this.scd.select(contextNode);
            if (!childNodes.isEmpty()) {
                if (this.firstChild != null) {
                    this.firstChild.applyAll(childNodes);
                }
                if (!this.bindings.isEmpty()) {
                    final Iterator<XSComponent> itr = childNodes.iterator();
                    final XSComponent target = itr.next();
                    if (itr.hasNext()) {
                        SCDBasedBindingSet.this.reportError(this.src, Messages.format("ERR_SCD_MATCHED_MULTIPLE_NODES", this.scd, childNodes.size()));
                        SCDBasedBindingSet.this.errorReceiver.error(target.getLocator(), Messages.format("ERR_SCD_MATCHED_MULTIPLE_NODES_FIRST", new Object[0]));
                        SCDBasedBindingSet.this.errorReceiver.error(itr.next().getLocator(), Messages.format("ERR_SCD_MATCHED_MULTIPLE_NODES_SECOND", new Object[0]));
                    }
                    for (final Element binding : this.bindings) {
                        for (final Element item : DOMUtils.getChildElements(binding)) {
                            final String localName = item.getLocalName();
                            if (!"bindings".equals(localName)) {
                                try {
                                    new DOMForestScanner(SCDBasedBindingSet.this.forest).scan(item, SCDBasedBindingSet.this.loader);
                                    final BIDeclaration decl = (BIDeclaration)SCDBasedBindingSet.this.unmarshaller.getResult();
                                    final XSAnnotation ann = target.getAnnotation(true);
                                    BindInfo bi = (BindInfo)ann.getAnnotation();
                                    if (bi == null) {
                                        bi = new BindInfo();
                                        ann.setAnnotation(bi);
                                    }
                                    bi.addDecl(decl);
                                }
                                catch (SAXException e2) {}
                                catch (JAXBException e) {
                                    throw new AssertionError((Object)e);
                                }
                            }
                        }
                    }
                }
                return;
            }
            if (this.src.getAttributeNode("if-exists") != null) {
                return;
            }
            SCDBasedBindingSet.this.reportError(this.src, Messages.format("ERR_SCD_EVALUATED_EMPTY", this.scd));
        }
    }
}
