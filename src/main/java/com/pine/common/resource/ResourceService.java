package com.pine.common.resource;

import com.pine.common.Loggable;

import java.util.UUID;

public interface ResourceService<T extends IResource, R extends IResourceRuntimeData, C extends IResourceCreationData> extends Loggable {

    /**
     * Bind with custom data
     *
     * @param instance Resource
     * @param data     Data object
     */
    void bind(T instance, R data);

    /**
     * Bind with no data
     *
     * @param instance Resource
     */
    void bind(T instance);

    /**
     * Unbind last resource bound of that type
     */
    void unbind();

    IResource add(C data);

    void remove(T resource);

    default String getId() {
        return UUID.randomUUID().toString();
    }
}
