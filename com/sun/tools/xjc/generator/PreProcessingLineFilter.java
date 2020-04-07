// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator;

import java.text.ParseException;
import java.util.Stack;
import com.sun.codemodel.fmt.JStaticJavaFile;

public abstract class PreProcessingLineFilter implements JStaticJavaFile.LineFilter
{
    private final Stack conditions;
    private static final String META_TOKEN = "// META-";
    
    public PreProcessingLineFilter() {
        this.conditions = new Stack();
    }
    
    private boolean isOn() {
        for (int i = this.conditions.size() - 1; i >= 0; --i) {
            if (!(boolean)this.conditions.get(i)) {
                return false;
            }
        }
        return true;
    }
    
    public String process(final String line) throws ParseException {
        int idx = line.indexOf("// META-");
        if (idx < 0) {
            if (this.isOn()) {
                return line;
            }
            return null;
        }
        else {
            final String cond = line.substring(idx + "// META-".length()).trim();
            if (cond.startsWith("IF(")) {
                idx = cond.indexOf(41);
                if (idx < 0) {
                    throw new ParseException("Unable to parse " + cond, -1);
                }
                final String exp = cond.substring(3, idx);
                this.conditions.push(this.eval(exp) ? Boolean.TRUE : Boolean.FALSE);
                return null;
            }
            else {
                if (cond.equals("ELSE")) {
                    final Boolean b = this.conditions.pop();
                    this.conditions.push(((boolean)b) ? Boolean.FALSE : Boolean.TRUE);
                    return null;
                }
                if (cond.equals("ENDIF")) {
                    this.conditions.pop();
                    return null;
                }
                throw new ParseException("unrecognized meta statement " + line, -1);
            }
        }
    }
    
    private boolean eval(final String exp) throws ParseException {
        boolean r = this.getVar(exp.charAt(0));
        int i = 1;
        if (i < exp.length()) {
            final char op = exp.charAt(i++);
            if (i == exp.length()) {
                throw new ParseException("Unable to parse " + exp, -1);
            }
            final boolean rhs = this.getVar(exp.charAt(i++));
            switch (op) {
                case '|': {
                    r |= rhs;
                    break;
                }
                case '&': {
                    r &= rhs;
                    break;
                }
                default: {
                    throw new ParseException("Unable to parse" + exp, -1);
                }
            }
        }
        return r;
    }
    
    protected abstract boolean getVar(final char p0) throws ParseException;
}
