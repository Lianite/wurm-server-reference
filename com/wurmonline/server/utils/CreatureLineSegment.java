// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.utils;

import java.util.Iterator;
import java.util.ArrayList;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.shared.util.MulticolorLineSegment;

public class CreatureLineSegment extends MulticolorLineSegment
{
    private static final String YOU_STRING = "you";
    private Creature creature;
    
    public CreatureLineSegment(final Creature c) {
        super((c == null) ? "something" : c.getName(), (byte)0);
        this.creature = c;
    }
    
    public String getText(final Creature sendingTo) {
        if (sendingTo != this.creature) {
            return this.getText();
        }
        return "you";
    }
    
    public byte getColor(final Creature sendingTo) {
        if (this.creature == null || sendingTo == null) {
            return this.getColor();
        }
        switch (this.creature.getAttitude(sendingTo)) {
            case 2:
            case 4: {
                return 4;
            }
            case 1:
            case 5: {
                return 9;
            }
            case 7: {
                return 14;
            }
            case 0: {
                return 12;
            }
            case 3:
            case 6: {
                return 8;
            }
            default: {
                return this.getColor();
            }
        }
    }
    
    public static ArrayList<MulticolorLineSegment> cloneLineList(final ArrayList<MulticolorLineSegment> list) {
        final ArrayList<MulticolorLineSegment> toReturn = new ArrayList<MulticolorLineSegment>(list.size());
        for (final MulticolorLineSegment s : list) {
            if (s instanceof CreatureLineSegment) {
                toReturn.add(new CreatureLineSegment(((CreatureLineSegment)s).creature));
            }
            else {
                toReturn.add(new MulticolorLineSegment(s.getText(), s.getColor()));
            }
        }
        return toReturn;
    }
}
