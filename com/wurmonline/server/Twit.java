// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import java.util.LinkedList;
import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.OAuthSignpostClient;
import java.util.logging.Level;
import java.util.ListIterator;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.concurrent.GuardedBy;
import java.util.List;
import java.util.logging.Logger;

public final class Twit
{
    private static final Logger logger;
    private final String sender;
    private final String twit;
    private final String consumerKey;
    private final String consumerSecret;
    private final String oauthToken;
    private final String oauthTokenSecret;
    private final boolean isVillage;
    private static final Twit[] emptyTwits;
    @GuardedBy("TWITS_RW_LOCK")
    private static final List<Twit> twits;
    private static final ReentrantReadWriteLock TWITS_RW_LOCK;
    private static final TwitterThread twitterThread;
    
    public Twit(final String senderName, final String toTwit, final String consumerKeyToUse, final String consumerSecretToUse, final String applicationToken, final String applicationSecret, final boolean _isVillage) {
        this.sender = senderName;
        this.twit = toTwit.substring(0, Math.min(toTwit.length(), 279));
        this.consumerKey = consumerKeyToUse;
        this.consumerSecret = consumerSecretToUse;
        this.oauthToken = applicationToken;
        this.oauthTokenSecret = applicationSecret;
        this.isVillage = _isVillage;
    }
    
    private static final Twit[] getTwitsArray() {
        try {
            Twit.TWITS_RW_LOCK.writeLock().lock();
            if (Twit.twits.size() > 0) {
                final Twit[] toReturn = new Twit[Twit.twits.size()];
                int x = 0;
                final ListIterator<Twit> it = Twit.twits.listIterator();
                while (it.hasNext()) {
                    toReturn[x] = it.next();
                    ++x;
                }
                return toReturn;
            }
        }
        finally {
            Twit.TWITS_RW_LOCK.writeLock().unlock();
        }
        return Twit.emptyTwits;
    }
    
    private static final void removeTwit(final Twit twit) {
        try {
            Twit.TWITS_RW_LOCK.writeLock().lock();
            Twit.twits.remove(twit);
        }
        finally {
            Twit.TWITS_RW_LOCK.writeLock().unlock();
        }
    }
    
    private static void pollTwits() {
        final Twit[] twitarr = getTwitsArray();
        if (twitarr.length > 0) {
            for (int y = 0; y < twitarr.length; ++y) {
                try {
                    twitJTwitter(twitarr[y]);
                    removeTwit(twitarr[y]);
                }
                catch (Exception ex) {
                    if (ex.getMessage().startsWith("Already tweeted!") || ex.getMessage().startsWith("Forbidden") || ex.getMessage().startsWith("Unauthorized") || ex.getMessage().startsWith("Invalid")) {
                        Twit.logger.log(Level.INFO, "Removed duplicate or unauthorized " + twitarr[y].twit);
                        removeTwit(twitarr[y]);
                    }
                    else if (twitarr[y].isVillage) {
                        Twit.logger.log(Level.INFO, "Twitting failed for village " + ex.getMessage() + " Removing.");
                        removeTwit(twitarr[y]);
                    }
                    else {
                        if (twitarr[y].twit == null || twitarr[y].twit.length() == 0) {
                            removeTwit(twitarr[y]);
                        }
                        Twit.logger.log(Level.INFO, "Twitting failed for server " + ex.getMessage() + ". Trying later.");
                    }
                }
            }
        }
    }
    
    public static final void twit(final Twit twit) {
        if (twit != null) {
            try {
                Twit.TWITS_RW_LOCK.writeLock().lock();
                Twit.twits.add(twit);
            }
            finally {
                Twit.TWITS_RW_LOCK.writeLock().unlock();
            }
        }
    }
    
    private static void twitJTwitter(final Twit twit) {
        Twit.logger.log(Level.INFO, "creating oauthClient for " + twit.twit);
        final OAuthSignpostClient oauthClient = new OAuthSignpostClient(twit.consumerKey, twit.consumerSecret, twit.oauthToken, twit.oauthTokenSecret);
        Twit.logger.log(Level.INFO, "creating twitter for " + twit.twit);
        final Twitter twitter = new Twitter(twit.sender, oauthClient);
        twitter.setStatus(twit.twit);
        Twit.logger.log(Level.INFO, "done sending twit " + twit.twit);
    }
    
    public static final TwitterThread getTwitterThread() {
        return Twit.twitterThread;
    }
    
    static {
        logger = Logger.getLogger(Twit.class.getName());
        emptyTwits = new Twit[0];
        twits = new LinkedList<Twit>();
        TWITS_RW_LOCK = new ReentrantReadWriteLock();
        twitterThread = new TwitterThread();
    }
    
    private static class TwitterThread implements Runnable
    {
        @Override
        public void run() {
            try {
                final long start = System.nanoTime();
                pollTwits();
                final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
                if (lElapsedTime > Constants.lagThreshold) {
                    Twit.logger.info("Finished calling Twit.pollTwits(), which took " + lElapsedTime + " millis.");
                }
            }
            catch (RuntimeException e) {
                Twit.logger.log(Level.WARNING, "Caught exception in ScheduledExecutorService while calling Twit.pollTwits()", e);
                throw e;
            }
        }
    }
}
