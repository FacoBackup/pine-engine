flat in int renderingIndex;
in float depthFunc;

layout (location = 0) out vec3 gBufferMetallicRoughnessAO;
layout (location = 1) out vec4 gBufferAlbedoEmissive;



void main() {
    gBufferMetallicRoughnessAO = vec3(1., 1., 0.);
    gBufferAlbedoEmissive = vec4(1.);
}