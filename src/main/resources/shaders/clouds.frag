uniform float time;
uniform float cover;
uniform float speed;
uniform float sharpness;
varying vec4 worldCoord;
varying vec4 color;
varying float distToCamera;

float hash( float n )
{
	return fract(sin(n)*43758.5453);
}

float noise( in vec2 x )
{
	vec2 p = floor(x);
	vec2 f = fract(x);
    	f = f*f*(3.0-2.0*f);
    	float n = p.x + p.y*57.0;
    	float res = mix(mix( hash(n+  0.0), hash(n+  1.0),f.x), mix( hash(n+ 57.0), hash(n+ 58.0),f.x),f.y);
    	return res;
}

float fbm( vec2 p )
{
    	float f = 0.0;
    	f += 0.50000*noise( p ); p = p*2.02;
    	f += 0.25000*noise( p ); p = p*2.03;
    	f += 0.12500*noise( p ); p = p*2.01;
    	f += 0.06250*noise( p ); p = p*2.04;
    	f += 0.03125*noise( p );
    	return f/0.984375;
}

// Entry point
void main( void ) {
    vec2 resolution = (1000,1000);

	// Wind - Used to animate the clouds
	vec2 wind_vec = vec2(0.001 + time*speed, 0.003 + time*speed);
	
	
	// Set up domain
	vec2 q = ( worldCoord.xz / resolution.xy );
	vec2 p = -1.0 + 3.0 * q + wind_vec;
	
	// Fix aspect ratio
	p.x *= resolution.x / resolution.y;

	
	// Create noise using fBm
	float f = fbm( 4.0*p );
	
	float c = f - (1.0 - cover);
	if ( c < 0.0 )
		c = 0.0;
	
	f = 1.0 - (pow(sharpness, c));
	

	gl_FragColor = vec4(f,f,f,f*clamp(200.0/(distToCamera), 0.0, 1.0))*color;
}