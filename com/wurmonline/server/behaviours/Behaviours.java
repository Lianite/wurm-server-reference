// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.Map;
import java.util.logging.Logger;

public final class Behaviours
{
    private static final Logger logger;
    private static Behaviours instance;
    private static Map<Short, Behaviour> behaviours;
    
    public static Behaviours getInstance() {
        if (Behaviours.instance == null) {
            Behaviours.instance = new Behaviours();
        }
        return Behaviours.instance;
    }
    
    void addBehaviour(final Behaviour aBehaviour) {
        if (Behaviours.logger.isLoggable(Level.FINER)) {
            Behaviours.logger.finer("Adding Behaviour: " + aBehaviour + ", Class: " + aBehaviour.getClass());
        }
        Behaviours.behaviours.put(aBehaviour.getType(), aBehaviour);
    }
    
    public Behaviour getBehaviour(final short type) throws NoSuchBehaviourException {
        final Behaviour toReturn = Behaviours.behaviours.get(type);
        if (toReturn == null) {
            throw new NoSuchBehaviourException("No Behaviour with type " + type);
        }
        return toReturn;
    }
    
    static {
        logger = Logger.getLogger(Behaviours.class.getName());
        Behaviours.instance = null;
        Behaviours.behaviours = new HashMap<Short, Behaviour>();
        new Behaviour();
        new ItemBehaviour();
        new CreatureBehaviour();
        new TileBehaviour();
        new TileTreeBehaviour();
        new BodyPartBehaviour();
        new TileGrassBehaviour();
        new TileRockBehaviour();
        new ExamineBehaviour();
        new TileDirtBehaviour();
        new TileFieldBehaviour();
        new VegetableBehaviour();
        new FireBehaviour();
        new WallBehaviour();
        new WritBehaviour();
        new ItemPileBehaviour();
        new FenceBehaviour();
        new UnfinishedItemBehaviour();
        new VillageDeedBehaviour();
        new VillageTokenBehaviour();
        new ToyBehaviour();
        new WoundBehaviour();
        new CorpseBehaviour();
        new TraderBookBehaviour();
        new CornucopiaBehaviour();
        new PracticeDollBehaviour();
        new TileBorderBehaviour();
        new DomainItemBehaviour();
        new HugeAltarBehaviour();
        new ArtifactBehaviour();
        new PlanetBehaviour();
        new HugeLogBehaviour();
        new CaveWallBehaviour();
        new CaveTileBehaviour();
        new WarmachineBehaviour();
        new VehicleBehaviour();
        new SkillBehaviour();
        new MissionBehaviour();
        new PapyrusBehaviour();
        new StructureBehaviour();
        new FloorBehaviour();
        new ShardBehaviour();
        new FlowerpotBehaviour();
        new GravestoneBehaviour();
        new InventoryBehaviour();
        new TicketBehaviour();
        new BridgePartBehaviour();
        new OwnershipPaperBehaviour();
        new MenuRequestBehaviour();
        new TileCornerBehaviour();
        new PlanterBehaviour();
        new MarkerBehaviour();
        new AlmanacBehaviour();
        new TrellisBehaviour();
        new WagonerContractBehaviour();
        new BridgeCornerBehaviour();
        new WagonerContainerBehaviour();
    }
}
