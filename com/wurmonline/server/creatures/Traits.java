// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.wurmonline.server.Servers;
import java.util.logging.Level;
import com.wurmonline.server.Server;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.BitSet;
import java.util.Random;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class Traits implements MiscConstants
{
    private static final String[] treatDescs;
    private static final boolean[] negativeTraits;
    private static final boolean[] neutralTraits;
    private static final Logger logger;
    
    static void initialiseTraits() {
        for (int x = 0; x < 64; ++x) {
            Traits.treatDescs[x] = "";
            if (x == 0) {
                Traits.treatDescs[x] = "It will fight fiercely.";
            }
            else if (x == 1) {
                Traits.treatDescs[x] = "It has fleeter movement than normal.";
            }
            else if (x == 2) {
                Traits.treatDescs[x] = "It is a tough bugger.";
            }
            else if (x == 3) {
                Traits.treatDescs[x] = "It has a strong body.";
            }
            else if (x == 4) {
                Traits.treatDescs[x] = "It has lightning movement.";
            }
            else if (x == 5) {
                Traits.treatDescs[x] = "It can carry more than average.";
            }
            else if (x == 6) {
                Traits.treatDescs[x] = "It has very strong leg muscles.";
            }
            else if (x == 7) {
                Traits.treatDescs[x] = "It has keen senses.";
            }
            else if (x == 8) {
                Traits.treatDescs[x] = "It has malformed hindlegs.";
                Traits.negativeTraits[x] = true;
            }
            else if (x == 9) {
                Traits.treatDescs[x] = "The legs are of different length.";
                Traits.negativeTraits[x] = true;
            }
            else if (x == 10) {
                Traits.treatDescs[x] = "It seems overly aggressive.";
                Traits.negativeTraits[x] = true;
            }
            else if (x == 11) {
                Traits.treatDescs[x] = "It looks very unmotivated.";
                Traits.negativeTraits[x] = true;
            }
            else if (x == 12) {
                Traits.treatDescs[x] = "It is unusually strong willed.";
                Traits.negativeTraits[x] = true;
            }
            else if (x == 13) {
                Traits.treatDescs[x] = "It has some illness.";
                Traits.negativeTraits[x] = true;
            }
            else if (x == 14) {
                Traits.treatDescs[x] = "It looks constantly hungry.";
                Traits.negativeTraits[x] = true;
            }
            else if (x == 19) {
                Traits.treatDescs[x] = "It looks feeble and unhealthy.";
                Traits.negativeTraits[x] = true;
            }
            else if (x == 20) {
                Traits.treatDescs[x] = "It looks unusually strong and healthy.";
                Traits.negativeTraits[x] = false;
            }
            else if (x == 21) {
                Traits.treatDescs[x] = "It has a certain spark in its eyes.";
                Traits.negativeTraits[x] = false;
            }
            else if (x == 22) {
                Traits.treatDescs[x] = "It has been corrupted.";
                Traits.neutralTraits[x] = true;
            }
            else if (x == 27) {
                Traits.treatDescs[x] = "It bears the mark of the rift.";
                Traits.neutralTraits[x] = true;
            }
            else if (x == 28) {
                Traits.treatDescs[x] = "It bears the mark of a traitor.";
                Traits.neutralTraits[x] = true;
            }
            else if (x == 63) {
                Traits.treatDescs[x] = "It has been bred in captivity.";
                Traits.neutralTraits[x] = true;
            }
            else if (x == 29) {
                Traits.treatDescs[x] = "It has a mark of Valrei.";
                Traits.neutralTraits[x] = true;
            }
            else if (x == 15 || x == 16 || x == 17 || x == 18 || x == 24 || x == 25 || x == 23 || x == 30 || x == 31 || x == 32 || x == 33 || x == 34) {
                Traits.neutralTraits[x] = true;
            }
        }
    }
    
    static long calcNewTraits(final boolean inbred, final long mothertraits, final long fathertraits) {
        final Random rand = new Random();
        final BitSet motherSet = new BitSet(64);
        final BitSet fatherSet = new BitSet(64);
        final BitSet childSet = new BitSet(64);
        setTraitBits(fathertraits, fatherSet);
        setTraitBits(mothertraits, motherSet);
        for (int bitIndex = 0; bitIndex < 64; ++bitIndex) {
            calcOneNewTrait(inbred, rand, motherSet, fatherSet, childSet, bitIndex);
        }
        return getTraitBits(childSet);
    }
    
    static void calcOneNewTrait(final boolean inbred, final Random rand, final BitSet motherSet, final BitSet fatherSet, final BitSet childSet, final int bitIndex) {
        if (bitIndex == 27 || bitIndex == 28 || bitIndex == 29) {
            return;
        }
        if (motherSet.get(bitIndex) && fatherSet.get(bitIndex)) {
            int chance = 66;
            if (Traits.negativeTraits[bitIndex]) {
                chance = 10;
                if (inbred) {
                    chance = 20;
                }
            }
            childSet.set(bitIndex, rand.nextInt(100) < chance);
        }
        else if (motherSet.get(bitIndex)) {
            int chance = 25;
            if (Traits.negativeTraits[bitIndex]) {
                chance = 8;
                if (inbred) {
                    chance = 12;
                }
            }
            childSet.set(bitIndex, rand.nextInt(100) < chance);
        }
        else if (fatherSet.get(bitIndex)) {
            int chance = 25;
            if (Traits.negativeTraits[bitIndex]) {
                chance = 8;
                if (inbred) {
                    chance = 12;
                }
            }
            childSet.set(bitIndex, rand.nextInt(100) < chance);
        }
        else {
            if (bitIndex == 22) {
                return;
            }
            int chance = 7;
            if (Traits.negativeTraits[bitIndex]) {
                chance = 5;
                if (inbred) {
                    chance = 10;
                }
            }
            childSet.set(bitIndex, rand.nextInt(100) < chance);
        }
    }
    
    public static long calcNewTraits(final double breederSkill, final boolean inbred, final long mothertraits, final long fathertraits) {
        final Random rand = new Random();
        final BitSet motherSet = new BitSet(64);
        final BitSet fatherSet = new BitSet(64);
        final BitSet childSet = new BitSet(64);
        final int maxTraits = Math.min(8, Math.max(1, (int)(breederSkill / 10.0)));
        final int maxPoints = maxTraits * 60;
        int allocated = 0;
        final Map<Integer, Integer> newSet = new HashMap<Integer, Integer>();
        final List<Integer> availableTraits = new ArrayList<Integer>();
        setTraitBits(fathertraits, fatherSet);
        setTraitBits(mothertraits, motherSet);
        for (int bitIndex = 0; bitIndex <= 34; ++bitIndex) {
            if (bitIndex != 22 && bitIndex != 27 && bitIndex != 28) {
                if (bitIndex != 29) {
                    availableTraits.add(bitIndex);
                    if (motherSet.get(bitIndex) && fatherSet.get(bitIndex)) {
                        int num = 50;
                        if (inbred && Traits.negativeTraits[bitIndex]) {
                            num += 10;
                        }
                        newSet.put(bitIndex, num);
                        if (!isTraitNeutral(bitIndex)) {
                            allocated += 50;
                        }
                        availableTraits.remove((Object)bitIndex);
                    }
                    else if (motherSet.get(bitIndex)) {
                        int num = 30;
                        if (inbred && Traits.negativeTraits[bitIndex]) {
                            num += 10;
                        }
                        newSet.put(bitIndex, num);
                        if (!isTraitNeutral(bitIndex)) {
                            allocated += 30;
                        }
                        availableTraits.remove((Object)bitIndex);
                    }
                    else if (fatherSet.get(bitIndex)) {
                        int num = 20;
                        if (inbred && Traits.negativeTraits[bitIndex]) {
                            num += 10;
                        }
                        newSet.put(bitIndex, num);
                        if (!isTraitNeutral(bitIndex)) {
                            allocated += 20;
                        }
                        availableTraits.remove((Object)bitIndex);
                    }
                }
            }
        }
        final int left = maxPoints - allocated;
        float traitsLeft = 0.0f;
        if (left > 0) {
            traitsLeft = left / 50.0f;
            if (traitsLeft - (int)traitsLeft > 0.0f) {
                ++traitsLeft;
            }
            for (int x = 0; x < (int)traitsLeft; ++x) {
                if (rand.nextBoolean()) {
                    int num2 = 20;
                    final Integer newTrait = availableTraits.remove(rand.nextInt(availableTraits.size()));
                    if (Traits.negativeTraits[newTrait]) {
                        num2 -= maxTraits;
                        if (inbred) {
                            num2 += 10;
                        }
                    }
                    if (isTraitNeutral(newTrait)) {
                        --x;
                    }
                    newSet.put(newTrait, num2);
                }
            }
            traitsLeft = maxTraits;
        }
        else {
            traitsLeft = Math.max(Math.min(newSet.size(), maxTraits), 3 + Server.rand.nextInt(3));
        }
        for (int t = 0; t < traitsLeft && !newSet.isEmpty(); ++t) {
            final Integer selected = pickOneTrait(rand, newSet);
            if (selected >= 0) {
                if (selected != 22 && selected != 27 && selected != 28 && selected != 29) {
                    childSet.set(selected, true);
                    newSet.remove(selected);
                    if (isTraitNeutral(selected)) {
                        --t;
                    }
                }
            }
            else {
                Traits.logger.log(Level.WARNING, "Failed to select a trait from a map of size " + newSet.size());
            }
        }
        if (!Servers.isThisAPvpServer()) {
            childSet.clear(22);
        }
        else if (fatherSet.get(22) || motherSet.get(22)) {
            childSet.set(22);
        }
        childSet.set(63, true);
        return getTraitBits(childSet);
    }
    
    static Integer pickOneTrait(final Random rand, final Map<Integer, Integer> traitMap) {
        int chance = 0;
        for (final Map.Entry<Integer, Integer> entry : traitMap.entrySet()) {
            chance += entry.getValue();
        }
        if (chance == 0 || chance < 0) {
            Traits.logger.log(Level.INFO, "Trait rand=" + chance + " should not be <=0! Size of map is " + traitMap.size());
            return -1;
        }
        final int selectedTrait = rand.nextInt(chance);
        chance = 0;
        for (final Map.Entry<Integer, Integer> entry2 : traitMap.entrySet()) {
            chance += entry2.getValue();
            if (chance > selectedTrait) {
                return entry2.getKey();
            }
        }
        return -1;
    }
    
    static BitSet setTraitBits(final long bits, final BitSet toSet) {
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
    
    static long getTraitBits(final BitSet bitsprovided) {
        long ret = 0L;
        for (int x = 0; x < 64; ++x) {
            if (bitsprovided.get(x)) {
                ret += 1L << x;
            }
        }
        return ret;
    }
    
    public static String getTraitString(final int trait) {
        if (trait >= 0 && trait < 64) {
            return Traits.treatDescs[trait];
        }
        return "";
    }
    
    public static boolean isTraitNegative(final int trait) {
        return trait >= 0 && trait <= 64 && Traits.negativeTraits[trait];
    }
    
    public static boolean isTraitNeutral(final int trait) {
        return trait >= 0 && trait <= 64 && Traits.neutralTraits[trait];
    }
    
    static {
        treatDescs = new String[64];
        negativeTraits = new boolean[64];
        neutralTraits = new boolean[64];
        logger = Logger.getLogger(Traits.class.getName());
        initialiseTraits();
    }
}
