// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.skills;

import com.wurmonline.server.players.Titles;
import com.wurmonline.server.players.ItemBonus;
import com.wurmonline.server.items.RuneUtilities;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.Players;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.Servers;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Server;
import java.util.Iterator;
import java.util.HashSet;
import java.io.IOException;
import com.wurmonline.shared.exceptions.WurmServerException;
import java.util.logging.Level;
import com.wurmonline.server.WurmId;
import java.util.Random;
import com.wurmonline.server.modifiers.DoubleValueModifier;
import java.util.Set;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.shared.constants.CounterTypes;
import com.wurmonline.server.MiscConstants;

public abstract class Skill implements MiscConstants, CounterTypes, TimeConstants, Comparable<Skill>
{
    public long lastUsed;
    protected double knowledge;
    private static final double regainMultiplicator = 3.0;
    public double minimum;
    boolean joat;
    int number;
    private static final double maxBonus = 70.0;
    public static final Logger affinityDebug;
    private static int totalAffinityChecks;
    private static int totalAffinitiesGiven;
    Skills parent;
    private static Logger logger;
    public int affinity;
    private static final float affinityMultiplier = 0.1f;
    public long id;
    private Set<DoubleValueModifier> modifiers;
    private byte saveCounter;
    private static Random random;
    private static final byte[][] chances;
    private static final double skillMod;
    private static final double maxSkillGain = 1.0;
    private boolean basicPersonal;
    private boolean noCurve;
    protected static final boolean isChallenge;
    
    Skill(final int aNumber, final double startValue, final Skills aParent) {
        this.knowledge = 1.0;
        this.joat = false;
        this.affinity = 0;
        this.id = -10L;
        this.modifiers = null;
        this.saveCounter = 0;
        this.basicPersonal = false;
        this.noCurve = false;
        this.number = aNumber;
        this.knowledge = Math.max(1.0, startValue);
        this.minimum = startValue;
        this.parent = aParent;
        if (aParent.isPersonal()) {
            if (WurmId.getType(aParent.getId()) == 0) {
                this.id = (this.isTemporary() ? WurmId.getNextTemporarySkillId() : WurmId.getNextPlayerSkillId());
                if (SkillSystem.getTypeFor(aNumber) == 0 || SkillSystem.getTypeFor(this.number) == 1) {
                    this.knowledge = Math.max(1.0, startValue);
                    this.minimum = this.knowledge;
                    this.basicPersonal = true;
                    this.noCurve = true;
                }
            }
            else {
                this.id = (this.isTemporary() ? WurmId.getNextTemporarySkillId() : WurmId.getNextCreatureSkillId());
            }
            if (this.number == 10076) {
                this.noCurve = true;
            }
        }
    }
    
    Skill(final long _id, final int _number, final double _knowledge, final double _minimum, final long _lastused) {
        this.knowledge = 1.0;
        this.joat = false;
        this.affinity = 0;
        this.id = -10L;
        this.modifiers = null;
        this.saveCounter = 0;
        this.basicPersonal = false;
        this.noCurve = false;
        this.id = _id;
        this.number = _number;
        this.knowledge = _knowledge;
        this.minimum = _minimum;
        this.lastUsed = _lastused;
    }
    
    public boolean isDirty() {
        return this.saveCounter > 0;
    }
    
    Skill(final long _id, final Skills _parent, final int _number, final double _knowledge, final double _minimum, final long _lastused) {
        this.knowledge = 1.0;
        this.joat = false;
        this.affinity = 0;
        this.id = -10L;
        this.modifiers = null;
        this.saveCounter = 0;
        this.basicPersonal = false;
        this.noCurve = false;
        this.id = _id;
        this.parent = _parent;
        this.number = _number;
        this.knowledge = _knowledge;
        this.minimum = _minimum;
        this.lastUsed = _lastused;
        if (WurmId.getType(this.parent.getId()) == 0) {
            if (SkillSystem.getTypeFor(this.number) == 0 || SkillSystem.getTypeFor(this.number) == 1) {
                this.basicPersonal = true;
                this.noCurve = true;
            }
            if (this.number == 10076) {
                this.noCurve = true;
            }
        }
    }
    
    @Override
    public int compareTo(final Skill otherSkill) {
        return this.getName().compareTo(otherSkill.getName());
    }
    
    private static final byte[][] calculateChances() {
        Skill.logger.log(Level.INFO, "Calculating skill chances...");
        final long start = System.nanoTime();
        byte[][] toReturn = null;
        try {
            toReturn = DbSkill.loadSkillChances();
            if (toReturn == null) {
                throw new WurmServerException("Load failed. Creating chances.");
            }
            Skill.logger.log(Level.INFO, "Loaded skill chances succeeded.");
        }
        catch (Exception ex) {
            toReturn = new byte[101][101];
            for (int x = 0; x < 101; ++x) {
                for (int y = 0; y < 101; ++y) {
                    if (x == 0) {
                        toReturn[x][y] = 0;
                    }
                    else if (y == 0) {
                        toReturn[x][y] = 99;
                    }
                    else {
                        float succeed = 0.0f;
                        for (int t = 0; t < 1000; ++t) {
                            ++succeed;
                        }
                        succeed /= 10.0f;
                        toReturn[x][y] = (byte)succeed;
                    }
                }
            }
            final Thread t2 = new Thread() {
                @Override
                public void run() {
                    Skill.logger.log(Level.INFO, "Starting to slowly build up statistics.");
                    final byte[][] toSave = new byte[101][101];
                    for (int x = 0; x < 101; ++x) {
                        for (int y = 0; y < 101; ++y) {
                            if (x == 0) {
                                toSave[x][y] = 0;
                            }
                            else if (y == 0) {
                                toSave[x][y] = 99;
                            }
                            else {
                                float succeed = 0.0f;
                                for (int t2 = 0; t2 < 30000; ++t2) {
                                    if (Skill.rollGaussian(x, y, 0L, "test") > 0.0f) {
                                        ++succeed;
                                    }
                                }
                                succeed /= 300.0f;
                                toSave[x][y] = (byte)succeed;
                            }
                        }
                    }
                    try {
                        Skill.logger.log(Level.INFO, "Saving skill chances.");
                        DbSkill.saveSkillChances(toSave);
                    }
                    catch (Exception ex2) {
                        Skill.logger.log(Level.WARNING, "Saving failed.", ex2);
                    }
                }
            };
            t2.setPriority(3);
            t2.start();
        }
        finally {
            final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
            Skill.logger.info("Done. Loading/Calculating skill chances from the database took " + lElapsedTime + " millis.");
        }
        return toReturn;
    }
    
    Skill(final long aId, final Skills aParent) throws IOException {
        this.knowledge = 1.0;
        this.joat = false;
        this.affinity = 0;
        this.id = -10L;
        this.modifiers = null;
        this.saveCounter = 0;
        this.basicPersonal = false;
        this.noCurve = false;
        this.id = aId;
        this.parent = aParent;
        this.load();
    }
    
    public void addModifier(final DoubleValueModifier modifier) {
        if (this.modifiers == null) {
            this.modifiers = new HashSet<DoubleValueModifier>();
        }
        this.modifiers.add(modifier);
    }
    
    public void removeModifier(final DoubleValueModifier modifier) {
        if (this.modifiers != null) {
            this.modifiers.remove(modifier);
        }
    }
    
    private boolean ignoresEnemy() {
        return SkillSystem.ignoresEnemies(this.number);
    }
    
    public double getModifierValues() {
        double toReturn = 0.0;
        if (this.modifiers != null) {
            final Iterator<DoubleValueModifier> it = this.modifiers.iterator();
            while (it.hasNext()) {
                toReturn += it.next().getModifier();
            }
        }
        return toReturn;
    }
    
    void setParent(final Skills skills) {
        this.parent = skills;
    }
    
    public String getName() {
        return SkillSystem.getNameFor(this.number);
    }
    
    public int getNumber() {
        return this.number;
    }
    
    public long getId() {
        return this.id;
    }
    
    public double getKnowledge() {
        return this.knowledge;
    }
    
    public double getKnowledge(double bonus) {
        if (bonus > 70.0) {
            bonus = 70.0;
        }
        double bonusKnowledge = this.knowledge;
        if (this.number == 102 || this.number == 105) {
            final long parentId = this.parent.getId();
            if (parentId != -10L) {
                try {
                    final Creature holder = Server.getInstance().getCreature(parentId);
                    final float hellStrength = holder.getBonusForSpellEffect((byte)40);
                    final float forestGiantStrength = holder.getBonusForSpellEffect((byte)25);
                    if (hellStrength > 0.0f) {
                        final double pow = 0.8;
                        final double target = Math.pow(this.knowledge / 100.0, 0.8) * 100.0;
                        final double diff = target - this.knowledge;
                        bonusKnowledge += diff * hellStrength / 100.0;
                    }
                    else if (forestGiantStrength > 0.0f && this.number == 102) {
                        final double pow = 0.6;
                        final double target = Math.pow(this.knowledge / 100.0, 0.6) * 100.0;
                        final double diff = target - this.knowledge;
                        bonusKnowledge += diff * forestGiantStrength / 100.0;
                    }
                    final float ws = holder.getBonusForSpellEffect((byte)41);
                    if (ws > 0.0f) {
                        bonusKnowledge *= 0.800000011920929;
                    }
                }
                catch (NoSuchPlayerException ex) {}
                catch (NoSuchCreatureException ex2) {}
            }
        }
        if (bonus != 0.0) {
            final double linearMax = (100.0 + bonusKnowledge) / 2.0;
            final double diffToMaxChange = Math.min(bonusKnowledge, linearMax - bonusKnowledge);
            final double newBon = diffToMaxChange * bonus / 100.0;
            bonusKnowledge += newBon;
        }
        bonusKnowledge = Math.max(1.0, bonusKnowledge * (1.0 + this.getModifierValues()));
        if (!this.parent.paying) {
            if (!this.basicPersonal || Servers.localServer.PVPSERVER) {
                return Math.min(bonusKnowledge, 20.0);
            }
            return Math.min(bonusKnowledge, 30.0);
        }
        else {
            if (this.noCurve) {
                return bonusKnowledge;
            }
            return Server.getModifiedPercentageEffect(bonusKnowledge);
        }
    }
    
    public double getKnowledge(final Item item, double bonus) {
        if (item == null || item.isBodyPart()) {
            return this.getKnowledge(bonus);
        }
        if (this.number == 1023) {
            try {
                final int primweaponskill = item.getPrimarySkill();
                Skill pw = null;
                try {
                    pw = this.parent.getSkill(primweaponskill);
                    bonus += pw.getKnowledge(item, 0.0);
                }
                catch (NoSuchSkillException nss) {
                    pw = this.parent.learn(primweaponskill, 1.0f);
                    bonus += pw.getKnowledge(item, 0.0);
                }
            }
            catch (NoSuchSkillException ex) {}
        }
        double bonusKnowledge = 0.0;
        final double ql = item.getCurrentQualityLevel();
        if (bonus > 70.0) {
            bonus = 70.0;
        }
        if (ql <= this.knowledge) {
            bonusKnowledge = (this.knowledge + ql) / 2.0;
        }
        else {
            final double diff = ql - this.knowledge;
            bonusKnowledge = this.knowledge + this.knowledge * diff / 100.0;
        }
        if (this.number == 102) {
            final long parentId = this.parent.getId();
            if (parentId != -10L) {
                try {
                    final Creature holder = Server.getInstance().getCreature(parentId);
                    final float hs = holder.getBonusForSpellEffect((byte)40);
                    if (hs > 0.0f) {
                        if (this.knowledge < 40.0) {
                            final double diff2 = 40.0 - this.knowledge;
                            bonusKnowledge += diff2 * hs / 100.0;
                        }
                    }
                    else {
                        final float x = holder.getBonusForSpellEffect((byte)25);
                        if (x > 0.0f && this.knowledge < 40.0) {
                            final double diff3 = 40.0 - this.knowledge;
                            bonusKnowledge += diff3 * x / 100.0;
                        }
                    }
                    final float ws = holder.getBonusForSpellEffect((byte)41);
                    if (ws > 0.0f) {
                        bonusKnowledge *= 0.800000011920929;
                    }
                }
                catch (NoSuchPlayerException nsp) {
                    Skill.logger.log(Level.WARNING, nsp.getMessage(), nsp);
                }
                catch (NoSuchCreatureException ex2) {}
            }
        }
        if (bonus != 0.0) {
            final double linearMax = (100.0 + bonusKnowledge) / 2.0;
            final double diffToMaxChange = Math.min(bonusKnowledge, linearMax - bonusKnowledge);
            final double newBon = diffToMaxChange * bonus / 100.0;
            bonusKnowledge += newBon;
        }
        bonusKnowledge = Math.max(1.0, bonusKnowledge * (1.0 + this.getModifierValues()));
        if (!this.parent.paying) {
            if (!this.basicPersonal || Servers.localServer.PVPSERVER) {
                return Math.min(bonusKnowledge, 20.0);
            }
            return Math.min(bonusKnowledge, 30.0);
        }
        else {
            if (this.basicPersonal) {
                return bonusKnowledge;
            }
            return Server.getModifiedPercentageEffect(bonusKnowledge);
        }
    }
    
    public final double getRealKnowledge() {
        if (this.parent.paying) {
            return this.getKnowledge();
        }
        if (!this.basicPersonal || Servers.localServer.PVPSERVER) {
            return Math.min(this.getKnowledge(), 20.0);
        }
        return Math.min(this.getKnowledge(), 30.0);
    }
    
    public void setKnowledge(final double aKnowledge, final boolean load) {
        this.setKnowledge(aKnowledge, load, false);
    }
    
    public void setKnowledge(final double aKnowledge, final boolean load, final boolean setMinimum) {
        if (aKnowledge < 100.0) {
            final double oldknowledge = this.knowledge;
            this.checkTitleChange(oldknowledge, this.knowledge = Math.max(Math.min(aKnowledge, 100.0), 1.0));
            if (!load) {
                if (setMinimum) {
                    this.minimum = this.knowledge;
                }
                try {
                    this.save();
                }
                catch (IOException iox) {
                    Skill.logger.log(Level.INFO, "Failed to save skill " + this.id, iox);
                }
                final long parentId = this.parent.getId();
                if (parentId != -10L && WurmId.getType(parentId) == 0) {
                    try {
                        final Player holder = Players.getInstance().getPlayer(parentId);
                        double bonusKnowledge = this.knowledge;
                        if (this.number == 102) {
                            final float hs = holder.getBonusForSpellEffect((byte)40);
                            if (hs > 0.0f) {
                                if (this.knowledge < 40.0) {
                                    final double diff = 40.0 - this.knowledge;
                                    bonusKnowledge = this.knowledge + diff * hs / 100.0;
                                }
                            }
                            else {
                                final float x = holder.getBonusForSpellEffect((byte)25);
                                if (x > 0.0f && this.knowledge < 40.0) {
                                    final double diff2 = 40.0 - this.knowledge;
                                    bonusKnowledge = this.knowledge + diff2 * x / 100.0;
                                }
                            }
                            final float ws = holder.getBonusForSpellEffect((byte)41);
                            if (ws > 0.0f) {
                                bonusKnowledge *= 0.800000011920929;
                            }
                        }
                        if (!this.parent.paying && !this.basicPersonal) {
                            bonusKnowledge = Math.min(20.0, bonusKnowledge);
                        }
                        else if (!this.parent.paying && bonusKnowledge > 20.0) {
                            bonusKnowledge = Math.min(this.getKnowledge(0.0), bonusKnowledge);
                        }
                        holder.getCommunicator().sendUpdateSkill(this.number, (float)bonusKnowledge, this.isTemporary() ? 0 : this.affinity);
                    }
                    catch (NoSuchPlayerException nsp) {
                        Skill.logger.log(Level.WARNING, nsp.getMessage(), nsp);
                    }
                }
            }
        }
    }
    
    public double getMinimumValue() {
        return this.minimum;
    }
    
    @Nonnull
    public int[] getDependencies() {
        return SkillSystem.getDependenciesFor(this.number);
    }
    
    public int[] getUniqueDependencies() {
        final int[] fDeps = this.getDependencies();
        final Set<Integer> lst = new HashSet<Integer>();
        for (int i = 0; i < fDeps.length; ++i) {
            final Integer val = fDeps[i];
            if (!lst.contains(val)) {
                lst.add(val);
            }
        }
        final int[] deps = new int[lst.size()];
        int ind = 0;
        for (final Integer j : lst) {
            deps[ind] = j;
            ++ind;
        }
        return deps;
    }
    
    public double getDifficulty(final boolean checkPriest) {
        return SkillSystem.getDifficultyFor(this.number, checkPriest);
    }
    
    public short getType() {
        return SkillSystem.getTypeFor(this.number);
    }
    
    public double skillCheck(final double check, final double bonus, final boolean test, final float times, final boolean useNewSystem, final double skillDivider) {
        return this.skillCheck(check, bonus, test, times, useNewSystem, skillDivider, null, null);
    }
    
    public double skillCheck(final double check, final double bonus, final boolean test, final float times) {
        return this.skillCheck(check, bonus, test, 10.0f, true, 2.0);
    }
    
    public double skillCheck(final double check, final double bonus, final boolean test, final float times, @Nullable final Creature skillowner, @Nullable final Creature opponent) {
        return this.skillCheck(check, bonus, test, 10.0f, true, 2.0, skillowner, opponent);
    }
    
    public double skillCheck(final double check, final double bonus, final boolean test, final float times, final boolean useNewSystem, final double skillDivider, @Nullable final Creature skillowner, @Nullable final Creature opponent) {
        if (skillowner == null || opponent == null || this.number == 10055 || this.number == 10053 || this.number == 10054) {}
        this.touch();
        final double power = this.checkAdvance(check, null, bonus, test, times, useNewSystem, skillDivider);
        if (WurmId.getType(this.parent.getId()) == 0) {
            try {
                this.save();
            }
            catch (IOException ex) {}
        }
        return power;
    }
    
    public double skillCheck(final double check, final Item item, final double bonus, final boolean test, final float times, @Nullable final Creature skillowner, @Nullable final Creature opponent) {
        return this.skillCheck(check, item, bonus, test, 10.0f, true, 2.0, skillowner, opponent);
    }
    
    public double skillCheck(final double check, final Item item, final double bonus, final boolean test, final float times, final boolean useNewSystem, final double skillDivider, @Nullable final Creature skillowner, @Nullable final Creature opponent) {
        if (skillowner == null || opponent != null) {}
        this.touch();
        final double power = this.checkAdvance(check, item, bonus, test, times, useNewSystem, skillDivider);
        if (WurmId.getType(this.parent.getId()) == 0) {
            try {
                this.save();
            }
            catch (IOException ex) {}
        }
        return power;
    }
    
    public double skillCheck(final double check, final Item item, final double bonus, final boolean test, final float times, final boolean useNewSystem, final double skillDivider) {
        return this.skillCheck(check, item, bonus, test, times, useNewSystem, skillDivider, null, null);
    }
    
    public double skillCheck(final double check, final Item item, final double bonus, final boolean test, final float times) {
        return this.skillCheck(check, item, bonus, test, 10.0f, true, 2.0, null, null);
    }
    
    public long getDecayTime() {
        return SkillSystem.getDecayTimeFor(this.number);
    }
    
    public void touch() {
        if (SkillSystem.getTickTimeFor(this.getNumber()) <= 0L) {
            this.lastUsed = System.currentTimeMillis();
        }
    }
    
    long getLastUsed() {
        return this.lastUsed;
    }
    
    boolean mayUpdateTimedSkill() {
        return System.currentTimeMillis() - this.lastUsed < SkillSystem.getTickTimeFor(this.getNumber());
    }
    
    void checkDecay() {
    }
    
    private void decay(final boolean saved) {
        float decrease = 0.0f;
        if (this.getType() == 1) {
            this.alterSkill(-(100.0 - this.knowledge) / (this.getDifficulty(false) * this.knowledge), true, 1.0f);
        }
        else if (this.getType() == 0) {
            decrease = -0.1f;
            if (this.affinity > 0) {
                decrease = -0.1f + 0.05f * this.affinity;
            }
            if (saved) {
                this.alterSkill(decrease / 2.0f, true, 1.0f);
            }
            else {
                this.alterSkill(decrease, true, 1.0f);
            }
        }
        else {
            decrease = -0.25f;
            if (this.affinity > 0) {
                decrease = -0.25f + 0.025f * this.affinity;
            }
            if (saved) {
                this.alterSkill(decrease / 2.0f, true, 1.0f);
            }
            else {
                this.alterSkill(decrease, true, 1.0f);
            }
        }
    }
    
    public double getParentBonus() {
        double bonus = 0.0;
        final int[] dep = this.getDependencies();
        for (int x = 0; x < dep.length; ++x) {
            final short sType = SkillSystem.getTypeFor(dep[x]);
            if (sType == 2) {
                try {
                    final Skill enhancer = this.parent.getSkill(dep[x]);
                    final double ebonus = enhancer.getKnowledge(0.0);
                    bonus += ebonus;
                }
                catch (NoSuchSkillException ex) {
                    Skill.logger.log(Level.WARNING, "Skill.checkAdvance(): Skillsystem bad. Skill '" + this.getName() + "' has no enhance parent with number " + dep[x] + ". Learning!", ex);
                    this.parent.learn(dep[x], 1.0f);
                }
            }
        }
        return bonus;
    }
    
    public double getChance(double check, @Nullable final Item item, double bonus) {
        bonus += this.getParentBonus();
        double skill = this.knowledge;
        if (bonus != 0.0 || item != null) {
            if (item == null) {
                skill = this.getKnowledge(bonus);
            }
            else {
                skill = this.getKnowledge(item, bonus);
            }
        }
        if (skill < 1.0) {
            skill = 1.0;
        }
        if (check < 1.0) {
            check = 1.0;
        }
        if (item != null && item.getSpellEffects() != null) {
            final float skillBonus = (float)((100.0 - skill) * (item.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_SKILLCHECKBONUS) - 1.0f));
            skill += skillBonus;
        }
        return getGaussianChance(skill, check);
    }
    
    public static final double getGaussianChance(final double skill, final double difficulty) {
        if (skill > 99.0 || difficulty > 99.0) {
            return Math.max(0.0, Math.min(100.0, ((skill * skill * skill - difficulty * difficulty * difficulty) / 50000.0 + (skill - difficulty)) / 2.0 + 50.0 + 0.5 * (skill - difficulty)));
        }
        return Skill.chances[(int)skill][(int)difficulty];
    }
    
    public static final float rollGaussian(final float skill, final float difficulty, final long parentId, final String name) {
        final float slide = (skill * skill * skill - difficulty * difficulty * difficulty) / 50000.0f + (skill - difficulty);
        final float w = 30.0f - Math.abs(skill - difficulty) / 4.0f;
        int attempts = 0;
        float result = 0.0f;
        do {
            result = (float)Skill.random.nextGaussian() * (w + Math.abs(slide) / 6.0f) + slide;
            final float rejectCutoff = (float)Skill.random.nextGaussian() * (w - Math.abs(slide) / 6.0f) + slide;
            if (slide > 0.0f) {
                if (result > rejectCutoff + Math.max(100.0f - slide, 0.0f)) {
                    result = -1000.0f;
                }
            }
            else if (result < rejectCutoff - Math.max(100.0f + slide, 0.0f)) {
                result = -1000.0f;
            }
            if (++attempts == 100) {
                if (result > 100.0f) {
                    return 90.0f + Server.rand.nextFloat() * 5.0f;
                }
                if (result < -100.0f) {
                    return -90.0f - Server.rand.nextFloat() * 5.0f;
                }
                continue;
            }
        } while (result < -100.0f || result > 100.0f);
        return result;
    }
    
    private double checkAdvance(double check, @Nullable final Item item, double bonus, boolean dryRun, final float times, final boolean useNewSystem, final double skillDivider) {
        if (!dryRun) {
            dryRun = this.mayUpdateTimedSkill();
        }
        check = Math.max(1.0, check);
        final short skillType = SkillSystem.getTypeFor(this.number);
        final int[] dep = this.getUniqueDependencies();
        for (int x = 0; x < dep.length; ++x) {
            final short sType = SkillSystem.getTypeFor(dep[x]);
            if (sType == 2) {
                try {
                    final Skill enhancer = this.parent.getSkill(dep[x]);
                    final double ebonus = Math.max(0.0, enhancer.skillCheck(check, 0.0, dryRun, times, useNewSystem, skillDivider) / 10.0);
                    bonus += ebonus;
                }
                catch (NoSuchSkillException ex) {
                    Creature cret = null;
                    try {
                        cret = Server.getInstance().getCreature(this.parent.getId());
                    }
                    catch (NoSuchCreatureException ex2) {}
                    catch (NoSuchPlayerException ex3) {}
                    String name = "Unknown creature";
                    if (cret != null) {
                        name = cret.getName();
                    }
                    Skill.logger.log(Level.WARNING, name + " - Skill.checkAdvance(): Skillsystem bad. Skill '" + this.getName() + "' has no enhance parent with number " + dep[x], ex);
                    this.parent.learn(dep[x], 1.0f);
                }
            }
            else {
                try {
                    final Skill par = this.parent.getSkill(dep[x]);
                    if (par.getNumber() != 1023) {
                        par.skillCheck(check, 0.0, dryRun, times, useNewSystem, skillDivider);
                    }
                }
                catch (NoSuchSkillException ex) {
                    Creature cret = null;
                    try {
                        cret = Server.getInstance().getCreature(this.parent.getId());
                    }
                    catch (NoSuchCreatureException ex4) {}
                    catch (NoSuchPlayerException ex5) {}
                    String name = "Unknown creature";
                    if (cret != null) {
                        name = cret.getName();
                    }
                    Skill.logger.log(Level.WARNING, name + ": Skill.checkAdvance(): Skillsystem bad. Skill '" + this.getName() + "' has no limiting parent with number " + dep[x], ex);
                    this.parent.learn(dep[x], 1.0f);
                }
            }
        }
        bonus = Math.min(70.0, bonus);
        double skill = this.knowledge;
        double learnMod = 1.0;
        if (item == null) {
            skill = this.getKnowledge(bonus);
        }
        else {
            skill = this.getKnowledge(item, bonus);
            if (item.getSpellSkillBonus() > 0.0f) {
                learnMod += item.getSpellSkillBonus() / 100.0f;
            }
        }
        if (item != null && item.getSpellEffects() != null) {
            final float skillBonus = (float)((100.0 - skill) * (item.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_SKILLCHECKBONUS) - 1.0f));
            skill += skillBonus;
        }
        final double power = rollGaussian((float)skill, (float)check, this.parent.getId(), this.getName());
        if (!dryRun) {
            if (useNewSystem) {
                this.doSkillGainNew(check, power, learnMod, times, skillDivider);
            }
            else {
                this.doSkillGainOld(power, learnMod, times);
            }
        }
        if (power > 0.0) {
            final Player p = Players.getInstance().getPlayerOrNull(this.parent.getId());
            if (p != null) {
                ++Skill.totalAffinityChecks;
                if (p.shouldGiveAffinity(this.affinity, skillType == 1 || skillType == 0)) {
                    if (this.affinity == 0) {
                        p.getCommunicator().sendNormalServerMessage("You realize that you have developed an affinity for " + SkillSystem.getNameFor(this.number).toLowerCase() + ".", (byte)2);
                    }
                    else {
                        p.getCommunicator().sendNormalServerMessage("You realize that your affinity for " + SkillSystem.getNameFor(this.number).toLowerCase() + " has grown stronger.", (byte)2);
                    }
                    Affinities.setAffinity(p.getWurmId(), this.number, this.affinity + 1, false);
                    ++Skill.totalAffinitiesGiven;
                    Skill.affinityDebug.log(Level.INFO, p.getName() + " gained affinity for skill " + SkillSystem.getNameFor(this.number) + " from skill usage. New affinity: " + this.affinity + ". Total checks this restart: " + Skill.totalAffinityChecks + " Total affinities given this restart: " + Skill.totalAffinitiesGiven);
                }
            }
        }
        return power;
    }
    
    private final void doSkillGainNew(final double check, final double power, final double learnMod, final float times, final double skillDivider) {
        double bonus = 1.0;
        final double diff = Math.abs(check - this.knowledge);
        final short sType = SkillSystem.getTypeFor(this.number);
        boolean awardBonus = true;
        if (sType == 1 || sType == 0) {
            awardBonus = false;
        }
        if (diff <= 15.0 && awardBonus) {
            bonus = 1.0 + 0.10000000149011612 * (diff / 15.0);
        }
        if (power < 0.0) {
            if (this.knowledge < 20.0) {
                this.alterSkill((100.0 - this.knowledge) / (this.getDifficulty(this.parent.priest) * this.knowledge * this.knowledge) * learnMod * bonus, false, times, true, skillDivider);
            }
        }
        else {
            this.alterSkill((100.0 - this.knowledge) / (this.getDifficulty(this.parent.priest) * this.knowledge * this.knowledge) * learnMod * bonus, false, times, true, skillDivider);
        }
    }
    
    private final void doSkillGainOld(final double power, final double learnMod, final float times) {
        if (power >= 0.0) {
            if (this.knowledge < 20.0) {
                this.alterSkill((100.0 - this.knowledge) / (this.getDifficulty(this.parent.priest) * this.knowledge * this.knowledge) * learnMod, false, times);
            }
            else if (power > 0.0 && power < 40.0) {
                this.alterSkill((100.0 - this.knowledge) / (this.getDifficulty(this.parent.priest) * this.knowledge * this.knowledge) * learnMod, false, times);
            }
            else if (this.number == 10055 || this.number == 10053 || this.number == 10054) {
                Creature cret = null;
                try {
                    cret = Server.getInstance().getCreature(this.parent.getId());
                    if (cret.loggerCreature1 > 0L) {
                        Skill.logger.log(Level.INFO, cret.getName() + " POWER=" + power);
                    }
                }
                catch (NoSuchCreatureException ex) {}
                catch (NoSuchPlayerException ex2) {}
            }
        }
    }
    
    protected void alterSkill(final double advanceMultiplicator, final boolean decay, final float times) {
        this.alterSkill(advanceMultiplicator, decay, times, false, 1.0);
    }
    
    protected void alterSkill(double advanceMultiplicator, final boolean decay, float times, final boolean useNewSystem, final double skillDivider) {
        if (this.parent.hasSkillGain) {
            times = Math.min((SkillSystem.getTickTimeFor(this.getNumber()) > 0L || this.getNumber() == 10033) ? 100.0f : 30.0f, times);
            advanceMultiplicator *= times * Servers.localServer.getSkillGainRate();
            this.lastUsed = System.currentTimeMillis();
            boolean isplayer = false;
            final long pid = this.parent.getId();
            if (WurmId.getType(pid) == 0) {
                isplayer = true;
            }
            final double oldknowledge = this.knowledge;
            if (decay) {
                if (isplayer) {
                    if (this.knowledge <= 70.0) {
                        return;
                    }
                    double villageMod = 1.0;
                    try {
                        final Player player = Players.getInstance().getPlayer(pid);
                        villageMod = player.getVillageSkillModifier();
                    }
                    catch (NoSuchPlayerException nsp) {
                        Skill.logger.log(Level.WARNING, "Player with id " + this.id + " is decaying skills while not online?", nsp);
                    }
                    this.knowledge = Math.max(1.0, this.knowledge + advanceMultiplicator * villageMod);
                }
                else {
                    this.knowledge = Math.max(1.0, this.knowledge + advanceMultiplicator);
                }
            }
            else {
                advanceMultiplicator *= Skill.skillMod;
                if (this.number == 10086 && Servers.localServer.isChallengeOrEpicServer() && !Server.getInstance().isPS()) {
                    advanceMultiplicator *= 2.0;
                }
                if (isplayer) {
                    try {
                        final Player player2 = Players.getInstance().getPlayer(pid);
                        advanceMultiplicator *= 1.0f + ItemBonus.getSkillGainBonus(player2, this.getNumber());
                        final int currstam = player2.getStatus().getStamina();
                        float staminaMod = 1.0f;
                        if (currstam <= 400) {
                            staminaMod = 0.1f;
                        }
                        if (player2.getCultist() != null && player2.getCultist().levelElevenSkillgain()) {
                            staminaMod *= 1.25f;
                        }
                        if (player2.getDeity() != null) {
                            if (player2.mustChangeTerritory() && !player2.isFighting()) {
                                staminaMod = 0.1f;
                                if (Server.rand.nextInt(100) == 0) {
                                    player2.getCommunicator().sendAlertServerMessage("You sense a lack of energy. Rumours have it that " + player2.getDeity().name + " wants " + player2.getDeity().getHisHerItsString() + " champions to move between kingdoms and seek out the enemy.");
                                }
                            }
                            if (player2.getDeity().isLearner()) {
                                if (player2.getFaith() > 20.0f && player2.getFavor() >= 10.0f) {
                                    staminaMod += 0.1f;
                                }
                            }
                            else if (player2.getDeity().isWarrior() && player2.getFaith() > 20.0f && player2.getFavor() >= 20.0f && this.isFightingSkill()) {
                                staminaMod += 0.25f;
                            }
                        }
                        staminaMod += Math.max(player2.getStatus().getNutritionlevel() / 10.0f - 0.05f, 0.0f);
                        if (player2.isFighting() && currstam <= 400) {
                            staminaMod = 0.0f;
                        }
                        advanceMultiplicator *= staminaMod;
                        if (player2.getEnemyPresense() > Player.minEnemyPresence && !this.ignoresEnemy()) {
                            advanceMultiplicator *= 0.800000011920929;
                        }
                        if (this.knowledge < this.minimum || (this.basicPersonal && this.knowledge < 20.0)) {
                            advanceMultiplicator *= 3.0;
                        }
                        if (player2.hasSleepBonus()) {
                            advanceMultiplicator *= 2.0;
                        }
                        final int taffinity = this.affinity + (AffinitiesTimed.isTimedAffinity(pid, this.getNumber()) ? 1 : 0);
                        advanceMultiplicator *= 1.0f + taffinity * 0.1f;
                        if (player2.getMovementScheme().samePosCounts > 20) {
                            advanceMultiplicator = 0.0;
                        }
                        if (!player2.isPaying() && this.knowledge >= 20.0) {
                            advanceMultiplicator = 0.0;
                            if (!player2.isPlayerAssistant() && Server.rand.nextInt(500) == 0) {
                                player2.getCommunicator().sendNormalServerMessage("You may only gain skill beyond level 20 if you have a premium account.", (byte)2);
                            }
                        }
                        if ((this.number == 10055 || this.number == 10053 || this.number == 10054) && player2.loggerCreature1 > 0L) {
                            Skill.logger.log(Level.INFO, player2.getName() + " advancing " + Math.min(1.0, advanceMultiplicator * this.knowledge / skillDivider) + "!");
                        }
                    }
                    catch (NoSuchPlayerException nsp2) {
                        advanceMultiplicator = 0.0;
                        Skill.logger.log(Level.WARNING, "Player with id " + this.id + " is learning skills while not online?", nsp2);
                    }
                }
                if (useNewSystem) {
                    double maxSkillRate = 40.0;
                    double rateMod = 1.0;
                    final short sType = SkillSystem.getTypeFor(this.number);
                    if (sType == 1 || sType == 0) {
                        maxSkillRate = 60.0;
                        rateMod = 0.8;
                    }
                    final double skillRate = Math.min(maxSkillRate, skillDivider * (1.0 + this.knowledge / (100.0 - 90.0 * (this.knowledge / 110.0))) * rateMod);
                    this.knowledge = Math.max(1.0, this.knowledge + Math.min(1.0, advanceMultiplicator * this.knowledge / skillRate));
                }
                else {
                    this.knowledge = Math.max(1.0, this.knowledge + Math.min(1.0, advanceMultiplicator * this.knowledge));
                }
                if (this.minimum < this.knowledge) {
                    this.minimum = this.knowledge;
                }
                this.checkTitleChange(oldknowledge, this.knowledge);
            }
            try {
                if ((oldknowledge != this.knowledge && (this.saveCounter == 0 || this.knowledge > 50.0)) || decay) {
                    this.saveValue(isplayer);
                }
                ++this.saveCounter;
                if (this.saveCounter == 10) {
                    this.saveCounter = 0;
                }
            }
            catch (IOException ex) {
                Skill.logger.log(Level.WARNING, "Failed to save skill " + this.getName() + "(" + this.getNumber() + ") for creature " + this.parent.getId(), ex);
            }
            if (pid != -10L && isplayer) {
                try {
                    final Player holder = Players.getInstance().getPlayer(pid);
                    float weakMod = 1.0f;
                    double bonusKnowledge = this.knowledge;
                    final float ws = holder.getBonusForSpellEffect((byte)41);
                    if (ws > 0.0f) {
                        weakMod = 0.8f;
                    }
                    if (this.number == 102 && this.knowledge < 40.0) {
                        final float x = holder.getBonusForSpellEffect((byte)25);
                        if (x > 0.0f) {
                            final double diff = 40.0 - this.knowledge;
                            bonusKnowledge = this.knowledge + diff * x / 100.0;
                        }
                        else {
                            final float hs = holder.getBonusForSpellEffect((byte)40);
                            if (hs > 0.0f) {
                                final double diff2 = 40.0 - this.knowledge;
                                bonusKnowledge = this.knowledge + diff2 * hs / 100.0;
                            }
                        }
                    }
                    bonusKnowledge *= weakMod;
                    if (isplayer) {
                        final int diff3 = (int)this.knowledge - (int)oldknowledge;
                        if (diff3 > 0) {
                            holder.achievement(371, diff3);
                        }
                    }
                    if (!this.parent.paying && !this.basicPersonal) {
                        bonusKnowledge = Math.min(20.0, bonusKnowledge);
                    }
                    else if (!this.parent.paying && bonusKnowledge > 20.0) {
                        bonusKnowledge = Math.min(this.getKnowledge(0.0), bonusKnowledge);
                    }
                    holder.getCommunicator().sendUpdateSkill(this.number, (float)bonusKnowledge, this.isTemporary() ? 0 : this.affinity);
                    if (this.number != 2147483644 && this.number != 2147483642) {
                        holder.resetInactivity(true);
                    }
                }
                catch (NoSuchPlayerException nsp2) {
                    Skill.logger.log(Level.WARNING, pid + ":" + nsp2.getMessage(), nsp2);
                }
            }
        }
    }
    
    public boolean isTemporary() {
        return false;
    }
    
    public boolean isFightingSkill() {
        return SkillSystem.isFightingSkill(this.number);
    }
    
    public void checkInitialTitle() {
        if (this.getNumber() == 10067) {
            final long pid = this.parent.getId();
            if (WurmId.getType(pid) == 0) {
                if (this.knowledge >= 20.0) {
                    final Player p = Players.getInstance().getPlayerOrNull(pid);
                    if (p != null) {
                        p.maybeTriggerAchievement(605, true);
                    }
                }
                if (this.knowledge >= 50.0) {
                    final Player p = Players.getInstance().getPlayerOrNull(pid);
                    if (p != null) {
                        p.maybeTriggerAchievement(617, true);
                    }
                }
            }
        }
        if (this.knowledge >= 50.0) {
            final long pid = this.parent.getId();
            if (WurmId.getType(pid) == 0) {
                final Titles.Title title = Titles.Title.getTitle(this.number, Titles.TitleType.NORMAL);
                if (title != null) {
                    try {
                        Players.getInstance().getPlayer(pid).addTitle(title);
                    }
                    catch (NoSuchPlayerException nsp) {
                        Skill.logger.log(Level.WARNING, pid + ":" + nsp.getMessage(), nsp);
                    }
                }
            }
            final Player p = Players.getInstance().getPlayerOrNull(pid);
            if (p != null) {
                p.maybeTriggerAchievement(555, true);
            }
        }
        if (this.knowledge >= 70.0) {
            final long pid = this.parent.getId();
            if (WurmId.getType(pid) == 0) {
                final Titles.Title title = Titles.Title.getTitle(this.number, Titles.TitleType.MINOR);
                if (title != null) {
                    try {
                        Players.getInstance().getPlayer(pid).addTitle(title);
                    }
                    catch (NoSuchPlayerException nsp) {
                        Skill.logger.log(Level.WARNING, pid + ":" + nsp.getMessage(), nsp);
                    }
                }
            }
            final Player p = Players.getInstance().getPlayerOrNull(pid);
            if (p != null) {
                p.maybeTriggerAchievement(564, true);
            }
            if (p != null && this.getNumber() == 10066) {
                p.maybeTriggerAchievement(633, true);
            }
        }
        if (this.knowledge >= 90.0) {
            final long pid = this.parent.getId();
            if (WurmId.getType(pid) == 0) {
                final Titles.Title title = Titles.Title.getTitle(this.number, Titles.TitleType.MASTER);
                if (title != null) {
                    try {
                        Players.getInstance().getPlayer(pid).addTitle(title);
                    }
                    catch (NoSuchPlayerException nsp) {
                        Skill.logger.log(Level.WARNING, pid + ":" + nsp.getMessage(), nsp);
                    }
                }
            }
            final Player p = Players.getInstance().getPlayerOrNull(pid);
            if (p != null) {
                p.maybeTriggerAchievement(590, true);
            }
        }
        if (this.knowledge >= 99.99999615) {
            final long pid = this.parent.getId();
            if (WurmId.getType(pid) == 0) {
                final Titles.Title title = Titles.Title.getTitle(this.number, Titles.TitleType.LEGENDARY);
                if (title != null) {
                    try {
                        Players.getInstance().getPlayer(pid).addTitle(title);
                    }
                    catch (NoSuchPlayerException nsp) {
                        Skill.logger.log(Level.WARNING, pid + ":" + nsp.getMessage(), nsp);
                    }
                }
            }
        }
    }
    
    void checkTitleChange(final double oldknowledge, final double newknowledge) {
        if (this.getNumber() == 10067 && oldknowledge < 20.0 && newknowledge >= 20.0) {
            final long pid = this.parent.getId();
            if (WurmId.getType(pid) == 0) {
                try {
                    final Player p = Players.getInstance().getPlayer(pid);
                    p.maybeTriggerAchievement(605, true);
                }
                catch (NoSuchPlayerException nsp) {
                    Skill.logger.log(Level.WARNING, pid + ":" + nsp.getMessage(), nsp);
                }
            }
        }
        if (oldknowledge < 50.0 && newknowledge >= 50.0) {
            final long pid = this.parent.getId();
            if (WurmId.getType(pid) == 0) {
                final Titles.Title title = Titles.Title.getTitle(this.number, Titles.TitleType.NORMAL);
                if (title != null) {
                    try {
                        final Player p2 = Players.getInstance().getPlayer(pid);
                        p2.addTitle(title);
                        p2.achievement(555);
                        if (this.getNumber() == 10067) {
                            p2.maybeTriggerAchievement(617, true);
                        }
                    }
                    catch (NoSuchPlayerException nsp2) {
                        Skill.logger.log(Level.WARNING, pid + ":" + nsp2.getMessage(), nsp2);
                    }
                }
                int count = 0;
                for (final Skill s : this.parent.getSkills()) {
                    if (s.getKnowledge() >= 50.0) {
                        ++count;
                    }
                }
                if (count >= 10) {
                    try {
                        final Player p3 = Players.getInstance().getPlayer(pid);
                        p3.maybeTriggerAchievement(598, true);
                    }
                    catch (NoSuchPlayerException ex) {}
                }
            }
        }
        if (oldknowledge < 70.0 && newknowledge >= 70.0) {
            final long pid = this.parent.getId();
            if (WurmId.getType(pid) == 0) {
                final Titles.Title title = Titles.Title.getTitle(this.number, Titles.TitleType.MINOR);
                if (title != null) {
                    try {
                        final Player p2 = Players.getInstance().getPlayer(pid);
                        p2.addTitle(title);
                        p2.achievement(564);
                        if (this.getNumber() == 10066) {
                            p2.maybeTriggerAchievement(633, true);
                        }
                    }
                    catch (NoSuchPlayerException nsp2) {
                        Skill.logger.log(Level.WARNING, pid + ":" + nsp2.getMessage(), nsp2);
                    }
                }
            }
        }
        if (oldknowledge < 90.0 && newknowledge >= 90.0) {
            final long pid = this.parent.getId();
            if (WurmId.getType(pid) == 0) {
                final Titles.Title title = Titles.Title.getTitle(this.number, Titles.TitleType.MASTER);
                if (title != null) {
                    try {
                        final Player p2 = Players.getInstance().getPlayer(pid);
                        p2.addTitle(title);
                        p2.achievement(590);
                    }
                    catch (NoSuchPlayerException nsp2) {
                        Skill.logger.log(Level.WARNING, pid + ":" + nsp2.getMessage(), nsp2);
                    }
                }
            }
        }
        if (oldknowledge < 99.99999615 && newknowledge >= 99.99999615) {
            final long pid = this.parent.getId();
            if (WurmId.getType(pid) == 0) {
                final Titles.Title title = Titles.Title.getTitle(this.number, Titles.TitleType.LEGENDARY);
                if (title != null) {
                    try {
                        Players.getInstance().getPlayer(pid).addTitle(title);
                    }
                    catch (NoSuchPlayerException nsp2) {
                        Skill.logger.log(Level.WARNING, pid + ":" + nsp2.getMessage(), nsp2);
                    }
                }
            }
        }
    }
    
    public void setAffinity(final int aff) {
        this.affinity = aff;
        final long pid = this.parent.getId();
        if (WurmId.getType(pid) == 0 && !this.isTemporary()) {
            try {
                final Player holder = Players.getInstance().getPlayer(pid);
                float weakMod = 1.0f;
                double bonusKnowledge = this.knowledge;
                final float ws = holder.getBonusForSpellEffect((byte)41);
                if (ws > 0.0f) {
                    weakMod = 0.8f;
                }
                if (this.number == 102 && this.knowledge < 40.0) {
                    final float x = holder.getBonusForSpellEffect((byte)25);
                    if (x > 0.0f) {
                        final double diff = 40.0 - this.knowledge;
                        bonusKnowledge = this.knowledge + diff * x / 100.0;
                    }
                    else {
                        final float hs = holder.getBonusForSpellEffect((byte)40);
                        if (hs > 0.0f) {
                            final double diff2 = 40.0 - this.knowledge;
                            bonusKnowledge = this.knowledge + diff2 * hs / 100.0;
                        }
                    }
                }
                bonusKnowledge *= weakMod;
                if (!this.parent.paying && !this.basicPersonal) {
                    bonusKnowledge = Math.min(20.0, bonusKnowledge);
                }
                else if (!this.parent.paying && bonusKnowledge > 20.0) {
                    bonusKnowledge = Math.min(this.getKnowledge(0.0), bonusKnowledge);
                }
                holder.getCommunicator().sendUpdateSkill(this.number, (float)bonusKnowledge, this.affinity);
            }
            catch (NoSuchPlayerException nsp) {
                Skill.logger.log(Level.WARNING, nsp.getMessage(), nsp);
            }
        }
    }
    
    abstract void save() throws IOException;
    
    abstract void load() throws IOException;
    
    abstract void saveValue(final boolean p0) throws IOException;
    
    public abstract void setJoat(final boolean p0) throws IOException;
    
    public abstract void setNumber(final int p0) throws IOException;
    
    public boolean hasLowCreationGain() {
        switch (this.getNumber()) {
            case 1010:
            case 10034:
            case 10036:
            case 10037:
            case 10041:
            case 10042:
            case 10083:
            case 10091: {
                return false;
            }
            default: {
                return true;
            }
        }
    }
    
    public void maybeSetMinimum() {
        if (this.minimum < this.knowledge) {
            this.minimum = this.knowledge;
            try {
                this.save();
            }
            catch (IOException iox) {
                Skill.logger.log(Level.INFO, "Failed to save skill " + this.id, iox);
            }
        }
    }
    
    public static int getTotalAffinityChecks() {
        return Skill.totalAffinityChecks;
    }
    
    public static int getTotalAffinitiesGiven() {
        return Skill.totalAffinitiesGiven;
    }
    
    static {
        affinityDebug = Logger.getLogger("affinities");
        Skill.totalAffinityChecks = 0;
        Skill.totalAffinitiesGiven = 0;
        Skill.logger = Logger.getLogger(Skill.class.getName());
        Skill.random = new Random();
        chances = calculateChances();
        skillMod = (Servers.localServer.EPIC ? 3.0 : 1.5);
        isChallenge = Servers.localServer.isChallengeServer();
    }
}
