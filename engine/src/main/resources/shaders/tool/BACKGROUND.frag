in vec2 texCoords;

layout(binding = 0) uniform sampler2D depth;

uniform vec3 color;

out vec4 finalColor;

void main(){
    if (texture(depth, texCoords).r != 0){
        discard;
    }
    finalColor = vec4(color, 1);
}