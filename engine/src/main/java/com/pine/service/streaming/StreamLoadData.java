package com.pine.service.streaming;

import com.pine.repository.streaming.StreamableResourceType;

import java.io.Serializable;

public interface StreamLoadData extends Serializable {
    StreamableResourceType getResourceType();
}
