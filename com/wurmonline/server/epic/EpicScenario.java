// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.epic;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class EpicScenario implements MiscConstants
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
    private boolean current;
    private static final String INSERTSCENARIO = "INSERT INTO SCENARIOS (NAME,REASONEFF,COLLREQ,COLLWURMREQ,SPAWNREQ,HEXREQ,QUESTSTRING,CURRENT,NUMBER) VALUES (?,?,?,?,?,?,?,?,?)";
    private static final String UPDATESCENARIO = "UPDATE SCENARIOS SET NAME=?,REASONEFF=?,COLLREQ=?,COLLWURMREQ=?,SPAWNREQ=?,HEXREQ=?,QUESTSTRING=?,CURRENT=? WHERE NUMBER=?";
    private static final String LOADCURRENTSCENARIO = "SELECT * FROM SCENARIOS WHERE CURRENT=1";
    
    public EpicScenario() {
        this.collectiblesToWin = 5;
        this.collectiblesForWurmToWin = 8;
        this.spawnPointRequiredToWin = true;
        this.hexNumRequiredToWin = 0;
        this.scenarioNumber = 0;
        this.reasonPlusEffect = 0;
        this.scenarioName = "";
        this.scenarioQuest = "";
        this.current = false;
    }
    
    boolean loadCurrentScenario() {
        boolean toReturn = false;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getDeityDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM SCENARIOS WHERE CURRENT=1");
            rs = ps.executeQuery();
            while (rs.next()) {
                this.scenarioName = rs.getString("NAME");
                this.scenarioNumber = rs.getInt("NUMBER");
                this.reasonPlusEffect = rs.getInt("REASONEFF");
                this.collectiblesToWin = rs.getInt("COLLREQ");
                this.collectiblesForWurmToWin = rs.getInt("COLLWURMREQ");
                this.spawnPointRequiredToWin = rs.getBoolean("SPAWNREQ");
                this.hexNumRequiredToWin = rs.getInt("HEXREQ");
                this.scenarioQuest = rs.getString("QUESTSTRING");
                this.current = true;
                EpicScenario.logger.log(Level.INFO, "Loaded current scenario " + this.scenarioName);
                toReturn = true;
            }
        }
        catch (SQLException sqx) {
            EpicScenario.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return toReturn;
    }
    
    public final void saveScenario(final boolean _current) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        this.current = _current;
        try {
            dbcon = DbConnector.getDeityDbCon();
            if (this.current) {
                ps = dbcon.prepareStatement("INSERT INTO SCENARIOS (NAME,REASONEFF,COLLREQ,COLLWURMREQ,SPAWNREQ,HEXREQ,QUESTSTRING,CURRENT,NUMBER) VALUES (?,?,?,?,?,?,?,?,?)");
            }
            else {
                ps = dbcon.prepareStatement("UPDATE SCENARIOS SET NAME=?,REASONEFF=?,COLLREQ=?,COLLWURMREQ=?,SPAWNREQ=?,HEXREQ=?,QUESTSTRING=?,CURRENT=? WHERE NUMBER=?");
            }
            ps.setString(1, this.scenarioName);
            ps.setInt(2, this.reasonPlusEffect);
            ps.setInt(3, this.collectiblesToWin);
            ps.setInt(4, this.collectiblesForWurmToWin);
            ps.setBoolean(5, this.spawnPointRequiredToWin);
            ps.setInt(6, this.hexNumRequiredToWin);
            ps.setString(7, this.scenarioQuest);
            ps.setBoolean(8, this.current);
            ps.setInt(9, this.scenarioNumber);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            EpicScenario.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public int getCollectiblesToWin() {
        return this.collectiblesToWin;
    }
    
    public void setCollectiblesToWin(final int aCollectiblesToWin) {
        this.collectiblesToWin = aCollectiblesToWin;
    }
    
    public int getCollectiblesForWurmToWin() {
        return this.collectiblesForWurmToWin;
    }
    
    public void setCollectiblesForWurmToWin(final int aCollectiblesForWurmToWin) {
        this.collectiblesForWurmToWin = aCollectiblesForWurmToWin;
    }
    
    public boolean isSpawnPointRequiredToWin() {
        return this.getHexNumRequiredToWin() <= 0 || this.spawnPointRequiredToWin;
    }
    
    public void setSpawnPointRequiredToWin(final boolean aSpawnPointRequiredToWin) {
        this.spawnPointRequiredToWin = aSpawnPointRequiredToWin;
    }
    
    public int getHexNumRequiredToWin() {
        return this.hexNumRequiredToWin;
    }
    
    public void setHexNumRequiredToWin(final int aHexNumRequiredToWin) {
        this.hexNumRequiredToWin = aHexNumRequiredToWin;
    }
    
    public int getScenarioNumber() {
        return this.scenarioNumber;
    }
    
    public void setScenarioNumber(final int aScenarioNumber) {
        this.scenarioNumber = aScenarioNumber;
    }
    
    void incrementScenarioNumber() {
        ++this.scenarioNumber;
    }
    
    public int getReasonPlusEffect() {
        return this.reasonPlusEffect;
    }
    
    public void setReasonPlusEffect(final int aReasonPlusEffect) {
        this.reasonPlusEffect = aReasonPlusEffect;
    }
    
    public String getScenarioName() {
        return this.scenarioName;
    }
    
    public void setScenarioName(final String aScenarioName) {
        this.scenarioName = aScenarioName;
    }
    
    public String getScenarioQuest() {
        return this.scenarioQuest;
    }
    
    public void setScenarioQuest(final String aScenarioQuest) {
        this.scenarioQuest = aScenarioQuest;
    }
    
    public boolean isCurrent() {
        return this.current;
    }
    
    public void setCurrent(final boolean aCurrent) {
        this.current = aCurrent;
    }
    
    static {
        logger = Logger.getLogger(EpicScenario.class.getName());
    }
}
