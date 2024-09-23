flat in int renderingIndex;
in float depthFunc;

layout (location = 0) out vec4 v_depth_velocity;
layout (location = 1) out int v_entity;

float encode() {
    float half_co = depthFunc * 0.5;
    float clamp_z = max(0.000001, gl_FragCoord.z);
    return log2(clamp_z) * half_co;
}

void main() {
    v_depth_velocity = vec4(encode(), 0., 0., 1.);
    v_entity = renderingIndex;
}