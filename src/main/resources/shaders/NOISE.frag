in vec2 texCoords;
out vec4 fragColor;

#include "./util/NOISE.glsl"

#include "./buffer_objects/GLOBAL_DATA_UBO.glsl"

uniform vec3 settings;

#define AMPLITUDE settings.x
#define FREQUENCY settings.y
#define STRENGTH settings.z
// Pseudo-random number generator
float random(vec2 uv) {
    return fract(sin(dot(uv.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

// Simplex noise function (or use a library for more advanced noise)
float noiseGen(vec2 uv) {
    vec2 p = floor(uv);
    vec2 f = fract(uv);
    f = f * f * (3.0 - 2.0 * f);

    float n = p.x + p.y * 57.0;
    float res = mix(
    mix(random(vec2(n + 0.0)), random(vec2(n + 1.0)), f.x),
    mix(random(vec2(n + 57.0)), random(vec2(n + 58.0)), f.x),
    f.y
    );

    return res;
}

void main(){
    float added = texCoords.x + texCoords.y;
    float noise = (noiseGen(texCoords) * AMPLITUDE + timeOfDay);
    float wave = sin((added + noise) * FREQUENCY);
    fragColor = vec4(vec2(wave) * STRENGTH, 0, 1);
}