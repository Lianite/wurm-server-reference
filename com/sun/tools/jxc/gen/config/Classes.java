// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.jxc.gen.config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.util.ArrayList;
import java.util.List;
import com.sun.tools.jxc.NGCCRuntimeEx;

public class Classes extends NGCCHandler
{
    private String __text;
    private String exclude_content;
    private String include_content;
    protected final NGCCRuntimeEx $runtime;
    private int $_ngcc_current_state;
    protected String $uri;
    protected String $localName;
    protected String $qname;
    private List includes;
    private List excludes;
    
    public final NGCCRuntime getRuntime() {
        return this.$runtime;
    }
    
    public Classes(final NGCCHandler parent, final NGCCEventSource source, final NGCCRuntimeEx runtime, final int cookie) {
        super(source, parent, cookie);
        this.includes = new ArrayList();
        this.excludes = new ArrayList();
        this.$runtime = runtime;
        this.$_ngcc_current_state = 12;
    }
    
    public Classes(final NGCCRuntimeEx runtime) {
        this(null, runtime, runtime, -1);
    }
    
    private void action0() throws SAXException {
        this.excludes.add(this.exclude_content);
    }
    
    private void action1() throws SAXException {
        this.$runtime.processList(this.__text);
    }
    
    private void action2() throws SAXException {
        this.includes.add(this.include_content);
    }
    
    private void action3() throws SAXException {
        this.$runtime.processList(this.__text);
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
            case 2: {
                if ($__uri == "" && $__local == "excludes") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 6;
                    break;
                }
                this.$_ngcc_current_state = 1;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 11: {
                if ($__uri == "" && $__local == "includes") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 10;
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 4: {
                this.$_ngcc_current_state = 3;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 12: {
                if ($__uri == "" && $__local == "classes") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 11;
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
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 8: {
                if ($__uri == "" && $__local == "includes") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 2;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 3: {
                if ($__uri == "" && $__local == "excludes") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 1;
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
            case 1: {
                if ($__uri == "" && $__local == "classes") {
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
            case 0: {
                this.revertToParentFromEnterAttribute(this, super._cookie, $__uri, $__local, $__qname);
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
            case 0: {
                this.revertToParentFromText(this, super._cookie, $value);
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 8: {
                this.include_content = $value;
                this.$_ngcc_current_state = 8;
                this.action2();
                break;
            }
            case 6: {
                this.__text = $value;
                this.$_ngcc_current_state = 4;
                this.action1();
                break;
            }
            case 3: {
                this.exclude_content = $value;
                this.$_ngcc_current_state = 3;
                this.action0();
                break;
            }
            case 10: {
                this.__text = $value;
                this.$_ngcc_current_state = 9;
                this.action3();
                break;
            }
            case 9: {
                this.include_content = $value;
                this.$_ngcc_current_state = 8;
                this.action2();
                break;
            }
            case 4: {
                this.exclude_content = $value;
                this.$_ngcc_current_state = 3;
                this.action0();
                break;
            }
        }
    }
    
    public void onChildCompleted(final Object $__result__, final int $__cookie__, final boolean $__needAttCheck__) throws SAXException {
    }
    
    public boolean accepted() {
        return this.$_ngcc_current_state == 0;
    }
    
    public List getIncludes() {
        return this.$runtime.getIncludePatterns(this.includes);
    }
    
    public List getExcludes() {
        return this.$runtime.getExcludePatterns(this.excludes);
    }
}
