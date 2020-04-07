// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import java.util.BitSet;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class Flags implements MiscConstants
{
    private static final String[] flagDescs;
    private static final Logger logger;
    
    static void initialiseFlags() {
        for (int x = 0; x < 64; ++x) {
            Flags.flagDescs[x] = "";
            if (x == 0) {
                Flags.flagDescs[x] = "Seen structure door warning";
            }
            if (x == 1) {
                Flags.flagDescs[x] = "Allow Incoming PMs";
            }
            if (x == 2) {
                Flags.flagDescs[x] = "Allow Incoming Cross-Kingdoms PMs";
            }
            if (x == 3) {
                Flags.flagDescs[x] = "Allow Incoming Cross-Servers PMs";
            }
        }
    }
    
    static BitSet setFlagBits(final long bits, final BitSet toSet) {
        for (int x = 0; x < 64; ++x) {
            if (x == 0) {
                if ((bits & 0x1L) == 0x1L) {
                    toSet.set(x, true);
                }
                else {
                    toSet.set(x, false);
                }
            }
            else if ((bits >> x & 0x1L) == 0x1L) {
                toSet.set(x, true);
            }
            else {
                toSet.set(x, false);
            }
        }
        return toSet;
    }
    
    static long getFlagBits(final BitSet bitsprovided) {
        long ret = 0L;
        for (int x = 0; x <= 64; ++x) {
            if (bitsprovided.get(x)) {
                ret += 1 << x;
            }
        }
        return ret;
    }
    
    public static String getFlagString(final int flag) {
        if (flag >= 0 && flag < 64) {
            return Flags.flagDescs[flag];
        }
        return "";
    }
    
    static {
        flagDescs = new String[64];
        logger = Logger.getLogger(javax.mail.Flags.Flag.class.getName());
        initialiseFlags();
    }
}
