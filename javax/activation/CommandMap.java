// 
// Decompiled by Procyon v0.5.30
// 

package javax.activation;

public abstract class CommandMap
{
    private static CommandMap defaultCommandMap;
    static /* synthetic */ Class class$javax$activation$CommandMap;
    
    public static CommandMap getDefaultCommandMap() {
        if (CommandMap.defaultCommandMap == null) {
            CommandMap.defaultCommandMap = new MailcapCommandMap();
        }
        return CommandMap.defaultCommandMap;
    }
    
    public static void setDefaultCommandMap(final CommandMap commandMap) {
        final SecurityManager security = System.getSecurityManager();
        if (security != null) {
            try {
                security.checkSetFactory();
            }
            catch (SecurityException ex) {
                if (((CommandMap.class$javax$activation$CommandMap == null) ? (CommandMap.class$javax$activation$CommandMap = class$("javax.activation.CommandMap")) : CommandMap.class$javax$activation$CommandMap).getClassLoader() != commandMap.getClass().getClassLoader()) {
                    throw ex;
                }
            }
        }
        CommandMap.defaultCommandMap = commandMap;
    }
    
    public abstract CommandInfo[] getPreferredCommands(final String p0);
    
    public CommandInfo[] getPreferredCommands(final String mimeType, final DataSource ds) {
        return this.getPreferredCommands(mimeType);
    }
    
    public abstract CommandInfo[] getAllCommands(final String p0);
    
    public CommandInfo[] getAllCommands(final String mimeType, final DataSource ds) {
        return this.getAllCommands(mimeType);
    }
    
    public abstract CommandInfo getCommand(final String p0, final String p1);
    
    public CommandInfo getCommand(final String mimeType, final String cmdName, final DataSource ds) {
        return this.getCommand(mimeType, cmdName);
    }
    
    public abstract DataContentHandler createDataContentHandler(final String p0);
    
    public DataContentHandler createDataContentHandler(final String mimeType, final DataSource ds) {
        return this.createDataContentHandler(mimeType);
    }
    
    public String[] getMimeTypes() {
        return null;
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
        CommandMap.defaultCommandMap = null;
    }
}
