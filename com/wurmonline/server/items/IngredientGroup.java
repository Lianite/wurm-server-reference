// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class IngredientGroup
{
    private static final Logger logger;
    public static final byte INGREDIENT_GROUP_RESULT = -3;
    public static final byte INGREDIENT_GROUP_ACTIVE = -2;
    public static final byte INGREDIENT_GROUP_TARGET = -1;
    public static final byte INGREDIENT_GROUP_NONE = 0;
    public static final byte INGREDIENT_GROUP_MANDATORY = 1;
    public static final byte INGREDIENT_GROUP_ZERO_OR_ONE = 2;
    public static final byte INGREDIENT_GROUP_ONE_OF = 3;
    public static final byte INGREDIENT_GROUP_ONE_OR_MORE = 4;
    public static final byte INGREDIENT_GROUP_OPTIONAL = 5;
    public static final byte INGREDIENT_GROUP_ANY = 6;
    private final Map<String, Ingredient> ingredientsByName;
    private final Map<Integer, Ingredient> ingredients;
    private final byte groupType;
    private int groupDifficulty;
    
    public IngredientGroup(final byte groupType) {
        this.ingredientsByName = new HashMap<String, Ingredient>();
        this.ingredients = new HashMap<Integer, Ingredient>();
        this.groupDifficulty = 0;
        this.groupType = groupType;
    }
    
    public IngredientGroup(final DataInputStream dis) throws IOException, NoSuchTemplateException {
        this.ingredientsByName = new HashMap<String, Ingredient>();
        this.ingredients = new HashMap<Integer, Ingredient>();
        this.groupDifficulty = 0;
        this.groupType = dis.readByte();
        final byte icount = dis.readByte();
        for (int i = 0; i < icount; ++i) {
            this.add(new Ingredient(dis));
        }
    }
    
    public void pack(final DataOutputStream dos) throws IOException {
        dos.writeByte(this.groupType);
        dos.writeByte(this.ingredients.size());
        for (final Ingredient ii : this.ingredients.values()) {
            ii.pack(dos);
        }
    }
    
    public void add(final Ingredient ingredient) {
        if (this.groupType == 1) {
            this.ingredientsByName.put(ingredient.getTemplateName(), ingredient);
        }
        this.ingredients.put((int)ingredient.getIngredientId(), ingredient);
    }
    
    public byte getGroupType() {
        return this.groupType;
    }
    
    public String getGroupTypeName() {
        switch (this.groupType) {
            case 1: {
                return "Mandatory";
            }
            case 2: {
                return "Zero or one";
            }
            case 3: {
                return "One of";
            }
            case 4: {
                return "One or more";
            }
            case 5: {
                return "Optional";
            }
            case 6: {
                return "Any";
            }
            default: {
                return "unknown";
            }
        }
    }
    
    public Ingredient[] getIngredients() {
        return this.ingredients.values().toArray(new Ingredient[this.ingredients.size()]);
    }
    
    public int size() {
        return this.ingredients.size();
    }
    
    public boolean contains(final String ingredientName) {
        return this.ingredientsByName.containsKey(ingredientName);
    }
    
    @Nullable
    public Ingredient getIngredientByName(final String ingredientName) {
        return this.ingredientsByName.get(ingredientName);
    }
    
    void clearFound() {
        for (final Ingredient i : this.ingredients.values()) {
            i.setFound(false);
        }
        this.groupDifficulty = 0;
    }
    
    boolean matches(final Item item) {
        for (final Ingredient i : this.ingredients.values()) {
            if (i.matches(item)) {
                this.groupDifficulty += i.setFound(true);
                return true;
            }
        }
        return false;
    }
    
    boolean wasFound() {
        final int count = 0;
        switch (this.groupType) {
            case 1: {
                for (final Ingredient i : this.ingredients.values()) {
                    if (!i.wasFound(false, false)) {
                        return false;
                    }
                }
                return true;
            }
            case 2: {
                return this.getFound(false) <= 1;
            }
            case 3: {
                return this.getFound(false) == 1;
            }
            case 4: {
                return this.getFound(true) >= 1;
            }
            case 6: {
                return true;
            }
            case 5: {
                for (final Ingredient i : this.ingredients.values()) {
                    if (!i.wasFound(false, true)) {
                        return false;
                    }
                }
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    int getFound(final boolean any) {
        int count = 0;
        for (final Ingredient i : this.ingredients.values()) {
            if (i.wasFound(any, false)) {
                ++count;
            }
        }
        return count;
    }
    
    int getGroupDifficulty() {
        return this.groupDifficulty;
    }
    
    @Override
    protected IngredientGroup clone() {
        final IngredientGroup ig = new IngredientGroup(this.groupType);
        return ig;
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("{group:" + this.getGroupTypeName() + "(" + this.getGroupType() + ")");
        boolean first = true;
        for (final Ingredient i : this.ingredients.values()) {
            if (first) {
                first = false;
            }
            else {
                buf.append(",");
            }
            buf.append(i.toString());
        }
        buf.append("}");
        return buf.toString();
    }
    
    static {
        logger = Logger.getLogger(IngredientGroup.class.getName());
    }
}
