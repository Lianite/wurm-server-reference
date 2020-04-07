// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl.scd;

import com.sun.xml.xsom.XSComponent;
import java.util.Iterator;
import com.sun.xml.xsom.SCD;

public final class SCDImpl extends SCD
{
    private final Step[] steps;
    private final String text;
    
    public SCDImpl(final String text, final Step[] steps) {
        this.text = text;
        this.steps = steps;
    }
    
    public Iterator<XSComponent> select(final Iterator<? extends XSComponent> contextNode) {
        Iterator<XSComponent> nodeSet = (Iterator<XSComponent>)contextNode;
        for (int len = this.steps.length, i = 0; i < len; ++i) {
            if (i != 0 && i != len - 1 && !this.steps[i - 1].axis.isModelGroup() && this.steps[i].axis.isModelGroup()) {
                nodeSet = new Iterators.Unique<XSComponent>((Iterator<? extends XSComponent>)new Iterators.Map<XSComponent, XSComponent>(nodeSet) {
                    protected Iterator<XSComponent> apply(final XSComponent u) {
                        return new Iterators.Union<XSComponent>((Iterator<? extends XSComponent>)Iterators.singleton((T)u), Axis.INTERMEDIATE_SKIP.iterator(u));
                    }
                });
            }
            nodeSet = this.steps[i].evaluate(nodeSet);
        }
        return nodeSet;
    }
    
    public String toString() {
        return this.text;
    }
}
