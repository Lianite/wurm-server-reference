// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JClassContainer;
import javax.xml.bind.annotation.XmlAccessType;
import com.sun.tools.xjc.generator.annotation.spec.XmlAccessorTypeWriter;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.outline.Outline;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlEnum;

@XmlEnum(Boolean.class)
public enum ImplStructureStrategy
{
    BEAN_ONLY {
        protected Result createClasses(final Outline outline, final CClassInfo bean) {
            final JClassContainer parent = outline.getContainer(bean.parent(), Aspect.EXPOSED);
            final JDefinedClass impl = outline.getClassFactory().createClass(parent, 0x1 | (parent.isPackage() ? 0 : 16) | (bean.isAbstract() ? 32 : 0), bean.shortName, bean.getLocator());
            impl.annotate2(XmlAccessorTypeWriter.class).value(XmlAccessType.FIELD);
            return new Result(impl, impl);
        }
        
        protected JPackage getPackage(final JPackage pkg, final Aspect a) {
            return pkg;
        }
        
        protected MethodWriter createMethodWriter(final ClassOutlineImpl target) {
            assert target.ref == target.implClass;
            return new MethodWriter(target) {
                private final JDefinedClass impl = target.implClass;
                private JMethod implMethod;
                
                public JVar addParameter(final JType type, final String name) {
                    return this.implMethod.param(type, name);
                }
                
                public JMethod declareMethod(final JType returnType, final String methodName) {
                    return this.implMethod = this.impl.method(1, returnType, methodName);
                }
                
                public JDocComment javadoc() {
                    return this.implMethod.javadoc();
                }
            };
        }
        
        protected void _extends(final ClassOutlineImpl derived, final ClassOutlineImpl base) {
            derived.implClass._extends(base.implRef);
        }
    }, 
    INTF_AND_IMPL {
        protected Result createClasses(final Outline outline, final CClassInfo bean) {
            JClassContainer parent = outline.getContainer(bean.parent(), Aspect.EXPOSED);
            final JDefinedClass intf = outline.getClassFactory().createInterface(parent, bean.shortName, bean.getLocator());
            parent = outline.getContainer(bean.parent(), Aspect.IMPLEMENTATION);
            final JDefinedClass impl = outline.getClassFactory().createClass(parent, 0x1 | (parent.isPackage() ? 0 : 16) | (bean.isAbstract() ? 32 : 0), bean.shortName + "Impl", bean.getLocator());
            impl.annotate2(XmlAccessorTypeWriter.class).value(XmlAccessType.FIELD);
            impl._implements(intf);
            return new Result(intf, impl);
        }
        
        protected JPackage getPackage(final JPackage pkg, final Aspect a) {
            switch (a) {
                case EXPOSED: {
                    return pkg;
                }
                case IMPLEMENTATION: {
                    return pkg.subPackage("impl");
                }
                default: {
                    assert false;
                    throw new IllegalStateException();
                }
            }
        }
        
        protected MethodWriter createMethodWriter(final ClassOutlineImpl target) {
            return new MethodWriter(target) {
                private final JDefinedClass intf = target.ref;
                private final JDefinedClass impl = target.implClass;
                private JMethod intfMethod;
                private JMethod implMethod;
                
                public JVar addParameter(final JType type, final String name) {
                    if (this.intf != null) {
                        this.intfMethod.param(type, name);
                    }
                    return this.implMethod.param(type, name);
                }
                
                public JMethod declareMethod(final JType returnType, final String methodName) {
                    if (this.intf != null) {
                        this.intfMethod = this.intf.method(0, returnType, methodName);
                    }
                    return this.implMethod = this.impl.method(1, returnType, methodName);
                }
                
                public JDocComment javadoc() {
                    if (this.intf != null) {
                        return this.intfMethod.javadoc();
                    }
                    return this.implMethod.javadoc();
                }
            };
        }
        
        protected void _extends(final ClassOutlineImpl derived, final ClassOutlineImpl base) {
            derived.implClass._extends(base.implRef);
            derived.ref._implements(base.ref);
        }
    };
    
    protected abstract Result createClasses(final Outline p0, final CClassInfo p1);
    
    protected abstract JPackage getPackage(final JPackage p0, final Aspect p1);
    
    protected abstract MethodWriter createMethodWriter(final ClassOutlineImpl p0);
    
    protected abstract void _extends(final ClassOutlineImpl p0, final ClassOutlineImpl p1);
    
    public static final class Result
    {
        public final JDefinedClass exposed;
        public final JDefinedClass implementation;
        
        public Result(final JDefinedClass exposed, final JDefinedClass implementation) {
            this.exposed = exposed;
            this.implementation = implementation;
        }
    }
}
