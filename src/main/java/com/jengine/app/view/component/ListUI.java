package com.jengine.app.view.component;

import com.jengine.app.view.core.state.State;

import java.util.List;

public abstract class ListUI<T> extends AbstractUI<State<List<T>>>{
    public ListUI(List<T> state) {
        super(new State<>(state));
    }

    @Override
    public void render() {
        List<T> state = this.state.getState();
        for(int i = 0; i < state.size(); i++){
            renderRow(state.get(i), i);
        }
    }

    public abstract void renderRow(T row, int index);
}
