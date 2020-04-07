// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.ServerEntry;
import java.util.Iterator;
import com.wurmonline.server.VoteServer;
import com.wurmonline.server.support.Tickets;
import com.wurmonline.server.support.VoteQuestion;
import com.wurmonline.server.webinterface.WcVoting;
import com.wurmonline.server.players.PlayerVotes;
import com.wurmonline.server.players.PlayerVote;
import com.wurmonline.server.Servers;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.support.VoteQuestions;
import java.util.logging.Level;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public class InGameVoteQuestion extends Question
{
    private static final Logger logger;
    private static final byte SHOWLIST = 0;
    private static final byte EDITQUESTION = 2;
    private static final byte VIEWQUESTION = 3;
    private static final byte SHOWRESULTS = 4;
    private byte part;
    private int questionId;
    
    public InGameVoteQuestion(final Creature aResponder) {
        this(aResponder, "In game voting", "", (byte)0);
    }
    
    public InGameVoteQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final byte aPart) {
        this(aResponder, aTitle, aQuestion, aPart, 0);
    }
    
    public InGameVoteQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final byte aPart, final int aQuestionId) {
        super(aResponder, aTitle, aQuestion, 107, aResponder.getWurmId());
        this.part = 0;
        this.questionId = 0;
        this.part = aPart;
        this.questionId = aQuestionId;
    }
    
    @Override
    public void answer(final Properties aAnswer) {
        this.setAnswer(aAnswer);
        if (this.type == 0) {
            InGameVoteQuestion.logger.log(Level.INFO, "Received answer for a question with NOQUESTION.");
            return;
        }
        if (this.type == 107) {
            final boolean hasPower = this.getResponder().getPower() >= 2;
            switch (this.part) {
                case 0: {
                    final String strId = aAnswer.getProperty("qid");
                    if (strId == null) {
                        return;
                    }
                    this.questionId = Integer.parseInt(strId);
                    if (this.questionId == -1) {
                        break;
                    }
                    byte nextPart = 0;
                    final VoteQuestion vq = VoteQuestions.getVoteQuestion(this.questionId);
                    final Player p = (Player)this.getResponder();
                    String windowTitle;
                    String header;
                    if ((hasPower && vq.isActive()) || vq.canVote(p)) {
                        if (hasPower || p.hasVoted(this.questionId)) {
                            nextPart = 3;
                            windowTitle = "View Question";
                            header = vq.getQuestionTitle();
                        }
                        else {
                            nextPart = 2;
                            windowTitle = "Vote";
                            header = vq.getQuestionTitle();
                        }
                    }
                    else {
                        nextPart = 4;
                        windowTitle = "Vote Results";
                        header = vq.getQuestionTitle();
                    }
                    final InGameVoteQuestion igvsq = new InGameVoteQuestion(this.getResponder(), windowTitle, header, nextPart, this.questionId);
                    igvsq.sendQuestion();
                    break;
                }
                case 2: {
                    boolean opt1 = false;
                    boolean opt2 = false;
                    boolean opt3 = false;
                    boolean opt4 = false;
                    final VoteQuestion vq2 = VoteQuestions.getVoteQuestion(this.questionId);
                    if (vq2.isAllowMultiple()) {
                        final String sopt1 = aAnswer.getProperty("opt1");
                        final String sopt2 = aAnswer.getProperty("opt2");
                        final String sopt3 = aAnswer.getProperty("opt3");
                        final String sopt4 = aAnswer.getProperty("opt4");
                        opt1 = Boolean.parseBoolean(sopt1);
                        opt2 = Boolean.parseBoolean(sopt2);
                        opt3 = Boolean.parseBoolean(sopt3);
                        opt4 = Boolean.parseBoolean(sopt4);
                    }
                    else {
                        final String sopts = aAnswer.getProperty("opts");
                        final int ans = Integer.parseInt(sopts);
                        switch (ans) {
                            case 1: {
                                opt1 = true;
                                break;
                            }
                            case 2: {
                                opt2 = true;
                                break;
                            }
                            case 3: {
                                opt3 = true;
                                break;
                            }
                            case 4: {
                                opt4 = true;
                                break;
                            }
                            default: {
                                return;
                            }
                        }
                    }
                    if (!opt1 && !opt2 && !opt3 && !opt4) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("You did not select anything.");
                        break;
                    }
                    if (!Servers.isThisLoginServer() && !this.isLoginAvailable()) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("Login server is currently down, so your vote cannot be registered, please try again later.");
                        break;
                    }
                    final PlayerVote pv = PlayerVotes.addPlayerVote(new PlayerVote(this.getResponder().getWurmId(), this.questionId, opt1, opt2, opt3, opt4), true);
                    ((Player)this.getResponder()).addPlayerVote(pv);
                    if (!Servers.isThisLoginServer()) {
                        final WcVoting wv = new WcVoting(pv);
                        wv.sendToLoginServer();
                    }
                    this.getResponder().getCommunicator().sendNormalServerMessage("Your vote has been registered.");
                    break;
                }
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        int width = 350;
        int height = 500;
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        final boolean hasPower = this.getResponder().getPower() >= 2;
        try {
            String disabled = "";
            switch (this.part) {
                case 0: {
                    int cols = 5;
                    VoteQuestion[] vqs;
                    if (hasPower) {
                        vqs = VoteQuestions.getVoteQuestions();
                        cols = 4;
                    }
                    else {
                        vqs = VoteQuestions.getVoteQuestions((Player)this.getResponder());
                    }
                    if (vqs.length > 0) {
                        buf.append("text{text=\"Select the questions to view.\"};");
                        buf.append("table{rows=\"" + (vqs.length + 2) + "\";cols=\"5\";label{text=\"\"};label{type=\"bold\";text=\"Question\"};label{type=\"bold\";text=\"Vote Start\"};label{type=\"bold\";text=\"Vote End\"};label{type=\"bold\";text=\"" + ((cols == 5) ? "Voted?" : "") + "\"};");
                        for (final VoteQuestion vq : vqs) {
                            final boolean voted = ((Player)this.getResponder()).hasVoted(vq.getQuestionId());
                            String colour = "";
                            if (vq.getSent() == 2) {
                                if (voted) {
                                    colour = "color=\"255,255,100\";";
                                }
                                else {
                                    colour = "color=\"100,255,100\";";
                                }
                            }
                            else if (vq.hasSummary()) {
                                colour = "color=\"100,100,255\";";
                            }
                            final String strVoted = voted ? "Yes" : "No";
                            buf.append(((colour.length() == 0) ? "label{text=\"\"}" : ("radio{group=\"qid\";id=\"" + vq.getQuestionId() + "\"};")) + "label{" + colour + "text=\"" + vq.getQuestionTitle() + "\"};label{" + colour + "text=\"" + Tickets.convertTime(vq.getVoteStart()) + "\"};label{" + colour + "text=\"" + Tickets.convertTime(vq.getVoteEnd() - 1000L) + "\"};label{" + colour + "text=\"" + ((cols == 5) ? strVoted : "") + "\"};");
                        }
                        buf.append("radio{group=\"qid\";id=\"-1\";selected=\"true\";hidden=\"true\"};label{text=\"\"};label{text=\"\"};label{text=\"\"};label{text=\"\"};");
                        buf.append("}");
                    }
                    else {
                        buf.append("text{type=\"bold\";text=\"No polls available at the moment.\"};");
                    }
                    width = 350;
                    height = 140 + vqs.length * 16;
                    break;
                }
                case 3: {
                    disabled = ";enabled=\"false\"";
                }
                case 2: {
                    width = 300;
                    height = 200;
                    final VoteQuestion vq2 = VoteQuestions.getVoteQuestion(this.questionId);
                    final PlayerVote pv = PlayerVotes.getPlayerVoteByQuestion(this.getResponder().getWurmId(), this.questionId);
                    if (this.part == 3 && !hasPower) {
                        if (vq2.hasEnded()) {
                            buf.append("label{type=\"bolditalic\";color=\"255,100,100\";text=\"Voting has ended so cannot amend\"}");
                        }
                        else {
                            buf.append("label{type=\"bolditalic\";color=\"255,255,100\";text=\"Already voted and therefore cannot amend\"}");
                        }
                    }
                    String optType = "";
                    buf.append("text{color=\"255,150,0\";type=\"bold\";text=\"" + vq2.getQuestionText() + "\"};");
                    if (vq2.isAllowMultiple()) {
                        optType = "checkbox{id=\"opt";
                    }
                    else {
                        optType = "radio{group=\"opts\";id=\"";
                        buf.append(optType + "0\";text=\"" + vq2.getOption1Text() + "\";selected=\"true\";hidden=\"true\"}");
                    }
                    String selected = "";
                    selected = ((pv != null && pv.getOption1()) ? ";selected=\"true\"" : "");
                    buf.append(optType + "1\";text=\"" + vq2.getOption1Text() + "\"" + selected + disabled + "}");
                    selected = ((pv != null && pv.getOption2()) ? ";selected=\"true\"" : "");
                    buf.append(optType + "2\";text=\"" + vq2.getOption2Text() + "\"" + selected + disabled + "}");
                    if (vq2.getOption3Text().length() > 0) {
                        selected = ((pv != null && pv.getOption3()) ? ";selected=\"true\"" : "");
                        buf.append(optType + "3\";text=\"" + vq2.getOption3Text() + "\"" + selected + disabled + "}");
                        height += 16;
                    }
                    if (vq2.getOption4Text().length() > 0) {
                        selected = ((pv != null && pv.getOption4()) ? ";selected=\"true\"" : "");
                        buf.append(optType + "4\";text=\"" + vq2.getOption4Text() + "\"" + selected + disabled + "}");
                        height += 16;
                    }
                    if (hasPower) {
                        if (vq2.isPremOnly()) {
                            buf.append("label{text=\"Prem-Only can vote.\"}");
                        }
                        else {
                            buf.append("label{text=\"Everyone can vote.\"}");
                        }
                        if (vq2.isAllowMultiple()) {
                            buf.append("label{text=\"Multiple selections allowed.\"}");
                        }
                        else {
                            buf.append("label{text=\"Single selection only.\"}");
                        }
                        buf.append("label{text=\"Kingdoms allowed:");
                        int len = 0;
                        int cnt = 1;
                        if (vq2.isJK()) {
                            ++len;
                        }
                        if (vq2.isMR()) {
                            ++len;
                        }
                        if (vq2.isHots()) {
                            ++len;
                        }
                        if (vq2.isFreedom()) {
                            ++len;
                        }
                        String comma = "";
                        if (vq2.isJK()) {
                            buf.append(comma + "JK");
                            if (++cnt < len) {
                                comma = ", ";
                            }
                            else {
                                comma = " and ";
                            }
                        }
                        if (vq2.isMR()) {
                            buf.append(comma + "MR");
                            if (++cnt < len) {
                                comma = ", ";
                            }
                            else {
                                comma = " and ";
                            }
                        }
                        if (vq2.isHots()) {
                            buf.append(comma + "Hots");
                            if (++cnt < len) {
                                comma = ", ";
                            }
                            else {
                                comma = " and ";
                            }
                        }
                        if (vq2.isFreedom()) {
                            buf.append(comma + "Freedom");
                        }
                        buf.append("\"}");
                        if (vq2.getServers().isEmpty()) {
                            buf.append("label{text=\"List of Servers is only available on Login Server.\"}");
                        }
                        else {
                            buf.append("label{text=\"Servers:");
                            len = vq2.getServers().size();
                            cnt = 1;
                            comma = "";
                            for (final VoteServer vs : vq2.getServers()) {
                                final ServerEntry se = Servers.getServerWithId(vs.getServerId());
                                buf.append(comma + se.getAbbreviation());
                                if (++cnt < len) {
                                    comma = ", ";
                                }
                                else {
                                    comma = " and ";
                                }
                            }
                            buf.append("\"}");
                        }
                        height += 32;
                    }
                    else if (vq2.isAllowMultiple()) {
                        buf.append("label{color=\"0,255,255\";type=\"italic\";text=\"Multiple selections are allowed.\"}");
                    }
                    else {
                        buf.append("label{color=\"0,255,255\";type=\"italic\";text=\"Only one selection is allowed.\"}");
                    }
                    buf.append("text{type=\"bold\";text=\"Note: Votes will be used as guidelines to assess player sentiment. There is no guarantee that any majority vote will result in the implementation or removal of anything.\"}");
                    height += 32;
                    break;
                }
                case 4: {
                    final VoteQuestion vq3 = VoteQuestions.getVoteQuestion(this.questionId);
                    int rows = 3;
                    if (vq3.getOption3Text().length() > 0) {
                        ++rows;
                    }
                    if (vq3.getOption4Text().length() > 0) {
                        ++rows;
                    }
                    buf.append("table{rows=\"" + rows + "\";cols=\"2\";");
                    buf.append("label{text=\"" + vq3.getOption1Text() + "\"}label{text=\"" + vq3.getOption1Count() + "\"}");
                    buf.append("label{text=\"" + vq3.getOption2Text() + "\"}label{text=\"" + vq3.getOption2Count() + "\"}");
                    if (vq3.getOption3Text().length() > 0) {
                        buf.append("label{text=\"" + vq3.getOption3Text() + "\"}label{text=\"" + vq3.getOption3Count() + "\"}");
                    }
                    if (vq3.getOption4Text().length() > 0) {
                        buf.append("label{text=\"" + vq3.getOption4Text() + "\"}label{text=\"" + vq3.getOption4Count() + "\"}");
                    }
                    buf.append("label{text=\"Total Players who voted\"}label{text=\"" + vq3.getVoteCount() + "\"}");
                    buf.append("}");
                    buf.append("text{text=\"\"}");
                    break;
                }
            }
        }
        catch (Exception e) {
            InGameVoteQuestion.logger.log(Level.WARNING, e.getMessage(), e);
            this.getResponder().getCommunicator().sendNormalServerMessage("Exception:" + e.getMessage());
        }
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(width, height, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    private boolean isLoginAvailable() {
        return Servers.isThisLoginServer() || Servers.loginServer.isAvailable(5, true);
    }
    
    static {
        logger = Logger.getLogger(InGameVoteQuestion.class.getName());
    }
}
