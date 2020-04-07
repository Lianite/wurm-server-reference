// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class JNarrowedClass extends JClass
{
    final JClass basis;
    private final List<JClass> args;
    
    JNarrowedClass(final JClass basis, final JClass arg) {
        this(basis, Collections.singletonList(arg));
    }
    
    JNarrowedClass(final JClass basis, final List<JClass> args) {
        super(basis.owner());
        this.basis = basis;
        assert !(basis instanceof JNarrowedClass);
        this.args = args;
    }
    
    public JClass narrow(final JClass clazz) {
        final List<JClass> newArgs = new ArrayList<JClass>(this.args);
        newArgs.add(clazz);
        return new JNarrowedClass(this.basis, newArgs);
    }
    
    public JClass narrow(final JClass... clazz) {
        final List<JClass> newArgs = new ArrayList<JClass>(this.args);
        for (final JClass c : clazz) {
            newArgs.add(c);
        }
        return new JNarrowedClass(this.basis, newArgs);
    }
    
    public String name() {
        final StringBuffer buf = new StringBuffer();
        buf.append(this.basis.name());
        buf.append('<');
        boolean first = true;
        for (final JClass c : this.args) {
            if (first) {
                first = false;
            }
            else {
                buf.append(',');
            }
            buf.append(c.name());
        }
        buf.append('>');
        return buf.toString();
    }
    
    public String fullName() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.basis.fullName());
        buf.append('<');
        boolean first = true;
        for (final JClass c : this.args) {
            if (first) {
                first = false;
            }
            else {
                buf.append(',');
            }
            buf.append(c.fullName());
        }
        buf.append('>');
        return buf.toString();
    }
    
    public String binaryName() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.basis.binaryName());
        buf.append('<');
        boolean first = true;
        for (final JClass c : this.args) {
            if (first) {
                first = false;
            }
            else {
                buf.append(',');
            }
            buf.append(c.binaryName());
        }
        buf.append('>');
        return buf.toString();
    }
    
    public void generate(final JFormatter f) {
        f.t(this.basis).p('<').g(this.args).p('\uffff');
    }
    
    void printLink(final JFormatter f) {
        this.basis.printLink(f);
        f.p("{@code <}");
        boolean first = true;
        for (final JClass c : this.args) {
            if (first) {
                first = false;
            }
            else {
                f.p(',');
            }
            c.printLink(f);
        }
        f.p("{@code >}");
    }
    
    public JPackage _package() {
        return this.basis._package();
    }
    
    public JClass _extends() {
        final JClass base = this.basis._extends();
        if (base == null) {
            return base;
        }
        return base.substituteParams(this.basis.typeParams(), this.args);
    }
    
    public Iterator<JClass> _implements() {
        return new Iterator<JClass>() {
            private final Iterator<JClass> core = JNarrowedClass.this.basis._implements();
            
            public void remove() {
                this.core.remove();
            }
            
            public JClass next() {
                return this.core.next().substituteParams(JNarrowedClass.this.basis.typeParams(), JNarrowedClass.this.args);
            }
            
            public boolean hasNext() {
                return this.core.hasNext();
            }
        };
    }
    
    public JClass erasure() {
        return this.basis;
    }
    
    public boolean isInterface() {
        return this.basis.isInterface();
    }
    
    public boolean isAbstract() {
        return this.basis.isAbstract();
    }
    
    public boolean isArray() {
        return false;
    }
    
    public boolean equals(final Object obj) {
        return obj instanceof JNarrowedClass && this.fullName().equals(((JClass)obj).fullName());
    }
    
    public int hashCode() {
        return this.fullName().hashCode();
    }
    
    protected JClass substituteParams(final JTypeVar[] variables, final List<JClass> bindings) {
        final JClass b = this.basis.substituteParams(variables, bindings);
        boolean different = b != this.basis;
        final List<JClass> clazz = new ArrayList<JClass>(this.args.size());
        for (int i = 0; i < clazz.size(); ++i) {
            final JClass c = this.args.get(i).substituteParams(variables, bindings);
            clazz.set(i, c);
            different |= (c != this.args.get(i));
        }
        if (different) {
            return new JNarrowedClass(b, clazz);
        }
        return this;
    }
    
    public List<JClass> getTypeParameters() {
        return this.args;
    }
}
