varying vec4 color;
varying vec2 vTexCoord;

varying float distToCamera;

uniform sampler2D u_texture;
uniform int lightingEnabled;
uniform float time;

void main(){

    vec2 uv = gl_TexCoord[0].st;

	vec4 texColor = texture2D(u_texture, uv);

	if(texColor.a <= float(0)){
		discard;
	}else{
		/**if(pdistToCamera <= 1.0f && lightingEnabled == 1){
			gl_FragColor = texColor * color * vec4(pdistToCamera, pdistToCamera, pdistToCamera, 1.0f);
		}else{**/
			gl_FragColor = texColor * color;
		//}
	}
}