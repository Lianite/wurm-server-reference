// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import com.wurmonline.server.players.AchievementTemplate;
import com.wurmonline.shared.constants.IconConstants;
import com.wurmonline.server.players.Achievement;
import com.wurmonline.server.support.JSONArray;
import com.wurmonline.server.creatures.CreatureTemplate;
import java.io.InputStream;
import com.wurmonline.server.creatures.CreatureTemplateFactory;
import com.wurmonline.server.skills.SkillSystem;
import com.wurmonline.server.support.JSONException;
import com.wurmonline.server.support.JSONObject;
import com.wurmonline.server.support.JSONTokener;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import com.wurmonline.server.players.PlayerInfo;
import javax.annotation.Nullable;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.Players;
import com.wurmonline.server.creatures.Creature;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.DbConnector;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.logging.Level;
import java.nio.file.LinkOption;
import java.nio.file.FileVisitOption;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import com.wurmonline.server.gui.folders.GameEntity;
import com.wurmonline.server.gui.folders.DistEntity;
import com.wurmonline.server.gui.folders.Folders;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.server.players.AchievementList;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.shared.constants.ItemMaterials;

public class Recipes implements ItemMaterials, MiscConstants, AchievementList
{
    private static final Logger logger;
    private static final Map<Short, Recipe> recipes;
    private static final Map<Short, String> namedRecipes;
    private static final List<Recipe> recipesList;
    private static final String GET_ALL_NAMED_RECIPES = "SELECT * FROM RECIPESNAMED";
    private static final String CREATE_NAMED_RECIPE = "INSERT INTO RECIPESNAMED (RECIPEID, NAMER) VALUES(?,?)";
    private static final String DELETE_NAMED_RECIPE = "DELETE FROM RECIPESNAMED WHERE RECIPEID=?";
    public static final byte NOT_FOUND = 0;
    public static final byte FOUND = 1;
    public static final byte SWAPPED = 2;
    
    public static void add(final Recipe recipe) {
        Recipes.recipes.put(recipe.getMenuId(), recipe);
        Recipes.recipesList.add(recipe);
    }
    
    public static boolean exists(final short recipeId) {
        return Recipes.recipes.containsKey((short)(recipeId + 8000));
    }
    
    public static void loadAllRecipes() {
        loadRecipes(Folders.getDist().getPathFor(DistEntity.Recipes));
        if (!GameEntity.Recipes.existsIn(Folders.getCurrent())) {
            try {
                Files.createDirectory(Folders.getCurrent().getPathFor(GameEntity.Recipes), (FileAttribute<?>[])new FileAttribute[0]);
            }
            catch (IOException e) {
                Recipes.logger.warning("Could not create recipe folder");
                return;
            }
        }
        loadRecipes(Folders.getCurrent().getPathFor(GameEntity.Recipes));
    }
    
    public static void loadRecipes(final Path path) {
        Recipes.logger.info("Loading all Recipes");
        final long start = System.nanoTime();
        try {
            Files.walk(path, new FileVisitOption[0]).sorted().forEachOrdered(p -> {
                if (!Files.isDirectory(p, new LinkOption[0])) {
                    if (p.getFileName().toString().startsWith("recipe ") && p.getFileName().toString().endsWith(".json") && p.getFileName().toString().length() == 16) {
                        readRecipeFile(p.toString());
                    }
                    else {
                        Recipes.logger.log(Level.INFO, "recipe file name (" + p.toString() + ") is not in correct format, expected \" recipe xxxx.json\" where xxxx are the recipe id (same as in the file).");
                    }
                }
                return;
            });
        }
        catch (IOException e) {
            Recipes.logger.warning("Exception loading recipes");
            e.printStackTrace();
        }
        final int numberOfRecipes = Recipes.recipes.size();
        Recipes.logger.log(Level.INFO, "Total number of recipes=" + numberOfRecipes + ".");
        final int numberOfKnownRecipes = RecipesByPlayer.loadAllPlayerKnownRecipes();
        Recipes.logger.log(Level.INFO, "Number of player known recipes=" + numberOfKnownRecipes + ".");
        final int numberOfNamedRecipes = dbNamedRecipes();
        Recipes.logger.log(Level.INFO, "Number of named recipes=" + numberOfNamedRecipes + ".");
        Recipes.logger.log(Level.INFO, "Recipes loaded. That took " + (System.nanoTime() - start) / 1000000.0f + " ms.");
    }
    
    public static Recipe[] getNamedRecipesFor(final String playerName) {
        final Set<Recipe> recipes = new HashSet<Recipe>();
        for (final Map.Entry<Short, String> entry : Recipes.namedRecipes.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(playerName)) {
                final Recipe recipe = getRecipeById(entry.getKey());
                if (recipe == null) {
                    continue;
                }
                recipes.add(recipe);
            }
        }
        return recipes.toArray(new Recipe[recipes.size()]);
    }
    
    public static Recipe[] getNamedRecipes() {
        final Set<Recipe> recipes = new HashSet<Recipe>();
        for (final Map.Entry<Short, String> entry : Recipes.namedRecipes.entrySet()) {
            final Recipe recipe = getRecipeById(entry.getKey());
            if (recipe != null) {
                recipes.add(recipe);
            }
        }
        return recipes.toArray(new Recipe[recipes.size()]);
    }
    
    private static int dbNamedRecipes() {
        int count = 0;
        final int failed = 0;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM RECIPESNAMED");
            rs = ps.executeQuery();
            while (rs.next()) {
                final short recipeId = rs.getShort("RECIPEID");
                final String namer = rs.getString("NAMER");
                final long playerId = PlayerInfoFactory.getWurmId(namer);
                Recipes.namedRecipes.put(recipeId, namer);
                ++count;
            }
        }
        catch (SQLException sqex) {
            Recipes.logger.log(Level.WARNING, "Failed to get namer on known recipes: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        if (failed > 0) {
            Recipes.logger.log(Level.INFO, "Number of removed named recipes=" + failed + ".");
        }
        return count;
    }
    
    public static void setRecipeNamer(final Recipe recipe, final Creature creature) {
        if (recipe.isNameable() && !Recipes.namedRecipes.containsKey(recipe.getRecipeId()) && creature.isPlayer() && !creature.hasFlag(50)) {
            Recipes.namedRecipes.put(recipe.getRecipeId(), creature.getName());
            creature.setFlag(50, true);
            dbSetRecipeNamer(recipe.getRecipeId(), creature.getName());
            for (final Player player : Players.getInstance().getPlayers()) {
                if (player.isViewingCookbook() && RecipesByPlayer.isKnownRecipe(player.getWurmId(), recipe.getRecipeId())) {
                    player.getCommunicator().sendCookbookRecipe(recipe);
                }
            }
        }
    }
    
    @Nullable
    public static String getRecipeNamer(final short recipeId) {
        return Recipes.namedRecipes.get(recipeId);
    }
    
    public static short getNamedRecipe(final String namer) {
        for (final Map.Entry<Short, String> entry : Recipes.namedRecipes.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(namer)) {
                return entry.getKey();
            }
        }
        return 0;
    }
    
    private static void dbSetRecipeNamer(final short recipeId, final String namer) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("INSERT INTO RECIPESNAMED (RECIPEID, NAMER) VALUES(?,?)");
            ps.setShort(1, recipeId);
            ps.setString(2, namer);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            Recipes.logger.log(Level.WARNING, "Failed to save namer (" + namer + ") on recipe (" + recipeId + "): " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static boolean removeRecipeNamer(final short recipeId) {
        final String namer = Recipes.namedRecipes.remove(recipeId);
        if (namer != null) {
            final PlayerInfo pInfo = PlayerInfoFactory.getPlayerInfoWithName(namer);
            if (pInfo != null) {
                pInfo.setFlag(50, false);
            }
        }
        dbRemoveRecipeNamer(recipeId);
        return namer != null;
    }
    
    private static void dbRemoveRecipeNamer(final short recipeId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("DELETE FROM RECIPESNAMED WHERE RECIPEID=?");
            ps.setShort(1, recipeId);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            Recipes.logger.log(Level.WARNING, "Failed to delete entry for recipe (" + recipeId + "): " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void readRecipeFile(final String fileName) {
        try {
            final File file = new File(fileName);
            if (!file.exists()) {
                Recipes.logger.log(Level.INFO, "file '" + fileName + "' not found!");
                return;
            }
            final BufferedReader br = new BufferedReader(new FileReader(file));
            final StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }
            br.close();
            final InputStream in = new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));
            try {
                String name = "";
                String recipeId = "unknown";
                String skillName = "";
                try {
                    final JSONTokener tk = new JSONTokener(in);
                    final JSONObject recipeJO = new JSONObject(tk);
                    if (recipeJO.has("name")) {
                        name = recipeJO.getString("name");
                    }
                    if (!recipeJO.has("recipeid")) {
                        throw new JSONException("RecipeId for '" + name + "' is missing.");
                    }
                    recipeId = recipeJO.getString("recipeid");
                    if (recipeId.length() == 1) {
                        recipeId = "000" + recipeId;
                    }
                    else if (recipeId.length() == 2) {
                        recipeId = "00" + recipeId;
                    }
                    else if (recipeId.length() == 3) {
                        recipeId = "0" + recipeId;
                    }
                    if (recipeId.length() != 4) {
                        throw new JSONException("RecipeId " + recipeId + " for '" + name + "' is wrong length, should be 1 to 4 digits.");
                    }
                    if (!fileName.endsWith("recipe " + recipeId + ".json")) {
                        throw new JSONException("RecipeId " + recipeId + " does not match the filename (" + fileName + ").");
                    }
                    short rid = 0;
                    try {
                        rid = Short.parseShort(recipeId);
                    }
                    catch (NumberFormatException e4) {
                        throw new JSONException("RecipeId for '" + name + "' (" + recipeId + ") is not a number.");
                    }
                    if (name.length() == 0) {
                        throw new JSONException("Name missing for recipe id of " + recipeId + ".");
                    }
                    checkRecipeSchema(rid, "recipe", recipeJO);
                    if (rid < 1 || rid > 1999) {
                        throw new JSONException("RecipeId for '" + name + "' (" + recipeId + ") is not in range (1..1999), was " + rid + " in recipe.");
                    }
                    if (Recipes.recipes.containsKey((short)(rid + 8000))) {
                        Recipes.logger.info("Recipe '" + name + "' (" + recipeId + ") already exists, replacing");
                        Recipes.recipes.remove((short)(rid + 8000));
                    }
                    final Recipe recipe = new Recipe(name, rid);
                    if (recipeJO.has("skill")) {
                        skillName = recipeJO.getString("skill");
                    }
                    if (skillName.length() == 0) {
                        throw new JSONException("Skill name missing for '" + name + "' (" + recipeId + ").");
                    }
                    final int skillId = SkillSystem.getSkillByName(skillName);
                    if (skillId <= 0) {
                        throw new JSONException("Skill '" + skillName + "' does not exist in recipe '" + name + "' (" + recipeId + ").");
                    }
                    recipe.setSkill(skillId, skillName);
                    if (recipeJO.has("known")) {
                        recipe.setKnown(recipeJO.getBoolean("known"));
                    }
                    if (recipeJO.has("nameable")) {
                        recipe.setNameable(recipeJO.getBoolean("nameable"));
                    }
                    if (recipeJO.has("lootable")) {
                        final JSONObject jo = recipeJO.getJSONObject("lootable");
                        checkRecipeSchema(rid, "lootable", jo);
                        int cid = -10;
                        byte rarity = -1;
                        if (!jo.has("creature")) {
                            throw new JSONException("Recipe '" + name + "' (" + recipeId + ") Lootable is missing creature.");
                        }
                        final String creatureName = jo.getString("creature");
                        try {
                            final CreatureTemplate ct = CreatureTemplateFactory.getInstance().getTemplate(creatureName);
                            cid = ct.getTemplateId();
                        }
                        catch (Exception e5) {
                            throw new JSONException("Recipe '" + name + "' (" + recipeId + ") Creature '" + creatureName + "' does not exist as a creature.");
                        }
                        if (!jo.has("rarity")) {
                            throw new JSONException("Recipe '" + name + "' (" + recipeId + ") Lootable is missing rarity.");
                        }
                        final String rarityName = jo.getString("rarity");
                        rarity = convertRarityStringIntoByte(recipe.getRecipeId(), rarityName);
                        recipe.setLootable(cid, rarity);
                    }
                    if (recipeJO.has("trigger")) {
                        final String triggerName = recipeJO.getString("trigger");
                        byte triggerId = -1;
                        final String s = triggerName;
                        switch (s) {
                            case "heat": {
                                triggerId = 1;
                                break;
                            }
                            case "time": {
                                triggerId = 0;
                                break;
                            }
                            case "create": {
                                triggerId = 2;
                                break;
                            }
                        }
                        if (triggerId < 0) {
                            throw new JSONException("Trigger '" + triggerName + "' does not exist.");
                        }
                        recipe.setTrigger(triggerId);
                    }
                    if (recipeJO.has("cookers")) {
                        final JSONArray cja = recipeJO.getJSONArray("cookers");
                        for (int c = 0; c < cja.length(); ++c) {
                            final JSONObject jo2 = cja.getJSONObject(c);
                            checkRecipeSchema(rid, "cookers", jo2);
                            final String cooker = jo2.getString("id");
                            final ItemTemplate cookerIT = ItemTemplateFactory.getInstance().getTemplate(cooker);
                            if (cookerIT == null) {
                                throw new JSONException("Cooker '" + cooker + "' does not exist as a template.");
                            }
                            if (!cookerIT.isCooker()) {
                                throw new JSONException("Cooker '" + cooker + "' cannot be used to make food in.");
                            }
                            int dif = 0;
                            if (jo2.has("difficulty")) {
                                dif = jo2.getInt("difficulty");
                            }
                            if (dif < 0 || dif > 100) {
                                throw new JSONException("Difficulty for cooker '" + cooker + "' is out of range (0..100), was " + dif + " in recipe.");
                            }
                            recipe.addToCookerList(cookerIT.getTemplateId(), cooker, dif);
                        }
                    }
                    if (recipeJO.has("containers")) {
                        final JSONArray cja = recipeJO.getJSONArray("containers");
                        for (int c = 0; c < cja.length(); ++c) {
                            final JSONObject jo2 = cja.getJSONObject(c);
                            checkRecipeSchema(rid, "containers", jo2);
                            final String container = jo2.getString("id");
                            final ItemTemplate containerIT = ItemTemplateFactory.getInstance().getTemplate(container);
                            if (containerIT == null) {
                                throw new JSONException("Container '" + container + "' does not exist as a template.");
                            }
                            if (!containerIT.isFoodMaker() && !containerIT.isRecipeItem()) {
                                throw new JSONException("Container '" + container + "' cannot be used to make food in.");
                            }
                            int dif = 0;
                            if (jo2.has("difficulty")) {
                                dif = jo2.getInt("difficulty");
                            }
                            if (dif < 0 || dif > 100) {
                                throw new JSONException("Difficulty for container '" + container + "' is out of range (0..100), was " + dif + " in recipe.");
                            }
                            recipe.addToContainerList(containerIT.getTemplateId(), container, dif);
                        }
                    }
                    if (recipeJO.has("active")) {
                        final JSONObject activeJO = recipeJO.getJSONObject("active");
                        checkRecipeSchema(rid, "active", activeJO);
                        readIngredient(recipe, activeJO, "Active item", false, true, true, -2);
                    }
                    if (recipeJO.has("target")) {
                        final JSONObject targetJO = recipeJO.getJSONObject("target");
                        checkRecipeSchema(rid, "target", targetJO);
                        readIngredient(recipe, targetJO, "Target item", false, true, true, -1);
                    }
                    if (recipeJO.has("ingredients")) {
                        final JSONObject ingredientsJO = recipeJO.getJSONObject("ingredients");
                        checkRecipeSchema(rid, "ingredients group", ingredientsJO);
                        if (ingredientsJO.has("mandatory")) {
                            final IngredientGroup group = new IngredientGroup((byte)1);
                            recipe.addToIngredientGroupList(group);
                            final JSONArray groupJA = ingredientsJO.getJSONArray("mandatory");
                            for (int i = 0; i < groupJA.length(); ++i) {
                                final JSONObject ingredientJO = groupJA.getJSONObject(i);
                                checkRecipeSchema(rid, "mandatory", ingredientJO);
                                readIngredient(recipe, ingredientJO, "Mandatory Ingredient", false, false, true, recipe.getCurrentGroupId());
                            }
                        }
                        if (ingredientsJO.has("zeroorone")) {
                            final JSONArray zerooroneJA = ingredientsJO.getJSONArray("zeroorone");
                            for (int j = 0; j < zerooroneJA.length(); ++j) {
                                final IngredientGroup group2 = new IngredientGroup((byte)2);
                                recipe.addToIngredientGroupList(group2);
                                final JSONObject groupJO = zerooroneJA.getJSONObject(j);
                                checkRecipeSchema(rid, "zeroorone group", groupJO);
                                final JSONArray listJA = groupJO.getJSONArray("list");
                                for (int k = 0; k < listJA.length(); ++k) {
                                    final JSONObject listJO = listJA.getJSONObject(k);
                                    checkRecipeSchema(rid, "zeroorone", listJO);
                                    readIngredient(recipe, listJO, "ZeroOrOne Ingredient", false, false, true, recipe.getCurrentGroupId());
                                }
                            }
                        }
                        if (ingredientsJO.has("oneof")) {
                            final JSONArray oneOfJA = ingredientsJO.getJSONArray("oneof");
                            for (int j = 0; j < oneOfJA.length(); ++j) {
                                final IngredientGroup group2 = new IngredientGroup((byte)3);
                                recipe.addToIngredientGroupList(group2);
                                final JSONObject groupJO = oneOfJA.getJSONObject(j);
                                checkRecipeSchema(rid, "oneof group", groupJO);
                                final JSONArray listJA = groupJO.getJSONArray("list");
                                for (int k = 0; k < listJA.length(); ++k) {
                                    final JSONObject listJO = listJA.getJSONObject(k);
                                    checkRecipeSchema(rid, "oneof", listJO);
                                    readIngredient(recipe, listJO, "OneOf Ingredient ", false, false, true, recipe.getCurrentGroupId());
                                }
                            }
                        }
                        if (ingredientsJO.has("oneormore")) {
                            final JSONArray oneormoreJA = ingredientsJO.getJSONArray("oneormore");
                            for (int j = 0; j < oneormoreJA.length(); ++j) {
                                final IngredientGroup group2 = new IngredientGroup((byte)4);
                                recipe.addToIngredientGroupList(group2);
                                final JSONObject groupJO = oneormoreJA.getJSONObject(j);
                                checkRecipeSchema(rid, "oneormore group", groupJO);
                                final JSONArray listJA = groupJO.getJSONArray("list");
                                for (int k = 0; k < listJA.length(); ++k) {
                                    final JSONObject listJO = listJA.getJSONObject(k);
                                    checkRecipeSchema(rid, "oneormore", listJO);
                                    readIngredient(recipe, listJO, "OneOrMore Ingredient", false, false, true, recipe.getCurrentGroupId());
                                }
                            }
                        }
                        if (ingredientsJO.has("optional")) {
                            final IngredientGroup group = new IngredientGroup((byte)5);
                            recipe.addToIngredientGroupList(group);
                            final JSONArray groupJA = ingredientsJO.getJSONArray("optional");
                            for (int i = 0; i < groupJA.length(); ++i) {
                                final JSONObject ingredientJO = groupJA.getJSONObject(i);
                                checkRecipeSchema(rid, "optional", ingredientJO);
                                readIngredient(recipe, ingredientJO, "Optional Ingredient", false, false, true, recipe.getCurrentGroupId());
                            }
                        }
                        if (ingredientsJO.has("any")) {
                            final IngredientGroup group = new IngredientGroup((byte)6);
                            recipe.addToIngredientGroupList(group);
                            final JSONArray groupJA = ingredientsJO.getJSONArray("any");
                            for (int i = 0; i < groupJA.length(); ++i) {
                                final JSONObject ingredientJO = groupJA.getJSONObject(i);
                                checkRecipeSchema(rid, "any", ingredientJO);
                                readIngredient(recipe, ingredientJO, "Any Ingredient", false, false, true, recipe.getCurrentGroupId());
                            }
                        }
                    }
                    final JSONObject resultJO = recipeJO.getJSONObject("result");
                    checkRecipeSchema(rid, "result", resultJO);
                    readIngredient(recipe, resultJO, "Result item", true, false, true, -3);
                    add(recipe);
                }
                catch (JSONException e) {
                    if (name.equals("")) {
                        Recipes.logger.log(Level.WARNING, "Failed to load recipe from file " + fileName, e);
                    }
                    else {
                        Recipes.logger.log(Level.WARNING, "Failed to load recipe " + name + " {" + recipeId + "}", e);
                    }
                }
            }
            catch (JSONException e2) {
                final int lpos = e2.getMessage().indexOf("[character ");
                if (lpos > -1) {
                    final String rline = e2.getMessage().substring(lpos + 11);
                    final String ss = rline.substring(0, rline.indexOf(" line 1]"));
                    try {
                        final int pos = Integer.parseInt(ss);
                        final int p = sb.lastIndexOf("recipeid", pos);
                        if (p > -1) {
                            final int pio = sb.indexOf("\"", p + 11);
                            final String recipeId2 = sb.substring(p + 11, pio);
                            final String lloc = sb.substring(pos - 17, pos - 2);
                            final String bad = sb.substring(pos - 2, pos - 1);
                            final String rloc = sb.substring(pos - 1, pos + 16);
                            Recipes.logger.log(Level.INFO, "Bad recipeid:" + recipeId2 + " |" + lloc + ">" + bad + "<" + rloc);
                        }
                    }
                    catch (NumberFormatException ex) {}
                    catch (StringIndexOutOfBoundsException ex2) {}
                }
                Recipes.logger.log(Level.SEVERE, "Unable to load recipes:" + e2.getMessage(), e2);
            }
            finally {
                in.close();
            }
        }
        catch (Exception e3) {
            Recipes.logger.log(Level.SEVERE, "Unable to load recipes:" + e3.getMessage(), e3);
        }
    }
    
    private static void readIngredient(final Recipe recipe, final JSONObject currentJO, final String ingredientType, final boolean isResult, final boolean canBeTool, final boolean canBeRecipeItem, final int groupId) throws JSONException {
        final String idName = currentJO.getString("id");
        final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(idName);
        if (template == null) {
            throw new JSONException(ingredientType + " '" + idName + "' does not exist as a template.");
        }
        if (!template.isFood() && !template.isLiquidCooking()) {
            if (canBeTool) {
                if (canBeRecipeItem) {
                    if (!template.isCookingTool() && !template.isRecipeItem()) {
                        throw new JSONException(ingredientType + " '" + idName + "' is not a food item or tool or usable item.");
                    }
                }
                else if (!template.isCookingTool()) {
                    throw new JSONException(ingredientType + " '" + idName + "' is not a food item or tool.");
                }
            }
            else {
                if (!canBeRecipeItem) {
                    throw new JSONException(ingredientType + " '" + idName + "' is not a food item.");
                }
                if (!template.isRecipeItem() && !template.canBeFermented()) {
                    throw new JSONException(ingredientType + " '" + idName + "' is not a food item or usable item.");
                }
            }
        }
        final Ingredient ingredient = new Ingredient(template, isResult, (byte)groupId);
        if (currentJO.has("cstate")) {
            final String cstate = currentJO.getString("cstate");
            ingredient.setCState(convertCookingStateIntoByte(recipe.getRecipeId(), cstate), cstate);
        }
        if (currentJO.has("pstate")) {
            final String pstate = currentJO.getString("pstate");
            ingredient.setPState(convertPhysicalStateIntoByte(recipe.getRecipeId(), pstate), pstate);
        }
        if (currentJO.has("material")) {
            final String material = currentJO.getString("material");
            final byte mat = Materials.convertMaterialStringIntoByte(material);
            if (mat == 0) {
                throw new JSONException(ingredientType + " Material '" + material + "' does not exist as a material.");
            }
            ingredient.setMaterial(mat, material);
        }
        if (currentJO.has("realtemplate")) {
            final String realTemplateName = currentJO.getString("realtemplate");
            if (realTemplateName.equalsIgnoreCase("none")) {
                ingredient.setRealTemplate(null);
            }
            else {
                final ItemTemplate realIT = ItemTemplateFactory.getInstance().getTemplate(realTemplateName);
                if (realIT == null) {
                    throw new JSONException(ingredientType + " RealTemplate '" + realTemplateName + "' does not exist as a template.");
                }
                ingredient.setRealTemplate(realIT);
            }
        }
        if (currentJO.has("difficulty")) {
            final int dif = currentJO.getInt("difficulty");
            if (dif < 0 || dif > 100) {
                throw new JSONException("Difficulty for ingredient '" + idName + "' is out of range (0..100), was " + dif + " in recipe.");
            }
            ingredient.setDifficulty(dif);
        }
        if (isResult) {
            if (currentJO.has("name")) {
                ingredient.setResultName(currentJO.getString("name"));
            }
            if (currentJO.has("refmaterial")) {
                final String ingredientRef = currentJO.getString("refmaterial");
                if (recipe.getTargetItem() != null && recipe.getTargetItem().getTemplateName().equalsIgnoreCase(ingredientRef)) {
                    ingredient.setMaterialRef(ingredientRef);
                }
                else {
                    final IngredientGroup group = recipe.getGroupByType(1);
                    if (group == null || !group.contains(ingredientRef)) {
                        throw new JSONException("Result ref material '" + ingredientRef + "' does not reference a mandatory ingredient.");
                    }
                    ingredient.setMaterialRef(ingredientRef);
                }
            }
            if (currentJO.has("refrealtemplate")) {
                final String ingredientRef = currentJO.getString("refrealtemplate");
                if (recipe.getTargetItem() != null && recipe.getTargetItem().getTemplateName().equalsIgnoreCase(ingredientRef)) {
                    ingredient.setRealTemplateRef(ingredientRef);
                }
                else if (recipe.hasContainer(ingredientRef)) {
                    ingredient.setRealTemplateRef(ingredientRef);
                }
                else {
                    final IngredientGroup group = recipe.getGroupByType(1);
                    if (group == null || !group.contains(ingredientRef)) {
                        throw new JSONException("Result ref realtemplate '" + ingredientRef + "' does not reference a mandatory ingredient.");
                    }
                    ingredient.setRealTemplateRef(ingredientRef);
                }
            }
            if (currentJO.has("achievement")) {
                final String achievementStr = currentJO.getString("achievement");
                final AchievementTemplate at = Achievement.getTemplate(achievementStr);
                if (at == null) {
                    throw new JSONException("Achievement '" + achievementStr + "' does not reference an achievement.");
                }
                if (!at.isForCooking()) {
                    throw new JSONException("Achievement '" + achievementStr + "' is not for recipes.");
                }
                recipe.setAchievementTriggered(at.getNumber(), at.getName());
            }
            if (currentJO.has("usetemplateweight")) {
                ingredient.setUseResultTemplateWeight(currentJO.getBoolean("usetemplateweight"));
            }
            if (currentJO.has("description")) {
                ingredient.setResultDescription(currentJO.getString("description"));
            }
            if (currentJO.has("icon")) {
                final String iconName = currentJO.getString("icon");
                final int icon = IconConstants.getRecipeIconFromName(iconName);
                if (icon < 0) {
                    throw new JSONException("No Icon found with name of '" + iconName + "' in recipe in '" + recipe.getName() + "' (" + recipe.getRecipeId() + ").");
                }
                ingredient.setIcon((short)icon);
            }
        }
        else {
            if (currentJO.has("creature")) {
                final String corpseName = currentJO.getString("creature");
                try {
                    final CreatureTemplate ct = CreatureTemplateFactory.getInstance().getTemplate(corpseName);
                    ingredient.setCorpseData(ct.getTemplateId(), corpseName);
                }
                catch (Exception e) {
                    throw new JSONException(ingredientType + " Creature '" + corpseName + "' does not exist as a creature.");
                }
            }
            if (ingredient.isLiquid()) {
                if (!currentJO.has("ratio")) {
                    throw new JSONException("Ratio is missing for liquid ingredient '" + idName + "'.");
                }
                final int rat = currentJO.getInt("ratio");
                if (rat < 0 || rat > 10000) {
                    throw new JSONException("Ratio percentage for ingredient '" + idName + "' is out of range (0..10000), was " + rat + " in recipe.");
                }
                ingredient.setRatio(rat);
            }
            else if (currentJO.has("amount")) {
                final int amo = currentJO.getInt("amount");
                if (amo < 1 || amo > 3) {
                    throw new JSONException("Amount for ingredient '" + idName + "' is out of range (1..3), was " + amo + " in recipe.");
                }
                ingredient.setAmount(amo);
            }
            if (currentJO.has("loss")) {
                final int los = currentJO.getInt("loss");
                if (los < 0 || los > 100) {
                    throw new JSONException("Loss for ingredient '" + idName + "' is out of range (0..100), was " + los + " in recipe.");
                }
                ingredient.setLoss(los);
            }
            else if (ingredient.isLiquid()) {
                throw new JSONException("Loss is missing for liquid ingredient '" + idName + "'.");
            }
            ingredient.setIngredientId(recipe.getIngredientCount());
        }
        recipe.addIngredient(ingredient);
    }
    
    private static void checkRecipeSchema(final short recipeId, final String level, final JSONObject recipeJO) throws JSONException {
        final Iterator keys = recipeJO.keys();
        while (keys.hasNext()) {
            final String nextKey = keys.next();
            switch (level) {
                case "recipe": {
                    final String s = nextKey;
                    switch (s) {
                        case "name":
                        case "recipeid":
                        case "skill":
                        case "trigger":
                        case "cookers":
                        case "containers":
                        case "active":
                        case "target":
                        case "ingredients":
                        case "result":
                        case "known":
                        case "nameable":
                        case "lootable": {
                            continue;
                        }
                        default: {
                            throw new JSONException("Recipe " + recipeId + " invalid " + level + " attribute " + nextKey + ".");
                        }
                    }
                    break;
                }
                case "lootable": {
                    final String s2 = nextKey;
                    switch (s2) {
                        case "creature":
                        case "rarity": {
                            continue;
                        }
                        default: {
                            throw new JSONException("Recipe " + recipeId + " invalid " + level + " attribute " + nextKey + ".");
                        }
                    }
                    break;
                }
                case "cookers":
                case "containers": {
                    final String s3 = nextKey;
                    switch (s3) {
                        case "id":
                        case "difficulty": {
                            continue;
                        }
                        default: {
                            throw new JSONException("Recipe " + recipeId + " invalid " + level + " attribute " + nextKey + ".");
                        }
                    }
                    break;
                }
                case "active": {
                    final String s4 = nextKey;
                    switch (s4) {
                        case "id":
                        case "cstate":
                        case "pstate":
                        case "material":
                        case "realtemplate":
                        case "difficulty":
                        case "loss":
                        case "ratio": {
                            continue;
                        }
                        default: {
                            throw new JSONException("Recipe " + recipeId + " invalid " + level + " attribute " + nextKey + ".");
                        }
                    }
                    break;
                }
                case "target": {
                    final String s5 = nextKey;
                    switch (s5) {
                        case "id":
                        case "cstate":
                        case "pstate":
                        case "material":
                        case "realtemplate":
                        case "difficulty":
                        case "loss":
                        case "ratio":
                        case "creature": {
                            continue;
                        }
                        default: {
                            throw new JSONException("Recipe " + recipeId + " invalid " + level + " attribute " + nextKey + ".");
                        }
                    }
                    break;
                }
                case "ingredients group": {
                    final String s6 = nextKey;
                    switch (s6) {
                        case "mandatory":
                        case "optional":
                        case "oneof":
                        case "zeroorone":
                        case "oneormore":
                        case "any": {
                            continue;
                        }
                        default: {
                            throw new JSONException("Recipe " + recipeId + " invalid " + level + " attribute " + nextKey + ".");
                        }
                    }
                    break;
                }
                case "oneof group":
                case "zeroorone group":
                case "oneormore group": {
                    final String s7 = nextKey;
                    switch (s7) {
                        case "list": {
                            continue;
                        }
                        default: {
                            throw new JSONException("Recipe " + recipeId + " invalid " + level + " attribute " + nextKey + ".");
                        }
                    }
                    break;
                }
                case "mandatory":
                case "optional":
                case "oneof":
                case "zeroorone":
                case "oneormore":
                case "any": {
                    final String s8 = nextKey;
                    switch (s8) {
                        case "id":
                        case "cstate":
                        case "pstate":
                        case "material":
                        case "realtemplate":
                        case "difficulty":
                        case "loss":
                        case "ratio":
                        case "amount": {
                            continue;
                        }
                        default: {
                            throw new JSONException("Recipe " + recipeId + " invalid " + level + " attribute " + nextKey + ".");
                        }
                    }
                    break;
                }
                case "result": {
                    final String s9 = nextKey;
                    switch (s9) {
                        case "id":
                        case "name":
                        case "cstate":
                        case "pstate":
                        case "material":
                        case "realtemplate":
                        case "refmaterial":
                        case "refrealtemplate":
                        case "difficulty":
                        case "description":
                        case "achievement":
                        case "usetemplateweight":
                        case "icon": {
                            continue;
                        }
                        default: {
                            throw new JSONException("Recipe " + recipeId + " invalid " + level + " attribute " + nextKey + ".");
                        }
                    }
                    break;
                }
                default: {
                    throw new JSONException("Recipe " + recipeId + " invalid " + level + " when checing attributes .");
                }
            }
        }
    }
    
    private static byte convertCookingStateIntoByte(final short recipeId, final String state) {
        switch (state) {
            case "raw": {
                return 0;
            }
            case "fried": {
                return 1;
            }
            case "grilled": {
                return 2;
            }
            case "boiled": {
                return 3;
            }
            case "roasted": {
                return 4;
            }
            case "steamed": {
                return 5;
            }
            case "baked": {
                return 6;
            }
            case "cooked": {
                return 7;
            }
            case "candied": {
                return 8;
            }
            case "chocolate coated": {
                return 9;
            }
            default: {
                Recipes.logger.warning("Recipe " + recipeId + " has unknown state name:" + state);
                return 0;
            }
        }
    }
    
    private static byte convertPhysicalStateIntoByte(final short recipeId, final String state) {
        if (state.contains("+")) {
            final String[] states = state.split("\\+");
            byte theByte = 0;
            for (final String s : states) {
                final byte code = convertPhysicalStateIntoByte(recipeId, s);
                theByte |= code;
            }
            return theByte;
        }
        switch (state) {
            case "none": {
                return 0;
            }
            case "chopped": {
                return 16;
            }
            case "diced": {
                return 16;
            }
            case "ground": {
                return 16;
            }
            case "unfermented": {
                return 16;
            }
            case "zombiefied": {
                return 16;
            }
            case "whipped": {
                return 16;
            }
            case "mashed": {
                return 32;
            }
            case "minced": {
                return 32;
            }
            case "fermenting": {
                return 32;
            }
            case "clotted": {
                return 32;
            }
            case "wrapped": {
                return 64;
            }
            case "undistilled": {
                return 64;
            }
            case "salted": {
                return -128;
            }
            case "fresh": {
                return -128;
            }
            default: {
                Recipes.logger.warning("Recipe " + recipeId + " has unknown state name:" + state);
                return 0;
            }
        }
    }
    
    private static byte convertRarityStringIntoByte(final short recipeId, final String rarityName) {
        switch (rarityName) {
            case "common": {
                return 0;
            }
            case "rare": {
                return 1;
            }
            case "supreme": {
                return 2;
            }
            case "fantastic": {
                return 3;
            }
            default: {
                Recipes.logger.warning("Recipe " + recipeId + " has unknown rarity name:" + rarityName);
                return 0;
            }
        }
    }
    
    @Nullable
    public static Recipe getRecipeFor(final long playerId, final byte wantedType, @Nullable final Item activeItem, final Item targetItem, final boolean checkActive, final boolean checkLiquids) {
        return getRecipeFor(playerId, activeItem, targetItem, wantedType, checkActive, checkLiquids);
    }
    
    @Nullable
    private static Recipe getRecipeFor(final long playerId, @Nullable final Item activeItem, final Item targetItem, final byte wantedType, final boolean checkActive, final boolean checkLiquids) {
        for (final Recipe recipe : Recipes.recipesList) {
            if (recipe.getTrigger() == wantedType && isRecipeOk(playerId, recipe, activeItem, targetItem, checkActive, checkLiquids) != 0) {
                return recipe;
            }
        }
        return null;
    }
    
    public static int isRecipeOk(final long playerId, final Recipe recipe, @Nullable final Item activeItem, final Item target, final boolean checkActive, final boolean checkLiquids) {
        if (recipe.isRecipeOk(playerId, activeItem, target, checkActive, checkLiquids)) {
            return 1;
        }
        if (recipe.getTrigger() == 2 && activeItem != null && !activeItem.getTemplate().isCookingTool() && !target.getTemplate().isFoodMaker() && recipe.isRecipeOk(playerId, target, activeItem, checkActive, checkLiquids)) {
            return 2;
        }
        return 0;
    }
    
    public static Recipe[] getPartialRecipeListFor(final Creature performer, final byte wantedType, final Item target) {
        final Set<Recipe> recipes = new HashSet<Recipe>();
        for (final Recipe recipe : Recipes.recipesList) {
            if (recipe.getTrigger() == wantedType && recipe.isPartialMatch(target)) {
                recipes.add(recipe);
            }
        }
        return recipes.toArray(new Recipe[recipes.size()]);
    }
    
    @Nullable
    public static Recipe getRecipeByActionId(final short id) {
        return Recipes.recipes.get(id);
    }
    
    @Nullable
    public static Recipe getRecipeById(final short id) {
        return Recipes.recipes.get((short)(id + 8000));
    }
    
    @Nullable
    public static Recipe getRecipeByResult(final Ingredient ingredient) {
        for (final Recipe recipe : Recipes.recipesList) {
            if (recipe.matchesResult(ingredient, true)) {
                return recipe;
            }
        }
        for (final Recipe recipe : Recipes.recipesList) {
            if (recipe.matchesResult(ingredient, false)) {
                return recipe;
            }
        }
        return null;
    }
    
    @Nullable
    public static Recipe[] getRecipesByResult(final Ingredient ingredient) {
        final LinkedList<Recipe> recipes = new LinkedList<Recipe>();
        for (final Recipe recipe : Recipes.recipesList) {
            if (recipe.matchesResult(ingredient, true)) {
                recipes.add(recipe);
            }
        }
        if (recipes.size() == 0) {
            for (final Recipe recipe : Recipes.recipesList) {
                if (recipe.matchesResult(ingredient, false)) {
                    recipes.add(recipe);
                }
            }
        }
        final Recipe[] recipesArr = recipes.toArray(new Recipe[recipes.size()]);
        if (recipesArr.length > 1) {
            Arrays.sort(recipesArr, new Comparator<Recipe>() {
                @Override
                public int compare(final Recipe param1, final Recipe param2) {
                    return param1.getName().compareTo(param2.getName());
                }
            });
        }
        return recipesArr;
    }
    
    public static String getIngredientName(@Nullable final Ingredient ingredient) {
        return getIngredientName(ingredient, true);
    }
    
    public static String getIngredientName(@Nullable final Ingredient ingredient, final boolean withAmount) {
        if (ingredient == null) {
            return "";
        }
        final StringBuilder buf = new StringBuilder();
        if (ingredient.hasCState()) {
            buf.append(ingredient.getCStateName());
            if (ingredient.hasPState()) {
                buf.append(" " + ingredient.getPStateName());
            }
            buf.append(" ");
        }
        else if (ingredient.hasPState()) {
            buf.append(ingredient.getPStateName() + " ");
        }
        if (ingredient.hasCorpseData()) {
            buf.append(ingredient.getCorpseName() + " corpse");
            return buf.toString();
        }
        if (ingredient.hasMaterial() || ingredient.hasRealTemplate() || ingredient.hasMaterialRef() || ingredient.hasRealTemplateRef()) {
            final Recipe[] recipes = getRecipesByResult(ingredient);
            if (recipes.length > 0) {
                final StringBuilder buf2 = new StringBuilder();
                if (recipes.length == 1) {
                    buf2.append(recipes[0].getResultName(ingredient));
                }
                else {
                    buf2.append(ingredient.getName(withAmount));
                }
                return buf2.toString();
            }
        }
        return ingredient.getName(withAmount);
    }
    
    public static boolean isRecipeAction(final short action) {
        return action >= 8000 && action < 10000;
    }
    
    public static Set<Recipe> getKnownRecipes() {
        final Set<Recipe> knownRecipes = new HashSet<Recipe>();
        for (final Recipe recipe : Recipes.recipesList) {
            if (recipe.isKnown()) {
                knownRecipes.add(recipe);
            }
        }
        return knownRecipes;
    }
    
    public static Recipe[] getUnknownRecipes() {
        final LinkedList<Recipe> unknownRecipes = new LinkedList<Recipe>();
        for (final Recipe recipe : Recipes.recipesList) {
            if (!recipe.isKnown()) {
                unknownRecipes.add(recipe);
            }
        }
        return unknownRecipes.toArray(new Recipe[unknownRecipes.size()]);
    }
    
    public static Recipe[] getAllRecipes() {
        return Recipes.recipesList.toArray(new Recipe[Recipes.recipesList.size()]);
    }
    
    public static boolean isKnownRecipe(final short recipeId) {
        final Recipe recipe = getRecipeById(recipeId);
        return recipe != null && recipe.isKnown();
    }
    
    static {
        logger = Logger.getLogger(Recipes.class.getName());
        recipes = new HashMap<Short, Recipe>();
        namedRecipes = new HashMap<Short, String>();
        recipesList = new ArrayList<Recipe>();
    }
}
