package com.github.engatec.vdl.dto.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AssetDto(
        String name,
        @JsonProperty("browser_download_url") String downloadUrl
) {}
