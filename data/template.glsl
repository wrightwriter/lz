uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;

void main(){
    vec2 uv = (gl_FragCoord.xy-.5*resolution) / resolution.y;
//    vec3 col = 0.5 + 0.5*cos(time+uv.xyx+vec3(0,2,4));

    vec3 col = 1.-texture(texture,gl_FragCoord.xy/resolution).xyz;

    vec2 U = gl_FragCoord.xy;

    #define T(u) texture(texture,((u).xy)/resolution)

    vec2 st = vec2(1,0);


    #define get_d(U,st,v) vec2( T(U + vec2(st))[v] - T(U - vec2(st))[v], T(U + vec2(st.yx))[v] - T(U - vec2(st.yx))[v] )

    vec2 d = get_d(U,st,1);
    col.x += length(d);

    st *= 5.;
    d = get_d(U,st,1);

    col.y += length(d)*0.2;

//    col *= 0;
    gl_FragColor = vec4(col, 1.);
}