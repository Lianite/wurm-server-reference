// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.Date;
import java.text.SimpleDateFormat;
import com.wurmonline.server.players.PlayerState;
import com.wurmonline.server.villages.VillageRole;
import com.wurmonline.server.villages.Citizen;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.NoSuchItemException;
import java.util.logging.Level;
import com.wurmonline.server.creatures.Wagoner;
import com.wurmonline.shared.constants.PlayerOnlineStatus;
import com.wurmonline.server.players.PlayerInfoFactory;
import java.util.Arrays;
import com.wurmonline.server.Servers;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.Items;
import com.wurmonline.server.villages.NoSuchVillageException;
import java.util.Properties;
import java.util.HashMap;
import com.wurmonline.server.creatures.Creature;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.server.villages.VillageStatus;

public final class VillageCitizenManageQuestion extends Question implements VillageStatus
{
    private static final Logger logger;
    private boolean selecting;
    private String allowedLetters;
    private final Map<Integer, Long> idMap;
    
    public VillageCitizenManageQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 8, aTarget);
        this.selecting = false;
        this.allowedLetters = "abcdefghijklmnopqrstuvwxyz";
        this.idMap = new HashMap<Integer, Long>();
    }
    
    public VillageCitizenManageQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget, final boolean aSelecting) {
        super(aResponder, aTitle, aQuestion, 8, aTarget);
        this.selecting = false;
        this.allowedLetters = "abcdefghijklmnopqrstuvwxyz";
        this.idMap = new HashMap<Integer, Long>();
        this.selecting = aSelecting;
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        QuestionParser.parseVillageCitizenManageQuestion(this);
    }
    
    @Override
    public void sendQuestion() {
        try {
            Village village;
            if (this.target == -10L) {
                village = this.getResponder().getCitizenVillage();
                if (village == null) {
                    throw new NoSuchVillageException("You are not a citizen of any village (on this server).");
                }
            }
            else {
                final Item deed = Items.getItem(this.target);
                final int villageId = deed.getData2();
                village = Villages.getVillage(villageId);
            }
            final StringBuilder buf = new StringBuilder(this.getBmlHeader());
            buf.append("text{text=\"Manage citizens. Assign them roles and titles.\"}");
            final Citizen[] citizens = village.getCitizens();
            final String ch = village.unlimitedCitizens ? ";selected='true'" : "";
            buf.append("text{type=\"bold\";text=\"Unlimited citizens:\"}");
            if (!Servers.isThisAPvpServer()) {
                buf.append("text{type=\"italic\";text=\"The maximum number of branded animals is " + village.getMaxCitizens() + "\"}");
            }
            buf.append("checkbox{id=\"unlimitC\"" + ch + ";text=\"Mark this if you want to be able to recruit more than " + village.getMaxCitizens() + " citizens.\"}text{text=\"Your upkeep costs are doubled as long as you have more than that amount of citizens.\"}");
            if (this.selecting) {
                if (citizens.length > 40) {
                    buf.append("label{text=\"Select the range of citizens to manage:\"}");
                    buf.append("dropdown{id=\"selectRange\";options=\"A-F,G-L,M-R,S-Z\"}");
                    buf.append(this.createAnswerButton2());
                    this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
                    return;
                }
                this.selecting = false;
            }
            buf.append("text{text=\"\"}");
            final VillageRole[] roles = village.getRoles();
            Arrays.sort(citizens);
            buf.append("table{rows=\"" + (citizens.length - 1) + "\";cols=\"7\";");
            for (int x = 0; x < citizens.length; ++x) {
                if (this.allowedLetters.indexOf(citizens[x].getName().substring(0, 1).toLowerCase()) >= 0) {
                    this.idMap.put(x, new Long(citizens[x].getId()));
                    final VillageRole role = citizens[x].getRole();
                    final int roleid = role.getId();
                    if (role.getStatus() != 4 && role.getStatus() != 5 && role.getStatus() != 6) {
                        int defaultrole = 0;
                        buf.append("label{text=\"" + citizens[x].getName() + "\"}label{text=\"role:\"}dropdown{id=\"" + x + "\";options=\"");
                        int added = 0;
                        for (int r = 0; r < roles.length; ++r) {
                            if (roles[r].getStatus() != 4 && roles[r].getStatus() != 5 && roles[r].getStatus() != 1 && roles[r].getStatus() != 6) {
                                if (added > 0 && r != roles.length) {
                                    buf.append(",");
                                }
                                String name = roles[r].getName();
                                if (name.length() == 0) {
                                    name = "[blank]";
                                }
                                buf.append(name.substring(0, Math.min(name.length(), 10)));
                                if (roleid == roles[r].getId()) {
                                    defaultrole = added;
                                }
                                ++added;
                            }
                        }
                        buf.append("\";default=\"" + defaultrole + "\"}");
                        final PlayerState cState = PlayerInfoFactory.getPlayerState(citizens[x].getId());
                        if (cState == null) {
                            buf.append("label{text=\"\"}");
                            buf.append("label{text=\"\"}");
                            buf.append("label{text=\"\"}");
                        }
                        else {
                            String sColour = "";
                            String sState = "";
                            long changedDate = 0L;
                            if (cState.getState() == PlayerOnlineStatus.ONLINE) {
                                sColour = "66,225,66";
                                sState = "Online";
                                changedDate = cState.getLastLogin();
                            }
                            else {
                                sColour = "255,66,66";
                                sState = "Offline";
                                changedDate = cState.getLastLogout();
                            }
                            buf.append("label{color=\"" + sColour + "\";text=\"" + sState + "\"}");
                            buf.append("label{text=\"" + convertTime(changedDate) + "\"}");
                            buf.append("label{text=\"" + cState.getServerName() + "\"}");
                        }
                        if (!village.isDemocracy() && citizens[x].getId() != this.getResponder().getWurmId() && citizens[x].getRole().getStatus() != 2) {
                            buf.append("checkbox{id=\"" + x + "revoke\";selected=\"false\";text=\" Revoke citizenship \"}");
                        }
                        else {
                            buf.append("label{text=\"\"}");
                        }
                    }
                    else if (citizens[x].getRole().getStatus() == 6) {
                        final Wagoner wagoner = Wagoner.getWagoner(citizens[x].getId());
                        buf.append("label{text=\"" + citizens[x].getName() + "\"}label{text=\"role:\"};label{text=\"" + citizens[x].getRole().getName() + "\"};label{text=\"" + wagoner.getStateName() + "\"};label{text=\"\"};label{text=\"\"};label{text=\"\"}");
                    }
                }
            }
            buf.append("}text{text=\"\"}");
            buf.append(this.createAnswerButton2());
            this.getResponder().getCommunicator().sendBml(500, 300, true, true, buf.toString(), 200, 200, 200, this.title);
        }
        catch (NoSuchItemException nsi) {
            VillageCitizenManageQuestion.logger.log(Level.WARNING, "Failed to locate village/homestead deed with id " + this.target, nsi);
            this.getResponder().getCommunicator().sendNormalServerMessage("Failed to locate the deed item for that request. Please contact administration.");
        }
        catch (NoSuchVillageException nsv) {
            VillageCitizenManageQuestion.logger.log(Level.WARNING, "Failed to locate the village/homestead for the deed with id " + this.target, nsv);
            this.getResponder().getCommunicator().sendNormalServerMessage("Failed to locate the village for that deed. Please contact administration.");
        }
    }
    
    boolean isSelecting() {
        return this.selecting;
    }
    
    public void setSelecting(final boolean aSelecting) {
        this.selecting = aSelecting;
    }
    
    void setAllowedLetters(final String aAllowedLetters) {
        this.allowedLetters = aAllowedLetters;
    }
    
    Map<Integer, Long> getIdMap() {
        return this.idMap;
    }
    
    private static String convertTime(final long time) {
        final String fd = new SimpleDateFormat("dd/MMM/yyyy HH:mm").format(new Date(time));
        return fd;
    }
    
    static {
        logger = Logger.getLogger(VillageCitizenManageQuestion.class.getName());
    }
}
