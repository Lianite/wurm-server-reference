// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.Properties;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.NoSuchItemException;
import java.util.Arrays;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.items.WurmColor;
import com.wurmonline.server.Items;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.ItemTemplate;
import java.util.LinkedList;
import com.wurmonline.shared.constants.ItemMaterials;

public final class ItemDataQuestion extends Question implements ItemMaterials
{
    private LinkedList<ItemTemplate> itemplates;
    
    public ItemDataQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 4, aTarget);
        this.itemplates = new LinkedList<ItemTemplate>();
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder(this.getBmlHeader());
        final int height = 380;
        int data1 = -1;
        int data2 = -1;
        int extra1 = -1;
        int extra2 = -1;
        byte auxData = 0;
        try {
            final Item it = Items.getItem(this.target);
            data1 = it.getData1();
            data2 = it.getData2();
            extra1 = it.getExtra1();
            extra2 = it.getExtra2();
            auxData = it.getAuxData();
            buf.append("harray{input{id='itemName'; maxchars='60'; text='" + it.getActualName() + "'}label{text='Item Actual Name'}}");
            if (it.hasData()) {
                buf.append("harray{input{id='data1'; maxchars='20'; text='" + data1 + "'}label{text='Data1'}}");
                buf.append("harray{input{id='data2'; maxchars='20'; text='" + data2 + "'}label{text='Data2'}}");
                buf.append("harray{input{id='extra1'; maxchars='20'; text='" + extra1 + "'}label{text='Extra1'}}");
                buf.append("harray{input{id='extra2'; maxchars='20'; text='" + extra2 + "'}label{text='Extra2'}}");
            }
            if (it.usesFoodState()) {
                buf.append("label{type=\"bold\";text=\"Food State (AuxByte)\"}");
                final int raux = it.getRightAuxData();
                buf.append("table{rows=\"4\";cols=\"8\";");
                buf.append(this.addRaux(0, raux, "(Raw)"));
                buf.append(this.addRaux(1, raux, "Fried"));
                buf.append(this.addRaux(2, raux, "Grilled"));
                buf.append(this.addRaux(3, raux, "Boiled"));
                buf.append(this.addRaux(4, raux, "Roasted"));
                buf.append(this.addRaux(5, raux, "Steamed"));
                buf.append(this.addRaux(6, raux, "Baked"));
                buf.append(this.addRaux(7, raux, "Cooked"));
                buf.append(this.addRaux(8, raux, "Candied"));
                buf.append(this.addRaux(9, raux, "Choc Coated"));
                buf.append("label{text=\"\"};label{text=\"\"}");
                buf.append("label{text=\"\"};label{text=\"\"}");
                if (it.isHerb() || it.isVegetable() || it.isFish() || it.isMushroom()) {
                    buf.append(this.addLaux("chopped", it.isChopped(), "Chopped"));
                }
                else if (it.isMeat()) {
                    buf.append(this.addLaux("chopped", it.isDiced(), "Diced"));
                }
                else if (it.isSpice()) {
                    buf.append(this.addLaux("chopped", it.isGround(), "Ground"));
                }
                else if (it.canBeFermented()) {
                    buf.append(this.addLaux("chopped", it.isUnfermented(), "Unfermented"));
                }
                else if (it.getTemplateId() == 1249) {
                    buf.append(this.addLaux("chopped", it.isWhipped(), "Whipped"));
                }
                else {
                    buf.append(this.addLaux("chopped", it.isZombiefied(), "Zombiefied"));
                }
                if (it.isVegetable()) {
                    buf.append(this.addLaux("mashed", it.isMashed(), "Mashed"));
                }
                else if (it.isMeat()) {
                    buf.append(this.addLaux("mashed", it.isMinced(), "Minced"));
                }
                else if (it.canBeFermented()) {
                    buf.append(this.addLaux("mashed", it.isFermenting(), "Fermenting"));
                }
                else if (it.isFish()) {
                    buf.append(this.addLaux("mashed", it.isUnderWeight(), "Underweight"));
                }
                else {
                    buf.append(this.addLaux("mashed", it.isClotted(), "Clotted"));
                }
                if (it.canBeDistilled()) {
                    buf.append(this.addLaux("wrap", it.isUndistilled(), "Undistilled"));
                }
                else {
                    buf.append(this.addLaux("wrap", it.isWrapped(), "Wrapped"));
                }
                if (it.isHerb() || it.isSpice()) {
                    buf.append(this.addLaux("fresh", it.isFresh(), "Fresh"));
                }
                else if (it.isDish() || it.isLiquid()) {
                    buf.append(this.addLaux("fresh", it.isSalted(), "Salted"));
                }
                else if (it.isFish()) {
                    buf.append(this.addLaux("fresh", it.isLive(), "live"));
                }
                else {
                    buf.append(this.addLaux("fresh", false, "n/a"));
                }
                buf.append("}");
                buf.append("label{text=\"\"}");
            }
            else {
                buf.append("harray{input{id='aux'; maxchars='4'; text='" + auxData + "'}label{text='Auxdata'}}");
            }
            buf.append("harray{input{id='dam'; maxchars='2'; text='" + (int)it.getDamage() + "'}label{text='Damage'}}");
            buf.append("harray{input{id='temp'; maxchars='5'; text='" + it.getTemperature() + "'}label{text='Temperature'}}");
            buf.append("harray{input{id='weight'; maxchars='7'; text='" + it.getWeightGrams(false) + "'}label{text='Weight'}}");
            buf.append("harray{input{id='rarity'; maxchars='1'; text='" + it.getRarity() + "'}label{text='Rarity'}}");
            buf.append("table{rows=\"1\";cols=\"4\";");
            String red = "";
            String green = "";
            String blue = "";
            boolean tick = false;
            if (it.getColor() != -1) {
                red = Integer.toString(WurmColor.getColorRed(it.getColor()));
                green = Integer.toString(WurmColor.getColorGreen(it.getColor()));
                blue = Integer.toString(WurmColor.getColorBlue(it.getColor()));
                tick = true;
            }
            buf.append("checkbox{id=\"primary\";text='Primary ';selected=\"" + tick + "\";hover=\"tick if has color\"}");
            buf.append("harray{label{text=' Red:'}input{id='c_red'; maxchars='3'; text='" + red + "'}}");
            buf.append("harray{label{text=' Green:'}input{id='c_green'; maxchars='3'; text='" + green + "'}}");
            buf.append("harray{label{text=' Blue:'}input{id='c_blue'; maxchars='3'; text='" + blue + "'}}");
            if (it.getTemplate().supportsSecondryColor()) {
                red = "";
                green = "";
                blue = "";
                tick = false;
                if (it.getColor2() != -1) {
                    red = Integer.toString(WurmColor.getColorRed(it.getColor2()));
                    green = Integer.toString(WurmColor.getColorGreen(it.getColor2()));
                    blue = Integer.toString(WurmColor.getColorBlue(it.getColor2()));
                    tick = true;
                }
                buf.append("checkbox{id=\"secondary\";text='Secondary ';selected=\"" + tick + "\";hover=\"tick if has color\"}");
                buf.append("harray{label{text=' Red:'}input{id='c2_red'; maxchars='3'; text='" + red + "'}}");
                buf.append("harray{label{text=' Green:'}input{id='c2_green'; maxchars='3'; text='" + green + "'}}");
                buf.append("harray{label{text=' Blue:'}input{id='c2_blue'; maxchars='3'; text='" + blue + "'}}");
            }
            buf.append("}");
            if (this.getResponder().getPower() >= 4) {
                final Item lunchbox = it.getParentOuterItemOrNull();
                long decayTick;
                if (it.isInLunchbox() && lunchbox != null) {
                    decayTick = it.getLastMaintained() - it.getDecayTime() * (lunchbox.getRarity() / 4 + 2) - 1L;
                }
                else {
                    decayTick = it.getLastMaintained() - it.getDecayTime() - 1L;
                }
                buf.append("harray{input{id='lastMaintained'; maxchars='60'; text='" + it.getLastMaintained() + "'}label{text='Last Maintained'}}");
                buf.append("harray{label{text=\"(Set to\"};label{text=\"" + decayTick + "\"hover=\"this number can be copied using copy line\"};label{text=\"for a decay tick!)\"}}");
            }
            buf.append("label{text=\"\"}");
            if (it.getTemplate().usesRealTemplate()) {
                final ItemTemplate[] templates = ItemTemplateFactory.getInstance().getTemplates();
                Arrays.sort(templates);
                this.itemplates.add(null);
                int def = 0;
                if (it.getTemplate().isRune()) {
                    for (int x = 0; x < templates.length; ++x) {
                        if (templates[x].getTemplateId() == 1102 || templates[x].getTemplateId() == 1104 || templates[x].getTemplateId() == 1103) {
                            if (it.getRealTemplate() == templates[x]) {
                                def = this.itemplates.size();
                            }
                            this.itemplates.add(templates[x]);
                        }
                    }
                }
                else {
                    for (int x = 0; x < templates.length; ++x) {
                        if (templates[x].isFood() || templates[x].isLiquid() || templates[x].isFruit()) {
                            if (it.getRealTemplate() == templates[x]) {
                                def = this.itemplates.size();
                            }
                            this.itemplates.add(templates[x]);
                        }
                        else if (it.getTemplateId() == 1307) {
                            this.itemplates.add(templates[x]);
                        }
                    }
                }
                buf.append("harray{label{text=\"Real Template\"};");
                buf.append("dropdown{id=\"fruit\";default=\"" + def + "\";options=\"");
                for (int i = 0; i < this.itemplates.size(); ++i) {
                    if (i > 0) {
                        buf.append(",");
                    }
                    final ItemTemplate tp = this.itemplates.get(i);
                    if (tp == null) {
                        buf.append("None");
                    }
                    else {
                        buf.append(tp.getName());
                    }
                }
                buf.append("\"}}");
                buf.append("label{text=\"\"}");
            }
            if (it.isFood()) {}
            buf.append("harray{label{text='LastOwner'}input{id='lastowner'; maxchars='11'; text='" + it.getLastOwnerId() + "'}}");
        }
        catch (NoSuchItemException ex) {}
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(320, height, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        QuestionParser.parseItemDataQuestion(this);
    }
    
    ItemTemplate getTemplate(final int fruitId) {
        return this.itemplates.get(fruitId);
    }
    
    private String addRaux(final int id, final int raux, final String name) {
        final StringBuilder buf = new StringBuilder();
        buf.append("radio{group=\"raux\";id=\"");
        buf.append(id);
        buf.append("\"");
        if (raux == id) {
            buf.append(";selected=\"true\"");
        }
        buf.append("};");
        buf.append("label{text=\"");
        buf.append(name);
        buf.append("\"};");
        return buf.toString();
    }
    
    private String addLaux(final String id, final boolean sel, final String name) {
        final StringBuilder buf = new StringBuilder();
        buf.append("checkbox{id=\"");
        buf.append(id);
        buf.append("\"");
        if (sel) {
            buf.append(";selected=\"true\"");
        }
        buf.append("};");
        buf.append("label{text=\"");
        buf.append(name);
        buf.append("\"};");
        return buf.toString();
    }
}
