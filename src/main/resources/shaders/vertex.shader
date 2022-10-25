#version 330

layout (location=0) in vec3 position;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

out float height;

void main()
{
    vec4 mvPos = viewMatrix * vec4(position.x, position.y, position.z, 1.0);
    gl_Position = projectionMatrix * mvPos;
    height = position.y;
}
