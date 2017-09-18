package com.blueberry.crash;

/**
 * Created by blueberry on 9/18/2017.
 */

public interface GlobalErrorHandler {
    void handlerError(String header, Throwable throwable);
}
