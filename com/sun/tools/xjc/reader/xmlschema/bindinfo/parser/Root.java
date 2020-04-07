// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo.parser;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.NGCCRuntimeEx;

class Root extends NGCCHandler
{
    protected final NGCCRuntimeEx $runtime;
    private int $_ngcc_current_state;
    protected String $uri;
    protected String $localName;
    protected String $qname;
    
    public final NGCCRuntime getRuntime() {
        return (NGCCRuntime)this.$runtime;
    }
    
    public Root(final NGCCHandler parent, final NGCCEventSource source, final NGCCRuntimeEx runtime, final int cookie) {
        super(source, parent, cookie);
        this.$runtime = runtime;
        this.$_ngcc_current_state = 1;
    }
    
    public Root(final NGCCRuntimeEx runtime) {
        this(null, (NGCCEventSource)runtime, runtime, -1);
    }
    
    public void enterElement(final String $__uri, final String $__local, final String $__qname, final Attributes $attrs) throws SAXException {
        this.$uri = $__uri;
        this.$localName = $__local;
        this.$qname = $__qname;
        switch (this.$_ngcc_current_state) {
            case 0: {
                this.revertToParentFromEnterElement((Object)this, super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 1: {
                if (($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "typesafeEnumMember") || ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "dom") || ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "class") || ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "typesafeEnumClass") || ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "property") || ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "idSymbolSpace") || ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "globalBindings") || ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "schemaBindings") || ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "javaType")) {
                    final NGCCHandler h = (NGCCHandler)new declaration((NGCCHandler)this, super._source, this.$runtime, 426);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                if ($__uri == "http://www.w3.org/2001/XMLSchema" && $__local == "annotation") {
                    final NGCCHandler h = (NGCCHandler)new AnnotationState((NGCCHandler)this, super._source, this.$runtime, 424);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
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
            case 0: {
                this.revertToParentFromLeaveElement((Object)this, super._cookie, $__uri, $__local, $__qname);
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
            case 0: {
                this.revertToParentFromEnterAttribute((Object)this, super._cookie, $__uri, $__local, $__qname);
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
            case 0: {
                this.revertToParentFromLeaveAttribute((Object)this, super._cookie, $__uri, $__local, $__qname);
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
            case 0: {
                this.revertToParentFromText((Object)this, super._cookie, $value);
                break;
            }
        }
    }
    
    public void onChildCompleted(final Object $__result__, final int $__cookie__, final boolean $__needAttCheck__) throws SAXException {
        switch ($__cookie__) {
            case 426: {
                this.$_ngcc_current_state = 0;
                break;
            }
            case 424: {
                this.$_ngcc_current_state = 0;
                break;
            }
        }
    }
    
    public boolean accepted() {
        return this.$_ngcc_current_state == 0;
    }
}
