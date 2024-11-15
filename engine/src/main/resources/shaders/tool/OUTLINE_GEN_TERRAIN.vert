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

    TerrainData terrain = computePosition(tilesScaleTranslation, textureSize, heightMap, heightScale);
    gl_Position = viewProjection * vec4(terrain.position, 1);
}
