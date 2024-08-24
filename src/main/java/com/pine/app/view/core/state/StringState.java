package com.pine.app.view.core.state;

import imgui.type.ImString;

public class StringState extends State<ImString> {
    public StringState(int maxLength) {
        super(new ImString(maxLength));
    }

    public StringState setState(String state) {
        super.setState(new ImString(state));
        return this;
    }

    @Override
    public String toString() {
        return state.get();
    }
}
