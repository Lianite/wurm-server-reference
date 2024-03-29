// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.bodys;

import com.wurmonline.shared.exceptions.WurmServerException;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;

public final class BodySnake extends BodyTemplate
{
    BodySnake() {
        super((byte)9);
        this.leftOverArmS = "upper body";
        this.rightOverArmS = "upper body";
        this.leftThighS = "center body";
        this.rightThighS = "center body";
        this.leftUnderArmS = "center body";
        this.rightUnderArmS = "center body";
        this.torsoS = "upper body";
        this.chestS = "chest";
        this.topBackS = "upper body";
        this.lowerBackS = "lower body";
        this.legsS = "tail";
        this.leftCalfS = "lower body";
        this.rightCalfS = "lower body";
        this.leftHandS = "center body";
        this.rightHandS = "center body";
        this.leftFootS = "tail";
        this.rightFootS = "tail";
        this.leftArmS = "upper body";
        this.rightArmS = "upper body";
        this.leftLegS = "center body";
        this.rightLegS = "center body";
        this.leftEyeS = "eyes";
        this.rightEyeS = "eyes";
        this.baseOfNoseS = "nostrils";
        this.typeString = new String[] { this.bodyS, this.headS, this.torsoS, this.leftArmS, this.rightArmS, this.leftOverArmS, this.rightOverArmS, this.leftThighS, this.rightThighS, this.leftUnderArmS, this.rightUnderArmS, this.leftCalfS, this.rightCalfS, this.leftHandS, this.rightHandS, this.leftFootS, this.rightFootS, this.neckS, this.leftEyeS, this.rightEyeS, this.centerEyeS, this.chestS, this.topBackS, this.stomachS, this.lowerBackS, this.crotchS, this.leftShoulderS, this.rightShoulderS, this.secondHeadS, this.faceS, this.leftLegS, this.rightLegS, this.hipS, this.baseOfNoseS, this.legsS };
    }
    
    @Override
    void buildBody(final Item[] spaces, final Creature owner) {
        spaces[0].setOwner(owner.getWurmId(), true);
        spaces[0].insertItem(spaces[1]);
        spaces[1].insertItem(spaces[29]);
        spaces[0].insertItem(spaces[2]);
        spaces[2].insertItem(spaces[34]);
    }
    
    @Override
    public byte getRandomWoundPos() throws Exception {
        final int rand = Server.rand.nextInt(100);
        if (rand < 10) {
            return 1;
        }
        if (rand < 40) {
            return 21;
        }
        if (rand < 50) {
            return 22;
        }
        if (rand < 60) {
            return 24;
        }
        if (rand < 101) {
            return 34;
        }
        throw new WurmServerException("Bad randomizer");
    }
    
    @Override
    byte getUpperLeftWoundPos() throws Exception {
        final int rand = Server.rand.nextInt(100);
        if (rand < 3) {
            return 1;
        }
        return 21;
    }
    
    @Override
    byte getUpperRightWoundPos() throws Exception {
        final int rand = Server.rand.nextInt(100);
        if (rand < 3) {
            return 1;
        }
        return 21;
    }
    
    @Override
    byte getHighWoundPos() throws Exception {
        final int rand = Server.rand.nextInt(100);
        if (rand < 40) {
            return 1;
        }
        if (rand < 60) {
            return 17;
        }
        if (rand < 61) {
            return 18;
        }
        if (rand < 62) {
            return 19;
        }
        if (rand < 64) {
            return 29;
        }
        if (rand < 81) {
            return 22;
        }
        if (rand < 100) {
            return 21;
        }
        throw new WurmServerException("Bad randomizer");
    }
    
    @Override
    byte getMidLeftWoundPos() throws Exception {
        final int rand = Server.rand.nextInt(100);
        if (rand < 58) {
            return 21;
        }
        if (rand < 76) {
            return 22;
        }
        if (rand < 100) {
            return 24;
        }
        throw new WurmServerException("Bad randomizer");
    }
    
    @Override
    public byte getCenterWoundPos() throws Exception {
        final int rand = Server.rand.nextInt(100);
        if (rand < 58) {
            return 21;
        }
        if (rand < 76) {
            return 22;
        }
        if (rand < 100) {
            return 24;
        }
        throw new WurmServerException("Bad randomizer");
    }
    
    @Override
    byte getMidRightWoundPos() throws Exception {
        final int rand = Server.rand.nextInt(100);
        if (rand < 58) {
            return 21;
        }
        if (rand < 76) {
            return 22;
        }
        if (rand < 100) {
            return 24;
        }
        throw new WurmServerException("Bad randomizer");
    }
    
    @Override
    byte getLowerLeftWoundPos() throws Exception {
        final int rand = Server.rand.nextInt(100);
        if (rand < 58) {
            return 24;
        }
        if (rand < 100) {
            return 34;
        }
        throw new WurmServerException("Bad randomizer");
    }
    
    @Override
    byte getLowWoundPos() throws Exception {
        final int rand = Server.rand.nextInt(100);
        if (rand < 58) {
            return 24;
        }
        if (rand < 100) {
            return 34;
        }
        throw new WurmServerException("Bad randomizer");
    }
    
    @Override
    byte getLowerRightWoundPos() throws Exception {
        final int rand = Server.rand.nextInt(100);
        if (rand < 58) {
            return 24;
        }
        if (rand < 100) {
            return 34;
        }
        throw new WurmServerException("Bad randomizer");
    }
}
