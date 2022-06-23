package com.github.engatec.vdl.preference.configitem.misc;

import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.engatec.vdl.preference.model.TableConfigModel;
import org.apache.commons.lang3.StringUtils;

public class HistoryTableConfigItem extends MiscConfigItem<TableConfigModel> {

    @Override
    protected String getName() {
        return "history_table";
    }

    @Override
    public TableConfigModel getValue(Preferences prefs) {
        return stringToModel(prefs.get(getKey(), StringUtils.EMPTY));
    }

    @Override
    public void setValue(Preferences prefs, TableConfigModel value) {
        if (value == null ) {
            prefs.remove(getKey());
        }

        String str = modelToString(value);
        if (StringUtils.isBlank(str)) {
            prefs.remove(getKey());
        } else {
            prefs.put(getKey(), str);
        }
    }

    private TableConfigModel stringToModel(String str) {
        if (StringUtils.isBlank(str)) {
            return new TableConfigModel(List.of(), Set.of());
        }

        var objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(str, new TypeReference<>(){});
        } catch (JsonProcessingException e) {
            return new TableConfigModel(List.of(), Set.of());
        }
    }

    private String modelToString(TableConfigModel model) {
        var objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(model);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
