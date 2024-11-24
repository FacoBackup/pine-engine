uniform GlobalData{
    mat4 viewProjection;
    mat4 viewMatrix;
    mat4 invViewMatrix;
    vec4 cameraWorldPosition;
    mat4 projectionMatrix;
    mat4 invProjectionMatrix;
    vec2 bufferResolution;
    float logDepthFC;
    float timeOfDay;
    vec4 sunLightDirection;
    vec3 sunLightColor;
    float sunShadowsResolution;
    mat4 lightSpaceMatrix;
};