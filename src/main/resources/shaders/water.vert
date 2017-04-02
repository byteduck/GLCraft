varying vec4 color;

uniform float time;
uniform float waveMultiplier;
uniform float waveAdd;
uniform float waveSpeed;
uniform float waveFrequency;

void main(){
	gl_TexCoord[0] = gl_MultiTexCoord0;
	color = gl_Color.rgba;
	gl_Vertex.y += sin((gl_Vertex.x+time*waveSpeed)*waveFrequency)*cos(gl_Vertex.z*waveFrequency)*waveMultiplier-waveMultiplier+waveAdd;

	gl_Position = gl_ProjectionMatrix * gl_ModelViewMatrix * gl_Vertex;
}