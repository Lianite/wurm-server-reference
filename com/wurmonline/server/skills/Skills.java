// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.skills;

import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.creatures.Creature;
import java.util.HashSet;
import java.io.IOException;
import java.util.Iterator;
import com.wurmonline.server.creatures.Communicator;
import javax.annotation.Nonnull;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Players;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.Server;
import com.wurmonline.server.Servers;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.shared.constants.CounterTypes;
import com.wurmonline.server.MiscConstants;

public abstract class Skills implements MiscConstants, CounterTypes, TimeConstants
{
    private static final ConcurrentHashMap<Long, Set<Skill>> creatureSkillsMap;
    Map<Integer, Skill> skills;
    long id;
    String templateName;
    private static Logger logger;
    public boolean paying;
    public boolean priest;
    public boolean hasSkillGain;
    private static final String moveWeek = "UPDATE SKILLS SET WEEK2=DAY7";
    private static final String moveDays = "UPDATE LOW_PRIORITY WURMPLAYERS.SKILLS SET DAY7=DAY6, DAY6=DAY5, DAY5=DAY4, DAY4=DAY3, DAY3=DAY2, DAY2=DAY1, DAY1=VALUE WHERE DAY7!=DAY6 OR DAY6!=DAY5 OR DAY5!=DAY4 OR DAY4!=DAY3 OR DAY3!=DAY2 OR DAY2!=DAY1 OR DAY1!=VALUE";
    private static final String moveDay6 = "UPDATE SKILLS SET DAY7=DAY6";
    private static final String moveDay5 = "UPDATE SKILLS SET DAY6=DAY5";
    private static final String moveDay4 = "UPDATE SKILLS SET DAY5=DAY4";
    private static final String moveDay3 = "UPDATE SKILLS SET DAY4=DAY3";
    private static final String moveDay2 = "UPDATE SKILLS SET DAY3=DAY2";
    private static final String moveDay1 = "UPDATE SKILLS SET DAY2=DAY1";
    private static final String moveDay0 = "UPDATE SKILLS SET DAY1=VALUE";
    public static final AtomicBoolean daySwitcherBeingRun;
    public static final float minChallengeValue = 21.0f;
    
    Skills() {
        this.id = -10L;
        this.templateName = null;
        this.paying = true;
        this.priest = false;
        this.hasSkillGain = true;
        this.skills = new TreeMap<Integer, Skill>();
    }
    
    public boolean isTemplate() {
        return this.templateName != null;
    }
    
    boolean isPersonal() {
        return this.id != -10L;
    }
    
    private static final void switchWeek() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("UPDATE SKILLS SET WEEK2=DAY7");
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Skills.logger.log(Level.WARNING, "moveWeek: UPDATE SKILLS SET WEEK2=DAY7 - " + ex.getMessage(), ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static final String getSkillSwitchString(final int day) {
        switch (day) {
            case 0: {
                return "UPDATE SKILLS SET DAY1=VALUE";
            }
            case 1: {
                return "UPDATE SKILLS SET DAY2=DAY1";
            }
            case 2: {
                return "UPDATE SKILLS SET DAY3=DAY2";
            }
            case 3: {
                return "UPDATE SKILLS SET DAY4=DAY3";
            }
            case 4: {
                return "UPDATE SKILLS SET DAY5=DAY4";
            }
            case 5: {
                return "UPDATE SKILLS SET DAY6=DAY5";
            }
            case 6: {
                return "UPDATE SKILLS SET DAY7=DAY6";
            }
            default: {
                Skills.logger.log(Level.WARNING, "This shouldn't happen: " + day);
                return "UPDATE SKILLS SET DAY7=DAY6";
            }
        }
    }
    
    private static final void switchDay(final int day) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final String psString = getSkillSwitchString(day);
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement(psString);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Skills.logger.log(Level.WARNING, "Day: " + day + " - " + ex.getMessage(), ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static final void switchDays() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("UPDATE LOW_PRIORITY WURMPLAYERS.SKILLS SET DAY7=DAY6, DAY6=DAY5, DAY5=DAY4, DAY4=DAY3, DAY3=DAY2, DAY2=DAY1, DAY1=VALUE WHERE DAY7!=DAY6 OR DAY6!=DAY5 OR DAY5!=DAY4 OR DAY4!=DAY3 OR DAY3!=DAY2 OR DAY2!=DAY1 OR DAY1!=VALUE");
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Skills.logger.log(Level.WARNING, "Update days - " + ex.getMessage(), ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static void switchSkills(final long now) {
        if (!Servers.localServer.LOGINSERVER && !Server.getInstance().isPS()) {
            if (Skills.daySwitcherBeingRun.get()) {
                return;
            }
            final boolean switchWeek = now - Servers.localServer.getSkillWeekSwitch() > 604800000L;
            final boolean switchDay = now - Servers.localServer.getSkillDaySwitch() > 86400000L;
            if (!switchDay && !switchWeek) {
                return;
            }
            Skills.daySwitcherBeingRun.set(true);
            final Thread statsPoller = new Thread("Skills Day/Week Updater") {
                @Override
                public void run() {
                    final long start = System.currentTimeMillis();
                    if (switchWeek) {
                        Skills.logger.log(Level.INFO, "Switching skill week");
                        switchWeek();
                        Servers.localServer.setSkillWeekSwitch(now);
                    }
                    if (switchDay) {
                        Skills.logger.log(Level.INFO, "Switching skill day");
                        switchDays();
                        Servers.localServer.setSkillDaySwitch(now);
                    }
                    Skills.logger.log(Level.INFO, "Skills Day/Week Updater took " + (System.currentTimeMillis() - start) + "ms");
                    Skills.daySwitcherBeingRun.set(false);
                }
            };
            statsPoller.start();
        }
        else {
            Servers.localServer.setSkillDaySwitch(now);
            Servers.localServer.setSkillWeekSwitch(now);
        }
    }
    
    public TempSkill learnTemp(final int skillNumber, final float startValue) {
        final TempSkill skill = new TempSkill(skillNumber, startValue, this);
        final int[] needed = skill.getDependencies();
        for (int x = 0; x < needed.length; ++x) {
            if (!this.skills.containsKey(needed[x])) {
                this.learnTemp(needed[x], 1.0f);
            }
        }
        if (this.id != -10L && WurmId.getType(this.id) == 0) {
            int parentSkillId = 0;
            if (needed.length > 0) {
                parentSkillId = needed[0];
            }
            try {
                if (parentSkillId != 0) {
                    final int parentType = SkillSystem.getTypeFor(parentSkillId);
                    if (parentType == 0) {
                        parentSkillId = Integer.MAX_VALUE;
                    }
                }
                else if (skill.getType() == 1) {
                    parentSkillId = 2147483646;
                }
                else {
                    parentSkillId = Integer.MAX_VALUE;
                }
                final Affinity[] affs = Affinities.getAffinities(this.id);
                if (affs.length > 0) {
                    for (int x2 = 0; x2 < affs.length; ++x2) {
                        if (affs[x2].skillNumber == skillNumber) {
                            skill.affinity = affs[x2].number;
                        }
                    }
                }
                Players.getInstance().getPlayer(this.id).getCommunicator().sendAddSkill(skillNumber, parentSkillId, skill.getName(), startValue, startValue, skill.affinity);
            }
            catch (NoSuchPlayerException ex) {}
        }
        skill.touch();
        this.skills.put(skillNumber, skill);
        return skill;
    }
    
    @Nonnull
    public Skill learn(final int skillNumber, final float startValue) {
        return this.learn(skillNumber, startValue, true);
    }
    
    @Nonnull
    public Skill learn(final int skillNumber, final float startValue, final boolean sendAdd) {
        final Skill skill = new DbSkill(skillNumber, startValue, this);
        final int[] dependencies;
        final int[] needed = dependencies = skill.getDependencies();
        for (final int aNeeded : dependencies) {
            if (!this.skills.containsKey(aNeeded)) {
                this.learn(aNeeded, 1.0f);
            }
        }
        if (this.id != -10L && WurmId.getType(this.id) == 0) {
            int parentSkillId = 0;
            if (needed.length > 0) {
                parentSkillId = needed[0];
            }
            try {
                if (parentSkillId != 0) {
                    final int parentType = SkillSystem.getTypeFor(parentSkillId);
                    if (parentType == 0) {
                        parentSkillId = Integer.MAX_VALUE;
                    }
                }
                else if (skill.getType() == 1) {
                    parentSkillId = 2147483646;
                }
                else {
                    parentSkillId = Integer.MAX_VALUE;
                }
                for (final Affinity aff : Affinities.getAffinities(this.id)) {
                    if (aff.skillNumber == skillNumber) {
                        skill.affinity = aff.number;
                    }
                }
                final Communicator comm = Players.getInstance().getPlayer(this.id).getCommunicator();
                if (sendAdd) {
                    comm.sendAddSkill(skillNumber, parentSkillId, skill.getName(), startValue, startValue, skill.affinity);
                }
                else {
                    comm.sendUpdateSkill(skillNumber, startValue, skill.affinity);
                }
            }
            catch (NoSuchPlayerException nsp) {
                Skills.logger.log(Level.WARNING, "skillNumber: " + skillNumber + ", startValue: " + startValue, nsp);
            }
        }
        skill.touch();
        this.skills.put(skillNumber, skill);
        try {
            skill.save();
            this.save();
        }
        catch (Exception ex) {
            Skills.logger.log(Level.WARNING, "Failed to save skill " + skill.getName() + "(" + skillNumber + ")", ex);
        }
        return skill;
    }
    
    @Nonnull
    public Skill getSkill(final String name) throws NoSuchSkillException {
        Skill toReturn = null;
        for (final Skill checked : this.skills.values()) {
            if (checked.getName().equals(name)) {
                toReturn = checked;
                break;
            }
        }
        if (toReturn == null) {
            throw new NoSuchSkillException("Unknown skill - " + name + ", total number of skills known is: " + this.skills.size());
        }
        return toReturn;
    }
    
    @Nonnull
    public Skill getSkill(final int number) throws NoSuchSkillException {
        final Skill toReturn = this.skills.get(number);
        if (toReturn == null) {
            throw new NoSuchSkillException("Unknown skill - " + SkillSystem.getNameFor(number) + ", total number of skills known is: " + this.skills.size());
        }
        return toReturn;
    }
    
    public final void switchSkillNumbers(final Skill skillOne, final Skill skillTwo) {
        final int numberOne = skillTwo.getNumber();
        try {
            skillTwo.setNumber(skillOne.getNumber());
            this.skills.put(skillTwo.number, skillTwo);
            skillTwo.setKnowledge(skillTwo.knowledge, false, false);
        }
        catch (IOException iox2) {
            Skills.logger.log(Level.INFO, iox2.getMessage());
        }
        try {
            skillOne.setNumber(numberOne);
            this.skills.put(skillOne.number, skillOne);
            skillOne.setKnowledge(skillOne.knowledge, false, false);
        }
        catch (IOException iox3) {
            Skills.logger.log(Level.INFO, iox3.getMessage());
        }
    }
    
    @Nonnull
    public Skill getSkillOrLearn(final int number) {
        final Skill toReturn = this.skills.get(number);
        if (toReturn == null) {
            return this.learn(number, 1.0f);
        }
        return toReturn;
    }
    
    public void checkDecay() {
        final Set<Skill> memorySkills = new HashSet<Skill>();
        final Set<Skill> otherSkills = new HashSet<Skill>();
        final Set<Map.Entry<Integer, Skill>> toRemove = new HashSet<Map.Entry<Integer, Skill>>();
        for (final Map.Entry<Integer, Skill> entry : this.skills.entrySet()) {
            final Skill toCheck = entry.getValue();
            try {
                if (toCheck.getType() == 1) {
                    memorySkills.add(toCheck);
                }
                else {
                    otherSkills.add(toCheck);
                }
            }
            catch (NullPointerException np) {
                toRemove.add(entry);
            }
        }
        for (final Skill mem : memorySkills) {
            mem.checkDecay();
        }
        for (final Skill other : otherSkills) {
            other.checkDecay();
        }
        for (final Map.Entry<Integer, Skill> entry : toRemove) {
            final Integer toremove = entry.getKey();
            this.skills.remove(toremove);
        }
    }
    
    public Map<Integer, Skill> getSkillTree() {
        return this.skills;
    }
    
    public Skill[] getSkills() {
        final Skill[] toReturn = new Skill[this.skills.size()];
        int i = 0;
        final Iterator<Skill> it = this.skills.values().iterator();
        while (it.hasNext()) {
            toReturn[i] = it.next();
            ++i;
        }
        return toReturn;
    }
    
    public Skill[] getSkillsNoTemp() {
        final Set<Skill> noTemps = new HashSet<Skill>();
        for (final Skill isTemp : this.skills.values()) {
            if (!isTemp.isTemporary()) {
                noTemps.add(isTemp);
            }
        }
        final Skill[] toReturn = noTemps.toArray(new Skill[noTemps.size()]);
        return toReturn;
    }
    
    public void clone(final Skill[] skillarr) {
        this.skills = new TreeMap<Integer, Skill>();
        for (int x = 0; x < skillarr.length; ++x) {
            if (!skillarr[x].isTemporary() && !(skillarr[x] instanceof TempSkill)) {
                final DbSkill newSkill = new DbSkill(skillarr[x].getNumber(), skillarr[x].knowledge, this);
                this.skills.put(skillarr[x].getNumber(), newSkill);
                try {
                    newSkill.touch();
                    newSkill.save();
                }
                catch (Exception iox) {
                    Skills.logger.log(Level.WARNING, "Failed to save skill " + newSkill.getName() + " for " + this.id, iox);
                }
            }
            else {
                final TempSkill newSkill2 = new TempSkill(skillarr[x].getNumber(), skillarr[x].knowledge, this);
                this.skills.put(skillarr[x].getNumber(), newSkill2);
                newSkill2.touch();
            }
        }
    }
    
    public long getId() {
        return this.id;
    }
    
    public static final void clearCreatureLoadMap() {
        Skills.creatureSkillsMap.clear();
    }
    
    public static final void loadAllCreatureSkills() throws Exception {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM SKILLS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final Skill skill = new DbSkill(rs.getLong("ID"), rs.getInt("NUMBER"), rs.getDouble("VALUE"), rs.getDouble("MINVALUE"), rs.getLong("LASTUSED"));
                final long owner = rs.getLong("OWNER");
                Set<Skill> skills = Skills.creatureSkillsMap.get(owner);
                if (skills == null) {
                    skills = new HashSet<Skill>();
                }
                skills.add(skill);
                Skills.creatureSkillsMap.put(owner, skills);
            }
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static final void fillCreatureTempSkills(final Creature creature) {
        final Skills cSkills = creature.getSkills();
        final Map<Integer, Skill> treeSkills = creature.getSkills().getSkillTree();
        final CreatureTemplate template = creature.getTemplate();
        try {
            final Skills tSkills = template.getSkills();
            for (final Skill ts : tSkills.getSkills()) {
                if (!treeSkills.containsKey(ts.getNumber())) {
                    cSkills.learnTemp(ts.getNumber(), (float)ts.knowledge);
                }
            }
        }
        catch (Exception e) {
            Skills.logger.log(Level.WARNING, "Unknown error while checking temp skill for creature: " + creature.getWurmId() + ".", e);
        }
    }
    
    public final void initializeSkills() {
        final Set<Skill> skillSet = Skills.creatureSkillsMap.get(this.id);
        if (skillSet == null) {
            return;
        }
        for (final Skill skill : skillSet) {
            final Skill dbSkill = new DbSkill(skill.id, this, skill.getNumber(), skill.knowledge, skill.minimum, skill.lastUsed);
            this.skills.put(dbSkill.getNumber(), dbSkill);
        }
    }
    
    public String getTemplateName() {
        return this.templateName;
    }
    
    public void saveDirty() throws IOException {
        if (this.id != -10L && WurmId.getType(this.id) == 0) {
            for (final Skill skill : this.skills.values()) {
                skill.saveValue(true);
            }
        }
    }
    
    public void save() throws IOException {
        if (this.id != -10L && WurmId.getType(this.id) == 0) {
            for (final Skill skill : this.skills.values()) {
                if (skill.isDirty()) {
                    skill.saveValue(true);
                }
            }
        }
    }
    
    public final void addTempSkills() {
        final float initialTempValue = (WurmId.getType(this.id) == 0) ? Servers.localServer.getSkilloverallval() : 1.0f;
        for (int i = 0; i < SkillList.skillArray.length; ++i) {
            final Integer key = SkillList.skillArray[i];
            if (!this.skills.containsKey(key)) {
                if (key == 1023 && WurmId.getType(this.id) == 0) {
                    this.learnTemp(key, Servers.localServer.getSkillfightval());
                }
                else if (key == 100 && WurmId.getType(this.id) == 0) {
                    this.learnTemp(key, Servers.localServer.getSkillmindval());
                }
                else if (key == 104 && WurmId.getType(this.id) == 0) {
                    this.learnTemp(key, Servers.localServer.getSkillbcval());
                }
                else {
                    this.learnTemp(key, initialTempValue);
                }
            }
        }
    }
    
    public abstract void load() throws Exception;
    
    public abstract void delete() throws Exception;
    
    static {
        creatureSkillsMap = new ConcurrentHashMap<Long, Set<Skill>>();
        Skills.logger = Logger.getLogger(Skills.class.getName());
        daySwitcherBeingRun = new AtomicBoolean();
    }
}
