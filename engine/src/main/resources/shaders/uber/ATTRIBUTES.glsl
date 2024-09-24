#define MAX_LIGHTS 310

#define PI 3.14159265359
#define FRAG_DEPTH_THRESHOLD .001
#define DECAL_DEPTH_THRESHOLD 1.

#define PARALLAX_THRESHOLD 200.
#define CLAMP_MIN .1
#define CLAMP_MAX .9
#define SEARCH_STEPS 5
#define DEPTH_THRESHOLD 1.2
#define PI2 6.2831853

#define UNLIT 0
#define ISOTROPIC 1
#define ANISOTROPIC 2
#define SHEEN 3
#define CLEAR_COAT 4
#define TRANSPARENCY 5
#define SKY 6

in vec2 naturalTextureUV;
in vec3 naturalNormal;
in vec3 worldPosition;
in mat4 invModelMatrix;
in flat int renderIndex;

uniform int lightCount;
uniform float elapsedTime;
uniform bool isDecalPass;
uniform float shadowMapsQuantity;
uniform float shadowMapResolution;
uniform float SSRFalloff;
uniform float stepSizeSSR;
uniform float maxSSSDistance;
uniform float SSSDepthThickness;
uniform float SSSEdgeAttenuation;
uniform float SSSDepthDelta;
uniform float SSAOFalloff;
uniform int maxStepsSSR;
uniform int maxStepsSSS;
uniform bool hasAmbientOcclusion;
uniform sampler2D brdfSampler;
uniform sampler2D SSAO;
uniform sampler2D SSGI;
uniform sampler2D previousFrame;
uniform sampler2D shadowAtlas;
uniform samplerCube shadowCube;

uniform bool ssrEnabled;
uniform int renderingMode;
uniform float anisotropicRotation;
uniform float anisotropy;
uniform float clearCoat;
uniform float sheen;
uniform float sheenTint;
uniform bool useAlbedoDecal;
uniform bool useMetallicDecal;
uniform bool useRoughnessDecal;
uniform bool useNormalDecal;
uniform bool useOcclusionDecal;


float naturalAO = 1.;
float roughness = .5;
float metallic = .5;
float refractionIndex = 0.;
float alpha = 1.;
vec3 albedo = vec3(.5);

mat3 TBN;
vec3 T;
vec3 B;
vec3 N;

vec3 emission = vec3(0.);
vec3 albedoOverPI;
vec3 VrN;
vec2 brdf;
vec3 F0 = vec3(0.04);
float NdotV;
vec2 texelSize;
bool flatShading = false;
bool alphaTested;

vec2 quadUV;
vec3 viewDirection;
bool hasTBNComputed = false;
bool hasViewDirectionComputed = false;
float distanceFromCamera;
vec3 V;
vec2 texCoords;
vec3 viewSpacePosition;
vec3 worldSpacePosition;
vec3 normalVec;
float depthData;

void computeTBN() {
    if (!hasTBNComputed) {
        hasTBNComputed = true;
        if (isDecalPass) {
            vec3 N = abs(normalVec);
            if (N.z > N.x && N.z > N.y)
            T = vec3(1., 0., 0.);
            else
            T = vec3(0., 0., 1.);
            T = normalize(T - N * dot(T, N));
            B = cross(T, N);
            TBN = mat3(T, B, N);
            return;
        }
        vec3 dp1 = dFdx(worldPosition);
        vec3 dp2 = dFdy(worldPosition);
        vec2 duv1 = dFdx(naturalTextureUV);
        vec2 duv2 = dFdy(naturalTextureUV);

        vec3 dp2perp = cross(dp2, naturalNormal);
        vec3 dp1perp = cross(naturalNormal, dp1);
        vec3 T = dp2perp * duv1.x + dp1perp * duv2.x;
        vec3 B = dp2perp * duv1.y + dp1perp * duv2.y;

        float invmax = inversesqrt(max(dot(T, T), dot(B, B)));
        TBN = mat3(T * invmax, B * invmax, naturalNormal);
    }
}

out vec4 fragColor;
