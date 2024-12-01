in vec2 texCoords;
out vec4 finalColor;

layout (binding = 0) uniform sampler2D primary;
layout (binding = 1) uniform sampler2D depth;
layout (binding = 2) uniform sampler2D secondary;

void main(){
    float d= texture(depth, texCoords).r;
    if(d != 0){
        finalColor = vec4(texture(primary, texCoords).rgb, 1);
    }else {
        vec3 s = texture(secondary, texCoords).rgb;
        float sLength = length(s);
        finalColor = vec4(s, 1);
    }
}