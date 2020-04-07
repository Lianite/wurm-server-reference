// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.kingdom;

import java.util.Iterator;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.items.Item;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class InfluenceChain
{
    protected static Logger logger;
    public static final int MAX_TOWER_CHAIN_DISTANCE = 120;
    protected static HashMap<Byte, InfluenceChain> influenceChains;
    protected ArrayList<Item> chainMarkers;
    protected int chainedMarkers;
    protected byte kingdom;
    
    public InfluenceChain(final byte kingdom) {
        this.chainMarkers = new ArrayList<Item>();
        this.chainedMarkers = 0;
        this.kingdom = kingdom;
        Village capital = Villages.getCapital(kingdom);
        if (capital != null) {
            try {
                this.chainMarkers.add(capital.getToken());
            }
            catch (NoSuchItemException e) {
                InfluenceChain.logger.warning(String.format("Influence Chain Error: No token found for village %s.", capital.getName()));
            }
        }
        else {
            for (final Village v : Villages.getVillages()) {
                if (v.kingdom == kingdom) {
                    InfluenceChain.logger.info(String.format("Because kingdom %s has no capital, the village %s has been selected as it's influence chain start.", Kingdoms.getKingdom(kingdom).getName(), v.getName()));
                    capital = v;
                    break;
                }
            }
            if (capital != null) {
                try {
                    this.chainMarkers.add(capital.getToken());
                }
                catch (NoSuchItemException e) {
                    InfluenceChain.logger.warning(String.format("Influence Chain Error: No token found for village %s.", capital.getName()));
                }
            }
            else {
                InfluenceChain.logger.warning(String.format("Influence Chain Error: There is no compatible villages for kingdom %s to start an influence chain.", Kingdoms.getKingdom(kingdom).getName()));
            }
        }
    }
    
    public ArrayList<Item> getChainMarkers() {
        return this.chainMarkers;
    }
    
    public void pulseChain(final Item marker) {
        for (final Item otherMarker : this.chainMarkers) {
            if (otherMarker.isChained()) {
                continue;
            }
            final int distX = Math.abs(marker.getTileX() - otherMarker.getTileX());
            final int distY = Math.abs(marker.getTileY() - otherMarker.getTileY());
            final int maxDist = Math.max(distX, distY);
            if (maxDist > 120) {
                continue;
            }
            otherMarker.setChained(true);
            ++this.chainedMarkers;
            this.pulseChain(otherMarker);
        }
    }
    
    public void recalculateChain() {
        for (final Item marker : this.chainMarkers) {
            marker.setChained(false);
        }
        final Item capitalToken = this.chainMarkers.get(0);
        capitalToken.setChained(true);
        this.chainedMarkers = 1;
        for (final Village v : Villages.getVillages()) {
            if (v.kingdom == this.kingdom && v.isPermanent) {
                try {
                    final Item villageToken = v.getToken();
                    villageToken.setChained(true);
                    ++this.chainedMarkers;
                    this.pulseChain(villageToken);
                }
                catch (NoSuchItemException e) {
                    InfluenceChain.logger.warning(String.format("Influence Chain Error: No token found for village %s.", v.getName()));
                }
            }
        }
        this.pulseChain(capitalToken);
    }
    
    public static InfluenceChain getInfluenceChain(final byte kingdom) {
        if (InfluenceChain.influenceChains.containsKey(kingdom)) {
            return InfluenceChain.influenceChains.get(kingdom);
        }
        final InfluenceChain newChain = new InfluenceChain(kingdom);
        InfluenceChain.influenceChains.put(kingdom, newChain);
        return newChain;
    }
    
    public void addToken(final Item token) {
        if (this.chainMarkers.contains(token)) {
            InfluenceChain.logger.info(String.format("Token at %d, %d already exists in the influence chain.", token.getTileX(), token.getTileY()));
        }
        this.chainMarkers.add(token);
        this.recalculateChain();
        InfluenceChain.logger.info(String.format("Added new village token to %s, which now has %d markers ad %d successfully linked.", Kingdoms.getKingdom(this.kingdom).getName(), this.chainMarkers.size(), this.chainedMarkers));
    }
    
    public static void addTokenToChain(final byte kingdom, final Item token) {
        final InfluenceChain kingdomChain = getInfluenceChain(kingdom);
        kingdomChain.addToken(token);
    }
    
    public void addTower(final Item tower) {
        if (this.chainMarkers.contains(tower)) {
            InfluenceChain.logger.info(String.format("Tower at %d, %d already exists in the influence chain.", tower.getTileX(), tower.getTileY()));
            return;
        }
        this.chainMarkers.add(tower);
        this.recalculateChain();
        InfluenceChain.logger.info(String.format("Added new tower to %s, which now has %d markers and %d successfully linked.", Kingdoms.getKingdom(this.kingdom).getName(), this.chainMarkers.size(), this.chainedMarkers));
    }
    
    public static void addTowerToChain(final byte kingdom, final Item tower) {
        final InfluenceChain kingdomChain = getInfluenceChain(kingdom);
        kingdomChain.addTower(tower);
    }
    
    public void removeTower(final Item tower) {
        this.chainMarkers.remove(tower);
        this.recalculateChain();
        InfluenceChain.logger.info(String.format("Removed tower from %s, which now has %d markers and %d successfully linked.", Kingdoms.getKingdom(this.kingdom).getName(), this.chainMarkers.size(), this.chainedMarkers));
    }
    
    public static void removeTowerFromChain(final byte kingdom, final Item tower) {
        final InfluenceChain kingdomChain = getInfluenceChain(kingdom);
        kingdomChain.removeTower(tower);
    }
    
    static {
        InfluenceChain.logger = Logger.getLogger(InfluenceChain.class.getName());
        InfluenceChain.influenceChains = new HashMap<Byte, InfluenceChain>();
    }
}
