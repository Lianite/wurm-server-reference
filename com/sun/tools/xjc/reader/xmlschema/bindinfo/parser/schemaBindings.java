// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.NGCCRuntimeEx;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BISchemaBinding;

class schemaBindings extends NGCCHandler
{
    private BISchemaBinding.NamingRule at;
    private String packageName;
    private BISchemaBinding.NamingRule mt;
    private String javadoc;
    private BISchemaBinding.NamingRule nt;
    private BISchemaBinding.NamingRule tt;
    private BISchemaBinding.NamingRule et;
    protected final NGCCRuntimeEx $runtime;
    private int $_ngcc_current_state;
    protected String $uri;
    protected String $localName;
    protected String $qname;
    private Locator loc;
    
    public final NGCCRuntime getRuntime() {
        return (NGCCRuntime)this.$runtime;
    }
    
    public schemaBindings(final NGCCHandler parent, final NGCCEventSource source, final NGCCRuntimeEx runtime, final int cookie) {
        super(source, parent, cookie);
        this.$runtime = runtime;
        this.$_ngcc_current_state = 34;
    }
    
    public schemaBindings(final NGCCRuntimeEx runtime) {
        this(null, (NGCCEventSource)runtime, runtime, -1);
    }
    
    private void action0() throws SAXException {
        this.loc = this.$runtime.copyLocator();
    }
    
    public void enterElement(final String $__uri, final String $__local, final String $__qname, final Attributes $attrs) throws SAXException {
        this.$uri = $__uri;
        this.$localName = $__local;
        this.$qname = $__qname;
        switch (this.$_ngcc_current_state) {
            case 7: {
                int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "suffix")) >= 0 || ($ai = this.$runtime.getAttributeIndex("", "prefix")) >= 0) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 261);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 22: {
                int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "suffix")) >= 0 || ($ai = this.$runtime.getAttributeIndex("", "prefix")) >= 0) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 277);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 27: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "javadoc") {
                    final NGCCHandler h = (NGCCHandler)new javadoc((NGCCHandler)this, super._source, this.$runtime, 305);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 26;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 19: {
                int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "suffix")) >= 0 || ($ai = this.$runtime.getAttributeIndex("", "prefix")) >= 0) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 273);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 34: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "schemaBindings") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.action0();
                    this.$_ngcc_current_state = 25;
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 2: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "nameXmlTransform") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 4;
                    break;
                }
                this.$_ngcc_current_state = 1;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 15: {
                int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "suffix")) >= 0 || ($ai = this.$runtime.getAttributeIndex("", "prefix")) >= 0) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 269);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 3: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "typeName") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 22;
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "elementName") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 19;
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "attributeName") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 15;
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "modelGroupName") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 11;
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "anonymousTypeName") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 7;
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 25: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "package") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 29;
                    break;
                }
                this.$_ngcc_current_state = 2;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 0: {
                this.revertToParentFromEnterElement((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 29: {
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
            case 4: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "typeName") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 22;
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "elementName") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 19;
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "attributeName") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 15;
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "modelGroupName") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 11;
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "anonymousTypeName") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 7;
                    break;
                }
                this.$_ngcc_current_state = 3;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 11: {
                int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "suffix")) >= 0 || ($ai = this.$runtime.getAttributeIndex("", "prefix")) >= 0) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 265);
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
            case 7: {
                int $ai;
                if (($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "anonymousTypeName") || (($ai = this.$runtime.getAttributeIndex("", "suffix")) >= 0 && $__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "anonymousTypeName") || (($ai = this.$runtime.getAttributeIndex("", "prefix")) >= 0 && $__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "anonymousTypeName")) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 261);
                    this.spawnChildFromLeaveElement((NGCCEventReceiver)h, $__uri, $__local, $__qname);
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 22: {
                int $ai;
                if (($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "typeName") || (($ai = this.$runtime.getAttributeIndex("", "suffix")) >= 0 && $__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "typeName") || (($ai = this.$runtime.getAttributeIndex("", "prefix")) >= 0 && $__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "typeName")) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 277);
                    this.spawnChildFromLeaveElement((NGCCEventReceiver)h, $__uri, $__local, $__qname);
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 27: {
                this.$_ngcc_current_state = 26;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 19: {
                int $ai;
                if (($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "elementName") || (($ai = this.$runtime.getAttributeIndex("", "suffix")) >= 0 && $__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "elementName") || (($ai = this.$runtime.getAttributeIndex("", "prefix")) >= 0 && $__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "elementName")) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 273);
                    this.spawnChildFromLeaveElement((NGCCEventReceiver)h, $__uri, $__local, $__qname);
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 1: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "schemaBindings") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 0;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 26: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "package") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 2;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 15: {
                int $ai;
                if (($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "attributeName") || (($ai = this.$runtime.getAttributeIndex("", "suffix")) >= 0 && $__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "attributeName") || (($ai = this.$runtime.getAttributeIndex("", "prefix")) >= 0 && $__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "attributeName")) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 269);
                    this.spawnChildFromLeaveElement((NGCCEventReceiver)h, $__uri, $__local, $__qname);
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 6: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "anonymousTypeName") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 3;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 3: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "nameXmlTransform") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 1;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 10: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "modelGroupName") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 3;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 25: {
                this.$_ngcc_current_state = 2;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 0: {
                this.revertToParentFromLeaveElement((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 29: {
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
            case 18: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "elementName") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 3;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 21: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "typeName") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 3;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 14: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "attributeName") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 3;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 4: {
                this.$_ngcc_current_state = 3;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 11: {
                int $ai;
                if ((($ai = this.$runtime.getAttributeIndex("", "suffix")) >= 0 && $__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "modelGroupName") || ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "modelGroupName") || (($ai = this.$runtime.getAttributeIndex("", "prefix")) >= 0 && $__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "modelGroupName")) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 265);
                    this.spawnChildFromLeaveElement((NGCCEventReceiver)h, $__uri, $__local, $__qname);
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
            case 15: {
                if (($__uri == "" && $__local == "suffix") || ($__uri == "" && $__local == "prefix")) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 269);
                    this.spawnChildFromEnterAttribute((NGCCEventReceiver)h, $__uri, $__local, $__qname);
                    break;
                }
                this.unexpectedEnterAttribute($__qname);
                break;
            }
            case 7: {
                if (($__uri == "" && $__local == "suffix") || ($__uri == "" && $__local == "prefix")) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 261);
                    this.spawnChildFromEnterAttribute((NGCCEventReceiver)h, $__uri, $__local, $__qname);
                    break;
                }
                this.unexpectedEnterAttribute($__qname);
                break;
            }
            case 22: {
                if (($__uri == "" && $__local == "suffix") || ($__uri == "" && $__local == "prefix")) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 277);
                    this.spawnChildFromEnterAttribute((NGCCEventReceiver)h, $__uri, $__local, $__qname);
                    break;
                }
                this.unexpectedEnterAttribute($__qname);
                break;
            }
            case 25: {
                this.$_ngcc_current_state = 2;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 0: {
                this.revertToParentFromEnterAttribute((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 19: {
                if (($__uri == "" && $__local == "suffix") || ($__uri == "" && $__local == "prefix")) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 273);
                    this.spawnChildFromEnterAttribute((NGCCEventReceiver)h, $__uri, $__local, $__qname);
                    break;
                }
                this.unexpectedEnterAttribute($__qname);
                break;
            }
            case 29: {
                if ($__uri == "" && $__local == "name") {
                    this.$_ngcc_current_state = 31;
                    break;
                }
                this.$_ngcc_current_state = 27;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 27: {
                this.$_ngcc_current_state = 26;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 4: {
                this.$_ngcc_current_state = 3;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 11: {
                if (($__uri == "" && $__local == "suffix") || ($__uri == "" && $__local == "prefix")) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 265);
                    this.spawnChildFromEnterAttribute((NGCCEventReceiver)h, $__uri, $__local, $__qname);
                    break;
                }
                this.unexpectedEnterAttribute($__qname);
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
            case 25: {
                this.$_ngcc_current_state = 2;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 0: {
                this.revertToParentFromLeaveAttribute((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 30: {
                if ($__uri == "" && $__local == "name") {
                    this.$_ngcc_current_state = 27;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 29: {
                this.$_ngcc_current_state = 27;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 27: {
                this.$_ngcc_current_state = 26;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 4: {
                this.$_ngcc_current_state = 3;
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
            case 15: {
                int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "prefix")) >= 0) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 269);
                    this.spawnChildFromText((NGCCEventReceiver)h, $value);
                    break;
                }
                if (($ai = this.$runtime.getAttributeIndex("", "suffix")) >= 0) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 269);
                    this.spawnChildFromText((NGCCEventReceiver)h, $value);
                    break;
                }
                break;
            }
            case 7: {
                int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "prefix")) >= 0) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 261);
                    this.spawnChildFromText((NGCCEventReceiver)h, $value);
                    break;
                }
                if (($ai = this.$runtime.getAttributeIndex("", "suffix")) >= 0) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 261);
                    this.spawnChildFromText((NGCCEventReceiver)h, $value);
                    break;
                }
                break;
            }
            case 31: {
                this.packageName = $value;
                this.$_ngcc_current_state = 30;
                break;
            }
            case 22: {
                int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "prefix")) >= 0) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 277);
                    this.spawnChildFromText((NGCCEventReceiver)h, $value);
                    break;
                }
                if (($ai = this.$runtime.getAttributeIndex("", "suffix")) >= 0) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 277);
                    this.spawnChildFromText((NGCCEventReceiver)h, $value);
                    break;
                }
                break;
            }
            case 25: {
                this.$_ngcc_current_state = 2;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 0: {
                this.revertToParentFromText((Object)this.makeResult(), super._cookie, $value);
                break;
            }
            case 19: {
                int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "prefix")) >= 0) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 273);
                    this.spawnChildFromText((NGCCEventReceiver)h, $value);
                    break;
                }
                if (($ai = this.$runtime.getAttributeIndex("", "suffix")) >= 0) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 273);
                    this.spawnChildFromText((NGCCEventReceiver)h, $value);
                    break;
                }
                break;
            }
            case 29: {
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
            case 27: {
                this.$_ngcc_current_state = 26;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 4: {
                this.$_ngcc_current_state = 3;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 11: {
                int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "prefix")) >= 0) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 265);
                    this.spawnChildFromText((NGCCEventReceiver)h, $value);
                    break;
                }
                if (($ai = this.$runtime.getAttributeIndex("", "suffix")) >= 0) {
                    final NGCCHandler h = new nameXmlTransformRule(this, super._source, this.$runtime, 265);
                    this.spawnChildFromText((NGCCEventReceiver)h, $value);
                    break;
                }
                break;
            }
        }
    }
    
    public void onChildCompleted(final Object $__result__, final int $__cookie__, final boolean $__needAttCheck__) throws SAXException {
        switch ($__cookie__) {
            case 305: {
                this.javadoc = (String)$__result__;
                this.$_ngcc_current_state = 26;
                break;
            }
            case 261: {
                this.nt = (BISchemaBinding.NamingRule)$__result__;
                this.$_ngcc_current_state = 6;
                break;
            }
            case 277: {
                this.tt = (BISchemaBinding.NamingRule)$__result__;
                this.$_ngcc_current_state = 21;
                break;
            }
            case 273: {
                this.et = (BISchemaBinding.NamingRule)$__result__;
                this.$_ngcc_current_state = 18;
                break;
            }
            case 269: {
                this.at = (BISchemaBinding.NamingRule)$__result__;
                this.$_ngcc_current_state = 14;
                break;
            }
            case 265: {
                this.mt = (BISchemaBinding.NamingRule)$__result__;
                this.$_ngcc_current_state = 10;
                break;
            }
        }
    }
    
    public boolean accepted() {
        return this.$_ngcc_current_state == 0;
    }
    
    public BISchemaBinding makeResult() {
        return new BISchemaBinding(this.packageName, this.javadoc, this.tt, this.et, this.at, this.mt, this.nt, this.loc);
    }
}
