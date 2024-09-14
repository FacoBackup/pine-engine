package com.pine.engine.core.service.loader.impl.info;

public record LoadRequest(String path, boolean isStaticResource, AbstractLoaderExtraInfo extraInfo) {
}
