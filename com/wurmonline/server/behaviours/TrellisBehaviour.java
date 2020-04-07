// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.Servers;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.utils.logging.TileEvent;
import com.wurmonline.server.WurmHarvestables;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.items.NoSuchTemplateException;
import java.util.logging.Level;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.items.RuneUtilities;
import com.wurmonline.server.Server;
import com.wurmonline.server.zones.Zones;
import java.util.Collections;
import com.wurmonline.mesh.FoliageAge;
import java.util.LinkedList;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public class TrellisBehaviour extends ItemBehaviour
{
    private static final Logger logger;
    
    public TrellisBehaviour() {
        super((short)58);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, target);
        toReturn.addAll(this.getBehavioursForTrellis(performer, null, target));
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, source, target);
        toReturn.addAll(this.getBehavioursForTrellis(performer, source, target));
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item target, final short action, final float counter) {
        final boolean[] ans = this.trellisAction(act, performer, null, target, action, counter);
        if (ans[0]) {
            return ans[1];
        }
        return super.action(act, performer, target, action, counter);
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final Item target, final short action, final float counter) {
        final boolean[] ans = this.trellisAction(act, performer, source, target, action, counter);
        if (ans[0]) {
            return ans[1];
        }
        return super.action(act, performer, source, target, action, counter);
    }
    
    private List<ActionEntry> getBehavioursForTrellis(final Creature performer, @Nullable final Item source, final Item trellis) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        if (trellis.isHarvestable() && source != null && source.getTemplateId() == 267 && Methods.isActionAllowed(performer, (short)152, trellis)) {
            toReturn.add(Actions.actionEntrys[152]);
        }
        final List<ActionEntry> nature = new LinkedList<ActionEntry>();
        final FoliageAge age = FoliageAge.getFoliageAge(trellis.getAuxData());
        if (performer.getPower() >= 2 && source != null && source.getTemplateId() == 176) {
            nature.add(Actions.actionEntrys[188]);
        }
        if (age.isPrunable()) {
            nature.add(Actions.actionEntrys[373]);
        }
        if (TileTreeBehaviour.isSproutingAge(age.getAgeId())) {
            nature.add(Actions.actionEntrys[187]);
        }
        if (this.hasFruit(performer, trellis)) {
            nature.add(new ActionEntry((short)852, "Study", "making notes"));
        }
        if (nature.size() > 0) {
            Collections.sort(nature);
            toReturn.add(new ActionEntry((short)(-nature.size()), "Nature", "nature", TrellisBehaviour.emptyIntArr));
            toReturn.addAll(nature);
        }
        return toReturn;
    }
    
    public boolean[] trellisAction(final Action act, final Creature performer, @Nullable final Item source, final Item target, final short action, final float counter) {
        if (action == 152 && source.getTemplateId() == 267 && target.isPlanted() && (target.isHarvestable() || counter > 1.0f)) {
            return new boolean[] { true, this.harvest(act, performer, source, target, counter) };
        }
        if (action == 188 && performer.getPower() >= 2 && source != null && source.getTemplateId() == 176) {
            target.setLeftAuxData(target.getLeftAuxData() + 1);
            target.updateName();
            return new boolean[] { true, true };
        }
        if (action == 373 && source != null) {
            return new boolean[] { true, prune(act, performer, source, target, counter) };
        }
        if (action == 187 && source != null && source.getTemplateId() == 267) {
            return new boolean[] { true, this.pickSprout(performer, source, target, counter, act) };
        }
        if (action == 852 && this.hasFruit(performer, target)) {
            return new boolean[] { true, this.study(act, performer, target, action, counter) };
        }
        if (action == 1) {
            super.action(act, performer, target, action, counter);
            this.examine(performer, target);
            return new boolean[] { true, true };
        }
        return new boolean[] { false, false };
    }
    
    private boolean harvest(final Action act, final Creature performer, final Item tool, final Item target, final float counter) {
        if (!performer.getInventory().mayCreatureInsertItem()) {
            performer.getCommunicator().sendNormalServerMessage("You have no space left in your inventory to put what you harvest.");
            return true;
        }
        if (!Methods.isActionAllowed(performer, act.getNumber(), target)) {
            return true;
        }
        int templateId = target.getTemplate().getHarvestsTo();
        if (templateId == 411) {
            if (target.getTileY() > Zones.worldTileSizeY / 2) {
                templateId = 411;
            }
            else {
                templateId = 414;
            }
        }
        boolean toReturn = false;
        int time = 150;
        final Skill skill = performer.getSkills().getSkillOrLearn(10045);
        time = Actions.getStandardActionTime(performer, skill, tool, 0.0);
        if (counter == 1.0f) {
            final int maxSearches = calcTrellisMaxHarvest(target, skill.getKnowledge(0.0), tool);
            time = Actions.getQuickActionTime(performer, skill, null, 0.0);
            act.setNextTick(time);
            act.setTickCount(1);
            act.setData(0L);
            final float totalTime = time * maxSearches;
            act.setTimeLeft((int)totalTime);
            performer.getCommunicator().sendNormalServerMessage("You start to harvest the " + target.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " starts to harvest a trellis.", performer, 5);
            performer.sendActionControl(Actions.actionEntrys[152].getVerbString(), true, (int)totalTime);
            performer.getStatus().modifyStamina(-500.0f);
        }
        else {
            time = act.getTimeLeft();
        }
        if (tool != null && act.justTickedSecond()) {
            tool.setDamage(tool.getDamage() + 3.0E-4f * tool.getDamageModifier());
        }
        if (counter * 10.0f >= act.getNextTick()) {
            if (act.getRarity() != 0) {
                performer.playPersonalSound("sound.fx.drumroll");
            }
            final int searchCount = act.getTickCount();
            final int maxSearches2 = calcTrellisMaxHarvest(target, skill.getKnowledge(0.0), tool);
            act.incTickCount();
            act.incNextTick(Actions.getQuickActionTime(performer, skill, null, 0.0));
            final int knowledge = (int)skill.getKnowledge(0.0);
            performer.getStatus().modifyStamina(-1500 * searchCount);
            if (searchCount >= maxSearches2) {
                toReturn = true;
            }
            act.setData(act.getData() + 1L);
            final double power = skill.skillCheck(skill.getKnowledge(0.0) - 5.0, tool, 0.0, false, counter / searchCount);
            try {
                float ql = knowledge + (100 - knowledge) * ((float)power / 500.0f);
                float modifier = 1.0f;
                if (tool.getSpellEffects() != null) {
                    modifier = tool.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_RESGATHERED);
                }
                target.setLastMaintained(WurmCalendar.currentTime);
                ql = Math.min(100.0f, (ql + tool.getRarity()) * modifier);
                final Item harvested = ItemFactory.createItem(templateId, Math.max(1.0f, ql), act.getRarity(), null);
                if (ql < 0.0f) {
                    harvested.setDamage(-ql / 2.0f);
                }
                performer.getInventory().insertItem(harvested);
                SoundPlayer.playSound("sound.forest.branchsnap", target.getTileX(), target.getTileY(), true, 3.0f);
                if (searchCount == 1) {
                    target.setHarvestable(false);
                }
                performer.getCommunicator().sendNormalServerMessage("You harvest " + harvested.getNameWithGenus() + " from the " + target.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " harvests " + harvested.getNameWithGenus() + " from a trellis.", performer, 5);
                if (searchCount < maxSearches2 && !performer.getInventory().mayCreatureInsertItem()) {
                    performer.getCommunicator().sendNormalServerMessage("Your inventory is now full. You would have no space to put whatever you find.");
                    toReturn = true;
                }
            }
            catch (NoSuchTemplateException nst) {
                TrellisBehaviour.logger.log(Level.WARNING, "No template for " + templateId, nst);
                performer.getCommunicator().sendNormalServerMessage("You fail to harvest. You realize something is wrong with the world.");
            }
            catch (FailedException fe) {
                TrellisBehaviour.logger.log(Level.WARNING, fe.getMessage(), fe);
                performer.getCommunicator().sendNormalServerMessage("You fail to harvest. You realize something is wrong with the world.");
            }
            if (searchCount < maxSearches2) {
                act.setRarity(performer.getRarity());
            }
        }
        return toReturn;
    }
    
    private void examine(final Creature performer, final Item target) {
        switch (target.getTemplateId()) {
            case 920: {
                final String grapetype = (target.getTileY() > Zones.worldTileSizeY / 2) ? "blue grapes." : "green grapes.";
                if (target.isHarvestable()) {
                    performer.getCommunicator().sendNormalServerMessage("The trellis has some juicy " + grapetype);
                    break;
                }
                if (isAlmostRipe(920)) {
                    performer.getCommunicator().sendNormalServerMessage("The trellis has a couple of immature " + grapetype);
                    break;
                }
                if (hasBeenPicked(target, 920)) {
                    performer.getCommunicator().sendNormalServerMessage("The trellis has no grapes left; all have been picked.");
                    break;
                }
                break;
            }
            case 1018: {
                if (target.isHarvestable()) {
                    performer.getCommunicator().sendNormalServerMessage("The trellis has some beautiful flowers.");
                    break;
                }
                if (isAlmostRipe(1018)) {
                    performer.getCommunicator().sendNormalServerMessage("The trellis has a couple of promising buds.");
                    break;
                }
                if (hasBeenPicked(target, 1018)) {
                    performer.getCommunicator().sendNormalServerMessage("The trellis has no flowers left; all have been picked.");
                    break;
                }
                break;
            }
            case 1274: {
                if (target.isHarvestable()) {
                    performer.getCommunicator().sendNormalServerMessage("The trellis has some ripe hops, ready to be harvested.");
                    break;
                }
                if (isAlmostRipe(1274)) {
                    performer.getCommunicator().sendNormalServerMessage("The trellis has a couple of immature hops.");
                    break;
                }
                if (hasBeenPicked(target, 1274)) {
                    performer.getCommunicator().sendNormalServerMessage("The trellis has no hops left; all have been picked.");
                    break;
                }
                break;
            }
        }
    }
    
    private static boolean isAlmostRipe(final int type) {
        switch (type) {
            case 920: {
                return WurmHarvestables.Harvestable.GRAPE.isAlmostRipe();
            }
            case 1018: {
                return WurmHarvestables.Harvestable.ROSE.isAlmostRipe();
            }
            case 1274: {
                return WurmHarvestables.Harvestable.HOPS.isAlmostRipe();
            }
            default: {
                return false;
            }
        }
    }
    
    private static boolean hasBeenPicked(final Item target, final int type) {
        switch (type) {
            case 920: {
                if (!WurmHarvestables.Harvestable.GRAPE.isHarvestable()) {
                    return false;
                }
                break;
            }
            case 1018: {
                if (!WurmHarvestables.Harvestable.ROSE.isHarvestable()) {
                    return false;
                }
                break;
            }
            case 1274: {
                if (!WurmHarvestables.Harvestable.HOPS.isHarvestable()) {
                    return false;
                }
                break;
            }
        }
        return !target.isHarvestable();
    }
    
    static boolean prune(final Action action, final Creature performer, final Item sickle, final Item trellis, final float counter) {
        boolean toReturn = true;
        if (sickle.getTemplateId() == 267) {
            final FoliageAge age = FoliageAge.getFoliageAge(trellis.getAuxData());
            final String trellisName = trellis.getName().toLowerCase();
            if (!age.isPrunable()) {
                performer.getCommunicator().sendNormalServerMessage("It does not make sense to prune now.");
                return true;
            }
            toReturn = false;
            int time = 150;
            final Skill forestry = performer.getSkills().getSkillOrLearn(10048);
            final Skill sickskill = performer.getSkills().getSkillOrLearn(10046);
            if (sickle.getTemplateId() == 267) {
                time = Actions.getStandardActionTime(performer, forestry, sickle, sickskill.getKnowledge(0.0));
            }
            if (counter == 1.0f) {
                performer.getCommunicator().sendNormalServerMessage("You start to prune the " + trellisName + ".");
                Server.getInstance().broadCastAction(performer.getName() + " starts to prune the " + trellisName + ".", performer, 5);
                performer.sendActionControl(Actions.actionEntrys[373].getVerbString(), true, time);
            }
            if (action.justTickedSecond()) {
                sickle.setDamage(sickle.getDamage() + 3.0E-4f * sickle.getDamageModifier());
            }
            if (counter * 10.0f >= time) {
                double bonus = 0.0;
                double power = 0.0;
                bonus = Math.max(1.0, sickskill.skillCheck(1.0, sickle, 0.0, false, counter));
                power = forestry.skillCheck(forestry.getKnowledge(0.0) - 10.0, sickle, bonus, false, counter);
                toReturn = true;
                SoundPlayer.playSound("sound.forest.branchsnap", trellis.getTileX(), trellis.getTileY(), true, 3.0f);
                if (power < 0.0) {
                    performer.getCommunicator().sendNormalServerMessage("You make a lot of errors and need to take a break.");
                    return toReturn;
                }
                final FoliageAge newage = age.getPrunedAge();
                trellis.setLeftAuxData(newage.getAgeId());
                TileEvent.log(trellis.getTileX(), trellis.getTileY(), 0, performer.getWurmId(), 373);
                trellis.updateName();
                performer.getCommunicator().sendNormalServerMessage("You prune the " + trellisName + ".");
                Server.getInstance().broadCastAction(performer.getName() + " prunes the " + trellisName + ".", performer, 5);
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("You cannot prune with that.");
            TrellisBehaviour.logger.log(Level.WARNING, performer.getName() + " tried to prune with a " + sickle.getName());
        }
        return toReturn;
    }
    
    private boolean pickSprout(final Creature performer, final Item sickle, final Item trellis, final float counter, final Action act) {
        boolean toReturn = true;
        if (sickle.getTemplateId() == 267) {
            if (!performer.getInventory().mayCreatureInsertItem()) {
                performer.getCommunicator().sendNormalServerMessage("Your inventory is full. You would have no space to put the sprout.");
                return true;
            }
            int age = trellis.getLeftAuxData();
            if (age == 7 || age == 9 || age == 11 || age == 13) {
                final Skill forestry = performer.getSkills().getSkillOrLearn(10048);
                toReturn = false;
                final int time = Actions.getStandardActionTime(performer, forestry, sickle, 0.0);
                if (counter == 1.0f) {
                    try {
                        final int weight = ItemTemplateFactory.getInstance().getTemplate(266).getWeightGrams();
                        if (!performer.canCarry(weight)) {
                            performer.getCommunicator().sendNormalServerMessage("You would not be able to carry the sprout. You need to drop some things first.");
                            return true;
                        }
                    }
                    catch (NoSuchTemplateException nst) {
                        TrellisBehaviour.logger.log(Level.WARNING, nst.getLocalizedMessage(), nst);
                        return true;
                    }
                    performer.getCommunicator().sendNormalServerMessage("You start cutting a sprout from the trellis.");
                    Server.getInstance().broadCastAction(performer.getName() + " starts to cut a sprout off a trellis.", performer, 5);
                    performer.sendActionControl(Actions.actionEntrys[187].getVerbString(), true, time);
                }
                if (counter * 10.0f >= time) {
                    if (act.getRarity() != 0) {
                        performer.playPersonalSound("sound.fx.drumroll");
                    }
                    try {
                        final int weight = ItemTemplateFactory.getInstance().getTemplate(266).getWeightGrams();
                        if (!performer.canCarry(weight)) {
                            performer.getCommunicator().sendNormalServerMessage("You would not be able to carry the sprout. You need to drop some things first.");
                            return true;
                        }
                    }
                    catch (NoSuchTemplateException nst) {
                        TrellisBehaviour.logger.log(Level.WARNING, nst.getLocalizedMessage(), nst);
                        return true;
                    }
                    sickle.setDamage(sickle.getDamage() + 0.003f * sickle.getDamageModifier());
                    double bonus = 0.0;
                    double power = 0.0;
                    final Skill sickskill = performer.getSkills().getSkillOrLearn(10046);
                    bonus = Math.max(1.0, sickskill.skillCheck(1.0, sickle, 0.0, false, counter));
                    power = forestry.skillCheck(1.0, sickle, bonus, false, counter);
                    toReturn = true;
                    String templateType = "sprout";
                    try {
                        int template = 266;
                        byte material = 0;
                        if (trellis.getTemplateId() == 920) {
                            template = 266;
                            material = 49;
                            templateType = "sprout";
                        }
                        else if (trellis.getTemplateId() == 1018) {
                            template = 266;
                            material = 47;
                            templateType = "sprout";
                        }
                        else if (trellis.getTemplateId() == 919) {
                            template = 917;
                            material = 68;
                            templateType = "seedling";
                        }
                        else if (trellis.getTemplateId() == 1274) {
                            template = 1275;
                            material = 68;
                            templateType = "seedling";
                        }
                        float modifier = 1.0f;
                        if (sickle.getSpellEffects() != null) {
                            modifier = sickle.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_RESGATHERED);
                        }
                        final Item sprout = ItemFactory.createItem(template, Math.max(1.0f, Math.min(100.0f, (float)power * modifier + sickle.getRarity())), material, act.getRarity(), null);
                        if (power < 0.0) {
                            sprout.setDamage((float)(-power) / 2.0f);
                        }
                        SoundPlayer.playSound("sound.forest.branchsnap", trellis.getTileX(), trellis.getTileY(), true, 2.0f);
                        --age;
                        performer.getInventory().insertItem(sprout);
                        trellis.setLeftAuxData(age);
                        trellis.updateName();
                        performer.getCommunicator().sendNormalServerMessage("You cut a " + templateType + " from the trellis.");
                        Server.getInstance().broadCastAction(performer.getName() + " cuts a " + templateType + " off a trellis.", performer, 5);
                    }
                    catch (NoSuchTemplateException nst2) {
                        TrellisBehaviour.logger.log(Level.WARNING, "No template for " + templateType + "!", nst2);
                        performer.getCommunicator().sendNormalServerMessage("You fail to pick the " + templateType + ". You realize something is wrong with the world.");
                    }
                    catch (FailedException fe) {
                        TrellisBehaviour.logger.log(Level.WARNING, fe.getMessage(), fe);
                        performer.getCommunicator().sendNormalServerMessage("You fail to pick the " + templateType + ". You realize something is wrong with the world.");
                    }
                }
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("The trellis has nothing to pick.");
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("You cannot pick sprouts with that.");
            TrellisBehaviour.logger.log(Level.WARNING, performer.getName() + " tried to pick sprout with a " + sickle.getName());
        }
        return toReturn;
    }
    
    private boolean study(final Action act, final Creature performer, final Item trellis, final short action, final float counter) {
        final int harvestableId = WurmHarvestables.getHarvestableIdFromTrellis(trellis.getTemplateId());
        final WurmHarvestables.Harvestable harvestable = WurmHarvestables.getHarvestable(harvestableId);
        if (harvestable == null) {
            performer.getCommunicator().sendNormalServerMessage("You decide not to study " + trellis.getName() + " as it seems never to be harvestable.");
            return true;
        }
        int time = 0;
        if (counter == 1.0f) {
            time = 500;
            performer.getCommunicator().sendNormalServerMessage("You start to study the " + trellis.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " starts to study the " + trellis.getName() + ".", performer, 5);
            performer.sendActionControl("studying " + trellis.getName(), true, time);
            act.setTimeLeft(time);
            return false;
        }
        time = act.getTimeLeft();
        if (act.mayPlaySound()) {
            Methods.sendSound(performer, "sound.work.foragebotanize");
        }
        TileTreeBehaviour.sendStudyMessages(performer, harvestable, act.currentSecond());
        if (counter * 10.0f >= time) {
            if (performer.getPower() < 2) {
                trellis.setHarvestable(false);
            }
            ((Player)performer).setStudied(WurmHarvestables.getHarvestableIdFromTrellis(trellis.getTemplateId()));
            performer.getCommunicator().sendNormalServerMessage("You finish studying the " + trellis.getName() + ". You now need to record the study results.");
            Server.getInstance().broadCastAction(performer.getName() + " looks pleased with " + performer.getHisHerItsString() + " study results.", performer, 5);
            return true;
        }
        return false;
    }
    
    private static int calcTrellisMaxHarvest(final Item trellis, final double currentSkill, final Item tool) {
        int bonus = 0;
        if (tool.getSpellEffects() != null) {
            final float extraChance = tool.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_FARMYIELD) - 1.0f;
            if (extraChance > 0.0f && Server.rand.nextFloat() < extraChance) {
                ++bonus;
            }
        }
        return Math.min((int)(trellis.getCurrentQualityLevel() + 1.0f), (int)(currentSkill + 28.0) / 27 + bonus);
    }
    
    boolean hasFruit(final Creature performer, final Item trellis) {
        final int age = trellis.getLeftAuxData();
        return age > FoliageAge.YOUNG_FOUR.getAgeId() && age < FoliageAge.OVERAGED.getAgeId() && ((Servers.isThisATestServer() && performer.getPower() > 1) || trellis.isHarvestable());
    }
    
    static {
        logger = Logger.getLogger(TrellisBehaviour.class.getName());
    }
}
