package com.github.engatec.vdl.ui.controller;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

public class YoutubeCookiesGeneratorController extends StageAwareController {

    @FXML private WebView youtubeWebView;
    @FXML private Button okButton;
    @FXML private Button cancelButton;

    private Consumer<String> onOkButtonClickCallback;

    private CookieManager cookieManager;

    private YoutubeCookiesGeneratorController() {
    }

    public YoutubeCookiesGeneratorController(Stage stage, Consumer<String> onOkButtonClickCallback) {
        super(stage);
        this.onOkButtonClickCallback = onOkButtonClickCallback;
        cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }

    @FXML
    public void initialize() {
        okButton.setOnAction(this::handleOkButtonClick);
        cancelButton.setOnAction(this::handleCancelButtonClick);
        youtubeWebView.getEngine().load("https://youtube.com");
    }

    private void handleOkButtonClick(ActionEvent e) {
        StringBuilder cookiesContent = new StringBuilder();
        cookiesContent.append("# Netscape HTTP Cookie File").append(System.lineSeparator()).append(System.lineSeparator());

        for (HttpCookie cookie : cookieManager.getCookieStore().getCookies()) {
            if (!StringUtils.contains(cookie.getDomain(), "youtube.com")) {
                continue;
            }

            cookiesContent
                    .append(cookie.getDomain()).append("\t")
                    .append(String.valueOf(StringUtils.startsWith(cookie.getDomain(), ".")).toUpperCase()).append("\t")
                    .append(cookie.getPath()).append("\t")
                    .append(String.valueOf(cookie.getSecure()).toUpperCase()).append("\t")
                    .append(LocalDateTime.now().plusYears(2).toEpochSecond(ZoneOffset.UTC)).append("\t")
                    .append(cookie.getName()).append("\t")
                    .append(cookie.getValue()).append(System.lineSeparator());
        }

        onOkButtonClickCallback.accept(cookiesContent.toString());

        stage.close();
        e.consume();
    }

    private void handleCancelButtonClick(ActionEvent e) {
        stage.close();
        e.consume();
    }
}
