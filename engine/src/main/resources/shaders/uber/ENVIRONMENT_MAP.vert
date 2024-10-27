layout (location = 0) in vec3 position;
layout (location = 1) in vec2 uv;
layout (location = 2) in vec3 normal;

uniform mat4 model;
uniform mat4 viewProjection;

smooth out vec2 initialUV;
smooth out vec3 normalVec;
smooth out vec3 worldSpacePosition;

void main() {
    vec4 wPosition = model * vec4(position, 1.0);
    worldSpacePosition = wPosition.xyz;
    normalVec = normalize(mat3(model) * normal);
    initialUV = uv;

    gl_Position = viewProjection * wPosition;
}
