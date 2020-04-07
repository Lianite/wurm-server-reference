// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

public class IntegerDatatype extends AbstractDatatype<Integer>
{
    private int byteSize;
    
    public IntegerDatatype(final int byteSize) {
        this.byteSize = byteSize;
    }
    
    @Override
    public boolean isHandlingJavaType(final Class type) {
        return type == Integer.TYPE || Integer.class.isAssignableFrom(type);
    }
    
    @Override
    public Integer valueOf(final String s) throws InvalidValueException {
        if (s.equals("")) {
            return null;
        }
        try {
            final Integer value = Integer.parseInt(s.trim());
            if (!this.isValid(value)) {
                throw new InvalidValueException("Not a " + this.getByteSize() + " byte(s) integer: " + s);
            }
            return value;
        }
        catch (NumberFormatException ex) {
            if (s.equals("NOT_IMPLEMENTED")) {
                return this.getMaxValue();
            }
            throw new InvalidValueException("Can't convert string to number: " + s, ex);
        }
    }
    
    @Override
    public boolean isValid(final Integer value) {
        return value == null || (value >= this.getMinValue() && value <= this.getMaxValue());
    }
    
    public int getMinValue() {
        switch (this.getByteSize()) {
            case 1: {
                return -128;
            }
            case 2: {
                return -32768;
            }
            case 4: {
                return Integer.MIN_VALUE;
            }
            default: {
                throw new IllegalArgumentException("Invalid integer byte size: " + this.getByteSize());
            }
        }
    }
    
    public int getMaxValue() {
        switch (this.getByteSize()) {
            case 1: {
                return 127;
            }
            case 2: {
                return 32767;
            }
            case 4: {
                return Integer.MAX_VALUE;
            }
            default: {
                throw new IllegalArgumentException("Invalid integer byte size: " + this.getByteSize());
            }
        }
    }
    
    public int getByteSize() {
        return this.byteSize;
    }
}
