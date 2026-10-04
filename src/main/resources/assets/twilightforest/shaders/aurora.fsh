#version 120

vec3 roundVec3(vec3 v) {
    return floor(v + 0.5);
}

vec4 permute(vec4 t) {
    return t * (t * 34.0 + 133.0);
}

vec3 grad(float hash) {
    vec3 cube = mod(floor(hash / vec3(1.0, 2.0, 4.0)), 2.0) * 2.0 - 1.0;

    vec3 cuboct = cube;
    int idx = int(hash * (1.0 / 16.0));

    if (idx == 0)
        cuboct.x = 0.0;
    else if (idx == 1)
        cuboct.y = 0.0;
    else
        cuboct.z = 0.0;

    float type = mod(floor(hash / 8.0), 2.0);
    vec3 rhomb = (1.0 - type) * cube + type * (cuboct + cross(cube, cuboct));

    vec3 grad = cuboct * 1.22474487139 + rhomb;

    grad *= (1.0 - 0.042942436724648037 * type) * 32.80201376986577;

    return grad;
}

vec4 openSimplex2Base(vec3 X) {

    vec3 v1 = roundVec3(X);
    vec3 d1 = X - v1;
    vec3 score1 = abs(d1);
    vec3 dir1 = step(max(score1.yzx, score1.zxy), score1);
    vec3 v2 = v1 + dir1 * sign(d1);
    vec3 d2 = X - v2;

    vec3 X2 = X + 144.5;
    vec3 v3 = roundVec3(X2);
    vec3 d3 = X2 - v3;
    vec3 score2 = abs(d3);
    vec3 dir2 = step(max(score2.yzx, score2.zxy), score2);
    vec3 v4 = v3 + dir2 * sign(d3);
    vec3 d4 = X2 - v4;

    vec4 hashes = permute(mod(vec4(v1.x, v2.x, v3.x, v4.x), 289.0));
    hashes = permute(mod(hashes + vec4(v1.y, v2.y, v3.y, v4.y), 289.0));
    hashes = mod(permute(mod(hashes + vec4(v1.z, v2.z, v3.z, v4.z), 289.0)), 48.0);

    vec4 a = max(0.5 - vec4(dot(d1, d1), dot(d2, d2), dot(d3, d3), dot(d4, d4)), 0.0);
    vec4 aa = a * a;
    vec4 aaaa = aa * aa;
    vec3 g1 = grad(hashes.x);
    vec3 g2 = grad(hashes.y);
    vec3 g3 = grad(hashes.z);
    vec3 g4 = grad(hashes.w);
    vec4 extrapolations = vec4(dot(d1, g1), dot(d2, g2), dot(d3, g3), dot(d4, g4));

    vec3 derivative = -8.0 * mat4x3(d1, d2, d3, d4) * (aa * a * extrapolations)
            + mat4x3(g1, g2, g3, g4) * aaaa;

    return vec4(derivative, dot(aaaa, extrapolations));
}

vec4 openSimplex2_ImproveXY(vec3 X) {
    mat3 orthonormalMap = mat3(
            0.788675134594813, -0.211324865405187, -0.577350269189626,
            -0.211324865405187, 0.788675134594813, -0.577350269189626,
            0.577350269189626, 0.577350269189626, 0.577350269189626);

    vec4 result = openSimplex2Base(orthonormalMap * X);
    return vec4(result.xyz * orthonormalMap, result.w);
}

uniform float GameTime;
uniform int SeedContext;
uniform vec3 PositionContext;

varying vec4 pixelPos;
varying vec4 vertexColor;

const int STEPS = 16;
const float FSTEPS = 16.0;
const float PRECISION = 0.000001;

float genNoise(float x, float z, float speed) {
    int seedDiv360 = SeedContext / 360;
    float xx = x + PositionContext.x + float(seedDiv360);
    float zz = z + PositionContext.z + float(SeedContext - seedDiv360 * 360);
    return openSimplex2_ImproveXY(vec3(xx / 512.0, zz / 512.0, GameTime * speed)).a;
}

int floatToOneOrZero(float value) {
    return int(step(PRECISION, value));
}

float fixNoise(float noise) {
    float absNoise = abs(noise);
    int swap = floatToOneOrZero(absNoise - 0.2);
    noise = float(1 - swap) * (1.0 + absNoise * 5.0) + float(swap) * -1.0;

    noise = clamp((noise + 1.0) / 2.0 - 0.5, 0.0, 1.0);
    noise = (1.0 - noise) * float(floatToOneOrZero(noise));
    return noise;
}

float rayMarch(vec3 origin, vec3 direction) {
    float noise = 0.0;
    for (int i = 0; i < STEPS; ++i) {
        vec3 curPos = origin + ((float(i) / FSTEPS) * 0.35) * direction;

        int swap = min(i, 1);
        float fade = float(swap) * (((float(STEPS - i)) / FSTEPS) * 0.65) + float(1 - swap);
        noise += fixNoise(genNoise(curPos.x, curPos.z, 22.5)) * fade;
    }

    return noise;
}

float linearFogFade(float dist, float fogStart, float fogEnd) {
    if (fogEnd <= fogStart) {
        return 1.0;
    }
    return clamp((fogEnd - dist) / (fogEnd - fogStart), 0.0, 1.0);
}

vec4 applyFog(vec4 color, float dist, float fogStart, float fogEnd, vec4 fogColor) {
    float fogValue = 1.0 - linearFogFade(dist, fogStart, fogEnd);
    return vec4(mix(color.rgb, fogColor.rgb, fogValue), color.a);
}

void main() {
    vec2 uv = pixelPos.xz * 2.0 - 1.0;

    float noise = rayMarch(vec3(uv.x, 0.0, uv.y), vec3(uv.x, 1.0, uv.y));
    float colorNoise = genNoise(uv.x, uv.y, 720.0);

    colorNoise = ((colorNoise + 1.0) / 2.0) * 0.5;
    vec4 color = vec4(0.0, 0.5 + colorNoise, 1.0 - colorNoise, noise);

    float fogFade = linearFogFade(length(pixelPos.xz / 2.75), gl_Fog.start, gl_Fog.end);

    gl_FragColor = applyFog(
            vec4(vertexColor.rgb * color.rgb, vertexColor.a * color.a * fogFade),
            length(pixelPos.xz / 2.5), gl_Fog.start, gl_Fog.end, gl_Fog.color);
}
