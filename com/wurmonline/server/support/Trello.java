// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.support;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.util.zip.GZIPInputStream;
import java.io.IOException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.io.InputStream;
import java.util.Iterator;
import com.wurmonline.server.VoteServer;
import java.util.Map;
import java.util.HashMap;
import com.wurmonline.server.Servers;
import java.util.logging.Level;
import com.wurmonline.server.Constants;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Logger;

public final class Trello
{
    private static Logger logger;
    private static final TrelloThread trelloThread;
    private static final ConcurrentLinkedDeque<TrelloCard> trelloCardQueue;
    private static int tickerCount;
    private static final String[] trelloLists;
    private static String[] trelloListIds;
    private static String trelloFeedbackTemplateCardId;
    private static final String trelloMutes = "Mutes";
    private static final String trelloMutewarns = "Mutewarns";
    private static final String trelloVotings = "Voting";
    private static final String trelloHighways = "Highways";
    private static final String trelloDeaths = "Deaths";
    private static String trelloMuteIds;
    private static String trelloMutewarnIds;
    private static String trelloVotingIds;
    private static String trelloHighwaysIds;
    private static String trelloDeathsIds;
    private static boolean trelloMuteStorage;
    public static final byte LIST_NONE = 0;
    public static final byte LIST_WAITING_GM = 1;
    public static final byte LIST_WAITING_ARCH = 2;
    public static final byte LIST_CLOSED = 3;
    public static final byte LIST_WATCHING = 4;
    public static final byte LIST_FEEDBACK = 5;
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String GZIP_ENCODING = "gzip";
    private static final String HTTP_CHARACTER_ENCODING = "UTF-8";
    
    public static final TrelloThread getTrelloThread() {
        return Trello.trelloThread;
    }
    
    public static void addHighwayMessage(final String server, final String title, final String description) {
        if (Constants.trelloMVBoardId.length() == 0) {
            return;
        }
        final StringBuilder buf = new StringBuilder();
        buf.append(Tickets.convertTime(System.currentTimeMillis()));
        buf.append(" (");
        buf.append(server);
        buf.append(") ");
        buf.append(title);
        final String tList = Trello.trelloHighwaysIds;
        final TrelloCard tc = new TrelloCard(Constants.trelloMVBoardId, tList, buf.toString(), description, "");
        Trello.trelloCardQueue.add(tc);
    }
    
    public static void addImportantDeathsMessage(final String server, final String title, final String description) {
        if (Constants.trelloMVBoardId.length() == 0) {
            return;
        }
        final StringBuilder buf = new StringBuilder();
        buf.append(Tickets.convertTime(System.currentTimeMillis()));
        buf.append(" (");
        buf.append(server);
        buf.append(") ");
        buf.append(title);
        final String tList = Trello.trelloDeathsIds;
        final TrelloCard tc = new TrelloCard(Constants.trelloMVBoardId, tList, buf.toString(), description, "");
        Trello.trelloCardQueue.add(tc);
    }
    
    public static void addMessage(final String sender, final String playerName, final String reason, final int hours) {
        if (Constants.trelloMVBoardId.length() == 0) {
            return;
        }
        final StringBuilder buf = new StringBuilder();
        buf.append(Tickets.convertTime(System.currentTimeMillis()) + " " + sender + " ");
        String tList = "";
        String tLabel = "";
        if (hours == 0) {
            tList = Trello.trelloMutewarnIds;
            buf.append("Mutewarn " + playerName);
            tLabel = "Orange";
        }
        else if (reason.length() == 0) {
            tList = Trello.trelloMuteIds;
            buf.append("Unmute " + playerName);
            tLabel = "Unmute";
        }
        else {
            tList = Trello.trelloMuteIds;
            buf.append("Mute " + playerName + " for " + hours + " hour" + ((hours == 1) ? " " : "s "));
            tLabel = "Mute";
        }
        final TrelloCard tc = new TrelloCard(Constants.trelloMVBoardId, tList, buf.toString(), reason, tLabel);
        Trello.trelloCardQueue.add(tc);
    }
    
    private static void updateTicketsInTrello() {
        if (Trello.trelloListIds[1].length() == 0) {
            obtainListIds();
            return;
        }
        try {
            for (final Ticket ticket : Tickets.getDirtyTickets()) {
                if (ticket.getTrelloCardId().length() == 0) {
                    createCard(ticket);
                }
                else {
                    updateCard(ticket);
                }
                for (final TicketAction ta : ticket.getDirtyTicketActions()) {
                    addAction(ticket, ta);
                }
                if (ticket.hasFeedback() && ticket.getTrelloFeedbackCardId().length() == 0) {
                    createFeedbackCard(ticket);
                }
            }
            for (final Ticket ticket : Tickets.getArchiveTickets()) {
                if (ticket.getTrelloCardId().length() == 0) {
                    createCard(ticket);
                }
                archiveCard(ticket);
            }
            Trello.tickerCount = 0;
        }
        catch (RuntimeException e) {
            if (Trello.tickerCount % 10 == 0) {
                Trello.logger.log(Level.INFO, "Problem communicating with Trello " + (Trello.tickerCount + 1) + " times.", e);
            }
            if (Trello.tickerCount >= 1000) {
                throw e;
            }
            ++Trello.tickerCount;
        }
    }
    
    private static void updateMuteVoteInTrello() {
        if (!Trello.trelloMuteStorage) {
            obtainMVListIds();
            return;
        }
        try {
            for (final VoteQuestion vq : VoteQuestions.getFinishedQuestions()) {
                if (vq.getTrelloCardId().length() == 0) {
                    createCard(vq);
                }
            }
            for (final VoteQuestion vq : VoteQuestions.getArchiveVoteQuestions()) {
                if (vq.getTrelloCardId().length() == 0) {
                    createCard(vq);
                }
                archiveCard(vq);
            }
            for (TrelloCard card = Trello.trelloCardQueue.pollFirst(); card != null; card = Trello.trelloCardQueue.pollFirst()) {
                createCard(card);
            }
            Trello.tickerCount = 0;
        }
        catch (RuntimeException e) {
            if (Trello.tickerCount % 10 == 0) {
                Trello.logger.log(Level.INFO, "Problem communicating with Trello " + (Trello.tickerCount + 1) + " times.", e);
            }
            if (Trello.tickerCount >= 1000) {
                throw e;
            }
            ++Trello.tickerCount;
        }
    }
    
    private static void archiveCardsInList(final String trelloList, final String listName) {
        final long archiveTime = System.currentTimeMillis() - (Servers.isThisATestServer() ? 604800000L : 2419200000L);
        int archived = 0;
        final String lurl = TrelloURL.make("https://api.trello.com/1/lists/{0}/cards", trelloList);
        final Map<String, String> argumentsMap = new HashMap<String, String>();
        argumentsMap.put("fields", "id");
        final JSONArray lja = doGetArray(lurl, argumentsMap);
        for (int x = 0; x < lja.length(); ++x) {
            final JSONObject jo = lja.getJSONObject(x);
            final String id = jo.getString("id");
            final String createdDateHex = id.substring(0, 8);
            final long dms = Long.parseLong(createdDateHex, 16) * 1000L;
            if (archiveTime > dms && archiveCard(id)) {
                ++archived;
            }
        }
        if (archived > 0) {
            Trello.logger.log(Level.INFO, "Archived " + archived + " " + listName + " cards in Trello");
        }
    }
    
    private static void addAction(final Ticket ticket, final TicketAction ta) {
        try {
            final String url = TrelloURL.make("https://api.trello.com/1/cards/{0}/actions/comments", ticket.getTrelloCardId());
            ta.setTrelloCommentId(addComment(url, ta.getTrelloComment()));
        }
        catch (TrelloCardNotFoundException tcnfe) {
            ta.setTrelloCommentId("Failed");
            Trello.logger.log(Level.WARNING, tcnfe.getMessage(), tcnfe);
        }
    }
    
    private static String addComment(final String url, final String text) throws TrelloCardNotFoundException {
        final Map<String, String> keyValueMap = new HashMap<String, String>();
        keyValueMap.put("text", text);
        final JSONObject jo = doPost(url, keyValueMap);
        return jo.get("id").toString();
    }
    
    private static void updateCard(final Ticket ticket) {
        try {
            final String cardId = ticket.getTrelloCardId();
            if (ticket.hasSummaryChanged()) {
                updateCard(TrelloURL.make("https://api.trello.com/1/cards/{0}/name", cardId), ticket.getTrelloName());
            }
            if (ticket.hasDescriptionChanged()) {
                updateCard(TrelloURL.make("https://api.trello.com/1/cards/{0}/desc", cardId), ticket.getDescription());
            }
            if (ticket.hasListChanged()) {
                updateCard(TrelloURL.make("https://api.trello.com/1/cards/{0}/idList", cardId), Trello.trelloListIds[ticket.getTrelloListCode()]);
            }
            Tickets.setTicketIsDirty(ticket, false);
        }
        catch (TrelloCardNotFoundException tcnfe) {
            Tickets.setTicketIsDirty(ticket, false);
            Trello.logger.log(Level.WARNING, tcnfe.getMessage(), tcnfe);
        }
    }
    
    private static boolean archiveCard(final String cardId) {
        try {
            updateCard(TrelloURL.make("https://api.trello.com/1/cards/{0}/closed", cardId), "true");
            return true;
        }
        catch (TrelloCardNotFoundException tcnfe) {
            Trello.logger.log(Level.WARNING, tcnfe.getMessage(), tcnfe);
            return false;
        }
    }
    
    private static void archiveCard(final VoteQuestion voteQuestion) {
        try {
            updateCard(TrelloURL.make("https://api.trello.com/1/cards/{0}/closed", voteQuestion.getTrelloCardId()), "true");
        }
        catch (TrelloCardNotFoundException tcnfe) {
            Trello.logger.log(Level.WARNING, tcnfe.getMessage(), tcnfe);
        }
        VoteQuestions.queueSetArchiveState(voteQuestion.getQuestionId(), (byte)3);
    }
    
    private static void archiveCard(final Ticket ticket) {
        try {
            updateCard(TrelloURL.make("https://api.trello.com/1/cards/{0}/closed", ticket.getTrelloCardId()), "true");
            if (ticket.hasFeedback() && ticket.getTrelloFeedbackCardId().length() > 0) {
                updateCard(TrelloURL.make("https://api.trello.com/1/cards/{0}/closed", ticket.getTrelloFeedbackCardId()), "true");
            }
        }
        catch (TrelloCardNotFoundException tcnfe) {
            Trello.logger.log(Level.WARNING, tcnfe.getMessage(), tcnfe);
        }
        Tickets.setTicketArchiveState(ticket, (byte)3);
    }
    
    private static void updateCard(final String url, final String value) throws TrelloCardNotFoundException {
        final Map<String, String> keyValueMap = new HashMap<String, String>();
        keyValueMap.put("value", value);
        doPut(url, keyValueMap);
    }
    
    private static void createCard(final Ticket ticket) {
        try {
            final String url = TrelloURL.make("https://api.trello.com/1/cards", new String[0]);
            final Map<String, String> keyValueMap = new HashMap<String, String>();
            keyValueMap.put("name", ticket.getTrelloName());
            keyValueMap.put("desc", ticket.getDescription());
            keyValueMap.put("idList", Trello.trelloListIds[ticket.getTrelloListCode()]);
            final JSONObject jo = doPost(url, keyValueMap);
            final String shortLink = getShortLink(jo.getString("shortUrl"));
            Tickets.setTicketTrelloCardId(ticket, shortLink);
        }
        catch (TrelloCardNotFoundException tcnfe) {
            Trello.logger.log(Level.WARNING, tcnfe.getMessage(), tcnfe);
        }
    }
    
    private static void createCard(final TrelloCard card) {
        try {
            final String url = TrelloURL.make("https://api.trello.com/1/cards", new String[0]);
            final Map<String, String> keyValueMap = new HashMap<String, String>();
            keyValueMap.put("name", card.getTitle());
            keyValueMap.put("desc", card.getDescription());
            keyValueMap.put("idList", card.getListId());
            doPost(url, keyValueMap);
        }
        catch (TrelloCardNotFoundException tcnfe) {
            Trello.logger.log(Level.WARNING, tcnfe.getMessage(), tcnfe);
        }
    }
    
    private static void createCard(final VoteQuestion question) {
        try {
            final String name = Tickets.convertTime(question.getVoteStart()) + " " + question.getQuestionTitle() + " (" + Tickets.convertTime(question.getVoteEnd()) + ")";
            final String desc = question.getQuestionText();
            final String url = TrelloURL.make("https://api.trello.com/1/cards", new String[0]);
            final Map<String, String> keyValueMap = new HashMap<String, String>();
            keyValueMap.put("name", name);
            keyValueMap.put("desc", desc);
            keyValueMap.put("idList", Trello.trelloVotingIds);
            final JSONObject jo = doPost(url, keyValueMap);
            final String shortLink = getShortLink(jo.getString("shortUrl"));
            addVoteQuestionDetails(question, shortLink);
            VoteQuestions.queueSetTrelloCardId(question.getQuestionId(), shortLink);
        }
        catch (TrelloCardNotFoundException tcnfe) {
            Trello.logger.log(Level.WARNING, tcnfe.getMessage(), tcnfe);
        }
    }
    
    private static void addVoteQuestionDetails(final VoteQuestion question, final String shortLink) {
        try {
            final String url = TrelloURL.make("https://api.trello.com/1/cards/{0}/actions/comments", shortLink);
            StringBuilder buf = new StringBuilder();
            buf.append("SUMMARY\n\n");
            buf.append(getOptionSummary(question.getOption1Text(), question.getOption1Count(), question.getVoteCount()));
            buf.append(getOptionSummary(question.getOption2Text(), question.getOption2Count(), question.getVoteCount()));
            buf.append(getOptionSummary(question.getOption3Text(), question.getOption3Count(), question.getVoteCount()));
            buf.append(getOptionSummary(question.getOption4Text(), question.getOption4Count(), question.getVoteCount()));
            buf.append("\nTotal Players Voted: " + question.getVoteCount());
            String reply = addComment(url, buf.toString());
            buf = new StringBuilder();
            buf.append("OPTIONS\n");
            buf.append("\nVote Start: " + Tickets.convertTime(question.getVoteStart()));
            buf.append("\nVote End: " + Tickets.convertTime(question.getVoteEnd()));
            buf.append("\nAllow Multiple: " + question.isAllowMultiple());
            buf.append("\nPrem Only: " + question.isPremOnly());
            buf.append("\nJK: " + question.isJK());
            buf.append("\nMR: " + question.isMR());
            buf.append("\nHots: " + question.isHots());
            buf.append("\nFreedom: " + question.isFreedom());
            reply = addComment(url, buf.toString());
            buf = new StringBuilder();
            buf.append("SERVERS\n");
            for (final VoteServer vs : question.getServers()) {
                buf.append("\n" + Servers.getServerWithId(vs.getServerId()).name);
            }
            reply = addComment(url, buf.toString());
        }
        catch (TrelloCardNotFoundException tcnfe) {
            Trello.logger.log(Level.WARNING, tcnfe.getMessage(), tcnfe);
        }
    }
    
    private static String getOptionSummary(final String text, final int count, final int total) {
        if (text.length() == 0) {
            return "";
        }
        int perc = -1;
        String percText = " (Nan%)";
        if (total > 0) {
            perc = count * 100 / total;
            percText = " (" + perc + "%)";
        }
        final StringBuilder buf = new StringBuilder();
        buf.append(text + " [" + count + percText + "]\n");
        return buf.toString();
    }
    
    private static void createFeedbackCard(final Ticket ticket) {
        try {
            final String url = TrelloURL.make("https://api.trello.com/1/cards", new String[0]);
            final Map<String, String> keyValueMap = new HashMap<String, String>();
            keyValueMap.put("name", ticket.getTrelloFeedbackTitle());
            keyValueMap.put("idList", Trello.trelloListIds[5]);
            keyValueMap.put("idCardSource", Trello.trelloFeedbackTemplateCardId);
            final JSONObject fjo = doPost(url, keyValueMap);
            final String shortLink = getShortLink(fjo.getString("shortUrl"));
            updateCard(TrelloURL.make("https://api.trello.com/1/cards/{0}/desc", shortLink), ticket.getFeedbackText());
            tickSelected(ticket, shortLink);
            Tickets.setTicketTrelloFeedbackCardId(ticket, shortLink);
        }
        catch (TrelloCardNotFoundException tcnfe) {
            Trello.logger.log(Level.WARNING, tcnfe.getMessage(), tcnfe);
        }
    }
    
    private static void tickSelected(final Ticket ticket, final String cardId) {
        try {
            final String curl = TrelloURL.make("https://api.trello.com/1/cards/{0}/checklists", cardId);
            final Map<String, String> argumentsMap = new HashMap<String, String>();
            argumentsMap.put("card_fields", "checkItemStates,idChecklists,name");
            argumentsMap.put("checkItem_fields", "name");
            argumentsMap.put("fields", "name");
            final JSONArray ja = doGetArray(curl, argumentsMap);
            final String[] nService = { "Quality Of Service", "Superior", "Good", "Average", "Fair", "Poor" };
            final String[] nCourteous = { "Courteous", "Strongly Agree", "Somewhat Agree", "Neutral", "Somewhat Disagree", "Strongly Disagree" };
            final String[] nKnowledgeable = { "Knowledgeable", "Strongly Agree", "Somewhat Agree", "Neutral", "Somewhat Disagree", "Strongly Disagree" };
            final String[] nGeneral = { "General", "Wrong Info", "No Understand", "Unclear", "No Solve", "Disorganized", "Other", "Fine" };
            final String[] nQuality = { "Quality", "Patient", "Enthusiastic", "Listened", "Friendly", "Responsive", "Nothing" };
            final String[] nIrked = { "Irked", "Patient", "Enthusiastic", "Listened", "Friendly", "Responsive", "Nothing" };
            final String[] idService = { "", "", "", "", "", "" };
            final String[] idCourteous = { "", "", "", "", "", "" };
            final String[] idKnowledgeable = { "", "", "", "", "", "" };
            final String[] idGeneral = { "", "", "", "", "", "", "", "" };
            final String[] idQuality = { "", "", "", "", "", "", "" };
            final String[] idIrked = { "", "", "", "", "", "", "" };
            idService[0] = ja.getJSONObject(nService[0]).getString("id");
            for (int i = 1; i < idService.length; ++i) {
                idService[i] = ja.getJSONObject(nService[0]).getJSONArray("checkItems").getJSONObject(nService[i]).getString("id");
            }
            idCourteous[0] = ja.getJSONObject(nCourteous[0]).getString("id");
            for (int i = 1; i < idCourteous.length; ++i) {
                idCourteous[i] = ja.getJSONObject(nCourteous[0]).getJSONArray("checkItems").getJSONObject(nCourteous[i]).getString("id");
            }
            idKnowledgeable[0] = ja.getJSONObject(nKnowledgeable[0]).getString("id");
            for (int i = 1; i < idKnowledgeable.length; ++i) {
                idKnowledgeable[i] = ja.getJSONObject(nKnowledgeable[0]).getJSONArray("checkItems").getJSONObject(nKnowledgeable[i]).getString("id");
            }
            idGeneral[0] = ja.getJSONObject(nGeneral[0]).getString("id");
            for (int i = 1; i < idGeneral.length; ++i) {
                idGeneral[i] = ja.getJSONObject(nGeneral[0]).getJSONArray("checkItems").getJSONObject(nGeneral[i]).getString("id");
            }
            idQuality[0] = ja.getJSONObject(nQuality[0]).getString("id");
            for (int i = 1; i < idQuality.length; ++i) {
                idQuality[i] = ja.getJSONObject(nQuality[0]).getJSONArray("checkItems").getJSONObject(nQuality[i]).getString("id");
            }
            idIrked[0] = ja.getJSONObject(nIrked[0]).getString("id");
            for (int i = 1; i < idIrked.length; ++i) {
                idIrked[i] = ja.getJSONObject(nIrked[0]).getJSONArray("checkItems").getJSONObject(nIrked[i]).getString("id");
            }
            final TicketAction ta = ticket.getFeedback();
            if (ta.wasServiceSuperior()) {
                tick(cardId, idService[0], idService[1]);
            }
            if (ta.wasServiceGood()) {
                tick(cardId, idService[0], idService[2]);
            }
            if (ta.wasServiceAverage()) {
                tick(cardId, idService[0], idService[3]);
            }
            if (ta.wasServiceFair()) {
                tick(cardId, idService[0], idService[4]);
            }
            if (ta.wasServicePoor()) {
                tick(cardId, idService[0], idService[5]);
            }
            if (ta.wasCourteousStronglyAgree()) {
                tick(cardId, idCourteous[0], idCourteous[1]);
            }
            if (ta.wasCourteousSomewhatAgree()) {
                tick(cardId, idCourteous[0], idCourteous[2]);
            }
            if (ta.wasCourteousNeutral()) {
                tick(cardId, idCourteous[0], idCourteous[3]);
            }
            if (ta.wasCourteousSomewhatDisagree()) {
                tick(cardId, idCourteous[0], idCourteous[4]);
            }
            if (ta.wasCourteousStronglyDisagree()) {
                tick(cardId, idCourteous[0], idCourteous[5]);
            }
            if (ta.wasKnowledgeableStronglyAgree()) {
                tick(cardId, idKnowledgeable[0], idKnowledgeable[1]);
            }
            if (ta.wasKnowledgeableSomewhatAgree()) {
                tick(cardId, idKnowledgeable[0], idKnowledgeable[2]);
            }
            if (ta.wasKnowledgeableNeutral()) {
                tick(cardId, idKnowledgeable[0], idKnowledgeable[3]);
            }
            if (ta.wasKnowledgeableSomewhatDisagree()) {
                tick(cardId, idKnowledgeable[0], idKnowledgeable[4]);
            }
            if (ta.wasKnowledgeableStronglyDisagree()) {
                tick(cardId, idKnowledgeable[0], idKnowledgeable[5]);
            }
            if (ta.wasGeneralWrongInfo()) {
                tick(cardId, idGeneral[0], idGeneral[1]);
            }
            if (ta.wasGeneralNoUnderstand()) {
                tick(cardId, idGeneral[0], idGeneral[2]);
            }
            if (ta.wasGeneralUnclear()) {
                tick(cardId, idGeneral[0], idGeneral[3]);
            }
            if (ta.wasGeneralNoSolve()) {
                tick(cardId, idGeneral[0], idGeneral[4]);
            }
            if (ta.wasGeneralDisorganized()) {
                tick(cardId, idGeneral[0], idGeneral[5]);
            }
            if (ta.wasGeneralOther()) {
                tick(cardId, idGeneral[0], idGeneral[6]);
            }
            if (ta.wasGeneralFine()) {
                tick(cardId, idGeneral[0], idGeneral[7]);
            }
            if (ta.wasQualityPatient()) {
                tick(cardId, idQuality[0], idQuality[1]);
            }
            if (ta.wasQualityEnthusiastic()) {
                tick(cardId, idQuality[0], idQuality[2]);
            }
            if (ta.wasQualityListened()) {
                tick(cardId, idQuality[0], idQuality[3]);
            }
            if (ta.wasQualityFriendly()) {
                tick(cardId, idQuality[0], idQuality[4]);
            }
            if (ta.wasQualityResponsive()) {
                tick(cardId, idQuality[0], idQuality[5]);
            }
            if (ta.wasQualityNothing()) {
                tick(cardId, idQuality[0], idQuality[6]);
            }
            if (ta.wasIrkedPatient()) {
                tick(cardId, idIrked[0], idIrked[1]);
            }
            if (ta.wasIrkedEnthusiastic()) {
                tick(cardId, idIrked[0], idIrked[2]);
            }
            if (ta.wasIrkedListened()) {
                tick(cardId, idIrked[0], idIrked[3]);
            }
            if (ta.wasIrkedFriendly()) {
                tick(cardId, idIrked[0], idIrked[4]);
            }
            if (ta.wasIrkedResponsive()) {
                tick(cardId, idIrked[0], idIrked[5]);
            }
            if (ta.wasIrkedNothing()) {
                tick(cardId, idIrked[0], idIrked[6]);
            }
        }
        catch (TrelloCardNotFoundException tcnfe) {
            Trello.logger.log(Level.WARNING, tcnfe.getMessage(), tcnfe);
        }
    }
    
    private static void tick(final String cardId, final String checkListId, final String checkItemId) throws TrelloCardNotFoundException {
        final String url = TrelloURL.make("https://api.trello.com/1/cards/{0}/checklist/{1}/checkItem/{2}/state", cardId, checkListId, checkItemId);
        final Map<String, String> keyValueMap = new HashMap<String, String>();
        keyValueMap.put("idCheckList", checkListId);
        keyValueMap.put("idCheckItem", checkItemId);
        keyValueMap.put("value", "true");
        final JSONObject jo = doPut(url, keyValueMap);
    }
    
    private static void obtainListIds() {
        final String url = TrelloURL.make("https://api.trello.com/1/boards/{0}/lists", Constants.trelloBoardid);
        final JSONArray ja = doGetArray(url);
        int count = 0;
        for (int x = 0; x < ja.length(); ++x) {
            final JSONObject jo = ja.getJSONObject(x);
            final String name = jo.getString("name");
            for (int y = 1; y <= 5; ++y) {
                if (name.equalsIgnoreCase(Trello.trelloLists[y])) {
                    Trello.trelloListIds[y] = jo.getString("id");
                    ++count;
                    break;
                }
            }
        }
        if (count != Trello.trelloListIds.length - 1) {
            throw new JSONException("Not all the required lists found on Trello Ticket board.");
        }
        final String lurl = TrelloURL.make("https://api.trello.com/1/lists/{0}/cards", Trello.trelloListIds[5]);
        final Map<String, String> argumentsMap = new HashMap<String, String>();
        argumentsMap.put("fields", "name");
        argumentsMap.put("card_fields", "name");
        final JSONArray lja = doGetArray(lurl, argumentsMap);
        for (int x2 = 0; x2 < lja.length(); ++x2) {
            final JSONObject jo2 = lja.getJSONObject(x2);
            final String name2 = jo2.getString("name");
            if (name2.equalsIgnoreCase("Feedback Checklist Template")) {
                Trello.trelloFeedbackTemplateCardId = jo2.getString("id");
                break;
            }
        }
        if (Trello.trelloFeedbackTemplateCardId.length() == 0) {
            throw new JSONException("Could not find the Feedback Checklist Template on Trello Ticket board.");
        }
    }
    
    private static void obtainMVListIds() {
        if (Constants.trelloMVBoardId.length() == 0) {
            return;
        }
        final String url = TrelloURL.make("https://api.trello.com/1/boards/{0}/lists", Constants.trelloMVBoardId);
        final JSONArray ja = doGetArray(url);
        for (int x = 0; x < ja.length(); ++x) {
            final JSONObject jo = ja.getJSONObject(x);
            final String name = jo.getString("name");
            if (name.equals("Mutes")) {
                Trello.trelloMuteIds = jo.getString("id");
            }
            else if (name.equals("Mutewarns")) {
                Trello.trelloMutewarnIds = jo.getString("id");
            }
            else if (name.equals("Voting")) {
                Trello.trelloVotingIds = jo.getString("id");
            }
            else if (name.equals("Highways")) {
                Trello.trelloHighwaysIds = jo.getString("id");
            }
            else if (name.equals("Deaths")) {
                Trello.trelloDeathsIds = jo.getString("id");
            }
        }
        if (Trello.trelloMuteIds.length() == 0 || Trello.trelloMutewarnIds.length() == 0 || Trello.trelloVotingIds.length() == 0 || Trello.trelloHighwaysIds.length() == 0) {
            throw new JSONException("Not all the required lists found on Trello Mute Vote board.");
        }
        Trello.trelloMuteStorage = true;
        archiveCardsInList(Trello.trelloMuteIds, "Mutes");
        archiveCardsInList(Trello.trelloMutewarnIds, "Mutewarns");
        archiveCardsInList(Trello.trelloHighwaysIds, "Highways");
    }
    
    private static String getShortLink(final String shortUrl) {
        final String[] parts = shortUrl.split("/");
        final String shortLink = parts[parts.length - 1];
        return shortLink;
    }
    
    private static JSONArray doGetArray(final String url) {
        try {
            final InputStream in = doRequest(url, "GET", null);
            if (in == null) {
                throw new JSONException("Failed read permissions for Trello board.");
            }
            final JSONTokener tk = new JSONTokener(in);
            return new JSONArray(tk);
        }
        catch (TrelloCardNotFoundException tcnfe) {
            throw new JSONException("Cannot find ticket, but were not looking for one");
        }
    }
    
    private static JSONArray doGetArray(final String url, final Map<String, String> map) {
        String lurl = url;
        try {
            final StringBuilder sb = new StringBuilder();
            final boolean hasMap = map != null && !map.isEmpty();
            if (hasMap) {
                for (final String key : map.keySet()) {
                    sb.append("&");
                    sb.append(URLEncoder.encode(key, "UTF-8"));
                    sb.append("=");
                    sb.append(URLEncoder.encode(map.get(key), "UTF-8"));
                }
                lurl = url + sb.toString();
            }
        }
        catch (UnsupportedEncodingException e) {
            Trello.logger.log(Level.WARNING, e.getMessage(), e);
        }
        try {
            final InputStream in = doRequest(lurl, "GET", null);
            if (in == null) {
                throw new JSONException("Failed read permissions for Trello board.");
            }
            final JSONTokener tk = new JSONTokener(in);
            return new JSONArray(tk);
        }
        catch (TrelloCardNotFoundException tcnfe) {
            throw new JSONException("Cannot find ticket, but were not looking for one");
        }
    }
    
    private static JSONObject doPut(final String url, final Map<String, String> map) throws TrelloCardNotFoundException {
        final InputStream in = doRequest(url, "PUT", map);
        if (in == null) {
            throw new JSONException("Failed read permissions for Trello board.");
        }
        final JSONTokener tk = new JSONTokener(in);
        return new JSONObject(tk);
    }
    
    private static JSONObject doPost(final String url, final Map<String, String> map) throws TrelloCardNotFoundException {
        final InputStream in = doRequest(url, "POST", map);
        if (in == null) {
            throw new JSONException("Failed read permissions for Trello board.");
        }
        final JSONTokener tk = new JSONTokener(in);
        return new JSONObject(tk);
    }
    
    private static InputStream doRequest(final String url, final String requestMethod, final Map<String, String> map) throws TrelloCardNotFoundException {
        try {
            final boolean hasMap = map != null && !map.isEmpty();
            final HttpsURLConnection conn = (HttpsURLConnection)new URL(url).openConnection();
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            conn.setDoOutput(requestMethod.equals("POST") || requestMethod.equals("PUT"));
            conn.setRequestMethod(requestMethod);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            String arguments = "";
            if (hasMap) {
                final StringBuilder sb = new StringBuilder();
                for (final String key : map.keySet()) {
                    sb.append((sb.length() > 0) ? "&" : "");
                    sb.append(URLEncoder.encode(key, "UTF-8"));
                    sb.append("=");
                    sb.append(URLEncoder.encode(map.get(key), "UTF-8"));
                }
                conn.getOutputStream().write(sb.toString().getBytes());
                conn.getOutputStream().close();
                arguments = sb.toString();
            }
            conn.connect();
            final int rc = conn.getResponseCode();
            final String responseMessage = conn.getResponseMessage();
            if (rc != 200) {
                Trello.logger.info("response " + rc + " (" + responseMessage + ") from " + requestMethod + " " + url + " args:" + arguments);
            }
            if (rc == 404) {
                throw new TrelloCardNotFoundException("Ticket not found");
            }
            if (rc > 399) {
                final String str = stream2String(conn.getErrorStream());
                Trello.logger.info("error response:" + str);
                return null;
            }
            return getWrappedInputStream(conn.getInputStream(), "gzip".equalsIgnoreCase(conn.getContentEncoding()));
        }
        catch (IOException e) {
            throw new TrelloException(e.getMessage());
        }
    }
    
    private static InputStream getWrappedInputStream(final InputStream is, final boolean gzip) throws IOException {
        if (gzip) {
            return new BufferedInputStream(new GZIPInputStream(is));
        }
        return new BufferedInputStream(is);
    }
    
    private static String stream2String(final InputStream in) {
        final InputStreamReader is = new InputStreamReader(in);
        final BufferedReader br = new BufferedReader(is);
        final StringBuilder sb = new StringBuilder();
        try {
            for (String read = br.readLine(); read != null; read = br.readLine()) {
                sb.append(read);
            }
        }
        catch (IOException e) {
            return "Error trying to read stream:" + e.getMessage();
        }
        return sb.toString();
    }
    
    static {
        Trello.logger = Logger.getLogger(Trello.class.getName());
        trelloThread = new TrelloThread();
        trelloCardQueue = new ConcurrentLinkedDeque<TrelloCard>();
        Trello.tickerCount = 0;
        trelloLists = new String[] { "None", "Waiting GM Calls", "Waiting ARCH or Dev action", "Resolved/Cancelled", "Watching", "Feedback" };
        Trello.trelloListIds = new String[] { "", "", "", "", "", "" };
        Trello.trelloFeedbackTemplateCardId = "";
        Trello.trelloMuteIds = "";
        Trello.trelloMutewarnIds = "";
        Trello.trelloVotingIds = "";
        Trello.trelloHighwaysIds = "";
        Trello.trelloDeathsIds = "";
        Trello.trelloMuteStorage = false;
    }
    
    private static final class TrelloThread implements Runnable
    {
        @Override
        public void run() {
            if (Trello.logger.isLoggable(Level.FINEST)) {
                Trello.logger.finest("Running newSingleThreadScheduledExecutor for calling Tickets.ticker()");
            }
            try {
                final long now = System.nanoTime();
                if (Servers.isThisLoginServer() && Constants.trelloApiKey.length() > 0) {
                    updateTicketsInTrello();
                    updateMuteVoteInTrello();
                }
                final float lElapsedTime = (System.nanoTime() - now) / 1000000.0f;
                if (lElapsedTime > Constants.lagThreshold) {
                    Trello.logger.info("Finished calling Tickets.ticker(), which took " + lElapsedTime + " millis.");
                }
            }
            catch (RuntimeException e) {
                Trello.logger.log(Level.WARNING, "Caught exception in ScheduledExecutorService while calling Tickets.ticker()", e);
                throw e;
            }
        }
    }
}
