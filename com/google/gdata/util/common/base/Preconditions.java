// 
// Decompiled by Procyon v0.5.30
// 

package com.google.gdata.util.common.base;

import java.util.Iterator;
import java.util.Collection;

public final class Preconditions
{
    public static void checkArgument(final boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }
    
    public static void checkArgument(final boolean expression, final Object errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }
    
    public static void checkArgument(final boolean expression, final String errorMessageTemplate, final Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalArgumentException(format(errorMessageTemplate, errorMessageArgs));
        }
    }
    
    public static void checkState(final boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }
    
    public static void checkState(final boolean expression, final Object errorMessage) {
        if (!expression) {
            throw new IllegalStateException(String.valueOf(errorMessage));
        }
    }
    
    public static void checkState(final boolean expression, final String errorMessageTemplate, final Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalStateException(format(errorMessageTemplate, errorMessageArgs));
        }
    }
    
    public static <T> T checkNotNull(final T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }
    
    public static <T> T checkNotNull(final T reference, final Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }
    
    public static <T> T checkNotNull(final T reference, final String errorMessageTemplate, final Object... errorMessageArgs) {
        if (reference == null) {
            throw new NullPointerException(format(errorMessageTemplate, errorMessageArgs));
        }
        return reference;
    }
    
    public static <T extends Iterable<?>> T checkContentsNotNull(final T iterable) {
        if (containsOrIsNull(iterable)) {
            throw new NullPointerException();
        }
        return iterable;
    }
    
    public static <T extends Iterable<?>> T checkContentsNotNull(final T iterable, final Object errorMessage) {
        if (containsOrIsNull(iterable)) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return iterable;
    }
    
    public static <T extends Iterable<?>> T checkContentsNotNull(final T iterable, final String errorMessageTemplate, final Object... errorMessageArgs) {
        if (containsOrIsNull(iterable)) {
            throw new NullPointerException(format(errorMessageTemplate, errorMessageArgs));
        }
        return iterable;
    }
    
    private static boolean containsOrIsNull(final Iterable<?> iterable) {
        if (iterable == null) {
            return true;
        }
        if (iterable instanceof Collection) {
            final Collection<?> collection = (Collection<?>)(Collection)iterable;
            try {
                return collection.contains(null);
            }
            catch (NullPointerException e) {
                return false;
            }
        }
        for (final Object element : iterable) {
            if (element == null) {
                return true;
            }
        }
        return false;
    }
    
    public static void checkElementIndex(final int index, final int size) {
        checkElementIndex(index, size, "index");
    }
    
    public static void checkElementIndex(final int index, final int size, final String desc) {
        checkArgument(size >= 0, "negative size: %s", size);
        if (index < 0) {
            throw new IndexOutOfBoundsException(format("%s (%s) must not be negative", desc, index));
        }
        if (index >= size) {
            throw new IndexOutOfBoundsException(format("%s (%s) must be less than size (%s)", desc, index, size));
        }
    }
    
    public static void checkPositionIndex(final int index, final int size) {
        checkPositionIndex(index, size, "index");
    }
    
    public static void checkPositionIndex(final int index, final int size, final String desc) {
        checkArgument(size >= 0, "negative size: %s", size);
        if (index < 0) {
            throw new IndexOutOfBoundsException(format("%s (%s) must not be negative", desc, index));
        }
        if (index > size) {
            throw new IndexOutOfBoundsException(format("%s (%s) must not be greater than size (%s)", desc, index, size));
        }
    }
    
    public static void checkPositionIndexes(final int start, final int end, final int size) {
        checkPositionIndex(start, size, "start index");
        checkPositionIndex(end, size, "end index");
        if (end < start) {
            throw new IndexOutOfBoundsException(format("end index (%s) must not be less than start index (%s)", end, start));
        }
    }
    
    static String format(final String template, final Object... args) {
        final StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
        int templateStart = 0;
        int i = 0;
        while (i < args.length) {
            final int placeholderStart = template.indexOf("%s", templateStart);
            if (placeholderStart == -1) {
                break;
            }
            builder.append(template.substring(templateStart, placeholderStart));
            builder.append(args[i++]);
            templateStart = placeholderStart + 2;
        }
        builder.append(template.substring(templateStart));
        if (i < args.length) {
            builder.append(" [");
            builder.append(args[i++]);
            while (i < args.length) {
                builder.append(", ");
                builder.append(args[i++]);
            }
            builder.append("]");
        }
        return builder.toString();
    }
}
