// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

import org.w3c.dom.Element;

public class PersonWithRole extends Person
{
    private String role;
    
    public PersonWithRole(final String name) {
        super(name);
    }
    
    public PersonWithRole(final String name, final String role) {
        super(name);
        this.role = role;
    }
    
    public String getRole() {
        return this.role;
    }
    
    public void setOnElement(final Element element) {
        element.setTextContent(this.toString());
        if (this.getRole() != null) {
            element.setAttribute("role", this.getRole());
        }
    }
}
