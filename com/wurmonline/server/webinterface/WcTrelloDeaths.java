// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import java.io.IOException;
import com.wurmonline.server.support.Trello;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import com.wurmonline.shared.util.StreamUtilities;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import com.wurmonline.server.Servers;
import com.wurmonline.server.WurmId;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class WcTrelloDeaths extends WebCommand implements MiscConstants
{
    private static final Logger logger;
    private String server;
    private String title;
    private String description;
    
    WcTrelloDeaths(final long aId, final byte[] _data) {
        super(aId, (short)35, _data);
        this.server = "";
        this.title = "";
        this.description = "";
    }
    
    public WcTrelloDeaths(final String title, final String description) {
        super(WurmId.getNextWCCommandId(), (short)35);
        this.server = "";
        this.title = "";
        this.description = "";
        this.server = Servers.localServer.getAbbreviation();
        this.title = title;
        this.description = description;
    }
    
    @Override
    public boolean autoForward() {
        return false;
    }
    
    public byte[] encode() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = null;
        byte[] barr = null;
        try {
            dos = new DataOutputStream(bos);
            dos.writeUTF(this.server);
            dos.writeUTF(this.title);
            dos.writeUTF(this.description);
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcTrelloDeaths.logger.log(Level.WARNING, "Pack exception " + ex.getMessage(), ex);
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
            this.server = dis.readUTF();
            this.title = dis.readUTF();
            this.description = dis.readUTF();
            Trello.addImportantDeathsMessage(this.server, this.title, this.description);
        }
        catch (IOException ex) {
            WcTrelloDeaths.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeInputStreamIgnoreExceptions(dis);
        }
    }
    
    static {
        logger = Logger.getLogger(WcTrelloDeaths.class.getName());
    }
}
