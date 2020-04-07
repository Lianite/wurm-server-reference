// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;
import java.util.regex.Pattern;
import org.fourthline.cling.support.model.dlna.types.ScmsFlagType;

public class ScmsFlagHeader extends DLNAHeader<ScmsFlagType>
{
    static final Pattern pattern;
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        if (ScmsFlagHeader.pattern.matcher(s).matches()) {
            this.setValue(new ScmsFlagType(s.charAt(0) == '0', s.charAt(1) == '0'));
            return;
        }
        throw new InvalidHeaderException("Invalid ScmsFlag header value: " + s);
    }
    
    @Override
    public String getString() {
        final ScmsFlagType v = this.getValue();
        return (v.isCopyright() ? "0" : "1") + (v.isOriginal() ? "0" : "1");
    }
    
    static {
        pattern = Pattern.compile("^[01]{2}$", 2);
    }
}
