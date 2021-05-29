package com.github.engatec.vdl.util;

import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

public class YouDlUtils {

    public static String createFormatByHeight(Integer height) {
        String h = StringUtils.EMPTY;
        if (height != null) {
            h = "[height<=" + height + "]";
        }

        return new StringJoiner("/")
                .add("bestvideo" + h + "[ext=mp4]+bestaudio[ext=m4a]")
                .add("bestvideo" + h + "[ext=webm]+bestaudio[ext=webm]")
                .add("bestvideo" + h + "+bestaudio")
                .add("best" + h)
                .toString();
    }
}
