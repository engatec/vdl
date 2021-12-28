package com.github.engatec.vdl.exception;

import javafx.concurrent.Service;

public class ServiceStubException extends RuntimeException {

    public ServiceStubException(Class<? extends Service<?>> serviceClass) {
        super("Service '" + serviceClass.getSimpleName() + "' has failed without an exception.");
    }
}
