// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import java.util.concurrent.LinkedBlockingDeque;
import com.wurmonline.shared.util.MulticolorLineSegment;
import java.util.List;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.Zones;
import javax.annotation.Nullable;
import com.wurmonline.server.creatures.Creature;
import java.util.regex.Matcher;
import com.wurmonline.server.players.Player;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.Queue;
import java.util.logging.Logger;

public final class MessageServer implements MiscConstants
{
    private static final Logger logger;
    private static final Logger chatlogger;
    private static final Queue<Message> MESSAGES;
    
    static synchronized void initialise() {
        MessageServer.logger.info("Initialising MessageServer");
    }
    
    public static void addMessage(final Message message) {
        MessageServer.MESSAGES.add(message);
    }
    
    static void sendMessages() {
        for (final Message message : MessageServer.MESSAGES) {
            long senderid = message.getSenderId();
            if (senderid < 0L && message.getSender() != null) {
                senderid = message.getSender().getWurmId();
            }
            if (message.getType() == 9) {
                MessageServer.chatlogger.log(Level.INFO, "PR-" + message.getMessage());
                final Player[] playarr = Players.getInstance().getPlayers();
                for (int x = 0; x < playarr.length; ++x) {
                    if (playarr[x].mayHearMgmtTalk() && !playarr[x].isIgnored(senderid)) {
                        playarr[x].getCommunicator().sendMessage(message);
                    }
                }
            }
            else if (message.getType() == 10) {
                MessageServer.chatlogger.log(Level.INFO, "SH-" + message.getMessage());
                final Player[] playarr = Players.getInstance().getPlayers();
                final byte kingdom = message.getSender().getKingdomId();
                for (int x2 = 0; x2 < playarr.length; ++x2) {
                    if (playarr[x2].isKingdomChat() && !playarr[x2].isIgnored(senderid) && (playarr[x2].getPower() > 0 || kingdom == playarr[x2].getKingdomId()) && !playarr[x2].getCommunicator().isInvulnerable()) {
                        playarr[x2].getCommunicator().sendMessage(message);
                    }
                }
            }
            else if (message.getType() == 16) {
                MessageServer.chatlogger.log(Level.INFO, "KSH-" + message.getMessage());
                final Player[] playarr = Players.getInstance().getPlayers();
                final byte kingdom = message.getSenderKingdom();
                for (int x2 = 0; x2 < playarr.length; ++x2) {
                    if (playarr[x2].isGlobalChat() && ((!playarr[x2].isIgnored(senderid) && kingdom == playarr[x2].getKingdomId()) || playarr[x2].getPower() > 0) && !playarr[x2].getCommunicator().isInvulnerable()) {
                        playarr[x2].getCommunicator().sendMessage(message);
                    }
                }
            }
            else if (message.getType() == 5) {
                MessageServer.chatlogger.log(Level.INFO, "BR-" + message.getMessage());
                Server.getInstance().twitLocalServer(message.getMessage());
                final Player[] players;
                final Player[] playarr = players = Players.getInstance().getPlayers();
                for (final Player lElement : players) {
                    lElement.getCommunicator().sendMessage(message);
                }
            }
            else if (message.getType() == 1) {
                MessageServer.chatlogger.log(Level.INFO, "ANN-" + message.getMessage());
                final Player[] players2;
                final Player[] playarr = players2 = Players.getInstance().getPlayers();
                for (final Player lElement : players2) {
                    lElement.getCommunicator().sendMessage(message);
                }
            }
            else if (message.getType() == 11) {
                MessageServer.chatlogger.log(Level.INFO, "GM-" + message.getMessage());
                final Player[] players3;
                final Player[] playarr = players3 = Players.getInstance().getPlayers();
                for (final Player lElement : players3) {
                    if (lElement.mayHearDevTalk()) {
                        lElement.getCommunicator().sendMessage(message);
                    }
                }
            }
            else if (message.getType() == 3) {
                try {
                    final Player p = Players.getInstance().getPlayer(message.getReceiver());
                    p.getCommunicator().sendMessage(message);
                }
                catch (NoSuchPlayerException ex) {}
            }
            else if (message.getType() == 17) {
                try {
                    final Player p = Players.getInstance().getPlayer(message.getReceiver());
                    p.getCommunicator().sendNormalServerMessage(message.getMessage());
                }
                catch (NoSuchPlayerException ex2) {}
            }
            else {
                if (message.getType() != 18) {
                    continue;
                }
                MessageServer.chatlogger.log(Level.INFO, "TD-" + message.getMessage());
                final Player[] playarr = Players.getInstance().getPlayers();
                final byte kingdom = message.getSenderKingdom();
                final int red = message.getRed();
                final int blue = message.getBlue();
                final int green = message.getGreen();
                for (int x3 = 0; x3 < playarr.length; ++x3) {
                    if (playarr[x3].isTradeChannel() && ((!playarr[x3].isIgnored(senderid) && kingdom == playarr[x3].getKingdomId()) || playarr[x3].getPower() > 0)) {
                        boolean tell = true;
                        if (message.getMessage().contains(") @")) {
                            final String patternString = "@" + playarr[x3].getName().toLowerCase() + "\\b";
                            final Pattern pattern = Pattern.compile(patternString);
                            final Matcher matcher = pattern.matcher(message.getMessage().toLowerCase());
                            tell = matcher.find();
                        }
                        final String patternString = "\\b" + playarr[x3].getName().toLowerCase() + "\\b";
                        final Pattern pattern = Pattern.compile(patternString);
                        final Matcher matcher = pattern.matcher(message.getMessage().toLowerCase());
                        if (matcher.find()) {
                            message.setColorR(100);
                            message.setColorG(170);
                            message.setColorB(255);
                        }
                        else {
                            message.setColorR(red);
                            message.setColorG(green);
                            message.setColorB(blue);
                        }
                        if (!playarr[x3].getCommunicator().isInvulnerable() && tell) {
                            playarr[x3].getCommunicator().sendMessage(message);
                        }
                    }
                }
            }
        }
        MessageServer.MESSAGES.clear();
    }
    
    public static void broadCastNormal(final String message) {
        if (MessageServer.logger.isLoggable(Level.FINE)) {
            MessageServer.logger.fine("Broadcasting Serverwide Normal message: " + message);
        }
        final Player[] players;
        final Player[] playarr = players = Players.getInstance().getPlayers();
        for (final Player lElement : players) {
            lElement.getCommunicator().sendNormalServerMessage(message);
        }
    }
    
    public static void broadCastSafe(final String message, final byte messageType) {
        if (MessageServer.logger.isLoggable(Level.FINE)) {
            MessageServer.logger.fine("Broadcasting Serverwide Safe message: " + message);
        }
        final Player[] players;
        final Player[] playarr = players = Players.getInstance().getPlayers();
        for (final Player lElement : players) {
            lElement.getCommunicator().sendSafeServerMessage(message, messageType);
        }
    }
    
    public static void broadCastAlert(final String message, final byte messageType) {
        MessageServer.logger.info("Broadcasting Serverwide Alert: " + message);
        final Player[] players;
        final Player[] playarr = players = Players.getInstance().getPlayers();
        for (final Player lPlayer : players) {
            lPlayer.getCommunicator().sendAlertServerMessage(message, messageType);
            if (lPlayer.isFighting()) {
                lPlayer.getCommunicator().sendCombatAlertMessage(message);
            }
        }
    }
    
    public static void broadCastAction(final String message, final Creature performer, final int tileDist) {
        broadCastAction(message, performer, null, tileDist, false);
    }
    
    public static void broadCastAction(final String message, final Creature performer, final Creature receiver, final int tileDist) {
        broadCastAction(message, performer, receiver, tileDist, false);
    }
    
    public static void broadCastAction(final String message, final Creature performer, @Nullable final Creature receiver, final int tileDist, final boolean combat) {
        if (message.length() > 0) {
            final int lTileDist = Math.abs(tileDist);
            final int tilex = performer.getTileX();
            final int tiley = performer.getTileY();
            for (int x = tilex - lTileDist; x <= tilex + lTileDist; ++x) {
                for (int y = tiley - lTileDist; y <= tiley + lTileDist; ++y) {
                    try {
                        final Zone zone = Zones.getZone(x, y, performer.isOnSurface());
                        final VolaTile tile = zone.getTileOrNull(x, y);
                        if (tile != null) {
                            tile.broadCastAction(message, performer, receiver, combat);
                        }
                    }
                    catch (NoSuchZoneException ex) {}
                }
            }
        }
    }
    
    public static void broadcastColoredAction(final List<MulticolorLineSegment> segments, final Creature performer, final int tileDist, final boolean combat) {
        broadcastColoredAction(segments, performer, null, tileDist, combat, (byte)0);
    }
    
    public static void broadcastColoredAction(final List<MulticolorLineSegment> segments, final Creature performer, @Nullable final Creature receiver, final int tileDist, final boolean combat) {
        broadcastColoredAction(segments, performer, receiver, tileDist, combat, (byte)0);
    }
    
    public static void broadcastColoredAction(final List<MulticolorLineSegment> segments, final Creature performer, @Nullable final Creature receiver, final int tileDist, final boolean combat, final byte onScreenMessage) {
        if (segments == null || segments.isEmpty()) {
            return;
        }
        final int lTileDist = Math.abs(tileDist);
        final int tilex = performer.getTileX();
        final int tiley = performer.getTileY();
        for (int x = tilex - lTileDist; x <= tilex + lTileDist; ++x) {
            for (int y = tiley - lTileDist; y <= tiley + lTileDist; ++y) {
                try {
                    final Zone zone = Zones.getZone(x, y, performer.isOnSurface());
                    final VolaTile tile = zone.getTileOrNull(x, y);
                    if (tile != null) {
                        tile.broadCastMulticolored(segments, performer, receiver, combat, onScreenMessage);
                    }
                }
                catch (NoSuchZoneException ex) {}
            }
        }
    }
    
    public static void broadCastMessage(final String message, final int tilex, final int tiley, final boolean surfaced, final int tiledistance) {
        if (message.length() > 0) {
            for (int x = tilex - tiledistance; x <= tilex + tiledistance; ++x) {
                for (int y = tiley - tiledistance; y <= tiley + tiledistance; ++y) {
                    try {
                        final Zone zone = Zones.getZone(x, y, surfaced);
                        final VolaTile tile = zone.getTileOrNull(x, y);
                        if (tile != null) {
                            tile.broadCast(message);
                        }
                    }
                    catch (NoSuchZoneException ex) {}
                }
            }
        }
    }
    
    static {
        logger = Logger.getLogger(MessageServer.class.getName());
        chatlogger = Logger.getLogger("Chat");
        MESSAGES = new LinkedBlockingDeque<Message>();
    }
}
