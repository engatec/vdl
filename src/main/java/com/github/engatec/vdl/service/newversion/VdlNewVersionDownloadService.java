package com.github.engatec.vdl.service.newversion;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.StringJoiner;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.dto.github.AssetDto;
import com.github.engatec.vdl.dto.github.ReleaseDto;
import com.github.engatec.vdl.exception.AppException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

public class VdlNewVersionDownloadService extends Service<Void> {

    private final ReleaseDto releaseInfo;

    public VdlNewVersionDownloadService(ReleaseDto releaseInfo) {
        this.releaseInfo = releaseInfo;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws IOException, InterruptedException {
                StringJoiner assetNameStringJoiner = new StringJoiner("_", StringUtils.EMPTY, ".jar").add("upd");
                if (SystemUtils.IS_OS_WINDOWS) {
                    assetNameStringJoiner.add("windows");
                } else if (SystemUtils.IS_OS_LINUX) {
                    assetNameStringJoiner.add("linux");
                } else if (SystemUtils.IS_OS_MAC) {
                    assetNameStringJoiner.add("mac");
                } else {
                    throw new AppException(String.format("Unknown OS '%s'", SystemUtils.OS_NAME));
                }

                String assetName = assetNameStringJoiner.toString();

                String url = releaseInfo.assets().stream()
                        .filter(it -> assetName.equals(it.name()))
                        .map(AssetDto::downloadUrl)
                        .findFirst()
                        .orElseThrow(() -> new AppException(String.format("No %s asset has been found", assetName)));

                Path downloadPath = ApplicationContext.getInstance().getAppBinariesDir().resolve("vdl.jar.new");
                Files.deleteIfExists(downloadPath);

                HttpClient client = HttpClient.newBuilder()
                        .version(HttpClient.Version.HTTP_1_1)
                        .followRedirects(HttpClient.Redirect.NORMAL)
                        .connectTimeout(Duration.ofSeconds(30))
                        .build();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofMinutes(5))
                        .GET()
                        .build();

                client.send(request, HttpResponse.BodyHandlers.ofFile(downloadPath));
                return null;
            }
        };
    }
}
