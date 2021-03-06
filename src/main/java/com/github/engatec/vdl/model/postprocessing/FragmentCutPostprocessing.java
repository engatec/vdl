package com.github.engatec.vdl.model.postprocessing;

import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

import static com.github.engatec.vdl.core.ApplicationContext.APP_DIR;

public class FragmentCutPostprocessing implements Postprocessing {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private LocalTime startTime;
    private LocalTime endTime;

    private FragmentCutPostprocessing() {
    }

    public static FragmentCutPostprocessing newInstance(LocalTime startTime, LocalTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time " + startTime + " is after end time " + endTime);
        }

        var instance = new FragmentCutPostprocessing();
        instance.startTime = startTime;
        instance.endTime = endTime;
        return instance;
    }

    public String getStartTimeAsString() {
        return startTime.format(TIME_FORMATTER);
    }

    public String getEndTimeAsString() {
        return endTime.format(TIME_FORMATTER);
    }

    @Override
    public List<String> getCommandList() {
        return List.of(
                "--exec",
                new StringJoiner(StringUtils.SPACE)
                        .add(Path.of(StringUtils.defaultString(APP_DIR, StringUtils.EMPTY), "ffmpeg").toString())
                        .add("-i").add("{}")
                        .add("-ss").add(startTime.format(TIME_FORMATTER)).add("-to").add(endTime.format(TIME_FORMATTER))
                        .add("-c").add("copy")
                        .add("{}.mkv")
                        .toString()
        );
    }
}
