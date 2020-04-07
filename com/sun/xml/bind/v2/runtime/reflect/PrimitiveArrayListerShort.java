// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.reflect;

import com.sun.xml.bind.api.AccessorException;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;
import com.sun.xml.bind.v2.runtime.XMLSerializer;

final class PrimitiveArrayListerShort<BeanT> extends Lister<BeanT, short[], Short, ShortArrayPack>
{
    static void register() {
        Lister.primitiveArrayListers.put(Short.TYPE, new PrimitiveArrayListerShort());
    }
    
    public ListIterator<Short> iterator(final short[] objects, final XMLSerializer context) {
        return new ListIterator<Short>() {
            int idx = 0;
            
            public boolean hasNext() {
                return this.idx < objects.length;
            }
            
            public Short next() {
                return objects[this.idx++];
            }
        };
    }
    
    public ShortArrayPack startPacking(final BeanT current, final Accessor<BeanT, short[]> acc) {
        return new ShortArrayPack();
    }
    
    public void addToPack(final ShortArrayPack objects, final Short o) {
        objects.add(o);
    }
    
    public void endPacking(final ShortArrayPack pack, final BeanT bean, final Accessor<BeanT, short[]> acc) throws AccessorException {
        acc.set(bean, pack.build());
    }
    
    public void reset(final BeanT o, final Accessor<BeanT, short[]> acc) throws AccessorException {
        acc.set(o, new short[0]);
    }
    
    static final class ShortArrayPack
    {
        short[] buf;
        int size;
        
        ShortArrayPack() {
            this.buf = new short[16];
        }
        
        void add(final Short b) {
            if (this.buf.length == this.size) {
                final short[] nb = new short[this.buf.length * 2];
                System.arraycopy(this.buf, 0, nb, 0, this.buf.length);
                this.buf = nb;
            }
            if (b != null) {
                this.buf[this.size++] = b;
            }
        }
        
        short[] build() {
            if (this.buf.length == this.size) {
                return this.buf;
            }
            final short[] r = new short[this.size];
            System.arraycopy(this.buf, 0, r, 0, this.size);
            return r;
        }
    }
}
