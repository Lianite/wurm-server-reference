// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import java.util.concurrent.LinkedBlockingQueue;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.sounds.Sound;
import com.wurmonline.server.items.Trade;
import com.wurmonline.server.structures.NoSuchWallException;
import com.wurmonline.server.structures.Door;
import com.wurmonline.shared.constants.StructureTypeEnum;
import com.wurmonline.server.structures.Wall;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.zones.VolaTile;
import java.math.BigInteger;
import java.util.Iterator;
import com.wurmonline.server.behaviours.ActionEntry;
import java.util.List;
import com.wurmonline.server.zones.Zones;
import java.io.IOException;
import com.wurmonline.server.zones.Water;
import com.wurmonline.server.Features;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Constants;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.WurmColor;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import com.wurmonline.server.items.Item;
import java.util.Date;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Players;
import java.util.logging.Level;
import com.wurmonline.server.Message;
import java.nio.ByteBuffer;
import com.wurmonline.server.Server;
import com.wurmonline.communication.SocketConnection;
import com.wurmonline.server.creatures.PlayerMove;
import java.text.DateFormat;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public final class PlayerCommunicatorQueued extends PlayerCommunicator
{
    private static final Logger logger;
    private static final BlockingQueue<PlayerMessage> MESSAGES_TO_PLAYERS;
    private static final String EMPTY_STRING = "";
    private long timeMod;
    private final DateFormat df;
    private long newSeed;
    private int newSeedPointer;
    private PlayerMove currentmove;
    boolean receivedTicks;
    private static final float woundMultiplier = 0.0015259022f;
    private static final int emptyRock;
    
    public PlayerCommunicatorQueued(final Player aPlayer, final SocketConnection aConn) {
        super(aPlayer, aConn);
        this.timeMod = 0L;
        this.df = DateFormat.getTimeInstance();
        this.newSeed = (Server.rand.nextInt() & Integer.MAX_VALUE);
        this.newSeedPointer = 0;
        this.receivedTicks = false;
        PlayerCommunicatorQueued.logger.info("Created");
    }
    
    private ByteBuffer getBuffer(final int aBufferCapacity) {
        final ByteBuffer lBuffer = ByteBuffer.allocate(aBufferCapacity);
        lBuffer.clear();
        return lBuffer;
    }
    
    @Override
    public boolean pollNextMove() {
        return super.pollNextMove();
    }
    
    @Override
    public void sendMessage(final Message message) {
        if (this.player.hasLink()) {
            try {
                final byte[] byteArray = message.getMessage().getBytes("UTF-8");
                final byte[] window = message.getWindow().getBytes();
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)99);
                bb.put((byte)window.length);
                bb.put(window);
                bb.put((byte)message.getRed());
                bb.put((byte)message.getGreen());
                bb.put((byte)message.getBlue());
                bb.putShort((short)byteArray.length);
                bb.put(byteArray);
                bb.put((byte)0);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + " could not send a message '" + message + "' due to : " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    private void addMessageToQueue(final ByteBuffer aByteBuffer) {
        final byte[] lArray = new byte[aByteBuffer.position()];
        System.arraycopy(aByteBuffer.array(), 0, lArray, 0, aByteBuffer.position());
        final PlayerMessage lPlayerMessage = new PlayerMessage(new Long(this.player.getWurmId()), lArray);
        try {
            if (Players.getInstance().getPlayer(this.player.getWurmId()) != null) {
                PlayerCommunicatorQueued.MESSAGES_TO_PLAYERS.add(lPlayerMessage);
                if (PlayerCommunicatorQueued.logger.isLoggable(Level.FINEST)) {
                    PlayerCommunicatorQueued.logger.finest("Added " + lPlayerMessage + " message with " + aByteBuffer.position() + " bytes to MESSAGES_TO_PLAYERS, queue size: " + PlayerCommunicatorQueued.MESSAGES_TO_PLAYERS.size());
                }
            }
        }
        catch (NoSuchPlayerException e) {
            PlayerCommunicatorQueued.logger.log(Level.WARNING, "Player is not in Players map so could not add " + lPlayerMessage + " message with " + aByteBuffer.position() + " bytes to MESSAGES_TO_PLAYERS - " + e.getMessage(), e);
        }
    }
    
    @Override
    public void sendGmMessage(final long time, final String sender, final String message) {
        if (this.player.hasLink()) {
            try {
                final String fd = this.df.format(new Date(time));
                final byte[] byteArray = (fd + " <" + sender + "> " + message).getBytes("UTF-8");
                final byte[] window = "GM".getBytes();
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)99);
                bb.put((byte)window.length);
                bb.put(window);
                bb.put((byte)(-56));
                bb.put((byte)(-56));
                bb.put((byte)(-56));
                bb.putShort((short)byteArray.length);
                bb.put(byteArray);
                bb.put((byte)0);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + " could not send a GM message '" + message + "' due to : " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendSafeServerMessage(final String message) {
        if (this.player.hasLink()) {
            try {
                final byte[] byteArray = message.getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)99);
                bb.put((byte)PlayerCommunicatorQueued.event.length);
                bb.put(PlayerCommunicatorQueued.event);
                bb.put((byte)102);
                bb.put((byte)(-72));
                bb.put((byte)120);
                bb.putShort((short)byteArray.length);
                bb.put(byteArray);
                bb.put((byte)0);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + " could not send a message '" + message + "' due to : " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendNormalServerMessage(final String message) {
        if (this.player.hasLink()) {
            try {
                final byte[] byteArray = message.getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)99);
                bb.put((byte)PlayerCommunicatorQueued.event.length);
                bb.put(PlayerCommunicatorQueued.event);
                bb.put((byte)(-1));
                bb.put((byte)(-1));
                bb.put((byte)(-1));
                bb.putShort((short)byteArray.length);
                bb.put(byteArray);
                bb.put((byte)0);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + " could not send a message '" + message + "' due to : " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendCombatNormalMessage(final String message) {
        this.sendCombatServerMessage(message, (byte)(-1), (byte)(-1), (byte)(-1));
    }
    
    @Override
    public void sendCombatAlertMessage(final String message) {
        this.sendCombatServerMessage(message, (byte)(-1), (byte)(-106), (byte)10);
    }
    
    @Override
    public void sendCombatSafeMessage(final String message) {
        this.sendCombatServerMessage(message, (byte)102, (byte)(-72), (byte)120);
    }
    
    @Override
    public void sendCombatServerMessage(final String message, final byte r, final byte g, final byte b) {
        if (this.player.hasLink()) {
            try {
                final byte[] byteArray = message.getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)99);
                bb.put((byte)PlayerCommunicatorQueued.combat.length);
                bb.put(PlayerCommunicatorQueued.combat);
                bb.put(r);
                bb.put(g);
                bb.put(b);
                bb.putShort((short)byteArray.length);
                bb.put(byteArray);
                bb.put((byte)0);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendAlertServerMessage(final String message) {
        if (this.player.hasLink()) {
            try {
                final byte[] byteArray = message.getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)99);
                bb.put((byte)PlayerCommunicatorQueued.event.length);
                bb.put(PlayerCommunicatorQueued.event);
                bb.put((byte)(-1));
                bb.put((byte)(-106));
                bb.put((byte)10);
                bb.putShort((short)byteArray.length);
                bb.put(byteArray);
                bb.put((byte)0);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ": " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendAddToInventory(final Item item, final long inventoryWindow, final long rootid, final int price) {
        if (this.player.hasLink()) {
            try {
                int weight = item.getFullWeight();
                if (item.isLockable() && item.getLockId() != -10L) {
                    try {
                        final Item lock = Items.getItem(item.getLockId());
                        if (!this.player.hasKeyForLock(lock)) {
                            weight = item.getFullWeight();
                        }
                    }
                    catch (NoSuchItemException nsi) {
                        PlayerCommunicatorQueued.logger.log(Level.WARNING, item.getWurmId() + " has lock " + item.getLockId() + " but that doesn't exist." + nsi.getMessage(), nsi);
                    }
                }
                byte[] byteArray = item.getName().getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)76);
                bb.putLong(inventoryWindow);
                long parentId = 0L;
                if (item.isBanked()) {
                    parentId = inventoryWindow;
                }
                else if (rootid != 0L && item.getParentId() > 0L) {
                    parentId = item.getParentId();
                }
                bb.putLong(parentId);
                bb.putLong(item.getWurmId());
                bb.putShort(item.getImageNumber());
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                byteArray = item.getDescription().getBytes("UTF-8");
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                bb.putFloat(item.getQualityLevel());
                bb.putFloat(item.getDamage());
                bb.putInt(weight);
                bb.put((byte)((item.color != -1) ? 1 : 0));
                if (item.color != -1) {
                    bb.put((byte)WurmColor.getColorRed(item.color));
                    bb.put((byte)WurmColor.getColorGreen(item.color));
                    bb.put((byte)WurmColor.getColorBlue(item.color));
                }
                bb.put((byte)((price >= 0) ? 1 : 0));
                if (price >= 0) {
                    bb.putInt(price);
                }
                this.addMessageToQueue(bb);
                if (!item.isEmpty(false) && item.isViewableBy(this.player)) {
                    this.sendHasMoreItems(inventoryWindow, item.getWurmId());
                }
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + item.getName() + ": " + item.getDescription(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendUpdateInventoryItem(final Item item, final long inventoryWindow, final int price) {
        if (this.player.hasLink()) {
            try {
                byte[] byteArray = item.getName().getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)68);
                bb.putLong(inventoryWindow);
                bb.putLong(item.getWurmId());
                long parentId = -1L;
                if (item.getParentId() > 0L) {
                    parentId = item.getParentId();
                }
                bb.putLong(parentId);
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                byteArray = item.getDescription().getBytes("UTF-8");
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                bb.putFloat(item.getQualityLevel());
                bb.putFloat(item.getDamage());
                bb.putInt(item.getFullWeight());
                bb.put((byte)((item.color != -1) ? 1 : 0));
                if (item.color != -1) {
                    bb.put((byte)WurmColor.getColorRed(item.color));
                    bb.put((byte)WurmColor.getColorGreen(item.color));
                    bb.put((byte)WurmColor.getColorBlue(item.color));
                }
                bb.put((byte)((price >= 0) ? 1 : 0));
                if (price >= 0) {
                    bb.putInt(price);
                }
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendUpdateInventoryItem(final Item item) {
        long inventoryWindow = item.getTopParent();
        if (this.player == null) {
            PlayerCommunicatorQueued.logger.log(Level.WARNING, "Player is null ", new Exception());
        }
        if (item.getOwnerId() == this.player.getWurmId()) {
            inventoryWindow = -1L;
        }
        this.sendUpdateInventoryItem(item, inventoryWindow, -1);
        if (item.isTraded()) {
            item.getTradeWindow().updateItem(item);
        }
    }
    
    @Override
    public void sendRemoveFromInventory(final Item item, final long inventoryWindow) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(17);
                bb.put((byte)(-10));
                bb.putLong(inventoryWindow);
                bb.putLong(item.getWurmId());
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendRemoveFromInventory(final Item item) {
        if (this.player != null) {
            long inventoryWindow = item.getTopParent();
            if (item.getOwnerId() == this.player.getWurmId()) {
                inventoryWindow = -1L;
            }
            this.sendRemoveFromInventory(item, inventoryWindow);
        }
    }
    
    @Override
    public void sendOpenInventoryWindow(final long inventoryWindow, final String title) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(32767);
                final byte[] byteArray = title.getBytes("UTF-8");
                bb.put((byte)116);
                bb.putLong(inventoryWindow);
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public boolean sendCloseInventoryWindow(final long inventoryWindow) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(9);
                bb.put((byte)120);
                bb.putLong(inventoryWindow);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
        try {
            return this.player.removeItemWatched(Items.getItem(inventoryWindow));
        }
        catch (NoSuchItemException nsi) {
            return true;
        }
    }
    
    @Override
    public void sendNewCreature(final long id, final String name, final String model, final float x, final float y, final float z, final long onBridge, final float rot, final byte layer, final boolean onGround, final boolean floating, final boolean isSolid, final byte kingdomId, final long face, final byte blood, final boolean isUndead, final boolean isCopy, final byte modtype) {
        if (this.player.hasLink()) {
            try {
                byte[] byteArray = model.getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)108);
                bb.putLong(id);
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                bb.put((byte)(isSolid ? 1 : 0));
                bb.putFloat(y);
                bb.putFloat(x);
                bb.putFloat(rot);
                if (onGround) {
                    if (Structure.isGroundFloorAtPosition(x, y, layer == 0)) {
                        bb.putFloat(z + 0.1f);
                    }
                    else {
                        bb.putFloat(-3000.0f);
                    }
                }
                else {
                    bb.putFloat(z);
                }
                byteArray = name.getBytes("UTF-8");
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                bb.putLong(onBridge);
                if (floating) {
                    bb.put((byte)1);
                }
                else {
                    bb.put((byte)0);
                }
                bb.put(layer);
                if ((WurmId.getType(id) == 0 || isCopy) && !isUndead) {
                    bb.put((byte)1);
                }
                else {
                    bb.put((byte)0);
                }
                bb.put((byte)0);
                bb.put(kingdomId);
                bb.putLong(face);
                if ((WurmId.getType(id) == 0 || isCopy) && !isUndead) {
                    bb.putInt(Math.abs(Communicator.generateSoundSourceId(id)));
                }
                bb.put(blood);
                bb.put(modtype);
                bb.put((byte)0);
                this.addMessageToQueue(bb);
            }
            catch (NullPointerException np) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, this.player.getName() + ":" + np.getMessage(), np);
                this.player.setLink(false);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, this.player.getName() + ": " + name + " " + id + " " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendMoveCreature(final long id, final float x, final float y, final int rot, final boolean keepMoving) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(12);
                bb.put((byte)36);
                bb.putLong(id);
                bb.putFloat(y);
                bb.putFloat(x);
                bb.put((byte)rot);
                this.addMessageToQueue(bb);
            }
            catch (NullPointerException np) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, np.getMessage(), np);
                this.player.setLink(false);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendMoveCreatureAndSetZ(final long id, final float x, final float y, final float z, final int rot) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(14);
                bb.put((byte)72);
                bb.putLong(id);
                bb.putFloat(z);
                bb.putFloat(x);
                bb.put((byte)rot);
                bb.putFloat(y);
                this.addMessageToQueue(bb);
            }
            catch (NullPointerException np) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, np.getMessage(), np);
                this.player.setLink(false);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendCreatureChangedLayer(final long wurmid, final byte newlayer) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(10);
                bb.put((byte)30);
                bb.putLong(wurmid);
                bb.put(newlayer);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendDeleteCreature(final long id) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(9);
                bb.put((byte)14);
                bb.putLong(id);
                this.addMessageToQueue(bb);
            }
            catch (NullPointerException np) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, this.player.getName() + ":" + np.getMessage(), np);
                this.player.setLink(false);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendTileStripFar(final short xStart, final short yStart, final int width, final int height) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)103);
                bb.putShort(xStart);
                bb.putShort(yStart);
                bb.putShort((short)width);
                bb.putShort((short)height);
                for (int x = 0; x < width; ++x) {
                    for (int y = 0; y < height; ++y) {
                        int xx = (xStart + x) * 16;
                        int yy = (yStart + y) * 16;
                        if (xx < 0 || xx >= 1 << Constants.meshSize || yy < 0 || yy >= 1 << Constants.meshSize) {
                            xx = 0;
                            yy = 0;
                        }
                        bb.putShort(Tiles.decodeHeight(Server.surfaceMesh.data[xx | yy << Constants.meshSize]));
                    }
                }
                for (int x = 0; x < width; ++x) {
                    final int ms = Constants.meshSize - 4;
                    for (int y2 = 0; y2 < height; ++y2) {
                        int xx2 = xStart + x;
                        int yy2 = yStart + y2;
                        if (xx2 < 0 || xx2 >= 1 << ms || yy2 < 0 || yy2 >= 1 << ms) {
                            xx2 = 0;
                            yy2 = 0;
                        }
                        bb.put(Server.surfaceMesh.getDistantTerrainTypes()[xx2 | yy2 << ms]);
                    }
                }
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendTileStrip(final short xStart, final short yStart, final int width, final int height) throws IOException {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)73);
                bb.put((byte)(Features.Feature.SURFACEWATER.isEnabled() ? 1 : 0));
                bb.put((byte)(this.player.isSendExtraBytes() ? 1 : 0));
                bb.putShort(yStart);
                bb.putShort((short)width);
                bb.putShort((short)height);
                bb.putShort(xStart);
                for (int x = 0; x < width; ++x) {
                    for (int y = 0; y < height; ++y) {
                        int tempTileX = xStart + x;
                        int tempTileY = yStart + y;
                        if (tempTileX < 0 || tempTileX >= 1 << Constants.meshSize || tempTileY < 0 || tempTileY >= 1 << Constants.meshSize) {
                            tempTileX = 0;
                            tempTileY = 0;
                        }
                        bb.putInt(Server.surfaceMesh.data[tempTileX | tempTileY << Constants.meshSize]);
                        if (Features.Feature.SURFACEWATER.isEnabled()) {
                            bb.putShort((short)Water.getSurfaceWater(tempTileX, tempTileY));
                        }
                        if (this.player.isSendExtraBytes()) {
                            bb.put(Server.getClientSurfaceFlags(tempTileX, tempTileY));
                        }
                    }
                }
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
                throw new IOException(this.player.getName() + ":" + ex.getMessage());
            }
        }
    }
    
    @Override
    public void sendCaveStrip(final short xStart, final short yStart, final int width, final int height) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)102);
                bb.put((byte)(Features.Feature.CAVEWATER.isEnabled() ? 1 : 0));
                bb.put((byte)(this.player.isSendExtraBytes() ? 1 : 0));
                bb.putShort(xStart);
                bb.putShort(yStart);
                bb.putShort((short)width);
                bb.putShort((short)height);
                final boolean onSurface = this.player.isOnSurface();
                for (int x = 0; x < width; ++x) {
                    for (int y = 0; y < height; ++y) {
                        int xx = xStart + x;
                        int yy = yStart + y;
                        if (xx < 0 || xx >= Zones.worldTileSizeX || yy < 0 || yy >= Zones.worldTileSizeY) {
                            bb.putInt(PlayerCommunicatorQueued.emptyRock);
                            xx = 0;
                            yy = 0;
                        }
                        else if (!onSurface) {
                            bb.putInt(Server.caveMesh.data[xx | yy << Constants.meshSize]);
                        }
                        else if (Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.data[xx | yy << Constants.meshSize]))) {
                            bb.putInt(this.getDummyWall(xx, yy));
                        }
                        else {
                            bb.putInt(Server.caveMesh.data[xx | yy << Constants.meshSize]);
                        }
                        if (Features.Feature.CAVEWATER.isEnabled()) {
                            bb.putShort((short)Water.getCaveWater(xx, yy));
                        }
                        if (this.player.isSendExtraBytes()) {
                            bb.put(Server.getClientCaveFlags(xx, yy));
                        }
                    }
                }
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    private int getDummyWall(final int tilex, final int tiley) {
        return Tiles.encode(Tiles.decodeHeight(Server.caveMesh.data[tilex | tiley << Constants.meshSize]), Tiles.Tile.TILE_CAVE_WALL.id, Tiles.decodeData(Server.caveMesh.data[tilex | tiley << Constants.meshSize]));
    }
    
    private boolean isCaveWallHidden(final int tilex, final int tiley) {
        return this.isCaveWallSolid(tilex, tiley) && this.isCaveWallSolid(tilex, tiley - 1) && this.isCaveWallSolid(tilex + 1, tiley) && this.isCaveWallSolid(tilex, tiley + 1) && this.isCaveWallSolid(tilex - 1, tiley);
    }
    
    private boolean isCaveWallSolid(final int tilex, final int tiley) {
        return tilex < 0 || tilex >= Zones.worldTileSizeX || tiley < 0 || tiley >= Zones.worldTileSizeY || Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.data[tilex | tiley << Constants.meshSize]));
    }
    
    @Override
    public void sendAvailableActions(final byte requestId, final List<ActionEntry> availableActions, final String helpstring) {
        if (this.player.hasLink()) {
            try {
                if (PlayerCommunicatorQueued.logger.isLoggable(Level.FINEST)) {
                    PlayerCommunicatorQueued.logger.finest(this.player.getName() + ", sending # of available Actions: " + availableActions.size() + ", requestId: " + String.valueOf(requestId) + ", availableActions: " + availableActions + ", helpstring: " + helpstring);
                }
                else if (PlayerCommunicatorQueued.logger.isLoggable(Level.FINER)) {
                    PlayerCommunicatorQueued.logger.finer(this.player.getName() + ", sending # of available Actions: " + availableActions.size() + " , requestId: " + String.valueOf(requestId) + ", helpstring: " + helpstring);
                }
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)20);
                bb.put(requestId);
                bb.put((byte)availableActions.size());
                for (final ActionEntry entry : availableActions) {
                    bb.putShort(entry.getNumber());
                    final String actionString = entry.getActionString();
                    final byte[] byteArray = actionString.getBytes("UTF-8");
                    bb.put((byte)byteArray.length);
                    bb.put(byteArray);
                    if (entry.isQuickSkillLess()) {
                        bb.put((byte)1);
                    }
                    else {
                        bb.put((byte)0);
                    }
                }
                final byte[] byteArray2 = helpstring.getBytes("UTF-8");
                bb.put((byte)byteArray2.length);
                bb.put(byteArray2);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
        else if (PlayerCommunicatorQueued.logger.isLoggable(Level.FINER)) {
            PlayerCommunicatorQueued.logger.finer("Not sending Available Actions as Player has lost link, requestId: " + String.valueOf(requestId) + ", availableActions: " + availableActions + ", helpstring: " + helpstring);
        }
    }
    
    @Override
    public void sendItem(final Item item, final long creatureId, final boolean onGroundLevel) {
        if (this.player.hasLink()) {
            try {
                final long id = item.getWurmId();
                byte[] byteArray = item.getModelName().getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)(-9));
                bb.putLong(id);
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                byteArray = item.getName().getBytes("UTF-8");
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                bb.putFloat(item.getPosX());
                bb.putFloat(item.getPosY());
                bb.putFloat(item.getRotation());
                if (item.isFloating() && item.getPosZ() <= 0.0f) {
                    if (item.getCurrentQualityLevel() < 10.0f) {
                        bb.putFloat(-3000.0f);
                    }
                    else {
                        bb.putFloat(0.0f);
                    }
                }
                else if (item.getFloorLevel() > 0 || !onGroundLevel) {
                    bb.putFloat(item.getPosZ());
                }
                else {
                    bb.putFloat(-3000.0f);
                }
                bb.put((byte)(item.isOnSurface() ? 0 : -1));
                byteArray = item.getDescription().getBytes("UTF-8");
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                bb.putShort(item.getImageNumber());
                this.addMessageToQueue(bb);
                if (PlayerCommunicatorQueued.logger.isLoggable(Level.FINER)) {
                    PlayerCommunicatorQueued.logger.finer(this.player.getName() + " sent item " + item.getName() + " - " + item.getWurmId());
                }
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, "Failed to send item: " + this.player.getName() + ":" + item.getWurmId() + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendRename(final Item item, final String newName, final String newModelName) {
        if (this.player.hasLink()) {
            try {
                final long id = item.getWurmId();
                byte[] byteArray = newName.getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)44);
                bb.putLong(id);
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                bb.put(item.getMaterial());
                byteArray = item.getDescription().getBytes("UTF-8");
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                bb.putShort(item.getImageNumber());
                bb.put(item.getRarity());
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, "Failed to rename item: " + this.player.getName() + ":" + item.getWurmId() + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendRemoveItem(final Item item) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(9);
                bb.put((byte)10);
                bb.putLong(item.getWurmId());
                if (PlayerCommunicatorQueued.logger.isLoggable(Level.FINER)) {
                    PlayerCommunicatorQueued.logger.finer(this.player.getName() + " Sending remove " + item.getWurmId());
                }
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendAddSkill(final int id, final int parentSkillId, final String name, final float value, final float maxValue, final int affinities) {
        if (this.player.hasLink()) {
            try {
                final byte[] byteArray = name.getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(byteArray.length + 22);
                bb.put((byte)124);
                bb.putLong(BigInteger.valueOf(parentSkillId).shiftLeft(32).longValue() + 18L);
                bb.putLong(BigInteger.valueOf(id).shiftLeft(32).longValue() + 18L);
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                bb.putFloat(value);
                bb.putFloat(maxValue);
                bb.put((byte)affinities);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendUpdateSkill(final int id, final float value, final int affinities) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(13);
                bb.put((byte)66);
                bb.putLong((id << 32) + 18L);
                bb.putFloat(value);
                bb.put((byte)affinities);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendActionControl(final long creatureId, final String actionString, final boolean start, final int timeLeft) {
        if (this.player.hasLink()) {
            try {
                byte[] byteArray = "".getBytes("UTF-8");
                if (start) {
                    byteArray = actionString.getBytes("UTF-8");
                }
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)(-12));
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                final int lTimeLeft = Math.min(timeLeft, 65535);
                bb.putShort((short)lTimeLeft);
                bb.putLong(creatureId);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendAddEffect(final long id, final short type, final float x, final float y, final float z, final byte layer) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(24);
                bb.put((byte)64);
                bb.putLong(id);
                bb.putShort(type);
                bb.putFloat(x);
                bb.putFloat(y);
                bb.putFloat(z);
                bb.put(layer);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendRemoveEffect(final long id) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(9);
                bb.put((byte)37);
                bb.putLong(id);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    protected void sendStamina(final int stamina, final int damage) {
        if (this.player.hasLink() && !this.player.isTransferring()) {
            try {
                final ByteBuffer bb = this.getBuffer(5);
                bb.put((byte)90);
                final short lStamina = (short)((stamina & 0xFFFE) | (this.newSeed >> this.newSeedPointer++ & 0x1L));
                bb.putShort(lStamina);
                bb.putShort((short)damage);
                this.addMessageToQueue(bb);
                if (this.newSeedPointer == 32) {
                    this.getConnection().encryptRandom.setSeed(this.newSeed & -1L);
                    this.getConnection().changeProtocol(this.newSeed);
                    this.newSeedPointer = 0;
                    this.newSeed = (Server.rand.nextInt() & Integer.MAX_VALUE);
                }
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    protected void sendThirst(final int thirst) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(3);
                bb.put((byte)105);
                bb.putShort((short)thirst);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendHunger(final int hunger, final float nutrition, final float calories, final float carbs, final float fats, final float proteins) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(3);
                bb.put((byte)61);
                bb.putShort((short)hunger);
                bb.put((byte)(nutrition * 100.0f));
                bb.put((byte)(calories * 100.0f));
                bb.put((byte)(carbs * 100.0f));
                bb.put((byte)(fats * 100.0f));
                bb.put((byte)(proteins * 100.0f));
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    protected void sendWeight(final byte weight) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)5);
                bb.put(weight);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    protected void sendSpeedModifier(final float speedModifier) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(5);
                bb.put((byte)32);
                bb.putFloat(speedModifier);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    protected void sendTimeLeft(final short tenthOfSeconds) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)87);
                bb.putShort(tenthOfSeconds);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendSingleBuildMarker(final long structureId, final int tilex, final int tiley, final byte layer) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(14);
                bb.put((byte)96);
                bb.putLong(structureId);
                bb.put(layer);
                bb.put((byte)1);
                bb.putShort((short)tilex);
                bb.putShort((short)tiley);
                this.addMessageToQueue(bb);
                if (PlayerCommunicatorQueued.logger.isLoggable(Level.FINER)) {
                    PlayerCommunicatorQueued.logger.finer("adding or removing single marker");
                }
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendMultipleBuildMarkers(final long structureId, final VolaTile[] tiles, final byte layer) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)96);
                bb.putLong(structureId);
                bb.put(layer);
                bb.put((byte)tiles.length);
                for (int x = 0; x < tiles.length; ++x) {
                    bb.putShort((short)tiles[x].getTileX());
                    bb.putShort((short)tiles[x].getTileY());
                }
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendAddStructure(final String name, final short centerTilex, final short centerTiley, final long structureId, final byte structureType, final byte layer) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)112);
                bb.putLong(structureId);
                bb.put(structureType);
                final byte[] byteArray = name.getBytes("UTF-8");
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                bb.putShort(centerTilex);
                bb.putShort(centerTiley);
                bb.put(layer);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendRemoveStructure(final long structureId) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(9);
                bb.put((byte)48);
                bb.putLong(structureId);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    protected void sendUpdateFence(final Fence fence) {
        this.sendRemoveFence(fence);
        this.sendAddFence(fence);
    }
    
    @Override
    public void sendAddWall(final long structureId, final Wall wall) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)49);
                bb.putLong(structureId);
                bb.putShort((short)Math.min(wall.getStartY(), wall.getEndY()));
                bb.putShort((short)Math.min(wall.getStartX(), wall.getEndX()));
                if (wall.isHorizontal()) {
                    bb.put((byte)0);
                }
                else {
                    bb.put((byte)1);
                }
                if (wall.isFinished()) {
                    bb.put((byte)wall.getType().ordinal());
                }
                else {
                    bb.put((byte)StructureTypeEnum.PLAN.ordinal());
                }
                final byte[] byteArray = wall.getMaterialString().getBytes();
                if (PlayerCommunicatorQueued.logger.isLoggable(Level.FINER)) {
                    PlayerCommunicatorQueued.logger.finer(structureId + " Updating " + wall.getMaterialString());
                }
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                bb.put((byte)((wall.getColor() != -1) ? 1 : 0));
                if (wall.getColor() != -1) {
                    bb.put((byte)WurmColor.getColorRed(wall.getColor()));
                    bb.put((byte)WurmColor.getColorGreen(wall.getColor()));
                    bb.put((byte)WurmColor.getColorBlue(wall.getColor()));
                }
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendPassable(final boolean passable, final Door door) {
        if (this.player.hasLink()) {
            try {
                final Wall wall = door.getWall();
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)125);
                bb.putLong(door.getStructureId());
                bb.putShort((short)Math.min(wall.getStartX(), wall.getEndX()));
                bb.putShort((short)Math.min(wall.getStartY(), wall.getEndY()));
                if (wall.isHorizontal()) {
                    bb.put((byte)0);
                }
                else {
                    bb.put((byte)1);
                }
                if (passable) {
                    bb.put((byte)1);
                }
                else {
                    bb.put((byte)0);
                }
                this.addMessageToQueue(bb);
            }
            catch (NoSuchWallException nsw) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, this.player.getName() + ": Trying to make door passable for wall with no id! Structure=" + door.getStructureId(), nsw);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendOpenDoor(final Door door) {
        if (this.player.hasLink()) {
            try {
                final Wall wall = door.getWall();
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)122);
                bb.putLong(door.getStructureId());
                bb.putShort((short)Math.min(wall.getStartX(), wall.getEndX()));
                bb.putShort((short)Math.min(wall.getStartY(), wall.getEndY()));
                if (wall.isHorizontal()) {
                    bb.put((byte)0);
                }
                else {
                    bb.put((byte)1);
                }
                this.addMessageToQueue(bb);
            }
            catch (NoSuchWallException nsw) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, this.player.getName() + ": trying to open door for wall with no id! Structure=" + door.getStructureId(), nsw);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendCloseDoor(final Door door) {
        if (this.player.hasLink()) {
            try {
                final Wall wall = door.getWall();
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)127);
                bb.putLong(door.getStructureId());
                bb.putShort((short)Math.min(wall.getStartX(), wall.getEndX()));
                bb.putShort((short)Math.min(wall.getStartY(), wall.getEndY()));
                if (wall.isHorizontal()) {
                    bb.put((byte)0);
                }
                else {
                    bb.put((byte)1);
                }
                this.addMessageToQueue(bb);
            }
            catch (NoSuchWallException nsw) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, this.player.getName() + ": trying to close door for wall with no id! Structure=" + door.getStructureId(), nsw);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendBml(final int width, final int height, final boolean resizeable, final boolean closeable, final String content, final int r, final int g, final int b, final String title) {
        if (this.player.hasLink()) {
            try {
                byte[] byteArray = title.getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)106);
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                bb.putShort((short)width);
                bb.putShort((short)height);
                bb.put((byte)(closeable ? 1 : 0));
                bb.put((byte)(resizeable ? 1 : 0));
                bb.put((byte)r);
                bb.put((byte)g);
                bb.put((byte)b);
                byteArray = content.getBytes("UTF-8");
                bb.putShort((short)byteArray.length);
                bb.put(byteArray);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendChangeStructureName(final long structureId, final String newName) {
        if (this.player.hasLink()) {
            try {
                final byte[] byteArray = newName.getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)47);
                bb.put((byte)0);
                bb.putLong(structureId);
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendUseBinoculars() {
        this.sendClientFeature((byte)1, true);
    }
    
    @Override
    public void sendStopUseBinoculars() {
        this.sendClientFeature((byte)1, false);
    }
    
    @Override
    public void sendToggle(final int toggle, final boolean set) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(3);
                bb.put((byte)62);
                bb.put((byte)toggle);
                bb.put((byte)(set ? 1 : 0));
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, "Problem sending toggle (" + toggle + ',' + set + ") to " + this.player.getName() + " due to :" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendTeleport(final boolean aLocal) {
        this.sendTeleport(aLocal, true, (byte)0);
    }
    
    @Override
    public void sendTeleport(final boolean aLocal, final boolean disembark, final byte commandType) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(21);
                bb.put((byte)51);
                bb.putFloat(this.player.getStatus().getPositionX());
                bb.putFloat(this.player.getStatus().getPositionY());
                bb.putFloat(this.player.getStatus().getPositionZ());
                bb.putFloat(this.player.getStatus().getRotation());
                bb.put((byte)(aLocal ? 1 : 0));
                bb.put((byte)(this.player.isOnSurface() ? 0 : -1));
                bb.put((byte)(disembark ? 1 : 0));
                bb.put(commandType);
                this.addMessageToQueue(bb);
                this.currentmove = null;
                this.setMoves(0);
                this.receivedTicks = false;
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, "Problem sending teleport (local: " + aLocal + ") to " + this.player.getName() + " due to :" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendStartTrading(final Creature opponent) {
        final Trade trade = this.player.getTrade();
        if (trade != null && this.player.hasLink()) {
            try {
                final String name = opponent.getName();
                final byte[] byteArray = name.getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)119);
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                if (trade.creatureOne == this.player) {
                    bb.putLong(1L);
                    bb.putLong(2L);
                    bb.putLong(3L);
                    bb.putLong(4L);
                }
                else {
                    bb.putLong(2L);
                    bb.putLong(1L);
                    bb.putLong(4L);
                    bb.putLong(3L);
                }
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendCloseTradeWindow() {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(1);
                bb.put((byte)121);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendTradeAgree(final Creature agreer, final boolean agree) {
        if (this.player.hasLink()) {
            try {
                boolean me = false;
                if (agreer == this.player) {
                    me = true;
                }
                if (me && agree) {
                    return;
                }
                final ByteBuffer bb = this.getBuffer(2);
                bb.put((byte)42);
                bb.put((byte)(agree ? 1 : 0));
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendTradeChanged(final int id) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(5);
                bb.put((byte)91);
                bb.putInt(id);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendAddFence(final Fence fence) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)12);
                bb.putShort((short)fence.getTileX());
                bb.putShort((short)fence.getTileY());
                bb.put(fence.getDir().getCode());
                bb.putShort((short)fence.getType().ordinal());
                bb.put((byte)(fence.isFinished() ? 1 : 0));
                bb.put((byte)((fence.getColor() != -1) ? 1 : 0));
                if (fence.getColor() != -1) {
                    bb.put((byte)WurmColor.getColorRed(fence.getColor()));
                    bb.put((byte)WurmColor.getColorGreen(fence.getColor()));
                    bb.put((byte)WurmColor.getColorBlue(fence.getColor()));
                }
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + " adding fence: " + fence + " :" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendRemoveFence(final Fence fence) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)13);
                bb.putShort((short)fence.getTileX());
                bb.putShort((short)fence.getTileY());
                bb.put(fence.getDir().getCode());
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + " problem removing fence: " + fence + " due to :" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendOpenFence(final Fence fence, final boolean passable, final boolean changePassable) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)83);
                bb.putShort((short)fence.getTileX());
                bb.putShort((short)fence.getTileY());
                bb.put(fence.getDir().getCode());
                bb.put((byte)1);
                if (changePassable) {
                    bb.put((byte)(passable ? 1 : 0));
                }
                else {
                    bb.put((byte)2);
                }
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + " problem opening fence: " + fence + " due to :" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendCloseFence(final Fence fence, final boolean passable, final boolean changePassable) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)83);
                bb.putShort((short)fence.getTileX());
                bb.putShort((short)fence.getTileY());
                bb.put(fence.getDir().getCode());
                bb.put((byte)0);
                if (changePassable) {
                    bb.put((byte)(passable ? 1 : 0));
                }
                else {
                    bb.put((byte)2);
                }
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + " problem closing fence: " + fence + " due to :" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendSound(final Sound sound) {
        if (this.player.hasLink()) {
            try {
                final String name = sound.getName();
                final byte[] byteArray = name.getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)86);
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                bb.putFloat(sound.getPosX());
                bb.putFloat(sound.getPosY());
                bb.putFloat(sound.getPosZ());
                bb.putFloat(sound.getPitch());
                bb.putFloat(sound.getVolume());
                bb.putFloat(sound.getPriority());
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendMusic(final Sound sound) {
        if (this.player.hasLink()) {
            try {
                final String name = sound.getName();
                final byte[] byteArray = name.getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)115);
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                bb.putFloat(sound.getPosX());
                bb.putFloat(sound.getPosY());
                bb.putFloat(sound.getPosZ());
                bb.putFloat(sound.getPitch());
                bb.putFloat(sound.getVolume());
                bb.putFloat(sound.getPriority());
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    protected void sendStatus(final String status) {
        if (this.player.hasLink()) {
            try {
                final byte[] byteArray = status.getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)(-18));
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendAddWound(final Wound wound, final Item bodyPart) {
        if (this.player.hasLink()) {
            try {
                byte[] byteArray = wound.getName().getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)76);
                if (this.player == wound.getCreature()) {
                    bb.putLong(-1L);
                }
                else {
                    final Item body = wound.getCreature().getBody().getBodyItem();
                    bb.putLong(body.getWurmId());
                }
                final long parentId = bodyPart.getWurmId();
                bb.putLong(parentId);
                bb.putLong(wound.getWurmId());
                bb.putShort((short)wound.getWoundIconId());
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                byteArray = wound.getDescription().getBytes("UTF-8");
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                bb.putFloat(100.0f);
                if (PlayerCommunicatorQueued.logger.isLoggable(Level.FINER)) {
                    PlayerCommunicatorQueued.logger.finer("Sending wound ID: " + wound.getWurmId() + ", severity: " + wound.getSeverity() + "*" + 0.0015259022f + "=" + wound.getSeverity() * 0.0015259022f);
                }
                bb.putFloat(wound.getSeverity() * 0.0015259022f);
                bb.putInt(0);
                bb.put((byte)0);
                bb.put((byte)0);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + wound.getWoundString(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendRemoveWound(final Wound wound) {
        if (this.player.hasLink()) {
            try {
                if (PlayerCommunicatorQueued.logger.isLoggable(Level.FINER)) {
                    PlayerCommunicatorQueued.logger.finer("Removing wound ID " + wound.getWurmId() + " from player inventory.");
                }
                final ByteBuffer bb = this.getBuffer(17);
                bb.put((byte)(-10));
                if (this.player == wound.getCreature()) {
                    bb.putLong(-1L);
                }
                else {
                    final Item body = wound.getCreature().getBody().getBodyItem();
                    bb.putLong(body.getWurmId());
                }
                bb.putLong(wound.getWurmId());
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendUpdateWound(final Wound wound, final Item bodyPart) {
        if (this.player.hasLink()) {
            try {
                byte[] byteArray = wound.getName().getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)68);
                if (this.player == wound.getCreature()) {
                    bb.putLong(-1L);
                }
                else {
                    final Item body = wound.getCreature().getBody().getBodyItem();
                    bb.putLong(body.getWurmId());
                }
                bb.putLong(wound.getWurmId());
                final long parentId = bodyPart.getWurmId();
                bb.putLong(parentId);
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                byteArray = wound.getDescription().getBytes("UTF-8");
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                bb.putFloat(100.0f);
                if (PlayerCommunicatorQueued.logger.isLoggable(Level.FINER)) {
                    PlayerCommunicatorQueued.logger.finer("Sending wound ID: " + wound.getWurmId() + ", severity: " + wound.getSeverity() + "*" + 0.0015259022f + "=" + wound.getSeverity() * 0.0015259022f);
                }
                bb.putFloat(wound.getSeverity() * 0.0015259022f);
                bb.putInt(0);
                bb.put((byte)0);
                bb.put((byte)0);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendSelfToLocal() {
        if (this.player.hasLink()) {
            try {
                final byte[] byteArray = this.player.getName().getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)(-13));
                bb.put((byte)PlayerCommunicatorQueued.local.length);
                bb.put(PlayerCommunicatorQueued.local);
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                bb.putLong(this.player.getWurmId());
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendAddVillager(final String name, final long wurmid) {
        if (this.player.hasLink()) {
            try {
                final byte[] byteArray = name.getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)(-13));
                bb.put((byte)PlayerCommunicatorQueued.village.length);
                bb.put(PlayerCommunicatorQueued.village);
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                bb.putLong(wurmid);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendRemoveVillager(final String name) {
        if (this.player.hasLink()) {
            try {
                final byte[] byteArray = name.getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)114);
                bb.put((byte)PlayerCommunicatorQueued.village.length);
                bb.put(PlayerCommunicatorQueued.village);
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendAddAlly(final String name, final long wurmid) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = name.getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)(-13));
                bb.put((byte)PlayerCommunicatorQueued.alliance.length);
                bb.put(PlayerCommunicatorQueued.alliance);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.putLong(wurmid);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendRemoveAlly(final String name) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = name.getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)114);
                bb.put((byte)PlayerCommunicatorQueued.alliance.length);
                bb.put(PlayerCommunicatorQueued.alliance);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendAddLocal(final String name, final long wurmid) {
        if (this.player.hasLink()) {
            try {
                final byte[] byteArray = name.getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)(-13));
                bb.put((byte)PlayerCommunicatorQueued.local.length);
                bb.put(PlayerCommunicatorQueued.local);
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                bb.putLong(wurmid);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendRemoveLocal(final String name) {
        if (this.player.hasLink()) {
            try {
                final byte[] byteArray = name.getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)114);
                bb.put((byte)PlayerCommunicatorQueued.local.length);
                bb.put(PlayerCommunicatorQueued.local);
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendAddGm(final String name, final long wurmid) {
        if (this.player.hasLink()) {
            try {
                final byte[] byteArray = name.getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)(-13));
                bb.put((byte)PlayerCommunicatorQueued.gms.length);
                bb.put(PlayerCommunicatorQueued.gms);
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                bb.putLong(wurmid);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendRemoveGm(final String name) {
        if (this.player.hasLink()) {
            try {
                final byte[] byteArray = name.getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)114);
                bb.put((byte)PlayerCommunicatorQueued.gms.length);
                bb.put(PlayerCommunicatorQueued.gms);
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void changeAttitude(final long creatureId, final byte status) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(10);
                bb.put((byte)6);
                bb.putLong(creatureId);
                bb.put(status);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendWeather() {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(29);
                bb.put((byte)46);
                bb.putFloat(Server.getWeather().getCloudiness());
                bb.putFloat(Server.getWeather().getFog());
                bb.putFloat(Server.getWeather().getRain());
                bb.putFloat(Server.getWeather().getXWind());
                bb.putFloat(Server.getWeather().getYWind());
                bb.putFloat(Server.getWeather().getWindRotation());
                bb.putFloat(Server.getWeather().getWindPower());
                this.addMessageToQueue(bb);
                this.sendNormalServerMessage("The wind is now coming from " + Server.getWeather().getWindRotation() + "- strength x=" + Server.getWeather().getXWind() + ", y=" + Server.getWeather().getYWind());
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendDead() {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(3);
                bb.put((byte)65);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendClimb(final boolean climbing) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(2);
                bb.put((byte)79);
                bb.put((byte)(climbing ? 1 : 0));
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendReconnect(final String ip, final int port, final String session) {
        if (PlayerCommunicatorQueued.logger.isLoggable(Level.FINE)) {
            PlayerCommunicatorQueued.logger.fine("Sending reconnect to server: " + ip + ':' + port + " to " + this.player);
        }
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)23);
                byte[] byteArray = ip.getBytes("UTF-8");
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                bb.putInt(port);
                byteArray = session.getBytes("UTF-8");
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    protected void sendHasMoreItems(final long inventoryId, final long wurmid) {
        if (this.player.hasLink()) {
            try {
                if (PlayerCommunicatorQueued.logger.isLoggable(Level.FINER)) {
                    PlayerCommunicatorQueued.logger.finer("Inventory " + inventoryId + " containing Wurmid " + wurmid + " has MORE.");
                }
                final ByteBuffer bb = this.getBuffer(17);
                bb.put((byte)29);
                bb.putLong(inventoryId);
                bb.putLong(wurmid);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendIsEmpty(final long inventoryId, final long wurmid) {
        if (this.player.hasLink()) {
            try {
                if (PlayerCommunicatorQueued.logger.isLoggable(Level.FINER)) {
                    PlayerCommunicatorQueued.logger.finer("Inventory " + inventoryId + " containing Wurmid " + wurmid + " has no more items.");
                }
                final ByteBuffer bb = this.getBuffer(17);
                bb.put((byte)(-16));
                bb.putLong(inventoryId);
                bb.putLong(wurmid);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    protected void sendCompass(final Item item) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(3);
                bb.put((byte)(-30));
                bb.put((byte)0);
                if (item == null) {
                    bb.put((byte)0);
                }
                else {
                    bb.put((byte)Math.max(1.0f, item.getCurrentQualityLevel()));
                }
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    protected void sendToolbelt(final Item item) {
        if (this.player.hasLink()) {
            try {
                if (PlayerCommunicatorQueued.logger.isLoggable(Level.FINER)) {
                    if (item != null) {
                        PlayerCommunicatorQueued.logger.finer(this.player.getName() + " sending toolbelt with wurmid " + item.getWurmId() + ".");
                    }
                    else {
                        PlayerCommunicatorQueued.logger.finer(this.player.getName() + " sending toolbelt null.");
                    }
                }
                final ByteBuffer bb = this.getBuffer(3);
                bb.put((byte)(-30));
                bb.put((byte)2);
                if (item == null) {
                    bb.put((byte)0);
                }
                else {
                    bb.put((byte)Math.max(1.0f, item.getCurrentQualityLevel()));
                }
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    private void sendClientFeature(final byte feature, final boolean on) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(3);
                bb.put((byte)(-30));
                bb.put(feature);
                if (on) {
                    bb.put((byte)1);
                }
                else {
                    bb.put((byte)0);
                }
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendServerTime() {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(17);
                bb.put((byte)107);
                bb.putLong(System.currentTimeMillis());
                bb.putLong(WurmCalendar.currentTime + this.timeMod);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendAttachEffect(final long targetId, final byte effectType, final byte data0, final byte data1, final byte data2, final byte dimension) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(13);
                bb.put((byte)109);
                bb.putLong(targetId);
                bb.put(effectType);
                bb.put(data0);
                bb.put(data1);
                bb.put(data2);
                bb.put(dimension);
                if (PlayerCommunicatorQueued.logger.isLoggable(Level.FINEST)) {
                    PlayerCommunicatorQueued.logger.finest(this.player.getName() + ": " + targetId + ", light colour: " + data0 + ", " + data1 + ", " + data2);
                }
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendRemoveEffect(final long targetId, final byte effectType) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(10);
                bb.put((byte)18);
                bb.putLong(targetId);
                bb.put(effectType);
                this.addMessageToQueue(bb);
                if (PlayerCommunicatorQueued.logger.isLoggable(Level.FINER)) {
                    PlayerCommunicatorQueued.logger.finer(this.player.getName() + " removing :" + targetId + ", light " + effectType);
                }
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendWieldItem(final long creatureId, final byte slot, final String modelname, final byte rarity, final int colorRed, final int colorGreen, final int colorBlue, final int secondaryColorRed, final int secondaryColorGreen, final int secondaryColorBlue) {
        if (this.player.hasLink()) {
            try {
                if (creatureId == -1L || WurmId.getType(creatureId) == 0) {
                    final ByteBuffer bb = this.getBuffer(32767);
                    bb.put((byte)101);
                    bb.putLong(creatureId);
                    bb.put(slot);
                    final byte[] byteArray = modelname.getBytes("UTF-8");
                    bb.putShort((short)byteArray.length);
                    bb.put(byteArray);
                    bb.put(rarity);
                    bb.putInt(colorRed);
                    bb.putInt(colorGreen);
                    bb.putInt(colorBlue);
                    bb.putInt(secondaryColorRed);
                    bb.putInt(secondaryColorGreen);
                    bb.putInt(secondaryColorBlue);
                    this.addMessageToQueue(bb);
                }
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendUseItem(final long creatureId, final String modelname, final byte rarity, final int colorRed, final int colorGreen, final int colorBlue, final int secondaryColorRed, final int secondaryColorGreen, final int secondaryColorBlue) {
        if (this.player.hasLink()) {
            try {
                if (creatureId == -1L || WurmId.getType(creatureId) == 0) {
                    final ByteBuffer bb = this.getBuffer(32767);
                    bb.put((byte)110);
                    bb.putLong(creatureId);
                    final byte[] byteArray = modelname.getBytes("UTF-8");
                    bb.putShort((short)byteArray.length);
                    bb.put(byteArray);
                    bb.put(rarity);
                    bb.putFloat(colorRed);
                    bb.putFloat(colorGreen);
                    bb.putFloat(colorBlue);
                    bb.putInt(secondaryColorRed);
                    bb.putInt(secondaryColorGreen);
                    bb.putInt(secondaryColorBlue);
                    this.addMessageToQueue(bb);
                }
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendStopUseItem(final long creatureId) {
        if (this.player.hasLink()) {
            try {
                if (creatureId == -1L || WurmId.getType(creatureId) == 0) {
                    final ByteBuffer bb = this.getBuffer(9);
                    bb.put((byte)71);
                    bb.putLong(creatureId);
                    this.addMessageToQueue(bb);
                }
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendRepaint(final long wurmid, final byte r, final byte g, final byte b, final byte alpha, final byte paintType) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(14);
                bb.put((byte)92);
                bb.putLong(wurmid);
                bb.put(r);
                bb.put(g);
                bb.put(b);
                bb.put(alpha);
                bb.put(paintType);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ": " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendResize(final long wurmid, final byte xscaleMod, final byte yscaleMod, final byte zscaleMod) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(12);
                bb.put((byte)74);
                bb.putLong(wurmid);
                bb.put(xscaleMod);
                bb.put(yscaleMod);
                bb.put(zscaleMod);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendNewMovingItem(final long id, final String name, final String model, final float x, final float y, final float z, final long onBridge, final float rot, final byte layer, final boolean onGround, final boolean floating, final boolean isSolid, final byte material, final byte rarity) {
        if (this.player.hasLink()) {
            try {
                byte[] byteArray = model.getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)108);
                bb.putLong(id);
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                bb.put((byte)(isSolid ? 1 : 0));
                bb.putFloat(y);
                bb.putFloat(x);
                bb.putLong(onBridge);
                bb.putFloat(rot);
                if (onGround) {
                    if (Structure.isGroundFloorAtPosition(x, y, layer == 0)) {
                        bb.putFloat(z + 0.1f);
                    }
                    else {
                        bb.putFloat(-3000.0f);
                    }
                }
                else {
                    bb.putFloat(z);
                }
                byteArray = name.getBytes("UTF-8");
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                if (floating) {
                    bb.put((byte)1);
                }
                else {
                    bb.put((byte)0);
                }
                bb.put((byte)2);
                bb.put(layer);
                bb.put(material);
                bb.put((byte)0);
                bb.put(rarity);
                this.addMessageToQueue(bb);
                if (PlayerCommunicatorQueued.logger.isLoggable(Level.FINER)) {
                    PlayerCommunicatorQueued.logger.finer(this.player.getName() + " sent creature " + name + " model= " + model + " x " + x + " y " + y + " z " + z);
                }
            }
            catch (NullPointerException np) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, this.player.getName() + ":" + np.getMessage(), np);
                this.player.setLink(false);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendMoveMovingItem(final long id, final float x, final float y, final int rot) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(12);
                bb.put((byte)36);
                bb.putLong(id);
                bb.putFloat(x);
                bb.putFloat(y);
                bb.put((byte)rot);
                this.addMessageToQueue(bb);
            }
            catch (NullPointerException np) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, np.getMessage(), np);
                this.player.setLink(false);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendMoveMovingItemAndSetZ(final long id, final float x, final float y, final float z, final int rot) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(14);
                bb.put((byte)72);
                bb.putLong(id);
                bb.putFloat(x);
                bb.putFloat(y);
                bb.putFloat(z);
                bb.put((byte)rot);
                this.addMessageToQueue(bb);
            }
            catch (NullPointerException np) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, np.getMessage(), np);
                this.player.setLink(false);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendMovingItemChangedLayer(final long wurmid, final byte newlayer) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(10);
                bb.put((byte)30);
                bb.putLong(wurmid);
                bb.put(newlayer);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendDeleteMovingItem(final long id) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(9);
                bb.put((byte)14);
                bb.putLong(id);
                this.addMessageToQueue(bb);
            }
            catch (NullPointerException np) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, this.player.getName() + ":" + np.getMessage(), np);
                this.player.setLink(false);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendShutDown(final String reason, final boolean requested) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(3);
                bb.put((byte)4);
                final byte[] tempStringArr = reason.getBytes("UTF-8");
                bb.putShort((short)tempStringArr.length);
                bb.put(tempStringArr);
                bb.put((byte)(requested ? 1 : 0));
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void attachCreature(final long source, final long target, final float offx, final float offy, final float offz, final int seatId) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(29);
                bb.put((byte)111);
                bb.putLong(source);
                bb.putLong(target);
                bb.putFloat(offx);
                bb.putFloat(offy);
                bb.putFloat(offz);
                bb.put((byte)seatId);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void setVehicleController(final long playerId, final long targetId, final float offx, final float offy, final float offz, final float maxDepth, final float maxHeight, final float maxHeightDiff, final float vehicleRotation, final int seatId) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(45);
                bb.put((byte)63);
                bb.putLong(playerId);
                bb.putLong(targetId);
                bb.putFloat(offx);
                bb.putFloat(offy);
                bb.putFloat(offz);
                bb.putFloat(maxDepth);
                bb.putFloat(maxHeight);
                bb.putFloat(maxHeightDiff);
                bb.putFloat(vehicleRotation);
                bb.put((byte)seatId);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendAnimation(final long creatureId, final String animationName, final boolean looping, final boolean freezeAtFinish) {
        if (this.player.hasLink() && creatureId > 0L) {
            try {
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)24);
                bb.putLong(creatureId);
                final byte[] byteArray = animationName.getBytes("UTF-8");
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                if (looping) {
                    bb.put((byte)1);
                }
                else {
                    bb.put((byte)0);
                }
                if (freezeAtFinish) {
                    bb.put((byte)1);
                }
                else {
                    bb.put((byte)0);
                }
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendCombatOptions(final byte[] options, final short tenthsOfSeconds) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)98);
                bb.put((byte)options.length);
                bb.put(options);
                bb.putShort(tenthsOfSeconds);
                this.addMessageToQueue(bb);
            }
            catch (NullPointerException np) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, this.player.getName() + ":" + np.getMessage(), np);
                this.player.setLink(false);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    protected void sendCombatStatus(final float distanceToTarget, final float footing, final byte stance) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(10);
                bb.put((byte)(-14));
                bb.putFloat(distanceToTarget);
                bb.putFloat(footing);
                bb.put(stance);
                this.addMessageToQueue(bb);
            }
            catch (NullPointerException np) {
                PlayerCommunicatorQueued.logger.log(Level.WARNING, this.player.getName() + ":" + np.getMessage(), np);
                this.player.setLink(false);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    protected void sendStunned(final boolean stunned) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)28);
                if (stunned) {
                    bb.put((byte)1);
                }
                else {
                    bb.put((byte)0);
                }
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendSpecialMove(final short move, final String movename) {
        if (this.player.hasLink()) {
            try {
                final byte[] byteArray = movename.getBytes("UTF-8");
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)(-17));
                bb.putShort(move);
                bb.put((byte)byteArray.length);
                bb.put(byteArray);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendToggleShield(final boolean on) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(32767);
                bb.put((byte)(-17));
                bb.putShort((short)105);
                if (on) {
                    bb.put((byte)1);
                }
                else {
                    bb.put((byte)0);
                }
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    protected void sendTarget(final long id) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(9);
                bb.put((byte)25);
                bb.putLong(id);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    protected void sendFightStyle(final byte style) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(2);
                bb.put((byte)26);
                bb.put(style);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void setCreatureDamage(final long wurmid, final float damagePercent) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(13);
                bb.put((byte)11);
                bb.putLong(wurmid);
                bb.putFloat(damagePercent);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendWindImpact(final byte windimpact) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(2);
                bb.put((byte)117);
                bb.put(windimpact);
                this.addMessageToQueue(bb);
                this.player.sentWind = System.currentTimeMillis();
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendMountSpeed(final short mountSpeed) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(2);
                bb.put((byte)60);
                bb.putShort(mountSpeed);
                this.addMessageToQueue(bb);
                this.player.sentMountSpeed = System.currentTimeMillis();
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    @Override
    public void sendRotate(final long itemId, final float rotation) {
        if (this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.getBuffer(13);
                bb.put((byte)67);
                bb.putLong(itemId);
                bb.putFloat(rotation);
                this.addMessageToQueue(bb);
            }
            catch (Exception ex) {
                PlayerCommunicatorQueued.logger.log(Level.INFO, this.player.getName() + ":" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    static BlockingQueue<PlayerMessage> getMessageQueue() {
        return PlayerCommunicatorQueued.MESSAGES_TO_PLAYERS;
    }
    
    @Override
    public String toString() {
        return super.toString();
    }
    
    static {
        logger = Logger.getLogger(PlayerCommunicatorQueued.class.getName());
        MESSAGES_TO_PLAYERS = new LinkedBlockingQueue<PlayerMessage>();
        emptyRock = Tiles.encode((short)(-100), Tiles.Tile.TILE_CAVE_WALL.id, (byte)0);
    }
}
