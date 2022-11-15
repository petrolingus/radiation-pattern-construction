#version 330

in float height;

out vec4 fragColor;

float valueMapper(float value, float min, float max) {
    return ((value - min) / (max - min));
}

void main()
{
    float c = valueMapper(height, -0.5, 0.5);
    vec3 color = mix(vec3(0, 0, 0), vec3(1, 0, 0), c);
    fragColor = vec4(color, 1.0);
}
