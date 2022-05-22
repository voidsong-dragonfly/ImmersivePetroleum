#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform vec4 FogColor;
uniform float FogStart;
uniform float FogEnd;

uniform float alpha; // Passed in by callback
uniform float time;

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
	float n = (noise(texCoord0, time) - 0.5) * 0.25;
	fragColor = texture(Sampler0, texCoord0) * Color * vec4(1.0 + n, 1.0 + n, 1.0 + n, alpha);
}