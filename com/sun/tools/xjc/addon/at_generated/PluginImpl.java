// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.addon.at_generated;

import java.util.Date;
import java.text.SimpleDateFormat;
import com.sun.tools.xjc.Driver;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JAnnotatable;
import java.util.Iterator;
import com.sun.tools.xjc.outline.EnumOutline;
import com.sun.tools.xjc.outline.ClassOutline;
import org.xml.sax.ErrorHandler;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.Outline;
import com.sun.codemodel.JClass;
import com.sun.tools.xjc.Plugin;

public class PluginImpl extends Plugin
{
    private JClass annotation;
    private String date;
    
    public PluginImpl() {
        this.date = null;
    }
    
    public String getOptionName() {
        return "mark-generated";
    }
    
    public String getUsage() {
        return "  -mark-generated    :  mark the generated code as @javax.annotation.Generated";
    }
    
    public boolean run(final Outline model, final Options opt, final ErrorHandler errorHandler) {
        this.annotation = model.getCodeModel().ref("javax.annotation.Generated");
        for (final ClassOutline co : model.getClasses()) {
            this.augument(co);
        }
        for (final EnumOutline eo : model.getEnums()) {
            this.augument(eo);
        }
        return true;
    }
    
    private void augument(final EnumOutline eo) {
        this.annotate(eo.clazz);
    }
    
    private void augument(final ClassOutline co) {
        this.annotate(co.implClass);
        for (final JMethod m : co.implClass.methods()) {
            this.annotate(m);
        }
        for (final JFieldVar f : co.implClass.fields().values()) {
            this.annotate(f);
        }
    }
    
    private void annotate(final JAnnotatable m) {
        m.annotate(this.annotation).param("value", Driver.class.getName()).param("date", this.getISO8601Date()).param("comments", "JAXB RI v" + Options.getBuildID());
    }
    
    private String getISO8601Date() {
        if (this.date == null) {
            final StringBuffer tstamp = new StringBuffer();
            tstamp.append(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ").format(new Date()));
            tstamp.insert(tstamp.length() - 2, ':');
            this.date = tstamp.toString();
        }
        return this.date;
    }
}
