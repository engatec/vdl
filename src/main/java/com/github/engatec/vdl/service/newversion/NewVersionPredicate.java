package com.github.engatec.vdl.service.newversion;

import java.util.function.BiPredicate;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

public class NewVersionPredicate implements BiPredicate<String, String> {

    @Override
    public boolean test(String latestVersion, String currentVersion) {
        latestVersion = RegExUtils.replaceAll(latestVersion, "[^\\d]", "");
        currentVersion = RegExUtils.replaceAll(currentVersion, "[^\\d]", "");
        int length = Math.max(latestVersion.length(), currentVersion.length());
        latestVersion = StringUtils.rightPad(latestVersion, length, '0');
        currentVersion = StringUtils.rightPad(currentVersion, length, '0');
        return Integer.parseInt(latestVersion) > Integer.parseInt(currentVersion);
    }
}
