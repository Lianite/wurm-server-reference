// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.unmarshaller;

import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JSwitch;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JBlock;
import com.sun.tools.xjc.generator.unmarshaller.automaton.Transition;
import com.sun.msv.datatype.DatabindableDatatype;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import com.sun.tools.xjc.generator.validator.StringOutputStream;
import java.io.StringWriter;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.generator.unmarshaller.automaton.Alphabet;
import com.sun.tools.xjc.generator.unmarshaller.automaton.State;
import com.sun.codemodel.JVar;

class TextMethodGenerator extends HandlerMethodGenerator
{
    private JVar $value;
    private int datatypeId;
    
    TextMethodGenerator(final PerClassGenerator parent) {
        super(parent, "text", (TextMethodGenerator.class$com$sun$tools$xjc$generator$unmarshaller$automaton$Alphabet$Text == null) ? (TextMethodGenerator.class$com$sun$tools$xjc$generator$unmarshaller$automaton$Alphabet$Text = class$("com.sun.tools.xjc.generator.unmarshaller.automaton.Alphabet$Text")) : TextMethodGenerator.class$com$sun$tools$xjc$generator$unmarshaller$automaton$Alphabet$Text);
        this.datatypeId = 0;
    }
    
    private boolean needsGuard(final State state) {
        int count = 0;
        final TransitionTable.Entry[] e = this.table.list(state);
        for (int i = 0; i < e.length; ++i) {
            if (e[i].alphabet.isText()) {
                ++count;
            }
        }
        return count > 1;
    }
    
    private JExpression guardClause(final Alphabet a) {
        if (a instanceof Alphabet.IgnoredText || a instanceof Alphabet.SuperClass || a instanceof Alphabet.Child || a instanceof Alphabet.EverythingElse) {
            return JExpr.TRUE;
        }
        _assert(a instanceof Alphabet.BoundText);
        final DatabindableDatatype guard = ((Alphabet.BoundText)a).item.guard;
        final StringWriter sw = new StringWriter();
        try {
            final ObjectOutputStream oos = new ObjectOutputStream((OutputStream)new StringOutputStream((Writer)sw));
            oos.writeObject(guard);
            oos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new InternalError("unserializable datatype:" + guard);
        }
        final JVar $dt = this.parent.context.implClass.field(28, (TextMethodGenerator.class$org$relaxng$datatype$Datatype == null) ? (TextMethodGenerator.class$org$relaxng$datatype$Datatype = class$("org.relaxng.datatype.Datatype")) : TextMethodGenerator.class$org$relaxng$datatype$Datatype, "___dt" + this.datatypeId++, this.codeModel.ref((TextMethodGenerator.class$com$sun$xml$bind$unmarshaller$DatatypeDeserializer == null) ? (TextMethodGenerator.class$com$sun$xml$bind$unmarshaller$DatatypeDeserializer = class$("com.sun.xml.bind.unmarshaller.DatatypeDeserializer")) : TextMethodGenerator.class$com$sun$xml$bind$unmarshaller$DatatypeDeserializer).staticInvoke("deserialize").arg(JExpr.lit(sw.getBuffer().toString())));
        JExpression con;
        if (guard.isContextDependent()) {
            con = JExpr._new(this.parent.parent.context.getRuntime((TextMethodGenerator.class$com$sun$tools$xjc$runtime$ValidationContextAdaptor == null) ? (TextMethodGenerator.class$com$sun$tools$xjc$runtime$ValidationContextAdaptor = class$("com.sun.tools.xjc.runtime.ValidationContextAdaptor")) : TextMethodGenerator.class$com$sun$tools$xjc$runtime$ValidationContextAdaptor)).arg(this.parent.$context);
        }
        else {
            con = JExpr._null();
        }
        return $dt.invoke("isValid").arg(this.$value).arg(con);
    }
    
    protected boolean performTransition(final State state, final Alphabet alphabet, final Transition action) {
        JBlock block = this.getCase(state);
        final boolean needsGuard = this.needsGuard(state);
        if (needsGuard) {
            block = block._if(this.guardClause(alphabet))._then();
        }
        if (action == Transition.REVERT_TO_PARENT) {
            this.generateRevertToParent(block);
            return needsGuard;
        }
        if (action.alphabet instanceof Alphabet.Reference) {
            this.generateSpawnChild(block, action);
            return needsGuard;
        }
        if (action.alphabet instanceof Alphabet.BoundText) {
            this.parent.eatText(block, action.alphabet.asBoundText(), (JExpression)this.$value);
        }
        this.generateGoto(block, action.to);
        block._return();
        return needsGuard;
    }
    
    protected String getNameOfMethodDecl() {
        return "handleText";
    }
    
    protected JSwitch makeSwitch(final JMethod method, final JBlock body) {
        this.$value = method.param(8, (TextMethodGenerator.class$java$lang$String == null) ? (TextMethodGenerator.class$java$lang$String = class$("java.lang.String")) : TextMethodGenerator.class$java$lang$String, "value");
        if (this.trace) {
            body.invoke(this.$tracer, "onText").arg(this.$value);
        }
        final JTryBlock tryBlock = body._try();
        final JSwitch s = super.makeSwitch(method, tryBlock.body());
        final JCatchBlock c = tryBlock._catch(this.codeModel.ref((TextMethodGenerator.class$java$lang$RuntimeException == null) ? (TextMethodGenerator.class$java$lang$RuntimeException = class$("java.lang.RuntimeException")) : TextMethodGenerator.class$java$lang$RuntimeException));
        final JVar $e = c.param("e");
        c.body().invoke("handleUnexpectedTextException").arg(this.$value).arg($e);
        return s;
    }
    
    protected void addParametersToContextSwitch(final JInvocation inv) {
        inv.arg(this.$value);
    }
}
