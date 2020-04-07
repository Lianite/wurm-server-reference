// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.combat;

import com.wurmonline.server.creatures.NoArmourException;
import com.wurmonline.server.items.NoSpaceException;
import com.wurmonline.server.creatures.Creature;
import java.util.Iterator;
import com.wurmonline.server.Features;
import com.wurmonline.server.Servers;
import javax.annotation.Nullable;
import com.wurmonline.server.Server;
import javax.annotation.Nonnull;
import com.wurmonline.server.items.Item;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public class ArmourTemplate implements MiscConstants
{
    private static final Logger logger;
    public static HashMap<Integer, ArmourTemplate> armourTemplates;
    public static ArrayList<ProtectionSlot> protectionSlots;
    protected int templateId;
    protected ArmourType armourType;
    protected float moveModifier;
    public static ArmourType ARMOUR_TYPE_NONE;
    public static ArmourType ARMOUR_TYPE_LEATHER;
    public static ArmourType ARMOUR_TYPE_STUDDED;
    public static ArmourType ARMOUR_TYPE_CHAIN;
    public static ArmourType ARMOUR_TYPE_PLATE;
    public static ArmourType ARMOUR_TYPE_RING;
    public static ArmourType ARMOUR_TYPE_CLOTH;
    public static ArmourType ARMOUR_TYPE_SCALE;
    public static ArmourType ARMOUR_TYPE_SPLINT;
    public static ArmourType ARMOUR_TYPE_LEATHER_DRAGON;
    public static ArmourType ARMOUR_TYPE_SCALE_DRAGON;
    
    public static ArmourTemplate getArmourTemplate(final int templateId) {
        if (ArmourTemplate.armourTemplates.containsKey(templateId)) {
            return ArmourTemplate.armourTemplates.get(templateId);
        }
        ArmourTemplate.logger.warning(String.format("Item template id %s has no ArmourTemplate, but one was requested.", templateId));
        return null;
    }
    
    public static ArmourTemplate getArmourTemplate(final Item item) {
        return getArmourTemplate(item.getTemplateId());
    }
    
    public static void setArmourMoveModifier(final int templateId, final float value) {
        final ArmourTemplate template = getArmourTemplate(templateId);
        if (template != null) {
            template.setMoveModifier(value);
        }
        else {
            ArmourTemplate.logger.warning(String.format("Item template id %s has no ArmourTemplate, but one was requested.", templateId));
        }
    }
    
    public static void initializeProtectionSlots() {
        new ProtectionSlot((byte)1, new byte[] { 17 });
        new ProtectionSlot((byte)29, new byte[] { 18, 19, 20 });
        new ProtectionSlot((byte)2, new byte[] { 21, 27, 26, 32, 23, 24, 25, 22 });
        new ProtectionSlot((byte)3, new byte[] { 5, 9 });
        new ProtectionSlot((byte)4, new byte[] { 6, 10 });
        new ProtectionSlot((byte)34, new byte[] { 7, 11, 8, 12 });
        new ProtectionSlot((byte)15, new byte[0]);
        new ProtectionSlot((byte)16, new byte[0]);
    }
    
    public ArmourTemplate(final int templateId, final ArmourType armourType, final float moveModifier) {
        this.templateId = templateId;
        this.armourType = armourType;
        this.moveModifier = moveModifier;
        ArmourTemplate.armourTemplates.put(templateId, this);
    }
    
    public float getBaseDR() {
        return this.armourType.getBaseDR();
    }
    
    public float getEffectiveness(final byte woundType) {
        return this.armourType.getEffectiveness(woundType);
    }
    
    public float getMoveModifier() {
        return this.moveModifier;
    }
    
    public ArmourType getArmourType() {
        return this.armourType;
    }
    
    public float getLimitFactor() {
        return this.armourType.getLimitFactor();
    }
    
    public void setMoveModifier(final float newMoveModifier) {
        this.moveModifier = newMoveModifier;
    }
    
    public static float calculateDR(@Nonnull final Item armour, final byte woundType) {
        final ArmourTemplate armourTemplate = getArmourTemplate(armour);
        if (armourTemplate != null) {
            float toReturn = armourTemplate.getBaseDR();
            toReturn += getArmourMatBonus(armour.getMaterial());
            toReturn *= armourTemplate.getEffectiveness(woundType);
            toReturn *= 1.0f + getRarityArmourBonus(armour.getRarity());
            toReturn = 0.05f + (float)(toReturn * Server.getBuffedQualityEffect(armour.getCurrentQualityLevel() / 100.0f));
            return 1.0f - toReturn;
        }
        return 1.0f;
    }
    
    public static float calculateGlanceRate(@Nullable ArmourType armourType, @Nullable final Item armour, final byte woundType, final float armourRating) {
        float toReturn = 0.0f;
        float baseArmour = 0.0f;
        if (armour != null) {
            armourType = armour.getArmourType();
        }
        else if (armourType != null) {
            if (woundType == 5) {
                return 0.0f;
            }
            return (1.0f - armourRating) / 2.0f;
        }
        if (armourType != null) {
            baseArmour = 0.05f;
            toReturn = armourType.getGlanceRate(woundType, armour.getMaterial());
        }
        if (armour != null) {
            toReturn += getRarityArmourBonus(armour.getRarity());
            toReturn = baseArmour + toReturn * (float)Server.getBuffedQualityEffect(armour.getCurrentQualityLevel() / 100.0f);
        }
        else {
            toReturn *= 0.5f;
        }
        return toReturn;
    }
    
    public static float calculateCreatureGlanceRate(final byte woundType, @Nonnull final Item armour) {
        final ArmourType armourType = armour.getArmourType();
        if (armourType != null) {
            return armourType.getCreatureGlance(woundType, armour);
        }
        return 0.0f;
    }
    
    public static float calculateArrowGlance(@Nonnull final Item armour, final Item arrow) {
        final ArmourType armourType = armour.getArmourType();
        if (armourType != null) {
            float toReturn = armourType.getArrowGlance();
            if (arrow.getTemplateId() == 454) {
                toReturn += 0.1f;
            }
            else if (arrow.getTemplateId() == 456) {
                toReturn -= 0.05f;
            }
            toReturn *= 1.0f + getRarityArmourBonus(armour.getRarity());
            toReturn += (float)(toReturn * Server.getBuffedQualityEffect(armour.getCurrentQualityLevel() / 100.0f));
            return toReturn;
        }
        return 0.0f;
    }
    
    public static float getRarityArmourBonus(final byte rarity) {
        return rarity * 0.03f;
    }
    
    public static float getArmourMatBonus(final byte armourMaterial) {
        if (Servers.localServer.isChallengeOrEpicServer() || Features.Feature.NEW_ARMOUR_VALUES.isEnabled()) {
            if (armourMaterial == 9) {
                return 0.025f;
            }
            if (armourMaterial == 57 || armourMaterial == 56 || armourMaterial == 67) {
                return 0.05f;
            }
        }
        else {
            if (armourMaterial == 9) {
                return 0.02f;
            }
            if (armourMaterial == 57 || armourMaterial == 67) {
                return 0.1f;
            }
            if (armourMaterial == 56) {
                return 0.05f;
            }
        }
        if (Features.Feature.METALLIC_ITEMS.isEnabled()) {
            switch (armourMaterial) {
                case 56: {
                    return 0.05f;
                }
                case 30: {
                    return 0.01f;
                }
                case 31: {
                    return 0.01f;
                }
                case 10: {
                    return -0.01f;
                }
                case 57: {
                    return 0.05f;
                }
                case 7: {
                    return -0.01f;
                }
                case 12: {
                    return -0.025f;
                }
                case 67: {
                    return 0.05f;
                }
                case 8: {
                    return -0.0075f;
                }
                case 9: {
                    return 0.025f;
                }
                case 34: {
                    return -0.0175f;
                }
                case 13: {
                    return -0.02f;
                }
                case 96: {
                    return 0.005f;
                }
            }
        }
        return 0.0f;
    }
    
    public static float getArmourMatGlanceBonus(final byte armourMaterial) {
        float materialMod = 1.0f;
        if (Features.Feature.METALLIC_ITEMS.isEnabled()) {
            switch (armourMaterial) {
                case 10: {
                    materialMod = 0.98f;
                    break;
                }
                case 7: {
                    materialMod = 1.025f;
                    break;
                }
                case 8: {
                    materialMod = 1.01f;
                    break;
                }
                case 96: {
                    materialMod = 1.02f;
                    break;
                }
            }
        }
        return materialMod;
    }
    
    public static float getArmourDamageModFor(final Item armour, final byte woundType) {
        float toReturn = 1.0f;
        final ArmourType armourType = armour.getArmourType();
        if (woundType == 0) {
            if (armourType == ArmourTemplate.ARMOUR_TYPE_CLOTH || armourType == ArmourTemplate.ARMOUR_TYPE_PLATE || armourType == ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON) {
                toReturn = 4.0f;
            }
        }
        else if (woundType == 2) {
            if (armourType == ArmourTemplate.ARMOUR_TYPE_PLATE) {
                toReturn = 4.0f;
            }
            else if (armourType == ArmourTemplate.ARMOUR_TYPE_CLOTH || armourType == ArmourTemplate.ARMOUR_TYPE_CHAIN) {
                toReturn = 2.0f;
            }
        }
        else if (woundType == 1) {
            if (armourType == ArmourTemplate.ARMOUR_TYPE_CLOTH || armourType == ArmourTemplate.ARMOUR_TYPE_LEATHER || armourType == ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON || armourType == ArmourTemplate.ARMOUR_TYPE_PLATE) {
                toReturn = 4.0f;
            }
            else if (armourType == ArmourTemplate.ARMOUR_TYPE_STUDDED || armourType == ArmourTemplate.ARMOUR_TYPE_CHAIN) {
                toReturn = 2.0f;
            }
        }
        else if (woundType == 3) {
            toReturn = 4.0f;
        }
        else if (woundType == 4) {
            if (armourType == ArmourTemplate.ARMOUR_TYPE_CLOTH) {
                toReturn = 4.0f;
            }
            else if (armourType == ArmourTemplate.ARMOUR_TYPE_LEATHER || armourType == ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON) {
                toReturn = 0.5f;
            }
        }
        return toReturn;
    }
    
    public static float getMaterialMovementModifier(final byte armourMaterial) {
        if (Features.Feature.METALLIC_ITEMS.isEnabled()) {
            switch (armourMaterial) {
                case 56: {
                    return 0.95f;
                }
                case 30: {
                    return 1.0f;
                }
                case 31: {
                    return 1.0f;
                }
                case 10: {
                    return 0.99f;
                }
                case 57: {
                    return 0.9f;
                }
                case 7: {
                    return 1.05f;
                }
                case 11: {
                    return 1.0f;
                }
                case 12: {
                    return 1.025f;
                }
                case 67: {
                    return 0.9f;
                }
                case 8: {
                    return 1.02f;
                }
                case 9: {
                    return 1.0f;
                }
                case 34: {
                    return 0.98f;
                }
                case 13: {
                    return 0.975f;
                }
                case 96: {
                    return 1.01f;
                }
            }
        }
        return 1.0f;
    }
    
    public static byte getArmourPosition(final byte bodyPosition) {
        for (final ProtectionSlot slot : ArmourTemplate.protectionSlots) {
            if (slot.bodySlots.contains(bodyPosition)) {
                return slot.armourPosition;
            }
        }
        return bodyPosition;
    }
    
    public static float getArmourModForLocation(final Creature creature, final byte location, final byte woundType) throws NoArmourException {
        final byte bodyPosition = getArmourPosition(location);
        try {
            final Item armour = creature.getArmour(bodyPosition);
            return calculateDR(armour, woundType);
        }
        catch (NoSpaceException e) {
            ArmourTemplate.logger.warning(creature.getName() + " no armour space on loc " + location);
        }
        catch (NoArmourException e2) {
            throw new NoArmourException(String.format("Armour not found for %s at location %s.", creature.getName(), location));
        }
        return 1.0f;
    }
    
    public static void initializeArmourTemplates() {
        new ArmourTemplate(116, ArmourTemplate.ARMOUR_TYPE_STUDDED, 0.01f);
        new ArmourTemplate(117, ArmourTemplate.ARMOUR_TYPE_STUDDED, 0.005f);
        new ArmourTemplate(119, ArmourTemplate.ARMOUR_TYPE_STUDDED, 0.005f);
        new ArmourTemplate(118, ArmourTemplate.ARMOUR_TYPE_STUDDED, 0.05f);
        new ArmourTemplate(120, ArmourTemplate.ARMOUR_TYPE_STUDDED, 0.05f);
        new ArmourTemplate(115, ArmourTemplate.ARMOUR_TYPE_STUDDED, 0.005f);
        new ArmourTemplate(959, ArmourTemplate.ARMOUR_TYPE_STUDDED, 0.005f);
        new ArmourTemplate(1014, ArmourTemplate.ARMOUR_TYPE_STUDDED, 0.005f);
        new ArmourTemplate(1015, ArmourTemplate.ARMOUR_TYPE_STUDDED, 0.005f);
        new ArmourTemplate(105, ArmourTemplate.ARMOUR_TYPE_LEATHER, 0.007f);
        new ArmourTemplate(107, ArmourTemplate.ARMOUR_TYPE_LEATHER, 0.003f);
        new ArmourTemplate(103, ArmourTemplate.ARMOUR_TYPE_LEATHER, 0.003f);
        new ArmourTemplate(108, ArmourTemplate.ARMOUR_TYPE_LEATHER, 0.04f);
        new ArmourTemplate(104, ArmourTemplate.ARMOUR_TYPE_LEATHER, 0.04f);
        new ArmourTemplate(106, ArmourTemplate.ARMOUR_TYPE_LEATHER, 0.003f);
        new ArmourTemplate(702, ArmourTemplate.ARMOUR_TYPE_LEATHER, 0.13f);
        new ArmourTemplate(274, ArmourTemplate.ARMOUR_TYPE_CHAIN, 0.015f);
        new ArmourTemplate(279, ArmourTemplate.ARMOUR_TYPE_CHAIN, 0.01f);
        new ArmourTemplate(275, ArmourTemplate.ARMOUR_TYPE_CHAIN, 0.07f);
        new ArmourTemplate(276, ArmourTemplate.ARMOUR_TYPE_CHAIN, 0.07f);
        new ArmourTemplate(277, ArmourTemplate.ARMOUR_TYPE_CHAIN, 0.008f);
        new ArmourTemplate(278, ArmourTemplate.ARMOUR_TYPE_CHAIN, 0.01f);
        new ArmourTemplate(703, ArmourTemplate.ARMOUR_TYPE_CHAIN, 0.17f);
        new ArmourTemplate(474, ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON, 0.001f);
        new ArmourTemplate(475, ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON, 0.02f);
        new ArmourTemplate(476, ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON, 0.02f);
        new ArmourTemplate(477, ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON, 0.005f);
        new ArmourTemplate(478, ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON, 0.001f);
        new ArmourTemplate(280, ArmourTemplate.ARMOUR_TYPE_PLATE, 0.02f);
        new ArmourTemplate(284, ArmourTemplate.ARMOUR_TYPE_PLATE, 0.02f);
        new ArmourTemplate(281, ArmourTemplate.ARMOUR_TYPE_PLATE, 0.09f);
        new ArmourTemplate(282, ArmourTemplate.ARMOUR_TYPE_PLATE, 0.09f);
        new ArmourTemplate(283, ArmourTemplate.ARMOUR_TYPE_PLATE, 0.01f);
        new ArmourTemplate(273, ArmourTemplate.ARMOUR_TYPE_PLATE, 0.02f);
        new ArmourTemplate(285, ArmourTemplate.ARMOUR_TYPE_PLATE, 0.025f);
        new ArmourTemplate(286, ArmourTemplate.ARMOUR_TYPE_PLATE, 0.025f);
        new ArmourTemplate(287, ArmourTemplate.ARMOUR_TYPE_PLATE, 0.025f);
        if (Servers.localServer.isChallengeOrEpicServer() || Features.Feature.NEW_ARMOUR_VALUES.isEnabled()) {
            setArmourMoveModifier(280, 0.0175f);
            setArmourMoveModifier(284, 0.015f);
            setArmourMoveModifier(281, 0.08f);
            setArmourMoveModifier(282, 0.08f);
            setArmourMoveModifier(273, 0.015f);
        }
        new ArmourTemplate(109, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(113, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(1107, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(1070, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(1071, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(1072, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(1073, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(112, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(110, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(1067, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(1068, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(1069, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(114, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(111, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(1075, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(1105, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(1074, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(1106, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(779, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(1425, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(1427, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(1426, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(704, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.1f);
        new ArmourTemplate(791, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(943, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(944, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(947, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(945, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(946, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(948, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(949, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(950, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(951, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(953, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(952, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(954, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(957, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(956, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(955, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(958, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(961, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(964, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(963, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(962, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(965, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(966, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(960, ArmourTemplate.ARMOUR_TYPE_CLOTH, 0.0f);
        new ArmourTemplate(979, ArmourTemplate.ARMOUR_TYPE_PLATE, 0.025f);
        new ArmourTemplate(980, ArmourTemplate.ARMOUR_TYPE_PLATE, 0.025f);
        new ArmourTemplate(998, ArmourTemplate.ARMOUR_TYPE_PLATE, 0.025f);
        new ArmourTemplate(472, ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON, 0.001f);
        new ArmourTemplate(471, ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON, 0.04f);
        new ArmourTemplate(473, ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON, 0.04f);
        new ArmourTemplate(470, ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON, 0.004f);
        new ArmourTemplate(469, ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON, 0.004f);
        new ArmourTemplate(468, ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON, 0.001f);
        new ArmourTemplate(537, ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON, 0.04f);
        new ArmourTemplate(531, ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON, 0.04f);
        new ArmourTemplate(534, ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON, 0.04f);
        new ArmourTemplate(536, ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON, 0.001f);
        new ArmourTemplate(530, ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON, 0.001f);
        new ArmourTemplate(533, ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON, 0.001f);
        new ArmourTemplate(515, ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON, 0.001f);
        new ArmourTemplate(330, ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON, 0.009f);
        new ArmourTemplate(600, ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON, 0.001f);
    }
    
    public static void initializeCreatureArmour() {
        ArmourTemplate.ARMOUR_TYPE_NONE.setCreatureGlance(0.0f, (byte)0, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER.setCreatureGlance(0.25f, (byte)0, 0.05f);
        ArmourTemplate.ARMOUR_TYPE_STUDDED.setCreatureGlance(0.25f, (byte)1, 0.05f);
        ArmourTemplate.ARMOUR_TYPE_CHAIN.setCreatureGlance(0.3f, (byte)2, 0.1f);
        ArmourTemplate.ARMOUR_TYPE_PLATE.setCreatureGlance(0.3f, (byte)1, 0.05f);
        ArmourTemplate.ARMOUR_TYPE_RING.setCreatureGlance(0.3f, (byte)2, 0.05f);
        ArmourTemplate.ARMOUR_TYPE_CLOTH.setCreatureGlance(0.25f, (byte)0, 0.05f);
        ArmourTemplate.ARMOUR_TYPE_SCALE.setCreatureGlance(0.3f, (byte)1, 0.1f);
        ArmourTemplate.ARMOUR_TYPE_SPLINT.setCreatureGlance(0.3f, (byte)0, 0.1f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setCreatureGlance(0.35f, (byte)0, 0.05f);
        ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setCreatureGlance(0.35f, (byte)1, 0.05f);
    }
    
    public static void initializeArmourEffectiveness() {
        ArmourTemplate.ARMOUR_TYPE_LEATHER.setEffectiveness((byte)3, 0.95f);
        ArmourTemplate.ARMOUR_TYPE_STUDDED.setEffectiveness((byte)3, 1.05f);
        ArmourTemplate.ARMOUR_TYPE_CHAIN.setEffectiveness((byte)3, 1.05f);
        ArmourTemplate.ARMOUR_TYPE_PLATE.setEffectiveness((byte)3, 1.07f);
        ArmourTemplate.ARMOUR_TYPE_RING.setEffectiveness((byte)3, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_CLOTH.setEffectiveness((byte)3, 0.9f);
        ArmourTemplate.ARMOUR_TYPE_SCALE.setEffectiveness((byte)3, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_SPLINT.setEffectiveness((byte)3, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setEffectiveness((byte)3, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setEffectiveness((byte)3, 1.0f);
        if (Servers.isThisAnEpicOrChallengeServer() || Features.Feature.NEW_ARMOUR_VALUES.isEnabled()) {
            ArmourTemplate.ARMOUR_TYPE_LEATHER.setEffectiveness((byte)3, 0.9f);
            ArmourTemplate.ARMOUR_TYPE_CHAIN.setEffectiveness((byte)3, 1.075f);
            ArmourTemplate.ARMOUR_TYPE_PLATE.setEffectiveness((byte)3, 1.05f);
            ArmourTemplate.ARMOUR_TYPE_CLOTH.setEffectiveness((byte)3, 0.8f);
        }
        ArmourTemplate.ARMOUR_TYPE_LEATHER.setEffectiveness((byte)0, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_STUDDED.setEffectiveness((byte)0, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_CHAIN.setEffectiveness((byte)0, 1.1f);
        ArmourTemplate.ARMOUR_TYPE_PLATE.setEffectiveness((byte)0, 0.9f);
        ArmourTemplate.ARMOUR_TYPE_RING.setEffectiveness((byte)0, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_CLOTH.setEffectiveness((byte)0, 1.15f);
        ArmourTemplate.ARMOUR_TYPE_SCALE.setEffectiveness((byte)0, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_SPLINT.setEffectiveness((byte)0, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setEffectiveness((byte)0, 1.1f);
        ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setEffectiveness((byte)0, 0.95f);
        if (Servers.isThisAnEpicOrChallengeServer() || Features.Feature.NEW_ARMOUR_VALUES.isEnabled()) {
            ArmourTemplate.ARMOUR_TYPE_CHAIN.setEffectiveness((byte)0, 1.075f);
            ArmourTemplate.ARMOUR_TYPE_PLATE.setEffectiveness((byte)0, 0.95f);
            ArmourTemplate.ARMOUR_TYPE_CLOTH.setEffectiveness((byte)0, 1.2f);
            ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setEffectiveness((byte)0, 1.05f);
        }
        ArmourTemplate.ARMOUR_TYPE_LEATHER.setEffectiveness((byte)2, 0.9f);
        ArmourTemplate.ARMOUR_TYPE_STUDDED.setEffectiveness((byte)2, 1.1f);
        ArmourTemplate.ARMOUR_TYPE_CHAIN.setEffectiveness((byte)2, 0.9f);
        ArmourTemplate.ARMOUR_TYPE_PLATE.setEffectiveness((byte)2, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_RING.setEffectiveness((byte)2, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_CLOTH.setEffectiveness((byte)2, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_SCALE.setEffectiveness((byte)2, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_SPLINT.setEffectiveness((byte)2, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setEffectiveness((byte)2, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setEffectiveness((byte)2, 1.1f);
        if (Servers.isThisAnEpicOrChallengeServer() || Features.Feature.NEW_ARMOUR_VALUES.isEnabled()) {
            ArmourTemplate.ARMOUR_TYPE_CHAIN.setEffectiveness((byte)2, 0.925f);
            ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setEffectiveness((byte)2, 1.05f);
        }
        ArmourTemplate.ARMOUR_TYPE_LEATHER.setEffectiveness((byte)1, 1.1f);
        ArmourTemplate.ARMOUR_TYPE_STUDDED.setEffectiveness((byte)1, 0.9f);
        ArmourTemplate.ARMOUR_TYPE_CHAIN.setEffectiveness((byte)1, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_PLATE.setEffectiveness((byte)1, 1.05f);
        ArmourTemplate.ARMOUR_TYPE_RING.setEffectiveness((byte)1, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_CLOTH.setEffectiveness((byte)1, 0.8f);
        ArmourTemplate.ARMOUR_TYPE_SCALE.setEffectiveness((byte)1, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_SPLINT.setEffectiveness((byte)1, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setEffectiveness((byte)1, 0.9f);
        ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setEffectiveness((byte)1, 1.0f);
        if (Servers.isThisAnEpicOrChallengeServer() || Features.Feature.NEW_ARMOUR_VALUES.isEnabled()) {
            ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setEffectiveness((byte)1, 0.95f);
        }
        ArmourTemplate.ARMOUR_TYPE_LEATHER.setEffectiveness((byte)4, 1.15f);
        ArmourTemplate.ARMOUR_TYPE_STUDDED.setEffectiveness((byte)4, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_CHAIN.setEffectiveness((byte)4, 1.05f);
        ArmourTemplate.ARMOUR_TYPE_PLATE.setEffectiveness((byte)4, 0.95f);
        ArmourTemplate.ARMOUR_TYPE_RING.setEffectiveness((byte)4, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_CLOTH.setEffectiveness((byte)4, 0.9f);
        ArmourTemplate.ARMOUR_TYPE_SCALE.setEffectiveness((byte)4, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_SPLINT.setEffectiveness((byte)4, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setEffectiveness((byte)4, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setEffectiveness((byte)4, 1.1f);
        if (Servers.isThisAnEpicOrChallengeServer() || Features.Feature.NEW_ARMOUR_VALUES.isEnabled()) {
            ArmourTemplate.ARMOUR_TYPE_LEATHER.setEffectiveness((byte)4, 1.1f);
            ArmourTemplate.ARMOUR_TYPE_CHAIN.setEffectiveness((byte)4, 1.075f);
            ArmourTemplate.ARMOUR_TYPE_CLOTH.setEffectiveness((byte)4, 0.8f);
            ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setEffectiveness((byte)4, 1.05f);
        }
        ArmourTemplate.ARMOUR_TYPE_LEATHER.setEffectiveness((byte)8, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_STUDDED.setEffectiveness((byte)8, 0.9f);
        ArmourTemplate.ARMOUR_TYPE_CHAIN.setEffectiveness((byte)8, 0.9f);
        ArmourTemplate.ARMOUR_TYPE_PLATE.setEffectiveness((byte)8, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_RING.setEffectiveness((byte)8, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_CLOTH.setEffectiveness((byte)8, 1.25f);
        ArmourTemplate.ARMOUR_TYPE_SCALE.setEffectiveness((byte)8, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_SPLINT.setEffectiveness((byte)8, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setEffectiveness((byte)8, 1.05f);
        ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setEffectiveness((byte)8, 0.95f);
        if (Servers.isThisAnEpicOrChallengeServer() || Features.Feature.NEW_ARMOUR_VALUES.isEnabled()) {
            ArmourTemplate.ARMOUR_TYPE_STUDDED.setEffectiveness((byte)8, 1.1f);
            ArmourTemplate.ARMOUR_TYPE_CHAIN.setEffectiveness((byte)8, 0.925f);
            ArmourTemplate.ARMOUR_TYPE_CLOTH.setEffectiveness((byte)8, 1.2f);
        }
        ArmourTemplate.ARMOUR_TYPE_LEATHER.setEffectiveness((byte)10, 0.9f);
        ArmourTemplate.ARMOUR_TYPE_STUDDED.setEffectiveness((byte)10, 1.05f);
        ArmourTemplate.ARMOUR_TYPE_CHAIN.setEffectiveness((byte)10, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_PLATE.setEffectiveness((byte)10, 1.07f);
        ArmourTemplate.ARMOUR_TYPE_RING.setEffectiveness((byte)10, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_CLOTH.setEffectiveness((byte)10, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_SCALE.setEffectiveness((byte)10, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_SPLINT.setEffectiveness((byte)10, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setEffectiveness((byte)10, 0.95f);
        ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setEffectiveness((byte)10, 1.0f);
        if (Servers.isThisAnEpicOrChallengeServer() || Features.Feature.NEW_ARMOUR_VALUES.isEnabled()) {
            ArmourTemplate.ARMOUR_TYPE_STUDDED.setEffectiveness((byte)10, 0.9f);
            ArmourTemplate.ARMOUR_TYPE_PLATE.setEffectiveness((byte)10, 1.05f);
        }
        ArmourTemplate.ARMOUR_TYPE_LEATHER.setEffectiveness((byte)6, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_STUDDED.setEffectiveness((byte)6, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_CHAIN.setEffectiveness((byte)6, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_PLATE.setEffectiveness((byte)6, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_RING.setEffectiveness((byte)6, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_CLOTH.setEffectiveness((byte)6, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_SCALE.setEffectiveness((byte)6, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_SPLINT.setEffectiveness((byte)6, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setEffectiveness((byte)6, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setEffectiveness((byte)6, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER.setEffectiveness((byte)9, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_STUDDED.setEffectiveness((byte)9, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_CHAIN.setEffectiveness((byte)9, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_PLATE.setEffectiveness((byte)9, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_RING.setEffectiveness((byte)9, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_CLOTH.setEffectiveness((byte)9, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_SCALE.setEffectiveness((byte)9, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_SPLINT.setEffectiveness((byte)9, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setEffectiveness((byte)9, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setEffectiveness((byte)9, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER.setEffectiveness((byte)5, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_STUDDED.setEffectiveness((byte)5, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_CHAIN.setEffectiveness((byte)5, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_PLATE.setEffectiveness((byte)5, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_RING.setEffectiveness((byte)5, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_CLOTH.setEffectiveness((byte)5, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_SCALE.setEffectiveness((byte)5, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_SPLINT.setEffectiveness((byte)5, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setEffectiveness((byte)5, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setEffectiveness((byte)5, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER.setEffectiveness((byte)7, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_STUDDED.setEffectiveness((byte)7, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_CHAIN.setEffectiveness((byte)7, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_PLATE.setEffectiveness((byte)7, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_RING.setEffectiveness((byte)7, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_CLOTH.setEffectiveness((byte)7, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_SCALE.setEffectiveness((byte)7, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_SPLINT.setEffectiveness((byte)7, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setEffectiveness((byte)7, 1.0f);
        ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setEffectiveness((byte)7, 1.0f);
        for (byte woundType = 0; woundType <= 10; ++woundType) {
            ArmourTemplate.ARMOUR_TYPE_NONE.setEffectiveness(woundType, 1.0f);
        }
    }
    
    public static void initializeArmourGlanceRates() {
        ArmourTemplate.ARMOUR_TYPE_LEATHER.setGlanceRate((byte)3, 0.3f);
        ArmourTemplate.ARMOUR_TYPE_STUDDED.setGlanceRate((byte)3, 0.45f);
        ArmourTemplate.ARMOUR_TYPE_CHAIN.setGlanceRate((byte)3, 0.6f);
        ArmourTemplate.ARMOUR_TYPE_PLATE.setGlanceRate((byte)3, 0.45f);
        ArmourTemplate.ARMOUR_TYPE_RING.setGlanceRate((byte)3, 0.5f);
        ArmourTemplate.ARMOUR_TYPE_CLOTH.setGlanceRate((byte)3, 0.3f);
        ArmourTemplate.ARMOUR_TYPE_SCALE.setGlanceRate((byte)3, 0.6f);
        ArmourTemplate.ARMOUR_TYPE_SPLINT.setGlanceRate((byte)3, 0.3f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setGlanceRate((byte)3, 0.5f);
        ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setGlanceRate((byte)3, 0.4f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER.setGlanceRate((byte)0, 0.5f);
        ArmourTemplate.ARMOUR_TYPE_STUDDED.setGlanceRate((byte)0, 0.6f);
        ArmourTemplate.ARMOUR_TYPE_CHAIN.setGlanceRate((byte)0, 0.25f);
        ArmourTemplate.ARMOUR_TYPE_PLATE.setGlanceRate((byte)0, 0.25f);
        ArmourTemplate.ARMOUR_TYPE_RING.setGlanceRate((byte)0, 0.3f);
        ArmourTemplate.ARMOUR_TYPE_CLOTH.setGlanceRate((byte)0, 0.5f);
        ArmourTemplate.ARMOUR_TYPE_SCALE.setGlanceRate((byte)0, 0.3f);
        ArmourTemplate.ARMOUR_TYPE_SPLINT.setGlanceRate((byte)0, 0.4f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setGlanceRate((byte)0, 0.5f);
        ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setGlanceRate((byte)0, 0.5f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER.setGlanceRate((byte)2, 0.3f);
        ArmourTemplate.ARMOUR_TYPE_STUDDED.setGlanceRate((byte)2, 0.25f);
        ArmourTemplate.ARMOUR_TYPE_CHAIN.setGlanceRate((byte)2, 0.25f);
        ArmourTemplate.ARMOUR_TYPE_PLATE.setGlanceRate((byte)2, 0.6f);
        ArmourTemplate.ARMOUR_TYPE_RING.setGlanceRate((byte)2, 0.3f);
        ArmourTemplate.ARMOUR_TYPE_CLOTH.setGlanceRate((byte)2, 0.35f);
        ArmourTemplate.ARMOUR_TYPE_SCALE.setGlanceRate((byte)2, 0.5f);
        ArmourTemplate.ARMOUR_TYPE_SPLINT.setGlanceRate((byte)2, 0.4f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setGlanceRate((byte)2, 0.2f);
        ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setGlanceRate((byte)2, 0.6f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER.setGlanceRate((byte)1, 0.3f);
        ArmourTemplate.ARMOUR_TYPE_STUDDED.setGlanceRate((byte)1, 0.25f);
        ArmourTemplate.ARMOUR_TYPE_CHAIN.setGlanceRate((byte)1, 0.6f);
        ArmourTemplate.ARMOUR_TYPE_PLATE.setGlanceRate((byte)1, 0.25f);
        ArmourTemplate.ARMOUR_TYPE_RING.setGlanceRate((byte)1, 0.5f);
        ArmourTemplate.ARMOUR_TYPE_CLOTH.setGlanceRate((byte)1, 0.35f);
        ArmourTemplate.ARMOUR_TYPE_SCALE.setGlanceRate((byte)1, 0.3f);
        ArmourTemplate.ARMOUR_TYPE_SPLINT.setGlanceRate((byte)1, 0.4f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setGlanceRate((byte)1, 0.5f);
        ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setGlanceRate((byte)1, 0.2f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER.setGlanceRate((byte)4, 0.1f);
        ArmourTemplate.ARMOUR_TYPE_STUDDED.setGlanceRate((byte)4, 0.1f);
        ArmourTemplate.ARMOUR_TYPE_CHAIN.setGlanceRate((byte)4, 0.6f);
        ArmourTemplate.ARMOUR_TYPE_PLATE.setGlanceRate((byte)4, 0.3f);
        ArmourTemplate.ARMOUR_TYPE_RING.setGlanceRate((byte)4, 0.2f);
        ArmourTemplate.ARMOUR_TYPE_CLOTH.setGlanceRate((byte)4, 0.1f);
        ArmourTemplate.ARMOUR_TYPE_SCALE.setGlanceRate((byte)4, 0.3f);
        ArmourTemplate.ARMOUR_TYPE_SPLINT.setGlanceRate((byte)4, 0.2f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setGlanceRate((byte)4, 0.3f);
        ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setGlanceRate((byte)4, 0.5f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER.setGlanceRate((byte)8, 0.6f);
        ArmourTemplate.ARMOUR_TYPE_STUDDED.setGlanceRate((byte)8, 0.6f);
        ArmourTemplate.ARMOUR_TYPE_CHAIN.setGlanceRate((byte)8, 0.1f);
        ArmourTemplate.ARMOUR_TYPE_PLATE.setGlanceRate((byte)8, 0.3f);
        ArmourTemplate.ARMOUR_TYPE_RING.setGlanceRate((byte)8, 0.2f);
        ArmourTemplate.ARMOUR_TYPE_CLOTH.setGlanceRate((byte)8, 0.6f);
        ArmourTemplate.ARMOUR_TYPE_SCALE.setGlanceRate((byte)8, 0.3f);
        ArmourTemplate.ARMOUR_TYPE_SPLINT.setGlanceRate((byte)8, 0.2f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setGlanceRate((byte)8, 0.5f);
        ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setGlanceRate((byte)8, 0.2f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER.setGlanceRate((byte)10, 0.2f);
        ArmourTemplate.ARMOUR_TYPE_STUDDED.setGlanceRate((byte)10, 0.2f);
        ArmourTemplate.ARMOUR_TYPE_CHAIN.setGlanceRate((byte)10, 0.2f);
        ArmourTemplate.ARMOUR_TYPE_PLATE.setGlanceRate((byte)10, 0.3f);
        ArmourTemplate.ARMOUR_TYPE_RING.setGlanceRate((byte)10, 0.5f);
        ArmourTemplate.ARMOUR_TYPE_CLOTH.setGlanceRate((byte)10, 0.6f);
        ArmourTemplate.ARMOUR_TYPE_SCALE.setGlanceRate((byte)10, 0.3f);
        ArmourTemplate.ARMOUR_TYPE_SPLINT.setGlanceRate((byte)10, 0.6f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setGlanceRate((byte)10, 0.3f);
        ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setGlanceRate((byte)10, 0.2f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER.setGlanceRate((byte)6, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_STUDDED.setGlanceRate((byte)6, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_CHAIN.setGlanceRate((byte)6, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_PLATE.setGlanceRate((byte)6, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_RING.setGlanceRate((byte)6, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_CLOTH.setGlanceRate((byte)6, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_SCALE.setGlanceRate((byte)6, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_SPLINT.setGlanceRate((byte)6, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setGlanceRate((byte)6, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setGlanceRate((byte)6, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER.setGlanceRate((byte)9, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_STUDDED.setGlanceRate((byte)9, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_CHAIN.setGlanceRate((byte)9, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_PLATE.setGlanceRate((byte)9, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_RING.setGlanceRate((byte)9, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_CLOTH.setGlanceRate((byte)9, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_SCALE.setGlanceRate((byte)9, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_SPLINT.setGlanceRate((byte)9, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setGlanceRate((byte)9, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setGlanceRate((byte)9, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER.setGlanceRate((byte)5, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_STUDDED.setGlanceRate((byte)5, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_CHAIN.setGlanceRate((byte)5, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_PLATE.setGlanceRate((byte)5, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_RING.setGlanceRate((byte)5, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_CLOTH.setGlanceRate((byte)5, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_SCALE.setGlanceRate((byte)5, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_SPLINT.setGlanceRate((byte)5, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setGlanceRate((byte)5, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setGlanceRate((byte)5, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER.setGlanceRate((byte)7, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_STUDDED.setGlanceRate((byte)7, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_CHAIN.setGlanceRate((byte)7, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_PLATE.setGlanceRate((byte)7, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_RING.setGlanceRate((byte)7, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_CLOTH.setGlanceRate((byte)7, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_SCALE.setGlanceRate((byte)7, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_SPLINT.setGlanceRate((byte)7, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON.setGlanceRate((byte)7, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON.setGlanceRate((byte)7, 0.0f);
        for (byte woundType = 0; woundType <= 10; ++woundType) {
            ArmourTemplate.ARMOUR_TYPE_NONE.setGlanceRate(woundType, 0.0f);
        }
    }
    
    public static void initialize() {
        if (Servers.isThisAnEpicOrChallengeServer() || Features.Feature.NEW_ARMOUR_VALUES.isEnabled()) {
            ArmourTemplate.ARMOUR_TYPE_LEATHER.setBaseDR(0.6f);
            ArmourTemplate.ARMOUR_TYPE_STUDDED.setBaseDR(0.625f);
            ArmourTemplate.ARMOUR_TYPE_CHAIN.setBaseDR(0.625f);
            ArmourTemplate.ARMOUR_TYPE_PLATE.setBaseDR(0.65f);
            ArmourTemplate.ARMOUR_TYPE_CLOTH.setBaseDR(0.4f);
        }
        initializeProtectionSlots();
        initializeCreatureArmour();
        initializeArmourEffectiveness();
        initializeArmourGlanceRates();
        initializeArmourTemplates();
    }
    
    static {
        logger = Logger.getLogger(ArmourTemplate.class.getName());
        ArmourTemplate.armourTemplates = new HashMap<Integer, ArmourTemplate>();
        ArmourTemplate.protectionSlots = new ArrayList<ProtectionSlot>();
        ArmourTemplate.ARMOUR_TYPE_NONE = new ArmourType("none", 0.0f, 0.3f, 0.0f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER = new ArmourType("leather", 0.45f, 0.3f, 0.2f);
        ArmourTemplate.ARMOUR_TYPE_STUDDED = new ArmourType("studded", 0.5f, 0.0f, 0.2f);
        ArmourTemplate.ARMOUR_TYPE_CHAIN = new ArmourType("chain", 0.55f, -0.15f, 0.25f);
        ArmourTemplate.ARMOUR_TYPE_PLATE = new ArmourType("plate", 0.63f, -0.3f, 0.25f);
        ArmourTemplate.ARMOUR_TYPE_RING = new ArmourType("ring", 0.5f, 0.0f, 0.35f);
        ArmourTemplate.ARMOUR_TYPE_CLOTH = new ArmourType("cloth", 0.35f, 0.3f, 0.2f);
        ArmourTemplate.ARMOUR_TYPE_SCALE = new ArmourType("scale", 0.45f, 0.0f, 0.3f);
        ArmourTemplate.ARMOUR_TYPE_SPLINT = new ArmourType("splint", 0.55f, 0.0f, 0.3f);
        ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON = new ArmourType("drake", 0.65f, -0.3f, 0.25f);
        ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON = new ArmourType("dragonscale", 0.7f, -0.3f, 0.35f);
    }
    
    public static class ProtectionSlot
    {
        byte armourPosition;
        ArrayList<Byte> bodySlots;
        
        ProtectionSlot(final byte armourPosition, final byte[] bodyPositions) {
            this.bodySlots = new ArrayList<Byte>();
            this.armourPosition = armourPosition;
            for (final byte bodyPos : bodyPositions) {
                this.bodySlots.add(bodyPos);
            }
            ArmourTemplate.protectionSlots.add(this);
        }
    }
    
    public static class ArmourType
    {
        protected HashMap<Byte, Float> armourEffectiveness;
        protected HashMap<Byte, Float> glanceRates;
        protected String name;
        protected float baseDamageReduction;
        protected float limitFactor;
        protected float arrowGlance;
        protected float creatureGlanceRate;
        protected byte creatureGlanceBonusWoundType;
        protected float creatureBonusWoundIncrease;
        
        public ArmourType(final String name, final float baseDamageReduction, final float limitFactor, final float arrowGlance) {
            this.armourEffectiveness = new HashMap<Byte, Float>();
            this.glanceRates = new HashMap<Byte, Float>();
            this.creatureGlanceRate = 0.0f;
            this.creatureGlanceBonusWoundType = -1;
            this.creatureBonusWoundIncrease = 0.0f;
            this.name = name;
            this.baseDamageReduction = baseDamageReduction;
            this.limitFactor = limitFactor;
            this.arrowGlance = arrowGlance;
        }
        
        public String getName() {
            return this.name;
        }
        
        public float getBaseDR() {
            return this.baseDamageReduction;
        }
        
        public void setBaseDR(final float newBaseDR) {
            this.baseDamageReduction = newBaseDR;
        }
        
        public float getLimitFactor() {
            return this.limitFactor;
        }
        
        public float getArrowGlance() {
            return this.arrowGlance;
        }
        
        public float getEffectiveness(final byte woundType) {
            if (this.armourEffectiveness.containsKey(woundType)) {
                return this.armourEffectiveness.get(woundType);
            }
            ArmourTemplate.logger.warning(String.format("No armour effectiveness set for wound type %s against %s.", woundType, this.getName()));
            return 1.0f;
        }
        
        public void setEffectiveness(final byte woundType, final float effectiveness) {
            this.armourEffectiveness.put(woundType, effectiveness);
        }
        
        public float getGlanceRate(final byte woundType, final byte armourMaterial) {
            final float materialMod = ArmourTemplate.getArmourMatGlanceBonus(armourMaterial);
            if (this.glanceRates.containsKey(woundType)) {
                return this.glanceRates.get(woundType) * materialMod;
            }
            ArmourTemplate.logger.warning(String.format("No glance rate set for wound type %s against %s.", woundType, this.getName()));
            return 0.0f;
        }
        
        public void setGlanceRate(final byte woundType, final float glanceRate) {
            this.glanceRates.put(woundType, glanceRate);
        }
        
        public float getCreatureGlance(final byte woundType, @Nonnull final Item armour) {
            float toReturn = this.creatureGlanceRate;
            if (woundType == this.creatureGlanceBonusWoundType) {
                toReturn += this.creatureBonusWoundIncrease;
            }
            toReturn = 0.05f + toReturn * (float)Server.getBuffedQualityEffect(armour.getCurrentQualityLevel() / 100.0f);
            return toReturn;
        }
        
        public void setCreatureGlance(final float baseGlance, final byte bonusWoundType, final float woundBonus) {
            this.creatureGlanceRate = baseGlance;
            this.creatureGlanceBonusWoundType = bonusWoundType;
            this.creatureBonusWoundIncrease = woundBonus;
        }
    }
}
