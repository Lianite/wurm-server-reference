// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.writer;

import com.sun.tools.xjc.grammar.FieldItem;
import com.sun.tools.xjc.grammar.IgnoreItem;
import com.sun.tools.xjc.grammar.SuperClassItem;
import com.sun.tools.xjc.grammar.PrimitiveItem;
import com.sun.tools.xjc.grammar.ExternalItem;
import com.sun.tools.xjc.grammar.ClassCandidateItem;
import com.sun.tools.xjc.grammar.JavaItem;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.tools.xjc.grammar.JavaItemVisitor;
import com.sun.msv.grammar.NameClassVisitor;
import com.sun.msv.grammar.NameClass;
import com.sun.tools.xjc.grammar.FieldUse;
import java.util.Vector;
import com.sun.tools.xjc.grammar.InterfaceItem;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.msv.writer.SAXRuntimeException;
import com.sun.msv.grammar.ExpressionVisitorVoid;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import org.xml.sax.SAXException;
import com.sun.xml.bind.JAXBAssertionError;
import org.xml.sax.DocumentHandler;
import java.io.OutputStream;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.msv.grammar.Grammar;
import java.util.HashSet;
import com.sun.msv.writer.relaxng.PatternWriter;
import com.sun.msv.writer.XMLWriter;
import java.util.Set;
import com.sun.msv.writer.relaxng.Context;
import com.sun.msv.writer.GrammarWriter;

public class Writer implements GrammarWriter, Context
{
    private final boolean noNS;
    private final boolean signatureOnly;
    private final Set candidates;
    private final XMLWriter writer;
    private final PatternWriter patternWriter;
    
    public Writer(final boolean _noNS, final boolean _sigOnly) {
        this.candidates = new HashSet();
        this.writer = new XMLWriter();
        this.patternWriter = (PatternWriter)new SmartWriter(this, (Context)this);
        this.noNS = _noNS;
        this.signatureOnly = _sigOnly;
    }
    
    public static void writeToConsole(final boolean noNS, final Grammar grammar) {
        writeToConsole(noNS, false, grammar);
    }
    
    public static void writeToConsole(final boolean noNS, final boolean signatureOnly, final Grammar grammar) {
        try {
            final Writer w = new Writer(noNS, signatureOnly);
            final OutputFormat format = new OutputFormat("xml", null, true);
            format.setIndent(1);
            w.setDocumentHandler((DocumentHandler)new XMLSerializer(System.out, format));
            w.write(grammar);
        }
        catch (SAXException e) {
            throw new JAXBAssertionError((Throwable)e);
        }
    }
    
    public XMLWriter getWriter() {
        return this.writer;
    }
    
    public String getTargetNamespace() {
        return null;
    }
    
    public void setDocumentHandler(final DocumentHandler handler) {
        this.writer.setDocumentHandler(handler);
    }
    
    public void write(final Grammar g) throws SAXException {
        this.write((AnnotatedGrammar)g);
    }
    
    public void write(final AnnotatedGrammar grammar) throws SAXException {
        try {
            final DocumentHandler handler = this.writer.getDocumentHandler();
            handler.setDocumentLocator(new LocatorImpl());
            handler.startDocument();
            this.writer.start("bgm");
            this.writer.start("root");
            grammar.getTopLevel().visit((ExpressionVisitorVoid)this.patternWriter);
            this.writer.end("root");
            final ClassItem[] cs = grammar.getClasses();
            for (int i = 0; i < cs.length; ++i) {
                this.writeClass(cs[i]);
            }
            final InterfaceItem[] is = grammar.getInterfaces();
            for (int j = 0; j < is.length; ++j) {
                this.writeInterface(is[j]);
            }
            this.writer.end("bgm");
            handler.endDocument();
        }
        catch (SAXRuntimeException sre) {
            throw sre.e;
        }
    }
    
    private void writeClass(final ClassItem item) {
        this.writer.start("class", new String[] { "name", item.getType().fullName() });
        if (item.getSuperClass() != null) {
            this.writer.element("extends", new String[] { "name", item.getSuperClass().name });
        }
        this.writer.start("field-summary");
        final FieldUse[] fus = item.getDeclaredFieldUses();
        for (int i = 0; i < fus.length; ++i) {
            final FieldUse fu = fus[i];
            final Vector vec = new Vector();
            vec.add("name");
            vec.add(fu.name);
            vec.add("type");
            vec.add(fu.type.name());
            vec.add("occurs");
            vec.add(fu.multiplicity.toString());
            if (fu.getRealization() != null) {
                vec.add("realization");
                vec.add(fu.getRealization().getClass().getName());
            }
            this.writer.element("field", (String[])vec.toArray(new String[0]));
        }
        this.writer.end("field-summary");
        if (!this.signatureOnly) {
            this.patternWriter.visitUnary(item.exp);
        }
        this.writer.end("class");
    }
    
    private void writeInterface(final InterfaceItem item) {
        this.writer.start("interface", new String[] { "name", item.name });
        this.patternWriter.visitUnary(item.exp);
        this.writer.end("interface");
    }
    
    public void writeNameClass(final NameClass nc) {
        nc.visit((NameClassVisitor)new Writer$1(this));
    }
    
    private class SmartWriter extends PatternWriter implements JavaItemVisitor
    {
        SmartWriter(final Writer this$0, final Context context) {
            super(context);
            this.this$0 = this$0;
        }
        
        public void onRef(final ReferenceExp exp) {
            if (exp.name == null) {
                exp.exp.visit((ExpressionVisitorVoid)this);
            }
            else {
                this.writer.start("ref", new String[] { "name", exp.name });
                exp.exp.visit((ExpressionVisitorVoid)this);
                this.writer.end("ref");
            }
        }
        
        public void onOther(final OtherExp exp) {
            if (exp instanceof JavaItem) {
                ((JavaItem)exp).visitJI(this);
                return;
            }
            if (exp instanceof ClassCandidateItem) {
                final boolean isNew = this.this$0.candidates.add(exp);
                this.writer.start("class-candidate", new String[] { isNew ? "name" : "ref", ((ClassCandidateItem)exp).name });
                if (isNew) {
                    exp.exp.visit((ExpressionVisitorVoid)this);
                }
                this.writer.end("class-candidate");
                return;
            }
            exp.exp.visit((ExpressionVisitorVoid)this);
        }
        
        public Object onClass(final ClassItem item) {
            this.writer.element("class-ref", new String[] { "name", item.name });
            return null;
        }
        
        public Object onInterface(final InterfaceItem item) {
            this.writer.element("interface-ref", new String[] { "interface", item.name });
            return null;
        }
        
        public Object onExternal(final ExternalItem item) {
            this.writer.start("external", new String[] { "type", item.toString() });
            this.this$0.writeNameClass(item.elementName);
            this.writer.end("external");
            return null;
        }
        
        private String getClassName(final Object o) {
            final String name = o.getClass().getName();
            final int idx = name.lastIndexOf(46);
            if (idx < 0) {
                return name;
            }
            return name.substring(idx + 1);
        }
        
        public Object onPrimitive(final PrimitiveItem item) {
            this.writer.start("primitive", new String[] { "type", item.xducer.toString() });
            this.visitUnary(item.exp);
            this.writer.end("primitive");
            return null;
        }
        
        public Object onSuper(final SuperClassItem item) {
            this.writer.start("superClass");
            this.visitUnary(item.exp);
            this.writer.end("superClass");
            return null;
        }
        
        public Object onIgnore(final IgnoreItem item) {
            this.writer.start("ignore");
            this.writer.end("ignore");
            return null;
        }
        
        public Object onField(final FieldItem item) {
            final Vector vec = new Vector();
            vec.add("name");
            vec.add(item.name);
            if (item.defaultValues != null) {
                vec.add("hasDefaultValue");
                vec.add("true");
            }
            this.writer.start("field", (String[])vec.toArray(new String[vec.size()]));
            this.visitUnary(item.exp);
            this.writer.end("field");
            return null;
        }
    }
}
