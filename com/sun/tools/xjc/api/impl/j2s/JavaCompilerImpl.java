// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api.impl.j2s;

import com.sun.xml.bind.v2.runtime.IllegalAnnotationException;
import com.sun.mirror.apt.Messager;
import java.util.Iterator;
import com.sun.xml.bind.v2.model.core.TypeInfoSet;
import java.util.HashMap;
import com.sun.xml.bind.v2.model.core.Ref;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.sun.xml.bind.v2.model.core.ErrorHandler;
import com.sun.xml.bind.v2.model.nav.Navigator;
import com.sun.xml.bind.v2.model.annotation.AnnotationReader;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;
import com.sun.mirror.type.TypeMirror;
import com.sun.xml.bind.v2.model.impl.ModelBuilder;
import java.util.Collections;
import com.sun.tools.jxc.model.nav.APTNavigator;
import com.sun.tools.jxc.apt.InlineAnnotationReaderImpl;
import com.sun.tools.xjc.api.J2SJAXBModel;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import javax.xml.namespace.QName;
import java.util.Map;
import com.sun.tools.xjc.api.Reference;
import java.util.Collection;
import com.sun.tools.xjc.api.JavaCompiler;

public class JavaCompilerImpl implements JavaCompiler
{
    public J2SJAXBModel bind(final Collection<Reference> rootClasses, Map<QName, Reference> additionalElementDecls, final String defaultNamespaceRemap, final AnnotationProcessorEnvironment env) {
        final ModelBuilder<TypeMirror, TypeDeclaration, FieldDeclaration, MethodDeclaration> builder = new ModelBuilder<TypeMirror, TypeDeclaration, FieldDeclaration, MethodDeclaration>(InlineAnnotationReaderImpl.theInstance, new APTNavigator(env), Collections.emptyMap(), defaultNamespaceRemap);
        builder.setErrorHandler(new ErrorHandlerImpl(env.getMessager()));
        for (final Reference ref : rootClasses) {
            final TypeMirror t = ref.type;
            final XmlJavaTypeAdapter xjta = (XmlJavaTypeAdapter)ref.annotations.getAnnotation((Class)XmlJavaTypeAdapter.class);
            final XmlList xl = (XmlList)ref.annotations.getAnnotation((Class)XmlList.class);
            builder.getTypeInfo(new Ref<TypeMirror, TypeDeclaration>(builder, t, xjta, xl));
        }
        final TypeInfoSet r = builder.link();
        if (r == null) {
            return null;
        }
        if (additionalElementDecls == null) {
            additionalElementDecls = Collections.emptyMap();
        }
        else {
            for (final Map.Entry<QName, ? extends Reference> e : additionalElementDecls.entrySet()) {
                if (e.getKey() == null) {
                    throw new IllegalArgumentException("nulls in additionalElementDecls");
                }
            }
        }
        return new JAXBModelImpl(r, builder.reader, rootClasses, new HashMap<QName, Reference>(additionalElementDecls));
    }
    
    private static final class ErrorHandlerImpl implements ErrorHandler
    {
        private final Messager messager;
        
        public ErrorHandlerImpl(final Messager messager) {
            this.messager = messager;
        }
        
        public void error(final IllegalAnnotationException e) {
            this.messager.printError(e.toString());
        }
    }
}
