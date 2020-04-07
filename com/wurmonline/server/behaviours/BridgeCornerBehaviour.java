// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.highways.HighwayPos;
import com.wurmonline.server.highways.MethodsHighways;
import com.wurmonline.server.Features;
import java.util.LinkedList;
import java.util.List;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.MiscConstants;

final class BridgeCornerBehaviour extends Behaviour implements MiscConstants
{
    BridgeCornerBehaviour() {
        super((short)60);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final boolean onSurface, final BridgePart bridgePart) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        if (source.isSign() || source.isStreetLamp()) {
            toReturn.add(Actions.actionEntrys[176]);
        }
        else if (Features.Feature.HIGHWAYS.isEnabled() && source.isRoadMarker()) {
            final HighwayPos highwayPos = MethodsHighways.getHighwayPos(bridgePart);
            if (highwayPos != null && MethodsHighways.middleOfHighway(highwayPos) && !MethodsHighways.containsMarker(highwayPos, (byte)0)) {
                final byte pLinks = MethodsHighways.getPossibleLinksFrom(highwayPos, source);
                if (MethodsHighways.canPlantMarker(null, highwayPos, source, pLinks)) {
                    toReturn.add(new ActionEntry((short)176, "Plant", "planting"));
                }
                toReturn.add(new ActionEntry((short)759, "View possible protected tiles", "viewing"));
                if (pLinks != 0) {
                    toReturn.add(new ActionEntry((short)748, "View possible links", "viewing"));
                }
            }
        }
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final boolean onSurface, final BridgePart bridgePart) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final boolean onSurface, final BridgePart bridgePart, final int encodedTile, final short action, final float counter) {
        boolean done = true;
        if (action == 1) {
            done = this.action(act, performer, onSurface, bridgePart, encodedTile, action, counter);
        }
        else if (action == 176 && (source.isSign() || source.isStreetLamp())) {
            if (performer.getPower() > 0) {
                return MethodsItems.plantSignFinish(performer, source, true, bridgePart.getTileX(), bridgePart.getTileY(), performer.isOnSurface(), performer.getBridgeId(), false, -1L);
            }
            return MethodsItems.plantSign(performer, source, counter, true, bridgePart.getTileX(), bridgePart.getTileY(), performer.isOnSurface(), performer.getBridgeId(), false, -1L);
        }
        else if (action == 176 && source.isRoadMarker() && Features.Feature.HIGHWAYS.isEnabled()) {
            final HighwayPos highwayPos = MethodsHighways.getHighwayPos(bridgePart);
            if (highwayPos == null) {
                performer.getCommunicator().sendNormalServerMessage("Not a valid tile.");
                return true;
            }
            if (!MethodsHighways.middleOfHighway(highwayPos) || MethodsHighways.containsMarker(highwayPos, (byte)0)) {
                performer.getCommunicator().sendNormalServerMessage("Not a valid tile.");
                return true;
            }
            final byte pLinks = MethodsHighways.getPossibleLinksFrom(highwayPos, source);
            if (!MethodsHighways.canPlantMarker(performer, highwayPos, source, pLinks)) {
                done = true;
            }
            else if (performer.getPower() > 0) {
                done = MethodsItems.plantSignFinish(performer, source, true, highwayPos.getTilex(), highwayPos.getTiley(), highwayPos.isOnSurface(), highwayPos.getBridgeId(), false, -1L);
            }
            else {
                done = MethodsItems.plantSign(performer, source, counter, true, highwayPos.getTilex(), highwayPos.getTiley(), highwayPos.isOnSurface(), highwayPos.getBridgeId(), false, -1L);
            }
            if (done && source.isPlanted()) {
                MethodsHighways.autoLink(source, pLinks);
            }
        }
        else if (action == 748 && source.isRoadMarker() && Features.Feature.HIGHWAYS.isEnabled()) {
            final HighwayPos highwayPos = MethodsHighways.getHighwayPos(bridgePart);
            if (highwayPos == null) {
                performer.getCommunicator().sendNormalServerMessage("Not a valid tile.");
                return true;
            }
            if (!MethodsHighways.middleOfHighway(highwayPos) || MethodsHighways.containsMarker(highwayPos, (byte)0)) {
                performer.getCommunicator().sendNormalServerMessage("Not a valid tile.");
                return true;
            }
            done = MarkerBehaviour.showLinks(performer, source, act, counter, highwayPos);
        }
        else {
            if (action != 759 || !source.isRoadMarker() || !Features.Feature.HIGHWAYS.isEnabled()) {
                return this.action(act, performer, onSurface, bridgePart, encodedTile, action, counter);
            }
            final HighwayPos highwayPos = MethodsHighways.getHighwayPos(bridgePart);
            if (highwayPos == null) {
                performer.getCommunicator().sendNormalServerMessage("Not a valid tile.");
                return true;
            }
            if (!MethodsHighways.middleOfHighway(highwayPos) || MethodsHighways.containsMarker(highwayPos, (byte)0)) {
                performer.getCommunicator().sendNormalServerMessage("Not a valid tile.");
                return true;
            }
            done = MarkerBehaviour.showProtection(performer, source, act, counter, highwayPos);
        }
        return done;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final boolean onSurface, final BridgePart bridgePart, final int encodedTile, final short action, final float counter) {
        if (action == 1) {
            final HighwayPos highwayPos = MethodsHighways.getHighwayPos(bridgePart);
            if (highwayPos != null && MethodsHighways.middleOfHighway(highwayPos)) {
                performer.getCommunicator().sendNormalServerMessage("This outlines where signs and road markers could be planted.");
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("This outlines where signs can be planted.");
            }
        }
        return true;
    }
}
