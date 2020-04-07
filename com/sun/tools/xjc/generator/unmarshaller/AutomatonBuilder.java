// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.unmarshaller;

import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ValueExp;
import com.sun.tools.xjc.grammar.PrimitiveItem;
import com.sun.tools.xjc.grammar.ExternalItem;
import com.sun.tools.xjc.grammar.SuperClassItem;
import com.sun.tools.xjc.grammar.InterfaceItem;
import com.sun.tools.xjc.grammar.FieldItem;
import com.sun.tools.xjc.grammar.IgnoreItem;
import com.sun.tools.xjc.generator.LookupTableUse;
import com.sun.tools.xjc.grammar.JavaItem;
import com.sun.tools.xjc.grammar.FieldUse;
import com.sun.tools.xjc.grammar.JavaItemVisitor;
import com.sun.msv.grammar.ConcurExp;
import com.sun.xml.bind.JAXBAssertionError;
import com.sun.msv.grammar.MixedExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.OneOrMoreExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.SequenceExp;
import com.sun.tools.xjc.grammar.util.GroupFinder;
import com.sun.msv.grammar.InterleaveExp;
import com.sun.tools.xjc.generator.unmarshaller.automaton.Transition;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.NameClassAndExpression;
import com.sun.tools.xjc.generator.unmarshaller.automaton.Alphabet;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.ExpressionVisitorBoolean;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.xmlschema.OccurrenceExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.tools.xjc.generator.unmarshaller.automaton.Automaton;
import com.sun.msv.grammar.util.ExpressionFinder;
import com.sun.msv.grammar.ExpressionVisitor;
import com.sun.tools.xjc.generator.unmarshaller.automaton.State;
import java.util.Map;
import com.sun.tools.xjc.generator.GeneratorContext;
import com.sun.tools.xjc.grammar.ClassItem;

public class AutomatonBuilder
{
    private final ClassItem classItem;
    private final GeneratorContext context;
    private final Map otherAutomata;
    private State tail;
    private int idGen;
    private final ExpressionVisitor normal;
    private final ExpressionVisitor inIgnoredItem;
    private static final ExpressionFinder textFinder;
    
    public static void build(final Automaton a, final GeneratorContext context, final Map automata) {
        final ClassItem ci = a.getOwner().target;
        a.setInitialState((State)ci.exp.visit(new AutomatonBuilder(ci, context, automata).normal));
    }
    
    private Automaton getAutomaton(final ClassItem ci) {
        return this.otherAutomata.get(ci);
    }
    
    private AutomatonBuilder(final ClassItem ci, final GeneratorContext _context, final Map _automata) {
        this.idGen = 0;
        this.normal = (ExpressionVisitor)new Normal(this, (AutomatonBuilder$1)null);
        this.inIgnoredItem = (ExpressionVisitor)new Ignored(this, (AutomatonBuilder$1)null);
        (this.tail = new State()).markAsFinalState();
        this.classItem = ci;
        this.context = _context;
        this.otherAutomata = _automata;
    }
    
    static {
        AutomatonBuilder.textFinder = (ExpressionFinder)new AutomatonBuilder$1();
    }
    
    private abstract class Base implements ExpressionVisitor
    {
        private Base(final AutomatonBuilder this$0) {
            this.this$0 = this$0;
        }
        
        public Object onRef(final ReferenceExp exp) {
            return exp.exp.visit((ExpressionVisitor)this);
        }
        
        public Object onOther(final OtherExp exp) {
            if (exp instanceof OccurrenceExp) {
                return this.onOccurence((OccurrenceExp)exp);
            }
            return exp.exp.visit((ExpressionVisitor)this);
        }
        
        public Object onEpsilon() {
            return this.this$0.tail;
        }
        
        private boolean contansText(final Expression exp) {
            return exp.visit((ExpressionVisitorBoolean)AutomatonBuilder.textFinder);
        }
        
        public Object onElement(final ElementExp exp) {
            final int idx = this.this$0.idGen++;
            return this.onItem((NameClassAndExpression)exp, (Alphabet)new Alphabet.EnterElement(idx, exp.getNameClass(), this.contansText(exp.getContentModel())), (Alphabet)new Alphabet.LeaveElement(idx, exp.getNameClass()));
        }
        
        public Object onAttribute(final AttributeExp exp) {
            final int idx = this.this$0.idGen++;
            return this.onItem((NameClassAndExpression)exp, (Alphabet)new Alphabet.EnterAttribute(idx, exp.getNameClass()), (Alphabet)new Alphabet.LeaveAttribute(idx, exp.getNameClass()));
        }
        
        private State onItem(final NameClassAndExpression exp, final Alphabet s, final Alphabet e) {
            final State newTail = new State();
            newTail.addTransition(new Transition(e, this.this$0.tail));
            this.this$0.tail = newTail;
            final State contentHead = (State)exp.getContentModel().visit((ExpressionVisitor)this);
            final State head = new State();
            head.addTransition(new Transition(s, contentHead));
            return head;
        }
        
        public Object onInterleave(final InterleaveExp exp) {
            final Expression[] children = exp.getChildren();
            final State currentTail = this.this$0.tail;
            if (this.isInterleaveOptimizable(children)) {
                final State head = new State();
                for (int i = 0; i < children.length; ++i) {
                    this.this$0.tail = currentTail;
                    head.absorb((State)children[i].visit((ExpressionVisitor)this));
                }
                currentTail.absorb(head);
                return currentTail;
            }
            final Alphabet.Interleave.Branch[] branches = new Alphabet.Interleave.Branch[children.length];
            for (int i = 0; i < children.length; ++i) {
                this.this$0.tail = new State();
                this.this$0.tail.markAsFinalState();
                branches[i] = new Alphabet.Interleave.Branch((State)children[i].visit((ExpressionVisitor)this), children[i]);
            }
            final State head2 = new State();
            head2.addTransition(new Transition((Alphabet)new Alphabet.Interleave(branches, this.this$0.idGen++), currentTail));
            return head2;
        }
        
        private boolean isInterleaveOptimizable(final Expression[] children) {
            for (int i = 0; i < children.length; ++i) {
                if (GroupFinder.find(children[i])) {
                    return false;
                }
            }
            return true;
        }
        
        public Object onSequence(final SequenceExp exp) {
            final Expression[] children = exp.getChildren();
            for (int i = children.length - 1; i >= 0; --i) {
                this.this$0.tail = (State)children[i].visit((ExpressionVisitor)this);
            }
            return this.this$0.tail;
        }
        
        public Object onChoice(final ChoiceExp exp) {
            final Expression[] children = exp.getChildren();
            final State currentTail = this.this$0.tail;
            State head = new State();
            for (int i = children.length - 1; i >= 0; --i) {
                this.this$0.tail = currentTail;
                final State localHead = (State)children[i].visit((ExpressionVisitor)this);
                if (localHead != currentTail) {
                    head.absorb(localHead);
                }
            }
            if (exp.isEpsilonReducible()) {
                if (head.hasTransition()) {
                    head.setDelegatedState(currentTail);
                }
                else {
                    head = currentTail;
                }
            }
            return head;
        }
        
        public Object onOneOrMore(final OneOrMoreExp exp) {
            return this._onRepeated(exp.exp, false);
        }
        
        private State onOccurence(final OccurrenceExp exp) {
            return this._onRepeated(exp.itemExp, exp.minOccurs == 0);
        }
        
        private State _onRepeated(final Expression itemExp, final boolean isZeroAllowed) {
            final State _tail = this.this$0.tail;
            final State newHead = (State)itemExp.visit((ExpressionVisitor)this);
            _tail.absorb(newHead);
            return isZeroAllowed ? _tail : newHead;
        }
        
        public Object onList(final ListExp exp) {
            final State head = (State)exp.exp.visit((ExpressionVisitor)this);
            head.isListState = true;
            return head;
        }
        
        public Object onNullSet() {
            return new State();
        }
        
        public Object onMixed(final MixedExp exp) {
            throw new JAXBAssertionError();
        }
        
        public Object onConcur(final ConcurExp exp) {
            throw new JAXBAssertionError();
        }
    }
    
    private class Normal extends Base implements JavaItemVisitor
    {
        private FieldUse currentField;
        private boolean inSuperClass;
        
        private Normal(final AutomatonBuilder this$0) {
            super(this$0, (AutomatonBuilder$1)null);
            this.this$0 = this$0;
            this.currentField = null;
            this.inSuperClass = false;
        }
        
        public Object onOther(final OtherExp exp) {
            if (exp instanceof JavaItem) {
                return ((JavaItem)exp).visitJI((JavaItemVisitor)this);
            }
            return super.onOther(exp);
        }
        
        public Object onChoice(final ChoiceExp exp) {
            final LookupTableUse use = this.this$0.context.getLookupTableBuilder().buildTable(exp);
            if (use != null) {
                final State head = new State();
                if (use.anomaly != null) {
                    head.absorb((State)use.anomaly.visit((ExpressionVisitor)this));
                }
                head.addTransition(new Transition((Alphabet)new Alphabet.Dispatch(use, this.this$0.context.getField(this.currentField), this.this$0.idGen++), this.this$0.tail));
                return head;
            }
            return super.onChoice(exp);
        }
        
        public Object onIgnore(final IgnoreItem item) {
            return item.exp.visit(this.this$0.inIgnoredItem);
        }
        
        public Object onField(final FieldItem fi) {
            if (this.currentField != null) {
                throw new JAXBAssertionError();
            }
            this.currentField = this.this$0.classItem.getField(fi.name);
            if (this.currentField == null) {
                throw new JAXBAssertionError();
            }
            final Object r = fi.exp.visit((ExpressionVisitor)this);
            this.currentField = null;
            return r;
        }
        
        public Object onInterface(final InterfaceItem item) {
            return item.exp.visit((ExpressionVisitor)this);
        }
        
        public Object onSuper(final SuperClassItem item) {
            this.inSuperClass = true;
            final Object ret = item.exp.visit((ExpressionVisitor)this);
            this.inSuperClass = false;
            return ret;
        }
        
        public Object onExternal(final ExternalItem item) {
            final State head = new State();
            if (this.currentField == null) {
                throw new JAXBAssertionError();
            }
            final Alphabet a = (Alphabet)new Alphabet.External(item, this.this$0.context.getField(this.currentField), this.this$0.idGen++);
            head.addTransition(new Transition(a, this.this$0.tail));
            return head;
        }
        
        public Object onClass(final ClassItem item) {
            final State head = new State();
            Alphabet a;
            if (this.inSuperClass) {
                a = (Alphabet)new Alphabet.SuperClass(AutomatonBuilder.access$800(this.this$0, item), this.this$0.idGen++);
            }
            else {
                if (this.currentField == null) {
                    throw new JAXBAssertionError();
                }
                a = (Alphabet)new Alphabet.Child(AutomatonBuilder.access$800(this.this$0, item), this.this$0.context.getField(this.currentField), this.this$0.idGen++);
            }
            head.addTransition(new Transition(a, this.this$0.tail));
            return head;
        }
        
        public Object onPrimitive(final PrimitiveItem item) {
            if (this.currentField == null) {
                throw new JAXBAssertionError();
            }
            final State head = new State();
            head.addTransition(new Transition((Alphabet)new Alphabet.BoundText(this.this$0.idGen++, item, this.this$0.context.getField(this.currentField)), this.this$0.tail));
            return head;
        }
        
        public Object onValue(final ValueExp exp) {
            throw new JAXBAssertionError();
        }
        
        public Object onData(final DataExp exp) {
            throw new JAXBAssertionError();
        }
        
        public Object onAnyString() {
            throw new JAXBAssertionError();
        }
    }
    
    private class Ignored extends Base
    {
        private Ignored(final AutomatonBuilder this$0) {
            super(this$0, (AutomatonBuilder$1)null);
            this.this$0 = this$0;
        }
        
        public Object onAttribute(final AttributeExp exp) {
            return super.onEpsilon();
        }
        
        public Object onValue(final ValueExp exp) {
            return this.createIgnoredTextTransition();
        }
        
        public Object onData(final DataExp exp) {
            return this.createIgnoredTextTransition();
        }
        
        public Object onAnyString() {
            return this.createIgnoredTextTransition();
        }
        
        public Object onList(final ListExp exp) {
            return this.createIgnoredTextTransition();
        }
        
        private State createIgnoredTextTransition() {
            final State head = new State();
            head.addTransition(new Transition((Alphabet)new Alphabet.IgnoredText(this.this$0.idGen++), this.this$0.tail));
            return head;
        }
    }
}
