// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.jnl;

import com.sun.deploy.xml.XMLable;
import com.sun.deploy.xml.XMLAttribute;
import com.sun.deploy.xml.XMLNodeBuilder;
import com.sun.deploy.xml.XMLNode;
import java.util.Properties;
import com.sun.deploy.util.URLUtil;
import com.sun.javaws.util.VersionString;
import java.net.URL;
import com.sun.javaws.cache.DiskCacheEntry;
import com.sun.deploy.util.Trace;
import com.sun.javaws.cache.DownloadProtocol;
import com.sun.deploy.config.JREInfo;
import java.util.HashSet;
import java.util.ArrayList;

public class ResourcesDesc implements ResourceType
{
    private ArrayList _list;
    private LaunchDesc _parent;
    
    public ResourcesDesc() {
        this._list = null;
        this._parent = null;
        this._list = new ArrayList();
    }
    
    public LaunchDesc getParent() {
        return this._parent;
    }
    
    void setParent(final LaunchDesc launchDesc) {
        this._parent = launchDesc;
        for (int i = 0; i < this._list.size(); ++i) {
            final Object value = this._list.get(i);
            if (value instanceof JREDesc) {
                final JREDesc jreDesc = (JREDesc)value;
                if (jreDesc.getNestedResources() != null) {
                    jreDesc.getNestedResources().setParent(launchDesc);
                }
            }
        }
    }
    
    void addResource(final ResourceType resourceType) {
        if (resourceType != null) {
            this._list.add(resourceType);
        }
    }
    
    boolean isEmpty() {
        return this._list.isEmpty();
    }
    
    public JREDesc getSelectedJRE() {
        for (int i = 0; i < this._list.size(); ++i) {
            final Object value = this._list.get(i);
            if (value instanceof JREDesc && ((JREDesc)value).isSelected()) {
                return (JREDesc)value;
            }
        }
        return null;
    }
    
    public JARDesc[] getLocalJarDescs() {
        final ArrayList<Object> list = new ArrayList<Object>(this._list.size());
        for (int i = 0; i < this._list.size(); ++i) {
            final Object value = this._list.get(i);
            if (value instanceof JARDesc) {
                list.add(value);
            }
        }
        return this.toJARDescArray(list);
    }
    
    public ExtensionDesc[] getExtensionDescs() {
        final ArrayList list = new ArrayList();
        final ExtensionDesc[] array = new ExtensionDesc[0];
        this.visit(new ResourceVisitor() {
            public void visitJARDesc(final JARDesc jarDesc) {
            }
            
            public void visitPropertyDesc(final PropertyDesc propertyDesc) {
            }
            
            public void visitExtensionDesc(final ExtensionDesc extensionDesc) {
                list.add(extensionDesc);
            }
            
            public void visitJREDesc(final JREDesc jreDesc) {
            }
            
            public void visitPackageDesc(final PackageDesc packageDesc) {
            }
        });
        return list.toArray(array);
    }
    
    public JARDesc[] getEagerOrAllJarDescs(final boolean b) {
        final HashSet set = new HashSet();
        if (!b) {
            this.visit(new ResourceVisitor() {
                public void visitJARDesc(final JARDesc jarDesc) {
                    if (!jarDesc.isLazyDownload() && jarDesc.getPartName() != null) {
                        set.add(jarDesc.getPartName());
                    }
                }
                
                public void visitPropertyDesc(final PropertyDesc propertyDesc) {
                }
                
                public void visitExtensionDesc(final ExtensionDesc extensionDesc) {
                }
                
                public void visitJREDesc(final JREDesc jreDesc) {
                }
                
                public void visitPackageDesc(final PackageDesc packageDesc) {
                }
            });
        }
        final ArrayList list = new ArrayList();
        this.addJarsToList(list, set, b, true);
        return this.toJARDescArray(list);
    }
    
    private void addJarsToList(final ArrayList list, final HashSet set, final boolean b, final boolean b2) {
        this.visit(new ResourceVisitor() {
            public void visitJARDesc(final JARDesc jarDesc) {
                if (b || (b2 && !jarDesc.isLazyDownload()) || set.contains(jarDesc.getPartName())) {
                    list.add(jarDesc);
                }
            }
            
            public void visitPropertyDesc(final PropertyDesc propertyDesc) {
            }
            
            public void visitExtensionDesc(final ExtensionDesc extensionDesc) {
                final HashSet extensionPackages = extensionDesc.getExtensionPackages(set, b2);
                if (extensionDesc.getExtensionDesc() == null) {
                    final String knownPlatforms = JREInfo.getKnownPlatforms();
                    try {
                        final DiskCacheEntry cachedExtension = DownloadProtocol.getCachedExtension(extensionDesc.getLocation(), extensionDesc.getVersion(), knownPlatforms);
                        if (cachedExtension != null && cachedExtension.getFile() != null) {
                            extensionDesc.setExtensionDesc(LaunchDescFactory.buildDescriptor(cachedExtension.getFile()));
                        }
                    }
                    catch (Exception ex) {
                        Trace.ignoredException(ex);
                    }
                }
                if (extensionDesc.getExtensionDesc() != null) {
                    final ResourcesDesc resources = extensionDesc.getExtensionDesc().getResources();
                    if (resources != null) {
                        resources.addJarsToList(list, extensionPackages, b, b2);
                    }
                }
            }
            
            public void visitJREDesc(final JREDesc jreDesc) {
                if (jreDesc.isSelected()) {
                    final ResourcesDesc nestedResources = jreDesc.getNestedResources();
                    if (nestedResources != null) {
                        nestedResources.addJarsToList(list, set, b, b2);
                    }
                    if (jreDesc.getExtensionDesc() != null) {
                        final ResourcesDesc resources = jreDesc.getExtensionDesc().getResources();
                        if (resources != null) {
                            resources.addJarsToList(list, new HashSet(), b, b2);
                        }
                    }
                }
            }
            
            public void visitPackageDesc(final PackageDesc packageDesc) {
            }
        });
    }
    
    public JARDesc[] getPartJars(final String[] array) {
        final HashSet<String> set = new HashSet<String>();
        for (int i = 0; i < array.length; ++i) {
            set.add(array[i]);
        }
        final ArrayList list = new ArrayList();
        this.addJarsToList(list, set, false, false);
        return this.toJARDescArray(list);
    }
    
    public JARDesc[] getPartJars(final String s) {
        return this.getPartJars(new String[] { s });
    }
    
    public JARDesc[] getResource(final URL url, final String s) {
        final VersionString versionString = (s != null) ? new VersionString(s) : null;
        final JARDesc[] array = { null };
        this.visit(new ResourceVisitor() {
            public void visitJARDesc(final JARDesc jarDesc) {
                if (URLUtil.equals(jarDesc.getLocation(), url)) {
                    if (versionString == null) {
                        array[0] = jarDesc;
                    }
                    else if (versionString.contains(jarDesc.getVersion())) {
                        array[0] = jarDesc;
                    }
                }
            }
            
            public void visitPropertyDesc(final PropertyDesc propertyDesc) {
            }
            
            public void visitExtensionDesc(final ExtensionDesc extensionDesc) {
            }
            
            public void visitJREDesc(final JREDesc jreDesc) {
            }
            
            public void visitPackageDesc(final PackageDesc packageDesc) {
            }
        });
        if (array[0] == null) {
            return null;
        }
        if (array[0].getPartName() != null) {
            return this.getPartJars(array[0].getPartName());
        }
        return array;
    }
    
    public JARDesc[] getExtensionPart(final URL url, final String s, final String[] array) {
        final ExtensionDesc extension = this.findExtension(url, s);
        if (extension == null) {
            return null;
        }
        final ResourcesDesc extensionResources = extension.getExtensionResources();
        if (extensionResources == null) {
            return null;
        }
        return extensionResources.getPartJars(array);
    }
    
    private ExtensionDesc findExtension(final URL url, final String s) {
        final ExtensionDesc[] array = { null };
        this.visit(new ResourceVisitor() {
            public void visitJARDesc(final JARDesc jarDesc) {
            }
            
            public void visitPropertyDesc(final PropertyDesc propertyDesc) {
            }
            
            public void visitExtensionDesc(final ExtensionDesc extensionDesc) {
                if (array[0] == null) {
                    if (URLUtil.equals(extensionDesc.getLocation(), url) && (s == null || new VersionString(s).contains(extensionDesc.getVersion()))) {
                        array[0] = extensionDesc;
                    }
                    else {
                        final LaunchDesc extensionDesc2 = extensionDesc.getExtensionDesc();
                        if (extensionDesc2 != null && extensionDesc2.getResources() != null) {
                            array[0] = extensionDesc2.getResources().findExtension(url, s);
                        }
                    }
                }
            }
            
            public void visitJREDesc(final JREDesc jreDesc) {
            }
            
            public void visitPackageDesc(final PackageDesc packageDesc) {
            }
        });
        return array[0];
    }
    
    public JARDesc getMainJar(final boolean b) {
        final JARDesc[] array = new JARDesc[2];
        this.visit(new ResourceVisitor() {
            public void visitJARDesc(final JARDesc jarDesc) {
                if (jarDesc.isJavaFile()) {
                    if (array[0] == null) {
                        array[0] = jarDesc;
                    }
                    if (jarDesc.isMainJarFile()) {
                        array[1] = jarDesc;
                    }
                }
            }
            
            public void visitPropertyDesc(final PropertyDesc propertyDesc) {
            }
            
            public void visitExtensionDesc(final ExtensionDesc extensionDesc) {
            }
            
            public void visitJREDesc(final JREDesc jreDesc) {
            }
            
            public void visitPackageDesc(final PackageDesc packageDesc) {
            }
        });
        final JARDesc jarDesc = array[0];
        final JARDesc jarDesc2 = array[1];
        return (jarDesc2 != null && b) ? jarDesc2 : jarDesc;
    }
    
    public JARDesc[] getPart(final String s) {
        final ArrayList list = new ArrayList();
        this.visit(new ResourceVisitor() {
            public void visitJARDesc(final JARDesc jarDesc) {
                if (s.equals(jarDesc.getPartName())) {
                    list.add(jarDesc);
                }
            }
            
            public void visitPropertyDesc(final PropertyDesc propertyDesc) {
            }
            
            public void visitExtensionDesc(final ExtensionDesc extensionDesc) {
            }
            
            public void visitJREDesc(final JREDesc jreDesc) {
            }
            
            public void visitPackageDesc(final PackageDesc packageDesc) {
            }
        });
        return this.toJARDescArray(list);
    }
    
    public JARDesc[] getExtensionPart(final URL url, final String s, final String s2) {
        final JARDesc[][] array = { null };
        this.visit(new ResourceVisitor() {
            public void visitJARDesc(final JARDesc jarDesc) {
            }
            
            public void visitPropertyDesc(final PropertyDesc propertyDesc) {
            }
            
            public void visitExtensionDesc(final ExtensionDesc extensionDesc) {
                if (URLUtil.equals(extensionDesc.getLocation(), url)) {
                    if (s == null) {
                        if (extensionDesc.getVersion() == null && extensionDesc.getExtensionResources() != null) {
                            array[0] = extensionDesc.getExtensionResources().getPart(s2);
                        }
                    }
                    else if (s.equals(extensionDesc.getVersion()) && extensionDesc.getExtensionResources() != null) {
                        array[0] = extensionDesc.getExtensionResources().getPart(s2);
                    }
                }
            }
            
            public void visitJREDesc(final JREDesc jreDesc) {
            }
            
            public void visitPackageDesc(final PackageDesc packageDesc) {
            }
        });
        return array[0];
    }
    
    private JARDesc[] toJARDescArray(final ArrayList list) {
        return list.toArray(new JARDesc[list.size()]);
    }
    
    public Properties getResourceProperties() {
        final Properties properties = new Properties();
        this.visit(new ResourceVisitor() {
            public void visitPropertyDesc(final PropertyDesc propertyDesc) {
                properties.setProperty(propertyDesc.getKey(), propertyDesc.getValue());
            }
            
            public void visitJARDesc(final JARDesc jarDesc) {
            }
            
            public void visitExtensionDesc(final ExtensionDesc extensionDesc) {
            }
            
            public void visitJREDesc(final JREDesc jreDesc) {
            }
            
            public void visitPackageDesc(final PackageDesc packageDesc) {
            }
        });
        return properties;
    }
    
    public PackageInformation getPackageInformation(String s) {
        s = s.replace('/', '.');
        if (s.endsWith(".class")) {
            s = s.substring(0, s.length() - 6);
        }
        return visitPackageElements(this.getParent(), s);
    }
    
    public boolean isPackagePart(final String s) {
        final boolean[] array = { false };
        this.visit(new ResourceVisitor() {
            public void visitJARDesc(final JARDesc jarDesc) {
            }
            
            public void visitPropertyDesc(final PropertyDesc propertyDesc) {
            }
            
            public void visitJREDesc(final JREDesc jreDesc) {
            }
            
            public void visitExtensionDesc(final ExtensionDesc extensionDesc) {
                if (!extensionDesc.isInstaller()) {
                    final LaunchDesc extensionDesc2 = extensionDesc.getExtensionDesc();
                    if (!array[0] && extensionDesc2.isLibrary() && extensionDesc2.getResources() != null) {
                        array[0] = extensionDesc2.getResources().isPackagePart(s);
                    }
                }
            }
            
            public void visitPackageDesc(final PackageDesc packageDesc) {
                if (packageDesc.getPart().equals(s)) {
                    array[0] = true;
                }
            }
        });
        return array[0];
    }
    
    private static PackageInformation visitPackageElements(final LaunchDesc launchDesc, final String s) {
        final PackageInformation[] array = { null };
        launchDesc.getResources().visit(new ResourceVisitor() {
            public void visitJARDesc(final JARDesc jarDesc) {
            }
            
            public void visitPropertyDesc(final PropertyDesc propertyDesc) {
            }
            
            public void visitJREDesc(final JREDesc jreDesc) {
            }
            
            public void visitExtensionDesc(final ExtensionDesc extensionDesc) {
                if (!extensionDesc.isInstaller()) {
                    final LaunchDesc extensionDesc2 = extensionDesc.getExtensionDesc();
                    if (array[0] == null && extensionDesc2.isLibrary() && extensionDesc2.getResources() != null) {
                        array[0] = visitPackageElements(extensionDesc2, s);
                    }
                }
            }
            
            public void visitPackageDesc(final PackageDesc packageDesc) {
                if (array[0] == null && packageDesc.match(s)) {
                    array[0] = new PackageInformation(launchDesc, packageDesc.getPart());
                }
            }
        });
        return array[0];
    }
    
    public void visit(final ResourceVisitor resourceVisitor) {
        for (int i = 0; i < this._list.size(); ++i) {
            ((ResourceType)this._list.get(i)).visit(resourceVisitor);
        }
    }
    
    public XMLNode asXML() {
        final XMLNodeBuilder xmlNodeBuilder = new XMLNodeBuilder("resources", (XMLAttribute)null);
        for (int i = 0; i < this._list.size(); ++i) {
            xmlNodeBuilder.add((XMLable)this._list.get(i));
        }
        return xmlNodeBuilder.getNode();
    }
    
    public void addNested(final ResourcesDesc resourcesDesc) {
        if (resourcesDesc != null) {
            resourcesDesc.visit(new ResourceVisitor() {
                public void visitJARDesc(final JARDesc jarDesc) {
                    ResourcesDesc.this._list.add(jarDesc);
                }
                
                public void visitPropertyDesc(final PropertyDesc propertyDesc) {
                    ResourcesDesc.this._list.add(propertyDesc);
                }
                
                public void visitExtensionDesc(final ExtensionDesc extensionDesc) {
                    ResourcesDesc.this._list.add(extensionDesc);
                }
                
                public void visitJREDesc(final JREDesc jreDesc) {
                }
                
                public void visitPackageDesc(final PackageDesc packageDesc) {
                }
            });
        }
    }
    
    public static class PackageInformation
    {
        private LaunchDesc _launchDesc;
        private String _part;
        
        PackageInformation(final LaunchDesc launchDesc, final String part) {
            this._launchDesc = launchDesc;
            this._part = part;
        }
        
        public LaunchDesc getLaunchDesc() {
            return this._launchDesc;
        }
        
        public String getPart() {
            return this._part;
        }
    }
}
