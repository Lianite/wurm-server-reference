// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.villages;

public class VillageRecruitee
{
    final int villageId;
    final String recruiteeName;
    final long recruiteeId;
    
    public VillageRecruitee(final int v_id, final long r_id, final String r_name) {
        this.villageId = v_id;
        this.recruiteeId = r_id;
        this.recruiteeName = r_name;
    }
    
    public final int getVillageId() {
        return this.villageId;
    }
    
    public final String getRecruiteeName() {
        return this.recruiteeName;
    }
    
    public final long getRecruiteeId() {
        return this.recruiteeId;
    }
}
