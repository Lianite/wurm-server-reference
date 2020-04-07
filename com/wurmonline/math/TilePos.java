// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.math;

import java.util.NoSuchElementException;
import java.util.Iterator;

public final class TilePos implements Cloneable
{
    public int x;
    public int y;
    
    public TilePos() {
        this.x = 0;
        this.y = 0;
    }
    
    private TilePos(final int x, final int y) {
        this.x = x;
        this.y = y;
    }
    
    public TilePos(final TilePos rhs) {
        this.x = rhs.x;
        this.y = rhs.y;
    }
    
    public static TilePos fromXY(final int tileX, final int tileY) {
        return new TilePos(tileX, tileY);
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        final TilePos newPos = (TilePos)super.clone();
        newPos.set(this.x, this.y);
        return newPos;
    }
    
    public void set(final int x, final int y) {
        this.x = x;
        this.y = y;
    }
    
    public TilePos add(final int x, final int y, TilePos storage) {
        if (storage == null) {
            storage = new TilePos();
        }
        storage.set(this.x + x, this.y + y);
        return storage;
    }
    
    public TilePos North() {
        return fromXY(this.x, this.y - 1);
    }
    
    public TilePos South() {
        return new TilePos(this.x, this.y + 1);
    }
    
    public TilePos West() {
        return new TilePos(this.x - 1, this.y);
    }
    
    public TilePos East() {
        return fromXY(this.x + 1, this.y);
    }
    
    public TilePos NorthWest() {
        return fromXY(this.x - 1, this.y - 1);
    }
    
    public TilePos SouthEast() {
        return new TilePos(this.x + 1, this.y + 1);
    }
    
    public TilePos NorthEast() {
        return fromXY(this.x + 1, this.y - 1);
    }
    
    public TilePos SouthWest() {
        return new TilePos(this.x - 1, this.y + 1);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (!(obj instanceof TilePos)) {
            return false;
        }
        final TilePos pos = (TilePos)obj;
        return this.x == pos.x & this.y == pos.y;
    }
    
    public final boolean equals(final TilePos pos) {
        return this.x == pos.x && this.y == pos.y;
    }
    
    @Override
    public String toString() {
        return "tilePos: " + this.x + ", " + this.y;
    }
    
    public static Iterable<TilePos> areaIterator(final TilePos minPos, final TilePos maxPos) {
        return new Area(minPos, maxPos);
    }
    
    public static Iterable<TilePos> areaIterator(final int x1, final int y1, final int x2, final int y2) {
        return new Area(x1, y1, x2, y2);
    }
    
    public static Iterable<TilePos> bordersIterator(final TilePos minPos, final TilePos maxPos) {
        return new Borders(minPos, maxPos);
    }
    
    public static Iterable<TilePos> bordersIterator(final int x1, final int y1, final int x2, final int y2) {
        return new Borders(x1, y1, x2, y2);
    }
    
    private abstract static class IteratorPositions implements Iterator<TilePos>
    {
        final int x1;
        final int y1;
        final int x2;
        final int y2;
        final TilePos curPos;
        final TilePos userPos;
        
        IteratorPositions(final int x1, final int y1, final int x2, final int y2) {
            this.curPos = new TilePos();
            this.userPos = new TilePos();
            assert x1 <= x2;
            assert y1 <= y2;
            assert y1 < y2;
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.curPos.set(x1, y1);
        }
        
        @Override
        public boolean hasNext() {
            return this.curPos.x < this.x2 || this.curPos.y < this.y2;
        }
    }
    
    private static class Area implements Iterable<TilePos>
    {
        private final AreaIterator it;
        
        public Area(final TilePos posMin, final TilePos posMax) {
            this.it = new AreaIterator(posMin, posMax);
        }
        
        public Area(final int x1, final int y1, final int x2, final int y2) {
            this.it = new AreaIterator(x1, y1, x2, y2);
        }
        
        @Override
        public Iterator<TilePos> iterator() {
            return this.it;
        }
        
        private static class AreaIterator extends IteratorPositions
        {
            AreaIterator(final TilePos posMin, final TilePos posMax) {
                this(posMin.x, posMin.y, posMax.x, posMax.y);
            }
            
            AreaIterator(final int x1, final int y1, final int x2, final int y2) {
                super(x1, y1, x2, y2);
            }
            
            @Override
            public TilePos next() {
                assert this.curPos.y <= this.y2;
                if (this.curPos.x >= this.x2 && this.curPos.y >= this.y2) {
                    throw new NoSuchElementException("This condition should not be possible!");
                }
                final TilePos curPos = this.curPos;
                ++curPos.x;
                if (this.curPos.x > this.x2) {
                    this.curPos.x = this.x1;
                    final TilePos curPos2 = this.curPos;
                    ++curPos2.y;
                }
                this.userPos.set(this.curPos.x, this.curPos.y);
                return this.userPos;
            }
        }
    }
    
    private static class Borders implements Iterable<TilePos>
    {
        private final BorderIterator it;
        
        public Borders(final TilePos posMin, final TilePos posMax) {
            this.it = new BorderIterator(posMin, posMax);
        }
        
        public Borders(final int x1, final int y1, final int x2, final int y2) {
            this.it = new BorderIterator(x1, y1, x2, y2);
        }
        
        @Override
        public Iterator<TilePos> iterator() {
            return this.it;
        }
        
        private static class BorderIterator extends IteratorPositions
        {
            BorderIterator(final TilePos posMin, final TilePos posMax) {
                this(posMin.x, posMin.y, posMax.x, posMax.y);
            }
            
            BorderIterator(final int x1, final int y1, final int x2, final int y2) {
                super(x1, y1, x2, y2);
                assert x1 < x2 + 2 && y1 < y2 + 2;
            }
            
            @Override
            public TilePos next() {
                assert this.curPos.y <= this.y2;
                if (this.curPos.x >= this.x2 && this.curPos.y >= this.y2) {
                    throw new NoSuchElementException("This condition should not be possible!");
                }
                if (this.curPos.y == this.y1 || this.curPos.y == this.y2) {
                    final TilePos curPos = this.curPos;
                    ++curPos.x;
                    if (this.curPos.x > this.x2) {
                        this.curPos.x = this.x1;
                        final TilePos curPos2 = this.curPos;
                        ++curPos2.y;
                        assert this.curPos.y <= this.y2 : "This condition should not be possible!";
                    }
                }
                else {
                    assert this.curPos.x == this.x2 : "This condition should not be possible!";
                    this.curPos.x = ((this.curPos.x == this.x1) ? this.x2 : this.x1);
                    if (this.curPos.x == this.x1) {
                        final TilePos curPos3 = this.curPos;
                        ++curPos3.y;
                    }
                }
                this.userPos.set(this.curPos.x, this.curPos.y);
                return this.userPos;
            }
        }
    }
}
