// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

import java.util.Collection;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public final class JEnumConstant extends JExpressionImpl implements JDeclaration, JAnnotatable
{
    private final String name;
    private final JDefinedClass type;
    private JDocComment jdoc;
    private List<JAnnotationUse> annotations;
    private List<JExpression> args;
    
    JEnumConstant(final JDefinedClass type, final String name) {
        this.jdoc = null;
        this.annotations = null;
        this.args = null;
        this.name = name;
        this.type = type;
    }
    
    public JEnumConstant arg(final JExpression arg) {
        if (arg == null) {
            throw new IllegalArgumentException();
        }
        if (this.args == null) {
            this.args = new ArrayList<JExpression>();
        }
        this.args.add(arg);
        return this;
    }
    
    public String getName() {
        return this.type.fullName().concat(".").concat(this.name);
    }
    
    public JDocComment javadoc() {
        if (this.jdoc == null) {
            this.jdoc = new JDocComment(this.type.owner());
        }
        return this.jdoc;
    }
    
    public JAnnotationUse annotate(final JClass clazz) {
        if (this.annotations == null) {
            this.annotations = new ArrayList<JAnnotationUse>();
        }
        final JAnnotationUse a = new JAnnotationUse(clazz);
        this.annotations.add(a);
        return a;
    }
    
    public JAnnotationUse annotate(final Class<? extends Annotation> clazz) {
        return this.annotate(this.type.owner().ref(clazz));
    }
    
    public <W extends JAnnotationWriter> W annotate2(final Class<W> clazz) {
        return TypedAnnotationWriter.create(clazz, this);
    }
    
    public void declare(final JFormatter f) {
        if (this.jdoc != null) {
            f.nl().g(this.jdoc);
        }
        if (this.annotations != null) {
            for (int i = 0; i < this.annotations.size(); ++i) {
                f.g(this.annotations.get(i)).nl();
            }
        }
        f.id(this.name);
        if (this.args != null) {
            f.p('(').g(this.args).p(')');
        }
    }
    
    public void generate(final JFormatter f) {
        f.t(this.type).p('.').p(this.name);
    }
}
