in vec2 texCoords;

layout (binding = 0) uniform sampler2D sceneDepth;

uniform vec3 color;
uniform vec2 viewportOrigin;
uniform vec2 viewportSize;
uniform vec3 radiusDensityMode;
uniform vec3 xyMouse;


#include "../util/SCENE_DEPTH_UTILS.glsl"
#include "../util/UTIL.glsl"


out vec4 finalColor;

void main(){
    // RECONSTRUCT POSITION BASED ON MOUSE POSITION
    vec2 textureCoord = (xyMouse.xy + viewportOrigin) / viewportSize;
    float depthData = getLogDepth(textureCoord);
    if (depthData == 1.){
        discard;
    }
    vec3 viewSpacePositionMouse = viewSpacePositionFromDepth(depthData, textureCoord);
    vec3 worldSpacePositionMouse = vec3(invViewMatrix * vec4(viewSpacePositionMouse, 1.));


    // RECONSTRUCT POSITION BASED ON FRAGMENT POSITION
    textureCoord = texCoords;
    vec4 depthIdUVData = texture(sceneDepth, textureCoord);
    depthData = getLogDepthFromSampler(depthIdUVData);
    if (depthData == 1.){
        discard;
    }
    vec3 viewSpacePosition = viewSpacePositionFromDepth(depthData, textureCoord);
    vec3 worldSpacePosition = vec3(invViewMatrix * vec4(viewSpacePosition, 1.));

    worldSpacePositionMouse.y = worldSpacePosition.y = 0;

    // COMPARE THE DISTANCE BETWEEN THE PIXEL WORLD POSITION AND THE MOUSE WORLD POSITION
    float distToCenter = length(worldSpacePosition - worldSpacePositionMouse);
    if (distToCenter > radiusDensityMode.r) {
        discard;
    }

    if(distToCenter > radiusDensityMode.r - .35){
        finalColor = vec4(1, 1, 1, radiusDensityMode.y);
    }else{
        finalColor = vec4(.2, .2, .2, .35);
    }
}