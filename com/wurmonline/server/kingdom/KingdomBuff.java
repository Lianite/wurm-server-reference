// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.kingdom;

import java.util.logging.Logger;

public class KingdomBuff
{
    protected static Logger logger;
    public static final int BUFF_ACTION_QUEUE = 0;
    public static final int BUFF_FAVOR_REGENERATION = 1;
    public static final int BUFF_ENCHANT_EFFECT = 2;
    public static final int BUFF_RARITY_WINDOW = 3;
    public static final int BUFF_SKILL_BONUS = 4;
    public static final int BUFF_BURN_TIME = 5;
    public static final int BUFF_CREATION_CHANCE = 6;
    public static final int BUFF_ANALYSE_RANGE = 7;
    public static final int BUFF_CARRY_WEIGHT = 8;
    public static final int BUFF_STAMINA = 9;
    public static final int BUFF_HARDEN = 10;
    
    static {
        KingdomBuff.logger = Logger.getLogger(KingdomBuff.class.getName());
    }
}
