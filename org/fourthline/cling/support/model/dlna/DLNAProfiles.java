// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna;

public enum DLNAProfiles
{
    NONE("", ""), 
    JPEG_SM("JPEG_SM", "image/jpeg"), 
    JPEG_MED("JPEG_MED", "image/jpeg"), 
    JPEG_LRG("JPEG_LRG", "image/jpeg"), 
    JPEG_TN("JPEG_TN", "image/jpeg"), 
    JPEG_SM_ICO("JPEG_SM_ICO", "image/jpeg"), 
    JPEG_LRG_ICO("JPEG_LRG_ICO", "image/jpeg"), 
    PNG_TN("PNG_TN", "image/png"), 
    PNG_SM_ICO("PNG_SM_ICO", "image/png"), 
    PNG_LRG_ICO("PNG_LRG_ICO", "image/png"), 
    PNG_LRG("PNG_LRG", "image/png"), 
    LPCM("LPCM", "audio/L16"), 
    LPCM_LOW("LPCM_low", "audio/L16"), 
    MP3("MP3", "audio/mpeg"), 
    MP3X("MP3X", "audio/mpeg"), 
    WMABASE("WMABASE", "audio/x-ms-wma"), 
    WMAFULL("WMAFULL", "audio/x-ms-wma"), 
    WMAPRO("WMAPRO", "audio/x-ms-wma"), 
    AAC_ADTS("AAC_ADTS", "audio/vnd.dlna.adts"), 
    AAC_ADTS_320("AAC_ADTS_320", "audio/vnd.dlna.adts"), 
    AAC_ISO("AAC_ISO", "audio/mp4"), 
    AAC_ISO_320("AAC_ISO_320", "audio/mp4"), 
    AAC_LTP_ISO("AAC_LTP_ISO", "audio/mp4"), 
    AAC_LTP_MULT5_ISO("AAC_LTP_MULT5_ISO", "audio/mp4"), 
    AAC_LTP_MULT7_ISO("AAC_LTP_MULT7_ISO", "audio/mp4"), 
    AAC_MULT5_ADTS("AAC_MULT5_ADTS", "audio/vnd.dlna.adts"), 
    AAC_MULT5_ISO("AAC_MULT5_ISO", "audio/mp4"), 
    HEAAC_L2_ADTS("HEAAC_L2_ADTS", "audio/vnd.dlna.adts"), 
    HEAAC_L2_ISO("HEAAC_L2_ISO", "audio/mp4"), 
    HEAAC_L3_ADTS("HEAAC_L3_ADTS", "audio/vnd.dlna.adts"), 
    HEAAC_L3_ISO("HEAAC_L3_ISO", "audio/mp4"), 
    HEAAC_MULT5_ADTS("HEAAC_MULT5_ADTS", "audio/vnd.dlna.adts"), 
    HEAAC_MULT5_ISO("HEAAC_MULT5_ISO", "audio/mp4"), 
    HEAAC_L2_ADTS_320("HEAAC_L2_ADTS_320", "audio/vnd.dlna.adts"), 
    HEAAC_L2_ISO_320("HEAAC_L2_ISO_320", "audio/mp4"), 
    BSAC_ISO("BSAC_ISO", "audio/mp4"), 
    BSAC_MULT5_ISO("BSAC_MULT5_ISO", "audio/mp4"), 
    HEAACv2_L2("HEAACv2_L2", "audio/mp4"), 
    HEAACv2_L2_ADTS("HEAACv2_L2", "audio/vnd.dlna.adts"), 
    HEAACv2_L2_320("HEAACv2_L2_320", "audio/mp4"), 
    HEAACv2_L2_320_ADTS("HEAACv2_L2_320", "audio/vnd.dlna.adts"), 
    HEAACv2_L3("HEAACv2_L3", "audio/mp4"), 
    HEAACv2_L3_ADTS("HEAACv2_L3", "vnd.dlna.adts"), 
    HEAACv2_MULT5("HEAACv2_MULT5", "audio/mp4"), 
    HEAACv2_MULT5_ADTS("HEAACv2_MULT5", "vnd.dlna.adts"), 
    AC3("AC3", "audio/vnd.dolby.dd-raw"), 
    AMR("AMR_3GPP", "audio/mp4"), 
    THREE_GPP("AMR_3GPP", "audio/3gpp"), 
    AMR_WBplus("AMR_WBplus", "audio/3gpp"), 
    ATRAC3("ATRAC3plus", "audio/x-sony-oma"), 
    WMVMED_BASE("WMVMED_BASE", "video/x-ms-wmv"), 
    WMVMED_FULL("WMVMED_FULL", "video/x-ms-wmv"), 
    WMVMED_PRO("WMVMED_PRO", "video/x-ms-wmv"), 
    WMVHIGH_FULL("WMVHIGH_FULL", "video/x-ms-wmv"), 
    WMVHIGH_PRO("WMVHIGH_PRO", "video/x-ms-wmv"), 
    WMVHM_BASE("WMVHM_BASE", "video/x-ms-wmv"), 
    WMVSPLL_BASE("WMVSPLL_BASE", "video/x-ms-wmv"), 
    WMVSPML_BASE("WMVSPML_BASE", "video/x-ms-wmv"), 
    WMVSPML_MP3("WMVSPML_MP3", "video/x-ms-wmv"), 
    MPEG1("MPEG1", "video/mpeg"), 
    MPEG_PS_NTSC("MPEG_PS_NTSC", "video/mpeg"), 
    MPEG_PS_NTSC_XAC3("MPEG_PS_NTSC_XAC3", "video/mpeg"), 
    MPEG_PS_PAL("MPEG_PS_PAL", "video/mpeg"), 
    MPEG_PS_PAL_XAC3("MPEG_PS_PAL_XAC3", "video/mpeg"), 
    MPEG_TS_MP_LL_AAC("MPEG_TS_MP_LL_AAC", "video/vnd.dlna.mpeg-tts"), 
    MPEG_TS_MP_LL_AAC_T("MPEG_TS_MP_LL_AAC_T", "video/vnd.dlna.mpeg-tts"), 
    MPEG_TS_MP_LL_AAC_ISO("MPEG_TS_MP_LL_AAC_ISO", "video/mpeg"), 
    MPEG_TS_SD_50_L2_T("MPEG_TS_SD_50_L2_T", "video/vnd.dlna.mpeg-tts"), 
    MPEG_TS_SD_60_L2_T("MPEG_TS_SD_60_L2_T", "video/vnd.dlna.mpeg-tts"), 
    MPEG_TS_SD_60_AC3_T("MPEG_TS_SD_60_AC3_T", "video/vnd.dlna.mpeg-tts"), 
    MPEG_TS_SD_EU("MPEG_TS_SD_EU", "video/vnd.dlna.mpeg-tts"), 
    MPEG_TS_SD_EU_T("MPEG_TS_SD_EU_T", "video/vnd.dlna.mpeg-tts"), 
    MPEG_TS_SD_EU_ISO("MPEG_TS_SD_EU_ISO", "video/mpeg"), 
    MPEG_TS_SD_50_AC3_T("MPEG_TS_SD_50_AC3_T", "video/vnd.dlna.mpeg-tts"), 
    MPEG_TS_SD_NA("MPEG_TS_SD_NA", "video/vnd.dlna.mpeg-tts"), 
    MPEG_TS_SD_NA_T("MPEG_TS_SD_NA_T", "video/vnd.dlna.mpeg-tts"), 
    MPEG_TS_SD_NA_ISO("MPEG_TS_SD_NA_ISO", "video/mpeg"), 
    MPEG_TS_SD_NA_XAC3("MPEG_TS_SD_NA_XAC3", "video/vnd.dlna.mpeg-tts"), 
    MPEG_TS_SD_NA_XAC3_T("MPEG_TS_SD_NA_XAC3_T", "video/vnd.dlna.mpeg-tts"), 
    MPEG_TS_SD_NA_XAC3_ISO("MPEG_TS_SD_NA_XAC3_ISO", "video/mpeg"), 
    MPEG_TS_HD_NA("MPEG_TS_HD_NA", "video/vnd.dlna.mpeg-tts"), 
    MPEG_TS_HD_NA_T("MPEG_TS_HD_NA_T", "video/vnd.dlna.mpeg-tts"), 
    MPEG_TS_HD_50_L2_T("MPEG_TS_HD_50_L2_T", "video/vnd.dlna.mpeg-tts"), 
    MPEG_TS_HD_50_L2_ISO("MPEG_TS_HD_50_L2_ISO", "video/mpeg"), 
    MPEG_TS_HD_60_L2_T("MPEG_TS_HD_60_L2_T", "video/vnd.dlna.mpeg-tts"), 
    MPEG_TS_HD_60_L2_ISO("MPEG_TS_HD_60_L2_ISO", "video/mpeg"), 
    MPEG_TS_HD_NA_ISO("MPEG_TS_HD_NA_ISO", "video/mpeg"), 
    MPEG_TS_HD_NA_XAC3("MPEG_TS_HD_NA_XAC3", "video/vnd.dlna.mpeg-tts"), 
    MPEG_TS_HD_NA_XAC3_T("MPEG_TS_HD_NA_XAC3_T", "video/vnd.dlna.mpeg-tts"), 
    MPEG_TS_HD_NA_XAC3_ISO("MPEG_TS_HD_NA_XAC3_ISO", "video/mpeg"), 
    MPEG_ES_PAL("MPEG_ES_PAL", "video/mpeg"), 
    MPEG_ES_NTSC("MPEG_ES_NTSC", "video/mpeg"), 
    MPEG_ES_PAL_XAC3("MPEG_ES_PAL_XAC3", "video/mpeg"), 
    MPEG_ES_NTSC_XAC3("MPEG_ES_NTSC_XAC3", "video/mpeg"), 
    MPEG4_P2_MP4_SP_AAC("MPEG4_P2_MP4_SP_AAC", "video/mp4"), 
    MPEG4_P2_MP4_SP_HEAAC("MPEG4_P2_MP4_SP_HEAAC", "video/mp4"), 
    MPEG4_P2_MP4_SP_ATRAC3plus("MPEG4_P2_MP4_SP_ATRAC3plus", "video/mp4"), 
    MPEG4_P2_MP4_SP_AAC_LTP("MPEG4_P2_MP4_SP_AAC_LTP", "video/mp4"), 
    MPEG4_P2_MP4_SP_L2_AAC("MPEG4_P2_MP4_SP_L2_AAC", "video/mp4"), 
    MPEG4_P2_MP4_SP_L2_AMR("MPEG4_P2_MP4_SP_L2_AMR", "video/mp4"), 
    MPEG4_P2_MP4_SP_VGA_AAC("MPEG4_P2_MP4_SP_VGA_AAC", "video/mp4"), 
    MPEG4_P2_MP4_SP_VGA_HEAAC("MPEG4_P2_MP4_SP_VGA_HEAAC", "video/mp4"), 
    MPEG4_P2_MP4_ASP_AAC("MPEG4_P2_MP4_ASP_AAC", "video/mp4"), 
    MPEG4_P2_MP4_ASP_HEAAC("MPEG4_P2_MP4_ASP_HEAAC", "video/mp4"), 
    MPEG4_P2_MP4_ASP_HEAAC_MULT5("MPEG4_P2_MP4_ASP_HEAAC_MULT5", "video/mp4"), 
    MPEG4_P2_MP4_ASP_ATRAC3plus("MPEG4_P2_MP4_ASP_ATRAC3plus", "video/mp4"), 
    MPEG4_P2_MP4_ASP_L5_SO_AAC("MPEG4_P2_MP4_ASP_L5_SO_AAC", "video/mp4"), 
    MPEG4_P2_MP4_ASP_L5_SO_HEAAC("MPEG4_P2_MP4_ASP_L5_SO_HEAAC", "video/mp4"), 
    MPEG4_P2_MP4_ASP_L5_SO_HEAAC_MULT5("MPEG4_P2_MP4_ASP_L5_SO_HEAAC_MULT5", "video/mp4"), 
    MPEG4_P2_MP4_ASP_L4_SO_AAC("MPEG4_P2_MP4_ASP_L4_SO_AAC", "video/mp4"), 
    MPEG4_P2_MP4_ASP_L4_SO_HEAAC("MPEG4_P2_MP4_ASP_L4_SO_HEAAC", "video/mp4"), 
    MPEG4_P2_MP4_ASP_L4_SO_HEAAC_MULT5("MPEG4_P2_MP4_ASP_L4_SO_HEAAC_MULT5", "video/mp4"), 
    MPEG4_H263_MP4_P0_L10_AAC("MPEG4_H263_MP4_P0_L10_AAC", "video/3gpp"), 
    MPEG4_H263_MP4_P0_L10_AAC_LTP("MPEG4_H263_MP4_P0_L10_AAC_LTP", "video/3gpp"), 
    MPEG4_P2_TS_SP_AAC("MPEG4_P2_TS_SP_AAC", "video/vnd.dlna.mpeg-tts"), 
    MPEG4_P2_TS_SP_AAC_T("MPEG4_P2_TS_SP_AAC_T", "video/vnd.dlna.mpeg-tts"), 
    MPEG4_P2_TS_SP_AAC_ISO("MPEG4_P2_TS_SP_AAC_ISO", "video/mpeg"), 
    MPEG4_P2_TS_SP_MPEG1_L3("MPEG4_P2_TS_SP_MPEG1_L3", "video/vnd.dlna.mpeg-tts"), 
    MPEG4_P2_TS_SP_MPEG1_L3_T("MPEG4_P2_TS_SP_MPEG1_L3_T", "video/vnd.dlna.mpeg-tts"), 
    MPEG4_P2_TS_SP_MPEG1_L3_ISO("MPEG4_P2_TS_SP_MPEG1_L3_ISO", "video/mpeg"), 
    MPEG4_P2_TS_SP_AC3_L3("MPEG4_P2_TS_SP_AC3_L3", "video/vnd.dlna.mpeg-tts"), 
    MPEG4_P2_TS_SP_AC3_T("MPEG4_P2_TS_SP_AC3_T", "video/vnd.dlna.mpeg-tts"), 
    MPEG4_P2_TS_SP_AC3_ISO("MPEG4_P2_TS_SP_AC3_ISO", "video/mpeg"), 
    MPEG4_P2_TS_SP_MPEG2_L2("MPEG4_P2_TS_SP_MPEG2_L2", "video/vnd.dlna.mpeg-tts"), 
    MPEG4_P2_TS_SP_MPEG2_L2_T("MPEG4_P2_TS_SP_MPEG2_L2_T", "video/vnd.dlna.mpeg-tts"), 
    MPEG4_P2_TS_SP_MPEG2_L2_ISO("MPEG4_P2_TS_SP_MPEG2_L2_ISO", "video/mpeg"), 
    MPEG4_P2_TS_ASP_AAC("MPEG4_P2_TS_ASP_AAC", "video/vnd.dlna.mpeg-tts"), 
    MPEG4_P2_TS_ASP_AAC_T("MPEG4_P2_TS_ASP_AAC_T", "video/vnd.dlna.mpeg-tts"), 
    MPEG4_P2_TS_ASP_AAC_ISO("MPEG4_P2_TS_ASP_AAC_ISO", "video/mpeg"), 
    MPEG4_P2_TS_ASP_MPEG1_L3("MPEG4_P2_TS_ASP_MPEG1_L3", "video/vnd.dlna.mpeg-tts"), 
    MPEG4_P2_TS_ASP_MPEG1_L3_T("MPEG4_P2_TS_ASP_MPEG1_L3_T", "video/vnd.dlna.mpeg-tts"), 
    MPEG4_P2_TS_ASP_MPEG1_L3_ISO("MPEG4_P2_TS_ASP_MPEG1_L3_ISO", "video/mpeg"), 
    MPEG4_P2_TS_ASP_AC3_L3("MPEG4_P2_TS_ASP_AC3_L3", "video/vnd.dlna.mpeg-tts"), 
    MPEG4_P2_TS_ASP_AC3_T("MPEG4_P2_TS_ASP_AC3_T", "video/vnd.dlna.mpeg-tts"), 
    MPEG4_P2_TS_ASP_AC3_ISO("MPEG4_P2_TS_ASP_AC3_ISO", "video/mpeg"), 
    MPEG4_P2_TS_CO_AC3("MPEG4_P2_TS_CO_AC3", "video/vnd.dlna.mpeg-tts"), 
    MPEG4_P2_TS_CO_AC3_T("MPEG4_P2_TS_CO_AC3_T", "video/vnd.dlna.mpeg-tts"), 
    MPEG4_P2_TS_CO_AC3_ISO("MPEG4_P2_TS_CO_AC3_ISO", "video/mpeg"), 
    MPEG4_P2_TS_CO_MPEG2_L2("MPEG4_P2_TS_CO_MPEG2_L2", "video/vnd.dlna.mpeg-tts"), 
    MPEG4_P2_TS_CO_MPEG2_L2_T("MPEG4_P2_TS_CO_MPEG2_L2_T", "video/vnd.dlna.mpeg-tts"), 
    MPEG4_P2_TS_CO_MPEG2_L2_ISO("MPEG4_P2_TS_CO_MPEG2_L2_ISO", "video/mpeg"), 
    MPEG4_P2_ASF_SP_G726("MPEG4_P2_ASF_SP_G726", "video/x-ms-asf"), 
    MPEG4_P2_ASF_ASP_L5_SO_G726("MPEG4_P2_ASF_ASP_L5_SO_G726", "video/x-ms-asf"), 
    MPEG4_P2_ASF_ASP_L4_SO_G726("MPEG4_P2_ASF_ASP_L4_SO_G726", "video/x-ms-asf"), 
    MPEG4_H263_3GPP_P0_L10_AMR_WBplus("MPEG4_H263_3GPP_P0_L10_AMR_WBplus", "video/3gpp"), 
    MPEG4_P2_3GPP_SP_L0B_AAC("MPEG4_P2_3GPP_SP_L0B_AAC", "video/3gpp"), 
    MPEG4_P2_3GPP_SP_L0B_AMR("MPEG4_P2_3GPP_SP_L0B_AMR", "video/3gpp"), 
    MPEG4_H263_3GPP_P3_L10_AMR("MPEG4_H263_3GPP_P3_L10_AMR", "video/3gpp"), 
    AVC_MP4_MP_SD_AAC_MULT5("AVC_MP4_MP_SD_AAC_MULT5", "video/mp4"), 
    AVC_MP4_MP_SD_HEAAC_L2("AVC_MP4_MP_SD_HEAAC_L2", "video/mp4"), 
    AVC_MP4_MP_SD_MPEG1_L3("AVC_MP4_MP_SD_MPEG1_L3", "video/mp4"), 
    AVC_MP4_MP_SD_AC3("AVC_MP4_MP_SD_AC3", "video/mp4"), 
    AVC_MP4_MP_SD_AAC_LTP("AVC_MP4_MP_SD_AAC_LTP", "video/mp4"), 
    AVC_MP4_MP_SD_AAC_LTP_MULT5("AVC_MP4_MP_SD_AAC_LTP_MULT5", "video/mp4"), 
    AVC_MP4_MP_SD_AAC_LTP_MULT7("AVC_MP4_MP_SD_AAC_LTP_MULT7", "video/mp4"), 
    AVC_MP4_MP_SD_ATRAC3plus("AVC_MP4_MP_SD_ATRAC3plus", "video/mp4"), 
    AVC_MP4_MP_SD_BSAC("AVC_MP4_MP_SD_BSAC", "video/mp4"), 
    AVC_MP4_MP_HD_720p_AAC("AVC_MP4_MP_HD_720p_AAC", "video/mp4"), 
    AVC_MP4_MP_HD_1080i_AAC("AVC_MP4_MP_HD_1080i_AAC", "video/mp4"), 
    AVC_MP4_HP_HD_AAC("AVC_MP4_HP_HD_AAC", "video/mp4"), 
    AVC_MP4_BL_L3L_SD_AAC("AVC_MP4_BL_L3L_SD_AAC", "video/mp4"), 
    AVC_MP4_BL_L3L_SD_HEAAC("AVC_MP4_BL_L3L_SD_HEAAC", "video/mp4"), 
    AVC_MP4_BL_L3_SD_AAC("AVC_MP4_BL_L3_SD_AAC", "video/mp4"), 
    AVC_MP4_BL_CIF30_AAC_MULT5("AVC_MP4_BL_CIF30_AAC_MULT5", "video/mp4"), 
    AVC_MP4_BL_CIF30_HEAAC_L2("AVC_MP4_BL_CIF30_HEAAC_L2", "video/mp4"), 
    AVC_MP4_BL_CIF30_MPEG1_L3("AVC_MP4_BL_CIF30_MPEG1_L3", "video/mp4"), 
    AVC_MP4_BL_CIF30_AC3("AVC_MP4_BL_CIF30_AC3", "video/mp4"), 
    AVC_MP4_BL_CIF30_AAC_LTP("AVC_MP4_BL_CIF30_AAC_LTP", "video/mp4"), 
    AVC_MP4_BL_CIF30_AAC_LTP_MULT5("AVC_MP4_BL_CIF30_AAC_LTP_MULT5", "video/mp4"), 
    AVC_MP4_BL_L2_CIF30_AAC("AVC_MP4_BL_L2_CIF30_AAC", "video/mp4"), 
    AVC_MP4_BL_CIF30_BSAC("AVC_MP4_BL_CIF30_BSAC", "video/mp4"), 
    AVC_MP4_BL_CIF30_BSAC_MULT5("AVC_MP4_BL_CIF30_BSAC_MULT5", "video/mp4"), 
    AVC_MP4_BL_CIF15_HEAAC("AVC_MP4_BL_CIF15_HEAAC", "video/mp4"), 
    AVC_MP4_BL_CIF15_AMR("AVC_MP4_BL_CIF15_AMR", "video/mp4"), 
    AVC_MP4_BL_CIF15_AAC("AVC_MP4_BL_CIF15_AAC", "video/mp4"), 
    AVC_MP4_BL_CIF15_AAC_520("AVC_MP4_BL_CIF15_AAC_520", "video/mp4"), 
    AVC_MP4_BL_CIF15_AAC_LTP("AVC_MP4_BL_CIF15_AAC_LTP", "video/mp4"), 
    AVC_MP4_BL_CIF15_AAC_LTP_520("AVC_MP4_BL_CIF15_AAC_LTP_520", "video/mp4"), 
    AVC_MP4_BL_CIF15_BSAC("AVC_MP4_BL_CIF15_BSAC", "video/mp4"), 
    AVC_MP4_BL_L12_CIF15_HEAAC("AVC_MP4_BL_L12_CIF15_HEAAC", "video/mp4"), 
    AVC_MP4_BL_L1B_QCIF15_HEAAC("AVC_MP4_BL_L1B_QCIF15_HEAAC", "video/mp4"), 
    AVC_TS_MP_SD_AAC_MULT5("AVC_TS_MP_SD_AAC_MULT5", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_SD_AAC_MULT5_T("AVC_TS_MP_SD_AAC_MULT5_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_SD_AAC_MULT5_ISO("AVC_TS_MP_SD_AAC_MULT5_ISO", "video/mpeg"), 
    AVC_TS_MP_SD_HEAAC_L2("AVC_TS_MP_SD_HEAAC_L2", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_SD_HEAAC_L2_T("AVC_TS_MP_SD_HEAAC_L2_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_SD_HEAAC_L2_ISO("AVC_TS_MP_SD_HEAAC_L2_ISO", "video/mpeg"), 
    AVC_TS_MP_SD_MPEG1_L3("AVC_TS_MP_SD_MPEG1_L3", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_SD_MPEG1_L3_T("AVC_TS_MP_SD_MPEG1_L3_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_SD_MPEG1_L3_ISO("AVC_TS_MP_SD_MPEG1_L3_ISO", "video/mpeg"), 
    AVC_TS_MP_SD_AC3("AVC_TS_MP_SD_AC3", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_SD_AC3_T("AVC_TS_MP_SD_AC3_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_SD_AC3_ISO("AVC_TS_MP_SD_AC3_ISO", "video/mpeg"), 
    AVC_TS_MP_SD_AAC_LTP("AVC_TS_MP_SD_AAC_LTP", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_SD_AAC_LTP_T("AVC_TS_MP_SD_AAC_LTP_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_SD_AAC_LTP_ISO("AVC_TS_MP_SD_AAC_LTP_ISO", "video/mpeg"), 
    AVC_TS_MP_SD_AAC_LTP_MULT5("AVC_TS_MP_SD_AAC_LTP_MULT5", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_SD_AAC_LTP_MULT5_T("AVC_TS_MP_SD_AAC_LTP_MULT5_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_SD_AAC_LTP_MULT5_ISO("AVC_TS_MP_SD_AAC_LTP_MULT5_ISO", "video/mpeg"), 
    AVC_TS_MP_SD_AAC_LTP_MULT7("AVC_TS_MP_SD_AAC_LTP_MULT7", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_SD_AAC_LTP_MULT7_T("AVC_TS_MP_SD_AAC_LTP_MULT7_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_SD_AAC_LTP_MULT7_ISO("AVC_TS_MP_SD_AAC_LTP_MULT7_ISO", "video/mpeg"), 
    AVC_TS_MP_SD_BSAC("AVC_TS_MP_SD_BSAC", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_SD_BSAC_T("AVC_TS_MP_SD_BSAC_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_SD_BSAC_ISO("AVC_TS_MP_SD_BSAC_ISO", "video/mpeg"), 
    AVC_TS_HD_24_AC3("AVC_TS_HD_24_AC3", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_HD_24_AC3_T("AVC_TS_HD_24_AC3_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_HD_24_AC3_ISO("AVC_TS_HD_24_AC3_ISO", "video/mpeg"), 
    AVC_TS_HD_50_LPCM_T("AVC_TS_HD_50_LPCM_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_HD_50_AC3("AVC_TS_HD_50_AC3", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_HD_50_AC3_T("AVC_TS_HD_50_AC3_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_HD_50_AC3_ISO("AVC_TS_HD_50_AC3_ISO", "video/mpeg"), 
    AVC_TS_HD_60_AC3("AVC_TS_HD_60_AC3", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_HD_60_AC3_T("AVC_TS_HD_60_AC3_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_HD_60_AC3_ISO("AVC_TS_HD_60_AC3_ISO", "video/mpeg"), 
    AVC_TS_BL_CIF30_AAC_MULT5("AVC_TS_BL_CIF30_AAC_MULT5", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_BL_CIF30_AAC_MULT5_T("AVC_TS_BL_CIF30_AAC_MULT5_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_BL_CIF30_AAC_MULT5_ISO("AVC_TS_BL_CIF30_AAC_MULT5_ISO", "video/mpeg"), 
    AVC_TS_BL_CIF30_HEAAC_L2("AVC_TS_BL_CIF30_HEAAC_L2", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_BL_CIF30_HEAAC_L2_T("AVC_TS_BL_CIF30_HEAAC_L2_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_BL_CIF30_HEAAC_L2_ISO("AVC_TS_BL_CIF30_HEAAC_L2_ISO", "video/mpeg"), 
    AVC_TS_BL_CIF30_MPEG1_L3("AVC_TS_BL_CIF30_MPEG1_L3", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_BL_CIF30_MPEG1_L3_T("AVC_TS_BL_CIF30_MPEG1_L3_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_BL_CIF30_MPEG1_L3_ISO("AVC_TS_BL_CIF30_MPEG1_L3_ISO", "video/mpeg"), 
    AVC_TS_BL_CIF30_AC3("AVC_TS_BL_CIF30_AC3", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_BL_CIF30_AC3_T("AVC_TS_BL_CIF30_AC3_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_BL_CIF30_AC3_ISO("AVC_TS_BL_CIF30_AC3_ISO", "video/mpeg"), 
    AVC_TS_BL_CIF30_AAC_LTP("AVC_TS_BL_CIF30_AAC_LTP", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_BL_CIF30_AAC_LTP_T("AVC_TS_BL_CIF30_AAC_LTP_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_BL_CIF30_AAC_LTP_ISO("AVC_TS_BL_CIF30_AAC_LTP_ISO", "video/mpeg"), 
    AVC_TS_BL_CIF30_AAC_LTP_MULT5("AVC_TS_BL_CIF30_AAC_LTP_MULT5", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_BL_CIF30_AAC_LTP_MULT5_T("AVC_TS_BL_CIF30_AAC_LTP_MULT5_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_BL_CIF30_AAC_LTP_MULT5_ISO("AVC_TS_BL_CIF30_AAC_LTP_MULT5_ISO", "video/mpeg"), 
    AVC_TS_BL_CIF30_AAC_940("AVC_TS_BL_CIF30_AAC_940", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_BL_CIF30_AAC_940_T("AVC_TS_BL_CIF30_AAC_940_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_BL_CIF30_AAC_940_ISO("AVC_TS_BL_CIF30_AAC_940_ISO", "video/mpeg"), 
    AVC_TS_MP_HD_AAC_MULT5("AVC_TS_MP_HD_AAC_MULT5", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_HD_AAC_MULT5_T("AVC_TS_MP_HD_AAC_MULT5_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_HD_AAC_MULT5_ISO("AVC_TS_MP_HD_AAC_MULT5_ISO", "video/mpeg"), 
    AVC_TS_MP_HD_HEAAC_L2("AVC_TS_MP_HD_HEAAC_L2", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_HD_HEAAC_L2_T("AVC_TS_MP_HD_HEAAC_L2_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_HD_HEAAC_L2_ISO("AVC_TS_MP_HD_HEAAC_L2_ISO", "video/mpeg"), 
    AVC_TS_MP_HD_MPEG1_L3("AVC_TS_MP_HD_MPEG1_L3", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_HD_MPEG1_L3_T("AVC_TS_MP_HD_MPEG1_L3_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_HD_MPEG1_L3_ISO("AVC_TS_MP_HD_MPEG1_L3_ISO", "video/mpeg"), 
    AVC_TS_MP_HD_AC3("AVC_TS_MP_HD_AC3", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_HD_AC3_T("AVC_TS_MP_HD_AC3_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_HD_AC3_ISO("AVC_TS_MP_HD_AC3_ISO", "video/mpeg"), 
    AVC_TS_MP_HD_AAC("AVC_TS_MP_HD_AAC", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_HD_AAC_T("AVC_TS_MP_HD_AAC_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_HD_AAC_ISO("AVC_TS_MP_HD_AAC_ISO", "video/mpeg"), 
    AVC_TS_MP_HD_AAC_LTP("AVC_TS_MP_HD_AAC_LTP", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_HD_AAC_LTP_T("AVC_TS_MP_HD_AAC_LTP_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_HD_AAC_LTP_ISO("AVC_TS_MP_HD_AAC_LTP_ISO", "video/mpeg"), 
    AVC_TS_MP_HD_AAC_LTP_MULT5("AVC_TS_MP_HD_AAC_LTP_MULT5", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_HD_AAC_LTP_MULT5_T("AVC_TS_MP_HD_AAC_LTP_MULT5_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_HD_AAC_LTP_MULT5_ISO("AVC_TS_MP_HD_AAC_LTP_MULT5_ISO", "video/mpeg"), 
    AVC_TS_MP_HD_AAC_LTP_MULT7("AVC_TS_MP_HD_AAC_LTP_MULT7", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_HD_AAC_LTP_MULT7_T("AVC_TS_MP_HD_AAC_LTP_MULT7_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_MP_HD_AAC_LTP_MULT7_ISO("AVC_TS_MP_HD_AAC_LTP_MULT7_ISO", "video/mpeg"), 
    AVC_TS_BL_CIF15_AAC("AVC_TS_BL_CIF15_AAC", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_BL_CIF15_AAC_T("AVC_TS_BL_CIF15_AAC_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_BL_CIF15_AAC_ISO("AVC_TS_BL_CIF15_AAC_ISO", "video/mpeg"), 
    AVC_TS_BL_CIF15_AAC_540("AVC_TS_BL_CIF15_AAC_540", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_BL_CIF15_AAC_540_T("AVC_TS_BL_CIF15_AAC_540_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_BL_CIF15_AAC_540_ISO("AVC_TS_BL_CIF15_AAC_540_ISO", "video/mpeg"), 
    AVC_TS_BL_CIF15_AAC_LTP("AVC_TS_BL_CIF15_AAC_LTP", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_BL_CIF15_AAC_LTP_T("AVC_TS_BL_CIF15_AAC_LTP_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_BL_CIF15_AAC_LTP_ISO("AVC_TS_BL_CIF15_AAC_LTP_ISO", "video/mpeg"), 
    AVC_TS_BL_CIF15_BSAC("AVC_TS_BL_CIF15_BSAC", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_BL_CIF15_BSAC_T("AVC_TS_BL_CIF15_BSAC_T", "video/vnd.dlna.mpeg-tts"), 
    AVC_TS_BL_CIF15_BSAC_ISO("AVC_TS_BL_CIF15_BSAC_ISO", "video/mpeg"), 
    AVC_3GPP_BL_CIF30_AMR_WBplus("AVC_3GPP_BL_CIF30_AMR_WBplus", "video/3gpp"), 
    AVC_3GPP_BL_CIF15_AMR_WBplus("AVC_3GPP_BL_CIF15_AMR_WBplus", "video/3gpp"), 
    AVC_3GPP_BL_QCIF15_AAC("AVC_3GPP_BL_QCIF15_AAC", "video/3gpp"), 
    AVC_3GPP_BL_QCIF15_AAC_LTP("AVC_3GPP_BL_QCIF15_AAC_LTP", "video/3gpp"), 
    AVC_3GPP_BL_QCIF15_HEAAC("AVC_3GPP_BL_QCIF15_HEAAC", "video/3gpp"), 
    AVC_3GPP_BL_QCIF15_AMR_WBplus("AVC_3GPP_BL_QCIF15_AMR_WBplus", "video/3gpp"), 
    AVC_3GPP_BL_QCIF15_AMR("AVC_3GPP_BL_QCIF15_AMR", "video/3gpp"), 
    AVC_MP4_LPCM("AVC_MP4_LPCM", "video/mp4"), 
    AVI("AVI", "video/avi"), 
    AVI_XMS("AVI", "video/x-msvideo"), 
    DIVX("AVI", "video/divx"), 
    MATROSKA("MATROSKA", "video/x-matroska"), 
    MATROSKA_MKV("MATROSKA", "video/x-mkv"), 
    VC1_ASF_AP_L1_WMA("VC1_ASF_AP_L1_WMA", "video/x-ms-asf"), 
    VC1_ASF_AP_L2_WMA("VC1_ASF_AP_L2_WMA", "video/x-ms-asf"), 
    VC1_ASF_AP_L3_WMA("VC1_ASF_AP_L3_WMA", "video/x-ms-asf"), 
    VC1_ASF_AP_L1_WMA_WMV("VC1_ASF_AP_L1_WMA", "video/x-ms-wmv"), 
    VC1_ASF_AP_L2_WMA_WMV("VC1_ASF_AP_L2_WMA", "video/x-ms-wmv"), 
    VC1_ASF_AP_L3_WMA_WMV("VC1_ASF_AP_L3_WMA", "video/x-ms-wmv");
    
    private String code;
    private String contentFormat;
    
    private DLNAProfiles(final String code, final String contentFormat) {
        this.code = code;
        this.contentFormat = contentFormat;
    }
    
    public String getCode() {
        return this.code;
    }
    
    public String getContentFormat() {
        return this.contentFormat;
    }
    
    public static DLNAProfiles valueOf(final String code, final String contentFormat) {
        for (final DLNAProfiles errorCode : values()) {
            if (errorCode.getCode().equals(code) && (contentFormat.length() == 0 || errorCode.getContentFormat().equals(contentFormat))) {
                return errorCode;
            }
        }
        return null;
    }
    
    class DLNAMimeTypes
    {
        public static final String MIME_IMAGE_JPEG = "image/jpeg";
        public static final String MIME_IMAGE_PNG = "image/png";
        public static final String MIME_AUDIO_3GP = "audio/3gpp";
        public static final String MIME_AUDIO_ADTS = "audio/vnd.dlna.adts";
        public static final String MIME_AUDIO_ATRAC = "audio/x-sony-oma";
        public static final String MIME_AUDIO_DOLBY_DIGITAL = "audio/vnd.dolby.dd-raw";
        public static final String MIME_AUDIO_LPCM = "audio/L16";
        public static final String MIME_AUDIO_MPEG = "audio/mpeg";
        public static final String MIME_AUDIO_MPEG_4 = "audio/mp4";
        public static final String MIME_AUDIO_WMA = "audio/x-ms-wma";
        public static final String MIME_VIDEO_3GP = "video/3gpp";
        public static final String MIME_VIDEO_ASF = "video/x-ms-asf";
        public static final String MIME_VIDEO_MPEG = "video/mpeg";
        public static final String MIME_VIDEO_MPEG_4 = "video/mp4";
        public static final String MIME_VIDEO_MPEG_TS = "video/vnd.dlna.mpeg-tts";
        public static final String MIME_VIDEO_WMV = "video/x-ms-wmv";
        public static final String MIME_VIDEO_DIVX = "video/divx";
        public static final String MIME_VIDEO_AVI = "video/avi";
        public static final String MIME_VIDEO_XMS_AVI = "video/x-msvideo";
        public static final String MIME_VIDEO_MATROSKA = "video/x-matroska";
        public static final String MIME_VIDEO_MKV = "video/x-mkv";
    }
}
