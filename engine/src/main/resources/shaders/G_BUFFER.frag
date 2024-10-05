flat in int renderingIndex;
in float depthFunc;
in vec3 normal;
in vec2 uv;

layout (location = 0) out vec3 gBufferMetallicRoughnessAO;
layout (location = 1) out vec4 gBufferAlbedoEmissive;
layout (location = 2) out vec3 gBufferNormal;
layout (location = 3) out float depth;

float encode() {
    float half_co = depthFunc * 0.5;
    float clamp_z = max(0.000001, gl_FragCoord.z);
    return log2(clamp_z) * half_co;
}

void main() {
    gBufferMetallicRoughnessAO = vec3(1., 1., 0.);
    gBufferAlbedoEmissive = vec4(1.);
    gBufferNormal = normal;
    depth = encode();
}