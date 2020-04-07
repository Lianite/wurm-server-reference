// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.utils;

import java.security.cert.CertificateException;
import java.io.OutputStream;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLSocketFactory;
import java.io.InputStream;
import javax.net.ssl.SSLException;
import java.io.FileOutputStream;
import java.security.cert.Certificate;
import com.wurmonline.shared.util.StringUtilities;
import java.security.MessageDigest;
import javax.net.ssl.SSLSocket;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;
import java.security.KeyStoreException;
import java.security.KeyStore;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.io.File;
import java.util.logging.Logger;

public final class InstallCert
{
    private static final Logger logger;
    
    public static void installCert(final String host, final int port, final String password, final String keystoreName) throws Exception {
        final char[] passphrase = password.toCharArray();
        final char SEP = File.separatorChar;
        final File dir = new File(System.getProperty("java.home") + SEP + "lib" + SEP + "security");
        File file = new File(dir, keystoreName);
        if (!file.isFile()) {
            file = new File(dir, "cacerts");
        }
        InstallCert.logger.log(Level.INFO, "Loading KeyStore " + file + "...");
        final InputStream in = new FileInputStream(file);
        final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(in, passphrase);
        in.close();
        try {
            InstallCert.logger.log(Level.INFO, "Loaded Keystore size: " + ks.size());
        }
        catch (KeyStoreException kse) {
            InstallCert.logger.log(Level.INFO, "Keystore has not been initalized");
        }
        final SSLContext context = SSLContext.getInstance("TLS");
        final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
        final X509TrustManager defaultTrustManager = (X509TrustManager)tmf.getTrustManagers()[0];
        final SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
        context.init(null, new TrustManager[] { tm }, null);
        final SSLSocketFactory factory = context.getSocketFactory();
        InstallCert.logger.log(Level.INFO, "Opening connection to " + host + ":" + port + "...");
        final SSLSocket socket = (SSLSocket)factory.createSocket(host, port);
        socket.setSoTimeout(10000);
        try {
            InstallCert.logger.log(Level.INFO, "Starting SSL handshake...");
            socket.startHandshake();
            socket.close();
            InstallCert.logger.log(Level.INFO, "No errors, certificate is already trusted");
        }
        catch (SSLException e) {
            InstallCert.logger.log(Level.INFO, "Received SSLException. Untrusted cert. Installing.");
            final X509Certificate[] chain = tm.chain;
            if (chain == null) {
                InstallCert.logger.log(Level.INFO, "Could not obtain server certificate chain");
                return;
            }
            InstallCert.logger.log(Level.INFO, "Server sent " + chain.length + " certificate(s):");
            final MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            final MessageDigest md5 = MessageDigest.getInstance("MD5");
            for (int i = 0; i < chain.length; ++i) {
                final X509Certificate cert = chain[i];
                InstallCert.logger.log(Level.INFO, " " + (i + 1) + " Subject " + cert.getSubjectDN());
                InstallCert.logger.log(Level.INFO, "   Issuer  " + cert.getIssuerDN());
                sha1.update(cert.getEncoded());
                InstallCert.logger.log(Level.INFO, "   sha1    " + StringUtilities.toHexString(sha1.digest()));
                md5.update(cert.getEncoded());
                InstallCert.logger.log(Level.INFO, "   md5     " + StringUtilities.toHexString(md5.digest()));
            }
            final int k = chain.length - 1;
            final X509Certificate cert = chain[k];
            final String alias = host + "-" + (k + 1);
            ks.setCertificateEntry(alias, cert);
            final OutputStream out = new FileOutputStream(file);
            ks.store(out, passphrase);
            out.close();
            InstallCert.logger.log(Level.INFO, cert.toString());
            InstallCert.logger.log(Level.INFO, "Added certificate to keystore '" + file.getAbsolutePath() + "' using alias '" + alias + "'");
        }
    }
    
    static {
        logger = Logger.getLogger(InstallCert.class.getName());
    }
    
    private static class SavingTrustManager implements X509TrustManager
    {
        private final X509TrustManager tm;
        private X509Certificate[] chain;
        
        SavingTrustManager(final X509TrustManager aTm) {
            this.tm = aTm;
        }
        
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void checkClientTrusted(final X509Certificate[] aChain, final String authType) throws CertificateException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void checkServerTrusted(final X509Certificate[] aChain, final String authType) throws CertificateException {
            this.chain = aChain;
            this.tm.checkServerTrusted(aChain, authType);
        }
    }
}
