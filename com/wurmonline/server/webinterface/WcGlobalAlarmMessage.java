// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import java.io.IOException;
import com.wurmonline.server.Server;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import com.wurmonline.shared.util.StreamUtilities;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import com.wurmonline.server.WurmId;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class WcGlobalAlarmMessage extends WebCommand implements MiscConstants
{
    private static final Logger logger;
    private String alertMessage3;
    private long timeBetweenAlertMess3;
    private String alertMessage4;
    private long timeBetweenAlertMess4;
    
    public WcGlobalAlarmMessage(final String aMess3, final long aInterval3, final String aMess4, final long aInterval4) {
        super(WurmId.getNextWCCommandId(), (short)22);
        this.alertMessage3 = "";
        this.timeBetweenAlertMess3 = Long.MAX_VALUE;
        this.alertMessage4 = "";
        this.timeBetweenAlertMess4 = Long.MAX_VALUE;
        this.alertMessage3 = aMess3;
        this.timeBetweenAlertMess3 = aInterval3;
        this.alertMessage4 = aMess4;
        this.timeBetweenAlertMess4 = aInterval4;
    }
    
    WcGlobalAlarmMessage(final long aId, final byte[] _data) {
        super(aId, (short)22, _data);
        this.alertMessage3 = "";
        this.timeBetweenAlertMess3 = Long.MAX_VALUE;
        this.alertMessage4 = "";
        this.timeBetweenAlertMess4 = Long.MAX_VALUE;
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
            dos.writeUTF(this.alertMessage3);
            dos.writeLong(this.timeBetweenAlertMess3);
            dos.writeUTF(this.alertMessage4);
            dos.writeLong(this.timeBetweenAlertMess4);
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcGlobalAlarmMessage.logger.log(Level.WARNING, ex.getMessage(), ex);
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
            this.alertMessage3 = dis.readUTF();
            this.timeBetweenAlertMess3 = dis.readLong();
            this.alertMessage4 = dis.readUTF();
            this.timeBetweenAlertMess4 = dis.readLong();
            Server.alertMessage3 = this.alertMessage3;
            Server.timeBetweenAlertMess3 = this.timeBetweenAlertMess3;
            if (this.alertMessage3.length() == 0) {
                Server.lastAlertMess3 = Long.MAX_VALUE;
            }
            Server.alertMessage4 = this.alertMessage4;
            Server.timeBetweenAlertMess4 = this.timeBetweenAlertMess4;
            if (this.alertMessage4.length() == 0) {
                Server.lastAlertMess4 = Long.MAX_VALUE;
            }
        }
        catch (IOException ex) {
            WcGlobalAlarmMessage.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeInputStreamIgnoreExceptions(dis);
        }
    }
    
    static {
        logger = Logger.getLogger(WcGlobalAlarmMessage.class.getName());
    }
}
