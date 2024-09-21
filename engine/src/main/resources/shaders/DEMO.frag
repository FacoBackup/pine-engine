out vec4 fragColor;

in float instance;

void main() {
    fragColor = vec4(vec3(instance)/20., 1.);
}
