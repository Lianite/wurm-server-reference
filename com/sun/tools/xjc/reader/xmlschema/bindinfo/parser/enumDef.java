// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo.parser;

import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIEnum;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIEnumMember;
import org.xml.sax.Locator;
import java.util.HashMap;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.NGCCRuntimeEx;

class enumDef extends NGCCHandler
{
    private String jname;
    private String name;
    private String javadoc;
    private String value;
    protected final NGCCRuntimeEx $runtime;
    private int $_ngcc_current_state;
    protected String $uri;
    protected String $localName;
    protected String $qname;
    private HashMap members;
    private Locator loc;
    private Locator loc2;
    
    public final NGCCRuntime getRuntime() {
        return (NGCCRuntime)this.$runtime;
    }
    
    public enumDef(final NGCCHandler parent, final NGCCEventSource source, final NGCCRuntimeEx runtime, final int cookie) {
        super(source, parent, cookie);
        this.members = new HashMap();
        this.$runtime = runtime;
        this.$_ngcc_current_state = 22;
    }
    
    public enumDef(final NGCCRuntimeEx runtime) {
        this(null, (NGCCEventSource)runtime, runtime, -1);
    }
    
    private void action0() throws SAXException {
        this.members.put(this.value, new BIEnumMember(this.loc2, this.jname, this.javadoc));
    }
    
    private void action1() throws SAXException {
        this.loc2 = this.$runtime.copyLocator();
    }
    
    private void action2() throws SAXException {
        this.jname = null;
        this.javadoc = null;
    }
    
    private void action3() throws SAXException {
        this.loc = this.$runtime.copyLocator();
    }
    
    public void enterElement(final String $__uri, final String $__local, final String $__qname, final Attributes $attrs) throws SAXException {
        this.$uri = $__uri;
        this.$localName = $__local;
        this.$qname = $__qname;
        switch (this.$_ngcc_current_state) {
            case 22: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "typesafeEnumClass") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.action3();
                    this.$_ngcc_current_state = 18;
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 0: {
                this.revertToParentFromEnterElement((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 4: {
                this.action0();
                this.$_ngcc_current_state = 3;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 2: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "typesafeEnumMember") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.action2();
                    this.action1();
                    this.$_ngcc_current_state = 10;
                    break;
                }
                this.$_ngcc_current_state = 1;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 16: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "javadoc") {
                    final NGCCHandler h = (NGCCHandler)new javadoc((NGCCHandler)this, super._source, this.$runtime, 447);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 2;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 1: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "typesafeEnumMember") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.action2();
                    this.action1();
                    this.$_ngcc_current_state = 10;
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 10: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 9;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 18: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 16;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 5: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "javadoc") {
                    final NGCCHandler h = (NGCCHandler)new javadoc((NGCCHandler)this, super._source, this.$runtime, 431);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 4;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 9: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "value")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
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
                this.revertToParentFromLeaveElement((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 4: {
                this.action0();
                this.$_ngcc_current_state = 3;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 16: {
                this.$_ngcc_current_state = 2;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 1: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "typesafeEnumClass") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 0;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 10: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 9;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 18: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 16;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 5: {
                this.$_ngcc_current_state = 4;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 3: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "typesafeEnumMember") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 1;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 9: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "value")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
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
            case 0: {
                this.revertToParentFromEnterAttribute((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 4: {
                this.action0();
                this.$_ngcc_current_state = 3;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 16: {
                this.$_ngcc_current_state = 2;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 10: {
                if ($__uri == "" && $__local == "name") {
                    this.$_ngcc_current_state = 12;
                    break;
                }
                this.$_ngcc_current_state = 9;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 18: {
                if ($__uri == "" && $__local == "name") {
                    this.$_ngcc_current_state = 20;
                    break;
                }
                this.$_ngcc_current_state = 16;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 5: {
                this.$_ngcc_current_state = 4;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 9: {
                if ($__uri == "" && $__local == "value") {
                    this.$_ngcc_current_state = 8;
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
            case 0: {
                this.revertToParentFromLeaveAttribute((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 4: {
                this.action0();
                this.$_ngcc_current_state = 3;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 19: {
                if ($__uri == "" && $__local == "name") {
                    this.$_ngcc_current_state = 16;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 7: {
                if ($__uri == "" && $__local == "value") {
                    this.$_ngcc_current_state = 5;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 16: {
                this.$_ngcc_current_state = 2;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 11: {
                if ($__uri == "" && $__local == "name") {
                    this.$_ngcc_current_state = 9;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 10: {
                this.$_ngcc_current_state = 9;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 18: {
                this.$_ngcc_current_state = 16;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 5: {
                this.$_ngcc_current_state = 4;
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
            case 12: {
                this.jname = $value;
                this.$_ngcc_current_state = 11;
                break;
            }
            case 0: {
                this.revertToParentFromText((Object)this.makeResult(), super._cookie, $value);
                break;
            }
            case 4: {
                this.action0();
                this.$_ngcc_current_state = 3;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 16: {
                this.$_ngcc_current_state = 2;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 20: {
                this.name = $value;
                this.$_ngcc_current_state = 19;
                break;
            }
            case 10: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 9;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 8: {
                this.value = $value;
                this.$_ngcc_current_state = 7;
                break;
            }
            case 18: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 16;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 5: {
                this.$_ngcc_current_state = 4;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 9: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "value")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                break;
            }
        }
    }
    
    public void onChildCompleted(final Object $__result__, final int $__cookie__, final boolean $__needAttCheck__) throws SAXException {
        switch ($__cookie__) {
            case 447: {
                this.javadoc = (String)$__result__;
                this.$_ngcc_current_state = 2;
                break;
            }
            case 431: {
                this.javadoc = (String)$__result__;
                this.$_ngcc_current_state = 4;
                break;
            }
        }
    }
    
    public boolean accepted() {
        return this.$_ngcc_current_state == 0;
    }
    
    private BIEnum makeResult() {
        return new BIEnum(this.loc, this.name, this.javadoc, this.members);
    }
}
