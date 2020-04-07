// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.util;

public class EditDistance
{
    private int[] cost;
    private int[] back;
    private final String a;
    private final String b;
    
    public static int editDistance(final String a, final String b) {
        return new EditDistance(a, b).calc();
    }
    
    public static String findNearest(final String key, final String[] group) {
        int c = Integer.MAX_VALUE;
        String r = null;
        for (int i = 0; i < group.length; ++i) {
            final int ed = editDistance(key, group[i]);
            if (c > ed) {
                c = ed;
                r = group[i];
            }
        }
        return r;
    }
    
    private EditDistance(final String a, final String b) {
        this.a = a;
        this.b = b;
        this.cost = new int[a.length() + 1];
        this.back = new int[a.length() + 1];
        for (int i = 0; i <= a.length(); ++i) {
            this.cost[i] = i;
        }
    }
    
    private void flip() {
        final int[] t = this.cost;
        this.cost = this.back;
        this.back = t;
    }
    
    private int min(final int a, final int b, final int c) {
        return Math.min(a, Math.min(b, c));
    }
    
    private int calc() {
        for (int j = 0; j < this.b.length(); ++j) {
            this.flip();
            this.cost[0] = j + 1;
            for (int i = 0; i < this.a.length(); ++i) {
                final int match = (this.a.charAt(i) != this.b.charAt(j)) ? 1 : 0;
                this.cost[i + 1] = this.min(this.back[i] + match, this.cost[i] + 1, this.back[i + 1] + 1);
            }
        }
        return this.cost[this.a.length()];
    }
    
    public static void main(final String[] args) {
        System.out.println(editDistance(args[0], args[1]));
    }
}
