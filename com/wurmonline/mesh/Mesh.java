// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.mesh;

import java.util.Random;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.InputStream;

public class Mesh
{
    public static final float MAX_NOISE = 200.0f;
    private final int width;
    private final int height;
    Node[][] nodes;
    private final int meshWidth;
    private final float textureScale;
    private final int heightMinusOne;
    private final int widthMinusOne;
    private boolean wrap;
    
    public Mesh(final int width, final int height, final int meshWidth) {
        this.wrap = false;
        this.width = width;
        this.height = height;
        this.widthMinusOne = width - 1;
        this.heightMinusOne = height - 1;
        this.meshWidth = meshWidth;
        this.textureScale = 1.0f;
        this.nodes = new Node[width][height];
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < width; ++y) {
                this.nodes[x][y] = new Node();
            }
        }
    }
    
    public float getTextureScale() {
        return this.textureScale;
    }
    
    public final int getMeshWidth() {
        return this.meshWidth;
    }
    
    public void generateMesh(final InputStream in) throws IOException, ClassNotFoundException {
        final ObjectInputStream ois = new ObjectInputStream(in);
        final int[][] tileDatas = (int[][])ois.readObject();
        ois.close();
        for (int x = 0; x < this.width; ++x) {
            for (int y = 0; y < this.width; ++y) {
                (this.nodes[x][y] = new Node()).setHeight(Tiles.decodeHeightAsFloat(tileDatas[x][y]));
                this.nodes[x][y].setTexture(Tiles.decodeType(tileDatas[x][y]));
            }
        }
        this.processData();
    }
    
    public void generateEmpty(final boolean createObjects) {
        for (int x = 0; x < this.width; ++x) {
            for (int y = 0; y < this.width; ++y) {
                if (createObjects) {
                    this.nodes[x][y] = new Node();
                }
                this.nodes[x][y].setHeight(-100.0f);
                this.nodes[x][y].setTexture(Tiles.Tile.TILE_SAND.getId());
                if (createObjects) {
                    this.nodes[x][y].setNormals(new float[3]);
                }
                this.nodes[x][y].normals[0] = 0.0f;
                this.nodes[x][y].normals[1] = 1.0f;
                this.nodes[x][y].normals[2] = 0.0f;
            }
        }
        this.processData();
    }
    
    public void processData() {
        this.processData(0, 0, this.width, this.height);
    }
    
    public void processData(final int x1, final int y1, final int x2, final int y2) {
        for (int x3 = x1; x3 < x2; ++x3) {
            for (int y3 = y1; y3 < y2; ++y3) {
                final Node node = this.getNode(x3, y3);
                float b = node.getHeight();
                float t = node.getHeight();
                if (this.getNode(x3 + 1, y3).getHeight() < b) {
                    b = this.getNode(x3 + 1, y3).getHeight();
                }
                if (this.getNode(x3 + 1, y3).getHeight() > t) {
                    t = this.getNode(x3 + 1, y3).getHeight();
                }
                if (this.getNode(x3 + 1, y3 + 1).getHeight() < b) {
                    b = this.getNode(x3 + 1, y3 + 1).getHeight();
                }
                if (this.getNode(x3 + 1, y3 + 1).getHeight() > t) {
                    t = this.getNode(x3 + 1, y3 + 1).getHeight();
                }
                if (this.getNode(x3, y3 + 1).getHeight() < b) {
                    b = this.getNode(x3, y3 + 1).getHeight();
                }
                if (this.getNode(x3, y3 + 1).getHeight() > t) {
                    t = this.getNode(x3, y3 + 1).getHeight();
                }
                final float h = t - b;
                node.setBbBottom(b);
                node.setBbHeight(h);
            }
        }
    }
    
    public void calculateNormals() {
        this.calculateNormals(0, 0, this.width, this.height);
    }
    
    public void calculateNormals(final int x1, final int y1, final int x2, final int y2) {
        for (int x3 = x1; x3 < x2; ++x3) {
            for (int y3 = y1; y3 < y2; ++y3) {
                final Node n1 = this.getNode(x3, y3);
                float v1x = this.getMeshWidth();
                float v1y = this.getNode(x3 + 1, y3).getHeight() - n1.getHeight();
                float v1z = 0.0f;
                float v2x = 0.0f;
                float v2y = this.getNode(x3, y3 + 1).getHeight() - n1.getHeight();
                float v2z = this.getMeshWidth();
                float vx = v1y * v2z - v1z * v2y;
                float vy = v1z * v2x - v1x * v2z;
                float vz = v1x * v2y - v1y * v2x;
                v1x = -this.getMeshWidth();
                v1y = this.getNode(x3 - 1, y3).getHeight() - n1.getHeight();
                v1z = 0.0f;
                v2x = 0.0f;
                v2y = this.getNode(x3, y3 - 1).getHeight() - n1.getHeight();
                v2z = -this.getMeshWidth();
                vx += v1y * v2z - v1z * v2y;
                vy += v1z * v2x - v1x * v2z;
                vz += v1x * v2y - v1y * v2x;
                final float dist = (float)Math.sqrt(vx * vx + vy * vy + vz * vz);
                vx /= dist;
                vy /= dist;
                vz /= dist;
                n1.normals[0] = -vx;
                n1.normals[1] = -vy;
                n1.normals[2] = -vz;
            }
        }
    }
    
    public FloatBuffer createFloatBuffer(final int size) {
        final ByteBuffer temp = ByteBuffer.allocateDirect(4 * size);
        temp.order(ByteOrder.nativeOrder());
        return temp.asFloatBuffer();
    }
    
    public void setWraparound() {
        this.wrap = true;
    }
    
    public Node getNode(final int x, final int y) {
        if (!this.wrap && (x < 0 || y < 0 || x >= this.width || y >= this.height)) {
            return this.nodes[0][0];
        }
        return this.nodes[x & this.widthMinusOne][y & this.heightMinusOne];
    }
    
    public void generateHills() {
        final float[][] heights = new float[this.width][this.height];
        for (int x = 0; x < this.width; ++x) {
            for (int y = 0; y < this.height; ++y) {
                final Node node = this.nodes[x][y];
                heights[x][y] = node.getHeight();
                final boolean lower = heights[x][y] < -64.0f;
                if (heights[x][y] < 0.0f) {
                    heights[x][y] *= 0.1f;
                }
                if (lower) {
                    heights[x][y] = heights[x][y] * 2.0f - 3.0f;
                }
            }
        }
        for (int x = 0; x < this.width; ++x) {
            for (int y = 0; y < this.height; ++y) {
                this.nodes[x][y].setHeight(heights[x][y]);
            }
        }
    }
    
    public void smooth(final float bias) {
        final float[][] heights = new float[this.width][this.height];
        for (int x = 0; x < this.width; ++x) {
            for (int y = 0; y < this.height; ++y) {
                final Node node = this.nodes[x][y];
                float surroundingHeight = 0.0f;
                surroundingHeight += this.getNode(x - 1, y).getHeight();
                surroundingHeight += this.getNode(x + 1, y).getHeight();
                surroundingHeight += this.getNode(x, y - 1).getHeight();
                surroundingHeight += this.getNode(x, y + 1).getHeight();
                heights[x][y] = node.getHeight() * bias + surroundingHeight / 4.0f * (1.0f - bias);
            }
        }
        for (int x = 0; x < this.width; ++x) {
            for (int y = 0; y < this.height; ++y) {
                this.nodes[x][y].setHeight(heights[x][y]);
            }
        }
    }
    
    public void noise(final Random random, final float noiseLevel) {
        for (int x = 0; x < this.width; ++x) {
            for (int y = 0; y < this.height; ++y) {
                this.nodes[x][y].setHeight(this.nodes[x][y].getHeight() + (random.nextFloat() - 0.5f) * noiseLevel);
            }
        }
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public boolean isTransition(float xp, float yp) {
        while (xp < 0.0f) {
            xp += this.getWidth() * this.getMeshWidth();
        }
        while (yp < 0.0f) {
            yp += this.getHeight() * this.getMeshWidth();
        }
        final int xx = (int)(xp / this.getMeshWidth());
        final int yy = (int)(yp / this.getMeshWidth());
        return this.getNode(xx, yy).getTexture() == Tiles.Tile.TILE_HOLE.getId();
    }
    
    public float getHeight(float xp, float yp) {
        while (xp < 0.0f) {
            xp += this.getWidth() * this.getMeshWidth();
        }
        while (yp < 0.0f) {
            yp += this.getHeight() * this.getMeshWidth();
        }
        final int xx = (int)(xp / this.getMeshWidth());
        final int yy = (int)(yp / this.getMeshWidth());
        if (this.getNode(xx, yy).getTexture() == Tiles.Tile.TILE_HOLE.getId()) {
            return 1.0f;
        }
        float xa = xp / this.getMeshWidth() - xx;
        float ya = yp / this.getMeshWidth() - yy;
        if (xa < 0.0f) {
            xa = -xa;
        }
        if (ya < 0.0f) {
            ya = -ya;
        }
        float height = 0.0f;
        if (xa > ya) {
            xa -= ya;
            xa /= 1.0f - ya;
            final float xheight1 = this.getNode(xx, yy).getHeight() * (1.0f - xa) + this.getNode(xx + 1, yy).getHeight() * xa;
            final float xheight2 = this.getNode(xx + 1, yy + 1).getHeight();
            height = xheight1 * (1.0f - ya) + xheight2 * ya;
        }
        else {
            if (ya <= 0.001f) {
                ya = 0.001f;
            }
            xa /= ya;
            final float xheight1 = this.getNode(xx, yy).getHeight();
            final float xheight2 = this.getNode(xx, yy + 1).getHeight() * (1.0f - xa) + this.getNode(xx + 1, yy + 1).getHeight() * xa;
            height = xheight1 * (1.0f - ya) + xheight2 * ya;
        }
        return height;
    }
    
    public boolean setTiles(final int xStart, final int yStart, final int w, final int h, final int[][] tiles) {
        boolean changed = false;
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                final Node node = this.getNode(x + xStart, y + yStart);
                node.setHeight(Tiles.decodeHeightAsFloat(tiles[x][y]));
                final short t = Tiles.decodeType(tiles[x][y]);
                final short d = Tiles.decodeData(tiles[x][y]);
                if (t != node.getTexture() || d != node.data) {
                    node.setTexture((byte)t);
                    node.setData((byte)d);
                    changed = true;
                }
            }
        }
        return changed;
    }
    
    public void refresh(final int xStart, final int yStart, final int w, final int h) {
        this.calculateNormals(xStart - 1, yStart - 1, xStart + w + 2, yStart + h + 2);
        this.processData(xStart - 1, yStart - 1, xStart + w + 2, yStart + h + 2);
    }
    
    public void reset() {
        this.generateEmpty(false);
    }
}
