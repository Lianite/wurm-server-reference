// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.shared.constants;

import java.util.logging.Logger;

public enum StructureStateEnum
{
    UNINITIALIZED((byte)0), 
    INITIALIZED((byte)1), 
    STATE_2_NEEDED((byte)2), 
    STATE_3_NEEDED((byte)3), 
    STATE_4_NEEDED((byte)4), 
    STATE_5_NEEDED((byte)5), 
    STATE_6_NEEDED((byte)6), 
    STATE_7_NEEDED((byte)7), 
    STATE_8_NEEDED((byte)8), 
    STATE_9_NEEDED((byte)9), 
    STATE_10_NEEDED((byte)10), 
    STATE_11_NEEDED((byte)11), 
    STATE_12_NEEDED((byte)12), 
    STATE_13_NEEDED((byte)13), 
    STATE_14_NEEDED((byte)14), 
    STATE_15_NEEDED((byte)15), 
    STATE_16_NEEDED((byte)16), 
    STATE_17_NEEDED((byte)17), 
    STATE_18_NEEDED((byte)18), 
    STATE_19_NEEDED((byte)19), 
    STATE_20_NEEDED((byte)20), 
    STATE_21_NEEDED((byte)21), 
    STATE_22_NEEDED((byte)22), 
    STATE_23_NEEDED((byte)23), 
    STATE_24_NEEDED((byte)24), 
    STATE_25_NEEDED((byte)25), 
    STATE_26_NEEDED((byte)26), 
    STATE_27_NEEDED((byte)27), 
    STATE_28_NEEDED((byte)28), 
    STATE_29_NEEDED((byte)29), 
    STATE_30_NEEDED((byte)30), 
    STATE_31_NEEDED((byte)31), 
    STATE_32_NEEDED((byte)32), 
    STATE_33_NEEDED((byte)33), 
    STATE_34_NEEDED((byte)34), 
    STATE_35_NEEDED((byte)35), 
    STATE_36_NEEDED((byte)36), 
    STATE_37_NEEDED((byte)37), 
    STATE_38_NEEDED((byte)38), 
    STATE_39_NEEDED((byte)39), 
    STATE_40_NEEDED((byte)40), 
    STATE_41_NEEDED((byte)41), 
    STATE_42_NEEDED((byte)42), 
    STATE_43_NEEDED((byte)43), 
    STATE_44_NEEDED((byte)44), 
    STATE_45_NEEDED((byte)45), 
    STATE_46_NEEDED((byte)46), 
    STATE_47_NEEDED((byte)47), 
    STATE_48_NEEDED((byte)48), 
    STATE_49_NEEDED((byte)49), 
    STATE_50_NEEDED((byte)50), 
    STATE_51_NEEDED((byte)51), 
    STATE_52_NEEDED((byte)52), 
    STATE_53_NEEDED((byte)53), 
    STATE_54_NEEDED((byte)54), 
    STATE_55_NEEDED((byte)55), 
    STATE_56_NEEDED((byte)56), 
    STATE_57_NEEDED((byte)57), 
    STATE_58_NEEDED((byte)58), 
    STATE_59_NEEDED((byte)59), 
    STATE_60_NEEDED((byte)60), 
    STATE_61_NEEDED((byte)61), 
    STATE_62_NEEDED((byte)62), 
    STATE_63_NEEDED((byte)63), 
    STATE_64_NEEDED((byte)64), 
    STATE_65_NEEDED((byte)65), 
    STATE_66_NEEDED((byte)66), 
    STATE_67_NEEDED((byte)67), 
    STATE_68_NEEDED((byte)68), 
    STATE_69_NEEDED((byte)69), 
    STATE_70_NEEDED((byte)70), 
    STATE_71_NEEDED((byte)71), 
    STATE_72_NEEDED((byte)72), 
    STATE_73_NEEDED((byte)73), 
    STATE_74_NEEDED((byte)74), 
    STATE_75_NEEDED((byte)75), 
    STATE_76_NEEDED((byte)76), 
    STATE_77_NEEDED((byte)77), 
    STATE_78_NEEDED((byte)78), 
    STATE_79_NEEDED((byte)79), 
    STATE_80_NEEDED((byte)80), 
    STATE_81_NEEDED((byte)81), 
    STATE_82_NEEDED((byte)82), 
    STATE_83_NEEDED((byte)83), 
    STATE_84_NEEDED((byte)84), 
    STATE_85_NEEDED((byte)85), 
    STATE_86_NEEDED((byte)86), 
    STATE_87_NEEDED((byte)87), 
    STATE_88_NEEDED((byte)88), 
    STATE_89_NEEDED((byte)89), 
    STATE_90_NEEDED((byte)90), 
    STATE_91_NEEDED((byte)91), 
    STATE_92_NEEDED((byte)92), 
    STATE_93_NEEDED((byte)93), 
    STATE_94_NEEDED((byte)94), 
    STATE_95_NEEDED((byte)95), 
    STATE_96_NEEDED((byte)96), 
    STATE_97_NEEDED((byte)97), 
    STATE_98_NEEDED((byte)98), 
    STATE_99_NEEDED((byte)99), 
    STATE_100_NEEDED((byte)100), 
    STATE_101_NEEDED((byte)101), 
    STATE_102_NEEDED((byte)102), 
    STATE_103_NEEDED((byte)103), 
    STATE_104_NEEDED((byte)104), 
    STATE_105_NEEDED((byte)105), 
    STATE_106_NEEDED((byte)106), 
    STATE_107_NEEDED((byte)107), 
    STATE_108_NEEDED((byte)108), 
    STATE_109_NEEDED((byte)109), 
    STATE_110_NEEDED((byte)110), 
    STATE_111_NEEDED((byte)111), 
    STATE_112_NEEDED((byte)112), 
    STATE_113_NEEDED((byte)113), 
    STATE_114_NEEDED((byte)114), 
    STATE_115_NEEDED((byte)115), 
    STATE_116_NEEDED((byte)116), 
    STATE_117_NEEDED((byte)117), 
    STATE_118_NEEDED((byte)118), 
    STATE_119_NEEDED((byte)119), 
    STATE_120_NEEDED((byte)120), 
    STATE_121_NEEDED((byte)121), 
    STATE_122_NEEDED((byte)122), 
    STATE_123_NEEDED((byte)123), 
    STATE_124_NEEDED((byte)124), 
    STATE_125_NEEDED((byte)125), 
    STATE_126_NEEDED((byte)126), 
    FINISHED((byte)127), 
    WALL_PLAN((byte)0);
    
    public final byte state;
    private static final Logger logger;
    
    private StructureStateEnum(final byte value) {
        this.state = value;
    }
    
    public static StructureStateEnum getStateByValue(final byte value) {
        if (value >= 0 && value < values().length) {
            return values()[value];
        }
        StructureStateEnum.logger.warning("Value not a valid state: " + value + " RETURNING PLAN(VAL=0)!");
        return StructureStateEnum.WALL_PLAN;
    }
    
    static {
        logger = Logger.getLogger(StructureStateEnum.class.getName());
    }
}
