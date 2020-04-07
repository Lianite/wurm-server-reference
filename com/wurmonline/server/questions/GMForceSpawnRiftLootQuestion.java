// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public class GMForceSpawnRiftLootQuestion extends Question
{
    private static final Logger logger;
    
    public GMForceSpawnRiftLootQuestion(final Creature aResponder) {
        super(aResponder, "Spawn Rift Loot", "Which item would you like to spawn?", 144, aResponder.getWurmId());
    }
    
    @Override
    public void answer(final Properties aAnswer) {
        this.setAnswer(aAnswer);
    }
    
    @Override
    public void sendQuestion() {
    }
    
    static {
        logger = Logger.getLogger(GMForceSpawnRiftLootQuestion.class.getName());
    }
}
