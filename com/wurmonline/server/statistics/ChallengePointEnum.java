// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.statistics;

public class ChallengePointEnum
{
    public enum ChallengeScenario
    {
        UNUSED(0, "Unused", "http://www.wurmonline.com", "Placeholder"), 
        BUILDBATTLE(1, "Build The Battlefield", "http://www.wurmonline.com/images/challenge/midbb.png", "The First Ever"), 
        DOMINANCE(2, "Dominance", "http://www.wurmonline.com/images/challenge/dd64.png", "Rush to the front");
        
        public static final ChallengeScenario current;
        private final int num;
        private final String name;
        private final String url;
        private final String desc;
        private static final ChallengeScenario[] types;
        
        private ChallengeScenario(final int enumber, final String ename, final String eurl, final String edescription) {
            this.num = enumber;
            this.name = ename;
            this.url = eurl;
            this.desc = edescription;
        }
        
        public int getNum() {
            return this.num;
        }
        
        public String getName() {
            return this.name;
        }
        
        public String getUrl() {
            return this.url;
        }
        
        public String getDesc() {
            return this.desc;
        }
        
        public static final ChallengeScenario[] getScenarios() {
            return ChallengeScenario.types;
        }
        
        public static ChallengeScenario fromInt(final int typeByte) {
            try {
                return ChallengeScenario.types[typeByte];
            }
            catch (Exception ex) {
                return ChallengeScenario.UNUSED;
            }
        }
        
        static {
            current = ChallengeScenario.DOMINANCE;
            types = values();
        }
    }
    
    public enum ChallengePoint
    {
        UNUSED(0, "Unused", "This should not be used", Float.MAX_VALUE), 
        OVERALL(1, "Overall", "Most overall points", Float.MAX_VALUE), 
        ITEMSLOOTED(2, "Items Looted", "Most items looted from resource points", Float.MAX_VALUE), 
        BATTLEPOINTS(3, "Battle Points", "Most Battle Points", Float.MAX_VALUE), 
        HOTAWINS(4, "Hota Wins", "Player with most HOTA wins", Float.MAX_VALUE), 
        PLAYERKILLS(5, "Player Kills", "Most Unique player kills", Float.MAX_VALUE), 
        KARMAGAINED(6, "Karma Gained", "Most Karma gained", Float.MAX_VALUE), 
        ACHIEVEMENTS(7, "Achievements", "Most Unique Achievements accomplished", Float.MAX_VALUE), 
        PERSONALACHIEVEMENTS(8, "Personal Achievements", "Most Personal Achievements accomplished", Float.MAX_VALUE), 
        LAND(9, "Most Land", "Ruler with most land", Float.MAX_VALUE), 
        VILLAGEHOTA(10, "Village Hota", "Village with most HOTA wins (Mayor receives prize to share)", Float.MAX_VALUE), 
        HOTAPILLARS(11, "Hota pillars", "Player who conquers most pillars first in a HOTA round", Float.MAX_VALUE), 
        TREASURE_CHESTS(12, "Treasure Chests", "Player who found and opened most treasure chests", Float.MAX_VALUE);
        
        private final int enumtype;
        private final String description;
        private final String name;
        private final float maxvalue;
        private boolean isDirty;
        private static final ChallengePoint[] types;
        
        private ChallengePoint(final int type, final String _description, final String _name, final float maxValue) {
            this.isDirty = false;
            this.name = _name;
            this.maxvalue = maxValue;
            this.enumtype = type;
            this.description = _description;
        }
        
        public int getEnumtype() {
            return this.enumtype;
        }
        
        public String getDescription() {
            return this.description;
        }
        
        public String getName() {
            return this.name;
        }
        
        public float getMaxvalue() {
            return this.maxvalue;
        }
        
        public static ChallengePoint[] getTypes() {
            return ChallengePoint.types;
        }
        
        public static ChallengePoint fromInt(final int typeByte) {
            try {
                return ChallengePoint.types[typeByte];
            }
            catch (Exception ex) {
                return ChallengePoint.UNUSED;
            }
        }
        
        public boolean isDirty() {
            return this.isDirty;
        }
        
        public final void setDirty(final boolean dirty) {
            this.isDirty = dirty;
        }
        
        static {
            types = values();
        }
    }
}
