// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import java.util.logging.Level;
import java.util.logging.Logger;

public enum ClamLootEnum
{
    NONE(-1, false, false), 
    PEARL(1397, false, false), 
    COIN(51, false, false), 
    IRON_LUMP(46, true, false), 
    LEAD_LUMP(49, true, false), 
    COPPER_LUMP(47, true, false), 
    TIN_LUMP(220, true, false), 
    ZINC_LUMP(48, true, false), 
    SILVER_LUMP(45, true, false), 
    GOLD_LUMP(44, true, false), 
    SALT(349, true, false), 
    FLINT(446, true, false), 
    MEAT(92, false, false), 
    LINE_LIGHT(1348, true, false), 
    LINE_MEDIUM(1349, true, false), 
    LINE_HEAVY(1350, true, false), 
    LINE_BRAIDED(1351, true, false), 
    HANDLE(99, true, true), 
    HANDLE_LEATHER(101, true, true), 
    HANDLE_REINFORCED(1370, true, true), 
    HANDLE_PADDED(1371, true, true), 
    REEL_LIGHT(1372, true, true), 
    REEL_MEDIUM(1373, true, true), 
    REEL_DEEP(1374, true, true), 
    REEL_PROFESSIONAL(1375, true, true), 
    REEL_WOOD(1367, true, true), 
    REEL_METAL(1368, true, true), 
    HOOK_WOOD(1356, true, true), 
    HOOK_METAL(1357, true, true), 
    HOOK_BONE(1358, true, false), 
    NAILS_SMALL(218, true, true), 
    NAILS_LARGE(217, true, true), 
    RIVET(131, true, true), 
    SEED_CABBAGE(1146, true, false), 
    SEED_PUMPKIN(34, true, false), 
    SEED_WEMP(317, true, false), 
    SEED_REED(744, true, false), 
    SEED_COTTON(145, true, false), 
    SEED_STRAWBERRY(750, true, false), 
    SEED_FENNEL(1151, true, false), 
    SEED_CARROT(1145, true, false), 
    SEED_TOMATO(1147, true, false), 
    SEED_SUGARBEET(1148, true, false), 
    SEED_LETTUCE(1149, true, false), 
    SEED_CUCUMBER(1248, true, false), 
    SEED_PAPRIKA(1153, true, false), 
    SEED_TURMERIC(1154, true, false), 
    COCOABEAN(1155, true, false), 
    FRAGMENT(1307, false, false);
    
    private final int templateId;
    private final boolean canHaveDamage;
    private final boolean randomMaterial;
    private static final Logger logger;
    
    private ClamLootEnum(final int templateId, final boolean canHaveDamage, final boolean randomMaterial) {
        this.templateId = templateId;
        this.canHaveDamage = canHaveDamage;
        this.randomMaterial = randomMaterial;
    }
    
    public int getTemplateId() {
        return this.templateId;
    }
    
    public boolean canHaveDamage() {
        return this.canHaveDamage;
    }
    
    public boolean randomMaterial() {
        return this.randomMaterial;
    }
    
    public static ClamLootEnum[] getLootTable() {
        final ClamLootEnum[] loot = { ClamLootEnum.COIN, ClamLootEnum.IRON_LUMP, ClamLootEnum.IRON_LUMP, ClamLootEnum.IRON_LUMP, ClamLootEnum.IRON_LUMP, ClamLootEnum.IRON_LUMP, ClamLootEnum.IRON_LUMP, ClamLootEnum.LEAD_LUMP, ClamLootEnum.COPPER_LUMP, ClamLootEnum.LEAD_LUMP, ClamLootEnum.TIN_LUMP, ClamLootEnum.ZINC_LUMP, ClamLootEnum.SILVER_LUMP, ClamLootEnum.GOLD_LUMP, ClamLootEnum.SALT, ClamLootEnum.FLINT, ClamLootEnum.MEAT, ClamLootEnum.LINE_LIGHT, ClamLootEnum.LINE_MEDIUM, ClamLootEnum.LINE_HEAVY, ClamLootEnum.LINE_BRAIDED, ClamLootEnum.PEARL, ClamLootEnum.PEARL, ClamLootEnum.PEARL, ClamLootEnum.HANDLE, ClamLootEnum.HANDLE_LEATHER, ClamLootEnum.HANDLE_REINFORCED, ClamLootEnum.HANDLE_PADDED, ClamLootEnum.REEL_LIGHT, ClamLootEnum.REEL_MEDIUM, ClamLootEnum.REEL_DEEP, ClamLootEnum.REEL_PROFESSIONAL, ClamLootEnum.REEL_WOOD, ClamLootEnum.REEL_METAL, ClamLootEnum.HOOK_WOOD, ClamLootEnum.HOOK_METAL, ClamLootEnum.HOOK_BONE, ClamLootEnum.NAILS_SMALL, ClamLootEnum.NAILS_LARGE, ClamLootEnum.RIVET, ClamLootEnum.SEED_CABBAGE, ClamLootEnum.SEED_PUMPKIN, ClamLootEnum.SEED_WEMP, ClamLootEnum.SEED_REED, ClamLootEnum.SEED_COTTON, ClamLootEnum.SEED_STRAWBERRY, ClamLootEnum.SEED_FENNEL, ClamLootEnum.SEED_CARROT, ClamLootEnum.SEED_TOMATO, ClamLootEnum.SEED_SUGARBEET, ClamLootEnum.SEED_LETTUCE, ClamLootEnum.SEED_CUCUMBER, ClamLootEnum.SEED_PAPRIKA, ClamLootEnum.SEED_TURMERIC, ClamLootEnum.COCOABEAN, ClamLootEnum.PEARL, ClamLootEnum.FRAGMENT, ClamLootEnum.FRAGMENT, ClamLootEnum.FRAGMENT, ClamLootEnum.FRAGMENT, ClamLootEnum.PEARL, ClamLootEnum.PEARL, ClamLootEnum.PEARL, ClamLootEnum.PEARL, ClamLootEnum.PEARL, ClamLootEnum.PEARL, ClamLootEnum.PEARL, ClamLootEnum.PEARL, ClamLootEnum.PEARL, ClamLootEnum.PEARL, ClamLootEnum.PEARL, ClamLootEnum.PEARL, ClamLootEnum.PEARL, ClamLootEnum.PEARL, ClamLootEnum.PEARL, ClamLootEnum.PEARL, ClamLootEnum.PEARL, ClamLootEnum.PEARL, ClamLootEnum.PEARL, ClamLootEnum.PEARL, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.MEAT, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE, ClamLootEnum.NONE };
        if (loot.length != 256) {
            ClamLootEnum.logger.log(Level.SEVERE, "Wrong lenght (" + loot.length + ") loot table", new Exception("Bad loot table!"));
        }
        return loot;
    }
    
    static {
        logger = Logger.getLogger(ClamLootEnum.class.getName());
    }
}
