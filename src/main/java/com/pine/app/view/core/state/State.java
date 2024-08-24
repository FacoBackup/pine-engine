package com.pine.app.view.core.state;

import java.io.Serializable;

public class State<T> implements Serializable {
    T state;

    public State(T state) {
        this.state = state;
    }

    public T getState() {
        return state;
    }

    public void setState(T state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return state.toString();
    }
}
