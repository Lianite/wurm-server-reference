// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.kingdom.Appointment;
import com.wurmonline.server.kingdom.Appointments;
import com.wurmonline.server.Servers;
import com.wurmonline.server.players.Cults;
import com.wurmonline.shared.util.StringUtilities;
import com.wurmonline.server.kingdom.King;
import java.util.Arrays;
import java.util.Comparator;
import com.wurmonline.server.players.Player;
import java.util.Properties;
import java.util.LinkedList;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.players.Titles;
import java.util.List;

public final class TitleQuestion extends Question
{
    private final List<Titles.Title> titlelist;
    
    public TitleQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 39, aTarget);
        this.titlelist = new LinkedList<Titles.Title>();
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        QuestionParser.parseTitleQuestion(this);
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder sb = new StringBuilder(this.getBmlHeader());
        final boolean isMale = ((Player)this.getResponder()).isNotFemale();
        final Titles.Title[] titles = ((Player)this.getResponder()).getTitles();
        Arrays.sort(titles, new Comparator<Titles.Title>() {
            @Override
            public int compare(final Titles.Title t1, final Titles.Title t2) {
                return t1.getName(isMale).compareTo(t2.getName(isMale));
            }
        });
        String suff = "";
        String pre = "";
        if (!this.getResponder().hasFlag(24)) {
            pre = this.getResponder().getAbilityTitle();
        }
        if (this.getResponder().getCultist() != null && !this.getResponder().hasFlag(25)) {
            suff = suff + " " + this.getResponder().getCultist().getCultistTitleShort();
        }
        final Titles.Title lTempTitle = this.getResponder().getTitle();
        if (this.getResponder().isKing()) {
            suff = suff + " [" + King.getRulerTitle(this.getResponder().getSex() == 0, this.getResponder().getKingdomId()) + "]";
        }
        if (lTempTitle != null) {
            if (lTempTitle.isRoyalTitle()) {
                if (this.getResponder().getAppointments() != 0L || this.getResponder().isAppointed()) {
                    suff = suff + " [" + this.getResponder().getKingdomTitle() + "]";
                }
            }
            else {
                suff = suff + " [" + lTempTitle.getName(this.getResponder().isNotFemale()) + "]";
            }
        }
        if (this.getResponder().isChampion() && this.getResponder().getDeity() != null) {
            suff = suff + " [Champion of " + this.getResponder().getDeity().name + "]";
        }
        final String playerName = pre + StringUtilities.raiseFirstLetterOnly(this.getResponder().getName()) + suff;
        sb.append("text{text=\"You are currently known as: " + playerName + "\"}");
        sb.append("text{text=\"\"}");
        final Titles.Title currentTitle = ((Player)this.getResponder()).getTitle();
        int totalTitles = 0;
        if (titles.length == 0) {
            if (this.getResponder().getAppointments() != 0L || this.getResponder().isAppointed()) {
                int defaultTitle = 0;
                sb.append("harray{text{text=\"Title: \"};dropdown{id=\"TITLE\";options=\"None");
                sb.append(",");
                sb.append(Titles.Title.Kingdomtitle.getName(this.getResponder().isNotFemale()));
                this.titlelist.add(Titles.Title.Kingdomtitle);
                if (currentTitle != null && currentTitle.isRoyalTitle()) {
                    defaultTitle = 1;
                }
                sb.append("\";default=\"" + defaultTitle + "\"}}");
                ++totalTitles;
            }
            else {
                sb.append("text{text=\"You have no titles to select from.\"}");
            }
        }
        else {
            int defaultTitle = 0;
            sb.append("harray{text{text=\"Title: \"};dropdown{id=\"TITLE\";options=\"None");
            for (int x = 0; x < titles.length; ++x) {
                sb.append(",");
                sb.append(titles[x].getName(this.getResponder().isNotFemale()));
                if (currentTitle != null && titles[x].id == currentTitle.id) {
                    defaultTitle = x + 1;
                }
                this.titlelist.add(titles[x]);
                ++totalTitles;
            }
            if (this.getResponder().getAppointments() != 0L || this.getResponder().isAppointed()) {
                sb.append(",");
                sb.append(Titles.Title.Kingdomtitle.getName(this.getResponder().isNotFemale()));
                this.titlelist.add(Titles.Title.Kingdomtitle);
                if (currentTitle != null && currentTitle.isRoyalTitle()) {
                    defaultTitle = titles.length + 1;
                }
                ++totalTitles;
            }
            sb.append("\";default=\"" + defaultTitle + "\"}}");
            sb.append("text{text=\"\"}");
            sb.append("text{text=\"You have a total of " + totalTitles + " titles.\"}");
            sb.append("text{text=\"\"}");
            sb.append("text{type=\"italic\";text=\"Note: Armour smiths that use their title gets faster armour improvement rate.\"}");
        }
        final String occultist = this.getResponder().getAbilityTitle();
        final String meditation = (this.getResponder().getCultist() != null) ? Cults.getNameForLevel(this.getResponder().getCultist().getPath(), this.getResponder().getCultist().getLevel()) : "";
        if (occultist.length() > 0 || meditation.length() > 0) {
            sb.append("text{type=\"bold\";text=\"Select which titles to hide (if any)\"}");
            if (occultist.length() > 0) {
                sb.append("checkbox{id=\"hideoccultist\";text=\"" + occultist + "(Occultist)\";selected=\"" + this.getResponder().hasFlag(24) + "\"}");
            }
            if (meditation.length() > 0) {
                sb.append("checkbox{id=\"hidemeditation\";text=\"" + meditation + " (Meditation)\";selected=\"" + this.getResponder().hasFlag(25) + "\"}");
            }
            sb.append("text{text=\"\"}");
        }
        if (Servers.isThisAPvpServer()) {
            final King king = King.getKing(this.getResponder().getKingdomId());
            if (king != null && (this.getResponder().getAppointments() != 0L || this.getResponder().isAppointed())) {
                sb.append("text{type=\"bold\";text=\"Select which kingdom office to remove (if any)\"}");
                final Appointments a = Appointments.getAppointments(king.era);
                for (int x2 = 0; x2 < a.officials.length; ++x2) {
                    final int oId = x2 + 1500;
                    final Appointment o = a.getAppointment(oId);
                    if (a.officials[x2] == this.getResponder().getWurmId()) {
                        sb.append("checkbox{id=\"office" + oId + "\";text=\"" + o.getNameForGender(this.getResponder().getSex()) + " (Office)\";}");
                    }
                }
            }
        }
        sb.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(500, 300, true, true, sb.toString(), 200, 200, 200, this.title);
    }
    
    Titles.Title getTitle(final int aPosition) {
        return this.titlelist.get(aPosition);
    }
}
