out vec4 finalColor;

flat in int rIndex;

void main(){
    finalColor = vec4(rIndex);
}