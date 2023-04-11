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

#define PROCESSING_LINE_SHADER

uniform mat4 modelviewMatrix;
uniform mat4 projectionMatrix;

uniform vec4 viewport;
uniform int perspective;
uniform vec3 scale;
uniform float time;

attribute vec4 position;
attribute vec4 color;
attribute vec4 direction;

varying vec4 vertColor;
varying vec2 vUv;
varying vec2 vUvNorm;
varying float vThic;
varying float vId;
varying float vSeg;

#define rot(x) mat2(cos(x),sin(-x),sin(x),cos(x))

void main() {

    vSeg = 0.;
    vId = float(gl_VertexID);

    if(mod(time,5)<2.5){
        vSeg = 1.;
    }

    vec4 pos = position;

    if(vSeg == 1){
        pos.xz *= rot(sin(time)*0.2);
        pos.xy *= rot(sin(time + 0.5)*0.6);
    }

//    vSeg = 0.;
//    vSeg = 0.;



    vec4 posp = modelviewMatrix * pos;
    vec4 posq = modelviewMatrix * (pos + vec4(direction.xyz, 0));

    // Moving vertices slightly toward the camera
    // to avoid depth-fighting with the fill triangles.
    // Discussed here:
    // http://www.opengl.org/discussion_boards/ubbthreads.php?ubb=showflat&Number=252848
    posp.xyz = posp.xyz * scale;
    posq.xyz = posq.xyz * scale;

    vec4 p = projectionMatrix * posp;
    vec4 q = projectionMatrix * posq;

    vec2 tangent = (q.xy*p.w - p.xy*q.w) * viewport.zw;

    // don't normalize zero vector (line join triangles and lines perpendicular to the eye plane)
    tangent = length(tangent) == 0.0 ? vec2(0.0, 0.0) : normalize(tangent);

    // flip tangent to normal (it's already normalized)
    vec2 normal = vec2(-tangent.y, tangent.x);


    float thickness = direction.w;

    if(vSeg == 1){
        thickness *= 51;
        if(vId > 600)
            p.xy += 2000;
    }


    vec2 offset = normal * thickness;

    vec2 perspScale = (projectionMatrix * vec4(1, -1, 0, 0)).xy;

    vec2 noPerspScale = p.w / (0.5 * viewport.zw);

    vec2 offs_sc = mix(noPerspScale, perspScale, float(perspective > 0));
    offset = offset.xy * offs_sc;
    gl_Position.xy = p.xy + offset;
    gl_Position.zw = p.zw;

//    normal *= rot(10.5+time);
//    offset.y /= thickness*0.2;
//    offset.x = (offset.x);
//    offset *= rot(1.5);
//    offset*= 0.2;
    vUv = vec2(0);

    float vid_mod = mod(vId,5.);

    // up
    if( vid_mod == 0){
        // right
        vUv.x = 1;
        vUv.y = 1;
    }
    if( vid_mod == 2){
//        left
        vUv.x = 0;
        vUv.y = 1;
    }

    // down
    if( vid_mod == 1){
        // left
        vUv.x = 0;
        vUv.y = 0;
    }
    if( vid_mod == 4){
        // right
        vUv.x = 1;
        vUv.y = 0;
    }

    if( vid_mod == 3){
        vUv.x = 1; // connector
        vUv.y = 0.4;
    }


//    if(
//        mod(vId,5) < 1
////        mod(vId,3) == 0
//    //        mod(vId,4) == 0
//    ){
//        vUv = vec2(0,1);
//    }

//    offset = normal.xy;
//    vUv = offset/offs_sc*p.w;
//    vUv = offset.x/thickness;
    vUvNorm = offset.xy/thickness;
    vThic = thickness;


    vertColor = color;
}
