in vec2 texCoords;
uniform sampler2D sceneColor;
uniform float blurRadius;
uniform int samples;
uniform vec2 bufferResolution;
out vec4 fragColor;

#include "./util/STRONG_BLUR.glsl"

void main(){
    fragColor.rgb = blur(sceneColor, texCoords, bufferResolution, samples, blurRadius);
    fragColor.a = 1.;
}