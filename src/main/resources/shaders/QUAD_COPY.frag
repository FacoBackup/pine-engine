layout(binding = 0) uniform sampler2D origin;

in vec2 texCoords;
out vec4 finalColor;

void main(){
    finalColor = texture(origin, texCoords);
}