// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.swing.logging;

import java.util.logging.Level;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class LogCategory
{
    private String name;
    private List<Group> groups;
    
    public LogCategory(final String name) {
        this.groups = new ArrayList<Group>();
        this.name = name;
    }
    
    public LogCategory(final String name, final Group[] groups) {
        this.groups = new ArrayList<Group>();
        this.name = name;
        this.groups = Arrays.asList(groups);
    }
    
    public String getName() {
        return this.name;
    }
    
    public List<Group> getGroups() {
        return this.groups;
    }
    
    public void addGroup(final String name, final LoggerLevel[] loggerLevels) {
        this.groups.add(new Group(name, loggerLevels));
    }
    
    public static class Group
    {
        private String name;
        private List<LoggerLevel> loggerLevels;
        private List<LoggerLevel> previousLevels;
        private boolean enabled;
        
        public Group(final String name) {
            this.loggerLevels = new ArrayList<LoggerLevel>();
            this.previousLevels = new ArrayList<LoggerLevel>();
            this.name = name;
        }
        
        public Group(final String name, final LoggerLevel[] loggerLevels) {
            this.loggerLevels = new ArrayList<LoggerLevel>();
            this.previousLevels = new ArrayList<LoggerLevel>();
            this.name = name;
            this.loggerLevels = Arrays.asList(loggerLevels);
        }
        
        public String getName() {
            return this.name;
        }
        
        public List<LoggerLevel> getLoggerLevels() {
            return this.loggerLevels;
        }
        
        public boolean isEnabled() {
            return this.enabled;
        }
        
        public void setEnabled(final boolean enabled) {
            this.enabled = enabled;
        }
        
        public List<LoggerLevel> getPreviousLevels() {
            return this.previousLevels;
        }
        
        public void setPreviousLevels(final List<LoggerLevel> previousLevels) {
            this.previousLevels = previousLevels;
        }
    }
    
    public static class LoggerLevel
    {
        private String logger;
        private Level level;
        
        public LoggerLevel(final String logger, final Level level) {
            this.logger = logger;
            this.level = level;
        }
        
        public String getLogger() {
            return this.logger;
        }
        
        public Level getLevel() {
            return this.level;
        }
    }
}
