// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import java.util.HashMap;
import java.io.IOException;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.shared.exceptions.WurmServerException;
import java.util.Iterator;
import java.util.Map;

public final class CreatureTemplateFactory implements CreatureTemplateIds
{
    private static CreatureTemplateFactory instance;
    private static Map<Integer, CreatureTemplate> templates;
    private static Map<String, CreatureTemplate> templatesByName;
    
    public static CreatureTemplateFactory getInstance() {
        if (CreatureTemplateFactory.instance == null) {
            CreatureTemplateFactory.instance = new CreatureTemplateFactory();
        }
        return CreatureTemplateFactory.instance;
    }
    
    public static final boolean isNameOkay(final String aName) {
        final String lName = aName.toLowerCase();
        if (lName.startsWith("wurm")) {
            return false;
        }
        for (final CreatureTemplate template : CreatureTemplateFactory.templates.values()) {
            if (template.getName().toLowerCase().equals(lName)) {
                return false;
            }
        }
        return true;
    }
    
    public final CreatureTemplate getTemplate(final int id) throws NoSuchCreatureTemplateException {
        final CreatureTemplate toReturn = CreatureTemplateFactory.templates.get(id);
        if (toReturn == null) {
            throw new NoSuchCreatureTemplateException("No Creature template with id " + id);
        }
        return toReturn;
    }
    
    public CreatureTemplate getTemplate(final String name) throws Exception {
        final CreatureTemplate toReturn = CreatureTemplateFactory.templatesByName.get(name.toLowerCase());
        if (toReturn == null) {
            throw new WurmServerException("No Creature template with name " + name);
        }
        return toReturn;
    }
    
    public CreatureTemplate[] getTemplates() {
        final CreatureTemplate[] toReturn = new CreatureTemplate[CreatureTemplateFactory.templates.size()];
        return CreatureTemplateFactory.templates.values().toArray(toReturn);
    }
    
    public CreatureTemplate createCreatureTemplate(final int id, final String name, final String plural, final String longDesc, final String modelName, final int[] types, final byte bodyType, final Skills skills, final short vision, final byte sex, final short centimetersHigh, final short centimetersLong, final short centimetersWide, final String deathSndMale, final String deathSndFemale, final String hitSndMale, final String hitSndFemale, final float naturalArmour, final float handDam, final float kickDam, final float biteDam, final float headDam, final float breathDam, final float speed, final int moveRate, final int[] itemsButchered, final int maxHuntDist, final int aggress, final byte meatMaterial) throws IOException {
        final CreatureTemplate toReturn = new DbCreatureTemplate(id, name, plural, longDesc, modelName, types, bodyType, skills, vision, sex, centimetersHigh, centimetersLong, centimetersWide, deathSndMale, deathSndFemale, hitSndMale, hitSndFemale, naturalArmour, handDam, kickDam, biteDam, headDam, breathDam, speed, moveRate, itemsButchered, maxHuntDist, aggress, meatMaterial);
        CreatureTemplateFactory.templates.put(id, toReturn);
        CreatureTemplateFactory.templatesByName.put(name.toLowerCase(), toReturn);
        return toReturn;
    }
    
    public String getModelNameOrNull(final String templateName) {
        final CreatureTemplate t = CreatureTemplateFactory.templatesByName.get(templateName);
        if (t == null) {
            return null;
        }
        return t.getModelName();
    }
    
    static {
        CreatureTemplateFactory.templates = new HashMap<Integer, CreatureTemplate>();
        CreatureTemplateFactory.templatesByName = new HashMap<String, CreatureTemplate>();
    }
}
