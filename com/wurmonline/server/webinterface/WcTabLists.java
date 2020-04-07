// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import java.io.IOException;
import com.wurmonline.server.Players;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import com.wurmonline.shared.util.StreamUtilities;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.players.TabData;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class WcTabLists extends WebCommand implements MiscConstants
{
    private static final Logger logger;
    public static final byte TAB_GM = 0;
    public static final byte TAB_MGMT = 1;
    public static final byte REMOVE = 2;
    private byte tab;
    private TabData tabData;
    
    WcTabLists(final long aId, final byte[] _data) {
        super(aId, (short)31, _data);
    }
    
    public WcTabLists(final byte tab, final TabData tabData) {
        super(WurmId.getNextWCCommandId(), (short)31);
        this.tab = tab;
        this.tabData = tabData;
    }
    
    @Override
    public boolean autoForward() {
        return true;
    }
    
    public byte[] encode() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = null;
        byte[] barr = null;
        try {
            dos = new DataOutputStream(bos);
            dos.writeByte(this.tab);
            this.tabData.pack(dos);
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcTabLists.logger.log(Level.WARNING, "Pack exception " + ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeOutputStreamIgnoreExceptions(dos);
            barr = bos.toByteArray();
            StreamUtilities.closeOutputStreamIgnoreExceptions(bos);
            this.setData(barr);
        }
        return barr;
    }
    
    @Override
    public void execute() {
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(new ByteArrayInputStream(this.getData()));
            this.tab = dis.readByte();
            this.tabData = new TabData(dis);
            Players.getInstance().updateTabs(this.tab, this.tabData);
        }
        catch (IOException ex) {
            WcTabLists.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeInputStreamIgnoreExceptions(dis);
        }
    }
    
    static {
        logger = Logger.getLogger(WcTabLists.class.getName());
    }
}
