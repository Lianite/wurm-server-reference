// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.util.regex.PatternSyntaxException;
import com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import java.util.Deque;

final class GlobToRegex
{
    private static final InternalCharMatcher REGEX_RESERVED;
    private final String glob;
    private final String separators;
    private final InternalCharMatcher separatorMatcher;
    private final StringBuilder builder;
    private final Deque<State> states;
    private int index;
    private static final State NORMAL;
    private static final State ESCAPE;
    private static final State STAR;
    private static final State BRACKET_FIRST_CHAR;
    private static final State BRACKET;
    private static final State CURLY_BRACE;
    
    public static String toRegex(final String glob, final String separators) {
        return new GlobToRegex(glob, separators).convert();
    }
    
    private GlobToRegex(final String glob, final String separators) {
        this.builder = new StringBuilder();
        this.states = new ArrayDeque<State>();
        this.glob = Preconditions.checkNotNull(glob);
        this.separators = separators;
        this.separatorMatcher = InternalCharMatcher.anyOf(separators);
    }
    
    private String convert() {
        this.pushState(GlobToRegex.NORMAL);
        this.index = 0;
        while (this.index < this.glob.length()) {
            this.currentState().process(this, this.glob.charAt(this.index));
            ++this.index;
        }
        this.currentState().finish(this);
        return this.builder.toString();
    }
    
    private void pushState(final State state) {
        this.states.push(state);
    }
    
    private void popState() {
        this.states.pop();
    }
    
    private State currentState() {
        return this.states.peek();
    }
    
    private PatternSyntaxException syntaxError(final String desc) {
        throw new PatternSyntaxException(desc, this.glob, this.index);
    }
    
    private void appendExact(final char c) {
        this.builder.append(c);
    }
    
    private void append(final char c) {
        if (this.separatorMatcher.matches(c)) {
            this.appendSeparator();
        }
        else {
            this.appendNormal(c);
        }
    }
    
    private void appendNormal(final char c) {
        if (GlobToRegex.REGEX_RESERVED.matches(c)) {
            this.builder.append('\\');
        }
        this.builder.append(c);
    }
    
    private void appendSeparator() {
        if (this.separators.length() == 1) {
            this.appendNormal(this.separators.charAt(0));
        }
        else {
            this.builder.append('[');
            for (int i = 0; i < this.separators.length(); ++i) {
                this.appendInBracket(this.separators.charAt(i));
            }
            this.builder.append("]");
        }
    }
    
    private void appendNonSeparator() {
        this.builder.append("[^");
        for (int i = 0; i < this.separators.length(); ++i) {
            this.appendInBracket(this.separators.charAt(i));
        }
        this.builder.append(']');
    }
    
    private void appendQuestionMark() {
        this.appendNonSeparator();
    }
    
    private void appendStar() {
        this.appendNonSeparator();
        this.builder.append('*');
    }
    
    private void appendStarStar() {
        this.builder.append(".*");
    }
    
    private void appendBracketStart() {
        this.builder.append('[');
        this.appendNonSeparator();
        this.builder.append("&&[");
    }
    
    private void appendBracketEnd() {
        this.builder.append("]]");
    }
    
    private void appendInBracket(final char c) {
        if (c == '\\') {
            this.builder.append('\\');
        }
        this.builder.append(c);
    }
    
    private void appendCurlyBraceStart() {
        this.builder.append('(');
    }
    
    private void appendSubpatternSeparator() {
        this.builder.append('|');
    }
    
    private void appendCurlyBraceEnd() {
        this.builder.append(')');
    }
    
    static {
        REGEX_RESERVED = InternalCharMatcher.anyOf("^$.?+*\\[]{}()");
        NORMAL = new State() {
            @Override
            void process(final GlobToRegex converter, final char c) {
                switch (c) {
                    case '?': {
                        converter.appendQuestionMark();
                    }
                    case '[': {
                        converter.appendBracketStart();
                        converter.pushState(GlobToRegex.BRACKET_FIRST_CHAR);
                    }
                    case '{': {
                        converter.appendCurlyBraceStart();
                        converter.pushState(GlobToRegex.CURLY_BRACE);
                    }
                    case '*': {
                        converter.pushState(GlobToRegex.STAR);
                    }
                    case '\\': {
                        converter.pushState(GlobToRegex.ESCAPE);
                    }
                    default: {
                        converter.append(c);
                    }
                }
            }
            
            @Override
            public String toString() {
                return "NORMAL";
            }
        };
        ESCAPE = new State() {
            @Override
            void process(final GlobToRegex converter, final char c) {
                converter.append(c);
                converter.popState();
            }
            
            @Override
            void finish(final GlobToRegex converter) {
                throw converter.syntaxError("Hanging escape (\\) at end of pattern");
            }
            
            @Override
            public String toString() {
                return "ESCAPE";
            }
        };
        STAR = new State() {
            @Override
            void process(final GlobToRegex converter, final char c) {
                if (c == '*') {
                    converter.appendStarStar();
                    converter.popState();
                }
                else {
                    converter.appendStar();
                    converter.popState();
                    converter.currentState().process(converter, c);
                }
            }
            
            @Override
            void finish(final GlobToRegex converter) {
                converter.appendStar();
            }
            
            @Override
            public String toString() {
                return "STAR";
            }
        };
        BRACKET_FIRST_CHAR = new State() {
            @Override
            void process(final GlobToRegex converter, final char c) {
                if (c == ']') {
                    throw converter.syntaxError("Empty []");
                }
                if (c == '!') {
                    converter.appendExact('^');
                }
                else if (c == '-') {
                    converter.appendExact(c);
                }
                else {
                    converter.appendInBracket(c);
                }
                converter.popState();
                converter.pushState(GlobToRegex.BRACKET);
            }
            
            @Override
            void finish(final GlobToRegex converter) {
                throw converter.syntaxError("Unclosed [");
            }
            
            @Override
            public String toString() {
                return "BRACKET_FIRST_CHAR";
            }
        };
        BRACKET = new State() {
            @Override
            void process(final GlobToRegex converter, final char c) {
                if (c == ']') {
                    converter.appendBracketEnd();
                    converter.popState();
                }
                else {
                    converter.appendInBracket(c);
                }
            }
            
            @Override
            void finish(final GlobToRegex converter) {
                throw converter.syntaxError("Unclosed [");
            }
            
            @Override
            public String toString() {
                return "BRACKET";
            }
        };
        CURLY_BRACE = new State() {
            @Override
            void process(final GlobToRegex converter, final char c) {
                switch (c) {
                    case '?': {
                        converter.appendQuestionMark();
                        break;
                    }
                    case '[': {
                        converter.appendBracketStart();
                        converter.pushState(GlobToRegex.BRACKET_FIRST_CHAR);
                        break;
                    }
                    case '{': {
                        throw converter.syntaxError("{ not allowed in subpattern group");
                    }
                    case '*': {
                        converter.pushState(GlobToRegex.STAR);
                        break;
                    }
                    case '\\': {
                        converter.pushState(GlobToRegex.ESCAPE);
                        break;
                    }
                    case '}': {
                        converter.appendCurlyBraceEnd();
                        converter.popState();
                        break;
                    }
                    case ',': {
                        converter.appendSubpatternSeparator();
                        break;
                    }
                    default: {
                        converter.append(c);
                        break;
                    }
                }
            }
            
            @Override
            void finish(final GlobToRegex converter) {
                throw converter.syntaxError("Unclosed {");
            }
            
            @Override
            public String toString() {
                return "CURLY_BRACE";
            }
        };
    }
    
    private abstract static class State
    {
        abstract void process(final GlobToRegex p0, final char p1);
        
        void finish(final GlobToRegex converter) {
        }
    }
}
