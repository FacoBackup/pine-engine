package com.pine.app.view.core.state;

import imgui.type.ImString;

public class ConstStringState extends State<String> {
    public ConstStringState(String state) {
        super(state);
    }

    public static ConstStringState of(String val) {
        return new ConstStringState(val);
    }
}
