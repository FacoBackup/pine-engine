package com.pine.service.loader.impl.response;

import java.io.Serializable;

public record MeshInstanceMetadata(String name, String path, int index, String id) implements Serializable {
}
