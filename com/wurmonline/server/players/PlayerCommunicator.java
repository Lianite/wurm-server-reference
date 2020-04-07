// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import com.wurmonline.communication.SocketConnection;
import com.wurmonline.server.creatures.Communicator;

public class PlayerCommunicator extends Communicator
{
    public PlayerCommunicator(final Player aPlayer, final SocketConnection aConn) {
        super(aPlayer, aConn);
    }
}
