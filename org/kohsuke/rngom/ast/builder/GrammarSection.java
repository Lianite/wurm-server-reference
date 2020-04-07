// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.ast.builder;

import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.ast.om.ParsedElementAnnotation;
import org.kohsuke.rngom.ast.om.ParsedPattern;

public interface GrammarSection<P extends ParsedPattern, E extends ParsedElementAnnotation, L extends Location, A extends Annotations<E, L, CL>, CL extends CommentList<L>>
{
    public static final Combine COMBINE_CHOICE = new Combine("choice");
    public static final Combine COMBINE_INTERLEAVE = new Combine("interleave");
    public static final String START = "\u0000#start\u0000";
    
    void define(final String p0, final Combine p1, final P p2, final L p3, final A p4) throws BuildException;
    
    void topLevelAnnotation(final E p0) throws BuildException;
    
    void topLevelComment(final CL p0) throws BuildException;
    
    Div<P, E, L, A, CL> makeDiv();
    
    Include<P, E, L, A, CL> makeInclude();
    
    public static final class Combine
    {
        private final String name;
        
        private Combine(final String name) {
            this.name = name;
        }
        
        public final String toString() {
            return this.name;
        }
    }
}
