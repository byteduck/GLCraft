varying vec4 color;

uniform float time;

void main(){
	gl_TexCoord[0] = gl_MultiTexCoord0;
	color = gl_Color.rgba;

	gl_Position = gl_ProjectionMatrix * gl_ModelViewMatrix * gl_Vertex;
}