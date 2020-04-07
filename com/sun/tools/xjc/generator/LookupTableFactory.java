// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator;

import com.sun.tools.xjc.grammar.JavaItem;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.SequenceExp;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.msv.grammar.ValueExp;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.xml.bind.JAXBAssertionError;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JDefinedClass;

class LookupTableFactory implements LookupTableBuilder
{
    private JDefinedClass tableClass;
    private final JPackage pkg;
    private int id;
    
    public LookupTableFactory(final JPackage _pkg) {
        this.id = 0;
        this.pkg = _pkg;
    }
    
    JDefinedClass getTableClass() {
        if (this.tableClass == null) {
            try {
                this.tableClass = this.pkg._class(1, "Table");
            }
            catch (JClassAlreadyExistsException e) {
                throw new JAXBAssertionError();
            }
        }
        return this.tableClass;
    }
    
    public LookupTableUse buildTable(final ChoiceExp exp) {
        final Expression[] children = exp.getChildren();
        if (children.length < 3) {
            return null;
        }
        int nullBranchCount = 0;
        final Branch[] branches = new Branch[children.length];
        for (int i = 0; i < children.length; ++i) {
            if ((branches[i] = Branch.create(children[i])) == null) {
                ++nullBranchCount;
            }
        }
        if (nullBranchCount > 1) {
            return null;
        }
        int anomaly = -1;
        Branch dominant;
        if (Branch.access$000(branches[0], branches[1])) {
            dominant = branches[0];
        }
        else if (Branch.access$000(branches[0], branches[2])) {
            dominant = branches[0];
            anomaly = 1;
        }
        else {
            if (!Branch.access$000(branches[1], branches[2])) {
                return null;
            }
            dominant = branches[1];
            anomaly = 0;
        }
        for (int j = 2; j < branches.length; ++j) {
            if (!Branch.access$000(dominant, branches[j])) {
                if (anomaly != -1) {
                    return null;
                }
                anomaly = j;
            }
        }
        if (anomaly != -1) {
            branches[anomaly] = null;
        }
        final LookupTable t = new LookupTable(this, this.id++);
        for (int k = 0; k < branches.length; ++k) {
            if (branches[k] != null) {
                final LookupTable.Entry e = branches[k].toEntry();
                if (!t.isConsistentWith(e)) {
                    return null;
                }
                t.add(e);
            }
        }
        return new LookupTableUse(t, (anomaly == -1) ? null : children[anomaly], dominant.attName);
    }
    
    private static class Branch
    {
        final SimpleNameClass attName;
        final ValueExp value;
        final ClassItem body;
        
        private Branch(final SimpleNameClass _attName, final ValueExp _value, final ClassItem _body) {
            this.attName = _attName;
            this.value = _value;
            this.body = _body;
        }
        
        private static boolean agree(final Branch lhs, final Branch rhs) {
            return lhs != null && rhs != null && lhs.attName.namespaceURI.equals(rhs.attName.namespaceURI) && lhs.attName.localName.equals(rhs.attName.localName);
        }
        
        public LookupTable.Entry toEntry() {
            return new LookupTable.Entry(this.body, this.value);
        }
        
        static Branch create(final Expression exp) {
            try {
                final SequenceExp sexp = (SequenceExp)exp;
                final AttributeExp att = (AttributeExp)sexp.exp1;
                final SimpleNameClass name = (SimpleNameClass)att.nameClass;
                ValueExp value;
                if (att.exp instanceof ValueExp) {
                    value = (ValueExp)att.exp;
                }
                else {
                    value = (ValueExp)((JavaItem)att.exp).exp;
                }
                return new Branch(name, value, (ClassItem)sexp.exp2);
            }
            catch (ClassCastException e) {
                return null;
            }
        }
    }
}
