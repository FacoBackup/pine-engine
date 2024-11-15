#include "../buffer_objects/GLOBAL_DATA_UBO.glsl"
#include "../util/UTIL.glsl"
#include "../util/TERRAIN.glsl"

uniform vec4 tilesScaleTranslation;
uniform int textureSize;
uniform float heightScale;

layout (binding = 8) uniform sampler2D heightMap;

flat out vec3 cameraPlacement;
flat out int renderingIndex;
flat out float depthFunc;
smooth out vec2 initialUV;
smooth out vec3 normalVec;
smooth out vec3 worldSpacePosition;

void main() {
    cameraPlacement = cameraWorldPosition.xyz;
    renderingIndex = int(tilesScaleTranslation.z + tilesScaleTranslation.w);
    depthFunc = logDepthFC;

    vec3 position = computePosition(tilesScaleTranslation);
    initialUV = vec2(position.x/textureSize + .5, position.z/textureSize + .5);

    float height = texture(heightMap, initialUV).r;
    normalVec = getNormalFromHeightMap(height, heightMap, initialUV);

    worldSpacePosition = position;
    worldSpacePosition.y = height * heightScale;
    gl_Position = viewProjection * vec4(worldSpacePosition, 1);
}
