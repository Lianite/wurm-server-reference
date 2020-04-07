// 
// Decompiled by Procyon v0.5.30
// 

package javax.activation;

import java.util.Iterator;
import java.util.Map;
import java.util.Locale;
import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.io.File;
import com.sun.activation.registries.LogSupport;
import java.util.ArrayList;
import com.sun.activation.registries.MailcapFile;

public class MailcapCommandMap extends CommandMap
{
    private static MailcapFile defDB;
    private MailcapFile[] DB;
    private static final int PROG = 0;
    static /* synthetic */ Class class$javax$activation$MailcapCommandMap;
    
    public MailcapCommandMap() {
        final List dbv = new ArrayList(5);
        MailcapFile mf = null;
        dbv.add(null);
        LogSupport.log("MailcapCommandMap: load HOME");
        try {
            final String user_home = System.getProperty("user.home");
            if (user_home != null) {
                final String path = user_home + File.separator + ".mailcap";
                mf = this.loadFile(path);
                if (mf != null) {
                    dbv.add(mf);
                }
            }
        }
        catch (SecurityException ex) {}
        LogSupport.log("MailcapCommandMap: load SYS");
        try {
            final String system_mailcap = System.getProperty("java.home") + File.separator + "lib" + File.separator + "mailcap";
            mf = this.loadFile(system_mailcap);
            if (mf != null) {
                dbv.add(mf);
            }
        }
        catch (SecurityException ex2) {}
        LogSupport.log("MailcapCommandMap: load JAR");
        this.loadAllResources(dbv, "META-INF/mailcap");
        LogSupport.log("MailcapCommandMap: load DEF");
        Class class$;
        Class class$javax$activation$MailcapCommandMap;
        if (MailcapCommandMap.class$javax$activation$MailcapCommandMap == null) {
            class$javax$activation$MailcapCommandMap = (MailcapCommandMap.class$javax$activation$MailcapCommandMap = (class$ = class$("javax.activation.MailcapCommandMap")));
        }
        else {
            class$ = (class$javax$activation$MailcapCommandMap = MailcapCommandMap.class$javax$activation$MailcapCommandMap);
        }
        final Class clazz = class$javax$activation$MailcapCommandMap;
        synchronized (class$) {
            if (MailcapCommandMap.defDB == null) {
                MailcapCommandMap.defDB = this.loadResource("/META-INF/mailcap.default");
            }
        }
        if (MailcapCommandMap.defDB != null) {
            dbv.add(MailcapCommandMap.defDB);
        }
        this.DB = new MailcapFile[dbv.size()];
        this.DB = dbv.toArray(this.DB);
    }
    
    private MailcapFile loadResource(final String name) {
        InputStream clis = null;
        try {
            clis = SecuritySupport.getResourceAsStream(this.getClass(), name);
            if (clis != null) {
                final MailcapFile mf = new MailcapFile(clis);
                if (LogSupport.isLoggable()) {
                    LogSupport.log("MailcapCommandMap: successfully loaded mailcap file: " + name);
                }
                return mf;
            }
            if (LogSupport.isLoggable()) {
                LogSupport.log("MailcapCommandMap: not loading mailcap file: " + name);
            }
        }
        catch (IOException e) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("MailcapCommandMap: can't load " + name, e);
            }
        }
        catch (SecurityException sex) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("MailcapCommandMap: can't load " + name, sex);
            }
        }
        finally {
            try {
                if (clis != null) {
                    clis.close();
                }
            }
            catch (IOException ex) {}
        }
        return null;
    }
    
    private void loadAllResources(final List v, final String name) {
        boolean anyLoaded = false;
        try {
            ClassLoader cld = null;
            cld = SecuritySupport.getContextClassLoader();
            if (cld == null) {
                cld = this.getClass().getClassLoader();
            }
            URL[] urls;
            if (cld != null) {
                urls = SecuritySupport.getResources(cld, name);
            }
            else {
                urls = SecuritySupport.getSystemResources(name);
            }
            if (urls != null) {
                if (LogSupport.isLoggable()) {
                    LogSupport.log("MailcapCommandMap: getResources");
                }
                for (int i = 0; i < urls.length; ++i) {
                    final URL url = urls[i];
                    InputStream clis = null;
                    Label_0112: {
                        if (!LogSupport.isLoggable()) {
                            break Label_0112;
                        }
                        LogSupport.log("MailcapCommandMap: URL " + url);
                        try {
                            clis = SecuritySupport.openStream(url);
                            if (clis != null) {
                                v.add(new MailcapFile(clis));
                                anyLoaded = true;
                                if (LogSupport.isLoggable()) {
                                    LogSupport.log("MailcapCommandMap: successfully loaded mailcap file from URL: " + url);
                                }
                            }
                            else if (LogSupport.isLoggable()) {
                                LogSupport.log("MailcapCommandMap: not loading mailcap file from URL: " + url);
                            }
                        }
                        catch (IOException ioex) {
                            if (LogSupport.isLoggable()) {
                                LogSupport.log("MailcapCommandMap: can't load " + url, ioex);
                            }
                        }
                        catch (SecurityException sex) {
                            if (LogSupport.isLoggable()) {
                                LogSupport.log("MailcapCommandMap: can't load " + url, sex);
                            }
                        }
                        finally {
                            try {
                                if (clis != null) {
                                    clis.close();
                                }
                            }
                            catch (IOException ex2) {}
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("MailcapCommandMap: can't load " + name, ex);
            }
        }
        if (!anyLoaded) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("MailcapCommandMap: !anyLoaded");
            }
            final MailcapFile mf = this.loadResource("/" + name);
            if (mf != null) {
                v.add(mf);
            }
        }
    }
    
    private MailcapFile loadFile(final String name) {
        MailcapFile mtf = null;
        try {
            mtf = new MailcapFile(name);
        }
        catch (IOException ex) {}
        return mtf;
    }
    
    public MailcapCommandMap(final String fileName) throws IOException {
        this();
        if (LogSupport.isLoggable()) {
            LogSupport.log("MailcapCommandMap: load PROG from " + fileName);
        }
        if (this.DB[0] == null) {
            this.DB[0] = new MailcapFile(fileName);
        }
    }
    
    public MailcapCommandMap(final InputStream is) {
        this();
        LogSupport.log("MailcapCommandMap: load PROG");
        if (this.DB[0] == null) {
            try {
                this.DB[0] = new MailcapFile(is);
            }
            catch (IOException ex) {}
        }
    }
    
    public synchronized CommandInfo[] getPreferredCommands(String mimeType) {
        final List cmdList = new ArrayList();
        if (mimeType != null) {
            mimeType = mimeType.toLowerCase(Locale.ENGLISH);
        }
        for (int i = 0; i < this.DB.length; ++i) {
            if (this.DB[i] != null) {
                final Map cmdMap = this.DB[i].getMailcapList(mimeType);
                if (cmdMap != null) {
                    this.appendPrefCmdsToList(cmdMap, cmdList);
                }
            }
        }
        for (int i = 0; i < this.DB.length; ++i) {
            if (this.DB[i] != null) {
                final Map cmdMap = this.DB[i].getMailcapFallbackList(mimeType);
                if (cmdMap != null) {
                    this.appendPrefCmdsToList(cmdMap, cmdList);
                }
            }
        }
        CommandInfo[] cmdInfos = new CommandInfo[cmdList.size()];
        cmdInfos = cmdList.toArray(cmdInfos);
        return cmdInfos;
    }
    
    private void appendPrefCmdsToList(final Map cmdHash, final List cmdList) {
        for (final String verb : cmdHash.keySet()) {
            if (!this.checkForVerb(cmdList, verb)) {
                final List cmdList2 = cmdHash.get(verb);
                final String className = cmdList2.get(0);
                cmdList.add(new CommandInfo(verb, className));
            }
        }
    }
    
    private boolean checkForVerb(final List cmdList, final String verb) {
        final Iterator ee = cmdList.iterator();
        while (ee.hasNext()) {
            final String enum_verb = ee.next().getCommandName();
            if (enum_verb.equals(verb)) {
                return true;
            }
        }
        return false;
    }
    
    public synchronized CommandInfo[] getAllCommands(String mimeType) {
        final List cmdList = new ArrayList();
        if (mimeType != null) {
            mimeType = mimeType.toLowerCase(Locale.ENGLISH);
        }
        for (int i = 0; i < this.DB.length; ++i) {
            if (this.DB[i] != null) {
                final Map cmdMap = this.DB[i].getMailcapList(mimeType);
                if (cmdMap != null) {
                    this.appendCmdsToList(cmdMap, cmdList);
                }
            }
        }
        for (int i = 0; i < this.DB.length; ++i) {
            if (this.DB[i] != null) {
                final Map cmdMap = this.DB[i].getMailcapFallbackList(mimeType);
                if (cmdMap != null) {
                    this.appendCmdsToList(cmdMap, cmdList);
                }
            }
        }
        CommandInfo[] cmdInfos = new CommandInfo[cmdList.size()];
        cmdInfos = cmdList.toArray(cmdInfos);
        return cmdInfos;
    }
    
    private void appendCmdsToList(final Map typeHash, final List cmdList) {
        for (final String verb : typeHash.keySet()) {
            final List cmdList2 = typeHash.get(verb);
            for (final String cmd : cmdList2) {
                cmdList.add(new CommandInfo(verb, cmd));
            }
        }
    }
    
    public synchronized CommandInfo getCommand(String mimeType, final String cmdName) {
        if (mimeType != null) {
            mimeType = mimeType.toLowerCase(Locale.ENGLISH);
        }
        for (int i = 0; i < this.DB.length; ++i) {
            if (this.DB[i] != null) {
                final Map cmdMap = this.DB[i].getMailcapList(mimeType);
                if (cmdMap != null) {
                    final List v = cmdMap.get(cmdName);
                    if (v != null) {
                        final String cmdClassName = v.get(0);
                        if (cmdClassName != null) {
                            return new CommandInfo(cmdName, cmdClassName);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < this.DB.length; ++i) {
            if (this.DB[i] != null) {
                final Map cmdMap = this.DB[i].getMailcapFallbackList(mimeType);
                if (cmdMap != null) {
                    final List v = cmdMap.get(cmdName);
                    if (v != null) {
                        final String cmdClassName = v.get(0);
                        if (cmdClassName != null) {
                            return new CommandInfo(cmdName, cmdClassName);
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public synchronized void addMailcap(final String mail_cap) {
        LogSupport.log("MailcapCommandMap: add to PROG");
        if (this.DB[0] == null) {
            this.DB[0] = new MailcapFile();
        }
        this.DB[0].appendToMailcap(mail_cap);
    }
    
    public synchronized DataContentHandler createDataContentHandler(String mimeType) {
        if (LogSupport.isLoggable()) {
            LogSupport.log("MailcapCommandMap: createDataContentHandler for " + mimeType);
        }
        if (mimeType != null) {
            mimeType = mimeType.toLowerCase(Locale.ENGLISH);
        }
        for (int i = 0; i < this.DB.length; ++i) {
            if (this.DB[i] != null) {
                if (LogSupport.isLoggable()) {
                    LogSupport.log("  search DB #" + i);
                }
                final Map cmdMap = this.DB[i].getMailcapList(mimeType);
                if (cmdMap != null) {
                    final List v = cmdMap.get("content-handler");
                    if (v != null) {
                        final String name = v.get(0);
                        final DataContentHandler dch = this.getDataContentHandler(name);
                        if (dch != null) {
                            return dch;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < this.DB.length; ++i) {
            if (this.DB[i] != null) {
                if (LogSupport.isLoggable()) {
                    LogSupport.log("  search fallback DB #" + i);
                }
                final Map cmdMap = this.DB[i].getMailcapFallbackList(mimeType);
                if (cmdMap != null) {
                    final List v = cmdMap.get("content-handler");
                    if (v != null) {
                        final String name = v.get(0);
                        final DataContentHandler dch = this.getDataContentHandler(name);
                        if (dch != null) {
                            return dch;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private DataContentHandler getDataContentHandler(final String name) {
        if (LogSupport.isLoggable()) {
            LogSupport.log("    got content-handler");
        }
        if (LogSupport.isLoggable()) {
            LogSupport.log("      class " + name);
        }
        try {
            ClassLoader cld = null;
            cld = SecuritySupport.getContextClassLoader();
            if (cld == null) {
                cld = this.getClass().getClassLoader();
            }
            Class cl = null;
            try {
                cl = cld.loadClass(name);
            }
            catch (Exception ex) {
                cl = Class.forName(name);
            }
            if (cl != null) {
                return cl.newInstance();
            }
        }
        catch (IllegalAccessException e) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("Can't load DCH " + name, e);
            }
        }
        catch (ClassNotFoundException e2) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("Can't load DCH " + name, e2);
            }
        }
        catch (InstantiationException e3) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("Can't load DCH " + name, e3);
            }
        }
        return null;
    }
    
    public synchronized String[] getMimeTypes() {
        final List mtList = new ArrayList();
        for (int i = 0; i < this.DB.length; ++i) {
            if (this.DB[i] != null) {
                final String[] ts = this.DB[i].getMimeTypes();
                if (ts != null) {
                    for (int j = 0; j < ts.length; ++j) {
                        if (!mtList.contains(ts[j])) {
                            mtList.add(ts[j]);
                        }
                    }
                }
            }
        }
        String[] mts = new String[mtList.size()];
        mts = mtList.toArray(mts);
        return mts;
    }
    
    public synchronized String[] getNativeCommands(String mimeType) {
        final List cmdList = new ArrayList();
        if (mimeType != null) {
            mimeType = mimeType.toLowerCase(Locale.ENGLISH);
        }
        for (int i = 0; i < this.DB.length; ++i) {
            if (this.DB[i] != null) {
                final String[] cmds = this.DB[i].getNativeCommands(mimeType);
                if (cmds != null) {
                    for (int j = 0; j < cmds.length; ++j) {
                        if (!cmdList.contains(cmds[j])) {
                            cmdList.add(cmds[j]);
                        }
                    }
                }
            }
        }
        String[] cmds2 = new String[cmdList.size()];
        cmds2 = cmdList.toArray(cmds2);
        return cmds2;
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError().initCause(x);
        }
    }
    
    static {
        MailcapCommandMap.defDB = null;
    }
}
