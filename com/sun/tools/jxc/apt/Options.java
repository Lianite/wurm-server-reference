// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.jxc.apt;

import com.sun.tools.xjc.BadCommandLineException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class Options
{
    public String classpath;
    public File targetDir;
    public File episodeFile;
    public final List<String> arguments;
    
    public Options() {
        this.classpath = System.getenv("CLASSPATH");
        this.targetDir = null;
        this.episodeFile = null;
        this.arguments = new ArrayList<String>();
    }
    
    public void parseArguments(final String[] args) throws BadCommandLineException {
        for (int i = 0; i < args.length; ++i) {
            if (args[i].charAt(0) == '-') {
                final int j = this.parseArgument(args, i);
                if (j == 0) {
                    throw new BadCommandLineException(Messages.UNRECOGNIZED_PARAMETER.format(args[i]));
                }
                i += j;
            }
            else {
                this.arguments.add(args[i]);
            }
        }
    }
    
    private int parseArgument(final String[] args, int i) throws BadCommandLineException {
        if (args[i].equals("-d")) {
            if (i == args.length - 1) {
                throw new BadCommandLineException(Messages.OPERAND_MISSING.format(args[i]));
            }
            this.targetDir = new File(args[++i]);
            if (!this.targetDir.exists()) {
                throw new BadCommandLineException(Messages.NON_EXISTENT_FILE.format(this.targetDir));
            }
            return 1;
        }
        else if (args[i].equals("-episode")) {
            if (i == args.length - 1) {
                throw new BadCommandLineException(Messages.OPERAND_MISSING.format(args[i]));
            }
            this.episodeFile = new File(args[++i]);
            return 1;
        }
        else {
            if (!args[i].equals("-cp") && !args[i].equals("-classpath")) {
                return 0;
            }
            if (i == args.length - 1) {
                throw new BadCommandLineException(Messages.OPERAND_MISSING.format(args[i]));
            }
            this.classpath = args[++i];
            return 1;
        }
    }
}
