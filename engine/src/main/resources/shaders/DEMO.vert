layout(location = 0) in vec3 position;

#include "./TRANSFORMATION_SSBO.glsl"

#include "./CAMERA_VIEW_INFO.glsl"

uniform int transformationIndex;

mat4 createModelMatrix(vec3 translation, vec3 scale, vec3 rotation);

out float instance;

void main(){
    int actualIndex = (transformationIndex + gl_InstanceID) * 9;
    instance = float(gl_InstanceID);
    vec3 translation = vec3(transformation[actualIndex], transformation[actualIndex + 1], transformation[actualIndex + 2]);
    vec3 rotation = vec3(transformation[actualIndex + 3], transformation[actualIndex + 4], transformation[actualIndex + 5]);
    vec3 scale  = vec3(transformation[actualIndex + 6], transformation[actualIndex + 7], transformation[actualIndex + 8]);
    gl_Position = viewProjection * createModelMatrix(translation, rotation, scale) * vec4(position, 1.0);
}

mat4 createModelMatrix(vec3 translation, vec3 rotation, vec3 scale) {
    // Create translation matrix
    mat4 translationMatrix = mat4(1.0);
    translationMatrix[3] = vec4(translation, 1.0);

    // Create scale matrix
    mat4 scaleMatrix = mat4(1.0);
    scaleMatrix[0][0] = scale.x;
    scaleMatrix[1][1] = scale.y;
    scaleMatrix[2][2] = scale.z;

    // Create rotation matrices for each axis (rotation in radians)
    float cosX = cos(rotation.x);
    float sinX = sin(rotation.x);
    float cosY = cos(rotation.y);
    float sinY = sin(rotation.y);
    float cosZ = cos(rotation.z);
    float sinZ = sin(rotation.z);

    mat4 rotationX = mat4(1.0);
    rotationX[1][1] = cosX;
    rotationX[1][2] = -sinX;
    rotationX[2][1] = sinX;
    rotationX[2][2] = cosX;

    mat4 rotationY = mat4(1.0);
    rotationY[0][0] = cosY;
    rotationY[0][2] = sinY;
    rotationY[2][0] = -sinY;
    rotationY[2][2] = cosY;

    mat4 rotationZ = mat4(1.0);
    rotationZ[0][0] = cosZ;
    rotationZ[0][1] = -sinZ;
    rotationZ[1][0] = sinZ;
    rotationZ[1][1] = cosZ;

    // Combine rotation matrices
    mat4 rotationMatrix = rotationZ * rotationY * rotationX;

    // Combine all transformations: translation, rotation, and scale
    mat4 modelMatrix = translationMatrix * rotationMatrix * scaleMatrix;

    return modelMatrix;
}