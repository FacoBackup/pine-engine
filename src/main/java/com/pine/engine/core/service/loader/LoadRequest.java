package com.pine.engine.core.service.loader;

import com.pine.engine.core.service.loader.impl.info.AbstractLoaderExtraInfo;

public record LoadRequest(String path, boolean isStaticResource, AbstractLoaderExtraInfo extraInfo) {
}
