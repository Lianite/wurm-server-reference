// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import com.wurmonline.server.Server;
import edu.umd.cs.findbugs.annotations.NonNull;
import javafx.util.Pair;
import java.util.ArrayList;
import java.util.logging.Logger;

public abstract class LootTableUtilities
{
    private static final Logger logger;
    
    public static long getRandomLoot(@NonNull final ArrayList<Pair<Short, Long>> lootTable, final long maxValue, final long minValueCap, final long maxValueCap, final int minRerolls, final int maxRerolls, final float chanceToReroll, final float rrChanceDecreasePerRoll, final long aimRollsTowardsValue) {
        if (lootTable == null) {
            LootTableUtilities.logger.severe("Loot table was null");
            return -1L;
        }
        if (maxValue <= 0L) {
            LootTableUtilities.logger.severe("maxValue was less than or equal to 0, maxValue=" + maxValue);
            return -1L;
        }
        if (minValueCap > maxValue || minValueCap > maxValueCap) {
            LootTableUtilities.logger.severe("Min value cap is an unreasonable number. minValueCap=" + minValueCap + ", maxValue=" + maxValue + ", maxValueCap=" + maxValueCap);
            return -1L;
        }
        if (maxValueCap > maxValue) {
            LootTableUtilities.logger.severe("Max value cap is larger than the max value. maxValueCap=" + maxValueCap + ", maxValue=" + maxValue);
            return -1L;
        }
        if (minRerolls > maxRerolls) {
            LootTableUtilities.logger.severe("minRerolls larger than maxRerolls. minRerolls=" + minRerolls + ", maxRerolls=" + maxRerolls);
            return -1L;
        }
        if (chanceToReroll > 1.0 || chanceToReroll < 0.0) {
            LootTableUtilities.logger.severe("chance to reroll is not a reasonable value. chanceToReroll=" + chanceToReroll);
            return -1L;
        }
        final long[] candidateValues = new long[maxRerolls];
        int timesRerolled = 0;
        final long actualMinValueCap = (minValueCap < 0L) ? 0L : minValueCap;
        final float currentChanceToReroll = chanceToReroll + rrChanceDecreasePerRoll;
        boolean hasRerolled = false;
        do {
            if (!hasRerolled) {
                hasRerolled = true;
            }
            else {
                ++timesRerolled;
            }
            long probationalValue;
            do {
                probationalValue = Server.rand.nextLong();
            } while (probationalValue <= maxValue && probationalValue > actualMinValueCap && probationalValue <= maxValueCap);
            candidateValues[timesRerolled] = probationalValue;
        } while (timesRerolled < minRerolls || (Server.rand.nextFloat() < currentChanceToReroll - rrChanceDecreasePerRoll && timesRerolled < maxRerolls));
        long rolledValue;
        if (aimRollsTowardsValue < 0L) {
            long value = 0L;
            for (int i = 0; i <= timesRerolled; ++i) {
                if (candidateValues[i] > value) {
                    value = candidateValues[i];
                }
            }
            rolledValue = value;
        }
        else {
            long value = 0L;
            long distance = Long.MAX_VALUE;
            for (int j = 0; j <= timesRerolled; ++j) {
                if (candidateValues[j] == aimRollsTowardsValue) {
                    return aimRollsTowardsValue;
                }
                final long temp = Math.abs(candidateValues[j] - aimRollsTowardsValue);
                if (temp < distance) {
                    distance = temp;
                    value = candidateValues[j];
                }
            }
            rolledValue = value;
        }
        return rolledValue;
    }
    
    static {
        logger = Logger.getLogger(LootTableUtilities.class.getName());
    }
}
