package com.github.engatec.vdl.preference.model;

import java.util.List;
import java.util.Set;

public record TableConfigModel(List<Integer> sortOrderColumnIds, Set<TableColumnConfigModel> columnConfigModels) {}
