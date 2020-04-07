// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.i18n;

import java.nio.file.FileSystem;
import java.util.Collections;
import java.nio.file.FileSystems;
import java.io.File;
import java.util.ArrayList;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Locale;
import java.util.Optional;
import java.util.Iterator;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryIteratorException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Translations
{
    private static List<Translation> translations;
    
    private static void loadFrom(final Path rootPath) {
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(rootPath)) {
            for (final Path path : stream) {
                final String filename = path.getFileName().toString();
                if (!filename.startsWith("controlsfx") && !filename.endsWith(".properties")) {
                    continue;
                }
                if ("controlsfx.properties".equals(filename)) {
                    Translations.translations.add(new Translation("en", path));
                }
                else {
                    if (!filename.contains("_")) {
                        throw new IllegalStateException("Unknown translation file '" + path + "'.");
                    }
                    final String locale = filename.substring(11, filename.indexOf(".properties"));
                    Translations.translations.add(new Translation(locale, path));
                }
            }
        }
        catch (IOException ex) {}
        catch (DirectoryIteratorException ex2) {}
    }
    
    public static Optional<Translation> getTranslation(final String localeString) {
        for (final Translation t : Translations.translations) {
            if (localeString.equals(t.getLocaleString())) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }
    
    public static List<Translation> getAllTranslations() {
        return Translations.translations;
    }
    
    public static List<Locale> getAllTranslationLocales() {
        return Translations.translations.stream().map(t -> t.getLocale()).collect((Collector<? super Object, ?, List<Locale>>)Collectors.toList());
    }
    
    static {
        Translations.translations = new ArrayList<Translation>();
        final File file = new File(Translations.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        if (file.getName().endsWith(".jar")) {
            final Path jarFile = file.toPath();
            try (final FileSystem fs = FileSystems.newFileSystem(jarFile, null)) {
                fs.getRootDirectories().forEach(path -> loadFrom(path));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (Translations.translations.isEmpty()) {
            final Path srcDir = new File("src/main/resources").toPath();
            loadFrom(srcDir);
        }
        if (Translations.translations.isEmpty()) {
            final Path binDir = new File("bin").toPath();
            loadFrom(binDir);
        }
        if (Translations.translations.isEmpty() && file.getAbsolutePath().endsWith("controlsfx" + File.separator + "bin")) {
            loadFrom(file.toPath());
        }
        Collections.sort(Translations.translations);
    }
}
