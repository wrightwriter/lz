import MathUtils.tau
import com.krab.lazy.LazyGui
import processing.core.PApplet
import processing.core.PVector as Vec


//public fun PVector.dot(other: PVector): PVector {
//    return PVector(this.x*other.x + this.y*other.y + this.z*other.z)
//}



//public fun PVector.rotX(rad: Float): PVector{
//    return rotate(Mat4.Axis.X, rad)
//}
//fun rotY(rad: Float): Vec3{
//    return rotate(Mat4.Axis.Y, rad)
//}
//fun rotZ(rad: Float): Vec3{
//    return rotate(Mat4.Axis.Z, rad)
//}


class Sketch : PApplet() {
    companion object {
        fun run() {
            val art = Sketch()
            art.runSketch()
        }
    }
    public lateinit var gui: LazyGui

    override fun settings() {
        size(800,800,P3D)
        smooth(8)
    }

    override fun setup() {
        var v = Vec(1.0f,1.0f,1.0f)
        v *= v

        this.gui = LazyGui(this)
        windowResizable(true)
    }

    override fun draw() {
        pushMatrix();
        scale(width/2.0f,height/2.0f)
        translate(1.0f,1.0f);

        background(gui.colorPicker("bg").hex);
//        scale(gui.slider("sz"))

        noFill()

        w_strokeWeight(120.0f)

        colorMode(HSB, 1.0f)

        val N = pow(2.0f, gui.sliderInt("fft_sz",0, 0, 100).toFloat()).toInt()
//        gui.sliderSet("fft total iters", N)
        gui.sliderIntSet("fft total iters", N)

        val sampleRate = 44100

        fun x(t: Float): Float {
            return sin(t*gui.slider("freq"))
        }

        val samples = Array(N){0.0f}

        for(n in 0 until N){
            samples[n] = x(N.toFloat()/sampleRate.toFloat())
        }


        fun draw_curve(range:Int = 500, fn: (x: Float)->Float){
            beginShape()
            for(x in 0..range){
                val idx = x.toFloat()/range.toFloat()
                var sx = Vec(
                    idx,
//                    sin(idx*tau*freq.toFloat() + 0.25f*tau),
                    fn(idx),
                    1
                )
                sx.x -= 0.5f;

                if(x== 0)
                    curveVertex(sx)
                if(x == range)
                    curveVertex(sx)

                curveVertex(sx)
            }
            endShape()
        }

        push()
            w_strokeWeight(10.0f)
            stroke(gui.colorPicker("fn color").hex)
            draw_curve{ _x:Float->
                val a = 3
                x(_x)*0.2f+0.4f
            }
        pop()


        var multiplies = 0
        val gradient = gui.gradient("gradient")

        val minFreq = 0.0f
        val maxFreq = 22000.0f
        val windowSzTime = N/sampleRate

        (minFreq - maxFreq) * windowSzTime;

//        val windowSzSamples = 22000.0f
//        gradient.
//        function miniIFFT(re, im){
//            miniFFT(im, re);
//            for(var i = 0, N = re.length; i < N; i++){
//            im[i] /= N;
//            re[i] /= N;
//        }
//        }
        val freqMult = gui.slider("freq mult")

        push()
        fun FFT(offs: Int, jmp: Int, cnt: Int): ArrayList<Vec>{
            if(cnt == 1){
                val v = samples[offs]
                return arrayListOf(Vec(v,0))
            }

            stroke(gui.colorPicker("pt col").hex)
            val rw = floatArrayOf( cos(tau/cnt), sin(tau/cnt*1), ).toVec()

            w_point2D(rw*0.5)

            fun cplx_mul(v: Vec, r: Vec):Vec{
                return Vec(
                    v.x * r.x - v.y * r.y,
                    v.x * r.y + v.y * r.x
                )
            }

            val new_cnt = cnt/2
            val new_jmp = jmp*2

            val even = FFT(offs,new_jmp, new_cnt)
            val odd = FFT(offs + jmp,new_jmp, new_cnt)

            gui.sliderSet("even", even[0].x);
            gui.sliderSet("odd", odd[0].y);

            var y = ArrayList<Vec>()
            (0 until cnt)
                .forEach { i ->
//                    y[i] = Vec(0)
                    y.add(Vec(0))
                }


            (0 until cnt/2).forEach { j ->
                val o = cplx_mul(odd[j],rw)
                y[j] = even[j] + o
                y[j + cnt/2] = even[j] - o
                multiplies += 1
            }

            return y
        }
        pop()

//        print(multiplies)


        val fft_result = FFT(0, 1, N)
        gui.sliderIntSet("multiplies", multiplies)
        gui.sliderIntSet("max multiplies", N * N)

        push()
            w_strokeWeight(10.0f)
            stroke(gui.colorPicker("FFT col").hex)
            var b = 0
            draw_curve (range = N-1){ _x:Float->
//                fft_result[b++].length()*0.5f + gui.slider("fft height")
                fft_result[b++].y*0.5f + gui.slider("fft height")
            }
        pop()


//        fun FFT(depth: Int, it: Int, offs: Int){
////            if(depth <= max(gui.sliderInt("exit_depth"),0)){
////            val mult = pow(it.toFloat(), 2.0f)
////            if(pow(2.0f, (it-2).toFloat()) >= N.toFloat() - 1 && it != 0){
////            if(depth <=  max(gui.sliderInt("exit_depth"),0)){
//            val mul = pow(2.0f, it.toFloat() - 1.0f);
////            val mul_prev = pow(it.toFloat()-1.0f, 2.0f);
////            if(depth <=  ){
//            if(mul >=  N/4){
//                var x = offs.toFloat()/N.toFloat() - 0.5f
////                x += it * 2;
//
//
//                val height = 0.4f;
//                line(x,height,x,-height)
//                point(x,0.00f,0.0f)
//                return
//            }
//
//            val odd_offs = offs + (2.toDouble().pow(it.toDouble()) + 0.5).toInt()
//
//            val next_depth = depth/2;
//
//            val next_it = it + 1
//            val odd = FFT(next_depth, next_it,offs)
//            val even = FFT(next_depth, next_it,offs + odd_offs)
//
//        }
//
//        FFT(N,0, 0);
//        fun <Ta: Number, Tb: Number> draw_sin(freq: Ta, amp: Tb, offs: Tb,idx: Int){
//            beginShape()
//
//            val c = color(
//                (idx.toFloat()*0.46f)%1.0f,
//                0.75f,
//                0.7f
//            )
//            stroke(c);
//
//            val range = 40
//            for(x in 0..range){
//                val idx = x.toFloat()/range.toFloat()
//                var sx = Vec(
//                    idx,
//                    sin(idx*tau*freq.toFloat() + 0.25f*tau),
//                    1
//                )
//                sx.x -= 0.5f;
//                sx.y *= amp.toFloat()*0.5f;
//                sx.y += offs.toFloat();
//                sx.z += gui.slider("z")
//
//                if(x== 0)
//                    curveVertex(sx)
//                if(x == range)
//                    curveVertex(sx)
//
//                curveVertex(sx)
//            }
//
//
//            endShape()
//
//
//        }
//
//        val offs = gui.slider("offs")
//        val iters = gui.sliderInt("iterations")
//
//        for(iter in 0..iters){
//            val freq = iter.toFloat();
//
//            val iter_idx = (iter.toFloat()-1.0f)/iters.toFloat();
//            if(iter%2 == 0)
//                draw_sin(freq,0.5, offs*(iter_idx), iter)
//        }
//
//
//
//        strokeWeight(10.0f/width)
//        val c = color(0, 0, 255,100)
//        val split_cnt = iters
//        val finished = false
//
//        fun go_down(start: Float, end: Float, depth: Int){
//
//            val len = (end - start)
//            val mid = start + len*0.5f
//
//            val height = (depth + 1).toFloat()/split_cnt * 0.5f;
//            line(mid,height,mid,-height)
//
//            if(depth <= 0)
//                return
//
//            go_down(start, start + len*0.25f, depth - 1);
//            go_down(mid, mid + len*0.25f, depth - 1);
//
//        }
//
//        go_down(-0.5f, 0.5f, split_cnt);



//        for(split in 0..split_cnt){
//            var loc = split.toFloat()/split_cnt.toFloat()
//            loc -= 0.5f
//            line(loc,0.5f,loc,-0.5f)
//        }


//        {
//            var x = 0
//            while (x < width) {
//                val sx = map(sin((x * .05).toFloat()), -1f, 1f, -5f, 5f)
//                curveVertex(x.toFloat(), height / 2 + sx)
//                x += 1
//            }
//        }
        popMatrix();
    }
}

fun main(args: Array<String>) {
    Sketch.run()
}
