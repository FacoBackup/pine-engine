layout(local_size_x = 8, local_size_y = 8, local_size_z = 8) in;

#include "../util/NOISE.glsl"

layout(binding = 0) writeonly uniform image3D noiseSampler;

uniform int u_Size;

void main()
{
    vec3 texCoord = (vec3(gl_GlobalInvocationID) + vec3(0.5f)) / float(u_Size);

    float freq = 8.0f;

    float worley0 = worley_fbm(texCoord, freq);
    float worley1 = worley_fbm(texCoord, freq * 2.0f);
    float worley2 = worley_fbm(texCoord, freq * 4.0f);

    imageStore(noiseSampler, ivec3(gl_GlobalInvocationID), vec4(worley0, worley1, worley2, 1));
}

