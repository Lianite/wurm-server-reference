// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.util.Arrays;
import javax.annotation.Nullable;
import java.util.Iterator;
import com.google.common.collect.Sets;
import java.util.List;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.HashSet;
import com.google.common.base.Preconditions;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.Map;
import java.util.Collection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public final class Configuration
{
    final PathType pathType;
    final ImmutableSet<PathNormalization> nameDisplayNormalization;
    final ImmutableSet<PathNormalization> nameCanonicalNormalization;
    final boolean pathEqualityUsesCanonicalForm;
    final int blockSize;
    final long maxSize;
    final long maxCacheSize;
    final ImmutableSet<String> attributeViews;
    final ImmutableSet<AttributeProvider> attributeProviders;
    final ImmutableMap<String, Object> defaultAttributeValues;
    final WatchServiceConfiguration watchServiceConfig;
    final ImmutableSet<String> roots;
    final String workingDirectory;
    final ImmutableSet<Feature> supportedFeatures;
    
    public static Configuration unix() {
        return UnixHolder.UNIX;
    }
    
    public static Configuration osX() {
        return OsxHolder.OS_X;
    }
    
    public static Configuration windows() {
        return WindowsHolder.WINDOWS;
    }
    
    public static Configuration forCurrentPlatform() {
        final String os = System.getProperty("os.name");
        if (os.contains("Windows")) {
            return windows();
        }
        if (os.contains("OS X")) {
            return osX();
        }
        return unix();
    }
    
    public static Builder builder(final PathType pathType) {
        return new Builder(pathType);
    }
    
    private Configuration(final Builder builder) {
        this.pathType = builder.pathType;
        this.nameDisplayNormalization = builder.nameDisplayNormalization;
        this.nameCanonicalNormalization = builder.nameCanonicalNormalization;
        this.pathEqualityUsesCanonicalForm = builder.pathEqualityUsesCanonicalForm;
        this.blockSize = builder.blockSize;
        this.maxSize = builder.maxSize;
        this.maxCacheSize = builder.maxCacheSize;
        this.attributeViews = builder.attributeViews;
        this.attributeProviders = ((builder.attributeProviders == null) ? ImmutableSet.of() : ImmutableSet.copyOf((Collection<? extends AttributeProvider>)builder.attributeProviders));
        this.defaultAttributeValues = ((builder.defaultAttributeValues == null) ? ImmutableMap.of() : ImmutableMap.copyOf((Map<? extends String, ?>)builder.defaultAttributeValues));
        this.watchServiceConfig = builder.watchServiceConfig;
        this.roots = builder.roots;
        this.workingDirectory = builder.workingDirectory;
        this.supportedFeatures = builder.supportedFeatures;
    }
    
    public Builder toBuilder() {
        return new Builder(this);
    }
    
    private static final class UnixHolder
    {
        private static final Configuration UNIX;
        
        static {
            UNIX = Configuration.builder(PathType.unix()).setRoots("/", new String[0]).setWorkingDirectory("/work").setAttributeViews("basic", new String[0]).setSupportedFeatures(Feature.LINKS, Feature.SYMBOLIC_LINKS, Feature.SECURE_DIRECTORY_STREAM, Feature.FILE_CHANNEL).build();
        }
    }
    
    private static final class OsxHolder
    {
        private static final Configuration OS_X;
        
        static {
            OS_X = Configuration.unix().toBuilder().setNameDisplayNormalization(PathNormalization.NFC, new PathNormalization[0]).setNameCanonicalNormalization(PathNormalization.NFD, PathNormalization.CASE_FOLD_ASCII).setSupportedFeatures(Feature.LINKS, Feature.SYMBOLIC_LINKS, Feature.FILE_CHANNEL).build();
        }
    }
    
    private static final class WindowsHolder
    {
        private static final Configuration WINDOWS;
        
        static {
            WINDOWS = Configuration.builder(PathType.windows()).setRoots("C:\\", new String[0]).setWorkingDirectory("C:\\work").setNameCanonicalNormalization(PathNormalization.CASE_FOLD_ASCII, new PathNormalization[0]).setPathEqualityUsesCanonicalForm(true).setAttributeViews("basic", new String[0]).setSupportedFeatures(Feature.LINKS, Feature.SYMBOLIC_LINKS, Feature.FILE_CHANNEL).build();
        }
    }
    
    public static final class Builder
    {
        public static final int DEFAULT_BLOCK_SIZE = 8192;
        public static final long DEFAULT_MAX_SIZE = 4294967296L;
        public static final long DEFAULT_MAX_CACHE_SIZE = -1L;
        private final PathType pathType;
        private ImmutableSet<PathNormalization> nameDisplayNormalization;
        private ImmutableSet<PathNormalization> nameCanonicalNormalization;
        private boolean pathEqualityUsesCanonicalForm;
        private int blockSize;
        private long maxSize;
        private long maxCacheSize;
        private ImmutableSet<String> attributeViews;
        private Set<AttributeProvider> attributeProviders;
        private Map<String, Object> defaultAttributeValues;
        private WatchServiceConfiguration watchServiceConfig;
        private ImmutableSet<String> roots;
        private String workingDirectory;
        private ImmutableSet<Feature> supportedFeatures;
        private static final Pattern ATTRIBUTE_PATTERN;
        
        private Builder(final PathType pathType) {
            this.nameDisplayNormalization = ImmutableSet.of();
            this.nameCanonicalNormalization = ImmutableSet.of();
            this.pathEqualityUsesCanonicalForm = false;
            this.blockSize = 8192;
            this.maxSize = 4294967296L;
            this.maxCacheSize = -1L;
            this.attributeViews = ImmutableSet.of();
            this.attributeProviders = null;
            this.watchServiceConfig = WatchServiceConfiguration.DEFAULT;
            this.roots = ImmutableSet.of();
            this.supportedFeatures = ImmutableSet.of();
            this.pathType = Preconditions.checkNotNull(pathType);
        }
        
        private Builder(final Configuration configuration) {
            this.nameDisplayNormalization = ImmutableSet.of();
            this.nameCanonicalNormalization = ImmutableSet.of();
            this.pathEqualityUsesCanonicalForm = false;
            this.blockSize = 8192;
            this.maxSize = 4294967296L;
            this.maxCacheSize = -1L;
            this.attributeViews = ImmutableSet.of();
            this.attributeProviders = null;
            this.watchServiceConfig = WatchServiceConfiguration.DEFAULT;
            this.roots = ImmutableSet.of();
            this.supportedFeatures = ImmutableSet.of();
            this.pathType = configuration.pathType;
            this.nameDisplayNormalization = configuration.nameDisplayNormalization;
            this.nameCanonicalNormalization = configuration.nameCanonicalNormalization;
            this.pathEqualityUsesCanonicalForm = configuration.pathEqualityUsesCanonicalForm;
            this.blockSize = configuration.blockSize;
            this.maxSize = configuration.maxSize;
            this.maxCacheSize = configuration.maxCacheSize;
            this.attributeViews = configuration.attributeViews;
            this.attributeProviders = (configuration.attributeProviders.isEmpty() ? null : new HashSet<AttributeProvider>(configuration.attributeProviders));
            this.defaultAttributeValues = (configuration.defaultAttributeValues.isEmpty() ? null : new HashMap<String, Object>(configuration.defaultAttributeValues));
            this.watchServiceConfig = configuration.watchServiceConfig;
            this.roots = configuration.roots;
            this.workingDirectory = configuration.workingDirectory;
            this.supportedFeatures = configuration.supportedFeatures;
        }
        
        public Builder setNameDisplayNormalization(final PathNormalization first, final PathNormalization... more) {
            this.nameDisplayNormalization = this.checkNormalizations(Lists.asList(first, more));
            return this;
        }
        
        public Builder setNameCanonicalNormalization(final PathNormalization first, final PathNormalization... more) {
            this.nameCanonicalNormalization = this.checkNormalizations(Lists.asList(first, more));
            return this;
        }
        
        private ImmutableSet<PathNormalization> checkNormalizations(final List<PathNormalization> normalizations) {
            PathNormalization none = null;
            PathNormalization normalization = null;
            PathNormalization caseFold = null;
            for (final PathNormalization n : normalizations) {
                Preconditions.checkNotNull(n);
                checkNormalizationNotSet(n, none);
                switch (n) {
                    case NONE: {
                        none = n;
                        continue;
                    }
                    case NFC:
                    case NFD: {
                        checkNormalizationNotSet(n, normalization);
                        normalization = n;
                        continue;
                    }
                    case CASE_FOLD_UNICODE:
                    case CASE_FOLD_ASCII: {
                        checkNormalizationNotSet(n, caseFold);
                        caseFold = n;
                        continue;
                    }
                    default: {
                        throw new AssertionError();
                    }
                }
            }
            if (none != null) {
                return ImmutableSet.of();
            }
            return Sets.immutableEnumSet((Iterable<PathNormalization>)normalizations);
        }
        
        private static void checkNormalizationNotSet(final PathNormalization n, @Nullable final PathNormalization set) {
            if (set != null) {
                throw new IllegalArgumentException("can't set normalization " + n + ": normalization " + set + " already set");
            }
        }
        
        public Builder setPathEqualityUsesCanonicalForm(final boolean useCanonicalForm) {
            this.pathEqualityUsesCanonicalForm = useCanonicalForm;
            return this;
        }
        
        public Builder setBlockSize(final int blockSize) {
            Preconditions.checkArgument(blockSize > 0, "blockSize (%s) must be positive", blockSize);
            this.blockSize = blockSize;
            return this;
        }
        
        public Builder setMaxSize(final long maxSize) {
            Preconditions.checkArgument(maxSize > 0L, "maxSize (%s) must be positive", maxSize);
            this.maxSize = maxSize;
            return this;
        }
        
        public Builder setMaxCacheSize(final long maxCacheSize) {
            Preconditions.checkArgument(maxCacheSize >= 0L, "maxCacheSize (%s) may not be negative", maxCacheSize);
            this.maxCacheSize = maxCacheSize;
            return this;
        }
        
        public Builder setAttributeViews(final String first, final String... more) {
            this.attributeViews = ImmutableSet.copyOf((Collection<? extends String>)Lists.asList(first, more));
            return this;
        }
        
        public Builder addAttributeProvider(final AttributeProvider provider) {
            Preconditions.checkNotNull(provider);
            if (this.attributeProviders == null) {
                this.attributeProviders = new HashSet<AttributeProvider>();
            }
            this.attributeProviders.add(provider);
            return this;
        }
        
        public Builder setDefaultAttributeValue(final String attribute, final Object value) {
            Preconditions.checkArgument(Builder.ATTRIBUTE_PATTERN.matcher(attribute).matches(), "attribute (%s) must be of the form \"view:attribute\"", attribute);
            Preconditions.checkNotNull(value);
            if (this.defaultAttributeValues == null) {
                this.defaultAttributeValues = new HashMap<String, Object>();
            }
            this.defaultAttributeValues.put(attribute, value);
            return this;
        }
        
        public Builder setRoots(final String first, final String... more) {
            final List<String> roots = Lists.asList(first, more);
            for (final String root : roots) {
                final PathType.ParseResult parseResult = this.pathType.parsePath(root);
                Preconditions.checkArgument(parseResult.isRoot(), "invalid root: %s", root);
            }
            this.roots = ImmutableSet.copyOf((Collection<? extends String>)roots);
            return this;
        }
        
        public Builder setWorkingDirectory(final String workingDirectory) {
            final PathType.ParseResult parseResult = this.pathType.parsePath(workingDirectory);
            Preconditions.checkArgument(parseResult.isAbsolute(), "working directory must be an absolute path: %s", workingDirectory);
            this.workingDirectory = Preconditions.checkNotNull(workingDirectory);
            return this;
        }
        
        public Builder setSupportedFeatures(final Feature... features) {
            this.supportedFeatures = Sets.immutableEnumSet((Iterable<Feature>)Arrays.asList(features));
            return this;
        }
        
        public Builder setWatchServiceConfiguration(final WatchServiceConfiguration config) {
            this.watchServiceConfig = Preconditions.checkNotNull(config);
            return this;
        }
        
        public Configuration build() {
            return new Configuration(this, null);
        }
        
        static {
            ATTRIBUTE_PATTERN = Pattern.compile("[^:]+:[^:]+");
        }
    }
}
