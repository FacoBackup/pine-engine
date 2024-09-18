package com.pine.core.service.loader.impl.info;

public record LoadRequest(String path, boolean isStaticResource, AbstractLoaderExtraInfo extraInfo) {
}
