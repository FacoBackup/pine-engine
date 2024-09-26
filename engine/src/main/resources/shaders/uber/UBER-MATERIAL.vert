layout (location = 0) in vec3 position;
layout (location = 1) in vec2 uvTexture;
layout (location = 2) in vec3 normal;

#include "../buffer_objects/MODEL_SSBO.glsl"

#include "../buffer_objects/CAMERA_VIEW_INFO.glsl"

uniform int transformationIndex;

uniform mat4 materialAttributes;
uniform bool isDecalPass;

out flat int renderIndex;
out vec3 naturalNormal;
out vec3 worldPosition;
out vec2 naturalTextureUV;
out mat4 invModelMatrix;

void main(){
    renderIndex = (transformationIndex + gl_InstanceID);
    mat4 modelMatrix = modelMatrices[renderIndex];

    vec4 wPosition = modelMatrix * vec4(position, 1.0);
    invModelMatrix = isDecalPass ? inverse(modelMatrix) : mat4(0.);
    worldPosition = wPosition.xyz;
    naturalNormal = normalize(mat3(modelMatrix) * normal);
    naturalTextureUV = uvTexture;

    gl_Position = viewProjection * wPosition;
}
