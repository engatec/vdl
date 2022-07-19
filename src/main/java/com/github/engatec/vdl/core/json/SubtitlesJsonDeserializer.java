package com.github.engatec.vdl.core.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.engatec.vdl.model.Subtitle;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

public class SubtitlesJsonDeserializer extends JsonDeserializer<List<Subtitle>> {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record SubtitlesJsonStructure(String name) {}

    @Override
    public List<Subtitle> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Map<String, List<SubtitlesJsonStructure>> map = p.readValueAs(new TypeReference<Map<String, List<SubtitlesJsonStructure>>>() {});
        if (MapUtils.isEmpty(map)) {
            return null;
        }

        List<Subtitle> subtitles = new ArrayList<>();
        for (var item : map.entrySet()) {
            String isoCode = item.getKey();
            String language = ListUtils.emptyIfNull(item.getValue()).stream()
                    .map(SubtitlesJsonStructure::name)
                    .filter(StringUtils::isNotBlank)
                    .findFirst()
                    .orElse(StringUtils.EMPTY);
            subtitles.add(new Subtitle(language, isoCode));
        }

        return subtitles;
    }
}
