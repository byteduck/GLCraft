varying vec4 worldCoord;
varying vec4 color;
varying float distToCamera;

void main(void){
    vec4 cs_position = gl_ModelViewMatrix * gl_Vertex;
    distToCamera = -cs_position.z;
     gl_Position = ftransform();

    color = gl_Color.rgba;

    worldCoord = gl_ModelViewMatrix * gl_Vertex;
    worldCoord = gl_ModelViewMatrixInverse * worldCoord;
}   