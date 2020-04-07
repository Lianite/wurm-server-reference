// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.validation;

import java.util.regex.Pattern;
import java.util.function.Predicate;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.scene.control.Control;
import java.util.function.BiFunction;

public interface Validator<T> extends BiFunction<Control, T, ValidationResult>
{
    @SafeVarargs
    default <T> Validator<T> combine(final Validator<T>... validators) {
        return (Validator<T>)((control, value) -> Stream.of(validators).map(validator -> validator.apply(control, value)).collect((Collector<? super Object, ?, ValidationResult>)Collectors.reducing(new ValidationResult(), ValidationResult::combine)));
    }
    
    default <T> Validator<T> createEmptyValidator(final String message, final Severity severity) {
        final boolean condition;
        return (Validator<T>)((c, value) -> {
            condition = ((value instanceof String) ? value.toString().trim().isEmpty() : (value == null));
            return ValidationResult.fromMessageIf(c, message, severity, condition);
        });
    }
    
    default <T> Validator<T> createEmptyValidator(final String message) {
        return createEmptyValidator(message, Severity.ERROR);
    }
    
    default <T> Validator<T> createEqualsValidator(final String message, final Severity severity, final Collection<T> values) {
        return (Validator<T>)((c, value) -> ValidationResult.fromMessageIf(c, message, severity, !values.contains(value)));
    }
    
    default <T> Validator<T> createEqualsValidator(final String message, final Collection<T> values) {
        return createEqualsValidator(message, Severity.ERROR, values);
    }
    
    default <T> Validator<T> createPredicateValidator(final Predicate<T> predicate, final String message) {
        return createPredicateValidator(predicate, message, Severity.ERROR);
    }
    
    default <T> Validator<T> createPredicateValidator(final Predicate<T> predicate, final String message, final Severity severity) {
        return (Validator<T>)((control, value) -> ValidationResult.fromMessageIf(control, message, severity, !predicate.test(value)));
    }
    
    default Validator<String> createRegexValidator(final String message, final String regex, final Severity severity) {
        final boolean condition;
        return (Validator<String>)((c, value) -> {
            condition = (value == null || !Pattern.matches(regex, value));
            return ValidationResult.fromMessageIf(c, message, severity, condition);
        });
    }
    
    default Validator<String> createRegexValidator(final String message, final Pattern regex, final Severity severity) {
        final boolean condition;
        return (Validator<String>)((c, value) -> {
            condition = (value == null || !regex.matcher(value).matches());
            return ValidationResult.fromMessageIf(c, message, severity, condition);
        });
    }
}
