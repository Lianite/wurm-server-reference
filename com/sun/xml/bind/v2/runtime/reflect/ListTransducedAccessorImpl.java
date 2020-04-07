// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.reflect;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import com.sun.xml.bind.WhiteSpaceProcessor;
import org.xml.sax.SAXException;
import com.sun.xml.bind.api.AccessorException;
import javax.xml.bind.JAXBException;
import com.sun.xml.bind.v2.runtime.XMLSerializer;
import com.sun.xml.bind.v2.runtime.Transducer;

public final class ListTransducedAccessorImpl<BeanT, ListT, ItemT, PackT> extends DefaultTransducedAccessor<BeanT>
{
    private final Transducer<ItemT> xducer;
    private final Lister<BeanT, ListT, ItemT, PackT> lister;
    private final Accessor<BeanT, ListT> acc;
    
    public ListTransducedAccessorImpl(final Transducer<ItemT> xducer, final Accessor<BeanT, ListT> acc, final Lister<BeanT, ListT, ItemT, PackT> lister) {
        this.xducer = xducer;
        this.lister = lister;
        this.acc = acc;
    }
    
    public boolean useNamespace() {
        return this.xducer.useNamespace();
    }
    
    public void declareNamespace(final BeanT bean, final XMLSerializer w) throws AccessorException, SAXException {
        final ListT list = this.acc.get(bean);
        if (list != null) {
            final ListIterator<ItemT> itr = this.lister.iterator(list, w);
            while (itr.hasNext()) {
                try {
                    final ItemT item = itr.next();
                    if (item == null) {
                        continue;
                    }
                    this.xducer.declareNamespace(item, w);
                }
                catch (JAXBException e) {
                    w.reportError(null, e);
                }
            }
        }
    }
    
    public String print(final BeanT o) throws AccessorException, SAXException {
        final ListT list = this.acc.get(o);
        if (list == null) {
            return null;
        }
        final StringBuilder buf = new StringBuilder();
        final XMLSerializer w = XMLSerializer.getInstance();
        final ListIterator<ItemT> itr = this.lister.iterator(list, w);
        while (itr.hasNext()) {
            try {
                final ItemT item = itr.next();
                if (item == null) {
                    continue;
                }
                if (buf.length() > 0) {
                    buf.append(' ');
                }
                buf.append(this.xducer.print(item));
            }
            catch (JAXBException e) {
                w.reportError(null, e);
            }
        }
        return buf.toString();
    }
    
    private void processValue(final BeanT bean, final CharSequence s) throws AccessorException, SAXException {
        final PackT pack = this.lister.startPacking(bean, this.acc);
        int idx = 0;
        final int len = s.length();
        while (true) {
            int p;
            for (p = idx; p < len && !WhiteSpaceProcessor.isWhiteSpace(s.charAt(p)); ++p) {}
            final CharSequence token = s.subSequence(idx, p);
            if (!token.equals("")) {
                this.lister.addToPack(pack, this.xducer.parse(token));
            }
            if (p == len) {
                break;
            }
            while (p < len && WhiteSpaceProcessor.isWhiteSpace(s.charAt(p))) {
                ++p;
            }
            if (p == len) {
                break;
            }
            idx = p;
        }
        this.lister.endPacking(pack, bean, this.acc);
    }
    
    public void parse(final BeanT bean, final CharSequence lexical) throws AccessorException, SAXException {
        this.processValue(bean, lexical);
    }
    
    public boolean hasValue(final BeanT bean) throws AccessorException {
        return this.acc.get(bean) != null;
    }
}
