#include "../buffer_objects/GLOBAL_DATA_UBO.glsl"
#include "../util/UTIL.glsl"
#include "../util/TERRAIN.glsl"

uniform vec4 tilesScaleTranslation;
uniform int textureSize;
uniform float heightScale;

layout (binding = 8) uniform sampler2D heightMap;

flat out int rIndex;

void main() {
    rIndex = 1;

    vec3 position = computePosition(tilesScaleTranslation);
    vec2     initialUV = vec2(position.x/textureSize + .5, position.z/textureSize + .5);
    position.y = texture(heightMap, initialUV).r * heightScale;

    gl_Position = viewProjection * vec4(position, 1);
}
