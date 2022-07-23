package com.github.engatec.vdl.ui.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.engatec.vdl.preference.model.TableColumnConfigModel;
import com.github.engatec.vdl.preference.model.TableConfigModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Tables {

    private static final Logger LOGGER = LogManager.getLogger(Tables.class);

    public static <T> void restoreTableViewStateFromConfigModel(TableView<T> tableView, TableConfigModel tableConfigModel, Map<String, Integer> columnIdMap) {
        Map<Integer, TableColumnConfigModel> tableConfigModelMap = SetUtils.emptyIfNull(tableConfigModel.columnConfigModels()).stream()
                .collect(Collectors.toMap(TableColumnConfigModel::id, Function.identity()));

        if (tableConfigModelMap.isEmpty()) {
            return;
        }

        tableView.getColumns().sort((c1, c2) -> {
            TableColumnConfigModel c1Config = tableConfigModelMap.get(columnIdMap.get(c1.getId()));
            TableColumnConfigModel c2Config = tableConfigModelMap.get(columnIdMap.get(c2.getId()));
            return Integer.compare(c1Config.pos(), c2Config.pos());
        });

        tableView.getColumns().forEach(column -> {
            TableColumnConfigModel config = tableConfigModelMap.get(columnIdMap.get(column.getId()));
            if (config == null) {
                LOGGER.warn("Config for column {} is null", column.getId());
                return;
            }

            column.setPrefWidth(config.width());
            column.setSortType(config.sortType());
        });

        // Restore sort order
        List<Integer> sortOrderColumnIds = ListUtils.emptyIfNull(tableConfigModel.sortOrderColumnIds());
        List<TableColumn<T, ?>> sortOrderColumns = new ArrayList<>(sortOrderColumnIds.size());
        for (int i = 0; i < sortOrderColumnIds.size(); i++) {
            sortOrderColumns.add(null);
        }

        tableView.getColumns().forEach(column -> {
            int sortOrderIdx = sortOrderColumnIds.indexOf(columnIdMap.get(column.getId()));
            if (sortOrderIdx >= 0) {
                sortOrderColumns.set(sortOrderIdx, column);
            }
        });

        tableView.getSortOrder().addAll(sortOrderColumns);
    }

    public static <T> TableConfigModel convertTableViewStateToConfigModel(TableView<T> tableView, Map<String, Integer> columnIdMap) {
        Set<TableColumnConfigModel> columnConfigModels = new HashSet<>();
        var columns = tableView.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            var c = columns.get(i);
            columnConfigModels.add(new TableColumnConfigModel(columnIdMap.get(c.getId()), i, c.getWidth(), c.getSortType()));
        }

        List<Integer> sortOrderList = tableView.getSortOrder().stream()
                .map(TableColumnBase::getId)
                .map(columnIdMap::get)
                .toList();

        return new TableConfigModel(sortOrderList, columnConfigModels);
    }
}
