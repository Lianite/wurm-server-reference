// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import com.wurmonline.server.players.Player;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Players;
import com.wurmonline.server.creatures.Creature;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Collection;
import java.util.Set;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class RecipesByPlayer implements MiscConstants
{
    private static final Logger logger;
    private static final Map<Long, RecipesByPlayer> playerRecipes;
    private static final String GET_ALL_PLAYER_RECIPES = "SELECT * FROM RECIPESPLAYER";
    private static final String GET_ALL_PLAYER_COOKERS = "SELECT * FROM RECIPEPLAYERCOOKERS";
    private static final String GET_ALL_PLAYER_CONTAINERS = "SELECT * FROM RECIPEPLAYERCONTAINERS";
    private static final String GET_ALL_PLAYER_INGREDIENTS = "SELECT * FROM RECIPEPLAYERINGREDIENTS";
    private static final String CREATE_PLAYER_RECIPE = "INSERT INTO RECIPESPLAYER (PLAYERID,RECIPEID,FAVOURITE,NOTES) VALUES(?,?,?,?)";
    private static final String CREATE_PLAYER_RECIPE_COOKER = "INSERT INTO RECIPEPLAYERCOOKERS (PLAYERID,RECIPEID,COOKERID) VALUES(?,?,?)";
    private static final String CREATE_PLAYER_RECIPE_CONTAINER = "INSERT INTO RECIPEPLAYERCONTAINERS (PLAYERID,RECIPEID,CONTAINERID) VALUES(?,?,?)";
    private static final String CREATE_PLAYER_RECIPE_INGREDIENT = "INSERT INTO RECIPEPLAYERINGREDIENTS (PLAYERID,RECIPEID,INGREDIENTID,GROUPID,TEMPLATEID,CSTATE,PSTATE,MATERIAL,REALTEMPLATEID) VALUES(?,?,?,?,?,?,?,?,?)";
    private static final String UPDATE_PLAYER_RECIPE_FAVOURITE = "UPDATE RECIPESPLAYER SET FAVOURITE=? WHERE PLAYERID=? AND RECIPEID=?";
    private static final String UPDATE_PLAYER_RECIPE_NOTES = "UPDATE RECIPESPLAYER SET NOTES=? WHERE PLAYERID=? AND RECIPEID=?";
    private static final String UPDATE_PLAYER_RECIPE_INGREDIENT = "UPDATE RECIPEPLAYERINGREDIENTS SET TEMPLATEID=?,CSTATE=?,PSTATE=?,MATERIAL=?,REALTEMPLATEID=? WHERE PLAYERID=? AND RECIPEID=? AND INGREDIENTID=?";
    private static final String DELETE_PLAYER_RECIPES = "DELETE FROM RECIPESPLAYER WHERE PLAYERID=?";
    private static final String DELETE_PLAYER_RECIPES_COOKERS = "DELETE FROM RECIPEPLAYERCOOKERS WHERE PLAYERID=?";
    private static final String DELETE_PLAYER_RECIPES_CONTAINERS = "DELETE FROM RECIPEPLAYERCONTAINERS WHERE PLAYERID=?";
    private static final String DELETE_PLAYER_RECIPES_INGREDIENTS = "DELETE FROM RECIPEPLAYERINGREDIENTS WHERE PLAYERID=?";
    private static final String DELETE_PLAYER_RECIPE = "DELETE FROM RECIPESPLAYER WHERE PLAYERID=? AND RECIPEID=?";
    private static final String DELETE_PLAYER_RECIPE_COOKERS = "DELETE FROM RECIPEPLAYERCOOKERS WHERE PLAYERID=? AND RECIPEID=?";
    private static final String DELETE_PLAYER_RECIPE_CONTAINERS = "DELETE FROM RECIPEPLAYERCONTAINERS WHERE PLAYERID=? AND RECIPEID=?";
    private static final String DELETE_PLAYER_RECIPE_INGREDIENTS = "DELETE FROM RECIPEPLAYERINGREDIENTS WHERE PLAYERID=? AND RECIPEID=?";
    private static final String DELETE_ALL_PLAYER_RECIPES = "DELETE FROM RECIPESPLAYER";
    private static final String DELETE_ALL_PLAYER_RECIPE_COOKERS = "DELETE FROM RECIPEPLAYERCOOKERS";
    private static final String DELETE_ALL_PLAYER_RECIPE_CONTAINERS = "DELETE FROM RECIPEPLAYERCONTAINERS";
    private static final String DELETE_ALL_PLAYER_RECIPE_INGREDIENTS = "DELETE FROM RECIPEPLAYERINGREDIENTS";
    private final long wurmId;
    private final Map<Short, Recipe> knownRecipes;
    private final Map<Short, Boolean> playerFavourites;
    private final Map<Short, String> playerNotes;
    
    public RecipesByPlayer(final long playerId) {
        this.knownRecipes = new ConcurrentHashMap<Short, Recipe>();
        this.playerFavourites = new ConcurrentHashMap<Short, Boolean>();
        this.playerNotes = new ConcurrentHashMap<Short, String>();
        this.wurmId = playerId;
    }
    
    public long getPlayerId() {
        return this.wurmId;
    }
    
    public void addRecipe(final Recipe recipe) {
        this.knownRecipes.put(recipe.getRecipeId(), recipe);
    }
    
    @Nullable
    public Recipe getRecipe(final short recipeId) {
        return this.knownRecipes.get(recipeId);
    }
    
    public boolean isKnownRecipe(final short recipeId) {
        return this.knownRecipes.containsKey(recipeId);
    }
    
    public void removeRecipe(final short recipeId) {
        this.knownRecipes.remove(recipeId);
    }
    
    boolean setFavourite(final short recipeId, final boolean isFavourite) {
        final Boolean wasFavourite = this.playerFavourites.put(recipeId, isFavourite);
        if (!this.playerNotes.containsKey(recipeId)) {
            this.playerNotes.put(recipeId, "");
        }
        return wasFavourite == null || wasFavourite != isFavourite;
    }
    
    boolean setNotes(final short recipeId, final String notes) {
        final String newNotes = notes.substring(0, Math.min(notes.length(), 200));
        final String oldNotes = this.playerNotes.put(recipeId, newNotes);
        if (!this.playerFavourites.containsKey(recipeId)) {
            this.playerFavourites.put(recipeId, false);
        }
        return oldNotes == null || !oldNotes.equals(newNotes);
    }
    
    boolean isFavourite(final short recipeId) {
        final Boolean isFavourite = this.playerFavourites.get(recipeId);
        return isFavourite != null && isFavourite;
    }
    
    String getNotes(final short recipeId) {
        final String notes = this.playerNotes.get(recipeId);
        if (notes != null) {
            return notes.substring(0, Math.min(notes.length(), 200));
        }
        return "";
    }
    
    public static final int loadAllPlayerKnownRecipes() {
        int count = 0;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM RECIPESPLAYER");
            rs = ps.executeQuery();
            while (rs.next()) {
                final long playerId = rs.getLong("PLAYERID");
                final short recipeId = rs.getShort("RECIPEID");
                final boolean favourite = rs.getBoolean("FAVOURITE");
                final String notes = rs.getString("NOTES");
                final Recipe templateRecipe = Recipes.getRecipeById(recipeId);
                if (templateRecipe != null) {
                    final RecipesByPlayer rbp = getRecipesByPlayer(playerId, true);
                    rbp.setFavourite(recipeId, favourite);
                    rbp.setNotes(recipeId, notes);
                    if (templateRecipe.isKnown()) {
                        continue;
                    }
                    ++count;
                    final Recipe playerRecipe = new Recipe(recipeId);
                    rbp.addRecipe(playerRecipe);
                }
                else {
                    RecipesByPlayer.logger.log(Level.WARNING, "Known recipe is not found in templates " + recipeId + " for player " + playerId);
                }
            }
        }
        catch (SQLException sqex) {
            RecipesByPlayer.logger.log(Level.WARNING, "Failed to load all player known recipes: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
        }
        try {
            assert dbcon != null;
            ps = dbcon.prepareStatement("SELECT * FROM RECIPEPLAYERCOOKERS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final long playerId = rs.getLong("PLAYERID");
                final short recipeId = rs.getShort("RECIPEID");
                final short cookerId = rs.getShort("COOKERID");
                final RecipesByPlayer rbp2 = getRecipesByPlayer(playerId, false);
                if (rbp2 != null) {
                    final Recipe recipe = rbp2.getRecipe(recipeId);
                    if (recipe == null) {
                        continue;
                    }
                    recipe.addToCookerList(cookerId);
                }
            }
        }
        catch (SQLException sqex) {
            RecipesByPlayer.logger.log(Level.WARNING, "Failed to load all player known recipes: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
        }
        try {
            ps = dbcon.prepareStatement("SELECT * FROM RECIPEPLAYERCONTAINERS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final long playerId = rs.getLong("PLAYERID");
                final short recipeId = rs.getShort("RECIPEID");
                final short containerId = rs.getShort("CONTAINERID");
                final RecipesByPlayer rbp2 = getRecipesByPlayer(playerId, false);
                if (rbp2 != null) {
                    final Recipe recipe = rbp2.getRecipe(recipeId);
                    if (recipe == null) {
                        continue;
                    }
                    recipe.addToContainerList(containerId);
                }
            }
        }
        catch (SQLException sqex) {
            RecipesByPlayer.logger.log(Level.WARNING, "Failed to load all player known recipes: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
        }
        try {
            ps = dbcon.prepareStatement("SELECT * FROM RECIPEPLAYERINGREDIENTS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final long playerId = rs.getLong("PLAYERID");
                final short recipeId = rs.getShort("RECIPEID");
                final byte ingredientId = rs.getByte("INGREDIENTID");
                final byte groupId = rs.getByte("GROUPID");
                final short templateId = rs.getShort("TEMPLATEID");
                final byte cstate = rs.getByte("CSTATE");
                final byte pstate = rs.getByte("PSTATE");
                final byte material = rs.getByte("MATERIAL");
                final short realTemplateId = rs.getShort("REALTEMPLATEID");
                final RecipesByPlayer rbp3 = getRecipesByPlayer(playerId, false);
                if (rbp3 != null) {
                    try {
                        final Recipe recipe2 = rbp3.getRecipe(recipeId);
                        if (recipe2 != null) {
                            final Recipe refRecipe = Recipes.getRecipeById(recipeId);
                            assert refRecipe != null;
                            final Ingredient refingredient = refRecipe.getIngredientById(ingredientId);
                            final Ingredient ingredient = makeIngredient(templateId, cstate, pstate, material, refingredient.hasRealTemplate(), realTemplateId, groupId);
                            if (ingredient != null) {
                                ingredient.setAmount(refingredient.getAmount());
                                ingredient.setRatio(refingredient.getRatio());
                                ingredient.setLoss(refingredient.getLoss());
                                ingredient.setIngredientId(ingredientId);
                                recipe2.addIngredient(ingredient);
                            }
                            else {
                                RecipesByPlayer.logger.log(Level.WARNING, "Failed to find template for " + templateId + " or " + realTemplateId + ".");
                            }
                        }
                        else {
                            RecipesByPlayer.logger.log(Level.WARNING, "Failed to find player recipe " + recipeId + ".");
                        }
                    }
                    catch (Exception e) {
                        RecipesByPlayer.logger.log(Level.WARNING, "Failed to load player recipe " + recipeId + ", so deleted entry on db.");
                        dbRemovePlayerRecipe(playerId, recipeId);
                    }
                }
                else {
                    RecipesByPlayer.logger.log(Level.WARNING, "Failed to find player recipe list, so deleted entry on db.");
                    dbRemovePlayerRecipe(playerId, recipeId);
                }
            }
        }
        catch (SQLException sqex) {
            RecipesByPlayer.logger.log(Level.WARNING, "Failed to load all player known recipes: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return count;
    }
    
    private static Ingredient makeIngredient(final short templateId, final byte cstate, final byte pstate, final byte material, final boolean hasRealTemplate, final short realTemplateId, final int groupId) {
        try {
            final ItemTemplate itemTemplate = ItemTemplateFactory.getInstance().getTemplate(templateId);
            final Ingredient ingredient = new Ingredient(itemTemplate, false, (byte)groupId);
            ingredient.setCState(cstate);
            ingredient.setPState(pstate);
            ingredient.setMaterial(material);
            if (templateId == 272) {
                ingredient.setCorpseData(realTemplateId);
            }
            else if (realTemplateId > -1) {
                final ItemTemplate realItemTemplate = ItemTemplateFactory.getInstance().getTemplate(realTemplateId);
                ingredient.setRealTemplate(realItemTemplate);
            }
            else if (hasRealTemplate) {
                ingredient.setRealTemplate(null);
            }
            return ingredient;
        }
        catch (NoSuchTemplateException e) {
            return null;
        }
    }
    
    static final RecipesByPlayer getRecipesByPlayer(final long playerId, final boolean autoCreate) {
        RecipesByPlayer rbp = RecipesByPlayer.playerRecipes.get(playerId);
        if (rbp == null && autoCreate) {
            rbp = new RecipesByPlayer(playerId);
            RecipesByPlayer.playerRecipes.put(playerId, rbp);
        }
        return rbp;
    }
    
    private static final Set<Recipe> getKnownRecipesSetFor(final long playerId) {
        final Set<Recipe> recipes = Recipes.getKnownRecipes();
        final RecipesByPlayer rbp = RecipesByPlayer.playerRecipes.get(playerId);
        if (rbp != null) {
            recipes.addAll(rbp.knownRecipes.values());
        }
        return recipes;
    }
    
    public static boolean isKnownRecipe(final long playerId, final short recipeId) {
        if (Recipes.isKnownRecipe(recipeId)) {
            return true;
        }
        final RecipesByPlayer rbp = RecipesByPlayer.playerRecipes.get(playerId);
        return rbp != null && rbp.knownRecipes.containsKey(recipeId);
    }
    
    public static boolean isFavourite(final long playerId, final short recipeId) {
        final RecipesByPlayer rbp = RecipesByPlayer.playerRecipes.get(playerId);
        return rbp != null && rbp.isFavourite(recipeId);
    }
    
    public static String getNotes(final long playerId, final short recipeId) {
        final RecipesByPlayer rbp = RecipesByPlayer.playerRecipes.get(playerId);
        if (rbp != null) {
            return rbp.getNotes(recipeId);
        }
        return "";
    }
    
    public static Recipe getPlayerKnownRecipeOrNull(final long playerId, final short recipeId) {
        final RecipesByPlayer rbp = RecipesByPlayer.playerRecipes.get(playerId);
        if (rbp != null) {
            return rbp.knownRecipes.get(recipeId);
        }
        return null;
    }
    
    public static final Recipe[] getTargetActionRecipesFor(final long playerId) {
        final Set<Recipe> recipes = getKnownRecipesSetFor(playerId);
        final Set<Recipe> knownRecipes = new HashSet<Recipe>();
        for (final Recipe recipe : recipes) {
            if (recipe.isTargetActionType()) {
                knownRecipes.add(recipe);
            }
        }
        return knownRecipes.toArray(new Recipe[knownRecipes.size()]);
    }
    
    public static final Recipe[] getContainerActionRecipesFor(final long playerId) {
        final Set<Recipe> recipes = getKnownRecipesSetFor(playerId);
        final Set<Recipe> knownRecipes = new HashSet<Recipe>();
        for (final Recipe recipe : recipes) {
            if (recipe.isContainerActionType()) {
                knownRecipes.add(recipe);
            }
        }
        return knownRecipes.toArray(new Recipe[knownRecipes.size()]);
    }
    
    public static final Recipe[] getHeatRecipesFor(final long playerId) {
        final Set<Recipe> recipes = getKnownRecipesSetFor(playerId);
        final Set<Recipe> knownRecipes = new HashSet<Recipe>();
        for (final Recipe recipe : recipes) {
            if (recipe.isHeatType()) {
                knownRecipes.add(recipe);
            }
        }
        return knownRecipes.toArray(new Recipe[knownRecipes.size()]);
    }
    
    public static final Recipe[] getTimeRecipesFor(final long playerId) {
        final Set<Recipe> recipes = getKnownRecipesSetFor(playerId);
        final Set<Recipe> knownRecipes = new HashSet<Recipe>();
        for (final Recipe recipe : recipes) {
            if (recipe.isTimeType()) {
                knownRecipes.add(recipe);
            }
        }
        return knownRecipes.toArray(new Recipe[knownRecipes.size()]);
    }
    
    public static final ItemTemplate[] getKnownCookersFor(final long playerId) {
        final Set<Recipe> recipes = getKnownRecipesSetFor(playerId);
        final Set<ItemTemplate> knownCookers = new HashSet<ItemTemplate>();
        for (final Recipe recipe : recipes) {
            if (recipe.hasCooker()) {
                knownCookers.addAll(recipe.getCookerTemplates());
            }
        }
        return knownCookers.toArray(new ItemTemplate[knownCookers.size()]);
    }
    
    public static final Recipe[] getCookerRecipesFor(final long playerId, final int cookerId) {
        final Set<Recipe> recipes = getKnownRecipesSetFor(playerId);
        final Set<Recipe> knownRecipes = new HashSet<Recipe>();
        for (final Recipe recipe : recipes) {
            if (recipe.hasCooker(cookerId)) {
                knownRecipes.add(recipe);
            }
        }
        return knownRecipes.toArray(new Recipe[knownRecipes.size()]);
    }
    
    public static final ItemTemplate[] getKnownContainersFor(final long playerId) {
        final Set<Recipe> recipes = getKnownRecipesSetFor(playerId);
        final Set<ItemTemplate> knownContainers = new HashSet<ItemTemplate>();
        for (final Recipe recipe : recipes) {
            if (recipe.hasContainer()) {
                knownContainers.addAll(recipe.getContainerTemplates());
            }
        }
        return knownContainers.toArray(new ItemTemplate[knownContainers.size()]);
    }
    
    public static final Recipe[] getContainerRecipesFor(final long playerId, final int containerId) {
        final Set<Recipe> recipes = getKnownRecipesSetFor(playerId);
        final Set<Recipe> knownRecipes = new HashSet<Recipe>();
        for (final Recipe recipe : recipes) {
            if (recipe.hasContainer(containerId)) {
                knownRecipes.add(recipe);
            }
        }
        return knownRecipes.toArray(new Recipe[knownRecipes.size()]);
    }
    
    public static final ItemTemplate[] getKnownToolsFor(final long playerId) {
        final Set<Recipe> recipes = getKnownRecipesSetFor(playerId);
        final Set<ItemTemplate> knownTools = new HashSet<ItemTemplate>();
        for (final Recipe recipe : recipes) {
            if (recipe.getActiveItem() != null && recipe.getActiveItem().getTemplate().isCookingTool()) {
                knownTools.add(recipe.getActiveItem().getTemplate());
            }
        }
        return knownTools.toArray(new ItemTemplate[knownTools.size()]);
    }
    
    public static final Recipe[] getToolRecipesFor(final long playerId, final int toolId) {
        final Set<Recipe> recipes = getKnownRecipesSetFor(playerId);
        final Set<Recipe> knownRecipes = new HashSet<Recipe>();
        for (final Recipe recipe : recipes) {
            if (recipe.getActiveItem() != null && recipe.getActiveItem().getTemplateId() == toolId) {
                knownRecipes.add(recipe);
            }
        }
        return knownRecipes.toArray(new Recipe[knownRecipes.size()]);
    }
    
    public static final Ingredient[] getKnownIngredientsFor(final long playerId) {
        final Set<Recipe> recipes = getKnownRecipesSetFor(playerId);
        final Map<String, Ingredient> knownIngredients = new HashMap<String, Ingredient>();
        for (final Recipe recipe : recipes) {
            for (final Ingredient i : recipe.getAllIngredients(true).values()) {
                final Ingredient ingredient = i.clone(null);
                knownIngredients.put(ingredient.getName(true), ingredient);
            }
        }
        return knownIngredients.values().toArray(new Ingredient[knownIngredients.size()]);
    }
    
    public static final Ingredient[] getRecipeIngredientsFor(final long playerId, final int recipeId) {
        final Set<Recipe> recipes = getKnownRecipesSetFor(playerId);
        final Map<String, Ingredient> knownIngredients = new HashMap<String, Ingredient>();
        for (final Recipe recipe : recipes) {
            if (recipe.getRecipeId() == recipeId) {
                knownIngredients.putAll(recipe.getAllIngredients(true));
                break;
            }
        }
        return knownIngredients.values().toArray(new Ingredient[knownIngredients.size()]);
    }
    
    public static final Recipe[] getIngredientRecipesFor(final long playerId, final Ingredient ingredient) {
        final Set<Recipe> recipes = getKnownRecipesSetFor(playerId);
        final Set<Recipe> knownRecipes = new HashSet<Recipe>();
        for (final Recipe recipe : recipes) {
            final Map<String, Ingredient> recipeIngredients = recipe.getAllIngredients(true);
            for (final Ingredient i : recipeIngredients.values()) {
                if (i.getName(false).equalsIgnoreCase(ingredient.getName(false))) {
                    knownRecipes.add(recipe);
                    break;
                }
            }
        }
        return knownRecipes.toArray(new Recipe[knownRecipes.size()]);
    }
    
    public static final Recipe[] getSearchRecipesFor(final long playerId, final String searchFor) {
        final Set<Recipe> recipes = getKnownRecipesSetFor(playerId);
        final Set<Recipe> knownRecipes = new HashSet<Recipe>();
        for (final Recipe recipe : recipes) {
            if (recipe.getName().toLowerCase().contains(searchFor.toLowerCase())) {
                knownRecipes.add(recipe);
            }
        }
        return knownRecipes.toArray(new Recipe[knownRecipes.size()]);
    }
    
    public static final Recipe[] getKnownRecipesFor(final long playerId) {
        final Set<Recipe> recipes = getKnownRecipesSetFor(playerId);
        final Set<Recipe> knownRecipes = new HashSet<Recipe>();
        for (final Recipe recipe : recipes) {
            knownRecipes.add(recipe);
        }
        return knownRecipes.toArray(new Recipe[knownRecipes.size()]);
    }
    
    public static final Recipe getRecipe(final long playerId, final int recipeId) {
        final Set<Recipe> recipes = getKnownRecipesSetFor(playerId);
        for (final Recipe recipe : recipes) {
            if (recipe.getRecipeId() == recipeId) {
                return recipe;
            }
        }
        return null;
    }
    
    public static void packRecipes(final DataOutputStream dos, final long playerId) throws IOException {
        final Set<Recipe> recipes = new HashSet<Recipe>();
        final RecipesByPlayer rbp = RecipesByPlayer.playerRecipes.get(playerId);
        if (rbp != null) {
            recipes.addAll(rbp.knownRecipes.values());
        }
        dos.writeChar(88);
        dos.writeShort(recipes.size());
        RecipesByPlayer.logger.log(Level.INFO, "packing " + recipes.size() + " known recipes!");
        for (final Recipe recipe : recipes) {
            dos.writeChar(82);
            recipe.pack(dos);
        }
        if (rbp != null) {
            int count = 0;
            for (final Boolean b : rbp.playerFavourites.values()) {
                if (b) {
                    ++count;
                }
            }
            RecipesByPlayer.logger.log(Level.INFO, "packing " + count + " favourites!");
            dos.writeShort(count);
            for (final Map.Entry<Short, Boolean> entry : rbp.playerFavourites.entrySet()) {
                if (entry.getValue()) {
                    dos.writeShort(entry.getKey());
                }
            }
            count = 0;
            for (final String n : rbp.playerNotes.values()) {
                if (n.length() > 0) {
                    ++count;
                }
            }
            RecipesByPlayer.logger.log(Level.INFO, "packing " + count + " notes!");
            dos.writeShort(count);
            for (final Map.Entry<Short, String> entry2 : rbp.playerNotes.entrySet()) {
                if (entry2.getValue().length() > 0) {
                    dos.writeShort(entry2.getKey());
                    final byte[] notesAsBytes = entry2.getValue().getBytes("UTF-8");
                    dos.writeByte((byte)notesAsBytes.length);
                    dos.write(notesAsBytes);
                }
            }
        }
        else {
            dos.writeShort(0);
            dos.writeShort(0);
        }
    }
    
    public static void unPackRecipes(final DataInputStream dis, final long playerId) throws IOException {
        deleteRecipesForPlayer(playerId);
        if (dis.readChar() != 'X') {
            throw new IOException(new Exception("unpacking error, no start recipe list 'X' char"));
        }
        int count = dis.readShort();
        RecipesByPlayer.logger.log(Level.INFO, "unpacking " + count + " known recipes!");
        if (count > 0) {
            final RecipesByPlayer rbp = getRecipesByPlayer(playerId, true);
            for (int x = 0; x < count; ++x) {
                if (dis.readChar() != 'R') {
                    throw new IOException(new Exception("unpacking error, no start recipe 'R' char for recipe " + x + " out of " + count + "."));
                }
                try {
                    final Recipe recipe = new Recipe(dis);
                    addRecipe(rbp, recipe);
                }
                catch (NoSuchTemplateException e) {
                    RecipesByPlayer.logger.log(Level.INFO, "unpacking fail: " + e.getMessage(), e);
                    throw new IOException(e.getMessage());
                }
            }
        }
        count = dis.readShort();
        RecipesByPlayer.logger.log(Level.INFO, "unpacking " + count + " favourites!");
        if (count > 0) {
            for (int x2 = 0; x2 < count; ++x2) {
                final short recipeId = dis.readShort();
                setIsFavourite(playerId, recipeId, true);
            }
        }
        count = dis.readShort();
        RecipesByPlayer.logger.log(Level.INFO, "unpacking " + count + " notes!");
        if (count > 0) {
            for (int x2 = 0; x2 < count; ++x2) {
                final short recipeId = dis.readShort();
                final byte lByte = dis.readByte();
                final int length = lByte & 0xFF;
                final byte[] tempStringArr = new byte[length];
                final int read = dis.read(tempStringArr);
                if (length != read) {
                    RecipesByPlayer.logger.warning("Read in " + read + ", expected " + length);
                }
                final String notes = new String(tempStringArr, "UTF-8");
                setNotes(playerId, recipeId, notes);
            }
        }
    }
    
    public static boolean saveRecipe(@Nullable final Creature performer, final Recipe templateRecipe, final long playerId, @Nullable final Item source, final Item target) {
        if (templateRecipe.isKnown()) {
            return false;
        }
        if (playerId == -10L) {
            RecipesByPlayer.logger.log(Level.WARNING, "Failed to save recipe '" + templateRecipe.getName() + "' (#" + templateRecipe.getRecipeId() + "): No player ID given");
            return false;
        }
        if (performer != null) {
            Recipes.setRecipeNamer(templateRecipe, performer);
        }
        boolean isChanged = false;
        final RecipesByPlayer rbp = getRecipesByPlayer(playerId, true);
        Recipe playerRecipe = rbp.getRecipe(templateRecipe.getRecipeId());
        if (playerRecipe != null) {
            if (templateRecipe.hasCooker()) {
                final Item cooker = target.getTopParentOrNull();
                if (cooker != null && !playerRecipe.hasCooker(cooker.getTemplateId())) {
                    playerRecipe.addToCookerList(cooker.getTemplateId());
                    dbSaveRecipeCooker(playerId, playerRecipe.getRecipeId(), cooker.getTemplateId());
                    isChanged = true;
                }
            }
            if (templateRecipe.hasContainer() && target != null && !playerRecipe.hasContainer(target.getTemplateId())) {
                playerRecipe.addToContainerList(target.getTemplateId());
                dbSaveRecipeContainer(playerId, playerRecipe.getRecipeId(), target.getTemplateId());
                isChanged = true;
            }
            if ((source != null & templateRecipe.getActiveItem() != null) && playerRecipe.getActiveItem() != null && templateRecipe.getActiveItem().isFoodGroup() && !playerRecipe.getActiveItem().isFoodGroup() && playerRecipe.getActiveItem().getTemplateId() != source.getTemplateId()) {
                final Ingredient pi = playerRecipe.getIngredientById(templateRecipe.getActiveItem().getIngredientId());
                pi.setTemplate(templateRecipe.getActiveItem().getTemplate());
                dbSaveRecipeIngredient(true, playerId, playerRecipe.getRecipeId(), pi);
                playerRecipe.addIngredient(pi);
                isChanged = true;
            }
            if (templateRecipe.getTargetItem() != null && playerRecipe.getTargetItem() != null && templateRecipe.getTargetItem().isFoodGroup() && !playerRecipe.getTargetItem().isFoodGroup() && playerRecipe.getTargetItem().getTemplateId() != target.getTemplateId()) {
                final Ingredient pi = playerRecipe.getIngredientById(templateRecipe.getTargetItem().getIngredientId());
                pi.setTemplate(templateRecipe.getTargetItem().getTemplate());
                dbSaveRecipeIngredient(true, playerId, playerRecipe.getRecipeId(), pi);
                playerRecipe.addIngredient(pi);
                isChanged = true;
            }
            if (target.isFoodMaker() || target.getTemplate().isCooker()) {
                for (final Item item : target.getItemsAsArray()) {
                    final Ingredient ti = templateRecipe.findMatchingIngredient(item);
                    if (ti == null) {
                        RecipesByPlayer.logger.log(Level.WARNING, "Failed to find matching ingredient:" + item.getName() + ".");
                    }
                    else {
                        final Ingredient pi2 = playerRecipe.getIngredientById(ti.getIngredientId());
                        if (pi2 == null) {
                            final Ingredient ingredient = ti.clone(item);
                            ingredient.setTemplate(item.getTemplate());
                            dbSaveRecipeIngredient(false, playerId, playerRecipe.getRecipeId(), ingredient);
                            playerRecipe.addIngredient(ingredient);
                            isChanged = true;
                        }
                        else if (ti.isFoodGroup() && !pi2.isFoodGroup()) {
                            pi2.setTemplate(ti.getTemplate());
                            dbSaveRecipeIngredient(true, playerId, playerRecipe.getRecipeId(), pi2);
                            playerRecipe.addIngredient(pi2);
                            isChanged = true;
                        }
                    }
                }
            }
            if (isChanged && performer != null) {
                performer.getCommunicator().sendCookbookRecipe(playerRecipe);
            }
            return false;
        }
        playerRecipe = new Recipe(templateRecipe.getRecipeId());
        dbSaveRecipe(playerId, playerRecipe.getRecipeId(), false, "");
        if (templateRecipe.hasCooker()) {
            final Item cooker = target.getTopParentOrNull();
            if (cooker != null) {
                playerRecipe.addToCookerList((short)cooker.getTemplateId());
                dbSaveRecipeCooker(playerId, playerRecipe.getRecipeId(), cooker.getTemplateId());
            }
        }
        if (templateRecipe.hasContainer()) {
            playerRecipe.addToContainerList((short)target.getTemplateId());
            dbSaveRecipeContainer(playerId, playerRecipe.getRecipeId(), target.getTemplateId());
        }
        if (templateRecipe.getActiveItem() != null && source != null) {
            Ingredient pi;
            if (templateRecipe.getActiveItem().getTemplateId() == 14) {
                pi = new Ingredient(templateRecipe.getActiveItem().getTemplate(), false, (byte)(-2));
            }
            else {
                pi = new Ingredient(source.getTemplate(), false, (byte)(-2));
            }
            if (templateRecipe.getActiveItem().hasMaterial()) {
                pi.setMaterial(source.getMaterial());
            }
            if (templateRecipe.getActiveItem().hasCState()) {
                pi.setCState(source.getRightAuxData());
            }
            if (templateRecipe.getActiveItem().hasPState()) {
                pi.setPState((byte)(source.getLeftAuxData() * 16));
            }
            if (templateRecipe.getActiveItem().hasRealTemplate()) {
                pi.setRealTemplate(source.getRealTemplate());
            }
            pi.setIngredientId(templateRecipe.getActiveItem().getIngredientId());
            dbSaveRecipeIngredient(false, playerId, playerRecipe.getRecipeId(), pi);
            playerRecipe.addIngredient(pi);
        }
        if (templateRecipe.getTargetItem() != null) {
            final Ingredient pi = new Ingredient(target.getTemplate(), false, (byte)(-1));
            if (templateRecipe.getTargetItem().hasMaterial()) {
                pi.setMaterial(target.getMaterial());
            }
            if (templateRecipe.getTargetItem().hasCState()) {
                pi.setCState(target.getRightAuxData());
            }
            if (templateRecipe.getTargetItem().hasPState()) {
                pi.setPState((byte)(target.getLeftAuxData() * 16));
            }
            if (templateRecipe.getTargetItem().hasRealTemplate()) {
                pi.setRealTemplate(target.getRealTemplate());
            }
            if (templateRecipe.getTargetItem().hasCorpseData()) {
                pi.setCorpseData(templateRecipe.getTargetItem().getCorpseData());
            }
            pi.setIngredientId(templateRecipe.getTargetItem().getIngredientId());
            dbSaveRecipeIngredient(false, playerId, playerRecipe.getRecipeId(), pi);
            playerRecipe.addIngredient(pi);
        }
        if (target.isFoodMaker() || target.getTemplate().isCooker()) {
            templateRecipe.clearFound();
            for (final Item item : target.getItemsAsArray()) {
                final Ingredient ti = templateRecipe.findMatchingIngredient(item);
                if (ti == null) {
                    RecipesByPlayer.logger.log(Level.WARNING, "Failed to find matching ingredient:" + item.getName() + ".");
                }
                else if (!ti.wasFound(true, false)) {
                    ti.setFound(true);
                    final Ingredient pi2 = ti.clone(item);
                    pi2.setTemplate(item.getTemplate());
                    dbSaveRecipeIngredient(false, playerId, playerRecipe.getRecipeId(), pi2);
                    playerRecipe.addIngredient(pi2);
                }
            }
        }
        rbp.addRecipe(playerRecipe);
        if (performer != null) {
            performer.getCommunicator().sendCookbookRecipe(playerRecipe);
        }
        return true;
    }
    
    public static boolean addRecipe(final Creature performer, final Recipe recipe) {
        final RecipesByPlayer rbp = getRecipesByPlayer(performer.getWurmId(), true);
        if (addRecipe(rbp, recipe)) {
            return true;
        }
        performer.getCommunicator().sendNormalServerMessage("That recipe is already in your cookbook!");
        return false;
    }
    
    private static boolean addRecipe(final RecipesByPlayer rbp, final Recipe recipe) {
        final Recipe playerRecipe = rbp.getRecipe(recipe.getRecipeId());
        if (playerRecipe == null) {
            dbSaveRecipe(rbp.getPlayerId(), recipe.getRecipeId(), rbp.isFavourite(recipe.getRecipeId()), rbp.getNotes(recipe.getRecipeId()));
            if (recipe.hasCooker()) {
                for (final ItemTemplate cooker : recipe.getCookerTemplates()) {
                    dbSaveRecipeCooker(rbp.getPlayerId(), recipe.getRecipeId(), cooker.getTemplateId());
                }
            }
            if (recipe.hasContainer()) {
                for (final ItemTemplate container : recipe.getContainerTemplates()) {
                    dbSaveRecipeContainer(rbp.getPlayerId(), recipe.getRecipeId(), container.getTemplateId());
                }
            }
            if (recipe.getActiveItem() != null) {
                dbSaveRecipeIngredient(false, rbp.getPlayerId(), recipe.getRecipeId(), recipe.getActiveItem());
            }
            if (recipe.getTargetItem() != null) {
                dbSaveRecipeIngredient(false, rbp.getPlayerId(), recipe.getRecipeId(), recipe.getTargetItem());
            }
            for (final Ingredient i : recipe.getAllIngredients(true).values()) {
                dbSaveRecipeIngredient(false, rbp.getPlayerId(), recipe.getRecipeId(), i);
            }
            rbp.addRecipe(recipe);
            return true;
        }
        return false;
    }
    
    public static void removeRecipeForPlayer(final long playerId, final short recipeId) {
        dbRemovePlayerRecipe(playerId, recipeId);
        final RecipesByPlayer rbp = getRecipesByPlayer(playerId, false);
        if (rbp != null) {
            rbp.removeRecipe(recipeId);
        }
    }
    
    public static void deleteRecipesByNumber(final short recipeId) {
        for (final Map.Entry<Long, RecipesByPlayer> entry : RecipesByPlayer.playerRecipes.entrySet()) {
            final Recipe recipe = entry.getValue().getRecipe(recipeId);
            if (recipe != null) {
                final RecipesByPlayer rbp = entry.getValue();
                final long playerId = entry.getKey();
                dbRemovePlayerRecipe(playerId, recipeId);
                rbp.removeRecipe(recipeId);
                try {
                    final Player player = Players.getInstance().getPlayer(playerId);
                    player.getCommunicator().sendCookbookRecipe(recipe);
                }
                catch (NoSuchPlayerException ex) {}
            }
        }
    }
    
    public static void deleteRecipesForPlayer(final long playerId) {
        dbRemovePlayerRecipes(playerId);
        RecipesByPlayer.playerRecipes.remove(playerId);
    }
    
    public static void deleteAllKnownRecipes() {
        dbRemoveAllPlayerRecipes();
        RecipesByPlayer.playerRecipes.clear();
    }
    
    public static void setIsFavourite(final long playerId, final short recipeId, final boolean isFavourite) {
        final RecipesByPlayer rbp = getRecipesByPlayer(playerId, true);
        rbp.setFavourite(recipeId, isFavourite);
        dbUpdateRecipeFavourite(playerId, recipeId, isFavourite);
    }
    
    public static void setNotes(final long playerId, final short recipeId, final String notes) {
        final RecipesByPlayer rbp = getRecipesByPlayer(playerId, true);
        rbp.setNotes(recipeId, notes);
        dbUpdateRecipeNotes(playerId, recipeId, notes);
    }
    
    private static void dbSaveRecipe(final long playerId, final short recipeId, final boolean favourite, final String notes) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("INSERT INTO RECIPESPLAYER (PLAYERID,RECIPEID,FAVOURITE,NOTES) VALUES(?,?,?,?)");
            ps.setLong(1, playerId);
            ps.setShort(2, recipeId);
            ps.setBoolean(3, favourite);
            ps.setString(4, notes);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            RecipesByPlayer.logger.log(Level.WARNING, "Failed to save player recipe: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbSaveRecipeCooker(final long playerId, final short recipeId, final int cookerId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("INSERT INTO RECIPEPLAYERCOOKERS (PLAYERID,RECIPEID,COOKERID) VALUES(?,?,?)");
            ps.setLong(1, playerId);
            ps.setShort(2, recipeId);
            ps.setShort(3, (short)cookerId);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            RecipesByPlayer.logger.log(Level.WARNING, "Failed to save player recipe cooker: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbSaveRecipeContainer(final long playerId, final short recipeId, final int containerId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("INSERT INTO RECIPEPLAYERCONTAINERS (PLAYERID,RECIPEID,CONTAINERID) VALUES(?,?,?)");
            ps.setLong(1, playerId);
            ps.setShort(2, recipeId);
            ps.setShort(3, (short)containerId);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            RecipesByPlayer.logger.log(Level.WARNING, "Failed to save player recipe container: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbSaveRecipeIngredient(final boolean update, final long playerId, final short recipeId, final Ingredient ingredient) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            if (update) {
                ps = dbcon.prepareStatement("UPDATE RECIPEPLAYERINGREDIENTS SET TEMPLATEID=?,CSTATE=?,PSTATE=?,MATERIAL=?,REALTEMPLATEID=? WHERE PLAYERID=? AND RECIPEID=? AND INGREDIENTID=?");
                ps.setShort(1, (short)ingredient.getTemplateId());
                ps.setByte(2, ingredient.getCState());
                ps.setByte(3, ingredient.getPState());
                ps.setByte(4, ingredient.getMaterial());
                if (ingredient.getTemplateId() == 272) {
                    ps.setShort(5, (short)ingredient.getCorpseData());
                }
                else {
                    ps.setShort(5, (short)ingredient.getRealTemplateId());
                }
                ps.setLong(6, playerId);
                ps.setShort(7, recipeId);
                ps.setByte(8, ingredient.getIngredientId());
                final int did = ps.executeUpdate();
                if (did > 0) {
                    return;
                }
                DbUtilities.closeDatabaseObjects(ps, null);
            }
            ps = dbcon.prepareStatement("INSERT INTO RECIPEPLAYERINGREDIENTS (PLAYERID,RECIPEID,INGREDIENTID,GROUPID,TEMPLATEID,CSTATE,PSTATE,MATERIAL,REALTEMPLATEID) VALUES(?,?,?,?,?,?,?,?,?)");
            ps.setLong(1, playerId);
            ps.setShort(2, recipeId);
            ps.setByte(3, ingredient.getIngredientId());
            ps.setByte(4, ingredient.getGroupId());
            ps.setShort(5, (short)ingredient.getTemplateId());
            ps.setByte(6, ingredient.getCState());
            ps.setByte(7, ingredient.getPState());
            ps.setByte(8, ingredient.getMaterial());
            if (ingredient.getTemplateId() == 272) {
                ps.setShort(9, (short)ingredient.getCorpseData());
            }
            else {
                ps.setShort(9, (short)ingredient.getRealTemplateId());
            }
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            RecipesByPlayer.logger.log(Level.WARNING, "Failed to save player recipe ingredient: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbUpdateRecipeFavourite(final long playerId, final short recipeId, final boolean isFavourite) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            RecipesByPlayer.logger.info("update favourite for " + recipeId);
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("UPDATE RECIPESPLAYER SET FAVOURITE=? WHERE PLAYERID=? AND RECIPEID=?");
            ps.setBoolean(1, isFavourite);
            ps.setLong(2, playerId);
            ps.setShort(3, recipeId);
            final int did = ps.executeUpdate();
            if (did > 0) {
                return;
            }
            RecipesByPlayer.logger.info("Update favourite failed, so trying create " + did);
            DbUtilities.closeDatabaseObjects(ps, null);
            ps = dbcon.prepareStatement("INSERT INTO RECIPESPLAYER (PLAYERID,RECIPEID,FAVOURITE,NOTES) VALUES(?,?,?,?)");
            ps.setLong(1, playerId);
            ps.setShort(2, recipeId);
            ps.setBoolean(3, isFavourite);
            ps.setString(4, "");
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            RecipesByPlayer.logger.log(Level.WARNING, "Failed to update player (" + playerId + ") recipe (" + recipeId + ") favourite: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbUpdateRecipeNotes(final long playerId, final short recipeId, final String notes) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            RecipesByPlayer.logger.info("update notes for " + recipeId);
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("UPDATE RECIPESPLAYER SET NOTES=? WHERE PLAYERID=? AND RECIPEID=?");
            ps.setString(1, notes);
            ps.setLong(2, playerId);
            ps.setShort(3, recipeId);
            final int did = ps.executeUpdate();
            if (did > 0) {
                return;
            }
            RecipesByPlayer.logger.info("Update notes failed, so trying create " + did);
            DbUtilities.closeDatabaseObjects(ps, null);
            ps = dbcon.prepareStatement("INSERT INTO RECIPESPLAYER (PLAYERID,RECIPEID,FAVOURITE,NOTES) VALUES(?,?,?,?)");
            ps.setLong(1, playerId);
            ps.setShort(2, recipeId);
            ps.setBoolean(3, false);
            ps.setString(4, notes);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            RecipesByPlayer.logger.log(Level.WARNING, "Failed to update player (" + playerId + ") recipe (" + recipeId + ") notes: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbRemovePlayerRecipes(final long playerId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("DELETE FROM RECIPESPLAYER WHERE PLAYERID=?");
            ps.setLong(1, playerId);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            ps = dbcon.prepareStatement("DELETE FROM RECIPEPLAYERCOOKERS WHERE PLAYERID=?");
            ps.setLong(1, playerId);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            ps = dbcon.prepareStatement("DELETE FROM RECIPEPLAYERCONTAINERS WHERE PLAYERID=?");
            ps.setLong(1, playerId);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            ps = dbcon.prepareStatement("DELETE FROM RECIPEPLAYERINGREDIENTS WHERE PLAYERID=?");
            ps.setLong(1, playerId);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            RecipesByPlayer.logger.log(Level.WARNING, "Failed to remove player recipes: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbRemovePlayerRecipe(final long playerId, final short recipeId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("DELETE FROM RECIPESPLAYER WHERE PLAYERID=? AND RECIPEID=?");
            ps.setLong(1, playerId);
            ps.setShort(2, recipeId);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            ps = dbcon.prepareStatement("DELETE FROM RECIPEPLAYERCOOKERS WHERE PLAYERID=? AND RECIPEID=?");
            ps.setLong(1, playerId);
            ps.setShort(2, recipeId);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            ps = dbcon.prepareStatement("DELETE FROM RECIPEPLAYERCONTAINERS WHERE PLAYERID=? AND RECIPEID=?");
            ps.setLong(1, playerId);
            ps.setShort(2, recipeId);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            ps = dbcon.prepareStatement("DELETE FROM RECIPEPLAYERINGREDIENTS WHERE PLAYERID=? AND RECIPEID=?");
            ps.setLong(1, playerId);
            ps.setShort(2, recipeId);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            RecipesByPlayer.logger.log(Level.WARNING, "Failed to remove player recipes: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbRemoveAllPlayerRecipes() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("DELETE FROM RECIPESPLAYER");
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            ps = dbcon.prepareStatement("DELETE FROM RECIPEPLAYERCOOKERS");
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            ps = dbcon.prepareStatement("DELETE FROM RECIPEPLAYERCONTAINERS");
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            ps = dbcon.prepareStatement("DELETE FROM RECIPEPLAYERINGREDIENTS");
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            RecipesByPlayer.logger.log(Level.WARNING, "Failed to remove all player recipes: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        logger = Logger.getLogger(RecipesByPlayer.class.getName());
        playerRecipes = new ConcurrentHashMap<Long, RecipesByPlayer>();
    }
}
