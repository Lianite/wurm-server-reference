// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.gui.propertysheet;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import org.controlsfx.property.editor.PropertyEditor;
import java.util.Optional;
import java.util.logging.Level;
import com.wurmonline.server.Server;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.collections.FXCollections;
import java.util.HashSet;
import java.util.Set;
import org.controlsfx.control.PropertySheet;
import javafx.collections.ObservableList;
import com.wurmonline.server.gui.PlayerData;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;
import javafx.scene.layout.VBox;

public class PlayerPropertySheet extends VBox implements MiscConstants
{
    private static final Logger logger;
    private PlayerData current;
    private final ObservableList<PropertySheet.Item> list;
    private Set<PropertyType> changedProperties;
    
    public PlayerPropertySheet(final PlayerData entry) {
        this.changedProperties = new HashSet<PropertyType>();
        this.current = entry;
        (this.list = (ObservableList<PropertySheet.Item>)FXCollections.observableArrayList()).add((Object)new CustomPropertyItem(PropertyType.NAME, "Name", "Player Name", "Name", true, entry.getName()));
        this.list.add((Object)new CustomPropertyItem(PropertyType.POSX, "Position X", "Position in X", "The X position of the player", true, entry.getPosx()));
        this.list.add((Object)new CustomPropertyItem(PropertyType.POSY, "Position Y", "Position in Y", "The Y position of the player", true, entry.getPosy()));
        this.list.add((Object)new CustomPropertyItem(PropertyType.POWER, "Power", "Player Game Management Power", "Power from 0 to 5. 2 is Game Manager, 4 is Head GM and 5 Implementor", true, entry.getPower()));
        this.list.add((Object)new CustomPropertyItem(PropertyType.CURRENTSERVER, "Current server", "Server id of the player", "The id of the server that the player is on", true, entry.getServer()));
        this.list.add((Object)new CustomPropertyItem(PropertyType.UNDEAD, "Undead", "Whether the player is undead", "Lets the player play as undead", true, entry.isUndead()));
        final PropertySheet propertySheet = new PropertySheet(this.list);
        VBox.setVgrow((Node)propertySheet, Priority.ALWAYS);
        this.getChildren().add((Object)propertySheet);
    }
    
    public PlayerData getCurrentData() {
        return this.current;
    }
    
    public final String save() {
        String toReturn = "";
        boolean saveAtAll = false;
        for (final CustomPropertyItem item : (CustomPropertyItem[])this.list.toArray((Object[])new CustomPropertyItem[this.list.size()])) {
            if (this.changedProperties.contains(item.getPropertyType())) {
                saveAtAll = true;
                try {
                    switch (item.getPropertyType()) {
                        case NAME: {
                            this.current.setName(item.getValue().toString());
                            break;
                        }
                        case POSX: {
                            this.current.setPosx((float)item.getValue());
                            break;
                        }
                        case POSY: {
                            this.current.setPosy((float)item.getValue());
                            break;
                        }
                        case POWER: {
                            this.current.setPower((int)item.getValue());
                            break;
                        }
                        case CURRENTSERVER: {
                            this.current.setServer((int)item.getValue());
                            break;
                        }
                        case UNDEAD: {
                            if (!this.current.isUndead()) {
                                this.current.setUndeadType((byte)(1 + Server.rand.nextInt(3)));
                                break;
                            }
                            this.current.setUndeadType((byte)0);
                            break;
                        }
                    }
                }
                catch (Exception ex) {
                    saveAtAll = false;
                    toReturn = toReturn + "Invalid value " + item.getCategory() + ": " + item.getValue() + ". ";
                    PlayerPropertySheet.logger.log(Level.INFO, "Error " + ex.getMessage(), ex);
                }
            }
        }
        if (toReturn.length() == 0 && saveAtAll) {
            try {
                this.current.save();
                toReturn = "ok";
            }
            catch (Exception ex2) {
                toReturn = ex2.getMessage();
            }
        }
        return toReturn;
    }
    
    static {
        logger = Logger.getLogger(PlayerPropertySheet.class.getName());
    }
    
    private enum PropertyType
    {
        NAME, 
        POSX, 
        POSY, 
        POWER, 
        CURRENTSERVER, 
        UNDEAD;
    }
    
    class CustomPropertyItem implements PropertySheet.Item
    {
        private PropertyType type;
        private String category;
        private String name;
        private String description;
        private boolean editable;
        private Object value;
        
        CustomPropertyItem(final PropertyType aType, final String aCategory, final String aName, final String aDescription, final boolean aEditable, final Object aValue) {
            this.editable = true;
            this.type = aType;
            this.category = aCategory;
            this.name = aName;
            this.description = aDescription;
            this.editable = aEditable;
            this.value = aValue;
        }
        
        public PropertyType getPropertyType() {
            return this.type;
        }
        
        @Override
        public Class<?> getType() {
            return this.value.getClass();
        }
        
        @Override
        public String getCategory() {
            return this.category;
        }
        
        @Override
        public String getName() {
            return this.name;
        }
        
        @Override
        public String getDescription() {
            return this.description;
        }
        
        @Override
        public Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
            return super.getPropertyEditorClass();
        }
        
        @Override
        public boolean isEditable() {
            return this.editable;
        }
        
        @Override
        public Object getValue() {
            return this.value;
        }
        
        @Override
        public void setValue(final Object aValue) {
            if (!this.value.equals(aValue)) {
                PlayerPropertySheet.this.changedProperties.add(this.type);
            }
            this.value = aValue;
        }
        
        @Override
        public Optional<ObservableValue<?>> getObservableValue() {
            return Optional.of((ObservableValue<?>)new SimpleObjectProperty(this.value));
        }
    }
}
