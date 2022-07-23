package com.github.engatec.vdl.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.engatec.vdl.core.json.SubtitlesJsonDeserializer;
import org.apache.commons.lang3.StringUtils;

/**
 * Parameters explanation can be found here: https://github.com/yt-dlp/yt-dlp/blob/master/yt_dlp/extractor/common.py
 */
public record VideoInfo (
    String id,
    String extractor,
    @JsonProperty("_type") String type,
    @JsonProperty("webpage_url") String baseUrl,
    // 'url' property used by playlists as they don't have 'webpage_url'. One might think "Let's try @JsonAlias on baseUrl instead of separate url parameter"... don't!
    // Jackson doesn't prioritize @JsonProperty over @JsonAlias (at least current version) so you never know which of two will be chosen and it leads to breaking standalone
    // videos downloading on some resources. Thus I'm checking if baseUrl is blank in the constructor and if it is reassign it with url value
    String url,
    String title,
    Integer duration,
    String thumbnail,
    @JsonDeserialize(using = SubtitlesJsonDeserializer.class) List<Subtitle> subtitles,
    List<Format> formats
) {
    public VideoInfo {
        baseUrl = StringUtils.defaultIfBlank(baseUrl, url); // See comment to 'url' field explaining why the parameter is being reassigned
    }
}
