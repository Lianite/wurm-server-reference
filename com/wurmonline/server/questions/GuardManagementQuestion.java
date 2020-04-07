// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.economy.Change;
import com.wurmonline.server.villages.GuardPlan;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.Servers;
import com.wurmonline.server.economy.Economy;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.economy.MonetaryConstants;
import com.wurmonline.server.TimeConstants;

public final class GuardManagementQuestion extends Question implements TimeConstants, MonetaryConstants
{
    public GuardManagementQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 9, aTarget);
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        QuestionParser.parseGuardRentalQuestion(this);
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder(this.getBmlHeader());
        if (this.getResponder().getCitizenVillage() != null) {
            if (this.getResponder().getCitizenVillage().plan != null) {
                final GuardPlan plan = this.getResponder().getCitizenVillage().plan;
                if (this.getResponder().getCitizenVillage().isCitizen(this.getResponder())) {
                    buf.append("text{text=\"The size of " + this.getResponder().getCitizenVillage().getName() + " is " + this.getResponder().getCitizenVillage().getDiameterX() + " by " + this.getResponder().getCitizenVillage().getDiameterY() + ".\"}");
                    buf.append("text{text=\"The perimeter is " + (5 + this.getResponder().getCitizenVillage().getPerimeterSize()) + " and it has " + plan.getNumHiredGuards() + " guards hired.\"}");
                }
                buf.append("text{text=\"\"}");
                if (this.getResponder().getCitizenVillage().isPermanent) {
                    buf.append("text{text='This village is permanent, and should never run out of money or be drained.'}");
                }
                else {
                    final Change c = Economy.getEconomy().getChangeFor(plan.moneyLeft);
                    buf.append("text{text='The settlement has " + c.getChangeString() + " left in its coffers.'}");
                    final Change upkeep = Economy.getEconomy().getChangeFor(plan.getMonthlyCost());
                    buf.append("text{text='Upkeep per month is " + upkeep.getChangeString() + ".'}");
                    final float left = plan.moneyLeft / plan.getMonthlyCost();
                    buf.append("text{text=\"This means that the upkeep should last for about " + left * 28.0f + " days.\"}");
                    if (Servers.localServer.PVPSERVER || Servers.localServer.id == 3) {
                        buf.append("text{text=\"A drain would cost " + Economy.getEconomy().getChangeFor(plan.getMoneyDrained()).getChangeString() + ".\"};");
                        if (plan.moneyLeft < 30000L) {
                            buf.append("text{type='bold';text='Since minimum drain is 75 copper it may be drained to disband in less than 5 days.'}");
                        }
                    }
                    buf.append("text{text=\"\"}");
                }
                if (Servers.localServer.isChallengeOrEpicServer()) {
                    buf.append("text{text=\"The only guard type is heavy guards. The running upkeep cost increases the more guards you have in a sort of ladder system. The first guards are cheaper than the last.\"};");
                    buf.append("text{text=\"Make sure to review the cost for upkeep once you are done.\"};");
                }
                else {
                    buf.append("text{text=\"The only guard type is heavy guards. The cost for hiring them is " + Villages.GUARD_COST_STRING + " and running upkeep is " + Villages.GUARD_UPKEEP_STRING + " per month.\"};");
                    buf.append("text{text=\"\"};");
                }
                buf.append("text{text=\"The cost for hiring the guards is a one-time summoning fee that is not returned in case you decide to lower the amount of guards.\"};");
                buf.append("text{text=\"\"}");
                if (Servers.localServer.PVPSERVER) {
                    buf.append("label{text='Note that you will need at least 1 guard to enforce the role rules on deed!'}");
                    buf.append("text{text=\"\"}");
                }
                buf.append("text{text=\"How many guards do you wish to have? You currently have " + plan.getNumHiredGuards() + " and may hire up to " + GuardPlan.getMaxGuards(this.getResponder().getCitizenVillage()) + ".\"};input{text=\"" + plan.getNumHiredGuards() + "\";id=\"hired\"}; ");
                buf.append("text{text=\"\"};");
            }
            buf.append(this.createAnswerButton2());
            this.getResponder().getCommunicator().sendBml(400, 400, true, true, buf.toString(), 200, 200, 200, this.title);
        }
    }
}
