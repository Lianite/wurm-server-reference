// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.structures.PlanBridgeCheckResult;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.structures.PlanBridgeChecks;
import com.wurmonline.server.structures.PlanBridgeMethods;
import com.wurmonline.server.Servers;
import com.wurmonline.shared.constants.BridgeConstants;
import java.util.logging.Level;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.Point;
import java.util.logging.Logger;

public class PlanBridgeQuestion extends Question
{
    private static final Logger logger;
    private static final int MINHEIGHTWOOD = 0;
    private static final int MINHEIGHTSTONE = 0;
    private static final int MINSKILLROPE = 10;
    private static final int MINSKILLWOOD = 10;
    private static final int MINSKILLBRICK = 30;
    private static final int MINSKILLMARBLE = 40;
    private static final int MINSKILLARCH = 20;
    private int bridgeCount;
    private final Point start;
    private final Point end;
    private final byte dir;
    private final int width;
    private final int length;
    private int heightDiff;
    private int steepnessSelected;
    private int bmlLines;
    private int iconLines;
    private int startFloorlevel;
    private int endFloorlevel;
    private final int targetFloorLevel;
    private String fail;
    private String reason;
    private final String[] spansWood;
    private final String[] spansBrick;
    private final String[] archWood;
    private String bridgeName;
    private byte bridgeType;
    private boolean arched;
    private String bridgePlan;
    private int page;
    private int steepness;
    private int layer;
    
    public PlanBridgeQuestion(final Creature aResponder, final int aTargetFloorLevel, final Point aStart, final Point aEnd, final byte aDir, final int aWidth, final int aLength) {
        this(aResponder, aTargetFloorLevel, "Decide on what bridge to build", aStart, aEnd, aDir, aWidth, aLength, 0);
    }
    
    public PlanBridgeQuestion(final Creature aResponder, final int aTargetFloorLevel, final Point aStart, final Point aEnd, final byte aDir, final int aWidth, final int aLength, final byte aBridgeType, final boolean aArched, final String aBridgePlan, final String aBridgeName) {
        this(aResponder, aTargetFloorLevel, "Name your Bridge", aStart, aEnd, aDir, aWidth, aLength, 1);
        this.bridgeType = aBridgeType;
        this.arched = aArched;
        this.bridgePlan = aBridgePlan;
        this.bridgeName = aBridgeName;
        this.layer = aResponder.getLayer();
    }
    
    public PlanBridgeQuestion(final Creature aResponder, final int aTargetFloorLevel, final String aQuestion, final Point aStart, final Point aEnd, final byte aDir, final int aWidth, final int aLength, final int aPage) {
        super(aResponder, "Plan Bridge", aQuestion, 116, -10L);
        this.steepnessSelected = 20;
        this.bmlLines = 0;
        this.iconLines = 0;
        this.fail = "";
        this.reason = "";
        this.spansWood = new String[] { "", "C", "aA", "aCA", "aCCA", "aCCCA", "aASaCA", "aCASaCA" };
        this.spansBrick = new String[] { "", "E", "aA", "aDA", "abBA", "abCBA", "ESabBA", "ESabCBA", "aASabCBA", "ESabCBASE", "ESabCBASaA", "abCBASabCBA" };
        this.archWood = new String[] { "", "C", "aA", "aCA", "aCCA", "aCCCA", "aCCCCA", "aCCCCCA", "aCCCCCCA", "aCCCCCCCA", "aCCCCCCCCA", "aCCCCCCCCCA", "aASaCCCCASaA", "aASaCCCCCASaA", "aASaCCCCCCASaA", "aASaCCCCCCCASaA", "aASaCCCCCCCCASaA", "aCASaCCCCCCCASaCA", "aCASaCCCCCCCCASaCA", "aCCASaCCCCCCCASaCCA", "aCCASaCCCCCCCCASaCCA", "aASaASaCCCCCCCASaASaA", "aASaASaCCCCCCCCASaASaA", "aASaASaCCCCCCCCCASaASaA", "aCASaASaCCCCCCCCASaASaCA", "aCASaASaCCCCCCCCCASaASaCA", "aCASaASaCCCCCCCCCCASaASaCA", "aCASaCASaCCCCCCCCCASaCASaCA", "aCASaCASaCCCCCCCCCCASaCASaCA", "aCASaCASaCCCCCCCCCCCASaCASaCA", "aASaASaASaCCCCCCCCCCASaASaASaA", "aASaASaASaCCCCCCCCCCCASaASaASaA", "aASaASaASaCCCCCCCCCCCCASaASaASaA", "aCASaASaASaCCCCCCCCCCCASaASaASaCA", "aCASaASaASaCCCCCCCCCCCCASaASaASaCA", "aCASaASaASaCCCCCCCCCCCCCASaASaASaCA", "aCASaCASaASaCCCCCCCCCCCCASaASaCASaCA", "aCASaCASaASaCCCCCCCCCCCCCASaASaCASaCA", "aCASaCASaASaCCCCCCCCCCCCCCASaASaCASaCA", "aASaASaASaASaCCCCCCCCCCCCCASaASaASaASaA", "aASaASaASaASaCCCCCCCCCCCCCCASaASaASaASaA" };
        this.bridgeName = this.getResponder().getName() + "'s bridge";
        this.bridgeType = 0;
        this.arched = false;
        this.bridgePlan = "";
        this.page = 0;
        this.steepness = 0;
        this.start = aStart;
        this.end = aEnd;
        this.dir = aDir;
        this.width = aWidth;
        this.length = aLength;
        this.targetFloorLevel = aTargetFloorLevel;
        this.page = aPage;
        this.layer = aResponder.getLayer();
    }
    
    @Override
    public void answer(final Properties aAnswer) {
        this.setAnswer(aAnswer);
        if (this.type == 0) {
            PlanBridgeQuestion.logger.log(Level.INFO, "Received answer for a question with NOQUESTION.");
            return;
        }
        if (this.type == 116) {
            if (this.page == 0) {
                final String sAction = aAnswer.getProperty("bridgereply");
                final String[] reply = sAction.split(",");
                this.bridgeType = Byte.parseByte(reply[0]);
                if (this.bridgeType == 0) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("You decide not to build a bridge.");
                    return;
                }
                this.arched = Boolean.parseBoolean(reply[1]);
                final String sBridgeType = BridgeConstants.BridgeMaterial.fromByte(this.bridgeType).getName().toLowerCase();
                final String pre = (this.bridgeType == BridgeConstants.BridgeMaterial.ROPE.getCode()) ? "" : (this.arched ? "arched " : "flat ");
                this.bridgeName = this.getResponder().getName() + "'s " + pre + sBridgeType + " bridge";
                this.bridgePlan = reply[2];
                if (Servers.isThisATestServer() && this.getResponder().getPower() >= 2) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("(" + this.bridgeName + ":" + this.bridgePlan + ")");
                }
                if (!this.arched && this.length > 5) {
                    this.getResponder().getCommunicator().sendOpenPlanWindow(this.bridgeName, this.dir, (byte)this.length, (byte)this.width, this.start, this.end, this.bridgeType, this.bridgePlan);
                    return;
                }
                final PlanBridgeQuestion pbq = new PlanBridgeQuestion(this.getResponder(), this.targetFloorLevel, this.start, this.end, this.dir, this.width, this.length, this.bridgeType, this.arched, this.bridgePlan, this.bridgeName);
                pbq.sendQuestionPage2();
            }
            else {
                this.bridgeName = aAnswer.getProperty("bridgename");
                this.steepness = Integer.parseInt(aAnswer.getProperty("steepness"));
                if (this.steepnessSelected <= 20) {
                    PlanBridgeMethods.planBridge(this.getResponder(), this.dir, this.bridgeType, this.arched, this.bridgePlan, this.steepness, this.start, this.end, this.bridgeName);
                }
                else {
                    this.getResponder().getCommunicator().sendNormalServerMessage("You cancel planning a bridge");
                }
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        this.grabFloorLevels();
        this.bmlLines = 13;
        this.iconLines = 0;
        final StringBuilder buf = new StringBuilder();
        final StringBuilder bridges = new StringBuilder();
        boolean doneWoodCheck = false;
        boolean doneStoneCheck = false;
        String[] woodFail = { "", "" };
        String[] stoneFail = { "", "" };
        this.bridgeCount = 0;
        final boolean onSurface = this.getResponder().isOnSurface();
        String bridge = "";
        final String bridgeArea = "Planned bridge area is " + this.length + " tile" + ((this.length == 1) ? "" : "s") + " long and " + this.width + " tile" + ((this.width == 1) ? "" : "s") + " wide.";
        this.getResponder().getCommunicator().sendNormalServerMessage(bridgeArea);
        buf.append(this.getBmlHeaderWithScrollAndQuestion());
        buf.append("label{text=\"" + bridgeArea + "\"};");
        buf.append("label{type=\"bold\";text=\"Bridge types available...\"};");
        this.fail = "";
        this.reason = "";
        bridge = "";
        int maxLength = 38;
        Skill requiredSkill = this.getResponder().getSkills().getSkillOrLearn(1014);
        if (this.width != 1) {
            this.fail = "Too Wide";
            this.reason = "Rope bridges can only be 1 tile wide";
        }
        else if (this.length > maxLength) {
            this.fail = "Too Long";
            this.reason = "Rope bridges are restricted to " + maxLength + " tiles long";
        }
        else if (this.hasLowSkill(requiredSkill, 10, maxLength, true)) {
            this.fail = "Low Skill";
            if (requiredSkill.getKnowledge(0.0) < 10.0) {
                this.reason = "You need at least 10 ropemaking skill to plan any rope bridge.";
            }
            else {
                this.reason = "You dont have enough ropemaking skill for this length rope bridge.";
            }
        }
        else {
            final String[] reply = PlanBridgeMethods.isBuildingOk(BridgeConstants.BridgeMaterial.ROPE.getCode(), this.dir, onSurface, this.start, this.startFloorlevel, this.end, this.endFloorlevel);
            this.fail = reply[0];
            this.reason = reply[1];
        }
        if (this.fail.length() == 0) {
            if (this.length == 1) {
                bridge = "E";
            }
            else {
                bridge = this.makeArch("a", this.length, 'C', "A");
                this.ropeHeightsOk(bridge, this.calcMinSag());
            }
        }
        bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.ROPE, true, "Rope", bridge));
        this.fail = "";
        this.reason = "";
        bridge = "";
        maxLength = 38;
        requiredSkill = this.getResponder().getSkills().getSkillOrLearn(1005);
        if (this.width > 2) {
            this.fail = "Too Wide";
            this.reason = "Wood bridges can only be a maximum of 2 tiles wide.";
        }
        else if (this.heightDiff > 20 * this.length) {
            this.fail = "Too Steep";
            this.reason = "The slope of part of the bridge would exceed 20 dirt.";
        }
        else if (this.length > maxLength) {
            this.fail = "Too Long";
            this.reason = "Wood bridges are restricted to " + maxLength + " tiles long";
        }
        else if (this.hasLowSkill(requiredSkill, 10, maxLength, false)) {
            this.fail = "Low Skill";
            if (requiredSkill.getKnowledge(0.0) < 10.0) {
                this.reason = "You need at least 10 carpentry skill to plan any wood bridge.";
            }
            else {
                this.reason = "You dont have enough carpentry skill for this length wood bridge.";
            }
        }
        else if (this.length > 5 && (this.start.getH() < 0 || this.end.getH() < 0)) {
            this.fail = "Too Low";
            this.reason = "Both ends of a wood bridge (if it has supports) need to be 0 above water.";
        }
        else {
            final String[] reply = PlanBridgeMethods.isBuildingOk(BridgeConstants.BridgeMaterial.WOOD.getCode(), this.dir, onSurface, this.start, this.startFloorlevel, this.end, this.endFloorlevel);
            this.fail = reply[0];
            this.reason = reply[1];
            doneWoodCheck = true;
            woodFail = reply;
        }
        if (this.fail.length() > 0) {
            bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.WOOD, false, "Flat wood", bridge));
        }
        else {
            bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.WOOD, "Flat wood", this.getWoodSpan(this.length)));
        }
        this.fail = "";
        this.reason = "";
        bridge = "";
        maxLength = 38;
        requiredSkill = this.getResponder().getSkills().getSkillOrLearn(1013);
        if (this.width > 3) {
            this.fail = "Too Wide";
            this.reason = "Brick bridges are limited to 3 tiles wide.";
        }
        else if (this.heightDiff > 20 * this.length) {
            this.fail = "Too Steep";
            this.reason = "The slope of part of the bridge would exceed 20 dirt.";
        }
        else if (this.length > maxLength) {
            this.fail = "Too Long";
            this.reason = "Brick bridges are restricted to " + maxLength + " tiles long";
        }
        else if (this.hasLowSkill(requiredSkill, 30, maxLength, false)) {
            this.fail = "Low Skill";
            if (requiredSkill.getKnowledge(0.0) < 30.0) {
                this.reason = "You need at least 30 masonry skill to make any brick bridge.";
            }
            else {
                this.reason = "You dont have enough masonry skill for this length brick bridge.";
            }
        }
        else if (this.length > 5 && (this.start.getH() < 0 || this.end.getH() < 0)) {
            this.fail = "Too Low";
            this.reason = "Both ends of a Brick bridge (if it has supports) need to be 0 above water.";
        }
        else {
            final String[] reply = PlanBridgeMethods.isBuildingOk(BridgeConstants.BridgeMaterial.BRICK.getCode(), this.dir, onSurface, this.start, this.startFloorlevel, this.end, this.endFloorlevel);
            this.fail = reply[0];
            this.reason = reply[1];
            doneStoneCheck = true;
            stoneFail = reply;
        }
        if (this.fail.length() > 0) {
            bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.BRICK, false, "Flat brick", bridge));
        }
        else {
            bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.BRICK, "Flat brick", this.getBrickSpan(this.length)));
        }
        this.fail = "";
        this.reason = "";
        bridge = "";
        maxLength = 38;
        requiredSkill = this.getResponder().getSkills().getSkillOrLearn(1013);
        if (this.width > 3) {
            this.fail = "Too Wide";
            this.reason = "Marble bridges are limited to 3 tiles wide.";
        }
        else if (this.heightDiff > 20 * this.length) {
            this.fail = "Too Steep";
            this.reason = "The slope of part of the bridge would exceed 20 dirt.";
        }
        else if (this.length > maxLength) {
            this.fail = "Too Long";
            this.reason = "Marble bridges are restricted to " + maxLength + " tiles long";
        }
        else if (this.hasLowSkill(requiredSkill, 40, maxLength, false)) {
            this.fail = "Low Skill";
            if (requiredSkill.getKnowledge(0.0) < 40.0) {
                this.reason = "You need at least 40 masonry skill to make any marble bridge.";
            }
            else {
                this.reason = "You dont have enough masonry skill for this length marble bridge.";
            }
        }
        else if (this.length > 5 && (this.start.getH() < 0 || this.end.getH() < 0)) {
            this.fail = "Too Low";
            this.reason = "Both ends of a Marble bridge (if it has supports) need to be 0 above water.";
        }
        else if (doneStoneCheck) {
            this.fail = stoneFail[0];
            this.reason = stoneFail[1];
        }
        else {
            final String[] reply = PlanBridgeMethods.isBuildingOk(BridgeConstants.BridgeMaterial.MARBLE.getCode(), this.dir, onSurface, this.start, this.startFloorlevel, this.end, this.endFloorlevel);
            this.fail = reply[0];
            this.reason = reply[1];
            stoneFail = reply;
            doneStoneCheck = true;
        }
        if (this.fail.length() > 0) {
            bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.MARBLE, false, "Flat " + BridgeConstants.BridgeMaterial.MARBLE.getTextureName(), bridge));
            bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.SLATE, false, "Flat " + BridgeConstants.BridgeMaterial.SLATE.getTextureName(), bridge));
            bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.ROUNDED_STONE, false, "Flat " + BridgeConstants.BridgeMaterial.ROUNDED_STONE.getTextureName(), bridge));
            bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.POTTERY, false, "Flat " + BridgeConstants.BridgeMaterial.POTTERY.getTextureName(), bridge));
            bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.SANDSTONE, false, "Flat " + BridgeConstants.BridgeMaterial.SANDSTONE.getTextureName(), bridge));
            bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.RENDERED, false, "Flat " + BridgeConstants.BridgeMaterial.RENDERED.getTextureName(), bridge));
        }
        else {
            bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.MARBLE, "Flat " + BridgeConstants.BridgeMaterial.MARBLE.getTextureName(), this.getBrickSpan(this.length)));
            bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.SLATE, "Flat " + BridgeConstants.BridgeMaterial.SLATE.getTextureName(), this.getBrickSpan(this.length)));
            bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.ROUNDED_STONE, "Flat " + BridgeConstants.BridgeMaterial.ROUNDED_STONE.getTextureName(), this.getBrickSpan(this.length)));
            bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.POTTERY, "Flat " + BridgeConstants.BridgeMaterial.POTTERY.getTextureName(), this.getBrickSpan(this.length)));
            bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.SANDSTONE, "Flat " + BridgeConstants.BridgeMaterial.SANDSTONE.getTextureName(), this.getBrickSpan(this.length)));
            bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.RENDERED, "Flat " + BridgeConstants.BridgeMaterial.RENDERED.getTextureName(), this.getBrickSpan(this.length)));
        }
        this.fail = "";
        this.reason = "";
        bridge = "";
        if (!this.getResponder().isOnSurface()) {
            final boolean insta = this.getResponder().getPower() > 1 && Servers.isThisATestServer();
            final PlanBridgeCheckResult res = PlanBridgeChecks.checkCeilingClearance(this.getResponder(), this.length, this.start, this.end, this.dir, insta);
            if (res.failed()) {
                this.fail = "Too close";
                this.reason = res.pMsg();
                if (insta) {
                    this.getResponder().getCommunicator().sendNormalServerMessage(res.pMsg());
                }
                bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.WOOD, true, "Arched " + BridgeConstants.BridgeMaterial.WOOD.getTextureName(), bridge));
                bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.BRICK, true, "Arched " + BridgeConstants.BridgeMaterial.BRICK.getTextureName(), bridge));
                bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.MARBLE, true, "Arched " + BridgeConstants.BridgeMaterial.MARBLE.getTextureName(), bridge));
                bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.SLATE, true, "Arched " + BridgeConstants.BridgeMaterial.SLATE.getTextureName(), bridge));
                bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.ROUNDED_STONE, true, "Arched " + BridgeConstants.BridgeMaterial.ROUNDED_STONE.getTextureName(), bridge));
                bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.POTTERY, true, "Arched " + BridgeConstants.BridgeMaterial.POTTERY.getTextureName(), bridge));
                bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.SANDSTONE, true, "Arched " + BridgeConstants.BridgeMaterial.SANDSTONE.getTextureName(), bridge));
                bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.RENDERED, true, "Arched " + BridgeConstants.BridgeMaterial.RENDERED.getTextureName(), bridge));
            }
        }
        if (this.fail.length() == 0) {
            this.fail = "";
            this.reason = "";
            bridge = "";
            maxLength = 38;
            requiredSkill = this.getResponder().getSkills().getSkillOrLearn(1005);
            if (this.width > 2) {
                this.fail = "Too Wide";
                this.reason = "Wood bridges are limited to 2 tiles wide.";
            }
            else if (this.length < 2) {
                this.fail = "Too Short";
                this.reason = "Need to have a minium of 2 tiles to form an arch.";
            }
            else if (this.length * 2 > PlanBridgeMethods.getHighest().length) {
                this.fail = "Too Long";
                this.reason = "Arched wood bridges are restricted to " + (PlanBridgeMethods.getHighest().length >>> 1) + " tiles long";
            }
            else if (this.heightDiff > PlanBridgeMethods.getHighest()[this.length * 2]) {
                this.fail = "Too Steep";
                this.reason = "The slope of part of the bridge would exceed 20 dirt.";
            }
            else if (this.length > maxLength) {
                this.fail = "Too Long";
                this.reason = "Arched wood bridges are restricted to " + maxLength + " tiles long";
            }
            else if (this.hasLowSkill(requiredSkill, 20, maxLength, true)) {
                this.fail = "Low Skill";
                if (requiredSkill.getKnowledge(0.0) < 10.0) {
                    this.reason = "You need at least 10 carpentry skill to make any arched wood bridge.";
                }
                else {
                    this.reason = "You dont have enough carpentry skill for this length arched wood bridge.";
                }
            }
            else if (this.length > 11 && (this.start.getH() < 0 || this.end.getH() < 0)) {
                this.fail = "Too Low";
                this.reason = "Both ends of a wood arched bridge need to be 0 above water.";
            }
            else if (doneWoodCheck) {
                this.fail = woodFail[0];
                this.reason = woodFail[1];
            }
            else {
                final String[] reply = PlanBridgeMethods.isBuildingOk(BridgeConstants.BridgeMaterial.WOOD.getCode(), this.dir, onSurface, this.start, this.startFloorlevel, this.end, this.endFloorlevel);
                this.fail = reply[0];
                this.reason = reply[1];
            }
            if (this.fail.length() == 0) {
                bridge = this.addArchs(this.length, false);
            }
            bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.WOOD, true, "Arched wood", bridge));
            this.fail = "";
            this.reason = "";
            bridge = "";
            maxLength = 38;
            requiredSkill = this.getResponder().getSkills().getSkillOrLearn(1013);
            if (this.width > 3) {
                this.fail = "Too Wide";
                this.reason = "Brick bridges are limited to 3 tiles wide.";
            }
            else if (this.length < 2) {
                this.fail = "Too Short";
                this.reason = "Need to have a minium of 2 tiles to form an arch.";
            }
            else if (this.length * 2 > PlanBridgeMethods.getHighest().length) {
                this.fail = "Too Long";
                this.reason = "Arched brick bridges are restricted to " + (PlanBridgeMethods.getHighest().length >>> 1) + " tiles long";
            }
            else if (this.heightDiff > PlanBridgeMethods.getHighest()[this.length * 2]) {
                this.fail = "Too Steep";
                this.reason = "The slope of part of the bridge would exceed 20 dirt.";
            }
            else if (this.length > maxLength) {
                this.fail = "Too Long";
                this.reason = "Arched brick bridges are restricted to " + maxLength + " tiles long";
            }
            else if (this.hasLowSkill(requiredSkill, 50, maxLength, true)) {
                this.fail = "Low Skill";
                if (requiredSkill.getKnowledge(0.0) < 50.0) {
                    this.reason = "You need at least 50 masonry skill to make any arched brick bridge.";
                }
                else {
                    this.reason = "You dont have enough masonry skill for this length arched brick bridge.";
                }
            }
            else if (this.length > 8 && (this.start.getH() < 0 || this.end.getH() < 0)) {
                this.fail = "Too Low";
                this.reason = "Both ends of a brick arched bridge need to be 0 above water.";
            }
            else if (doneStoneCheck) {
                this.fail = stoneFail[0];
                this.reason = stoneFail[1];
            }
            else {
                final String[] reply = PlanBridgeMethods.isBuildingOk(BridgeConstants.BridgeMaterial.BRICK.getCode(), this.dir, onSurface, this.start, this.startFloorlevel, this.end, this.endFloorlevel);
                this.fail = reply[0];
                this.reason = reply[1];
                stoneFail = reply;
                doneStoneCheck = true;
            }
            if (this.fail.length() == 0) {
                bridge = this.addArchs(this.length, true);
                if (Servers.isThisATestServer() && this.getResponder().getPower() >= 2) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("(" + this.length + ":" + bridge + ")");
                }
            }
            bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.BRICK, true, "Arched brick", bridge));
            this.fail = "";
            this.reason = "";
            bridge = "";
            maxLength = 38;
            requiredSkill = this.getResponder().getSkills().getSkillOrLearn(1013);
            if (this.width > 3) {
                this.fail = "Too Wide";
                this.reason = "Marble bridges are limited to 3 tiles wide.";
            }
            else if (this.length < 2) {
                this.fail = "Too Short";
                this.reason = "Need to have a minium of 2 tiles to form an arch.";
            }
            else if (this.length * 2 > PlanBridgeMethods.getHighest().length) {
                this.fail = "Too Long";
                this.reason = "Arched marble bridges are restricted to " + (PlanBridgeMethods.getHighest().length >>> 1) + " tiles long";
            }
            else if (this.heightDiff > PlanBridgeMethods.getHighest()[this.length * 2]) {
                this.fail = "Too Steep";
                this.reason = "The slope of part of the bridge would exceed 20 dirt.";
            }
            else if (this.length > maxLength) {
                this.fail = "Too Long";
                this.reason = "Arched marble bridges are restricted to " + maxLength + " tiles long";
            }
            else if (this.hasLowSkill(requiredSkill, 60, maxLength, true)) {
                this.fail = "Low Skill";
                if (requiredSkill.getKnowledge(0.0) < 60.0) {
                    this.reason = "You need at least 60 masonry skill to make any arched marble bridge.";
                }
                else {
                    this.reason = "You dont have enough masonry skill for this length arched marble bridge.";
                }
            }
            else if (this.length > 8 && (this.start.getH() < 0 || this.end.getH() < 0)) {
                this.fail = "Too Low";
                this.reason = "Both ends of a marble arched bridge need to be 0 above water.";
            }
            else if (doneStoneCheck) {
                this.fail = stoneFail[0];
                this.reason = stoneFail[1];
            }
            else {
                final String[] reply = PlanBridgeMethods.isBuildingOk(BridgeConstants.BridgeMaterial.MARBLE.getCode(), this.dir, onSurface, this.start, this.startFloorlevel, this.end, this.endFloorlevel);
                this.fail = reply[0];
                this.reason = reply[1];
                stoneFail = reply;
                doneStoneCheck = true;
            }
            if (this.fail.length() == 0) {
                bridge = this.addArchs(this.length, true);
            }
            bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.MARBLE, true, "Arched " + BridgeConstants.BridgeMaterial.MARBLE.getTextureName(), bridge));
            bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.SLATE, true, "Arched " + BridgeConstants.BridgeMaterial.SLATE.getTextureName(), bridge));
            bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.ROUNDED_STONE, true, "Arched " + BridgeConstants.BridgeMaterial.ROUNDED_STONE.getTextureName(), bridge));
            bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.POTTERY, true, "Arched " + BridgeConstants.BridgeMaterial.POTTERY.getTextureName(), bridge));
            bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.SANDSTONE, true, "Arched " + BridgeConstants.BridgeMaterial.SANDSTONE.getTextureName(), bridge));
            bridges.append(this.addBridgeEntry(BridgeConstants.BridgeMaterial.RENDERED, true, "Arched " + BridgeConstants.BridgeMaterial.RENDERED.getTextureName(), bridge));
        }
        buf.append("table{rows=\"" + (this.bridgeCount + 1) + "\";cols=\"3\";");
        buf.append((CharSequence)bridges);
        buf.append("radio{group=\"bridgereply\";id=\"0,\";selected=\"true\"};label{text=\"None\"};label{text=\"\"};");
        buf.append("}");
        buf.append("label{text=\"\"}");
        buf.append("label{type=\"bolditalic\";text=\"Warning: As a bridge is a structure you will not be able to terraform or plant under it.\"}");
        buf.append("label{text=\"\"}");
        buf.append(this.createAnswerButton2("Next"));
        final int bmlHeight = this.bmlLines * 15 + this.iconLines * 34 + 50;
        final int bmlWidth = Math.max(480, 55 + (this.length + 2) * 34);
        this.getResponder().getCommunicator().sendBml(bmlWidth, bmlHeight, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    private String addSlopeEntry(final int maxSlope) {
        final StringBuilder buf = new StringBuilder();
        final int[] hts = PlanBridgeMethods.calcArch(this.getResponder(), maxSlope, this.length, this.start, this.end);
        boolean isOk = true;
        int i = 0;
        while (i < hts.length - 1) {
            final int slope = hts[i] - hts[i + 1];
            if (Math.abs(slope) > maxSlope + 1) {
                isOk = false;
                this.fail = "Too Steep";
                this.reason = "Part of this bridge would have over a 20 slope due to the height difference between the ends.";
                if (Servers.isThisATestServer() && this.getResponder().getPower() >= 2) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("Failed with slope " + slope + "(" + i + ") max:" + maxSlope);
                    break;
                }
                break;
            }
            else {
                ++i;
            }
        }
        if (isOk && !this.getResponder().isOnSurface()) {
            final boolean insta = this.getResponder().getPower() > 1 && Servers.isThisATestServer();
            final PlanBridgeCheckResult res = PlanBridgeChecks.checkCeilingClearance(this.start, this.end, this.dir, hts, insta);
            if (res.failed()) {
                isOk = false;
                this.fail = "Too Close";
                this.reason = res.pMsg();
            }
        }
        if (isOk && this.steepnessSelected > maxSlope) {
            this.steepnessSelected = maxSlope;
        }
        buf.append("radio{group=\"steepness\";id=\"" + maxSlope + "\";text=\"" + maxSlope + "\";enabled=\"" + isOk + "\";selected=\"" + (this.steepnessSelected == maxSlope) + "\"};");
        if (isOk) {
            buf.append("label{color=\"66,255,66\";text=\"Good\"};");
        }
        else {
            buf.append("label{color=\"255,66,66\";text=\"" + this.fail + "\";hover=\"" + this.reason + "\"};");
        }
        return buf.toString();
    }
    
    private String addSagEntry(final int testSag) {
        final StringBuilder buf = new StringBuilder();
        final String bridge = this.makeArch("a", this.length, 'C', "A");
        final boolean isOk = this.ropeHeightsOk(bridge, testSag);
        String hover = "";
        if (this.reason.length() > 0) {
            hover = ";hover=\"" + this.reason + "\"";
        }
        if (isOk && this.steepnessSelected > testSag) {
            this.steepnessSelected = testSag;
        }
        buf.append("radio{group=\"steepness\";id=\"" + testSag + "\";text=\"" + testSag + "%\";enabled=\"" + isOk + "\";selected=\"" + (this.steepnessSelected == testSag) + "\"" + hover + "};");
        if (isOk) {
            buf.append("label{color=\"66,255,66\";text=\"Good\"};");
        }
        else {
            buf.append("harray{image{src=\"img.gui.bridge.north\";size=\"12,12\";text=\"" + this.reason + "\"} label{color=\"255,66,66\";text=\"" + this.fail + "\"" + hover + "}};");
        }
        return buf.toString();
    }
    
    private boolean ropeHeightsOk(final String bridge, final int testSag) {
        this.fail = "";
        this.reason = "";
        final int[] hts = PlanBridgeMethods.calcHeights(this.getResponder(), this.dir, BridgeConstants.BridgeMaterial.ROPE.getCode(), true, bridge, testSag, this.start, this.end);
        for (int x = 0; x < hts.length - 1; ++x) {
            final int slope = Math.abs(hts[x] - hts[x + 1]);
            if (slope > 20) {
                this.fail = "Too Steep";
                this.reason = "Part of this rope bridge would have over a 20 slope due to the height difference between the ends.";
                break;
            }
        }
        if (this.fail.length() == 0) {
            int cx = this.start.getX();
            int cy = this.start.getY();
            int ch = this.start.getH();
            for (final int ht : hts) {
                final boolean onAnEnd = (cx <= this.start.getX() && cy <= this.start.getY()) || cx > this.end.getX() || cy > this.end.getY();
                final int tht = (int)(Zones.getHeightForNode(cx, cy, this.layer) * 10.0f);
                if (Servers.isThisATestServer() && this.getResponder().getPower() >= 2) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("(" + onAnEnd + " x:" + cx + " y:" + cy + " ht:" + ht + " tht:" + tht + ")");
                }
                if (!onAnEnd && ht < 5) {
                    this.fail = "Too Low";
                    this.reason = "Part of this rope bridge would be in the water.";
                    break;
                }
                if (!onAnEnd && ht < tht + 5) {
                    this.fail = "Too Low";
                    this.reason = "Part of this rope bridge would not be suspended above the ground.";
                    break;
                }
                if (ht < ch - 20 || ch < ht - 20) {
                    this.fail = "Too Steep";
                    this.reason = "Part of this rope bridge would have over a 20 slope due to the height difference between the ends.";
                    break;
                }
                ch = ht;
                if (this.dir == 0 || this.dir == 4) {
                    ++cy;
                }
                else {
                    ++cx;
                }
            }
        }
        return this.fail.length() == 0;
    }
    
    private String addBridgeEntry(final BridgeConstants.BridgeMaterial floorMaterial, final String aTypeName, final String spans) {
        this.fail = "";
        final String[] sps = spans.split(",");
        final Set<String> spanset = new HashSet<String>();
        for (final String s : sps) {
            spanset.add(s);
        }
        final StringBuilder buf = new StringBuilder();
        for (final String s2 : spanset) {
            buf.append(this.addBridgeEntry(floorMaterial, false, aTypeName, s2));
        }
        return buf.toString();
    }
    
    private String addBridge() {
        final StringBuilder buf = new StringBuilder();
        final BridgeConstants.BridgeMaterial floorMaterial = BridgeConstants.BridgeMaterial.fromByte(this.bridgeType);
        final String img = "image{src=\"img.gui.bridge.";
        final String size = "\";size=\"32,32\"";
        final String material = floorMaterial.getTextureName() + ".";
        buf.append("varray{table{rows=\"1\";cols=\"" + (this.length + 2) + "\";");
        if (this.dir == 0) {
            buf.append("image{src=\"img.gui.bridge.north\";size=\"32,32\";text=\"north\"}");
        }
        else {
            buf.append("image{src=\"img.gui.bridge.west\";size=\"32,32\";text=\"west\"}");
        }
        for (final char c : this.bridgePlan.toCharArray()) {
            buf.append("image{src=\"img.gui.bridge." + material);
            buf.append(this.getType(c));
            buf.append("\";size=\"32,32\";text=\"" + this.getAltText(c) + "\"};");
        }
        if (this.dir == 0) {
            buf.append("image{src=\"img.gui.bridge.south\";size=\"32,32\";text=\"south\"}");
        }
        else {
            buf.append("image{src=\"img.gui.bridge.east\";size=\"32,32\";text=\"east\"}");
        }
        buf.append("};");
        buf.append("image{src=\"img.gui.bridge.blank\";size=\"2,2\"}};");
        return buf.toString();
    }
    
    private String addBridgeEntry(final BridgeConstants.BridgeMaterial bridgeMaterial, final boolean aArched, final String aTypeName, final String bridge) {
        final StringBuilder buf = new StringBuilder();
        if (this.fail.length() > 0) {
            ++this.bmlLines;
            buf.append("label{text=\"\"};label{text=\"" + aTypeName + "\"};harray{image{src=\"img.gui.bridge.north\";size=\"12,12\";text=\"" + this.reason + "\"} label{color=\"255,66,66\";text=\"" + this.fail + "\";hover=\"" + this.reason + "\"}};");
        }
        else {
            ++this.iconLines;
            final String img = "image{src=\"img.gui.bridge.";
            final String size = "\";size=\"32,32\"";
            final String blank = "image{src=\"img.gui.bridge.blank\";size=\"2,34\";text=\"\"}";
            final String material = bridgeMaterial.getTextureName().replace(" ", "") + ".";
            buf.append("harray{image{src=\"img.gui.bridge.blank\";size=\"2,34\";text=\"\"};radio{group=\"bridgereply\";id=\"" + bridgeMaterial.getCode() + "," + aArched + "," + bridge + "\"}};harray{label{text=\"" + aTypeName + "\"};" + "image{src=\"img.gui.bridge.blank\";size=\"2,34\";text=\"\"}" + "};");
            buf.append("varray{table{rows=\"1\";cols=\"" + (bridge.length() + 2) + "\";");
            if (this.dir == 0) {
                buf.append("image{src=\"img.gui.bridge.north\";size=\"32,32\";text=\"north\"}");
            }
            else {
                buf.append("image{src=\"img.gui.bridge.west\";size=\"32,32\";text=\"west\"}");
            }
            for (final char c : bridge.toCharArray()) {
                buf.append("image{src=\"img.gui.bridge." + material);
                buf.append(this.getType(c));
                buf.append("\";size=\"32,32\";text=\"" + this.getAltText(c) + "\"};");
            }
            if (this.dir == 0) {
                buf.append("image{src=\"img.gui.bridge.south\";size=\"32,32\";text=\"south\"}");
            }
            else {
                buf.append("image{src=\"img.gui.bridge.east\";size=\"32,32\";text=\"east\"}");
            }
            buf.append("};");
            buf.append("image{src=\"img.gui.bridge.blank\";size=\"2,2\"}};");
        }
        ++this.bridgeCount;
        return buf.toString();
    }
    
    private String getType(final char c) {
        switch (c) {
            case 'A': {
                return "abutment.right";
            }
            case 'a': {
                return "abutment.left";
            }
            case 'B': {
                return "bracing.right";
            }
            case 'b': {
                return "bracing.left";
            }
            case 'C': {
                return "crown";
            }
            case 'D': {
                return "double";
            }
            case 'E': {
                return "end";
            }
            case 'F': {
                return "floating";
            }
            case 'S': {
                return "support";
            }
            default: {
                return "unknown";
            }
        }
    }
    
    private String getAltText(final char c) {
        switch (c) {
            case 'A':
            case 'a': {
                return "abutment";
            }
            case 'B':
            case 'b': {
                return "bracing";
            }
            case 'C': {
                return "crown";
            }
            case 'D': {
                return "double bracing";
            }
            case 'E': {
                return "double abutment";
            }
            case 'F': {
                return "floating";
            }
            case 'S': {
                return "support";
            }
            default: {
                return "unknown";
            }
        }
    }
    
    private String addArchs(final int alength, final boolean isStone) {
        if (alength == 2) {
            return "aA";
        }
        if (!isStone) {
            return this.archWood[alength];
        }
        if (alength == 3) {
            return "aDA";
        }
        if (alength < 9) {
            return this.makeArch("ab", alength, 'F', "BA");
        }
        return this.makeArch("ESab", alength, 'F', "BASE");
    }
    
    private String makeArch(final String oneEnd, final int alength, final char c, final String farEnd) {
        final int middle = alength - oneEnd.length() * 2;
        final StringBuilder buf = new StringBuilder();
        buf.append(oneEnd);
        for (int i = 0; i < middle; ++i) {
            buf.append(c);
        }
        buf.append(farEnd);
        return buf.toString();
    }
    
    private String getWoodSpan(final int alength) {
        if (alength < 8) {
            return this.spansWood[alength];
        }
        final int supports = alength / 6;
        final int spans = supports - 1;
        final int middleLen = spans * 6 + 4;
        final int rem = alength - middleLen;
        final int left = rem / 2;
        final int right = rem - left;
        final StringBuilder buf = new StringBuilder();
        buf.append("aCC".substring(0, left));
        for (int i = 0; i < spans; ++i) {
            buf.append("CASaCC");
        }
        buf.append("CASa");
        final StringBuilder bb = new StringBuilder("ACCC".substring(0, right));
        buf.append((CharSequence)bb.reverse());
        return buf.toString();
    }
    
    private String getBrickSpan(final int alength) {
        if (alength < 12) {
            return this.spansBrick[alength];
        }
        final int supports = alength / 6;
        final int spans = supports - 1;
        final int middleLen = spans * 6 + 1;
        final int rem = alength - middleLen;
        final int left = rem / 2;
        final int right = rem - left;
        final StringBuilder buf = new StringBuilder();
        buf.append(this.spansBrick[left]);
        for (int i = 0; i < spans; ++i) {
            buf.append("SabCBA");
        }
        buf.append('S');
        buf.append(this.spansBrick[right]);
        return buf.toString();
    }
    
    private boolean hasLowSkill(final Skill requiredSkill, final int minSkill, final int maxLength, final boolean slidingScale) {
        if (requiredSkill.getKnowledge(0.0) < minSkill) {
            return true;
        }
        if (slidingScale) {
            final float as = (float)Math.min(requiredSkill.getKnowledge(0.0), 99.0);
            final float k = 90.0f / (99.0f - minSkill);
            final float a = 90.0f - (as - minSkill) * k;
            final float b = (float)Math.toRadians(a);
            final float c = (float)Math.sin(b);
            final float d = 1.0f - c;
            final float r = d * (maxLength - 5) + 5.0f;
            if (r < this.length) {
                return true;
            }
        }
        return false;
    }
    
    public void sendQuestionPage2() {
        final StringBuilder buf = new StringBuilder();
        final String bridgeArea = "Planned bridge area is " + this.length + " tile" + ((this.length == 1) ? "" : "s") + " long and " + this.width + " tile" + ((this.width == 1) ? "" : "s") + " wide.";
        buf.append(this.getBmlHeaderWithScrollAndQuestion());
        buf.append("label{text=\"" + bridgeArea + "\"};");
        buf.append("label{type=\"bold\";text=\"Change bridge name here:\"}");
        buf.append("input{maxchars=\"40\";id=\"bridgename\";text=\"" + this.bridgeName + "\"}");
        buf.append("label{type=\"bolditalic\";text=\"Schematic of your bridge plan.\"}");
        buf.append(this.addBridge());
        if (this.arched && this.bridgeType != BridgeConstants.BridgeMaterial.ROPE.getCode()) {
            this.steepnessSelected = 40;
            buf.append("label{type=\"bold\";text=\"Arched bridge max steepness.\"};");
            buf.append("table{rows=\"1\";cols=\"8\";");
            buf.append(this.addSlopeEntry(5));
            buf.append(this.addSlopeEntry(10));
            buf.append(this.addSlopeEntry(15));
            buf.append(this.addSlopeEntry(20));
            buf.append("}");
        }
        else if (this.arched && this.bridgeType == BridgeConstants.BridgeMaterial.ROPE.getCode() && this.length > 1) {
            this.steepnessSelected = 40;
            final int minSag = this.calcMinSag();
            final int cols = Math.min((13 - minSag) * 2, 8);
            buf.append("label{type=\"bold\";text=\"Rope bridge max saggyness.\"};");
            buf.append("label{text=\"Your strength determines the minimum saggyness.\"};");
            buf.append("table{rows=\"1\";cols=\"" + cols + "\";");
            for (int sag = minSag; sag < 13; ++sag) {
                buf.append(this.addSagEntry(sag));
            }
            buf.append("label{text=\"\"};label{text=\"\"};label{text=\"\"};label{text=\"\"};label{text=\"\"};label{text=\"\"};");
            buf.append("}");
        }
        else {
            this.steepnessSelected = 0;
            buf.append("radio{group=\"steepness\";id=\"0\";text=\"0\";enabled=\"false\";selected=\"true\";hidden=\"true\"};");
        }
        if (this.arched && this.bridgeType != BridgeConstants.BridgeMaterial.ROPE.getCode() && this.steepnessSelected > 20) {
            buf.append("label{text=\"Could not work out a good steepness for this arched bridge.\"}");
            buf.append(this.createAnswerButton2("Cancel"));
        }
        else if (this.arched && this.bridgeType == BridgeConstants.BridgeMaterial.ROPE.getCode() && this.steepnessSelected > 12) {
            buf.append("label{text=\"Could not work out a good saggyness for this rope bridge.\"}");
            buf.append(this.createAnswerButton2("Cancel"));
        }
        else {
            buf.append("label{text=\"Once satisified with name" + ((this.bridgeType == BridgeConstants.BridgeMaterial.ROPE.getCode()) ? " and saggyness" : (this.arched ? " and steepness for arch" : "")) + ", select 'Finalise'\"}");
            buf.append(this.createAnswerButton2("Finalise"));
        }
        final String sBridgeType = BridgeConstants.BridgeMaterial.fromByte(this.bridgeType).getName().toLowerCase();
        final String sTitle = "Name your " + ((this.bridgeType == BridgeConstants.BridgeMaterial.ROPE.getCode()) ? "" : (this.arched ? "arched " : "flat ")) + sBridgeType + " bridge";
        final int bmlHeight = 230 + (this.arched ? 100 : 0);
        final int bmlWidth = Math.max(370, 55 + (this.length + 2) * 34);
        this.getResponder().getCommunicator().sendBml(bmlWidth, bmlHeight, true, true, buf.toString(), 200, 200, 200, sTitle);
    }
    
    private int calcMinSag() {
        final Skill str = this.getResponder().getSkills().getSkillOrLearn(102);
        return 12 - (int)(str.getKnowledge(0.0) / 10.0);
    }
    
    private void grabFloorLevels() {
        final int rFloorlevel = this.getResponder().getFloorLevel();
        final int tFloorlevel = this.targetFloorLevel;
        if (this.dir == 0) {
            if (this.getResponder().getTileY() == this.start.getY() - 1) {
                this.startFloorlevel = rFloorlevel;
                this.endFloorlevel = tFloorlevel;
            }
            else {
                this.startFloorlevel = tFloorlevel;
                this.endFloorlevel = rFloorlevel;
            }
            this.end.setY(this.end.getY() - 1);
        }
        else {
            if (this.getResponder().getTileX() == this.start.getX() - 1) {
                this.startFloorlevel = rFloorlevel;
                this.endFloorlevel = tFloorlevel;
            }
            else {
                this.startFloorlevel = tFloorlevel;
                this.endFloorlevel = rFloorlevel;
            }
            this.end.setX(this.end.getX() - 1);
        }
        this.heightDiff = Math.abs(this.end.getH() - this.start.getH());
    }
    
    static {
        logger = Logger.getLogger(PlanBridgeQuestion.class.getName());
    }
}
