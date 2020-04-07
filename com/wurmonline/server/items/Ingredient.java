// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.creatures.NoSuchCreatureTemplateException;
import com.wurmonline.server.creatures.CreatureTemplateFactory;
import javax.annotation.Nullable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public class Ingredient implements MiscConstants
{
    private static final Logger logger;
    private ItemTemplate itemTemplate;
    private final boolean isResult;
    private String cstateName;
    private byte cstate;
    private String pstateName;
    private byte pstate;
    private int amount;
    private int ratio;
    private String materialName;
    private byte material;
    private boolean hasRealTemplate;
    private ItemTemplate realItemTemplate;
    private String corpseDataName;
    private int corpseData;
    private String materialRef;
    private String realTemplateRef;
    private int difficulty;
    private int loss;
    private String resultName;
    private String resultDescription;
    private boolean useResultTemplateWeight;
    private byte groupId;
    private byte ingredientId;
    private int found;
    private short icon;
    
    public Ingredient(final ItemTemplate itemTemplate, final boolean isResult, final byte groupId) {
        this.cstateName = "";
        this.cstate = -1;
        this.pstateName = "";
        this.pstate = -1;
        this.amount = 0;
        this.ratio = 0;
        this.materialName = "";
        this.material = -1;
        this.hasRealTemplate = false;
        this.realItemTemplate = null;
        this.corpseDataName = "";
        this.corpseData = -1;
        this.materialRef = "";
        this.realTemplateRef = "";
        this.difficulty = 0;
        this.loss = 0;
        this.resultName = "";
        this.resultDescription = "";
        this.useResultTemplateWeight = false;
        this.ingredientId = -1;
        this.found = 0;
        this.icon = -1;
        this.itemTemplate = itemTemplate;
        this.isResult = isResult;
        if (isResult) {
            this.difficulty = -100;
        }
        this.groupId = groupId;
    }
    
    public Ingredient(final int templateId, final byte cstate, final byte pstate, final byte material, final boolean hasRealTemplate, final int realTemplateId, final int corpseType) throws NoSuchTemplateException {
        this.cstateName = "";
        this.cstate = -1;
        this.pstateName = "";
        this.pstate = -1;
        this.amount = 0;
        this.ratio = 0;
        this.materialName = "";
        this.material = -1;
        this.hasRealTemplate = false;
        this.realItemTemplate = null;
        this.corpseDataName = "";
        this.corpseData = -1;
        this.materialRef = "";
        this.realTemplateRef = "";
        this.difficulty = 0;
        this.loss = 0;
        this.resultName = "";
        this.resultDescription = "";
        this.useResultTemplateWeight = false;
        this.ingredientId = -1;
        this.found = 0;
        this.icon = -1;
        this.itemTemplate = ItemTemplateFactory.getInstance().getTemplate(templateId);
        this.isResult = false;
        this.groupId = 0;
        this.cstate = cstate;
        this.cstateName = this.generateCookingStateName(cstate);
        this.pstate = pstate;
        this.pstateName = this.generatePhysicalStateName(pstate);
        this.material = material;
        this.materialName = this.generateMaterialName();
        this.hasRealTemplate = hasRealTemplate;
        if (realTemplateId != -10L) {
            this.realItemTemplate = ItemTemplateFactory.getInstance().getTemplate(realTemplateId);
        }
        this.corpseData = corpseType;
        this.corpseDataName = this.generateCorpseName();
    }
    
    public Ingredient(final DataInputStream dis) throws IOException, NoSuchTemplateException {
        this.cstateName = "";
        this.cstate = -1;
        this.pstateName = "";
        this.pstate = -1;
        this.amount = 0;
        this.ratio = 0;
        this.materialName = "";
        this.material = -1;
        this.hasRealTemplate = false;
        this.realItemTemplate = null;
        this.corpseDataName = "";
        this.corpseData = -1;
        this.materialRef = "";
        this.realTemplateRef = "";
        this.difficulty = 0;
        this.loss = 0;
        this.resultName = "";
        this.resultDescription = "";
        this.useResultTemplateWeight = false;
        this.ingredientId = -1;
        this.found = 0;
        this.icon = -1;
        this.ingredientId = dis.readByte();
        this.groupId = dis.readByte();
        final short templateId = dis.readShort();
        this.itemTemplate = ItemTemplateFactory.getInstance().getTemplate(templateId);
        this.isResult = false;
        this.cstate = dis.readByte();
        this.cstateName = this.generateCookingStateName(this.cstate);
        this.pstate = dis.readByte();
        this.pstateName = this.generatePhysicalStateName(this.pstate);
        this.material = dis.readByte();
        this.materialName = this.generateMaterialName();
        if (templateId == 272) {
            this.corpseData = dis.readShort();
            this.corpseDataName = this.generateCorpseName();
        }
        else {
            this.hasRealTemplate = dis.readBoolean();
            final short realItemTemplateId = dis.readShort();
            if (realItemTemplateId != -10L) {
                this.realItemTemplate = ItemTemplateFactory.getInstance().getTemplate(realItemTemplateId);
            }
        }
    }
    
    public void pack(final DataOutputStream dos) throws IOException {
        dos.writeByte(this.ingredientId);
        dos.writeByte(this.groupId);
        dos.writeShort(this.itemTemplate.getTemplateId());
        dos.writeByte(this.cstate);
        dos.writeByte(this.pstate);
        dos.writeByte(this.material);
        if (this.itemTemplate.getTemplateId() == 272) {
            dos.writeShort(this.corpseData);
        }
        else {
            dos.writeBoolean(this.hasRealTemplate);
            dos.writeShort(this.getRealTemplateId());
        }
    }
    
    public byte getGroupId() {
        return this.groupId;
    }
    
    void setIngredientId(final byte ingredientId) {
        this.ingredientId = ingredientId;
    }
    
    public byte getIngredientId() {
        return this.ingredientId;
    }
    
    public boolean matches(final Item item) {
        return ((this.itemTemplate.isFoodGroup() && item.getTemplate().getFoodGroup() == this.itemTemplate.getTemplateId()) || this.getTemplateId() == item.getTemplateId()) && this.checkState(item) && this.checkMaterial(item) && this.checkRealTemplate(item) && this.checkCorpseData(item);
    }
    
    public Ingredient clone(@Nullable final Item item) {
        final Ingredient ingredient = new Ingredient((item != null) ? item.getTemplate() : this.itemTemplate, this.isResult, this.groupId);
        ingredient.setIngredientId(this.ingredientId);
        if (item != null) {
            ingredient.setAmount(this.getAmount());
        }
        ingredient.setRatio(this.getRatio());
        ingredient.setLoss(this.getLoss());
        if (this.hasCState()) {
            ingredient.setCState(this.cstate, this.cstateName);
        }
        if (this.hasPState()) {
            ingredient.setPState(this.pstate, this.pstateName);
        }
        if (this.hasMaterial()) {
            ingredient.setMaterial(this.material, this.materialName);
        }
        if (this.hasRealTemplate()) {
            ingredient.setRealTemplate(this.realItemTemplate);
        }
        if (this.hasCorpseData()) {
            ingredient.setCorpseData(this.corpseData, this.corpseDataName);
        }
        return ingredient;
    }
    
    public void setResultName(final String resultName) {
        this.resultName = resultName;
    }
    
    String getResultName() {
        return this.resultName;
    }
    
    public String getName(final boolean withAmount) {
        final StringBuilder buf = new StringBuilder();
        if (this.hasCState()) {
            buf.append(this.cstateName);
            if (this.hasPState() && this.pstateName.length() > 0) {
                buf.append("+" + this.pstateName);
            }
            buf.append(" ");
        }
        else if (this.hasPState() && this.pstateName.length() > 0) {
            buf.append(this.pstateName + " ");
        }
        if (this.hasCorpseData()) {
            buf.append(this.corpseDataName + " ");
        }
        buf.append(this.itemTemplate.getName());
        if (this.hasMaterial()) {
            buf.append(" (" + this.materialName + ")");
        }
        if (this.hasRealTemplate()) {}
        if (withAmount && this.getAmount() > 1) {
            if (this.getAmount() > 2) {
                buf.append(" (x3+)");
            }
            else {
                buf.append(" (x" + this.getAmount() + ")");
            }
        }
        return buf.toString().trim();
    }
    
    String getSubMenuName() {
        if (this.resultName.length() > 0) {
            return this.resultName;
        }
        if (this.hasCState()) {
            final StringBuilder buf = new StringBuilder();
            buf.append(this.cstateName);
            if (this.hasPState()) {
                buf.append("+" + this.pstateName);
            }
            buf.append(" ");
            buf.append(this.itemTemplate.getName());
            return buf.toString();
        }
        if (this.hasPState()) {
            return this.pstateName + " " + this.itemTemplate.getName();
        }
        return this.itemTemplate.getName();
    }
    
    String getCStateName() {
        return this.cstateName;
    }
    
    String getPStateName() {
        return this.pstateName;
    }
    
    void setUseResultTemplateWeight(final boolean useTemplateWeight) {
        this.useResultTemplateWeight = useTemplateWeight;
    }
    
    public boolean useResultTemplateWeight() {
        return this.useResultTemplateWeight;
    }
    
    void setResultDescription(final String description) {
        this.resultDescription = description;
    }
    
    boolean hasResultDescription() {
        return this.resultDescription.length() > 0;
    }
    
    String getResultDescription() {
        if (this.resultDescription.length() > 0) {
            return this.resultDescription;
        }
        return this.itemTemplate.getDescriptionLong();
    }
    
    public String getResultDescription(final Item resultItem) {
        if (this.resultDescription.length() > 0) {
            String desc = this.resultDescription;
            if (desc.indexOf(35) >= 0) {
                if (resultItem.getRealTemplateId() != -10 && resultItem.getRealTemplate() != null) {
                    desc = desc.replace("#", resultItem.getRealTemplate().getName());
                }
                else {
                    desc = desc.replace("# ", "").replace(" #", "");
                }
            }
            if (desc.indexOf(36) >= 0) {
                desc = desc.replace("$", this.generateMaterialName(resultItem.getMaterial()));
            }
            return desc;
        }
        return this.itemTemplate.getDescriptionLong();
    }
    
    public void setMaterialRef(final String materialRef) {
        this.materialRef = materialRef;
    }
    
    public String getMaterialRef() {
        return this.materialRef;
    }
    
    public boolean hasMaterialRef() {
        return this.materialRef.length() > 0;
    }
    
    public void setRealTemplateRef(final String realTemplateRef) {
        this.realTemplateRef = realTemplateRef;
    }
    
    public String getRealTemplateRef() {
        return this.realTemplateRef;
    }
    
    public boolean hasRealTemplateRef() {
        return this.realTemplateRef.length() > 0;
    }
    
    public void setCorpseData(final int data) {
        this.corpseData = data;
        this.corpseDataName = this.generateCorpseName();
    }
    
    public void setCorpseData(final int data, final String dataName) {
        this.corpseData = data;
        this.corpseDataName = dataName;
    }
    
    public int getCorpseData() {
        return this.corpseData;
    }
    
    public boolean hasCorpseData() {
        return this.corpseData != -1;
    }
    
    public String getCorpseName() {
        return this.corpseDataName;
    }
    
    public void setCState(final byte state) {
        this.cstate = state;
        this.cstateName = this.generateCookingStateName(this.cstate);
    }
    
    public void setCState(final byte state, final String stateName) {
        this.cstate = state;
        this.cstateName = this.generateCookingStateName(this.cstate);
    }
    
    public byte getCState() {
        return this.cstate;
    }
    
    public void setPState(final byte state) {
        this.pstate = state;
        this.pstateName = this.generatePhysicalStateName(this.pstate);
    }
    
    public void setPState(final byte state, final String stateName) {
        this.pstate = state;
        this.pstateName = this.generatePhysicalStateName(this.pstate);
    }
    
    public byte getPState() {
        return this.pstate;
    }
    
    public boolean hasCState() {
        return this.cstate != -1;
    }
    
    public boolean hasPState() {
        return this.pstate != -1;
    }
    
    public byte getXState() {
        if (!this.hasCState()) {
            return this.getPState();
        }
        if (this.hasPState()) {
            return (byte)(this.getCState() + this.getPState());
        }
        return this.getCState();
    }
    
    public boolean hasXState() {
        return this.cstate != -1 || this.pstate != -1;
    }
    
    public boolean hasRealTemplate() {
        return this.hasRealTemplate;
    }
    
    public boolean hasRealTemplateId() {
        return this.realItemTemplate != null;
    }
    
    public void setAmount(final int numb) {
        this.amount = numb;
    }
    
    public int getAmount() {
        if (this.amount == 0) {
            return 1;
        }
        return this.amount;
    }
    
    public void setRatio(final int numb) {
        this.ratio = numb;
    }
    
    public int getRatio() {
        return this.ratio;
    }
    
    public void setMaterial(final byte material) {
        this.material = material;
        this.materialName = this.generateMaterialName();
    }
    
    public void setMaterial(final byte material, final String materialName) {
        this.material = material;
        this.materialName = this.generateMaterialName();
    }
    
    public byte getMaterial() {
        return this.material;
    }
    
    public String getMaterialName() {
        return this.materialName;
    }
    
    public boolean hasMaterial() {
        return this.material != -1;
    }
    
    public void setDifficulty(final int difficulty) {
        this.difficulty = difficulty;
    }
    
    public int getDifficulty() {
        return this.difficulty;
    }
    
    void setIcon(final short icon) {
        this.icon = icon;
    }
    
    public short getIcon() {
        if (this.icon > -1) {
            return this.icon;
        }
        return this.itemTemplate.getImageNumber();
    }
    
    public void setLoss(final int loss) {
        this.loss = Math.min(100, Math.max(0, loss));
    }
    
    public int getLoss() {
        return this.loss;
    }
    
    public void setTemplate(final ItemTemplate itemTemplate) {
        this.itemTemplate = itemTemplate;
    }
    
    public void setRealTemplate(@Nullable final ItemTemplate itemTemplate) {
        this.realItemTemplate = itemTemplate;
        this.hasRealTemplate = true;
    }
    
    @Nullable
    public ItemTemplate getRealItemTemplate() {
        return this.realItemTemplate;
    }
    
    public ItemTemplate getTemplate() {
        return this.itemTemplate;
    }
    
    public String getTemplateName() {
        return this.itemTemplate.getName();
    }
    
    public int getTemplateId() {
        return this.itemTemplate.getTemplateId();
    }
    
    public int getRealTemplateId() {
        if (this.realItemTemplate == null) {
            return -10;
        }
        return this.realItemTemplate.getTemplateId();
    }
    
    public boolean isLiquid() {
        return this.itemTemplate.isLiquid();
    }
    
    public boolean isDrinkable() {
        return this.itemTemplate.drinkable;
    }
    
    public boolean isFoodGroup() {
        return this.itemTemplate.isFoodGroup();
    }
    
    int setFound(final boolean found) {
        if (found) {
            ++this.found;
        }
        else {
            this.found = 0;
        }
        return this.difficulty;
    }
    
    boolean wasFound(final boolean any, final boolean optional) {
        if (any || this.itemTemplate.isLiquid()) {
            return this.found > 0 || optional;
        }
        if (this.amount >= 3 && this.found >= 3) {
            return true;
        }
        if (this.found == 0) {
            return optional;
        }
        return this.amount == this.found || (this.amount == 0 && this.found == 1);
    }
    
    public int getFound() {
        return this.found;
    }
    
    boolean checkFoodGroup(final Item target) {
        if (this.isFoodGroup()) {
            return target.getTemplate().getFoodGroup() == this.getTemplateId();
        }
        return target.getTemplateId() == this.getTemplateId();
    }
    
    boolean checkState(final Item target) {
        return (!this.hasCState() && !this.hasPState()) || target.isCorrectFoodState(this.getCState(), this.getPState());
    }
    
    boolean checkMaterial(final Item target) {
        return !this.hasMaterial() || this.getMaterial() == target.getMaterial();
    }
    
    boolean checkCorpseData(final Item target) {
        return !this.hasCorpseData() || (this.getCorpseData() == target.getData1() && !target.isButchered());
    }
    
    boolean checkRealTemplate(final Item target) {
        return !this.hasRealTemplate() || this.getRealTemplateId() == target.getRealTemplateId() || (target.getRealTemplate() != null && target.getRealTemplate().getFoodGroup() == this.getRealTemplateId());
    }
    
    String generateCookingStateName(final byte state) {
        final StringBuilder builder = new StringBuilder();
        if (state != -1) {
            switch ((byte)(state & 0xF)) {
                case 0: {
                    if ((state & 0xF0) == 0x0) {
                        builder.append("raw");
                        break;
                    }
                    break;
                }
                case 1: {
                    builder.append("fried");
                    break;
                }
                case 2: {
                    builder.append("grilled");
                    break;
                }
                case 3: {
                    builder.append("boiled");
                    break;
                }
                case 4: {
                    builder.append("roasted");
                    break;
                }
                case 5: {
                    builder.append("steamed");
                    break;
                }
                case 6: {
                    builder.append("baked");
                    break;
                }
                case 7: {
                    builder.append("cooked");
                    break;
                }
                case 8: {
                    builder.append("candied");
                    break;
                }
                case 9: {
                    builder.append("chocolate coated");
                    break;
                }
            }
            if (state >= 16) {
                Ingredient.logger.info("Bad cooked state " + state + " for ingredient " + this.getName(true));
            }
        }
        return builder.toString();
    }
    
    String generatePhysicalStateName(final byte state) {
        final StringBuilder builder = new StringBuilder();
        if (state != -1) {
            if ((state & 0x10) != 0x0) {
                if (builder.length() > 0) {
                    builder.append("+");
                }
                if (this.itemTemplate.isHerb() || this.itemTemplate.isVegetable() || this.itemTemplate.isFish() || this.itemTemplate.isMushroom()) {
                    builder.append("chopped");
                }
                else if (this.itemTemplate.isMeat()) {
                    builder.append("diced");
                }
                else if (this.itemTemplate.isSpice()) {
                    builder.append("ground");
                }
                else if (this.itemTemplate.canBeFermented()) {
                    builder.append("unfermented");
                }
                else if (this.itemTemplate.getTemplateId() == 1249) {
                    builder.append("whipped");
                }
                else {
                    builder.append("zombified");
                }
            }
            if ((state & 0x20) != 0x0) {
                if (builder.length() > 0) {
                    builder.append("+");
                }
                if (this.itemTemplate.isMeat()) {
                    builder.append("minced");
                }
                else if (this.itemTemplate.isVegetable()) {
                    builder.append("mashed");
                }
                else if (this.itemTemplate.canBeFermented()) {
                    builder.append("fermenting");
                }
                else {
                    builder.append("clotted");
                }
            }
            if ((state & 0x40) != 0x0) {
                if (builder.length() > 0) {
                    builder.append("+");
                }
                if (this.itemTemplate.canBeDistilled()) {
                    builder.append("undistilled");
                }
                else {
                    builder.append("wrapped");
                }
            }
            if ((state & 0xFFFFFF80) != 0x0) {
                if (builder.length() > 0) {
                    builder.append("+");
                }
                if (this.itemTemplate.isDish) {
                    builder.append("salted");
                }
                else if (this.itemTemplate.isHerb() || this.itemTemplate.isSpice()) {
                    builder.append("fresh");
                }
            }
            if (state < 16 && state != 0) {
                Ingredient.logger.info("Bad physical state " + state + " for ingredient " + this.getName(true));
            }
        }
        return builder.toString();
    }
    
    String generateMaterialName() {
        return this.generateMaterialName(this.material);
    }
    
    String generateMaterialName(final byte mat) {
        if (mat != -1) {
            return Materials.convertMaterialByteIntoString(mat);
        }
        return "";
    }
    
    String generateCorpseName() {
        if (this.corpseData != -1) {
            try {
                final CreatureTemplate ct = CreatureTemplateFactory.getInstance().getTemplate(this.corpseData);
                return ct.getName();
            }
            catch (NoSuchCreatureTemplateException e) {
                return "unknown";
            }
        }
        return "";
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("{name='" + this.getTemplateName() + "'(" + this.getTemplateId());
        if (this.isFoodGroup()) {
            buf.append("(isFoodGroup)");
        }
        buf.append(")");
        if (this.cstate != -1) {
            buf.append(",cstate='" + this.cstateName + "'(" + this.cstate + ")");
        }
        if (this.pstate != -1) {
            buf.append(",pstate='" + this.pstateName + "'(" + this.pstate + ")");
        }
        if (this.material != -1) {
            buf.append(",material='" + this.materialName + "'(" + this.material + ")");
        }
        if (this.isResult) {
            if (this.difficulty != -100) {
                buf.append(",baseDifficulty='" + this.difficulty);
            }
        }
        else if (this.difficulty != 0) {
            buf.append(",addDifficulty='" + this.difficulty);
        }
        if (this.isLiquid()) {
            buf.append(",ratio=" + this.ratio + "%");
        }
        else if (this.amount > 1) {
            buf.append(",need=" + this.amount);
        }
        if (this.corpseData != -1) {
            buf.append(",creature='" + this.corpseDataName + "'(" + this.corpseData + ")");
        }
        if (this.hasRealTemplate) {
            buf.append(",realTemplate='");
            if (this.realItemTemplate != null) {
                buf.append(this.realItemTemplate.getName());
            }
            else {
                buf.append("null");
            }
            buf.append("'(" + this.getRealTemplateId() + ")");
        }
        if (this.isResult) {
            if (this.materialRef.length() > 0) {
                buf.append(",materialRef='" + this.materialRef + "'");
            }
            if (this.realTemplateRef.length() > 0) {
                buf.append(",realTemplateRef='" + this.realTemplateRef + "'");
            }
            if (this.resultName.length() > 0) {
                buf.append(",resultName='" + this.resultName + "'");
            }
            else {
                buf.append(",resultName='" + this.getName(true) + "'");
            }
        }
        buf.append("}");
        return buf.toString();
    }
    
    static {
        logger = Logger.getLogger(Ingredient.class.getName());
    }
}
