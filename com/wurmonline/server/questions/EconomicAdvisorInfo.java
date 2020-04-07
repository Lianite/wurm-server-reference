// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.economy.Shop;
import com.wurmonline.server.economy.Change;
import com.wurmonline.server.economy.Economy;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.kingdom.Appointment;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;

public final class EconomicAdvisorInfo extends Question
{
    public EconomicAdvisorInfo(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 74, aTarget);
    }
    
    @Override
    public void answer(final Properties aAnswers) {
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getBmlHeader());
        final Appointment a = Appointment.getAppointment(1505, this.getResponder().getKingdomId());
        String nam = "Economic advisor";
        if (a != null) {
            nam = a.getNameForGender(this.getResponder().getSex());
        }
        sb.append("text{type='italic';text=\"" + a + " confidential information.\"}");
        sb.append("text{text=\"  Economic statement for " + Kingdoms.getNameFor(this.getResponder().getKingdomId()) + ".\"}");
        long sum = 0L;
        final StringBuilder sb2 = new StringBuilder();
        final Creature[] crets = Creatures.getInstance().getCreatures();
        for (int x = 0; x < crets.length; ++x) {
            if (crets[x].isTrader() && crets[x].getKingdomId() == this.getResponder().getKingdomId() && crets[x].isNpcTrader()) {
                final Shop shop = Economy.getEconomy().getShop(crets[x]);
                if (!shop.isPersonal()) {
                    if (shop.getMoney() >= 0L) {
                        sb2.append("text{text=\"  Trader - " + new Change(shop.getMoney()).getChangeShortString() + ". Ratio=" + shop.getSellRatio() + "\"}");
                    }
                    else {
                        sb2.append("text{text=\"  Trader - " + shop.getMoney() + " irons. Ratio=" + shop.getSellRatio() + "\"}");
                    }
                    sum += shop.getMoney();
                }
            }
        }
        final Shop kingshop = Economy.getEconomy().getKingsShop();
        sb.append("text{text=\"  Kings coffers: " + new Change(kingshop.getMoney()).getChangeString() + " (" + kingshop.getMoney() + " irons).\"}");
        sb.append("text{text=\"  Total money at traders: " + new Change(sum).getChangeString() + ".\"}");
        sb.append("text{text=\"\"}");
        sb.append("text{type='bold';text=\"Trader breakdown:\"}");
        sb.append(sb2.toString());
        sb.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(300, 300, true, true, sb.toString(), 200, 200, 200, this.title);
    }
}
