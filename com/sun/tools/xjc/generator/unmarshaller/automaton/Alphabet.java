// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.unmarshaller.automaton;

import com.sun.tools.xjc.grammar.PrimitiveItem;
import com.sun.tools.xjc.grammar.ExternalItem;
import com.sun.tools.xjc.generator.LookupTableUse;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.tools.xjc.generator.LookupTable;
import com.sun.tools.xjc.generator.field.FieldRenderer;
import com.sun.tools.xjc.grammar.util.TextFinder;
import com.sun.tools.xjc.grammar.util.NameFinder;
import com.sun.msv.grammar.Expression;
import java.util.HashSet;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Set;
import com.sun.msv.grammar.NameClass;

public abstract class Alphabet
{
    public final int order;
    
    public Alphabet(final int _order) {
        this.order = _order;
    }
    
    public final Named asNamed() {
        return (Named)this;
    }
    
    public final Reference asReference() {
        return (Reference)this;
    }
    
    public final StaticReference asStaticReference() {
        return (StaticReference)this;
    }
    
    public final Text asText() {
        return (Text)this;
    }
    
    public final BoundText asBoundText() {
        return (BoundText)this;
    }
    
    public final Dispatch asDispatch() {
        return (Dispatch)this;
    }
    
    public final boolean isReference() {
        return this instanceof Reference;
    }
    
    public final boolean isEnterAttribute() {
        return this instanceof EnterAttribute;
    }
    
    public final boolean isLeaveAttribute() {
        return this instanceof LeaveAttribute;
    }
    
    public final boolean isText() {
        return this instanceof Text;
    }
    
    public final boolean isNamed() {
        return this instanceof Named;
    }
    
    public final boolean isBoundText() {
        return this instanceof BoundText;
    }
    
    public final boolean isDispatch() {
        return this instanceof Dispatch;
    }
    
    public abstract void accept(final AlphabetVisitor p0);
    
    protected abstract void accept(final TransitionVisitor p0, final Transition p1);
    
    public abstract static class Named extends Alphabet
    {
        public final NameClass name;
        
        public Named(final int _order, final NameClass _name) {
            super(_order);
            this.name = _name;
        }
    }
    
    public static final class EnterElement extends Named
    {
        public final boolean isDataElement;
        
        public EnterElement(final int _order, final NameClass name, final boolean dataElement) {
            super(_order, name);
            this.isDataElement = dataElement;
        }
        
        public void accept(final AlphabetVisitor visitor) {
            visitor.onEnterElement(this);
        }
        
        protected void accept(final TransitionVisitor visitor, final Transition t) {
            visitor.onEnterElement(this, t.to);
        }
        
        public String toString() {
            return '<' + this.name.toString() + '>';
        }
    }
    
    public static final class LeaveElement extends Named
    {
        public LeaveElement(final int _order, final NameClass name) {
            super(_order, name);
        }
        
        public void accept(final AlphabetVisitor visitor) {
            visitor.onLeaveElement(this);
        }
        
        protected void accept(final TransitionVisitor visitor, final Transition t) {
            visitor.onLeaveElement(this, t.to);
        }
        
        public String toString() {
            return "</" + this.name.toString() + '>';
        }
    }
    
    public static final class EnterAttribute extends Named
    {
        public EnterAttribute(final int _order, final NameClass name) {
            super(_order, name);
        }
        
        public void accept(final AlphabetVisitor visitor) {
            visitor.onEnterAttribute(this);
        }
        
        protected void accept(final TransitionVisitor visitor, final Transition t) {
            visitor.onEnterAttribute(this, t.to);
        }
        
        public String toString() {
            return '@' + this.name.toString();
        }
    }
    
    public static final class LeaveAttribute extends Named
    {
        public LeaveAttribute(final int _order, final NameClass name) {
            super(_order, name);
        }
        
        public void accept(final AlphabetVisitor visitor) {
            visitor.onLeaveAttribute(this);
        }
        
        protected void accept(final TransitionVisitor visitor, final Transition t) {
            visitor.onLeaveAttribute(this, t.to);
        }
        
        public String toString() {
            return "/@" + this.name.toString();
        }
    }
    
    public abstract static class Reference extends Alphabet
    {
        public Reference(final int _order) {
            super(_order);
        }
        
        public abstract boolean isNullable();
        
        public final Set head(final boolean includeEE) {
            final TreeSet r = new TreeSet(OrderComparator.theInstance);
            this.head(r, new HashSet(), includeEE);
            return r;
        }
        
        public abstract void head(final Set p0, final Set p1, final boolean p2);
    }
    
    public static final class Interleave extends Reference
    {
        public final Branch[] branches;
        
        public Interleave(final Branch[] _branches, final int _order) {
            super(_order);
            this.branches = _branches;
        }
        
        public boolean isNullable() {
            for (int i = 0; i < this.branches.length; ++i) {
                if (!this.branches[i].initialState.isFinalState()) {
                    return false;
                }
            }
            return true;
        }
        
        public int getTextBranchIndex() {
            for (int i = 0; i < this.branches.length; ++i) {
                if (this.branches[i].hasText) {
                    return i;
                }
            }
            return -1;
        }
        
        public void head(final Set result, final Set visitedStates, final boolean includeEE) {
            for (int i = 0; i < this.branches.length; ++i) {
                this.branches[i].initialState.head(result, visitedStates, includeEE);
            }
        }
        
        public void accept(final AlphabetVisitor visitor) {
            visitor.onInterleave(this);
        }
        
        protected void accept(final TransitionVisitor visitor, final Transition t) {
            visitor.onInterleave(this, t.to);
        }
        
        public String toString() {
            return "interleave";
        }
        
        public static final class Branch
        {
            public final State initialState;
            public final NameClass elementName;
            public final NameClass attributeName;
            public final boolean hasText;
            
            public Branch(final State s, final Expression e) {
                this.initialState = s;
                this.elementName = NameFinder.findElement(e);
                this.attributeName = NameFinder.findAttribute(e);
                this.hasText = TextFinder.find(e);
            }
            
            public NameClass getName(final int idx) {
                if (idx == 0) {
                    return this.elementName;
                }
                return this.attributeName;
            }
        }
    }
    
    public abstract static class StaticReference extends Reference
    {
        public final Automaton target;
        
        public StaticReference(final Automaton ta, final int _order) {
            super(_order);
            this.target = ta;
        }
        
        public final boolean isNullable() {
            return this.target.isNullable();
        }
        
        public void head(final Set result, final Set visitedStates, final boolean includeEE) {
            this.target.getInitialState().head(result, visitedStates, includeEE);
        }
    }
    
    public static final class Child extends StaticReference
    {
        public final FieldRenderer field;
        
        public Child(final Automaton ta, final FieldRenderer _field, final int order) {
            super(ta, order);
            this.field = _field;
        }
        
        public void accept(final AlphabetVisitor visitor) {
            visitor.onChild(this);
        }
        
        protected void accept(final TransitionVisitor visitor, final Transition t) {
            visitor.onChild(this, t.to);
        }
        
        public String toString() {
            return "child[" + this.target.getOwner().target.name + "]";
        }
    }
    
    public static final class SuperClass extends StaticReference
    {
        public SuperClass(final Automaton ta, final int order) {
            super(ta, order);
        }
        
        public void accept(final AlphabetVisitor visitor) {
            visitor.onSuper(this);
        }
        
        protected void accept(final TransitionVisitor visitor, final Transition t) {
            visitor.onSuper(this, t.to);
        }
        
        public String toString() {
            return "super[" + this.target.getOwner().target.name + "]";
        }
    }
    
    public static final class Dispatch extends Alphabet
    {
        public final LookupTable table;
        public final SimpleNameClass attName;
        public final FieldRenderer field;
        
        public Dispatch(final LookupTableUse _tableUse, final FieldRenderer _field, final int _order) {
            super(_order);
            this.table = _tableUse.table;
            this.attName = _tableUse.switchAttName;
            this.field = _field;
        }
        
        protected void accept(final TransitionVisitor visitor, final Transition t) {
            visitor.onDispatch(this, t.to);
        }
        
        public void accept(final AlphabetVisitor visitor) {
            visitor.onDispatch(this);
        }
        
        public String toString() {
            return "dispatch[@" + this.attName + "]";
        }
    }
    
    public static final class External extends Reference
    {
        public final ExternalItem owner;
        public final FieldRenderer field;
        private final EnterElement head;
        
        public External(final ExternalItem _owner, final FieldRenderer _field, final int _order) {
            super(_order);
            this.owner = _owner;
            this.field = _field;
            this.head = new EnterElement(_order, this.owner.elementName, false);
        }
        
        public boolean isNullable() {
            return false;
        }
        
        public void head(final Set result, final Set visitedStates, final boolean includeEE) {
            result.add(this.head);
        }
        
        public void accept(final AlphabetVisitor visitor) {
            visitor.onExternal(this);
        }
        
        protected void accept(final TransitionVisitor visitor, final Transition t) {
            visitor.onExternal(this, t.to);
        }
        
        public String toString() {
            return "external[" + this.owner.toString() + "]";
        }
    }
    
    public abstract static class Text extends Alphabet
    {
        public Text(final int _order) {
            super(_order);
        }
    }
    
    public static final class BoundText extends Text
    {
        public final PrimitiveItem item;
        public final FieldRenderer field;
        
        public BoundText(final int _order, final PrimitiveItem _item, final FieldRenderer _field) {
            super(_order);
            this.item = _item;
            this.field = _field;
        }
        
        public void accept(final AlphabetVisitor visitor) {
            visitor.onBoundText(this);
        }
        
        protected void accept(final TransitionVisitor visitor, final Transition t) {
            visitor.onBoundText(this, t.to);
        }
        
        public String toString() {
            return "text";
        }
    }
    
    public static final class IgnoredText extends Text
    {
        public IgnoredText(final int _order) {
            super(_order);
        }
        
        public void accept(final AlphabetVisitor visitor) {
            visitor.onIgnoredText(this);
        }
        
        protected void accept(final TransitionVisitor visitor, final Transition t) {
            visitor.onIgnoredText(this, t.to);
        }
        
        public String toString() {
            return "ignoredText";
        }
    }
    
    public static final class EverythingElse extends Alphabet
    {
        public static final Alphabet theInstance;
        
        private EverythingElse() {
            super(-1);
        }
        
        public void accept(final AlphabetVisitor visitor) {
            visitor.onEverythingElse(this);
        }
        
        protected void accept(final TransitionVisitor visitor, final Transition t) {
            visitor.onEverythingElse(this, t.to);
        }
        
        public String toString() {
            return "*";
        }
        
        static {
            EverythingElse.theInstance = (Alphabet)new EverythingElse();
        }
    }
}
