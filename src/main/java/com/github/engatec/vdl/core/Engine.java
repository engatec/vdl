package com.github.engatec.vdl.core;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

public enum Engine {

    YT_DLP {
        @Override
        public String resolveFileName() {
            return StringUtils.defaultIfBlank(
                    System.getProperty("app.ytdlp"),
                    SystemUtils.IS_OS_WINDOWS ? "yt-dlp.exe" : "yt-dlp" // If app.ytdlp is not set, assume default name
            );
        }
    };

    public abstract String resolveFileName();
}
