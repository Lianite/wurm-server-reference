// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.unmarshaller.automaton;

import java.io.IOException;
import java.util.Iterator;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.PrintStream;

public class AutomatonToGraphViz
{
    private static final PrintStream debug;
    
    private static String getStateName(final Automaton a, final State s) {
        return "\"s" + a.getStateNumber(s) + (s.isListState ? "*" : "") + '\"';
    }
    
    private static String getColor(final Alphabet a) {
        if (a instanceof Alphabet.EnterElement) {
            return "0";
        }
        if (a instanceof Alphabet.EnterAttribute) {
            return "0.125";
        }
        if (a instanceof Alphabet.LeaveAttribute) {
            return "0.25";
        }
        if (a instanceof Alphabet.LeaveElement) {
            return "0.375";
        }
        if (a instanceof Alphabet.Child) {
            return "0.5";
        }
        if (a instanceof Alphabet.SuperClass) {
            return "0.625";
        }
        if (a instanceof Alphabet.External) {
            return "0.625";
        }
        if (a instanceof Alphabet.Dispatch) {
            return "0.625";
        }
        if (a instanceof Alphabet.EverythingElse) {
            return "0.625";
        }
        if (a instanceof Alphabet.Text) {
            return "0.75";
        }
        if (a instanceof Alphabet.Interleave) {
            return "0.875";
        }
        throw new InternalError(a.getClass().getName());
    }
    
    public static void convert(final Automaton a, final File target) throws IOException, InterruptedException {
        System.err.println("generating a graph to " + target.getPath());
        final Process proc = Runtime.getRuntime().exec(new String[] { "dot", "-Tgif", "-o", target.getPath() });
        final PrintWriter out = new PrintWriter(new BufferedOutputStream(proc.getOutputStream()));
        out.println("digraph G {");
        out.println("node [shape=\"circle\"];");
        final Iterator itr = a.states();
        while (itr.hasNext()) {
            final State s = itr.next();
            if (s.isFinalState()) {
                out.println(getStateName(a, s) + " [shape=\"doublecircle\"];");
            }
            if (s.getDelegatedState() != null) {
                out.println(MessageFormat.format("{0} -> {1} [style=dotted];", getStateName(a, s), getStateName(a, s.getDelegatedState())));
            }
            final Iterator jtr = s.transitions();
            while (jtr.hasNext()) {
                final Transition t = jtr.next();
                final String str = MessageFormat.format("{0} -> {1} [ label=\"{2}\",color=\"{3} 1 .5\",fontcolor=\"{3} 1 .3\" ];", getStateName(a, s), getStateName(a, t.to), getAlphabetName(a, t.alphabet), getColor(t.alphabet));
                out.println(str);
                if (AutomatonToGraphViz.debug != null) {
                    AutomatonToGraphViz.debug.println(str);
                }
            }
        }
        out.println("}");
        out.flush();
        out.close();
        final BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        while (true) {
            final String s2 = in.readLine();
            if (s2 == null) {
                break;
            }
            System.out.println(s2);
        }
        in.close();
        proc.waitFor();
    }
    
    private static String getAlphabetName(final Automaton a, final Alphabet alphabet) {
        String s = alphabet.toString();
        if (alphabet instanceof Alphabet.Interleave) {
            s += " ->";
            final Alphabet.Interleave ia = (Alphabet.Interleave)alphabet;
            for (int i = 0; i < ia.branches.length; ++i) {
                s = s + " " + a.getStateNumber(ia.branches[i].initialState);
            }
        }
        return s;
    }
    
    static {
        debug = null;
    }
}
