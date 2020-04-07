// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.villages;

import com.wurmonline.server.WurmCalendar;

public class DeadVillage
{
    private static final String[] directions;
    private static final String[] distances;
    private final long deedId;
    private final int startX;
    private final int startY;
    private final int endX;
    private final int endY;
    private final String deedName;
    private final String founderName;
    private final String mayorName;
    private final long creationDate;
    private final long disbandDate;
    private final long lastLoginDate;
    private final byte kingdomId;
    
    public DeadVillage(final long deedId, final int startx, final int starty, final int endx, final int endy, final String name, final String founder, final String mayor, final long creationDate, final long disbandDate, final long lastLogin, final byte kingdom) {
        this.deedId = deedId;
        this.startX = startx;
        this.startY = starty;
        this.endX = endx;
        this.endY = endy;
        this.deedName = name;
        this.founderName = founder;
        this.mayorName = mayor;
        this.creationDate = creationDate;
        this.disbandDate = disbandDate;
        this.lastLoginDate = lastLogin;
        this.kingdomId = kingdom;
    }
    
    public long getDeedId() {
        return this.deedId;
    }
    
    public int getStartX() {
        return this.startX;
    }
    
    public int getStartY() {
        return this.startY;
    }
    
    public int getEndX() {
        return this.endX;
    }
    
    public int getEndY() {
        return this.endY;
    }
    
    public int getCenterX() {
        return this.getStartX() + (this.getEndX() - this.getStartX()) / 2;
    }
    
    public int getCenterY() {
        return this.getStartY() + (this.getEndY() - this.getStartY()) / 2;
    }
    
    public String getDeedName() {
        return this.deedName;
    }
    
    public String getFounderName() {
        return this.founderName;
    }
    
    public String getMayorName() {
        return this.mayorName;
    }
    
    public long getCreationDate() {
        return this.creationDate;
    }
    
    public long getDisbandDate() {
        return this.disbandDate;
    }
    
    public long getLastLoginDate() {
        return this.lastLoginDate;
    }
    
    public byte getKingdomId() {
        return this.kingdomId;
    }
    
    public float getTimeSinceDisband() {
        return (System.currentTimeMillis() - this.getLastLoginDate()) / 2.4192E9f;
    }
    
    public float getTotalAge() {
        return (this.getLastLoginDate() - this.getCreationDate()) / 2.4192E9f;
    }
    
    public String getDistanceFrom(final int tilex, final int tiley) {
        final int centerX = this.getStartX() + (this.getEndX() - this.getStartX()) / 2;
        final int centerY = this.getStartY() + (this.getEndY() - this.getStartY()) / 2;
        final int xDiff = centerX - tilex;
        final int yDiff = centerY - tiley;
        final int dist = Math.max(Math.abs(xDiff), Math.abs(yDiff));
        return getDistance(dist);
    }
    
    public String getDirectionFrom(final int tilex, final int tiley) {
        final int centerX = this.getStartX() + (this.getEndX() - this.getStartX()) / 2;
        final int centerY = this.getStartY() + (this.getEndY() - this.getStartY()) / 2;
        final int xDiff = centerX - tilex;
        final int yDiff = centerY - tiley;
        double degrees = Math.atan2(yDiff, xDiff) * 57.29577951308232 + 90.0;
        if (degrees < 0.0) {
            degrees += 360.0;
        }
        return getDirection(degrees);
    }
    
    private static String getDistance(final int dist) {
        if (dist <= 20) {
            return DeadVillage.distances[0];
        }
        if (dist <= 40) {
            return DeadVillage.distances[1];
        }
        if (dist <= 80) {
            return DeadVillage.distances[2];
        }
        if (dist <= 120) {
            return DeadVillage.distances[3];
        }
        if (dist <= 180) {
            return DeadVillage.distances[4];
        }
        return DeadVillage.distances[5];
    }
    
    private static String getDirection(final double degrees) {
        return DeadVillage.directions[(int)Math.round(degrees % 360.0 / 45.0) % 8];
    }
    
    public static final String getTimeString(final float monthsTotal, final boolean provideYear) {
        final StringBuilder sb = new StringBuilder();
        final int years = (int)(monthsTotal * 8.0f / 12.0f);
        final int months = (int)(monthsTotal * 8.0f) % 12;
        if (years > 0) {
            sb.append(years + " year" + ((years > 1) ? "s" : "") + ((months > 0) ? ", " : ""));
        }
        if (months > 0) {
            sb.append(months + " month" + ((months > 1) ? "s" : ""));
        }
        if (years <= 0 && months <= 0) {
            sb.append("less than a month");
        }
        if (provideYear) {
            sb.append(", somewhere around the year " + (WurmCalendar.getYear() - years));
        }
        return sb.toString();
    }
    
    static {
        directions = new String[] { "north", "northeast", "east", "southeast", "south", "southwest", "west", "northwest" };
        distances = new String[] { "very close", "nearby", "close", "far", "quite distant", "very far" };
    }
}
