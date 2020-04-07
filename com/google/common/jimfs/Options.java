// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.nio.file.StandardCopyOption;
import com.google.common.collect.Lists;
import java.nio.file.CopyOption;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.nio.file.OpenOption;
import java.nio.file.LinkOption;
import com.google.common.collect.ImmutableSet;

final class Options
{
    public static final ImmutableSet<LinkOption> NOFOLLOW_LINKS;
    public static final ImmutableSet<LinkOption> FOLLOW_LINKS;
    private static final ImmutableSet<OpenOption> DEFAULT_READ;
    private static final ImmutableSet<OpenOption> DEFAULT_READ_NOFOLLOW_LINKS;
    private static final ImmutableSet<OpenOption> DEFAULT_WRITE;
    
    public static ImmutableSet<LinkOption> getLinkOptions(final LinkOption... options) {
        return (options.length == 0) ? Options.FOLLOW_LINKS : Options.NOFOLLOW_LINKS;
    }
    
    public static ImmutableSet<OpenOption> getOptionsForChannel(final Set<? extends OpenOption> options) {
        if (options.isEmpty()) {
            return Options.DEFAULT_READ;
        }
        final boolean append = options.contains(StandardOpenOption.APPEND);
        final boolean write = append || options.contains(StandardOpenOption.WRITE);
        final boolean read = !write || options.contains(StandardOpenOption.READ);
        if (read) {
            if (append) {
                throw new UnsupportedOperationException("'READ' + 'APPEND' not allowed");
            }
            if (!write) {
                return options.contains(LinkOption.NOFOLLOW_LINKS) ? Options.DEFAULT_READ_NOFOLLOW_LINKS : Options.DEFAULT_READ;
            }
        }
        if (options.contains(StandardOpenOption.WRITE)) {
            return ImmutableSet.copyOf((Collection<? extends OpenOption>)options);
        }
        return (ImmutableSet<OpenOption>)new ImmutableSet.Builder<StandardOpenOption>().add(StandardOpenOption.WRITE).addAll((Iterable<? extends StandardOpenOption>)options).build();
    }
    
    public static ImmutableSet<OpenOption> getOptionsForInputStream(final OpenOption... options) {
        boolean nofollowLinks = false;
        for (final OpenOption option : options) {
            if (Preconditions.checkNotNull(option) != StandardOpenOption.READ) {
                if (option != LinkOption.NOFOLLOW_LINKS) {
                    throw new UnsupportedOperationException("'" + option + "' not allowed");
                }
                nofollowLinks = true;
            }
        }
        return (ImmutableSet<OpenOption>)(nofollowLinks ? Options.NOFOLLOW_LINKS : Options.FOLLOW_LINKS);
    }
    
    public static ImmutableSet<OpenOption> getOptionsForOutputStream(final OpenOption... options) {
        if (options.length == 0) {
            return Options.DEFAULT_WRITE;
        }
        final ImmutableSet<OpenOption> result = ImmutableSet.copyOf(options);
        if (result.contains(StandardOpenOption.READ)) {
            throw new UnsupportedOperationException("'READ' not allowed");
        }
        return result;
    }
    
    public static ImmutableSet<CopyOption> getMoveOptions(final CopyOption... options) {
        return ImmutableSet.copyOf((Collection<? extends CopyOption>)Lists.asList(LinkOption.NOFOLLOW_LINKS, options));
    }
    
    public static ImmutableSet<CopyOption> getCopyOptions(final CopyOption... options) {
        final ImmutableSet<CopyOption> result = ImmutableSet.copyOf(options);
        if (result.contains(StandardCopyOption.ATOMIC_MOVE)) {
            throw new UnsupportedOperationException("'ATOMIC_MOVE' not allowed");
        }
        return result;
    }
    
    static {
        NOFOLLOW_LINKS = ImmutableSet.of(LinkOption.NOFOLLOW_LINKS);
        FOLLOW_LINKS = ImmutableSet.of();
        DEFAULT_READ = ImmutableSet.of(StandardOpenOption.READ);
        DEFAULT_READ_NOFOLLOW_LINKS = ImmutableSet.of(StandardOpenOption.READ, LinkOption.NOFOLLOW_LINKS);
        DEFAULT_WRITE = ImmutableSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
