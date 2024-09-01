package com.pine.app.core.state;

import imgui.type.ImString;

public class StringState extends State<ImString> {
    public StringState(int maxLength) {
        super(new ImString(maxLength));
    }

    public StringState setState(String state) {
        getState().set(state);
        return this;
    }

    @Override
    public String toString() {
        return state.get();
    }

    public String get() {
        return new String(state.getData());
    }
}
