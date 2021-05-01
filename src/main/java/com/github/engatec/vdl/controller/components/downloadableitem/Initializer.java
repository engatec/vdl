package com.github.engatec.vdl.controller.components.downloadableitem;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.github.engatec.vdl.model.Format;
import com.github.engatec.vdl.model.Resolution;
import com.github.engatec.vdl.model.VideoInfo;
import javafx.collections.ObservableList;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

class Initializer {

    static void initialize(Context ctx, VideoInfo videoInfo) {
        initLabels(ctx, videoInfo);
        initFormats(ctx, videoInfo);
    }

    private static void initLabels(Context ctx, VideoInfo videoInfo) {
        ctx.getTitleLabel().setText(videoInfo.getTitle());

        int durationSeconds = Objects.requireNonNullElse(videoInfo.getDuration(), 0);
        String formattedDuration = durationSeconds <= 0 ? StringUtils.EMPTY : DurationFormatUtils.formatDuration(durationSeconds * 1000L, "HH:mm:ss", false);
        ctx.getDurationLabel().setText(formattedDuration);
    }

    private static void initFormats(Context ctx, VideoInfo videoInfo) {
        List<Integer> commonAvailableFormats = ListUtils.emptyIfNull(videoInfo.getFormats()).stream()
                .map(Format::getHeight)
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        ObservableList<String> comboBoxItems = ctx.getFormatsComboBox().getItems();
        for (Integer height : commonAvailableFormats) {
            comboBoxItems.add(height + "p " + Resolution.getDescriptionByHeight(height));
        }
    }
}
