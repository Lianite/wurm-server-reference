// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo.parser;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.ContentHandler;
import java.io.Writer;
import com.sun.xml.bind.marshaller.DataWriter;
import org.xml.sax.SAXException;
import java.io.StringWriter;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BindInfo;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.NGCCRuntimeEx;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIDeclaration;

public class AnnotationState extends NGCCHandler
{
    private BIDeclaration result;
    private String msg;
    protected final NGCCRuntimeEx $runtime;
    private int $_ngcc_current_state;
    protected String $uri;
    protected String $localName;
    protected String $qname;
    public BindInfo bi;
    private StringWriter w;
    
    public final NGCCRuntime getRuntime() {
        return (NGCCRuntime)this.$runtime;
    }
    
    public AnnotationState(final NGCCHandler parent, final NGCCEventSource source, final NGCCRuntimeEx runtime, final int cookie) {
        super(source, parent, cookie);
        this.$runtime = runtime;
        this.$_ngcc_current_state = 20;
    }
    
    public AnnotationState(final NGCCRuntimeEx runtime) {
        this(null, (NGCCEventSource)runtime, runtime, -1);
    }
    
    private void action0() throws SAXException {
        this.bi.appendDocumentation("<pre>" + this.$runtime.escapeMarkup(this.$runtime.truncateDocComment(this.w.toString())) + "</pre>", false);
        this.w = null;
    }
    
    private void action1() throws SAXException {
        this.w = new StringWriter();
        final DataWriter xw = new DataWriter(this.w, "UTF-8");
        xw.setXmlDecl(false);
        this.$runtime.redirectSubtree((ContentHandler)xw, this.$uri, this.$localName, this.$qname);
    }
    
    private void action2() throws SAXException {
        this.bi.appendDocumentation(this.$runtime.truncateDocComment(this.msg), true);
    }
    
    private void action3() throws SAXException {
        this.$runtime.redirectSubtree((ContentHandler)new DefaultHandler(), this.$uri, this.$localName, this.$qname);
    }
    
    private void action4() throws SAXException {
        this.bi.addDecl(this.result);
    }
    
    private void action5() throws SAXException {
        this.bi = new BindInfo(this.$runtime.copyLocator());
        this.$runtime.currentBindInfo = this.bi;
    }
    
    public void enterElement(final String $__uri, final String $__local, final String $__qname, final Attributes $attrs) throws SAXException {
        this.$uri = $__uri;
        this.$localName = $__local;
        this.$qname = $__qname;
        switch (this.$_ngcc_current_state) {
            case 12: {
                if (($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "typesafeEnumMember") || ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "dom") || ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "class") || ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "typesafeEnumClass") || ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "property") || ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "idSymbolSpace") || ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "globalBindings") || ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "schemaBindings") || ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "javaType")) {
                    final NGCCHandler h = (NGCCHandler)new declaration((NGCCHandler)this, super._source, this.$runtime, 343);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                if (!$__uri.equals("http://java.sun.com/xml/ns/jaxb/xjc") && !$__uri.equals("http://java.sun.com/xml/ns/jaxb")) {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.action3();
                    this.$_ngcc_current_state = 16;
                    break;
                }
                this.$_ngcc_current_state = 11;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 2: {
                if ($__uri == "http://www.w3.org/2001/XMLSchema" && $__local == "appinfo") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 12;
                    break;
                }
                if ($__uri == "http://www.w3.org/2001/XMLSchema" && $__local == "documentation") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 5;
                    break;
                }
                this.$_ngcc_current_state = 1;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 11: {
                if (($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "typesafeEnumMember") || ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "dom") || ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "class") || ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "typesafeEnumClass") || ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "property") || ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "idSymbolSpace") || ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "globalBindings") || ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "schemaBindings") || ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "javaType")) {
                    final NGCCHandler h = (NGCCHandler)new declaration((NGCCHandler)this, super._source, this.$runtime, 340);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                if (!$__uri.equals("http://java.sun.com/xml/ns/jaxb/xjc") && !$__uri.equals("http://java.sun.com/xml/ns/jaxb")) {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.action3();
                    this.$_ngcc_current_state = 16;
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 4: {
                this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                this.action1();
                this.$_ngcc_current_state = 7;
                break;
            }
            case 5: {
                this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                this.action1();
                this.$_ngcc_current_state = 7;
                break;
            }
            case 20: {
                if ($__uri == "http://www.w3.org/2001/XMLSchema" && $__local == "annotation") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.action5();
                    this.$_ngcc_current_state = 2;
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 0: {
                this.revertToParentFromEnterElement((Object)this.bi, super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 1: {
                if ($__uri == "http://www.w3.org/2001/XMLSchema" && $__local == "appinfo") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 12;
                    break;
                }
                if ($__uri == "http://www.w3.org/2001/XMLSchema" && $__local == "documentation") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 5;
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            default: {
                this.unexpectedEnterElement($__qname);
                break;
            }
        }
    }
    
    public void leaveElement(final String $__uri, final String $__local, final String $__qname) throws SAXException {
        this.$uri = $__uri;
        this.$localName = $__local;
        this.$qname = $__qname;
        switch (this.$_ngcc_current_state) {
            case 12: {
                this.$_ngcc_current_state = 11;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 11: {
                if ($__uri == "http://www.w3.org/2001/XMLSchema" && $__local == "appinfo") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 1;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 7: {
                this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                this.$_ngcc_current_state = 4;
                this.action0();
                break;
            }
            case 16: {
                if (!$__uri.equals("http://java.sun.com/xml/ns/jaxb/xjc") && !$__uri.equals("http://java.sun.com/xml/ns/jaxb")) {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 11;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 4: {
                if ($__uri == "http://www.w3.org/2001/XMLSchema" && $__local == "documentation") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 1;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 5: {
                this.$_ngcc_current_state = 4;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 0: {
                this.revertToParentFromLeaveElement((Object)this.bi, super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 1: {
                if ($__uri == "http://www.w3.org/2001/XMLSchema" && $__local == "annotation") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 0;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            default: {
                this.unexpectedLeaveElement($__qname);
                break;
            }
        }
    }
    
    public void enterAttribute(final String $__uri, final String $__local, final String $__qname) throws SAXException {
        this.$uri = $__uri;
        this.$localName = $__local;
        this.$qname = $__qname;
        switch (this.$_ngcc_current_state) {
            case 12: {
                this.$_ngcc_current_state = 11;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 5: {
                this.$_ngcc_current_state = 4;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 0: {
                this.revertToParentFromEnterAttribute((Object)this.bi, super._cookie, $__uri, $__local, $__qname);
                break;
            }
            default: {
                this.unexpectedEnterAttribute($__qname);
                break;
            }
        }
    }
    
    public void leaveAttribute(final String $__uri, final String $__local, final String $__qname) throws SAXException {
        this.$uri = $__uri;
        this.$localName = $__local;
        this.$qname = $__qname;
        switch (this.$_ngcc_current_state) {
            case 12: {
                this.$_ngcc_current_state = 11;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 5: {
                this.$_ngcc_current_state = 4;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 0: {
                this.revertToParentFromLeaveAttribute((Object)this.bi, super._cookie, $__uri, $__local, $__qname);
                break;
            }
            default: {
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
        }
    }
    
    public void text(final String $value) throws SAXException {
        switch (this.$_ngcc_current_state) {
            case 12: {
                this.$_ngcc_current_state = 11;
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 11: {
                this.$_ngcc_current_state = 11;
                break;
            }
            case 4: {
                this.msg = $value;
                this.$_ngcc_current_state = 4;
                this.action2();
                break;
            }
            case 5: {
                this.msg = $value;
                this.$_ngcc_current_state = 4;
                this.action2();
                break;
            }
            case 0: {
                this.revertToParentFromText((Object)this.bi, super._cookie, $value);
                break;
            }
        }
    }
    
    public void onChildCompleted(final Object $__result__, final int $__cookie__, final boolean $__needAttCheck__) throws SAXException {
        switch ($__cookie__) {
            case 340: {
                this.result = (BIDeclaration)$__result__;
                this.action4();
                this.$_ngcc_current_state = 11;
                break;
            }
            case 343: {
                this.result = (BIDeclaration)$__result__;
                this.action4();
                this.$_ngcc_current_state = 11;
                break;
            }
        }
    }
    
    public boolean accepted() {
        return this.$_ngcc_current_state == 0;
    }
}
