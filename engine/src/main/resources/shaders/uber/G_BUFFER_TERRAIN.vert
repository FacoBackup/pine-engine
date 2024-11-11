#include "../buffer_objects/GLOBAL_DATA_UBO.glsl"
#include "../util/UTIL.glsl"

uniform int planeSize;
uniform float heightScale;
uniform vec2 terrainLocation;

layout (binding = 8) uniform sampler2D heightMap;

flat out vec3 cameraPlacement;
flat out int renderingIndex;
flat out float depthFunc;
smooth out vec2 initialUV;
smooth out vec3 normalVec;
smooth out vec3 worldSpacePosition;

void main() {
    cameraPlacement = cameraWorldPosition.xyz;
    renderingIndex = 1;
    depthFunc = logDepthFC;

    const float VERTICES_PER_TILE = 6.0;
    const float TILES_PER_RUN = float(planeSize);
    const float VERTICES_PER_CHUNK = TILES_PER_RUN * TILES_PER_RUN * VERTICES_PER_TILE;

    float index = mod(float(gl_VertexID), VERTICES_PER_CHUNK);

    float zPos = mod(floor(index / VERTICES_PER_TILE), TILES_PER_RUN);
    float xPos = floor(index / (TILES_PER_RUN * VERTICES_PER_TILE));

    // Create a triangle
    int triangleID = int(mod(index, VERTICES_PER_TILE));
    if (triangleID == 1 || triangleID == 3 || triangleID == 4){
        zPos++;
    }
    if (triangleID == 2 || triangleID == 4 || triangleID == 5){
        xPos++;
    }

    vec3 position = vec3(xPos, 0.0, zPos);

    position.x += terrainLocation.x;
    position.z += terrainLocation.y;

    initialUV = vec2(position.x/planeSize + 0.5, position.z/planeSize  + 0.5);

    float height = texture(heightMap, initialUV).r;
    normalVec = getNormalFromHeightMap(height, heightMap, initialUV);

    worldSpacePosition = position;
    worldSpacePosition.y = height * heightScale;
    gl_Position = viewProjection * vec4(worldSpacePosition, 1);
}
