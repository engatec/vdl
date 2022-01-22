package com.github.engatec.vdl.util;

import java.util.regex.Pattern;

import com.github.engatec.vdl.model.VideoInfo;

public class Thumbnails {

    private static final Pattern YOUTUBE_PATTERN = Pattern.compile(".*youtube\\.com/.*");

    public enum Quality {
        MAX("https://img.youtube.com/vi/%s/maxresdefault.jpg"),
        HIGH("https://img.youtube.com/vi/%s/hqdefault.jpg"),
        MEDIUM("https://img.youtube.com/vi/%s/mqdefault.jpg");

        private final String youtubeUrl;

        Quality(String youtubeUrl) {
            this.youtubeUrl = youtubeUrl;
        }

        public String getYoutubeUrl() {
            return youtubeUrl;
        }
    }

    public static String normalizeThumbnailUrl(VideoInfo vi, Quality quality) {
        return YOUTUBE_PATTERN.matcher(vi.getBaseUrl()).matches() ? String.format(quality.getYoutubeUrl(), vi.getId()) : vi.getThumbnail();
    }
}
