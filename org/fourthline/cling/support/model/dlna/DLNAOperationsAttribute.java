// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna;

import java.util.Iterator;
import java.util.Locale;
import java.util.EnumSet;

public class DLNAOperationsAttribute extends DLNAAttribute<EnumSet<DLNAOperations>>
{
    public DLNAOperationsAttribute() {
        this.setValue(EnumSet.of(DLNAOperations.NONE));
    }
    
    public DLNAOperationsAttribute(final DLNAOperations... op) {
        if (op != null && op.length > 0) {
            final DLNAOperations first = op[0];
            if (op.length > 1) {
                System.arraycopy(op, 1, op, 0, op.length - 1);
                this.setValue(EnumSet.of(first, op));
            }
            else {
                this.setValue(EnumSet.of(first));
            }
        }
    }
    
    @Override
    public void setString(final String s, final String cf) throws InvalidDLNAProtocolAttributeException {
        final EnumSet<DLNAOperations> value = EnumSet.noneOf(DLNAOperations.class);
        try {
            final int parseInt = Integer.parseInt(s, 16);
            for (final DLNAOperations op : DLNAOperations.values()) {
                final int code = op.getCode() & parseInt;
                if (op != DLNAOperations.NONE && op.getCode() == code) {
                    value.add(op);
                }
            }
        }
        catch (NumberFormatException ex) {}
        if (value.isEmpty()) {
            throw new InvalidDLNAProtocolAttributeException("Can't parse DLNA operations integer from: " + s);
        }
        this.setValue(value);
    }
    
    @Override
    public String getString() {
        int code = DLNAOperations.NONE.getCode();
        for (final DLNAOperations op : this.getValue()) {
            code |= op.getCode();
        }
        return String.format(Locale.ROOT, "%02x", code);
    }
}
