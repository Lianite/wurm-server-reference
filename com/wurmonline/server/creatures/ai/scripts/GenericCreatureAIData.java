// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures.ai.scripts;

import com.wurmonline.math.Vector2f;
import com.wurmonline.server.creatures.ai.CreatureAIData;

public class GenericCreatureAIData extends CreatureAIData
{
    private boolean freezeMovement;
    private float randomMovementChance;
    private boolean prefersPlayers;
    private float prefersPlayersModifier;
    private boolean hasTether;
    private int tetherX;
    private int tetherY;
    private int tetherDistance;
    
    public GenericCreatureAIData() {
        this.freezeMovement = false;
        this.randomMovementChance = 0.01f;
        this.prefersPlayers = false;
        this.prefersPlayersModifier = 2.0f;
        this.hasTether = false;
        this.tetherX = -1;
        this.tetherY = -1;
        this.tetherDistance = -1;
    }
    
    public GenericCreatureAIData(final boolean prefersPlayers, final float movementChance) {
        this.freezeMovement = false;
        this.randomMovementChance = 0.01f;
        this.prefersPlayers = false;
        this.prefersPlayersModifier = 2.0f;
        this.hasTether = false;
        this.tetherX = -1;
        this.tetherY = -1;
        this.tetherDistance = -1;
        this.prefersPlayers = prefersPlayers;
        this.randomMovementChance = movementChance;
    }
    
    public GenericCreatureAIData(final boolean prefersPlayers, final float movementChance, final int tetherDistance) {
        this(prefersPlayers, movementChance);
        this.tetherDistance = tetherDistance;
        if (tetherDistance > 0) {
            this.hasTether = true;
        }
    }
    
    public boolean isMovementFrozen() {
        return this.freezeMovement;
    }
    
    public void setMovementFrozen(final boolean frozen) {
        this.freezeMovement = frozen;
    }
    
    public float getRandomMovementChance() {
        return this.randomMovementChance;
    }
    
    public void setRandomMovementChance(float newChance) {
        if (newChance > 1.0f || newChance < 0.0f) {
            newChance = Math.max(0.0f, Math.min(1.0f, newChance));
        }
        this.randomMovementChance = newChance;
    }
    
    public boolean doesPreferPlayers() {
        return this.prefersPlayers;
    }
    
    public void setPrefersPlayers(final boolean doesPreferPlayers) {
        this.prefersPlayers = doesPreferPlayers;
    }
    
    public float getPrefersPlayersModifier() {
        return this.prefersPlayersModifier;
    }
    
    public void setPrefersPlayersModifier(final float prefersPlayersModifier) {
        this.prefersPlayersModifier = prefersPlayersModifier;
    }
    
    public boolean hasTether() {
        return this.hasTether;
    }
    
    public void setTether(final boolean shouldHaveTether) {
        this.hasTether = shouldHaveTether;
    }
    
    public void setTether(final int tileX, final int tileY) {
        if (tileX > 0 && tileY > 0) {
            this.hasTether = true;
        }
        this.tetherX = tileX;
        this.tetherY = tileY;
    }
    
    public int getTetherX() {
        return this.tetherX;
    }
    
    public int getTetherY() {
        return this.tetherY;
    }
    
    public Vector2f getTetherPos() {
        return new Vector2f(this.tetherX, this.tetherY);
    }
    
    public void setTetherDistance(final int newDistance) {
        this.tetherDistance = newDistance;
    }
    
    public int getTetherDistance() {
        return this.tetherDistance;
    }
}
