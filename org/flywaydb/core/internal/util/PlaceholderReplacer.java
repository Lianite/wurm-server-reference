// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util;

import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import org.flywaydb.core.api.FlywayException;
import java.util.Collection;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.Iterator;
import java.util.Map;

public class PlaceholderReplacer
{
    public static final PlaceholderReplacer NO_PLACEHOLDERS;
    private final Map<String, String> placeholders;
    private final String placeholderPrefix;
    private final String placeholderSuffix;
    
    public PlaceholderReplacer(final Map<String, String> placeholders, final String placeholderPrefix, final String placeholderSuffix) {
        this.placeholders = placeholders;
        this.placeholderPrefix = placeholderPrefix;
        this.placeholderSuffix = placeholderSuffix;
    }
    
    public String replacePlaceholders(final String input) {
        String noPlaceholders = input;
        for (final String placeholder : this.placeholders.keySet()) {
            final String searchTerm = this.placeholderPrefix + placeholder + this.placeholderSuffix;
            final String value = this.placeholders.get(placeholder);
            noPlaceholders = StringUtils.replaceAll(noPlaceholders, searchTerm, (value == null) ? "" : value);
        }
        this.checkForUnmatchedPlaceholderExpression(noPlaceholders);
        return noPlaceholders;
    }
    
    private void checkForUnmatchedPlaceholderExpression(final String input) {
        final String regex = Pattern.quote(this.placeholderPrefix) + "(.+?)" + Pattern.quote(this.placeholderSuffix);
        final Matcher matcher = Pattern.compile(regex).matcher(input);
        final Set<String> unmatchedPlaceHolderExpressions = new TreeSet<String>();
        while (matcher.find()) {
            unmatchedPlaceHolderExpressions.add(matcher.group());
        }
        if (!unmatchedPlaceHolderExpressions.isEmpty()) {
            throw new FlywayException("No value provided for placeholder expressions: " + StringUtils.collectionToCommaDelimitedString(unmatchedPlaceHolderExpressions) + ".  Check your configuration!");
        }
    }
    
    static {
        NO_PLACEHOLDERS = new PlaceholderReplacer(new HashMap(), "", "") {
            @Override
            public String replacePlaceholders(final String input) {
                return input;
            }
        };
    }
}
