// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.utils;

import com.wurmonline.server.Server;
import com.wurmonline.server.Servers;
import com.wurmonline.server.players.Player;
import javax.annotation.Nonnull;
import java.awt.Color;
import javax.annotation.Nullable;

public class BMLBuilder
{
    private StringBuilder sb;
    private int openBrackets;
    
    public static BMLBuilder createBMLUpdate(final String... updates) {
        final BMLBuilder builder = new BMLBuilder();
        builder.openBracket("update");
        for (final String update : updates) {
            builder.addString(update);
        }
        builder.closeBracket();
        return builder;
    }
    
    public static BMLBuilder createBMLBorderPanel(@Nullable final BMLBuilder north, @Nullable final BMLBuilder west, @Nullable final BMLBuilder center, @Nullable final BMLBuilder east, @Nullable final BMLBuilder south) {
        return createBMLBorderPanel(north, west, center, east, south, 0, 0);
    }
    
    public static BMLBuilder createBMLBorderPanel(@Nullable final BMLBuilder north, @Nullable final BMLBuilder west, @Nullable final BMLBuilder center, @Nullable final BMLBuilder east, @Nullable final BMLBuilder south, final int width, final int height) {
        final BMLBuilder builder = new BMLBuilder();
        builder.openBracket("border");
        if (width > 0 || height > 0) {
            builder.addString("size=\"" + width + "," + height + "\";");
        }
        if (north != null) {
            builder.addString(north.toString());
        }
        else {
            builder.addString("null;");
        }
        if (west != null) {
            builder.addString(west.toString());
        }
        else {
            builder.addString("null;");
        }
        if (center != null) {
            builder.addString(center.toString());
        }
        else {
            builder.addString("null;");
        }
        if (east != null) {
            builder.addString(east.toString());
        }
        else {
            builder.addString("null;");
        }
        if (south != null) {
            builder.addString(south.toString());
        }
        else {
            builder.addString("null;");
        }
        builder.closeBracket();
        return builder;
    }
    
    public static BMLBuilder createNormalWindow(final String id, final String question, final BMLBuilder content) {
        final BMLBuilder header = createCenteredNode(createGenericBuilder().addText(question, null, TextType.BOLD, null));
        final BMLBuilder center = createScrollPanelNode(true, false);
        center.addString(createVertArrayNode(true).addPassthrough("id", id).addString(content.toString()).toString());
        return createBMLBorderPanel(header, null, center, null, null);
    }
    
    public static BMLBuilder createNoQuestionWindow(final String id, final BMLBuilder content) {
        final BMLBuilder center = createScrollPanelNode(true, false);
        center.addString(createVertArrayNode(true).addPassthrough("id", id).addString(content.toString()).toString());
        return createBMLBorderPanel(null, null, center, null, null);
    }
    
    public static BMLBuilder createCenteredNode(final BMLBuilder content) {
        return createAlignedNode("center", content);
    }
    
    public static BMLBuilder createLeftAlignedNode(final BMLBuilder content) {
        return createAlignedNode("left", content);
    }
    
    public static BMLBuilder createRightAlignedNode(final BMLBuilder content) {
        return createAlignedNode("right", content);
    }
    
    public static BMLBuilder createAlignedNode(final String alignment, final BMLBuilder content) {
        final BMLBuilder builder = new BMLBuilder();
        builder.openBracket(alignment);
        builder.addString(content.toString());
        builder.closeBracket();
        return builder;
    }
    
    public static BMLBuilder createScrollPanelNode(final boolean vertical, final boolean horizontal) {
        return createScrollPanelNode(vertical, horizontal, 0, 0);
    }
    
    public static BMLBuilder createScrollPanelNode(final boolean vertical, final boolean horizontal, final int width, final int height) {
        final BMLBuilder builder = new BMLBuilder();
        builder.openBracket("scroll");
        if (width > 0 || height > 0) {
            builder.addString("size=\"" + width + "," + height + "\";");
        }
        builder.addString("vertical=\"" + vertical + "\";");
        builder.addString("horizontal=\"" + horizontal + "\";");
        return builder;
    }
    
    public static BMLBuilder createVertArrayNode(final boolean rescale) {
        final BMLBuilder builder = new BMLBuilder();
        builder.openBracket("varray");
        builder.addString("rescale=\"" + rescale + "\";");
        return builder;
    }
    
    public static BMLBuilder createHorizArrayNode(final boolean rescale) {
        final BMLBuilder builder = new BMLBuilder();
        builder.openBracket("harray");
        builder.addString("rescale=\"" + rescale + "\";");
        return builder;
    }
    
    public static BMLBuilder createTable(final int columns) {
        final BMLBuilder builder = new BMLBuilder();
        builder.openBracket("table");
        builder.addString("cols=\"" + columns + "\";");
        return builder;
    }
    
    public static BMLBuilder createTreeList(final String id, final int columnCount, final int height, final boolean showHeader) {
        final BMLBuilder builder = new BMLBuilder();
        builder.openBracket("tree");
        builder.addString("id=\"" + id + "\";");
        builder.addString("height=\"" + height + "\";");
        builder.addString("cols=\"" + columnCount + "\";");
        builder.addString("showheader=\"" + showHeader + "\";");
        return builder;
    }
    
    public static BMLBuilder createGenericBuilder() {
        return new BMLBuilder();
    }
    
    public static String createHeader(final String text) {
        return createGenericBuilder().addHeader(text).toString();
    }
    
    public static String createLabel(final String text) {
        return createGenericBuilder().addLabel(text).toString();
    }
    
    public static String createButton(final String id, final String text) {
        return createButton(id, text, true);
    }
    
    public static String createButton(final String id, final String text, final boolean enabled) {
        return createGenericBuilder().addButton(id, text, enabled).toString();
    }
    
    public static String createButton(final String id, final String text, final int width, final int height, final boolean enabled) {
        return createGenericBuilder().addButton(id, text, width, height, enabled).toString();
    }
    
    public static String createInput(final String id, @Nullable final String text) {
        return createGenericBuilder().addInput(id, text, 0, 0).toString();
    }
    
    public static String createText(final String text) {
        return createGenericBuilder().addText(text).toString();
    }
    
    public static String createCheckbox(final String id, final String text, final boolean selected) {
        return createGenericBuilder().addCheckbox(id, text, selected).toString();
    }
    
    public static String createDropdown(final String id, final String defaultOption, final String... options) {
        return createGenericBuilder().addDropdown(id, defaultOption, options).toString();
    }
    
    public static String createRadioButton(final String id, final String group, final String text, final boolean selected) {
        return createGenericBuilder().addRadioButton(id, group, text, selected).toString();
    }
    
    public static String createImage(final String imageSrc, final int width, final int height) {
        return createGenericBuilder().addImage(imageSrc, width, height).toString();
    }
    
    public static TreeListRowData createTLRD(final String text) {
        return new TreeListRowData(text);
    }
    
    public static TreeListRowData createTLRD(final String id, final String text, final boolean checkbox, final boolean selected) {
        return new TreeListRowData(id, text, checkbox, selected);
    }
    
    private BMLBuilder() {
        this.openBrackets = 0;
        this.sb = new StringBuilder();
    }
    
    public BMLBuilder addHeader(final String text) {
        return this.addHeader(text, null);
    }
    
    public BMLBuilder addHeader(final String text, @Nullable final Color color) {
        this.openBracket("header");
        if (color != null) {
            this.addColor("color", color);
        }
        this.sb.append("text=\"" + text + "\";");
        this.closeBracket();
        return this;
    }
    
    public BMLBuilder addText(final String text) {
        return this.addText(text, null, null, null);
    }
    
    public BMLBuilder addText(final String text, @Nullable final String hover, @Nullable final TextType type, @Nullable final Color color) {
        return this.addText(text, hover, type, color, 0, 0);
    }
    
    public BMLBuilder addText(final String text, @Nullable final String hover, @Nullable final TextType type, @Nullable final Color color, final int width, final int height) {
        this.openBracket("text");
        if (width > 0 || height > 0) {
            this.sb.append("size=\"" + width + "," + height + "\";");
        }
        if (type != null && type != TextType.NONE) {
            this.sb.append("type=\"" + type.typeString + "\";");
        }
        if (hover != null) {
            this.sb.append("hover=\"" + hover + "\";");
        }
        if (color != null) {
            this.addColor("color", color);
        }
        this.sb.append("text=\"" + text + "\";");
        this.closeBracket();
        return this;
    }
    
    public BMLBuilder addLabel(final String text) {
        return this.addLabel(text, null, null, null);
    }
    
    public BMLBuilder addLabel(final String text, @Nullable final String hover, @Nullable final TextType type, @Nullable final Color color) {
        return this.addLabel(text, hover, type, color, 0, 0);
    }
    
    public BMLBuilder addLabel(final String text, @Nullable final String hover, @Nullable final TextType type, @Nullable final Color color, final int width, final int height) {
        this.openBracket("label");
        if (width > 0 || height > 0) {
            this.sb.append("size=\"" + width + "," + height + "\";");
        }
        if (type != null && type != TextType.NONE) {
            this.sb.append("type=\"" + type.typeString + "\";");
        }
        if (hover != null) {
            this.sb.append("hover=\"" + hover + "\";");
        }
        if (color != null) {
            this.addColor("color", color);
        }
        this.sb.append("text=\"" + text + "\";");
        this.closeBracket();
        return this;
    }
    
    public BMLBuilder addInput(final String id, @Nullable final String text, final int maxChars, final int maxLines) {
        return this.addInput(id, null, true, text, maxChars, maxLines, null, null, null);
    }
    
    public BMLBuilder addInput(final String id, @Nullable final String onEnter, final boolean enabled, @Nullable final String text, final int maxChars, final int maxLines, @Nullable final Color color, @Nullable final Color bgColor, @Nullable final String bgTexture) {
        return this.addInput(id, onEnter, enabled, text, maxChars, maxLines, color, bgColor, bgTexture, 0, 0);
    }
    
    public BMLBuilder addInput(final String id, @Nullable final String onEnter, final boolean enabled, @Nullable final String text, final int maxChars, final int maxLines, @Nullable final Color color, @Nullable final Color bgColor, @Nullable final String bgTexture, final int width, final int height) {
        this.openBracket("input");
        if (width > 0 || height > 0) {
            this.sb.append("size=\"" + width + "," + height + "\";");
        }
        this.sb.append("id=\"" + id + "\";");
        if (onEnter != null) {
            this.sb.append("onenter=\"" + onEnter + "\";");
        }
        this.sb.append("enabled=\"" + enabled + "\";");
        if (text != null) {
            this.sb.append("text=\"" + text + "\";");
        }
        if (maxChars > 0) {
            this.sb.append("maxchars=\"" + maxChars + "\";");
        }
        if (maxLines > 0) {
            this.sb.append("maxlines=\"" + maxLines + "\";");
        }
        if (color != null) {
            this.addColor("color", color);
        }
        if (bgColor != null) {
            this.addColor("bgcolor", bgColor);
        }
        if (bgTexture != null) {
            this.sb.append("bgtexture=\"" + bgTexture + "\";");
        }
        this.closeBracket();
        return this;
    }
    
    public BMLBuilder addPassthrough(final String id, final String text) {
        this.openBracket("passthrough");
        this.sb.append("id=\"" + id + "\";");
        this.sb.append("text=\"" + text + "\";");
        this.closeBracket();
        return this;
    }
    
    public BMLBuilder addDropdown(final String id, final String defaultOption, final String... options) {
        return this.addDropdown(id, defaultOption, 0, 0, options);
    }
    
    public BMLBuilder addDropdown(final String id, final String defaultOption, final int width, final int height, final String... options) {
        this.openBracket("dropdown");
        if (width > 0 || height > 0) {
            this.sb.append("size=\"" + width + "," + height + "\";");
        }
        this.sb.append("id=\"" + id + "\";");
        this.sb.append("default=\"" + defaultOption + "\";");
        this.sb.append("options=\"");
        int count = 0;
        for (final String s : options) {
            this.sb.append(s);
            if (++count < options.length) {
                this.sb.append(",");
            }
        }
        this.sb.append("\";");
        this.closeBracket();
        return this;
    }
    
    public BMLBuilder addButton(final String id, final String text) {
        return this.addButton(id, text, null, null, null, false);
    }
    
    public BMLBuilder addButton(final String id, final String text, final boolean enabled) {
        return this.addButton(id, text, null, null, null, false, 0, 0, enabled);
    }
    
    public BMLBuilder addButton(final String id, final String text, final int width, final int height, final boolean enabled) {
        return this.addButton(id, text, null, null, null, false, width, height, enabled);
    }
    
    public BMLBuilder addButton(final String id, final String text, @Nullable final String confirmQuestion, @Nullable final String confirmString, @Nullable final String hover, final boolean isDefaultButton) {
        return this.addButton(id, text, confirmQuestion, confirmString, hover, isDefaultButton, 0, 0, true);
    }
    
    public BMLBuilder addButton(final String id, final String text, @Nullable final String confirmQuestion, @Nullable final String confirmString, @Nullable final String hover, final boolean isDefaultButton, final int width, final int height, final boolean enabled) {
        this.openBracket("button");
        if (width > 0 || height > 0) {
            this.sb.append("size=\"" + width + "," + height + "\";");
        }
        this.sb.append("id=\"" + id + "\";");
        this.sb.append("text=\"" + text + "\";");
        if (confirmQuestion != null) {
            this.sb.append("question=\"" + confirmQuestion + "\";");
        }
        if (confirmString != null) {
            this.sb.append("confirm=\"" + confirmString + "\";");
        }
        if (hover != null) {
            this.sb.append("hover=\"" + hover + "\";");
        }
        this.sb.append("default=\"" + isDefaultButton + "\";");
        this.sb.append("enabled=\"" + enabled + "\";");
        this.closeBracket();
        return this;
    }
    
    public BMLBuilder addRadioButton(final String id, final String group, final String text, final boolean selected) {
        return this.addRadioButton(id, group, text, null, selected, true, false);
    }
    
    public BMLBuilder addRadioButton(final String id, final String group, final String text, @Nullable final String hover, final boolean selected, final boolean enabled, final boolean hidden) {
        return this.addRadioButton(id, group, text, hover, selected, enabled, hidden, 0, 0);
    }
    
    public BMLBuilder addRadioButton(final String id, final String group, final String text, @Nullable final String hover, final boolean selected, final boolean enabled, final boolean hidden, final int width, final int height) {
        this.openBracket("radio");
        if (width > 0 || height > 0) {
            this.sb.append("size=\"" + width + "," + height + "\";");
        }
        this.sb.append("id=\"" + id + "\";");
        this.sb.append("group=\"" + group + "\";");
        this.sb.append("text=\"" + text + "\";");
        if (hover != null) {
            this.sb.append("hover=\"" + hover + "\";");
        }
        this.sb.append("selected=\"" + selected + "\";");
        this.sb.append("enabled=\"" + enabled + "\";");
        this.sb.append("hidden=\"" + hidden + "\";");
        this.closeBracket();
        return this;
    }
    
    public BMLBuilder addCheckbox(final String id, final String text, final boolean selected) {
        return this.addCheckbox(id, text, null, null, null, null, null, selected, true, null);
    }
    
    public BMLBuilder addCheckbox(final String id, final String text, @Nullable final String confirmQuestion, @Nullable final String confirmString, @Nullable final String unconfirmQuestion, @Nullable final String unconfirmString, @Nullable final String hover, final boolean selected, final boolean enabled, @Nullable final Color color) {
        return this.addCheckbox(id, text, confirmQuestion, confirmString, unconfirmQuestion, unconfirmString, hover, selected, enabled, color, 0, 0);
    }
    
    public BMLBuilder addCheckbox(final String id, final String text, @Nullable final String confirmQuestion, @Nullable final String confirmString, @Nullable final String unconfirmQuestion, @Nullable final String unconfirmString, @Nullable final String hover, final boolean selected, final boolean enabled, @Nullable final Color color, final int width, final int height) {
        this.openBracket("checkbox");
        if (width > 0 || height > 0) {
            this.sb.append("size=\"" + width + "," + height + "\";");
        }
        this.sb.append("id=\"" + id + "\";");
        this.sb.append("text=\"" + text + "\";");
        if (confirmQuestion != null) {
            this.sb.append("question=\"" + confirmQuestion + "\";");
        }
        if (confirmString != null) {
            this.sb.append("confirm=\"" + confirmString + "\";");
        }
        if (unconfirmQuestion != null) {
            this.sb.append("unquestion=\"" + unconfirmQuestion + "\";");
        }
        if (unconfirmString != null) {
            this.sb.append("unconfirm=\"" + unconfirmString + "\";");
        }
        if (hover != null) {
            this.sb.append("hover=\"" + hover + "\";");
        }
        this.sb.append("selected=\"" + selected + "\";");
        this.sb.append("enabled=\"" + enabled + "\";");
        if (color != null) {
            this.addColor("color", color);
        }
        this.closeBracket();
        return this;
    }
    
    public BMLBuilder addImage(final String imageSrc, final int width, final int height) {
        return this.addImage(imageSrc, null, width, height);
    }
    
    public BMLBuilder addImage(final String imageSrc, @Nullable final String hoverText, final int width, final int height) {
        this.openBracket("image");
        if (width > 0 || height > 0) {
            this.sb.append("size=\"" + width + "," + height + "\";");
        }
        this.sb.append("src=\"" + imageSrc + "\";");
        if (hoverText != null) {
            this.sb.append("text=\"" + hoverText + "\";");
        }
        this.closeBracket();
        return this;
    }
    
    public BMLBuilder addTreeListColumn(final String text) {
        return this.addTreeListColumn(text, 0);
    }
    
    public BMLBuilder addTreeListColumn(final String text, final int width) {
        this.openBracket("col");
        this.sb.append("text=\"" + text + "\";");
        if (width > 0) {
            this.sb.append("width=\"" + width + "\";");
        }
        this.closeBracket();
        return this;
    }
    
    public BMLBuilder addTreeListRow(final String id, final String name, final TreeListRowData... colData) {
        return this.addTreeListRow(id, name, null, 0, 0, colData);
    }
    
    public BMLBuilder addTreeListRow(final String id, final String name, @Nullable final String hover, final int rarity, final int children, final TreeListRowData... colData) {
        this.openBracket("row");
        this.sb.append("id=\"" + id + "\";");
        this.sb.append("name=\"" + name + "\";");
        if (hover != null) {
            this.sb.append("hover=\"" + hover + "\";");
        }
        this.sb.append("rarity=\"" + rarity + "\";");
        this.sb.append("children=\"" + children + "\";");
        for (final TreeListRowData col : colData) {
            this.openBracket("col");
            this.sb.append("text=\"" + col.text + "\";");
            if (col.id != null) {
                this.sb.append("id=\"" + col.id + "\";");
            }
            if (col.checkbox) {
                this.sb.append("checkbox=\"" + col.checkbox + "\";");
            }
            if (col.selected) {
                this.sb.append("selected=\"" + col.selected + "\";");
            }
            this.closeBracket();
        }
        this.closeBracket();
        return this;
    }
    
    public void addColor(final String colorType, @Nonnull final Color color) {
        this.sb.append(colorType + "=\"" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "\";");
    }
    
    public void openBracket(final String type) {
        this.sb.append(type);
        this.sb.append("{");
        ++this.openBrackets;
    }
    
    public void closeBracket() {
        this.sb.append("}");
        --this.openBrackets;
    }
    
    public BMLBuilder prependString(final String s) {
        this.sb.insert(0, s);
        return this;
    }
    
    public BMLBuilder addString(final String s) {
        this.sb.append(s);
        return this;
    }
    
    @Override
    public String toString() {
        for (int i = 0; i < this.openBrackets; ++i) {
            this.sb.append("}");
        }
        return this.sb.toString();
    }
    
    public BMLBuilder maybeAddSkipButton() {
        return this.maybeAddSkipButton(null, false);
    }
    
    public BMLBuilder maybeAddSkipButton(final Player p, final boolean close) {
        if (!close) {
            if (Servers.localServer.testServer || Server.getInstance().isPS()) {
                return this.addText("", null, null, null, 35, 0).addButton("skip", "Skip Stage", " ", "Are you sure you want to skip this stage of the tutorial?", null, false, 80, 20, true);
            }
        }
        else {
            if (p == null) {
                return this;
            }
            if (Servers.localServer.testServer || Servers.isThisAPvpServer() || p.hasFlag(42) || System.currentTimeMillis() - p.getSaveFile().creationDate > 14515200000L) {
                return this.addText("", null, null, null, 35, 0).addButton("close", "Close Tutorial", " ", "Are you sure you want to skip the tutorial?", null, false, 80, 20, true);
            }
        }
        return this;
    }
    
    public enum TextType
    {
        NONE(""), 
        BOLD("bold"), 
        ITALIC("italic"), 
        BOLDITALIC("bolditalic");
        
        private final String typeString;
        
        private TextType(final String typeString) {
            this.typeString = typeString;
        }
    }
    
    public static class TreeListRowData
    {
        private String id;
        private String text;
        private boolean checkbox;
        private boolean selected;
        
        public TreeListRowData(final String text) {
            this(null, text, false, false);
        }
        
        public TreeListRowData(final String id, final String text, final boolean checkbox, final boolean selected) {
            this.checkbox = false;
            this.selected = false;
            this.id = id;
            this.text = text;
            this.checkbox = checkbox;
            this.selected = selected;
        }
    }
}
