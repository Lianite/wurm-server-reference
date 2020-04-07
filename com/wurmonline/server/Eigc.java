// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import com.wurmonline.server.creatures.Communicator;
import java.util.Iterator;
import java.io.Writer;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.wurmonline.server.utils.HttpResponseStatus;
import java.io.Closeable;
import com.wurmonline.shared.util.IoUtilities;
import java.net.URLEncoder;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.io.IOException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.Reader;
import java.io.StringReader;
import org.xml.sax.InputSource;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.HostnameVerifier;
import java.util.LinkedList;
import java.util.logging.Logger;

public final class Eigc implements MiscConstants
{
    private static final Logger logger;
    private static final String LOAD_ALL_EIGC = "SELECT * FROM EIGC";
    private static final String INSERT_EIGC_ACCOUNT = "INSERT INTO EIGC(USERNAME,PASSWORD,SERVICEBUNDLE,EXPIRATION,EMAIL) VALUES (?,?,?,?,?)";
    private static final String DELETE_EIGC_ACCOUNT = "DELETE FROM EIGC WHERE USERNAME=?";
    private static final LinkedList<EigcClient> EIGC_CLIENTS;
    private static final String HTTP_CHARACTER_ENCODING = "UTF-8";
    private static final int initialAccountsToProvision;
    private static final int accountsToProvision;
    private static boolean isProvisioning;
    public static final String SERVICE_PROXIMITY = "proximity";
    public static final String SERVICE_P2P = "p2p";
    public static final String SERVICE_TEAM = "team";
    public static final String SERVICE_LECTURE = "lecture";
    public static final String SERVICE_HIFI = "hifi";
    public static final String SERVICES_FREE = "proximity";
    public static final String SERVICES_BUNDLE = "proximity,team,p2p,hifi";
    public static final String PROTOCOL_PROVISIONING = "https://";
    private static String HOST_PROVISIONING;
    public static String URL_PROXIMITY;
    public static String URL_SIP_REGISTRAR;
    public static String URL_SIP_PROXY;
    private static String EIGC_REALM;
    private static final int PORT_PROVISIONING = 5002;
    private static String URL_PROVISIONING;
    private static String CREATE_URL;
    private static String MODIFY_URL;
    private static String VIEW_URL;
    private static String DELETE_URL;
    private static String EIGC_PASSWORD;
    
    private static final void setEigcHttpsOverride() {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(final String hostname, final SSLSession sslSession) {
                return hostname.equals(Eigc.HOST_PROVISIONING);
            }
        });
    }
    
    public static final void loadAllAccounts() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM EIGC");
            rs = ps.executeQuery();
            while (rs.next()) {
                final String eigcUserId = rs.getString("USERNAME");
                final EigcClient eigclient = new EigcClient(eigcUserId, rs.getString("PASSWORD"), rs.getString("SERVICEBUNDLE"), rs.getLong("EXPIRATION"), rs.getString("EMAIL"));
                Eigc.EIGC_CLIENTS.add(eigclient);
            }
            Eigc.logger.log(Level.INFO, "Loaded " + Eigc.EIGC_CLIENTS.size() + " eigc accounts.");
        }
        catch (SQLException sqx) {
            Eigc.logger.log(Level.WARNING, "Problem loading eigc clients for server due to " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        if (Constants.isEigcEnabled && Eigc.EIGC_CLIENTS.size() < Eigc.initialAccountsToProvision) {
            try {
                installCert();
                provisionAccounts(Eigc.initialAccountsToProvision, false);
            }
            catch (Exception exc) {
                Eigc.logger.log(Level.WARNING, exc.getMessage(), exc);
            }
        }
        if (Servers.localServer.testServer) {
            Eigc.HOST_PROVISIONING = "provisioning.eigctestnw.com";
            Eigc.URL_PROXIMITY = "sip:wurmonline.eigctestnw.com";
            Eigc.URL_SIP_REGISTRAR = "wurmonline.eigctestnw.com";
            Eigc.URL_SIP_PROXY = "sip:gateway.eigctestnw.com:35060";
            Eigc.EIGC_REALM = "wurmonline.eigctestnw.com";
            Eigc.EIGC_PASSWORD = "admin";
            changeEigcUrls();
        }
    }
    
    private static final void changeEigcUrls() {
    }
    
    public static final void installCert() throws Exception {
    }
    
    public static final void deleteAccounts(final int nums) {
        if (Constants.isEigcEnabled && !Eigc.isProvisioning) {
            Eigc.isProvisioning = true;
            new Thread() {
                @Override
                public void run() {
                    for (int x = 0; x < nums; ++x) {
                        final String userName = "Wurmpool" + Servers.localServer.id * 20000 + x + 1;
                        Eigc.logger.log(Level.INFO, Eigc.deleteUser(userName));
                    }
                    Eigc.isProvisioning = false;
                }
            }.start();
        }
    }
    
    public static final void deleteAccounts() {
        if (!Eigc.isProvisioning) {
            final EigcClient[] clients = Eigc.EIGC_CLIENTS.toArray(new EigcClient[Eigc.EIGC_CLIENTS.size()]);
            Eigc.isProvisioning = true;
            new Thread() {
                @Override
                public void run() {
                    for (final EigcClient client : clients) {
                        final String userName = client.getClientId();
                        Eigc.logger.log(Level.INFO, Eigc.deleteUser(userName));
                    }
                    Eigc.isProvisioning = false;
                }
            }.start();
        }
    }
    
    public static final void provisionAccounts(final int numberToProvision, final boolean overRide) {
        if ((Constants.isEigcEnabled || overRide) && !Eigc.isProvisioning) {
            Eigc.isProvisioning = true;
            new Thread() {
                @Override
                public void run() {
                    final String[] paramNames = { "servicebundle" };
                    final String[] paramVals = { "proximity" };
                    String userName = "Wurmpool" + (Servers.localServer.id * 20000 + Eigc.EIGC_CLIENTS.size() + 1);
                    int failed = 0;
                    for (int x = 0; x < numberToProvision; ++x) {
                        try {
                            userName = "Wurmpool" + (Servers.localServer.id * 20000 + Eigc.EIGC_CLIENTS.size() + failed + 1);
                            final String response = Eigc.httpPost(Eigc.CREATE_URL, paramNames, paramVals, userName);
                            if (Eigc.logger.isLoggable(Level.INFO)) {
                                Eigc.logger.info("Called " + Eigc.CREATE_URL + " with user name " + userName + " and received response " + response);
                            }
                            boolean created = false;
                            try {
                                final InputSource inStream = new InputSource();
                                inStream.setCharacterStream(new StringReader(response));
                                final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                                final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                                final Document doc = dBuilder.parse(inStream);
                                doc.getDocumentElement().normalize();
                                Eigc.logger.log(Level.INFO, "Root element :" + doc.getDocumentElement().getNodeName());
                                final NodeList nList = doc.getElementsByTagName("user");
                                for (int temp = 0; temp < nList.getLength(); ++temp) {
                                    final Node nNode = nList.item(temp);
                                    if (nNode.getNodeType() == 1) {
                                        final Element eElement = (Element)nNode;
                                        final String uname = getTagValue("username", eElement);
                                        Eigc.logger.log(Level.INFO, "UserName : " + uname);
                                        final String authid = getTagValue("authid", eElement);
                                        Eigc.logger.log(Level.INFO, "Auth Id : " + authid);
                                        final String password = getTagValue("passwd", eElement);
                                        Eigc.logger.log(Level.INFO, "Password : " + password);
                                        final String services = getTagValue("servicebundle", eElement);
                                        Eigc.logger.log(Level.INFO, "Service bundle : " + services);
                                        Eigc.createAccount(uname, password, services, Long.MAX_VALUE, "");
                                        created = true;
                                    }
                                }
                            }
                            catch (Exception e) {
                                Eigc.logger.log(Level.WARNING, e.getMessage());
                            }
                            if (!created) {
                                ++failed;
                            }
                        }
                        catch (Exception e2) {
                            Eigc.logger.log(Level.WARNING, "Problem calling " + Eigc.CREATE_URL + " with user name " + userName, e2);
                        }
                    }
                    Eigc.isProvisioning = false;
                    if (overRide) {
                        Constants.isEigcEnabled = true;
                    }
                }
            }.start();
        }
    }
    
    private static String getTagValue(final String sTag, final Element eElement) {
        final NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        final Node nValue = nlList.item(0);
        return nValue.getNodeValue();
    }
    
    public static final String getEigcInfo(final String eigcId) {
        try {
            final String answer = httpGet(Eigc.VIEW_URL, eigcId);
            return answer;
        }
        catch (IOException iox) {
            Eigc.logger.log(Level.INFO, iox.getMessage(), iox);
            return "Failed to retrieve information about " + eigcId;
        }
    }
    
    public static final String deleteUser(final String userId) {
        try {
            final String[] paramNames = new String[0];
            final String[] paramVals = new String[0];
            final String answer = httpDelete(Eigc.DELETE_URL, paramNames, paramVals, userId);
            Eigc.logger.log(Level.INFO, "Called " + Eigc.DELETE_URL + " with userId=" + userId);
            if (answer.toLowerCase().contains("<rsp stat=\"ok\">")) {
                Eigc.logger.log(Level.INFO, "Deleting " + userId + " from database.");
                deleteAccount(userId);
            }
            return answer;
        }
        catch (Exception iox) {
            Eigc.logger.log(Level.INFO, iox.getMessage(), iox);
            return "Failed to delete " + userId;
        }
    }
    
    public static final String modifyUser(final String userId, final String servicesAsCommaSeparatedString, final long expiration) {
        try {
            final String[] paramNames = { "servicebundle" };
            final String[] paramVals = { servicesAsCommaSeparatedString };
            try {
                final String response = httpPost(Eigc.MODIFY_URL, paramNames, paramVals, userId);
                if (Eigc.logger.isLoggable(Level.INFO)) {
                    Eigc.logger.info("Called " + Eigc.MODIFY_URL + " with user name " + userId + " and received response " + response);
                }
                try {
                    final InputSource inStream = new InputSource();
                    inStream.setCharacterStream(new StringReader(response));
                    final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    final Document doc = dBuilder.parse(inStream);
                    doc.getDocumentElement().normalize();
                    Eigc.logger.log(Level.INFO, "Root element :" + doc.getDocumentElement().getNodeName());
                    final NodeList nList = doc.getElementsByTagName("user");
                    for (int temp = 0; temp < nList.getLength(); ++temp) {
                        final Node nNode = nList.item(temp);
                        if (nNode.getNodeType() == 1) {
                            final Element eElement = (Element)nNode;
                            final String uname = getTagValue("username", eElement);
                            Eigc.logger.log(Level.INFO, "UserName : " + uname);
                            final String authid = getTagValue("authid", eElement);
                            Eigc.logger.log(Level.INFO, "Auth Id : " + authid);
                            final String password = getTagValue("passwd", eElement);
                            Eigc.logger.log(Level.INFO, "Password : " + password);
                            final String services = getTagValue("servicebundle", eElement);
                            Eigc.logger.log(Level.INFO, "Service bundle : " + services);
                            final EigcClient old = getClientWithId(uname);
                            if (old != null) {
                                updateAccount(uname, password, services, expiration, old.getAccountName());
                            }
                            else {
                                updateAccount(uname, password, services, expiration, "");
                            }
                        }
                    }
                }
                catch (Exception e) {
                    Eigc.logger.log(Level.WARNING, e.getMessage());
                }
            }
            catch (Exception e2) {
                Eigc.logger.log(Level.WARNING, "Problem calling " + Eigc.CREATE_URL + " with user name " + userId, e2);
            }
        }
        catch (Exception iox) {
            Eigc.logger.log(Level.INFO, iox.getMessage(), iox);
        }
        return "Failed to modify " + userId;
    }
    
    public static String httpPost(final String urlStr, final String[] paramName, final String[] paramVal, final String userName) throws Exception {
        final URL url = new URL(urlStr + userName);
        setEigcHttpsOverride();
        final HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
        StringBuilder sb;
        try {
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.setRequestProperty("Authorization", "Digest " + Eigc.EIGC_PASSWORD);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            final OutputStream out = conn.getOutputStream();
            final Writer writer = new OutputStreamWriter(out, "UTF-8");
            try {
                for (int i = 0; i < paramName.length; ++i) {
                    writer.write(paramName[i]);
                    writer.write("=");
                    Eigc.logger.log(Level.INFO, "Sending " + paramName[i] + "=" + paramVal[i]);
                    writer.write(URLEncoder.encode(paramVal[i], "UTF-8"));
                    writer.write("&");
                }
            }
            finally {
                IoUtilities.closeClosable(writer);
                IoUtilities.closeClosable(out);
            }
            if (conn.getResponseCode() != HttpResponseStatus.OK.getStatusCode()) {
                throw new IOException(conn.getResponseMessage());
            }
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            try {
                sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            }
            finally {
                IoUtilities.closeClosable(rd);
            }
        }
        finally {
            IoUtilities.closeHttpURLConnection(conn);
        }
        return sb.toString();
    }
    
    public static String httpDelete(final String urlStr, final String[] paramName, final String[] paramVal, final String userName) throws Exception {
        final URL url = new URL(urlStr + userName);
        setEigcHttpsOverride();
        final HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
        StringBuilder sb;
        try {
            conn.setRequestMethod("DELETE");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.setRequestProperty("Authorization", "Digest " + Eigc.EIGC_PASSWORD);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            final OutputStream out = conn.getOutputStream();
            final Writer writer = new OutputStreamWriter(out, "UTF-8");
            try {
                for (int i = 0; i < paramName.length; ++i) {
                    writer.write(paramName[i]);
                    writer.write("=");
                    Eigc.logger.log(Level.INFO, "Sending " + paramName[i] + "=" + paramVal[i]);
                    writer.write(URLEncoder.encode(paramVal[i], "UTF-8"));
                    writer.write("&");
                }
            }
            finally {
                IoUtilities.closeClosable(writer);
                IoUtilities.closeClosable(out);
            }
            if (conn.getResponseCode() != HttpResponseStatus.OK.getStatusCode()) {
                throw new IOException(conn.getResponseMessage());
            }
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            try {
                sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            }
            finally {
                IoUtilities.closeClosable(rd);
            }
        }
        finally {
            IoUtilities.closeHttpURLConnection(conn);
        }
        return sb.toString();
    }
    
    public static String httpGet(final String urlStr, final String userName) throws IOException {
        final URL url = new URL(urlStr + userName);
        setEigcHttpsOverride();
        final HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
        conn.setRequestProperty("Authorization", Eigc.EIGC_PASSWORD);
        if (conn.getResponseCode() != HttpResponseStatus.OK.getStatusCode()) {
            throw new IOException(conn.getResponseMessage());
        }
        final StringBuilder sb = new StringBuilder();
        try (final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
        }
        conn.disconnect();
        return sb.toString();
    }
    
    public static final String addPlayer(final String playerName) {
        final EigcClient found = getClientForPlayer(playerName);
        if (found != null) {
            Eigc.logger.log(Level.INFO, playerName + " already in use: " + found.getClientId());
            return found.getClientId();
        }
        for (final EigcClient gcc : Eigc.EIGC_CLIENTS) {
            if (gcc.getAccountName().equalsIgnoreCase(playerName)) {
                return setClientUsed(gcc, playerName, "found unused reserved client");
            }
        }
        EigcClient client = null;
        for (final EigcClient gcc2 : Eigc.EIGC_CLIENTS) {
            if (!gcc2.isUsed() && gcc2.getAccountName().length() <= 0) {
                client = gcc2;
                break;
            }
        }
        if (client != null) {
            Eigc.EIGC_CLIENTS.remove(client);
            Eigc.EIGC_CLIENTS.add(client);
            return setClientUsed(client, playerName, "found unused free client");
        }
        provisionAccounts(Eigc.accountsToProvision, false);
        return "";
    }
    
    private static final String setClientUsed(final EigcClient client, final String playerName, final String reason) {
        client.setPlayerName(playerName.toLowerCase(), reason);
        client.setAccountName(playerName.toLowerCase());
        return client.getClientId();
    }
    
    public static final EigcClient getClientForPlayer(final String playerName) {
        String nameSearched = LoginHandler.raiseFirstLetter(playerName);
        final boolean mustTrim = playerName.indexOf(" ") > 0;
        if (mustTrim) {
            nameSearched = playerName.substring(0, playerName.indexOf(" "));
            Eigc.logger.log(Level.INFO, "Trimmed " + playerName + " to " + nameSearched);
        }
        for (final EigcClient client : Eigc.EIGC_CLIENTS) {
            if (client.getPlayerName().equalsIgnoreCase(nameSearched)) {
                return client;
            }
        }
        return null;
    }
    
    public static final EigcClient getReservedClientForPlayer(final String playerName) {
        String nameSearched = LoginHandler.raiseFirstLetter(playerName);
        final boolean mustTrim = playerName.indexOf(" ") > 0;
        if (mustTrim) {
            nameSearched = playerName.substring(0, playerName.indexOf(" "));
            Eigc.logger.log(Level.INFO, "Trimmed " + playerName + " to " + nameSearched);
        }
        for (final EigcClient client : Eigc.EIGC_CLIENTS) {
            if (client.getAccountName().equalsIgnoreCase(nameSearched)) {
                return client;
            }
        }
        return null;
    }
    
    public static final EigcClient removePlayer(final String playerName) {
        final EigcClient client = getClientForPlayer(playerName);
        if (client != null) {
            client.setPlayerName("", "removed");
            if (client.getExpiration() == Long.MAX_VALUE || client.getExpiration() < System.currentTimeMillis()) {
                client.setAccountName("");
            }
        }
        return client;
    }
    
    public static final void sendAllClientInfo(final Communicator comm) {
        for (final EigcClient entry : Eigc.EIGC_CLIENTS) {
            comm.sendNormalServerMessage("ClientId: " + entry.getClientId() + ": user: " + entry.getPlayerName() + " occupied=" + entry.isUsed() + " accountname=" + entry.getAccountName() + " secs since last use=" + (entry.isUsed() ? entry.timeSinceLastUse() : "N/A"));
        }
    }
    
    public static final EigcClient transferPlayer(final String playerName) {
        final EigcClient client = getReservedClientForPlayer(playerName);
        if (client != null) {
            if (client.getExpiration() < Long.MAX_VALUE && client.getExpiration() > System.currentTimeMillis()) {
                return client;
            }
            Eigc.logger.log(Level.INFO, "Setting expired reserved client to unused at server transfer. This should be detected earlier.");
            client.setAccountName("");
            new Thread() {
                @Override
                public void run() {
                    Eigc.modifyUser(client.getClientId(), "proximity", Long.MAX_VALUE);
                }
            }.start();
        }
        return null;
    }
    
    public static final void updateAccount(final String eigcUserId, final String clientPass, final String services, final long expirationTime, final String accountName) {
        final EigcClient oldClient = getClientWithId(eigcUserId);
        if (oldClient == null) {
            createAccount(eigcUserId, clientPass, services, expirationTime, accountName.toLowerCase());
        }
        else {
            oldClient.setPassword(clientPass);
            oldClient.setServiceBundle(services);
            oldClient.setExpiration(expirationTime);
            oldClient.setAccountName(accountName.toLowerCase());
            oldClient.updateAccount();
            Players.getInstance().updateEigcInfo(oldClient);
        }
    }
    
    public static final void createAccount(final String eigcUserId, final String clientPass, final String services, final long expirationTime, final String accountName) {
        final EigcClient eigclient = new EigcClient(eigcUserId, clientPass, services, expirationTime, accountName.toLowerCase());
        Eigc.EIGC_CLIENTS.add(eigclient);
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("INSERT INTO EIGC(USERNAME,PASSWORD,SERVICEBUNDLE,EXPIRATION,EMAIL) VALUES (?,?,?,?,?)");
            ps.setString(1, eigcUserId);
            ps.setString(2, clientPass);
            ps.setString(3, services);
            ps.setLong(4, expirationTime);
            ps.setString(5, accountName.toLowerCase());
            ps.executeUpdate();
            Eigc.logger.log(Level.INFO, "Successfully saved " + eigcUserId);
        }
        catch (SQLException sqx) {
            Eigc.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static final EigcClient getClientWithId(final String eigcUserId) {
        for (final EigcClient entry : Eigc.EIGC_CLIENTS) {
            if (entry.getClientId().equalsIgnoreCase(eigcUserId)) {
                return entry;
            }
        }
        return null;
    }
    
    static void removeClientWithId(final String eigcUserId) {
        EigcClient toRemove = null;
        for (final EigcClient c : Eigc.EIGC_CLIENTS) {
            if (c.getClientId().equalsIgnoreCase(eigcUserId)) {
                toRemove = c;
                break;
            }
        }
        if (toRemove != null) {
            Eigc.EIGC_CLIENTS.remove(toRemove);
        }
    }
    
    public static final void deleteAccount(final String eigcUserId) {
        removeClientWithId(eigcUserId);
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("DELETE FROM EIGC WHERE USERNAME=?");
            ps.setString(1, eigcUserId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Eigc.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        logger = Logger.getLogger(Eigc.class.getName());
        EIGC_CLIENTS = new LinkedList<EigcClient>();
        initialAccountsToProvision = (Servers.isThisATestServer() ? 5 : 25);
        accountsToProvision = (Servers.isThisATestServer() ? 5 : 25);
        Eigc.isProvisioning = false;
        Eigc.HOST_PROVISIONING = "bla";
        Eigc.URL_PROXIMITY = "bla";
        Eigc.URL_SIP_REGISTRAR = "bla";
        Eigc.URL_SIP_PROXY = "bla";
        Eigc.EIGC_REALM = "bla";
        Eigc.URL_PROVISIONING = "https://" + Eigc.HOST_PROVISIONING + ":" + 5002 + "/";
        Eigc.CREATE_URL = Eigc.URL_PROVISIONING + "userprovisioning/v1/create/" + Eigc.EIGC_REALM + "/";
        Eigc.MODIFY_URL = Eigc.URL_PROVISIONING + "userprovisioning/v1/modify/" + Eigc.EIGC_REALM + "/";
        Eigc.VIEW_URL = Eigc.URL_PROVISIONING + "userprovisioning/v1/view/" + Eigc.EIGC_REALM + "/";
        Eigc.DELETE_URL = Eigc.URL_PROVISIONING + "userprovisioning/v1/delete/" + Eigc.EIGC_REALM + "/";
        Eigc.EIGC_PASSWORD = "tL4PDKim";
    }
}
