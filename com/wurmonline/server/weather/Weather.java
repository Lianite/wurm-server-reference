// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.weather;

import com.wurmonline.shared.constants.WeatherConstants;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.Servers;
import java.util.Calendar;
import com.wurmonline.shared.util.MovementChecker;
import java.util.Random;
import com.wurmonline.server.MiscConstants;

public final class Weather implements MiscConstants
{
    private float cloudiness;
    private float fog;
    private float rain;
    private int windChange;
    private float windAdd;
    private float fogAdd;
    private float rainAdd;
    private float fogTarget;
    private float rainTarget;
    private float cloudTarget;
    private final Random random;
    private float windDir;
    private float windRotation;
    private float windPower;
    private int rainTicks;
    private static boolean runningMain;
    
    public float getRainAdd() {
        return this.rainAdd;
    }
    
    public void setRainAdd(final float aRainAdd) {
        this.rainAdd = aRainAdd;
    }
    
    public float getRainTarget() {
        return this.rainTarget;
    }
    
    public void setRainTarget(final float aRainTarget) {
        this.rainTarget = aRainTarget;
    }
    
    public float getCloudTarget() {
        return this.cloudTarget;
    }
    
    public void setCloudTarget(final float aCloudTarget) {
        this.cloudTarget = aCloudTarget;
    }
    
    public Weather() {
        this.cloudiness = 0.0f;
        this.fog = 0.0f;
        this.rain = 0.0f;
        this.windChange = 0;
        this.windAdd = 0.0f;
        this.fogAdd = 0.0f;
        this.rainAdd = 0.0f;
        this.fogTarget = 0.0f;
        this.rainTarget = 0.0f;
        this.cloudTarget = 0.0f;
        this.random = new Random();
        this.windDir = this.random.nextFloat();
        this.windRotation = normalizeAngle(this.windDir * 360.0f);
        this.windPower = this.random.nextFloat() - 0.5f;
        this.rainTicks = 0;
    }
    
    public static final float normalizeAngle(final float angle) {
        return MovementChecker.normalizeAngle(angle);
    }
    
    public final void modifyFogTarget(final float modification) {
        this.fogTarget += modification;
    }
    
    public final void modifyRainTarget(final float modification) {
        this.rainTarget += modification;
    }
    
    public final void modifyCloudTarget(final float modification) {
        this.cloudTarget += modification;
    }
    
    public boolean tick() {
        final int day = Calendar.getInstance().get(7);
        if (!Weather.runningMain && Servers.localServer.LOGINSERVER) {
            ++this.windChange;
            if (this.windChange == 1) {
                this.windDir += (float)(this.random.nextGaussian() * this.random.nextFloat() * this.random.nextFloat() * this.random.nextFloat() * 0.10000000149011612);
                this.windRotation = normalizeAngle(this.windDir * 360.0f);
                final float p = 0.3f;
                this.windAdd += (float)(this.random.nextGaussian() * this.random.nextFloat() * this.random.nextFloat() * this.random.nextFloat() * 0.30000001192092896);
                this.windPower += this.windAdd;
                this.windAdd *= 0.94f;
                this.windPower *= 0.82f;
                if (this.windPower > 0.5f) {
                    this.windPower = 0.5f;
                }
                if (this.windPower < -0.5f) {
                    this.windPower = -0.5f;
                }
            }
            if (this.windChange > 20) {
                this.windChange = 0;
            }
        }
        this.rainAdd *= 0.9f;
        if (this.rainTicks > 15 && this.rainAdd > 0.0f) {
            this.rainAdd *= 0.5f;
        }
        this.rainAdd += (float)(this.random.nextGaussian() * this.random.nextFloat() * this.random.nextFloat() * this.random.nextFloat());
        float precipitation = 0.97f;
        if (!Weather.runningMain && WurmCalendar.isSpring()) {
            precipitation = 0.9f;
        }
        if (!Weather.runningMain && WurmCalendar.isAutumn()) {
            precipitation = 0.9f;
        }
        if (!Weather.runningMain && WurmCalendar.isSummer()) {
            precipitation = 0.99f;
        }
        this.rainTarget *= precipitation;
        this.rainTarget += this.rainAdd;
        if (this.rainTarget > this.cloudiness) {
            this.rainTarget = this.cloudiness;
        }
        if (day == 2) {
            if (this.rainTarget < -8.0f) {
                this.rainTarget = -8.0f;
            }
        }
        else if (this.rainTarget < -16.0f) {
            this.rainTarget = -16.0f;
        }
        this.fogAdd *= 0.8f;
        this.fogAdd += (float)(this.random.nextGaussian() * this.random.nextFloat() * this.random.nextFloat() * this.random.nextFloat() * 0.20000000298023224);
        this.fogTarget *= 0.9f;
        this.fogTarget += this.fogAdd;
        if (this.fogTarget > 1.0f) {
            this.fogTarget = 1.0f;
        }
        if (this.fogTarget < -16.0f) {
            this.fogTarget = -16.0f;
        }
        if (this.rainTarget > 0.0f) {
            this.fogTarget *= 1.0f - this.rainTarget;
        }
        if (this.windPower > 0.2) {
            this.fogTarget *= 1.0f - this.windPower;
        }
        float stability = 0.8f;
        if (!Weather.runningMain && WurmCalendar.isSpring()) {
            stability = 0.8f;
        }
        if (!Weather.runningMain && WurmCalendar.isAutumn()) {
            stability = 0.7f;
        }
        if (!Weather.runningMain && WurmCalendar.isSummer()) {
            stability = 0.3f;
        }
        this.rain = this.rain * stability + this.rainTarget * (1.0f - stability);
        if (this.rain < 0.0f) {
            this.rain = 0.0f;
            this.rainTicks = 0;
        }
        else if (this.rain > 0.0f) {
            ++this.rainTicks;
        }
        this.fog = this.fog * 0.9f + this.fogTarget * 0.1f;
        if (this.fog < 0.0f) {
            this.fog = 0.0f;
        }
        if (this.cloudiness < this.rain * 0.33f) {
            this.cloudiness = this.rain * 0.33f;
        }
        this.cloudTarget += (float)this.random.nextGaussian() * 0.2f * this.random.nextFloat();
        if (day == 2) {
            if (this.cloudTarget > 1.0f) {
                this.cloudTarget = 1.0f;
            }
            if (this.cloudTarget < -0.4f) {
                this.cloudTarget = -0.4f;
            }
        }
        else if (day == 6) {
            if (this.cloudTarget > 0.2f) {
                this.cloudTarget = 0.2f;
            }
            if (this.cloudTarget < -0.2f) {
                this.cloudTarget = -0.2f;
            }
        }
        else {
            if (this.cloudTarget > 1.0f) {
                this.cloudTarget = 1.0f;
            }
            if (this.cloudTarget < -0.1f) {
                this.cloudTarget = -0.1f;
            }
        }
        this.cloudiness = this.cloudiness * 0.98f + this.cloudTarget * 0.02f;
        return this.windChange == 1;
    }
    
    public int getRainTicks() {
        return this.rainTicks;
    }
    
    public void setRainTicks(final int aRainTicks) {
        this.rainTicks = aRainTicks;
    }
    
    public void setWindOnly(final float aWindRotation, final float aWindpower, final float aWindDir) {
        this.windDir = aWindDir;
        this.windPower = aWindpower;
        this.windRotation = aWindRotation;
    }
    
    public float getCloudiness() {
        return this.cloudiness;
    }
    
    public float getFog() {
        return this.fog;
    }
    
    public float getRain() {
        return this.rain;
    }
    
    public float getEvaporationRate() {
        return 0.1f;
    }
    
    public float getXWind() {
        return WeatherConstants.getWindX(this.windRotation, this.windPower);
    }
    
    public float getYWind() {
        return WeatherConstants.getWindY(this.windRotation, this.windPower);
    }
    
    public float getWindRotation() {
        return this.windRotation;
    }
    
    public float getWindDir() {
        return this.windDir;
    }
    
    public float getWindPower() {
        return this.windPower;
    }
    
    public String getWeatherString(final boolean addNumbers) {
        final StringBuilder buf = new StringBuilder();
        final float absoluteWindPower = Math.abs(this.windPower);
        if (absoluteWindPower > 0.4) {
            buf.append("A gale ");
        }
        else if (absoluteWindPower > 0.3) {
            buf.append("A strong wind ");
        }
        else if (absoluteWindPower > 0.2) {
            buf.append("A strong breeze ");
        }
        else if (absoluteWindPower > 0.1) {
            buf.append("A breeze ");
        }
        else {
            buf.append("A light breeze ");
        }
        buf.append("is coming from the ");
        byte dir = 0;
        final float degree = 22.5f;
        if (this.windRotation >= 337.5 || this.windRotation < 22.5f) {
            dir = 0;
        }
        else {
            for (int x = 0; x < 8; ++x) {
                if (this.windRotation < 22.5f + 45 * x) {
                    dir = (byte)x;
                    break;
                }
            }
        }
        if (dir == 0) {
            buf.append("north.");
        }
        else if (dir == 7) {
            buf.append("northwest.");
        }
        else if (dir == 6) {
            buf.append("west.");
        }
        else if (dir == 5) {
            buf.append("southwest.");
        }
        else if (dir == 4) {
            buf.append("south.");
        }
        else if (dir == 3) {
            buf.append("southeast.");
        }
        else if (dir == 2) {
            buf.append("east");
        }
        else if (dir == 1) {
            buf.append("northeast.");
        }
        if (addNumbers) {
            buf.append("(" + absoluteWindPower + " from " + this.windRotation + ")");
        }
        return buf.toString();
    }
    
    public static void main(final String[] args) {
        final Weather weather = new Weather();
        Weather.runningMain = true;
        int nums = 0;
        int ticksRain = 0;
        float maxRain = 0.0f;
        int ticksCloud = 0;
        int ticksSame = 0;
        int ticksAnyRain = 0;
        boolean keepGoing = true;
        final int maxTicks = 5000;
        while (keepGoing) {
            weather.tick();
            ++nums;
            if (weather.getRain() > 0.5) {
                ++ticksRain;
            }
            if (weather.getRain() > 0.0f) {
                ++ticksAnyRain;
            }
            if (weather.getRain() > maxRain) {
                maxRain = weather.getRain();
            }
            if (weather.getCloudiness() > 0.5) {
                ++ticksCloud;
            }
            if (weather.getRain() > 0.5 && weather.getCloudiness() > 0.5) {
                ++ticksSame;
            }
            if (nums > 5000) {
                keepGoing = false;
            }
        }
    }
    
    static {
        Weather.runningMain = false;
    }
}
