#version 330

layout (location=0) in vec3 position;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main()
{
    vec4 mvPos = viewMatrix * vec4(position.xyz, 1.0);
    gl_Position = projectionMatrix * mvPos;
}
