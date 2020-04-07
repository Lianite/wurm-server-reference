// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.questions.MailSendQuestion;
import java.util.Set;
import com.wurmonline.server.structures.BlockingResult;
import com.wurmonline.server.questions.MailReceiveQuestion;
import com.wurmonline.server.items.WurmMail;
import com.wurmonline.server.structures.Blocking;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.economy.MonetaryConstants;

final class WurmMailSender implements MonetaryConstants, MiscConstants
{
    static final void checkForWurmMail(final Creature performer, final Item mailbox) {
        if (mailbox.getOwnerId() == -10L) {
            if (performer.isWithinDistanceTo(mailbox.getPosX(), mailbox.getPosY(), mailbox.getPosZ(), 4.0f)) {
                final BlockingResult result = Blocking.getBlockerBetween(performer, mailbox, 4);
                if (result == null) {
                    if (mailbox.getSpellCourierBonus() > 0.0f) {
                        if (mailbox.hasDarkMessenger() || mailbox.hasCourier()) {
                            final Set<WurmMail> set = WurmMail.getSentMailsFor(performer.getWurmId(), 100);
                            if (!set.isEmpty()) {
                                new MailReceiveQuestion(performer, "Retrieving mail", "Which items do you wish to retrieve?", mailbox).sendQuestion();
                            }
                            else {
                                performer.getCommunicator().sendNormalServerMessage("You have no mail.");
                            }
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("The entities inside refuse to help you.");
                        }
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You have heard rumours that the mailbox will need some kind of enchantment to work.");
                    }
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You can't reach the " + mailbox.getName() + " now.");
                }
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You are too far away to do that now.");
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("The mailbox must be planted on the ground.");
        }
    }
    
    static final void sendWurmMail(final Creature performer, final Item mailbox) {
        if (mailbox.getOwnerId() == -10L) {
            if (performer.isWithinDistanceTo(mailbox.getPosX(), mailbox.getPosY(), mailbox.getPosZ(), 4.0f)) {
                final BlockingResult result = Blocking.getBlockerBetween(performer, mailbox, 4);
                if (result == null) {
                    if (mailbox.getSpellCourierBonus() > 0.0f) {
                        if (mailbox.hasDarkMessenger() || mailbox.hasCourier()) {
                            boolean ok = true;
                            final Item[] containedItems = mailbox.getItemsAsArray();
                            if (containedItems.length != 0) {
                                for (final Item lContainedItem : containedItems) {
                                    if (lContainedItem.isNoDrop() || lContainedItem.isArtifact() || lContainedItem.isBodyPart() || lContainedItem.isTemporary() || lContainedItem.isLiquid()) {
                                        performer.getCommunicator().sendNormalServerMessage("You may not send the " + lContainedItem.getName() + ".");
                                        ok = false;
                                    }
                                    else if (lContainedItem.lastOwner != performer.getWurmId()) {
                                        performer.getCommunicator().sendNormalServerMessage("You must possess the " + lContainedItem.getName() + " in order to send it.");
                                        ok = false;
                                    }
                                }
                                if (ok) {
                                    new MailSendQuestion(performer, "Sending mail", "Calculate the cost", mailbox).sendQuestion();
                                }
                            }
                            else {
                                performer.getCommunicator().sendNormalServerMessage("The " + mailbox.getName() + " is empty.");
                            }
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("The entities inside refuse to help you.");
                        }
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You have heard rumours that the mailbox will need some kind of enchantment to work.");
                    }
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You can't reach the " + mailbox.getName() + " now.");
                }
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You are too far away to do that now.");
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("The mailbox must be planted on the ground.");
        }
    }
}
