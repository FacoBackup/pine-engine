package com.pine.service.resource.fbo;

public record FBOTextureData(int attachment, int precision,
                             int format, int type,
                             boolean linear, boolean repeat) {
}
