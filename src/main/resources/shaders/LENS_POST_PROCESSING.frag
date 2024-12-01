in vec2 texCoords;
#define A  0.15
#define B  0.50
#define C  0.10
#define D  0.20
#define E  0.02
#define F  0.30
#define W  11.2

uniform float distortionIntensity;
uniform float chromaticAberrationIntensity;
uniform bool distortionEnabled;
uniform bool chromaticAberrationEnabled;
uniform bool bloomEnabled;
uniform float focusDistanceDOF;
uniform float apertureDOF;
uniform float focalLengthDOF;
uniform float samplesDOF;
uniform bool vignetteEnabled;
uniform float vignetteStrength;

layout(binding = 0)uniform sampler2D bloomColor;
layout(binding = 1)uniform sampler2D sceneColor;

out vec4 fragColor;

vec3 chromaticAberration(vec2 uv) {
    float amount = chromaticAberrationIntensity * .001;
    vec3 col;
    col.r = texture(sceneColor, vec2(uv.x + amount, uv.y)).r;
    col.g = texture(sceneColor, uv).g;
    col.b = texture(sceneColor, vec2(uv.x - amount, uv.y)).b;
    return col;
}
vec2 lensDistortion(vec2 uv, float k) {
    vec2 t = uv - .5;
    float r2 = t.x * t.x + t.y * t.y;
    float f = 1. + r2 * (.1 - k * sqrt(r2));

    vec2 nUv = f * t + .5;
    return nUv;
}

// https://github.com/KhronosGroup/ToneMapping/blob/main/PBR_Neutral/pbrNeutral.glsl
vec3 PBRNeutralToneMapping( vec3 color ) {
    const float startCompression = 0.8 - 0.04;
    const float desaturation = 0.15;

    float x = min(color.r, min(color.g, color.b));
    float offset = x < 0.08 ? x - 6.25 * x * x : 0.04;
    color -= offset;

    float peak = max(color.r, max(color.g, color.b));
    if (peak < startCompression) return color;

    const float d = 1. - startCompression;
    float newPeak = 1. - d * d / (peak + d - startCompression);
    color *= newPeak / peak;

    float g = 1. - 1. / (desaturation * (peak - newPeak) + 1.);
    return mix(color, newPeak * vec3(1, 1, 1), g);
}

vec3 aces( vec3 x ) {
    // Narkowicz 2015, "ACES Filmic Tone Mapping Curve"
    const float a = 2.51;
    const float b = 0.03;
    const float c = 2.43;
    const float d = 0.59;
    const float e = 0.14;
    return (x * (a * x + b)) / (x * (c * x + d) + e);
}

void main(void) {

    vec2 texCoords = distortionEnabled ? lensDistortion(texCoords, distortionIntensity * .5) : texCoords;
    vec3 color = bloomEnabled ? aces(texture(bloomColor, texCoords).rgb) : vec3(0.);
    color += chromaticAberrationEnabled ? chromaticAberration(texCoords) : texture(sceneColor, texCoords).rgb;
    fragColor = vec4(aces(color), 1.);

    if (vignetteEnabled) {
        vec2 uv = texCoords;
        uv *= 1.0 - uv.yx;
        float vig = pow(uv.x * uv.y * 15., vignetteStrength);
        fragColor.rgb *= vig;
    }
}