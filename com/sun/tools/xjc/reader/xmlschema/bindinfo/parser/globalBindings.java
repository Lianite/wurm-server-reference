// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo.parser;

import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIGlobalBinding;
import org.xml.sax.Attributes;
import javax.xml.namespace.QName;
import org.xml.sax.SAXException;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import com.sun.tools.xjc.reader.NameConverter;
import java.util.Map;
import org.xml.sax.Locator;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.NGCCRuntimeEx;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIXSuperClass;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIConversion;
import com.sun.tools.xjc.generator.field.FieldRendererFactory;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIXSerializable;

class globalBindings extends NGCCHandler
{
    private String xmlType;
    private BIXSerializable xSerializable;
    private boolean xTypeSubstitution;
    private FieldRendererFactory ct;
    private String value;
    private String __text;
    private BIConversion conv;
    private BIXSuperClass xSuperClass;
    protected final NGCCRuntimeEx $runtime;
    private int $_ngcc_current_state;
    protected String $uri;
    protected String $localName;
    protected String $qname;
    private Locator loc;
    private Map globalConvs;
    private NameConverter nameConverter;
    private String enableJavaNamingConvention;
    private String fixedAttrToConstantProperty;
    private String needIsSetMethod;
    private Set enumBaseTypes;
    private boolean generateEnumMemberName;
    private boolean modelGroupBinding;
    private boolean choiceContentPropertyWithModelGroupBinding;
    private boolean xSmartWildcardDefaultBinding;
    
    public final NGCCRuntime getRuntime() {
        return (NGCCRuntime)this.$runtime;
    }
    
    public globalBindings(final NGCCHandler parent, final NGCCEventSource source, final NGCCRuntimeEx runtime, final int cookie) {
        super(source, parent, cookie);
        this.globalConvs = new HashMap();
        this.nameConverter = NameConverter.standard;
        this.enableJavaNamingConvention = "true";
        this.fixedAttrToConstantProperty = "false";
        this.needIsSetMethod = "false";
        this.enumBaseTypes = new HashSet();
        this.generateEnumMemberName = false;
        this.modelGroupBinding = false;
        this.choiceContentPropertyWithModelGroupBinding = false;
        this.xSmartWildcardDefaultBinding = false;
        this.$runtime = runtime;
        this.$_ngcc_current_state = 81;
    }
    
    public globalBindings(final NGCCRuntimeEx runtime) {
        this(null, (NGCCEventSource)runtime, runtime, -1);
    }
    
    private void action0() throws SAXException {
        this.$runtime.options.generateValidatingUnmarshallingCode = false;
    }
    
    private void action1() throws SAXException {
        this.$runtime.options.generateValidationCode = false;
    }
    
    private void action2() throws SAXException {
        this.$runtime.options.generateUnmarshallingCode = false;
        this.$runtime.options.generateValidatingUnmarshallingCode = false;
    }
    
    private void action3() throws SAXException {
        this.$runtime.options.generateMarshallingCode = false;
    }
    
    private void action4() throws SAXException {
        this.xSmartWildcardDefaultBinding = true;
    }
    
    private void action5() throws SAXException {
        this.globalConvs.put(this.$runtime.parseQName(this.xmlType), this.conv);
    }
    
    private void action6() throws SAXException {
        if (this.$runtime.parseBoolean(this.value)) {
            this.$runtime.reportUnsupportedFeature("enableFailFastCheck");
        }
    }
    
    private void action7() throws SAXException {
        if (this.$runtime.parseBoolean(this.value)) {
            this.$runtime.reportUnsupportedFeature("enableValidation");
        }
    }
    
    private void action8() throws SAXException {
        this.choiceContentPropertyWithModelGroupBinding = this.$runtime.parseBoolean(this.value);
    }
    
    private void action9() throws SAXException {
        this.modelGroupBinding = true;
    }
    
    private void action10() throws SAXException {
        this.modelGroupBinding = false;
    }
    
    private void action11() throws SAXException {
        final QName qn = this.$runtime.parseQName(this.value);
        this.enumBaseTypes.add(qn);
    }
    
    private void action12() throws SAXException {
        this.$runtime.processList(this.__text);
    }
    
    private void action13() throws SAXException {
        this.generateEnumMemberName = true;
    }
    
    private void action14() throws SAXException {
        this.nameConverter = NameConverter.jaxrpcCompatible;
    }
    
    private void action15() throws SAXException {
        this.loc = this.$runtime.copyLocator();
    }
    
    public void enterElement(final String $__uri, final String $__local, final String $__qname, final Attributes $attrs) throws SAXException {
        this.$uri = $__uri;
        this.$localName = $__local;
        this.$qname = $__qname;
        switch (this.$_ngcc_current_state) {
            case 63: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "generateIsSetMethod")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 59;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 75: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "underscoreBinding")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 71;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 38: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "choiceContentProperty")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 34;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 30: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "enableFailFastCheck")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 2;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 1: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "javaType") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 28;
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "serializable") {
                    final NGCCHandler h = (NGCCHandler)new serializable((NGCCHandler)this, super._source, this.$runtime, 168);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "superClass") {
                    final NGCCHandler h = (NGCCHandler)new superClass((NGCCHandler)this, super._source, this.$runtime, 169);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "typeSubstitution") {
                    final NGCCHandler h = (NGCCHandler)new typeSubstitution((NGCCHandler)this, super._source, this.$runtime, 170);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "smartWildcardDefaultBinding") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.action4();
                    this.$_ngcc_current_state = 16;
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "noMarshaller") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.action3();
                    this.$_ngcc_current_state = 13;
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "noUnmarshaller") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.action2();
                    this.$_ngcc_current_state = 10;
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "noValidator") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.action1();
                    this.$_ngcc_current_state = 7;
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "noValidatingUnmarshaller") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.action0();
                    this.$_ngcc_current_state = 4;
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 48: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "typesafeEnumBase")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 42;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 25: {
                int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0 || ($ai = this.$runtime.getAttributeIndex("", "parseMethod")) >= 0 || ($ai = this.$runtime.getAttributeIndex("", "printMethod")) >= 0) {
                    final NGCCHandler h = (NGCCHandler)new conversionBody((NGCCHandler)this, super._source, this.$runtime, 126);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 53: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "typesafeEnumMemberName")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 48;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 59: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "collectionType")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 53;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 42: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "bindingStyle")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 38;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 2: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "javaType") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 28;
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "serializable") {
                    final NGCCHandler h = (NGCCHandler)new serializable((NGCCHandler)this, super._source, this.$runtime, 177);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "superClass") {
                    final NGCCHandler h = (NGCCHandler)new superClass((NGCCHandler)this, super._source, this.$runtime, 178);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "typeSubstitution") {
                    final NGCCHandler h = (NGCCHandler)new typeSubstitution((NGCCHandler)this, super._source, this.$runtime, 179);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "smartWildcardDefaultBinding") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.action4();
                    this.$_ngcc_current_state = 16;
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "noMarshaller") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.action3();
                    this.$_ngcc_current_state = 13;
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "noUnmarshaller") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.action2();
                    this.$_ngcc_current_state = 10;
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "noValidator") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.action1();
                    this.$_ngcc_current_state = 7;
                    break;
                }
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "noValidatingUnmarshaller") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.action0();
                    this.$_ngcc_current_state = 4;
                    break;
                }
                this.$_ngcc_current_state = 1;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 28: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "xmlType")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 67: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "fixedAttributeAsConstantProperty")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 63;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 34: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "enableValidation")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 30;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 71: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "enableJavaNamingConventions")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 67;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 81: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "globalBindings") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.action15();
                    this.$_ngcc_current_state = 75;
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 0: {
                this.revertToParentFromEnterElement((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname, $attrs);
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
            case 63: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "generateIsSetMethod")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 59;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 75: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "underscoreBinding")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 71;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 13: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "noMarshaller") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 1;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 38: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "choiceContentProperty")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 34;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 7: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "noValidator") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 1;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 30: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "enableFailFastCheck")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 2;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 24: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "javaType") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 1;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 4: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "noValidatingUnmarshaller") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 1;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 1: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "globalBindings") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 0;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 48: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "typesafeEnumBase")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 42;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 25: {
                int $ai;
                if ((($ai = this.$runtime.getAttributeIndex("", "name")) >= 0 && $__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "javaType") || (($ai = this.$runtime.getAttributeIndex("", "parseMethod")) >= 0 && $__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "javaType") || (($ai = this.$runtime.getAttributeIndex("", "printMethod")) >= 0 && $__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "javaType")) {
                    final NGCCHandler h = (NGCCHandler)new conversionBody((NGCCHandler)this, super._source, this.$runtime, 126);
                    this.spawnChildFromLeaveElement((NGCCEventReceiver)h, $__uri, $__local, $__qname);
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 53: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "typesafeEnumMemberName")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 48;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 59: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "collectionType")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 53;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 10: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "noUnmarshaller") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 1;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 42: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "bindingStyle")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 38;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 67: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "fixedAttributeAsConstantProperty")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 63;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 28: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "xmlType")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 34: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "enableValidation")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 30;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 16: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "smartWildcardDefaultBinding") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 1;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 71: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "enableJavaNamingConventions")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 67;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 0: {
                this.revertToParentFromLeaveElement((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname);
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
            case 63: {
                if ($__uri == "" && $__local == "generateIsSetMethod") {
                    this.$_ngcc_current_state = 65;
                    break;
                }
                this.$_ngcc_current_state = 59;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 75: {
                if ($__uri == "" && $__local == "underscoreBinding") {
                    this.$_ngcc_current_state = 77;
                    break;
                }
                this.$_ngcc_current_state = 71;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 38: {
                if ($__uri == "" && $__local == "choiceContentProperty") {
                    this.$_ngcc_current_state = 40;
                    break;
                }
                this.$_ngcc_current_state = 34;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 30: {
                if ($__uri == "" && $__local == "enableFailFastCheck") {
                    this.$_ngcc_current_state = 32;
                    break;
                }
                this.$_ngcc_current_state = 2;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 48: {
                if ($__uri == "" && $__local == "typesafeEnumBase") {
                    this.$_ngcc_current_state = 51;
                    break;
                }
                this.$_ngcc_current_state = 42;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 25: {
                if (($__uri == "" && $__local == "name") || ($__uri == "" && $__local == "parseMethod") || ($__uri == "" && $__local == "printMethod")) {
                    final NGCCHandler h = (NGCCHandler)new conversionBody((NGCCHandler)this, super._source, this.$runtime, 126);
                    this.spawnChildFromEnterAttribute((NGCCEventReceiver)h, $__uri, $__local, $__qname);
                    break;
                }
                this.unexpectedEnterAttribute($__qname);
                break;
            }
            case 59: {
                if ($__uri == "" && $__local == "collectionType") {
                    this.$_ngcc_current_state = 61;
                    break;
                }
                this.$_ngcc_current_state = 53;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 53: {
                if ($__uri == "" && $__local == "typesafeEnumMemberName") {
                    this.$_ngcc_current_state = 55;
                    break;
                }
                this.$_ngcc_current_state = 48;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 42: {
                if ($__uri == "" && $__local == "bindingStyle") {
                    this.$_ngcc_current_state = 44;
                    break;
                }
                this.$_ngcc_current_state = 38;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 28: {
                if ($__uri == "" && $__local == "xmlType") {
                    this.$_ngcc_current_state = 27;
                    break;
                }
                this.unexpectedEnterAttribute($__qname);
                break;
            }
            case 67: {
                if ($__uri == "" && $__local == "fixedAttributeAsConstantProperty") {
                    this.$_ngcc_current_state = 69;
                    break;
                }
                this.$_ngcc_current_state = 63;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 34: {
                if ($__uri == "" && $__local == "enableValidation") {
                    this.$_ngcc_current_state = 36;
                    break;
                }
                this.$_ngcc_current_state = 30;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 71: {
                if ($__uri == "" && $__local == "enableJavaNamingConventions") {
                    this.$_ngcc_current_state = 73;
                    break;
                }
                this.$_ngcc_current_state = 67;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 0: {
                this.revertToParentFromEnterAttribute((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname);
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
            case 63: {
                this.$_ngcc_current_state = 59;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 64: {
                if ($__uri == "" && $__local == "generateIsSetMethod") {
                    this.$_ngcc_current_state = 59;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 30: {
                this.$_ngcc_current_state = 2;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 26: {
                if ($__uri == "" && $__local == "xmlType") {
                    this.$_ngcc_current_state = 25;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 59: {
                this.$_ngcc_current_state = 53;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 76: {
                if ($__uri == "" && $__local == "underscoreBinding") {
                    this.$_ngcc_current_state = 71;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 35: {
                if ($__uri == "" && $__local == "enableValidation") {
                    this.$_ngcc_current_state = 30;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 54: {
                if ($__uri == "" && $__local == "typesafeEnumMemberName") {
                    this.$_ngcc_current_state = 48;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 67: {
                this.$_ngcc_current_state = 63;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 39: {
                if ($__uri == "" && $__local == "choiceContentProperty") {
                    this.$_ngcc_current_state = 34;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 71: {
                this.$_ngcc_current_state = 67;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 68: {
                if ($__uri == "" && $__local == "fixedAttributeAsConstantProperty") {
                    this.$_ngcc_current_state = 63;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 75: {
                this.$_ngcc_current_state = 71;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 38: {
                this.$_ngcc_current_state = 34;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 60: {
                if ($__uri == "" && $__local == "collectionType") {
                    this.$_ngcc_current_state = 53;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 43: {
                if ($__uri == "" && $__local == "bindingStyle") {
                    this.$_ngcc_current_state = 38;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 31: {
                if ($__uri == "" && $__local == "enableFailFastCheck") {
                    this.$_ngcc_current_state = 2;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 48: {
                this.$_ngcc_current_state = 42;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 53: {
                this.$_ngcc_current_state = 48;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 42: {
                this.$_ngcc_current_state = 38;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 34: {
                this.$_ngcc_current_state = 30;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 0: {
                this.revertToParentFromLeaveAttribute((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 72: {
                if ($__uri == "" && $__local == "enableJavaNamingConventions") {
                    this.$_ngcc_current_state = 67;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 49: {
                if ($__uri == "" && $__local == "typesafeEnumBase") {
                    this.$_ngcc_current_state = 42;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
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
            case 63: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "generateIsSetMethod")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 59;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 32: {
                this.value = $value;
                this.$_ngcc_current_state = 31;
                this.action6();
                break;
            }
            case 27: {
                this.xmlType = $value;
                this.$_ngcc_current_state = 26;
                break;
            }
            case 30: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "enableFailFastCheck")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 2;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 44: {
                if ($value.equals("elementBinding")) {
                    this.$_ngcc_current_state = 43;
                    this.action10();
                    break;
                }
                if ($value.equals("modelGroupBinding")) {
                    this.$_ngcc_current_state = 43;
                    this.action9();
                    break;
                }
                break;
            }
            case 25: {
                int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "printMethod")) >= 0) {
                    final NGCCHandler h = (NGCCHandler)new conversionBody((NGCCHandler)this, super._source, this.$runtime, 126);
                    this.spawnChildFromText((NGCCEventReceiver)h, $value);
                    break;
                }
                if (($ai = this.$runtime.getAttributeIndex("", "parseMethod")) >= 0) {
                    final NGCCHandler h = (NGCCHandler)new conversionBody((NGCCHandler)this, super._source, this.$runtime, 126);
                    this.spawnChildFromText((NGCCEventReceiver)h, $value);
                    break;
                }
                if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
                    final NGCCHandler h = (NGCCHandler)new conversionBody((NGCCHandler)this, super._source, this.$runtime, 126);
                    this.spawnChildFromText((NGCCEventReceiver)h, $value);
                    break;
                }
                break;
            }
            case 59: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "collectionType")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 53;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 69: {
                this.fixedAttrToConstantProperty = $value;
                this.$_ngcc_current_state = 68;
                break;
            }
            case 67: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "fixedAttributeAsConstantProperty")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 63;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 71: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "enableJavaNamingConventions")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 67;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 75: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "underscoreBinding")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 71;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 38: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "choiceContentProperty")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 34;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 51: {
                this.__text = $value;
                this.$_ngcc_current_state = 50;
                this.action12();
                break;
            }
            case 40: {
                this.value = $value;
                this.$_ngcc_current_state = 39;
                this.action8();
                break;
            }
            case 61: {
                final NGCCHandler h = new CollectionTypeState(this, super._source, this.$runtime, 225);
                this.spawnChildFromText((NGCCEventReceiver)h, $value);
                break;
            }
            case 48: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "typesafeEnumBase")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 42;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 53: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "typesafeEnumMemberName")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 48;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 65: {
                this.needIsSetMethod = $value;
                this.$_ngcc_current_state = 64;
                break;
            }
            case 42: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "bindingStyle")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 38;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 28: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "xmlType")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                break;
            }
            case 34: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "enableValidation")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 30;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 36: {
                this.value = $value;
                this.$_ngcc_current_state = 35;
                this.action7();
                break;
            }
            case 50: {
                this.value = $value;
                this.$_ngcc_current_state = 49;
                this.action11();
                break;
            }
            case 55: {
                if ($value.equals("generateError")) {
                    this.$_ngcc_current_state = 54;
                    break;
                }
                if ($value.equals("generateName")) {
                    this.$_ngcc_current_state = 54;
                    this.action13();
                    break;
                }
                break;
            }
            case 73: {
                this.enableJavaNamingConvention = $value;
                this.$_ngcc_current_state = 72;
                break;
            }
            case 77: {
                if ($value.equals("asWordSeparator")) {
                    this.$_ngcc_current_state = 76;
                    break;
                }
                if ($value.equals("asCharInWord")) {
                    this.$_ngcc_current_state = 76;
                    this.action14();
                    break;
                }
                break;
            }
            case 0: {
                this.revertToParentFromText((Object)this.makeResult(), super._cookie, $value);
                break;
            }
            case 49: {
                this.value = $value;
                this.$_ngcc_current_state = 49;
                this.action11();
                break;
            }
        }
    }
    
    public void onChildCompleted(final Object $__result__, final int $__cookie__, final boolean $__needAttCheck__) throws SAXException {
        switch ($__cookie__) {
            case 168: {
                this.xSerializable = (BIXSerializable)$__result__;
                this.$_ngcc_current_state = 1;
                break;
            }
            case 169: {
                this.xSuperClass = (BIXSuperClass)$__result__;
                this.$_ngcc_current_state = 1;
                break;
            }
            case 170: {
                this.xTypeSubstitution = (boolean)$__result__;
                this.$_ngcc_current_state = 1;
                break;
            }
            case 177: {
                this.xSerializable = (BIXSerializable)$__result__;
                this.$_ngcc_current_state = 1;
                break;
            }
            case 178: {
                this.xSuperClass = (BIXSuperClass)$__result__;
                this.$_ngcc_current_state = 1;
                break;
            }
            case 179: {
                this.xTypeSubstitution = (boolean)$__result__;
                this.$_ngcc_current_state = 1;
                break;
            }
            case 126: {
                this.conv = (BIConversion)$__result__;
                this.action5();
                this.$_ngcc_current_state = 24;
                break;
            }
            case 225: {
                this.ct = (FieldRendererFactory)$__result__;
                this.$_ngcc_current_state = 60;
                break;
            }
        }
    }
    
    public boolean accepted() {
        return this.$_ngcc_current_state == 0;
    }
    
    public BIGlobalBinding makeResult() {
        if (this.enumBaseTypes.size() == 0) {
            this.enumBaseTypes.add(new QName("http://www.w3.org/2001/XMLSchema", "NCName"));
        }
        return new BIGlobalBinding(this.$runtime.codeModel, this.globalConvs, this.nameConverter, this.modelGroupBinding, this.choiceContentPropertyWithModelGroupBinding, this.$runtime.parseBoolean(this.enableJavaNamingConvention), this.$runtime.parseBoolean(this.fixedAttrToConstantProperty), this.$runtime.parseBoolean(this.needIsSetMethod), this.generateEnumMemberName, this.enumBaseTypes, this.ct, this.xSerializable, this.xSuperClass, this.xTypeSubstitution, this.xSmartWildcardDefaultBinding, this.loc);
    }
}
