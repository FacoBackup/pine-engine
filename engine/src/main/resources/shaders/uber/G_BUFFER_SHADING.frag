#include "../buffer_objects/LIGHT_METADATA_SSBO.glsl"

#include "../util/SCENE_DEPTH_UTILS.glsl"

uniform sampler2D gBufferAlbedoSampler;
uniform sampler2D gBufferNormalSampler;
uniform sampler2D gBufferRMAOSampler;
uniform sampler2D gBufferMaterialSampler;
uniform sampler2D gBufferDepthSampler;

out vec4 color;

void main() {
    vec2 quadUV = gl_FragCoord.xy / bufferResolution;

    vec3 valueAlbedoSampler = texture(gBufferAlbedoSampler, quadUV).rgb;
    vec3 valueNormalSampler = texture(gBufferNormalSampler, quadUV).rgb;
    vec3 valueRMAOSampler = texture(gBufferRMAOSampler, quadUV).rgb;
    ivec4 valueMaterialSampler = ivec4(texture(gBufferMaterialSampler, quadUV));
    float valueDepthSampler = texture(gBufferDepthSampler, quadUV).r;

    if(valueMaterialSampler.a == 1) { // EMISSION
        color = vec4(valueAlbedoSampler, 1.);
    }else{
        color = vec4(valueAlbedoSampler, 1.);
    }
}


