// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.types;

import java.util.regex.Matcher;
import org.fourthline.cling.model.types.InvalidValueException;
import java.util.regex.Pattern;

public class BufferInfoType
{
    static final Pattern pattern;
    private Long dejitterSize;
    private CodedDataBuffer cdb;
    private Long targetDuration;
    private Boolean fullnessReports;
    
    public BufferInfoType(final Long dejitterSize) {
        this.dejitterSize = dejitterSize;
    }
    
    public BufferInfoType(final Long dejitterSize, final CodedDataBuffer cdb, final Long targetDuration, final Boolean fullnessReports) {
        this.dejitterSize = dejitterSize;
        this.cdb = cdb;
        this.targetDuration = targetDuration;
        this.fullnessReports = fullnessReports;
    }
    
    public static BufferInfoType valueOf(final String s) throws InvalidValueException {
        final Matcher matcher = BufferInfoType.pattern.matcher(s);
        if (matcher.matches()) {
            try {
                final Long dejitterSize = Long.parseLong(matcher.group(1));
                CodedDataBuffer cdb = null;
                Long targetDuration = null;
                Boolean fullnessReports = null;
                if (matcher.group(2) != null) {
                    cdb = new CodedDataBuffer(Long.parseLong(matcher.group(3)), CodedDataBuffer.TransferMechanism.values()[Integer.parseInt(matcher.group(4))]);
                }
                if (matcher.group(5) != null) {
                    targetDuration = Long.parseLong(matcher.group(6));
                }
                if (matcher.group(7) != null) {
                    fullnessReports = matcher.group(8).equals("1");
                }
                return new BufferInfoType(dejitterSize, cdb, targetDuration, fullnessReports);
            }
            catch (NumberFormatException ex) {}
        }
        throw new InvalidValueException("Can't parse BufferInfoType: " + s);
    }
    
    public String getString() {
        String s = "dejitter=" + this.dejitterSize.toString();
        if (this.cdb != null) {
            s = s + ";CDB=" + this.cdb.getSize().toString() + ";BTM=" + this.cdb.getTranfer().ordinal();
        }
        if (this.targetDuration != null) {
            s = s + ";TD=" + this.targetDuration.toString();
        }
        if (this.fullnessReports != null) {
            s = s + ";BFR=" + (this.fullnessReports ? "1" : "0");
        }
        return s;
    }
    
    public Long getDejitterSize() {
        return this.dejitterSize;
    }
    
    public CodedDataBuffer getCdb() {
        return this.cdb;
    }
    
    public Long getTargetDuration() {
        return this.targetDuration;
    }
    
    public Boolean isFullnessReports() {
        return this.fullnessReports;
    }
    
    static {
        pattern = Pattern.compile("^dejitter=(\\d{1,10})(;CDB=(\\d{1,10});BTM=(0|1|2))?(;TD=(\\d{1,10}))?(;BFR=(0|1))?$", 2);
    }
}
