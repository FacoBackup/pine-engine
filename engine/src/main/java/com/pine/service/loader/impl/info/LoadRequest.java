package com.pine.service.loader.impl.info;

public record LoadRequest(String path, boolean isStaticResource, AbstractLoaderExtraInfo extraInfo) {
}
