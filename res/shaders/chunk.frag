varying vec4 color;
varying vec2 vTexCoord;

uniform sampler2D u_texture;

void main(){
	vec4 texColor = texture2D(u_texture, gl_TexCoord[0].st);
	
	if(texColor.a <= 0.0f){
		discard;
	}else{
		gl_FragColor = texColor * color;
	}
}