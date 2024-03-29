// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.creatures.NoSuchCreatureTemplateException;
import com.wurmonline.server.creatures.CreatureTemplateFactory;
import com.wurmonline.server.epic.Effectuator;
import com.wurmonline.server.epic.SynchedEpicEffect;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.Message;
import com.wurmonline.server.HistoryManager;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.DbCreatureStatus;
import com.wurmonline.server.Players;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.creatures.CreatureTemplateCreator;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.Servers;
import java.io.IOException;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.WurmId;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import com.wurmonline.shared.util.StreamUtilities;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import com.wurmonline.server.Server;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.CounterTypes;
import com.wurmonline.server.creatures.CreatureTemplateIds;

public class WcEpicEvent extends WebCommand implements CreatureTemplateIds, CounterTypes
{
    private static final Logger logger;
    private int effectNumber;
    private long deityNumber;
    private int templateId;
    private int bonusEffectNum;
    private String eventString;
    private boolean resettingKarma;
    
    public WcEpicEvent(final long _id, final int effectNum, final long deityNum, final int selectedTemplate, final int bonusEffect, final String eventDesc, final boolean resetKarma) {
        super(_id, (short)9);
        this.eventString = "";
        this.resettingKarma = false;
        this.effectNumber = effectNum;
        this.deityNumber = deityNum;
        this.templateId = selectedTemplate;
        this.bonusEffectNum = bonusEffect;
        if (this.bonusEffectNum < 0) {
            this.bonusEffectNum = Server.rand.nextInt(4);
        }
        this.eventString = eventDesc;
        this.resettingKarma = resetKarma;
        this.isRestrictedEpic = true;
    }
    
    public WcEpicEvent(final long _id, final byte[] _data) {
        super(_id, (short)9, _data);
        this.eventString = "";
        this.resettingKarma = false;
        this.isRestrictedEpic = true;
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
            dos.writeInt(this.effectNumber);
            dos.writeLong(this.deityNumber);
            dos.writeInt(this.templateId);
            dos.writeInt(this.bonusEffectNum);
            dos.writeUTF(this.eventString);
            dos.writeBoolean(this.resettingKarma);
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcEpicEvent.logger.log(Level.WARNING, ex.getMessage(), ex);
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
            this.effectNumber = dis.readInt();
            this.deityNumber = dis.readLong();
            this.templateId = dis.readInt();
            this.bonusEffectNum = dis.readInt();
            this.eventString = dis.readUTF();
            this.resettingKarma = dis.readBoolean();
            if (this.bonusEffectNum > 0) {
                if (this.bonusEffectNum == 4 || this.bonusEffectNum == 5) {
                    String creatureName = "Unknown";
                    if (WurmId.getType(this.deityNumber) == 0) {
                        final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(this.deityNumber);
                        if (pinf == null) {
                            return;
                        }
                        if (!pinf.loaded) {
                            try {
                                pinf.load();
                                creatureName = pinf.getName();
                            }
                            catch (IOException iox) {
                                return;
                            }
                        }
                        if (pinf.currentServer != Servers.localServer.id) {
                            return;
                        }
                    }
                    try {
                        final boolean fragment = this.bonusEffectNum == 5;
                        final Item i = ItemFactory.createItem(fragment ? 1307 : this.templateId, 80 + Server.rand.nextInt(20), this.eventString);
                        if (fragment) {
                            i.setRealTemplate(this.templateId);
                            i.setAuxData((byte)1);
                        }
                        if (i.getTemplateId() == 465) {
                            i.setData1(CreatureTemplateCreator.getRandomDragonOrDrakeId());
                        }
                        else if (i.getTemplateId() == 794) {
                            final Deity[] deities;
                            final Deity[] deityArr = deities = Deities.getDeities();
                            for (final Deity de : deities) {
                                if (de.getName().equalsIgnoreCase(this.eventString)) {
                                    i.setAuxData((byte)de.getNumber());
                                    i.setData1(577);
                                }
                            }
                        }
                        else if (i.isAbility()) {
                            i.setAuxData((byte)2);
                        }
                        if (WurmId.getType(this.deityNumber) == 1) {
                            try {
                                final Creature c = Creatures.getInstance().getCreature(this.deityNumber);
                                c.getInventory().insertItem(i, true);
                                creatureName = c.getName();
                            }
                            catch (NoSuchCreatureException ex2) {}
                        }
                        else {
                            try {
                                final Player p = Players.getInstance().getPlayer(this.deityNumber);
                                p.getInventory().insertItem(i);
                                creatureName = p.getName();
                            }
                            catch (NoSuchPlayerException nsp) {
                                final long inventory = DbCreatureStatus.getInventoryIdFor(this.deityNumber);
                                i.setParentId(inventory, true);
                                i.setOwnerId(this.deityNumber);
                            }
                        }
                        HistoryManager.addHistory(creatureName, "receives the " + i.getName() + " from " + this.eventString + ".");
                    }
                    catch (Exception nsi) {
                        WcEpicEvent.logger.log(Level.WARNING, nsi.getMessage(), nsi);
                    }
                }
                else {
                    final Message mess = new Message(null, (byte)5, ":Event", this.eventString);
                    Server.getInstance().addMessage(mess);
                    final SynchedEpicEffect eff = new SynchedEpicEffect(3);
                    eff.setDeityNumber(this.deityNumber);
                    eff.setBonusEffectNum(this.bonusEffectNum);
                    eff.setResetKarma(this.resettingKarma);
                    eff.setEffectNumber(this.effectNumber);
                    Effectuator.addEpicEffect(eff);
                    if (this.templateId > 0) {
                        try {
                            final CreatureTemplate ct = CreatureTemplateFactory.getInstance().getTemplate(this.templateId);
                            final String effectDesc = "Some " + ct.getName() + " arrive.";
                            final Message mess2 = new Message(null, (byte)5, ":Event", effectDesc);
                            Server.getInstance().addMessage(mess2);
                            final SynchedEpicEffect eff2 = new SynchedEpicEffect(1);
                            eff2.setDeityNumber(this.deityNumber);
                            eff2.setCreatureTemplateId(this.templateId);
                            Effectuator.addEpicEffect(eff2);
                        }
                        catch (NoSuchCreatureTemplateException nst) {
                            WcEpicEvent.logger.log(Level.WARNING, nst.getMessage());
                        }
                    }
                }
            }
            else if (this.effectNumber > 0 && Servers.localServer.PVPSERVER) {
                if (this.effectNumber == 5) {
                    final Message mess = new Message(null, (byte)5, ":Event", this.eventString);
                    Server.getInstance().addMessage(mess);
                    final SynchedEpicEffect eff = new SynchedEpicEffect(1);
                    eff.setDeityNumber(this.deityNumber);
                    eff.setCreatureTemplateId(this.templateId);
                    eff.setResetKarma(this.resettingKarma);
                    Effectuator.addEpicEffect(eff);
                }
                else {
                    final Message mess = new Message(null, (byte)5, ":Event", this.eventString);
                    Server.getInstance().addMessage(mess);
                    final SynchedEpicEffect eff = new SynchedEpicEffect(2);
                    eff.setDeityNumber(this.deityNumber);
                    eff.setCreatureTemplateId(this.templateId);
                    eff.setEffectNumber(this.effectNumber);
                    eff.setBonusEffectNum(this.bonusEffectNum);
                    eff.setEventString(this.eventString);
                    eff.setResetKarma(this.resettingKarma);
                    Effectuator.addEpicEffect(eff);
                }
            }
        }
        catch (IOException ex) {
            WcEpicEvent.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeInputStreamIgnoreExceptions(dis);
        }
    }
    
    static {
        logger = Logger.getLogger(WcEpicEvent.class.getName());
    }
}
