// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.shared.constants;

public enum TicketGroup
{
    NONE(0, ""), 
    OPEN(-1, "Open tickets"), 
    FORUM(-2, "Forum requests"), 
    WATCH(-3, "Players to watch"), 
    CLOSED(-4, "Closed tickets");
    
    private final byte id;
    private final String name;
    private static final TicketGroup[] types;
    
    private TicketGroup(final int id, final String name) {
        this.id = (byte)id;
        this.name = name;
    }
    
    public byte getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getKey() {
        return "G" + Math.abs(this.id);
    }
    
    public static TicketGroup ticketGroupFromId(final byte aId) {
        for (int i = 0; i < TicketGroup.types.length; ++i) {
            if (aId == TicketGroup.types[i].getId()) {
                return TicketGroup.types[i];
            }
        }
        return TicketGroup.NONE;
    }
    
    static {
        types = values();
    }
}
