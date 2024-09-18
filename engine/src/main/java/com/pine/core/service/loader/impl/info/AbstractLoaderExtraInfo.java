package com.pine.core.service.loader.impl.info;

import com.pine.core.service.resource.resource.ResourceType;

/**
 * Used for defining limits in the load process, like, only load mesh number N
 */
public abstract class AbstractLoaderExtraInfo {

    private boolean silentOperation = false;

    public AbstractLoaderExtraInfo setSilentOperation(boolean silentOperation) {
        this.silentOperation = silentOperation;
        return this;
    }

    public boolean isSilentOperation() {
        return silentOperation;
    }

    public abstract ResourceType getResourceType();
}
