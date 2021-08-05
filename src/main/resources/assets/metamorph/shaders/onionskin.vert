#version 120

uniform vec4 onionskin;

varying vec4 color;
varying vec4 texcoord;

void main()
{
    gl_Position = ftransform();

    color = gl_Color * onionskin;
    texcoord = gl_TextureMatrix[0] * gl_MultiTexCoord0;
}