package com.pine.engine.core.service.loader;

import com.pine.engine.core.service.loader.impl.info.ILoaderExtraInfo;

public record LoadRequest(String path, boolean isStaticResource, ILoaderExtraInfo extraInfo) {
}
