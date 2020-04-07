// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.sql.Connection;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.io.IOException;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import com.wurmonline.server.Items;
import java.util.logging.Logger;

public final class InscriptionData
{
    private String inscription;
    private final long wurmid;
    private String inscriber;
    private int penColour;
    private static final Logger logger;
    private static final String legalInscriptionChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'- 1234567890.,+/!() ;:_#";
    
    public InscriptionData(final long wid, final String theData, final String theInscriber, final int thePenColour) {
        this.wurmid = wid;
        this.setInscription(theData);
        this.setInscriber(theInscriber);
        this.penColour = thePenColour;
        Items.addItemInscriptionData(this);
    }
    
    public InscriptionData(final long wid, final Recipe recipe, final String theInscriber, final int thePenColour) {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(bos);
        try {
            recipe.pack(dos);
            dos.flush();
            dos.close();
        }
        catch (IOException e) {
            InscriptionData.logger.log(Level.WARNING, e.getMessage(), e);
        }
        final byte[] data = bos.toByteArray();
        final String base64encodedRecipe = Base64.getEncoder().encodeToString(data);
        this.wurmid = wid;
        this.setInscription(base64encodedRecipe);
        this.setInscriber(theInscriber);
        this.penColour = thePenColour;
        Items.addItemInscriptionData(this);
    }
    
    public String getInscription() {
        return this.inscription;
    }
    
    @Nullable
    public Recipe getRecipe() {
        final byte[] bytes = Base64.getDecoder().decode(this.inscription);
        final DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
        try {
            return new Recipe(dis);
        }
        catch (NoSuchTemplateException e) {
            InscriptionData.logger.log(Level.WARNING, e.getMessage(), e);
        }
        catch (IOException e2) {
            InscriptionData.logger.log(Level.WARNING, e2.getMessage(), e2);
        }
        return null;
    }
    
    public void setInscription(final String newInscription) {
        this.inscription = newInscription;
    }
    
    public String getInscriber() {
        return this.inscriber;
    }
    
    public void setInscriber(final String aInscriber) {
        this.inscriber = aInscriber;
    }
    
    public int getPenColour() {
        return this.penColour;
    }
    
    public void setPenColour(final int newColour) {
        this.penColour = newColour;
    }
    
    public long getWurmId() {
        return this.wurmid;
    }
    
    public boolean hasBeenInscribed() {
        return this.getInscription() != null && this.getInscription().length() > 0;
    }
    
    public void createInscriptionEntry(final Connection dbcon) {
        PreparedStatement ps = null;
        try {
            ps = dbcon.prepareStatement(ItemDbStrings.getInstance().createInscription());
            ps.setLong(1, this.getWurmId());
            ps.setString(2, this.getInscription());
            ps.setString(3, this.getInscriber());
            ps.setInt(4, this.getPenColour());
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            InscriptionData.logger.log(Level.WARNING, "Failed to save inscription data " + this.getWurmId(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
        }
    }
    
    public static final boolean containsIllegalCharacters(final String name) {
        final char[] chars = name.toCharArray();
        for (int x = 0; x < chars.length; ++x) {
            if ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'- 1234567890.,+/!() ;:_#".indexOf(chars[x]) < 0) {
                return true;
            }
        }
        return false;
    }
    
    static {
        logger = Logger.getLogger(ItemData.class.getName());
    }
}
