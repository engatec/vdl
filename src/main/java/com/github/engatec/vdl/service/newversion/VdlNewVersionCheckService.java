package com.github.engatec.vdl.service.newversion;

import java.util.Optional;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.dto.github.ReleaseDto;
import com.github.engatec.vdl.util.GithubUtils;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class VdlNewVersionCheckService extends Service<Optional<ReleaseDto>> {

    @Override
    protected Task<Optional<ReleaseDto>> createTask() {
        return new Task<>() {
            @Override
            protected Optional<ReleaseDto> call() {
                ReleaseDto releaseInfo = GithubUtils.getLatestReleaseInfo("engatec", "vdl");
                String latestVersion = releaseInfo.tagName();
                String currentVersion = ApplicationContext.getInstance().getAppVersion();
                return new NewVersionPredicate().test(latestVersion, currentVersion) ? Optional.of(releaseInfo) : Optional.empty();
            }
        };
    }
}
