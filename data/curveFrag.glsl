/*
  Part of the Processing project - http://processing.org
  Copyright (c) 2012-21 The Processing Foundation
  Copyright (c) 2004-12 Ben Fry and Casey Reas
  Copyright (c) 2001-04 Massachusetts Institute of Technology
  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation, version 2.1.
  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.
  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
*/

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

varying vec4 vertColor;
varying vec2 vUv;
varying float vId;
varying vec2 vUvNorm;
varying float vThic;
// varying flo

uniform float time;


float pi = acos(-1);



#define U gl_FragCoord
#define C gl_FragColor

#define rot(x) mat2(cos(x),sin(-x),sin(x),cos(x))

void main() {
    float alpha = 1;

    C = vertColor*vec4(1.5,1,1,alpha);
    if(mod(vId,50) < 20){
        if(mod(U.x,1.) + mod(U.y,13.) < 8){
            alpha = 0.;
        }
        C.xyz = vUv.xxx;
        C.xyz = vec3(0) ;
        C.a = alpha*1.;
    } else {

//        vU
        vec2 u = vUv;
//        u *= rot(10.5+time);
        u.y -= 0.5;




        float dist = length(u.y);

        dist -= sin(u.x*pi*2.+ sin(time))*0.1;

        dist -= 0.2;
        float line = smoothstep(fwidth(abs(dist)),0,dist);

        C.xyz *= line;
        C.a = 1.;
        C.a = pow(line,4.);


//        C.xyz = abs(u.xxx);
//        C.xyz = mod(u.xy,1).xyx;

//        C.xyz = abs(length(u.xy)).xxx;

    }

}
