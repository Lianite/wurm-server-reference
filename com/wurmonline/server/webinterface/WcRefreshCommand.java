// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import com.wurmonline.server.players.PlayerInfo;
import java.io.IOException;
import com.wurmonline.server.players.PlayerInfoFactory;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import com.wurmonline.shared.util.StreamUtilities;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;

public final class WcRefreshCommand extends WebCommand
{
    private static final Logger logger;
    private String nameToReload;
    
    public WcRefreshCommand(final long aId, final String _nameToReload) {
        super(aId, (short)5);
        this.nameToReload = _nameToReload;
    }
    
    public WcRefreshCommand(final long aId, final byte[] _data) {
        super(aId, (short)5, _data);
    }
    
    @Override
    public boolean autoForward() {
        return true;
    }
    
    @Override
    byte[] encode() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = null;
        byte[] barr = null;
        try {
            dos = new DataOutputStream(bos);
            dos.writeUTF(this.nameToReload);
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcRefreshCommand.logger.log(Level.WARNING, ex.getMessage(), ex);
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
            this.nameToReload = dis.readUTF();
            final PlayerInfo pinf = PlayerInfoFactory.createPlayerInfo(this.nameToReload);
            pinf.loaded = false;
            pinf.load();
        }
        catch (IOException ex) {
            WcRefreshCommand.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeInputStreamIgnoreExceptions(dis);
        }
    }
    
    static {
        logger = Logger.getLogger(WcRefreshCommand.class.getName());
    }
}
