// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.validation;

import javafx.scene.control.Control;
import java.util.Comparator;

public interface ValidationMessage extends Comparable<ValidationMessage>
{
    public static final Comparator<ValidationMessage> COMPARATOR = new Comparator<ValidationMessage>() {
        @Override
        public int compare(final ValidationMessage vm1, final ValidationMessage vm2) {
            if (vm1 == vm2) {
                return 0;
            }
            if (vm1 == null) {
                return 1;
            }
            if (vm2 == null) {
                return -1;
            }
            return vm1.compareTo(vm2);
        }
    };
    
    String getText();
    
    Severity getSeverity();
    
    Control getTarget();
    
    default ValidationMessage error(final Control target, final String text) {
        return new SimpleValidationMessage(target, text, Severity.ERROR);
    }
    
    default ValidationMessage warning(final Control target, final String text) {
        return new SimpleValidationMessage(target, text, Severity.WARNING);
    }
    
    default int compareTo(final ValidationMessage msg) {
        return (msg == null || this.getTarget() != msg.getTarget()) ? -1 : this.getSeverity().compareTo(msg.getSeverity());
    }
}
