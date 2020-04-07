// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.Map;
import com.wurmonline.server.skills.SkillSystem;
import com.wurmonline.server.items.Recipes;
import java.util.Arrays;
import java.util.Comparator;
import com.wurmonline.server.Servers;
import java.util.Iterator;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.Server;
import com.wurmonline.server.items.RecipesByPlayer;
import java.util.logging.Level;
import java.util.Properties;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import com.wurmonline.server.creatures.Creature;
import java.util.ArrayList;
import com.wurmonline.server.items.Recipe;
import com.wurmonline.server.items.Ingredient;
import java.util.logging.Logger;

public class CookBookQuestion extends Question
{
    private static final Logger logger;
    private byte displayType;
    private final int sortBy;
    private Ingredient[] ingredients;
    private Ingredient ingred;
    private Recipe recip;
    private boolean showExtra;
    private boolean showLinks;
    private String from;
    private String searchFor;
    ArrayList<String> history;
    private static final String red = "color=\"255,127,127\"";
    private static final String green = "color=\"127,255,127\"";
    public static final byte TYPE_INFO = 0;
    public static final byte TYPE_TARGET_ACTION_RECIPES = 1;
    public static final byte TYPE_CONTAINER_ACTION_RECIPES = 2;
    public static final byte TYPE_HEAT_RECIPES = 3;
    public static final byte TYPE_TIME_RECIPES = 4;
    public static final byte TYPE_COOKERS_LIST = 5;
    public static final byte TYPE_COOKER_RECIPES = 6;
    public static final byte TYPE_CONTAINERS_LIST = 7;
    public static final byte TYPE_CONTAINER_RECIPES = 8;
    public static final byte TYPE_TOOLS_LIST = 9;
    public static final byte TYPE_TOOL_RECIPES = 10;
    public static final byte TYPE_INGREDIENTS_LIST = 11;
    public static final byte TYPE_INGREDIENT_RECIPES = 12;
    public static final byte TYPE_RECIPE = 13;
    public static final byte TYPE_SEARCH_RECIPES = 14;
    public static final byte TYPE_BACK = 15;
    
    public CookBookQuestion(final Creature aResponder, final long aTargetId) {
        super(aResponder, aResponder.getName() + "'s CookBook", makeTitle(aResponder, (byte)0, aTargetId), 135, aTargetId);
        this.ingred = null;
        this.recip = null;
        this.showExtra = false;
        this.showLinks = true;
        this.from = "";
        this.searchFor = "";
        this.history = new ArrayList<String>();
        this.sortBy = 1;
        if (aTargetId == -10L) {
            this.displayType = 0;
        }
        else {
            try {
                final Item target = Items.getItem(aTargetId);
                if (target.getTemplate().isCooker()) {
                    this.displayType = 6;
                }
                else if (target.getTemplate().isCookingTool()) {
                    this.displayType = 10;
                }
                else {
                    this.displayType = 12;
                }
            }
            catch (NoSuchItemException e) {
                this.displayType = 0;
            }
        }
    }
    
    public CookBookQuestion(final Creature aResponder, final byte aDisplayType, final long aTargetId) {
        super(aResponder, aResponder.getName() + "'s Cookbook", makeTitle(aResponder, aDisplayType, aTargetId), 135, aTargetId);
        this.ingred = null;
        this.recip = null;
        this.showExtra = false;
        this.showLinks = true;
        this.from = "";
        this.searchFor = "";
        this.history = new ArrayList<String>();
        this.sortBy = 1;
        this.displayType = aDisplayType;
    }
    
    public CookBookQuestion(final Creature aResponder, final byte aDisplayType, final long aTargetId, final int sortId) {
        super(aResponder, aResponder.getName() + "'s Cookbook", makeTitle(aResponder, aDisplayType, aTargetId), 135, aTargetId);
        this.ingred = null;
        this.recip = null;
        this.showExtra = false;
        this.showLinks = true;
        this.from = "";
        this.searchFor = "";
        this.history = new ArrayList<String>();
        this.sortBy = sortId;
        this.displayType = aDisplayType;
    }
    
    public CookBookQuestion(final Creature aResponder, final byte aDisplayType, final Ingredient ingredient) {
        super(aResponder, aResponder.getName() + "'s Cookbook", "List of recipes that use " + ingredient.getName(false) + ".", 135, -10L);
        this.ingred = null;
        this.recip = null;
        this.showExtra = false;
        this.showLinks = true;
        this.from = "";
        this.searchFor = "";
        this.history = new ArrayList<String>();
        this.sortBy = 1;
        this.displayType = aDisplayType;
        this.ingred = ingredient;
    }
    
    public CookBookQuestion(final Creature aResponder, final byte aDisplayType, final Recipe recipe, final boolean justRecipe, final long aTarget, final String signedBy) {
        super(aResponder, justRecipe ? recipe.getName() : (aResponder.getName() + "'s Cookbook"), "Recipe: " + recipe.getName(), 135, aTarget);
        this.ingred = null;
        this.recip = null;
        this.showExtra = false;
        this.showLinks = true;
        this.from = "";
        this.searchFor = "";
        this.history = new ArrayList<String>();
        this.sortBy = 1;
        this.displayType = aDisplayType;
        this.recip = recipe;
        this.showLinks = !justRecipe;
        if (signedBy.length() > 0) {
            this.from = signedBy;
        }
        if (justRecipe) {
            this.history.add(aDisplayType + "," + recipe.getRecipeId() + "," + justRecipe + "," + aTarget + "," + signedBy);
        }
    }
    
    public CookBookQuestion(final Creature aResponder, final String searchFor, final int sortId) {
        super(aResponder, aResponder.getName() + "'s Cookbook", (searchFor.length() > 0) ? ("List of recipes that have a name with " + searchFor + " in it.") : "List of all your known recipes.", 135, -10L);
        this.ingred = null;
        this.recip = null;
        this.showExtra = false;
        this.showLinks = true;
        this.from = "";
        this.searchFor = "";
        this.history = new ArrayList<String>();
        this.sortBy = sortId;
        this.displayType = 14;
        this.searchFor = searchFor;
    }
    
    static String makeTitle(final Creature aResponder, final byte aType, final long templateId) {
        switch (aType) {
            case 0: {
                return aResponder.getName() + "'s Cookbook";
            }
            case 1: {
                return "Target Action Recipe List";
            }
            case 2: {
                return "Container Action Recipe List";
            }
            case 3: {
                return "Heat Recipe List";
            }
            case 4: {
                return "Time Recipe List";
            }
            case 5: {
                return "List of known cookers";
            }
            case 6: {
                final ItemTemplate cookerIT = ItemTemplateFactory.getInstance().getTemplateOrNull((int)templateId);
                final String cookerName = (cookerIT == null) ? "xxxx" : cookerIT.getName();
                return "Recipes made in " + cookerName;
            }
            case 7: {
                return "List of known containers";
            }
            case 8: {
                final ItemTemplate containerIT = ItemTemplateFactory.getInstance().getTemplateOrNull((int)templateId);
                final String cookerName = (containerIT == null) ? "xxxx" : containerIT.getName();
                return "Recipes made in " + cookerName;
            }
            case 9: {
                return "List of known tools";
            }
            case 10: {
                final ItemTemplate toolIT = ItemTemplateFactory.getInstance().getTemplateOrNull((int)templateId);
                final String toolName = (toolIT == null) ? "xxxx" : toolIT.getName();
                return "Recipes made with a " + toolName;
            }
            case 11: {
                return "List of known ingredients";
            }
            case 12: {
                return "List of Recipes that use a xxxx";
            }
            case 14: {
                return "List of recipes that were found with xxxx in";
            }
            default: {
                return "";
            }
        }
    }
    
    @Override
    public void answer(final Properties aAnswer) {
        this.setAnswer(aAnswer);
        if (this.type == 0) {
            CookBookQuestion.logger.log(Level.INFO, "Received answer for a question with NOQUESTION.");
            return;
        }
        if (this.type != 135) {
            return;
        }
        final boolean close = this.getBooleanProp("close");
        if (close) {
            return;
        }
        final boolean add = this.getBooleanProp("add");
        if (add && this.target != -10L) {
            try {
                final Item paper = Items.getItem(this.target);
                final Item parent = paper.getTopParentOrNull();
                if (parent != null && parent.isInventory()) {
                    if (RecipesByPlayer.addRecipe(this.getResponder(), this.recip)) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("You finish adding the " + this.recip.getName() + " into your cookbook, just in time, as the recipe has decayed away.");
                        Server.getInstance().broadCastAction(this.getResponder().getName() + " stops writing.", this.getResponder(), 5);
                        Items.destroyItem(this.target);
                        this.getResponder().getCommunicator().sendCookbookRecipe(this.recip);
                    }
                }
                else {
                    this.getResponder().getCommunicator().sendNormalServerMessage("Cannot find the recipe on you!");
                }
            }
            catch (NoSuchItemException e) {
                CookBookQuestion.logger.log(Level.WARNING, e.getMessage(), e);
            }
            return;
        }
        for (final String key : this.getAnswer().stringPropertyNames()) {
            if (key.startsWith("sort")) {
                final String sid = key.substring(4);
                final int newSort = Integer.parseInt(sid);
                if (this.searchFor.equalsIgnoreCase("")) {
                    final CookBookQuestion cbq = new CookBookQuestion(this.getResponder(), this.displayType, this.target, newSort);
                    cbq.history = this.history;
                    cbq.ingred = this.ingred;
                    cbq.sendQuestion();
                    return;
                }
                final CookBookQuestion cbq = new CookBookQuestion(this.getResponder(), this.searchFor, newSort);
                cbq.history = this.history;
                cbq.ingred = this.ingred;
                cbq.sendQuestion();
                return;
            }
        }
        Label_1518: {
            if (this.getBooleanProp("find")) {
                final String srch = aAnswer.getProperty("search");
                if (srch != null) {
                    final CookBookQuestion cbq2 = new CookBookQuestion(this.getResponder(), srch, 0);
                    (cbq2.history = this.history).add("14," + srch);
                    cbq2.sendQuestion();
                    return;
                }
            }
            else if (this.getBooleanProp("remove")) {
                RecipesByPlayer.removeRecipeForPlayer(this.getResponder().getWurmId(), this.recip.getRecipeId());
                this.getResponder().getCommunicator().sendCookbookRecipe(this.recip);
            }
            else if (this.getBooleanProp("back")) {
                if (this.history.size() < 2) {
                    final CookBookQuestion cbq3 = new CookBookQuestion(this.getResponder(), 0L);
                    cbq3.history.add("0");
                    cbq3.sendQuestion();
                    return;
                }
                this.history.remove(this.history.size() - 1);
                final String last = this.history.get(this.history.size() - 1);
                final String[] parts = last.split(",");
                final byte type = Byte.parseByte(parts[0]);
                switch (type) {
                    case 0: {
                        final CookBookQuestion cbq4 = new CookBookQuestion(this.getResponder(), 0L);
                        cbq4.history.add("0");
                        cbq4.sendQuestion();
                        return;
                    }
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11: {
                        final int id = Integer.parseInt(parts[1]);
                        final CookBookQuestion cbq = new CookBookQuestion(this.getResponder(), type, id);
                        cbq.history = this.history;
                        cbq.sendQuestion();
                        return;
                    }
                    case 12: {
                        final int templateId = Integer.parseInt(parts[1]);
                        final byte cstate = Byte.parseByte(parts[2]);
                        final byte pstate = Byte.parseByte(parts[3]);
                        final byte material = Byte.parseByte(parts[4]);
                        final boolean hasRealTemplate = Boolean.parseBoolean(parts[5]);
                        final int realTemplateId = Integer.parseInt(parts[6]);
                        final int corpseData = Integer.parseInt(parts[7]);
                        try {
                            final Ingredient ingredient = new Ingredient(templateId, cstate, pstate, material, hasRealTemplate, realTemplateId, corpseData);
                            final CookBookQuestion cbq5 = new CookBookQuestion(this.getResponder(), type, ingredient);
                            cbq5.history = this.history;
                            cbq5.sendQuestion();
                            return;
                        }
                        catch (NoSuchTemplateException e2) {
                            CookBookQuestion.logger.log(Level.WARNING, e2.getMessage(), e2);
                        }
                    }
                    case 13: {
                        final int id = Integer.parseInt(parts[1]);
                        final Recipe recipe = RecipesByPlayer.getRecipe(this.getResponder().getWurmId(), id);
                        final CookBookQuestion cbq6 = new CookBookQuestion(this.getResponder(), (byte)13, recipe, false, -10L, "");
                        cbq6.history = this.history;
                        cbq6.sendQuestion();
                        return;
                    }
                    case 14: {
                        final String sFor = (parts.length > 1) ? parts[1] : "";
                        final CookBookQuestion cbq = new CookBookQuestion(this.getResponder(), sFor, 0);
                        cbq.history = this.history;
                        cbq.sendQuestion();
                        return;
                    }
                }
            }
            else if (this.getBooleanProp("show")) {
                final String sel = aAnswer.getProperty("sel");
                final String[] parts = sel.split(",");
                final byte type = Byte.parseByte(parts[0]);
                if (type == 12) {
                    final int templateId = Integer.parseInt(parts[1]);
                    final byte cstate = Byte.parseByte(parts[2]);
                    final byte pstate = Byte.parseByte(parts[3]);
                    final byte material = Byte.parseByte(parts[4]);
                    final boolean hasRealTemplate = Boolean.parseBoolean(parts[5]);
                    final int realTemplateId = Integer.parseInt(parts[6]);
                    final int corpseData = Integer.parseInt(parts[7]);
                    try {
                        final Ingredient ingredient = new Ingredient(templateId, cstate, pstate, material, hasRealTemplate, realTemplateId, corpseData);
                        final CookBookQuestion cbq5 = new CookBookQuestion(this.getResponder(), type, ingredient);
                        cbq5.history = this.history;
                        this.history.add("12," + templateId + "," + cstate + "," + pstate + "," + material + "," + hasRealTemplate + "," + realTemplateId + "," + corpseData);
                        cbq5.sendQuestion();
                        return;
                    }
                    catch (NoSuchTemplateException e2) {
                        CookBookQuestion.logger.log(Level.WARNING, e2.getMessage(), e2);
                        break Label_1518;
                    }
                }
                if (type == 13) {
                    final int id = Integer.parseInt(parts[1]);
                    final Recipe recipe = RecipesByPlayer.getRecipe(this.getResponder().getWurmId(), id);
                    final CookBookQuestion cbq6 = new CookBookQuestion(this.getResponder(), (byte)13, recipe, false, -10L, "");
                    cbq6.history = this.history;
                    this.history.add("13," + recipe.getRecipeId() + "," + false + "," + id + ",");
                    cbq6.sendQuestion();
                    return;
                }
                final int id = Integer.parseInt(parts[1]);
                final CookBookQuestion cbq = new CookBookQuestion(this.getResponder(), type, id);
                (cbq.history = this.history).add(type + "," + id);
                cbq.sendQuestion();
                return;
            }
        }
        final CookBookQuestion cbq3 = new CookBookQuestion(this.getResponder(), 0L);
        cbq3.history.add("0");
        cbq3.sendQuestion();
    }
    
    @Override
    public void sendQuestion() {
        this.showExtra = (this.getResponder().getPower() > 4 && Servers.isThisATestServer());
        switch (this.displayType) {
            case 0: {
                this.sendInfo();
                break;
            }
            case 1:
            case 2:
            case 3:
            case 4:
            case 6:
            case 8:
            case 10:
            case 12:
            case 14: {
                this.sendRecipes();
                break;
            }
            case 5:
            case 7:
            case 9:
            case 11: {
                this.sendList();
                break;
            }
            case 13: {
                this.sendRecipe();
                break;
            }
        }
    }
    
    public void sendInfo() {
        final StringBuilder buf = new StringBuilder();
        final String closeBtn = "harray{label{text=\" \"};button{text=\"Close\";id=\"close\";hover=\"Close the cookbook.\"};label{text=\" \"}};";
        buf.append("border{border{size=\"20,20\";null;null;center{varray{header{text=\"" + this.question + "\"}}};" + "harray{label{text=\" \"};button{text=\"Close\";id=\"close\";hover=\"Close the cookbook.\"};label{text=\" \"}};" + "null;}null;scroll{vertical=\"true\";horizontal=\"false\";varray{rescale=\"true\";passthrough{id=\"id\";text=\"" + this.getId() + "\"}");
        buf.append("label{type=\"bold\";text=\"Recipes are split into various categories, these are:\"};");
        buf.append("radio{group=\"sel\";id=\"0,-10\";selected=\"true\";hidden=\"true\"};");
        buf.append("table{rows=\"4\";cols=\"2\";");
        buf.append("radio{group=\"sel\";id=\"1,-10\"};label{text=\"Target actions\";hover=\"e.g. ones where you use one item on another.\"};");
        buf.append("radio{group=\"sel\";id=\"2,-10\"};label{text=\"Container actions\";hover=\"e.g. when you use an item on a container.\"};");
        buf.append("radio{group=\"sel\";id=\"3,-10\"};label{text=\"Heat \";hover=\"normal cooking ones.\"};");
        buf.append("radio{group=\"sel\";id=\"4,-10\"};label{text=\"Time \";hover=\"ones that take time e.g. brewing).\"};");
        buf.append("}");
        buf.append("label{text=\"Selecting one of the above types will give a list of the recipes that you know about of that type.\"};");
        buf.append("label{text=\"\"};");
        buf.append("label{type=\"bold\";text=\"Or you can get a list of:\"};");
        buf.append("table{rows=\"4\";cols=\"2\";");
        buf.append("radio{group=\"sel\";id=\"5,-10\"};label{text=\"Cookers\"};");
        buf.append("radio{group=\"sel\";id=\"7,-10\"};label{text=\"Containers\"};");
        buf.append("radio{group=\"sel\";id=\"9,-10\"};label{text=\"Tools\"};");
        buf.append("radio{group=\"sel\";id=\"11,-10\"};label{text=\"Ingredients\"};");
        buf.append("}");
        buf.append("text{text=\"Selecting one of the above searches will give a list of those items that are used in that category that you know about.\"};");
        buf.append("label{text=\"\"};");
        buf.append("harray{label{type=\"bold\";text=\"Select what you want to do above and click :\"};button{text=\"here\";id=\"show\";default=\"true\"}}");
        buf.append("label{text=\"\"};");
        buf.append("harray{label{text=\"Or you can \"}button{text=\"search\";id=\"find\";hover=\"Dont forget to add a search criteria.\"}label{text=\" for \"}input{maxchars=\"20\";id=\"search\";text=\"\";onenter=\"find\"}label{text=\" in your known recipe names.\"}}");
        buf.append("label{text=\"If you leave the input box blank and do a search, it will list all of your known recipes.\"};");
        buf.append("label{text=\"\"};");
        final Recipe[] knownrecipes = RecipesByPlayer.getSearchRecipesFor(this.getResponder().getWurmId(), "");
        buf.append("label{text=\"You know a total of " + knownrecipes.length + " recipes\"};");
        buf.append("}};null;null;}");
        this.getResponder().getCommunicator().sendBml(500, 480, true, false, buf.toString(), 200, 200, 200, this.title);
    }
    
    public void sendRecipes() {
        final StringBuilder buf = new StringBuilder();
        final String closeBtn = "harray{label{text=\" \"};harray{button{text=\"Back\";id=\"back\";hover=\"Go back to last screen.\"};label{text=\" \"};button{text=\"Close\";id=\"close\";hover=\"Close the cookbook.\"};};label{text=\" \"}};";
        buf.append("border{border{size=\"20,25\";null;null;label{type=\"bold\";text=\"" + this.question + ((this.showExtra && this.target != -10L) ? (" - " + this.target) : "") + "\"};" + "harray{label{text=\" \"};harray{button{text=\"Back\";id=\"back\";hover=\"Go back to last screen.\"};label{text=\" \"};button{text=\"Close\";id=\"close\";hover=\"Close the cookbook.\"};};label{text=\" \"}};" + "null;}null;scroll{vertical=\"true\";horizontal=\"false\";varray{rescale=\"true\";passthrough{id=\"id\";text=\"" + this.getId() + "\"}");
        int width = 470;
        final int height = 400;
        String mid = "null;";
        boolean defaultShow = true;
        switch (this.displayType) {
            case 1: {
                buf.append(this.sendTargetActionRecipes());
                break;
            }
            case 2: {
                buf.append(this.sendContainerActionRecipes());
                width = 730;
                break;
            }
            case 3: {
                buf.append(this.sendHeatRecipes());
                width = 730;
                break;
            }
            case 4: {
                buf.append(this.sendTimeRecipes());
                break;
            }
            case 6: {
                buf.append(this.sendCookerRecipes());
                break;
            }
            case 8: {
                buf.append(this.sendContainerRecipes());
                break;
            }
            case 10: {
                buf.append(this.sendToolRecipes());
                break;
            }
            case 12: {
                buf.append(this.sendIngredientRecipes());
                break;
            }
            case 14: {
                buf.append(this.sendSearchRecipes());
                mid = "center{harray{button{text=\"Search\";id=\"find\";default=\"true\"};label{text=\" \"};input{maxchars=\"20\";id=\"search\";text=\"" + this.searchFor + "\"}}};";
                defaultShow = false;
                break;
            }
        }
        buf.append("radio{group=\"sel\";id=\"-10\";selected=\"true\";hidden=\"true\";text=\"None\"}");
        buf.append("text{text=\"\"}");
        buf.append("}};null;");
        buf.append("border{size=\"20,20\";null;harray{label{text=\" \"};button{text=\"Show selected\";id=\"show\"" + (defaultShow ? "default=\"true\"" : "") + "}};" + mid + "harray{button{text=\"Go to info\";id=\"info\"};label{text=\" \"}};null;}");
        buf.append("}");
        this.getResponder().getCommunicator().sendBml(width, height, true, false, buf.toString(), 200, 200, 200, this.title);
    }
    
    private void sendList() {
        final StringBuilder buf = new StringBuilder();
        final String closeBtn = "harray{label{text=\" \"};harray{button{text=\"Back\";id=\"back\";hover=\"Go back to last screen.\"};label{text=\" \"};button{text=\"Close\";id=\"close\";hover=\"Close the cookbook.\"};};label{text=\" \"}};";
        buf.append("border{border{size=\"20,25\";null;null;label{type=\"bold\";text=\"" + this.question + "          \"};" + "harray{label{text=\" \"};harray{button{text=\"Back\";id=\"back\";hover=\"Go back to last screen.\"};label{text=\" \"};button{text=\"Close\";id=\"close\";hover=\"Close the cookbook.\"};};label{text=\" \"}};" + "null;}null;scroll{vertical=\"true\";horizontal=\"false\";varray{rescale=\"true\";passthrough{id=\"id\";text=\"" + this.getId() + "\"}");
        int width = 400;
        int height = 300;
        switch (this.displayType) {
            case 5: {
                buf.append(this.sendCookersList());
                break;
            }
            case 7: {
                buf.append(this.sendContainersList());
                break;
            }
            case 9: {
                buf.append(this.sendToolsList());
                break;
            }
            case 11: {
                buf.append(this.sendIngredientsList());
                width = (this.showExtra ? 550 : 450);
                height = 450;
                break;
            }
        }
        buf.append("radio{group=\"sel\";id=\"-10,-10\";selected=\"true\";hidden=\"true\";text=\"None\"}");
        buf.append("text{text=\"\"}");
        buf.append("}};null;");
        buf.append("border{size=\"20,20\";null;harray{label{text=\" \"};button{text=\"Show selected\";id=\"show\"}};null;harray{button{text=\"Go to info\";id=\"info\"};label{text=\" \"}};null;}");
        buf.append("}");
        this.getResponder().getCommunicator().sendBml(width, height, true, false, buf.toString(), 200, 200, 200, this.title);
    }
    
    public String sendTargetActionRecipes() {
        final StringBuilder buf = new StringBuilder();
        final Recipe[] recipes = RecipesByPlayer.getTargetActionRecipesFor(this.getResponder().getWurmId());
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        switch (absSortBy) {
            case 0: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        if (param1.getRecipeId() < param2.getRecipeId()) {
                            return -1;
                        }
                        return 1;
                    }
                });
                break;
            }
            case 1: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        return param1.getName().compareTo(param2.getName()) * upDown;
                    }
                });
                break;
            }
            case 2: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        return param1.getActiveItem().getName(false).compareTo(param2.getActiveItem().getName(false)) * upDown;
                    }
                });
                break;
            }
            case 3: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        return param1.getTargetItem().getName(false).compareTo(param2.getTargetItem().getName(false)) * upDown;
                    }
                });
                break;
            }
        }
        buf.append("table{rows=\"1\";cols=\"6\";label{text=\" \"};" + this.colHeader("Recipe Name", 1, this.sortBy) + "label{text=\" \"};" + this.colHeader("Active Item", 2, this.sortBy) + "label{text=\" \"};" + this.colHeader("Target Item", 3, this.sortBy));
        for (final Recipe recipe : recipes) {
            buf.append("radio{group=\"sel\";id=\"13," + recipe.getRecipeId() + "\";text=\"\"}" + this.colourRecipeName(recipe, recipe.getName(), false) + ((recipe.getActiveItem() == null) ? "label{text=\"\"}" : (recipe.getActiveItem().getTemplate().isCookingTool() ? ("radio{group=\"sel\";id=\"10," + recipe.getActiveItem().getTemplateId() + "\";text=\"\"}") : ("radio{group=\"sel\";id=\"12," + recipe.getActiveItem().getTemplateId() + "," + recipe.getActiveItem().getCState() + "," + recipe.getActiveItem().getPState() + "," + recipe.getActiveItem().getMaterial() + "," + recipe.getActiveItem().hasRealTemplate() + "," + recipe.getActiveItem().getRealTemplateId() + "," + recipe.getActiveItem().getCorpseData() + "\";text=\"\"}"))) + "label{text=\"" + Recipes.getIngredientName(recipe.getActiveItem()) + ((this.showExtra && recipe.getActiveItem() != null) ? (" - " + recipe.getActiveItem().getTemplateId()) : "") + "\"};" + ((recipe.getTargetItem() == null) ? "label{text=\"\"}" : (recipe.getTargetItem().getTemplate().isFoodMaker() ? ("radio{group=\"sel\";id=\"8," + recipe.getTargetItem().getTemplateId() + "\";text=\"\"}") : ("radio{group=\"sel\";id=\"12," + recipe.getTargetItem().getTemplateId() + "," + recipe.getTargetItem().getCState() + "," + recipe.getTargetItem().getPState() + "," + recipe.getTargetItem().getMaterial() + "," + recipe.getTargetItem().hasRealTemplate() + "," + recipe.getTargetItem().getRealTemplateId() + "," + recipe.getTargetItem().getCorpseData() + "\";text=\"\"}"))) + "label{text=\"" + Recipes.getIngredientName(recipe.getTargetItem()) + ((this.showExtra && recipe.getTargetItem() != null) ? (" - " + recipe.getTargetItem().getTemplateId()) : "") + "\"};");
        }
        buf.append("}");
        if (recipes.length == 0) {
            buf.append("label{text=\"You dont know any target action recipes.\"}");
        }
        return buf.toString();
    }
    
    public String sendContainerActionRecipes() {
        final StringBuilder buf = new StringBuilder();
        final Recipe[] recipes = RecipesByPlayer.getContainerActionRecipesFor(this.getResponder().getWurmId());
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        switch (absSortBy) {
            case 0: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        if (param1.getRecipeId() < param2.getRecipeId()) {
                            return -1;
                        }
                        return 1;
                    }
                });
                break;
            }
            case 1: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        return param1.getName().compareTo(param2.getName()) * upDown;
                    }
                });
                break;
            }
            case 2: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        return Recipes.getIngredientName(param1.getActiveItem()).compareTo(Recipes.getIngredientName(param2.getActiveItem())) * upDown;
                    }
                });
                break;
            }
            case 3: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        return param1.getContainersAsString().compareTo(param2.getContainersAsString()) * upDown;
                    }
                });
                break;
            }
            case 4: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        return param1.getIngredientsAsString().compareTo(param2.getIngredientsAsString()) * upDown;
                    }
                });
                break;
            }
        }
        buf.append("table{rows=\"1\";cols=\"8\";label{text=\" \"};" + this.colHeader("Recipe Name", 1, this.sortBy) + "label{text=\" \"};" + this.colHeader("Active Item", 2, this.sortBy) + "label{text=\" \"};" + this.colHeader("Containers", 3, this.sortBy) + "label{text=\" \"};" + this.colHeader("Ingredients", 4, this.sortBy));
        for (final Recipe recipe : recipes) {
            buf.append("radio{group=\"sel\";id=\"13," + recipe.getRecipeId() + "\";text=\"\"}" + this.colourRecipeName(recipe, recipe.getName(), false) + ((recipe.getActiveItem() == null) ? "label{text=\"\"}" : (recipe.getActiveItem().getTemplate().isCookingTool() ? ("radio{group=\"sel\";id=\"9," + recipe.getActiveItem().getTemplateId() + "\";text=\"\"}") : ("radio{group=\"sel\";id=\"12," + recipe.getActiveItem().getTemplateId() + "," + recipe.getActiveItem().getCState() + "," + recipe.getActiveItem().getPState() + "," + recipe.getActiveItem().getMaterial() + "," + recipe.getActiveItem().hasRealTemplate() + "," + recipe.getActiveItem().getRealTemplateId() + "," + recipe.getActiveItem().getCorpseData() + "\";text=\"\"}"))) + "label{text=\"" + recipe.getActiveItemName() + ((this.showExtra && recipe.getActiveItem() != null) ? (" - " + recipe.getActiveItem().getTemplateId()) : "") + "\"};" + (recipe.hasOneContainer() ? ("radio{group=\"sel\";id=\"7," + recipe.getContainerId() + "\";text=\"\"}") : "label{text=\"\"}") + "label{text=\"" + recipe.getContainersAsString() + "\"};label{text=\" \"};label{text=\"" + recipe.getIngredientsAsString() + "\"};");
        }
        buf.append("}");
        if (recipes.length == 0) {
            buf.append("label{text=\"You dont know any container action recipes.\"}");
        }
        return buf.toString();
    }
    
    public String sendHeatRecipes() {
        final StringBuilder buf = new StringBuilder();
        final Recipe[] recipes = RecipesByPlayer.getHeatRecipesFor(this.getResponder().getWurmId());
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        switch (absSortBy) {
            case 0: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        if (param1.getRecipeId() < param2.getRecipeId()) {
                            return -1;
                        }
                        return 1;
                    }
                });
                break;
            }
            case 1: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        return param1.getName().compareTo(param2.getName()) * upDown;
                    }
                });
                break;
            }
            case 2: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        return param1.getCookersAsString().compareTo(param2.getCookersAsString()) * upDown;
                    }
                });
                break;
            }
            case 3: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        return param1.getContainersAsString().compareTo(param2.getContainersAsString()) * upDown;
                    }
                });
                break;
            }
            case 4: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        return param1.getIngredientsAsString().compareTo(param2.getIngredientsAsString()) * upDown;
                    }
                });
                break;
            }
        }
        buf.append("table{rows=\"1\";cols=\"8\";label{text=\" \"};" + this.colHeader("Recipe Name", 1, this.sortBy) + "label{text=\" \"};" + this.colHeader("Cookers List", 2, this.sortBy) + "label{text=\" \"};" + this.colHeader("Containers List", 3, this.sortBy) + "label{text=\" \"};" + this.colHeader("Ingredients List", 4, this.sortBy));
        for (final Recipe recipe : recipes) {
            buf.append("radio{group=\"sel\";id=\"13," + recipe.getRecipeId() + "\";text=\"\"}" + this.colourRecipeName(recipe, recipe.getName(), false) + (recipe.hasOneCooker() ? ("radio{group=\"sel\";id=\"6," + recipe.getCookerId() + "\";text=\"\"}") : "label{text=\"\"}") + "label{text=\"" + recipe.getCookersAsString() + "\"};" + (recipe.hasOneContainer() ? ("radio{group=\"sel\";id=\"8," + recipe.getContainerId() + "\";text=\"\"}") : "label{text=\"\"}") + "label{text=\"" + recipe.getContainersAsString() + "\"};label{text=\" \"};label{text=\"" + recipe.getIngredientsAsString() + "\"};");
        }
        buf.append("}");
        if (recipes.length == 0) {
            buf.append("label{text=\"You dont know any heat recipes.\"}");
        }
        return buf.toString();
    }
    
    public String sendTimeRecipes() {
        final StringBuilder buf = new StringBuilder();
        final Recipe[] recipes = RecipesByPlayer.getTimeRecipesFor(this.getResponder().getWurmId());
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        switch (absSortBy) {
            case 0: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        if (param1.getRecipeId() < param2.getRecipeId()) {
                            return -1;
                        }
                        return 1;
                    }
                });
                break;
            }
            case 1: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        return param1.getName().compareTo(param2.getName()) * upDown;
                    }
                });
                break;
            }
            case 2: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        return param1.getContainersAsString().compareTo(param2.getContainersAsString()) * upDown;
                    }
                });
                break;
            }
            case 3: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        return param1.getIngredientsAsString().compareTo(param2.getIngredientsAsString()) * upDown;
                    }
                });
                break;
            }
        }
        buf.append("table{rows=\"1\";cols=\"6\";label{text=\" \"};" + this.colHeader("Recipe Name", 1, this.sortBy) + "label{text=\" \"};" + this.colHeader("Containers", 2, this.sortBy) + "label{text=\" \"};" + this.colHeader("Ingredients", 3, this.sortBy));
        for (final Recipe recipe : recipes) {
            buf.append("radio{group=\"sel\";id=\"13," + recipe.getRecipeId() + "\";text=\" \"}" + this.colourRecipeName(recipe, recipe.getName(), false) + (recipe.hasOneContainer() ? ("radio{group=\"sel\";id=\"7," + recipe.getContainerId() + "\";text=\"\"}") : "label{text=\" \"}") + "label{text=\"" + recipe.getContainersAsString() + "\"};label{text=\"\"};label{text=\"" + recipe.getIngredientsAsString() + "\"};");
        }
        buf.append("}");
        if (recipes.length == 0) {
            buf.append("label{text=\"You dont know any time recipes.\"}");
        }
        return buf.toString();
    }
    
    public String sendCookersList() {
        final StringBuilder buf = new StringBuilder();
        final ItemTemplate[] templates = RecipesByPlayer.getKnownCookersFor(this.getResponder().getWurmId());
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        switch (absSortBy) {
            case 0: {
                Arrays.sort(templates, new Comparator<ItemTemplate>() {
                    @Override
                    public int compare(final ItemTemplate param1, final ItemTemplate param2) {
                        if (param1.getTemplateId() < param2.getTemplateId()) {
                            return -1;
                        }
                        return 1;
                    }
                });
                break;
            }
            case 1: {
                Arrays.sort(templates, new Comparator<ItemTemplate>() {
                    @Override
                    public int compare(final ItemTemplate param1, final ItemTemplate param2) {
                        return param1.getName().compareTo(param2.getName()) * upDown;
                    }
                });
                break;
            }
        }
        buf.append("table{rows=\"1\";cols=\"3\";label{text=\" \"};" + this.colHeader("Cooker Name", 1, this.sortBy) + "label{text=\" \"};");
        for (final ItemTemplate itemTemplate : templates) {
            buf.append("radio{group=\"sel\";id=\"6," + itemTemplate.getTemplateId() + "\";text=\" \"}label{text=\"" + itemTemplate.getName() + (this.showExtra ? (" - " + itemTemplate.getTemplateId()) : "") + "\"};label{text=\"\"};");
        }
        buf.append("}");
        if (templates.length == 0) {
            buf.append("label{text=\"You dont know any cookers.\"}");
        }
        return buf.toString();
    }
    
    public String sendCookerRecipes() {
        final StringBuilder buf = new StringBuilder();
        final Recipe[] recipes = RecipesByPlayer.getCookerRecipesFor(this.getResponder().getWurmId(), (int)this.target);
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        switch (absSortBy) {
            case 0: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        if (param1.getRecipeId() < param2.getRecipeId()) {
                            return -1;
                        }
                        return 1;
                    }
                });
                break;
            }
            case 1: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        return param1.getName().compareTo(param2.getName()) * upDown;
                    }
                });
                break;
            }
        }
        buf.append("table{rows=\"1\";cols=\"1\";" + this.colHeader("Recipe Name", 1, this.sortBy));
        buf.append("table{rows=\"1\";cols=\"6\";");
        for (final Recipe recipe : recipes) {
            buf.append("radio{group=\"sel\";id=\"13," + recipe.getRecipeId() + "\";text=\" \"}" + this.colourRecipeName(recipe, recipe.getName(), false));
        }
        final int rem = recipes.length % 3;
        if (rem > 0) {
            for (int i = 0; i < 3 - rem; ++i) {
                buf.append("label{text=\"\"};label{text=\"\"}");
            }
        }
        buf.append("}");
        buf.append("}");
        if (recipes.length == 0) {
            buf.append("label{text=\"You dont know any recipes using that cooker.\"}");
        }
        return buf.toString();
    }
    
    public String sendContainersList() {
        final StringBuilder buf = new StringBuilder();
        final ItemTemplate[] templates = RecipesByPlayer.getKnownContainersFor(this.getResponder().getWurmId());
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        switch (absSortBy) {
            case 0: {
                Arrays.sort(templates, new Comparator<ItemTemplate>() {
                    @Override
                    public int compare(final ItemTemplate param1, final ItemTemplate param2) {
                        if (param1.getTemplateId() < param2.getTemplateId()) {
                            return -1;
                        }
                        return 1;
                    }
                });
                break;
            }
            case 1: {
                Arrays.sort(templates, new Comparator<ItemTemplate>() {
                    @Override
                    public int compare(final ItemTemplate param1, final ItemTemplate param2) {
                        return param1.getName().compareTo(param2.getName()) * upDown;
                    }
                });
                break;
            }
        }
        buf.append("table{rows=\"1\";cols=\"3\";label{text=\" \"};" + this.colHeader("Container Name", 1, this.sortBy) + "label{text=\" \"};");
        for (final ItemTemplate itemTemplate : templates) {
            buf.append("radio{group=\"sel\";id=\"8," + itemTemplate.getTemplateId() + "\";text=\"\"}label{text=\"" + itemTemplate.getName() + (this.showExtra ? (" - " + itemTemplate.getTemplateId()) : "") + "\"};label{text=\"\"};");
        }
        buf.append("}");
        if (templates.length == 0) {
            buf.append("label{text=\"You dont know any containers.\"}");
        }
        return buf.toString();
    }
    
    public String sendContainerRecipes() {
        final StringBuilder buf = new StringBuilder();
        final Recipe[] recipes = RecipesByPlayer.getContainerRecipesFor(this.getResponder().getWurmId(), (int)this.target);
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        switch (absSortBy) {
            case 0: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        if (param1.getRecipeId() < param2.getRecipeId()) {
                            return -1;
                        }
                        return 1;
                    }
                });
                break;
            }
            case 1: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        return param1.getName().compareTo(param2.getName()) * upDown;
                    }
                });
                break;
            }
        }
        buf.append("table{rows=\"1\";cols=\"1\";" + this.colHeader("Recipe Name", 1, this.sortBy));
        buf.append("table{rows=\"1\";cols=\"6\";");
        for (final Recipe recipe : recipes) {
            buf.append("radio{group=\"sel\";id=\"13," + recipe.getRecipeId() + "\";text=\" \"}" + this.colourRecipeName(recipe, recipe.getName(), false));
        }
        final int rem = recipes.length % 3;
        if (rem > 0) {
            for (int i = 0; i < 3 - rem; ++i) {
                buf.append("label{text=\"\"};label{text=\"\"}");
            }
        }
        buf.append("}");
        buf.append("}");
        if (recipes.length == 0) {
            buf.append("label{text=\"You dont know any recipes using that container.\"}");
        }
        return buf.toString();
    }
    
    public String sendToolsList() {
        final StringBuilder buf = new StringBuilder();
        final ItemTemplate[] templates = RecipesByPlayer.getKnownToolsFor(this.getResponder().getWurmId());
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        switch (absSortBy) {
            case 0: {
                Arrays.sort(templates, new Comparator<ItemTemplate>() {
                    @Override
                    public int compare(final ItemTemplate param1, final ItemTemplate param2) {
                        if (param1.getTemplateId() < param2.getTemplateId()) {
                            return -1;
                        }
                        return 1;
                    }
                });
                break;
            }
            case 1: {
                Arrays.sort(templates, new Comparator<ItemTemplate>() {
                    @Override
                    public int compare(final ItemTemplate param1, final ItemTemplate param2) {
                        return param1.getName().compareTo(param2.getName()) * upDown;
                    }
                });
                break;
            }
        }
        buf.append("table{rows=\"1\";cols=\"3\";label{text=\" \"};" + this.colHeader("Tool Name", 1, this.sortBy) + "label{text=\" \"};");
        for (final ItemTemplate itemTemplate : templates) {
            buf.append("radio{group=\"sel\";id=\"10," + itemTemplate.getTemplateId() + "\";text=\"\"}label{text=\"" + itemTemplate.getName() + (this.showExtra ? (" - " + itemTemplate.getTemplateId()) : "") + "\"};label{text=\"\"};");
        }
        buf.append("}");
        if (templates.length == 0) {
            buf.append("label{text=\"You dont know any cooking tools.\"}");
        }
        return buf.toString();
    }
    
    public String sendToolRecipes() {
        final StringBuilder buf = new StringBuilder();
        final Recipe[] recipes = RecipesByPlayer.getToolRecipesFor(this.getResponder().getWurmId(), (int)this.target);
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        switch (absSortBy) {
            case 0: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        if (param1.getRecipeId() < param2.getRecipeId()) {
                            return -1;
                        }
                        return 1;
                    }
                });
                break;
            }
            case 1: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        return param1.getName().compareTo(param2.getName()) * upDown;
                    }
                });
                break;
            }
        }
        buf.append("table{rows=\"1\";cols=\"1\";" + this.colHeader("Recipe Name", 1, this.sortBy));
        buf.append("table{rows=\"1\";cols=\"6\";");
        for (final Recipe recipe : recipes) {
            buf.append("radio{group=\"sel\";id=\"13," + recipe.getRecipeId() + "\";text=\"\"}" + this.colourRecipeName(recipe, recipe.getName(), false));
        }
        final int rem = recipes.length % 3;
        if (rem > 0) {
            for (int i = 0; i < 3 - rem; ++i) {
                buf.append("label{text=\"\"};label{text=\"\"}");
            }
        }
        buf.append("}");
        buf.append("}");
        if (recipes.length == 0) {
            buf.append("label{text=\"You dont know any recipes using that tool.\"}");
        }
        return buf.toString();
    }
    
    public String sendIngredientsList() {
        final StringBuilder buf = new StringBuilder();
        this.ingredients = RecipesByPlayer.getKnownIngredientsFor(this.getResponder().getWurmId());
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        switch (absSortBy) {
            case 0:
            case 1: {
                Arrays.sort(this.ingredients, new Comparator<Ingredient>() {
                    @Override
                    public int compare(final Ingredient param1, final Ingredient param2) {
                        return Recipes.getIngredientName(param1).compareTo(Recipes.getIngredientName(param2)) * upDown;
                    }
                });
                break;
            }
        }
        buf.append("table{rows=\"1\";cols=\"1\";" + this.colHeader("Ingredient Name", 1, this.sortBy));
        buf.append("table{rows=\"1\";cols=\"9\";");
        for (final Ingredient ingredient : this.ingredients) {
            buf.append("radio{group=\"sel\";id=\"12," + ingredient.getTemplateId() + "," + ingredient.getCState() + "," + ingredient.getPState() + "," + ingredient.getMaterial() + "," + ingredient.hasRealTemplate() + "," + ingredient.getRealTemplateId() + "," + ingredient.getCorpseData() + "\";text=\"\"}label{text=\"" + Recipes.getIngredientName(ingredient) + (this.showExtra ? (" - " + ingredient.getTemplateId()) : "") + "\"};label{text=\"\"};");
        }
        final int rem = this.ingredients.length % 3;
        if (rem > 0) {
            for (int i = 0; i < 3 - rem; ++i) {
                buf.append("label{text=\"\"};label{text=\"\"}");
            }
        }
        buf.append("}");
        buf.append("}");
        if (this.ingredients.length == 0) {
            buf.append("label{text=\"You dont know any ingredients.\"}");
        }
        return buf.toString();
    }
    
    public String sendIngredientRecipes() {
        final StringBuilder buf = new StringBuilder();
        final Recipe[] recipes = RecipesByPlayer.getIngredientRecipesFor(this.getResponder().getWurmId(), this.ingred);
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        switch (absSortBy) {
            case 0: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        if (param1.getRecipeId() < param2.getRecipeId()) {
                            return -1;
                        }
                        return 1;
                    }
                });
                break;
            }
            case 1: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        return param1.getName().compareTo(param2.getName()) * upDown;
                    }
                });
                break;
            }
        }
        buf.append("table{rows=\"1\";cols=\"1\";" + this.colHeader("Recipe Name", 1, this.sortBy));
        buf.append("table{rows=\"1\";cols=\"6\";");
        for (final Recipe recipe : recipes) {
            buf.append("radio{group=\"sel\";id=\"13," + recipe.getRecipeId() + "\";text=\" \"}" + this.colourRecipeName(recipe, recipe.getName(), false));
        }
        final int rem = recipes.length % 3;
        if (rem > 0) {
            for (int i = 0; i < 3 - rem; ++i) {
                buf.append("label{text=\"\"};label{text=\"\"}");
            }
        }
        buf.append("}");
        buf.append("}");
        if (recipes.length == 0) {
            buf.append("label{text=\"You dont know any recipes using that ingredient.\"}");
        }
        return buf.toString();
    }
    
    public String sendSearchRecipes() {
        final StringBuilder buf = new StringBuilder();
        final Recipe[] recipes = RecipesByPlayer.getSearchRecipesFor(this.getResponder().getWurmId(), this.searchFor);
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        switch (absSortBy) {
            case 0: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        if (param1.getRecipeId() < param2.getRecipeId()) {
                            return -1;
                        }
                        return 1;
                    }
                });
                break;
            }
            case 1: {
                Arrays.sort(recipes, new Comparator<Recipe>() {
                    @Override
                    public int compare(final Recipe param1, final Recipe param2) {
                        return param1.getName().compareTo(param2.getName()) * upDown;
                    }
                });
                break;
            }
        }
        buf.append("table{rows=\"1\";cols=\"1\";" + this.colHeader("Recipe Name", 1, this.sortBy));
        buf.append("table{rows=\"1\";cols=\"6\";");
        for (final Recipe recipe : recipes) {
            buf.append("radio{group=\"sel\";id=\"13," + recipe.getRecipeId() + "\";text=\" \"}" + this.colourRecipeName(recipe, recipe.getName(), false));
        }
        final int rem = recipes.length % 3;
        if (rem > 0) {
            for (int i = 0; i < 3 - rem; ++i) {
                buf.append("label{text=\"\"};label{text=\"\"}");
            }
        }
        buf.append("}");
        buf.append("}");
        if (recipes.length == 0) {
            buf.append("label{text=\"You dont know any recipes using that ingredient.\"}");
        }
        return buf.toString();
    }
    
    public void sendRecipe() {
        if (RecipesByPlayer.isKnownRecipe(this.getResponder().getWurmId(), this.recip.getRecipeId())) {
            this.ingredients = RecipesByPlayer.getRecipeIngredientsFor(this.getResponder().getWurmId(), this.recip.getRecipeId());
        }
        else {
            final Map<String, Ingredient> knownIngredients = this.recip.getAllIngredients(true);
            this.ingredients = knownIngredients.values().toArray(new Ingredient[knownIngredients.size()]);
        }
        Arrays.sort(this.ingredients, new Comparator<Ingredient>() {
            @Override
            public int compare(final Ingredient param1, final Ingredient param2) {
                return Recipes.getIngredientName(param1).compareTo(Recipes.getIngredientName(param2));
            }
        });
        Arrays.sort(this.ingredients, new Comparator<Ingredient>() {
            @Override
            public int compare(final Ingredient param1, final Ingredient param2) {
                return Byte.valueOf(param1.getGroupId()).compareTo(Byte.valueOf(param2.getGroupId()));
            }
        });
        final String name = this.colourRecipeName(this.recip, this.question, true);
        final StringBuilder buf = new StringBuilder();
        final String closeBtn = "harray{label{text=\" \"};harray{" + (this.showLinks ? "button{text=\"Back\";id=\"back\";hover=\"Go back to last screen.\"};" : "") + "label{text=\" \"};button{text=\"Close\";id=\"close\";hover=\"Close the cookbook.\"};};label{text=\" \"}};";
        buf.append("border{border{size=\"20,25\";null;null;" + name + closeBtn + "null;}null;scroll{vertical=\"true\";horizontal=\"false\";varray{rescale=\"true\";passthrough{id=\"id\";text=\"" + this.getId() + "\"}");
        buf.append("table{rows=\"1\";cols=\"5\";");
        byte type = 1;
        if (this.recip.isContainerActionType()) {
            type = 2;
        }
        else if (this.recip.isHeatType()) {
            type = 3;
        }
        else if (this.recip.isTimeType()) {
            type = 4;
        }
        int lines = 7;
        buf.append("label{text=\"Type:\"}");
        buf.append(this.showLinks ? ("radio{group=\"sel\";id=\"" + type + "," + -10L + "\"}") : "label{text=\"\"}");
        buf.append("label{text=\"" + this.recip.getTriggerName().toLowerCase() + "\"}");
        buf.append("label{text=\"\"}");
        buf.append("label{text=\"\"}");
        if (this.recip.getSkillId() != -1) {
            ++lines;
            buf.append("label{text=\"Skill:\"}");
            buf.append("label{text=\"\"}");
            buf.append("label{text=\"" + SkillSystem.getNameFor(this.recip.getSkillId()).toLowerCase() + "\"}");
            buf.append("label{text=\"" + (this.showExtra ? this.recip.getSkillId() : "") + "\"}");
            buf.append("label{text=\"\"}");
        }
        if (this.recip.getActiveItem() != null) {
            ++lines;
            if (this.recip.getActiveItem().getTemplate().isCookingTool()) {
                buf.append("label{text=\"Tool:\"}");
                buf.append(this.showLinks ? ("radio{group=\"sel\";id=\"10," + this.recip.getActiveItem().getTemplateId() + "\"}") : "label{text=\"\"}");
                buf.append("label{text=\"" + this.recip.getActiveItem().getName(false) + "\"}");
                buf.append("label{text=\"" + (this.showExtra ? this.recip.getActiveItem().getTemplateId() : "") + "\"}");
                buf.append("label{text=\"\"}");
            }
            else {
                final Recipe recipe = Recipes.getRecipeByResult(this.recip.getActiveItem());
                final String link = (recipe == null) ? "label{text=\" \"}" : (RecipesByPlayer.isKnownRecipe(this.getResponder().getWurmId(), recipe.getRecipeId()) ? ("harray{radio{group=\"sel\";id=\"13," + recipe.getRecipeId() + "\";text=\"\"}" + this.colourRecipeName(recipe, "Show recipe", false) + "}") : ("label{color=\"255,127,127\";text=\"Unknown recipe" + (this.showExtra ? (" - " + recipe.getRecipeId()) : "") + "\"}"));
                buf.append("label{text=\"Active Item:\"}");
                buf.append(this.showLinks ? ("radio{group=\"sel\";id=\"12," + this.recip.getActiveItem().getTemplateId() + "," + this.recip.getActiveItem().getCState() + "," + this.recip.getActiveItem().getPState() + "," + this.recip.getActiveItem().getMaterial() + "," + this.recip.getActiveItem().hasRealTemplate() + "," + this.recip.getActiveItem().getRealTemplateId() + "," + this.recip.getActiveItem().getCorpseData() + "\";text=\"\"}") : "label{text=\"\"}");
                buf.append("label{text=\"" + Recipes.getIngredientName(this.recip.getActiveItem()) + "\"}");
                buf.append("label{text=\"" + (this.showExtra ? this.recip.getActiveItem().getTemplateId() : "") + "\"}");
                buf.append(link);
            }
        }
        if (this.recip.getTargetItem() != null) {
            ++lines;
            final Recipe recipe = Recipes.getRecipeByResult(this.recip.getTargetItem());
            final String link = (recipe == null) ? "label{text=\" \"}" : (RecipesByPlayer.isKnownRecipe(this.getResponder().getWurmId(), recipe.getRecipeId()) ? ("harray{radio{group=\"sel\";id=\"13," + recipe.getRecipeId() + "\";text=\"\"}" + this.colourRecipeName(recipe, "Show recipe", false) + "}") : ("label{color=\"255,127,127\";text=\"Unknown recipe" + (this.showExtra ? (" - " + recipe.getRecipeId()) : "") + "\"}"));
            buf.append("label{text=\"Target Item:\"}");
            buf.append(this.showLinks ? ("radio{group=\"sel\";id=\"12," + this.recip.getTargetItem().getTemplateId() + "," + this.recip.getTargetItem().getCState() + "," + this.recip.getTargetItem().getPState() + "," + this.recip.getTargetItem().getMaterial() + "," + this.recip.getTargetItem().hasRealTemplate() + "," + this.recip.getTargetItem().getRealTemplateId() + "," + this.recip.getTargetItem().getCorpseData() + "\";text=\"\"}") : "label{text=\"\"}");
            buf.append("label{text=\"" + Recipes.getIngredientName(this.recip.getTargetItem()) + "\"}");
            buf.append("label{text=\"" + (this.showExtra ? this.recip.getTargetItem().getTemplateId() : "") + "\"}");
            buf.append(link);
        }
        if (this.recip.hasCooker()) {
            String cooker = "Cooker:";
            for (final ItemTemplate template : this.recip.getCookerTemplates()) {
                ++lines;
                buf.append("label{text=\"" + cooker + "\"}");
                buf.append(this.showLinks ? ("radio{group=\"sel\";id=\"6," + template.getTemplateId() + "\"}") : "label{text=\"\"}");
                buf.append("label{text=\"" + template.getName() + "\"}");
                buf.append("label{text=\"" + (this.showExtra ? template.getTemplateId() : "") + "\"}");
                buf.append("label{text=\"\"}");
                cooker = "";
            }
        }
        if (this.recip.hasContainer()) {
            String container = "Container:";
            for (final ItemTemplate template : this.recip.getContainerTemplates()) {
                ++lines;
                buf.append("label{text=\"" + container + "\"}");
                buf.append(this.showLinks ? ("radio{group=\"sel\";id=\"8," + template.getTemplateId() + "\"}") : "label{text=\"\"}");
                buf.append("label{text=\"" + template.getName() + "\"}");
                buf.append("label{text=\"" + (this.showExtra ? template.getTemplateId() : "") + "\"}");
                buf.append("label{text=\"\"}");
                container = "";
            }
        }
        if (this.ingredients.length > 0) {
            byte gid = -5;
            String strIngredient = "";
            for (final Ingredient ingredient : this.ingredients) {
                final Recipe recipe2 = Recipes.getRecipeByResult(ingredient);
                final String link2 = (recipe2 == null) ? "label{text=\" \"}" : (RecipesByPlayer.isKnownRecipe(this.getResponder().getWurmId(), recipe2.getRecipeId()) ? ("harray{radio{group=\"sel\";id=\"13," + recipe2.getRecipeId() + "\";text=\"\"}" + this.colourRecipeName(recipe2, "Show recipe", false) + "}") : ("label{color=\"255,127,127\";text=\"Unknown recipe" + (this.showExtra ? (" - " + recipe2.getRecipeId()) : "") + "\"}"));
                if (ingredient.getGroupId() >= 0) {
                    if (gid == -5) {
                        ++lines;
                        buf.append("label{type=\"bolditalic\";text=\"Ingredients:\"};label{text=\"\"};label{text=\"\"};label{text=\"\"}label{text=\"\"}");
                    }
                    if (gid < ingredient.getGroupId()) {
                        gid = ingredient.getGroupId();
                        strIngredient = this.recip.getGroupById(gid).getGroupTypeName() + ":";
                    }
                    ++lines;
                    buf.append("label{text=\"" + strIngredient + "\"}");
                    buf.append(this.showLinks ? ("radio{group=\"sel\";id=\"12," + ingredient.getTemplateId() + "," + ingredient.getCState() + "," + ingredient.getPState() + "," + ingredient.getMaterial() + "," + ingredient.hasRealTemplate() + "," + ingredient.getRealTemplateId() + "," + ingredient.getCorpseData() + "\";text=\"\"}") : "label{text=\"\"}");
                    final String amount = ((ingredient.isLiquid() && ingredient.getRatio() != 0) ? (" (ratio " + ingredient.getRatio() + "%)") : "") + ((ingredient.getLoss() > 0) ? (" (loss " + ingredient.getLoss() + "%)") : "");
                    buf.append("label{text=\"" + Recipes.getIngredientName(ingredient) + amount + "\"}");
                    buf.append("label{text=\"" + (this.showExtra ? (ingredient.getGroupId() + "," + ingredient.getTemplateId() + "," + ingredient.getCState() + "," + ingredient.getPState() + "," + ingredient.getRatio() + "," + ingredient.getLoss() + "," + ingredient.getMaterial() + "," + ingredient.getRealTemplateId() + "," + ingredient.getCorpseData()) : "") + "\"}");
                    buf.append(link2);
                    strIngredient = "";
                }
            }
        }
        buf.append("}");
        buf.append("radio{group=\"sel\";id=\"-10\";selected=\"true\";hidden=\"true\";text=\"None\"}");
        buf.append("text{text=\"\"}");
        boolean knownRecipe = false;
        String mid = "null;";
        if (this.getResponder().getPower() == 5) {
            mid = "center{harray{button{text=\"Remove from Cookbook\";id=\"remove\";hover=\"Use with care\";confirm=\"Are you sure you want to do that?\";question=\"This will remove this recipe from your cookbook.\"};}};";
        }
        if (Recipes.isKnownRecipe(this.recip.getRecipeId())) {
            buf.append("label{color=\"127,255,127\"text=\"This recipe is known to everyone\"}");
            ++lines;
            mid = "null;";
        }
        if (!this.showLinks) {
            if (this.from.length() > 0) {
                buf.append("harray{label{text=\"Signed:\"};label{type=\"italics\";text=\"" + this.from + "\"}}");
                buf.append("text{text=\"\"}");
                ++lines;
                ++lines;
            }
            if (RecipesByPlayer.isKnownRecipe(this.getResponder().getWurmId(), this.recip.getRecipeId())) {
                buf.append("label{type=\"bold\";color=\"255,127,127\"text=\"This recipe is already in your cookbook.\"}");
                knownRecipe = true;
            }
            else {
                buf.append("label{type=\"bold\";color=\"255,127,127\"text=\"To make this recipe it first must be put in your cookbook.\"}");
            }
            ++lines;
            mid = "null;";
        }
        buf.append("}};null;");
        Item paper = null;
        if (this.target != -10L) {
            try {
                paper = Items.getItem(this.target);
            }
            catch (NoSuchItemException e) {
                CookBookQuestion.logger.log(Level.WARNING, "Target (" + this.target + ") no longer exists!");
            }
        }
        if (this.showLinks) {
            buf.append("border{size=\"20,20\";null;harray{label{text=\" \"};button{text=\"Show selected\";id=\"show\"}};" + mid + "harray{button{text=\"Go to info\";id=\"info\"};label{text=\" \"}};null;}");
        }
        else if (this.target != -10L && !knownRecipe && paper != null && paper.getTopParent() == this.getResponder().getInventory().getWurmId()) {
            buf.append("border{size=\"20,20\";null;null;center{harray{label{text=\" \"};button{text=\"Add to cookbook\";id=\"add\"}}};null;null;}");
        }
        else {
            buf.append("null;");
        }
        buf.append("}");
        final int height = lines * 20;
        this.getResponder().getCommunicator().sendBml(this.showExtra ? 470 : 400, height, true, false, buf.toString(), 200, 200, 200, this.title);
    }
    
    private String colourRecipeName(final Recipe recipe, final String name, final boolean isBold) {
        final StringBuilder buf = new StringBuilder();
        if (recipe.isKnown()) {
            buf.append("label{" + (isBold ? "type=\"bold\";" : "") + "color=\"127,255,127\"" + "text=\"" + name.replace("\"", "''") + (this.showExtra ? (" - " + recipe.getRecipeId()) : "") + "\"};");
        }
        else {
            buf.append(Question.nameColoredByRarity(name + (this.showExtra ? (" - " + recipe.getRecipeId()) : ""), "", recipe.getLootableRarity(), isBold));
        }
        return buf.toString();
    }
    
    public Recipe getRecipe() {
        return this.recip;
    }
    
    public byte getDisplayType() {
        return this.displayType;
    }
    
    static {
        logger = Logger.getLogger(CookBookQuestion.class.getName());
    }
}
