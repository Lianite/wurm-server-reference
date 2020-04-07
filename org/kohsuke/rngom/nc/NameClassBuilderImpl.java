// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.nc;

import org.kohsuke.rngom.ast.om.ParsedNameClass;
import org.kohsuke.rngom.ast.builder.BuildException;
import java.util.List;
import org.kohsuke.rngom.ast.builder.NameClassBuilder;
import org.kohsuke.rngom.ast.builder.CommentList;
import org.kohsuke.rngom.ast.builder.Annotations;
import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.ast.om.ParsedElementAnnotation;

public class NameClassBuilderImpl<E extends ParsedElementAnnotation, L extends Location, A extends Annotations<E, L, CL>, CL extends CommentList<L>> implements NameClassBuilder<NameClass, E, L, A, CL>
{
    public NameClass makeChoice(final List<NameClass> nameClasses, final L loc, final A anno) {
        NameClass result = nameClasses.get(0);
        for (int i = 1; i < nameClasses.size(); ++i) {
            result = new ChoiceNameClass(result, nameClasses.get(i));
        }
        return result;
    }
    
    public NameClass makeName(final String ns, final String localName, final String prefix, final L loc, final A anno) {
        return new SimpleNameClass(ns, localName);
    }
    
    public NameClass makeNsName(final String ns, final L loc, final A anno) {
        return new NsNameClass(ns);
    }
    
    public NameClass makeNsName(final String ns, final NameClass except, final L loc, final A anno) {
        return new NsNameExceptNameClass(ns, except);
    }
    
    public NameClass makeAnyName(final L loc, final A anno) {
        return NameClass.ANY;
    }
    
    public NameClass makeAnyName(final NameClass except, final L loc, final A anno) {
        return new AnyNameExceptNameClass(except);
    }
    
    public NameClass makeErrorNameClass() {
        return NameClass.NULL;
    }
    
    public NameClass annotate(final NameClass nc, final A anno) throws BuildException {
        return nc;
    }
    
    public NameClass annotateAfter(final NameClass nc, final E e) throws BuildException {
        return nc;
    }
    
    public NameClass commentAfter(final NameClass nc, final CL comments) throws BuildException {
        return nc;
    }
}
