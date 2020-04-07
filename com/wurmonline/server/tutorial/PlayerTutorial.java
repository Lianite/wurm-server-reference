// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.tutorial;

import java.util.Properties;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Players;
import com.wurmonline.server.players.Titles;
import com.wurmonline.server.tutorial.stages.WelcomeStage;
import com.wurmonline.server.tutorial.stages.FishingStage;
import java.util.StringTokenizer;
import com.wurmonline.server.players.Player;
import java.util.HashMap;

public class PlayerTutorial
{
    private static HashMap<Long, PlayerTutorial> currentTutorials;
    public static final String[] DEFAULT_STAGE_NAMES;
    public static final String[] FISHING_STAGE_NAMES;
    private long playerId;
    private TutorialStage currentStage;
    private final TutorialStage initialStage;
    private final TutorialMethods customMethods;
    
    public static PlayerTutorial getTutorialForPlayer(final long wurmId, final boolean create) {
        if (!PlayerTutorial.currentTutorials.containsKey(wurmId) && create) {
            addTutorial(wurmId, new PlayerTutorial(wurmId));
        }
        return PlayerTutorial.currentTutorials.get(wurmId);
    }
    
    public static void addTutorial(final long wurmId, final PlayerTutorial tutorial) {
        PlayerTutorial.currentTutorials.put(wurmId, tutorial);
    }
    
    public static void removeTutorial(final long wurmId) {
        PlayerTutorial.currentTutorials.remove(wurmId);
    }
    
    public static void firePlayerTrigger(final long wurmId, final PlayerTrigger trigger) {
        if (!PlayerTutorial.currentTutorials.containsKey(wurmId)) {
            return;
        }
        if (!PlayerTutorial.currentTutorials.get(wurmId).getCurrentStage().awaitingTrigger(trigger)) {
            return;
        }
        PlayerTutorial.currentTutorials.get(wurmId).getCurrentStage().clearTrigger();
        PlayerTutorial.currentTutorials.get(wurmId).sendUpdateStageBML();
    }
    
    public static boolean doesTutorialExist(final long wurmId) {
        return PlayerTutorial.currentTutorials.containsKey(wurmId);
    }
    
    public static void endTutorial(final Player p) {
        if (doesTutorialExist(p.getWurmId())) {
            getTutorialForPlayer(p.getWurmId(), false).customMethods.tutorialSkipped(p);
        }
    }
    
    public static void startTutorialCommand(final Player player, final String message) {
        getTutorialForPlayer(player.getWurmId(), true).sendCurrentStageBML();
    }
    
    public static void skipTutorialCommand(final Player player, final String message) {
        if (doesTutorialExist(player.getWurmId())) {
            player.getCommunicator().sendCloseWindow(getTutorialForPlayer(player.getWurmId(), false).getCurrentStage().getWindowId());
            endTutorial(player);
            removeTutorial(player.getWurmId());
        }
        else {
            player.getCommunicator().sendNormalServerMessage("You do not currently have an active tutorial. Nothing to skip.");
        }
    }
    
    public static void testTutorialCommand(final Player player, final String message) {
        final StringTokenizer st = new StringTokenizer(message);
        st.nextToken();
        if (doesTutorialExist(player.getWurmId())) {
            player.getCommunicator().sendCloseWindow(getTutorialForPlayer(player.getWurmId(), false).getCurrentStage().getWindowId());
            endTutorial(player);
            removeTutorial(player.getWurmId());
        }
        else if (st.hasMoreTokens()) {
            final String tutorialType = st.nextToken();
            int fastForward = 0;
            if (st.hasMoreTokens()) {
                fastForward = Integer.parseInt(st.nextToken());
            }
            final String lowerCase = tutorialType.toLowerCase();
            switch (lowerCase) {
                case "fishing": {
                    startNewTutorial(player, TutorialType.FISHING, fastForward);
                    break;
                }
                default: {
                    startNewTutorial(player, TutorialType.DEFAULT, fastForward);
                    break;
                }
            }
        }
        else {
            getTutorialForPlayer(player.getWurmId(), true).sendCurrentStageBML();
        }
    }
    
    public static void startNewTutorial(final Player p, final TutorialType t, final int fastForward) {
        if (doesTutorialExist(p.getWurmId())) {
            p.getCommunicator().sendCloseWindow(getTutorialForPlayer(p.getWurmId(), false).getCurrentStage().getWindowId());
            if (!p.hasFlag(42)) {
                endTutorial(p);
            }
            removeTutorial(p.getWurmId());
        }
        switch (t) {
            case FISHING: {
                final PlayerTutorial newTut = new PlayerTutorial(p.getWurmId(), new FishingStage(p.getWurmId()), new TutorialMethods() {
                    @Override
                    public void tutorialCompleted(final Player p) {
                        p.getCommunicator().sendNormalServerMessage("Fishing tutorial completed. You can restart the tutorial through your journal.");
                    }
                    
                    @Override
                    public void tutorialSkipped(final Player p) {
                        p.getCommunicator().sendNormalServerMessage("Fishing tutorial closed. You can restart the tutorial through your journal.");
                    }
                });
                addTutorial(p.getWurmId(), newTut);
                if (fastForward > 0) {
                    for (int i = 0; i < fastForward; ++i) {
                        if (!newTut.skipCurrentStage()) {
                            p.getCommunicator().sendNormalServerMessage("Cannot skip to stage " + fastForward + " as there are only " + i + " stages in this tutorial.");
                            break;
                        }
                    }
                    newTut.getCurrentStage().setForceOpened(true);
                }
                newTut.sendCurrentStageBML();
                break;
            }
            default: {
                final PlayerTutorial newBasicTut = getTutorialForPlayer(p.getWurmId(), true);
                if (fastForward > 0) {
                    for (int j = 0; j < fastForward; ++j) {
                        if (!newBasicTut.skipCurrentStage()) {
                            p.getCommunicator().sendNormalServerMessage("Cannot skip to stage " + fastForward + " as there are only " + j + " stages in this tutorial.");
                            break;
                        }
                    }
                    newBasicTut.getCurrentStage().setForceOpened(true);
                }
                newBasicTut.sendCurrentStageBML();
                break;
            }
        }
    }
    
    public static void sendTutorialList(final Player p) {
        p.getCommunicator().sendPersonalJournalTutorial((byte)(-1), (byte)TutorialType.DEFAULT.ordinal(), "New Player Tutorial");
        p.getCommunicator().sendPersonalJournalTutorial((byte)(-1), (byte)TutorialType.FISHING.ordinal(), "Fishing Tutorial");
        for (int i = 0; i < PlayerTutorial.DEFAULT_STAGE_NAMES.length; ++i) {
            p.getCommunicator().sendPersonalJournalTutorial((byte)TutorialType.DEFAULT.ordinal(), (byte)i, PlayerTutorial.DEFAULT_STAGE_NAMES[i]);
        }
        for (int i = 0; i < PlayerTutorial.FISHING_STAGE_NAMES.length; ++i) {
            p.getCommunicator().sendPersonalJournalTutorial((byte)TutorialType.FISHING.ordinal(), (byte)i, PlayerTutorial.FISHING_STAGE_NAMES[i]);
        }
    }
    
    public PlayerTutorial(final long playerId) {
        this(playerId, new WelcomeStage(playerId), new TutorialMethods() {
            @Override
            public void tutorialCompleted(final Player p) {
                p.getCommunicator().sendOpenWindow((short)41, true);
                p.addTitle(Titles.Title.Educated);
                if (!p.hasFlag(42)) {
                    p.setFlag(42, true);
                    p.getSaveFile().addToSleep(3600);
                    p.getCommunicator().sendNormalServerMessage("For completing the tutorial you are awarded 1 hour of sleep bonus!", (byte)2);
                }
            }
            
            @Override
            public void tutorialSkipped(final Player p) {
                p.getCommunicator().sendNormalServerMessage("Tutorial closed. You can restart the tutorial through your journal.");
                p.getCommunicator().sendOpenWindow((short)9, false);
                p.getCommunicator().sendOpenWindow((short)5, false);
                p.getCommunicator().sendOpenWindow((short)1, false);
                p.getCommunicator().sendOpenWindow((short)3, false);
                p.getCommunicator().sendOpenWindow((short)11, false);
                p.getCommunicator().sendOpenWindow((short)4, false);
                p.getCommunicator().sendOpenWindow((short)6, false);
                p.getCommunicator().sendOpenWindow((short)7, false);
                p.getCommunicator().sendOpenWindow((short)2, false);
                p.getCommunicator().sendOpenWindow((short)12, false);
                p.getCommunicator().sendOpenWindow((short)13, false);
                p.getCommunicator().sendOpenWindow((short)41, false);
                p.getCommunicator().sendToggleAllQuickbarBtns(true);
                p.addTitle(Titles.Title.Educated);
            }
        });
    }
    
    public PlayerTutorial(final long playerId, final TutorialStage initialStage, final TutorialMethods customMethods) {
        this.playerId = playerId;
        this.initialStage = initialStage;
        this.currentStage = initialStage;
        this.customMethods = customMethods;
        addTutorial(playerId, this);
    }
    
    public long getPlayerId() {
        return this.playerId;
    }
    
    public TutorialStage getCurrentStage() {
        return this.currentStage;
    }
    
    public boolean skipCurrentStage() {
        if (this.currentStage.getNextStage() != null) {
            this.currentStage = this.currentStage.getNextStage();
            return true;
        }
        return false;
    }
    
    public boolean increaseCurrentStage() {
        if (!this.getCurrentStage().increaseSubStage()) {
            if (this.currentStage.isForceOpened()) {
                this.currentStage = null;
            }
            else {
                this.currentStage = this.currentStage.getNextStage();
            }
            if (this.currentStage == null) {
                removeTutorial(this.getPlayerId());
                return false;
            }
        }
        return true;
    }
    
    public void restart() {
        (this.currentStage = this.initialStage).resetSubStage();
        this.sendCurrentStageBML();
    }
    
    public void decreaseCurrentStage() {
        if (this.getCurrentStage().decreaseSubStage()) {
            (this.currentStage = this.currentStage.getLastStage()).toLastSubStage();
            if (this.currentStage == null) {
                this.currentStage = this.initialStage;
            }
        }
    }
    
    public void sendCurrentStageBML() {
        try {
            final Player p = Players.getInstance().getPlayer(this.getPlayerId());
            p.getCommunicator().sendBml(this.getCurrentStage().getWindowId(), 320, 450, 0.0f, 0.5f, false, p.hasFlag(42), this.getCurrentStage().getCurrentBML(), 255, 255, 255, "Tutorial");
        }
        catch (NoSuchPlayerException e) {
            removeTutorial(this.getPlayerId());
        }
    }
    
    public void sendUpdateStageBML() {
        try {
            final Player p = Players.getInstance().getPlayer(this.getPlayerId());
            p.getCommunicator().sendBml(this.getCurrentStage().getWindowId(), 320, 450, 0.0f, 0.5f, false, p.hasFlag(42), this.getCurrentStage().getUpdateBML(), 255, 255, 255, "Tutorial");
        }
        catch (NoSuchPlayerException e) {
            removeTutorial(this.getPlayerId());
        }
    }
    
    public void updateReceived(final Properties answers) {
        final String skipTutorial = answers.getProperty("close");
        if (skipTutorial != null && skipTutorial.equals("true")) {
            try {
                final Player p = Players.getInstance().getPlayer(this.getPlayerId());
                endTutorial(p);
            }
            catch (NoSuchPlayerException ex) {}
            removeTutorial(this.getPlayerId());
        }
        final String nextStage = answers.getProperty("next");
        final String skipStage = answers.getProperty("skip");
        if ((nextStage != null && nextStage.equals("true")) || (skipStage != null && skipStage.equals("true"))) {
            final boolean wasForced = this.getCurrentStage().isForceOpened();
            if (this.increaseCurrentStage()) {
                this.sendCurrentStageBML();
                if (this.getCurrentStage().shouldSkipTrigger()) {
                    this.sendUpdateStageBML();
                }
            }
            else if (!wasForced) {
                try {
                    final Player p2 = Players.getInstance().getPlayer(this.playerId);
                    this.customMethods.tutorialCompleted(p2);
                }
                catch (NoSuchPlayerException ex2) {}
            }
        }
        final String lastStage = answers.getProperty("back");
        if (lastStage != null && lastStage.equals("true")) {
            this.decreaseCurrentStage();
            this.sendCurrentStageBML();
            this.getCurrentStage().clearTrigger();
            this.sendUpdateStageBML();
        }
        final String restartTut = answers.getProperty("restart");
        if (restartTut != null && restartTut.equals("true")) {
            getTutorialForPlayer(this.getPlayerId(), false).restart();
        }
    }
    
    static {
        PlayerTutorial.currentTutorials = new HashMap<Long, PlayerTutorial>();
        DEFAULT_STAGE_NAMES = new String[] { "Welcome to Wurm", "Looking Around", "Movement", "Inventory & Items", "Starting Out", "Activating & Equipping", "World Interaction", "Dropping & Taking", "Terraforming", "Woodcutting", "Creating Items", "Mining", "Skills", "Combat", "Keybinds, Rules & Settings" };
        FISHING_STAGE_NAMES = new String[] { "Intro, Net & Spear Fishing", "Rod & Pole Fishing", "Final Tips" };
    }
    
    public enum TutorialType
    {
        DEFAULT, 
        FISHING;
    }
    
    public enum PlayerTrigger
    {
        NONE, 
        MOVED_PLAYER_VIEW, 
        MOVED_PLAYER, 
        ENABLED_CLIMBING, 
        DISABLED_CLIMBING, 
        ENABLED_INVENTORY, 
        DISABLED_INVENTORY, 
        ACTIVATED_ITEM, 
        EQUIPPED_ITEM, 
        ENABLED_CHARACTER, 
        PLACED_ITEM, 
        TAKEN_ITEM, 
        DIG_TILE, 
        CUT_TREE, 
        FELL_TREE, 
        CREATE_LOG, 
        ENABLED_CREATION, 
        CREATE_KINDLING, 
        CREATE_CAMPFIRE, 
        MINE_IRON;
    }
    
    public abstract static class TutorialMethods
    {
        public abstract void tutorialCompleted(final Player p0);
        
        public abstract void tutorialSkipped(final Player p0);
    }
}
