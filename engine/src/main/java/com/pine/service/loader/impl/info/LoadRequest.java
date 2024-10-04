package com.pine.service.loader.impl.info;

import java.io.Serializable;

public record LoadRequest(String path, AbstractLoaderExtraInfo extraInfo) implements Serializable {
}
