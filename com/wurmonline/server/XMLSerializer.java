// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Annotation;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import org.w3c.dom.Document;
import java.util.logging.Level;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.OutputStream;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.File;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.logging.Logger;

public abstract class XMLSerializer
{
    private static Logger logger;
    private static final String baseDirectory = "wurmDB";
    private static final String subDirectory = "test";
    private static final String subDirectoryDirectory = "base";
    private String fileName;
    private static final String dotXML = ".xml";
    private final Object[] emptyObjectArray;
    
    public XMLSerializer() {
        this.fileName = "xmlTest.xml";
        this.emptyObjectArray = new Object[0];
    }
    
    public LinkedList<Field> getSaveFields() {
        final LinkedList<Field> result = new LinkedList<Field>();
        for (Class<?> c = this.getClass(); c != null; c = c.getSuperclass()) {
            final Field[] declaredFields;
            final Field[] fields = declaredFields = c.getDeclaredFields();
            for (final Field classField : declaredFields) {
                if (classField.getAnnotation(Saved.class) != null) {
                    result.add(classField);
                }
            }
        }
        return result;
    }
    
    public Map<String, Field> getSaveFieldsMap() {
        final ConcurrentHashMap<String, Field> result = new ConcurrentHashMap<String, Field>();
        for (Class<?> c = this.getClass(); c != null; c = c.getSuperclass()) {
            final Field[] declaredFields;
            final Field[] fields = declaredFields = c.getDeclaredFields();
            for (final Field classField : declaredFields) {
                if (classField.getAnnotation(Saved.class) != null) {
                    result.put(classField.getName(), classField);
                }
            }
        }
        return result;
    }
    
    public final boolean saveXML() {
        final LinkedList<Field> result = this.getSaveFields();
        final boolean saved = this.saveToDisk(result);
        return saved;
    }
    
    private final boolean saveToDisk(final LinkedList<Field> result) {
        final long start = System.nanoTime();
        try {
            final Document document = this.createFieldsXmlDocument(result);
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            final DOMSource source = new DOMSource(document);
            final File dir = this.getRootDir();
            dir.mkdirs();
            final File file = new File(dir + File.separator + this.fileName);
            XMLSerializer.logger.info("Dumping fields to absolute path: " + file.getAbsolutePath());
            file.createNewFile();
            if (file != null) {
                final StreamResult sresult = new StreamResult(new FileOutputStream(file));
                transformer.transform(source, sresult);
            }
        }
        catch (Exception ex) {
            XMLSerializer.logger.log(Level.WARNING, ex.getMessage(), ex);
            return false;
        }
        finally {
            final long end = System.nanoTime();
            XMLSerializer.logger.info("Dumping fields to XML took " + (end - start) / 1000000.0f + " ms");
        }
        return true;
    }
    
    public final File getRootDir() {
        return new File("wurmDB" + File.separator + "test" + File.separator + "base" + File.separator);
    }
    
    public abstract Object createInstanceAndCallLoadXML(final File p0);
    
    public final void loadXML(final File file) {
        try {
            final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            final Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            final NodeList nList = doc.getElementsByTagName("FIELD");
            System.out.println("----------------------------");
            final Map<String, Field> fieldsMap = this.getSaveFieldsMap();
            for (int temp = 0; temp < nList.getLength(); ++temp) {
                final Node nNode = nList.item(temp);
                System.out.println("\nCurrent Element :" + nNode.getNodeName());
                if (nNode.getNodeType() == 1) {
                    final Element eElement = (Element)nNode;
                    final String fieldName = eElement.getElementsByTagName("NAME").item(0).getTextContent();
                    final String value = eElement.getElementsByTagName("VALUE").item(0).getTextContent();
                    System.out.println("NAME : " + eElement.getElementsByTagName("NAME").item(0).getTextContent());
                    System.out.println("VALUE : " + eElement.getElementsByTagName("VALUE").item(0).getTextContent());
                    final Field f = fieldsMap.get(fieldName);
                    try {
                        if (f != null) {
                            f.setAccessible(true);
                            if (f.getType() == Boolean.class || f.getType() == Boolean.TYPE) {
                                f.set(this, Boolean.parseBoolean(value));
                            }
                            if (f.getType() == Byte.class || f.getType() == Byte.TYPE) {
                                f.set(this, Byte.parseByte(value));
                            }
                            else if (f.getType() == Short.class || f.getType() == Short.TYPE) {
                                f.set(this, Short.parseShort(value));
                            }
                            else if (f.getType() == Integer.class || f.getType() == Integer.TYPE) {
                                f.set(this, Integer.parseInt(value));
                            }
                            else if (f.getType() == Float.class || f.getType() == Float.TYPE) {
                                f.set(this, Float.parseFloat(value));
                            }
                            else if (f.getType() == Long.class || f.getType() == Long.TYPE) {
                                f.set(this, Long.parseLong(value));
                            }
                            else if (f.getType() == String.class) {
                                f.set(this, value);
                            }
                        }
                        else {
                            XMLSerializer.logger.log(Level.INFO, "Field " + fieldName + " is missing from Xml and will not be set");
                        }
                    }
                    catch (Exception ex) {
                        XMLSerializer.logger.log(Level.WARNING, fieldName + ":" + ex.getMessage());
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Object[] loadAllXMLData() {
        final Set loadedObjects = new HashSet();
        final File baseDir = new File("wurmDB" + File.separator + "test");
        for (final File dir : baseDir.listFiles()) {
            if (dir.isDirectory()) {
                for (final File f : dir.listFiles()) {
                    if (f.isDirectory()) {
                        for (final File toLoad : f.listFiles()) {
                            if (!toLoad.isDirectory()) {
                                if (toLoad.getName().endsWith(".xml")) {
                                    loadedObjects.add(this.createInstanceAndCallLoadXML(toLoad));
                                }
                            }
                            else {
                                XMLSerializer.logger.log(Level.INFO, "Not loading " + toLoad + " since it is a directory.");
                            }
                        }
                    }
                    else if (f.getName().endsWith(".xml")) {
                        loadedObjects.add(this.createInstanceAndCallLoadXML(f));
                    }
                }
            }
        }
        if (!loadedObjects.isEmpty()) {
            return loadedObjects.toArray(new Object[loadedObjects.size()]);
        }
        return this.emptyObjectArray;
    }
    
    Document createFieldsXmlDocument(final LinkedList<Field> fields) throws ParserConfigurationException {
        final String root = "fields";
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        final Document document = documentBuilder.newDocument();
        final Element rootElement = document.createElement("fields");
        document.appendChild(rootElement);
        for (final Field field : fields) {
            field.setAccessible(true);
            final Element node = document.createElement("FIELD");
            rootElement.appendChild(node);
            try {
                createNode("NAME", field.getName(), document, node);
                createNode("VALUE", field.get(this).toString(), document, node);
            }
            catch (IllegalAccessException iae) {
                XMLSerializer.logger.log(Level.WARNING, "Failed to write " + field.getName());
            }
        }
        return document;
    }
    
    public static void createNode(final String element, final String data, final Document document, final Element rootElement) {
        final Element em = document.createElement(element);
        em.appendChild(document.createTextNode(data));
        rootElement.appendChild(em);
    }
    
    static {
        XMLSerializer.logger = Logger.getLogger(XMLSerializer.class.getName());
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    public @interface Saved {
    }
}
