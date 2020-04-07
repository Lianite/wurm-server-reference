// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo.parser;

import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIXDom;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIXIdSymbolSpace;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIEnumMember;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIEnum;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIConversion;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIClass;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BISchemaBinding;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIGlobalBinding;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIDeclaration;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.NGCCRuntimeEx;

class declaration extends NGCCHandler
{
    protected final NGCCRuntimeEx $runtime;
    private int $_ngcc_current_state;
    protected String $uri;
    protected String $localName;
    protected String $qname;
    private BIDeclaration result;
    
    public final NGCCRuntime getRuntime() {
        return (NGCCRuntime)this.$runtime;
    }
    
    public declaration(final NGCCHandler parent, final NGCCEventSource source, final NGCCRuntimeEx runtime, final int cookie) {
        super(source, parent, cookie);
        this.$runtime = runtime;
        this.$_ngcc_current_state = 1;
    }
    
    public declaration(final NGCCRuntimeEx runtime) {
        this(null, (NGCCEventSource)runtime, runtime, -1);
    }
    
    public void enterElement(final String $__uri, final String $__local, final String $__qname, final Attributes $attrs) throws SAXException {
        this.$uri = $__uri;
        this.$localName = $__local;
        this.$qname = $__qname;
        switch (this.$_ngcc_current_state) {
            case 1: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "globalBindings") {
                    final NGCCHandler h = (NGCCHandler)new globalBindings((NGCCHandler)this, super._source, this.$runtime, 71);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "schemaBindings") {
                    final NGCCHandler h = (NGCCHandler)new schemaBindings((NGCCHandler)this, super._source, this.$runtime, 72);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "class") {
                    final NGCCHandler h = new BIClassState(this, super._source, this.$runtime, 73);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "javaType") {
                    final NGCCHandler h = new conversion(this, super._source, this.$runtime, 74);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "property") {
                    final NGCCHandler h = (NGCCHandler)new property((NGCCHandler)this, super._source, this.$runtime, 75);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "typesafeEnumClass") {
                    final NGCCHandler h = (NGCCHandler)new enumDef((NGCCHandler)this, super._source, this.$runtime, 76);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "typesafeEnumMember") {
                    final NGCCHandler h = (NGCCHandler)new enumMember((NGCCHandler)this, super._source, this.$runtime, 77);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "idSymbolSpace") {
                    final NGCCHandler h = (NGCCHandler)new idSymbolSpace((NGCCHandler)this, super._source, this.$runtime, 78);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "dom") {
                    final NGCCHandler h = (NGCCHandler)new dom((NGCCHandler)this, super._source, this.$runtime, 27);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 0: {
                this.revertToParentFromEnterElement((Object)this.result, super._cookie, $__uri, $__local, $__qname, $attrs);
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
                this.revertToParentFromLeaveElement((Object)this.result, super._cookie, $__uri, $__local, $__qname);
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
                this.revertToParentFromEnterAttribute((Object)this.result, super._cookie, $__uri, $__local, $__qname);
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
                this.revertToParentFromLeaveAttribute((Object)this.result, super._cookie, $__uri, $__local, $__qname);
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
                this.revertToParentFromText((Object)this.result, super._cookie, $value);
                break;
            }
        }
    }
    
    public void onChildCompleted(final Object $__result__, final int $__cookie__, final boolean $__needAttCheck__) throws SAXException {
        switch ($__cookie__) {
            case 71: {
                this.result = (BIGlobalBinding)$__result__;
                this.$_ngcc_current_state = 0;
                break;
            }
            case 72: {
                this.result = (BISchemaBinding)$__result__;
                this.$_ngcc_current_state = 0;
                break;
            }
            case 73: {
                this.result = (BIClass)$__result__;
                this.$_ngcc_current_state = 0;
                break;
            }
            case 74: {
                this.result = (BIConversion)$__result__;
                this.$_ngcc_current_state = 0;
                break;
            }
            case 75: {
                this.result = (BIProperty)$__result__;
                this.$_ngcc_current_state = 0;
                break;
            }
            case 76: {
                this.result = (BIEnum)$__result__;
                this.$_ngcc_current_state = 0;
                break;
            }
            case 77: {
                this.result = (BIEnumMember)$__result__;
                this.$_ngcc_current_state = 0;
                break;
            }
            case 78: {
                this.result = (BIDeclaration)$__result__;
                this.$_ngcc_current_state = 0;
                break;
            }
            case 27: {
                this.result = (BIXDom)$__result__;
                this.$_ngcc_current_state = 0;
                break;
            }
        }
    }
    
    public boolean accepted() {
        return this.$_ngcc_current_state == 0;
    }
}
