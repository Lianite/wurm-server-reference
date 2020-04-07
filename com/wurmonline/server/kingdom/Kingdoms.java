// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.kingdom;

import java.util.HashMap;
import com.wurmonline.server.Servers;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Server;
import java.util.LinkedList;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.Items;
import com.wurmonline.server.Features;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.Players;
import java.util.Iterator;
import com.wurmonline.server.items.Item;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.Map;
import com.wurmonline.server.MiscConstants;

public final class Kingdoms implements MiscConstants
{
    public static final String KINGDOM_NAME_JENN = "Jenn-Kellon";
    public static final String KINGDOM_CHAT_JENN = "Jenn-Kellon";
    public static final String KINGDOM_NAME_MOLREHAN = "Mol Rehan";
    public static final String KINGDOM_CHAT_MOLREHAN = "Mol Rehan";
    public static final String KINGDOM_NAME_LIBILA = "Horde of the Summoned";
    public static final String KINGDOM_CHAT_HOTS = "HOTS";
    public static final String KINGDOM_NAME_FREEDOM = "Freedom Isles";
    public static final String KINGDOM_CHAT_FREEDOM = "Freedom";
    public static final String KINGDOM_NAME_NONE = "no known kingdom";
    public static final String KINGDOM_SUFFIX_JENN = "jenn.";
    public static final String KINGDOM_SUFFIX_MOLREHAN = "molr.";
    public static final String KINGDOM_SUFFIX_HOTS = "hots.";
    public static final String KINGDOM_SUFFIX_FREEDOM = "free.";
    public static final String KINGDOM_SUFFIX_NONE = "";
    public static int activePremiumJenn;
    public static int activePremiumMolr;
    public static int activePremiumHots;
    public static final int TOWER_INFLUENCE = 60;
    public static final int CHALLENGE_ITEM_INFLUENCE = 20;
    public static final int minKingdomDist;
    public static final int maxTowerDistance = 100;
    private static final Map<Byte, Kingdom> kingdoms;
    private static Logger logger;
    private static final ConcurrentHashMap<Item, GuardTower> towers;
    public static final int minOwnTowerDistance = 50;
    public static final int minArcheryTowerDistance = 20;
    
    public static final void createBasicKingdoms() {
        addKingdom(new Kingdom((byte)0, (byte)0, "no known kingdom", "abofk7ba", "none", "", "Unknown", "Unknown", false));
        addKingdom(new Kingdom((byte)1, (byte)1, "Jenn-Kellon", "abosdsd", "Jenn-Kellon", "jenn.", "Noble", "Protectors", true));
        addKingdom(new Kingdom((byte)2, (byte)2, "Mol Rehan", "ajajkjh3d", "Mol Rehan", "molr.", "Fire", "Gold", true));
        addKingdom(new Kingdom((byte)3, (byte)3, "Horde of the Summoned", "11dfkjutyd", "HOTS", "hots.", "Hate", "Vengeance", true));
        addKingdom(new Kingdom((byte)4, (byte)4, "Freedom Isles", "asiuytsr", "Freedom", "free.", "Peaceful", "Friendly", true));
    }
    
    protected static final int numKingdoms() {
        return Kingdoms.kingdoms.size();
    }
    
    static int getActivePremiumJenn() {
        return Kingdoms.activePremiumJenn;
    }
    
    static void setActivePremiumJenn(final int aActivePremiumJenn) {
        Kingdoms.activePremiumJenn = aActivePremiumJenn;
    }
    
    static int getActivePremiumMolr() {
        return Kingdoms.activePremiumMolr;
    }
    
    static void setActivePremiumMolr(final int aActivePremiumMolr) {
        Kingdoms.activePremiumMolr = aActivePremiumMolr;
    }
    
    static int getActivePremiumHots() {
        return Kingdoms.activePremiumHots;
    }
    
    static void setActivePremiumHots(final int aActivePremiumHots) {
        Kingdoms.activePremiumHots = aActivePremiumHots;
    }
    
    public static final String getNameFor(final byte kingdom) {
        final Kingdom k = getKingdomOrNull(kingdom);
        if (k != null) {
            return k.getName();
        }
        return "no known kingdom";
    }
    
    public static final boolean isKingdomChat(final String chatTitle) {
        for (final Kingdom k : Kingdoms.kingdoms.values()) {
            if (k.getChatName().equals(chatTitle)) {
                return true;
            }
        }
        return false;
    }
    
    public static final boolean isGlobalKingdomChat(final String chatTitle) {
        for (final Kingdom k : Kingdoms.kingdoms.values()) {
            if (("GL-" + k.getChatName()).equals(chatTitle)) {
                return true;
            }
        }
        return false;
    }
    
    public static final boolean mayCreateKingdom() {
        return Kingdoms.kingdoms.size() < 255;
    }
    
    public static final Kingdom getKingdomWithName(final String kname) {
        for (final Kingdom k : Kingdoms.kingdoms.values()) {
            if (k.getName().equalsIgnoreCase(kname)) {
                return k;
            }
        }
        return null;
    }
    
    public static final Kingdom getKingdomWithChatTitle(final String chatTitle) {
        for (final Kingdom k : Kingdoms.kingdoms.values()) {
            if (k.getChatName().equals(chatTitle)) {
                return k;
            }
        }
        return null;
    }
    
    public static final Kingdom getKingdomWithSuffix(final String suffix) {
        for (final Kingdom k : Kingdoms.kingdoms.values()) {
            if (k.getSuffix().equals(suffix)) {
                return k;
            }
        }
        return null;
    }
    
    public static final void loadKingdom(final Kingdom kingdom) {
        final Kingdom oldk = Kingdoms.kingdoms.get(kingdom.kingdomId);
        if (oldk != null) {
            kingdom.setAlliances(oldk.getAllianceMap());
        }
        Kingdoms.kingdoms.put(kingdom.kingdomId, kingdom);
    }
    
    public static final boolean addKingdom(final Kingdom kingdom) {
        boolean isNew = false;
        boolean exists = false;
        final Kingdom oldk = Kingdoms.kingdoms.get(kingdom.kingdomId);
        if (oldk != null) {
            exists = true;
            kingdom.setAlliances(oldk.getAllianceMap());
            kingdom.setExistsHere(oldk.existsHere());
            kingdom.activePremiums = oldk.activePremiums;
            if (oldk.acceptsTransfers() != kingdom.acceptsTransfers() || !oldk.getFirstMotto().equals(kingdom.getFirstMotto()) || !oldk.getSecondMotto().equals(kingdom.getSecondMotto()) || !oldk.getPassword().equals(kingdom.getPassword())) {
                kingdom.update();
            }
        }
        else {
            isNew = true;
        }
        Kingdoms.kingdoms.put(kingdom.kingdomId, kingdom);
        if (isNew) {
            Players.getInstance().sendKingdomToPlayers(kingdom);
        }
        kingdom.setShouldBeDeleted(false);
        if (!exists) {
            kingdom.saveToDisk();
        }
        return isNew;
    }
    
    public static void markAllKingdomsForDeletion() {
        final Kingdom[] allKingdoms2;
        final Kingdom[] allKingdoms = allKingdoms2 = getAllKingdoms();
        for (final Kingdom k : allKingdoms2) {
            k.setShouldBeDeleted(true);
        }
    }
    
    public static void trimKingdoms() {
        final Kingdom[] allKingdoms2;
        final Kingdom[] allKingdoms = allKingdoms2 = getAllKingdoms();
        for (final Kingdom k : allKingdoms2) {
            if (k.isShouldBeDeleted()) {
                k.delete();
                removeKingdom(k.getId());
            }
        }
    }
    
    public static final void removeKingdom(final byte id) {
        King.purgeKing(id);
        Kingdoms.kingdoms.remove(id);
    }
    
    public static final Kingdom getKingdomOrNull(final byte id) {
        return Kingdoms.kingdoms.get(id);
    }
    
    public static final Kingdom getKingdom(final byte id) {
        final Kingdom toret = Kingdoms.kingdoms.get(id);
        if (toret == null) {
            return Kingdoms.kingdoms.get((byte)0);
        }
        return toret;
    }
    
    public static final byte getKingdomTemplateFor(final byte id) {
        final Kingdom toret = Kingdoms.kingdoms.get(id);
        if (toret == null) {
            return 0;
        }
        return toret.getTemplate();
    }
    
    public static final Kingdom[] getAllKingdoms() {
        return Kingdoms.kingdoms.values().toArray(new Kingdom[Kingdoms.kingdoms.values().size()]);
    }
    
    public static ConcurrentHashMap<Item, GuardTower> getTowers() {
        return Kingdoms.towers;
    }
    
    public static final byte getNextAvailableKingdomId() {
        for (byte b = -128; b < 127; ++b) {
            if ((b < 0 || b > 4) && Kingdoms.kingdoms.get(b) == null) {
                return b;
            }
        }
        return 0;
    }
    
    public static final String getSuffixFor(final byte kingdom) {
        final Kingdom k = getKingdomOrNull(kingdom);
        if (k != null) {
            return k.getSuffix();
        }
        return "";
    }
    
    public static final String getChatNameFor(final byte kingdom) {
        final Kingdom k = getKingdomOrNull(kingdom);
        if (k != null) {
            return k.getChatName();
        }
        return "no known kingdom";
    }
    
    public static final void addTower(final Item tower) {
        if (!Kingdoms.towers.keySet().contains(tower)) {
            Kingdoms.towers.put(tower, new GuardTower(tower));
            addTowerKingdom(tower);
        }
    }
    
    public static final void reAddKingdomInfluences(final int startx, final int starty, final int endx, final int endy) {
        for (final Village v : Villages.getVillagesWithin(startx, starty, endx, endy)) {
            v.setKingdomInfluence();
        }
        Zones.addWarDomains();
        for (final Item it : Kingdoms.towers.keySet()) {
            if (it.getTileX() >= startx && it.getTileX() <= endx && it.getTileY() >= starty && it.getTileY() < endy) {
                addTowerKingdom(it);
            }
        }
    }
    
    public static void addTowerKingdom(final Item tower) {
        if (tower.getKingdom() != 0 && tower.getTemplateId() != 996) {
            final Kingdom k = getKingdom(tower.getKingdom());
            if (k.getId() != 0) {
                for (int x = tower.getTileX() - 60; x < tower.getTileX() + 60; ++x) {
                    for (int y = tower.getTileY() - 60; y < tower.getTileY() + 60; ++y) {
                        if (Zones.getKingdom(x, y) == 0) {
                            Zones.setKingdom(x, y, tower.getKingdom());
                        }
                    }
                }
                if (Features.Feature.TOWER_CHAINING.isEnabled()) {
                    InfluenceChain.addTowerToChain(k.getId(), tower);
                }
            }
        }
    }
    
    public static final void removeInfluenceForTower(final Item item) {
        final int extraCheckedTiles = 1;
        for (int x = item.getTileX() - 60 - 1; x < item.getTileX() + 60 + 1; ++x) {
            for (int y = item.getTileY() - 60 - 1; y < item.getTileY() + 60 + 1; ++y) {
                if (Zones.getKingdom(x, y) == item.getKingdom() && Villages.getVillageWithPerimeterAt(x, y, true) == null) {
                    Zones.setKingdom(x, y, (byte)0);
                }
            }
        }
        if (Features.Feature.TOWER_CHAINING.isEnabled()) {
            InfluenceChain.removeTowerFromChain(item.getKingdom(), item);
        }
    }
    
    public static final void addWarTargetKingdom(final Item target) {
        if (target.getKingdom() != 0) {
            final Kingdom k = getKingdom(target.getKingdom());
            if (k.getId() != 0) {
                final int sx = Zones.safeTileX(target.getTileX() - 60);
                final int ex = Zones.safeTileX(target.getTileX() + 60);
                final int sy = Zones.safeTileY(target.getTileY() - 60);
                final int ey = Zones.safeTileY(target.getTileY() + 60);
                for (int x = sx; x <= ex; ++x) {
                    for (int y = sy; y <= ey; ++y) {
                        if (Villages.getVillageWithPerimeterAt(x, y, true) == null) {
                            Zones.setKingdom(x, y, target.getKingdom());
                        }
                    }
                }
            }
        }
    }
    
    public static final void destroyTower(final Item item) {
        destroyTower(item, false);
    }
    
    public static final void destroyTower(final Item item, final boolean destroyItem) {
        if (Kingdoms.towers == null || Kingdoms.towers.size() == 0) {
            final GuardTower t = new GuardTower(item);
            t.destroy();
            Items.destroyItem(item.getWurmId());
        }
        else {
            final GuardTower t = Kingdoms.towers.get(item);
            if (t != null) {
                t.destroy();
            }
            Kingdoms.towers.remove(item);
            if (destroyItem) {
                Items.destroyItem(item.getWurmId());
            }
            removeInfluenceForTower(item);
            Zones.removeGuardTower(item);
            reAddKingdomInfluences(item.getTileX() - 200, item.getTileY() - 200, item.getTileX() + 200, item.getTileY() + 200);
        }
    }
    
    public static final GuardTower getTower(final Item tower) {
        return Kingdoms.towers.get(tower);
    }
    
    public static final GuardTower getClosestTower(final int tilex, final int tiley, final boolean surfaced) {
        GuardTower closest = null;
        int minDist = 2000;
        for (final GuardTower tower : Kingdoms.towers.values()) {
            if (tower.getTower().isOnSurface() == surfaced) {
                final int distx = Math.abs(tower.getTower().getTileX() - tilex);
                final int disty = Math.abs(tower.getTower().getTileY() - tiley);
                if (distx >= 50 || disty >= 50 || (distx > minDist && disty > minDist)) {
                    continue;
                }
                minDist = Math.min(distx, disty);
                closest = tower;
            }
        }
        return closest;
    }
    
    public static final GuardTower getClosestEnemyTower(final int tilex, final int tiley, final boolean surfaced, final Creature searcher) {
        GuardTower closest = null;
        if (searcher.getKingdomId() != 0) {
            int minDist = 2000;
            for (final GuardTower tower : Kingdoms.towers.values()) {
                if (tower.getTower().isOnSurface() == surfaced) {
                    final int distx = Math.abs(tower.getTower().getTileX() - tilex);
                    final int disty = Math.abs(tower.getTower().getTileY() - tiley);
                    if ((distx > minDist && disty > minDist) || searcher.isFriendlyKingdom(tower.getKingdom())) {
                        continue;
                    }
                    minDist = Math.min(distx, disty);
                    closest = tower;
                }
            }
        }
        return closest;
    }
    
    public static final Item getClosestWarTarget(final int tilex, final int tiley, final Creature searcher) {
        Item closest = null;
        if (searcher.getKingdomId() != 0) {
            int minDist = 200;
            for (final Item target : Items.getWarTargets()) {
                final int distx = Math.abs(target.getTileX() - tilex);
                final int disty = Math.abs(target.getTileY() - tiley);
                if ((distx <= minDist || disty <= minDist) && searcher.isFriendlyKingdom(target.getKingdom())) {
                    minDist = Math.min(distx, disty);
                    closest = target;
                }
            }
        }
        return closest;
    }
    
    public static final GuardTower getTower(final Creature guard) {
        return guard.getGuardTower();
    }
    
    public static final GuardTower getRandomTowerForKingdom(final byte kingdom) {
        final LinkedList<GuardTower> tows = new LinkedList<GuardTower>();
        for (final GuardTower tower : Kingdoms.towers.values()) {
            if (tower.getKingdom() == kingdom) {
                tows.add(tower);
            }
        }
        if (tows.size() > 0) {
            return tows.get(Server.rand.nextInt(tows.size()));
        }
        return null;
    }
    
    public static final boolean isTowerTooNear(final int tilex, final int tiley, final boolean surfaced, final boolean archery) {
        if (archery) {
            for (final Item gt : Items.getAllItems()) {
                if (gt.isProtectionTower() && gt.isOnSurface() == surfaced && Math.abs(((int)gt.getPosX() >> 2) - tilex) < 20 && Math.abs(((int)gt.getPosY() >> 2) - tiley) < 20) {
                    return true;
                }
            }
        }
        else {
            for (final Item gt2 : Kingdoms.towers.keySet()) {
                if (gt2.isOnSurface() == surfaced && Math.abs(((int)gt2.getPosX() >> 2) - tilex) < 50 && Math.abs(((int)gt2.getPosY() >> 2) - tiley) < 50) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static final void convertTowersWithin(final int startx, final int starty, final int endx, final int endy, final byte newKingdom) {
        for (final Item it : Kingdoms.towers.keySet()) {
            if (it.getTileX() >= startx && it.getTileX() <= endx && it.getTileY() >= starty && it.getTileY() < endy) {
                removeInfluenceForTower(it);
                it.setAuxData(newKingdom);
                addTowerKingdom(it);
                final Kingdom k = getKingdom(newKingdom);
                boolean changed = false;
                if (k != null) {
                    final String aName = k.getName() + " guard tower";
                    it.setName(aName);
                    int templateId = 384;
                    if (k.getTemplate() == 2) {
                        templateId = 528;
                    }
                    else if (k.getTemplate() == 3) {
                        templateId = 430;
                    }
                    if (k.getTemplate() == 4) {
                        templateId = 638;
                    }
                    if (it.getTemplateId() != templateId) {
                        it.setTemplateId(templateId);
                        changed = true;
                    }
                }
                if (!changed) {
                    it.updateIfGroundItem();
                }
                Kingdoms.towers.get(it).destroyGuards();
            }
        }
    }
    
    public static final void poll() {
        final Iterator<GuardTower> it = Kingdoms.towers.values().iterator();
        while (it.hasNext()) {
            it.next().poll();
        }
        final King[] kings = King.getKings();
        if (kings != null) {
            for (final King king : kings) {
                try {
                    final Player player = Players.getInstance().getPlayer(king.kingid);
                    if (player.getKingdomId() != king.kingdom) {
                        king.abdicate(player.isOnSurface(), false);
                    }
                }
                catch (NoSuchPlayerException ex) {}
            }
        }
    }
    
    public static int getNumberOfGuardTowers() {
        int numberOfTowers;
        if (Kingdoms.towers != null) {
            numberOfTowers = Kingdoms.towers.size();
        }
        else {
            numberOfTowers = 0;
        }
        return numberOfTowers;
    }
    
    public static void checkIfDisbandKingdom() {
    }
    
    public static final void destroyTowersWithKingdom(final byte deletedKingdom) {
        if (Kingdoms.towers != null) {
            for (final GuardTower tower : Kingdoms.towers.values()) {
                if (tower.getKingdom() == deletedKingdom) {
                    destroyTower(tower.getTower(), true);
                }
            }
        }
    }
    
    public static final boolean isCustomKingdom(final byte kingdomId) {
        return kingdomId < 0 || kingdomId > 4;
    }
    
    static {
        Kingdoms.activePremiumJenn = 0;
        Kingdoms.activePremiumMolr = 0;
        Kingdoms.activePremiumHots = 0;
        minKingdomDist = (Servers.localServer.isChallengeServer() ? 60 : ((Servers.localServer.id == 3) ? 100 : 150));
        kingdoms = new HashMap<Byte, Kingdom>();
        Kingdoms.logger = Logger.getLogger(Kingdoms.class.getName());
        towers = new ConcurrentHashMap<Item, GuardTower>();
    }
}
