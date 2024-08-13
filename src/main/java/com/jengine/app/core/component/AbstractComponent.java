package com.jengine.app.core.component;

import com.artemis.Component;

import java.util.List;

public abstract class AbstractComponent extends Component{
    public abstract List<Class<? extends AbstractComponent>> getDependencies();
}
