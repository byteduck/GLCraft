varying vec4 color;
varying vec2 vTexCoord;

varying float distToCamera;

uniform sampler2D u_texture;
uniform int lightingEnabled;

void main(){
	
	/**float ambientLight = 0.05f;
	float pdistToCamera = (5.0f/distToCamera) + ambientLight - 0.1f;**/
	
	vec4 texColor = texture2D(u_texture, gl_TexCoord[0].st);
	
	if(texColor.a <= 0.0f){
		discard;
	}else{
		/**if(pdistToCamera <= 1.0f && lightingEnabled == 1){
			gl_FragColor = texColor * color * vec4(pdistToCamera, pdistToCamera, pdistToCamera, 1.0f);
		}else{**/
			gl_FragColor = texColor * color;
		//}
	}
}