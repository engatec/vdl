package com.github.engatec.vdl.handler;

import javafx.event.EventHandler;
import javafx.scene.control.Slider;
import javafx.scene.input.ScrollEvent;

public class SliderMouseScrollHandler implements EventHandler<ScrollEvent> {

    @Override
    public void handle(ScrollEvent event) {
        if (event.getSource() instanceof Slider src) {
            if (event.getDeltaY() < 0) {
                src.decrement();
            } else if (event.getDeltaY() > 0) {
                src.increment();
            }
        }
        event.consume();
    }
}
