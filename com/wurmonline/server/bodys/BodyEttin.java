// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.bodys;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.shared.exceptions.WurmServerException;
import com.wurmonline.server.Server;

final class BodyEttin extends BodyTemplate
{
    BodyEttin() {
        super((byte)4);
        this.headS = "left head";
        this.secondHeadS = "right head";
        this.typeString = new String[] { this.bodyS, this.headS, this.torsoS, this.leftArmS, this.rightArmS, this.leftOverArmS, this.rightOverArmS, this.leftThighS, this.rightThighS, this.leftUnderArmS, this.rightUnderArmS, this.leftCalfS, this.rightCalfS, this.leftHandS, this.rightHandS, this.leftFootS, this.rightFootS, this.neckS, this.leftEyeS, this.rightEyeS, this.centerEyeS, this.chestS, this.topBackS, this.stomachS, this.lowerBackS, this.crotchS, this.leftShoulderS, this.rightShoulderS, this.secondHeadS, this.faceS, this.leftLegS, this.rightLegS, this.hipS, this.baseOfNoseS, this.legsS };
    }
    
    @Override
    public byte getRandomWoundPos() throws Exception {
        final int rand = Server.rand.nextInt(1000);
        if (rand < 30) {
            return 1;
        }
        if (rand < 60) {
            return 28;
        }
        if (rand < 110) {
            return 5;
        }
        if (rand < 160) {
            return 6;
        }
        if (rand < 210) {
            return 7;
        }
        if (rand < 260) {
            return 8;
        }
        if (rand < 310) {
            return 9;
        }
        if (rand < 360) {
            return 10;
        }
        if (rand < 410) {
            return 11;
        }
        if (rand < 460) {
            return 12;
        }
        if (rand < 500) {
            return 13;
        }
        if (rand < 540) {
            return 14;
        }
        if (rand < 580) {
            return 15;
        }
        if (rand < 620) {
            return 16;
        }
        if (rand < 630) {
            return 17;
        }
        if (rand < 631) {
            return 18;
        }
        if (rand < 632) {
            return 19;
        }
        if (rand < 730) {
            return 21;
        }
        if (rand < 780) {
            return 22;
        }
        if (rand < 830) {
            return 23;
        }
        if (rand < 890) {
            return 24;
        }
        if (rand < 900) {
            return 25;
        }
        if (rand < 950) {
            return 26;
        }
        if (rand < 1000) {
            return 27;
        }
        throw new WurmServerException("Bad randomizer");
    }
    
    @Override
    void buildBody(final Item[] spaces, final Creature owner) {
        spaces[0].setOwner(owner.getWurmId(), true);
        spaces[0].insertItem(spaces[1]);
        spaces[0].insertItem(spaces[28]);
        spaces[1].insertItem(spaces[29]);
        spaces[0].insertItem(spaces[2]);
        spaces[2].insertItem(spaces[3]);
        spaces[2].insertItem(spaces[4]);
        spaces[3].insertItem(spaces[13]);
        spaces[4].insertItem(spaces[14]);
        spaces[2].insertItem(spaces[34]);
        spaces[34].insertItem(spaces[15]);
        spaces[34].insertItem(spaces[16]);
    }
}
