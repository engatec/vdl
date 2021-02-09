package com.github.engatec.vdl.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class LabelUtils {

    private static final String N_A = "N/A";
    private static final String QUESTION_MARK = "?";
    private static final String TRACK_PREFIX = "Track ";

    public static String formatResolution(Integer width, Integer height) {
        String w = width == null ? QUESTION_MARK : width.toString();
        String h = height == null ? QUESTION_MARK : height.toString();
        return w.equals(QUESTION_MARK) && h.equals(QUESTION_MARK) ? N_A : w + "x" + h;
    }

    public static String formatSize(Long filesize) {
        return filesize == null || filesize == 0 ? N_A : FileUtils.byteCountToDisplaySize(filesize);
    }

    public static String formatCodec(String codec) {
        return StringUtils.defaultIfBlank(codec, N_A);
    }

    public static String formatBitrate(Double bitrate) {
        return bitrate == null ? N_A : String.valueOf(bitrate.intValue());
    }

    public static String formatTrackNo(int trackNo) {
        return TRACK_PREFIX + trackNo;
    }
}
