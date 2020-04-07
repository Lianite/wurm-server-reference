// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.HashSet;
import com.wurmonline.server.players.Player;
import java.util.logging.Logger;
import java.util.Map;
import com.wurmonline.server.TimeConstants;

public final class Questions implements TimeConstants
{
    private static Map<Integer, Question> questions;
    private static Logger logger;
    
    static void addQuestion(final Question question) {
        Questions.questions.put(question.getId(), question);
        final Question lastQuestion = ((Player)question.getResponder()).getCurrentQuestion();
        if (lastQuestion != null) {
            lastQuestion.timedOut();
        }
        ((Player)question.getResponder()).setQuestion(question);
    }
    
    public static Question getQuestion(final int id) throws NoSuchQuestionException {
        final Integer iid = id;
        final Question question = Questions.questions.get(iid);
        if (question == null) {
            throw new NoSuchQuestionException(String.valueOf(id));
        }
        return question;
    }
    
    public static final int getNumUnanswered() {
        return Questions.questions.size();
    }
    
    public static void removeQuestion(final Question question) {
        if (question != null) {
            final Integer iid = question.getId();
            Questions.questions.remove(iid);
        }
    }
    
    public static void removeQuestions(final Player player) {
        final Question[] quests = Questions.questions.values().toArray(new Question[Questions.questions.values().size()]);
        for (int x = 0; x < quests.length; ++x) {
            if (quests[x].getResponder() == player) {
                quests[x].clearResponder();
                Questions.questions.remove(quests[x].getId());
            }
        }
    }
    
    public static void trimQuestions() {
        final long now = System.currentTimeMillis();
        final Set<Question> toRemove = new HashSet<Question>();
        for (final Question lQuestion : Questions.questions.values()) {
            long maxTime = 900000L;
            if (lQuestion instanceof CultQuestion) {
                maxTime = 1800000L;
            }
            if (lQuestion instanceof SpawnQuestion) {
                maxTime = 7200000L;
            }
            if (!(lQuestion instanceof SelectSpawnQuestion) && (now - lQuestion.getSendTime() > maxTime || !lQuestion.getResponder().hasLink())) {
                toRemove.add(lQuestion);
            }
        }
        for (final Question lQuestion : toRemove) {
            lQuestion.timedOut();
            removeQuestion(lQuestion);
            if (lQuestion.getResponder().isPlayer() && ((Player)lQuestion.getResponder()).question == lQuestion) {
                ((Player)lQuestion.getResponder()).question = null;
            }
        }
        if (Questions.logger.isLoggable(Level.FINER) && Questions.questions.size() > 0) {
            Questions.logger.finer("Size of question list=" + Questions.questions.size());
        }
    }
    
    static {
        Questions.questions = new HashMap<Integer, Question>();
        Questions.logger = Logger.getLogger(Questions.class.getName());
    }
}
