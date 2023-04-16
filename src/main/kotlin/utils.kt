import processing.core.PApplet
import processing.core.PVector
import com.krab.lazy.LazyGui
import com.krab.lazy.PickerColor
import com.krab.lazy.ShaderReloader
import com.krab.lazy.stores.GlobalReferences
import com.krab.lazy.stores.NormColorStore
import processing.core.PApplet.sqrt
import kotlin.math.abs
import kotlin.math.sin


//fun Array<Float>.toVec():PVector{
//    return PVector(this[0], this[1])
//}

//fun <T: Number> fract(t: T): Float{
fun PickerColor.adjust(hue: Float = 0.0f, sat: Float = 0.0f, v: Float = 0.0f, alpha: Float = 0.0f): Int{
    return NormColorStore.color(
        this.hue + hue,
        this.saturation + sat,
        this.brightness + v,
        this.alpha + alpha,
    )
}

fun PApplet.camera(eye: PVector, lookAt: PVector){
    camera(
        eye.x, eye.y, eye.z,
        lookAt.x, lookAt.y, lookAt.z,
        0f,1f,0f
    )
}




var PVector.x: Float
    get() = this.x
    set(v) {
        this.x = v
    }
var PVector.y: Float
    get() = this.y
    set(v) {
        this.y = v
    }
var PVector.z: Float
    get() = this.z
    set(v) {
        this.z = v
    }

fun sin(v: PVector):PVector{
    return PVector(sin(v.x),sin(v.y),sin(v.z))
}

fun FloatArray.toVec(): PVector {
    return PVector(this[0], this[1])
}

//fun PApplet.w_point_2D(p: PVector){
//    poin
//}

fun cross(a: PVector, b: PVector): PVector{
    return a.copy().cross(b)
}

fun PVector.length(): Float{
    return sqrt(this.x*this.x + this.y*this.y + this.z*this.z)
}

fun PApplet.w_point2D(p: PVector){
    point(p.x,p.y)
}
fun PApplet.w_strokeWeight(w: Float){
    strokeWeight(w/width.toFloat())
}

fun PApplet.stroke(col: PVector){
//    strokeWeight(w/width.toFloat())
    stroke(col.x,col.y,col.z)
}


//fun PickerColor.toVec():PVector{
//    this.hex
//    return PVector(this.)
//}


object MathUtils {
    const val pi = Math.PI.toFloat()
    const val tau = 2 * pi
}



operator fun PVector.get(a: Int): Float {
    if(a == 0){
        return this.x
    } else if(a == 1){
        return this.y
    } else if(a == 2){
        return this.z
    }
    return 0f
}

fun <T: Number> fract(t: T): Float{
    return t.toFloat()%1
}


fun PVector.abs(): PVector{
    return PVector(abs(this.x), abs(this.y), abs(this.z))
}

fun lerp(a: Float, b: Float, c: Float): Float{
    return a*(1f-c) + b * c;
}

fun lerp(a: PVector, b: PVector, c: Float): PVector{
    return PVector(
        lerp(a.x,b.x,c),
        lerp(a.y,b.y,c),
        lerp(a.z,b.z,c),
    )
}



//public fun <T: Number> Vec(x: T, y: T, z: T): PVector{
//fun <T: Number> PApplet.line(a: T, b: T, c: T, d: T){
//    line(a,b,c,d)
//}


fun PApplet.point(a: PVector){
    point(a.x,a.y, a.z)
}

fun PApplet.line(a: PVector, b: PVector){
    line(a.x,a.y, a.z,b.x,b.y, b.z)
}

fun PApplet.curveVertex(p: PVector){
    curveVertex(p.x,p.y,p.z)
}

//fun <T: Number>PApplet.translate(x: T, y:T){
//}

public fun <T: Number> Vec(x: T, y: T, z: T): PVector{
    return PVector(x.toFloat(),y.toFloat(),z.toFloat())
}
public fun <T: Number> Vec(x: T, y: T): PVector{
    return PVector(x.toFloat(),y.toFloat(),0.0f)
}
public fun <T: Number> Vec(x: T): PVector{
    return PVector(x.toFloat(),x.toFloat(),x.toFloat(),)
}

//class PVector{
//    constructor<T: Number>(x: T, y: T, z: T){
//        return PVector(x.toFloat(),y.toFloat(),z.toFloat())
//    }
//
////    public fun  PVector.constructor(x: T, y: T, z: T): PVector{
////        return PVector(x.toFloat(),y.toFloat(),z.toFloat())
////    }
//
//}

//PVector

//public fun <T: Number> PVector.constructor(x: T, y: T, z: T): PVector{
//    return PVector(x.toFloat(),y.toFloat(),z.toFloat())
//}

operator fun <T: Number> PVector.times(mag: T): PVector {
    return PVector.mult(this, mag.toFloat())
}
operator fun PVector.times(other: PVector): PVector {
    return PVector(this.x*other.x, this.y*other.y, this.z*other.z)
}

operator fun <T: Number>PVector.div(mag: T): PVector {
    return PVector.div(this, mag.toFloat())
}
operator fun PVector.div(other: PVector): PVector {
    return PVector(this.x/other.x, this.y/other.y, this.z/other.z)
}
operator fun PVector.plus(other: PVector): PVector {
    return PVector.add(this, other)
}
operator fun <T: Number>PVector.plus(other: T): PVector {
    return PVector.add(this, PVector(other.toFloat(),other.toFloat(),other.toFloat()))
}

operator fun <T: Number>PVector.minus(other: T): PVector {
    return PVector.sub(this, PVector(other.toFloat(),other.toFloat(),other.toFloat()))
}

operator fun PVector.minus(other: PVector): PVector {
    return PVector.sub(this, other)
}
