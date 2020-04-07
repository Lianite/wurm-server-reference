// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import java.util.function.BiConsumer;
import com.wurmonline.server.creatures.Communicator;
import java.util.StringTokenizer;
import com.wurmonline.server.players.Player;

public final class ServerTweaksHandler
{
    public static boolean isTweakCommand(final String message) {
        for (final Tweak tweak : Tweak.values()) {
            if (message.startsWith(tweak.getCommand())) {
                return true;
            }
        }
        return false;
    }
    
    public static void handleTweakCommand(final String message, final Player admin) {
        final StringTokenizer tokenizer = new StringTokenizer(message);
        final String cmd = tokenizer.nextToken();
        final Tweak tweak = Tweak.getByCommand(cmd);
        tweak.execute(tokenizer, admin);
    }
    
    private static boolean validatePassword(final String pass, final Player admin) {
        final String adminPass = ServerProperties.getString("ADMINPASSWORD", "");
        if (adminPass.isEmpty()) {
            admin.getCommunicator().sendNormalServerMessage("There is no admin password on this server, so admin commands is disabled.");
            return false;
        }
        if (pass.equals(adminPass)) {
            return true;
        }
        admin.getCommunicator().sendNormalServerMessage("Incorrect admin password.");
        return false;
    }
    
    public static void handleUnknownCommad(final StringTokenizer tokenizer, final Player admin) {
        admin.getCommunicator().sendNormalServerMessage("Unknown command.");
    }
    
    private static boolean tokenCheck(final Tweak tweak, final StringTokenizer tokenizer, final Player admin) {
        final int numTokens = tokenizer.countTokens();
        if (numTokens != tweak.tokenCount()) {
            final String message = "Incorrect number of parameters! Provided: %d Expected: %d";
            admin.getCommunicator().sendNormalServerMessage(String.format("Incorrect number of parameters! Provided: %d Expected: %d", numTokens, tweak.tokenCount()));
            return false;
        }
        return true;
    }
    
    public static void handleSkillGainRateCommand(final StringTokenizer tokenizer, final Player admin) {
        if (!tokenCheck(Tweak.SKILL_GAIN_RATE, tokenizer, admin)) {
            return;
        }
        final String param = tokenizer.nextToken();
        final String pass = tokenizer.nextToken();
        if (!validatePassword(pass, admin)) {
            return;
        }
        try {
            float rate = Float.parseFloat(param);
            rate = Math.max(0.01f, rate);
            admin.getCommunicator().sendNormalServerMessage("Changed skill gain multiplier to: " + rate + ".");
            Servers.localServer.setSkillGainRate(rate);
            Servers.localServer.saveNewGui(Servers.localServer.id);
        }
        catch (NumberFormatException nfe) {
            admin.getCommunicator().sendNormalServerMessage("'" + param + "' is not in the correct format.");
        }
    }
    
    public static void handleFieldGrowthCommand(final StringTokenizer tokenizer, final Player admin) {
        if (!tokenCheck(Tweak.FIELD_GROWTH, tokenizer, admin)) {
            return;
        }
        final String param = tokenizer.nextToken();
        final String pass = tokenizer.nextToken();
        if (!validatePassword(pass, admin)) {
            return;
        }
        try {
            final Float time = Float.parseFloat(param);
            admin.getCommunicator().sendNormalServerMessage("Changed field growth timer to: " + time.toString() + " hours.");
            Servers.localServer.setFieldGrowthTime((long)(time * 3600.0f * 1000.0f));
            Servers.localServer.saveNewGui(Servers.localServer.id);
        }
        catch (NumberFormatException nfe) {
            admin.getCommunicator().sendNormalServerMessage("'" + param + "' is not in the correct format.");
        }
    }
    
    public static void handleCharacteristicsStartCommand(final StringTokenizer tokenizer, final Player admin) {
        if (!tokenCheck(Tweak.CHARACTERISTICS_START, tokenizer, admin)) {
            return;
        }
        final String param = tokenizer.nextToken();
        final String pass = tokenizer.nextToken();
        if (!validatePassword(pass, admin)) {
            return;
        }
        try {
            final Float charVal = Float.parseFloat(param);
            admin.getCommunicator().sendNormalServerMessage("Changed characteristics start value to: " + charVal.toString() + ".");
            Servers.localServer.setSkillbasicval(charVal);
            Servers.localServer.saveNewGui(Servers.localServer.id);
            admin.getCommunicator().sendNormalServerMessage("Server restart needed before the changes take effect.");
        }
        catch (NumberFormatException nfe) {
            admin.getCommunicator().sendNormalServerMessage("'" + param + "' is not in the correct format.");
        }
    }
    
    public static void handleMindLogicStartCommand(final StringTokenizer tokenizer, final Player admin) {
        if (!tokenCheck(Tweak.MIND_LOGIC_START, tokenizer, admin)) {
            return;
        }
        final String param = tokenizer.nextToken();
        final String pass = tokenizer.nextToken();
        if (!validatePassword(pass, admin)) {
            return;
        }
        try {
            final Float val = Float.parseFloat(param);
            admin.getCommunicator().sendNormalServerMessage("Changed mind logic start value to: " + val.toString() + ".");
            Servers.localServer.setSkillmindval(val);
            Servers.localServer.saveNewGui(Servers.localServer.id);
            admin.getCommunicator().sendNormalServerMessage("Server restart needed before the changes take effect.");
        }
        catch (NumberFormatException nfe) {
            admin.getCommunicator().sendNormalServerMessage("'" + param + "' is not in the correct format.");
        }
    }
    
    public static void handleBodyControlStartCommand(final StringTokenizer tokenizer, final Player admin) {
        if (!tokenCheck(Tweak.BC_START, tokenizer, admin)) {
            return;
        }
        final String param = tokenizer.nextToken();
        final String pass = tokenizer.nextToken();
        if (!validatePassword(pass, admin)) {
            return;
        }
        try {
            final Float val = Float.parseFloat(param);
            admin.getCommunicator().sendNormalServerMessage("Changed body control start value to: " + val.toString() + ".");
            Servers.localServer.setSkillbcval(val);
            Servers.localServer.saveNewGui(Servers.localServer.id);
            admin.getCommunicator().sendNormalServerMessage("Server restart needed before the changes take effect.");
        }
        catch (NumberFormatException nfe) {
            admin.getCommunicator().sendNormalServerMessage("'" + param + "' is not in the correct format.");
        }
    }
    
    public static void handleFightingStartCommand(final StringTokenizer tokenizer, final Player admin) {
        if (!tokenCheck(Tweak.FIGHT_START, tokenizer, admin)) {
            return;
        }
        final String param = tokenizer.nextToken();
        final String pass = tokenizer.nextToken();
        if (!validatePassword(pass, admin)) {
            return;
        }
        try {
            final Float val = Float.parseFloat(param);
            admin.getCommunicator().sendNormalServerMessage("Changed fighting start value to: " + val.toString() + ".");
            Servers.localServer.setSkillfightval(val);
            Servers.localServer.saveNewGui(Servers.localServer.id);
            admin.getCommunicator().sendNormalServerMessage("Server restart needed before the changes take effect.");
        }
        catch (NumberFormatException nfe) {
            admin.getCommunicator().sendNormalServerMessage("'" + param + "' is not in the correct format.");
        }
    }
    
    public static void handleOverallStartCommand(final StringTokenizer tokenizer, final Player admin) {
        if (!tokenCheck(Tweak.OVERALL_START, tokenizer, admin)) {
            return;
        }
        final String param = tokenizer.nextToken();
        final String pass = tokenizer.nextToken();
        if (!validatePassword(pass, admin)) {
            return;
        }
        try {
            final Float val = Float.parseFloat(param);
            admin.getCommunicator().sendNormalServerMessage("Changed overall start skill value to: " + val.toString() + ".");
            Servers.localServer.setSkilloverallval(val);
            Servers.localServer.saveNewGui(Servers.localServer.id);
            admin.getCommunicator().sendNormalServerMessage("Server restart needed before the changes take effect.");
        }
        catch (NumberFormatException nfe) {
            admin.getCommunicator().sendNormalServerMessage("'" + param + "' is not in the correct format.");
        }
    }
    
    public static void handlePlayerCRCommand(final StringTokenizer tokenizer, final Player admin) {
        if (!tokenCheck(Tweak.PLAYER_CR, tokenizer, admin)) {
            return;
        }
        final String param = tokenizer.nextToken();
        final String pass = tokenizer.nextToken();
        if (!validatePassword(pass, admin)) {
            return;
        }
        try {
            final Float val = Float.parseFloat(param);
            admin.getCommunicator().sendNormalServerMessage("Changed player CR mod to: " + val.toString() + ".");
            Servers.localServer.setCombatRatingModifier(val);
            Servers.localServer.saveNewGui(Servers.localServer.id);
        }
        catch (NumberFormatException nfe) {
            admin.getCommunicator().sendNormalServerMessage("'" + param + "' is not in the correct format.");
        }
    }
    
    public static void handleActionSpeedCommand(final StringTokenizer tokenizer, final Player admin) {
        if (!tokenCheck(Tweak.ACTION_SPEED, tokenizer, admin)) {
            return;
        }
        final String param = tokenizer.nextToken();
        final String pass = tokenizer.nextToken();
        if (!validatePassword(pass, admin)) {
            return;
        }
        try {
            final Float val = Float.parseFloat(param);
            admin.getCommunicator().sendNormalServerMessage("Changed action speed mod to: " + val.toString() + ".");
            Servers.localServer.setActionTimer(val);
            Servers.localServer.saveNewGui(Servers.localServer.id);
        }
        catch (NumberFormatException nfe) {
            admin.getCommunicator().sendNormalServerMessage("'" + param + "' is not in the correct format.");
        }
    }
    
    public static void handleHOTACommand(final StringTokenizer tokenizer, final Player admin) {
        if (!tokenCheck(Tweak.HOTA, tokenizer, admin)) {
            return;
        }
        final String param = tokenizer.nextToken();
        final String pass = tokenizer.nextToken();
        if (!validatePassword(pass, admin)) {
            return;
        }
        try {
            final Integer val = Integer.parseInt(param);
            admin.getCommunicator().sendNormalServerMessage("Changed HOTA delay to: " + val.toString() + ".");
            Servers.localServer.setHotaDelay(val);
            Servers.localServer.saveNewGui(Servers.localServer.id);
        }
        catch (NumberFormatException nfe) {
            admin.getCommunicator().sendNormalServerMessage("'" + param + "' is not in the correct format.");
        }
    }
    
    public static void handleMaxCreaturesCommand(final StringTokenizer tokenizer, final Player admin) {
        if (!tokenCheck(Tweak.MAX_CREATURES, tokenizer, admin)) {
            return;
        }
        final String param = tokenizer.nextToken();
        final String pass = tokenizer.nextToken();
        if (!validatePassword(pass, admin)) {
            return;
        }
        try {
            int val = Integer.parseInt(param);
            val = Math.max(0, val);
            admin.getCommunicator().sendNormalServerMessage("Changed max creatures to: " + val + ".");
            Servers.localServer.maxCreatures = val;
            Servers.localServer.saveNewGui(Servers.localServer.id);
        }
        catch (NumberFormatException nfe) {
            admin.getCommunicator().sendNormalServerMessage("'" + param + "' is not in the correct format.");
        }
    }
    
    public static void handleAggCreaturesCommand(final StringTokenizer tokenizer, final Player admin) {
        if (!tokenCheck(Tweak.AGG_PERCENT, tokenizer, admin)) {
            return;
        }
        final String param = tokenizer.nextToken();
        final String pass = tokenizer.nextToken();
        if (!validatePassword(pass, admin)) {
            return;
        }
        try {
            float val = Float.parseFloat(param);
            val = Math.max(0.0f, Math.min(100.0f, val));
            admin.getCommunicator().sendNormalServerMessage("Changed aggressive creature % to: " + val + ".");
            Servers.localServer.percentAggCreatures = val;
            Servers.localServer.saveNewGui(Servers.localServer.id);
        }
        catch (NumberFormatException nfe) {
            admin.getCommunicator().sendNormalServerMessage("'" + param + "' is not in the correct format.");
        }
    }
    
    public static void handleUpkeepCommand(final StringTokenizer tokenizer, final Player admin) {
        if (!tokenCheck(Tweak.UPKEEP, tokenizer, admin)) {
            return;
        }
        final String param = tokenizer.nextToken();
        final String pass = tokenizer.nextToken();
        if (!validatePassword(pass, admin)) {
            return;
        }
        final boolean val = Boolean.parseBoolean(param);
        admin.getCommunicator().sendNormalServerMessage("Changed upkeep to: " + val + ".");
        Servers.localServer.setUpkeep(val);
        Servers.localServer.saveNewGui(Servers.localServer.id);
    }
    
    public static void handleFreeDeedsCommand(final StringTokenizer tokenizer, final Player admin) {
        if (!tokenCheck(Tweak.FREE_DEEDS, tokenizer, admin)) {
            return;
        }
        final String param = tokenizer.nextToken();
        final String pass = tokenizer.nextToken();
        if (!validatePassword(pass, admin)) {
            return;
        }
        final boolean val = Boolean.parseBoolean(param);
        admin.getCommunicator().sendNormalServerMessage("Changed free deeding to: " + val + ".");
        Servers.localServer.setFreeDeeds(val);
        Servers.localServer.saveNewGui(Servers.localServer.id);
    }
    
    public static void handleTraderMaxMoneyCommand(final StringTokenizer tokenizer, final Player admin) {
        if (!tokenCheck(Tweak.TRADER_MAX_MONEY, tokenizer, admin)) {
            return;
        }
        final String param = tokenizer.nextToken();
        final String pass = tokenizer.nextToken();
        if (!validatePassword(pass, admin)) {
            return;
        }
        try {
            int val = Integer.parseInt(param);
            val = Math.max(0, val);
            admin.getCommunicator().sendNormalServerMessage("Changed trader max money to: " + val + " silver.");
            Servers.localServer.setTraderMaxIrons(val * 10000);
            Servers.localServer.saveNewGui(Servers.localServer.id);
        }
        catch (NumberFormatException nfe) {
            admin.getCommunicator().sendNormalServerMessage("'" + param + "' is not in the correct format.");
        }
    }
    
    public static void handleTraderInitialMoneyCommand(final StringTokenizer tokenizer, final Player admin) {
        if (!tokenCheck(Tweak.TRADER_INITIAL_MONEY, tokenizer, admin)) {
            return;
        }
        final String param = tokenizer.nextToken();
        final String pass = tokenizer.nextToken();
        if (!validatePassword(pass, admin)) {
            return;
        }
        try {
            int val = Integer.parseInt(param);
            val = Math.max(0, val);
            admin.getCommunicator().sendNormalServerMessage("Changed trader initial money to: " + val + " silver.");
            Servers.localServer.setInitialTraderIrons(val * 10000);
            Servers.localServer.saveNewGui(Servers.localServer.id);
        }
        catch (NumberFormatException nfe) {
            admin.getCommunicator().sendNormalServerMessage("'" + param + "' is not in the correct format.");
        }
    }
    
    public static void handleMinimumHitsCommand(final StringTokenizer tokenizer, final Player admin) {
        if (!tokenCheck(Tweak.MINING_HITS, tokenizer, admin)) {
            return;
        }
        final String param = tokenizer.nextToken();
        final String pass = tokenizer.nextToken();
        if (!validatePassword(pass, admin)) {
            return;
        }
        try {
            int val = Integer.parseInt(param);
            val = Math.max(0, val);
            admin.getCommunicator().sendNormalServerMessage("Changed minimum mining hits on rock to: " + val + ".");
            Servers.localServer.setTunnelingHits(val);
            Servers.localServer.saveNewGui(Servers.localServer.id);
        }
        catch (NumberFormatException nfe) {
            admin.getCommunicator().sendNormalServerMessage("'" + param + "' is not in the correct format.");
        }
    }
    
    public static void handleBreedingTimeCommand(final StringTokenizer tokenizer, final Player admin) {
        if (!tokenCheck(Tweak.BREEDING_TIME, tokenizer, admin)) {
            return;
        }
        final String param = tokenizer.nextToken();
        final String pass = tokenizer.nextToken();
        if (!validatePassword(pass, admin)) {
            return;
        }
        try {
            long val = Long.parseLong(param);
            val = Math.max(1L, val);
            admin.getCommunicator().sendNormalServerMessage("Changed breeding time modifier to: " + val + ".");
            Servers.localServer.setBreedingTimer(val);
            Servers.localServer.saveNewGui(Servers.localServer.id);
        }
        catch (NumberFormatException nfe) {
            admin.getCommunicator().sendNormalServerMessage("'" + param + "' is not in the correct format.");
        }
    }
    
    public static void handleTreeSpreadOddsCommand(final StringTokenizer tokenizer, final Player admin) {
        if (!tokenCheck(Tweak.TREE_GROWTH, tokenizer, admin)) {
            return;
        }
        final String param = tokenizer.nextToken();
        final String pass = tokenizer.nextToken();
        if (!validatePassword(pass, admin)) {
            return;
        }
        try {
            int val = Integer.parseInt(param);
            val = Math.max(0, val);
            admin.getCommunicator().sendNormalServerMessage("Changed tree spread odds to: " + val + ".");
            Servers.localServer.treeGrowth = val;
            Servers.localServer.saveNewGui(Servers.localServer.id);
        }
        catch (NumberFormatException nfe) {
            admin.getCommunicator().sendNormalServerMessage("'" + param + "' is not in the correct format.");
        }
    }
    
    public static void handleMoneyPoolCommand(final StringTokenizer tokenizer, final Player admin) {
        if (!tokenCheck(Tweak.MONEY_POOL, tokenizer, admin)) {
            return;
        }
        final String param = tokenizer.nextToken();
        final String pass = tokenizer.nextToken();
        if (!validatePassword(pass, admin)) {
            return;
        }
        try {
            int val = Integer.parseInt(param);
            val = Math.max(0, val);
            admin.getCommunicator().sendNormalServerMessage("Money pool will be set to: " + val + " after a restart.");
            Servers.localServer.setKingsmoneyAtRestart(val * 10000);
            Servers.localServer.saveNewGui(Servers.localServer.id);
        }
        catch (NumberFormatException nfe) {
            admin.getCommunicator().sendNormalServerMessage("'" + param + "' is not in the correct format.");
        }
    }
    
    public static void sendHelp(final Player player) {
        final Communicator com = player.getCommunicator();
        for (final Tweak tweak : Tweak.values()) {
            if (tweak != Tweak.UNKNOWN) {
                com.sendHelpMessage(tweak.parameterString + " - " + tweak.helpDescription);
            }
        }
    }
    
    public enum Tweak
    {
        UNKNOWN("UNKNOWN", "", "", 0, ServerTweaksHandler::handleUnknownCommad), 
        FIELD_GROWTH("/setfieldgrowthtime", "/setfieldgrowthtime <time> <password>", "Sets how often fields will be polled.", 2, ServerTweaksHandler::handleFieldGrowthCommand), 
        SKILL_GAIN_RATE("/setskillgainmultiplier", "/setskillgainmultiplier <rate> <password>", "Skill gain rate multiplier.", 2, ServerTweaksHandler::handleSkillGainRateCommand), 
        CHARACTERISTICS_START("/setcharacteristicsstartvalue", "/setcharacteristicsstartvalue <value> <password>", "Sets the starting value of the characteristics skills for new players. (requires restart)", 2, ServerTweaksHandler::handleCharacteristicsStartCommand, true), 
        MIND_LOGIC_START("/setmindlogicstartvalue", "/setmindlogicstartvalue <value> <password>", "Sets the starting value of mind logic for new players. (requires restart)", 2, ServerTweaksHandler::handleMindLogicStartCommand, true), 
        BC_START("/setbodycontrolstartvalue", "/setbodycontrolstartvalue <value> <password>", "Sets the starting value of the body control skill for new players. (requires restart)", 2, ServerTweaksHandler::handleBodyControlStartCommand, true), 
        FIGHT_START("/setfightingstartvalue", "/setfightingstartvalue <value> <password>", "Sets the fighting skill start value for new players. (requires restart)", 2, ServerTweaksHandler::handleFightingStartCommand, true), 
        OVERALL_START("/setoverallstartskillvalue", "/setoverallstartskillvalue <value> <password>", "Sets the overall starting skill value for new players. (restart required)", 2, ServerTweaksHandler::handleOverallStartCommand, true), 
        PLAYER_CR("/setplayercrmod", "/setplayercrmod <CR> <password>", "Sets the combat rating of players.", 2, ServerTweaksHandler::handlePlayerCRCommand), 
        ACTION_SPEED("/setactionspeedmod", "/setactionspeedmod <mod> <password>", "Speeds up or slows down action timers.", 2, ServerTweaksHandler::handleActionSpeedCommand), 
        HOTA("/sethotadelay", "/sethotadelay <delay> <password>", "HOTA delay", 2, ServerTweaksHandler::handleHOTACommand), 
        MAX_CREATURES("/setmaxcreatures", "/setmaxcreatures <max> <password>", "Sets the maximum number of creatures that can naturally spawn on the server.", 2, ServerTweaksHandler::handleMaxCreaturesCommand), 
        AGG_PERCENT("/setmaxaggcreatures", "/setmaxaggcreatures <percent> <password>", "Sets the % of the creature pool that can be aggressive creatures.", 2, ServerTweaksHandler::handleAggCreaturesCommand), 
        UPKEEP("/setupkeep", "/setupkeep <true/false> <password>", "Toggle settlement upkeep on or off.", 2, ServerTweaksHandler::handleUpkeepCommand), 
        FREE_DEEDS("/setfreedeeds", "/setfreedeeds <true/false> <password>", "Toggle free deed creation on or off.", 2, ServerTweaksHandler::handleFreeDeedsCommand), 
        TRADER_MAX_MONEY("/settradermaxmoney", "/settradermaxmoney <silver> <password>", "Sets the max amount of money a trader can have.", 2, ServerTweaksHandler::handleTraderMaxMoneyCommand), 
        TRADER_INITIAL_MONEY("/settraderinitialmoney", "/settraderinitialmoney <silver> <password>", "Sets the initial amount of money a trader has.", 2, ServerTweaksHandler::handleTraderInitialMoneyCommand), 
        MINING_HITS("/setminimummininghits", "/setminimummininghits <hits> <password>", "Sets the amount of hits required to tunnel through rock.", 2, ServerTweaksHandler::handleMinimumHitsCommand), 
        BREEDING_TIME("/setbreedingtime", "/setbreedingtime <mod> <password>", "Modifier to speed up or slow down breeding.", 2, ServerTweaksHandler::handleBreedingTimeCommand), 
        TREE_GROWTH("/settreespreadodds", "/settreespreadodds <odds> <password>", "Toggles the spreading of trees and mushrooms.", 2, ServerTweaksHandler::handleTreeSpreadOddsCommand), 
        MONEY_POOL("/setmoneypool", "/setmoneypool <silver> <password>", "Sets the amount of money in the server pool. (requires restart)", 2, ServerTweaksHandler::handleMoneyPoolCommand);
        
        final String command;
        final String parameterString;
        final String helpDescription;
        final int expectedNumberOfTokens;
        final BiConsumer<StringTokenizer, Player> cmd;
        final boolean requiresRestart;
        
        private Tweak(final String _command, final String _parameter, final String _helpDescription, final int numberOfTokens, final BiConsumer<StringTokenizer, Player> _cmd) {
            this.command = _command;
            this.parameterString = _parameter;
            this.helpDescription = _helpDescription;
            this.expectedNumberOfTokens = numberOfTokens;
            this.cmd = _cmd;
            this.requiresRestart = false;
        }
        
        private Tweak(final String _command, final String _parameter, final String _helpDescription, final int numberOfTokens, final BiConsumer<StringTokenizer, Player> _cmd, final boolean restart) {
            this.command = _command;
            this.parameterString = _parameter;
            this.helpDescription = _helpDescription;
            this.expectedNumberOfTokens = numberOfTokens;
            this.cmd = _cmd;
            this.requiresRestart = restart;
        }
        
        public final String getCommand() {
            return this.command;
        }
        
        public final String getParameterString() {
            return this.parameterString;
        }
        
        public final int tokenCount() {
            return this.expectedNumberOfTokens;
        }
        
        public final void execute(final StringTokenizer tokenizer, final Player admin) {
            if (this.cmd != null) {
                this.cmd.accept(tokenizer, admin);
            }
        }
        
        public static final Tweak getByCommand(final String cmd) {
            for (final Tweak tweak : values()) {
                if (tweak.getCommand().equalsIgnoreCase(cmd)) {
                    return tweak;
                }
            }
            return Tweak.UNKNOWN;
        }
    }
}
