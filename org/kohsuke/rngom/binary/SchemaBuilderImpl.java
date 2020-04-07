// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary;

import org.kohsuke.rngom.ast.builder.Include;
import org.kohsuke.rngom.ast.builder.GrammarSection;
import java.util.Enumeration;
import java.util.Hashtable;
import org.kohsuke.rngom.ast.builder.IncludedGrammar;
import org.kohsuke.rngom.ast.builder.Div;
import org.kohsuke.rngom.ast.util.LocatorImpl;
import org.kohsuke.rngom.parse.Parseable;
import org.kohsuke.rngom.ast.om.ParsedElementAnnotation;
import org.kohsuke.rngom.ast.builder.Grammar;
import org.kohsuke.rngom.ast.builder.Scope;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.ValidationContext;
import org.kohsuke.rngom.parse.Context;
import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeException;
import org.kohsuke.rngom.ast.builder.DataPatternBuilder;
import org.kohsuke.rngom.nc.NameClass;
import org.kohsuke.rngom.ast.om.ParsedNameClass;
import org.xml.sax.Locator;
import org.kohsuke.rngom.ast.builder.Annotations;
import org.kohsuke.rngom.ast.om.Location;
import java.util.List;
import org.kohsuke.rngom.nc.NameClassBuilderImpl;
import org.kohsuke.rngom.dt.CascadingDatatypeLibraryFactory;
import org.kohsuke.rngom.dt.builtin.BuiltinDatatypeLibraryFactory;
import org.relaxng.datatype.helpers.DatatypeLibraryLoader;
import org.kohsuke.rngom.parse.IllegalSchemaException;
import org.xml.sax.SAXException;
import org.kohsuke.rngom.ast.builder.BuildException;
import org.xml.sax.SAXParseException;
import org.kohsuke.rngom.ast.om.ParsedPattern;
import org.kohsuke.rngom.util.Localizer;
import org.kohsuke.rngom.ast.builder.NameClassBuilder;
import org.xml.sax.ErrorHandler;
import org.relaxng.datatype.DatatypeLibraryFactory;
import org.kohsuke.rngom.ast.builder.CommentList;
import org.kohsuke.rngom.ast.builder.ElementAnnotationBuilder;
import org.kohsuke.rngom.ast.builder.SchemaBuilder;

public class SchemaBuilderImpl implements SchemaBuilder, ElementAnnotationBuilder, CommentList
{
    private final SchemaBuilderImpl parent;
    private boolean hadError;
    private final SchemaPatternBuilder pb;
    private final DatatypeLibraryFactory datatypeLibraryFactory;
    private final String inheritNs;
    private final ErrorHandler eh;
    private final OpenIncludes openIncludes;
    private final NameClassBuilder ncb;
    static final Localizer localizer;
    
    public ParsedPattern expandPattern(final ParsedPattern _pattern) throws BuildException, IllegalSchemaException {
        Pattern pattern = (Pattern)_pattern;
        if (!this.hadError) {
            try {
                pattern.checkRecursion(0);
                pattern = pattern.expand(this.pb);
                pattern.checkRestrictions(0, null, null);
                if (!this.hadError) {
                    return pattern;
                }
            }
            catch (SAXParseException e) {
                this.error(e);
            }
            catch (SAXException e2) {
                throw new BuildException(e2);
            }
            catch (RestrictionViolationException e3) {
                if (e3.getName() != null) {
                    this.error(e3.getMessageId(), e3.getName().toString(), e3.getLocator());
                }
                else {
                    this.error(e3.getMessageId(), e3.getLocator());
                }
            }
        }
        throw new IllegalSchemaException();
    }
    
    public SchemaBuilderImpl(final ErrorHandler eh) {
        this(eh, new CascadingDatatypeLibraryFactory(new DatatypeLibraryLoader(), new BuiltinDatatypeLibraryFactory(new DatatypeLibraryLoader())), new SchemaPatternBuilder());
    }
    
    public SchemaBuilderImpl(final ErrorHandler eh, final DatatypeLibraryFactory datatypeLibraryFactory, final SchemaPatternBuilder pb) {
        this.hadError = false;
        this.ncb = new NameClassBuilderImpl();
        this.parent = null;
        this.eh = eh;
        this.datatypeLibraryFactory = datatypeLibraryFactory;
        this.pb = pb;
        this.inheritNs = "";
        this.openIncludes = null;
    }
    
    private SchemaBuilderImpl(final String inheritNs, final String uri, final SchemaBuilderImpl parent) {
        this.hadError = false;
        this.ncb = new NameClassBuilderImpl();
        this.parent = parent;
        this.eh = parent.eh;
        this.datatypeLibraryFactory = parent.datatypeLibraryFactory;
        this.pb = parent.pb;
        this.inheritNs = inheritNs;
        this.openIncludes = new OpenIncludes(uri, parent.openIncludes);
    }
    
    public NameClassBuilder getNameClassBuilder() {
        return this.ncb;
    }
    
    public ParsedPattern makeChoice(final List patterns, final Location loc, final Annotations anno) throws BuildException {
        if (patterns.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Pattern result = patterns.get(0);
        for (int i = 1; i < patterns.size(); ++i) {
            result = this.pb.makeChoice(result, patterns.get(i));
        }
        return result;
    }
    
    public ParsedPattern makeInterleave(final List patterns, final Location loc, final Annotations anno) throws BuildException {
        if (patterns.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Pattern result = patterns.get(0);
        for (int i = 1; i < patterns.size(); ++i) {
            result = this.pb.makeInterleave(result, patterns.get(i));
        }
        return result;
    }
    
    public ParsedPattern makeGroup(final List patterns, final Location loc, final Annotations anno) throws BuildException {
        if (patterns.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Pattern result = patterns.get(0);
        for (int i = 1; i < patterns.size(); ++i) {
            result = this.pb.makeGroup(result, patterns.get(i));
        }
        return result;
    }
    
    public ParsedPattern makeOneOrMore(final ParsedPattern p, final Location loc, final Annotations anno) throws BuildException {
        return this.pb.makeOneOrMore((Pattern)p);
    }
    
    public ParsedPattern makeZeroOrMore(final ParsedPattern p, final Location loc, final Annotations anno) throws BuildException {
        return this.pb.makeZeroOrMore((Pattern)p);
    }
    
    public ParsedPattern makeOptional(final ParsedPattern p, final Location loc, final Annotations anno) throws BuildException {
        return this.pb.makeOptional((Pattern)p);
    }
    
    public ParsedPattern makeList(final ParsedPattern p, final Location loc, final Annotations anno) throws BuildException {
        return this.pb.makeList((Pattern)p, (Locator)loc);
    }
    
    public ParsedPattern makeMixed(final ParsedPattern p, final Location loc, final Annotations anno) throws BuildException {
        return this.pb.makeMixed((Pattern)p);
    }
    
    public ParsedPattern makeEmpty(final Location loc, final Annotations anno) {
        return this.pb.makeEmpty();
    }
    
    public ParsedPattern makeNotAllowed(final Location loc, final Annotations anno) {
        return this.pb.makeUnexpandedNotAllowed();
    }
    
    public ParsedPattern makeText(final Location loc, final Annotations anno) {
        return this.pb.makeText();
    }
    
    public ParsedPattern makeErrorPattern() {
        return this.pb.makeError();
    }
    
    public ParsedPattern makeAttribute(final ParsedNameClass nc, final ParsedPattern p, final Location loc, final Annotations anno) throws BuildException {
        return this.pb.makeAttribute((NameClass)nc, (Pattern)p, (Locator)loc);
    }
    
    public ParsedPattern makeElement(final ParsedNameClass nc, final ParsedPattern p, final Location loc, final Annotations anno) throws BuildException {
        return this.pb.makeElement((NameClass)nc, (Pattern)p, (Locator)loc);
    }
    
    public DataPatternBuilder makeDataPatternBuilder(final String datatypeLibrary, final String type, final Location loc) throws BuildException {
        final DatatypeLibrary dl = this.datatypeLibraryFactory.createDatatypeLibrary(datatypeLibrary);
        if (dl == null) {
            this.error("unrecognized_datatype_library", datatypeLibrary, (Locator)loc);
        }
        else {
            try {
                return new DataPatternBuilderImpl(dl.createDatatypeBuilder(type));
            }
            catch (DatatypeException e) {
                final String detail = e.getMessage();
                if (detail != null) {
                    this.error("unsupported_datatype_detail", datatypeLibrary, type, detail, (Locator)loc);
                }
                else {
                    this.error("unrecognized_datatype", datatypeLibrary, type, (Locator)loc);
                }
            }
        }
        return new DummyDataPatternBuilder();
    }
    
    public ParsedPattern makeValue(final String datatypeLibrary, final String type, final String value, final Context context, final String ns, final Location loc, final Annotations anno) throws BuildException {
        final DatatypeLibrary dl = this.datatypeLibraryFactory.createDatatypeLibrary(datatypeLibrary);
        if (dl == null) {
            this.error("unrecognized_datatype_library", datatypeLibrary, (Locator)loc);
        }
        else {
            try {
                final DatatypeBuilder dtb = dl.createDatatypeBuilder(type);
                try {
                    final Datatype dt = dtb.createDatatype();
                    final Object obj = dt.createValue(value, new ValidationContextImpl(context, ns));
                    if (obj != null) {
                        return this.pb.makeValue(dt, obj);
                    }
                    this.error("invalid_value", value, (Locator)loc);
                }
                catch (DatatypeException e) {
                    final String detail = e.getMessage();
                    if (detail != null) {
                        this.error("datatype_requires_param_detail", detail, (Locator)loc);
                    }
                    else {
                        this.error("datatype_requires_param", (Locator)loc);
                    }
                }
            }
            catch (DatatypeException e2) {
                this.error("unrecognized_datatype", datatypeLibrary, type, (Locator)loc);
            }
        }
        return this.pb.makeError();
    }
    
    public Grammar makeGrammar(final Scope parent) {
        return new GrammarImpl(this, parent);
    }
    
    public ParsedPattern annotate(final ParsedPattern p, final Annotations anno) throws BuildException {
        return p;
    }
    
    public ParsedPattern annotateAfter(final ParsedPattern p, final ParsedElementAnnotation e) throws BuildException {
        return p;
    }
    
    public ParsedPattern commentAfter(final ParsedPattern p, final CommentList comments) throws BuildException {
        return p;
    }
    
    public ParsedPattern makeExternalRef(final Parseable current, final String uri, final String ns, final Scope scope, final Location loc, final Annotations anno) throws BuildException {
        for (OpenIncludes inc = this.openIncludes; inc != null; inc = inc.parent) {
            if (inc.uri.equals(uri)) {
                this.error("recursive_include", uri, (Locator)loc);
                return this.pb.makeError();
            }
        }
        try {
            return current.parseExternal(uri, (SchemaBuilder<?, ParsedPattern, ?, ?, ?, ?>)new SchemaBuilderImpl(ns, uri, this), scope, ns);
        }
        catch (IllegalSchemaException e) {
            this.noteError();
            return this.pb.makeError();
        }
    }
    
    public Location makeLocation(final String systemId, final int lineNumber, final int columnNumber) {
        return new LocatorImpl(systemId, lineNumber, columnNumber);
    }
    
    public Annotations makeAnnotations(final CommentList comments, final Context context) {
        return this;
    }
    
    public ElementAnnotationBuilder makeElementAnnotationBuilder(final String ns, final String localName, final String prefix, final Location loc, final CommentList comments, final Context context) {
        return this;
    }
    
    public CommentList makeCommentList() {
        return this;
    }
    
    public void addComment(final String value, final Location loc) throws BuildException {
    }
    
    public void addAttribute(final String ns, final String localName, final String prefix, final String value, final Location loc) {
    }
    
    public void addElement(final ParsedElementAnnotation ea) {
    }
    
    public void addComment(final CommentList comments) throws BuildException {
    }
    
    public void addLeadingComment(final CommentList comments) throws BuildException {
    }
    
    public ParsedElementAnnotation makeElementAnnotation() {
        return null;
    }
    
    public void addText(final String value, final Location loc, final CommentList comments) throws BuildException {
    }
    
    public boolean usesComments() {
        return false;
    }
    
    private void error(final SAXParseException message) throws BuildException {
        this.noteError();
        try {
            if (this.eh != null) {
                this.eh.error(message);
            }
        }
        catch (SAXException e) {
            throw new BuildException(e);
        }
    }
    
    private void warning(final SAXParseException message) throws BuildException {
        try {
            if (this.eh != null) {
                this.eh.warning(message);
            }
        }
        catch (SAXException e) {
            throw new BuildException(e);
        }
    }
    
    private void error(final String key, final Locator loc) throws BuildException {
        this.error(new SAXParseException(SchemaBuilderImpl.localizer.message(key), loc));
    }
    
    private void error(final String key, final String arg, final Locator loc) throws BuildException {
        this.error(new SAXParseException(SchemaBuilderImpl.localizer.message(key, arg), loc));
    }
    
    private void error(final String key, final String arg1, final String arg2, final Locator loc) throws BuildException {
        this.error(new SAXParseException(SchemaBuilderImpl.localizer.message(key, arg1, arg2), loc));
    }
    
    private void error(final String key, final String arg1, final String arg2, final String arg3, final Locator loc) throws BuildException {
        this.error(new SAXParseException(SchemaBuilderImpl.localizer.message(key, new Object[] { arg1, arg2, arg3 }), loc));
    }
    
    private void noteError() {
        if (!this.hadError && this.parent != null) {
            this.parent.noteError();
        }
        this.hadError = true;
    }
    
    static {
        localizer = new Localizer(SchemaBuilderImpl.class);
    }
    
    static class OpenIncludes
    {
        final String uri;
        final OpenIncludes parent;
        
        OpenIncludes(final String uri, final OpenIncludes parent) {
            this.uri = uri;
            this.parent = parent;
        }
    }
    
    private class DummyDataPatternBuilder implements DataPatternBuilder
    {
        public void addParam(final String name, final String value, final Context context, final String ns, final Location loc, final Annotations anno) throws BuildException {
        }
        
        public ParsedPattern makePattern(final Location loc, final Annotations anno) throws BuildException {
            return SchemaBuilderImpl.this.pb.makeError();
        }
        
        public ParsedPattern makePattern(final ParsedPattern except, final Location loc, final Annotations anno) throws BuildException {
            return SchemaBuilderImpl.this.pb.makeError();
        }
        
        public void annotation(final ParsedElementAnnotation ea) {
        }
    }
    
    private class ValidationContextImpl implements ValidationContext
    {
        private ValidationContext vc;
        private String ns;
        
        ValidationContextImpl(final ValidationContext vc, final String ns) {
            this.vc = vc;
            this.ns = ((ns.length() == 0) ? null : ns);
        }
        
        public String resolveNamespacePrefix(final String prefix) {
            return (prefix.length() == 0) ? this.ns : this.vc.resolveNamespacePrefix(prefix);
        }
        
        public String getBaseUri() {
            return this.vc.getBaseUri();
        }
        
        public boolean isUnparsedEntity(final String entityName) {
            return this.vc.isUnparsedEntity(entityName);
        }
        
        public boolean isNotation(final String notationName) {
            return this.vc.isNotation(notationName);
        }
    }
    
    private class DataPatternBuilderImpl implements DataPatternBuilder
    {
        private DatatypeBuilder dtb;
        
        DataPatternBuilderImpl(final DatatypeBuilder dtb) {
            this.dtb = dtb;
        }
        
        public void addParam(final String name, final String value, final Context context, final String ns, final Location loc, final Annotations anno) throws BuildException {
            try {
                this.dtb.addParameter(name, value, new ValidationContextImpl(context, ns));
            }
            catch (DatatypeException e) {
                final String detail = e.getMessage();
                final int pos = e.getIndex();
                String displayedParam;
                if (pos == -1) {
                    displayedParam = null;
                }
                else {
                    displayedParam = this.displayParam(value, pos);
                }
                if (displayedParam != null) {
                    if (detail != null) {
                        SchemaBuilderImpl.this.error("invalid_param_detail_display", detail, displayedParam, (Locator)loc);
                    }
                    else {
                        SchemaBuilderImpl.this.error("invalid_param_display", displayedParam, (Locator)loc);
                    }
                }
                else if (detail != null) {
                    SchemaBuilderImpl.this.error("invalid_param_detail", detail, (Locator)loc);
                }
                else {
                    SchemaBuilderImpl.this.error("invalid_param", (Locator)loc);
                }
            }
        }
        
        String displayParam(final String value, int pos) {
            if (pos < 0) {
                pos = 0;
            }
            else if (pos > value.length()) {
                pos = value.length();
            }
            return SchemaBuilderImpl.localizer.message("display_param", value.substring(0, pos), value.substring(pos));
        }
        
        public ParsedPattern makePattern(final Location loc, final Annotations anno) throws BuildException {
            try {
                return SchemaBuilderImpl.this.pb.makeData(this.dtb.createDatatype());
            }
            catch (DatatypeException e) {
                final String detail = e.getMessage();
                if (detail != null) {
                    SchemaBuilderImpl.this.error("invalid_params_detail", detail, (Locator)loc);
                }
                else {
                    SchemaBuilderImpl.this.error("invalid_params", (Locator)loc);
                }
                return SchemaBuilderImpl.this.pb.makeError();
            }
        }
        
        public ParsedPattern makePattern(final ParsedPattern except, final Location loc, final Annotations anno) throws BuildException {
            try {
                return SchemaBuilderImpl.this.pb.makeDataExcept(this.dtb.createDatatype(), (Pattern)except, (Locator)loc);
            }
            catch (DatatypeException e) {
                final String detail = e.getMessage();
                if (detail != null) {
                    SchemaBuilderImpl.this.error("invalid_params_detail", detail, (Locator)loc);
                }
                else {
                    SchemaBuilderImpl.this.error("invalid_params", (Locator)loc);
                }
                return SchemaBuilderImpl.this.pb.makeError();
            }
        }
        
        public void annotation(final ParsedElementAnnotation ea) {
        }
    }
    
    static class GrammarImpl implements Grammar, Div, IncludedGrammar
    {
        private final SchemaBuilderImpl sb;
        private final Hashtable defines;
        private final RefPattern startRef;
        private final Scope parent;
        
        private GrammarImpl(final SchemaBuilderImpl sb, final Scope parent) {
            this.sb = sb;
            this.parent = parent;
            this.defines = new Hashtable();
            this.startRef = new RefPattern(null);
        }
        
        protected GrammarImpl(final SchemaBuilderImpl sb, final GrammarImpl g) {
            this.sb = sb;
            this.parent = g.parent;
            this.startRef = g.startRef;
            this.defines = g.defines;
        }
        
        public ParsedPattern endGrammar(final Location loc, final Annotations anno) throws BuildException {
            final Enumeration e = this.defines.keys();
            while (e.hasMoreElements()) {
                final String name = e.nextElement();
                final RefPattern rp = this.defines.get(name);
                if (rp.getPattern() == null) {
                    this.sb.error("reference_to_undefined", name, rp.getRefLocator());
                    rp.setPattern(this.sb.pb.makeError());
                }
            }
            Pattern start = this.startRef.getPattern();
            if (start == null) {
                this.sb.error("missing_start_element", (Locator)loc);
                start = this.sb.pb.makeError();
            }
            return start;
        }
        
        public void endDiv(final Location loc, final Annotations anno) throws BuildException {
        }
        
        public ParsedPattern endIncludedGrammar(final Location loc, final Annotations anno) throws BuildException {
            return null;
        }
        
        public void define(final String name, final GrammarSection.Combine combine, final ParsedPattern pattern, final Location loc, final Annotations anno) throws BuildException {
            this.define(this.lookup(name), combine, pattern, loc);
        }
        
        private void define(final RefPattern rp, final GrammarSection.Combine combine, final ParsedPattern pattern, final Location loc) throws BuildException {
            switch (rp.getReplacementStatus()) {
                case 0: {
                    if (combine == null) {
                        if (rp.isCombineImplicit()) {
                            if (rp.getName() == null) {
                                this.sb.error("duplicate_start", (Locator)loc);
                            }
                            else {
                                this.sb.error("duplicate_define", rp.getName(), (Locator)loc);
                            }
                        }
                        else {
                            rp.setCombineImplicit();
                        }
                    }
                    else {
                        final byte combineType = (byte)((combine == GrammarImpl.COMBINE_CHOICE) ? 1 : 2);
                        if (rp.getCombineType() != 0 && rp.getCombineType() != combineType) {
                            if (rp.getName() == null) {
                                this.sb.error("conflict_combine_start", (Locator)loc);
                            }
                            else {
                                this.sb.error("conflict_combine_define", rp.getName(), (Locator)loc);
                            }
                        }
                        rp.setCombineType(combineType);
                    }
                    final Pattern p = (Pattern)pattern;
                    if (rp.getPattern() == null) {
                        rp.setPattern(p);
                        break;
                    }
                    if (rp.getCombineType() == 2) {
                        rp.setPattern(this.sb.pb.makeInterleave(rp.getPattern(), p));
                        break;
                    }
                    rp.setPattern(this.sb.pb.makeChoice(rp.getPattern(), p));
                    break;
                }
                case 1: {
                    rp.setReplacementStatus((byte)2);
                    break;
                }
            }
        }
        
        public void topLevelAnnotation(final ParsedElementAnnotation ea) throws BuildException {
        }
        
        public void topLevelComment(final CommentList comments) throws BuildException {
        }
        
        private RefPattern lookup(final String name) {
            if (name == "\u0000#start\u0000") {
                return this.startRef;
            }
            return this.lookup1(name);
        }
        
        private RefPattern lookup1(final String name) {
            RefPattern p = this.defines.get(name);
            if (p == null) {
                p = new RefPattern(name);
                this.defines.put(name, p);
            }
            return p;
        }
        
        public ParsedPattern makeRef(final String name, final Location loc, final Annotations anno) throws BuildException {
            final RefPattern p = this.lookup1(name);
            if (p.getRefLocator() == null && loc != null) {
                p.setRefLocator((Locator)loc);
            }
            return p;
        }
        
        public ParsedPattern makeParentRef(final String name, final Location loc, final Annotations anno) throws BuildException {
            if (this.parent == null) {
                this.sb.error("parent_ref_outside_grammar", (Locator)loc);
                return this.sb.makeErrorPattern();
            }
            return this.parent.makeRef(name, loc, anno);
        }
        
        public Div makeDiv() {
            return this;
        }
        
        public Include makeInclude() {
            return new IncludeImpl(this.sb, this);
        }
    }
    
    static class Override
    {
        RefPattern prp;
        Override next;
        byte replacementStatus;
        
        Override(final RefPattern prp, final Override next) {
            this.prp = prp;
            this.next = next;
        }
    }
    
    private static class IncludeImpl implements Include, Div
    {
        private SchemaBuilderImpl sb;
        private Override overrides;
        private GrammarImpl grammar;
        
        private IncludeImpl(final SchemaBuilderImpl sb, final GrammarImpl grammar) {
            this.sb = sb;
            this.grammar = grammar;
        }
        
        public void define(final String name, final GrammarSection.Combine combine, final ParsedPattern pattern, final Location loc, final Annotations anno) throws BuildException {
            final RefPattern rp = this.grammar.lookup(name);
            this.overrides = new Override(rp, this.overrides);
            this.grammar.define(rp, combine, pattern, loc);
        }
        
        public void endDiv(final Location loc, final Annotations anno) throws BuildException {
        }
        
        public void topLevelAnnotation(final ParsedElementAnnotation ea) throws BuildException {
        }
        
        public void topLevelComment(final CommentList comments) throws BuildException {
        }
        
        public Div makeDiv() {
            return this;
        }
        
        public void endInclude(final Parseable current, final String uri, final String ns, final Location loc, final Annotations anno) throws BuildException {
            for (OpenIncludes inc = this.sb.openIncludes; inc != null; inc = inc.parent) {
                if (inc.uri.equals(uri)) {
                    this.sb.error("recursive_include", uri, (Locator)loc);
                    return;
                }
            }
            for (Override o = this.overrides; o != null; o = o.next) {
                o.replacementStatus = o.prp.getReplacementStatus();
                o.prp.setReplacementStatus((byte)1);
            }
            try {
                final SchemaBuilderImpl isb = new SchemaBuilderImpl(ns, uri, this.sb, null);
                current.parseInclude(uri, isb, (IncludedGrammar<ParsedPattern, ?, ?, ?, ?>)new GrammarImpl(isb, this.grammar), ns);
                for (Override o2 = this.overrides; o2 != null; o2 = o2.next) {
                    if (o2.prp.getReplacementStatus() == 1) {
                        if (o2.prp.getName() == null) {
                            this.sb.error("missing_start_replacement", (Locator)loc);
                        }
                        else {
                            this.sb.error("missing_define_replacement", o2.prp.getName(), (Locator)loc);
                        }
                    }
                }
            }
            catch (IllegalSchemaException e) {
                this.sb.noteError();
            }
            finally {
                for (Override o3 = this.overrides; o3 != null; o3 = o3.next) {
                    o3.prp.setReplacementStatus(o3.replacementStatus);
                }
            }
        }
        
        public Include makeInclude() {
            return null;
        }
    }
}
