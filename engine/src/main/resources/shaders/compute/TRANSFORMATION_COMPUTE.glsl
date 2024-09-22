layout(local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

#include "../LIGHT_TYPE.glsl"

#include "../buffer_objects/TRANSFORMATION_SSBO.glsl"

#include "../buffer_objects/LIGHT_DESCRIPTION_SSBO.glsl"

#include "../buffer_objects/LIGHT_METADATA_SSBO.glsl"

#include "../buffer_objects/MODEL_SSBO.glsl"

#include "../buffer_objects/CAMERA_VIEW_INFO.glsl"

uniform int lightCount;
uniform int entityCount;

#define TO_RAD 0.01745329251
#define INFO_PER_LIGHT 30
#define INFO_PER_ENTITY 9
#define INFO_PER_LIGHT_METADATA 2

#include "../MATH.glsl"


void main() {
    for (int i = 0; i < entityCount; i++){
        int actualIndex = i * INFO_PER_ENTITY;
        vec3 translation = vec3(transformation[actualIndex], transformation[actualIndex + 1], transformation[actualIndex + 2]);
        vec3 rotation = vec3(transformation[actualIndex + 3], transformation[actualIndex + 4], transformation[actualIndex + 5]);
        vec3 scale  = vec3(transformation[actualIndex + 6], transformation[actualIndex + 7], transformation[actualIndex + 8]);
        modelView[i] = viewProjection * createModelMatrix(translation, rotation, scale);
    }

    for (int i = 0; i < lightCount; i++){
        int actualIndex = i * INFO_PER_LIGHT;
        int metadataIndex = i * INFO_PER_LIGHT_METADATA;
        mat4 primaryBuffer = mat4(0.);
        mat4 secondaryBuffer = mat4(0.);

        float screenSpaceShadows = lightDescription[actualIndex];
        float shadowMap = lightDescription[actualIndex + 1];
        float shadowBias = lightDescription[actualIndex + 2];
        float shadowSamples = lightDescription[actualIndex + 3];
        float zNear = lightDescription[actualIndex + 4];
        float zFar = lightDescription[actualIndex + 5];
        float cutoff = lightDescription[actualIndex + 6];
        float shadowAttenuationMinDistance = lightDescription[actualIndex + 7];
        float smoothing = lightDescription[actualIndex + 8];
        float radius = lightDescription[actualIndex + 9];
        float size = lightDescription[actualIndex + 10];
        float areaRadius = lightDescription[actualIndex + 11];
        float planeAreaWidth = lightDescription[actualIndex + 12];
        float planeAreaHeight = lightDescription[actualIndex + 13];
        float intensity = lightDescription[actualIndex + 14];
        int type = int(lightDescription[actualIndex + 15]);
        float colorX = lightDescription[actualIndex + 16];
        float colorY = lightDescription[actualIndex + 17];
        float colorZ = lightDescription[actualIndex + 18];
        float translationX = lightDescription[actualIndex + 19];
        float translationY = lightDescription[actualIndex + 20];
        float translationZ = lightDescription[actualIndex + 21];
        float rotationX = lightDescription[actualIndex + 22];
        float rotationY = lightDescription[actualIndex + 23];
        float rotationZ = lightDescription[actualIndex + 24];
        float atlasFaceX = lightDescription[actualIndex + 25];
        float atlasFaceY = lightDescription[actualIndex + 26];
        float attenuationX = lightDescription[actualIndex + 27];
        float attenuationY = lightDescription[actualIndex + 28];

        primaryBuffer[0][0] = float(type);
        primaryBuffer[1][0] = colorX;
        primaryBuffer[2][0] = colorY;
        primaryBuffer[3][0] = colorZ;

        primaryBuffer[0][1] = translationX;
        primaryBuffer[1][1] = translationY;
        primaryBuffer[2][1] = translationZ;

        primaryBuffer[0][2] = screenSpaceShadows;
        primaryBuffer[1][2] = shadowMap;
        primaryBuffer[2][2] = shadowAttenuationMinDistance;
        primaryBuffer[3][2] = shadowBias;

        switch (type) {
            case DIRECTIONAL: {
                primaryBuffer[0][3] = atlasFaceX;
                primaryBuffer[1][3] = atlasFaceY;
                primaryBuffer[2][3] = shadowSamples;
                //                primaryBuffer[3][3]

                //                if (int(shadowMap) == 1) {
                //                    mat4.lookAt(__lightView, position, [0, 0, 0], [0, 1, 0])
                //                    mat4.ortho(__lightProjection, -size, size, -size, size, zNear, zFar)
                //                    mat4.multiply(lightViewProjection, __lightProjection, __lightView)
                //
                //                    for (let i = 0; i < 16; i++)
                //                        secondaryBuffer[offset + i] = lightViewProjection[i]
                //                }
                break;
            }
            case POINT: {
                primaryBuffer[0][3] = shadowSamples;
                primaryBuffer[1][3] = attenuationX;
                primaryBuffer[2][3] = attenuationY;
                primaryBuffer[3][3] = cutoff;
                secondaryBuffer[0][0] = cutoff * smoothing;
                break;
            }
            case SPOT: {
                //                mat4.lookAt(cache1Mat4, position, position, [0, 1, 0])
                //                mat4.fromQuat(cache2Mat4, transformationrotationQuaternionFinal)
                //                mat4.multiply(cache1Mat4, cache1Mat4, cache2Mat4)


                //                primaryBuffer[metadataIndex] = cache1Mat4[8];
                //                primaryBuffer[metadataIndex] = cache1Mat4[9];
                //                primaryBuffer[metadataIndex] = cache1Mat4[10];
                primaryBuffer[0][3] = cutoff;
                primaryBuffer[1][3] = attenuationX;
                primaryBuffer[2][3] = attenuationY;
                primaryBuffer[3][3] = cos(radius * TO_RAD);
                break;
            }
            case SPHERE: {
                primaryBuffer[0][3] = areaRadius * TO_RAD;
                primaryBuffer[1][3] = 0;
                primaryBuffer[2][3] = 0;
                primaryBuffer[3][3] = cutoff;

                secondaryBuffer[0][0] = attenuationX;
                secondaryBuffer[1][0] = attenuationY;
                break;
            }
            case DISK: {
                mat4 matrix = createModelMatrix(vec3(translationX, translationY, translationZ), vec3(rotationX, rotationY, rotationZ), vec3(1.));
                transformMat4(matrix, vec3(0, 1, 0));

                primaryBuffer[0][3] = areaRadius * TO_RAD;
                primaryBuffer[1][3] = attenuationX;
                primaryBuffer[2][3] = attenuationY;
                primaryBuffer[3][3] = cutoff;

                secondaryBuffer[0][0] = matrix[0][0];
                secondaryBuffer[1][0] = matrix[1][0];
                secondaryBuffer[2][0] = matrix[2][0];
                break;
            }
        }
        lightMetadata[metadataIndex] = primaryBuffer;
        lightMetadata[metadataIndex + 1] = secondaryBuffer;
    }
}
