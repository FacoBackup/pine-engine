#include "../buffer_objects/GLOBAL_DATA_UBO.glsl"
#include "../util/UTIL.glsl"
#include "../util/TERRAIN.glsl"

uniform vec4 tilesScaleTranslation;
uniform int textureSize;
uniform float heightScale;

layout (binding = 8) uniform sampler2D heightMap;

out mat4 invModelMatrix;
flat out int isDecalPass;
flat out int renderingIndex;
smooth out vec2 initialUV;
smooth out vec3 normalVec;
smooth out vec3 worldSpacePosition;

void main() {
    isDecalPass = 0;
    invModelMatrix = mat4(0);

    renderingIndex = int(tilesScaleTranslation.z + tilesScaleTranslation.w);

    TerrainData terrain = computePosition(tilesScaleTranslation, textureSize, heightMap, heightScale);
    initialUV = terrain.uv;

    normalVec = getNormalFromHeightMap(terrain.position.y, heightScale, heightMap, initialUV);

    worldSpacePosition = terrain.position;
    gl_Position = viewProjection * vec4(worldSpacePosition, 1);
}
