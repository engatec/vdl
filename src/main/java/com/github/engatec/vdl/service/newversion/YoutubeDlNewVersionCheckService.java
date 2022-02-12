package com.github.engatec.vdl.service.newversion;

import java.util.Optional;

import com.github.engatec.vdl.core.Engine;
import com.github.engatec.vdl.core.YoutubeDlManager;
import com.github.engatec.vdl.dto.github.ReleaseDto;
import com.github.engatec.vdl.util.GithubUtils;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class YoutubeDlNewVersionCheckService extends Service<Optional<ReleaseDto>> {

    @Override
    protected Task<Optional<ReleaseDto>> createTask() {
        return new Task<>() {
            @Override
            protected Optional<ReleaseDto> call() {
                ReleaseDto releaseInfo = GithubUtils.getLatestReleaseInfo("ytdl-org", "youtube-dl");
                String latestVersion = releaseInfo.tagName();
                String currentVersion = YoutubeDlManager.INSTANCE.getCurrentVersion(Engine.YOUTUBE_DL);
                return new NewVersionPredicate().test(latestVersion, currentVersion) ? Optional.of(releaseInfo) : Optional.empty();
            }
        };
    }
}
