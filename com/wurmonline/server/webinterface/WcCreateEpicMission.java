// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import java.io.IOException;
import com.wurmonline.server.epic.EpicServerStatus;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import com.wurmonline.shared.util.StreamUtilities;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public class WcCreateEpicMission extends WebCommand implements MiscConstants
{
    private static final Logger logger;
    private int collectiblesToWin;
    private int collectiblesForWurmToWin;
    private boolean spawnPointRequiredToWin;
    private int hexNumRequiredToWin;
    private int scenarioNumber;
    private int reasonPlusEffect;
    private String scenarioName;
    private String scenarioQuest;
    public long entityNumber;
    private String entityName;
    private int difficulty;
    private long maxTimeSeconds;
    private boolean destroyPreviousMissions;
    
    public WcCreateEpicMission(final long a_id, final String scenName, final int scenNumber, final int reasonEff, final int collReq, final int collReqWurm, final boolean spawnP, final int hexNumReq, final String questString, final long epicEntity, final int diff, final String epicEntityName, final long maxTimeSecs, final boolean destroyPrevMissions) {
        super(a_id, (short)11);
        this.collectiblesToWin = 5;
        this.collectiblesForWurmToWin = 8;
        this.spawnPointRequiredToWin = true;
        this.hexNumRequiredToWin = 0;
        this.scenarioNumber = 0;
        this.reasonPlusEffect = 0;
        this.scenarioName = "";
        this.scenarioQuest = "";
        this.entityNumber = 0L;
        this.entityName = "unknown";
        this.difficulty = 0;
        this.maxTimeSeconds = 0L;
        this.destroyPreviousMissions = false;
        this.scenarioName = scenName;
        this.scenarioNumber = scenNumber;
        this.reasonPlusEffect = reasonEff;
        this.collectiblesToWin = collReq;
        this.collectiblesForWurmToWin = collReqWurm;
        this.spawnPointRequiredToWin = spawnP;
        this.hexNumRequiredToWin = hexNumReq;
        this.scenarioQuest = questString;
        this.entityNumber = epicEntity;
        this.difficulty = diff;
        this.entityName = epicEntityName;
        this.maxTimeSeconds = maxTimeSecs;
        this.destroyPreviousMissions = destroyPrevMissions;
        this.isRestrictedEpic = true;
    }
    
    public WcCreateEpicMission(final long aId, final byte[] _data) {
        super(aId, (short)11, _data);
        this.collectiblesToWin = 5;
        this.collectiblesForWurmToWin = 8;
        this.spawnPointRequiredToWin = true;
        this.hexNumRequiredToWin = 0;
        this.scenarioNumber = 0;
        this.reasonPlusEffect = 0;
        this.scenarioName = "";
        this.scenarioQuest = "";
        this.entityNumber = 0L;
        this.entityName = "unknown";
        this.difficulty = 0;
        this.maxTimeSeconds = 0L;
        this.destroyPreviousMissions = false;
        this.isRestrictedEpic = true;
    }
    
    @Override
    public boolean autoForward() {
        return true;
    }
    
    @Override
    byte[] encode() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = null;
        byte[] barr = null;
        try {
            dos = new DataOutputStream(bos);
            dos.writeUTF(this.scenarioName);
            dos.writeInt(this.scenarioNumber);
            dos.writeInt(this.reasonPlusEffect);
            dos.writeInt(this.collectiblesToWin);
            dos.writeInt(this.collectiblesForWurmToWin);
            dos.writeBoolean(this.spawnPointRequiredToWin);
            dos.writeInt(this.hexNumRequiredToWin);
            dos.writeUTF(this.scenarioQuest);
            dos.writeLong(this.entityNumber);
            dos.writeInt(this.difficulty);
            dos.writeUTF(this.entityName);
            dos.writeLong(this.maxTimeSeconds);
            dos.writeBoolean(this.destroyPreviousMissions);
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcCreateEpicMission.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeOutputStreamIgnoreExceptions(dos);
            barr = bos.toByteArray();
            StreamUtilities.closeOutputStreamIgnoreExceptions(bos);
            this.setData(barr);
        }
        return barr;
    }
    
    @Override
    public void execute() {
        new Thread() {
            @Override
            public void run() {
                DataInputStream dis = null;
                try {
                    dis = new DataInputStream(new ByteArrayInputStream(WcCreateEpicMission.this.getData()));
                    WcCreateEpicMission.this.scenarioName = dis.readUTF();
                    WcCreateEpicMission.this.scenarioNumber = dis.readInt();
                    WcCreateEpicMission.this.reasonPlusEffect = dis.readInt();
                    WcCreateEpicMission.this.collectiblesToWin = dis.readInt();
                    WcCreateEpicMission.this.collectiblesForWurmToWin = dis.readInt();
                    WcCreateEpicMission.this.spawnPointRequiredToWin = dis.readBoolean();
                    WcCreateEpicMission.this.hexNumRequiredToWin = dis.readInt();
                    WcCreateEpicMission.this.scenarioQuest = dis.readUTF();
                    WcCreateEpicMission.this.entityNumber = dis.readLong();
                    WcCreateEpicMission.this.difficulty = dis.readInt();
                    WcCreateEpicMission.this.entityName = dis.readUTF();
                    WcCreateEpicMission.this.maxTimeSeconds = dis.readLong();
                    WcCreateEpicMission.this.destroyPreviousMissions = dis.readBoolean();
                    if (EpicServerStatus.getCurrentScenario().getScenarioNumber() != WcCreateEpicMission.this.scenarioNumber) {
                        EpicServerStatus.getCurrentScenario().saveScenario(false);
                        EpicServerStatus.getCurrentScenario().setScenarioQuest(WcCreateEpicMission.this.scenarioQuest);
                        EpicServerStatus.getCurrentScenario().setScenarioName(WcCreateEpicMission.this.scenarioName);
                        EpicServerStatus.getCurrentScenario().setScenarioNumber(WcCreateEpicMission.this.scenarioNumber);
                        EpicServerStatus.getCurrentScenario().setReasonPlusEffect(WcCreateEpicMission.this.reasonPlusEffect);
                        EpicServerStatus.getCurrentScenario().setCollectiblesToWin(WcCreateEpicMission.this.collectiblesToWin);
                        EpicServerStatus.getCurrentScenario().setCollectiblesForWurmToWin(WcCreateEpicMission.this.collectiblesForWurmToWin);
                        EpicServerStatus.getCurrentScenario().setHexNumRequiredToWin(WcCreateEpicMission.this.hexNumRequiredToWin);
                        EpicServerStatus.getCurrentScenario().saveScenario(true);
                    }
                    final EpicServerStatus es = new EpicServerStatus();
                    es.generateNewMissionForEpicEntity((int)WcCreateEpicMission.this.entityNumber, WcCreateEpicMission.this.entityName, WcCreateEpicMission.this.difficulty, (int)WcCreateEpicMission.this.maxTimeSeconds, WcCreateEpicMission.this.scenarioName, WcCreateEpicMission.this.scenarioNumber, WcCreateEpicMission.this.scenarioQuest, WcCreateEpicMission.this.destroyPreviousMissions);
                }
                catch (IOException ex) {
                    WcCreateEpicMission.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
                }
                finally {
                    StreamUtilities.closeInputStreamIgnoreExceptions(dis);
                }
            }
        }.start();
    }
    
    static {
        logger = Logger.getLogger(WcCreateEpicMission.class.getName());
    }
}
