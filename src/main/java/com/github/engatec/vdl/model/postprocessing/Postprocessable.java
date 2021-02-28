package com.github.engatec.vdl.model.postprocessing;

import java.util.List;

public interface Postprocessable {

    List<Postprocessing> getPostprocessingSteps();

    void setPostprocessingSteps(List<Postprocessing> items);
}
