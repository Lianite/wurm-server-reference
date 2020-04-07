// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import java.io.File;

public class DummyTestClass extends XMLSerializer
{
    @Saved
    long test;
    @Saved
    String myClass;
    @Saved
    float saveThis;
    final float dontSave = 0.9333222f;
    
    public DummyTestClass() {
        this.test = 0L;
        this.myClass = "my Class is dummy";
        this.saveThis = 3.24324E-4f;
    }
    
    public long getTest() {
        return this.test;
    }
    
    public void setTest(final long aTest) {
        this.test = aTest;
    }
    
    public String getMyClass() {
        return this.myClass;
    }
    
    public void setMyClass(final String aMyClass) {
        this.myClass = aMyClass;
    }
    
    public float getDontSave() {
        return 0.9333222f;
    }
    
    public void setSaveThis(final float aSaveThis) {
        this.saveThis = aSaveThis;
    }
    
    public float getSaveThis() {
        return this.saveThis;
    }
    
    @Override
    public final DummyTestClass createInstanceAndCallLoadXML(final File file) {
        this.loadXML(file);
        return this;
    }
}
