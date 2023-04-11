//import lazy.LazyGui
import com.krab.lazy.LazyGui
import com.krab.lazy.ShaderReloader
import processing.core.PApplet
import processing.opengl.PShader


fun main(args: Array<String>) {
    Hmmm.run()
}

private fun PApplet.draw_curve(
    range:Int = 500,
    fn: (x: Float)->Float,
    amp: Float = 1.0f,
    offs: Float = 0.0f
){
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
        sx.y *= amp
        sx.y += offs

        if(x== 0)
            curveVertex(sx)
        if(x == range)
            curveVertex(sx)

        curveVertex(sx)
    }
    endShape()
}

class Hmmm : PApplet() {
    companion object {
        fun run() {
            val art = Hmmm()
            art.runSketch()
        }
    }
    public lateinit var gui: LazyGui

    override fun settings() {
        size(800,800,P3D)
        smooth(8)
    }

//    public lateinit var audioBuff: AudioSample
//    var sample: AudioSample? = null
//    var frag: PShader? = null


    override fun setup() {
        this.gui = LazyGui(this)

//        frag = loadShader("data/curveFrag.glsl", "data/curveVert.glsl");



//        val samples = FloatArray(44100.toInt())
//
//        for (i in 0 until 44100.toInt()) {
//            samples[i] = sin(400.0f*tau * i / 44100)
//        }
//        val sample = AudioSample(this,samples,1)
//        sample.amp(0.2f)

//        this.audioBuff = AudioSample(this,samples,1)
        // Create an array and manually write a single sine wave oscillation into it.
        // Create an array and manually write a single sine wave oscillation into it.

        // Create the audiosample based on the data, set framerate to play 200 oscillations/second

        // Create the audiosample based on the data, set framerate to play 200 oscillations/second


        windowResizable(true)
    }

    override fun draw() {
        pushMatrix();
        scale(width/2.0f,height/2.0f)
        translate(1.0f,1.0f);

        val grad = gui.gradient("grad")
        background(gui.colorPicker("bg").hex);


        noFill()

        w_strokeWeight(120.0f)

        colorMode(HSB, 1.0f)

//        fun x(t: Float): Float {
////            return sin(t*gui.slider("freq"))
//            val t = t * freq
//            return fract(t)
//            val mt = fract(t)
//            if(mt < 0.5)
//                return -1f
//            else
//                return 1f
////            return sin(t*)
//        }


        curveTightness(gui.slider("curve tightness"))
//        push()
//            w_strokeWeight(10.0f)
//            stroke(gui.colorPicker("fn color input").hex)
//            draw_curve(amp = 0.2f, offs = 0.4f, fn =
//                { _x:Float->
//                    val a = 3
//                    x(_x)
//                }
//            )
//        pop()
//
//        val sampleRate = gui.slider("sample rate",0f,0f,44100f)

        val T = millis()/1000.0f


        val shader = ShaderReloader.getShader("curveFrag.glsl", "curveVert.glsl")
        shader["time"] = T
        ShaderReloader.shader("curveFrag.glsl", "curveVert.glsl")

//        shader(shader)

        push()
            val str_w = gui.slider("stroke weight")
            w_strokeWeight(str_w)

            randomSeed(gui.sliderInt("random seed").toLong());

            var iters = gui.sliderInt("iters",200, 0,20000)

            val n = noise(T)
            iters += (n*gui.slider("noise amt")).toInt()

            val freq: Float = gui.slider("freq")

            beginShape()
            for (i in 0 until iters){

                val idx = i.toFloat()/iters.toFloat()

                val r = random(1.0f)
                val rb = random(1.0f)
                val rc = random(1.0f)
                val rd = random(1.0f)

                val col = gui.gradientColorAt("grad", r)
                stroke(col.hex)
                val stroke_wiggle = gui.slider("stroke wiggle");
                w_strokeWeight(
                    str_w + stroke_wiggle * sin(idx*freq + T*6f)*str_w*0.5f
                )

                var sx = Vec(
                    rb,
                    rc,
                    rd
                )
                sx.x -= 0.5f;
                sx.y -= 0.5f;
//                sx.y *= amp
//                sx.y += offs

                curveVertex(sx)
            }
            endShape()
        pop()

        resetShader();



        popMatrix();
    }
}

