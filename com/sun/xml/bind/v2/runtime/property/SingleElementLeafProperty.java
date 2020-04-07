// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.property;

import com.sun.xml.bind.v2.model.core.PropertyKind;
import com.sun.xml.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader;
import com.sun.xml.bind.v2.runtime.unmarshaller.DefaultValueLoaderDecorator;
import com.sun.xml.bind.v2.runtime.unmarshaller.LeafPropertyLoader;
import com.sun.xml.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.bind.v2.util.QNameMap;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.sun.xml.bind.v2.runtime.XMLSerializer;
import org.xml.sax.SAXException;
import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.v2.model.runtime.RuntimeNonElementRef;
import com.sun.xml.bind.v2.model.runtime.RuntimeTypeRef;
import com.sun.xml.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.bind.v2.runtime.Name;

final class SingleElementLeafProperty<BeanT> extends PropertyImpl<BeanT>
{
    private final Name tagName;
    private final boolean nillable;
    private final Accessor acc;
    private final String defaultValue;
    private final TransducedAccessor<BeanT> xacc;
    
    public SingleElementLeafProperty(final JAXBContextImpl context, final RuntimeElementPropertyInfo prop) {
        super(context, prop);
        final RuntimeTypeRef ref = (RuntimeTypeRef)prop.getTypes().get(0);
        this.tagName = context.nameBuilder.createElementName(ref.getTagName());
        assert this.tagName != null;
        this.nillable = ref.isNillable();
        this.defaultValue = ref.getDefaultValue();
        this.acc = prop.getAccessor().optimize(context);
        this.xacc = TransducedAccessor.get(context, ref);
        assert this.xacc != null;
    }
    
    public void reset(final BeanT o) throws AccessorException {
        this.acc.set(o, null);
    }
    
    public String getIdValue(final BeanT bean) throws AccessorException, SAXException {
        return this.xacc.print(bean).toString();
    }
    
    public void serializeBody(final BeanT o, final XMLSerializer w, final Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
        final boolean hasValue = this.xacc.hasValue(o);
        if (hasValue) {
            this.xacc.writeLeafElement(w, this.tagName, o, this.fieldName);
        }
        else if (this.nillable) {
            w.startElement(this.tagName, null);
            w.writeXsiNilTrue();
            w.endElement();
        }
    }
    
    public void buildChildElementUnmarshallers(final UnmarshallerChain chain, final QNameMap<ChildLoader> handlers) {
        Loader l = new LeafPropertyLoader(this.xacc);
        if (this.defaultValue != null) {
            l = new DefaultValueLoaderDecorator(l, this.defaultValue);
        }
        if (this.nillable || chain.context.allNillable) {
            l = new XsiNilLoader.Single(l, this.acc);
        }
        handlers.put(this.tagName, new ChildLoader(l, null));
    }
    
    public PropertyKind getKind() {
        return PropertyKind.ELEMENT;
    }
    
    public Accessor getElementPropertyAccessor(final String nsUri, final String localName) {
        if (this.tagName.equals(nsUri, localName)) {
            return this.acc;
        }
        return null;
    }
}
