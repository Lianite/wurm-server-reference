// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.reflect;

import com.sun.xml.bind.api.AccessorException;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;
import com.sun.xml.bind.v2.runtime.XMLSerializer;

final class PrimitiveArrayListerBoolean<BeanT> extends Lister<BeanT, boolean[], Boolean, BooleanArrayPack>
{
    static void register() {
        Lister.primitiveArrayListers.put(Boolean.TYPE, new PrimitiveArrayListerBoolean());
    }
    
    public ListIterator<Boolean> iterator(final boolean[] objects, final XMLSerializer context) {
        return new ListIterator<Boolean>() {
            int idx = 0;
            
            public boolean hasNext() {
                return this.idx < objects.length;
            }
            
            public Boolean next() {
                return objects[this.idx++];
            }
        };
    }
    
    public BooleanArrayPack startPacking(final BeanT current, final Accessor<BeanT, boolean[]> acc) {
        return new BooleanArrayPack();
    }
    
    public void addToPack(final BooleanArrayPack objects, final Boolean o) {
        objects.add(o);
    }
    
    public void endPacking(final BooleanArrayPack pack, final BeanT bean, final Accessor<BeanT, boolean[]> acc) throws AccessorException {
        acc.set(bean, pack.build());
    }
    
    public void reset(final BeanT o, final Accessor<BeanT, boolean[]> acc) throws AccessorException {
        acc.set(o, new boolean[0]);
    }
    
    static final class BooleanArrayPack
    {
        boolean[] buf;
        int size;
        
        BooleanArrayPack() {
            this.buf = new boolean[16];
        }
        
        void add(final Boolean b) {
            if (this.buf.length == this.size) {
                final boolean[] nb = new boolean[this.buf.length * 2];
                System.arraycopy(this.buf, 0, nb, 0, this.buf.length);
                this.buf = nb;
            }
            if (b != null) {
                this.buf[this.size++] = b;
            }
        }
        
        boolean[] build() {
            if (this.buf.length == this.size) {
                return this.buf;
            }
            final boolean[] r = new boolean[this.size];
            System.arraycopy(this.buf, 0, r, 0, this.size);
            return r;
        }
    }
}
