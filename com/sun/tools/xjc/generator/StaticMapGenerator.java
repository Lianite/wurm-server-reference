// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator;

import com.sun.codemodel.JMethod;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.util.Util;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JVar;

public abstract class StaticMapGenerator
{
    public final JVar $map;
    private JBlock block;
    private int cnt;
    private int ticketMaster;
    private final int THRESHOLD;
    
    protected StaticMapGenerator(final JVar $map, final JBlock block) {
        this.ticketMaster = 1;
        this.$map = $map;
        this.block = block;
        final String debug = Util.getSystemProperty((StaticMapGenerator.class$com$sun$tools$xjc$generator$ObjectFactoryGenerator == null) ? (StaticMapGenerator.class$com$sun$tools$xjc$generator$ObjectFactoryGenerator = class$("com.sun.tools.xjc.generator.ObjectFactoryGenerator")) : StaticMapGenerator.class$com$sun$tools$xjc$generator$ObjectFactoryGenerator, "staticThreshold");
        if (debug == null) {
            this.THRESHOLD = 500;
        }
        else {
            this.THRESHOLD = Integer.parseInt(debug);
        }
    }
    
    protected final void add(final JExpression key, final JExpression value) {
        this.block.invoke(this.$map, "put").arg(key).arg(value);
        if (++this.cnt >= this.THRESHOLD) {
            final JMethod m = this.createNewMethod(this.ticketMaster++);
            this.block.invoke(m);
            this.block = m.body();
            this.cnt = 0;
        }
    }
    
    protected abstract JMethod createNewMethod(final int p0);
}
