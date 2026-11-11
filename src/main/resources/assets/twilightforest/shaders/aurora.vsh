#version 120

varying vec4 pixelPos;
varying vec4 vertexColor;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    pixelPos = gl_Vertex;
    vertexColor = gl_Color;
}
