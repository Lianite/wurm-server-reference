// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.Arrays;
import java.util.Comparator;
import com.wurmonline.server.items.Recipes;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.Recipe;
import com.wurmonline.shared.constants.ItemMaterials;

public final class WriteRecipeQuestion extends Question implements ItemMaterials
{
    private Recipe[] recipes;
    private Item paper;
    
    public WriteRecipeQuestion(final Creature aResponder, final Item apaper) {
        super(aResponder, "Select Recipe", "Select Recipe", 138, -10L);
        this.paper = apaper;
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        final String sel = answers.getProperty("recipe");
        final int selId = Integer.parseInt(sel);
        final Recipe recipe = this.recipes[selId];
        this.paper.setInscription(recipe, this.getResponder().getName(), 0);
        this.getResponder().getCommunicator().sendNormalServerMessage("You carefully finish writing the recipe \"" + recipe.getName() + "\" and sign it.");
    }
    
    @Override
    public void sendQuestion() {
        Arrays.sort(this.recipes = Recipes.getUnknownRecipes(), new Comparator<Recipe>() {
            @Override
            public int compare(final Recipe param1, final Recipe param2) {
                return param1.getName().compareTo(param2.getName());
            }
        });
        final StringBuilder buf = new StringBuilder(this.getBmlHeader());
        buf.append("harray{label{text=\"Recipe\"};");
        buf.append("dropdown{id=\"recipe\";default=\"0\";options=\"");
        for (int i = 0; i < this.recipes.length; ++i) {
            if (i > 0) {
                buf.append(",");
            }
            final Recipe recipe = this.recipes[i];
            buf.append(recipe.getName().replace(",", "") + " - " + recipe.getRecipeId());
        }
        buf.append("\"}}");
        buf.append("label{text=\"\"}");
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(300, 120, true, true, buf.toString(), 200, 200, 200, this.title);
    }
}
