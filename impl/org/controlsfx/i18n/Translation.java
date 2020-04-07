// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.i18n;

import java.nio.file.Path;
import java.util.Locale;

public class Translation implements Comparable<Translation>
{
    private final String localeString;
    private final Locale locale;
    private final Path path;
    
    public Translation(final String locale, final Path path) {
        this.localeString = locale;
        this.path = path;
        final String[] split = this.localeString.split("_");
        if (split.length == 1) {
            this.locale = new Locale(this.localeString);
        }
        else if (split.length == 2) {
            this.locale = new Locale(split[0], split[1]);
        }
        else {
            if (split.length != 3) {
                throw new IllegalArgumentException("Unknown locale string '" + locale + "'");
            }
            this.locale = new Locale(split[0], split[1], split[2]);
        }
    }
    
    public final String getLocaleString() {
        return this.localeString;
    }
    
    public final Locale getLocale() {
        return this.locale;
    }
    
    public final Path getPath() {
        return this.path;
    }
    
    @Override
    public String toString() {
        return this.localeString;
    }
    
    @Override
    public int compareTo(final Translation o) {
        if (o == null) {
            return 1;
        }
        return this.localeString.compareTo(o.localeString);
    }
}
