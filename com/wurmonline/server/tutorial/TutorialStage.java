// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.tutorial;

import com.wurmonline.server.utils.BMLBuilder;
import java.util.ArrayList;

public abstract class TutorialStage
{
    private static final String ERROR_NOSUBSTAGE;
    private static final String ERROR_OVERSUBSTAGE;
    private static final String ERROR_NOSUBSTAGE_UPDATE;
    private static final String ERROR_OVERSUBSTAGE_UPDATE;
    private final long playerId;
    private int currentSubStage;
    private boolean forceOpened;
    protected ArrayList<TutorialSubStage> subStages;
    
    public TutorialStage(final long playerId) {
        this.forceOpened = false;
        this.subStages = new ArrayList<TutorialSubStage>();
        this.playerId = playerId;
        this.buildSubStages();
    }
    
    public abstract TutorialStage getNextStage();
    
    public abstract TutorialStage getLastStage();
    
    public abstract void buildSubStages();
    
    public abstract short getWindowId();
    
    public String getCurrentBML() {
        if (this.subStages == null) {
            return TutorialStage.ERROR_NOSUBSTAGE;
        }
        if (this.subStages.size() < this.getCurrentSubStage()) {
            return TutorialStage.ERROR_OVERSUBSTAGE;
        }
        return this.subStages.get(this.currentSubStage).getBMLString();
    }
    
    public String getUpdateBML() {
        if (this.subStages == null) {
            return TutorialStage.ERROR_NOSUBSTAGE_UPDATE;
        }
        if (this.subStages.size() < this.getCurrentSubStage()) {
            return TutorialStage.ERROR_OVERSUBSTAGE_UPDATE;
        }
        return this.subStages.get(this.currentSubStage).getBMLUpdateString();
    }
    
    public boolean isAwaitingAnyTrigger() {
        return this.subStages.get(this.currentSubStage).awaitingTrigger();
    }
    
    public boolean shouldSkipTrigger() {
        return !this.isAwaitingAnyTrigger() && this.subStages.get(this.currentSubStage).hadNextTrigger();
    }
    
    public boolean awaitingTrigger(final PlayerTutorial.PlayerTrigger trigger) {
        return this.subStages.get(this.currentSubStage).hasNextTrigger(trigger);
    }
    
    public void clearTrigger() {
        this.subStages.get(this.currentSubStage).clearNextTrigger();
    }
    
    public int getCurrentSubStage() {
        return this.currentSubStage;
    }
    
    public void setForceOpened(final boolean forceOpened) {
        this.forceOpened = forceOpened;
    }
    
    public boolean isForceOpened() {
        return this.forceOpened;
    }
    
    public boolean increaseSubStage() {
        ++this.currentSubStage;
        return this.currentSubStage < this.subStages.size();
    }
    
    public boolean decreaseSubStage() {
        if (this.currentSubStage == 0) {
            return true;
        }
        this.currentSubStage = Math.max(0, this.currentSubStage - 1);
        return false;
    }
    
    public void toLastSubStage() {
        this.currentSubStage = this.subStages.size() - 1;
    }
    
    public void resetSubStage() {
        this.subStages.clear();
        this.currentSubStage = 0;
        this.buildSubStages();
    }
    
    public long getPlayerId() {
        return this.playerId;
    }
    
    static {
        ERROR_NOSUBSTAGE = BMLBuilder.createText("Error while loading tutorial stage: no sub stages found.");
        ERROR_OVERSUBSTAGE = BMLBuilder.createText("Error while loading tutorial stage: not enough sub stages found.");
        ERROR_NOSUBSTAGE_UPDATE = BMLBuilder.createText("Error while updating tutorial stage: no sub stages found.");
        ERROR_OVERSUBSTAGE_UPDATE = BMLBuilder.createText("Error while updating tutorial stage: not enough sub stages found.");
    }
    
    public abstract class TutorialSubStage
    {
        protected final long playerId;
        protected String bmlString;
        protected boolean hadNextTrigger;
        protected PlayerTutorial.PlayerTrigger enableNextTrigger;
        
        public TutorialSubStage(final long playerId) {
            this.bmlString = null;
            this.hadNextTrigger = false;
            this.enableNextTrigger = PlayerTutorial.PlayerTrigger.NONE;
            this.playerId = playerId;
        }
        
        public long getPlayerId() {
            return this.playerId;
        }
        
        public boolean awaitingTrigger() {
            return this.enableNextTrigger != PlayerTutorial.PlayerTrigger.NONE;
        }
        
        public boolean hasNextTrigger(final PlayerTutorial.PlayerTrigger trigger) {
            return this.enableNextTrigger == trigger;
        }
        
        public void setNextTrigger(final PlayerTutorial.PlayerTrigger trigger) {
            this.enableNextTrigger = trigger;
            if (trigger != PlayerTutorial.PlayerTrigger.NONE) {
                this.hadNextTrigger = true;
            }
        }
        
        public void clearNextTrigger() {
            this.setNextTrigger(PlayerTutorial.PlayerTrigger.NONE);
        }
        
        public boolean hadNextTrigger() {
            return this.hadNextTrigger;
        }
        
        public String getBMLString() {
            if (this.bmlString == null) {
                this.buildBMLString();
                this.triggerOnView();
            }
            return this.bmlString;
        }
        
        public void triggerOnView() {
        }
        
        public String getBMLUpdateString() {
            return BMLBuilder.createBMLUpdate(BMLBuilder.createButton("next", "Next", 80, 20, true)).toString();
        }
        
        protected abstract void buildBMLString();
    }
}
