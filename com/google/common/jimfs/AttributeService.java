// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import com.google.common.base.Preconditions;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import javax.annotation.Nullable;
import java.nio.file.attribute.FileAttributeView;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.google.common.base.Splitter;
import java.nio.file.attribute.FileAttribute;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

final class AttributeService
{
    private static final String ALL_ATTRIBUTES = "*";
    private final ImmutableMap<String, AttributeProvider> providersByName;
    private final ImmutableMap<Class<?>, AttributeProvider> providersByViewType;
    private final ImmutableMap<Class<?>, AttributeProvider> providersByAttributesType;
    private final ImmutableList<FileAttribute<?>> defaultValues;
    private static final Splitter ATTRIBUTE_SPLITTER;
    
    public AttributeService(final Configuration configuration) {
        this(getProviders(configuration), configuration.defaultAttributeValues);
    }
    
    public AttributeService(final Iterable<? extends AttributeProvider> providers, final Map<String, ?> userProvidedDefaults) {
        final ImmutableMap.Builder<String, AttributeProvider> byViewNameBuilder = ImmutableMap.builder();
        final ImmutableMap.Builder<Class<?>, AttributeProvider> byViewTypeBuilder = ImmutableMap.builder();
        final ImmutableMap.Builder<Class<?>, AttributeProvider> byAttributesTypeBuilder = ImmutableMap.builder();
        final ImmutableList.Builder<FileAttribute<?>> defaultAttributesBuilder = ImmutableList.builder();
        for (final AttributeProvider provider : providers) {
            byViewNameBuilder.put(provider.name(), provider);
            byViewTypeBuilder.put(provider.viewType(), provider);
            if (provider.attributesType() != null) {
                byAttributesTypeBuilder.put(provider.attributesType(), provider);
            }
            for (final Map.Entry<String, ?> entry : provider.defaultValues(userProvidedDefaults).entrySet()) {
                defaultAttributesBuilder.add(new SimpleFileAttribute<Object>(entry.getKey(), entry.getValue()));
            }
        }
        this.providersByName = byViewNameBuilder.build();
        this.providersByViewType = byViewTypeBuilder.build();
        this.providersByAttributesType = byAttributesTypeBuilder.build();
        this.defaultValues = defaultAttributesBuilder.build();
    }
    
    private static Iterable<AttributeProvider> getProviders(final Configuration configuration) {
        final Map<String, AttributeProvider> result = new HashMap<String, AttributeProvider>();
        for (final AttributeProvider provider : configuration.attributeProviders) {
            result.put(provider.name(), provider);
        }
        for (final String view : configuration.attributeViews) {
            addStandardProvider(result, view);
        }
        addMissingProviders(result);
        return (Iterable<AttributeProvider>)Collections.unmodifiableCollection((Collection<?>)result.values());
    }
    
    private static void addMissingProviders(final Map<String, AttributeProvider> providers) {
        final Set<String> missingViews = new HashSet<String>();
        for (final AttributeProvider provider : providers.values()) {
            for (final String inheritedView : provider.inherits()) {
                if (!providers.containsKey(inheritedView)) {
                    missingViews.add(inheritedView);
                }
            }
        }
        if (missingViews.isEmpty()) {
            return;
        }
        for (final String view : missingViews) {
            addStandardProvider(providers, view);
        }
        addMissingProviders(providers);
    }
    
    private static void addStandardProvider(final Map<String, AttributeProvider> result, final String view) {
        final AttributeProvider provider = StandardAttributeProviders.get(view);
        if (provider == null) {
            if (!result.containsKey(view)) {
                throw new IllegalStateException("no provider found for attribute view '" + view + "'");
            }
        }
        else {
            result.put(provider.name(), provider);
        }
    }
    
    public ImmutableSet<String> supportedFileAttributeViews() {
        return this.providersByName.keySet();
    }
    
    public boolean supportsFileAttributeView(final Class<? extends FileAttributeView> type) {
        return this.providersByViewType.containsKey(type);
    }
    
    public void setInitialAttributes(final File file, final FileAttribute<?>... attrs) {
        for (int i = 0; i < this.defaultValues.size(); ++i) {
            final FileAttribute<?> attribute = this.defaultValues.get(i);
            final int separatorIndex = attribute.name().indexOf(58);
            final String view = attribute.name().substring(0, separatorIndex);
            final String attr = attribute.name().substring(separatorIndex + 1);
            file.setAttribute(view, attr, attribute.value());
        }
        for (final FileAttribute<?> attr2 : attrs) {
            this.setAttribute(file, attr2.name(), attr2.value(), true);
        }
    }
    
    public void copyAttributes(final File file, final File copy, final AttributeCopyOption copyOption) {
        switch (copyOption) {
            case ALL: {
                file.copyAttributes(copy);
                break;
            }
            case BASIC: {
                file.copyBasicAttributes(copy);
                break;
            }
        }
    }
    
    public Object getAttribute(final File file, final String attribute) {
        final String view = getViewName(attribute);
        final String attr = getSingleAttribute(attribute);
        return this.getAttribute(file, view, attr);
    }
    
    public Object getAttribute(final File file, final String view, final String attribute) {
        final Object value = this.getAttributeInternal(file, view, attribute);
        if (value == null) {
            throw new IllegalArgumentException("invalid attribute for view '" + view + "': " + attribute);
        }
        return value;
    }
    
    @Nullable
    private Object getAttributeInternal(final File file, final String view, final String attribute) {
        final AttributeProvider provider = this.providersByName.get(view);
        if (provider == null) {
            return null;
        }
        Object value = provider.get(file, attribute);
        if (value == null) {
            for (final String inheritedView : provider.inherits()) {
                value = this.getAttributeInternal(file, inheritedView, attribute);
                if (value != null) {
                    break;
                }
            }
        }
        return value;
    }
    
    public void setAttribute(final File file, final String attribute, final Object value, final boolean create) {
        final String view = getViewName(attribute);
        final String attr = getSingleAttribute(attribute);
        this.setAttributeInternal(file, view, attr, value, create);
    }
    
    private void setAttributeInternal(final File file, final String view, final String attribute, final Object value, final boolean create) {
        final AttributeProvider provider = this.providersByName.get(view);
        if (provider != null) {
            if (provider.supports(attribute)) {
                provider.set(file, view, attribute, value, create);
                return;
            }
            for (final String inheritedView : provider.inherits()) {
                final AttributeProvider inheritedProvider = this.providersByName.get(inheritedView);
                if (inheritedProvider.supports(attribute)) {
                    inheritedProvider.set(file, view, attribute, value, create);
                    return;
                }
            }
        }
        throw new IllegalArgumentException("cannot set attribute '" + view + ":" + attribute + "'");
    }
    
    @Nullable
    public <V extends FileAttributeView> V getFileAttributeView(final FileLookup lookup, final Class<V> type) {
        final AttributeProvider provider = this.providersByViewType.get(type);
        if (provider != null) {
            return (V)provider.view(lookup, this.createInheritedViews(lookup, provider));
        }
        return null;
    }
    
    private ImmutableMap<String, FileAttributeView> createInheritedViews(final FileLookup lookup, final AttributeProvider provider) {
        if (provider.inherits().isEmpty()) {
            return ImmutableMap.of();
        }
        final Map<String, FileAttributeView> inheritedViews = new HashMap<String, FileAttributeView>();
        this.createInheritedViews(lookup, provider, inheritedViews);
        return ImmutableMap.copyOf((Map<? extends String, ? extends FileAttributeView>)inheritedViews);
    }
    
    private void createInheritedViews(final FileLookup lookup, final AttributeProvider provider, final Map<String, FileAttributeView> inheritedViews) {
        for (final String inherited : provider.inherits()) {
            if (!inheritedViews.containsKey(inherited)) {
                final AttributeProvider inheritedProvider = this.providersByName.get(inherited);
                final FileAttributeView inheritedView = this.getFileAttributeView(lookup, inheritedProvider.viewType(), inheritedViews);
                inheritedViews.put(inherited, inheritedView);
            }
        }
    }
    
    private FileAttributeView getFileAttributeView(final FileLookup lookup, final Class<? extends FileAttributeView> viewType, final Map<String, FileAttributeView> inheritedViews) {
        final AttributeProvider provider = this.providersByViewType.get(viewType);
        this.createInheritedViews(lookup, provider, inheritedViews);
        return provider.view(lookup, ImmutableMap.copyOf((Map<? extends String, ? extends FileAttributeView>)inheritedViews));
    }
    
    public ImmutableMap<String, Object> readAttributes(final File file, final String attributes) {
        final String view = getViewName(attributes);
        final List<String> attrs = getAttributeNames(attributes);
        if (attrs.size() > 1 && attrs.contains("*")) {
            throw new IllegalArgumentException("invalid attributes: " + attributes);
        }
        final Map<String, Object> result = new HashMap<String, Object>();
        if (attrs.size() == 1 && attrs.contains("*")) {
            final AttributeProvider provider = this.providersByName.get(view);
            readAll(file, provider, result);
            for (final String inheritedView : provider.inherits()) {
                final AttributeProvider inheritedProvider = this.providersByName.get(inheritedView);
                readAll(file, inheritedProvider, result);
            }
        }
        else {
            for (final String attr : attrs) {
                result.put(attr, this.getAttribute(file, view, attr));
            }
        }
        return ImmutableMap.copyOf((Map<? extends String, ?>)result);
    }
    
    private static void readAll(final File file, final AttributeProvider provider, final Map<String, Object> map) {
        for (final String attribute : provider.attributes(file)) {
            final Object value = provider.get(file, attribute);
            if (value != null) {
                map.put(attribute, value);
            }
        }
    }
    
    public <A extends BasicFileAttributes> A readAttributes(final File file, final Class<A> type) {
        final AttributeProvider provider = this.providersByAttributesType.get(type);
        if (provider != null) {
            return (A)provider.readAttributes(file);
        }
        throw new UnsupportedOperationException("unsupported attributes type: " + type);
    }
    
    private static String getViewName(final String attribute) {
        final int separatorIndex = attribute.indexOf(58);
        if (separatorIndex == -1) {
            return "basic";
        }
        if (separatorIndex == 0 || separatorIndex == attribute.length() - 1 || attribute.indexOf(58, separatorIndex + 1) != -1) {
            throw new IllegalArgumentException("illegal attribute format: " + attribute);
        }
        return attribute.substring(0, separatorIndex);
    }
    
    private static ImmutableList<String> getAttributeNames(final String attributes) {
        final int separatorIndex = attributes.indexOf(58);
        final String attributesPart = attributes.substring(separatorIndex + 1);
        return ImmutableList.copyOf((Iterable<? extends String>)AttributeService.ATTRIBUTE_SPLITTER.split(attributesPart));
    }
    
    private static String getSingleAttribute(final String attribute) {
        final ImmutableList<String> attributeNames = getAttributeNames(attribute);
        if (attributeNames.size() != 1 || "*".equals(attributeNames.get(0))) {
            throw new IllegalArgumentException("must specify a single attribute: " + attribute);
        }
        return attributeNames.get(0);
    }
    
    static {
        ATTRIBUTE_SPLITTER = Splitter.on(',');
    }
    
    private static final class SimpleFileAttribute<T> implements FileAttribute<T>
    {
        private final String name;
        private final T value;
        
        SimpleFileAttribute(final String name, final T value) {
            this.name = Preconditions.checkNotNull(name);
            this.value = Preconditions.checkNotNull(value);
        }
        
        @Override
        public String name() {
            return this.name;
        }
        
        @Override
        public T value() {
            return this.value;
        }
    }
}
