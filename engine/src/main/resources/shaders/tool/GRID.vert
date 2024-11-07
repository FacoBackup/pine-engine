layout (location = 0) in vec3 position;

#include "./buffer_objects/GLOBAL_DATA_UBO.glsl"

out vec3 worldPosition;
out vec3 cameraPosition;
uniform vec4 settings;

void main(){

    cameraPosition = cameraWorldPosition.xyz;
    worldPosition = position * max(10., settings.z) + vec3(cameraWorldPosition.x, 0., cameraWorldPosition.z);
    gl_Position = viewProjection * vec4(worldPosition, 1.);
}