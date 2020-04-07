// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.jnl;

import com.sun.deploy.xml.XMLAttribute;
import com.sun.deploy.xml.XMLNodeBuilder;
import com.sun.deploy.xml.XMLAttributeBuilder;
import com.sun.deploy.xml.XMLNode;
import com.sun.javaws.exceptions.JNLPSigningException;
import java.net.URL;
import com.sun.deploy.xml.XMLable;

public class LaunchDesc implements XMLable
{
    private String _specVersion;
    private String _version;
    private URL _home;
    private URL _codebase;
    private InformationDesc _information;
    private int _securiyModel;
    private ResourcesDesc _resources;
    private int _launchType;
    private ApplicationDesc _applicationDesc;
    private AppletDesc _appletDesc;
    private LibraryDesc _libraryDesc;
    private InstallerDesc _installerDesc;
    private String _internalCommand;
    private String _source;
    private boolean _propsSet;
    private byte[] _bits;
    public static final int SANDBOX_SECURITY = 0;
    public static final int ALLPERMISSIONS_SECURITY = 1;
    public static final int J2EE_APP_CLIENT_SECURITY = 2;
    public static final int APPLICATION_DESC_TYPE = 1;
    public static final int APPLET_DESC_TYPE = 2;
    public static final int LIBRARY_DESC_TYPE = 3;
    public static final int INSTALLER_DESC_TYPE = 4;
    public static final int INTERNAL_TYPE = 5;
    
    public LaunchDesc(final String specVersion, final URL codebase, final URL home, final String version, final InformationDesc information, final int securiyModel, final ResourcesDesc resources, final int launchType, final ApplicationDesc applicationDesc, final AppletDesc appletDesc, final LibraryDesc libraryDesc, final InstallerDesc installerDesc, final String internalCommand, final String source, final byte[] bits) {
        this._propsSet = false;
        this._specVersion = specVersion;
        this._version = version;
        this._codebase = codebase;
        this._home = home;
        this._information = information;
        this._securiyModel = securiyModel;
        this._resources = resources;
        this._launchType = launchType;
        this._applicationDesc = applicationDesc;
        this._appletDesc = appletDesc;
        this._libraryDesc = libraryDesc;
        this._installerDesc = installerDesc;
        this._internalCommand = internalCommand;
        this._source = source;
        this._bits = bits;
        if (this._resources != null) {
            this._resources.setParent(this);
        }
    }
    
    public String getSpecVersion() {
        return this._specVersion;
    }
    
    public synchronized URL getCodebase() {
        return this._codebase;
    }
    
    public byte[] getBytes() {
        return this._bits;
    }
    
    public synchronized URL getLocation() {
        return this._home;
    }
    
    public synchronized URL getCanonicalHome() {
        if (this._home == null && this._resources != null) {
            final JARDesc mainJar = this._resources.getMainJar(true);
            return (mainJar != null) ? mainJar.getLocation() : null;
        }
        return this._home;
    }
    
    public InformationDesc getInformation() {
        return this._information;
    }
    
    public String getInternalCommand() {
        return this._internalCommand;
    }
    
    public int getSecurityModel() {
        return this._securiyModel;
    }
    
    public ResourcesDesc getResources() {
        return this._resources;
    }
    
    public boolean arePropsSet() {
        return this._propsSet;
    }
    
    public void setPropsSet(final boolean propsSet) {
        this._propsSet = propsSet;
    }
    
    public int getLaunchType() {
        return this._launchType;
    }
    
    public ApplicationDesc getApplicationDescriptor() {
        return this._applicationDesc;
    }
    
    public AppletDesc getAppletDescriptor() {
        return this._appletDesc;
    }
    
    public InstallerDesc getInstallerDescriptor() {
        return this._installerDesc;
    }
    
    public boolean isApplication() {
        return this._launchType == 1;
    }
    
    public boolean isApplet() {
        return this._launchType == 2;
    }
    
    public boolean isLibrary() {
        return this._launchType == 3;
    }
    
    public boolean isInstaller() {
        return this._launchType == 4;
    }
    
    public boolean isApplicationDescriptor() {
        return this.isApplication() || this.isApplet();
    }
    
    public boolean isHttps() {
        return this._codebase.getProtocol().equals("https");
    }
    
    public String getSource() {
        return this._source;
    }
    
    public void checkSigning(final LaunchDesc launchDesc) throws JNLPSigningException {
        if (!launchDesc.getSource().equals(this.getSource())) {
            throw new JNLPSigningException(this, launchDesc.getSource());
        }
    }
    
    public boolean isJRESpecified() {
        final boolean[] array = { false };
        final boolean[] array2 = { false };
        if (this.getResources() != null) {
            this.getResources().visit(new ResourceVisitor() {
                public void visitJARDesc(final JARDesc jarDesc) {
                    array2[0] = true;
                }
                
                public void visitPropertyDesc(final PropertyDesc propertyDesc) {
                }
                
                public void visitPackageDesc(final PackageDesc packageDesc) {
                }
                
                public void visitExtensionDesc(final ExtensionDesc extensionDesc) {
                    array2[0] = true;
                }
                
                public void visitJREDesc(final JREDesc jreDesc) {
                    array[0] = true;
                }
            });
        }
        if (this._launchType == 1 || this._launchType == 2) {
            array2[0] = true;
        }
        return array[0] || !array2[0];
    }
    
    public XMLNode asXML() {
        final XMLAttributeBuilder xmlAttributeBuilder = new XMLAttributeBuilder();
        xmlAttributeBuilder.add("spec", this._specVersion);
        xmlAttributeBuilder.add("codebase", this._codebase);
        xmlAttributeBuilder.add("version", this._version);
        xmlAttributeBuilder.add("href", this._home);
        final XMLNodeBuilder xmlNodeBuilder = new XMLNodeBuilder("jnlp", xmlAttributeBuilder.getAttributeList());
        xmlNodeBuilder.add((XMLable)this._information);
        if (this._securiyModel == 1) {
            xmlNodeBuilder.add(new XMLNode("security", (XMLAttribute)null, new XMLNode("all-permissions", (XMLAttribute)null), (XMLNode)null));
        }
        else if (this._securiyModel == 2) {
            xmlNodeBuilder.add(new XMLNode("security", (XMLAttribute)null, new XMLNode("j2ee-application-client-permissions", (XMLAttribute)null), (XMLNode)null));
        }
        xmlNodeBuilder.add((XMLable)this._resources);
        xmlNodeBuilder.add((XMLable)this._applicationDesc);
        xmlNodeBuilder.add((XMLable)this._appletDesc);
        xmlNodeBuilder.add((XMLable)this._libraryDesc);
        xmlNodeBuilder.add((XMLable)this._installerDesc);
        return xmlNodeBuilder.getNode();
    }
    
    public String toString() {
        return this.asXML().toString();
    }
}
