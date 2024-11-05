layout (location = 0) in vec3 position;

#include "../buffer_objects/CAMERA_VIEW_INFO.glsl"
#include "../util/UTIL.glsl"

uniform int planeSize;
uniform float heightScale;

uniform sampler2D heightMap;

flat out vec3 cameraPlacement;
flat out int renderingIndex;
flat out float depthFunc;
smooth out vec2 initialUV;
smooth out vec3 normalVec;
smooth out vec3 worldSpacePosition;


void main() {

    cameraPlacement = placement.xyz;
    renderingIndex = 1;
    depthFunc = logDepthFC;


    initialUV = vec2(position.x / planeSize + 0.5, position.z / planeSize + 0.5);

    float height = texture(heightMap, initialUV).r;
    normalVec = getNormalFromHeightMap(height, heightMap, initialUV);

    worldSpacePosition = position;
    worldSpacePosition.y = height * heightScale;
    gl_Position = viewProjection * vec4(worldSpacePosition, 1);
}
