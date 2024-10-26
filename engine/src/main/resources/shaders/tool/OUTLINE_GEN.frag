out vec4 finalColor;

uniform int renderIndex;

void main(){
    finalColor = vec4(renderIndex);
}