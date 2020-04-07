// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.util;

import java.security.SecureRandom;
import java.util.Random;

public class RandomToken
{
    protected final Random random;
    
    public RandomToken() {
        try {
            this.random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        this.random.nextBytes(new byte[1]);
    }
    
    public String generate() {
        String token;
        long r0;
        long r2;
        for (token = null; token == null || token.length() == 0; token = Long.toString(r0, 36) + Long.toString(r2, 36)) {
            r0 = this.random.nextLong();
            if (r0 < 0L) {
                r0 = -r0;
            }
            r2 = this.random.nextLong();
            if (r2 < 0L) {
                r2 = -r2;
            }
        }
        return token;
    }
}
