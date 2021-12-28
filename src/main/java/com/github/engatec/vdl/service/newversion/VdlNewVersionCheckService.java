package com.github.engatec.vdl.service.newversion;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.dto.github.ReleaseDto;
import com.github.engatec.vdl.util.GithubUtils;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.commons.lang3.StringUtils;

public class VdlNewVersionCheckService extends Service<Optional<ReleaseDto>> {

    @Override
    protected Task<Optional<ReleaseDto>> createTask() {
        return new Task<>() {
            @Override
            protected Optional<ReleaseDto> call() {
                ReleaseDto releaseInfo = GithubUtils.getLatestReleaseInfo("engatec", "vdl");
                int latestVersion = parseVersion(releaseInfo.tagName());
                int currentVersion = parseVersion(ApplicationContext.getInstance().getAppVersion());
                return latestVersion > currentVersion ? Optional.of(releaseInfo) : Optional.empty();
            }

            private int parseVersion(String rawVersion) {
                List<Integer> versionNumbers = Arrays.stream(StringUtils.split(rawVersion, '.'))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());

                while (versionNumbers.size() < 3) {
                    versionNumbers.add(0);
                }

                Collections.reverse(versionNumbers);

                int version = 0;
                for (int i = 0; i < versionNumbers.size(); i++) {
                    version += versionNumbers.get(i) * Math.pow(10, i);
                }

                return version;
            }
        };
    }
}
