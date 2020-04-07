// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.unmarshaller;

import java.util.concurrent.Callable;
import org.xml.sax.SAXException;
import javax.xml.bind.ValidationEventHandler;
import java.util.HashMap;
import com.sun.xml.bind.IDResolver;

final class DefaultIDResolver extends IDResolver
{
    private HashMap<String, Object> idmap;
    
    DefaultIDResolver() {
        this.idmap = null;
    }
    
    public void startDocument(final ValidationEventHandler eventHandler) throws SAXException {
        if (this.idmap != null) {
            this.idmap.clear();
        }
    }
    
    public void bind(final String id, final Object obj) {
        if (this.idmap == null) {
            this.idmap = new HashMap<String, Object>();
        }
        this.idmap.put(id, obj);
    }
    
    public Callable resolve(final String id, final Class targetType) {
        return new Callable() {
            public Object call() throws Exception {
                if (DefaultIDResolver.this.idmap == null) {
                    return null;
                }
                return DefaultIDResolver.this.idmap.get(id);
            }
        };
    }
}
