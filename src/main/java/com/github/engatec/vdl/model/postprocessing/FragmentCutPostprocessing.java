package com.github.engatec.vdl.model.postprocessing;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

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
    public String toString() {
        return new StringJoiner(StringUtils.SPACE)
                .add("-ss")
                .add(startTime.format(TIME_FORMATTER))
                .add("-to")
                .add(endTime.format(TIME_FORMATTER))
                .toString();
    }
}
