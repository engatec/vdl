package com.github.engatec.vdl.util;

import java.util.List;
import java.util.stream.Collectors;

import com.github.engatec.vdl.model.downloadable.Audio;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class LabelUtils {

    private static final String N_A = "N/A";
    private static final String QUESTION_MARK = "?";
    private static final String AUDIO_LABEL_DELIMITER = " / ";

    public static String formatResolution(Integer width, Integer height) {
        String w = width == null ? QUESTION_MARK : width.toString();
        String h = height == null ? QUESTION_MARK : height.toString();
        return w.equals(QUESTION_MARK) && h.equals(QUESTION_MARK) ? N_A : w + "x" + h;
    }

    public static String formatSize(Long filesize) {
        return formatSize(filesize, N_A);
    }

    public static String formatSize(Long filesize, String defaultValue) {
        return filesize == null || filesize == 0 ? defaultValue : FileUtils.byteCountToDisplaySize(filesize);
    }

    public static String formatCodec(String codec) {
        codec = StringUtils.defaultIfBlank(codec, N_A);
        return StringUtils.substringBefore(codec, ".");
    }

    public static String formatAudio(Audio item) {
        return List.of(
                item.getExtension(),
                formatSize(item.getFilesize(), StringUtils.EMPTY)
        ).stream()
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(AUDIO_LABEL_DELIMITER));
    }
}
