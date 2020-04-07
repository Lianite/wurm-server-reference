// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

import java.util.logging.Logger;

public abstract class UnsignedVariableInteger
{
    private static final Logger log;
    protected long value;
    
    protected UnsignedVariableInteger() {
    }
    
    public UnsignedVariableInteger(final long value) throws NumberFormatException {
        this.setValue(value);
    }
    
    public UnsignedVariableInteger(String s) throws NumberFormatException {
        if (s.startsWith("-")) {
            UnsignedVariableInteger.log.warning("Invalid negative integer value '" + s + "', assuming value 0!");
            s = "0";
        }
        this.setValue(Long.parseLong(s.trim()));
    }
    
    protected UnsignedVariableInteger setValue(final long value) {
        this.isInRange(value);
        this.value = value;
        return this;
    }
    
    public Long getValue() {
        return this.value;
    }
    
    public void isInRange(final long value) throws NumberFormatException {
        if (value < this.getMinValue() || value > this.getBits().getMaxValue()) {
            throw new NumberFormatException("Value must be between " + this.getMinValue() + " and " + this.getBits().getMaxValue() + ": " + value);
        }
    }
    
    public int getMinValue() {
        return 0;
    }
    
    public abstract Bits getBits();
    
    public UnsignedVariableInteger increment(final boolean rolloverToOne) {
        if (this.value + 1L > this.getBits().getMaxValue()) {
            this.value = (rolloverToOne ? 1 : 0);
        }
        else {
            ++this.value;
        }
        return this;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final UnsignedVariableInteger that = (UnsignedVariableInteger)o;
        return this.value == that.value;
    }
    
    @Override
    public int hashCode() {
        return (int)(this.value ^ this.value >>> 32);
    }
    
    @Override
    public String toString() {
        return Long.toString(this.value);
    }
    
    static {
        log = Logger.getLogger(UnsignedVariableInteger.class.getName());
    }
    
    public enum Bits
    {
        EIGHT(255L), 
        SIXTEEN(65535L), 
        TWENTYFOUR(16777215L), 
        THIRTYTWO(4294967295L);
        
        private long maxValue;
        
        private Bits(final long maxValue) {
            this.maxValue = maxValue;
        }
        
        public long getMaxValue() {
            return this.maxValue;
        }
    }
}
