void transformMat4(mat4 m, vec3 a) {
    float i0 = m[0][0];
    float i1 =  m[1][0];
    float i2 =  m[2][0];
    float i3 = m[3][0];
    float i4 = m[0][1];
    float i5 =  m[1][1];
    float i6 =  m[2][1];
    float i7 = m[3][1];
    float i8 = m[0][2];
    float i9 =  m[1][2];
    float i10 =  m[2][2];
    float i11 = m[3][2];
    float i12 = m[0][3];
    float i13 =  m[1][3];
    float i14 =  m[2][3];
    float i15 = m[3][3];

    float x = a.x,
    y = a.y,
    z = a.z;

    float w = i3 * x + i7 * y + i11 * z + i15;
    w = w == 0. ? 1.0 : w;
    i0 = (i0 * x + i4 * y + i8 * z + i12) / w;
    i1 = (i1 * x + i5 * y + i9 * z + i13) / w;
    i2 = (i2 * x + i6 * y + i10 * z + i14) / w;

    m[0][0] = i0;
    m[1][0] = i1;
    m[2][0] = i2;
    m[3][0] = i3;
    m[0][1] = i4;
    m[1][1] = i5;
    m[2][1] = i6;
    m[3][1] = i7;
    m[0][2] = i8;
    m[1][2] = i9;
    m[2][2] = i10;
    m[3][2] = i11;
    m[0][3] = i12;
    m[1][3] = i13;
    m[2][3] = i14;
    m[3][3] = i15;
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