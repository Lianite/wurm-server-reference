// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import sun.misc.BASE64Encoder;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import com.wurmonline.shared.exceptions.WurmServerException;
import java.security.MessageDigest;
import java.util.logging.Logger;

public final class BatchEncrypter
{
    private static final String getPlayers = "select * from PLAYERS";
    private static final String updatePw = "update PLAYERS set PASSWORD=? where NAME=?";
    private static Logger logger;
    protected static final String destroyString = "ALTER TABLE PLAYERS DROP COLUMN PASSWORD";
    protected static final String createString = "ALTER TABLE PLAYERS ADD PASSWORD VARCHAR(30)";
    
    public static String encrypt(final String plaintext) throws Exception {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA");
        }
        catch (NoSuchAlgorithmException e) {
            throw new WurmServerException("No such algorithm 'SHA'");
        }
        try {
            md.update(plaintext.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e2) {
            throw new WurmServerException("No such encoding: UTF-8");
        }
        final byte[] raw = md.digest();
        final String hash = new BASE64Encoder().encode(raw);
        return hash;
    }
    
    public static void encryptPasswords() {
        try {
            final Connection dbcon = DbConnector.getPlayerDbCon();
            final PreparedStatement ps = dbcon.prepareStatement("select * from PLAYERS");
            final ResultSet rs = ps.executeQuery();
            final PreparedStatement destroy = dbcon.prepareStatement("ALTER TABLE PLAYERS DROP COLUMN PASSWORD");
            destroy.execute();
            destroy.close();
            final PreparedStatement create = dbcon.prepareStatement("ALTER TABLE PLAYERS ADD PASSWORD VARCHAR(30)");
            create.execute();
            create.close();
            while (rs.next()) {
                final String password = rs.getString("PASSWORD");
                final String name = rs.getString("NAME");
                String newPw = "";
                try {
                    newPw = encrypt(name + password);
                }
                catch (Exception ex) {
                    BatchEncrypter.logger.log(Level.WARNING, ex.getMessage(), ex);
                }
                final PreparedStatement ps2 = dbcon.prepareStatement("update PLAYERS set PASSWORD=? where NAME=?");
                ps2.setString(1, newPw);
                ps2.setString(2, name);
                ps2.executeUpdate();
                ps2.close();
            }
            ps.close();
            DbConnector.closeAll();
        }
        catch (Exception ex2) {
            BatchEncrypter.logger.log(Level.INFO, ex2.getMessage(), ex2);
        }
    }
    
    static {
        BatchEncrypter.logger = Logger.getLogger(BatchEncrypter.class.getName());
    }
}
