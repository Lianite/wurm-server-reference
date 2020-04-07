// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.jxc.apt;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import java.util.Set;
import java.util.Arrays;
import java.util.Collection;
import com.sun.mirror.apt.AnnotationProcessorFactory;

public class AnnotationProcessorFactoryImpl implements AnnotationProcessorFactory
{
    public Collection<String> supportedOptions() {
        return Arrays.asList("-Ajaxb.config");
    }
    
    public Collection<String> supportedAnnotationTypes() {
        return Arrays.asList("javax.xml.bind.annotation.*");
    }
    
    public AnnotationProcessor getProcessorFor(final Set<AnnotationTypeDeclaration> atds, final AnnotationProcessorEnvironment env) {
        return (AnnotationProcessor)new AnnotationParser(atds, env);
    }
}
