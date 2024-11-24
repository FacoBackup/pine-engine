mat3 getRotationFromNormal(inout vec3 normal){
    const vec3 UP_VEC = vec3(0.0, 1.0, 0.0);// Default "up" direction

    vec3 axis = cross(UP_VEC, normal);// Axis to rotate around
    float angle = acos(dot(UP_VEC, normal));// Angle between up and direction

    // Use Rodrigues' rotation formula components
    float cosA = cos(angle);
    float sinA = sin(angle);
    return mat3(
        cosA + axis.x * axis.x * (1.0 - cosA), axis.x * axis.y * (1.0 - cosA) - axis.z * sinA, axis.x * axis.z * (1.0 - cosA) + axis.y * sinA,
        axis.y * axis.x * (1.0 - cosA) + axis.z * sinA, cosA + axis.y * axis.y * (1.0 - cosA), axis.y * axis.z * (1.0 - cosA) - axis.x * sinA,
        axis.z * axis.x * (1.0 - cosA) - axis.y * sinA, axis.z * axis.y * (1.0 - cosA) + axis.x * sinA, cosA + axis.z * axis.z * (1.0 - cosA)
    );
}

vec3 getNormalFromHeightMap(float heightScale, sampler2D heightMap, vec2 texCoords){
    vec2 texelSize = 1./vec2(textureSize(heightMap, 0));
    float hL = texture(heightMap, texCoords - vec2(texelSize.x, 0.0)).r;
    float hR = texture(heightMap, texCoords + vec2(texelSize.x, 0.0)).r;
    float hD = texture(heightMap, texCoords - vec2(0.0, texelSize.y)).r;
    float hU = texture(heightMap, texCoords + vec2(0.0, texelSize.y)).r;

    float dx = (hR - hL) * heightScale;
    float dz = (hU - hD) * heightScale;

    return normalize(vec3(-dx, heightScale * heightScale * texelSize.x/2., -dz));
}