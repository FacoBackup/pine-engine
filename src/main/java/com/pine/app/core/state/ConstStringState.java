package com.pine.app.core.state;

public class ConstStringState extends State<String> {
    public ConstStringState(String state) {
        super(state);
    }

    public static ConstStringState of(String val) {
        return new ConstStringState(val);
    }
}
