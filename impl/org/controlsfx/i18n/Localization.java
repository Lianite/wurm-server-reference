// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.i18n;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Locale;

public class Localization
{
    public static final String KEY_PREFIX = "@@";
    private static final String LOCALE_BUNDLE_NAME = "controlsfx";
    private static Locale locale;
    private static Locale resourceBundleLocale;
    private static ResourceBundle resourceBundle;
    
    public static final Locale getLocale() {
        return (Localization.locale == null) ? Locale.getDefault() : Localization.locale;
    }
    
    public static final void setLocale(final Locale newLocale) {
        Localization.locale = newLocale;
    }
    
    private static final synchronized ResourceBundle getLocaleBundle() {
        final Locale currentLocale = getLocale();
        if (!currentLocale.equals(Localization.resourceBundleLocale)) {
            Localization.resourceBundleLocale = currentLocale;
            Localization.resourceBundle = ResourceBundle.getBundle("controlsfx", Localization.resourceBundleLocale, Localization.class.getClassLoader());
        }
        return Localization.resourceBundle;
    }
    
    public static final String getString(final String key) {
        try {
            return getLocaleBundle().getString(key);
        }
        catch (MissingResourceException ex) {
            return String.format("<%s>", key);
        }
    }
    
    public static final String asKey(final String text) {
        return "@@" + text;
    }
    
    public static final boolean isKey(final String text) {
        return text != null && text.startsWith("@@");
    }
    
    public static String localize(final String text) {
        return isKey(text) ? getString(text.substring("@@".length()).trim()) : text;
    }
    
    static {
        Localization.locale = null;
        Localization.resourceBundleLocale = null;
        Localization.resourceBundle = null;
    }
}
