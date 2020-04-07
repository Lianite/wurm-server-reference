// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.structures.BridgePart;
import edu.umd.cs.findbugs.annotations.Nullable;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.structures.Wall;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.mesh.Tiles;
import java.util.Collection;
import com.wurmonline.server.items.Item;
import java.util.LinkedList;
import javax.annotation.Nonnull;
import com.wurmonline.server.creatures.Creature;
import java.util.List;
import com.wurmonline.shared.constants.SoundNames;

public class Behaviour implements SoundNames
{
    private static List<ActionEntry> emptyActionList;
    private short behaviourType;
    public static final int[] emptyIntArr;
    
    public Behaviour() {
        this.behaviourType = 0;
        Behaviours.getInstance().addBehaviour(this);
    }
    
    public Behaviour(final short type) {
        this.behaviourType = 0;
        this.behaviourType = type;
        Behaviours.getInstance().addBehaviour(this);
    }
    
    @Nonnull
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, final long target) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        return toReturn;
    }
    
    @Nonnull
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, @Nonnull final Item object, final int tilex, final int tiley, final boolean onSurface, final int tile) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.addAll(Actions.getDefaultTileActions());
        return toReturn;
    }
    
    @Nonnull
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, final int tilex, final int tiley, final boolean onSurface, final int tile) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.addAll(Actions.getDefaultTileActions());
        return toReturn;
    }
    
    public static final List<ActionEntry> getEmptyActionList() {
        return Behaviour.emptyActionList;
    }
    
    @Nonnull
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, @Nonnull final Item object, final int tilex, final int tiley, final boolean onSurface, final int tile, final int dir) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.addAll(Actions.getDefaultTileActions());
        return toReturn;
    }
    
    @Nonnull
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, final int tilex, final int tiley, final boolean onSurface, final int tile, final int dir) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.addAll(Actions.getDefaultTileActions());
        return toReturn;
    }
    
    @Nonnull
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, @Nonnull final Item object, final int tilex, final int tiley, final boolean onSurface, final Tiles.TileBorderDirection dir, final boolean border, final int heightOffset) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        return toReturn;
    }
    
    @Nonnull
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, final int tilex, final int tiley, final boolean onSurface, final Tiles.TileBorderDirection dir, final boolean border, final int heightOffset) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        return toReturn;
    }
    
    @Nonnull
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, @Nonnull final Item object, final int tilex, final int tiley, final boolean onSurface, final boolean corner, final int tile, final int heightOffset) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        return toReturn;
    }
    
    @Nonnull
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, final int tilex, final int tiley, final boolean onSurface, final boolean corner, final int tile, final int heightOffset) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        return toReturn;
    }
    
    public boolean action(@Nonnull final Action act, @Nonnull final Creature performer, @Nonnull final Item source, final int tilex, final int tiley, final boolean onSurface, final boolean corner, final int tile, final int heightOffset, final short action, final float counter) {
        return true;
    }
    
    public boolean action(@Nonnull final Action act, @Nonnull final Creature performer, final int tilex, final int tiley, final boolean onSurface, final boolean corner, final int tile, final int heightOffset, final short action, final float counter) {
        return true;
    }
    
    @Nonnull
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, @Nonnull final Item subject, @Nonnull final Skill skill) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        return toReturn;
    }
    
    @Nonnull
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, @Nonnull final Skill skill) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        return toReturn;
    }
    
    @Nonnull
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, @Nonnull final Item target) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        return toReturn;
    }
    
    @Nonnull
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, @Nonnull final Item subject, @Nonnull final Item target) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        return toReturn;
    }
    
    @Nonnull
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, @Nonnull final Wound target) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        return toReturn;
    }
    
    @Nonnull
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, @Nonnull final Item subject, @Nonnull final Wound target) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        return toReturn;
    }
    
    @Nonnull
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, @Nonnull final Creature target) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        return toReturn;
    }
    
    @Nonnull
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, @Nonnull final Item subject, @Nonnull final Creature target) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        return toReturn;
    }
    
    @Nonnull
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, @Nonnull final Item subject, @Nonnull final Wall target) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        return toReturn;
    }
    
    @Nonnull
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, @Nonnull final Wall target) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        return toReturn;
    }
    
    @Nonnull
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, @Nonnull final Item subject, @Nonnull final Fence target) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        return toReturn;
    }
    
    @Nonnull
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, @Nonnull final Fence target) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        return toReturn;
    }
    
    @Nonnull
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, @Nonnull final Item object, final int planetId) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        return toReturn;
    }
    
    @Nonnull
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, final int planetId) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        return toReturn;
    }
    
    public boolean action(@Nonnull final Action action, @Nonnull final Creature performer, final int tilex, final int tiley, final boolean onSurface, final int tile, final short num, final float counter) {
        return true;
    }
    
    public boolean action(@Nonnull final Action action, @Nonnull final Creature performer, @Nonnull final Item source, final int tilex, final int tiley, final boolean onSurface, final int heightOffset, final int tile, final short num, final float counter) {
        return true;
    }
    
    public boolean action(@Nonnull final Action action, @Nonnull final Creature performer, final int tilex, final int tiley, final boolean onSurface, final int tile, final int dir, final short num, final float counter) {
        return true;
    }
    
    public boolean action(@Nonnull final Action action, @Nonnull final Creature performer, @Nonnull final Item source, final int tilex, final int tiley, final boolean onSurface, final int heightOffset, final int tile, final int dir, final short num, final float counter) {
        return true;
    }
    
    public boolean action(@Nonnull final Action action, @Nonnull final Creature performer, final int planetId, final short num, final float counter) {
        return true;
    }
    
    public boolean action(@Nonnull final Action action, @Nonnull final Creature performer, @Nonnull final Item source, final int planetId, final short num, final float counter) {
        return true;
    }
    
    public boolean action(@Nonnull final Action action, @Nonnull final Creature performer, @Nonnull final Item source, @Nonnull final Item target, final short num, final float counter) {
        return true;
    }
    
    public boolean action(@Nonnull final Action action, @Nonnull final Creature performer, @Nonnull final Wound target, final short num, final float counter) {
        return true;
    }
    
    public boolean action(@Nonnull final Action action, @Nonnull final Creature performer, @Nonnull final Item source, @Nonnull final Wound target, final short num, final float counter) {
        return true;
    }
    
    public boolean action(@Nonnull final Action action, @Nonnull final Creature performer, @Nonnull final Item target, final short num, final float counter) {
        return true;
    }
    
    public boolean action(@Nonnull final Action action, @Nonnull final Creature performer, @Nonnull final Item source, @Nonnull final Creature target, final short num, final float counter) {
        return true;
    }
    
    public boolean action(@Nonnull final Action action, @Nonnull final Creature performer, @Nonnull final Creature target, final short num, final float counter) {
        return true;
    }
    
    final short getType() {
        return this.behaviourType;
    }
    
    public boolean action(@Nonnull final Action action, @Nonnull final Creature performer, @Nonnull final Item source, @Nonnull final Wall target, final short num, final float counter) {
        return true;
    }
    
    public boolean action(@Nonnull final Action action, @Nonnull final Creature performer, @Nonnull final Wall target, final short num, final float counter) {
        return true;
    }
    
    static void addEmotes(final List<ActionEntry> list) {
    }
    
    public boolean action(@Nonnull final Action action, @Nonnull final Creature performer, @Nonnull final Item source, final boolean onSurface, @Nonnull final Fence target, final short num, final float counter) {
        return true;
    }
    
    public boolean action(@Nonnull final Action action, @Nonnull final Creature performer, final boolean onSurface, @Nonnull final Fence target, final short num, final float counter) {
        return true;
    }
    
    public boolean action(@Nonnull final Action act, @Nonnull final Creature performer, @Nonnull final Item source, @Nonnull final Skill skill, final short action, final float counter) {
        return this.action(act, performer, skill, action, counter);
    }
    
    public boolean action(@Nonnull final Action act, @Nonnull final Creature performer, @Nonnull final Skill skill, final short action, final float counter) {
        return true;
    }
    
    @Nullable
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, final boolean onSurface, @Nonnull final Floor floor) {
        return null;
    }
    
    @Nullable
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature creature, @Nonnull final Item item, final boolean onSurface, final Floor floor) {
        return null;
    }
    
    public boolean action(@Nonnull final Action action, @Nonnull final Creature performer, @Nonnull final Item source, final boolean onSurface, final Floor target, final int encodedTile, final short num, final float counter) {
        return true;
    }
    
    public boolean action(@Nonnull final Action act, @Nonnull final Creature performer, final boolean onSurface, @Nonnull final Floor floor, final int encodedTile, final short action, final float counter) {
        return true;
    }
    
    public boolean action(@Nonnull final Action aAct, @Nonnull final Creature aPerformer, @Nonnull final Item aSource, final int aTilex, final int aTiley, final boolean onSurface, final int aHeightOffset, final Tiles.TileBorderDirection aDir, final long borderId, final short aAction, final float aCounter) {
        return true;
    }
    
    public boolean action(@Nonnull final Action aAct, @Nonnull final Creature aPerformer, final int aTilex, final int aTiley, final boolean onSurface, final Tiles.TileBorderDirection aDir, final long borderId, final short aAction, final float aCounter) {
        return true;
    }
    
    public boolean action(@Nonnull final Action action, @Nonnull final Creature performer, @Nonnull final Item[] targets, final short num, final float counter) {
        return true;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        return 31 * this.getType();
    }
    
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Behaviour)) {
            return false;
        }
        final Behaviour other = (Behaviour)obj;
        return this.getType() == other.getType();
    }
    
    @Nonnull
    @Override
    public String toString() {
        return "Behaviour [behaviourType=" + this.getType() + "]";
    }
    
    @Nullable
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature aPerformer, final boolean aOnSurface, @Nonnull final BridgePart aBridgePart) {
        return null;
    }
    
    @Nullable
    public List<ActionEntry> getBehavioursFor(@Nonnull final Creature aPerformer, @Nonnull final Item item, final boolean aOnSurface, @Nonnull final BridgePart aBridgePart) {
        return null;
    }
    
    public boolean action(@Nonnull final Action act, @Nonnull final Creature performer, final boolean onSurface, @Nonnull final BridgePart aBridgePart, final int encodedTile, final short action, final float counter) {
        return true;
    }
    
    public boolean action(@Nonnull final Action act, @Nonnull final Creature performer, @Nonnull final Item item, final boolean onSurface, @Nonnull final BridgePart aBridgePart, final int encodedTile, final short action, final float counter) {
        return true;
    }
    
    static {
        emptyIntArr = new int[0];
    }
}
