// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import com.sun.tools.xjc.generator.bean.field.FieldRendererFactory;
import com.sun.tools.xjc.model.Model;
import javax.xml.bind.annotation.XmlTransient;
import com.sun.tools.xjc.generator.bean.field.FieldRenderer;
import javax.xml.bind.annotation.XmlValue;

final class CollectionTypeAttribute
{
    @XmlValue
    String collectionType;
    @XmlTransient
    private FieldRenderer fr;
    
    CollectionTypeAttribute() {
        this.collectionType = null;
    }
    
    FieldRenderer get(final Model m) {
        if (this.fr == null) {
            this.fr = this.calcFr(m);
        }
        return this.fr;
    }
    
    private FieldRenderer calcFr(final Model m) {
        final FieldRendererFactory frf = m.options.getFieldRendererFactory();
        if (this.collectionType == null) {
            return frf.getDefault();
        }
        if (this.collectionType.equals("indexed")) {
            return frf.getArray();
        }
        return frf.getList(m.codeModel.ref(this.collectionType));
    }
}
