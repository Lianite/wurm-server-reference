// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import java.util.concurrent.ConcurrentHashMap;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.Map;
import java.util.logging.Logger;

public class ItemMealData
{
    private static final Logger logger;
    private static final Map<Long, ItemMealData> mealData;
    private static final String GET_ALL_MEAL_DATA = "SELECT * FROM MEALDATA";
    private static final String CREATE_MEAL_DATA = "INSERT INTO MEALDATA(MEALID,RECIPEID,CALORIES,CARBS,FATS,PROTEINS,BONUS,STAGESCOUNT,INGREDIENTSCOUNT) VALUES(?,?,?,?,?,?,?,?,?)";
    private static final String DELETE_MEAL_DATA = "DELETE FROM MEALDATA WHERE MEALID=?";
    private static final String UPDATE_MEAL_DATA = "UPDATE MEALDATA SET RECIPEID=?,CALORIES=?,CARBS=?,FATS=?,PROTEINS=?,BONUS=? WHERE MEALID=?";
    private final long wurmId;
    private short recipeId;
    private short calories;
    private short carbs;
    private short fats;
    private short proteins;
    private byte bonus;
    private byte stagesCount;
    private byte ingredientsCount;
    
    public ItemMealData(final long mealId, final short recipeId, final short calorie, final short carb, final short fat, final short protein, final byte bonus, final byte stages, final byte ingredients) {
        this.wurmId = mealId;
        this.recipeId = recipeId;
        this.calories = calorie;
        this.carbs = carb;
        this.fats = fat;
        this.proteins = protein;
        this.bonus = bonus;
        this.stagesCount = stages;
        this.ingredientsCount = ingredients;
    }
    
    public long getMealId() {
        return this.wurmId;
    }
    
    public short getRecipeId() {
        return this.recipeId;
    }
    
    public short getCalories() {
        return this.calories;
    }
    
    public short getCarbs() {
        return this.carbs;
    }
    
    public short getFats() {
        return this.fats;
    }
    
    public short getProteins() {
        return this.proteins;
    }
    
    public byte getBonus() {
        return this.bonus;
    }
    
    public byte getStages() {
        return this.stagesCount;
    }
    
    public byte getIngredients() {
        return this.ingredientsCount;
    }
    
    public byte getBonus(final long playerId) {
        return (byte)((int)(this.bonus + (playerId >> 24)) & 0xFF);
    }
    
    boolean update(final short recipeId, final short calorie, final short carb, final short fat, final short protein, final byte bonus) {
        this.recipeId = recipeId;
        this.calories = calorie;
        this.carbs = carb;
        this.fats = fat;
        this.proteins = protein;
        this.bonus = bonus;
        return this.dbUpdateMealData();
    }
    
    private boolean dbUpdateMealData() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("UPDATE MEALDATA SET RECIPEID=?,CALORIES=?,CARBS=?,FATS=?,PROTEINS=?,BONUS=? WHERE MEALID=?");
            ps.setShort(1, this.recipeId);
            ps.setShort(2, this.calories);
            ps.setShort(3, this.carbs);
            ps.setShort(4, this.fats);
            ps.setShort(5, this.proteins);
            ps.setByte(6, this.bonus);
            ps.setLong(7, this.wurmId);
            return ps.executeUpdate() == 1 || this.dbSaveMealData();
        }
        catch (SQLException sqex) {
            ItemMealData.logger.log(Level.WARNING, "Failed to update item (meal) data: " + sqex.getMessage(), sqex);
            return false;
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private boolean dbSaveMealData() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("INSERT INTO MEALDATA(MEALID,RECIPEID,CALORIES,CARBS,FATS,PROTEINS,BONUS,STAGESCOUNT,INGREDIENTSCOUNT) VALUES(?,?,?,?,?,?,?,?,?)");
            ps.setLong(1, this.wurmId);
            ps.setShort(2, this.recipeId);
            ps.setShort(3, this.calories);
            ps.setShort(4, this.carbs);
            ps.setShort(5, this.fats);
            ps.setShort(6, this.proteins);
            ps.setByte(7, this.bonus);
            ps.setByte(8, this.stagesCount);
            ps.setByte(9, this.ingredientsCount);
            ps.executeUpdate();
            return true;
        }
        catch (SQLException sqex) {
            ItemMealData.logger.log(Level.WARNING, "Failed to save item (meal) data: " + sqex.getMessage(), sqex);
            return false;
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private boolean dbDeleteMealData() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("DELETE FROM MEALDATA WHERE MEALID=?");
            ps.setLong(1, this.wurmId);
            ps.executeUpdate();
            return true;
        }
        catch (SQLException sqex) {
            ItemMealData.logger.log(Level.WARNING, "Failed to delete item (meal) data: " + sqex.getMessage(), sqex);
            return false;
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static final ItemMealData add(final ItemMealData itemData) {
        return ItemMealData.mealData.put(itemData.getMealId(), itemData);
    }
    
    public static ItemMealData getItemMealData(final long mealId) {
        return ItemMealData.mealData.get(mealId);
    }
    
    public static final void save(final long mealId, final short recipeId, final short calories, final short carbs, final short fats, final short proteins, final byte bonus, final byte stages, final byte ingredients) {
        final ItemMealData imd = new ItemMealData(mealId, recipeId, calories, carbs, fats, proteins, bonus, stages, ingredients);
        if (add(imd) != null) {
            imd.dbUpdateMealData();
        }
        else {
            imd.dbSaveMealData();
        }
    }
    
    public static final void update(final long mealId, final short recipeId, final short calories, final short carbs, final short fats, final short proteins, final byte bonus, final byte stages, final byte ingredients) {
        final ItemMealData imd = ItemMealData.mealData.get(mealId);
        if (imd != null) {
            imd.update(recipeId, calories, carbs, fats, proteins, bonus);
        }
        else {
            save(mealId, recipeId, calories, carbs, fats, proteins, bonus, stages, ingredients);
        }
    }
    
    public static final boolean delete(final long mealId) {
        final ItemMealData imd = ItemMealData.mealData.get(mealId);
        return imd != null && imd.dbDeleteMealData();
    }
    
    public static final int loadAllMealData() {
        int count = 0;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM MEALDATA");
            rs = ps.executeQuery();
            while (rs.next()) {
                ++count;
                final long mealId = rs.getLong("MEALID");
                final short recipeId = rs.getShort("RECIPEID");
                final short calories = rs.getShort("CALORIES");
                final short carbs = rs.getShort("CARBS");
                final short fats = rs.getShort("FATS");
                final short proteins = rs.getShort("PROTEINS");
                final byte bonus = rs.getByte("BONUS");
                final byte stages = rs.getByte("STAGESCOUNT");
                final byte ingredients = rs.getByte("INGREDIENTSCOUNT");
                add(new ItemMealData(mealId, recipeId, calories, carbs, fats, proteins, bonus, stages, ingredients));
            }
        }
        catch (SQLException sqex) {
            ItemMealData.logger.log(Level.WARNING, "Failed to load all meal data: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return count;
    }
    
    static {
        logger = Logger.getLogger(ItemMealData.class.getName());
        mealData = new ConcurrentHashMap<Long, ItemMealData>();
    }
}
