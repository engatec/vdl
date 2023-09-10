package com.github.engatec.vdl.service.newversion;

import java.util.function.BiPredicate;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

public class NewVersionPredicate implements BiPredicate<String, String> {

    @Override
    public boolean test(String newVersion, String currentVersion) {
        newVersion = RegExUtils.replaceAll(newVersion, "[^\\d]", "");
        currentVersion = RegExUtils.replaceAll(currentVersion, "[^\\d]", "");
        int length = Math.max(newVersion.length(), currentVersion.length());
        newVersion = StringUtils.rightPad(newVersion, length, '0');
        currentVersion = StringUtils.rightPad(currentVersion, length, '0');
        return Integer.parseInt(newVersion) > Integer.parseInt(currentVersion);
    }
}
