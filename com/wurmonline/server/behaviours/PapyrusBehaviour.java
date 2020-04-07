// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.Items;
import com.wurmonline.server.items.RecipesByPlayer;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.questions.GMSelectHarvestable;
import com.wurmonline.server.questions.WriteRecipeQuestion;
import com.wurmonline.server.players.PlayerInfo;
import java.util.Iterator;
import com.wurmonline.server.questions.ShowHarvestableInfo;
import com.wurmonline.server.questions.CookBookQuestion;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.shared.util.MaterialUtilities;
import com.wurmonline.server.questions.SimplePopup;
import com.wurmonline.server.questions.TextInputQuestion;
import com.wurmonline.server.Server;
import java.util.Collection;
import com.wurmonline.server.items.InscriptionData;
import com.wurmonline.server.items.Recipe;
import com.wurmonline.server.WurmHarvestables;
import com.wurmonline.server.players.Player;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public final class PapyrusBehaviour extends ItemBehaviour
{
    private static final Logger logger;
    
    PapyrusBehaviour() {
        super((short)44);
    }
    
    List<ActionEntry> getPapyrusBehaviours(final Creature performer, @Nullable final Item source, final Item target) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        if (source != null) {
            if (source.isContainerLiquid() || source.getTemplateId() == 14 || source.getTemplateId() == 272) {
                if (target.getInscription() == null) {
                    toReturn.add(Actions.actionEntrys[505]);
                }
                if (target.getInscription() == null) {
                    final Player player = (Player)performer;
                    final Recipe recipe = player.getViewingRecipe();
                    if (recipe != null) {
                        toReturn.add(new ActionEntry((short)742, "Write recipe \"" + recipe.getName() + "\"", "writing recipe"));
                    }
                    if (player.getStudied() > 0) {
                        final WurmHarvestables.Harvestable harvestable = WurmHarvestables.getHarvestable(player.getStudied());
                        if (harvestable != null) {
                            toReturn.add(new ActionEntry((short)853, "Record " + harvestable.getName() + " info", "Recording info"));
                        }
                    }
                }
            }
            else if (source.getTemplateId() == 176 && performer.getPower() > 2 && target.getInscription() == null) {
                toReturn.add(new ActionEntry((short)742, "Write recipe", "writing recipe"));
                toReturn.add(new ActionEntry((short)853, "Record harvestable info", "Recording info"));
            }
        }
        if (target.canHaveInscription()) {
            final InscriptionData ins = target.getInscription();
            if (ins != null && ins.hasBeenInscribed()) {
                if (target.getAuxData() == 0) {
                    toReturn.add(Actions.actionEntrys[506]);
                }
                else if (target.getAuxData() == 1) {
                    toReturn.add(Actions.actionEntrys[743]);
                }
                else if (target.getAuxData() > 8) {
                    final WurmHarvestables.Harvestable harvestable2 = WurmHarvestables.getHarvestable(target.getAuxData() - 8);
                    if (harvestable2 != null) {
                        toReturn.add(new ActionEntry((short)17, "Read " + harvestable2.getName() + " info", "reading"));
                    }
                }
            }
            else if (source != null && source.getTemplateId() == 1254 && target.getAuxData() == 0) {
                toReturn.add(new ActionEntry((short)118, "Wax", "waxing"));
            }
        }
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, target);
        toReturn.addAll(this.getPapyrusBehaviours(performer, null, target));
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, source, target);
        toReturn.addAll(this.getPapyrusBehaviours(performer, source, target));
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item target, final short action, final float counter) {
        return this.performPapyrusAction(act, performer, null, target, action, counter);
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final Item target, final short action, final float counter) {
        return this.performPapyrusAction(act, performer, source, target, action, counter);
    }
    
    boolean performPapyrusAction(final Action act, final Creature performer, @Nullable final Item source, final Item target, final short action, final float counter) {
        final InscriptionData inscriptionData = target.getInscription();
        if (action == 505) {
            if (source == null) {
                performer.getCommunicator().sendNormalServerMessage("You fumble with the " + target.getName() + " but you cannot figure out how it works.");
                return true;
            }
            if (!target.canHaveInscription()) {
                performer.getCommunicator().sendNormalServerMessage("You cannot inscribe on that!");
                return true;
            }
            if (inscriptionData != null && (performer.getPower() < 2 || target.getAuxData() != 0) && inscriptionData.hasBeenInscribed()) {
                performer.getCommunicator().sendNormalServerMessage("This " + target.getName() + " has already been used by " + inscriptionData.getInscriber() + ".");
                return true;
            }
            if (target.getAuxData() == 2) {
                performer.getCommunicator().sendNormalServerMessage("This " + target.getName() + " has a waxy surface and therefore cannot be used to write on.");
                return true;
            }
            if (source.getTemplateId() == 14) {
                performer.getCommunicator().sendNormalServerMessage("You push your hand against the " + target.getName() + " in hopes of producing an imprint.");
                Server.getInstance().broadCastAction(performer.getName() + " presses a hand against the " + target.getName() + " in hopes of producing an imprint.", performer, 5);
                target.setInscription("The dirty hand of " + performer.getName() + " vaguely appears on the " + target.getName() + ".", performer.getName());
                return true;
            }
            if (source.getTemplateId() == 272) {
                performer.getCommunicator().sendNormalServerMessage("You push the bloody face of " + source.getName() + " against the " + target.getName() + " in hopes of producing an imprint.");
                Server.getInstance().broadCastAction(performer.getName() + " pushes the bloody face of " + source.getName() + " against the " + target.getName() + " in hopes of producing an imprint.", performer, 5);
                target.setInscription("The smeared bloody faceprint of " + source.getName() + " vaguely appears on the " + target.getName() + ".", performer.getName());
                return true;
            }
            if (!source.isContainerLiquid()) {
                performer.getCommunicator().sendNormalServerMessage("You have no container with liquid active.");
                return true;
            }
            Item liquid = null;
            Item contained = null;
            final Iterator<Item> it = source.getItems().iterator();
            while (it.hasNext()) {
                contained = it.next();
                if (!isValidColorant(contained.getTemplateId())) {
                    performer.getCommunicator().sendNormalServerMessage("You cannot inscribe with " + contained.getName() + ". You need to use something special for writing on " + target.getName() + ".");
                    return true;
                }
                liquid = contained;
            }
            if (liquid == null) {
                performer.getCommunicator().sendNormalServerMessage("You need a colorant to be able to inscribe with the " + source.getName() + " on the " + target.getName() + ".");
                return true;
            }
            short numChars = 0;
            final float inkQlBonus = liquid.getQualityLevel();
            final float targetQlBonus = target.getQualityLevel();
            switch (source.getTemplateId()) {
                case 749: {
                    numChars += getNumCharBonusForInk(liquid.getTemplateId());
                    numChars += (short)(2.0f * source.getQualityLevel() + 2.0f * inkQlBonus);
                    numChars += (short)(source.getRarity() * 40);
                    numChars += (short)(targetQlBonus * 2.0f);
                    break;
                }
                case 5: {
                    numChars = (short)(4.0f + source.getQualityLevel() + inkQlBonus);
                    numChars += (short)(target.getRarity() * 10);
                    break;
                }
                case 78: {
                    numChars = (short)(4.0 + 0.5 * source.getQualityLevel() + 0.5 * inkQlBonus + 0.5 * targetQlBonus);
                    numChars += (short)(target.getRarity() * 10);
                    break;
                }
                case 653: {
                    numChars = (short)(4.0 + 0.5 * source.getQualityLevel() + 0.5 * inkQlBonus + 0.5 * targetQlBonus);
                    numChars += (short)(target.getRarity() * 10);
                    break;
                }
                case 76: {
                    numChars = (short)(2.0 + 0.5 * source.getQualityLevel() + 0.5 * inkQlBonus + 0.5 * targetQlBonus);
                    numChars += (short)(target.getRarity() * 10);
                    break;
                }
                default: {
                    numChars = 0;
                    break;
                }
            }
            if (numChars <= 0) {
                performer.getCommunicator().sendNormalServerMessage("You would only make a mess with the " + source.getName() + " on the " + target.getName() + ". Try to use a more delicate tool when inscribing.");
                return true;
            }
            numChars = (short)Math.min(500, numChars / 2);
            final TextInputQuestion tiq = new TextInputQuestion(performer, "Inscribing a message on " + target.getName() + ".", "Inscribing is an irreversible process. Enter your important message here:", 2, target.getWurmId(), numChars, false);
            Server.getInstance().broadCastAction(performer.getName() + " starts to inscribe with " + source.getName() + " on " + target.getNameWithGenus() + ".", performer, 5);
            tiq.setLiquid(liquid);
            tiq.sendQuestion();
            return true;
        }
        else {
            if (action == 506) {
                if (inscriptionData != null) {
                    final SimplePopup pp = new SimplePopup(performer, target.getName(), inscriptionData);
                    performer.getCommunicator().sendNormalServerMessage("You read the " + target.getName() + ".");
                    pp.sendQuestion("Close");
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("There was no inscription to read.");
                }
                return true;
            }
            if (action == 83 || action == 180) {
                return (!performer.mayDestroy(target) && performer.getPower() <= 0) || MethodsItems.destroyItem(action, performer, source, target, false, counter);
            }
            if (action == 185) {
                if (performer.getPower() >= 0) {
                    performer.getCommunicator().sendNormalServerMessage("It is made from " + MaterialUtilities.getMaterialString(target.getMaterial()) + " (" + target.getMaterial() + ") " + ".");
                    performer.getCommunicator().sendNormalServerMessage("WurmId:" + target.getWurmId() + ", posx=" + target.getPosX() + "(" + ((int)target.getPosX() >> 2) + "), posy=" + target.getPosY() + "(" + ((int)target.getPosY() >> 2) + "), posz=" + target.getPosZ() + ", rot" + target.getRotation() + " layer=" + (target.isOnSurface() ? 0 : -1));
                    performer.getCommunicator().sendNormalServerMessage("Ql:" + target.getQualityLevel() + ", damage=" + target.getDamage() + ", weight=" + target.getWeightGrams() + ", temp=" + target.getTemperature());
                    performer.getCommunicator().sendNormalServerMessage("parentid=" + target.getParentId() + " ownerid=" + target.getOwnerId() + " zoneid=" + target.getZoneId() + " sizex=" + target.getSizeX() + ", sizey=" + target.getSizeY() + " sizez=" + target.getSizeZ() + ".");
                    if (inscriptionData != null) {
                        performer.getCommunicator().sendNormalServerMessage("Inscription: '" + inscriptionData.getInscription() + "'");
                        performer.getCommunicator().sendNormalServerMessage("Inscriber: '" + inscriptionData.getInscriber() + "'");
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("Inscription: NO INSCRIPTION!");
                        if (target.getAuxData() == 2) {
                            performer.getCommunicator().sendNormalServerMessage("This " + target.getName() + " has a waxy surface.");
                            return true;
                        }
                    }
                    final long timeSince = WurmCalendar.currentTime - target.getLastMaintained();
                    final String timeString = Server.getTimeFor(timeSince * 1000L);
                    performer.getCommunicator().sendNormalServerMessage("Last maintained " + timeString + " ago.");
                    String lastOwnerS = String.valueOf(target.lastOwner);
                    final PlayerInfo p = PlayerInfoFactory.getPlayerInfoWithWurmId(target.getLastOwnerId());
                    if (p != null) {
                        lastOwnerS = p.getName();
                    }
                    else {
                        try {
                            final Creature c = Creatures.getInstance().getCreature(target.lastOwner);
                            lastOwnerS = c.getName();
                        }
                        catch (NoSuchCreatureException nsc) {
                            lastOwnerS = "dead " + lastOwnerS;
                        }
                    }
                    performer.getCommunicator().sendNormalServerMessage("lastownerid=" + lastOwnerS + ", Model=" + target.getModelName());
                    if (performer.getPower() >= 5) {
                        performer.getCommunicator().sendNormalServerMessage("Zoneid=" + target.getZoneId() + " real zid=" + target.zoneId + " Counter=" + WurmId.getNumber(target.getWurmId()) + " origin=" + WurmId.getOrigin(target.getWurmId()));
                    }
                    if (target.hasData()) {
                        performer.getCommunicator().sendNormalServerMessage("data=" + target.getData() + ", data1=" + target.getData1() + " data2=" + target.getData2());
                    }
                    String creator = ", creator=" + target.creator;
                    if (target.creator == null || target.creator.length() == 0) {
                        creator = "";
                    }
                    performer.getCommunicator().sendNormalServerMessage("auxdata=" + target.getAuxData() + creator);
                    if (target.isKey()) {
                        performer.getCommunicator().sendNormalServerMessage("lock id=" + target.getLockId());
                    }
                    if (target.isLock()) {
                        final long[] keys = target.getKeyIds();
                        performer.getCommunicator().sendNormalServerMessage("Keys:");
                        for (final long lKey : keys) {
                            performer.getCommunicator().sendNormalServerMessage(String.valueOf(lKey));
                        }
                    }
                }
                return true;
            }
            if (action == 1) {
                final StringBuilder sendString = new StringBuilder(target.examine(performer));
                if (inscriptionData != null && inscriptionData.hasBeenInscribed()) {
                    if (target.getAuxData() > 8) {
                        sendString.append(" It appears to be a " + target.getName() + ", which should contain its harvest times.");
                    }
                    else {
                        sendString.append(" It appears the " + target.getName() + " has something written on it.");
                    }
                    if (target.getQualityLevel() > 20.0f && inscriptionData.getInscriber() != null) {
                        sendString.append(" The author appears to be someone named '" + inscriptionData.getInscriber() + "'.");
                    }
                }
                else {
                    sendString.append(" It is possible to inscribe on the " + target.getName() + " if you know how.");
                }
                final String s = target.getSignature();
                if (s != null && s.length() > 0) {
                    sendString.append("You can barely make out the signature of its maker,  '" + s + "'.");
                }
                performer.getCommunicator().sendNormalServerMessage(sendString.toString());
                return true;
            }
            if (action == 742) {
                return this.writeRecipe(act, performer, source, target, action, counter, inscriptionData);
            }
            if (action == 853) {
                return this.recordInfo(act, performer, source, target, action, counter, inscriptionData);
            }
            if (action == 743) {
                if (inscriptionData != null && target.getAuxData() == 1) {
                    final Recipe recipe = inscriptionData.getRecipe();
                    if (recipe != null) {
                        performer.getCommunicator().sendNormalServerMessage("You read the " + target.getName() + ".");
                        final CookBookQuestion pp2 = new CookBookQuestion(performer, (byte)13, recipe, true, target.getWurmId(), inscriptionData.getInscriber());
                        pp2.sendQuestion();
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You could not decipher the recipe.");
                    }
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("There was no recipe to read.");
                }
                return true;
            }
            if (action == 17) {
                if (inscriptionData != null && target.getAuxData() > 8) {
                    final WurmHarvestables.Harvestable harvestable = WurmHarvestables.getHarvestable(target.getAuxData() - 8);
                    if (harvestable != null) {
                        performer.getCommunicator().sendNormalServerMessage("You read the " + target.getName() + ".");
                        final ShowHarvestableInfo pp3 = new ShowHarvestableInfo(performer, target, harvestable);
                        pp3.sendQuestion();
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You could not decipher the report.");
                    }
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("There was no report to read.");
                }
                return true;
            }
            if (action == 118 && source.getTemplateId() == 1254 && target.canHaveInscription()) {
                if (inscriptionData != null && inscriptionData.hasBeenInscribed()) {
                    performer.getCommunicator().sendNormalServerMessage("This " + target.getName() + " has already been used by " + inscriptionData.getInscriber() + ".");
                    return true;
                }
                if (target.getAuxData() == 2) {
                    performer.getCommunicator().sendNormalServerMessage("This " + target.getName() + " has a waxy surface and does not need more wax.");
                    return true;
                }
                if (inscriptionData != null || target.getAuxData() != 0) {
                    return true;
                }
                if (counter == 1.0f) {
                    final int time = 50;
                    performer.getCommunicator().sendNormalServerMessage("You start to wax the " + target.getActualName() + ".");
                    Server.getInstance().broadCastAction(performer.getName() + " starts to wax something.", performer, 5);
                    performer.sendActionControl("Waxing " + target.getActualName(), true, 50);
                    performer.getStatus().modifyStamina(-400.0f);
                    act.setTimeLeft(50);
                }
                if (counter * 10.0f > act.getTimeLeft()) {
                    performer.getCommunicator().sendNormalServerMessage("You finish waxing the " + target.getActualName() + ".");
                    Server.getInstance().broadCastAction(performer.getName() + " stops waxing.", performer, 5);
                    target.setAuxData((byte)2);
                    target.updateName();
                    source.setWeight(source.getWeightGrams() - 10, true);
                    return true;
                }
                return false;
            }
            else {
                if (source == null) {
                    return super.action(act, performer, target, action, counter);
                }
                return super.action(act, performer, source, target, action, counter);
            }
        }
    }
    
    private boolean writeRecipe(final Action act, final Creature performer, @Nullable final Item source, final Item target, final short action, final float counter, final InscriptionData inscriptionData) {
        if (source == null) {
            performer.getCommunicator().sendNormalServerMessage("You fumble with the " + target.getName() + " but you cannot figure out how it works.");
            return true;
        }
        if (!target.canHaveInscription()) {
            performer.getCommunicator().sendNormalServerMessage("You cannot write a recipe on that!");
            return true;
        }
        if (inscriptionData != null && inscriptionData.hasBeenInscribed()) {
            performer.getCommunicator().sendNormalServerMessage("This " + target.getName() + " has already been used by " + inscriptionData.getInscriber() + ".");
            return true;
        }
        if (target.getAuxData() == 2) {
            performer.getCommunicator().sendNormalServerMessage("This " + target.getName() + " has a waxy surface and therefore cannot be used to write on.");
            return true;
        }
        if (target.getAuxData() > 8) {
            performer.getCommunicator().sendNormalServerMessage("This " + target.getName() + " has some study results on and therefore cannot be used again.");
            return true;
        }
        if (source.getTemplateId() == 176) {
            final WriteRecipeQuestion pp = new WriteRecipeQuestion(performer, target);
            pp.sendQuestion();
            return true;
        }
        if (!source.isContainerLiquid()) {
            performer.getCommunicator().sendNormalServerMessage("You have no container with liquid active.");
            return true;
        }
        Item liquid = null;
        Item contained = null;
        final Iterator<Item> it = source.getItems().iterator();
        while (it.hasNext()) {
            contained = it.next();
            if (!isValidColorant(contained.getTemplateId())) {
                performer.getCommunicator().sendNormalServerMessage("You cannot write a recipe with " + contained.getName() + ". You need to use something special for writing on " + target.getName() + ".");
                return true;
            }
            liquid = contained;
        }
        if (liquid == null) {
            performer.getCommunicator().sendNormalServerMessage("You need a colorant to be able to write a recipe with the " + source.getName() + " on the " + target.getName() + ".");
            return true;
        }
        final Recipe recipe = ((Player)performer).getViewingRecipe();
        if (recipe == null) {
            performer.getCommunicator().sendNormalServerMessage("You need to be viewing a recipe to be able to write it.");
            return true;
        }
        if (recipe.isKnown()) {
            performer.getCommunicator().sendNormalServerMessage("Everyone knows that recipe, seems a waste of time to write it down.");
            return true;
        }
        if (recipe.isLootable()) {
            performer.getCommunicator().sendNormalServerMessage("You cannot bring yourself to write that recipe down.");
            return true;
        }
        if (counter == 1.0f) {
            ((Player)performer).setIsWritingRecipe(true);
            final int time = 50;
            performer.getCommunicator().sendNormalServerMessage("You start to write the recipe for \"" + recipe.getName() + "\" on the " + target.getActualName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " starts to write a recipe down.", performer, 5);
            performer.sendActionControl("Writing " + recipe.getName(), true, 50);
            performer.getStatus().modifyStamina(-400.0f);
            act.setTimeLeft(50);
        }
        if (counter * 10.0f > act.getTimeLeft()) {
            target.setInscription(recipe, performer.getName(), liquid.getColor());
            performer.getCommunicator().sendNormalServerMessage("You carefully finish writing the recipe \"" + recipe.getName() + "\" and sign it.");
            Server.getInstance().broadCastAction(performer.getName() + " stops writing.", performer, 5);
            if (liquid != null) {
                liquid.setWeight(liquid.getWeightGrams() - 10, true);
            }
            ((Player)performer).setIsWritingRecipe(false);
            return true;
        }
        return false;
    }
    
    private boolean recordInfo(final Action act, final Creature performer, @Nullable final Item source, final Item target, final short action, final float counter, final InscriptionData inscriptionData) {
        final Player player = (Player)performer;
        if (source == null) {
            performer.getCommunicator().sendNormalServerMessage("You fumble with the " + target.getName() + " but you cannot figure out how it works.");
            return true;
        }
        if (!target.canHaveInscription()) {
            performer.getCommunicator().sendNormalServerMessage("You cannot write a recipe on that!");
            return true;
        }
        if (inscriptionData != null && inscriptionData.hasBeenInscribed()) {
            performer.getCommunicator().sendNormalServerMessage("This " + target.getName() + " has already been used by " + inscriptionData.getInscriber() + ".");
            return true;
        }
        if (target.getAuxData() == 2) {
            performer.getCommunicator().sendNormalServerMessage("This " + target.getName() + " has a waxy surface and therefore cannot be used to write on.");
            return true;
        }
        if (target.getAuxData() > 8) {
            performer.getCommunicator().sendNormalServerMessage("This " + target.getName() + " already has some study results on and therefore cannot be used again.");
            return true;
        }
        final int studied = player.getStudied();
        if (studied == 0) {
            if (source.getTemplateId() == 176) {
                final GMSelectHarvestable pp = new GMSelectHarvestable(performer, target);
                pp.sendQuestion();
                return true;
            }
            performer.getCommunicator().sendNormalServerMessage("You need to have just studied something to be able to record the results.");
            return true;
        }
        else {
            if (!source.isContainerLiquid()) {
                performer.getCommunicator().sendNormalServerMessage("You have no container with liquid active.");
                return true;
            }
            Item liquid = null;
            Item contained = null;
            final Iterator<Item> it = source.getItems().iterator();
            while (it.hasNext()) {
                contained = it.next();
                if (!isValidColorant(contained.getTemplateId())) {
                    performer.getCommunicator().sendNormalServerMessage("You cannot write a study report with " + contained.getName() + ". You need to use something special for writing on " + target.getName() + ".");
                    return true;
                }
                liquid = contained;
            }
            if (liquid == null) {
                performer.getCommunicator().sendNormalServerMessage("You need a colorant to be able to write a recipe with the " + source.getName() + " on the " + target.getName() + ".");
                return true;
            }
            final WurmHarvestables.Harvestable harvestable = WurmHarvestables.getHarvestable(player.getStudied());
            if (harvestable == null) {
                performer.getCommunicator().sendNormalServerMessage("You need to have studied something to be able to write a report about it.");
                return true;
            }
            if (counter == 1.0f) {
                final int time = 50;
                performer.getCommunicator().sendNormalServerMessage("You start to write a report for \"" + harvestable.getName() + "\" on the " + target.getActualName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " starts to write a report.", performer, 5);
                performer.sendActionControl("Writing " + harvestable.getName(), true, 50);
                performer.getStatus().modifyStamina(-400.0f);
                act.setTimeLeft(50);
            }
            if (counter * 10.0f > act.getTimeLeft()) {
                final Skill forestry = performer.getSkills().getSkillOrLearn(10048);
                final float skillMultiplier = Math.min(Math.max(1.0f, counter / 3.0f), 20.0f);
                float alc = 0.0f;
                if (performer.isPlayer()) {
                    alc = ((Player)performer).getAlcohol();
                }
                final int diff = harvestable.getReportDifficulty();
                final float power = (float)forestry.skillCheck(diff + alc, target, 0.0, false, skillMultiplier);
                final float knowledge = (float)forestry.getKnowledge(0.0);
                final float modify = (100.0f - knowledge) * power / 1000.0f - alc / 10.0f;
                final float newQL = Math.min(100.0f, Math.max(1.0f, knowledge + modify));
                performer.getCommunicator().sendNormalServerMessage("You carefully finish writing the report about \"" + harvestable.getName() + "\" and sign it.");
                Server.getInstance().broadCastAction(performer.getName() + " stops writing.", performer, 5);
                player.setStudied(0);
                target.setAuxData((byte)(studied + 8));
                target.setOriginalQualityLevel(newQL);
                target.setQualityLevel(newQL);
                target.setDamage(0.0f);
                target.setInscription(harvestable.getName() + " report", player.getName(), liquid.getColor());
                target.setName(harvestable.getName() + " report", true);
                if (liquid != null) {
                    liquid.setWeight(liquid.getWeightGrams() - 10, true);
                }
                return true;
            }
            return false;
        }
    }
    
    private static boolean isValidColorant(final int templateId) {
        switch (templateId) {
            case 431:
            case 432:
            case 433:
            case 434:
            case 435:
            case 438:
            case 753: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private static short getNumCharBonusForInk(final int templateId) {
        switch (templateId) {
            case 753: {
                return 200;
            }
            case 431: {
                return 200;
            }
            case 432:
            case 433:
            case 434:
            case 435: {
                return 20;
            }
            case 438: {
                return 20;
            }
            default: {
                return 0;
            }
        }
    }
    
    static List<ActionEntry> getPapyrusBehavioursFor(final Creature performer, final Item source) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        if (source.canHaveInscription() && source.getParentId() != -10L) {
            final InscriptionData inscriptionData = source.getInscription();
            if (inscriptionData != null && source.getAuxData() == 1) {
                final Recipe recipe = inscriptionData.getRecipe();
                if (recipe != null) {
                    toReturn.add(new ActionEntry((short)744, "Add \"" + recipe.getName() + "\" to cookbook", "adding recipe"));
                }
            }
        }
        return toReturn;
    }
    
    static boolean addToCookbook(final Action act, final Creature performer, final Item source, final Item target, final short action, final float counter) {
        final InscriptionData inscriptionData = source.getInscription();
        if (inscriptionData != null && source.getAuxData() == 1) {
            final Recipe recipe = inscriptionData.getRecipe();
            if (recipe != null) {
                if (RecipesByPlayer.isKnownRecipe(performer.getWurmId(), recipe.getRecipeId())) {
                    performer.getCommunicator().sendNormalServerMessage("As you look for a place to add the recipe in your cookbook, you notice that recipe already exists in your cookbook, therefore decide to stop!");
                    return true;
                }
                if (counter == 1.0f) {
                    final int time = 50;
                    performer.getCommunicator().sendNormalServerMessage("You start to add the recipe " + source.getName() + " into your cookbook.");
                    Server.getInstance().broadCastAction(performer.getName() + " starts to write a recipe down.", performer, 5);
                    performer.sendActionControl("Writing " + recipe.getName(), true, 50);
                    performer.getStatus().modifyStamina(-400.0f);
                    act.setTimeLeft(50);
                }
                if (counter * 10.0f > act.getTimeLeft()) {
                    if (RecipesByPlayer.addRecipe(performer, recipe)) {
                        performer.getCommunicator().sendNormalServerMessage("You finish adding the " + source.getName() + " into your cookbook, just in time, as the recipe has decayed away.");
                        Server.getInstance().broadCastAction(performer.getName() + " stops writing.", performer, 5);
                        Items.destroyItem(source.getWurmId());
                    }
                    return true;
                }
                return false;
            }
        }
        performer.getCommunicator().sendNormalServerMessage("That " + source.getName() + " does not have a recipe on it");
        return true;
    }
    
    static {
        logger = Logger.getLogger(PapyrusBehaviour.class.getName());
    }
}
