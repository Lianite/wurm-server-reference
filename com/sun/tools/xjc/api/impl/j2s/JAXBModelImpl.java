// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api.impl.j2s;

import com.sun.mirror.type.PrimitiveType;
import com.sun.xml.txw2.output.ResultFactory;
import javax.xml.transform.Result;
import java.io.IOException;
import com.sun.tools.xjc.api.ErrorListener;
import javax.xml.bind.SchemaOutputResolver;
import com.sun.xml.bind.v2.model.core.Ref;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.sun.xml.bind.v2.model.core.ElementInfo;
import com.sun.xml.bind.v2.model.core.Element;
import java.util.Iterator;
import com.sun.xml.bind.v2.model.nav.Navigator;
import com.sun.xml.bind.v2.model.core.EnumLeafInfo;
import com.sun.xml.bind.v2.model.core.ArrayInfo;
import com.sun.xml.bind.v2.model.core.ClassInfo;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import com.sun.xml.bind.v2.model.core.NonElement;
import com.sun.xml.bind.v2.schemagen.XmlSchemaGenerator;
import com.sun.xml.bind.v2.model.annotation.AnnotationReader;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;
import com.sun.mirror.type.TypeMirror;
import com.sun.xml.bind.v2.model.core.TypeInfoSet;
import java.util.List;
import com.sun.tools.xjc.api.Reference;
import javax.xml.namespace.QName;
import java.util.Map;
import com.sun.tools.xjc.api.J2SJAXBModel;

final class JAXBModelImpl implements J2SJAXBModel
{
    private final Map<QName, Reference> additionalElementDecls;
    private final List<String> classList;
    private final TypeInfoSet<TypeMirror, TypeDeclaration, FieldDeclaration, MethodDeclaration> types;
    private final AnnotationReader<TypeMirror, TypeDeclaration, FieldDeclaration, MethodDeclaration> reader;
    private XmlSchemaGenerator<TypeMirror, TypeDeclaration, FieldDeclaration, MethodDeclaration> xsdgen;
    private final Map<Reference, NonElement<TypeMirror, TypeDeclaration>> refMap;
    
    public JAXBModelImpl(final TypeInfoSet<TypeMirror, TypeDeclaration, FieldDeclaration, MethodDeclaration> types, final AnnotationReader<TypeMirror, TypeDeclaration, FieldDeclaration, MethodDeclaration> reader, final Collection<Reference> rootClasses, final Map<QName, Reference> additionalElementDecls) {
        this.classList = new ArrayList<String>();
        this.refMap = new HashMap<Reference, NonElement<TypeMirror, TypeDeclaration>>();
        this.types = types;
        this.reader = reader;
        this.additionalElementDecls = additionalElementDecls;
        final Navigator<TypeMirror, TypeDeclaration, FieldDeclaration, MethodDeclaration> navigator = types.getNavigator();
        for (final ClassInfo<TypeMirror, TypeDeclaration> i : types.beans().values()) {
            this.classList.add(i.getName());
        }
        for (final ArrayInfo<TypeMirror, TypeDeclaration> a : types.arrays().values()) {
            final String javaName = navigator.getTypeName(a.getType());
            this.classList.add(javaName);
        }
        for (final EnumLeafInfo<TypeMirror, TypeDeclaration> l : types.enums().values()) {
            final QName tn = l.getTypeName();
            if (tn != null) {
                final String javaName2 = navigator.getTypeName(l.getType());
                this.classList.add(javaName2);
            }
        }
        for (final Reference ref : rootClasses) {
            this.refMap.put(ref, this.getXmlType(ref));
        }
        final Iterator<Map.Entry<QName, Reference>> itr = additionalElementDecls.entrySet().iterator();
        while (itr.hasNext()) {
            final Map.Entry<QName, Reference> entry = itr.next();
            if (entry.getValue() == null) {
                continue;
            }
            final NonElement<TypeMirror, TypeDeclaration> xt = this.getXmlType(entry.getValue());
            assert xt != null;
            this.refMap.put(entry.getValue(), xt);
            if (xt instanceof ClassInfo) {
                final ClassInfo<TypeMirror, TypeDeclaration> xct = (ClassInfo<TypeMirror, TypeDeclaration>)(ClassInfo)xt;
                final Element<TypeMirror, TypeDeclaration> elem = xct.asElement();
                if (elem != null && elem.getElementName().equals(entry.getKey())) {
                    itr.remove();
                    continue;
                }
            }
            final ElementInfo<TypeMirror, TypeDeclaration> ei = types.getElementInfo(null, entry.getKey());
            if (ei == null || ei.getContentType() != xt) {
                continue;
            }
            itr.remove();
        }
    }
    
    public List<String> getClassList() {
        return this.classList;
    }
    
    public QName getXmlTypeName(final Reference javaType) {
        final NonElement<TypeMirror, TypeDeclaration> ti = this.refMap.get(javaType);
        if (ti != null) {
            return ti.getTypeName();
        }
        return null;
    }
    
    private NonElement<TypeMirror, TypeDeclaration> getXmlType(final Reference r) {
        if (r == null) {
            throw new IllegalArgumentException();
        }
        final XmlJavaTypeAdapter xjta = (XmlJavaTypeAdapter)r.annotations.getAnnotation((Class)XmlJavaTypeAdapter.class);
        final XmlList xl = (XmlList)r.annotations.getAnnotation((Class)XmlList.class);
        final Ref<TypeMirror, TypeDeclaration> ref = new Ref<TypeMirror, TypeDeclaration>(this.reader, this.types.getNavigator(), r.type, xjta, xl);
        return this.types.getTypeInfo(ref);
    }
    
    public void generateSchema(final SchemaOutputResolver outputResolver, final ErrorListener errorListener) throws IOException {
        this.getSchemaGenerator().write(outputResolver, errorListener);
    }
    
    public void generateEpisodeFile(final Result output) {
        this.getSchemaGenerator().writeEpisodeFile(ResultFactory.createSerializer(output));
    }
    
    private synchronized XmlSchemaGenerator<TypeMirror, TypeDeclaration, FieldDeclaration, MethodDeclaration> getSchemaGenerator() {
        if (this.xsdgen == null) {
            this.xsdgen = new XmlSchemaGenerator<TypeMirror, TypeDeclaration, FieldDeclaration, MethodDeclaration>(this.types.getNavigator(), this.types);
            for (final Map.Entry<QName, Reference> e : this.additionalElementDecls.entrySet()) {
                final Reference value = e.getValue();
                if (value != null) {
                    final NonElement<TypeMirror, TypeDeclaration> typeInfo = this.refMap.get(value);
                    if (typeInfo == null) {
                        throw new IllegalArgumentException(e.getValue() + " was not specified to JavaCompiler.bind");
                    }
                    this.xsdgen.add(e.getKey(), !(value.type instanceof PrimitiveType), typeInfo);
                }
                else {
                    this.xsdgen.add(e.getKey(), false, null);
                }
            }
        }
        return this.xsdgen;
    }
}
