layout (location = 0) in vec3 position;

#include "../buffer_objects/CAMERA_VIEW_INFO.glsl"

uniform int planeSize;
uniform float heightScale;

uniform sampler2D heightMap;

flat out vec3 cameraPlacement;
flat out int renderingIndex;
flat out float depthFunc;
smooth out vec2 initialUV;
smooth out vec3 normalVec;
smooth out vec3 worldSpacePosition;


const float normalOffset = .005;

void main() {
    cameraPlacement = placement.xyz;
    renderingIndex = 1;
    depthFunc = logDepthFC;


    initialUV = vec2(position.x / planeSize + 0.5, position.z / planeSize + 0.5);

    // Sample the height map at the current position
    float height = texture(heightMap, initialUV).r ;

    // Offset texture coordinates to sample neighboring points
    float heightL = texture(heightMap, initialUV + vec2(-normalOffset, 0.0)).r;
    float heightR = texture(heightMap, initialUV + vec2(normalOffset, 0.0)).r;
    float heightD = texture(heightMap, initialUV + vec2(0.0, -normalOffset)).r;
    float heightU = texture(heightMap, initialUV + vec2(0.0, normalOffset)).r;

    // Compute tangent vectors
    // Compute tangent vectors with height affecting the Y axis
    vec3 dx = vec3(2.0 * normalOffset, heightR - heightL, 0.0);
    vec3 dz = vec3(0.0, heightU - heightD, 2.0 * normalOffset);

    // Compute the normal as the cross product of dx and dz
    normalVec = normalize(cross(dz, dx));

    worldSpacePosition = position;
    worldSpacePosition.y = height * heightScale;
    gl_Position = viewProjection * vec4(worldSpacePosition, 1);
}
