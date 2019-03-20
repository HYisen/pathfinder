#version 330 core

layout (points) in;
layout (triangle_strip,max_vertices=3) out;

in TRI
{
    vec3 color;
    vec4 vertexA;
    vec4 vertexB;
    vec4 vertexC;
}gs_in[];

out vec3 myColor;

void fuck()
{
    myColor=gs_in[0].color;

//    gl_Position=vec4(0.5,0.5,0.0,1.0);
    EmitVertex();
//    gl_Position=vec4(-0.5,-0.5,0.0,1.0);
    EmitVertex();
//    gl_Position=vec4(0.2,0.8,0.0,1.0);
    EmitVertex();

    EndPrimitive();
}

void main()
{
    myColor=gs_in[0].color;

    gl_Position=gs_in[0].vertexA;
//    gl_Position=vec4(0.5,0.5,0.0,1.0);
    EmitVertex();
    gl_Position=gs_in[0].vertexB;
//    gl_Position=vec4(-0.5,-0.5,0.0,1.0);
    EmitVertex();
    gl_Position=gs_in[0].vertexC;
//    gl_Position=vec4(0.2,0.8,0.0,1.0);
    EmitVertex();

    EndPrimitive();
}
