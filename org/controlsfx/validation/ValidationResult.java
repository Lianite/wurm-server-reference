// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.validation;

import java.util.Collections;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.control.Control;
import java.util.ArrayList;
import java.util.List;

public class ValidationResult
{
    private List<ValidationMessage> errors;
    private List<ValidationMessage> warnings;
    
    public ValidationResult() {
        this.errors = new ArrayList<ValidationMessage>();
        this.warnings = new ArrayList<ValidationMessage>();
    }
    
    public static final ValidationResult fromMessageIf(final Control target, final String text, final Severity severity, final boolean condition) {
        return new ValidationResult().addMessageIf(target, text, severity, condition);
    }
    
    public static final ValidationResult fromErrorIf(final Control target, final String text, final boolean condition) {
        return new ValidationResult().addErrorIf(target, text, condition);
    }
    
    public static final ValidationResult fromWarningIf(final Control target, final String text, final boolean condition) {
        return new ValidationResult().addWarningIf(target, text, condition);
    }
    
    public static final ValidationResult fromError(final Control target, final String text) {
        return fromMessages(ValidationMessage.error(target, text));
    }
    
    public static final ValidationResult fromWarning(final Control target, final String text) {
        return fromMessages(ValidationMessage.warning(target, text));
    }
    
    public static final ValidationResult fromMessages(final ValidationMessage... messages) {
        return new ValidationResult().addAll(messages);
    }
    
    public static final ValidationResult fromMessages(final Collection<? extends ValidationMessage> messages) {
        return new ValidationResult().addAll(messages);
    }
    
    public static final ValidationResult fromResults(final ValidationResult... results) {
        return new ValidationResult().combineAll(results);
    }
    
    public static final ValidationResult fromResults(final Collection<ValidationResult> results) {
        return new ValidationResult().combineAll(results);
    }
    
    public ValidationResult copy() {
        return fromMessages(this.getMessages());
    }
    
    public ValidationResult add(final ValidationMessage message) {
        if (message != null) {
            switch (message.getSeverity()) {
                case ERROR: {
                    this.errors.add(message);
                    break;
                }
                case WARNING: {
                    this.warnings.add(message);
                    break;
                }
            }
        }
        return this;
    }
    
    public ValidationResult addMessageIf(final Control target, final String text, final Severity severity, final boolean condition) {
        return condition ? this.add(new SimpleValidationMessage(target, text, severity)) : this;
    }
    
    public ValidationResult addErrorIf(final Control target, final String text, final boolean condition) {
        return this.addMessageIf(target, text, Severity.ERROR, condition);
    }
    
    public ValidationResult addWarningIf(final Control target, final String text, final boolean condition) {
        return this.addMessageIf(target, text, Severity.WARNING, condition);
    }
    
    public ValidationResult addAll(final Collection<? extends ValidationMessage> messages) {
        messages.stream().forEach(msg -> this.add(msg));
        return this;
    }
    
    public ValidationResult addAll(final ValidationMessage... messages) {
        return this.addAll(Arrays.asList(messages));
    }
    
    public ValidationResult combine(final ValidationResult validationResult) {
        return (validationResult == null) ? this.copy() : this.copy().addAll(validationResult.getMessages());
    }
    
    public ValidationResult combineAll(final Collection<ValidationResult> validationResults) {
        return validationResults.stream().reduce(this.copy(), (x, r) -> (r == null) ? x : x.addAll(r.getMessages()));
    }
    
    public ValidationResult combineAll(final ValidationResult... validationResults) {
        return this.combineAll(Arrays.asList(validationResults));
    }
    
    public Collection<ValidationMessage> getErrors() {
        return (Collection<ValidationMessage>)Collections.unmodifiableList((List<?>)this.errors);
    }
    
    public Collection<ValidationMessage> getWarnings() {
        return (Collection<ValidationMessage>)Collections.unmodifiableList((List<?>)this.warnings);
    }
    
    public Collection<ValidationMessage> getMessages() {
        final List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
        messages.addAll(this.errors);
        messages.addAll(this.warnings);
        return (Collection<ValidationMessage>)Collections.unmodifiableList((List<?>)messages);
    }
}
