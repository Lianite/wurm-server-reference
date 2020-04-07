// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.mesh;

public final class CaveMesh extends Mesh
{
    public CaveMesh(final Mesh mesh, final int width, final int height) {
        super(width, height, mesh.getMeshWidth());
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                (this.nodes[x][y] = new CaveNode()).setTexture(CaveTile.TILE_ROCK.id);
                ((CaveNode)this.nodes[x][y]).setCeilingTexture(CaveTile.TILE_ROCK.id);
                this.nodes[x][y].setHeight((float)(Math.random() * Math.random() * Math.random()) * 100.0f + 0.2f);
                this.nodes[x][y].setNormals(new float[3]);
            }
        }
        this.processData();
        this.calculateNormals();
    }
    
    public CaveNode getCaveNode(final int x, final int y) {
        return (CaveNode)this.getNode(x, y);
    }
    
    public CaveMesh(final int width, final int height, final int meshWidth) {
        super(width, height, meshWidth);
        this.setWraparound();
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < width; ++y) {
                (this.nodes[x][y] = new CaveNode()).setTexture(CaveTile.TILE_ROCK.id);
                this.getCaveNode(x, y).setCeilingTexture(CaveTile.TILE_ROCK.id);
                this.getCaveNode(x, y).setHeight(Float.MAX_VALUE);
                this.getCaveNode(x, y).setData(0.0f);
                this.getCaveNode(x, y).setSpecial(1);
            }
        }
        this.processData();
        this.calculateNormals();
    }
    
    @Override
    public boolean isTransition(float xp, float yp) {
        while (xp < 0.0f) {
            xp += this.getWidth() * this.getMeshWidth();
        }
        while (yp < 0.0f) {
            yp += this.getHeight() * this.getMeshWidth();
        }
        final int xx = (int)(xp / this.getMeshWidth());
        final int yy = (int)(yp / this.getMeshWidth());
        return this.getCaveNode(xx, yy).getSpecial() != 0;
    }
    
    @Override
    public void processData(final int x1, final int y1, final int x2, final int y2) {
        for (int x3 = x1; x3 < x2; ++x3) {
            for (int y3 = y1; y3 < y2; ++y3) {
                final Node node = this.getNode(x3, y3);
                float b = node.getHeight();
                float t = node.getHeight() + this.getCaveNode(x3, y3).getData();
                if (this.getNode(x3 + 1, y3).getHeight() < b) {
                    b = this.getNode(x3 + 1, y3).getHeight();
                }
                if (this.getNode(x3 + 1, y3).getHeight() + this.getCaveNode(x3 + 1, y3).getData() > t) {
                    t = this.getNode(x3 + 1, y3).getHeight() + this.getCaveNode(x3 + 1, y3).getData();
                }
                if (this.getNode(x3 + 1, y3 + 1).getHeight() < b) {
                    b = this.getNode(x3 + 1, y3 + 1).getHeight();
                }
                if (this.getNode(x3 + 1, y3 + 1).getHeight() + this.getCaveNode(x3 + 1, y3 + 1).getData() > t) {
                    t = this.getNode(x3 + 1, y3 + 1).getHeight() + this.getCaveNode(x3 + 1, y3 + 1).getData();
                }
                if (this.getNode(x3, y3 + 1).getHeight() < b) {
                    b = this.getNode(x3, y3 + 1).getHeight();
                }
                if (this.getNode(x3, y3 + 1).getHeight() + this.getCaveNode(x3, y3 + 1).getData() > t) {
                    t = this.getNode(x3, y3 + 1).getHeight() + this.getCaveNode(x3, y3 + 1).getData();
                }
                final float h = t - b;
                node.setBbBottom(b);
                node.setBbHeight(h);
            }
        }
    }
    
    @Override
    public void calculateNormals(final int x1, final int y1, final int x2, final int y2) {
        for (int x3 = x1; x3 < x2; ++x3) {
            for (int y3 = y1; y3 < y2; ++y3) {
                final CaveNode n1 = this.getCaveNode(x3, y3);
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
    
    @Override
    public boolean setTiles(final int xStart, final int yStart, final int w, final int h, final int[][] tiles) {
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                final CaveNode node = this.getCaveNode(x + xStart, y + yStart);
                node.setHeight(CaveTile.decodeHeightAsFloat(tiles[x][y]));
                node.setTexture(CaveTile.decodeFloorTexture(tiles[x][y]));
                node.setCeilingTexture(CaveTile.decodeCeilingTexture(tiles[x][y]));
                node.setData(CaveTile.decodeCeilingHeightAsFloat(tiles[x][y]));
                node.setSpecial(0);
                if (node.getData() < 0.0f) {
                    node.setData(-node.getData());
                    node.setSpecial(1);
                }
            }
        }
        return false;
    }
}
