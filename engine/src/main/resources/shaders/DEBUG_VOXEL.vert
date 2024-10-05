layout(location = 0) in vec3 position;

layout(std430, binding = 12) buffer VoxelBlock {
    float voxelData[];
};

#include "./buffer_objects/CAMERA_VIEW_INFO.glsl"

out flat int instanceID;


void main(){
    instanceID = gl_InstanceID;
    int id = gl_InstanceID * 3;
    mat4 translationMatrix = mat4(1.0);
    translationMatrix[3][0] = voxelData[id];
    translationMatrix[3][1] = voxelData[id + 1];
    translationMatrix[3][2] = voxelData[id + 2];
    gl_Position = viewProjection * translationMatrix * vec4(position, 1.0);
}