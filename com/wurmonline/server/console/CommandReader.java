// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.console;

import java.io.IOException;
import java.util.logging.Level;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import com.wurmonline.server.Server;
import java.util.logging.Logger;

public class CommandReader implements Runnable
{
    private static final Logger logger;
    private final Server server;
    private final InputStream inputStream;
    private static final String SHUTDOWN = "shutdown";
    
    public CommandReader(final Server server, final InputStream inputStream) {
        this.server = server;
        this.inputStream = inputStream;
    }
    
    @Override
    public void run() {
        CommandReader.logger.info("Starting command reader for console input");
        final BufferedReader consoleReader = new BufferedReader(new InputStreamReader(this.inputStream));
        String nextLine;
        do {
            try {
                nextLine = consoleReader.readLine();
                if (nextLine == null) {
                    break;
                }
                if (nextLine.equals("shutdown")) {
                    this.server.shutDown();
                    break;
                }
                CommandReader.logger.warning("Unknown command: " + nextLine);
            }
            catch (IOException e) {
                CommandReader.logger.log(Level.SEVERE, "Can't read from console", e);
                nextLine = null;
            }
        } while (nextLine != null);
        CommandReader.logger.info("Console reader exiting.");
    }
    
    static {
        logger = Logger.getLogger(CommandReader.class.getName());
    }
}
