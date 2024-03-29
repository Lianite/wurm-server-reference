// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.bodys;

import java.util.logging.Level;
import com.wurmonline.shared.exceptions.WurmServerException;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import java.util.logging.Logger;

public class BodyTemplate
{
    private static Logger logger;
    public static final byte TYPE_HUMAN = 0;
    public static final byte TYPE_HORSE = 1;
    public static final byte TYPE_BEAR = 2;
    public static final byte TYPE_DOG = 3;
    public static final byte TYPE_ETTIN = 4;
    public static final byte TYPE_CYCLOPS = 5;
    public static final byte TYPE_DRAGON = 6;
    public static final byte TYPE_BIRD = 7;
    public static final byte TYPE_SPIDER = 8;
    public static final byte TYPE_SNAKE = 9;
    byte type;
    public static final byte body = 0;
    String bodyS;
    public static final byte head = 1;
    String headS;
    public static final byte torso = 2;
    String torsoS;
    public static final byte leftArm = 3;
    String leftArmS;
    public static final byte rightArm = 4;
    String rightArmS;
    public static final byte leftOverArm = 5;
    String leftOverArmS;
    public static final byte rightOverArm = 6;
    String rightOverArmS;
    public static final byte leftThigh = 7;
    String leftThighS;
    public static final byte rightThigh = 8;
    String rightThighS;
    public static final byte leftUnderArm = 9;
    String leftUnderArmS;
    public static final byte rightUnderArm = 10;
    String rightUnderArmS;
    public static final byte leftCalf = 11;
    String leftCalfS;
    public static final byte rightCalf = 12;
    String rightCalfS;
    public static final byte leftHand = 13;
    String leftHandS;
    public static final byte rightHand = 14;
    String rightHandS;
    public static final byte leftFoot = 15;
    String leftFootS;
    public static final byte rightFoot = 16;
    String rightFootS;
    public static final byte neck = 17;
    String neckS;
    public static final byte leftEye = 18;
    String leftEyeS;
    public static final byte rightEye = 19;
    String rightEyeS;
    public static final byte centerEye = 20;
    String centerEyeS;
    public static final byte chest = 21;
    String chestS;
    public static final byte topBack = 22;
    String topBackS;
    public static final byte stomach = 23;
    String stomachS;
    public static final byte lowerBack = 24;
    String lowerBackS;
    public static final byte crotch = 25;
    String crotchS;
    public static final byte leftShoulder = 26;
    String leftShoulderS;
    public static final byte rightShoulder = 27;
    String rightShoulderS;
    public static final byte secondHead = 28;
    String secondHeadS;
    public static final byte face = 29;
    String faceS;
    public static final byte leftLeg = 30;
    String leftLegS;
    public static final byte rightLeg = 31;
    String rightLegS;
    public static final byte hip = 32;
    String hipS;
    public static final byte baseOfNose = 33;
    String baseOfNoseS;
    public static final byte legs = 34;
    String legsS;
    public static final byte tabardSlot = 35;
    public static final byte neckSlot = 36;
    public static final byte lHeldSlot = 37;
    public static final byte rHeldSlot = 38;
    public static final byte lRingSlot = 39;
    public static final byte rRingSlot = 40;
    public static final byte quiverSlot = 41;
    public static final byte backSlot = 42;
    public static final byte beltSlot = 43;
    public static final byte shieldSlot = 44;
    public static final byte capeSlot = 45;
    public static final byte lShoulderSlot = 46;
    public static final byte rShoulderSlot = 47;
    public static final byte inventory = 48;
    public String[] typeString;
    
    BodyTemplate(final byte aType) {
        this.type = 0;
        this.bodyS = "body";
        this.headS = "head";
        this.torsoS = "torso";
        this.leftArmS = "left arm";
        this.rightArmS = "right arm";
        this.leftOverArmS = "left upper arm";
        this.rightOverArmS = "right upper arm";
        this.leftThighS = "left thigh";
        this.rightThighS = "right thigh";
        this.leftUnderArmS = "left underarm";
        this.rightUnderArmS = "right underarm";
        this.leftCalfS = "left calf";
        this.rightCalfS = "right calf";
        this.leftHandS = "left hand";
        this.rightHandS = "right hand";
        this.leftFootS = "left foot";
        this.rightFootS = "right foot";
        this.neckS = "neck";
        this.leftEyeS = "left eye";
        this.rightEyeS = "right eye";
        this.centerEyeS = "center eye";
        this.chestS = "chest";
        this.topBackS = "top of the back";
        this.stomachS = "stomach";
        this.lowerBackS = "lower back";
        this.crotchS = "crotch";
        this.leftShoulderS = "left shoulder";
        this.rightShoulderS = "right shoulder";
        this.secondHeadS = "second head";
        this.faceS = "face";
        this.leftLegS = "left leg";
        this.rightLegS = "right leg";
        this.hipS = "hip";
        this.baseOfNoseS = "baseOfNose";
        this.legsS = "legs";
        this.typeString = new String[] { this.bodyS, this.headS, this.torsoS, this.leftArmS, this.rightArmS, this.leftOverArmS, this.rightOverArmS, this.leftThighS, this.rightThighS, this.leftUnderArmS, this.rightUnderArmS, this.leftCalfS, this.rightCalfS, this.leftHandS, this.rightHandS, this.leftFootS, this.rightFootS, this.neckS, this.leftEyeS, this.rightEyeS, this.centerEyeS, this.chestS, this.topBackS, this.stomachS, this.lowerBackS, this.crotchS, this.leftShoulderS, this.rightShoulderS, this.secondHeadS, this.faceS, this.leftLegS, this.rightLegS, this.hipS, this.baseOfNoseS, this.legsS };
        this.type = aType;
    }
    
    void buildBody(final Item[] spaces, final Creature owner) {
        spaces[0].setOwner(owner.getWurmId(), true);
        spaces[0].insertItem(spaces[1]);
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
    
    public byte getRandomWoundPos() throws Exception {
        final int rand = Server.rand.nextInt(1000);
        if (rand < 30) {
            return 1;
        }
        if (rand < 80) {
            return 5;
        }
        if (rand < 130) {
            return 6;
        }
        if (rand < 180) {
            return 7;
        }
        if (rand < 230) {
            return 8;
        }
        if (rand < 280) {
            return 9;
        }
        if (rand < 320) {
            return 10;
        }
        if (rand < 370) {
            return 11;
        }
        if (rand < 420) {
            return 12;
        }
        if (rand < 460) {
            return 13;
        }
        if (rand < 500) {
            return 14;
        }
        if (rand < 540) {
            return 15;
        }
        if (rand < 580) {
            return 16;
        }
        if (rand < 600) {
            return 17;
        }
        if (rand < 601) {
            return 18;
        }
        if (rand < 602) {
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
    
    byte getUpperLeftWoundPos() throws Exception {
        final int rand = Server.rand.nextInt(100);
        if (rand < 3) {
            return 1;
        }
        if (rand < 40) {
            return 5;
        }
        if (rand < 50) {
            return 17;
        }
        if (rand < 51) {
            return 18;
        }
        if (rand < 60) {
            return 21;
        }
        if (rand < 78) {
            return 22;
        }
        if (rand < 100) {
            return 26;
        }
        throw new WurmServerException("Bad randomizer");
    }
    
    byte getUpperRightWoundPos() throws Exception {
        final int rand = Server.rand.nextInt(100);
        if (rand < 3) {
            return 1;
        }
        if (rand < 13) {
            return 17;
        }
        if (rand < 50) {
            return 6;
        }
        if (rand < 51) {
            return 19;
        }
        if (rand < 63) {
            return 21;
        }
        if (rand < 78) {
            return 22;
        }
        if (rand < 100) {
            return 27;
        }
        throw new WurmServerException("Bad randomizer");
    }
    
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
        if (rand < 81) {
            return 26;
        }
        if (rand < 100) {
            return 27;
        }
        throw new WurmServerException("Bad randomizer");
    }
    
    byte getMidLeftWoundPos() throws Exception {
        final int rand = Server.rand.nextInt(100);
        if (rand < 18) {
            return 5;
        }
        if (rand < 48) {
            return 7;
        }
        if (rand < 58) {
            return 9;
        }
        if (rand < 66) {
            return 13;
        }
        if (rand < 73) {
            return 21;
        }
        if (rand < 83) {
            return 23;
        }
        if (rand < 100) {
            return 24;
        }
        throw new WurmServerException("Bad randomizer");
    }
    
    public byte getCenterWoundPos() throws Exception {
        final int rand = Server.rand.nextInt(100);
        if (rand < 11) {
            return 7;
        }
        if (rand < 22) {
            return 8;
        }
        if (rand < 32) {
            return 9;
        }
        if (rand < 42) {
            return 10;
        }
        if (rand < 46) {
            return 13;
        }
        if (rand < 50) {
            return 14;
        }
        if (rand < 73) {
            return 21;
        }
        if (rand < 100) {
            return 23;
        }
        throw new WurmServerException("Bad randomizer");
    }
    
    byte getMidRightWoundPos() throws Exception {
        final int rand = Server.rand.nextInt(100);
        if (rand < 18) {
            return 6;
        }
        if (rand < 48) {
            return 8;
        }
        if (rand < 58) {
            return 10;
        }
        if (rand < 66) {
            return 14;
        }
        if (rand < 73) {
            return 21;
        }
        if (rand < 83) {
            return 23;
        }
        if (rand < 100) {
            return 24;
        }
        throw new WurmServerException("Bad randomizer");
    }
    
    byte getLowerLeftWoundPos() throws Exception {
        final int rand = Server.rand.nextInt(100);
        if (rand < 48) {
            return 7;
        }
        if (rand < 58) {
            return 9;
        }
        if (rand < 78) {
            return 11;
        }
        if (rand < 98) {
            return 15;
        }
        if (rand < 100) {
            return 25;
        }
        throw new WurmServerException("Bad randomizer");
    }
    
    byte getLowWoundPos() throws Exception {
        final int rand = Server.rand.nextInt(100);
        if (rand < 10) {
            return 7;
        }
        if (rand < 20) {
            return 8;
        }
        if (rand < 40) {
            return 11;
        }
        if (rand < 60) {
            return 12;
        }
        if (rand < 75) {
            return 15;
        }
        if (rand < 90) {
            return 16;
        }
        if (rand < 100) {
            return 25;
        }
        throw new WurmServerException("Bad randomizer");
    }
    
    byte getLowerRightWoundPos() throws Exception {
        final int rand = Server.rand.nextInt(100);
        if (rand < 48) {
            return 8;
        }
        if (rand < 58) {
            return 10;
        }
        if (rand < 78) {
            return 12;
        }
        if (rand < 98) {
            return 16;
        }
        if (rand < 100) {
            return 25;
        }
        throw new WurmServerException("Bad randomizer");
    }
    
    public final String getBodyPositionDescription(final byte position) {
        String lDescription;
        if (position >= 0 && position <= this.typeString.length) {
            lDescription = this.typeString[position];
        }
        else {
            lDescription = "Unknown position-" + position;
        }
        return lDescription;
    }
    
    public static byte convertToArmorEquipementSlot(final byte bodyPart) {
        byte toReturn = -1;
        switch (bodyPart) {
            case 1:
            case 28: {
                toReturn = 2;
                break;
            }
            case 29: {
                toReturn = 25;
                break;
            }
            case 0:
            case 2: {
                toReturn = 3;
                break;
            }
            case 42: {
                toReturn = 20;
                break;
            }
            case 34: {
                toReturn = 4;
                break;
            }
            case 43: {
                toReturn = 22;
                break;
            }
            case 3: {
                toReturn = 5;
                break;
            }
            case 4: {
                toReturn = 6;
                break;
            }
            case 13: {
                toReturn = 7;
                break;
            }
            case 14: {
                toReturn = 8;
                break;
            }
            case 15: {
                toReturn = 9;
                break;
            }
            case 16: {
                toReturn = 10;
                break;
            }
            case 46: {
                toReturn = 18;
                break;
            }
            case 47: {
                toReturn = 19;
                break;
            }
            case 39: {
                toReturn = 17;
                break;
            }
            case 40: {
                toReturn = 16;
                break;
            }
        }
        if (toReturn == -1) {
            BodyTemplate.logger.log(Level.FINEST, "Could not convert BodyTemplate bodypart to Equipementpart, Constant number: " + bodyPart);
        }
        return toReturn;
    }
    
    public static byte convertToItemEquipementSlot(final byte bodyPart) {
        switch (bodyPart) {
            case 1: {
                return 2;
            }
            case 29: {
                return 25;
            }
            case 13: {
                return 7;
            }
            case 37: {
                return 0;
            }
            case 14: {
                return 8;
            }
            case 38: {
                return 1;
            }
            case 0:
            case 2: {
                return 12;
            }
            case 42: {
                return 20;
            }
            case 3: {
                return 26;
            }
            case 44: {
                return 11;
            }
            case 4: {
                return 27;
            }
            case 34: {
                return 13;
            }
            case 43: {
                return 22;
            }
            case 41: {
                return 23;
            }
            case 15: {
                return 9;
            }
            case 16: {
                return 10;
            }
            case 35: {
                return 15;
            }
            case 40: {
                return 16;
            }
            case 39: {
                return 17;
            }
            case 36: {
                return 21;
            }
            case 46: {
                return 18;
            }
            case 47: {
                return 19;
            }
            default: {
                BodyTemplate.logger.log(Level.FINEST, "Could not convert BodyTemplate bodypart to Equipementpart, Constant number: " + bodyPart);
                return -1;
            }
        }
    }
    
    static {
        BodyTemplate.logger = Logger.getLogger(BodyTemplate.class.getName());
    }
}
