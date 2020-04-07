// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo.parser;

import com.sun.codemodel.JType;
import com.sun.tools.xjc.grammar.xducer.IdentityTransducer;
import org.xml.sax.SAXParseException;
import com.sun.tools.xjc.grammar.xducer.Transducer;
import com.sun.tools.xjc.grammar.xducer.FacadeTransducer;
import com.sun.tools.xjc.grammar.xducer.UserTransducer;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.MagicTransducer;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIConversion;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.NGCCRuntimeEx;

class conversionBody extends NGCCHandler
{
    private String _context;
    protected final NGCCRuntimeEx $runtime;
    private int $_ngcc_current_state;
    protected String $uri;
    protected String $localName;
    protected String $qname;
    private String type;
    private String parse;
    private String print;
    private boolean context;
    
    public final NGCCRuntime getRuntime() {
        return (NGCCRuntime)this.$runtime;
    }
    
    public conversionBody(final NGCCHandler parent, final NGCCEventSource source, final NGCCRuntimeEx runtime, final int cookie) {
        super(source, parent, cookie);
        this.type = "java.lang.String";
        this.parse = null;
        this.print = null;
        this.context = false;
        this.$runtime = runtime;
        this.$_ngcc_current_state = 12;
    }
    
    public conversionBody(final NGCCRuntimeEx runtime) {
        this(null, (NGCCEventSource)runtime, runtime, -1);
    }
    
    private void action0() throws SAXException {
        this.context = this.$runtime.parseBoolean(this._context);
    }
    
    public void enterElement(final String $__uri, final String $__local, final String $__qname, final Attributes $attrs) throws SAXException {
        this.$uri = $__uri;
        this.$localName = $__local;
        this.$qname = $__qname;
        switch (this.$_ngcc_current_state) {
            case 0: {
                this.revertToParentFromEnterElement((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 7: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 8: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "printMethod")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 7;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 1: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "hasNsContext")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 0;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 12: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "parseMethod")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 8;
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
            case 0: {
                this.revertToParentFromLeaveElement((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 7: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 8: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "printMethod")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 7;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 1: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "hasNsContext")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 0;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 12: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "parseMethod")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 8;
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
            case 0: {
                this.revertToParentFromEnterAttribute((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 7: {
                if ($__uri == "" && $__local == "name") {
                    this.$_ngcc_current_state = 6;
                    break;
                }
                this.unexpectedEnterAttribute($__qname);
                break;
            }
            case 8: {
                if ($__uri == "" && $__local == "printMethod") {
                    this.$_ngcc_current_state = 10;
                    break;
                }
                this.$_ngcc_current_state = 7;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 1: {
                if ($__uri == "" && $__local == "hasNsContext") {
                    this.$_ngcc_current_state = 3;
                    break;
                }
                this.$_ngcc_current_state = 0;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 12: {
                if ($__uri == "" && $__local == "parseMethod") {
                    this.$_ngcc_current_state = 14;
                    break;
                }
                this.$_ngcc_current_state = 8;
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
            case 13: {
                if ($__uri == "" && $__local == "parseMethod") {
                    this.$_ngcc_current_state = 8;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 0: {
                this.revertToParentFromLeaveAttribute((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 2: {
                if ($__uri == "" && $__local == "hasNsContext") {
                    this.$_ngcc_current_state = 0;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 5: {
                if ($__uri == "" && $__local == "name") {
                    this.$_ngcc_current_state = 1;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 9: {
                if ($__uri == "" && $__local == "printMethod") {
                    this.$_ngcc_current_state = 7;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 8: {
                this.$_ngcc_current_state = 7;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 1: {
                this.$_ngcc_current_state = 0;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 12: {
                this.$_ngcc_current_state = 8;
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
            case 10: {
                this.print = $value;
                this.$_ngcc_current_state = 9;
                break;
            }
            case 14: {
                this.parse = $value;
                this.$_ngcc_current_state = 13;
                break;
            }
            case 6: {
                this.type = $value;
                this.$_ngcc_current_state = 5;
                break;
            }
            case 0: {
                this.revertToParentFromText((Object)this.makeResult(), super._cookie, $value);
                break;
            }
            case 7: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                break;
            }
            case 8: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "printMethod")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 7;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 3: {
                this._context = $value;
                this.$_ngcc_current_state = 2;
                this.action0();
                break;
            }
            case 1: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "hasNsContext")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 0;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 12: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "parseMethod")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 8;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
        }
    }
    
    public void onChildCompleted(final Object $__result__, final int $__cookie__, final boolean $__needAttCheck__) throws SAXException {
    }
    
    public boolean accepted() {
        return this.$_ngcc_current_state == 1 || this.$_ngcc_current_state == 0;
    }
    
    public BIConversion makeResult() throws SAXException {
        Transducer xducer = null;
        MagicTransducer magic = null;
        try {
            final JType typeObj = this.$runtime.getType(this.type);
            if (this.print == null || this.parse == null) {
                magic = new MagicTransducer(typeObj);
            }
            if (this.print != null || this.parse != null) {
                xducer = new UserTransducer(typeObj, (this.parse != null) ? this.parse : "new", (this.print != null) ? this.print : "toString", this.context);
            }
            if (this.print == null && this.parse == null) {
                xducer = magic;
            }
            if (this.print == null && this.parse != null) {
                xducer = new FacadeTransducer(magic, xducer);
            }
            if (this.print != null && this.parse == null) {
                xducer = new FacadeTransducer(xducer, magic);
            }
        }
        catch (IllegalArgumentException e) {
            this.$runtime.errorHandler.error(new SAXParseException(e.getMessage(), this.$runtime.getLocator()));
            xducer = new IdentityTransducer(this.$runtime.codeModel);
        }
        final BIConversion r = new BIConversion(this.$runtime.copyLocator(), xducer);
        if (magic != null) {
            magic.setParent(r);
        }
        return r;
    }
}
