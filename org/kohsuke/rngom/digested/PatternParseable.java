// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

import java.util.List;
import java.util.ArrayList;
import org.kohsuke.rngom.ast.om.ParsedNameClass;
import org.kohsuke.rngom.nc.NameClass;
import org.xml.sax.Locator;
import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.ast.builder.Annotations;
import org.kohsuke.rngom.ast.builder.Scope;
import org.kohsuke.rngom.ast.builder.IncludedGrammar;
import org.kohsuke.rngom.ast.builder.BuildException;
import org.kohsuke.rngom.ast.om.ParsedPattern;
import org.kohsuke.rngom.ast.builder.SchemaBuilder;
import org.kohsuke.rngom.parse.Parseable;

final class PatternParseable implements Parseable
{
    private final DPattern pattern;
    
    public PatternParseable(final DPattern p) {
        this.pattern = p;
    }
    
    public ParsedPattern parse(final SchemaBuilder sb) throws BuildException {
        return this.pattern.accept((DPatternVisitor<ParsedPattern>)new Parser(sb));
    }
    
    public ParsedPattern parseInclude(final String uri, final SchemaBuilder f, final IncludedGrammar g, final String inheritedNs) throws BuildException {
        throw new UnsupportedOperationException();
    }
    
    public ParsedPattern parseExternal(final String uri, final SchemaBuilder f, final Scope s, final String inheritedNs) throws BuildException {
        throw new UnsupportedOperationException();
    }
    
    private static class Parser implements DPatternVisitor<ParsedPattern>
    {
        private final SchemaBuilder sb;
        
        public Parser(final SchemaBuilder sb) {
            this.sb = sb;
        }
        
        private Annotations parseAnnotation(final DPattern p) {
            return null;
        }
        
        private Location parseLocation(final DPattern p) {
            final Locator l = p.getLocation();
            return this.sb.makeLocation(l.getSystemId(), l.getLineNumber(), l.getColumnNumber());
        }
        
        private ParsedNameClass parseNameClass(final NameClass name) {
            return name;
        }
        
        public ParsedPattern onAttribute(final DAttributePattern p) {
            return this.sb.makeAttribute(this.parseNameClass(p.getName()), p.getChild().accept((DPatternVisitor<ParsedPattern>)this), this.parseLocation(p), this.parseAnnotation(p));
        }
        
        public ParsedPattern onChoice(final DChoicePattern p) {
            final List<ParsedPattern> kids = new ArrayList<ParsedPattern>();
            for (DPattern c = p.firstChild(); c != null; c = c.next) {
                kids.add(c.accept((DPatternVisitor<ParsedPattern>)this));
            }
            return this.sb.makeChoice(kids, this.parseLocation(p), null);
        }
        
        public ParsedPattern onData(final DDataPattern p) {
            return null;
        }
        
        public ParsedPattern onElement(final DElementPattern p) {
            return this.sb.makeElement(this.parseNameClass(p.getName()), p.getChild().accept((DPatternVisitor<ParsedPattern>)this), this.parseLocation(p), this.parseAnnotation(p));
        }
        
        public ParsedPattern onEmpty(final DEmptyPattern p) {
            return this.sb.makeEmpty(this.parseLocation(p), this.parseAnnotation(p));
        }
        
        public ParsedPattern onGrammar(final DGrammarPattern p) {
            return null;
        }
        
        public ParsedPattern onGroup(final DGroupPattern p) {
            final List<ParsedPattern> kids = new ArrayList<ParsedPattern>();
            for (DPattern c = p.firstChild(); c != null; c = c.next) {
                kids.add(c.accept((DPatternVisitor<ParsedPattern>)this));
            }
            return this.sb.makeGroup(kids, this.parseLocation(p), null);
        }
        
        public ParsedPattern onInterleave(final DInterleavePattern p) {
            final List<ParsedPattern> kids = new ArrayList<ParsedPattern>();
            for (DPattern c = p.firstChild(); c != null; c = c.next) {
                kids.add(c.accept((DPatternVisitor<ParsedPattern>)this));
            }
            return this.sb.makeInterleave(kids, this.parseLocation(p), null);
        }
        
        public ParsedPattern onList(final DListPattern p) {
            return this.sb.makeList(p.getChild().accept((DPatternVisitor<ParsedPattern>)this), this.parseLocation(p), this.parseAnnotation(p));
        }
        
        public ParsedPattern onMixed(final DMixedPattern p) {
            return this.sb.makeMixed(p.getChild().accept((DPatternVisitor<ParsedPattern>)this), this.parseLocation(p), this.parseAnnotation(p));
        }
        
        public ParsedPattern onNotAllowed(final DNotAllowedPattern p) {
            return this.sb.makeNotAllowed(this.parseLocation(p), this.parseAnnotation(p));
        }
        
        public ParsedPattern onOneOrMore(final DOneOrMorePattern p) {
            return this.sb.makeOneOrMore(p.getChild().accept((DPatternVisitor<ParsedPattern>)this), this.parseLocation(p), this.parseAnnotation(p));
        }
        
        public ParsedPattern onOptional(final DOptionalPattern p) {
            return this.sb.makeOptional(p.getChild().accept((DPatternVisitor<ParsedPattern>)this), this.parseLocation(p), this.parseAnnotation(p));
        }
        
        public ParsedPattern onRef(final DRefPattern p) {
            return null;
        }
        
        public ParsedPattern onText(final DTextPattern p) {
            return this.sb.makeText(this.parseLocation(p), this.parseAnnotation(p));
        }
        
        public ParsedPattern onValue(final DValuePattern p) {
            return this.sb.makeValue(p.getDatatypeLibrary(), p.getType(), p.getValue(), p.getContext(), p.getNs(), this.parseLocation(p), this.parseAnnotation(p));
        }
        
        public ParsedPattern onZeroOrMore(final DZeroOrMorePattern p) {
            return this.sb.makeZeroOrMore(p.getChild().accept((DPatternVisitor<ParsedPattern>)this), this.parseLocation(p), this.parseAnnotation(p));
        }
    }
}
