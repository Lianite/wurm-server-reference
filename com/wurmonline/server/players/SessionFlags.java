// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import java.util.BitSet;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class SessionFlags implements MiscConstants
{
    private static final String[] flagDescs;
    private static final Logger logger;
    
    static void initialiseFlags() {
        for (int x = 0; x < 64; ++x) {
            SessionFlags.flagDescs[x] = "";
            if (x == 0) {
                SessionFlags.flagDescs[x] = "Player has signed in";
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
            return SessionFlags.flagDescs[flag];
        }
        return "";
    }
    
    static {
        flagDescs = new String[64];
        logger = Logger.getLogger(SessionFlags.class.getName());
        initialiseFlags();
    }
}
