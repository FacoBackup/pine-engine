package com.pine.service.resource.fbo;

public record FBOTextureData(Integer w, Integer h,
                             int attachment, int precision,
                             int format, int type,
                             boolean linear, boolean repeat) {
}
