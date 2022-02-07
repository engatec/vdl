package com.github.engatec.vdl.service.newversion;

import java.util.Optional;

import com.github.engatec.vdl.core.Engine;
import com.github.engatec.vdl.core.YoutubeDlManager;
import com.github.engatec.vdl.dto.github.ReleaseDto;
import com.github.engatec.vdl.util.GithubUtils;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

public class YoutubeDlNewVersionCheckService extends Service<Optional<ReleaseDto>> {

    @Override
    protected Task<Optional<ReleaseDto>> createTask() {
        return new Task<>() {
            @Override
            protected Optional<ReleaseDto> call() {
                ReleaseDto releaseInfo = GithubUtils.getLatestReleaseInfo("ytdl-org", "youtube-dl");
                String latestVersion = RegExUtils.replaceAll(releaseInfo.tagName(), "[^\\d]", "");
                String currentVersion = RegExUtils.replaceAll(YoutubeDlManager.INSTANCE.getCurrentVersion(Engine.YOUTUBE_DL), "[^\\d]", "");
                int length = Math.max(latestVersion.length(), currentVersion.length());
                latestVersion = StringUtils.rightPad(latestVersion, length, '0');
                currentVersion = StringUtils.rightPad(currentVersion, length, '0');
                return Integer.parseInt(latestVersion) > Integer.parseInt(currentVersion) ? Optional.of(releaseInfo) : Optional.empty();
            }
        };
    }
}
