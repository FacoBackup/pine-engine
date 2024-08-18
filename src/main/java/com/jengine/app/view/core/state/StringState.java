package com.jengine.app.view.core.state;

import imgui.type.ImString;

public class StringState extends State<ImString> {
    public StringState(int maxLength) {
        super(new ImString(maxLength));
    }

    @Override
    public String toString() {
        return state.get();
    }
}
