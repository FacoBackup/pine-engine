out vec4 finalColor;

flat in vec3 translation;
flat in vec3 scale;
flat in int rIndex;

#include "../buffer_objects/GLOBAL_DATA_UBO.glsl"

float sdBoxFrame(vec3 p, vec3 halfSize, float e)
{
    p = abs(p - translation)-halfSize;
    vec3 q = abs(p+e)-e;
    return min(min(
    length(max(vec3(p.x, q.y, q.z), 0.0))+min(max(p.x, max(q.y, q.z)), 0.0),
    length(max(vec3(q.x, p.y, q.z), 0.0))+min(max(q.x, max(p.y, q.z)), 0.0)),
    length(max(vec3(q.x, q.y, p.z), 0.0))+min(max(q.x, max(q.y, p.z)), 0.0));
}


vec3 createRay(vec2 texCoords) {
    vec2 pxNDS = texCoords * 2. - 1.;
    vec3 pointNDS = vec3(pxNDS, -1.);
    vec4 pointNDSH = vec4(pointNDS, 1.0);
    vec4 dirEye = invProjectionMatrix * pointNDSH;
    dirEye.w = 0.;
    vec3 dirWorld = (invViewMatrix * dirEye).xyz;
    return normalize(dirWorld);
}

bool rayMarch(vec3 ro, vec3 rd, vec3 halfSize, float width) {
    float t = 0.0;
    for (int i = 0; i < 256; i++) {
        vec3 p = ro + t * rd;
        float d = sdBoxFrame(p, halfSize, width);
        if (d < 0.001) return true;
        if (t > 100.0) break;
        t += d;
    }
    return false;
}

void main(){
    vec2 texCoords = gl_FragCoord.xy / bufferResolution;
    vec3 rayDir = createRay(texCoords);
    if (!rayMarch(cameraWorldPosition.xyz, rayDir, scale, .025)){
        discard;
    }
    finalColor = vec4(vec3(rIndex), 1);
}