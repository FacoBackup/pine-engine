in vec2 texCoords;
out vec4 FragColor;

layout(binding = 0) uniform sampler2D heightMap;

uniform vec4 color1;
uniform vec4 color2;
uniform vec4 color3;
uniform vec4 color4;

void main() {
    float height = texture(heightMap, texCoords).r;

    float t1 = 0.33;
    float t2 = 0.66;

    vec4 blendedColor;

    if (height < t1) {
        float factor = height / t1;
        blendedColor = mix(color1, color2, factor);
    }
    else if (height < t2) {
        float factor = (height - t1) / (t2 - t1);
        blendedColor = mix(color2, color3, factor);
    }
    else {
        float factor = (height - t2) / (1.0 - t2);
        blendedColor = mix(color3, color4, factor);
    }

    FragColor = blendedColor;
}