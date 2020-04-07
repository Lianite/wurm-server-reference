// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.jxc.gen.config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import com.sun.tools.jxc.NGCCRuntimeEx;

public class Config extends NGCCHandler
{
    private String bd;
    private Schema _schema;
    protected final NGCCRuntimeEx $runtime;
    private int $_ngcc_current_state;
    protected String $uri;
    protected String $localName;
    protected String $qname;
    private File baseDir;
    private Classes classes;
    private List schema;
    
    public final NGCCRuntime getRuntime() {
        return this.$runtime;
    }
    
    public Config(final NGCCHandler parent, final NGCCEventSource source, final NGCCRuntimeEx runtime, final int cookie) {
        super(source, parent, cookie);
        this.schema = new ArrayList();
        this.$runtime = runtime;
        this.$_ngcc_current_state = 8;
    }
    
    public Config(final NGCCRuntimeEx runtime) {
        this(null, runtime, runtime, -1);
    }
    
    private void action0() throws SAXException {
        this.schema.add(this._schema);
    }
    
    private void action1() throws SAXException {
        this.baseDir = this.$runtime.getBaseDir(this.bd);
    }
    
    public void enterElement(final String $__uri, final String $__local, final String $__qname, final Attributes $attrs) throws SAXException {
        this.$uri = $__uri;
        this.$localName = $__local;
        this.$qname = $__qname;
        switch (this.$_ngcc_current_state) {
            case 0: {
                this.revertToParentFromEnterElement(this, super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 1: {
                if ($__uri == "" && $__local == "schema") {
                    final NGCCHandler h = new Schema(this, super._source, this.$runtime, 19, this.baseDir);
                    this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 2: {
                if ($__uri == "" && $__local == "schema") {
                    final NGCCHandler h = new Schema(this, super._source, this.$runtime, 20, this.baseDir);
                    this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 1;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 8: {
                if ($__uri == "" && $__local == "config") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 7;
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 7: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "baseDir")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 4: {
                if ($__uri == "" && $__local == "classes") {
                    final NGCCHandler h = new Classes(this, super._source, this.$runtime, 22);
                    this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
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
                this.revertToParentFromLeaveElement(this, super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 1: {
                if ($__uri == "" && $__local == "config") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 0;
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
            case 7: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "baseDir")) >= 0) {
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
                this.revertToParentFromEnterAttribute(this, super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 7: {
                if ($__uri == "" && $__local == "baseDir") {
                    this.$_ngcc_current_state = 6;
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
                this.revertToParentFromLeaveAttribute(this, super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 5: {
                if ($__uri == "" && $__local == "baseDir") {
                    this.$_ngcc_current_state = 4;
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
            default: {
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
        }
    }
    
    public void text(final String $value) throws SAXException {
        switch (this.$_ngcc_current_state) {
            case 0: {
                this.revertToParentFromText(this, super._cookie, $value);
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 7: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "baseDir")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                break;
            }
            case 6: {
                this.bd = $value;
                this.$_ngcc_current_state = 5;
                this.action1();
                break;
            }
        }
    }
    
    public void onChildCompleted(final Object $__result__, final int $__cookie__, final boolean $__needAttCheck__) throws SAXException {
        switch ($__cookie__) {
            case 19: {
                this._schema = (Schema)$__result__;
                this.action0();
                this.$_ngcc_current_state = 1;
                break;
            }
            case 20: {
                this._schema = (Schema)$__result__;
                this.action0();
                this.$_ngcc_current_state = 1;
                break;
            }
            case 22: {
                this.classes = (Classes)$__result__;
                this.$_ngcc_current_state = 2;
                break;
            }
        }
    }
    
    public boolean accepted() {
        return this.$_ngcc_current_state == 0;
    }
    
    public Classes getClasses() {
        return this.classes;
    }
    
    public File getBaseDir() {
        return this.baseDir;
    }
    
    public List getSchema() {
        return this.schema;
    }
}
