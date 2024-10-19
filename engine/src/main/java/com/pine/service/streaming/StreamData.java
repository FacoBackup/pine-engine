package com.pine.service.streaming;

import com.pine.repository.streaming.StreamableResourceType;

import java.io.Serializable;
import java.util.UUID;

public interface StreamData {
    StreamableResourceType getResourceType();
}
