// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.Servers;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.NoSuchActionException;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.shared.constants.SoundNames;

public final class MusicPlayer implements SoundNames, MiscConstants
{
    private int currentTile;
    private int tileType;
    private int lasttileType;
    private int numSameTiles;
    private int secondsSinceLastCheck;
    private int nextSongAvail;
    private String lastSong;
    private int tilesOnWater;
    private final Player listener;
    private static final Logger logger;
    
    MusicPlayer(final Player _listener) {
        this.currentTile = 0;
        this.tileType = 0;
        this.lasttileType = 0;
        this.numSameTiles = 0;
        this.secondsSinceLastCheck = 0;
        this.nextSongAvail = 200;
        this.lastSong = "";
        this.tilesOnWater = 0;
        this.listener = _listener;
    }
    
    public boolean isItOkToPlaySong(final boolean checkRand) {
        if (this.secondsSinceLastCheck > this.nextSongAvail) {
            this.secondsSinceLastCheck = 0;
            this.nextSongAvail = 180;
            if (!checkRand) {
                return true;
            }
            if (Server.rand.nextInt(2) == 0) {
                return true;
            }
        }
        return false;
    }
    
    public final boolean checkMUSIC_BLACKLIGHT_SND() {
        this.playSong("sound.music.song.blacklight", 30);
        return true;
    }
    
    private final boolean checkMUSIC_CAVEHALL1_SND() {
        if (Server.rand.nextInt(10) == 0 && this.listener.getLayer() < 0) {
            final int tx = this.listener.getTileX();
            final int ty = this.listener.getTileY();
            for (int x = -2; x <= 2; ++x) {
                for (int y = -2; y <= 2; ++y) {
                    final int c = Server.caveMesh.getTile(tx + x, ty + y);
                    if (Tiles.decodeType(c) != Tiles.Tile.TILE_CAVE.id) {
                        return false;
                    }
                }
            }
            this.playSong("sound.music.song.cavehall1", 60);
            return true;
        }
        return false;
    }
    
    private final boolean checkMUSIC_CAVEHALL2_SND() {
        if (Server.rand.nextInt(10) == 0 && this.listener.getLayer() < 0) {
            final int tx = this.listener.getTileX();
            final int ty = this.listener.getTileY();
            for (int x = -1; x <= 1; ++x) {
                for (int y = -1; y <= 1; ++y) {
                    final int c = Server.caveMesh.getTile(tx + x, ty + y);
                    if (Tiles.decodeType(c) != Tiles.Tile.TILE_CAVE.id) {
                        return false;
                    }
                }
            }
            this.playSong("sound.music.song.cavehall2", 60);
            return true;
        }
        return false;
    }
    
    public final boolean checkMUSIC_COLOSSUS_SND() {
        this.playSong("sound.music.song.colossus", 60);
        return true;
    }
    
    public final boolean checkMUSIC_DISBAND_SND() {
        this.playSong("sound.music.song.disbandvillage", 60);
        return true;
    }
    
    public final boolean checkMUSIC_DYING1_SND() {
        if (this.isItOkToPlaySong(false)) {
            final int r = Server.rand.nextInt(3);
            if (r == 0) {
                this.playSong("sound.music.song.dying1", 30);
                return true;
            }
            if (r == 1) {
                this.playSong("sound.music.song.dying2", 30);
                return true;
            }
        }
        return false;
    }
    
    public final boolean checkMUSIC_FOUNDSETTLEMENT_SND() {
        final int tx = this.listener.getTileX();
        final int ty = this.listener.getTileY();
        for (int x = -20; x <= 20; ++x) {
            for (int y = -20; y <= 20; ++y) {
                final VolaTile t = Zones.getTileOrNull(tx + x, ty + y, true);
                if (t != null) {
                    final Creature[] crets = t.getCreatures();
                    for (int c = 0; c < crets.length; ++c) {
                        if (crets[c].getMusicPlayer() != null) {
                            crets[c].getMusicPlayer().playSong("sound.music.song.foundsettlement");
                        }
                    }
                }
            }
        }
        return true;
    }
    
    private final boolean checkMUSIC_MOUNTAINTOP_SND() {
        if (Server.rand.nextInt(5) == 0 && this.listener.getLayer() >= 0) {
            final float height = Tiles.decodeHeightAsFloat(this.currentTile);
            if (height > 100.0f) {
                final int tx = this.listener.getTileX();
                final int ty = this.listener.getTileY();
                for (int x = -1; x <= 1; ++x) {
                    for (int y = -1; y <= 1; ++y) {
                        final int c = Server.surfaceMesh.getTile(tx + x, ty + y);
                        if (Tiles.decodeHeight(c) > height) {
                            return false;
                        }
                    }
                }
                if (this.listener.getKingdomTemplateId() == 3) {
                    this.playSongEvil("sound.music.song.mountaintop", 60);
                }
                else {
                    this.playSong("sound.music.song.mountaintop");
                }
                return true;
            }
        }
        return false;
    }
    
    public final boolean playPrayer() {
        if (this.listener.getDeity() != null) {
            if (this.listener.getDeity().number == 1) {
                this.playSong("sound.music.song.prayingfo");
            }
            if (this.listener.getDeity().number == 3) {
                this.playSong("sound.music.song.prayingvynora");
            }
            if (this.listener.getDeity().number == 2) {
                this.playSong("sound.music.song.prayingmagranon");
            }
            if (this.listener.getDeity().number == 4) {
                this.playSong("sound.music.song.prayinglibila");
            }
            return true;
        }
        return false;
    }
    
    public final boolean checkMUSIC_TERRITORYHOTS_SND() {
        if (Server.rand.nextInt(10) == 0) {
            this.playSong("sound.music.song.territoryhots", 60);
            return true;
        }
        return false;
    }
    
    public final boolean checkMUSIC_TERRITORYWL_SND() {
        if (Server.rand.nextInt(10) == 0) {
            if (this.listener.getKingdomTemplateId() == 3) {
                this.playSongEvil("sound.music.song.territorywl", 60);
            }
            else {
                this.playSong("sound.music.song.territorywl", 60);
            }
            return true;
        }
        return false;
    }
    
    public final boolean checkMUSIC_WHITELIGHT_SND() {
        this.playSong("sound.music.song.whitelight", 60);
        return true;
    }
    
    public final boolean checkMUSIC_UNLIMITED_SND() {
        this.playSong("sound.music.song.unlimited", 600);
        return true;
    }
    
    private final boolean checkMUSIC_VILLAGERAIN_SND() {
        if (this.listener.getCurrentVillage() != null && this.listener.getCitizenVillage() == this.listener.getCurrentVillage() && Server.rand.nextInt(2) == 0 && this.isItOkToPlaySong(false) && Server.getWeather().getRain() > 0.4f) {
            if (this.listener.getKingdomTemplateId() == 3) {
                this.playSongEvil("sound.music.song.villagerain");
            }
            else {
                this.playSong("sound.music.song.villagerain");
            }
            return true;
        }
        return false;
    }
    
    private final boolean checkMUSIC_VILLAGESUN_SND() {
        if (this.listener.getCurrentVillage() != null && this.listener.getCitizenVillage() == this.listener.getCurrentVillage() && Server.rand.nextInt(2) == 0 && this.isItOkToPlaySong(false) && Server.getWeather().getCloudiness() < 0.5f) {
            if (this.listener.getKingdomTemplateId() == 3) {
                this.playSongEvil("sound.music.song.villagesun");
            }
            else {
                this.playSong("sound.music.song.villagesun");
            }
            return true;
        }
        return false;
    }
    
    private final boolean checkMUSIC_SUNRISEPASS_SND() {
        if (Server.rand.nextInt(2) == 0 && this.isItOkToPlaySong(false) && Server.getWeather().getCloudiness() < 0.5f && this.listener.getStatus().getRotation() < 165.0f && this.listener.getStatus().getRotation() > 15.0f && WurmCalendar.isMorning()) {
            if (this.listener.getKingdomTemplateId() == 3) {
                this.playSongEvil("sound.music.song.sunrisepass");
            }
            else {
                this.playSong("sound.music.song.sunrisepass");
            }
            return true;
        }
        return false;
    }
    
    private final boolean checkMUSIC_SUNRISE1_SND() {
        if (Server.rand.nextInt(2) == 0 && this.isItOkToPlaySong(false) && Server.getWeather().getCloudiness() < 0.5f && this.listener.getStatus().getRotation() < 165.0f && this.listener.getStatus().getRotation() > 15.0f && WurmCalendar.isMorning()) {
            if (this.listener.getKingdomTemplateId() == 3) {
                this.playSongEvil("sound.music.song.sunrise1");
            }
            else {
                this.playSong("sound.music.song.sunrise1");
            }
            return true;
        }
        return false;
    }
    
    public final boolean checkMUSIC_VILLAGEWORK_SND() {
        return false;
    }
    
    private final boolean checkMUSIC_WURMISWAITING_SND() {
        if (Server.rand.nextInt(100) == 0 && this.isItOkToPlaySong(false)) {
            this.playSong("sound.music.song.wurmiswaiting");
            return true;
        }
        return false;
    }
    
    public final boolean checkMUSIC_ANTHEMHOTS_SND() {
        if (this.isItOkToPlaySong(false)) {
            this.playSong("sound.music.song.anthemhots");
        }
        return true;
    }
    
    public final boolean checkMUSIC_ANTHEMMOLREHAN_SND() {
        if (this.isItOkToPlaySong(false)) {
            this.playSong("sound.music.song.anthemmolrehan");
        }
        return true;
    }
    
    public final boolean checkMUSIC_ANTHEMJENN_SND() {
        if (this.isItOkToPlaySong(false)) {
            this.playSong("sound.music.song.anthemjenn");
        }
        return true;
    }
    
    private boolean checkTravelling() {
        if (this.numSameTiles > 100) {
            this.numSameTiles = 0;
            final int song = Server.rand.nextInt(6);
            if (song == 0) {
                return this.playSong("sound.music.song.travelling1");
            }
            if (song == 1) {
                return this.playSong("sound.music.song.travelling2");
            }
            if (song == 2) {
                return this.playSong("sound.music.song.travelling3");
            }
        }
        return false;
    }
    
    private final boolean checkBattleAdventure(final boolean mix) {
        if (this.listener.currentKingdom == this.listener.getKingdomId() && !mix && this.listener.getEnemyPresense() <= 0) {
            return false;
        }
        if (!mix && Server.rand.nextInt(10) == 0) {
            return this.checkTravelExploration(true);
        }
        final int num = Server.rand.nextInt(mix ? 20 : 8);
        if (num == 0) {
            return this.playSong("sound.music.song.abandon", 400);
        }
        if (num == 1) {
            return this.playSong("sound.music.song.backhome", 400);
        }
        if (num == 2) {
            return this.playSong("sound.music.song.deadwater", 400);
        }
        if (num == 3) {
            return this.playSong("sound.music.song.contact", 400);
        }
        if (num == 4) {
            return this.playSong("sound.music.song.sunglow", 400);
        }
        return num == 5 && this.playSong("sound.music.song.dancehorde", 400);
    }
    
    private final boolean checkTravelExploration(final boolean mix) {
        if (this.listener.getCurrentVillage() != null && !mix) {
            return false;
        }
        if (!mix && Server.rand.nextInt(10) == 0) {
            return this.checkVillageMeditation(true);
        }
        if (this.listener.getVehicle() != -10L) {
            if (this.listener.getMovementScheme().getMountSpeed() > 0.1f && this.listener.isMoving()) {
                final int num = Server.rand.nextInt(6);
                if (num == 0) {
                    return this.playSong("sound.music.song.north", 400);
                }
                if (num == 1) {
                    return this.playSong("sound.music.song.stride", 400);
                }
                if (num == 2) {
                    return this.playSong("sound.music.song.shores", 400);
                }
                return num == 3 && this.listener.getCurrentKingdom() == this.listener.getKingdomId() && this.playSong("sound.music.song.familiar", 400);
            }
            else {
                final int num = Server.rand.nextInt(6);
                if (num == 0) {
                    return this.playSong("sound.music.song.north", 400);
                }
                if (num == 1) {
                    return this.playSong("sound.music.song.ridge", 400);
                }
                if (num == 2) {
                    return this.playSong("sound.music.song.through", 400);
                }
                return num == 3 && this.playSong("sound.music.song.skyfire", 400);
            }
        }
        else {
            final int num = Server.rand.nextInt(8);
            if (num == 0) {
                return this.playSong("sound.music.song.ridge", 400);
            }
            if (num == 1) {
                return this.playSong("sound.music.song.skyfire", 400);
            }
            if (num == 2) {
                return this.playSong("sound.music.song.shores", 400);
            }
            if (num == 3) {
                return this.playSong("sound.music.song.through", 400);
            }
            return num == 4 && this.listener.getCurrentKingdom() == this.listener.getKingdomId() && this.playSong("sound.music.song.familiar", 400);
        }
    }
    
    private boolean checkVillageMeditation(final boolean mix) {
        if (!mix && Server.rand.nextInt(10) == 0) {
            return this.checkTravelExploration(true);
        }
        int num = 20;
        boolean meditating = false;
        try {
            final Action act = this.listener.getCurrentAction();
            if (act.getNumber() == 384) {
                meditating = true;
            }
        }
        catch (NoSuchActionException ex) {}
        if (this.listener.getCurrentVillage() != null || mix) {
            if (meditating) {
                num = 18;
            }
            if (this.listener.getCurrentVillage() == this.listener.getCitizenVillage() || this.listener.getCitizenVillage() == null) {
                num /= 2;
            }
            final int song = Server.rand.nextInt(num);
            if (song == 0) {
                return this.playSong("sound.music.song.wakingup", 420);
            }
            if (song == 1) {
                return this.playSong("sound.music.song.fingerfo", 400);
            }
            if (song == 2 && WurmCalendar.isNight()) {
                return this.playSong("sound.music.song.inyoureyes", 400);
            }
            if (song == 3) {
                return this.playSong("sound.music.song.beatinganvil", 400);
            }
            if (song == 4) {
                return this.playSong("sound.music.song.promisingfoal", 400);
            }
            if (song == 5) {
                return this.playSong("sound.music.song.longsummer", 420);
            }
            if (song == 6) {
                return this.playSong("sound.music.song.whyyoudive", 400);
            }
        }
        return false;
    }
    
    private boolean checkEchoes() {
        final int song = Server.rand.nextInt(10);
        if (song == 0) {
            return this.playSong("sound.music.song.echoes1", 120);
        }
        if (song == 1) {
            return this.playSong("sound.music.song.echoes2", 120);
        }
        if (song == 2 && WurmCalendar.isNight()) {
            return this.playSong("sound.music.song.echoes3", 120);
        }
        if (song == 3) {
            return this.playSong("sound.music.song.echoes4", 120);
        }
        if (song == 4) {
            return this.playSong("sound.music.song.echoes5", 120);
        }
        return song == 5 && this.playSong("sound.music.song.echoes6", 120);
    }
    
    public void moveTile(final int _tileNum, final boolean onWater) {
        this.currentTile = _tileNum;
        final int _tileType = Tiles.decodeType(this.currentTile);
        if (this.tileType == _tileType || this.lasttileType == _tileType) {
            ++this.numSameTiles;
        }
        else {
            this.lasttileType = this.tileType;
            this.tileType = _tileType;
            this.numSameTiles = 0;
        }
        if (onWater) {
            ++this.tilesOnWater;
        }
        if (this.isItOkToPlaySong(true)) {
            if (this.listener.getLayer() >= 0) {
                if (!this.checkMUSIC_MOUNTAINTOP_SND() && !this.checkTravelling() && this.tilesOnWater > 100) {
                    final int song = Server.rand.nextInt(4);
                    if (song == 0) {
                        this.playSong("sound.music.song.shanty1");
                    }
                    this.tilesOnWater = 0;
                }
            }
            else if (!this.checkMUSIC_CAVEHALL1_SND() && !this.checkMUSIC_CAVEHALL2_SND() && !this.checkEchoes()) {
                this.checkTravelling();
            }
        }
    }
    
    void tickSecond() {
        ++this.secondsSinceLastCheck;
        if (this.isItOkToPlaySong(true) && !this.checkBattleAdventure(!Servers.localServer.PVPSERVER) && !this.checkTravelExploration(false) && !this.checkVillageMeditation(false) && !this.checkEchoes()) {
            if (!this.checkMUSIC_SUNRISE1_SND() && !this.checkMUSIC_SUNRISEPASS_SND() && !this.checkMUSIC_VILLAGESUN_SND()) {
                this.checkMUSIC_VILLAGERAIN_SND();
            }
            this.checkMUSIC_WURMISWAITING_SND();
        }
    }
    
    private boolean playSong(final String song) {
        return this.playSong(song, 180);
    }
    
    private boolean playSongEvil(final String song) {
        return this.playSongEvil(song, 180);
    }
    
    private boolean playSong(final String song, final int _nextSongAvail) {
        if (!song.equals(this.lastSong)) {
            this.nextSongAvail = _nextSongAvail;
            SoundPlayer.playSong(song, this.listener);
            this.lastSong = song;
            return true;
        }
        return false;
    }
    
    private boolean playSongEvil(final String song, final int _nextSongAvail) {
        if (!song.equals(this.lastSong)) {
            this.nextSongAvail = _nextSongAvail;
            SoundPlayer.playSong(song, this.listener, 1.0f);
            this.lastSong = song;
            return true;
        }
        return false;
    }
    
    static {
        logger = Logger.getLogger(MusicPlayer.class.getName());
    }
}
