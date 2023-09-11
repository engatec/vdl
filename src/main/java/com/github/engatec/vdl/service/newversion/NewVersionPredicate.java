package com.github.engatec.vdl.service.newversion;

import java.util.function.BiPredicate;

import org.apache.commons.lang3.RegExUtils;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;

public class NewVersionPredicate implements BiPredicate<String, String> {

    private static final String VERSION_SEPARATOR = ".";

    @Override
    public boolean test(String newVersion, String currentVersion) {
        return verifyVersion(RegExUtils.replaceAll(newVersion, "[^\\d]", VERSION_SEPARATOR), RegExUtils.replaceAll(currentVersion, "[^\\d]", VERSION_SEPARATOR));
    }

    private boolean verifyVersion(String newVersion, String currentVersion) {
        if (isBlank(newVersion) && isBlank(currentVersion)) {
            return false;
        }

        int newVer = Integer.parseInt(defaultIfBlank(substringBefore(newVersion, VERSION_SEPARATOR), "0"));
        int curVer = Integer.parseInt(defaultIfBlank(substringBefore(currentVersion, VERSION_SEPARATOR), "0"));
        return newVer == curVer ? verifyVersion(substringAfter(newVersion, VERSION_SEPARATOR), substringAfter(currentVersion, VERSION_SEPARATOR)) : newVer > curVer;
    }
}
