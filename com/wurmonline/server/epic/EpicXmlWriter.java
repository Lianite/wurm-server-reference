// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.epic;

import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import com.wurmonline.server.Server;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import org.w3c.dom.Document;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.OutputStream;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.File;
import com.wurmonline.server.Constants;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class EpicXmlWriter
{
    private static final Logger logger;
    
    public static void dumpEntities(final HexMap map) {
        if (EpicXmlWriter.logger.isLoggable(Level.FINE)) {
            EpicXmlWriter.logger.fine("Starting to dump Epic Entities to XML for HexMap: " + map);
        }
        final long start = System.nanoTime();
        try {
            final EpicEntity[] entities = map.getAllEntities();
            final Document document = createEntitiesXmlDocument(entities);
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            final DOMSource source = new DOMSource(document);
            final File f = new File(Constants.webPath + File.separator + "entities.xml");
            EpicXmlWriter.logger.info("Dumping Epic entities to absolute path: " + f.getAbsolutePath());
            f.createNewFile();
            if (f != null) {
                final StreamResult result = new StreamResult(new FileOutputStream(f));
                transformer.transform(source, result);
            }
        }
        catch (Exception ex) {
            EpicXmlWriter.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        finally {
            final long end = System.nanoTime();
            EpicXmlWriter.logger.info("Dumping Epic Entities to XML took " + (end - start) / 1000000.0f + " ms");
        }
    }
    
    static Document createEntitiesXmlDocument(final EpicEntity[] entities) throws ParserConfigurationException {
        final String root = "entities";
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        final Document document = documentBuilder.newDocument();
        final Element rootElement = document.createElement("entities");
        document.appendChild(rootElement);
        for (final EpicEntity entity : entities) {
            final Element newElement = document.createElement("entity");
            rootElement.appendChild(newElement);
            createNode("name", entity.getName(), document, newElement);
            if (entity.getMapHex() != null) {
                createNode("hexnumber", String.valueOf(entity.getMapHex().getId()), document, newElement);
                createNode("hexname", entity.getMapHex().getName(), document, newElement);
            }
            else {
                createNode("hexnumber", "-1", document, newElement);
                createNode("hexname", "unknown", document, newElement);
            }
            if (entity.isDeity() || entity.isWurm()) {
                if (System.currentTimeMillis() < entity.getTimeUntilLeave()) {
                    final long leaveTime = entity.getTimeUntilLeave() - System.currentTimeMillis();
                    createNode("timetoleavehex", Server.getTimeFor(leaveTime), document, newElement);
                }
                if (System.currentTimeMillis() < entity.getTimeToNextHex()) {
                    final long nextTime = entity.getTimeToNextHex() - System.currentTimeMillis();
                    createNode("timetonexthex", Server.getTimeFor(nextTime), document, newElement);
                }
            }
            createNode("location", entity.getLocationStatus(), document, newElement);
            if (!entity.isSource() && !entity.isCollectable()) {
                createNode("enemy", entity.getEnemyStatus(), document, newElement);
                createNode("strength", String.valueOf(entity.getAttack()), document, newElement);
                createNode("vitality", String.valueOf(entity.getVitality()), document, newElement);
                final int colls = entity.countCollectables();
                createNode("collectibles", String.valueOf(colls), document, newElement);
                if (colls > 0) {
                    createNode("collectiblename", entity.getCollectibleName(), document, newElement);
                }
            }
            if (entity.getCarrier() != null) {
                createNode("carrier", entity.getCarrier().getName(), document, newElement);
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
        logger = Logger.getLogger(EpicXmlWriter.class.getName());
    }
}
