package com.pine.engine.core.component;

import java.util.List;

public class UIComponent extends AbstractComponent{
    @Override
    public List<Class<? extends AbstractComponent>> getDependencies() {
        return List.of();
    }
}
