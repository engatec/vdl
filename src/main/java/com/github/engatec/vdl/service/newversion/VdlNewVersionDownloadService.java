package com.github.engatec.vdl.service.newversion;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.dto.github.AssetDto;
import com.github.engatec.vdl.dto.github.ReleaseDto;
import com.github.engatec.vdl.exception.AppException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

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
                String assetName = "vdl.jar";
                String url = releaseInfo.assets().stream()
                        .filter(it -> assetName.equals(it.name()))
                        .map(AssetDto::downloadUrl)
                        .findFirst()
                        .orElseThrow(() -> new AppException(String.format("No %s asset has been found", assetName)));
                Path downloadPath = ApplicationContext.getInstance().getAppBinariesDir().resolve(assetName + ".new");

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
