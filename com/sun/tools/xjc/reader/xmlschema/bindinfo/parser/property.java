// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo.parser;

import com.sun.codemodel.JType;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIProperty;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.NGCCRuntimeEx;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIConversion;
import com.sun.tools.xjc.generator.field.FieldRendererFactory;

class property extends NGCCHandler
{
    private String name;
    private String javadoc;
    private FieldRendererFactory ct;
    private String isSetStr;
    private String baseType;
    private String isConstStr;
    private BIConversion conv;
    private String failFast;
    protected final NGCCRuntimeEx $runtime;
    private int $_ngcc_current_state;
    protected String $uri;
    protected String $localName;
    protected String $qname;
    private Locator loc;
    private Boolean isConst;
    private Boolean isSet;
    
    public final NGCCRuntime getRuntime() {
        return (NGCCRuntime)this.$runtime;
    }
    
    public property(final NGCCHandler parent, final NGCCEventSource source, final NGCCRuntimeEx runtime, final int cookie) {
        super(source, parent, cookie);
        this.isConst = null;
        this.isSet = null;
        this.$runtime = runtime;
        this.$_ngcc_current_state = 35;
    }
    
    public property(final NGCCRuntimeEx runtime) {
        this(null, (NGCCEventSource)runtime, runtime, -1);
    }
    
    private void action0() throws SAXException {
        if (this.$runtime.parseBoolean(this.failFast)) {
            this.$runtime.reportUnimplementedFeature("generateFailFastSetterMethod");
        }
    }
    
    private void action1() throws SAXException {
        this.isSet = (this.$runtime.parseBoolean(this.isSetStr) ? Boolean.TRUE : Boolean.FALSE);
    }
    
    private void action2() throws SAXException {
        this.isConst = (this.$runtime.parseBoolean(this.isConstStr) ? Boolean.TRUE : Boolean.FALSE);
    }
    
    private void action3() throws SAXException {
        this.loc = this.$runtime.copyLocator();
    }
    
    public void enterElement(final String $__uri, final String $__local, final String $__qname, final Attributes $attrs) throws SAXException {
        this.$uri = $__uri;
        this.$localName = $__local;
        this.$qname = $__qname;
        switch (this.$_ngcc_current_state) {
            case 27: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "baseType")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 23;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 31: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 27;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 10: {
                if (($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "baseType") || ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "javadoc")) {
                    this.spawnChildFromEnterElement((NGCCEventReceiver)new InterleaveFilter_8_3(this, this, 370), $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 0: {
                this.revertToParentFromEnterElement((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 15: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "generateIsSetMethod")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 11;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 35: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "property") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.action3();
                    this.$_ngcc_current_state = 31;
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 11: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "generateFailFastSetterMethod")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 10;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 23: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "collectionType")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 19;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 19: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "fixedAttributeAsConstantProperty")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 15;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
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
            case 27: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "baseType")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 23;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 31: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 27;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 1: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "property") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 0;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 10: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "property") {
                    this.spawnChildFromLeaveElement((NGCCEventReceiver)new InterleaveFilter_8_3(this, this, 370), $__uri, $__local, $__qname);
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 0: {
                this.revertToParentFromLeaveElement((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 15: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "generateIsSetMethod")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 11;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 11: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "generateFailFastSetterMethod")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 10;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 23: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "collectionType")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 19;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 19: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "fixedAttributeAsConstantProperty")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 15;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
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
            case 27: {
                if ($__uri == "" && $__local == "baseType") {
                    this.$_ngcc_current_state = 29;
                    break;
                }
                this.$_ngcc_current_state = 23;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 31: {
                if ($__uri == "" && $__local == "name") {
                    this.$_ngcc_current_state = 33;
                    break;
                }
                this.$_ngcc_current_state = 27;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 0: {
                this.revertToParentFromEnterAttribute((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 15: {
                if ($__uri == "" && $__local == "generateIsSetMethod") {
                    this.$_ngcc_current_state = 17;
                    break;
                }
                this.$_ngcc_current_state = 11;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 11: {
                if ($__uri == "" && $__local == "generateFailFastSetterMethod") {
                    this.$_ngcc_current_state = 13;
                    break;
                }
                this.$_ngcc_current_state = 10;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 23: {
                if ($__uri == "" && $__local == "collectionType") {
                    this.$_ngcc_current_state = 25;
                    break;
                }
                this.$_ngcc_current_state = 19;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 19: {
                if ($__uri == "" && $__local == "fixedAttributeAsConstantProperty") {
                    this.$_ngcc_current_state = 21;
                    break;
                }
                this.$_ngcc_current_state = 15;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
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
            case 24: {
                if ($__uri == "" && $__local == "collectionType") {
                    this.$_ngcc_current_state = 19;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 28: {
                if ($__uri == "" && $__local == "baseType") {
                    this.$_ngcc_current_state = 23;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 16: {
                if ($__uri == "" && $__local == "generateIsSetMethod") {
                    this.$_ngcc_current_state = 11;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 0: {
                this.revertToParentFromLeaveAttribute((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 15: {
                this.$_ngcc_current_state = 11;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 32: {
                if ($__uri == "" && $__local == "name") {
                    this.$_ngcc_current_state = 27;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 20: {
                if ($__uri == "" && $__local == "fixedAttributeAsConstantProperty") {
                    this.$_ngcc_current_state = 15;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 27: {
                this.$_ngcc_current_state = 23;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 12: {
                if ($__uri == "" && $__local == "generateFailFastSetterMethod") {
                    this.$_ngcc_current_state = 10;
                    this.action0();
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 31: {
                this.$_ngcc_current_state = 27;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 11: {
                this.$_ngcc_current_state = 10;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 19: {
                this.$_ngcc_current_state = 15;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 23: {
                this.$_ngcc_current_state = 19;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
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
            case 13: {
                this.failFast = $value;
                this.$_ngcc_current_state = 12;
                break;
            }
            case 33: {
                this.name = $value;
                this.$_ngcc_current_state = 32;
                break;
            }
            case 0: {
                this.revertToParentFromText((Object)this.makeResult(), super._cookie, $value);
                break;
            }
            case 15: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "generateIsSetMethod")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 11;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 25: {
                final NGCCHandler h = new CollectionTypeState(this, super._source, this.$runtime, 387);
                this.spawnChildFromText((NGCCEventReceiver)h, $value);
                break;
            }
            case 27: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "baseType")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 23;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 21: {
                this.isConstStr = $value;
                this.$_ngcc_current_state = 20;
                this.action2();
                break;
            }
            case 31: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 27;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 17: {
                this.isSetStr = $value;
                this.$_ngcc_current_state = 16;
                this.action1();
                break;
            }
            case 29: {
                this.baseType = $value;
                this.$_ngcc_current_state = 28;
                break;
            }
            case 11: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "generateFailFastSetterMethod")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 10;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 19: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "fixedAttributeAsConstantProperty")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 15;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 23: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "collectionType")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 19;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
        }
    }
    
    public void onChildCompleted(final Object $__result__, final int $__cookie__, final boolean $__needAttCheck__) throws SAXException {
        switch ($__cookie__) {
            case 368: {
                this.javadoc = (String)$__result__;
                this.$_ngcc_current_state = 7;
                break;
            }
            case 370: {
                this.$_ngcc_current_state = 1;
                break;
            }
            case 363: {
                this.conv = (BIConversion)$__result__;
                this.$_ngcc_current_state = 4;
                break;
            }
            case 387: {
                this.ct = (FieldRendererFactory)$__result__;
                this.$_ngcc_current_state = 24;
                break;
            }
        }
    }
    
    public boolean accepted() {
        return this.$_ngcc_current_state == 3 || this.$_ngcc_current_state == 7 || this.$_ngcc_current_state == 0 || this.$_ngcc_current_state == 8 || this.$_ngcc_current_state == 2;
    }
    
    public BIProperty makeResult() throws SAXException {
        JType baseTypeRef = null;
        if (this.baseType != null) {
            baseTypeRef = this.$runtime.getType(this.baseType);
        }
        return new BIProperty(this.loc, this.name, this.javadoc, baseTypeRef, this.conv, this.ct, this.isConst, this.isSet);
    }
    
    class InterleaveFilter_8_3 extends NGCCInterleaveFilter
    {
        InterleaveFilter_8_3(final property this$0, final property parent, final int cookie) {
            super((NGCCHandler)parent, cookie);
            this.this$0 = this$0;
            this.setHandlers(new NGCCEventReceiver[] { new Branch8(this$0, (NGCCInterleaveFilter)this), new Branch3(this$0, (NGCCInterleaveFilter)this) });
        }
        
        protected int findReceiverOfElement(final String $uri, final String $local) {
            if ($uri == "http://java.sun.com/xml/ns/jaxb" && $local == "javadoc") {
                return 0;
            }
            if ($uri == "http://java.sun.com/xml/ns/jaxb" && $local == "baseType") {
                return 1;
            }
            return -1;
        }
        
        protected int findReceiverOfAttribute(final String $uri, final String $local) {
            return -1;
        }
        
        protected int findReceiverOfText() {
            return -1;
        }
    }
    
    class Branch8 extends NGCCHandler
    {
        private int $_ngcc_current_state;
        
        public final NGCCRuntime getRuntime() {
            return (NGCCRuntime)this.this$0.$runtime;
        }
        
        Branch8(final property this$0, final NGCCInterleaveFilter source) {
            super((NGCCEventSource)source, (NGCCHandler)null, -1);
            this.this$0 = this$0;
            this.$_ngcc_current_state = 8;
        }
        
        public void enterElement(final String $__uri, final String $__local, final String $__qname, final Attributes $attrs) throws SAXException {
            this.this$0.$uri = $__uri;
            this.this$0.$localName = $__local;
            this.this$0.$qname = $__qname;
            switch (this.$_ngcc_current_state) {
                case 7: {
                    ((NGCCInterleaveFilter)super._source).joinByEnterElement((NGCCEventReceiver)this, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                case 8: {
                    if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "javadoc") {
                        final NGCCHandler h = (NGCCHandler)new javadoc((NGCCHandler)this, super._source, this.this$0.$runtime, 368);
                        this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                        break;
                    }
                    this.$_ngcc_current_state = 7;
                    this.this$0.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                default: {
                    this.unexpectedEnterElement($__qname);
                    break;
                }
            }
        }
        
        public void leaveElement(final String $__uri, final String $__local, final String $__qname) throws SAXException {
            this.this$0.$uri = $__uri;
            this.this$0.$localName = $__local;
            this.this$0.$qname = $__qname;
            switch (this.$_ngcc_current_state) {
                case 7: {
                    ((NGCCInterleaveFilter)super._source).joinByLeaveElement((NGCCEventReceiver)this, $__uri, $__local, $__qname);
                    break;
                }
                case 8: {
                    this.$_ngcc_current_state = 7;
                    this.this$0.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                default: {
                    this.unexpectedLeaveElement($__qname);
                    break;
                }
            }
        }
        
        public void enterAttribute(final String $__uri, final String $__local, final String $__qname) throws SAXException {
            this.this$0.$uri = $__uri;
            this.this$0.$localName = $__local;
            this.this$0.$qname = $__qname;
            switch (this.$_ngcc_current_state) {
                case 7: {
                    ((NGCCInterleaveFilter)super._source).joinByEnterAttribute((NGCCEventReceiver)this, $__uri, $__local, $__qname);
                    break;
                }
                case 8: {
                    this.$_ngcc_current_state = 7;
                    this.this$0.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                default: {
                    this.unexpectedEnterAttribute($__qname);
                    break;
                }
            }
        }
        
        public void leaveAttribute(final String $__uri, final String $__local, final String $__qname) throws SAXException {
            this.this$0.$uri = $__uri;
            this.this$0.$localName = $__local;
            this.this$0.$qname = $__qname;
            switch (this.$_ngcc_current_state) {
                case 7: {
                    ((NGCCInterleaveFilter)super._source).joinByLeaveAttribute((NGCCEventReceiver)this, $__uri, $__local, $__qname);
                    break;
                }
                case 8: {
                    this.$_ngcc_current_state = 7;
                    this.this$0.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
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
                case 7: {
                    ((NGCCInterleaveFilter)super._source).joinByText((NGCCEventReceiver)this, $value);
                    break;
                }
                case 8: {
                    this.$_ngcc_current_state = 7;
                    this.this$0.$runtime.sendText(super._cookie, $value);
                    break;
                }
            }
        }
        
        public void onChildCompleted(final Object $__result__, final int $__cookie__, final boolean $__needAttCheck__) throws SAXException {
            switch ($__cookie__) {
                case 368: {
                    this.this$0.javadoc = (String)$__result__;
                    this.$_ngcc_current_state = 7;
                    break;
                }
                case 370: {
                    this.$_ngcc_current_state = 1;
                    break;
                }
                case 363: {
                    this.this$0.conv = (BIConversion)$__result__;
                    this.$_ngcc_current_state = 4;
                    break;
                }
                case 387: {
                    this.this$0.ct = (FieldRendererFactory)$__result__;
                    this.$_ngcc_current_state = 24;
                    break;
                }
            }
        }
        
        public boolean accepted() {
            return this.$_ngcc_current_state == 3 || this.$_ngcc_current_state == 7 || this.$_ngcc_current_state == 0 || this.$_ngcc_current_state == 8 || this.$_ngcc_current_state == 2;
        }
    }
    
    class Branch3 extends NGCCHandler
    {
        private int $_ngcc_current_state;
        
        public final NGCCRuntime getRuntime() {
            return (NGCCRuntime)this.this$0.$runtime;
        }
        
        Branch3(final property this$0, final NGCCInterleaveFilter source) {
            super((NGCCEventSource)source, (NGCCHandler)null, -1);
            this.this$0 = this$0;
            this.$_ngcc_current_state = 3;
        }
        
        public void enterElement(final String $__uri, final String $__local, final String $__qname, final Attributes $attrs) throws SAXException {
            this.this$0.$uri = $__uri;
            this.this$0.$localName = $__local;
            this.this$0.$qname = $__qname;
            switch (this.$_ngcc_current_state) {
                case 3: {
                    if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "baseType") {
                        this.this$0.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                        this.$_ngcc_current_state = 5;
                        break;
                    }
                    this.$_ngcc_current_state = 2;
                    this.this$0.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                case 2: {
                    ((NGCCInterleaveFilter)super._source).joinByEnterElement((NGCCEventReceiver)this, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                case 5: {
                    if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "javaType") {
                        final NGCCHandler h = new conversion(this, super._source, this.this$0.$runtime, 363);
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
            this.this$0.$uri = $__uri;
            this.this$0.$localName = $__local;
            this.this$0.$qname = $__qname;
            switch (this.$_ngcc_current_state) {
                case 4: {
                    if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "baseType") {
                        this.this$0.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                        this.$_ngcc_current_state = 2;
                        break;
                    }
                    this.unexpectedLeaveElement($__qname);
                    break;
                }
                case 3: {
                    this.$_ngcc_current_state = 2;
                    this.this$0.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                case 2: {
                    ((NGCCInterleaveFilter)super._source).joinByLeaveElement((NGCCEventReceiver)this, $__uri, $__local, $__qname);
                    break;
                }
                default: {
                    this.unexpectedLeaveElement($__qname);
                    break;
                }
            }
        }
        
        public void enterAttribute(final String $__uri, final String $__local, final String $__qname) throws SAXException {
            this.this$0.$uri = $__uri;
            this.this$0.$localName = $__local;
            this.this$0.$qname = $__qname;
            switch (this.$_ngcc_current_state) {
                case 3: {
                    this.$_ngcc_current_state = 2;
                    this.this$0.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                case 2: {
                    ((NGCCInterleaveFilter)super._source).joinByEnterAttribute((NGCCEventReceiver)this, $__uri, $__local, $__qname);
                    break;
                }
                default: {
                    this.unexpectedEnterAttribute($__qname);
                    break;
                }
            }
        }
        
        public void leaveAttribute(final String $__uri, final String $__local, final String $__qname) throws SAXException {
            this.this$0.$uri = $__uri;
            this.this$0.$localName = $__local;
            this.this$0.$qname = $__qname;
            switch (this.$_ngcc_current_state) {
                case 3: {
                    this.$_ngcc_current_state = 2;
                    this.this$0.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                case 2: {
                    ((NGCCInterleaveFilter)super._source).joinByLeaveAttribute((NGCCEventReceiver)this, $__uri, $__local, $__qname);
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
                case 3: {
                    this.$_ngcc_current_state = 2;
                    this.this$0.$runtime.sendText(super._cookie, $value);
                    break;
                }
                case 2: {
                    ((NGCCInterleaveFilter)super._source).joinByText((NGCCEventReceiver)this, $value);
                    break;
                }
            }
        }
        
        public void onChildCompleted(final Object $__result__, final int $__cookie__, final boolean $__needAttCheck__) throws SAXException {
            switch ($__cookie__) {
                case 368: {
                    this.this$0.javadoc = (String)$__result__;
                    this.$_ngcc_current_state = 7;
                    break;
                }
                case 370: {
                    this.$_ngcc_current_state = 1;
                    break;
                }
                case 363: {
                    this.this$0.conv = (BIConversion)$__result__;
                    this.$_ngcc_current_state = 4;
                    break;
                }
                case 387: {
                    this.this$0.ct = (FieldRendererFactory)$__result__;
                    this.$_ngcc_current_state = 24;
                    break;
                }
            }
        }
        
        public boolean accepted() {
            return this.$_ngcc_current_state == 3 || this.$_ngcc_current_state == 7 || this.$_ngcc_current_state == 0 || this.$_ngcc_current_state == 8 || this.$_ngcc_current_state == 2;
        }
    }
}
