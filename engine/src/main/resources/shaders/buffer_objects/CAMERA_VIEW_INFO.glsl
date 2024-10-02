uniform CameraViewInfo{
    mat4 viewProjection;
    mat4 viewMatrix;
    mat4 invViewMatrix;
    vec4 placement;
    mat4 projectionMatrix;
    mat4 invProjectionMatrix;
    vec2 bufferResolution;
    float logDepthFC;
};