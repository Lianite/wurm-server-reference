// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.combat;

import java.util.Iterator;
import java.io.IOException;
import java.util.logging.Level;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import com.wurmonline.server.Constants;
import java.util.Date;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.logging.Logger;
import java.util.List;
import com.wurmonline.server.creatures.Creature;
import java.util.Set;
import java.text.SimpleDateFormat;

public final class Battle
{
    private final SimpleDateFormat df;
    private final SimpleDateFormat filedf;
    private final Set<Creature> creatures;
    private List<Creature> casualties;
    private final List<BattleEvent> events;
    private final long startTime;
    private long endTime;
    private final String name;
    private static final Logger logger;
    private static final String header = "<HTML> <HEAD><TITLE>Wurm battle log</TITLE></HEAD><BODY><BR><BR><B>";
    private static final String footer = "</BODY></HTML>";
    
    Battle(final Creature attacker, final Creature defender) {
        this.df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.filedf = new SimpleDateFormat("yyyy-MM-ddHHmmss");
        (this.creatures = new HashSet<Creature>()).add(attacker);
        this.creatures.add(defender);
        this.startTime = System.currentTimeMillis();
        this.endTime = System.currentTimeMillis();
        attacker.setBattle(this);
        defender.setBattle(this);
        this.events = new LinkedList<BattleEvent>();
        this.name = "Battle_" + attacker.getName() + "_vs_" + defender.getName();
    }
    
    boolean containsCreature(final Creature creature) {
        return this.creatures.contains(creature);
    }
    
    void addCreature(final Creature creature) {
        if (!this.creatures.contains(creature)) {
            this.creatures.add(creature);
            this.events.add(new BattleEvent((short)(-1), creature.getName()));
            creature.setBattle(this);
        }
        this.touch();
    }
    
    public void removeCreature(final Creature creature) {
        this.creatures.remove(creature);
        creature.setBattle(null);
        this.events.add(new BattleEvent((short)(-2), creature.getName()));
        this.touch();
    }
    
    void clearCreatures() {
        this.creatures.clear();
    }
    
    public void addCasualty(final Creature dead) {
        if (this.casualties == null) {
            this.casualties = new LinkedList<Creature>();
        }
        this.casualties.add(dead);
        this.events.add(new BattleEvent((short)(-3), dead.getName()));
        this.creatures.remove(dead);
        dead.setBattle(null);
        this.touch();
    }
    
    void touch() {
        this.endTime = System.currentTimeMillis();
    }
    
    public void addCasualty(final Creature killer, final Creature dead) {
        if (this.casualties == null) {
            this.casualties = new LinkedList<Creature>();
        }
        this.casualties.add(dead);
        this.events.add(new BattleEvent((short)(-3), dead.getName(), killer.getName()));
        this.creatures.remove(dead);
        dead.setBattle(null);
        this.touch();
    }
    
    public void addEvent(final BattleEvent event) {
        this.events.add(event);
        this.touch();
    }
    
    Creature[] getCreatures() {
        return this.creatures.toArray(new Creature[this.creatures.size()]);
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    public long getEndTime() {
        return this.endTime;
    }
    
    void save() {
        if (this.casualties != null && this.casualties.size() > 0) {
            Writer output = null;
            try {
                final Date d = new Date(this.startTime);
                String dir = Constants.webPath;
                if (!dir.endsWith(File.separator)) {
                    dir += File.separator;
                }
                final File aFile = new File(dir + this.name + "_" + this.filedf.format(d) + ".html");
                output = new BufferedWriter(new FileWriter(aFile));
                final String start = this.name + "</B><BR><I>started at " + this.df.format(d) + " and ended on " + this.df.format(new Date(this.endTime)) + "</I><BR><BR>";
                try {
                    output.write("<HTML> <HEAD><TITLE>Wurm battle log</TITLE></HEAD><BODY><BR><BR><B>");
                    output.write(start);
                }
                catch (IOException iox) {
                    Battle.logger.log(Level.WARNING, iox.getMessage(), iox);
                }
                for (final BattleEvent lBattleEvent : this.events) {
                    final String ts = lBattleEvent.toString();
                    try {
                        output.write(ts);
                    }
                    catch (IOException iox2) {
                        Battle.logger.log(Level.WARNING, iox2.getMessage(), iox2);
                    }
                }
                output.write("</BODY></HTML>");
            }
            catch (IOException iox3) {
                Battle.logger.log(Level.WARNING, "Failed to close " + this.name, iox3);
            }
            finally {
                try {
                    if (output != null) {
                        output.close();
                    }
                }
                catch (IOException ex) {}
            }
        }
        for (final Creature cret : this.creatures) {
            cret.setBattle(null);
        }
    }
    
    static {
        logger = Logger.getLogger(Battle.class.getName());
    }
}
