package com.tms.csp.util;

public class Control<T> {

    private T result;
    private boolean stop;

    public Control(T result) {
        this.result = result;
    }

    public Control() {
        this.result = null;
    }

    public void stop() {
        stop = true;
    }

    public void stop(T result) {
        stop = true;
        this.result = result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public boolean isStopped() {
        return stop;
    }

    public T getResult() {
        return result;
    }
}
