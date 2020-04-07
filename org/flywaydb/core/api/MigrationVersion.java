// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.api;

import java.util.ArrayList;
import java.math.BigInteger;
import java.util.List;
import java.util.regex.Pattern;

public final class MigrationVersion implements Comparable<MigrationVersion>
{
    public static final MigrationVersion EMPTY;
    public static final MigrationVersion LATEST;
    public static final MigrationVersion CURRENT;
    private static Pattern splitPattern;
    private final List<BigInteger> versionParts;
    private final String displayText;
    
    public static MigrationVersion fromVersion(final String version) {
        if ("current".equalsIgnoreCase(version)) {
            return MigrationVersion.CURRENT;
        }
        if (MigrationVersion.LATEST.getVersion().equals(version)) {
            return MigrationVersion.LATEST;
        }
        if (version == null) {
            return MigrationVersion.EMPTY;
        }
        return new MigrationVersion(version);
    }
    
    private MigrationVersion(final String version) {
        final String normalizedVersion = version.replace('_', '.');
        this.versionParts = this.tokenize(normalizedVersion);
        this.displayText = normalizedVersion;
    }
    
    private MigrationVersion(final BigInteger version, final String displayText) {
        (this.versionParts = new ArrayList<BigInteger>()).add(version);
        this.displayText = displayText;
    }
    
    @Override
    public String toString() {
        return this.displayText;
    }
    
    public String getVersion() {
        if (this.equals(MigrationVersion.EMPTY)) {
            return null;
        }
        if (this.equals(MigrationVersion.LATEST)) {
            return Long.toString(Long.MAX_VALUE);
        }
        return this.displayText;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final MigrationVersion version1 = (MigrationVersion)o;
        return this.compareTo(version1) == 0;
    }
    
    @Override
    public int hashCode() {
        return (this.versionParts == null) ? 0 : this.versionParts.hashCode();
    }
    
    @Override
    public int compareTo(final MigrationVersion o) {
        if (o == null) {
            return 1;
        }
        if (this == MigrationVersion.EMPTY) {
            return (o == MigrationVersion.EMPTY) ? 0 : Integer.MIN_VALUE;
        }
        if (this == MigrationVersion.CURRENT) {
            return (o == MigrationVersion.CURRENT) ? 0 : Integer.MIN_VALUE;
        }
        if (this == MigrationVersion.LATEST) {
            return (o == MigrationVersion.LATEST) ? 0 : Integer.MAX_VALUE;
        }
        if (o == MigrationVersion.EMPTY) {
            return Integer.MAX_VALUE;
        }
        if (o == MigrationVersion.CURRENT) {
            return Integer.MAX_VALUE;
        }
        if (o == MigrationVersion.LATEST) {
            return Integer.MIN_VALUE;
        }
        final List<BigInteger> elements1 = this.versionParts;
        final List<BigInteger> elements2 = o.versionParts;
        for (int largestNumberOfElements = Math.max(elements1.size(), elements2.size()), i = 0; i < largestNumberOfElements; ++i) {
            final int compared = this.getOrZero(elements1, i).compareTo(this.getOrZero(elements2, i));
            if (compared != 0) {
                return compared;
            }
        }
        return 0;
    }
    
    private BigInteger getOrZero(final List<BigInteger> elements, final int i) {
        return (i < elements.size()) ? elements.get(i) : BigInteger.ZERO;
    }
    
    private List<BigInteger> tokenize(final String str) {
        final List<BigInteger> numbers = new ArrayList<BigInteger>();
        for (final String number : MigrationVersion.splitPattern.split(str)) {
            try {
                numbers.add(new BigInteger(number));
            }
            catch (NumberFormatException e) {
                throw new FlywayException("Invalid version containing non-numeric characters. Only 0..9 and . are allowed. Invalid version: " + str);
            }
        }
        for (int i = numbers.size() - 1; i > 0 && numbers.get(i).equals(BigInteger.ZERO); --i) {
            numbers.remove(i);
        }
        return numbers;
    }
    
    static {
        EMPTY = new MigrationVersion(null, "<< Empty Schema >>");
        LATEST = new MigrationVersion(BigInteger.valueOf(-1L), "<< Latest Version >>");
        CURRENT = new MigrationVersion(BigInteger.valueOf(-2L), "<< Current Version >>");
        MigrationVersion.splitPattern = Pattern.compile("\\.(?=\\d)");
    }
}
