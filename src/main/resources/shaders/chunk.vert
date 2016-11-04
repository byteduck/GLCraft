varying vec4 color;
varying float distToCamera;

void main(){
	vec4 cs_position = gl_ModelViewMatrix * gl_Vertex;
    distToCamera = -cs_position.z;
    
	gl_TexCoord[0] = gl_MultiTexCoord0;
	color = gl_Color.rgba;
	gl_Position = ftransform();
}