package com.pine.core.service.common;

import com.pine.app.Loggable;

public interface IResourceRepository<R extends IResourceRuntimeData, C extends IResourceCreationData> extends Loggable {

    /**
     * Bind with custom data
     * @param id Resource ID
     * @param data Data object
     */
    void bind(String id, R data);

    /**
     * Bind with no data
     * @param id Resource ID
     */
    void bind(String id);

    /**
     * Unbind last resource bound of that type
     */
    void unbind();

    <T extends IResource> T add(C data);

    void remove(String id);
}
