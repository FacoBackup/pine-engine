package com.pine.common.resource;

import com.pine.common.Loggable;

import java.util.List;

public interface IResourceService extends Loggable {
    <C extends IResourceCreationData> IResource addResource(C data);

    void removeResource(String id);

    <T extends IResource, R extends IResourceRuntimeData> void bind(T instance, R data);

    <T extends IResource> void bind(T instance);

    List<IResource> getAllByType(ResourceType type);
}
