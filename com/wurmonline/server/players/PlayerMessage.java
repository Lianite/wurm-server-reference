// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import com.wurmonline.server.utils.ProtocolUtilities;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

final class PlayerMessage
{
    private static final Logger logger;
    private final Long iPlayerId;
    private final byte[] iMessageBytes;
    private final String iDescription;
    private final long iMessageId;
    private static final AtomicLong messageIdSequence;
    
    public PlayerMessage(final Long aPlayerId, final byte[] aMessageBytes) {
        this.iMessageId = PlayerMessage.messageIdSequence.getAndIncrement();
        if (PlayerMessage.logger.isLoggable(Level.FINEST)) {
            PlayerMessage.logger.finest("Constructor - PlayerId: " + aPlayerId + ", MessageBytes: " + Arrays.toString(aMessageBytes));
        }
        if (aPlayerId == null || aMessageBytes == null || aMessageBytes.length == 0) {
            throw new IllegalArgumentException("PlayerId and MessageBytes must be non-null andthe MessageBytes must conatin at least one byte");
        }
        this.iPlayerId = aPlayerId;
        this.iMessageBytes = aMessageBytes;
        this.iDescription = ProtocolUtilities.getDescriptionForCommand(this.iMessageBytes[0]);
    }
    
    long getMessageId() {
        return this.iMessageId;
    }
    
    Long getPlayerId() {
        return this.iPlayerId;
    }
    
    byte[] getMessageBytes() {
        return this.iMessageBytes;
    }
    
    String getDescription() {
        return this.iDescription;
    }
    
    @Override
    public boolean equals(final Object aObj) {
        boolean lEquals = false;
        if (aObj instanceof PlayerMessage) {
            final PlayerMessage lMessage = (PlayerMessage)aObj;
            if (lMessage.iMessageId != this.iMessageId && lMessage.iPlayerId != null && lMessage.iPlayerId.equals(this.iPlayerId) && lMessage.iMessageBytes != null && Arrays.equals(lMessage.iMessageBytes, this.iMessageBytes)) {
                lEquals = true;
            }
        }
        return lEquals;
    }
    
    @Override
    public int hashCode() {
        int lHashCode = (int)(this.iMessageId ^ this.iMessageId >>> 32);
        if (this.iPlayerId != null) {
            lHashCode ^= this.iPlayerId.hashCode();
        }
        if (this.iMessageBytes != null) {
            lHashCode ^= Arrays.hashCode(this.iMessageBytes);
        }
        return lHashCode;
    }
    
    @Override
    public String toString() {
        return "PlayerMessage [MessageId: " + this.iMessageId + ", PlayerId: " + this.iPlayerId + ", MessageBytes length: " + this.iMessageBytes.length + ", Description: " + this.iDescription + ']';
    }
    
    static {
        logger = Logger.getLogger(PlayerMessage.class.getName());
        messageIdSequence = new AtomicLong();
    }
}
