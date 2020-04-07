// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.action;

import org.controlsfx.glyphfont.Glyph;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.Node;
import javafx.scene.input.KeyCombination;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class DefaultActionFactory implements AnnotatedActionFactory
{
    @Override
    public AnnotatedAction createAction(final ActionProxy annotation, final Method method, final Object target) {
        AnnotatedAction action;
        if (method.isAnnotationPresent(ActionCheck.class)) {
            action = new AnnotatedCheckAction(annotation.text(), method, target);
        }
        else {
            action = new AnnotatedAction(annotation.text(), method, target);
        }
        this.configureAction(annotation, action);
        return action;
    }
    
    protected void configureAction(final ActionProxy annotation, final AnnotatedAction action) {
        final Node graphic = this.resolveGraphic(annotation);
        action.setGraphic(graphic);
        final String longText = annotation.longText().trim();
        if (graphic != null) {
            action.setLongText(longText);
        }
        final String acceleratorText = annotation.accelerator().trim();
        if (!acceleratorText.isEmpty()) {
            action.setAccelerator(KeyCombination.keyCombination(acceleratorText));
        }
    }
    
    protected Node resolveGraphic(final ActionProxy annotation) {
        final String graphicDef = annotation.graphic().trim();
        if (graphicDef.isEmpty()) {
            return null;
        }
        final String[] def = graphicDef.split("\\>");
        if (def.length == 1) {
            return (Node)new ImageView(new Image(def[0]));
        }
        final String s = def[0];
        switch (s) {
            case "font": {
                return (Node)Glyph.create(def[1]);
            }
            case "image": {
                return (Node)new ImageView(new Image(def[1]));
            }
            default: {
                throw new IllegalArgumentException(String.format("Unknown ActionProxy graphic protocol: %s", def[0]));
            }
        }
    }
}
