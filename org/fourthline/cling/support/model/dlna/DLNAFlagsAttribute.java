// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna;

import java.util.Iterator;
import java.util.Locale;
import java.util.EnumSet;

public class DLNAFlagsAttribute extends DLNAAttribute<EnumSet<DLNAFlags>>
{
    public DLNAFlagsAttribute() {
        this.setValue(EnumSet.noneOf(DLNAFlags.class));
    }
    
    public DLNAFlagsAttribute(final DLNAFlags... flags) {
        if (flags != null && flags.length > 0) {
            final DLNAFlags first = flags[0];
            if (flags.length > 1) {
                System.arraycopy(flags, 1, flags, 0, flags.length - 1);
                this.setValue(EnumSet.of(first, flags));
            }
            else {
                this.setValue(EnumSet.of(first));
            }
        }
    }
    
    @Override
    public void setString(final String s, final String cf) throws InvalidDLNAProtocolAttributeException {
        final EnumSet<DLNAFlags> value = EnumSet.noneOf(DLNAFlags.class);
        try {
            final int parseInt = Integer.parseInt(s.substring(0, s.length() - 24), 16);
            for (final DLNAFlags op : DLNAFlags.values()) {
                final int code = op.getCode() & parseInt;
                if (op.getCode() == code) {
                    value.add(op);
                }
            }
        }
        catch (Exception ex) {}
        if (value.isEmpty()) {
            throw new InvalidDLNAProtocolAttributeException("Can't parse DLNA flags integer from: " + s);
        }
        this.setValue(value);
    }
    
    @Override
    public String getString() {
        int code = 0;
        for (final DLNAFlags op : this.getValue()) {
            code |= op.getCode();
        }
        return String.format(Locale.ROOT, "%08x%024x", code, 0);
    }
}
