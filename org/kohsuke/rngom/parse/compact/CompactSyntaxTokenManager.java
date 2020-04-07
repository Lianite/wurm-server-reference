// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse.compact;

import java.io.IOException;
import java.io.PrintStream;

public class CompactSyntaxTokenManager implements CompactSyntaxConstants
{
    public PrintStream debugStream;
    static final long[] jjbitVec0;
    static final long[] jjbitVec2;
    static final long[] jjbitVec3;
    static final long[] jjbitVec4;
    static final long[] jjbitVec5;
    static final long[] jjbitVec6;
    static final long[] jjbitVec7;
    static final long[] jjbitVec8;
    static final long[] jjbitVec9;
    static final long[] jjbitVec10;
    static final long[] jjbitVec11;
    static final long[] jjbitVec12;
    static final long[] jjbitVec13;
    static final long[] jjbitVec14;
    static final long[] jjbitVec15;
    static final long[] jjbitVec16;
    static final long[] jjbitVec17;
    static final long[] jjbitVec18;
    static final long[] jjbitVec19;
    static final long[] jjbitVec20;
    static final long[] jjbitVec21;
    static final long[] jjbitVec22;
    static final long[] jjbitVec23;
    static final long[] jjbitVec24;
    static final long[] jjbitVec25;
    static final long[] jjbitVec26;
    static final long[] jjbitVec27;
    static final long[] jjbitVec28;
    static final long[] jjbitVec29;
    static final long[] jjbitVec30;
    static final long[] jjbitVec31;
    static final long[] jjbitVec32;
    static final long[] jjbitVec33;
    static final long[] jjbitVec34;
    static final long[] jjbitVec35;
    static final long[] jjbitVec36;
    static final long[] jjbitVec37;
    static final long[] jjbitVec38;
    static final long[] jjbitVec39;
    static final long[] jjbitVec40;
    static final long[] jjbitVec41;
    static final int[] jjnextStates;
    public static final String[] jjstrLiteralImages;
    public static final String[] lexStateNames;
    public static final int[] jjnewLexState;
    static final long[] jjtoToken;
    static final long[] jjtoSkip;
    static final long[] jjtoSpecial;
    protected JavaCharStream input_stream;
    private final int[] jjrounds;
    private final int[] jjstateSet;
    StringBuffer image;
    int jjimageLen;
    int lengthOfMatch;
    protected char curChar;
    int curLexState;
    int defaultLexState;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;
    
    public void setDebugStream(final PrintStream ds) {
        this.debugStream = ds;
    }
    
    private final int jjStopStringLiteralDfa_0(final int pos, final long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x1F8C0FE4E0L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    return 43;
                }
                if ((active0 & 0x800000000000000L) != 0x0L) {
                    this.jjmatchedKind = 60;
                    return -1;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x1F8C0FE4E0L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    this.jjmatchedPos = 1;
                    return 43;
                }
                if ((active0 & 0x800000000000000L) != 0x0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 60;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x1F8C0FE4A0L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    this.jjmatchedPos = 2;
                    return 43;
                }
                if ((active0 & 0x40L) != 0x0L) {
                    return 43;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x1F0C0BE4A0L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    this.jjmatchedPos = 3;
                    return 43;
                }
                if ((active0 & 0x80040000L) != 0x0L) {
                    return 43;
                }
                return -1;
            }
            case 4: {
                if ((active0 & 0xE0C09E480L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    this.jjmatchedPos = 4;
                    return 43;
                }
                if ((active0 & 0x1100020020L) != 0x0L) {
                    return 43;
                }
                return -1;
            }
            case 5: {
                if ((active0 & 0x20C09E480L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    this.jjmatchedPos = 5;
                    return 43;
                }
                if ((active0 & 0xC00000000L) != 0x0L) {
                    return 43;
                }
                return -1;
            }
            case 6: {
                if ((active0 & 0x208092000L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    this.jjmatchedPos = 6;
                    return 43;
                }
                if ((active0 & 0x400C480L) != 0x0L) {
                    return 43;
                }
                return -1;
            }
            case 7: {
                if ((active0 & 0x8092000L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    this.jjmatchedPos = 7;
                    return 43;
                }
                if ((active0 & 0x200000000L) != 0x0L) {
                    return 43;
                }
                return -1;
            }
            case 8: {
                if ((active0 & 0x80000L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    this.jjmatchedPos = 8;
                    return 43;
                }
                if ((active0 & 0x8012000L) != 0x0L) {
                    return 43;
                }
                return -1;
            }
            default: {
                return -1;
            }
        }
    }
    
    private final int jjStartNfa_0(final int pos, final long active0) {
        return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(pos, active0), pos + 1);
    }
    
    private final int jjStopAtPos(final int pos, final int kind) {
        this.jjmatchedKind = kind;
        return (this.jjmatchedPos = pos) + 1;
    }
    
    private final int jjStartNfaWithStates_0(final int pos, final int kind, final int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_0(state, pos + 1);
    }
    
    private final int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case '&': {
                this.jjmatchedKind = 21;
                return this.jjMoveStringLiteralDfa1_0(8L);
            }
            case '(': {
                return this.jjStopAtPos(0, 28);
            }
            case ')': {
                return this.jjStopAtPos(0, 29);
            }
            case '*': {
                return this.jjStopAtPos(0, 25);
            }
            case '+': {
                return this.jjStopAtPos(0, 23);
            }
            case ',': {
                return this.jjStopAtPos(0, 22);
            }
            case '-': {
                return this.jjStopAtPos(0, 30);
            }
            case '=': {
                return this.jjStopAtPos(0, 2);
            }
            case '>': {
                return this.jjMoveStringLiteralDfa1_0(576460752303423488L);
            }
            case '?': {
                return this.jjStopAtPos(0, 24);
            }
            case '[': {
                return this.jjStopAtPos(0, 1);
            }
            case ']': {
                return this.jjStopAtPos(0, 9);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa1_0(134217728L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa1_0(81984L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa1_0(8657174528L);
            }
            case 'g': {
                return this.jjMoveStringLiteralDfa1_0(1024L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa1_0(32896L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa1_0(2147483648L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa1_0(4294967296L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa1_0(532480L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa1_0(17179869184L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa1_0(34359738400L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa1_0(68719738880L);
            }
            case '{': {
                return this.jjStopAtPos(0, 11);
            }
            case '|': {
                this.jjmatchedKind = 20;
                return this.jjMoveStringLiteralDfa1_0(16L);
            }
            case '}': {
                return this.jjStopAtPos(0, 12);
            }
            case '~': {
                return this.jjStopAtPos(0, 8);
            }
            default: {
                return this.jjMoveNfa_0(3, 0);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa1_0(final long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '=': {
                if ((active0 & 0x8L) != 0x0L) {
                    return this.jjStopAtPos(1, 3);
                }
                if ((active0 & 0x10L) != 0x0L) {
                    return this.jjStopAtPos(1, 4);
                }
                break;
            }
            case '>': {
                if ((active0 & 0x800000000000000L) != 0x0L) {
                    return this.jjStopAtPos(1, 59);
                }
                break;
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa2_0(active0, 17179942912L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa2_0(active0, 278528L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa2_0(active0, 6442451008L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa2_0(active0, 67108864L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa2_0(active0, 131072L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa2_0(active0, 32896L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa2_0(active0, 68720001024L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa2_0(active0, 1024L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa2_0(active0, 34493956128L);
            }
            case 'x': {
                return this.jjMoveStringLiteralDfa2_0(active0, 8589934592L);
            }
        }
        return this.jjStartNfa_0(0, active0);
    }
    
    private final int jjMoveStringLiteralDfa2_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(1, active0);
            return 2;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa3_0(active0, 1056L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa3_0(active0, 128L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa3_0(active0, 67108864L);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa3_0(active0, 16384L);
            }
            case 'h': {
                return this.jjMoveStringLiteralDfa3_0(active0, 32768L);
            }
            case 'k': {
                return this.jjMoveStringLiteralDfa3_0(active0, 68719476736L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa3_0(active0, 8192L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa3_0(active0, 131072L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa3_0(active0, 51539607552L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa3_0(active0, 2147483648L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa3_0(active0, 8724742144L);
            }
            case 'v': {
                if ((active0 & 0x40L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 6, 43);
                }
                break;
            }
            case 'x': {
                return this.jjMoveStringLiteralDfa3_0(active0, 4295229440L);
            }
        }
        return this.jjStartNfa_0(1, active0);
    }
    
    private final int jjMoveStringLiteralDfa3_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(1, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(2, active0);
            return 3;
        }
        switch (this.curChar) {
            case 'A': {
                return this.jjMoveStringLiteralDfa4_0(active0, 524288L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa4_0(active0, 81920L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa4_0(active0, 98784288768L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa4_0(active0, 34359738368L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa4_0(active0, 128L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa4_0(active0, 67109888L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa4_0(active0, 134217760L);
            }
            case 't': {
                if ((active0 & 0x40000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 18, 43);
                }
                if ((active0 & 0x80000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 31, 43);
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 131072L);
            }
            default: {
                return this.jjStartNfa_0(2, active0);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa4_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(2, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(3, active0);
            return 4;
        }
        switch (this.curChar) {
            case 'd': {
                if ((active0 & 0x100000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 32, 43);
                }
                break;
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa5_0(active0, 67108864L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa5_0(active0, 134217728L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa5_0(active0, 524288L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa5_0(active0, 1024L);
            }
            case 'n': {
                if ((active0 & 0x1000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 36, 43);
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 51539607552L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa5_0(active0, 8589967360L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa5_0(active0, 8192L);
            }
            case 't': {
                if ((active0 & 0x20L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 5, 43);
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 65536L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa5_0(active0, 16512L);
            }
            case 'y': {
                if ((active0 & 0x20000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 17, 43);
                }
                break;
            }
        }
        return this.jjStartNfa_0(3, active0);
    }
    
    private final int jjMoveStringLiteralDfa5_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(3, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(4, active0);
            return 5;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa6_0(active0, 1024L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa6_0(active0, 134217728L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa6_0(active0, 128L);
            }
            case 'g': {
                if ((active0 & 0x800000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 35, 43);
                }
                break;
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa6_0(active0, 32768L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa6_0(active0, 540672L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa6_0(active0, 8657043456L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa6_0(active0, 8192L);
            }
            case 't': {
                if ((active0 & 0x400000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 34, 43);
                }
                break;
            }
            case 'y': {
                return this.jjMoveStringLiteralDfa6_0(active0, 65536L);
            }
        }
        return this.jjStartNfa_0(4, active0);
    }
    
    private final int jjMoveStringLiteralDfa6_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(4, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(5, active0);
            return 6;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa7_0(active0, 8589942784L);
            }
            case 'e': {
                if ((active0 & 0x80L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 7, 43);
                }
                break;
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa7_0(active0, 524288L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa7_0(active0, 65536L);
            }
            case 'r': {
                if ((active0 & 0x400L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 10, 43);
                }
                break;
            }
            case 't': {
                if ((active0 & 0x4000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 14, 43);
                }
                if ((active0 & 0x8000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 15, 43);
                }
                if ((active0 & 0x4000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 26, 43);
                }
                break;
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa7_0(active0, 134217728L);
            }
        }
        return this.jjStartNfa_0(5, active0);
    }
    
    private final int jjMoveStringLiteralDfa7_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(5, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(6, active0);
            return 7;
        }
        switch (this.curChar) {
            case 'c': {
                return this.jjMoveStringLiteralDfa8_0(active0, 8192L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa8_0(active0, 65536L);
            }
            case 'l': {
                if ((active0 & 0x200000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 33, 43);
                }
                break;
            }
            case 't': {
                return this.jjMoveStringLiteralDfa8_0(active0, 134217728L);
            }
            case 'w': {
                return this.jjMoveStringLiteralDfa8_0(active0, 524288L);
            }
        }
        return this.jjStartNfa_0(6, active0);
    }
    
    private final int jjMoveStringLiteralDfa8_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(6, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(7, active0);
            return 8;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x2000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 13, 43);
                }
                if ((active0 & 0x8000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 27, 43);
                }
                return this.jjMoveStringLiteralDfa9_0(active0, 524288L);
            }
            case 's': {
                if ((active0 & 0x10000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 16, 43);
                }
                break;
            }
        }
        return this.jjStartNfa_0(7, active0);
    }
    
    private final int jjMoveStringLiteralDfa9_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(7, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(8, active0);
            return 9;
        }
        switch (this.curChar) {
            case 'd': {
                if ((active0 & 0x80000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 19, 43);
                }
                break;
            }
        }
        return this.jjStartNfa_0(8, active0);
    }
    
    private final void jjCheckNAdd(final int state) {
        if (this.jjrounds[state] != this.jjround) {
            this.jjstateSet[this.jjnewStateCnt++] = state;
            this.jjrounds[state] = this.jjround;
        }
    }
    
    private final void jjAddStates(int start, final int end) {
        do {
            this.jjstateSet[this.jjnewStateCnt++] = CompactSyntaxTokenManager.jjnextStates[start];
        } while (start++ != end);
    }
    
    private final void jjCheckNAddTwoStates(final int state1, final int state2) {
        this.jjCheckNAdd(state1);
        this.jjCheckNAdd(state2);
    }
    
    private final void jjCheckNAddStates(int start, final int end) {
        do {
            this.jjCheckNAdd(CompactSyntaxTokenManager.jjnextStates[start]);
        } while (start++ != end);
    }
    
    private final void jjCheckNAddStates(final int start) {
        this.jjCheckNAdd(CompactSyntaxTokenManager.jjnextStates[start]);
        this.jjCheckNAdd(CompactSyntaxTokenManager.jjnextStates[start + 1]);
    }
    
    private final int jjMoveNfa_0(final int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 43;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long l = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if ((0xFFFFFFFFFFFFF9FFL & l) != 0x0L && kind > 60) {
                                kind = 60;
                            }
                            if ((0x100000601L & l) != 0x0L) {
                                if (kind > 39) {
                                    kind = 39;
                                }
                                this.jjCheckNAdd(0);
                            }
                            else if (this.curChar == '\'') {
                                this.jjstateSet[this.jjnewStateCnt++] = 31;
                            }
                            else if (this.curChar == '\"') {
                                this.jjstateSet[this.jjnewStateCnt++] = 22;
                            }
                            else if (this.curChar == '#') {
                                if (kind > 42) {
                                    kind = 42;
                                }
                                this.jjCheckNAdd(5);
                            }
                            if (this.curChar == '\'') {
                                this.jjCheckNAddTwoStates(13, 14);
                                continue;
                            }
                            if (this.curChar == '\"') {
                                this.jjCheckNAddTwoStates(10, 11);
                                continue;
                            }
                            if (this.curChar == '#') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                continue;
                            }
                            continue;
                        }
                        case 43: {
                            if ((0x3FF600000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(39, 40);
                            }
                            else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 41;
                            }
                            if ((0x3FF600000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(36, 38);
                            }
                            else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 37;
                            }
                            if ((0x3FF600000000000L & l) != 0x0L) {
                                if (kind > 54) {
                                    kind = 54;
                                }
                                this.jjCheckNAdd(35);
                                continue;
                            }
                            continue;
                        }
                        case 0: {
                            if ((0x100000601L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 39) {
                                kind = 39;
                            }
                            this.jjCheckNAdd(0);
                            continue;
                        }
                        case 1: {
                            if (this.curChar != '#') {
                                continue;
                            }
                            if (kind > 40) {
                                kind = 40;
                            }
                            this.jjCheckNAdd(2);
                            continue;
                        }
                        case 2: {
                            if ((0xFFFFFFFFFFFFFBFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 40) {
                                kind = 40;
                            }
                            this.jjCheckNAdd(2);
                            continue;
                        }
                        case 4: {
                            if (this.curChar != '#') {
                                continue;
                            }
                            if (kind > 42) {
                                kind = 42;
                            }
                            this.jjCheckNAdd(5);
                            continue;
                        }
                        case 5: {
                            if ((0xFFFFFFFFFFFFFBFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 42) {
                                kind = 42;
                            }
                            this.jjCheckNAdd(5);
                            continue;
                        }
                        case 8: {
                            if ((0x3FF600000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 55) {
                                kind = 55;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            continue;
                        }
                        case 9: {
                            if (this.curChar == '\"') {
                                this.jjCheckNAddTwoStates(10, 11);
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            if ((0xFFFFFFFBFFFFFFFEL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(10, 11);
                                continue;
                            }
                            continue;
                        }
                        case 11:
                        case 20: {
                            if (this.curChar == '\"' && kind > 58) {
                                kind = 58;
                                continue;
                            }
                            continue;
                        }
                        case 12: {
                            if (this.curChar == '\'') {
                                this.jjCheckNAddTwoStates(13, 14);
                                continue;
                            }
                            continue;
                        }
                        case 13: {
                            if ((0xFFFFFF7FFFFFFFFEL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(13, 14);
                                continue;
                            }
                            continue;
                        }
                        case 14:
                        case 29: {
                            if (this.curChar == '\'' && kind > 58) {
                                kind = 58;
                                continue;
                            }
                            continue;
                        }
                        case 15: {
                            if (this.curChar == '\"') {
                                this.jjCheckNAddStates(0, 2);
                                continue;
                            }
                            continue;
                        }
                        case 16: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(0, 2);
                                continue;
                            }
                            continue;
                        }
                        case 17:
                        case 19: {
                            if (this.curChar == '\"') {
                                this.jjCheckNAdd(16);
                                continue;
                            }
                            continue;
                        }
                        case 18: {
                            if (this.curChar == '\"') {
                                this.jjAddStates(3, 4);
                                continue;
                            }
                            continue;
                        }
                        case 21: {
                            if (this.curChar == '\"') {
                                this.jjstateSet[this.jjnewStateCnt++] = 20;
                                continue;
                            }
                            continue;
                        }
                        case 22: {
                            if (this.curChar == '\"') {
                                this.jjstateSet[this.jjnewStateCnt++] = 15;
                                continue;
                            }
                            continue;
                        }
                        case 23: {
                            if (this.curChar == '\"') {
                                this.jjstateSet[this.jjnewStateCnt++] = 22;
                                continue;
                            }
                            continue;
                        }
                        case 24: {
                            if (this.curChar == '\'') {
                                this.jjCheckNAddStates(5, 7);
                                continue;
                            }
                            continue;
                        }
                        case 25: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(5, 7);
                                continue;
                            }
                            continue;
                        }
                        case 26:
                        case 28: {
                            if (this.curChar == '\'') {
                                this.jjCheckNAdd(25);
                                continue;
                            }
                            continue;
                        }
                        case 27: {
                            if (this.curChar == '\'') {
                                this.jjAddStates(8, 9);
                                continue;
                            }
                            continue;
                        }
                        case 30: {
                            if (this.curChar == '\'') {
                                this.jjstateSet[this.jjnewStateCnt++] = 29;
                                continue;
                            }
                            continue;
                        }
                        case 31: {
                            if (this.curChar == '\'') {
                                this.jjstateSet[this.jjnewStateCnt++] = 24;
                                continue;
                            }
                            continue;
                        }
                        case 32: {
                            if (this.curChar == '\'') {
                                this.jjstateSet[this.jjnewStateCnt++] = 31;
                                continue;
                            }
                            continue;
                        }
                        case 33: {
                            if ((0xFFFFFFFFFFFFF9FFL & l) != 0x0L && kind > 60) {
                                kind = 60;
                                continue;
                            }
                            continue;
                        }
                        case 35: {
                            if ((0x3FF600000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 54) {
                                kind = 54;
                            }
                            this.jjCheckNAdd(35);
                            continue;
                        }
                        case 36: {
                            if ((0x3FF600000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(36, 38);
                                continue;
                            }
                            continue;
                        }
                        case 37: {
                            if (this.curChar == '*' && kind > 56) {
                                kind = 56;
                                continue;
                            }
                            continue;
                        }
                        case 38: {
                            if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 37;
                                continue;
                            }
                            continue;
                        }
                        case 39: {
                            if ((0x3FF600000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(39, 40);
                                continue;
                            }
                            continue;
                        }
                        case 40: {
                            if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 41;
                                continue;
                            }
                            continue;
                        }
                        case 42: {
                            if ((0x3FF600000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 42;
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != startsAt);
            }
            else if (this.curChar < '\u0080') {
                final long l = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if (kind > 60) {
                                kind = 60;
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 54) {
                                    kind = 54;
                                }
                                this.jjCheckNAddStates(10, 14);
                                continue;
                            }
                            if (this.curChar == '\\') {
                                this.jjstateSet[this.jjnewStateCnt++] = 7;
                                continue;
                            }
                            continue;
                        }
                        case 43: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(39, 40);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(36, 38);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 54) {
                                    kind = 54;
                                }
                                this.jjCheckNAdd(35);
                                continue;
                            }
                            continue;
                        }
                        case 6: {
                            if (this.curChar == '\\') {
                                this.jjstateSet[this.jjnewStateCnt++] = 7;
                                continue;
                            }
                            continue;
                        }
                        case 7:
                        case 8: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 55) {
                                kind = 55;
                            }
                            this.jjCheckNAdd(8);
                            continue;
                        }
                        case 33: {
                            if (kind > 60) {
                                kind = 60;
                                continue;
                            }
                            continue;
                        }
                        case 34: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 54) {
                                kind = 54;
                            }
                            this.jjCheckNAddStates(10, 14);
                            continue;
                        }
                        case 35: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 54) {
                                kind = 54;
                            }
                            this.jjCheckNAdd(35);
                            continue;
                        }
                        case 36: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(36, 38);
                                continue;
                            }
                            continue;
                        }
                        case 39: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(39, 40);
                                continue;
                            }
                            continue;
                        }
                        case 41:
                        case 42: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(42);
                            continue;
                        }
                        default: {
                            continue;
                        }
                        case 2: {
                            if (kind > 40) {
                                kind = 40;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            continue;
                        }
                        case 5: {
                            if (kind > 42) {
                                kind = 42;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            continue;
                        }
                        case 10: {
                            this.jjAddStates(15, 16);
                            continue;
                        }
                        case 13: {
                            this.jjAddStates(17, 18);
                            continue;
                        }
                        case 16: {
                            this.jjAddStates(0, 2);
                            continue;
                        }
                        case 25: {
                            this.jjAddStates(5, 7);
                            continue;
                        }
                    }
                } while (i != startsAt);
            }
            else {
                final int hiByte = this.curChar >> 8;
                final int i2 = hiByte >> 6;
                final long l2 = 1L << (hiByte & 0x3F);
                final int i3 = (this.curChar & '\u00ff') >> 6;
                final long l3 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3) && kind > 60) {
                                kind = 60;
                            }
                            if (jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                if (kind > 54) {
                                    kind = 54;
                                }
                                this.jjCheckNAddStates(10, 14);
                                continue;
                            }
                            continue;
                        }
                        case 43: {
                            if (jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                if (kind > 54) {
                                    kind = 54;
                                }
                                this.jjCheckNAdd(35);
                            }
                            if (jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(36, 38);
                            }
                            if (jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(39, 40);
                                continue;
                            }
                            continue;
                        }
                        case 2: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 40) {
                                kind = 40;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            continue;
                        }
                        case 5: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 42) {
                                kind = 42;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            continue;
                        }
                        case 7: {
                            if (!jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 55) {
                                kind = 55;
                            }
                            this.jjCheckNAdd(8);
                            continue;
                        }
                        case 8: {
                            if (!jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 55) {
                                kind = 55;
                            }
                            this.jjCheckNAdd(8);
                            continue;
                        }
                        case 10: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjAddStates(15, 16);
                                continue;
                            }
                            continue;
                        }
                        case 13: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjAddStates(17, 18);
                                continue;
                            }
                            continue;
                        }
                        case 16: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjAddStates(0, 2);
                                continue;
                            }
                            continue;
                        }
                        case 25: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjAddStates(5, 7);
                                continue;
                            }
                            continue;
                        }
                        case 33: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3) && kind > 60) {
                                kind = 60;
                                continue;
                            }
                            continue;
                        }
                        case 34: {
                            if (!jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 54) {
                                kind = 54;
                            }
                            this.jjCheckNAddStates(10, 14);
                            continue;
                        }
                        case 35: {
                            if (!jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 54) {
                                kind = 54;
                            }
                            this.jjCheckNAdd(35);
                            continue;
                        }
                        case 36: {
                            if (jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(36, 38);
                                continue;
                            }
                            continue;
                        }
                        case 39: {
                            if (jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(39, 40);
                                continue;
                            }
                            continue;
                        }
                        case 41: {
                            if (!jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(42);
                            continue;
                        }
                        case 42: {
                            if (!jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(42);
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != startsAt);
            }
            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }
            ++curPos;
            final int n = i = this.jjnewStateCnt;
            final int n2 = 43;
            final int jjnewStateCnt = startsAt;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n == (startsAt = n2 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException e) {
                return curPos;
            }
        }
        return curPos;
    }
    
    private final int jjMoveStringLiteralDfa0_1() {
        return this.jjMoveNfa_1(1, 0);
    }
    
    private final int jjMoveNfa_1(final int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 10;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long l = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if ((0xFFFFFFFFFFFFF9FFL & l) != 0x0L && kind > 60) {
                                kind = 60;
                            }
                            if ((0x100000601L & l) != 0x0L) {
                                if (kind > 39) {
                                    kind = 39;
                                }
                                this.jjCheckNAdd(0);
                            }
                            if ((0x401L & l) != 0x0L) {
                                this.jjCheckNAddStates(19, 22);
                                continue;
                            }
                            continue;
                        }
                        case 0: {
                            if ((0x100000601L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 39) {
                                kind = 39;
                            }
                            this.jjCheckNAdd(0);
                            continue;
                        }
                        case 2: {
                            if ((0x401L & l) != 0x0L) {
                                this.jjCheckNAddStates(19, 22);
                                continue;
                            }
                            continue;
                        }
                        case 3: {
                            if ((0x100000200L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(3, 6);
                                continue;
                            }
                            continue;
                        }
                        case 4: {
                            if (this.curChar != '#') {
                                continue;
                            }
                            if (kind > 43) {
                                kind = 43;
                            }
                            this.jjCheckNAdd(5);
                            continue;
                        }
                        case 5: {
                            if ((0xFFFFFFFFFFFFFBFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 43) {
                                kind = 43;
                            }
                            this.jjCheckNAdd(5);
                            continue;
                        }
                        case 6: {
                            if (this.curChar == '#') {
                                this.jjstateSet[this.jjnewStateCnt++] = 4;
                                continue;
                            }
                            continue;
                        }
                        case 7: {
                            if ((0x100000200L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(7, 8);
                                continue;
                            }
                            continue;
                        }
                        case 8: {
                            if (this.curChar != '#') {
                                continue;
                            }
                            if (kind > 44) {
                                kind = 44;
                            }
                            this.jjCheckNAdd(9);
                            continue;
                        }
                        case 9: {
                            if ((0xFFFFFFFFFFFFFBFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 44) {
                                kind = 44;
                            }
                            this.jjCheckNAdd(9);
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != startsAt);
            }
            else if (this.curChar < '\u0080') {
                final long l = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (kind > 60) {
                                kind = 60;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                        case 5: {
                            if (kind > 43) {
                                kind = 43;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            continue;
                        }
                        case 9: {
                            if (kind > 44) {
                                kind = 44;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 9;
                            continue;
                        }
                    }
                } while (i != startsAt);
            }
            else {
                final int hiByte = this.curChar >> 8;
                final int i2 = hiByte >> 6;
                final long l2 = 1L << (hiByte & 0x3F);
                final int i3 = (this.curChar & '\u00ff') >> 6;
                final long l3 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3) && kind > 60) {
                                kind = 60;
                                continue;
                            }
                            continue;
                        }
                        case 5: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 43) {
                                kind = 43;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            continue;
                        }
                        case 9: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 44) {
                                kind = 44;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 9;
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != startsAt);
            }
            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }
            ++curPos;
            final int n = i = this.jjnewStateCnt;
            final int n2 = 10;
            final int jjnewStateCnt = startsAt;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n == (startsAt = n2 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException e) {
                return curPos;
            }
        }
        return curPos;
    }
    
    private final int jjMoveStringLiteralDfa0_2() {
        return this.jjMoveNfa_2(1, 0);
    }
    
    private final int jjMoveNfa_2(final int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 7;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long l = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if ((0xFFFFFFFFFFFFF9FFL & l) != 0x0L && kind > 60) {
                                kind = 60;
                            }
                            if ((0x100000601L & l) != 0x0L) {
                                if (kind > 39) {
                                    kind = 39;
                                }
                                this.jjCheckNAdd(0);
                            }
                            if ((0x401L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(2, 5);
                                continue;
                            }
                            continue;
                        }
                        case 0: {
                            if ((0x100000601L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 39) {
                                kind = 39;
                            }
                            this.jjCheckNAdd(0);
                            continue;
                        }
                        case 2: {
                            if ((0x100000200L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(2, 5);
                                continue;
                            }
                            continue;
                        }
                        case 3: {
                            if (this.curChar != '#') {
                                continue;
                            }
                            if (kind > 41) {
                                kind = 41;
                            }
                            this.jjCheckNAdd(4);
                            continue;
                        }
                        case 4: {
                            if ((0xFFFFFFFFFFFFFBFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 41) {
                                kind = 41;
                            }
                            this.jjCheckNAdd(4);
                            continue;
                        }
                        case 5: {
                            if (this.curChar == '#') {
                                this.jjstateSet[this.jjnewStateCnt++] = 3;
                                continue;
                            }
                            continue;
                        }
                        case 6: {
                            if ((0xFFFFFFFFFFFFF9FFL & l) != 0x0L && kind > 60) {
                                kind = 60;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != startsAt);
            }
            else if (this.curChar < '\u0080') {
                final long l = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (kind > 60) {
                                kind = 60;
                                continue;
                            }
                            continue;
                        }
                        case 4: {
                            if (kind > 41) {
                                kind = 41;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != startsAt);
            }
            else {
                final int hiByte = this.curChar >> 8;
                final int i2 = hiByte >> 6;
                final long l2 = 1L << (hiByte & 0x3F);
                final int i3 = (this.curChar & '\u00ff') >> 6;
                final long l3 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3) && kind > 60) {
                                kind = 60;
                                continue;
                            }
                            continue;
                        }
                        case 4: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 41) {
                                kind = 41;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != startsAt);
            }
            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }
            ++curPos;
            final int n = i = this.jjnewStateCnt;
            final int n2 = 7;
            final int jjnewStateCnt = startsAt;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n == (startsAt = n2 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException e) {
                return curPos;
            }
        }
        return curPos;
    }
    
    private static final boolean jjCanMove_0(final int hiByte, final int i1, final int i2, final long l1, final long l2) {
        switch (hiByte) {
            case 0: {
                return (CompactSyntaxTokenManager.jjbitVec2[i2] & l2) != 0x0L;
            }
            default: {
                return (CompactSyntaxTokenManager.jjbitVec0[i1] & l1) != 0x0L;
            }
        }
    }
    
    private static final boolean jjCanMove_1(final int hiByte, final int i1, final int i2, final long l1, final long l2) {
        switch (hiByte) {
            case 0: {
                return (CompactSyntaxTokenManager.jjbitVec4[i2] & l2) != 0x0L;
            }
            case 1: {
                return (CompactSyntaxTokenManager.jjbitVec5[i2] & l2) != 0x0L;
            }
            case 2: {
                return (CompactSyntaxTokenManager.jjbitVec6[i2] & l2) != 0x0L;
            }
            case 3: {
                return (CompactSyntaxTokenManager.jjbitVec7[i2] & l2) != 0x0L;
            }
            case 4: {
                return (CompactSyntaxTokenManager.jjbitVec8[i2] & l2) != 0x0L;
            }
            case 5: {
                return (CompactSyntaxTokenManager.jjbitVec9[i2] & l2) != 0x0L;
            }
            case 6: {
                return (CompactSyntaxTokenManager.jjbitVec10[i2] & l2) != 0x0L;
            }
            case 9: {
                return (CompactSyntaxTokenManager.jjbitVec11[i2] & l2) != 0x0L;
            }
            case 10: {
                return (CompactSyntaxTokenManager.jjbitVec12[i2] & l2) != 0x0L;
            }
            case 11: {
                return (CompactSyntaxTokenManager.jjbitVec13[i2] & l2) != 0x0L;
            }
            case 12: {
                return (CompactSyntaxTokenManager.jjbitVec14[i2] & l2) != 0x0L;
            }
            case 13: {
                return (CompactSyntaxTokenManager.jjbitVec15[i2] & l2) != 0x0L;
            }
            case 14: {
                return (CompactSyntaxTokenManager.jjbitVec16[i2] & l2) != 0x0L;
            }
            case 15: {
                return (CompactSyntaxTokenManager.jjbitVec17[i2] & l2) != 0x0L;
            }
            case 16: {
                return (CompactSyntaxTokenManager.jjbitVec18[i2] & l2) != 0x0L;
            }
            case 17: {
                return (CompactSyntaxTokenManager.jjbitVec19[i2] & l2) != 0x0L;
            }
            case 30: {
                return (CompactSyntaxTokenManager.jjbitVec20[i2] & l2) != 0x0L;
            }
            case 31: {
                return (CompactSyntaxTokenManager.jjbitVec21[i2] & l2) != 0x0L;
            }
            case 33: {
                return (CompactSyntaxTokenManager.jjbitVec22[i2] & l2) != 0x0L;
            }
            case 48: {
                return (CompactSyntaxTokenManager.jjbitVec23[i2] & l2) != 0x0L;
            }
            case 49: {
                return (CompactSyntaxTokenManager.jjbitVec24[i2] & l2) != 0x0L;
            }
            case 159: {
                return (CompactSyntaxTokenManager.jjbitVec25[i2] & l2) != 0x0L;
            }
            case 215: {
                return (CompactSyntaxTokenManager.jjbitVec26[i2] & l2) != 0x0L;
            }
            default: {
                return (CompactSyntaxTokenManager.jjbitVec3[i1] & l1) != 0x0L;
            }
        }
    }
    
    private static final boolean jjCanMove_2(final int hiByte, final int i1, final int i2, final long l1, final long l2) {
        switch (hiByte) {
            case 0: {
                return (CompactSyntaxTokenManager.jjbitVec27[i2] & l2) != 0x0L;
            }
            case 1: {
                return (CompactSyntaxTokenManager.jjbitVec5[i2] & l2) != 0x0L;
            }
            case 2: {
                return (CompactSyntaxTokenManager.jjbitVec28[i2] & l2) != 0x0L;
            }
            case 3: {
                return (CompactSyntaxTokenManager.jjbitVec29[i2] & l2) != 0x0L;
            }
            case 4: {
                return (CompactSyntaxTokenManager.jjbitVec30[i2] & l2) != 0x0L;
            }
            case 5: {
                return (CompactSyntaxTokenManager.jjbitVec31[i2] & l2) != 0x0L;
            }
            case 6: {
                return (CompactSyntaxTokenManager.jjbitVec32[i2] & l2) != 0x0L;
            }
            case 9: {
                return (CompactSyntaxTokenManager.jjbitVec33[i2] & l2) != 0x0L;
            }
            case 10: {
                return (CompactSyntaxTokenManager.jjbitVec34[i2] & l2) != 0x0L;
            }
            case 11: {
                return (CompactSyntaxTokenManager.jjbitVec35[i2] & l2) != 0x0L;
            }
            case 12: {
                return (CompactSyntaxTokenManager.jjbitVec36[i2] & l2) != 0x0L;
            }
            case 13: {
                return (CompactSyntaxTokenManager.jjbitVec37[i2] & l2) != 0x0L;
            }
            case 14: {
                return (CompactSyntaxTokenManager.jjbitVec38[i2] & l2) != 0x0L;
            }
            case 15: {
                return (CompactSyntaxTokenManager.jjbitVec39[i2] & l2) != 0x0L;
            }
            case 16: {
                return (CompactSyntaxTokenManager.jjbitVec18[i2] & l2) != 0x0L;
            }
            case 17: {
                return (CompactSyntaxTokenManager.jjbitVec19[i2] & l2) != 0x0L;
            }
            case 30: {
                return (CompactSyntaxTokenManager.jjbitVec20[i2] & l2) != 0x0L;
            }
            case 31: {
                return (CompactSyntaxTokenManager.jjbitVec21[i2] & l2) != 0x0L;
            }
            case 32: {
                return (CompactSyntaxTokenManager.jjbitVec40[i2] & l2) != 0x0L;
            }
            case 33: {
                return (CompactSyntaxTokenManager.jjbitVec22[i2] & l2) != 0x0L;
            }
            case 48: {
                return (CompactSyntaxTokenManager.jjbitVec41[i2] & l2) != 0x0L;
            }
            case 49: {
                return (CompactSyntaxTokenManager.jjbitVec24[i2] & l2) != 0x0L;
            }
            case 159: {
                return (CompactSyntaxTokenManager.jjbitVec25[i2] & l2) != 0x0L;
            }
            case 215: {
                return (CompactSyntaxTokenManager.jjbitVec26[i2] & l2) != 0x0L;
            }
            default: {
                return (CompactSyntaxTokenManager.jjbitVec3[i1] & l1) != 0x0L;
            }
        }
    }
    
    public CompactSyntaxTokenManager(final JavaCharStream stream) {
        this.debugStream = System.out;
        this.jjrounds = new int[43];
        this.jjstateSet = new int[86];
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.input_stream = stream;
    }
    
    public CompactSyntaxTokenManager(final JavaCharStream stream, final int lexState) {
        this(stream);
        this.SwitchTo(lexState);
    }
    
    public void ReInit(final JavaCharStream stream) {
        final boolean b = false;
        this.jjnewStateCnt = (b ? 1 : 0);
        this.jjmatchedPos = (b ? 1 : 0);
        this.curLexState = this.defaultLexState;
        this.input_stream = stream;
        this.ReInitRounds();
    }
    
    private final void ReInitRounds() {
        this.jjround = -2147483647;
        int i = 43;
        while (i-- > 0) {
            this.jjrounds[i] = Integer.MIN_VALUE;
        }
    }
    
    public void ReInit(final JavaCharStream stream, final int lexState) {
        this.ReInit(stream);
        this.SwitchTo(lexState);
    }
    
    public void SwitchTo(final int lexState) {
        if (lexState >= 3 || lexState < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
        }
        this.curLexState = lexState;
    }
    
    protected Token jjFillToken() {
        final Token t = Token.newToken(this.jjmatchedKind);
        t.kind = this.jjmatchedKind;
        final String im = CompactSyntaxTokenManager.jjstrLiteralImages[this.jjmatchedKind];
        t.image = ((im == null) ? this.input_stream.GetImage() : im);
        t.beginLine = this.input_stream.getBeginLine();
        t.beginColumn = this.input_stream.getBeginColumn();
        t.endLine = this.input_stream.getEndLine();
        t.endColumn = this.input_stream.getEndColumn();
        return t;
    }
    
    public Token getNextToken() {
        Token specialToken = null;
        int curPos = 0;
        while (true) {
            try {
                this.curChar = this.input_stream.BeginToken();
            }
            catch (IOException e) {
                this.jjmatchedKind = 0;
                final Token matchedToken = this.jjFillToken();
                matchedToken.specialToken = specialToken;
                return matchedToken;
            }
            this.image = null;
            this.jjimageLen = 0;
            switch (this.curLexState) {
                case 0: {
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_0();
                    break;
                }
                case 1: {
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_1();
                    break;
                }
                case 2: {
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_2();
                    break;
                }
            }
            if (this.jjmatchedKind == Integer.MAX_VALUE) {
                int error_line = this.input_stream.getEndLine();
                int error_column = this.input_stream.getEndColumn();
                String error_after = null;
                boolean EOFSeen = false;
                try {
                    this.input_stream.readChar();
                    this.input_stream.backup(1);
                }
                catch (IOException e2) {
                    EOFSeen = true;
                    error_after = ((curPos <= 1) ? "" : this.input_stream.GetImage());
                    if (this.curChar == '\n' || this.curChar == '\r') {
                        ++error_line;
                        error_column = 0;
                    }
                    else {
                        ++error_column;
                    }
                }
                if (!EOFSeen) {
                    this.input_stream.backup(1);
                    error_after = ((curPos <= 1) ? "" : this.input_stream.GetImage());
                }
                throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after, this.curChar, 0);
            }
            if (this.jjmatchedPos + 1 < curPos) {
                this.input_stream.backup(curPos - this.jjmatchedPos - 1);
            }
            if ((CompactSyntaxTokenManager.jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0x0L) {
                final Token matchedToken = this.jjFillToken();
                matchedToken.specialToken = specialToken;
                if (CompactSyntaxTokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                    this.curLexState = CompactSyntaxTokenManager.jjnewLexState[this.jjmatchedKind];
                }
                return matchedToken;
            }
            if ((CompactSyntaxTokenManager.jjtoSpecial[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0x0L) {
                final Token matchedToken = this.jjFillToken();
                if (specialToken == null) {
                    specialToken = matchedToken;
                }
                else {
                    matchedToken.specialToken = specialToken;
                    final Token token = specialToken;
                    final Token next = matchedToken;
                    token.next = next;
                    specialToken = next;
                }
                this.SkipLexicalActions(matchedToken);
            }
            else {
                this.SkipLexicalActions(null);
            }
            if (CompactSyntaxTokenManager.jjnewLexState[this.jjmatchedKind] == -1) {
                continue;
            }
            this.curLexState = CompactSyntaxTokenManager.jjnewLexState[this.jjmatchedKind];
        }
    }
    
    void SkipLexicalActions(final Token matchedToken) {
        final int jjmatchedKind = this.jjmatchedKind;
    }
    
    static {
        jjbitVec0 = new long[] { -2L, -1L, -1L, -1L };
        jjbitVec2 = new long[] { 0L, 0L, -1L, -1L };
        jjbitVec3 = new long[] { 0L, -16384L, -17590038560769L, 8388607L };
        jjbitVec4 = new long[] { 0L, 0L, 0L, -36028797027352577L };
        jjbitVec5 = new long[] { 9219994337134247935L, 9223372036854775294L, -1L, -274156627316187121L };
        jjbitVec6 = new long[] { 16777215L, -65536L, -576458553280167937L, 3L };
        jjbitVec7 = new long[] { 0L, 0L, -17179879616L, 4503588160110591L };
        jjbitVec8 = new long[] { -8194L, -536936449L, -65533L, 234134404065073567L };
        jjbitVec9 = new long[] { -562949953421312L, -8547991553L, 127L, 1979120929931264L };
        jjbitVec10 = new long[] { 576460743713488896L, -562949953419266L, 9007199254740991999L, 412319973375L };
        jjbitVec11 = new long[] { 2594073385365405664L, 17163091968L, 271902628478820320L, 844440767823872L };
        jjbitVec12 = new long[] { 247132830528276448L, 7881300924956672L, 2589004636761075680L, 4294967296L };
        jjbitVec13 = new long[] { 2579997437506199520L, 15837691904L, 270153412153034720L, 0L };
        jjbitVec14 = new long[] { 283724577500946400L, 12884901888L, 283724577500946400L, 13958643712L };
        jjbitVec15 = new long[] { 288228177128316896L, 12884901888L, 0L, 0L };
        jjbitVec16 = new long[] { 3799912185593854L, 63L, 2309621682768192918L, 31L };
        jjbitVec17 = new long[] { 0L, 4398046510847L, 0L, 0L };
        jjbitVec18 = new long[] { 0L, 0L, -4294967296L, 36028797018898495L };
        jjbitVec19 = new long[] { 5764607523034749677L, 12493387738468353L, -756383734487318528L, 144405459145588743L };
        jjbitVec20 = new long[] { -1L, -1L, -4026531841L, 288230376151711743L };
        jjbitVec21 = new long[] { -3233808385L, 4611686017001275199L, 6908521828386340863L, 2295745090394464220L };
        jjbitVec22 = new long[] { 83837761617920L, 0L, 7L, 0L };
        jjbitVec23 = new long[] { 4389456576640L, -2L, -8587837441L, 576460752303423487L };
        jjbitVec24 = new long[] { 35184372088800L, 0L, 0L, 0L };
        jjbitVec25 = new long[] { -1L, -1L, 274877906943L, 0L };
        jjbitVec26 = new long[] { -1L, -1L, 68719476735L, 0L };
        jjbitVec27 = new long[] { 0L, 0L, 36028797018963968L, -36028797027352577L };
        jjbitVec28 = new long[] { 16777215L, -65536L, -576458553280167937L, 196611L };
        jjbitVec29 = new long[] { -1L, 12884901951L, -17179879488L, 4503588160110591L };
        jjbitVec30 = new long[] { -8194L, -536936449L, -65413L, 234134404065073567L };
        jjbitVec31 = new long[] { -562949953421312L, -8547991553L, -4899916411759099777L, 1979120929931286L };
        jjbitVec32 = new long[] { 576460743713488896L, -277081224642561L, 9007199254740991999L, 288017070894841855L };
        jjbitVec33 = new long[] { -864691128455135250L, 281268803485695L, -3186861885341720594L, 1125692414638495L };
        jjbitVec34 = new long[] { -3211631683292264476L, 9006925953907079L, -869759877059465234L, 281204393786303L };
        jjbitVec35 = new long[] { -878767076314341394L, 281215949093263L, -4341532606274353172L, 280925229301191L };
        jjbitVec36 = new long[] { -4327961440926441490L, 281212990012895L, -4327961440926441492L, 281214063754719L };
        jjbitVec37 = new long[] { -4323457841299070996L, 281212992110031L, 0L, 0L };
        jjbitVec38 = new long[] { 576320014815068158L, 67076095L, 4323293666156225942L, 67059551L };
        jjbitVec39 = new long[] { -4422530440275951616L, -558551906910465L, 215680200883507167L, 0L };
        jjbitVec40 = new long[] { 0L, 0L, 0L, 9126739968L };
        jjbitVec41 = new long[] { 17732914942836896L, -2L, -6876561409L, 8646911284551352319L };
        jjnextStates = new int[] { 16, 17, 18, 19, 21, 25, 26, 27, 28, 30, 35, 36, 38, 39, 40, 10, 11, 13, 14, 3, 6, 7, 8 };
        jjstrLiteralImages = new String[] { "", "[", "=", "&=", "|=", "start", "div", "include", "~", "]", "grammar", "{", "}", "namespace", "default", "inherit", "datatypes", "empty", "text", "notAllowed", "|", "&", ",", "+", "?", "*", "element", "attribute", "(", ")", "-", "list", "mixed", "external", "parent", "string", "token", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, ">>", null };
        lexStateNames = new String[] { "DEFAULT", "AFTER_SINGLE_LINE_COMMENT", "AFTER_DOCUMENTATION" };
        jjnewLexState = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 2, -1, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
        jjtoToken = new long[] { 2287840842771070975L };
        jjtoSkip = new long[] { 22539988369408L };
        jjtoSpecial = new long[] { 21990232555520L };
    }
}
