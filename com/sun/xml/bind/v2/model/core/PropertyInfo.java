// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.core;

import com.sun.istack.Nullable;
import javax.xml.namespace.QName;
import javax.activation.MimeType;
import java.util.Collection;
import com.sun.xml.bind.v2.model.annotation.AnnotationSource;

public interface PropertyInfo<T, C> extends AnnotationSource
{
    TypeInfo<T, C> parent();
    
    String getName();
    
    String displayName();
    
    boolean isCollection();
    
    Collection<? extends TypeInfo<T, C>> ref();
    
    PropertyKind kind();
    
    Adapter<T, C> getAdapter();
    
    ID id();
    
    MimeType getExpectedMimeType();
    
    boolean inlineBinaryData();
    
    @Nullable
    QName getSchemaType();
}
