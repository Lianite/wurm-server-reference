// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.reflect;

import com.sun.xml.bind.api.AccessorException;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;
import com.sun.xml.bind.v2.runtime.XMLSerializer;

final class PrimitiveArrayListerInteger<BeanT> extends Lister<BeanT, int[], Integer, IntegerArrayPack>
{
    static void register() {
        Lister.primitiveArrayListers.put(Integer.TYPE, new PrimitiveArrayListerInteger());
    }
    
    public ListIterator<Integer> iterator(final int[] objects, final XMLSerializer context) {
        return new ListIterator<Integer>() {
            int idx = 0;
            
            public boolean hasNext() {
                return this.idx < objects.length;
            }
            
            public Integer next() {
                return objects[this.idx++];
            }
        };
    }
    
    public IntegerArrayPack startPacking(final BeanT current, final Accessor<BeanT, int[]> acc) {
        return new IntegerArrayPack();
    }
    
    public void addToPack(final IntegerArrayPack objects, final Integer o) {
        objects.add(o);
    }
    
    public void endPacking(final IntegerArrayPack pack, final BeanT bean, final Accessor<BeanT, int[]> acc) throws AccessorException {
        acc.set(bean, pack.build());
    }
    
    public void reset(final BeanT o, final Accessor<BeanT, int[]> acc) throws AccessorException {
        acc.set(o, new int[0]);
    }
    
    static final class IntegerArrayPack
    {
        int[] buf;
        int size;
        
        IntegerArrayPack() {
            this.buf = new int[16];
        }
        
        void add(final Integer b) {
            if (this.buf.length == this.size) {
                final int[] nb = new int[this.buf.length * 2];
                System.arraycopy(this.buf, 0, nb, 0, this.buf.length);
                this.buf = nb;
            }
            if (b != null) {
                this.buf[this.size++] = b;
            }
        }
        
        int[] build() {
            if (this.buf.length == this.size) {
                return this.buf;
            }
            final int[] r = new int[this.size];
            System.arraycopy(this.buf, 0, r, 0, this.size);
            return r;
        }
    }
}
