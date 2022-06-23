package com.github.engatec.vdl.ui.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.github.engatec.vdl.ui.data.UserDataType;
import javafx.stage.Stage;

public class StageUtils {

    @SuppressWarnings("unchecked")
    public static void setUserData(Stage stage, UserDataType userDataType, Object data) {
        var userData = (Map<UserDataType, Object>) stage.getUserData();
        if (userData == null) {
            userData = new HashMap<>();
            stage.setUserData(userData);
        }

        userData.put(userDataType, data);
    }

    @SuppressWarnings("unchecked")
    public static Optional<Object> getUserData(Stage stage, UserDataType userDataType) {
        var userData = (Map<UserDataType, Object>) stage.getUserData();
        if (userData == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(userData.get(userDataType));
    }
}
