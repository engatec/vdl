package com.github.engatec.vdl.dto.github;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReleaseDto(
    @JsonProperty("tag_name") String tagName,
    List<AssetDto> assets,
    String message
) {}
