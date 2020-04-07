// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.nio.file.PathMatcher;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.net.URI;
import com.google.common.collect.ComparisonChain;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;
import javax.annotation.Nullable;
import com.google.common.annotations.VisibleForTesting;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.nio.file.FileSystem;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import java.util.Comparator;

final class PathService implements Comparator<JimfsPath>
{
    private static final Ordering<Name> DISPLAY_ROOT_ORDERING;
    private static final Ordering<Iterable<Name>> DISPLAY_NAMES_ORDERING;
    private static final Ordering<Name> CANONICAL_ROOT_ORDERING;
    private static final Ordering<Iterable<Name>> CANONICAL_NAMES_ORDERING;
    private final PathType type;
    private final ImmutableSet<PathNormalization> displayNormalizations;
    private final ImmutableSet<PathNormalization> canonicalNormalizations;
    private final boolean equalityUsesCanonicalForm;
    private final Ordering<Name> rootOrdering;
    private final Ordering<Iterable<Name>> namesOrdering;
    private volatile FileSystem fileSystem;
    private volatile JimfsPath emptyPath;
    private static final Predicate<Object> NOT_EMPTY;
    
    PathService(final Configuration config) {
        this(config.pathType, config.nameDisplayNormalization, config.nameCanonicalNormalization, config.pathEqualityUsesCanonicalForm);
    }
    
    PathService(final PathType type, final Iterable<PathNormalization> displayNormalizations, final Iterable<PathNormalization> canonicalNormalizations, final boolean equalityUsesCanonicalForm) {
        this.type = Preconditions.checkNotNull(type);
        this.displayNormalizations = ImmutableSet.copyOf((Iterable<? extends PathNormalization>)displayNormalizations);
        this.canonicalNormalizations = ImmutableSet.copyOf((Iterable<? extends PathNormalization>)canonicalNormalizations);
        this.equalityUsesCanonicalForm = equalityUsesCanonicalForm;
        this.rootOrdering = (equalityUsesCanonicalForm ? PathService.CANONICAL_ROOT_ORDERING : PathService.DISPLAY_ROOT_ORDERING);
        this.namesOrdering = (equalityUsesCanonicalForm ? PathService.CANONICAL_NAMES_ORDERING : PathService.DISPLAY_NAMES_ORDERING);
    }
    
    public void setFileSystem(final FileSystem fileSystem) {
        Preconditions.checkState(this.fileSystem == null, (Object)"may not set fileSystem twice");
        this.fileSystem = Preconditions.checkNotNull(fileSystem);
    }
    
    public FileSystem getFileSystem() {
        return this.fileSystem;
    }
    
    public String getSeparator() {
        return this.type.getSeparator();
    }
    
    public JimfsPath emptyPath() {
        JimfsPath result = this.emptyPath;
        if (result == null) {
            result = this.createPathInternal(null, ImmutableList.of(Name.EMPTY));
            return this.emptyPath = result;
        }
        return result;
    }
    
    public Name name(final String name) {
        switch (name) {
            case "": {
                return Name.EMPTY;
            }
            case ".": {
                return Name.SELF;
            }
            case "..": {
                return Name.PARENT;
            }
            default: {
                final String display = PathNormalization.normalize(name, this.displayNormalizations);
                final String canonical = PathNormalization.normalize(name, this.canonicalNormalizations);
                return Name.create(display, canonical);
            }
        }
    }
    
    @VisibleForTesting
    List<Name> names(final Iterable<String> names) {
        final List<Name> result = new ArrayList<Name>();
        for (final String name : names) {
            result.add(this.name(name));
        }
        return result;
    }
    
    public JimfsPath createRoot(final Name root) {
        return this.createPath(Preconditions.checkNotNull(root), (Iterable<Name>)ImmutableList.of());
    }
    
    public JimfsPath createFileName(final Name name) {
        return this.createPath(null, ImmutableList.of(name));
    }
    
    public JimfsPath createRelativePath(final Iterable<Name> names) {
        return this.createPath(null, (Iterable<Name>)ImmutableList.copyOf((Iterable<?>)names));
    }
    
    public JimfsPath createPath(@Nullable final Name root, final Iterable<Name> names) {
        final ImmutableList<Name> nameList = ImmutableList.copyOf((Iterable<? extends Name>)Iterables.filter((Iterable<? extends E>)names, PathService.NOT_EMPTY));
        if (root == null && nameList.isEmpty()) {
            return this.emptyPath();
        }
        return this.createPathInternal(root, nameList);
    }
    
    protected final JimfsPath createPathInternal(@Nullable final Name root, final Iterable<Name> names) {
        return new JimfsPath(this, root, names);
    }
    
    public JimfsPath parsePath(final String first, final String... more) {
        final String joined = this.type.joiner().join(Iterables.filter(Lists.asList(first, more), PathService.NOT_EMPTY));
        return this.toPath(this.type.parsePath(joined));
    }
    
    private JimfsPath toPath(final PathType.ParseResult parsed) {
        final Name root = (parsed.root() == null) ? null : this.name(parsed.root());
        final Iterable<Name> names = this.names(parsed.names());
        return this.createPath(root, names);
    }
    
    public String toString(final JimfsPath path) {
        final Name root = path.root();
        final String rootString = (root == null) ? null : root.toString();
        final Iterable<String> names = Iterables.transform((Iterable<Name>)path.names(), (Function<? super Name, ? extends String>)Functions.toStringFunction());
        return this.type.toString(rootString, names);
    }
    
    public int hash(final JimfsPath path) {
        int hash = 31;
        hash = 31 * hash + this.getFileSystem().hashCode();
        final Name root = path.root();
        final ImmutableList<Name> names = path.names();
        if (this.equalityUsesCanonicalForm) {
            hash = 31 * hash + ((root == null) ? 0 : root.hashCode());
            for (final Name name : names) {
                hash = 31 * hash + name.hashCode();
            }
        }
        else {
            hash = 31 * hash + ((root == null) ? 0 : root.toString().hashCode());
            for (final Name name : names) {
                hash = 31 * hash + name.toString().hashCode();
            }
        }
        return hash;
    }
    
    @Override
    public int compare(final JimfsPath a, final JimfsPath b) {
        return ComparisonChain.start().compare(a.root(), b.root(), this.rootOrdering).compare(a.names(), b.names(), this.namesOrdering).result();
    }
    
    public URI toUri(final URI fileSystemUri, final JimfsPath path) {
        Preconditions.checkArgument(path.isAbsolute(), "path (%s) must be absolute", path);
        final String root = String.valueOf(path.root());
        final Iterable<String> names = Iterables.transform((Iterable<Name>)path.names(), (Function<? super Name, ? extends String>)Functions.toStringFunction());
        return this.type.toUri(fileSystemUri, root, names, Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS));
    }
    
    public JimfsPath fromUri(final URI uri) {
        return this.toPath(this.type.fromUri(uri));
    }
    
    public PathMatcher createPathMatcher(final String syntaxAndPattern) {
        return PathMatchers.getPathMatcher(syntaxAndPattern, this.type.getSeparator() + this.type.getOtherSeparators(), this.displayNormalizations);
    }
    
    static {
        DISPLAY_ROOT_ORDERING = Name.displayOrdering().nullsLast();
        DISPLAY_NAMES_ORDERING = Name.displayOrdering().lexicographical();
        CANONICAL_ROOT_ORDERING = Name.canonicalOrdering().nullsLast();
        CANONICAL_NAMES_ORDERING = Name.canonicalOrdering().lexicographical();
        NOT_EMPTY = new Predicate<Object>() {
            @Override
            public boolean apply(final Object input) {
                return !input.toString().isEmpty();
            }
        };
    }
}
