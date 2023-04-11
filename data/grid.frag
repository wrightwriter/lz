uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;

varying vec3 worldPos;

#define T(u) texture(texture,((u).xy)/resolution)

void main(){
    vec2 uv = (gl_FragCoord.xy-.5*resolution) / resolution.y;

    vec3 col = vec3(0);

    vec2 U = gl_FragCoord.xy;


    vec2 st = vec2(1,0);

    col = vec3(0);
    vec2 wp = worldPos.xy;

    wp = mod(wp,0.25);

    float d = length(wp.x);

    d = min(d, length(wp.y));

    d -= 0.04;

    col = mix(col,vec3(1), smoothstep(0.001,0.,d));
//    col = vec3(0);

    gl_FragColor = vec4(col, col.x);
}