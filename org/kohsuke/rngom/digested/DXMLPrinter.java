// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

import java.util.List;
import org.w3c.dom.Element;
import javax.xml.namespace.QName;
import org.kohsuke.rngom.nc.SimpleNameClass;
import java.util.Iterator;
import java.io.OutputStream;
import org.kohsuke.rngom.parse.Parseable;
import org.xml.sax.ErrorHandler;
import org.kohsuke.rngom.ast.builder.BuildException;
import javax.xml.stream.XMLOutputFactory;
import java.io.FileOutputStream;
import org.kohsuke.rngom.ast.builder.SchemaBuilder;
import org.kohsuke.rngom.ast.util.CheckingSchemaBuilder;
import org.kohsuke.rngom.parse.compact.CompactParseable;
import org.kohsuke.rngom.parse.xml.SAXParseable;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import org.w3c.dom.Node;
import org.kohsuke.rngom.nc.NameClassVisitor;
import org.kohsuke.rngom.nc.NameClass;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class DXMLPrinter
{
    protected XMLStreamWriter out;
    protected String indentStep;
    protected String newLine;
    protected int indent;
    protected boolean afterEnd;
    protected DXMLPrinterVisitor visitor;
    protected NameClassXMLPrinterVisitor ncVisitor;
    protected DOMPrinter domPrinter;
    
    public DXMLPrinter(final XMLStreamWriter out) {
        this.indentStep = "\t";
        this.newLine = System.getProperty("line.separator");
        this.afterEnd = false;
        this.out = out;
        this.visitor = new DXMLPrinterVisitor();
        this.ncVisitor = new NameClassXMLPrinterVisitor();
        this.domPrinter = new DOMPrinter(out);
    }
    
    public void printDocument(final DGrammarPattern grammar) throws XMLStreamException {
        try {
            this.visitor.startDocument();
            this.visitor.on(grammar);
            this.visitor.endDocument();
        }
        catch (XMLWriterException e) {
            throw (XMLStreamException)e.getCause();
        }
    }
    
    public void print(final DPattern pattern) throws XMLStreamException {
        try {
            pattern.accept((DPatternVisitor<Object>)this.visitor);
        }
        catch (XMLWriterException e) {
            throw (XMLStreamException)e.getCause();
        }
    }
    
    public void print(final NameClass nc) throws XMLStreamException {
        try {
            nc.accept((NameClassVisitor<Object>)this.ncVisitor);
        }
        catch (XMLWriterException e) {
            throw (XMLStreamException)e.getCause();
        }
    }
    
    public void print(final Node node) throws XMLStreamException {
        this.domPrinter.print(node);
    }
    
    public static void main(final String[] args) throws Exception {
        final ErrorHandler eh = new DefaultHandler() {
            public void error(final SAXParseException e) throws SAXException {
                throw e;
            }
        };
        Parseable p;
        if (args[0].endsWith(".rng")) {
            p = new SAXParseable(new InputSource(args[0]), eh);
        }
        else {
            p = new CompactParseable(new InputSource(args[0]), eh);
        }
        final SchemaBuilder sb = new CheckingSchemaBuilder(new DSchemaBuilderImpl(), eh);
        try {
            final DGrammarPattern grammar = p.parse((SchemaBuilder<?, DGrammarPattern, ?, ?, ?, ?>)sb);
            final OutputStream out = new FileOutputStream(args[1]);
            final XMLOutputFactory factory = XMLOutputFactory.newInstance();
            final XMLStreamWriter output = factory.createXMLStreamWriter(out);
            final DXMLPrinter printer = new DXMLPrinter(output);
            printer.printDocument(grammar);
            output.close();
            out.close();
        }
        catch (BuildException e) {
            if (e.getCause() instanceof SAXParseException) {
                final SAXParseException se = (SAXParseException)e.getCause();
                System.out.println("(" + se.getLineNumber() + "," + se.getColumnNumber() + "): " + se.getMessage());
                return;
            }
            if (e.getCause() instanceof SAXException) {
                final SAXException se2 = (SAXException)e.getCause();
                if (se2.getException() != null) {
                    se2.getException().printStackTrace();
                }
            }
            throw e;
        }
    }
    
    protected class XMLWriterException extends RuntimeException
    {
        protected XMLWriterException(final Throwable cause) {
            super(cause);
        }
    }
    
    protected class XMLWriter
    {
        protected void newLine() {
            try {
                DXMLPrinter.this.out.writeCharacters(DXMLPrinter.this.newLine);
            }
            catch (XMLStreamException e) {
                throw new XMLWriterException(e);
            }
        }
        
        protected void indent() {
            try {
                for (int i = 0; i < DXMLPrinter.this.indent; ++i) {
                    DXMLPrinter.this.out.writeCharacters(DXMLPrinter.this.indentStep);
                }
            }
            catch (XMLStreamException e) {
                throw new XMLWriterException(e);
            }
        }
        
        public void startDocument() {
            try {
                DXMLPrinter.this.out.writeStartDocument();
            }
            catch (XMLStreamException e) {
                throw new XMLWriterException(e);
            }
        }
        
        public void endDocument() {
            try {
                DXMLPrinter.this.out.writeEndDocument();
            }
            catch (XMLStreamException e) {
                throw new XMLWriterException(e);
            }
        }
        
        public final void start(final String element) {
            try {
                this.newLine();
                this.indent();
                DXMLPrinter.this.out.writeStartElement(element);
                final DXMLPrinter this$0 = DXMLPrinter.this;
                ++this$0.indent;
                DXMLPrinter.this.afterEnd = false;
            }
            catch (XMLStreamException e) {
                throw new XMLWriterException(e);
            }
        }
        
        public void end() {
            try {
                final DXMLPrinter this$0 = DXMLPrinter.this;
                --this$0.indent;
                if (DXMLPrinter.this.afterEnd) {
                    this.newLine();
                    this.indent();
                }
                DXMLPrinter.this.out.writeEndElement();
                DXMLPrinter.this.afterEnd = true;
            }
            catch (XMLStreamException e) {
                throw new XMLWriterException(e);
            }
        }
        
        public void attr(final String prefix, final String ns, final String name, final String value) {
            try {
                DXMLPrinter.this.out.writeAttribute(prefix, ns, name, value);
            }
            catch (XMLStreamException e) {
                throw new XMLWriterException(e);
            }
        }
        
        public void attr(final String name, final String value) {
            try {
                DXMLPrinter.this.out.writeAttribute(name, value);
            }
            catch (XMLStreamException e) {
                throw new XMLWriterException(e);
            }
        }
        
        public void ns(final String prefix, final String uri) {
            try {
                DXMLPrinter.this.out.writeNamespace(prefix, uri);
            }
            catch (XMLStreamException e) {
                throw new XMLWriterException(e);
            }
        }
        
        public void body(final String text) {
            try {
                DXMLPrinter.this.out.writeCharacters(text);
                DXMLPrinter.this.afterEnd = false;
            }
            catch (XMLStreamException e) {
                throw new XMLWriterException(e);
            }
        }
    }
    
    protected class DXMLPrinterVisitor extends XMLWriter implements DPatternVisitor<Void>
    {
        protected void on(final DPattern p) {
            p.accept((DPatternVisitor<Object>)this);
        }
        
        protected void unwrapGroup(final DPattern p) {
            if (p instanceof DGroupPattern && p.getAnnotation() == DAnnotation.EMPTY) {
                for (final DPattern d : (DGroupPattern)p) {
                    this.on(d);
                }
            }
            else {
                this.on(p);
            }
        }
        
        protected void unwrapChoice(final DPattern p) {
            if (p instanceof DChoicePattern && p.getAnnotation() == DAnnotation.EMPTY) {
                for (final DPattern d : (DChoicePattern)p) {
                    this.on(d);
                }
            }
            else {
                this.on(p);
            }
        }
        
        protected void on(final NameClass nc) {
            if (nc instanceof SimpleNameClass) {
                final QName qname = ((SimpleNameClass)nc).name;
                String name = qname.getLocalPart();
                if (!qname.getPrefix().equals("")) {
                    name = qname.getPrefix() + ":";
                }
                this.attr("name", name);
            }
            else {
                nc.accept((NameClassVisitor<Object>)DXMLPrinter.this.ncVisitor);
            }
        }
        
        protected void on(final DAnnotation ann) {
            if (ann == DAnnotation.EMPTY) {
                return;
            }
            for (final DAnnotation.Attribute attr : ann.getAttributes().values()) {
                this.attr(attr.getPrefix(), attr.getNs(), attr.getLocalName(), attr.getValue());
            }
            for (final Element elem : ann.getChildren()) {
                try {
                    this.newLine();
                    this.indent();
                    DXMLPrinter.this.print(elem);
                }
                catch (XMLStreamException e) {
                    throw new XMLWriterException(e);
                }
            }
        }
        
        public Void onAttribute(final DAttributePattern p) {
            this.start("attribute");
            this.on(p.getName());
            this.on(p.getAnnotation());
            final DPattern child = p.getChild();
            if (!(child instanceof DTextPattern)) {
                this.on(p.getChild());
            }
            this.end();
            return null;
        }
        
        public Void onChoice(final DChoicePattern p) {
            this.start("choice");
            this.on(p.getAnnotation());
            for (final DPattern d : p) {
                this.on(d);
            }
            this.end();
            return null;
        }
        
        public Void onData(final DDataPattern p) {
            final List<DDataPattern.Param> params = p.getParams();
            final DPattern except = p.getExcept();
            this.start("data");
            this.attr("datatypeLibrary", p.getDatatypeLibrary());
            this.attr("type", p.getType());
            this.on(p.getAnnotation());
            for (final DDataPattern.Param param : params) {
                this.start("param");
                this.attr("ns", param.getNs());
                this.attr("name", param.getName());
                this.body(param.getValue());
                this.end();
            }
            if (except != null) {
                this.start("except");
                this.unwrapChoice(except);
                this.end();
            }
            this.end();
            return null;
        }
        
        public Void onElement(final DElementPattern p) {
            this.start("element");
            this.on(p.getName());
            this.on(p.getAnnotation());
            this.unwrapGroup(p.getChild());
            this.end();
            return null;
        }
        
        public Void onEmpty(final DEmptyPattern p) {
            this.start("empty");
            this.on(p.getAnnotation());
            this.end();
            return null;
        }
        
        public Void onGrammar(final DGrammarPattern p) {
            this.start("grammar");
            this.ns(null, "http://relaxng.org/ns/structure/1.0");
            this.on(p.getAnnotation());
            this.start("start");
            this.on(p.getStart());
            this.end();
            for (final DDefine d : p) {
                this.start("define");
                this.attr("name", d.getName());
                this.on(d.getAnnotation());
                this.unwrapGroup(d.getPattern());
                this.end();
            }
            this.end();
            return null;
        }
        
        public Void onGroup(final DGroupPattern p) {
            this.start("group");
            this.on(p.getAnnotation());
            for (final DPattern d : p) {
                this.on(d);
            }
            this.end();
            return null;
        }
        
        public Void onInterleave(final DInterleavePattern p) {
            this.start("interleave");
            this.on(p.getAnnotation());
            for (final DPattern d : p) {
                this.on(d);
            }
            this.end();
            return null;
        }
        
        public Void onList(final DListPattern p) {
            this.start("list");
            this.on(p.getAnnotation());
            this.unwrapGroup(p.getChild());
            this.end();
            return null;
        }
        
        public Void onMixed(final DMixedPattern p) {
            this.start("mixed");
            this.on(p.getAnnotation());
            this.unwrapGroup(p.getChild());
            this.end();
            return null;
        }
        
        public Void onNotAllowed(final DNotAllowedPattern p) {
            this.start("notAllowed");
            this.on(p.getAnnotation());
            this.end();
            return null;
        }
        
        public Void onOneOrMore(final DOneOrMorePattern p) {
            this.start("oneOrMore");
            this.on(p.getAnnotation());
            this.unwrapGroup(p.getChild());
            this.end();
            return null;
        }
        
        public Void onOptional(final DOptionalPattern p) {
            this.start("optional");
            this.on(p.getAnnotation());
            this.unwrapGroup(p.getChild());
            this.end();
            return null;
        }
        
        public Void onRef(final DRefPattern p) {
            this.start("ref");
            this.attr("name", p.getName());
            this.on(p.getAnnotation());
            this.end();
            return null;
        }
        
        public Void onText(final DTextPattern p) {
            this.start("text");
            this.on(p.getAnnotation());
            this.end();
            return null;
        }
        
        public Void onValue(final DValuePattern p) {
            this.start("value");
            if (!p.getNs().equals("")) {
                this.attr("ns", p.getNs());
            }
            this.attr("datatypeLibrary", p.getDatatypeLibrary());
            this.attr("type", p.getType());
            this.on(p.getAnnotation());
            this.body(p.getValue());
            this.end();
            return null;
        }
        
        public Void onZeroOrMore(final DZeroOrMorePattern p) {
            this.start("zeroOrMore");
            this.on(p.getAnnotation());
            this.unwrapGroup(p.getChild());
            this.end();
            return null;
        }
    }
    
    protected class NameClassXMLPrinterVisitor extends XMLWriter implements NameClassVisitor<Void>
    {
        public Void visitChoice(final NameClass nc1, final NameClass nc2) {
            this.start("choice");
            nc1.accept((NameClassVisitor<Object>)this);
            nc2.accept((NameClassVisitor<Object>)this);
            this.end();
            return null;
        }
        
        public Void visitNsName(final String ns) {
            this.start("nsName");
            this.attr("ns", ns);
            this.end();
            return null;
        }
        
        public Void visitNsNameExcept(final String ns, final NameClass nc) {
            this.start("nsName");
            this.attr("ns", ns);
            this.start("except");
            nc.accept((NameClassVisitor<Object>)this);
            this.end();
            this.end();
            return null;
        }
        
        public Void visitAnyName() {
            this.start("anyName");
            this.end();
            return null;
        }
        
        public Void visitAnyNameExcept(final NameClass nc) {
            this.start("anyName");
            this.start("except");
            nc.accept((NameClassVisitor<Object>)this);
            this.end();
            this.end();
            return null;
        }
        
        public Void visitName(final QName name) {
            this.start("name");
            if (!name.getPrefix().equals("")) {
                this.body(name.getPrefix() + ":");
            }
            this.body(name.getLocalPart());
            this.end();
            return null;
        }
        
        public Void visitNull() {
            throw new UnsupportedOperationException("visitNull");
        }
    }
}
