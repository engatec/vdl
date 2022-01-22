package com.github.engatec.vdl.core.json;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class SubtitlesJsonDeserializer extends JsonDeserializer<Set<String>> {

    @Override
    public Set<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        TreeNode node = p.readValueAsTree();
        if (node == null) {
            return null;
        }

        Set<String> subLangs = new HashSet<>();
        node.fieldNames().forEachRemaining(subLangs::add);
        return subLangs;
    }
}
