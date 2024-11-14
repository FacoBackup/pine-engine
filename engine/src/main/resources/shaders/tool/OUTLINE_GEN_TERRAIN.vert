#include "../buffer_objects/GLOBAL_DATA_UBO.glsl"
#include "../util/UTIL.glsl"
#include "../util/TERRAIN.glsl"

uniform int planeSize;
uniform float heightScale;
uniform vec2 terrainLocation;

layout (binding = 8) uniform sampler2D heightMap;

flat out int rIndex;

void main() {
    rIndex = 1;

    vec3 position = computePosition(float(planeSize));

    position.x += terrainLocation.x;
    position.z += terrainLocation.y;

    vec2 initialUV = vec2(position.x/planeSize, position.z/planeSize);
    position.y = texture(heightMap, initialUV).r * heightScale;

    gl_Position = viewProjection * vec4(position, 1);
}
