// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.Map;
import java.util.logging.Logger;

public final class ItemTemplateFactory
{
    private static Logger logger;
    private static ItemTemplateFactory instance;
    private static Map<Integer, ItemTemplate> templates;
    private static Set<ItemTemplate> missionTemplates;
    private static Set<ItemTemplate> epicMissionTemplates;
    private static Map<String, ItemTemplate> templatesByName;
    
    public static ItemTemplateFactory getInstance() {
        if (ItemTemplateFactory.instance == null) {
            ItemTemplateFactory.instance = new ItemTemplateFactory();
        }
        return ItemTemplateFactory.instance;
    }
    
    public ItemTemplate getTemplateOrNull(final int templateId) {
        return ItemTemplateFactory.templates.get(templateId);
    }
    
    public String getTemplateName(final int templateId) {
        final ItemTemplate it = this.getTemplateOrNull(templateId);
        if (it != null) {
            return it.getName();
        }
        return "";
    }
    
    public ItemTemplate getTemplate(final int templateId) throws NoSuchTemplateException {
        final ItemTemplate toReturn = ItemTemplateFactory.templates.get(templateId);
        if (toReturn == null) {
            throw new NoSuchTemplateException("No item template with id " + templateId);
        }
        return toReturn;
    }
    
    public ItemTemplate getTemplate(final String name) {
        return ItemTemplateFactory.templatesByName.get(name);
    }
    
    public ItemTemplate[] getTemplates() {
        final ItemTemplate[] toReturn = new ItemTemplate[ItemTemplateFactory.templates.size()];
        return ItemTemplateFactory.templates.values().toArray(toReturn);
    }
    
    public ItemTemplate[] getMissionTemplates() {
        final ItemTemplate[] toReturn = new ItemTemplate[ItemTemplateFactory.missionTemplates.size()];
        return ItemTemplateFactory.missionTemplates.toArray(toReturn);
    }
    
    public ItemTemplate[] getEpicMissionTemplates() {
        final ItemTemplate[] toReturn = new ItemTemplate[ItemTemplateFactory.epicMissionTemplates.size()];
        return ItemTemplateFactory.epicMissionTemplates.toArray(toReturn);
    }
    
    public ItemTemplate[] getMostDamageUpdated() {
        final ItemTemplate[] temps = this.getTemplates();
        Arrays.sort(temps, new Comparator<ItemTemplate>() {
            @Override
            public int compare(final ItemTemplate o1, final ItemTemplate o2) {
                if (o1.damUpdates == o2.damUpdates) {
                    return 0;
                }
                if (o1.damUpdates > o2.damUpdates) {
                    return 1;
                }
                return -1;
            }
        });
        return temps;
    }
    
    public ItemTemplate[] getMostMaintenanceUpdated() {
        final ItemTemplate[] temps = this.getTemplates();
        Arrays.sort(temps, new Comparator<ItemTemplate>() {
            @Override
            public int compare(final ItemTemplate o1, final ItemTemplate o2) {
                if (o1.maintUpdates == o2.maintUpdates) {
                    return 0;
                }
                if (o1.maintUpdates > o2.maintUpdates) {
                    return 1;
                }
                return -1;
            }
        });
        return temps;
    }
    
    public ItemTemplate createItemTemplate(final int templateId, final int size, final String name, final String plural, final String itemDescriptionSuperb, final String itemDescriptionNormal, final String itemDescriptionBad, final String itemDescriptionRotten, final String itemDescriptionLong, final short[] itemTypes, final short imageNumber, final short behaviourType, final int combatDamage, final long decayTime, final int centimetersX, final int centimetersY, final int centimetersZ, final int primarySkill, final byte[] bodySpaces, final String modelName, final float difficulty, final int weight, final byte material, final int value, final boolean isTraded, final int dyeAmountOverrideGrams) throws IOException {
        final ItemTemplate toReturn = new ItemTemplate(templateId, size, name, plural, itemDescriptionSuperb, itemDescriptionNormal, itemDescriptionBad, itemDescriptionRotten, itemDescriptionLong, itemTypes, imageNumber, behaviourType, combatDamage, decayTime, centimetersX, centimetersY, centimetersZ, primarySkill, bodySpaces, modelName, difficulty, weight, material, value, isTraded);
        toReturn.setDyeAmountGrams(dyeAmountOverrideGrams);
        final ItemTemplate old = ItemTemplateFactory.templates.put(templateId, toReturn);
        if (old != null) {
            ItemTemplateFactory.logger.warning("Duplicate definition for template " + templateId + " ('" + name + "' overwrites '" + old.getName() + "').");
        }
        final ItemTemplate it = ItemTemplateFactory.templatesByName.put(name, toReturn);
        if (it != null && toReturn.isFood()) {
            ItemTemplateFactory.logger.warning("Template " + it.getName() + " already being used.");
        }
        if (toReturn.isMissionItem()) {
            ItemTemplateFactory.missionTemplates.add(toReturn);
            if (!toReturn.isNoTake() && !toReturn.isNoDrop() && toReturn.getWeightGrams() < 12000 && !toReturn.isRiftLoot() && (!toReturn.isFood() || !toReturn.isBulk()) && !toReturn.isLiquid() && toReturn.getTemplateId() != 652 && toReturn.getTemplateId() != 737 && toReturn.getTemplateId() != 1097 && toReturn.getTemplateId() != 1306 && toReturn.getTemplateId() != 1414) {
                ItemTemplateFactory.epicMissionTemplates.add(toReturn);
            }
        }
        return toReturn;
    }
    
    public void logAllTemplates() {
        for (final ItemTemplate template : ItemTemplateFactory.templates.values()) {
            ItemTemplateFactory.logger.info(template.toString());
        }
    }
    
    public String getModelNameOrNull(final String templateName) {
        final ItemTemplate i = ItemTemplateFactory.templatesByName.get(templateName);
        if (i == null) {
            return null;
        }
        return i.getModelName();
    }
    
    static {
        ItemTemplateFactory.logger = Logger.getLogger(ItemTemplateFactory.class.getName());
        ItemTemplateFactory.templates = new HashMap<Integer, ItemTemplate>();
        ItemTemplateFactory.missionTemplates = new HashSet<ItemTemplate>();
        ItemTemplateFactory.epicMissionTemplates = new HashSet<ItemTemplate>();
        ItemTemplateFactory.templatesByName = new HashMap<String, ItemTemplate>();
    }
}
