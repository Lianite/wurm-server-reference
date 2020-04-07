// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse.xml;

import org.kohsuke.rngom.xml.sax.AbstractLexicalHandler;
import java.util.Arrays;
import org.kohsuke.rngom.ast.builder.Grammar;
import org.kohsuke.rngom.ast.builder.Include;
import org.kohsuke.rngom.ast.builder.Div;
import org.kohsuke.rngom.ast.builder.GrammarSection;
import org.kohsuke.rngom.ast.builder.DataPatternBuilder;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.helpers.DefaultHandler;
import java.util.Stack;
import org.kohsuke.rngom.ast.builder.ElementAnnotationBuilder;
import org.kohsuke.rngom.ast.om.ParsedElementAnnotation;
import org.xml.sax.Attributes;
import org.kohsuke.rngom.ast.builder.CommentList;
import org.xml.sax.ContentHandler;
import java.util.Vector;
import java.util.Enumeration;
import org.kohsuke.rngom.parse.Parseable;
import org.kohsuke.rngom.util.Uri;
import org.kohsuke.rngom.xml.util.Naming;
import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.ast.om.ParsedNameClass;
import org.kohsuke.rngom.ast.builder.Annotations;
import org.kohsuke.rngom.parse.Context;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.DTDHandler;
import org.kohsuke.rngom.ast.builder.Scope;
import org.kohsuke.rngom.ast.builder.IncludedGrammar;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.kohsuke.rngom.parse.IllegalSchemaException;
import java.util.Hashtable;
import org.kohsuke.rngom.xml.sax.XmlBaseHandler;
import org.xml.sax.Locator;
import org.kohsuke.rngom.ast.om.ParsedPattern;
import org.kohsuke.rngom.ast.builder.NameClassBuilder;
import org.kohsuke.rngom.ast.builder.SchemaBuilder;
import org.xml.sax.ErrorHandler;
import org.xml.sax.XMLReader;
import org.kohsuke.rngom.util.Localizer;

class SchemaParser
{
    private static final String relaxngURIPrefix;
    static final String relaxng10URI = "http://relaxng.org/ns/structure/1.0";
    private static final Localizer localizer;
    private String relaxngURI;
    private final XMLReader xr;
    private final ErrorHandler eh;
    private final SchemaBuilder schemaBuilder;
    private final NameClassBuilder nameClassBuilder;
    private ParsedPattern startPattern;
    private Locator locator;
    private final XmlBaseHandler xmlBaseHandler;
    private final ContextImpl context;
    private boolean hadError;
    private Hashtable patternTable;
    private Hashtable nameClassTable;
    private static final int INIT_CHILD_ALLOC = 5;
    private static final int PATTERN_CONTEXT = 0;
    private static final int ANY_NAME_CONTEXT = 1;
    private static final int NS_NAME_CONTEXT = 2;
    private SAXParseable parseable;
    
    private void initPatternTable() {
        (this.patternTable = new Hashtable()).put("zeroOrMore", new ZeroOrMoreState());
        this.patternTable.put("oneOrMore", new OneOrMoreState());
        this.patternTable.put("optional", new OptionalState());
        this.patternTable.put("list", new ListState());
        this.patternTable.put("choice", new ChoiceState());
        this.patternTable.put("interleave", new InterleaveState());
        this.patternTable.put("group", new GroupState());
        this.patternTable.put("mixed", new MixedState());
        this.patternTable.put("element", new ElementState());
        this.patternTable.put("attribute", new AttributeState());
        this.patternTable.put("empty", new EmptyState());
        this.patternTable.put("text", new TextState());
        this.patternTable.put("value", new ValueState());
        this.patternTable.put("data", new DataState());
        this.patternTable.put("notAllowed", new NotAllowedState());
        this.patternTable.put("grammar", new GrammarState());
        this.patternTable.put("ref", new RefState());
        this.patternTable.put("parentRef", new ParentRefState());
        this.patternTable.put("externalRef", new ExternalRefState());
    }
    
    private void initNameClassTable() {
        (this.nameClassTable = new Hashtable()).put("name", new NameState());
        this.nameClassTable.put("anyName", new AnyNameState());
        this.nameClassTable.put("nsName", new NsNameState());
        this.nameClassTable.put("choice", new NameClassChoiceState());
    }
    
    public ParsedPattern getParsedPattern() throws IllegalSchemaException {
        if (this.hadError) {
            throw new IllegalSchemaException();
        }
        return this.startPattern;
    }
    
    private void error(final String key) throws SAXException {
        this.error(key, this.locator);
    }
    
    private void error(final String key, final String arg) throws SAXException {
        this.error(key, arg, this.locator);
    }
    
    void error(final String key, final String arg1, final String arg2) throws SAXException {
        this.error(key, arg1, arg2, this.locator);
    }
    
    private void error(final String key, final Locator loc) throws SAXException {
        this.error(new SAXParseException(SchemaParser.localizer.message(key), loc));
    }
    
    private void error(final String key, final String arg, final Locator loc) throws SAXException {
        this.error(new SAXParseException(SchemaParser.localizer.message(key, arg), loc));
    }
    
    private void error(final String key, final String arg1, final String arg2, final Locator loc) throws SAXException {
        this.error(new SAXParseException(SchemaParser.localizer.message(key, arg1, arg2), loc));
    }
    
    private void error(final SAXParseException e) throws SAXException {
        this.hadError = true;
        if (this.eh != null) {
            this.eh.error(e);
        }
    }
    
    void warning(final String key) throws SAXException {
        this.warning(key, this.locator);
    }
    
    private void warning(final String key, final String arg) throws SAXException {
        this.warning(key, arg, this.locator);
    }
    
    private void warning(final String key, final String arg1, final String arg2) throws SAXException {
        this.warning(key, arg1, arg2, this.locator);
    }
    
    private void warning(final String key, final Locator loc) throws SAXException {
        this.warning(new SAXParseException(SchemaParser.localizer.message(key), loc));
    }
    
    private void warning(final String key, final String arg, final Locator loc) throws SAXException {
        this.warning(new SAXParseException(SchemaParser.localizer.message(key, arg), loc));
    }
    
    private void warning(final String key, final String arg1, final String arg2, final Locator loc) throws SAXException {
        this.warning(new SAXParseException(SchemaParser.localizer.message(key, arg1, arg2), loc));
    }
    
    private void warning(final SAXParseException e) throws SAXException {
        if (this.eh != null) {
            this.eh.warning(e);
        }
    }
    
    SchemaParser(final SAXParseable parseable, final XMLReader xr, final ErrorHandler eh, final SchemaBuilder schemaBuilder, final IncludedGrammar grammar, final Scope scope, final String inheritedNs) throws SAXException {
        this.xmlBaseHandler = new XmlBaseHandler();
        this.context = new ContextImpl();
        this.hadError = false;
        this.parseable = parseable;
        this.xr = xr;
        this.eh = eh;
        this.schemaBuilder = schemaBuilder;
        this.nameClassBuilder = schemaBuilder.getNameClassBuilder();
        if (eh != null) {
            xr.setErrorHandler(eh);
        }
        xr.setDTDHandler(this.context);
        if (schemaBuilder.usesComments()) {
            try {
                xr.setProperty("http://xml.org/sax/properties/lexical-handler", new LexicalHandlerImpl());
            }
            catch (SAXNotRecognizedException e) {
                this.warning("no_comment_support", xr.getClass().getName());
            }
            catch (SAXNotSupportedException e2) {
                this.warning("no_comment_support", xr.getClass().getName());
            }
        }
        this.initPatternTable();
        this.initNameClassTable();
        new RootState(grammar, scope, inheritedNs).set();
    }
    
    private Context getContext() {
        return this.context;
    }
    
    private ParsedNameClass expandName(final String name, final String ns, final Annotations anno) throws SAXException {
        final int ic = name.indexOf(58);
        if (ic == -1) {
            return this.nameClassBuilder.makeName(ns, this.checkNCName(name), null, null, anno);
        }
        final String prefix = this.checkNCName(name.substring(0, ic));
        final String localName = this.checkNCName(name.substring(ic + 1));
        for (PrefixMapping tem = this.context.prefixMapping; tem != null; tem = tem.next) {
            if (tem.prefix.equals(prefix)) {
                return this.nameClassBuilder.makeName(tem.uri, localName, prefix, null, anno);
            }
        }
        this.error("undefined_prefix", prefix);
        return this.nameClassBuilder.makeName("", localName, null, null, anno);
    }
    
    private String findPrefix(final String qName, final String uri) {
        String prefix = null;
        if (qName == null || qName.equals("")) {
            for (PrefixMapping p = this.context.prefixMapping; p != null; p = p.next) {
                if (p.uri.equals(uri)) {
                    prefix = p.prefix;
                    break;
                }
            }
        }
        else {
            final int off = qName.indexOf(58);
            if (off > 0) {
                prefix = qName.substring(0, off);
            }
        }
        return prefix;
    }
    
    private String checkNCName(final String str) throws SAXException {
        if (!Naming.isNcname(str)) {
            this.error("invalid_ncname", str);
        }
        return str;
    }
    
    private String resolve(String systemId) throws SAXException {
        if (Uri.hasFragmentId(systemId)) {
            this.error("href_fragment_id");
        }
        systemId = Uri.escapeDisallowedChars(systemId);
        return Uri.resolve(this.xmlBaseHandler.getBaseUri(), systemId);
    }
    
    private Location makeLocation() {
        if (this.locator == null) {
            return null;
        }
        return this.schemaBuilder.makeLocation(this.locator.getSystemId(), this.locator.getLineNumber(), this.locator.getColumnNumber());
    }
    
    private void checkUri(final String s) throws SAXException {
        if (!Uri.isValid(s)) {
            this.error("invalid_uri", s);
        }
    }
    
    static {
        relaxngURIPrefix = "http://relaxng.org/ns/structure/1.0".substring(0, "http://relaxng.org/ns/structure/1.0".lastIndexOf(47) + 1);
        localizer = new Localizer(new Localizer(Parseable.class), SchemaParser.class);
    }
    
    static class PrefixMapping
    {
        final String prefix;
        final String uri;
        final PrefixMapping next;
        
        PrefixMapping(final String prefix, final String uri, final PrefixMapping next) {
            this.prefix = prefix;
            this.uri = uri;
            this.next = next;
        }
    }
    
    abstract static class AbstractContext extends DtdContext implements Context
    {
        PrefixMapping prefixMapping;
        
        AbstractContext() {
            this.prefixMapping = new PrefixMapping("xml", "http://www.w3.org/XML/1998/namespace", null);
        }
        
        AbstractContext(final AbstractContext context) {
            super(context);
            this.prefixMapping = context.prefixMapping;
        }
        
        public String resolveNamespacePrefix(final String prefix) {
            for (PrefixMapping p = this.prefixMapping; p != null; p = p.next) {
                if (p.prefix.equals(prefix)) {
                    return p.uri;
                }
            }
            return null;
        }
        
        public Enumeration prefixes() {
            final Vector v = new Vector();
            for (PrefixMapping p = this.prefixMapping; p != null; p = p.next) {
                if (!v.contains(p.prefix)) {
                    v.addElement(p.prefix);
                }
            }
            return v.elements();
        }
        
        public Context copy() {
            return new SavedContext(this);
        }
    }
    
    static class SavedContext extends AbstractContext
    {
        private final String baseUri;
        
        SavedContext(final AbstractContext context) {
            super(context);
            this.baseUri = context.getBaseUri();
        }
        
        public String getBaseUri() {
            return this.baseUri;
        }
    }
    
    class ContextImpl extends AbstractContext
    {
        public String getBaseUri() {
            return SchemaParser.this.xmlBaseHandler.getBaseUri();
        }
    }
    
    abstract class Handler implements ContentHandler, CommentHandler
    {
        CommentList comments;
        
        CommentList getComments() {
            final CommentList tem = this.comments;
            this.comments = null;
            return tem;
        }
        
        public void comment(final String value) {
            if (this.comments == null) {
                this.comments = SchemaParser.this.schemaBuilder.makeCommentList();
            }
            this.comments.addComment(value, SchemaParser.this.makeLocation());
        }
        
        public void processingInstruction(final String target, final String date) {
        }
        
        public void skippedEntity(final String name) {
        }
        
        public void ignorableWhitespace(final char[] ch, final int start, final int len) {
        }
        
        public void startDocument() {
        }
        
        public void endDocument() {
        }
        
        public void startPrefixMapping(final String prefix, final String uri) {
            SchemaParser.this.context.prefixMapping = new PrefixMapping(prefix, uri, SchemaParser.this.context.prefixMapping);
        }
        
        public void endPrefixMapping(final String prefix) {
            SchemaParser.this.context.prefixMapping = SchemaParser.this.context.prefixMapping.next;
        }
        
        public void setDocumentLocator(final Locator loc) {
            SchemaParser.this.locator = loc;
            SchemaParser.this.xmlBaseHandler.setLocator(loc);
        }
    }
    
    abstract class State extends Handler
    {
        State parent;
        String nsInherit;
        String ns;
        String datatypeLibrary;
        Scope scope;
        Location startLocation;
        Annotations annotations;
        
        void set() {
            SchemaParser.this.xr.setContentHandler(this);
        }
        
        abstract State create();
        
        abstract State createChildState(final String p0) throws SAXException;
        
        void setParent(final State parent) {
            this.parent = parent;
            this.nsInherit = parent.getNs();
            this.datatypeLibrary = parent.datatypeLibrary;
            this.scope = parent.scope;
            this.startLocation = SchemaParser.this.makeLocation();
            if (parent.comments != null) {
                this.annotations = SchemaParser.this.schemaBuilder.makeAnnotations(parent.comments, SchemaParser.this.getContext());
                parent.comments = null;
            }
            else if (parent instanceof RootState) {
                this.annotations = SchemaParser.this.schemaBuilder.makeAnnotations(null, SchemaParser.this.getContext());
            }
        }
        
        String getNs() {
            return (this.ns == null) ? this.nsInherit : this.ns;
        }
        
        boolean isRelaxNGElement(final String uri) throws SAXException {
            return uri.equals(SchemaParser.this.relaxngURI);
        }
        
        public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
            SchemaParser.this.xmlBaseHandler.startElement();
            if (this.isRelaxNGElement(namespaceURI)) {
                final State state = this.createChildState(localName);
                if (state == null) {
                    SchemaParser.this.xr.setContentHandler(new Skipper(this));
                    return;
                }
                state.setParent(this);
                state.set();
                state.attributes(atts);
            }
            else {
                this.checkForeignElement();
                final ForeignElementHandler feh = new ForeignElementHandler(this, this.getComments());
                feh.startElement(namespaceURI, localName, qName, atts);
                SchemaParser.this.xr.setContentHandler(feh);
            }
        }
        
        public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
            SchemaParser.this.xmlBaseHandler.endElement();
            this.parent.set();
            this.end();
        }
        
        void setName(final String name) throws SAXException {
            SchemaParser.this.error("illegal_name_attribute");
        }
        
        void setOtherAttribute(final String name, final String value) throws SAXException {
            SchemaParser.this.error("illegal_attribute_ignored", name);
        }
        
        void endAttributes() throws SAXException {
        }
        
        void checkForeignElement() throws SAXException {
        }
        
        void attributes(final Attributes atts) throws SAXException {
            for (int len = atts.getLength(), i = 0; i < len; ++i) {
                final String uri = atts.getURI(i);
                if (uri.length() == 0) {
                    final String name = atts.getLocalName(i);
                    if (name.equals("name")) {
                        this.setName(atts.getValue(i).trim());
                    }
                    else if (name.equals("ns")) {
                        this.ns = atts.getValue(i);
                    }
                    else if (name.equals("datatypeLibrary")) {
                        this.datatypeLibrary = atts.getValue(i);
                        SchemaParser.this.checkUri(this.datatypeLibrary);
                        if (!this.datatypeLibrary.equals("") && !Uri.isAbsolute(this.datatypeLibrary)) {
                            SchemaParser.this.error("relative_datatype_library");
                        }
                        if (Uri.hasFragmentId(this.datatypeLibrary)) {
                            SchemaParser.this.error("fragment_identifier_datatype_library");
                        }
                        this.datatypeLibrary = Uri.escapeDisallowedChars(this.datatypeLibrary);
                    }
                    else {
                        this.setOtherAttribute(name, atts.getValue(i));
                    }
                }
                else if (uri.equals(SchemaParser.this.relaxngURI)) {
                    SchemaParser.this.error("qualified_attribute", atts.getLocalName(i));
                }
                else if (uri.equals("http://www.w3.org/XML/1998/namespace") && atts.getLocalName(i).equals("base")) {
                    SchemaParser.this.xmlBaseHandler.xmlBaseAttribute(atts.getValue(i));
                }
                else {
                    if (this.annotations == null) {
                        this.annotations = SchemaParser.this.schemaBuilder.makeAnnotations(null, SchemaParser.this.getContext());
                    }
                    this.annotations.addAttribute(uri, atts.getLocalName(i), SchemaParser.this.findPrefix(atts.getQName(i), uri), atts.getValue(i), this.startLocation);
                }
            }
            this.endAttributes();
        }
        
        abstract void end() throws SAXException;
        
        void endChild(final ParsedPattern pattern) {
        }
        
        void endChild(final ParsedNameClass nc) {
        }
        
        public void startDocument() {
        }
        
        public void endDocument() {
            if (this.comments != null && SchemaParser.this.startPattern != null) {
                SchemaParser.this.startPattern = SchemaParser.this.schemaBuilder.commentAfter(SchemaParser.this.startPattern, this.comments);
                this.comments = null;
            }
        }
        
        public void characters(final char[] ch, final int start, final int len) throws SAXException {
            for (int i = 0; i < len; ++i) {
                switch (ch[start + i]) {
                    case '\t':
                    case '\n':
                    case '\r':
                    case ' ': {
                        break;
                    }
                    default: {
                        SchemaParser.this.error("illegal_characters_ignored");
                        break;
                    }
                }
            }
        }
        
        boolean isPatternNamespaceURI(final String s) {
            return s.equals(SchemaParser.this.relaxngURI);
        }
        
        void endForeignChild(final ParsedElementAnnotation ea) {
            if (this.annotations == null) {
                this.annotations = SchemaParser.this.schemaBuilder.makeAnnotations(null, SchemaParser.this.getContext());
            }
            this.annotations.addElement(ea);
        }
        
        void mergeLeadingComments() {
            if (this.comments != null) {
                if (this.annotations == null) {
                    this.annotations = SchemaParser.this.schemaBuilder.makeAnnotations(this.comments, SchemaParser.this.getContext());
                }
                else {
                    this.annotations.addLeadingComment(this.comments);
                }
                this.comments = null;
            }
        }
    }
    
    class ForeignElementHandler extends Handler
    {
        final State nextState;
        ElementAnnotationBuilder builder;
        final Stack builderStack;
        StringBuffer textBuf;
        Location textLoc;
        
        ForeignElementHandler(final State nextState, final CommentList comments) {
            this.builderStack = new Stack();
            this.nextState = nextState;
            this.comments = comments;
        }
        
        public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) {
            this.flushText();
            if (this.builder != null) {
                this.builderStack.push(this.builder);
            }
            final Location loc = SchemaParser.this.makeLocation();
            this.builder = SchemaParser.this.schemaBuilder.makeElementAnnotationBuilder(namespaceURI, localName, SchemaParser.this.findPrefix(qName, namespaceURI), loc, this.getComments(), SchemaParser.this.getContext());
            for (int len = atts.getLength(), i = 0; i < len; ++i) {
                final String uri = atts.getURI(i);
                this.builder.addAttribute(uri, atts.getLocalName(i), SchemaParser.this.findPrefix(atts.getQName(i), uri), atts.getValue(i), loc);
            }
        }
        
        public void endElement(final String namespaceURI, final String localName, final String qName) {
            this.flushText();
            if (this.comments != null) {
                this.builder.addComment(this.getComments());
            }
            final ParsedElementAnnotation ea = this.builder.makeElementAnnotation();
            if (this.builderStack.empty()) {
                this.nextState.endForeignChild(ea);
                this.nextState.set();
            }
            else {
                (this.builder = this.builderStack.pop()).addElement(ea);
            }
        }
        
        public void characters(final char[] ch, final int start, final int length) {
            if (this.textBuf == null) {
                this.textBuf = new StringBuffer();
            }
            this.textBuf.append(ch, start, length);
            if (this.textLoc == null) {
                this.textLoc = SchemaParser.this.makeLocation();
            }
        }
        
        public void comment(final String value) {
            this.flushText();
            super.comment(value);
        }
        
        void flushText() {
            if (this.textBuf != null && this.textBuf.length() != 0) {
                this.builder.addText(this.textBuf.toString(), this.textLoc, this.getComments());
                this.textBuf.setLength(0);
            }
            this.textLoc = null;
        }
    }
    
    class Skipper extends DefaultHandler implements CommentHandler
    {
        int level;
        final State nextState;
        
        Skipper(final State nextState) {
            this.level = 1;
            this.nextState = nextState;
        }
        
        public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
            ++this.level;
        }
        
        public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
            final int level = this.level - 1;
            this.level = level;
            if (level == 0) {
                this.nextState.set();
            }
        }
        
        public void comment(final String value) {
        }
    }
    
    abstract class EmptyContentState extends State
    {
        State createChildState(final String localName) throws SAXException {
            SchemaParser.this.error("expected_empty", localName);
            return null;
        }
        
        abstract ParsedPattern makePattern() throws SAXException;
        
        void end() throws SAXException {
            if (this.comments != null) {
                if (this.annotations == null) {
                    this.annotations = SchemaParser.this.schemaBuilder.makeAnnotations(null, SchemaParser.this.getContext());
                }
                this.annotations.addComment(this.comments);
                this.comments = null;
            }
            this.parent.endChild(this.makePattern());
        }
    }
    
    abstract class PatternContainerState extends State
    {
        List<ParsedPattern> childPatterns;
        
        State createChildState(final String localName) throws SAXException {
            final State state = SchemaParser.this.patternTable.get(localName);
            if (state == null) {
                SchemaParser.this.error("expected_pattern", localName);
                return null;
            }
            return state.create();
        }
        
        ParsedPattern buildPattern(final List<ParsedPattern> patterns, final Location loc, final Annotations anno) throws SAXException {
            if (patterns.size() == 1 && anno == null) {
                return patterns.get(0);
            }
            return SchemaParser.this.schemaBuilder.makeGroup(patterns, loc, anno);
        }
        
        void endChild(final ParsedPattern pattern) {
            if (this.childPatterns == null) {
                this.childPatterns = new ArrayList<ParsedPattern>(5);
            }
            this.childPatterns.add(pattern);
        }
        
        void endForeignChild(final ParsedElementAnnotation ea) {
            if (this.childPatterns == null) {
                super.endForeignChild(ea);
            }
            else {
                final int idx = this.childPatterns.size() - 1;
                this.childPatterns.set(idx, SchemaParser.this.schemaBuilder.annotateAfter(this.childPatterns.get(idx), ea));
            }
        }
        
        void end() throws SAXException {
            if (this.childPatterns == null) {
                SchemaParser.this.error("missing_children");
                this.endChild(SchemaParser.this.schemaBuilder.makeErrorPattern());
            }
            if (this.comments != null) {
                final int idx = this.childPatterns.size() - 1;
                this.childPatterns.set(idx, SchemaParser.this.schemaBuilder.commentAfter(this.childPatterns.get(idx), this.comments));
                this.comments = null;
            }
            this.sendPatternToParent(this.buildPattern(this.childPatterns, this.startLocation, this.annotations));
        }
        
        void sendPatternToParent(final ParsedPattern p) {
            this.parent.endChild(p);
        }
    }
    
    class GroupState extends PatternContainerState
    {
        State create() {
            return new GroupState();
        }
    }
    
    class ZeroOrMoreState extends PatternContainerState
    {
        State create() {
            return new ZeroOrMoreState();
        }
        
        ParsedPattern buildPattern(final List<ParsedPattern> patterns, final Location loc, final Annotations anno) throws SAXException {
            return SchemaParser.this.schemaBuilder.makeZeroOrMore(super.buildPattern(patterns, loc, null), loc, anno);
        }
    }
    
    class OneOrMoreState extends PatternContainerState
    {
        State create() {
            return new OneOrMoreState();
        }
        
        ParsedPattern buildPattern(final List<ParsedPattern> patterns, final Location loc, final Annotations anno) throws SAXException {
            return SchemaParser.this.schemaBuilder.makeOneOrMore(super.buildPattern(patterns, loc, null), loc, anno);
        }
    }
    
    class OptionalState extends PatternContainerState
    {
        State create() {
            return new OptionalState();
        }
        
        ParsedPattern buildPattern(final List<ParsedPattern> patterns, final Location loc, final Annotations anno) throws SAXException {
            return SchemaParser.this.schemaBuilder.makeOptional(super.buildPattern(patterns, loc, null), loc, anno);
        }
    }
    
    class ListState extends PatternContainerState
    {
        State create() {
            return new ListState();
        }
        
        ParsedPattern buildPattern(final List<ParsedPattern> patterns, final Location loc, final Annotations anno) throws SAXException {
            return SchemaParser.this.schemaBuilder.makeList(super.buildPattern(patterns, loc, null), loc, anno);
        }
    }
    
    class ChoiceState extends PatternContainerState
    {
        State create() {
            return new ChoiceState();
        }
        
        ParsedPattern buildPattern(final List<ParsedPattern> patterns, final Location loc, final Annotations anno) throws SAXException {
            return SchemaParser.this.schemaBuilder.makeChoice(patterns, loc, anno);
        }
    }
    
    class InterleaveState extends PatternContainerState
    {
        State create() {
            return new InterleaveState();
        }
        
        ParsedPattern buildPattern(final List<ParsedPattern> patterns, final Location loc, final Annotations anno) {
            return SchemaParser.this.schemaBuilder.makeInterleave(patterns, loc, anno);
        }
    }
    
    class MixedState extends PatternContainerState
    {
        State create() {
            return new MixedState();
        }
        
        ParsedPattern buildPattern(final List<ParsedPattern> patterns, final Location loc, final Annotations anno) throws SAXException {
            return SchemaParser.this.schemaBuilder.makeMixed(super.buildPattern(patterns, loc, null), loc, anno);
        }
    }
    
    class ElementState extends PatternContainerState implements NameClassRef
    {
        ParsedNameClass nameClass;
        boolean nameClassWasAttribute;
        String name;
        
        void setName(final String name) {
            this.name = name;
        }
        
        public void setNameClass(final ParsedNameClass nc) {
            this.nameClass = nc;
        }
        
        void endAttributes() throws SAXException {
            if (this.name != null) {
                this.nameClass = SchemaParser.this.expandName(this.name, this.getNs(), null);
                this.nameClassWasAttribute = true;
            }
            else {
                new NameClassChildState(this, this).set();
            }
        }
        
        State create() {
            return new ElementState();
        }
        
        ParsedPattern buildPattern(final List<ParsedPattern> patterns, final Location loc, final Annotations anno) throws SAXException {
            return SchemaParser.this.schemaBuilder.makeElement(this.nameClass, super.buildPattern(patterns, loc, null), loc, anno);
        }
        
        void endForeignChild(final ParsedElementAnnotation ea) {
            if (this.nameClassWasAttribute || this.childPatterns != null || this.nameClass == null) {
                super.endForeignChild(ea);
            }
            else {
                this.nameClass = SchemaParser.this.nameClassBuilder.annotateAfter(this.nameClass, ea);
            }
        }
    }
    
    class RootState extends PatternContainerState
    {
        IncludedGrammar grammar;
        
        RootState() {
        }
        
        RootState(final IncludedGrammar grammar, final Scope scope, final String ns) {
            this.grammar = grammar;
            this.scope = scope;
            this.nsInherit = ns;
            this.datatypeLibrary = "";
        }
        
        State create() {
            return new RootState();
        }
        
        State createChildState(final String localName) throws SAXException {
            if (this.grammar == null) {
                return super.createChildState(localName);
            }
            if (localName.equals("grammar")) {
                return new MergeGrammarState(this.grammar);
            }
            SchemaParser.this.error("expected_grammar", localName);
            return null;
        }
        
        void checkForeignElement() throws SAXException {
            SchemaParser.this.error("root_bad_namespace_uri", "http://relaxng.org/ns/structure/1.0");
        }
        
        void endChild(final ParsedPattern pattern) {
            SchemaParser.this.startPattern = pattern;
        }
        
        boolean isRelaxNGElement(final String uri) throws SAXException {
            if (!uri.startsWith(SchemaParser.relaxngURIPrefix)) {
                return false;
            }
            if (!uri.equals("http://relaxng.org/ns/structure/1.0")) {
                SchemaParser.this.warning("wrong_uri_version", "http://relaxng.org/ns/structure/1.0".substring(SchemaParser.relaxngURIPrefix.length()), uri.substring(SchemaParser.relaxngURIPrefix.length()));
            }
            SchemaParser.this.relaxngURI = uri;
            return true;
        }
    }
    
    class NotAllowedState extends EmptyContentState
    {
        State create() {
            return new NotAllowedState();
        }
        
        ParsedPattern makePattern() {
            return SchemaParser.this.schemaBuilder.makeNotAllowed(this.startLocation, this.annotations);
        }
    }
    
    class EmptyState extends EmptyContentState
    {
        State create() {
            return new EmptyState();
        }
        
        ParsedPattern makePattern() {
            return SchemaParser.this.schemaBuilder.makeEmpty(this.startLocation, this.annotations);
        }
    }
    
    class TextState extends EmptyContentState
    {
        State create() {
            return new TextState();
        }
        
        ParsedPattern makePattern() {
            return SchemaParser.this.schemaBuilder.makeText(this.startLocation, this.annotations);
        }
    }
    
    class ValueState extends EmptyContentState
    {
        final StringBuffer buf;
        String type;
        
        ValueState() {
            this.buf = new StringBuffer();
        }
        
        State create() {
            return new ValueState();
        }
        
        void setOtherAttribute(final String name, final String value) throws SAXException {
            if (name.equals("type")) {
                this.type = SchemaParser.this.checkNCName(value.trim());
            }
            else {
                super.setOtherAttribute(name, value);
            }
        }
        
        public void characters(final char[] ch, final int start, final int len) {
            this.buf.append(ch, start, len);
        }
        
        void checkForeignElement() throws SAXException {
            SchemaParser.this.error("value_contains_foreign_element");
        }
        
        ParsedPattern makePattern() throws SAXException {
            if (this.type == null) {
                return this.makePattern("", "token");
            }
            return this.makePattern(this.datatypeLibrary, this.type);
        }
        
        void end() throws SAXException {
            this.mergeLeadingComments();
            super.end();
        }
        
        ParsedPattern makePattern(final String datatypeLibrary, final String type) {
            return SchemaParser.this.schemaBuilder.makeValue(datatypeLibrary, type, this.buf.toString(), SchemaParser.this.getContext(), this.getNs(), this.startLocation, this.annotations);
        }
    }
    
    class DataState extends State
    {
        String type;
        ParsedPattern except;
        DataPatternBuilder dpb;
        
        DataState() {
            this.except = null;
            this.dpb = null;
        }
        
        State create() {
            return new DataState();
        }
        
        State createChildState(final String localName) throws SAXException {
            if (localName.equals("param")) {
                if (this.except != null) {
                    SchemaParser.this.error("param_after_except");
                }
                return new ParamState(this.dpb);
            }
            if (localName.equals("except")) {
                if (this.except != null) {
                    SchemaParser.this.error("multiple_except");
                }
                return new ChoiceState();
            }
            SchemaParser.this.error("expected_param_except", localName);
            return null;
        }
        
        void setOtherAttribute(final String name, final String value) throws SAXException {
            if (name.equals("type")) {
                this.type = SchemaParser.this.checkNCName(value.trim());
            }
            else {
                super.setOtherAttribute(name, value);
            }
        }
        
        void endAttributes() throws SAXException {
            if (this.type == null) {
                SchemaParser.this.error("missing_type_attribute");
            }
            else {
                this.dpb = SchemaParser.this.schemaBuilder.makeDataPatternBuilder(this.datatypeLibrary, this.type, this.startLocation);
            }
        }
        
        void endForeignChild(final ParsedElementAnnotation ea) {
            this.dpb.annotation(ea);
        }
        
        void end() throws SAXException {
            ParsedPattern p;
            if (this.dpb != null) {
                if (this.except != null) {
                    p = this.dpb.makePattern(this.except, this.startLocation, this.annotations);
                }
                else {
                    p = this.dpb.makePattern(this.startLocation, this.annotations);
                }
            }
            else {
                p = SchemaParser.this.schemaBuilder.makeErrorPattern();
            }
            this.parent.endChild(p);
        }
        
        void endChild(final ParsedPattern pattern) {
            this.except = pattern;
        }
    }
    
    class ParamState extends State
    {
        private final StringBuffer buf;
        private final DataPatternBuilder dpb;
        private String name;
        
        ParamState(final DataPatternBuilder dpb) {
            this.buf = new StringBuffer();
            this.dpb = dpb;
        }
        
        State create() {
            return new ParamState(null);
        }
        
        void setName(final String name) throws SAXException {
            this.name = SchemaParser.this.checkNCName(name);
        }
        
        void endAttributes() throws SAXException {
            if (this.name == null) {
                SchemaParser.this.error("missing_name_attribute");
            }
        }
        
        State createChildState(final String localName) throws SAXException {
            SchemaParser.this.error("expected_empty", localName);
            return null;
        }
        
        public void characters(final char[] ch, final int start, final int len) {
            this.buf.append(ch, start, len);
        }
        
        void checkForeignElement() throws SAXException {
            SchemaParser.this.error("param_contains_foreign_element");
        }
        
        void end() throws SAXException {
            if (this.name == null) {
                return;
            }
            if (this.dpb == null) {
                return;
            }
            this.mergeLeadingComments();
            this.dpb.addParam(this.name, this.buf.toString(), SchemaParser.this.getContext(), this.getNs(), this.startLocation, this.annotations);
        }
    }
    
    class AttributeState extends PatternContainerState implements NameClassRef
    {
        ParsedNameClass nameClass;
        boolean nameClassWasAttribute;
        String name;
        
        State create() {
            return new AttributeState();
        }
        
        void setName(final String name) {
            this.name = name;
        }
        
        public void setNameClass(final ParsedNameClass nc) {
            this.nameClass = nc;
        }
        
        void endAttributes() throws SAXException {
            if (this.name != null) {
                String nsUse;
                if (this.ns != null) {
                    nsUse = this.ns;
                }
                else {
                    nsUse = "";
                }
                this.nameClass = SchemaParser.this.expandName(this.name, nsUse, null);
                this.nameClassWasAttribute = true;
            }
            else {
                new NameClassChildState(this, this).set();
            }
        }
        
        void endForeignChild(final ParsedElementAnnotation ea) {
            if (this.nameClassWasAttribute || this.childPatterns != null || this.nameClass == null) {
                super.endForeignChild(ea);
            }
            else {
                this.nameClass = SchemaParser.this.nameClassBuilder.annotateAfter(this.nameClass, ea);
            }
        }
        
        void end() throws SAXException {
            if (this.childPatterns == null) {
                this.endChild(SchemaParser.this.schemaBuilder.makeText(this.startLocation, null));
            }
            super.end();
        }
        
        ParsedPattern buildPattern(final List<ParsedPattern> patterns, final Location loc, final Annotations anno) throws SAXException {
            return SchemaParser.this.schemaBuilder.makeAttribute(this.nameClass, super.buildPattern(patterns, loc, null), loc, anno);
        }
        
        State createChildState(final String localName) throws SAXException {
            final State tem = super.createChildState(localName);
            if (tem != null && this.childPatterns != null) {
                SchemaParser.this.error("attribute_multi_pattern");
            }
            return tem;
        }
    }
    
    abstract class SinglePatternContainerState extends PatternContainerState
    {
        State createChildState(final String localName) throws SAXException {
            if (this.childPatterns == null) {
                return super.createChildState(localName);
            }
            SchemaParser.this.error("too_many_children");
            return null;
        }
    }
    
    class GrammarSectionState extends State
    {
        GrammarSection section;
        
        GrammarSectionState() {
        }
        
        GrammarSectionState(final GrammarSection section) {
            this.section = section;
        }
        
        State create() {
            return new GrammarSectionState(null);
        }
        
        State createChildState(final String localName) throws SAXException {
            if (localName.equals("define")) {
                return new DefineState(this.section);
            }
            if (localName.equals("start")) {
                return new StartState(this.section);
            }
            if (localName.equals("include")) {
                final Include include = this.section.makeInclude();
                if (include != null) {
                    return new IncludeState(include);
                }
            }
            if (localName.equals("div")) {
                return new DivState(this.section.makeDiv());
            }
            SchemaParser.this.error("expected_define", localName);
            return null;
        }
        
        void end() throws SAXException {
            if (this.comments != null) {
                this.section.topLevelComment(this.comments);
                this.comments = null;
            }
        }
        
        void endForeignChild(final ParsedElementAnnotation ea) {
            this.section.topLevelAnnotation(ea);
        }
    }
    
    class DivState extends GrammarSectionState
    {
        final Div div;
        
        DivState(final Div div) {
            super(div);
            this.div = div;
        }
        
        void end() throws SAXException {
            super.end();
            this.div.endDiv(this.startLocation, this.annotations);
        }
    }
    
    class IncludeState extends GrammarSectionState
    {
        String href;
        final Include include;
        
        IncludeState(final Include include) {
            super(include);
            this.include = include;
        }
        
        void setOtherAttribute(final String name, final String value) throws SAXException {
            if (name.equals("href")) {
                this.href = value;
                SchemaParser.this.checkUri(this.href);
            }
            else {
                super.setOtherAttribute(name, value);
            }
        }
        
        void endAttributes() throws SAXException {
            if (this.href == null) {
                SchemaParser.this.error("missing_href_attribute");
            }
            else {
                this.href = SchemaParser.this.resolve(this.href);
            }
        }
        
        void end() throws SAXException {
            super.end();
            if (this.href != null) {
                try {
                    this.include.endInclude(SchemaParser.this.parseable, this.href, this.getNs(), this.startLocation, this.annotations);
                }
                catch (IllegalSchemaException ex) {}
            }
        }
    }
    
    class MergeGrammarState extends GrammarSectionState
    {
        final IncludedGrammar grammar;
        
        MergeGrammarState(final IncludedGrammar grammar) {
            super(grammar);
            this.grammar = grammar;
        }
        
        void end() throws SAXException {
            super.end();
            this.parent.endChild(this.grammar.endIncludedGrammar(this.startLocation, this.annotations));
        }
    }
    
    class GrammarState extends GrammarSectionState
    {
        Grammar grammar;
        
        void setParent(final State parent) {
            super.setParent(parent);
            this.grammar = SchemaParser.this.schemaBuilder.makeGrammar(this.scope);
            this.section = this.grammar;
            this.scope = this.grammar;
        }
        
        State create() {
            return new GrammarState();
        }
        
        void end() throws SAXException {
            super.end();
            this.parent.endChild(this.grammar.endGrammar(this.startLocation, this.annotations));
        }
    }
    
    class RefState extends EmptyContentState
    {
        String name;
        
        State create() {
            return new RefState();
        }
        
        void endAttributes() throws SAXException {
            if (this.name == null) {
                SchemaParser.this.error("missing_name_attribute");
            }
        }
        
        void setName(final String name) throws SAXException {
            this.name = SchemaParser.this.checkNCName(name);
        }
        
        ParsedPattern makePattern() throws SAXException {
            if (this.name == null) {
                return SchemaParser.this.schemaBuilder.makeErrorPattern();
            }
            if (this.scope == null) {
                SchemaParser.this.error("ref_outside_grammar", this.name);
                return SchemaParser.this.schemaBuilder.makeErrorPattern();
            }
            return this.scope.makeRef(this.name, this.startLocation, this.annotations);
        }
    }
    
    class ParentRefState extends RefState
    {
        State create() {
            return new ParentRefState();
        }
        
        ParsedPattern makePattern() throws SAXException {
            if (this.name == null) {
                return SchemaParser.this.schemaBuilder.makeErrorPattern();
            }
            if (this.scope == null) {
                SchemaParser.this.error("parent_ref_outside_grammar", this.name);
                return SchemaParser.this.schemaBuilder.makeErrorPattern();
            }
            return this.scope.makeParentRef(this.name, this.startLocation, this.annotations);
        }
    }
    
    class ExternalRefState extends EmptyContentState
    {
        String href;
        ParsedPattern includedPattern;
        
        State create() {
            return new ExternalRefState();
        }
        
        void setOtherAttribute(final String name, final String value) throws SAXException {
            if (name.equals("href")) {
                this.href = value;
                SchemaParser.this.checkUri(this.href);
            }
            else {
                super.setOtherAttribute(name, value);
            }
        }
        
        void endAttributes() throws SAXException {
            if (this.href == null) {
                SchemaParser.this.error("missing_href_attribute");
            }
            else {
                this.href = SchemaParser.this.resolve(this.href);
            }
        }
        
        ParsedPattern makePattern() {
            if (this.href != null) {
                try {
                    return SchemaParser.this.schemaBuilder.makeExternalRef(SchemaParser.this.parseable, this.href, this.getNs(), this.scope, this.startLocation, this.annotations);
                }
                catch (IllegalSchemaException ex) {}
            }
            return SchemaParser.this.schemaBuilder.makeErrorPattern();
        }
    }
    
    abstract class DefinitionState extends PatternContainerState
    {
        GrammarSection.Combine combine;
        final GrammarSection section;
        
        DefinitionState(final GrammarSection section) {
            this.combine = null;
            this.section = section;
        }
        
        void setOtherAttribute(final String name, String value) throws SAXException {
            if (name.equals("combine")) {
                value = value.trim();
                if (value.equals("choice")) {
                    this.combine = GrammarSection.COMBINE_CHOICE;
                }
                else if (value.equals("interleave")) {
                    this.combine = GrammarSection.COMBINE_INTERLEAVE;
                }
                else {
                    SchemaParser.this.error("combine_attribute_bad_value", value);
                }
            }
            else {
                super.setOtherAttribute(name, value);
            }
        }
        
        ParsedPattern buildPattern(final List<ParsedPattern> patterns, final Location loc, final Annotations anno) throws SAXException {
            return super.buildPattern(patterns, loc, null);
        }
    }
    
    class DefineState extends DefinitionState
    {
        String name;
        
        DefineState(final GrammarSection section) {
            super(section);
        }
        
        State create() {
            return new DefineState(null);
        }
        
        void setName(final String name) throws SAXException {
            this.name = SchemaParser.this.checkNCName(name);
        }
        
        void endAttributes() throws SAXException {
            if (this.name == null) {
                SchemaParser.this.error("missing_name_attribute");
            }
        }
        
        void sendPatternToParent(final ParsedPattern p) {
            if (this.name != null) {
                this.section.define(this.name, this.combine, p, this.startLocation, this.annotations);
            }
        }
    }
    
    class StartState extends DefinitionState
    {
        StartState(final GrammarSection section) {
            super(section);
        }
        
        State create() {
            return new StartState(null);
        }
        
        void sendPatternToParent(final ParsedPattern p) {
            this.section.define("\u0000#start\u0000", this.combine, p, this.startLocation, this.annotations);
        }
        
        State createChildState(final String localName) throws SAXException {
            final State tem = super.createChildState(localName);
            if (tem != null && this.childPatterns != null) {
                SchemaParser.this.error("start_multi_pattern");
            }
            return tem;
        }
    }
    
    abstract class NameClassContainerState extends State
    {
        State createChildState(final String localName) throws SAXException {
            final State state = SchemaParser.this.nameClassTable.get(localName);
            if (state == null) {
                SchemaParser.this.error("expected_name_class", localName);
                return null;
            }
            return state.create();
        }
    }
    
    class NameClassChildState extends NameClassContainerState
    {
        final State prevState;
        final NameClassRef nameClassRef;
        
        State create() {
            return null;
        }
        
        NameClassChildState(final State prevState, final NameClassRef nameClassRef) {
            this.prevState = prevState;
            this.nameClassRef = nameClassRef;
            this.setParent(prevState.parent);
            this.ns = prevState.ns;
        }
        
        void endChild(final ParsedNameClass nameClass) {
            this.nameClassRef.setNameClass(nameClass);
            this.prevState.set();
        }
        
        void endForeignChild(final ParsedElementAnnotation ea) {
            this.prevState.endForeignChild(ea);
        }
        
        void end() throws SAXException {
            this.nameClassRef.setNameClass(SchemaParser.this.nameClassBuilder.makeErrorNameClass());
            SchemaParser.this.error("missing_name_class");
            this.prevState.set();
            this.prevState.end();
        }
    }
    
    abstract class NameClassBaseState extends State
    {
        abstract ParsedNameClass makeNameClass() throws SAXException;
        
        void end() throws SAXException {
            this.parent.endChild(this.makeNameClass());
        }
    }
    
    class NameState extends NameClassBaseState
    {
        final StringBuffer buf;
        
        NameState() {
            this.buf = new StringBuffer();
        }
        
        State createChildState(final String localName) throws SAXException {
            SchemaParser.this.error("expected_name", localName);
            return null;
        }
        
        State create() {
            return new NameState();
        }
        
        public void characters(final char[] ch, final int start, final int len) {
            this.buf.append(ch, start, len);
        }
        
        void checkForeignElement() throws SAXException {
            SchemaParser.this.error("name_contains_foreign_element");
        }
        
        ParsedNameClass makeNameClass() throws SAXException {
            this.mergeLeadingComments();
            return SchemaParser.this.expandName(this.buf.toString().trim(), this.getNs(), this.annotations);
        }
    }
    
    class AnyNameState extends NameClassBaseState
    {
        ParsedNameClass except;
        
        AnyNameState() {
            this.except = null;
        }
        
        State create() {
            return new AnyNameState();
        }
        
        State createChildState(final String localName) throws SAXException {
            if (localName.equals("except")) {
                if (this.except != null) {
                    SchemaParser.this.error("multiple_except");
                }
                return new NameClassChoiceState(this.getContext());
            }
            SchemaParser.this.error("expected_except", localName);
            return null;
        }
        
        int getContext() {
            return 1;
        }
        
        ParsedNameClass makeNameClass() {
            if (this.except == null) {
                return this.makeNameClassNoExcept();
            }
            return this.makeNameClassExcept(this.except);
        }
        
        ParsedNameClass makeNameClassNoExcept() {
            return SchemaParser.this.nameClassBuilder.makeAnyName(this.startLocation, this.annotations);
        }
        
        ParsedNameClass makeNameClassExcept(final ParsedNameClass except) {
            return SchemaParser.this.nameClassBuilder.makeAnyName(except, this.startLocation, this.annotations);
        }
        
        void endChild(final ParsedNameClass nameClass) {
            this.except = nameClass;
        }
    }
    
    class NsNameState extends AnyNameState
    {
        State create() {
            return new NsNameState();
        }
        
        ParsedNameClass makeNameClassNoExcept() {
            return SchemaParser.this.nameClassBuilder.makeNsName(this.getNs(), null, null);
        }
        
        ParsedNameClass makeNameClassExcept(final ParsedNameClass except) {
            return SchemaParser.this.nameClassBuilder.makeNsName(this.getNs(), except, null, null);
        }
        
        int getContext() {
            return 2;
        }
    }
    
    class NameClassChoiceState extends NameClassContainerState
    {
        private ParsedNameClass[] nameClasses;
        private int nNameClasses;
        private int context;
        
        NameClassChoiceState() {
            this.context = 0;
        }
        
        NameClassChoiceState(final int context) {
            this.context = context;
        }
        
        void setParent(final State parent) {
            super.setParent(parent);
            if (parent instanceof NameClassChoiceState) {
                this.context = ((NameClassChoiceState)parent).context;
            }
        }
        
        State create() {
            return new NameClassChoiceState();
        }
        
        State createChildState(final String localName) throws SAXException {
            if (localName.equals("anyName")) {
                if (this.context >= 1) {
                    SchemaParser.this.error((this.context == 1) ? "any_name_except_contains_any_name" : "ns_name_except_contains_any_name");
                    return null;
                }
            }
            else if (localName.equals("nsName") && this.context == 2) {
                SchemaParser.this.error("ns_name_except_contains_ns_name");
                return null;
            }
            return super.createChildState(localName);
        }
        
        void endChild(final ParsedNameClass nc) {
            if (this.nameClasses == null) {
                this.nameClasses = new ParsedNameClass[5];
            }
            else if (this.nNameClasses >= this.nameClasses.length) {
                final ParsedNameClass[] newNameClasses = new ParsedNameClass[this.nameClasses.length * 2];
                System.arraycopy(this.nameClasses, 0, newNameClasses, 0, this.nameClasses.length);
                this.nameClasses = newNameClasses;
            }
            this.nameClasses[this.nNameClasses++] = nc;
        }
        
        void endForeignChild(final ParsedElementAnnotation ea) {
            if (this.nNameClasses == 0) {
                super.endForeignChild(ea);
            }
            else {
                this.nameClasses[this.nNameClasses - 1] = SchemaParser.this.nameClassBuilder.annotateAfter(this.nameClasses[this.nNameClasses - 1], ea);
            }
        }
        
        void end() throws SAXException {
            if (this.nNameClasses == 0) {
                SchemaParser.this.error("missing_name_class");
                this.parent.endChild(SchemaParser.this.nameClassBuilder.makeErrorNameClass());
                return;
            }
            if (this.comments != null) {
                this.nameClasses[this.nNameClasses - 1] = SchemaParser.this.nameClassBuilder.commentAfter(this.nameClasses[this.nNameClasses - 1], this.comments);
                this.comments = null;
            }
            this.parent.endChild(SchemaParser.this.nameClassBuilder.makeChoice(Arrays.asList(this.nameClasses).subList(0, this.nNameClasses), this.startLocation, this.annotations));
        }
    }
    
    class LexicalHandlerImpl extends AbstractLexicalHandler
    {
        private boolean inDtd;
        
        LexicalHandlerImpl() {
            this.inDtd = false;
        }
        
        public void startDTD(final String s, final String s1, final String s2) throws SAXException {
            this.inDtd = true;
        }
        
        public void endDTD() throws SAXException {
            this.inDtd = false;
        }
        
        public void comment(final char[] chars, final int start, final int length) throws SAXException {
            if (!this.inDtd) {
                ((CommentHandler)SchemaParser.this.xr.getContentHandler()).comment(new String(chars, start, length));
            }
        }
    }
    
    interface CommentHandler
    {
        void comment(final String p0);
    }
    
    interface NameClassRef
    {
        void setNameClass(final ParsedNameClass p0);
    }
}
