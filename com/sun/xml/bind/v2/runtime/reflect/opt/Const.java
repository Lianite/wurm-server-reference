// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.reflect.opt;

final class Const
{
    static byte default_value_byte;
    static boolean default_value_boolean;
    static char default_value_char;
    static float default_value_float;
    static double default_value_double;
    static int default_value_int;
    static long default_value_long;
    static short default_value_short;
    
    static {
        Const.default_value_byte = 0;
        Const.default_value_boolean = false;
        Const.default_value_char = '\0';
        Const.default_value_float = 0.0f;
        Const.default_value_double = 0.0;
        Const.default_value_int = 0;
        Const.default_value_long = 0L;
        Const.default_value_short = 0;
    }
}
