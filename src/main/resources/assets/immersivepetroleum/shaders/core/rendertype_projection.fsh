#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform vec4 FogColor;
uniform float FogStart;
uniform float FogEnd;

uniform float Alpha; // Passed in by callback
uniform float Time;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec4 normal;

out vec4 fragColor;

float noise(in vec2 coordinate, in float seed){
	vec2 coordActual = floor(textureSize(Sampler0, 0) * coordinate);
	return fract(sin(dot(coordActual * seed, vec2(12.9898, 78.233))) * 43758.5453);
}

void main(){
	float n = 0.5 + (0.25 * noise(texCoord0, Time));
	vec4 v = vec4(0.5 + n, 0.5 + n, 0.5 + n, Alpha);
	vec4 color = texture(Sampler0, texCoord0) * vertexColor * v * ColorModulator;
	
	fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
