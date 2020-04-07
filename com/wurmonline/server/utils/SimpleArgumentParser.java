// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.utils;

import java.util.Locale;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

public class SimpleArgumentParser
{
    private final HashMap<String, String> assignedOptions;
    private final HashSet<String> flagOptions;
    private final HashSet<String> unknownOptions;
    
    public SimpleArgumentParser(final String[] args, final Set<String> allowedOptions) {
        this.assignedOptions = new HashMap<String, String>();
        this.flagOptions = new HashSet<String>();
        this.unknownOptions = new HashSet<String>();
        for (String arg : args) {
            arg = arg.trim();
            if (!arg.isEmpty()) {
                if (!arg.contains("WurmServerLauncher")) {
                    final int assignmentIdx = arg.indexOf(61);
                    if (assignmentIdx > 0) {
                        final String option = arg.substring(0, assignmentIdx).toLowerCase(Locale.ENGLISH);
                        if (!allowedOptions.contains(option)) {
                            System.err.println("Unknown parameter: " + option);
                        }
                        else if (assignmentIdx >= arg.length()) {
                            this.assignedOptions.put(option, "");
                        }
                        else {
                            this.assignedOptions.put(option, arg.substring(assignmentIdx + 1));
                        }
                    }
                    else if (allowedOptions.contains(arg)) {
                        this.flagOptions.add(arg.toLowerCase(Locale.ENGLISH));
                    }
                    else {
                        System.err.println("Unknown parameter: " + arg);
                        this.unknownOptions.add(arg);
                    }
                }
            }
        }
    }
    
    public boolean hasOption(final String option) {
        return this.flagOptions.contains(option) || this.assignedOptions.containsKey(option);
    }
    
    public boolean hasFlag(final String option) {
        return this.flagOptions.contains(option);
    }
    
    public String getOptionValue(final String option) {
        return this.assignedOptions.get(option);
    }
    
    public boolean hasUnknownOptions() {
        return !this.unknownOptions.isEmpty();
    }
}
