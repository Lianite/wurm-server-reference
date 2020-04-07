// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.validation;

import javafx.scene.control.Control;

class SimpleValidationMessage implements ValidationMessage
{
    private final String text;
    private final Severity severity;
    private final Control target;
    
    public SimpleValidationMessage(final Control target, final String text, final Severity severity) {
        this.text = text;
        this.severity = ((severity == null) ? Severity.ERROR : severity);
        this.target = target;
    }
    
    @Override
    public Control getTarget() {
        return this.target;
    }
    
    @Override
    public String getText() {
        return this.text;
    }
    
    @Override
    public Severity getSeverity() {
        return this.severity;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.severity == null) ? 0 : this.severity.hashCode());
        result = 31 * result + ((this.target == null) ? 0 : this.target.hashCode());
        result = 31 * result + ((this.text == null) ? 0 : this.text.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final SimpleValidationMessage other = (SimpleValidationMessage)obj;
        if (this.severity != other.severity) {
            return false;
        }
        if (this.target == null) {
            if (other.target != null) {
                return false;
            }
        }
        else if (!this.target.equals(other.target)) {
            return false;
        }
        if (this.text == null) {
            if (other.text != null) {
                return false;
            }
        }
        else if (!this.text.equals(other.text)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("%s(%s)", this.severity, this.text);
    }
}
