// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

public class Person
{
    private String name;
    
    public Person(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Person person = (Person)o;
        return this.name.equals(person.name);
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    @Override
    public String toString() {
        return this.getName();
    }
}
