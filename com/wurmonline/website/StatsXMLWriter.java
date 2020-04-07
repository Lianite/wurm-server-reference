// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.website;

import javax.xml.transform.Transformer;
import com.wurmonline.server.ServerEntry;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import com.wurmonline.server.Players;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.Server;
import com.wurmonline.server.Servers;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public final class StatsXMLWriter
{
    public static final void createXML(final File outputFile) throws Exception {
        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        final Document doc = docBuilder.newDocument();
        final Element rootElement = doc.createElement("statistics");
        doc.appendChild(rootElement);
        final Element timestamp = doc.createElement("timestamp");
        timestamp.setTextContent(new StringBuilder().append(System.currentTimeMillis() / 1000L).toString());
        rootElement.appendChild(timestamp);
        final Element status = doc.createElement("status");
        status.setTextContent(Servers.localServer.maintaining ? "offline" : ((Server.getMillisToShutDown() > 0L) ? "shutting down" : "online"));
        if (Server.getMillisToShutDown() > 0L) {
            timestamp.setAttribute("ttl", new StringBuilder("" + Server.getMillisToShutDown() / 1000L).toString());
        }
        rootElement.appendChild(status);
        final Element uptime = doc.createElement("uptime");
        uptime.setTextContent(new StringBuilder().append(Server.getSecondsUptime()).toString());
        rootElement.appendChild(uptime);
        final Element wurmtime = doc.createElement("wurmtime");
        wurmtime.setTextContent(WurmCalendar.getTime());
        rootElement.appendChild(wurmtime);
        final Element weather = doc.createElement("weather");
        weather.setTextContent(Server.getWeather().getWeatherString(false));
        rootElement.appendChild(weather);
        final Element serverselm = doc.createElement("servers");
        final ServerEntry[] servers = Servers.getAllServers();
        int epic = 0;
        int epicMax = 0;
        for (final ServerEntry entry : servers) {
            if (!entry.EPIC) {
                final Element srv = doc.createElement("server");
                if (!entry.isLocal) {
                    srv.setAttribute("name", entry.getName());
                    srv.setAttribute("players", new StringBuilder().append(entry.currentPlayers).toString());
                    srv.setAttribute("maxplayers", new StringBuilder().append(entry.pLimit).toString());
                }
                else {
                    srv.setAttribute("name", entry.getName());
                    srv.setAttribute("players", new StringBuilder().append(Players.getInstance().getNumberOfPlayers()).toString());
                    srv.setAttribute("maxplayers", new StringBuilder().append(entry.pLimit).toString());
                }
                serverselm.appendChild(srv);
            }
            else {
                epic += entry.currentPlayers;
                epicMax += entry.pLimit;
            }
        }
        final Element srv2 = doc.createElement("server");
        srv2.setAttribute("name", "Epic cluster");
        srv2.setAttribute("players", new StringBuilder().append(epic).toString());
        srv2.setAttribute("maxplayers", new StringBuilder().append(epicMax).toString());
        serverselm.appendChild(srv2);
        rootElement.appendChild(serverselm);
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        final Transformer transformer = transformerFactory.newTransformer();
        final DOMSource source = new DOMSource(doc);
        final StreamResult result = new StreamResult(new BufferedWriter(new FileWriter(outputFile)));
        transformer.transform(source, result);
    }
}
