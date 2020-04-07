// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl.scd;

import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.xml.xsom.XSUnionSimpleType;
import com.sun.xml.xsom.XSListSimpleType;
import com.sun.xml.xsom.XSAttContainer;
import com.sun.xml.xsom.XSAttributeUse;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSComplexType;
import java.util.Iterator;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSComponent;

public interface Axis<T extends XSComponent>
{
    public static final Axis<XSSchema> ROOT = new Axis<XSSchema>() {
        public Iterator<XSSchema> iterator(final XSComponent contextNode) {
            return contextNode.getRoot().iterateSchema();
        }
        
        public Iterator<XSSchema> iterator(final Iterator<? extends XSComponent> contextNodes) {
            if (!contextNodes.hasNext()) {
                return Iterators.empty();
            }
            return this.iterator((XSComponent)contextNodes.next());
        }
        
        public boolean isModelGroup() {
            return false;
        }
        
        public String toString() {
            return "root::";
        }
    };
    public static final Axis<XSComponent> INTERMEDIATE_SKIP = new AbstractAxisImpl<XSComponent>() {
        public Iterator<XSComponent> elementDecl(final XSElementDecl decl) {
            final XSComplexType ct = decl.getType().asComplexType();
            if (ct == null) {
                return this.empty();
            }
            return new Iterators.Union<XSComponent>((Iterator<? extends XSComponent>)((AbstractAxisImpl<? extends T>)this).singleton((T)ct), this.complexType(ct));
        }
        
        public Iterator<XSComponent> modelGroupDecl(final XSModelGroupDecl decl) {
            return this.descendants(decl.getModelGroup());
        }
        
        public Iterator<XSComponent> particle(final XSParticle particle) {
            return this.descendants(particle.getTerm().asModelGroup());
        }
        
        private Iterator<XSComponent> descendants(final XSModelGroup mg) {
            final List<XSComponent> r = new ArrayList<XSComponent>();
            this.visit(mg, r);
            return r.iterator();
        }
        
        private void visit(final XSModelGroup mg, final List<XSComponent> r) {
            r.add(mg);
            for (final XSParticle p : mg) {
                final XSModelGroup child = p.getTerm().asModelGroup();
                if (child != null) {
                    this.visit(child, r);
                }
            }
        }
        
        public String toString() {
            return "(intermediateSkip)";
        }
    };
    public static final Axis<XSComponent> DESCENDANTS = new Axis<XSComponent>() {
        public Iterator<XSComponent> iterator(final XSComponent contextNode) {
            return new Visitor().iterator(contextNode);
        }
        
        public Iterator<XSComponent> iterator(final Iterator<? extends XSComponent> contextNodes) {
            return new Visitor().iterator(contextNodes);
        }
        
        public boolean isModelGroup() {
            return false;
        }
        
        public String toString() {
            return "/";
        }
        
        final class Visitor extends AbstractAxisImpl<XSComponent>
        {
            private final Set<XSComponent> visited;
            
            Visitor() {
                this.visited = new HashSet<XSComponent>();
            }
            
            public Iterator<XSComponent> schema(final XSSchema schema) {
                if (this.visited.add(schema)) {
                    return this.ret(schema, (Iterator<? extends XSComponent>)new Recursion(schema.iterateElementDecls()));
                }
                return this.empty();
            }
            
            public Iterator<XSComponent> elementDecl(final XSElementDecl decl) {
                if (this.visited.add(decl)) {
                    return this.ret(decl, this.iterator(decl.getType()));
                }
                return this.empty();
            }
            
            public Iterator<XSComponent> simpleType(final XSSimpleType type) {
                if (this.visited.add(type)) {
                    return this.ret(type, Visitor.FACET.iterator(type));
                }
                return this.empty();
            }
            
            public Iterator<XSComponent> complexType(final XSComplexType type) {
                if (this.visited.add(type)) {
                    return this.ret(type, this.iterator(type.getContentType()));
                }
                return this.empty();
            }
            
            public Iterator<XSComponent> particle(final XSParticle particle) {
                if (this.visited.add(particle)) {
                    return this.ret(particle, this.iterator(particle.getTerm()));
                }
                return this.empty();
            }
            
            public Iterator<XSComponent> modelGroupDecl(final XSModelGroupDecl decl) {
                if (this.visited.add(decl)) {
                    return this.ret(decl, this.iterator(decl.getModelGroup()));
                }
                return this.empty();
            }
            
            public Iterator<XSComponent> modelGroup(final XSModelGroup group) {
                if (this.visited.add(group)) {
                    return this.ret(group, (Iterator<? extends XSComponent>)new Recursion(group.iterator()));
                }
                return this.empty();
            }
            
            public Iterator<XSComponent> attGroupDecl(final XSAttGroupDecl decl) {
                if (this.visited.add(decl)) {
                    return this.ret(decl, (Iterator<? extends XSComponent>)new Recursion(decl.iterateAttributeUses()));
                }
                return this.empty();
            }
            
            public Iterator<XSComponent> attributeUse(final XSAttributeUse use) {
                if (this.visited.add(use)) {
                    return this.ret(use, this.iterator(use.getDecl()));
                }
                return this.empty();
            }
            
            public Iterator<XSComponent> attributeDecl(final XSAttributeDecl decl) {
                if (this.visited.add(decl)) {
                    return this.ret(decl, this.iterator(decl.getType()));
                }
                return this.empty();
            }
            
            private Iterator<XSComponent> ret(final XSComponent one, final Iterator<? extends XSComponent> rest) {
                return this.union(this.singleton(one), rest);
            }
            
            final class Recursion extends Iterators.Map<XSComponent, XSComponent>
            {
                public Recursion(final Iterator<? extends XSComponent> core) {
                    super(core);
                }
                
                protected Iterator<XSComponent> apply(final XSComponent u) {
                    return Axis.DESCENDANTS.iterator(u);
                }
            }
        }
    };
    public static final Axis<XSSchema> X_SCHEMA = new Axis<XSSchema>() {
        public Iterator<XSSchema> iterator(final XSComponent contextNode) {
            return Iterators.singleton(contextNode.getOwnerSchema());
        }
        
        public Iterator<XSSchema> iterator(final Iterator<? extends XSComponent> contextNodes) {
            return (Iterator<XSSchema>)new Iterators.Adapter<XSSchema, XSComponent>(contextNodes) {
                protected XSSchema filter(final XSComponent u) {
                    return u.getOwnerSchema();
                }
            };
        }
        
        public boolean isModelGroup() {
            return false;
        }
        
        public String toString() {
            return "x-schema::";
        }
    };
    public static final Axis<XSElementDecl> SUBSTITUTION_GROUP = new AbstractAxisImpl<XSElementDecl>() {
        public Iterator<XSElementDecl> elementDecl(final XSElementDecl decl) {
            return this.singleton(decl.getSubstAffiliation());
        }
        
        public String toString() {
            return "substitutionGroup::";
        }
    };
    public static final Axis<XSAttributeDecl> ATTRIBUTE = new AbstractAxisImpl<XSAttributeDecl>() {
        public Iterator<XSAttributeDecl> complexType(final XSComplexType type) {
            return this.attributeHolder(type);
        }
        
        public Iterator<XSAttributeDecl> attGroupDecl(final XSAttGroupDecl decl) {
            return this.attributeHolder(decl);
        }
        
        private Iterator<XSAttributeDecl> attributeHolder(final XSAttContainer atts) {
            return (Iterator<XSAttributeDecl>)new Iterators.Adapter<XSAttributeDecl, XSAttributeUse>(atts.iterateAttributeUses()) {
                protected XSAttributeDecl filter(final XSAttributeUse u) {
                    return u.getDecl();
                }
            };
        }
        
        public Iterator<XSAttributeDecl> schema(final XSSchema schema) {
            return schema.iterateAttributeDecls();
        }
        
        public String toString() {
            return "@";
        }
    };
    public static final Axis<XSElementDecl> ELEMENT = new AbstractAxisImpl<XSElementDecl>() {
        public Iterator<XSElementDecl> particle(final XSParticle particle) {
            return this.singleton(particle.getTerm().asElementDecl());
        }
        
        public Iterator<XSElementDecl> schema(final XSSchema schema) {
            return schema.iterateElementDecls();
        }
        
        public Iterator<XSElementDecl> modelGroupDecl(final XSModelGroupDecl decl) {
            return this.modelGroup(decl.getModelGroup());
        }
        
        public String getName() {
            return "";
        }
        
        public String toString() {
            return "element::";
        }
    };
    public static final Axis<XSType> TYPE_DEFINITION = new AbstractAxisImpl<XSType>() {
        public Iterator<XSType> schema(final XSSchema schema) {
            return schema.iterateTypes();
        }
        
        public Iterator<XSType> attributeDecl(final XSAttributeDecl decl) {
            return (Iterator<XSType>)((AbstractAxisImpl<XSSimpleType>)this).singleton(decl.getType());
        }
        
        public Iterator<XSType> elementDecl(final XSElementDecl decl) {
            return this.singleton(decl.getType());
        }
        
        public String toString() {
            return "~";
        }
    };
    public static final Axis<XSType> BASETYPE = new AbstractAxisImpl<XSType>() {
        public Iterator<XSType> simpleType(final XSSimpleType type) {
            return this.singleton(type.getBaseType());
        }
        
        public Iterator<XSType> complexType(final XSComplexType type) {
            return this.singleton(type.getBaseType());
        }
        
        public String toString() {
            return "baseType::";
        }
    };
    public static final Axis<XSSimpleType> PRIMITIVE_TYPE = new AbstractAxisImpl<XSSimpleType>() {
        public Iterator<XSSimpleType> simpleType(final XSSimpleType type) {
            return this.singleton(type.getPrimitiveType());
        }
        
        public String toString() {
            return "primitiveType::";
        }
    };
    public static final Axis<XSSimpleType> ITEM_TYPE = new AbstractAxisImpl<XSSimpleType>() {
        public Iterator<XSSimpleType> simpleType(final XSSimpleType type) {
            final XSListSimpleType baseList = type.getBaseListType();
            if (baseList == null) {
                return this.empty();
            }
            return this.singleton(baseList.getItemType());
        }
        
        public String toString() {
            return "itemType::";
        }
    };
    public static final Axis<XSSimpleType> MEMBER_TYPE = new AbstractAxisImpl<XSSimpleType>() {
        public Iterator<XSSimpleType> simpleType(final XSSimpleType type) {
            final XSUnionSimpleType baseUnion = type.getBaseUnionType();
            if (baseUnion == null) {
                return this.empty();
            }
            return baseUnion.iterator();
        }
        
        public String toString() {
            return "memberType::";
        }
    };
    public static final Axis<XSComponent> SCOPE = new AbstractAxisImpl<XSComponent>() {
        public Iterator<XSComponent> complexType(final XSComplexType type) {
            return (Iterator<XSComponent>)((AbstractAxisImpl<XSElementDecl>)this).singleton(type.getScope());
        }
        
        public String toString() {
            return "scope::";
        }
    };
    public static final Axis<XSAttGroupDecl> ATTRIBUTE_GROUP = new AbstractAxisImpl<XSAttGroupDecl>() {
        public Iterator<XSAttGroupDecl> schema(final XSSchema schema) {
            return schema.iterateAttGroupDecls();
        }
        
        public String toString() {
            return "attributeGroup::";
        }
    };
    public static final Axis<XSModelGroupDecl> MODEL_GROUP_DECL = new AbstractAxisImpl<XSModelGroupDecl>() {
        public Iterator<XSModelGroupDecl> schema(final XSSchema schema) {
            return schema.iterateModelGroupDecls();
        }
        
        public Iterator<XSModelGroupDecl> particle(final XSParticle particle) {
            return this.singleton(particle.getTerm().asModelGroupDecl());
        }
        
        public String toString() {
            return "group::";
        }
    };
    public static final Axis<XSIdentityConstraint> IDENTITY_CONSTRAINT = new AbstractAxisImpl<XSIdentityConstraint>() {
        public Iterator<XSIdentityConstraint> elementDecl(final XSElementDecl decl) {
            return decl.getIdentityConstraints().iterator();
        }
        
        public Iterator<XSIdentityConstraint> schema(final XSSchema schema) {
            return super.schema(schema);
        }
        
        public String toString() {
            return "identityConstraint::";
        }
    };
    public static final Axis<XSIdentityConstraint> REFERENCED_KEY = new AbstractAxisImpl<XSIdentityConstraint>() {
        public Iterator<XSIdentityConstraint> identityConstraint(final XSIdentityConstraint decl) {
            return this.singleton(decl.getReferencedKey());
        }
        
        public String toString() {
            return "key::";
        }
    };
    public static final Axis<XSNotation> NOTATION = new AbstractAxisImpl<XSNotation>() {
        public Iterator<XSNotation> schema(final XSSchema schema) {
            return schema.iterateNotations();
        }
        
        public String toString() {
            return "notation::";
        }
    };
    public static final Axis<XSWildcard> WILDCARD = new AbstractAxisImpl<XSWildcard>() {
        public Iterator<XSWildcard> particle(final XSParticle particle) {
            return this.singleton(particle.getTerm().asWildcard());
        }
        
        public String toString() {
            return "any::";
        }
    };
    public static final Axis<XSWildcard> ATTRIBUTE_WILDCARD = new AbstractAxisImpl<XSWildcard>() {
        public Iterator<XSWildcard> complexType(final XSComplexType type) {
            return this.singleton(type.getAttributeWildcard());
        }
        
        public Iterator<XSWildcard> attGroupDecl(final XSAttGroupDecl decl) {
            return this.singleton(decl.getAttributeWildcard());
        }
        
        public String toString() {
            return "anyAttribute::";
        }
    };
    public static final Axis<XSFacet> FACET = new AbstractAxisImpl<XSFacet>() {
        public Iterator<XSFacet> simpleType(final XSSimpleType type) {
            final XSRestrictionSimpleType r = type.asRestriction();
            if (r != null) {
                return r.iterateDeclaredFacets();
            }
            return this.empty();
        }
        
        public String toString() {
            return "facet::";
        }
    };
    public static final Axis<XSModelGroup> MODELGROUP_ALL = new ModelGroupAxis(XSModelGroup.Compositor.ALL);
    public static final Axis<XSModelGroup> MODELGROUP_CHOICE = new ModelGroupAxis(XSModelGroup.Compositor.CHOICE);
    public static final Axis<XSModelGroup> MODELGROUP_SEQUENCE = new ModelGroupAxis(XSModelGroup.Compositor.SEQUENCE);
    public static final Axis<XSModelGroup> MODELGROUP_ANY = new ModelGroupAxis(null);
    
    Iterator<T> iterator(final XSComponent p0);
    
    Iterator<T> iterator(final Iterator<? extends XSComponent> p0);
    
    boolean isModelGroup();
    
    public static final class ModelGroupAxis extends AbstractAxisImpl<XSModelGroup>
    {
        private final XSModelGroup.Compositor compositor;
        
        ModelGroupAxis(final XSModelGroup.Compositor compositor) {
            this.compositor = compositor;
        }
        
        public boolean isModelGroup() {
            return true;
        }
        
        public Iterator<XSModelGroup> particle(final XSParticle particle) {
            return this.filter(particle.getTerm().asModelGroup());
        }
        
        public Iterator<XSModelGroup> modelGroupDecl(final XSModelGroupDecl decl) {
            return this.filter(decl.getModelGroup());
        }
        
        private Iterator<XSModelGroup> filter(final XSModelGroup mg) {
            if (mg == null) {
                return this.empty();
            }
            if (mg.getCompositor() == this.compositor || this.compositor == null) {
                return this.singleton(mg);
            }
            return this.empty();
        }
        
        public String toString() {
            if (this.compositor == null) {
                return "model::*";
            }
            return "model::" + this.compositor;
        }
    }
}
