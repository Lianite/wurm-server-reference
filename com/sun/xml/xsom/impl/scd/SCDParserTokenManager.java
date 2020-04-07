// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl.scd;

import java.io.IOException;
import java.io.PrintStream;

public class SCDParserTokenManager implements SCDParserConstants
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
    static final int[] jjnextStates;
    public static final String[] jjstrLiteralImages;
    public static final String[] lexStateNames;
    static final long[] jjtoToken;
    static final long[] jjtoSkip;
    protected SimpleCharStream input_stream;
    private final int[] jjrounds;
    private final int[] jjstateSet;
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
                if ((active0 & 0x3C08000000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    return 103;
                }
                if ((active0 & 0x400000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    return 55;
                }
                if ((active0 & 0x30000000000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    return 68;
                }
                if ((active0 & 0x2000000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    return 81;
                }
                if ((active0 & 0x200000000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    return 23;
                }
                if ((active0 & 0x40000000000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    return 34;
                }
                if ((active0 & 0x100000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    return 91;
                }
                if ((active0 & 0x18C1F4240000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    return 1;
                }
                if ((active0 & 0x1000000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    return 16;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x1FFFFF740000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    return this.jjmatchedPos = 1;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x1FFFFF740000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    this.jjmatchedPos = 2;
                    return 1;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x4100000000L) != 0x0L) {
                    if (this.jjmatchedPos < 2) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 2;
                    }
                    return -1;
                }
                if ((active0 & 0x1FBEFF740000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    this.jjmatchedPos = 3;
                    return 1;
                }
                return -1;
            }
            case 4: {
                if ((active0 & 0x4100000000L) != 0x0L) {
                    if (this.jjmatchedPos < 2) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 2;
                    }
                    return -1;
                }
                if ((active0 & 0x400000L) != 0x0L) {
                    if (this.jjmatchedPos < 3) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 3;
                    }
                    return -1;
                }
                if ((active0 & 0x1FBEFF340000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    this.jjmatchedPos = 4;
                    return 1;
                }
                return -1;
            }
            case 5: {
                if ((active0 & 0x4000000000L) != 0x0L) {
                    if (this.jjmatchedPos < 2) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 2;
                    }
                    return -1;
                }
                if ((active0 & 0x33C50000000L) != 0x0L) {
                    if (this.jjmatchedPos < 4) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 4;
                    }
                    return -1;
                }
                if ((active0 & 0x400000L) != 0x0L) {
                    if (this.jjmatchedPos < 3) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 3;
                    }
                    return -1;
                }
                if ((active0 & 0x1C82AF340000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    this.jjmatchedPos = 5;
                    return 1;
                }
                return -1;
            }
            case 6: {
                if ((active0 & 0x33C50000000L) != 0x0L) {
                    if (this.jjmatchedPos < 4) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 4;
                    }
                    return -1;
                }
                if ((active0 & 0x1C82AF340000L) != 0x0L) {
                    if (this.jjmatchedPos != 6) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 6;
                    }
                    return 1;
                }
                return -1;
            }
            case 7: {
                if ((active0 & 0x100000L) != 0x0L) {
                    if (this.jjmatchedPos < 6) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 6;
                    }
                    return -1;
                }
                if ((active0 & 0x13C00000000L) != 0x0L) {
                    if (this.jjmatchedPos < 4) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 4;
                    }
                    return -1;
                }
                if ((active0 & 0x1C82AF240000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    this.jjmatchedPos = 7;
                    return 1;
                }
                return -1;
            }
            case 8: {
                if ((active0 & 0x480AA240000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    this.jjmatchedPos = 8;
                    return 1;
                }
                if ((active0 & 0x180205000000L) != 0x0L) {
                    if (this.jjmatchedPos < 7) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 7;
                    }
                    return -1;
                }
                if ((active0 & 0x100000L) != 0x0L) {
                    if (this.jjmatchedPos < 6) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 6;
                    }
                    return -1;
                }
                if ((active0 & 0x1C00000000L) != 0x0L) {
                    if (this.jjmatchedPos < 4) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 4;
                    }
                    return -1;
                }
                return -1;
            }
            case 9: {
                if ((active0 & 0x80AA200000L) != 0x0L) {
                    if (this.jjmatchedPos != 9) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 9;
                    }
                    return 1;
                }
                if ((active0 & 0x180205000000L) != 0x0L) {
                    if (this.jjmatchedPos < 7) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 7;
                    }
                    return -1;
                }
                if ((active0 & 0x40000040000L) != 0x0L) {
                    if (this.jjmatchedPos < 8) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 8;
                    }
                    return -1;
                }
                if ((active0 & 0x1C00000000L) != 0x0L) {
                    if (this.jjmatchedPos < 4) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 4;
                    }
                    return -1;
                }
                return -1;
            }
            case 10: {
                if ((active0 & 0x100000000000L) != 0x0L) {
                    if (this.jjmatchedPos < 7) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 7;
                    }
                    return -1;
                }
                if ((active0 & 0x8000000L) != 0x0L) {
                    if (this.jjmatchedPos < 9) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 9;
                    }
                    return -1;
                }
                if ((active0 & 0x40000040000L) != 0x0L) {
                    if (this.jjmatchedPos < 8) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 8;
                    }
                    return -1;
                }
                if ((active0 & 0x80A2200000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    this.jjmatchedPos = 10;
                    return 1;
                }
                if ((active0 & 0xC00000000L) != 0x0L) {
                    if (this.jjmatchedPos < 4) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 4;
                    }
                    return -1;
                }
                return -1;
            }
            case 11: {
                if ((active0 & 0x40000000000L) != 0x0L) {
                    if (this.jjmatchedPos < 8) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 8;
                    }
                    return -1;
                }
                if ((active0 & 0x8000000L) != 0x0L) {
                    if (this.jjmatchedPos < 9) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 9;
                    }
                    return -1;
                }
                if ((active0 & 0xC00000000L) != 0x0L) {
                    if (this.jjmatchedPos < 4) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 4;
                    }
                    return -1;
                }
                if ((active0 & 0x80A2200000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    this.jjmatchedPos = 11;
                    return 1;
                }
                return -1;
            }
            case 12: {
                if ((active0 & 0x8000000000L) != 0x0L) {
                    if (this.jjmatchedPos < 11) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 11;
                    }
                    return -1;
                }
                if ((active0 & 0xC00000000L) != 0x0L) {
                    if (this.jjmatchedPos < 4) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 4;
                    }
                    return -1;
                }
                if ((active0 & 0xA2200000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    this.jjmatchedPos = 12;
                    return 1;
                }
                return -1;
            }
            case 13: {
                if ((active0 & 0x8000000000L) != 0x0L) {
                    if (this.jjmatchedPos < 11) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 11;
                    }
                    return -1;
                }
                if ((active0 & 0x2000000L) != 0x0L) {
                    if (this.jjmatchedPos < 12) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 12;
                    }
                    return -1;
                }
                if ((active0 & 0x400000000L) != 0x0L) {
                    if (this.jjmatchedPos < 4) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 4;
                    }
                    return -1;
                }
                if ((active0 & 0xA0200000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    this.jjmatchedPos = 13;
                    return 1;
                }
                return -1;
            }
            case 14: {
                if ((active0 & 0x8000000000L) != 0x0L) {
                    if (this.jjmatchedPos < 11) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 11;
                    }
                    return -1;
                }
                if ((active0 & 0x20000000L) != 0x0L) {
                    if (this.jjmatchedPos < 13) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 13;
                    }
                    return -1;
                }
                if ((active0 & 0x2000000L) != 0x0L) {
                    if (this.jjmatchedPos < 12) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 12;
                    }
                    return -1;
                }
                if ((active0 & 0x400000000L) != 0x0L) {
                    if (this.jjmatchedPos < 4) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 4;
                    }
                    return -1;
                }
                if ((active0 & 0x80200000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    this.jjmatchedPos = 14;
                    return 1;
                }
                return -1;
            }
            case 15: {
                if ((active0 & 0x20000000L) != 0x0L) {
                    if (this.jjmatchedPos < 13) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 13;
                    }
                    return -1;
                }
                if ((active0 & 0x80200000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    this.jjmatchedPos = 15;
                    return 1;
                }
                return -1;
            }
            case 16: {
                if ((active0 & 0x80200000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    this.jjmatchedPos = 16;
                    return 1;
                }
                return -1;
            }
            case 17: {
                if ((active0 & 0x80200000L) != 0x0L) {
                    if (this.jjmatchedPos < 16) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 16;
                    }
                    return -1;
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
            case '*': {
                return this.jjStopAtPos(0, 45);
            }
            case '/': {
                this.jjmatchedKind = 16;
                return this.jjMoveStringLiteralDfa1_0(131072L);
            }
            case '0': {
                return this.jjStopAtPos(0, 46);
            }
            case ':': {
                return this.jjStopAtPos(0, 15);
            }
            case '@': {
                return this.jjStopAtPos(0, 19);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa1_0(825170853888L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa1_0(16777216L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa1_0(4398046511104L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa1_0(1048576L);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa1_0(3298534883328L);
            }
            case 'g': {
                return this.jjMoveStringLiteralDfa1_0(1073741824L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa1_0(2214592512L);
            }
            case 'k': {
                return this.jjMoveStringLiteralDfa1_0(4294967296L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa1_0(257832255488L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa1_0(8589934592L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa1_0(33554432L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa1_0(270532608L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa1_0(4194304L);
            }
            case 'x': {
                return this.jjMoveStringLiteralDfa1_0(26388279066624L);
            }
            case '~': {
                return this.jjStopAtPos(0, 23);
            }
            default: {
                return this.jjMoveNfa_0(0, 0);
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
            case '-': {
                return this.jjMoveStringLiteralDfa2_0(active0, 26388279066624L);
            }
            case '/': {
                if ((active0 & 0x20000L) != 0x0L) {
                    return this.jjStopAtPos(1, 17);
                }
                break;
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa2_0(active0, 3298551660544L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa2_0(active0, 268435456L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa2_0(active0, 2147483648L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa2_0(active0, 4429185024L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa2_0(active0, 1048576L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa2_0(active0, 824633720832L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa2_0(active0, 4664334483456L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa2_0(active0, 1107296256L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa2_0(active0, 604241920L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa2_0(active0, 2097152L);
            }
            case 'y': {
                return this.jjMoveStringLiteralDfa2_0(active0, 4194304L);
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
            case 'b': {
                return this.jjMoveStringLiteralDfa3_0(active0, 2097152L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa3_0(active0, 3298534883328L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa3_0(active0, 257698037760L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa3_0(active0, 2215641088L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa3_0(active0, 33554432L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa3_0(active0, 4398180728832L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa3_0(active0, 1342177280L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa3_0(active0, 4194304L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa3_0(active0, 26388295843840L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa3_0(active0, 9127067648L);
            }
            case 'y': {
                return this.jjMoveStringLiteralDfa3_0(active0, 828928688128L);
            }
            default: {
                return this.jjStartNfa_0(1, active0);
            }
        }
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
            case ':': {
                return this.jjMoveStringLiteralDfa4_0(active0, 279172874240L);
            }
            case 'A': {
                return this.jjMoveStringLiteralDfa4_0(active0, 549755813888L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa4_0(active0, 8589934592L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa4_0(active0, 134217728L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa4_0(active0, 26388279066624L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa4_0(active0, 3556253892608L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa4_0(active0, 101711872L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa4_0(active0, 2147483648L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa4_0(active0, 4398314946560L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa4_0(active0, 537133056L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa4_0(active0, 2097152L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa4_0(active0, 1073741824L);
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
            case ':': {
                if ((active0 & 0x100000000L) != 0x0L) {
                    return this.jjStopAtPos(4, 32);
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 274882101248L);
            }
            case 'T': {
                return this.jjMoveStringLiteralDfa5_0(active0, 83886080L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa5_0(active0, 403701760L);
            }
            case 'h': {
                return this.jjMoveStringLiteralDfa5_0(active0, 26388279066624L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa5_0(active0, 570687488L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa5_0(active0, 257698037760L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa5_0(active0, 4398046511104L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa5_0(active0, 1073741824L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa5_0(active0, 3859030212608L);
            }
            default: {
                return this.jjStartNfa_0(3, active0);
            }
        }
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
            case '*': {
                if ((active0 & 0x4000000000L) != 0x0L) {
                    return this.jjStopAtPos(5, 38);
                }
                break;
            }
            case ':': {
                if ((active0 & 0x400000L) != 0x0L) {
                    return this.jjStopAtPos(5, 22);
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 3557575098368L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa6_0(active0, 537133056L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa6_0(active0, 26388279066624L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa6_0(active0, 10739515392L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa6_0(active0, 4398047559680L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa6_0(active0, 134217728L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa6_0(active0, 549789368320L);
            }
            case 'y': {
                return this.jjMoveStringLiteralDfa6_0(active0, 83886080L);
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
            case ':': {
                if ((active0 & 0x10000000L) != 0x0L) {
                    return this.jjStopAtPos(6, 28);
                }
                if ((active0 & 0x40000000L) != 0x0L) {
                    return this.jjStopAtPos(6, 30);
                }
                if ((active0 & 0x20000000000L) != 0x0L) {
                    this.jjmatchedKind = 41;
                    this.jjmatchedPos = 6;
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 1357209665536L);
            }
            case 'T': {
                return this.jjMoveStringLiteralDfa7_0(active0, 134217728L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa7_0(active0, 4398046511104L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa7_0(active0, 33554432L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa7_0(active0, 26388279066624L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa7_0(active0, 8589934592L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa7_0(active0, 83886080L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa7_0(active0, 549755813888L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa7_0(active0, 2150629376L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa7_0(active0, 537133056L);
            }
            default: {
                return this.jjStartNfa_0(5, active0);
            }
        }
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
            case '*': {
                if ((active0 & 0x2000000000L) != 0x0L) {
                    return this.jjStopAtPos(7, 37);
                }
                if ((active0 & 0x10000000000L) != 0x0L) {
                    return this.jjStopAtPos(7, 40);
                }
                break;
            }
            case ':': {
                return this.jjMoveStringLiteralDfa8_0(active0, 1048576L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa8_0(active0, 26456998543360L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa8_0(active0, 34359738368L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa8_0(active0, 83886080L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa8_0(active0, 549755813888L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa8_0(active0, 4406636445696L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa8_0(active0, 17179869184L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa8_0(active0, 537133056L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa8_0(active0, 2097152L);
            }
            case 'v': {
                return this.jjMoveStringLiteralDfa8_0(active0, 33554432L);
            }
            case 'y': {
                return this.jjMoveStringLiteralDfa8_0(active0, 2281701376L);
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
            case ':': {
                if ((active0 & 0x100000L) != 0x0L) {
                    return this.jjStopAtPos(8, 20);
                }
                return this.jjMoveStringLiteralDfa9_0(active0, 26396952887296L);
            }
            case 'C': {
                return this.jjMoveStringLiteralDfa9_0(active0, 2147483648L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa9_0(active0, 549755813888L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa9_0(active0, 17750556672L);
            }
            case 'h': {
                return this.jjMoveStringLiteralDfa9_0(active0, 34359738368L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa9_0(active0, 68719476736L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa9_0(active0, 134217728L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa9_0(active0, 4398048608256L);
            }
            default: {
                return this.jjStartNfa_0(7, active0);
            }
        }
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
            case ':': {
                if ((active0 & 0x1000000L) != 0x0L) {
                    return this.jjStopAtPos(9, 24);
                }
                if ((active0 & 0x4000000L) != 0x0L) {
                    return this.jjStopAtPos(9, 26);
                }
                if ((active0 & 0x200000000L) != 0x0L) {
                    return this.jjStopAtPos(9, 33);
                }
                if ((active0 & 0x80000000000L) != 0x0L) {
                    this.jjmatchedKind = 43;
                    this.jjmatchedPos = 9;
                }
                return this.jjMoveStringLiteralDfa10_0(active0, 21990232817664L);
            }
            case 'G': {
                return this.jjMoveStringLiteralDfa10_0(active0, 536870912L);
            }
            case 'T': {
                return this.jjMoveStringLiteralDfa10_0(active0, 33554432L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa10_0(active0, 134217728L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa10_0(active0, 2097152L);
            }
            case 'l': {
                if ((active0 & 0x1000000000L) != 0x0L) {
                    return this.jjStopAtPos(9, 36);
                }
                break;
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa10_0(active0, 36507222016L);
            }
            case 'q': {
                return this.jjMoveStringLiteralDfa10_0(active0, 17179869184L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa10_0(active0, 549755813888L);
            }
        }
        return this.jjStartNfa_0(8, active0);
    }
    
    private final int jjMoveStringLiteralDfa10_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(8, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(9, active0);
            return 10;
        }
        switch (this.curChar) {
            case '*': {
                if ((active0 & 0x100000000000L) != 0x0L) {
                    return this.jjStopAtPos(10, 44);
                }
                break;
            }
            case ':': {
                if ((active0 & 0x40000L) != 0x0L) {
                    return this.jjStopAtPos(10, 18);
                }
                return this.jjMoveStringLiteralDfa11_0(active0, 4398180728832L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa11_0(active0, 34359738368L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa11_0(active0, 2147483648L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa11_0(active0, 2097152L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa11_0(active0, 536870912L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa11_0(active0, 549755813888L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa11_0(active0, 17179869184L);
            }
            case 'y': {
                return this.jjMoveStringLiteralDfa11_0(active0, 33554432L);
            }
        }
        return this.jjStartNfa_0(9, active0);
    }
    
    private final int jjMoveStringLiteralDfa11_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(9, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(10, active0);
            return 11;
        }
        switch (this.curChar) {
            case '*': {
                if ((active0 & 0x40000000000L) != 0x0L) {
                    return this.jjStopAtPos(11, 42);
                }
                break;
            }
            case ':': {
                if ((active0 & 0x8000000L) != 0x0L) {
                    return this.jjStopAtPos(11, 27);
                }
                break;
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa12_0(active0, 34359738368L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa12_0(active0, 566935683072L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa12_0(active0, 2097152L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa12_0(active0, 536870912L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa12_0(active0, 33554432L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa12_0(active0, 2147483648L);
            }
        }
        return this.jjStartNfa_0(10, active0);
    }
    
    private final int jjMoveStringLiteralDfa12_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(10, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(11, active0);
            return 12;
        }
        switch (this.curChar) {
            case ':': {
                return this.jjMoveStringLiteralDfa13_0(active0, 549755813888L);
            }
            case 'G': {
                return this.jjMoveStringLiteralDfa13_0(active0, 2097152L);
            }
            case 'e': {
                if ((active0 & 0x800000000L) != 0x0L) {
                    return this.jjStopAtPos(12, 35);
                }
                return this.jjMoveStringLiteralDfa13_0(active0, 33554432L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa13_0(active0, 17179869184L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa13_0(active0, 2147483648L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa13_0(active0, 536870912L);
            }
            default: {
                return this.jjStartNfa_0(11, active0);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa13_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(11, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(12, active0);
            return 13;
        }
        switch (this.curChar) {
            case ':': {
                return this.jjMoveStringLiteralDfa14_0(active0, 549789368320L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa14_0(active0, 2147483648L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa14_0(active0, 17179869184L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa14_0(active0, 536870912L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa14_0(active0, 2097152L);
            }
            default: {
                return this.jjStartNfa_0(12, active0);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa14_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(12, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(13, active0);
            return 14;
        }
        switch (this.curChar) {
            case '*': {
                if ((active0 & 0x8000000000L) != 0x0L) {
                    return this.jjStopAtPos(14, 39);
                }
                break;
            }
            case ':': {
                if ((active0 & 0x2000000L) != 0x0L) {
                    return this.jjStopAtPos(14, 25);
                }
                return this.jjMoveStringLiteralDfa15_0(active0, 536870912L);
            }
            case 'e': {
                if ((active0 & 0x400000000L) != 0x0L) {
                    return this.jjStopAtPos(14, 34);
                }
                break;
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa15_0(active0, 2147483648L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa15_0(active0, 2097152L);
            }
        }
        return this.jjStartNfa_0(13, active0);
    }
    
    private final int jjMoveStringLiteralDfa15_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(13, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(14, active0);
            return 15;
        }
        switch (this.curChar) {
            case ':': {
                if ((active0 & 0x20000000L) != 0x0L) {
                    return this.jjStopAtPos(15, 29);
                }
                break;
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa16_0(active0, 2147483648L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa16_0(active0, 2097152L);
            }
        }
        return this.jjStartNfa_0(14, active0);
    }
    
    private final int jjMoveStringLiteralDfa16_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(14, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(15, active0);
            return 16;
        }
        switch (this.curChar) {
            case 'p': {
                return this.jjMoveStringLiteralDfa17_0(active0, 2097152L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa17_0(active0, 2147483648L);
            }
            default: {
                return this.jjStartNfa_0(15, active0);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa17_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(15, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(16, active0);
            return 17;
        }
        switch (this.curChar) {
            case ':': {
                return this.jjMoveStringLiteralDfa18_0(active0, 2149580800L);
            }
            default: {
                return this.jjStartNfa_0(16, active0);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa18_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(16, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(17, active0);
            return 18;
        }
        switch (this.curChar) {
            case ':': {
                if ((active0 & 0x200000L) != 0x0L) {
                    return this.jjStopAtPos(18, 21);
                }
                if ((active0 & 0x80000000L) != 0x0L) {
                    return this.jjStopAtPos(18, 31);
                }
                break;
            }
        }
        return this.jjStartNfa_0(17, active0);
    }
    
    private final void jjCheckNAdd(final int state) {
        if (this.jjrounds[state] != this.jjround) {
            this.jjstateSet[this.jjnewStateCnt++] = state;
            this.jjrounds[state] = this.jjround;
        }
    }
    
    private final void jjAddStates(int start, final int end) {
        do {
            this.jjstateSet[this.jjnewStateCnt++] = SCDParserTokenManager.jjnextStates[start];
        } while (start++ != end);
    }
    
    private final void jjCheckNAddTwoStates(final int state1, final int state2) {
        this.jjCheckNAdd(state1);
        this.jjCheckNAdd(state2);
    }
    
    private final void jjCheckNAddStates(int start, final int end) {
        do {
            this.jjCheckNAdd(SCDParserTokenManager.jjnextStates[start]);
        } while (start++ != end);
    }
    
    private final void jjCheckNAddStates(final int start) {
        this.jjCheckNAdd(SCDParserTokenManager.jjnextStates[start]);
        this.jjCheckNAdd(SCDParserTokenManager.jjnextStates[start + 1]);
    }
    
    private final int jjMoveNfa_0(final int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 148;
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
                        case 1:
                        case 34: {
                            if ((0x3FF600000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(1);
                            continue;
                        }
                        case 91: {
                            if ((0x3FF600000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(1);
                            continue;
                        }
                        case 16: {
                            if ((0x3FF600000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(1);
                            continue;
                        }
                        case 55: {
                            if ((0x3FF600000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(1);
                            continue;
                        }
                        case 68: {
                            if ((0x3FF600000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(1);
                            continue;
                        }
                        case 103: {
                            if ((0x3FF600000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(1);
                            continue;
                        }
                        case 81: {
                            if ((0x3FF600000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(1);
                            continue;
                        }
                        case 23: {
                            if ((0x3FF600000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(1);
                            continue;
                        }
                        case 3: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjAddStates(0, 1);
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
                        case 34: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 12) {
                                    kind = 12;
                                }
                                this.jjCheckNAdd(1);
                            }
                            if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 33;
                                continue;
                            }
                            continue;
                        }
                        case 91: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 12) {
                                    kind = 12;
                                }
                                this.jjCheckNAdd(1);
                            }
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 90;
                                continue;
                            }
                            continue;
                        }
                        case 16: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 12) {
                                    kind = 12;
                                }
                                this.jjCheckNAdd(1);
                            }
                            if (this.curChar == 'o') {
                                this.jjstateSet[this.jjnewStateCnt++] = 15;
                                continue;
                            }
                            continue;
                        }
                        case 55: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 12) {
                                    kind = 12;
                                }
                                this.jjCheckNAdd(1);
                            }
                            if (this.curChar == 'o') {
                                this.jjstateSet[this.jjnewStateCnt++] = 54;
                                continue;
                            }
                            continue;
                        }
                        case 68: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 12) {
                                    kind = 12;
                                }
                                this.jjCheckNAdd(1);
                            }
                            if (this.curChar == 'r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 67;
                                continue;
                            }
                            continue;
                        }
                        case 103: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 12) {
                                    kind = 12;
                                }
                                this.jjCheckNAdd(1);
                            }
                            if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 146;
                            }
                            else if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 139;
                            }
                            if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 132;
                            }
                            else if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 122;
                            }
                            if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 112;
                                continue;
                            }
                            if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 102;
                                continue;
                            }
                            continue;
                        }
                        case 0: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 12) {
                                    kind = 12;
                                }
                                this.jjCheckNAdd(1);
                            }
                            else if (this.curChar == '[') {
                                this.jjstateSet[this.jjnewStateCnt++] = 3;
                            }
                            if (this.curChar == 'm') {
                                this.jjAddStates(2, 7);
                                continue;
                            }
                            if (this.curChar == 'e') {
                                this.jjstateSet[this.jjnewStateCnt++] = 91;
                                continue;
                            }
                            if (this.curChar == 'p') {
                                this.jjstateSet[this.jjnewStateCnt++] = 81;
                                continue;
                            }
                            if (this.curChar == 'l') {
                                this.jjstateSet[this.jjnewStateCnt++] = 74;
                                continue;
                            }
                            if (this.curChar == 'f') {
                                this.jjstateSet[this.jjnewStateCnt++] = 68;
                                continue;
                            }
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 55;
                                continue;
                            }
                            if (this.curChar == 'w') {
                                this.jjstateSet[this.jjnewStateCnt++] = 44;
                                continue;
                            }
                            if (this.curChar == 'c') {
                                this.jjstateSet[this.jjnewStateCnt++] = 34;
                                continue;
                            }
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 23;
                                continue;
                            }
                            if (this.curChar == 'b') {
                                this.jjstateSet[this.jjnewStateCnt++] = 16;
                                continue;
                            }
                            if (this.curChar == 'o') {
                                this.jjstateSet[this.jjnewStateCnt++] = 10;
                                continue;
                            }
                            continue;
                        }
                        case 81: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 12) {
                                    kind = 12;
                                }
                                this.jjCheckNAdd(1);
                            }
                            if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 80;
                                continue;
                            }
                            continue;
                        }
                        case 23: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 12) {
                                    kind = 12;
                                }
                                this.jjCheckNAdd(1);
                            }
                            if (this.curChar == 'u') {
                                this.jjstateSet[this.jjnewStateCnt++] = 22;
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(1);
                            continue;
                        }
                        case 2: {
                            if (this.curChar == '[') {
                                this.jjstateSet[this.jjnewStateCnt++] = 3;
                                continue;
                            }
                            continue;
                        }
                        case 4: {
                            if (this.curChar == ']') {
                                kind = 13;
                                continue;
                            }
                            continue;
                        }
                        case 5: {
                            if (this.curChar == 'd' && kind > 14) {
                                kind = 14;
                                continue;
                            }
                            continue;
                        }
                        case 6:
                        case 12: {
                            if (this.curChar == 'e') {
                                this.jjCheckNAdd(5);
                                continue;
                            }
                            continue;
                        }
                        case 7: {
                            if (this.curChar == 'r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 6;
                                continue;
                            }
                            continue;
                        }
                        case 8: {
                            if (this.curChar == 'e') {
                                this.jjstateSet[this.jjnewStateCnt++] = 7;
                                continue;
                            }
                            continue;
                        }
                        case 9: {
                            if (this.curChar == 'd') {
                                this.jjstateSet[this.jjnewStateCnt++] = 8;
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            if (this.curChar == 'r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 9;
                                continue;
                            }
                            continue;
                        }
                        case 11: {
                            if (this.curChar == 'o') {
                                this.jjstateSet[this.jjnewStateCnt++] = 10;
                                continue;
                            }
                            continue;
                        }
                        case 13: {
                            if (this.curChar == 'd') {
                                this.jjstateSet[this.jjnewStateCnt++] = 12;
                                continue;
                            }
                            continue;
                        }
                        case 14: {
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 13;
                                continue;
                            }
                            continue;
                        }
                        case 15: {
                            if (this.curChar == 'u') {
                                this.jjstateSet[this.jjnewStateCnt++] = 14;
                                continue;
                            }
                            continue;
                        }
                        case 17: {
                            if (this.curChar == 'b') {
                                this.jjstateSet[this.jjnewStateCnt++] = 16;
                                continue;
                            }
                            continue;
                        }
                        case 18: {
                            if (this.curChar == 'c' && kind > 14) {
                                kind = 14;
                                continue;
                            }
                            continue;
                        }
                        case 19: {
                            if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 18;
                                continue;
                            }
                            continue;
                        }
                        case 20: {
                            if (this.curChar == 'r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 19;
                                continue;
                            }
                            continue;
                        }
                        case 21: {
                            if (this.curChar == 'e') {
                                this.jjstateSet[this.jjnewStateCnt++] = 20;
                                continue;
                            }
                            continue;
                        }
                        case 22: {
                            if (this.curChar == 'm') {
                                this.jjstateSet[this.jjnewStateCnt++] = 21;
                                continue;
                            }
                            continue;
                        }
                        case 24: {
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 23;
                                continue;
                            }
                            continue;
                        }
                        case 25: {
                            if (this.curChar == 'y' && kind > 14) {
                                kind = 14;
                                continue;
                            }
                            continue;
                        }
                        case 26: {
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 25;
                                continue;
                            }
                            continue;
                        }
                        case 27: {
                            if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 26;
                                continue;
                            }
                            continue;
                        }
                        case 28: {
                            if (this.curChar == 'l') {
                                this.jjstateSet[this.jjnewStateCnt++] = 27;
                                continue;
                            }
                            continue;
                        }
                        case 29: {
                            if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 28;
                                continue;
                            }
                            continue;
                        }
                        case 30: {
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 29;
                                continue;
                            }
                            continue;
                        }
                        case 31: {
                            if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 30;
                                continue;
                            }
                            continue;
                        }
                        case 32: {
                            if (this.curChar == 'd') {
                                this.jjstateSet[this.jjnewStateCnt++] = 31;
                                continue;
                            }
                            continue;
                        }
                        case 33: {
                            if (this.curChar == 'r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 32;
                                continue;
                            }
                            continue;
                        }
                        case 35: {
                            if (this.curChar == 'c') {
                                this.jjstateSet[this.jjnewStateCnt++] = 34;
                                continue;
                            }
                            continue;
                        }
                        case 36: {
                            if (this.curChar == 'e' && kind > 14) {
                                kind = 14;
                                continue;
                            }
                            continue;
                        }
                        case 37: {
                            if (this.curChar == 'c') {
                                this.jjCheckNAdd(36);
                                continue;
                            }
                            continue;
                        }
                        case 38: {
                            if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 37;
                                continue;
                            }
                            continue;
                        }
                        case 39: {
                            if (this.curChar == 'p') {
                                this.jjstateSet[this.jjnewStateCnt++] = 38;
                                continue;
                            }
                            continue;
                        }
                        case 40: {
                            if (this.curChar == 'S') {
                                this.jjstateSet[this.jjnewStateCnt++] = 39;
                                continue;
                            }
                            continue;
                        }
                        case 41: {
                            if (this.curChar == 'e') {
                                this.jjstateSet[this.jjnewStateCnt++] = 40;
                                continue;
                            }
                            continue;
                        }
                        case 42: {
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 41;
                                continue;
                            }
                            continue;
                        }
                        case 43: {
                            if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 42;
                                continue;
                            }
                            continue;
                        }
                        case 44: {
                            if (this.curChar == 'h') {
                                this.jjstateSet[this.jjnewStateCnt++] = 43;
                                continue;
                            }
                            continue;
                        }
                        case 45: {
                            if (this.curChar == 'w') {
                                this.jjstateSet[this.jjnewStateCnt++] = 44;
                                continue;
                            }
                            continue;
                        }
                        case 46: {
                            if (this.curChar == 's' && kind > 14) {
                                kind = 14;
                                continue;
                            }
                            continue;
                        }
                        case 47:
                        case 57: {
                            if (this.curChar == 't') {
                                this.jjCheckNAdd(46);
                                continue;
                            }
                            continue;
                        }
                        case 48: {
                            if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 47;
                                continue;
                            }
                            continue;
                        }
                        case 49: {
                            if (this.curChar == 'g') {
                                this.jjstateSet[this.jjnewStateCnt++] = 48;
                                continue;
                            }
                            continue;
                        }
                        case 50: {
                            if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 49;
                                continue;
                            }
                            continue;
                        }
                        case 51: {
                            if (this.curChar == 'D') {
                                this.jjstateSet[this.jjnewStateCnt++] = 50;
                                continue;
                            }
                            continue;
                        }
                        case 52: {
                            if (this.curChar == 'l') {
                                this.jjstateSet[this.jjnewStateCnt++] = 51;
                                continue;
                            }
                            continue;
                        }
                        case 53: {
                            if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 52;
                                continue;
                            }
                            continue;
                        }
                        case 54: {
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 53;
                                continue;
                            }
                            continue;
                        }
                        case 56: {
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 55;
                                continue;
                            }
                            continue;
                        }
                        case 58: {
                            if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 57;
                                continue;
                            }
                            continue;
                        }
                        case 59: {
                            if (this.curChar == 'g') {
                                this.jjstateSet[this.jjnewStateCnt++] = 58;
                                continue;
                            }
                            continue;
                        }
                        case 60: {
                            if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 59;
                                continue;
                            }
                            continue;
                        }
                        case 61: {
                            if (this.curChar == 'D') {
                                this.jjstateSet[this.jjnewStateCnt++] = 60;
                                continue;
                            }
                            continue;
                        }
                        case 62: {
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 61;
                                continue;
                            }
                            continue;
                        }
                        case 63: {
                            if (this.curChar == 'o') {
                                this.jjstateSet[this.jjnewStateCnt++] = 62;
                                continue;
                            }
                            continue;
                        }
                        case 64: {
                            if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 63;
                                continue;
                            }
                            continue;
                        }
                        case 65: {
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 64;
                                continue;
                            }
                            continue;
                        }
                        case 66: {
                            if (this.curChar == 'c') {
                                this.jjstateSet[this.jjnewStateCnt++] = 65;
                                continue;
                            }
                            continue;
                        }
                        case 67: {
                            if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 66;
                                continue;
                            }
                            continue;
                        }
                        case 69: {
                            if (this.curChar == 'f') {
                                this.jjstateSet[this.jjnewStateCnt++] = 68;
                                continue;
                            }
                            continue;
                        }
                        case 70: {
                            if (this.curChar == 'h' && kind > 14) {
                                kind = 14;
                                continue;
                            }
                            continue;
                        }
                        case 71:
                        case 134:
                        case 141: {
                            if (this.curChar == 't') {
                                this.jjCheckNAdd(70);
                                continue;
                            }
                            continue;
                        }
                        case 72: {
                            if (this.curChar == 'g') {
                                this.jjstateSet[this.jjnewStateCnt++] = 71;
                                continue;
                            }
                            continue;
                        }
                        case 73: {
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 72;
                                continue;
                            }
                            continue;
                        }
                        case 74: {
                            if (this.curChar == 'e') {
                                this.jjstateSet[this.jjnewStateCnt++] = 73;
                                continue;
                            }
                            continue;
                        }
                        case 75: {
                            if (this.curChar == 'l') {
                                this.jjstateSet[this.jjnewStateCnt++] = 74;
                                continue;
                            }
                            continue;
                        }
                        case 76: {
                            if (this.curChar == 'n' && kind > 14) {
                                kind = 14;
                                continue;
                            }
                            continue;
                        }
                        case 77: {
                            if (this.curChar == 'r') {
                                this.jjCheckNAdd(76);
                                continue;
                            }
                            continue;
                        }
                        case 78: {
                            if (this.curChar == 'e') {
                                this.jjstateSet[this.jjnewStateCnt++] = 77;
                                continue;
                            }
                            continue;
                        }
                        case 79: {
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 78;
                                continue;
                            }
                            continue;
                        }
                        case 80: {
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 79;
                                continue;
                            }
                            continue;
                        }
                        case 82: {
                            if (this.curChar == 'p') {
                                this.jjstateSet[this.jjnewStateCnt++] = 81;
                                continue;
                            }
                            continue;
                        }
                        case 83: {
                            if (this.curChar == 'o') {
                                this.jjCheckNAdd(76);
                                continue;
                            }
                            continue;
                        }
                        case 84: {
                            if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 83;
                                continue;
                            }
                            continue;
                        }
                        case 85: {
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 84;
                                continue;
                            }
                            continue;
                        }
                        case 86: {
                            if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 85;
                                continue;
                            }
                            continue;
                        }
                        case 87: {
                            if (this.curChar == 'r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 86;
                                continue;
                            }
                            continue;
                        }
                        case 88: {
                            if (this.curChar == 'e') {
                                this.jjstateSet[this.jjnewStateCnt++] = 87;
                                continue;
                            }
                            continue;
                        }
                        case 89: {
                            if (this.curChar == 'm') {
                                this.jjstateSet[this.jjnewStateCnt++] = 88;
                                continue;
                            }
                            continue;
                        }
                        case 90: {
                            if (this.curChar == 'u') {
                                this.jjstateSet[this.jjnewStateCnt++] = 89;
                                continue;
                            }
                            continue;
                        }
                        case 92: {
                            if (this.curChar == 'e') {
                                this.jjstateSet[this.jjnewStateCnt++] = 91;
                                continue;
                            }
                            continue;
                        }
                        case 93: {
                            if (this.curChar == 'm') {
                                this.jjAddStates(2, 7);
                                continue;
                            }
                            continue;
                        }
                        case 94:
                        case 104:
                        case 114:
                        case 124: {
                            if (this.curChar == 'v') {
                                this.jjCheckNAdd(36);
                                continue;
                            }
                            continue;
                        }
                        case 95: {
                            if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 94;
                                continue;
                            }
                            continue;
                        }
                        case 96: {
                            if (this.curChar == 's') {
                                this.jjstateSet[this.jjnewStateCnt++] = 95;
                                continue;
                            }
                            continue;
                        }
                        case 97: {
                            if (this.curChar == 'u') {
                                this.jjstateSet[this.jjnewStateCnt++] = 96;
                                continue;
                            }
                            continue;
                        }
                        case 98: {
                            if (this.curChar == 'l') {
                                this.jjstateSet[this.jjnewStateCnt++] = 97;
                                continue;
                            }
                            continue;
                        }
                        case 99: {
                            if (this.curChar == 'c') {
                                this.jjstateSet[this.jjnewStateCnt++] = 98;
                                continue;
                            }
                            continue;
                        }
                        case 100: {
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 99;
                                continue;
                            }
                            continue;
                        }
                        case 101: {
                            if (this.curChar == 'I') {
                                this.jjstateSet[this.jjnewStateCnt++] = 100;
                                continue;
                            }
                            continue;
                        }
                        case 102: {
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 101;
                                continue;
                            }
                            continue;
                        }
                        case 105: {
                            if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 104;
                                continue;
                            }
                            continue;
                        }
                        case 106: {
                            if (this.curChar == 's') {
                                this.jjstateSet[this.jjnewStateCnt++] = 105;
                                continue;
                            }
                            continue;
                        }
                        case 107: {
                            if (this.curChar == 'u') {
                                this.jjstateSet[this.jjnewStateCnt++] = 106;
                                continue;
                            }
                            continue;
                        }
                        case 108: {
                            if (this.curChar == 'l') {
                                this.jjstateSet[this.jjnewStateCnt++] = 107;
                                continue;
                            }
                            continue;
                        }
                        case 109: {
                            if (this.curChar == 'c') {
                                this.jjstateSet[this.jjnewStateCnt++] = 108;
                                continue;
                            }
                            continue;
                        }
                        case 110: {
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 109;
                                continue;
                            }
                            continue;
                        }
                        case 111: {
                            if (this.curChar == 'I') {
                                this.jjstateSet[this.jjnewStateCnt++] = 110;
                                continue;
                            }
                            continue;
                        }
                        case 112: {
                            if (this.curChar == 'x') {
                                this.jjstateSet[this.jjnewStateCnt++] = 111;
                                continue;
                            }
                            continue;
                        }
                        case 113: {
                            if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 112;
                                continue;
                            }
                            continue;
                        }
                        case 115: {
                            if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 114;
                                continue;
                            }
                            continue;
                        }
                        case 116: {
                            if (this.curChar == 's') {
                                this.jjstateSet[this.jjnewStateCnt++] = 115;
                                continue;
                            }
                            continue;
                        }
                        case 117: {
                            if (this.curChar == 'u') {
                                this.jjstateSet[this.jjnewStateCnt++] = 116;
                                continue;
                            }
                            continue;
                        }
                        case 118: {
                            if (this.curChar == 'l') {
                                this.jjstateSet[this.jjnewStateCnt++] = 117;
                                continue;
                            }
                            continue;
                        }
                        case 119: {
                            if (this.curChar == 'c') {
                                this.jjstateSet[this.jjnewStateCnt++] = 118;
                                continue;
                            }
                            continue;
                        }
                        case 120: {
                            if (this.curChar == 'x') {
                                this.jjstateSet[this.jjnewStateCnt++] = 119;
                                continue;
                            }
                            continue;
                        }
                        case 121: {
                            if (this.curChar == 'E') {
                                this.jjstateSet[this.jjnewStateCnt++] = 120;
                                continue;
                            }
                            continue;
                        }
                        case 122: {
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 121;
                                continue;
                            }
                            continue;
                        }
                        case 123: {
                            if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 122;
                                continue;
                            }
                            continue;
                        }
                        case 125: {
                            if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 124;
                                continue;
                            }
                            continue;
                        }
                        case 126: {
                            if (this.curChar == 's') {
                                this.jjstateSet[this.jjnewStateCnt++] = 125;
                                continue;
                            }
                            continue;
                        }
                        case 127: {
                            if (this.curChar == 'u') {
                                this.jjstateSet[this.jjnewStateCnt++] = 126;
                                continue;
                            }
                            continue;
                        }
                        case 128: {
                            if (this.curChar == 'l') {
                                this.jjstateSet[this.jjnewStateCnt++] = 127;
                                continue;
                            }
                            continue;
                        }
                        case 129: {
                            if (this.curChar == 'c') {
                                this.jjstateSet[this.jjnewStateCnt++] = 128;
                                continue;
                            }
                            continue;
                        }
                        case 130: {
                            if (this.curChar == 'x') {
                                this.jjstateSet[this.jjnewStateCnt++] = 129;
                                continue;
                            }
                            continue;
                        }
                        case 131: {
                            if (this.curChar == 'E') {
                                this.jjstateSet[this.jjnewStateCnt++] = 130;
                                continue;
                            }
                            continue;
                        }
                        case 132: {
                            if (this.curChar == 'x') {
                                this.jjstateSet[this.jjnewStateCnt++] = 131;
                                continue;
                            }
                            continue;
                        }
                        case 133: {
                            if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 132;
                                continue;
                            }
                            continue;
                        }
                        case 135: {
                            if (this.curChar == 'g') {
                                this.jjstateSet[this.jjnewStateCnt++] = 134;
                                continue;
                            }
                            continue;
                        }
                        case 136: {
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 135;
                                continue;
                            }
                            continue;
                        }
                        case 137: {
                            if (this.curChar == 'e') {
                                this.jjstateSet[this.jjnewStateCnt++] = 136;
                                continue;
                            }
                            continue;
                        }
                        case 138: {
                            if (this.curChar == 'L') {
                                this.jjstateSet[this.jjnewStateCnt++] = 137;
                                continue;
                            }
                            continue;
                        }
                        case 139: {
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 138;
                                continue;
                            }
                            continue;
                        }
                        case 140: {
                            if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 139;
                                continue;
                            }
                            continue;
                        }
                        case 142: {
                            if (this.curChar == 'g') {
                                this.jjstateSet[this.jjnewStateCnt++] = 141;
                                continue;
                            }
                            continue;
                        }
                        case 143: {
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 142;
                                continue;
                            }
                            continue;
                        }
                        case 144: {
                            if (this.curChar == 'e') {
                                this.jjstateSet[this.jjnewStateCnt++] = 143;
                                continue;
                            }
                            continue;
                        }
                        case 145: {
                            if (this.curChar == 'L') {
                                this.jjstateSet[this.jjnewStateCnt++] = 144;
                                continue;
                            }
                            continue;
                        }
                        case 146: {
                            if (this.curChar == 'x') {
                                this.jjstateSet[this.jjnewStateCnt++] = 145;
                                continue;
                            }
                            continue;
                        }
                        case 147: {
                            if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 146;
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
            else {
                final int hiByte = this.curChar >> 8;
                final int i2 = hiByte >> 6;
                final long l2 = 1L << (hiByte & 0x3F);
                final int i3 = (this.curChar & '\u00ff') >> 6;
                final long l3 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 1:
                        case 34: {
                            if (!jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(1);
                            continue;
                        }
                        case 91: {
                            if (!jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(1);
                            continue;
                        }
                        case 16: {
                            if (!jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(1);
                            continue;
                        }
                        case 55: {
                            if (!jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(1);
                            continue;
                        }
                        case 68: {
                            if (!jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(1);
                            continue;
                        }
                        case 103: {
                            if (!jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(1);
                            continue;
                        }
                        case 0: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(1);
                            continue;
                        }
                        case 81: {
                            if (!jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(1);
                            continue;
                        }
                        case 23: {
                            if (!jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(1);
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
            final int n2 = 148;
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
                return (SCDParserTokenManager.jjbitVec2[i2] & l2) != 0x0L;
            }
            case 1: {
                return (SCDParserTokenManager.jjbitVec3[i2] & l2) != 0x0L;
            }
            case 2: {
                return (SCDParserTokenManager.jjbitVec4[i2] & l2) != 0x0L;
            }
            case 3: {
                return (SCDParserTokenManager.jjbitVec5[i2] & l2) != 0x0L;
            }
            case 4: {
                return (SCDParserTokenManager.jjbitVec6[i2] & l2) != 0x0L;
            }
            case 5: {
                return (SCDParserTokenManager.jjbitVec7[i2] & l2) != 0x0L;
            }
            case 6: {
                return (SCDParserTokenManager.jjbitVec8[i2] & l2) != 0x0L;
            }
            case 9: {
                return (SCDParserTokenManager.jjbitVec9[i2] & l2) != 0x0L;
            }
            case 10: {
                return (SCDParserTokenManager.jjbitVec10[i2] & l2) != 0x0L;
            }
            case 11: {
                return (SCDParserTokenManager.jjbitVec11[i2] & l2) != 0x0L;
            }
            case 12: {
                return (SCDParserTokenManager.jjbitVec12[i2] & l2) != 0x0L;
            }
            case 13: {
                return (SCDParserTokenManager.jjbitVec13[i2] & l2) != 0x0L;
            }
            case 14: {
                return (SCDParserTokenManager.jjbitVec14[i2] & l2) != 0x0L;
            }
            case 15: {
                return (SCDParserTokenManager.jjbitVec15[i2] & l2) != 0x0L;
            }
            case 16: {
                return (SCDParserTokenManager.jjbitVec16[i2] & l2) != 0x0L;
            }
            case 17: {
                return (SCDParserTokenManager.jjbitVec17[i2] & l2) != 0x0L;
            }
            case 30: {
                return (SCDParserTokenManager.jjbitVec18[i2] & l2) != 0x0L;
            }
            case 31: {
                return (SCDParserTokenManager.jjbitVec19[i2] & l2) != 0x0L;
            }
            case 33: {
                return (SCDParserTokenManager.jjbitVec20[i2] & l2) != 0x0L;
            }
            case 48: {
                return (SCDParserTokenManager.jjbitVec21[i2] & l2) != 0x0L;
            }
            case 49: {
                return (SCDParserTokenManager.jjbitVec22[i2] & l2) != 0x0L;
            }
            case 159: {
                return (SCDParserTokenManager.jjbitVec23[i2] & l2) != 0x0L;
            }
            case 215: {
                return (SCDParserTokenManager.jjbitVec24[i2] & l2) != 0x0L;
            }
            default: {
                return (SCDParserTokenManager.jjbitVec0[i1] & l1) != 0x0L;
            }
        }
    }
    
    private static final boolean jjCanMove_1(final int hiByte, final int i1, final int i2, final long l1, final long l2) {
        switch (hiByte) {
            case 0: {
                return (SCDParserTokenManager.jjbitVec25[i2] & l2) != 0x0L;
            }
            case 1: {
                return (SCDParserTokenManager.jjbitVec3[i2] & l2) != 0x0L;
            }
            case 2: {
                return (SCDParserTokenManager.jjbitVec26[i2] & l2) != 0x0L;
            }
            case 3: {
                return (SCDParserTokenManager.jjbitVec27[i2] & l2) != 0x0L;
            }
            case 4: {
                return (SCDParserTokenManager.jjbitVec28[i2] & l2) != 0x0L;
            }
            case 5: {
                return (SCDParserTokenManager.jjbitVec29[i2] & l2) != 0x0L;
            }
            case 6: {
                return (SCDParserTokenManager.jjbitVec30[i2] & l2) != 0x0L;
            }
            case 9: {
                return (SCDParserTokenManager.jjbitVec31[i2] & l2) != 0x0L;
            }
            case 10: {
                return (SCDParserTokenManager.jjbitVec32[i2] & l2) != 0x0L;
            }
            case 11: {
                return (SCDParserTokenManager.jjbitVec33[i2] & l2) != 0x0L;
            }
            case 12: {
                return (SCDParserTokenManager.jjbitVec34[i2] & l2) != 0x0L;
            }
            case 13: {
                return (SCDParserTokenManager.jjbitVec35[i2] & l2) != 0x0L;
            }
            case 14: {
                return (SCDParserTokenManager.jjbitVec36[i2] & l2) != 0x0L;
            }
            case 15: {
                return (SCDParserTokenManager.jjbitVec37[i2] & l2) != 0x0L;
            }
            case 16: {
                return (SCDParserTokenManager.jjbitVec16[i2] & l2) != 0x0L;
            }
            case 17: {
                return (SCDParserTokenManager.jjbitVec17[i2] & l2) != 0x0L;
            }
            case 30: {
                return (SCDParserTokenManager.jjbitVec18[i2] & l2) != 0x0L;
            }
            case 31: {
                return (SCDParserTokenManager.jjbitVec19[i2] & l2) != 0x0L;
            }
            case 32: {
                return (SCDParserTokenManager.jjbitVec38[i2] & l2) != 0x0L;
            }
            case 33: {
                return (SCDParserTokenManager.jjbitVec20[i2] & l2) != 0x0L;
            }
            case 48: {
                return (SCDParserTokenManager.jjbitVec39[i2] & l2) != 0x0L;
            }
            case 49: {
                return (SCDParserTokenManager.jjbitVec22[i2] & l2) != 0x0L;
            }
            case 159: {
                return (SCDParserTokenManager.jjbitVec23[i2] & l2) != 0x0L;
            }
            case 215: {
                return (SCDParserTokenManager.jjbitVec24[i2] & l2) != 0x0L;
            }
            default: {
                return (SCDParserTokenManager.jjbitVec0[i1] & l1) != 0x0L;
            }
        }
    }
    
    public SCDParserTokenManager(final SimpleCharStream stream) {
        this.debugStream = System.out;
        this.jjrounds = new int[148];
        this.jjstateSet = new int[296];
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.input_stream = stream;
    }
    
    public SCDParserTokenManager(final SimpleCharStream stream, final int lexState) {
        this(stream);
        this.SwitchTo(lexState);
    }
    
    public void ReInit(final SimpleCharStream stream) {
        final boolean b = false;
        this.jjnewStateCnt = (b ? 1 : 0);
        this.jjmatchedPos = (b ? 1 : 0);
        this.curLexState = this.defaultLexState;
        this.input_stream = stream;
        this.ReInitRounds();
    }
    
    private final void ReInitRounds() {
        this.jjround = -2147483647;
        int i = 148;
        while (i-- > 0) {
            this.jjrounds[i] = Integer.MIN_VALUE;
        }
    }
    
    public void ReInit(final SimpleCharStream stream, final int lexState) {
        this.ReInit(stream);
        this.SwitchTo(lexState);
    }
    
    public void SwitchTo(final int lexState) {
        if (lexState >= 1 || lexState < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
        }
        this.curLexState = lexState;
    }
    
    protected Token jjFillToken() {
        final Token t = Token.newToken(this.jjmatchedKind);
        t.kind = this.jjmatchedKind;
        final String im = SCDParserTokenManager.jjstrLiteralImages[this.jjmatchedKind];
        t.image = ((im == null) ? this.input_stream.GetImage() : im);
        t.beginLine = this.input_stream.getBeginLine();
        t.beginColumn = this.input_stream.getBeginColumn();
        t.endLine = this.input_stream.getEndLine();
        t.endColumn = this.input_stream.getEndColumn();
        return t;
    }
    
    public Token getNextToken() {
        final Token specialToken = null;
        int curPos = 0;
        while (true) {
            try {
                this.curChar = this.input_stream.BeginToken();
            }
            catch (IOException e) {
                this.jjmatchedKind = 0;
                final Token matchedToken = this.jjFillToken();
                return matchedToken;
            }
            try {
                this.input_stream.backup(0);
                while (this.curChar <= ' ' && (0x100003600L & 1L << this.curChar) != 0x0L) {
                    this.curChar = this.input_stream.BeginToken();
                }
            }
            catch (IOException e2) {
                continue;
            }
            this.jjmatchedKind = Integer.MAX_VALUE;
            this.jjmatchedPos = 0;
            curPos = this.jjMoveStringLiteralDfa0_0();
            if (this.jjmatchedKind == Integer.MAX_VALUE) {
                int error_line = this.input_stream.getEndLine();
                int error_column = this.input_stream.getEndColumn();
                String error_after = null;
                boolean EOFSeen = false;
                try {
                    this.input_stream.readChar();
                    this.input_stream.backup(1);
                }
                catch (IOException e3) {
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
            if ((SCDParserTokenManager.jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0x0L) {
                final Token matchedToken = this.jjFillToken();
                return matchedToken;
            }
        }
    }
    
    static {
        jjbitVec0 = new long[] { 0L, -16384L, -17590038560769L, 8388607L };
        jjbitVec2 = new long[] { 0L, 0L, 0L, -36028797027352577L };
        jjbitVec3 = new long[] { 9219994337134247935L, 9223372036854775294L, -1L, -274156627316187121L };
        jjbitVec4 = new long[] { 16777215L, -65536L, -576458553280167937L, 3L };
        jjbitVec5 = new long[] { 0L, 0L, -17179879616L, 4503588160110591L };
        jjbitVec6 = new long[] { -8194L, -536936449L, -65533L, 234134404065073567L };
        jjbitVec7 = new long[] { -562949953421312L, -8547991553L, 127L, 1979120929931264L };
        jjbitVec8 = new long[] { 576460743713488896L, -562949953419266L, 9007199254740991999L, 412319973375L };
        jjbitVec9 = new long[] { 2594073385365405664L, 17163091968L, 271902628478820320L, 844440767823872L };
        jjbitVec10 = new long[] { 247132830528276448L, 7881300924956672L, 2589004636761075680L, 4294967296L };
        jjbitVec11 = new long[] { 2579997437506199520L, 15837691904L, 270153412153034720L, 0L };
        jjbitVec12 = new long[] { 283724577500946400L, 12884901888L, 283724577500946400L, 13958643712L };
        jjbitVec13 = new long[] { 288228177128316896L, 12884901888L, 0L, 0L };
        jjbitVec14 = new long[] { 3799912185593854L, 63L, 2309621682768192918L, 31L };
        jjbitVec15 = new long[] { 0L, 4398046510847L, 0L, 0L };
        jjbitVec16 = new long[] { 0L, 0L, -4294967296L, 36028797018898495L };
        jjbitVec17 = new long[] { 5764607523034749677L, 12493387738468353L, -756383734487318528L, 144405459145588743L };
        jjbitVec18 = new long[] { -1L, -1L, -4026531841L, 288230376151711743L };
        jjbitVec19 = new long[] { -3233808385L, 4611686017001275199L, 6908521828386340863L, 2295745090394464220L };
        jjbitVec20 = new long[] { 83837761617920L, 0L, 7L, 0L };
        jjbitVec21 = new long[] { 4389456576640L, -2L, -8587837441L, 576460752303423487L };
        jjbitVec22 = new long[] { 35184372088800L, 0L, 0L, 0L };
        jjbitVec23 = new long[] { -1L, -1L, 274877906943L, 0L };
        jjbitVec24 = new long[] { -1L, -1L, 68719476735L, 0L };
        jjbitVec25 = new long[] { 0L, 0L, 36028797018963968L, -36028797027352577L };
        jjbitVec26 = new long[] { 16777215L, -65536L, -576458553280167937L, 196611L };
        jjbitVec27 = new long[] { -1L, 12884901951L, -17179879488L, 4503588160110591L };
        jjbitVec28 = new long[] { -8194L, -536936449L, -65413L, 234134404065073567L };
        jjbitVec29 = new long[] { -562949953421312L, -8547991553L, -4899916411759099777L, 1979120929931286L };
        jjbitVec30 = new long[] { 576460743713488896L, -277081224642561L, 9007199254740991999L, 288017070894841855L };
        jjbitVec31 = new long[] { -864691128455135250L, 281268803485695L, -3186861885341720594L, 1125692414638495L };
        jjbitVec32 = new long[] { -3211631683292264476L, 9006925953907079L, -869759877059465234L, 281204393786303L };
        jjbitVec33 = new long[] { -878767076314341394L, 281215949093263L, -4341532606274353172L, 280925229301191L };
        jjbitVec34 = new long[] { -4327961440926441490L, 281212990012895L, -4327961440926441492L, 281214063754719L };
        jjbitVec35 = new long[] { -4323457841299070996L, 281212992110031L, 0L, 0L };
        jjbitVec36 = new long[] { 576320014815068158L, 67076095L, 4323293666156225942L, 67059551L };
        jjbitVec37 = new long[] { -4422530440275951616L, -558551906910465L, 215680200883507167L, 0L };
        jjbitVec38 = new long[] { 0L, 0L, 0L, 9126739968L };
        jjbitVec39 = new long[] { 17732914942836896L, -2L, -6876561409L, 8646911284551352319L };
        jjnextStates = new int[] { 3, 4, 103, 113, 123, 133, 140, 147 };
        jjstrLiteralImages = new String[] { "", null, null, null, null, null, null, null, null, null, null, null, null, null, null, ":", "/", "//", "attribute::", "@", "element::", "substitutionGroup::", "type::", "~", "baseType::", "primitiveType::", "itemType::", "memberType::", "scope::", "attributeGroup::", "group::", "identityContraint::", "key::", "notation::", "model::sequence", "model::choice", "model::all", "model::*", "any::*", "anyAttribute::*", "facet::*", "facet::", "component::*", "x-schema::", "x-schema::*", "*", "0" };
        lexStateNames = new String[] { "DEFAULT" };
        jjtoToken = new long[] { 140737488351233L };
        jjtoSkip = new long[] { 62L };
    }
}
