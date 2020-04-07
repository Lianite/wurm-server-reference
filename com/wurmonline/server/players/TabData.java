// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import com.wurmonline.server.ServerEntry;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import com.wurmonline.server.Servers;
import com.wurmonline.server.MiscConstants;

public class TabData implements MiscConstants
{
    private final long wurmId;
    private final String name;
    private final byte power;
    private final boolean isVisible;
    private final int serverId;
    
    public TabData(final long wurmid, final String name, final byte power, final boolean isVisible) {
        this.wurmId = wurmid;
        this.name = name;
        this.isVisible = isVisible;
        this.power = power;
        this.serverId = Servers.getLocalServerId();
    }
    
    public TabData(final DataInputStream dis) throws IOException {
        this.wurmId = dis.readLong();
        this.name = dis.readUTF();
        this.isVisible = dis.readBoolean();
        this.power = dis.readByte();
        this.serverId = dis.readInt();
    }
    
    public void pack(final DataOutputStream dos) throws IOException {
        dos.writeLong(this.wurmId);
        dos.writeUTF(this.name);
        dos.writeBoolean(this.isVisible);
        dos.writeByte(this.power);
        dos.writeInt(this.serverId);
    }
    
    public long getWurmId() {
        return this.wurmId;
    }
    
    public String getName() {
        if (this.serverId == Servers.getLocalServerId()) {
            return this.name;
        }
        final ServerEntry se = Servers.getServerWithId(this.serverId);
        if (se != null) {
            return this.name + " (" + se.getAbbreviation() + ")";
        }
        return this.name + " (" + this.serverId + ")";
    }
    
    public byte getPower() {
        return this.power;
    }
    
    public boolean isVisible() {
        return this.isVisible;
    }
    
    public int getServerId() {
        return this.serverId;
    }
    
    @Override
    public String toString() {
        return "(TabData:" + this.wurmId + "," + this.name + "," + this.power + "," + this.isVisible + "," + this.serverId + ")";
    }
}
