// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.meshgen;

import java.util.Random;

final class PerlinNoise
{
    static float[] f_lut;
    float[][] noise;
    private float[][] noiseValues;
    private int level;
    private int width;
    private Random random;
    
    PerlinNoise(final Random aRandom, final int aLevel) {
        this.random = aRandom;
        this.width = 2 << aLevel;
        this.level = aLevel;
        if (this.width > 4096) {
            throw new IllegalArgumentException("Max size is 4096");
        }
        this.noise = new float[this.width][this.width];
        this.noiseValues = new float[this.width][this.width];
    }
    
    float[][] generatePerlinNoise(final float persistence, final int mode, final MeshGenGui.Task task, final int progressStart, final int progressRange) {
        final int highnoisesteps = 1;
        final int start = 0;
        for (int x = 0; x < this.width; ++x) {
            for (int y = 0; y < this.width; ++y) {
                this.noiseValues[x][y] = 0.0f;
            }
        }
        for (int i = 0; i < this.level + 2; ++i) {
            final int w = 1 << i;
            final float perst = (float)Math.pow(0.9990000128746033, i - 0 + 1) * persistence;
            float amplitude = (float)Math.pow(perst, i - 0 + 1);
            if (i <= 1) {
                amplitude *= i * i / 1.0f;
            }
            final NoiseMap noiseMap = new NoiseMap(this.random, w, mode);
            for (int x2 = 0; x2 < this.width; ++x2) {
                task.setNote(progressStart + (x2 + (i - 0) * this.width) / (this.level - 0 + 2));
                final int xx = x2 * w / this.width;
                final int xx2 = x2 * w % this.width * 4096 / this.width;
                for (int y2 = 0; y2 < this.width; ++y2) {
                    final int yy = y2 * w / this.width;
                    final int yy2 = y2 * w % this.width * 4096 / this.width;
                    final float[] array = this.noiseValues[x2];
                    final int n = y2;
                    array[n] += noiseMap.getInterpolatedNoise(xx, yy, xx2, yy2) * amplitude;
                }
            }
        }
        return this.noiseValues;
    }
    
    void setRandom(final Random aRandom) {
        this.random = aRandom;
    }
    
    static {
        PerlinNoise.f_lut = new float[4096];
        for (int i = 0; i < PerlinNoise.f_lut.length; ++i) {
            final double ft = i / PerlinNoise.f_lut.length * 3.141592653589793;
            PerlinNoise.f_lut[i] = (float)((1.0 - Math.cos(ft)) * 0.5);
        }
    }
    
    private final class NoiseMap
    {
        private int lWidth;
        
        private NoiseMap(final Random aRandom, final int aWidth, final int aMode) {
            this.lWidth = aWidth;
            if (aMode == 0) {
                for (int x = 0; x < aWidth; ++x) {
                    for (int y = 0; y < aWidth; ++y) {
                        if ((x == 0 || y == 0) && aMode < 3) {
                            PerlinNoise.this.noise[x][y] = 0.0f;
                        }
                        else {
                            PerlinNoise.this.noise[x][y] = aRandom.nextFloat();
                        }
                    }
                }
            }
            else {
                for (int x = 0; x < aWidth; ++x) {
                    for (int y = 0; y < aWidth; ++y) {
                        if ((x == 0 || y == 0) && aMode < 3) {
                            PerlinNoise.this.noise[x][y] = 0.0f;
                        }
                        else {
                            PerlinNoise.this.noise[x][y] = (aRandom.nextFloat() + aRandom.nextFloat()) / 2.0f;
                        }
                    }
                }
            }
        }
        
        private float getNoise(final int x, final int y) {
            return PerlinNoise.this.noise[x & this.lWidth - 1][y & this.lWidth - 1];
        }
        
        private float getInterpolatedNoise(final int x, final int y, final int xFraction, final int yFraction) {
            final float v1 = this.getNoise(x, y);
            final float v2 = this.getNoise(x + 1, y);
            final float v3 = this.getNoise(x, y + 1);
            final float v4 = this.getNoise(x + 1, y + 1);
            final float i1 = this.interpolate(v1, v2, xFraction);
            final float i2 = this.interpolate(v3, v4, xFraction);
            return this.interpolate(i1, i2, yFraction);
        }
        
        private final float interpolate(final float a, final float b, final int x) {
            final float f = PerlinNoise.f_lut[x];
            return a * (1.0f - f) + b * f;
        }
    }
}
