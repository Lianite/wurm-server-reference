// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import com.wurmonline.server.players.Player;
import java.io.IOException;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.Servers;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.Players;
import com.wurmonline.server.questions.PortalQuestion;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import com.wurmonline.shared.util.StreamUtilities;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;

public class WcOpenEpicPortal extends WebCommand
{
    private static final Logger logger;
    private boolean open;
    
    public WcOpenEpicPortal(final long _id, final boolean toggleOpen) {
        super(_id, (short)12);
        this.open = true;
        this.open = toggleOpen;
    }
    
    public WcOpenEpicPortal(final long _id, final byte[] _data) {
        super(_id, (short)12, _data);
        this.open = true;
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
            dos.writeBoolean(this.open);
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcOpenEpicPortal.logger.log(Level.WARNING, ex.getMessage(), ex);
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
            this.open = dis.readBoolean();
            PortalQuestion.epicPortalsEnabled = this.open;
            final Player[] players2;
            final Player[] players = players2 = Players.getInstance().getPlayers();
            for (final Player p : players2) {
                SoundPlayer.playSound("sound.music.song.mountaintop", p, 2.0f);
            }
            if (Servers.localServer.LOGINSERVER) {
                final WcOpenEpicPortal wccom = new WcOpenEpicPortal(WurmId.getNextWCCommandId(), PortalQuestion.epicPortalsEnabled);
                wccom.sendFromLoginServer();
            }
        }
        catch (IOException ex) {
            WcOpenEpicPortal.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeInputStreamIgnoreExceptions(dis);
        }
    }
    
    static {
        logger = Logger.getLogger(WcOpenEpicPortal.class.getName());
    }
}
