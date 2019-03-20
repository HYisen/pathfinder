#version 330 core

layout (location=0) in vec3 vertexA;
layout (location=1) in vec3 vertexB;
layout (location=2) in vec3 vertexC;
layout (location=3) in vec3 color;

out TRI
{
    vec3 color;
    vec4 vertexA;
    vec4 vertexB;
    vec4 vertexC;
}vs_out;

uniform mat4 transform;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

vec4 calculate(vec3 pos)
{
//    return vec4(pos,1.0);
    return projection*view*model*transform*vec4(pos,1.0);
}

void main()
{
    vs_out.color=color;
    vs_out.vertexA=calculate(vertexA);
    vs_out.vertexB=calculate(vertexB);
    vs_out.vertexC=calculate(vertexC);
}
