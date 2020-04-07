// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.Arrays;
import com.wurmonline.server.tutorial.MissionPerformer;
import com.wurmonline.server.tutorial.Mission;
import java.util.logging.Level;
import com.wurmonline.server.tutorial.MissionPerformed;
import com.wurmonline.server.tutorial.Missions;
import java.util.Properties;
import java.util.HashMap;
import com.wurmonline.server.creatures.Creature;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;

public class MissionStats extends Question implements TimeConstants
{
    private static final Logger logger;
    private final int targetMission;
    private MissionManager root;
    private final Map<Float, Integer> perfstats;
    private static final String red = "color=\"255,127,127\"";
    private static final String green = "color=\"127,255,127\"";
    
    public MissionStats(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 93, aTarget);
        this.root = null;
        this.perfstats = new HashMap<Float, Integer>();
        this.targetMission = (int)aTarget;
    }
    
    @Override
    public void answer(final Properties aAnswers) {
        this.setAnswer(aAnswers);
        final boolean update = this.getBooleanProp("update");
        if (update) {
            final MissionStats ms = new MissionStats(this.getResponder(), this.title, this.question, this.targetMission);
            ms.setRoot(this.root);
            ms.sendQuestion();
            return;
        }
        if (this.root != null) {
            this.root.reshow();
        }
    }
    
    @Override
    public void sendQuestion() {
        try {
            final Mission m = Missions.getMissionWithId(this.targetMission);
            final MissionPerformer[] mps = MissionPerformed.getAllPerformers();
            final StringBuilder buf = new StringBuilder(this.getBmlHeader());
            buf.append("text{text=\"\"}");
            this.perfstats.clear();
            float total = 0.0f;
            for (int x = 0; x < mps.length; ++x) {
                final MissionPerformed mp = mps[x].getMission(this.targetMission);
                if (mp != null) {
                    final float reached = mp.getState();
                    Integer numbers = this.perfstats.get(reached);
                    if (numbers == null) {
                        numbers = 1;
                        this.perfstats.put(reached, numbers);
                    }
                    else {
                        ++numbers;
                        this.perfstats.put(reached, numbers);
                    }
                    ++total;
                }
            }
            buf.append("text{type=\"bold\";text=\"Total statistics for mission " + m.getName() + ":\"}");
            this.showStats(buf, total);
            this.perfstats.clear();
            total = 0.0f;
            for (int x = 0; x < mps.length; ++x) {
                final MissionPerformed mp = mps[x].getMission(this.targetMission);
                if (mp != null && System.currentTimeMillis() - mp.getStartTimeMillis() < 86400000L) {
                    final float reached = mp.getState();
                    Integer numbers = this.perfstats.get(reached);
                    if (numbers == null) {
                        numbers = 1;
                        this.perfstats.put(reached, numbers);
                    }
                    else {
                        ++numbers;
                        this.perfstats.put(reached, numbers);
                    }
                    ++total;
                }
            }
            buf.append("text{type=\"bold\";text=\"Statistics for mission " + m.getName() + " started within last 24 hours:\"}");
            this.showStats(buf, total);
            this.perfstats.clear();
            total = 0.0f;
            for (int x = 0; x < mps.length; ++x) {
                final MissionPerformed mp = mps[x].getMission(this.targetMission);
                if (mp != null && System.currentTimeMillis() - mp.getStartTimeMillis() < 259200000L) {
                    final float reached = mp.getState();
                    Integer numbers = this.perfstats.get(reached);
                    if (numbers == null) {
                        numbers = 1;
                        this.perfstats.put(reached, numbers);
                    }
                    else {
                        ++numbers;
                        this.perfstats.put(reached, numbers);
                    }
                    ++total;
                }
            }
            buf.append("text{type=\"bold\";text=\"Statistics for mission " + m.getName() + " started within last three days:\"}");
            this.showStats(buf, total);
            buf.append("harray{button{text=\"Refresh Statistics\";id=\"update\"};label{text=\"  \"};button{text=\"Back to mission list\";id=\"back\"};}");
            buf.append("}};null;null;}");
            this.getResponder().getCommunicator().sendBml(400, 400, true, true, buf.toString(), 200, 200, 200, this.title);
        }
        catch (Exception ex) {
            if (MissionStats.logger.isLoggable(Level.FINER)) {
                MissionStats.logger.finer("Problem sending a question about target mission: " + this.targetMission);
            }
        }
    }
    
    private void showStats(final StringBuilder buf, final float total) {
        buf.append("table{rows=\"1\"; cols=\"3\";label{text=\"Percent complete\"};label{text=\"People reached\"};label{text=\"Percent of total\"}");
        final Float[] farr = this.perfstats.keySet().toArray(new Float[this.perfstats.size()]);
        Arrays.sort(farr);
        for (final Float f : farr) {
            String perc = f + "";
            String colour = "";
            if (f == -1.0) {
                perc = "Failed (-1.0)";
                colour = "color=\"255,127,127\"";
            }
            else if (f == 100.0) {
                perc = "Completed (100.0)";
                colour = "color=\"127,255,127\"";
            }
            buf.append("label{" + colour + "text=\"" + perc + "\"};");
            buf.append("label{text=\"" + this.perfstats.get(f) + "\"};");
            buf.append("label{text=\"" + this.perfstats.get(f) / total * 100.0f + "\"};");
        }
        buf.append("}");
        if (farr.length == 0) {
            buf.append("text{text=\"none\"}");
        }
        buf.append("text{text=\"\"}");
    }
    
    void setRoot(final MissionManager aRoot) {
        this.root = aRoot;
    }
    
    static {
        logger = Logger.getLogger(MissionStats.class.getName());
    }
}
