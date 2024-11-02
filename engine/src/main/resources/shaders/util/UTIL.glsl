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

vec3 getNormalFromHeightMap(inout float height, sampler2D heightMap, inout vec2 uv, inout float normalOffset){

    // Offset texture coordinates to sample neighboring points
    float heightL = texture(heightMap, uv + vec2(-normalOffset, 0.0)).r;
    float heightR = texture(heightMap, uv + vec2(normalOffset, 0.0)).r;
    float heightD = texture(heightMap, uv + vec2(0.0, -normalOffset)).r;
    float heightU = texture(heightMap, uv + vec2(0.0, normalOffset)).r;

    // Compute tangent vectors
    // Compute tangent vectors with height affecting the Y axis
    vec3 dx = vec3(2.0 * normalOffset, heightR - heightL, 0.0);
    vec3 dz = vec3(0.0, heightU - heightD, 2.0 * normalOffset);

    // Compute the normal as the cross product of dx and dz
    return normalize(cross(dz, dx));
}