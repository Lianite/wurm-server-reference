// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import java.util.GregorianCalendar;
import java.util.Calendar;

public final class EasterCalculator
{
    public static Calendar findHolyDay(final int year) {
        if (year <= 1582) {
            throw new IllegalArgumentException("Algorithm invalid before April 1583");
        }
        final int golden = year % 19 + 1;
        final int century = year / 100 + 1;
        final int x = 3 * century / 4 - 12;
        final int z = (8 * century + 5) / 25 - 5;
        final int d = 5 * year / 4 - x - 10;
        int epact = (11 * golden + 20 + z - x) % 30;
        if ((epact == 25 && golden > 11) || epact == 24) {
            ++epact;
        }
        int n = 44 - epact;
        n += 30 * ((n < 21) ? 1 : 0);
        n += 7 - (d + n) % 7;
        if (n > 31) {
            return new GregorianCalendar(year, 3, n - 31);
        }
        return new GregorianCalendar(year, 2, n);
    }
}
