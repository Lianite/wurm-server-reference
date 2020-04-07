// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import java.io.IOException;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.ModelConstants;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.economy.MonetaryConstants;

public class ItemTemplateCreatorKingdom extends ItemTemplateCreator implements MonetaryConstants, MiscConstants, ModelConstants, ItemTypes
{
    private static final Logger logger;
    
    public static void initializeTemplates() throws IOException {
        ItemTemplateCreator.createItemTemplate(384, "guard tower", "towers", "excellent", "good", "ok", "poor", "A high guard tower.", new short[] { 52, 25, 31, 67, 44, 85, 86, 49, 98, 123, 194, 239 }, (short)60, (short)1, 0, 19353600L, 400, 400, 600, -10, ItemTemplateCreatorKingdom.EMPTY_BYTE_PRIMITIVE_ARRAY, "model.structure.guardtower.jenn.", 20.0f, 500000, (byte)15);
        ItemTemplateCreator.createItemTemplate(430, "guard tower", "towers", "excellent", "good", "ok", "poor", "A high guard tower.", new short[] { 52, 25, 31, 67, 44, 85, 86, 49, 98, 123, 194, 239 }, (short)60, (short)1, 0, 19353600L, 400, 400, 600, -10, ItemTemplateCreatorKingdom.EMPTY_BYTE_PRIMITIVE_ARRAY, "model.structure.guardtower.hots.", 20.0f, 500000, (byte)15);
        ItemTemplateCreator.createItemTemplate(528, "guard tower", "towers", "excellent", "good", "ok", "poor", "A high guard tower.", new short[] { 52, 25, 31, 67, 44, 85, 86, 49, 98, 123, 194, 239 }, (short)60, (short)1, 0, 19353600L, 400, 400, 600, -10, ItemTemplateCreatorKingdom.EMPTY_BYTE_PRIMITIVE_ARRAY, "model.structure.guardtower.molr.", 20.0f, 500000, (byte)15);
        ItemTemplateCreator.createItemTemplate(638, "guard tower", "towers", "excellent", "good", "ok", "poor", "A high guard tower.", new short[] { 52, 25, 31, 67, 44, 85, 86, 49, 98, 123, 194, 239 }, (short)60, (short)1, 0, 19353600L, 400, 400, 600, -10, ItemTemplateCreatorKingdom.EMPTY_BYTE_PRIMITIVE_ARRAY, "model.structure.guardtower.free.", 20.0f, 500000, (byte)15);
        ItemTemplateCreator.createItemTemplate(1431, "kingdom stone", "kingdom stones", "excellent", "good", "ok", "poor", "A stone that can be claimed for your kingdom to apply a buff.", new short[] { 108, 25, 31, 123, 52, 40, 98, 49, 109 }, (short)60, (short)1, 0, Long.MAX_VALUE, 200, 250, 400, -10, ItemTemplateCreatorKingdom.EMPTY_BYTE_PRIMITIVE_ARRAY, "model.structure.portal.10.", 25.0f, 30000, (byte)15, 10000, true);
    }
    
    static {
        logger = Logger.getLogger(ItemTemplateCreatorKingdom.class.getName());
    }
}
