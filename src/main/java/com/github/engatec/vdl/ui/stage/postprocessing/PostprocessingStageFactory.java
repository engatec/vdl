package com.github.engatec.vdl.ui.stage.postprocessing;

import java.util.function.Consumer;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.model.postprocessing.FragmentCutPostprocessing;
import com.github.engatec.vdl.model.postprocessing.Postprocessing;
import com.github.engatec.vdl.ui.stage.AppStage;

public enum PostprocessingStageFactory {

    FRAGMENT("stage.postprocessing.fragment", FragmentCutPostprocessing.class) {
        @Override
        public AppStage create(Postprocessing model, Consumer<? super Postprocessing> okClickCallback) {
            return new FragmentStage((FragmentCutPostprocessing) model, okClickCallback);
        }
    };

    private final String labelKey;
    private final Class<? extends Postprocessing> postprocessingClass;

    PostprocessingStageFactory(String labelKey, Class<? extends Postprocessing> postprocessingClass) {
        this.labelKey = labelKey;
        this.postprocessingClass = postprocessingClass;
    }

    public abstract AppStage create(Postprocessing model, Consumer<? super Postprocessing> okClickCallback);

    public Class<? extends Postprocessing> getPostprocessingClass() {
        return postprocessingClass;
    }

    @Override
    public String toString() {
        return ApplicationContext.INSTANCE.getResourceBundle().getString(labelKey);
    }
}
